package org.doogle.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bson.Document;
import org.doogle.entity.BookOrdersEntity;
import org.doogle.mappers.BookOrdersMapper;
import org.doogle.model.BookOrdersModel;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Path("/bookOrders")
@GraphQLApi
@ApplicationScoped
public class BookResource {

  @ConfigProperty(name = "quarkus.mongodb.database")
  String defaultDatabase;

  @Inject
  MeterRegistry registry;
  @Inject
  ObjectMapper mapper;
  @Inject
  BookOrdersMapper bookOrdersMapper;
  @Inject
  ReactiveMongoClient mongoClient;

  private static List<ResultMap> getResultMaps(BulkWriteResult r) {
    return List.of(new ResultMap("wasAcknowledged", String.valueOf(r.wasAcknowledged())),
        new ResultMap("insertedCount", String.valueOf(r.getInsertedCount())),
        new ResultMap("matchededCount", String.valueOf(r.getMatchedCount())),
        new ResultMap("deletededCount", String.valueOf(r.getDeletedCount())),
        new ResultMap("modifiededCount", String.valueOf(r.getModifiedCount())));
  }

  @GET
  @Path("/fetchAll")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<List<BookOrdersModel>> fetchBookOrdersApi() {
    registry.counter("fetchBookOrdersApi", "type", "methodcall").increment();
    return BookOrdersEntity.getAll().log("ALL_BOOKS")
        .map(b -> bookOrdersMapper.fromBookEntities(b));
  }

  @Query("BookOrders")
  public Uni<List<BookOrdersModel>> fetchBookOrdersGraphQL() {
    registry.counter("fetchBookOrdersGraphQL", "type", "methodcall").increment();
    return this.fetchBookOrdersApi();
  }

  @Mutation("createUpdateBookOrder")
  public Uni<BookOrdersModel> createBookOrderGraphQL(BookOrdersModel bookOrder) {
    registry.counter("createBookOrderGraphQL", "type", "methodcall").increment();
    return this.createBookOrderApi(bookOrder);
  }

  @POST
  @Path("/createorupdate")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<BookOrdersModel> createBookOrderApi(@RequestBody BookOrdersModel bookOrder) {
    registry.counter("createBookOrderApi", "type", "methodcall").increment();
    Uni<BookOrdersModel> bookOrderModel = Uni.createFrom().item(bookOrder)
        .map(b -> bookOrdersMapper.toBookEntity(b))
        .flatMap(BookOrdersEntity::persistOrUpdateBookOrdersEntity).log()
        .map(be -> bookOrdersMapper.fromBookEntity(be));
    return bookOrderModel.log();
  }

  @Mutation("deleteBookOrder")
  public Uni<BookOrdersModel> deleteBookOrderGraphQL(BookOrdersModel bookOrder) {
    registry.counter("deleteBookOrderGraphQL", "type", "methodcall").increment();
    return this.deleteBookOrderApi(bookOrder);
  }

  @DELETE
  @Path("/delete")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<BookOrdersModel> deleteBookOrderApi(@RequestBody BookOrdersModel bookOrder) {
    registry.counter("deleteBookOrderApi", "type", "methodcall").increment();
    Uni<BookOrdersModel> bookOrderModel = Uni.createFrom().item(bookOrder)
        .map(b -> bookOrdersMapper.toBookEntity(b))
        .flatMap(b -> BookOrdersEntity.deleteBookOrdersEntity(b.getId())).log()
        .map(be -> bookOrdersMapper.fromBookEntity(be));
    return bookOrderModel.log();
  }

  @Mutation
  public Uni<List<ResultMap>> bulkInsert(List<BookOrdersModel> bookOrdersModels) {
    List<BookOrdersEntity> boe = bookOrdersMapper.toBookEntities(bookOrdersModels);
    List<Map<String, ?>> boelist = mapper.convertValue(boe,
        new TypeReference<List<Map<String, ?>>>() {
        });
    boelist.forEach(m -> m.values().removeIf(Objects::isNull));
    List<InsertOneModel<Document>> insertOneModels = boelist.stream().map(Document::new).map(
        InsertOneModel::new).toList();
    List<InsertOneModel<Document>> insertOneModels2 = boelist.stream().map(Document::new).map(
        InsertOneModel::new).toList();
    Uni<BulkWriteResult> result = getCollection().bulkWrite(insertOneModels,
        new BulkWriteOptions().ordered(true));
    Uni<List<ResultMap>> res = result.map(BookResource::getResultMaps);
    result = getCollection().bulkWrite(insertOneModels2, new BulkWriteOptions().ordered(false));
    Uni<List<ResultMap>> res1 = result.map(BookResource::getResultMaps);
    return Uni.combine().all().unis(res, res1).asTuple()
        .map(tup -> Stream.of(tup.getItem1(), tup.getItem2())
            .flatMap(Collection::stream).collect(Collectors.toList()));
  }

  @Mutation
  @Blocking
  public List<ResultMap> bulkInsertBlocking(List<BookOrdersModel> bookOrdersModels)
      throws ExecutionException, InterruptedException {
    List<BookOrdersEntity> boe = bookOrdersMapper.toBookEntities(bookOrdersModels);
    List<Map<String, ?>> boelist = mapper.convertValue(boe,
        new TypeReference<List<Map<String, ?>>>() {
        });
    boelist.forEach(m -> m.values().removeIf(Objects::isNull));
    List<InsertOneModel<Document>> insertOneModels = boelist.stream().map(d -> new Document(d))
        .map(doc -> new InsertOneModel<>(doc)).toList();
    List<InsertOneModel<Document>> insertOneModels2 = boelist.stream().map(d -> new Document(d))
        .map(doc -> new InsertOneModel<>(doc)).toList();
    BulkWriteResult r = getCollection().bulkWrite(insertOneModels,
        new BulkWriteOptions().ordered(true)).subscribe().asCompletionStage().get();
    Log.info(r);
    List<ResultMap> res = getResultMaps(r);
    BulkWriteResult result1 = getCollection().bulkWrite(insertOneModels2,
        new BulkWriteOptions().ordered(false)).subscribe().asCompletionStage().get();
    Log.info(result1);
    List<ResultMap> res1 = getResultMaps(result1);
    return Stream.of(res1, res)
        .flatMap(Collection::stream).collect(Collectors.toList());
  }

  private ReactiveMongoCollection<Document> getCollection() {
    return mongoClient.getDatabase(defaultDatabase).getCollection("bulkCollection");
  }
}
