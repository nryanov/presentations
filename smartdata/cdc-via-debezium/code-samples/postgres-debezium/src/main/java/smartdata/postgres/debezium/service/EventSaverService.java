package smartdata.postgres.debezium.service;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import smartdata.postgres.debezium.configuration.EventSaverConfiguration;
import smartdata.postgres.debezium.event.model.EventCommitter;
import smartdata.postgres.debezium.event.model.EventRecord;
import smartdata.postgres.debezium.repository.EventSaver;
import smartdata.postgres.debezium.repository.console.ConsoleEventSaver;
import smartdata.postgres.debezium.repository.s3.S3ParquetEventSaver;

import java.util.stream.Stream;

@ApplicationScoped
public class EventSaverService {
    private final EventSaverConfiguration eventSaverConfiguration;
    private final Instance<EventSaver> eventSavers;

    private EventSaver selectedEventSaver;

    public EventSaverService(EventSaverConfiguration eventSaverConfiguration, Instance<EventSaver> eventSavers) {
        this.eventSaverConfiguration = eventSaverConfiguration;
        this.eventSavers = eventSavers;
    }

    void onStart(@Observes StartupEvent ev) {
        switch (eventSaverConfiguration.type()) {
            case CONSOLE -> selectedEventSaver = eventSavers.select(ConsoleEventSaver.class).get();
            case S3 -> selectedEventSaver = eventSavers.select(S3ParquetEventSaver.class).get();
        }
    }

    public void save(Stream<EventRecord> events, EventCommitter committer) {
        selectedEventSaver.save(events, committer);
    }
}
