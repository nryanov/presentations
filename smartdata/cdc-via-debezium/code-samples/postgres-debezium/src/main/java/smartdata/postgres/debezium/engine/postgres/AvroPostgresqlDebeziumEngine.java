package smartdata.postgres.debezium.engine.postgres;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Binary;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import smartdata.postgres.debezium.engine.consumer.AvroEventConsumer;
import smartdata.postgres.debezium.event.converter.serde.BinaryAvroSerDe;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class AvroPostgresqlDebeziumEngine implements PostgresqlDebeziumEngine {
    private static final Logger logger = Logger.getLogger(AvroPostgresqlDebeziumEngine.class);

    private final DebeziumCommonConfiguration debeziumCommonConfiguration;
    private final AvroEventConsumer consumer;

    private DebeziumEngine<ChangeEvent<Object, Object>> engine;
    private ExecutorService executor;

    public AvroPostgresqlDebeziumEngine(
            AvroEventConsumer consumer,
            DebeziumCommonConfiguration debeziumCommonConfiguration
    ) {
        this.debeziumCommonConfiguration = debeziumCommonConfiguration;
        this.consumer = consumer;
    }

    @Override
    public void initialize() {
        var properties = properties();
        engine = DebeziumEngine.create(Binary.class)
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
        var properties = debeziumCommonConfiguration.properties();
        // avro
        properties.setProperty("key.converter.delegate.converter.type", BinaryAvroSerDe.class.getName());
        properties.setProperty("value.converter.delegate.converter.type", BinaryAvroSerDe.class.getName());

        return properties;
    }
}
