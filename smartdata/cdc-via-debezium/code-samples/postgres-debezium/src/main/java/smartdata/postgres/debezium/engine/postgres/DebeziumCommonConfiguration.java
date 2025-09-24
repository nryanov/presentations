package smartdata.postgres.debezium.engine.postgres;

import jakarta.enterprise.context.ApplicationScoped;
import smartdata.postgres.debezium.configuration.DebeziumConfiguration;

import java.util.Properties;

@ApplicationScoped
public class DebeziumCommonConfiguration {
    private final DebeziumConfiguration debeziumConfiguration;

    public DebeziumCommonConfiguration(DebeziumConfiguration debeziumConfiguration) {
        this.debeziumConfiguration = debeziumConfiguration;
    }

    public Properties properties() {
        var properties = new Properties();
        properties.setProperty("name", debeziumConfiguration.name());
        properties.setProperty("connector.class", debeziumConfiguration.connectorClass());
        debeziumConfiguration.additionalProperties().forEach(properties::setProperty);
        // offset storage settings
        properties.setProperty("offset.storage", debeziumConfiguration.offsetStorage().storageClass());
        properties.setProperty("offset.flush.interval.ms", String.valueOf(debeziumConfiguration.offsetStorage().flushInterval()));
        debeziumConfiguration.offsetStorage().additionalProperties().forEach((key, value) -> properties.setProperty("offset.storage." + key, value));
        // database settings
        properties.setProperty("database.hostname", debeziumConfiguration.database().host());
        properties.setProperty("database.dbname", debeziumConfiguration.database().name());
        properties.setProperty("database.port", String.valueOf(debeziumConfiguration.database().port()));
        properties.setProperty("database.user", debeziumConfiguration.database().username());
        properties.setProperty("database.password", debeziumConfiguration.database().password());
        properties.setProperty("publication.name", debeziumConfiguration.replication().publicationName());
        properties.setProperty("slot.name", debeziumConfiguration.replication().slotName());
        properties.setProperty("plugin.name", debeziumConfiguration.replication().pluginName());
        properties.setProperty("snapshot.mode", debeziumConfiguration.replication().snapshotMode().name());
        properties.setProperty("topic.prefix", debeziumConfiguration.replication().topicPrefix());

        return properties;
    }
}
