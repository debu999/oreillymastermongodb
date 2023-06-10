package org.doogle.cdc.service;

import static io.debezium.data.Envelope.FieldName.AFTER;
import static io.debezium.data.Envelope.FieldName.BEFORE;
import static io.debezium.data.Envelope.FieldName.OPERATION;
import static io.debezium.data.Envelope.FieldName.SOURCE;
import static io.debezium.data.Envelope.FieldName.TIMESTAMP;
import static io.debezium.data.Envelope.FieldName.TRANSACTION;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class DebeziumService {

  public static final String UPDATE_DESCRIPTION = "updateDescription";
  public static Map<String, ?> jsonConverterConfigs = Map.of("schemas.enabled", false);
  public JsonDeserializer jsonDeserializer = new JsonDeserializer();
  // Interface to send events to movies Kafka topic
  @Channel("bookOrders")
  Emitter<Record<Long, JsonNode>> bookOrdersEmitter;
  @Channel("bulkCollection")
  Emitter<Record<Long, JsonNode>> bulkCollectionEmitter;

  public static JsonConverter getJsonConverter(Map<String, ?> configs, boolean isKey) {
    JsonConverter converter = new JsonConverter();
    converter.configure(configs, isKey);
    return converter;
  }

  public void logEvents(Map<String, Optional<String>> eventRecord) {
    Log.info(eventRecord);
  }

  public JsonDeserializer getJsonDeserializer() {
    return this.jsonDeserializer;
  }

  public Map<String, Optional<String>> convertToEventMap(Struct sourceRecordStruct) {
    return Map.of(
        BEFORE, Optional.ofNullable(sourceRecordStruct.getString(BEFORE)),
        AFTER, Optional.ofNullable(sourceRecordStruct.getString(AFTER)),
        OPERATION, Optional.ofNullable(sourceRecordStruct.getString(OPERATION)),
        TIMESTAMP, Optional.of(sourceRecordStruct.getInt64(TIMESTAMP).toString()),
        UPDATE_DESCRIPTION,
        Optional.ofNullable(convertStructToString(sourceRecordStruct.getStruct(UPDATE_DESCRIPTION), false)),
        SOURCE, Optional.ofNullable(convertStructToString(sourceRecordStruct.getStruct(SOURCE), false)),
        TRANSACTION, Optional.ofNullable(convertStructToString(sourceRecordStruct.getStruct(TRANSACTION), false))
    );
  }

  public String convertStructToString(Struct struct, boolean isKey) {
    if (ObjectUtils.isEmpty(struct)) {
      return null;
    }
    JsonConverter jsonConverter = getJsonConverter(jsonConverterConfigs, isKey);
    JsonNode jsonNode = jsonDeserializer.deserialize("", jsonConverter.fromConnectData("",
        struct.schema(), struct));
    return jsonNode.toString();
  }

}
