package org.doogle.cdc;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import jakarta.enterprise.inject.Produces;
import java.util.Properties;
import io.debezium.config.Configuration;

/**
 * Few Extra configs not added now
 * max.batch.size default 2048
 * max.queue.size default 8192
 * max.queue.size.in.bytes default 0 no limit if you set max.queue.size=1000, and max.queue.size.in.bytes=5000, writing to the queue is blocked after the queue contains 1000 records, or after the volume of the records in the queue reaches 5000 bytes.
 * poll.interval.ms default 1sec
 * connect.backoff.initial.delay.ms default 1sec
 * connect.backoff.max.delay.ms default 1s
 * connect.max.attempts default 16
 * source.struct.version default v2
 * heartbeat.interval.ms default 0 disabled by default
 * skipped.operations default t // c - create/insert, u update/replace, d delete, t truncate, none to not skip any operations
 */

public class DebeziumConnectorConfiguration {

    public static final String DEBEZIUM_PREFIX = "debezium.";
    public final Properties properties = new Properties();

    @Produces
    public Configuration debeziumMongoDBConnectConfiguration() {

        final Config config = ConfigProvider.getConfig();
        configToProperties(config, properties);
        return Configuration.from(properties);
    }

    private void configToProperties(Config config, Properties properties) {
        for (String propertyName : config.getPropertyNames()) {
            if (propertyName.startsWith(DEBEZIUM_PREFIX)) {
                properties.setProperty(propertyName.substring(DEBEZIUM_PREFIX.length()), config.getConfigValue(propertyName).getValue());
            }
        }
    }
}
