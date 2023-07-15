package org.doogle.entity;


import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@MongoEntity(collection = "block")
public class BlockEntity extends ReactivePanacheMongoEntityBase {

  @Id
  public ObjectId id;
  public int height;
  public int transactionsNumber;
  public int internalTransactionsNumber;
  public long difficulty;
  public String hash;
  public long gasUsed;
  public ZonedDateTime timestamp;


  public static Uni<List<BlockEntity>> getAll() {
    return listAll();
  }

  public static Uni<BlockEntity> persistOrUpdateAccountEntity(BlockEntity entity) {
    return persistOrUpdate(entity).replaceWith(entity).log("ACCOUNT_ENTITY_PERSISTED");
  }

  public static Uni<BlockEntity> deleteAccountEntity(ObjectId id) {
    Uni<BlockEntity> account = findById(id);
    return account.call(b -> deleteById(id)).log("ACCOUNT_ENTITY_DELETED");
  }

  public static ReactiveMongoCollection<BlockEntity> getCollection() {
    return mongoCollection();
  }

  public static ReactiveMongoDatabase getDatabase() {
    return mongoDatabase();
  }

  public static Uni<String> editChangeStreamPreAndPostImages(boolean enabled) {
    String collectionName = getCollection().getNamespace().getCollectionName();
    Document changeStreamPreAndPostImagesCommand = new Document("collMod", collectionName).append(
        "changeStreamPreAndPostImages", new Document("enabled", enabled));
    return getDatabase().runCommand(changeStreamPreAndPostImagesCommand).map(Document::toJson);
  }

  public static Uni<String> createCollection() {
    String collectionName = getCollection().getNamespace().getCollectionName();
    return getDatabase().createCollection(collectionName)
        .map(v -> String.join("", "Collection '", collectionName, "' created successfully."));
  }

  public static Uni<BlockEntity> findByAccountIdentifier(String accountIdentifier) {
    Uni<BlockEntity> account = find("accountIdentifier", accountIdentifier).firstResult();
    return account.log("ACCOUNT_BY_IDENTIFIER");
  }

}
