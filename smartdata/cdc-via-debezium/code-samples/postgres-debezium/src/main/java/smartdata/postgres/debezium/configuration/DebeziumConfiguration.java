package smartdata.postgres.debezium.configuration;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ConfigMapping(prefix = "debezium")
public interface DebeziumConfiguration {
    String name();

    @WithDefault("io.debezium.connector.postgresql.PostgresConnector")
    String connectorClass();

    @WithName("offset-storage")
    OffsetStorageConfiguration offsetStorage();

    @WithName("database")
    DatabaseConfiguration database();

    @WithName("replication")
    ReplicationConfiguration replication();

    EventFormat eventFormat();

    Map<String, String> additionalProperties();

    interface OffsetStorageConfiguration {
        @WithDefault("org.apache.kafka.connect.storage.FileOffsetBackingStore")
        String storageClass();

        long flushInterval();

        Map<String, String> additionalProperties();
    }

    interface DatabaseConfiguration {
        String host();

        String name();

        int port();

        String username();

        String password();

        Map<String, String> additionalProperties();
    }

    interface ReplicationConfiguration {
        String publicationName();

        @WithDefault("pgoutput")
        String pluginName();

        String slotName();

        String topicPrefix();

        SnapshotMode snapshotMode();

        Map<String, String> additionalProperties();

        enum SnapshotMode {
            INITIAL, NONE, INITIAL_ONLY, ALWAYS, NO_DATA, CUSTOM
        }
    }

    enum EventFormat {
        CONFLUENT_AVRO, JSON, AVRO
    }
}
