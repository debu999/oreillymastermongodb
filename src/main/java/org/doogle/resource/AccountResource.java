package org.doogle.resource;

import static com.mongodb.client.model.Updates.inc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.reactivestreams.client.ClientSession;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.util.List;
import mutiny.zero.flow.adapters.AdaptersToFlow;
import org.bson.Document;
import org.doogle.entity.AccountEntity;
import org.doogle.mappers.AccountMapper;
import org.doogle.mappers.utils.ObjectIdUtils;
import org.doogle.model.AccountModel;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
@ApplicationScoped
public class AccountResource {

  @Inject
  MeterRegistry registry;
  @Inject
  ObjectMapper mapper;
  @Inject
  AccountMapper accountMapper;
  @Inject
  ReactiveMongoClient client;

  private Uni<Void> abortTransaction(ClientSession sess) {
    return Uni.createFrom().publisher(AdaptersToFlow.publisher(sess.abortTransaction()))
        .log("Transaction Aborted");
  }

  private Uni<Void> commitTransaction(ClientSession sess) {
    return Uni.createFrom().publisher(AdaptersToFlow.publisher(sess.commitTransaction()))
        .log("Transaction Committed");
  }

  @Query("Account")
  public Uni<List<AccountModel>> fetchAccounts() {
    registry.counter("fetchAccountGraphQL", "type", "methodcall").increment();
    return AccountEntity.getAll().log("ALL_TICKETS")
        .map(b -> accountMapper.fromAccountEntities(b));
  }

  @Mutation("createUpdateAccount")
  public Uni<AccountModel> createUpdateAccount(AccountModel account) {
    registry.counter("createUpdateAccount", "type", "methodcall").increment();
    return Uni.createFrom().item(account)
        .map(t -> accountMapper.toAccountEntity(t))
        .flatMap(AccountEntity::persistOrUpdateAccountEntity).log()
        .map(te -> accountMapper.fromAccountEntity(te));
  }

  @Mutation("deleteAccount")
  public Uni<AccountModel> deleteAccount(String id) {
    registry.counter("deleteAccount", "type", "methodcall").increment();
    return Uni.createFrom().item(id)
        .map(ObjectIdUtils::toObjectId)
        .flatMap(AccountEntity::deleteAccountEntity).log("DELETED_ACCOUNT")
        .map(accountEntity -> accountMapper.fromAccountEntity(accountEntity))
        .log("DELETED_ACCOUNT_MODEL");
  }

  @Mutation("editAccountChangeStreamPreAndPostImages")
  public Uni<String> editAccountChangeStreamPreAndPostImages(Boolean enabled) {
    registry.counter("editChangeStreamPreAndPostImages", "type", "methodcall").increment();
    return AccountEntity.editChangeStreamPreAndPostImages(enabled);
  }

  @Mutation("createAccountCollection")
  public Uni<String> createAccountCollection() {
    registry.counter("createAccountCollection", "type", "methodcall").increment();
    return AccountEntity.createCollection();
  }

  @Mutation("accountTransfer")
  public Uni<List<AccountModel>> accountTransfer(String fromAccountIdentifier,
      String toAccountIdentifier, Double amountToTransfer) {
    Log.infov("transferring {0} Hypnotons from {1} to {2}", amountToTransfer, fromAccountIdentifier,
        toAccountIdentifier);
    return client.startSession()
        .flatMap(s -> {
          Log.infov("session active transaction {0}", s.hasActiveTransaction());
          s.startTransaction();
          Log.infov("session {0}", s);
          Uni<AccountEntity> sourceAccount =
              AccountEntity.getCollection()
                  .findOneAndUpdate(s, new Document("accountIdentifier", fromAccountIdentifier),
                      inc("accountBalance", amountToTransfer * (-1)),
                      new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
                          .upsert(true))
                  .log("SOURCE_ACCOUNT_AFTER");
//        AccountEntity.getCollection()
//        .updateOne(new Document("accountIdentifier", fromAccountIdentifier)
//            , new Document("$inc", new Document("accountBalance", amountToTransfer * (-1)))
//        ).log("sourceAccountUpdate").flatMap(r-> AccountEntity.findByAccountIdentifier(
//            fromAccountIdentifier));

          Uni<AccountEntity> targetAccount =
              AccountEntity.getCollection()
                  .findOneAndUpdate(s, new Document("accountIdentifier", toAccountIdentifier),
                      inc("accountBalance", amountToTransfer),
                      new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
                          .upsert(true)).onFailure().retry()
                  .withBackOff(Duration.ofMillis(100), Duration.ofSeconds(1)).atMost(5)
                  .log("TARGET_ACCOUNT_AFTER");
//        AccountEntity.getCollection()
//        .updateOne(new Document("accountIdentifier", toAccountIdentifier)
//            , new Document("$inc", new Document("accountBalance", amountToTransfer))
//        ).log("targetAccountUpdate").flatMap(r-> AccountEntity.findByAccountIdentifier(
//            toAccountIdentifier));
          Log.infov("Is there a active transaction {0}", s.hasActiveTransaction());
          return Uni.combine().all().unis(sourceAccount, targetAccount).asTuple().log("ACCOUNTS")
              .invoke(this::checkTransaction).map(t -> List.of(t.getItem1(), t.getItem2()))
              .map(acc -> accountMapper.fromAccountEntities(acc))
              .call(v->commitTransaction(s)).onFailure().call(
                  error -> abortTransaction(s).onFailure().retry()
                      .withBackOff(Duration.ofSeconds(2), Duration.ofSeconds(5)).atMost(5)
                      .eventually(s::close));
        });
  }

  private void checkTransaction(Tuple2<AccountEntity, AccountEntity> t) {
    if (t.getItem1().getAccountBalance() < 0 || t.getItem2().getAccountBalance() < 0) {
      throw new UnsupportedOperationException("Funds Insufficient");
    }
  }


}
