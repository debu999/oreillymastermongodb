package org.doogle.resource;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.doogle.entity.InventoryEntity;
import org.doogle.mappers.InventoryMapper;
import org.doogle.model.Inventory;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
@ApplicationScoped
public class InventoryResource {

    @Inject
    InventoryMapper inventoryMapper;

    @Query("Inventory")
    public Uni<List<Inventory>> getAllInventorys() {

        Uni<List<InventoryEntity>> inventoryEntityUni = InventoryEntity.listAll();
        return inventoryEntityUni.log().map(u -> inventoryMapper.fromInventoryEntities(u)).log();
    }

    @Mutation("addUpdateInventory")
    public Uni<Inventory> addInventory(@Name("inventory") Inventory inventory) {

        InventoryEntity u = inventoryMapper.toInventoryEntity(inventory);
        Log.info(u);
        return Uni.createFrom().item(u).flatMap(InventoryEntity::persistOrUpdateInventory).log("Response").map(obj -> inventoryMapper.fromInventoryEntity(obj)).log();
    }
}
