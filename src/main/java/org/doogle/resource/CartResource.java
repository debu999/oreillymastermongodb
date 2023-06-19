package org.doogle.resource;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.doogle.entity.CartEntity;
import org.doogle.mappers.CartMapper;
import org.doogle.model.Cart;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
@ApplicationScoped
public class CartResource {

    @Inject
    CartMapper cartMapper;

    @Query("Cart")
    public Uni<List<Cart>> getAllCarts() {

        Uni<List<CartEntity>> cartEntityUni = CartEntity.listAll();
        return cartEntityUni.log().map(u -> cartMapper.fromCartEntities(u)).log();
    }

    @Mutation("addUpdateCart")
    public Uni<Cart> addCart(@Name("cart") Cart cart) {

        CartEntity u = cartMapper.toCartEntity(cart);
        Log.info(u);
        return Uni.createFrom().item(u).flatMap(CartEntity::persistOrUpdateCart).log("Response").map(obj -> cartMapper.fromCartEntity(obj)).log();
    }
}
