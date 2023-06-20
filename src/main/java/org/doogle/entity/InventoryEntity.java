package org.doogle.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "oreilly_inventory")
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InventoryEntity extends ReactivePanacheMongoEntityBase {

    @Id
    public ObjectId id;
    public long itemIdentifier;
    public String description;
    public Double price;
    public long quantity;

    public static Uni<InventoryEntity> persistOrUpdateInventory(InventoryEntity inventory)
    {
        return persistOrUpdate(inventory).replaceWith(inventory).log("INVENTORYENTITYPERSIST");
    }

    public static ReactiveMongoCollection<InventoryEntity> getCollection()
    {
        return mongoCollection();
    }
}
