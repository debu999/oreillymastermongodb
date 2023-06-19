package org.doogle.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Id;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.bson.types.ObjectId;

@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "oreilly_cart")
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CartEntity extends ReactivePanacheMongoEntityBase {

    @Id
    public ObjectId id;
    public long cartIdentifier;

    public long userIdentifier;
    public List<String> products;

    public static Uni<CartEntity> persistOrUpdateCart(CartEntity cart)
    {
        return persistOrUpdate(cart).replaceWith(cart).log("CARTENTITYPERSIST");
    }

    public static ReactiveMongoCollection<CartEntity> getCollection()
    {
        return mongoCollection();
    }
}
