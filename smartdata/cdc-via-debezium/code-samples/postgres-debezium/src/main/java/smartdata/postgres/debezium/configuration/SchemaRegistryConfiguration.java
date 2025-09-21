package smartdata.postgres.debezium.configuration;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "schema-registry")
public interface SchemaRegistryConfiguration {
    String url();

    Compatibility compatibility();

    boolean autoRegister();

    enum Compatibility {
        NONE, BACKWARD, BACKWARD_TRANSITIVE, FORWARD, FORWARD_TRANSITIVE, FULL, FULL_TRANSITIVE
    }
}
