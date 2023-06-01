package org.doogle.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "bookOrders")
public class BookOrdersEntity extends ReactivePanacheMongoEntityBase {

    @Id
    public ObjectId id;
    public String name;
    public int isbn;
    public double price;

    public static Uni<List<BookOrdersEntity>> getAll() {
        return listAll();
    }

    public static Uni<BookOrdersEntity> persistOrUpdateBookOrdersEntity(BookOrdersEntity entity)
    {
        return persistOrUpdate(entity).replaceWith(entity).log("BOOK_ORDER_ENITITY_PERSISTED");
    }
}
