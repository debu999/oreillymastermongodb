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
@MongoEntity(collection = "account")
public class AccountEntity extends ReactivePanacheMongoEntityBase {

  @Id
  public ObjectId id;
  public String accountIdentifier;
  public String accountName;
  public double accountBalance;


  public static Uni<List<AccountEntity>> getAll() {
    return listAll();
  }

  public static Uni<AccountEntity> persistOrUpdateAccountEntity(AccountEntity entity) {
    return persistOrUpdate(entity).replaceWith(entity).log("ACCOUNT_ENTITY_PERSISTED");
  }

  public static Uni<AccountEntity> deleteAccountEntity(ObjectId id) {
    Uni<AccountEntity> account = findById(id);
    return account.call(b -> deleteById(id)).log("ACCOUNT_ENTITY_DELETED");
  }

  public static ReactiveMongoCollection<AccountEntity> getCollection() {
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

  public static Uni<String> createCollection() {
    String collectionName = getCollection().getNamespace().getCollectionName();
    return getDatabase().createCollection(collectionName)
        .map(v -> String.join("", "Collection '", collectionName, "' created successfully."));
  }

  public static Uni<AccountEntity> findByAccountIdentifier(String accountIdentifier) {
    Uni<AccountEntity> account = find("accountIdentifier",
        accountIdentifier).firstResult();
    return account.log("ACCOUNT_BY_IDENTIFIER");
  }

}
