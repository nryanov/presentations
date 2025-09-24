package smartdata.postgres.debezium.repository.console;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import smartdata.postgres.debezium.event.model.EventCommitter;
import smartdata.postgres.debezium.event.model.EventRecord;
import smartdata.postgres.debezium.repository.EventSaver;

import java.util.stream.Stream;

@ApplicationScoped
public class ConsoleEventSaver implements EventSaver {
    private static final Logger logger = Logger.getLogger(ConsoleEventSaver.class);

    @Override
    public void save(Stream<EventRecord> events, EventCommitter committer) {
        events.forEach(event -> logger.infof("Event: %s", event.record()));
        committer.commit();
    }
}
