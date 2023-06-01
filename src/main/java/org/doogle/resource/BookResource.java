package org.doogle.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.doogle.entity.BookOrdersEntity;
import org.doogle.mappers.BookOrdersMapper;
import org.doogle.model.BookOrdersModel;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.util.List;

@ApplicationScoped
@Path("/bookOrders")
@GraphQLApi
public class BookResource {


    @Inject
    ObjectMapper mapper;

    @Inject
    BookOrdersMapper bookOrdersMapper;

    @GET
    @Path("/fetchAll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<List<BookOrdersModel>> fetchBookOrdersApi() {

        return BookOrdersEntity.getAll().log("ALL_BOOKS").map(b -> bookOrdersMapper.fromBookEntities(b));
    }

    @Query("BookOrders")
    public Uni<List<BookOrdersModel>> fetchBookOrdersGraphQL() {
        return this.fetchBookOrdersApi();
    }

    @Mutation("createBookOrder")
    public Uni<BookOrdersModel> createBookOrderGraphQL(BookOrdersModel bookOrder) {
        return this.createBookOrderApi(bookOrder);
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<BookOrdersModel> createBookOrderApi(@RequestBody BookOrdersModel bookOrder) {
        Uni<BookOrdersModel> bookOrderModel = Uni.createFrom().item(bookOrder).map(b -> bookOrdersMapper.toBookEntity(b)).call(BookOrdersEntity::persistOrUpdateBookOrdersEntity).log().map(be -> bookOrdersMapper.fromBookEntity(be));
        return bookOrderModel.log();
    }
}
