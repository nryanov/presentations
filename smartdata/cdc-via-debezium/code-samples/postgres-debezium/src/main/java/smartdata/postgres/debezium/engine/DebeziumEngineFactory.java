package smartdata.postgres.debezium.engine;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import org.jboss.logging.Logger;
import smartdata.postgres.debezium.configuration.DebeziumConfiguration;
import smartdata.postgres.debezium.engine.postgres.AvroPostgresqlDebeziumEngine;
import smartdata.postgres.debezium.engine.postgres.ConfluentAvroPostgresqlDebeziumEngine;
import smartdata.postgres.debezium.engine.postgres.JsonPostgresqlDebeziumEngine;
import smartdata.postgres.debezium.engine.postgres.PostgresqlDebeziumEngine;

@ApplicationScoped
public class DebeziumEngineFactory {
    private static final Logger logger = Logger.getLogger(DebeziumEngineFactory.class);

    private final DebeziumConfiguration configuration;
    private final Instance<PostgresqlDebeziumEngine> engines;

    private PostgresqlDebeziumEngine selectedEngine;

    public DebeziumEngineFactory(DebeziumConfiguration configuration, Instance<PostgresqlDebeziumEngine> engines) {
        this.configuration = configuration;
        this.engines = engines;
    }

    void onStart(@Observes StartupEvent ev) {
        logger.info("The application is starting...");

        switch (configuration.eventFormat()) {
            case CONFLUENT_AVRO -> selectedEngine = engines.select(ConfluentAvroPostgresqlDebeziumEngine.class).get();
            case AVRO -> selectedEngine = engines.select(AvroPostgresqlDebeziumEngine.class).get();
            case JSON -> selectedEngine = engines.select(JsonPostgresqlDebeziumEngine.class).get();
        }

        selectedEngine.initialize();
        selectedEngine.start();
    }

    void onStop(@Observes ShutdownEvent ev) {
        logger.info("The application is stopping...");
        selectedEngine.stop();
    }
}
