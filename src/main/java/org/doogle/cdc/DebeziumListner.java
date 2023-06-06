package org.doogle.cdc;

import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.source.SourceRecord;
import org.doogle.cdc.service.DebeziumService;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.io.IOException;

@ApplicationScoped
public class DebeziumListner {

    @Inject
    ManagedExecutor executor;

    @Inject
    Configuration configuration;

    DebeziumEngine<RecordChangeEvent<SourceRecord>> engine;

    @Inject
    DebeziumService service;

    void onStart(@Observes StartupEvent event) {
        this.engine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class)).using(this.configuration.asProperties()).notifying(this::handleChangeEvent).build();
        // Starts Debezium in different thread
        this.executor.execute(this.engine);
    }

    void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        JsonConverter jsonConverter = null;
        JsonDeserializer jsonDeserializer = null;

        try {
            SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
            Log.infov("key {} , value = {}, topic: {}", sourceRecord.key(), sourceRecord.value(), sourceRecord.topic());
            service.logEvents();
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        } finally {
            if (jsonConverter != null) {
                jsonConverter.close();

            }
            if (jsonDeserializer != null) {
                jsonDeserializer.close();

            }
        }
    }

    void onStop(@Observes ShutdownEvent event) throws IOException {
        if(this.engine != null){
            this.engine.close();
        }
    }
}
