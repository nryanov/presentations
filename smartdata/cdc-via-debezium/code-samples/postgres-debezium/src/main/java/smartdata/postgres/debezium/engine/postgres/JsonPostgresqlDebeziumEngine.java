package smartdata.postgres.debezium.engine.postgres;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import smartdata.postgres.debezium.configuration.DebeziumConfiguration;
import smartdata.postgres.debezium.engine.consumer.JsonEventConsumer;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class JsonPostgresqlDebeziumEngine implements PostgresqlDebeziumEngine {
    private static final Logger logger = Logger.getLogger(JsonPostgresqlDebeziumEngine.class);

    private final DebeziumConfiguration debeziumConfiguration;
    private final JsonEventConsumer consumer;

    private DebeziumEngine<ChangeEvent<String, String>> engine;
    private ExecutorService executor;

    public JsonPostgresqlDebeziumEngine(
            DebeziumConfiguration debeziumConfiguration,
            JsonEventConsumer consumer
    ) {
        this.debeziumConfiguration = debeziumConfiguration;
        this.consumer = consumer;
    }

    @Override
    public void initialize() {
        var properties = properties();
        engine = DebeziumEngine.create(Json.class)
                .using(properties)
                .notifying(consumer)
                .build();

        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void start() {
        executor.execute(engine);
    }

    @Override
    public void stop() {
        try {
            engine.close();
            executor.shutdown();
            while (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.info("Waiting another 5 seconds for the embedded engine to shut down");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.warnf(e, "Error happened while closing engine: %s", e.getLocalizedMessage());
        }
    }

    private Properties properties() {
        var properties = new Properties();
        properties.setProperty("name", debeziumConfiguration.name());
        properties.setProperty("connector.class", debeziumConfiguration.connectorClass());
        debeziumConfiguration.additionalProperties().forEach(properties::setProperty);
        // offset storage settings
        properties.setProperty("offset.storage", debeziumConfiguration.offsetStorage().storageClass());
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
        // json
        properties.setProperty("key.converter.schemas.enable", "false");
        properties.setProperty("value.converter.schemas.enable", "false");

        return properties;
    }
}
