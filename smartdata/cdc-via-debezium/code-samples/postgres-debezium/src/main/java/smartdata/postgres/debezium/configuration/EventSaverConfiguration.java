package smartdata.postgres.debezium.configuration;

import io.smallrye.config.ConfigMapping;

import java.time.Duration;

@ConfigMapping(prefix = "event-saver")
public interface EventSaverConfiguration {
    Threshold threshold();

    EventSaverType type();

    interface Threshold {
        Duration timeout();

        int totalRecords();
    }


    enum EventSaverType {
        CONSOLE, S3
    }
}
