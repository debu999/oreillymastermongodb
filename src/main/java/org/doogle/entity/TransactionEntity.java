package org.doogle.entity;


import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.smallrye.mutiny.Multi;
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
import org.doogle.entity.views.TransactionSummaryView;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@MongoEntity(collection = "transaction")
public class TransactionEntity extends ReactivePanacheMongoEntityBase {

  @Id
  public ObjectId id;
  public String from;
  public String to;
  public String txHash;
  public Double txfee;
  public Double value;
  public int block;
  public ZonedDateTime timestamp;
  public List<String> tags;

  public static Multi<TransactionSummaryView> getNTransactionsByCountWithOrder(long n, long sortOrder) {
    List<Document> aggregationPipeline = Arrays.asList(new Document("$group",
            new Document("_id", "$from").append("count", new Document("$sum", 1L))),
        new Document("$sort", new Document("count", sortOrder)), new Document("$limit", n));
    return mongoCollection().aggregate(aggregationPipeline, TransactionSummaryView.class);
  }

}
