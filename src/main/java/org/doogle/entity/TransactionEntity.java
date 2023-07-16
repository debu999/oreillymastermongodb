package org.doogle.entity;


import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.smallrye.mutiny.Multi;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.doogle.entity.views.ActiveTransactionSummaryView;
import org.doogle.entity.views.SpammerView;
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

  public static Multi<TransactionSummaryView> getNTransactionsOriginatedFromWithCountAndLimit(
      long n, long sortOrder) {
    List<Document> aggregationPipeline = Arrays.asList(new Document("$group",
            new Document("_id", "$from").append("count", new Document("$sum", 1L))),
        new Document("$sort", new Document("count", sortOrder)), new Document("$limit", n));
    return mongoCollection().aggregate(aggregationPipeline, TransactionSummaryView.class);
  }

  public static Multi<TransactionSummaryView> getNTransactionsCompletedAtWithCountAndLimit(long n,
      long sortOrder) {
    List<Document> aggregationPipeline = Arrays.asList(new Document("$group",
            new Document("_id", "$to").append("count", new Document("$sum", 1L))),
        new Document("$sort", new Document("count", sortOrder)), new Document("$limit", n));
    return mongoCollection().aggregate(aggregationPipeline, TransactionSummaryView.class);
  }

  public static Multi<TransactionSummaryView> getAverageValueAndStandardDeviation(long sortOrder) {
    List<Document> aggregationPipeline = Arrays.asList(new Document("$group",
            new Document("_id", "value").append("averageValue", new Document("$avg", "$value"))
                .append("stdDevValue", new Document("$stdDevPop", "$value"))
                .append("avgTrxFee", new Document("$avg", "$txfee"))
                .append("stdDevTrxFee", new Document("$stdDevPop", "$txfee"))),
        new Document("$sort", new Document("average", sortOrder)));
    return mongoCollection().aggregate(aggregationPipeline, TransactionSummaryView.class);
  }

  public static Multi<ActiveTransactionSummaryView> getActiveHoursSummary(long sortOrder) {
    List<Document> aggregationPipeline = Arrays.asList(new Document("$group",
            new Document("_id", new Document("$hour", "$timestamp")).append("count",
                new Document("$sum", 1L)).append("transactionValues", new Document("$sum", "$value"))),
        new Document("$sort", new Document("count", sortOrder)));
    return mongoCollection().aggregate(aggregationPipeline, ActiveTransactionSummaryView.class);
  }

  public static Multi<ActiveTransactionSummaryView> getActiveDaySummary(long sortOrder) {
    List<Document> aggregationPipeline = Arrays.asList(new Document("$group",
            new Document("_id", new Document("$dayOfWeek", "$timestamp")).append("count",
                new Document("$sum", 1L)).append("transactionValues", new Document("$sum", "$value"))),
        new Document("$sort", new Document("count", sortOrder)));
    return mongoCollection().aggregate(aggregationPipeline, ActiveTransactionSummaryView.class);
  }

  public static Multi<SpammerView> getSpammers(ZonedDateTime start, ZonedDateTime end)
      throws ExecutionException, InterruptedException {

    // ZonedDateTime is marked as Instant in filters.
    List<Document> aggregationPipeline = Arrays.asList(new Document("$match",
            new Document("timestamp",
                new Document("$gte", start.toInstant()).append("$lt", end.toInstant()))),
        new Document("$project",
            new Document("to", 1L).append("txHash", 1L).append("from", 1L).append("block", 1L)
                .append("txfee", 1L).append("tags", 1L).append("value", 1L)
                .append("reportPeriod", String.format("%s %s", start.getMonth(), start.getYear()))
                .append("_id", 0L)), new Document("$unwind",
            new Document("path", "$tags").append("preserveNullAndEmptyArrays", true)),
        new Document("$lookup", new Document("from", "scam_details").append("localField", "from")
            .append("foreignField", "scam_address").append("as", "scam_details")),
        new Document("$match", new Document("scam_details", new Document("$ne", List.of()))),
        new Document("$set", new Document("scamAddress",
            new Document("$arrayElemAt", Arrays.asList("$scam_details.scam_address", 0L))).append(
            "scamEmailAddress",
            new Document("$arrayElemAt", Arrays.asList("$scam_details.email_address", 0L)))),
        new Document("$project", new Document("scam_details", 0L)));
    return mongoCollection().aggregate(aggregationPipeline, SpammerView.class);
  }

}
