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
@MongoEntity(collection = "oreilly_payment")
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity extends ReactivePanacheMongoEntityBase {

    @Id
    public ObjectId id;
    public long cartIdentifier;
    public long itemIdentifier;
    public long userIdentifier;
    public String status;

    public static Uni<PaymentEntity> persistOrUpdatePayment(PaymentEntity payment)
    {
        return persistOrUpdate(payment).replaceWith(payment).log("PAYMENTENTITYPERSIST");
    }

    public static ReactiveMongoCollection<PaymentEntity> getCollection()
    {
        return mongoCollection();
    }
}
