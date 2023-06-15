package org.doogle.entity;

import static com.mongodb.client.model.Updates.inc;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.Document;

public class SequenceEntity extends ReactivePanacheMongoEntity {

  public static Uni<SequenceEntity> getNextSequenceByType(String type, Long... incrementBy) {
    long increment = incrementBy.length > 0 ? incrementBy[0] : 1;

    return getCollection().findOneAndUpdate(new Document("type", type), inc("value", increment),
        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER).upsert(true)).log("SEQUENCE_INCREMENT");
  }

  public static ReactiveMongoCollection<SequenceEntity> getCollection()
  {
    return mongoCollection();
  }

}
