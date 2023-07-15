package org.doogle.data;

import static com.fasterxml.jackson.databind.node.JsonNodeType.ARRAY;
import static com.fasterxml.jackson.databind.node.JsonNodeType.BINARY;
import static com.fasterxml.jackson.databind.node.JsonNodeType.NUMBER;
import static com.fasterxml.jackson.databind.node.JsonNodeType.OBJECT;
import static com.fasterxml.jackson.databind.node.JsonNodeType.POJO;
import static com.fasterxml.jackson.databind.node.JsonNodeType.STRING;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.smallrye.graphql.api.Subscription;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.smallrye.mutiny.vertx.UniHelper;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.doogle.entity.BlockEntity;
import org.doogle.entity.TransactionEntity;
import org.doogle.entity.views.TransactionSummaryView;
import org.doogle.mapper.GenericMapper;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

@ApplicationScoped
@GraphQLApi
public class LoaderResource {

  private final Vertx vertx;
  ObjectMapper mapper;

  SecureRandom random = new SecureRandom();

  BroadcastProcessor<TransactionSummaryView> processor = BroadcastProcessor.create();

  @Inject
  public LoaderResource(Vertx vertx, ObjectMapper mapper) {
    this.vertx = vertx;
    this.mapper = mapper;
  }

  public static ZonedDateTime generateRandomDateTime(ZonedDateTime start, ZonedDateTime end) {
    // Generate a random datetime between `start` and `end`
    return ZonedDateTime.now();
  }

  @Query("LoadEthereumTransactions")
  public Uni<List<TransactionEntity>> loadEthereumTransactions() throws IOException {
    Future<List<TransactionEntity>> data = vertx.fileSystem().readFile("ethereum-transactions.json")
        .map(content -> content.toString(StandardCharsets.UTF_8))
        .map(Unchecked.function(dt -> mapper.readValue(dt, JsonNode.class)))
        .map(this::enrichTimestamp)
        .map(ej -> mapper.convertValue(ej, new TypeReference<List<TransactionData>>() {
        })).map(tds -> tds.stream().map(GenericMapper::getTransactionsFromTransactionData)
            .flatMap(List::stream).toList());

    return UniHelper.toUni(data).call(TransactionEntity::persistOrUpdate)
        .log("enriched_transaction");
  }

  @Query("LoadEthereumBlock")
  public Uni<List<BlockEntity>> loadEthereumBlock() throws IOException {
    Future<List<BlockEntity>> data = vertx.fileSystem().readFile("ethereum-blocks.json")
        .map(content -> content.toString(StandardCharsets.UTF_8))
        .map(Unchecked.function(dt -> mapper.readValue(dt, JsonNode.class)))
        .map(this::enrichTimestamp)
        .map(ej -> mapper.convertValue(ej, new TypeReference<List<BlockData>>() {
        })).map(bds -> bds.stream().map(GenericMapper::getBlocksFromBlockData).toList());

    return UniHelper.toUni(data).call(BlockEntity::persistOrUpdate).log("enriched_block");
  }

  public JsonNode enrichTimestamp(JsonNode jn) {
    if (jn.isArray()) {
      ArrayNode an = (ArrayNode) jn;
      for (int i = 0; i < an.size(); i++) {
        an.set(i, enrichTimestamp(an.get(i)));
      }
      return jn;
    }

    for (Iterator<String> it = jn.fieldNames(); it.hasNext(); ) {
      String f = it.next();
      if ("timestamp".equalsIgnoreCase(f) && (jn.get(f).getNodeType() == STRING
          || jn.get(f).getNodeType() == NUMBER)) {
        ZonedDateTime convertedDateTime = ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(Long.parseLong(jn.get(f).toString())), ZoneOffset.UTC)
            .plusYears(6);
        ((ObjectNode) jn).put(f, convertedDateTime.toString());
      }
      if (Arrays.asList(ARRAY, BINARY, OBJECT, POJO).contains(jn.get(f).getNodeType())) {
        ((ObjectNode) jn).replace(f, enrichTimestamp(jn.get(f)));
      }
    }
    return jn;
  }

  @Query("TransactionOriginatedFromCountWithOrderAndLimit")
  public Uni<List<TransactionSummaryView>> getNTransactionsOriginatedFromWithCountAndLimit(
      long limit, long order) {
    return TransactionEntity.getNTransactionsOriginatedFromWithCountAndLimit(limit, order)
        .invoke(t -> processor.onNext(t)).log().collect().asList();
  }

  @Query("TransactionCompletedAtCountWithOrderAndLimit")
  public Uni<List<TransactionSummaryView>> getNTransactionsCompletedAtWithCountAndLimit(long limit,
      long order) {
    return TransactionEntity.getNTransactionsCompletedAtWithCountAndLimit(limit, order)
        .invoke(t -> processor.onNext(t)).log().collect().asList();
  }

  @Query("TransactionAverageValueAndStdDeviationWithSortOrder")
  public Uni<List<TransactionSummaryView>> getAverageValueAndStandardDeviation(long order) {
    return TransactionEntity.getAverageValueAndStandardDeviation(order)
        .invoke(t -> processor.onNext(t)).log().collect().asList();
  }

  @Subscription
  public Multi<TransactionSummaryView> transactionSummary() {
    return processor;
  }

}
