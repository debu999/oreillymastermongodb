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
@MongoEntity(collection = "ticket")
public class TicketEntity extends ReactivePanacheMongoEntityBase {


  @Id
  public ObjectId id;
  public String type;
  public boolean premium;
  public double price;
  public String event;
  public String currency;

  public static Uni<List<TicketEntity>> getAll() {
    return listAll();
  }

  public static Uni<TicketEntity> persistOrUpdateTicketEntity(TicketEntity entity) {
    return persistOrUpdate(entity).replaceWith(entity).log("TICKET_ENTITY_PERSISTED");
  }

  public static Uni<TicketEntity> deleteTicketEntity(ObjectId id) {
    Uni<TicketEntity> ticket = findById(id);
    return ticket.call(b -> deleteById(id)).log("TICKET_ENTITY_DELETED");
  }

  public static ReactiveMongoCollection<TicketEntity> getCollection() {
    return mongoCollection();
  }

  public static ReactiveMongoDatabase getDatabase() {
    return mongoDatabase();
  }

  public static Uni<String> editChangeStreamPreAndPostImages(boolean enabled) {
    String collectionName = getCollection().getNamespace().getCollectionName();
    Document changeStreamPreAndPostImagesCommand = new Document("collMod", collectionName)
        .append("changeStreamPreAndPostImages", new Document("enabled", enabled));
    return getDatabase().runCommand(changeStreamPreAndPostImagesCommand).map(Document::toJson);
  }

}
