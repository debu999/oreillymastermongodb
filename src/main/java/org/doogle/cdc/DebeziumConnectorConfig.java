package org.doogle.cdc;

import io.debezium.config.Configuration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

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

public class DebeziumConnectorConfig {
    @ConfigProperty(name = "quarkus.mongodb.database")
    String mongodbName;

    @ConfigProperty(name = "debezium.collection.include.list")
    String debeziumCollectionIncludeList;

    @ConfigProperty(name = "quarkus.mongodb.connection-string")
    String mongodbConnectionString;

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String kafkaBootStrapServers;

    @Produces
    public Configuration debeziumMongodbConnect()
    {
        return Configuration.create()
                .with("name", "mongodb-connector")
                .with("connector.class", "io.debezium.connector.mongodb.MongoDbConnector")
                .with("mongodb.connection.string", mongodbConnectionString)
                .with("mongodb.connection.mode", "replica-set")
                .with("topic.prefix", "mongotopic")
//                .with("monogdb.user", "") // Added in mongo connection string
//                .with("monogdb.password", "") // Added in mongo connection string
//                .with("mongodb.authsource","admin") // default is admin
                .with("mongodb.ssl.enabled", true) // default is false
                .with("mongodb.ssl.invalid.hostname.allowed", true) // default is false
                .with("database.include.list", mongodbName) // An optional comma-separated list of regular expressions that match database names to be monitored. By default, all databases are monitored. When database.include.list is set, the connector monitors only the databases that the property specifies.
//                .with("database.exclude.list", "") // set only if database.include.list is not set both do not work.
                .with("collection.include.list", debeziumCollectionIncludeList )
//                .with("collection.exclude.list", "") // Collection identifiers are of the form databaseName.collectionName. do not use if collection.include.list is set.
                .with("snapshot.mode", "initial") // initial - When the connector starts, if it does not detect a value in its offsets topic, it performs a snapshot of the database.
                // never -  When the connector starts, it skips the snapshot process and immediately begins to stream change events for operations that the database records to the oplog.
                .with("capture.mode", "change_streams_update_full_with_pre_image") // default change_streams_update_full but we can also use change_streams / change_streams_update_full_with_pre_image / change_streams_with_pre_image
//                .with("snapshot.include.collection.list", "") // All collections specified in collection.include.list
//                .with("field.exclude.list", "") // Fully-qualified names for fields are of the form databaseName.collectionName.fieldName.nestedFieldName, where databaseName and collectionName may contain the wildcard (*) which matches any characters.
//                .with("field.renames", "") // Fully-qualified replacements for fields are of the form databaseName.collectionName.fieldName.nestedFieldName:newNestedFieldName, where databaseName and collectionName may contain the wildcard (*) which matches any characters, the colon character (:) is used to determine rename mapping of field.
                .with("tasks.max", 1) // default 1 good for replica set for sharded db it should be more than no. of shards.
                .with("snapshot.max.threads", 1) // Positive integer value that specifies the maximum number of threads used to perform an intial sync of the collections in a replica set. Defaults to 1.
                .with("tombstones.on.delete", true)  // default true delete operation is represented by a delete event and a subsequent tombstone event. After a source record is deleted, emitting a tombstone event (the default behavior) allows Kafka to completely delete all events that pertain to the key of the deleted row in case log compaction is enabled for the topic.
//                .with("snapshot.delay.ms", "")  // An interval in milliseconds that the connector should wait before taking a snapshot after starting up.
                .with("snapshot.fetch.size", 0) // Default 0 so server choose an appropriate fetch size.
                .with("schema.name.adjustment.mode", "avro" ) // none is default , avro replaces the characters that cannot be used in the Avro type name with underscore.
//              avro_unicode replaces the underscore or characters that cannot be used in the Avro type name with corresponding unicode like _uxxxx. Note: _ is an escape sequence like backslash in Java
                .with("field.name.adjustment.mode", "avro") // same as schema.name.adjustment.mode
//                .with("mongodb.hosts" , "'") // not needed as we added connection string deprecated
                .with("skipped.operations", "none")
                .with("offset.storage", "org.apache.kafka.connect.storage.KafkaOffsetBackingStore")
                .with("offset.storage.topic", "debezium-offset-storage")
                .with("group.id", "doogle-debezium-offset-storage-consumer-group")
                .with("key.converter", "org.apache.kafka.connect.json.JsonConverter")
                .with("value.converter", "org.apache.kafka.connect.json.JsonConverter")
                .with("internal.key.converter", "org.apache.kafka.connect.json.JsonConverter")
                .with("internal.value.converter", "org.apache.kafka.connect.json.JsonConverter")
                .with("bootstrap.servers", kafkaBootStrapServers)
                .with("offset.flush.interval.ms", "10000")
                .with("offset.storage.partitions", "1")
                .with("offset.storage.replication.factor", "1")
                .with("mongodb.name", mongodbName)
                .with("mongodb.members.auto.discover", true)
                .with("connection.keep.alive", true)
                .with("include.schema.changes", true).build();
    }
}
