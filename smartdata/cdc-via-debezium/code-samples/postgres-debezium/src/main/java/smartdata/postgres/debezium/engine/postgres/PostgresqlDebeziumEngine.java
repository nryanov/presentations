package smartdata.postgres.debezium.engine.postgres;


public interface PostgresqlDebeziumEngine {
    void initialize();

    void start();

    void stop();
}
