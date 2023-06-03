package org.doogle.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.bson.Document;
import org.doogle.entity.BookOrdersEntity;
import org.doogle.mappers.BookOrdersMapper;
import org.doogle.model.BookOrdersModel;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Path("/bookOrders")
@GraphQLApi
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


    @GET
    @Path("/fetchAll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<BookOrdersModel>> fetchBookOrdersApi() {
        registry.counter("fetchBookOrdersApi", "type", "methodcall").increment();
        return BookOrdersEntity.getAll().log("ALL_BOOKS").map(b -> bookOrdersMapper.fromBookEntities(b));
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
        Uni<BookOrdersModel> bookOrderModel = Uni.createFrom().item(bookOrder).map(b -> bookOrdersMapper.toBookEntity(b)).flatMap(BookOrdersEntity::persistOrUpdateBookOrdersEntity).log().map(be -> bookOrdersMapper.fromBookEntity(be));
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
        Uni<BookOrdersModel> bookOrderModel = Uni.createFrom().item(bookOrder).map(b -> bookOrdersMapper.toBookEntity(b)).flatMap(b -> BookOrdersEntity.deleteBookOrdersEntity(b.getId())).log().map(be -> bookOrdersMapper.fromBookEntity(be));
        return bookOrderModel.log();
    }

    @Mutation
    public Uni<Map<String, String>> bulkInsert(List<BookOrdersModel> bookOrdersModels) throws ExecutionException, InterruptedException {
        List<BookOrdersEntity> boe = bookOrdersMapper.toBookEntities(bookOrdersModels);
        List<Map<String, ?>> boelist = mapper.convertValue(boe, new TypeReference<List<Map<String, ?>>>() {
        });
        boelist.forEach(b -> b.values().removeIf(Objects::isNull));
        List<InsertOneModel<Document>> insertOneModels = boelist.stream().map(d -> new Document(d)).map(doc -> new InsertOneModel<>(doc)).toList();
        BulkWriteResult r = getCollection().bulkWrite(insertOneModels, new BulkWriteOptions().ordered(true)).log()
                .subscribe().asCompletionStage().get();
        Map<String, String> res = new HashMap<>();
        res.put("wasAcknowledged", String.valueOf(r.wasAcknowledged()));
        res.put("insertedCount", String.valueOf(r.getInsertedCount()));
        res.put("matchededCount", String.valueOf(r.getMatchedCount()));
        res.put("deletededCount", String.valueOf(r.getDeletedCount()));
        res.put("modifiededCount", String.valueOf(r.getModifiedCount()));

        return Uni.createFrom().item(res);
    }

    public ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(defaultDatabase).getCollection("bulkCollection");
    }
}
