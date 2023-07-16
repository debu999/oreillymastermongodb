package org.doogle.entity;


import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.doogle.entity.views.BlockSummaryView;

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

  public static Multi<BlockSummaryView> getAverageTransactionPerBlock(long sortOrder) {
    List<Document> aggregationPipeline = Arrays.asList(new Document("$group",
            new Document("_id", "avg_trx_per_block").append("count",
                new Document("$avg", "$transactionsNumber"))
                .append("avgIntTrx",
                    new Document("$avg", "$internalTransactionsNumber"))
                .append("avgGasUsed",
                    new Document("$avg", "$gasUsed"))
                .append("avgDifficulty",
                    new Document("$avg", "$difficulty"))
                .append("stdDevDifficulty",
                    new Document("$stdDevPop", "$difficulty"))

        ),
        new Document("$sort", new Document("count", sortOrder)));
    return mongoCollection().aggregate(aggregationPipeline, BlockSummaryView.class);
  }

}
