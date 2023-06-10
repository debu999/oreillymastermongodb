package org.doogle.cdc;

import static io.debezium.data.Envelope.FieldName.OPERATION;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.config.Configuration;
import io.debezium.data.Envelope.Operation;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.DebeziumEngine.RecordCommitter;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.doogle.cdc.service.DebeziumService;
import org.eclipse.microprofile.context.ManagedExecutor;

@ApplicationScoped
public class DebeziumListener {

  ObjectMapper mapper;

  // Start Debezium engine in different thread
  ManagedExecutor executor;

  // Debezium configuration object
  Configuration configuration;

  DebeziumEngine<RecordChangeEvent<SourceRecord>> engine;

  DebeziumService service;

  public DebeziumListener(ObjectMapper mapper, ManagedExecutor executor,
      Configuration configuration,
      DebeziumService service) {
    this.mapper = mapper;
    this.executor = executor;
    this.configuration = configuration;
    this.service = service;
  }

  void onStart(@Observes StartupEvent event) {
//    configure Debezium engine
    this.engine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
        .using(this.configuration.asProperties()).notifying(this::handlePayload)
        .build();

    // Starts Debezium in different thread
    this.executor.execute(this.engine);
  }

  @SneakyThrows
  void handlePayload(List<RecordChangeEvent<SourceRecord>> recordChangeEvents,
      RecordCommitter<RecordChangeEvent<SourceRecord>> recordCommitter) {

    for (RecordChangeEvent<SourceRecord> r : recordChangeEvents) {

      // For each triggered event, we get the information
      SourceRecord sourceRecord = r.record();
      Struct sourceRecordChangeValue = (Struct) sourceRecord.value();

      if (sourceRecordChangeValue == null) {
        continue;
      }
      Operation operation = Operation.forCode(sourceRecordChangeValue.getString(OPERATION));
      Map<String, Optional<String>> sourceRecordChangeValueMap = service.convertToEventMap(
          sourceRecordChangeValue);
      String result = mapper.writeValueAsString(sourceRecordChangeValueMap);

      service.logEvents(sourceRecordChangeValueMap);
    }
  }
//
//  private List<Map<String, Object>> structToMap(Struct struct) {
//    List<Map<String, Object>> result =
//        struct.schema().fields().stream()
//            .map(this::fieldToMap).toList();
//    return result;
//  }
//
//  private Map<String, Object> fieldToMap(Field field) {
//    Map<String, Object> map =
//        field.schema().fields().stream()
//            .map(f -> {
//              Map<String, Object> mapper = f.schema().fields().stream()
//                  .filter(fl -> fl.schema().type().isPrimitive())
//                  .collect(toMap(Field::name, field -> field));
//              flds.stream().filter(fl -> fl.schema().type().isPrimitive())
//              return Pair.of(fieldName.name(), struct.get(fieldName.name()))
//            })
//            .collect(toMap(Pair::getKey, Pair::getValue));
//    if (map.containsKey(FilterJsonFieldEnum.ts_ms.name())) {
//      map.put("changeTime", map.get(FilterJsonFieldEnum.ts_ms.name()));
//      map.remove(FilterJsonFieldEnum.ts_ms.name());
//    }
//    return map;
//  }

  void onStop(@Observes ShutdownEvent event) throws IOException {
    if (this.engine != null) {
      this.engine.close();
    }
  }
}
