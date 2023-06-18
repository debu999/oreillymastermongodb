package org.doogle.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Id;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;

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

  public static Uni<BookOrdersEntity> persistOrUpdateBookOrdersEntity(BookOrdersEntity entity) {
    return persistOrUpdate(entity).replaceWith(entity).log("BOOK_ORDER_ENTITY_PERSISTED");
  }

  public static Uni<BookOrdersEntity> deleteBookOrdersEntity(ObjectId id) {
    Uni<BookOrdersEntity> bookOrder = findById(id);
    return bookOrder.call(b -> deleteById(id)).log("BOOK_ORDER_ENTITY_PERSISTED");
  }

  public static ReactiveMongoCollection<BookOrdersEntity> getCollection() {
    return mongoCollection();
  }

  public static ReactiveMongoDatabase getDatabase() {
    return mongoDatabase();
  }


  public static Uni<String> compact(boolean force, String comment) {
    String collectionName = getCollection().getNamespace().getCollectionName();
    Document compactCommand = new Document("compact", collectionName)
        .append("force", force)
    .append("comment", comment);
    return getDatabase().runCommand(compactCommand).map(Document::toJson);
  }
}
