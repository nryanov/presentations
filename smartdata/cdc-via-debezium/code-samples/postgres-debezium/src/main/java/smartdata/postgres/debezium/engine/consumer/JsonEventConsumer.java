package smartdata.postgres.debezium.engine.consumer;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import smartdata.postgres.debezium.event.converter.JsonEventConverter;
import smartdata.postgres.debezium.event.model.EventCommitter;
import smartdata.postgres.debezium.event.model.EventRecord;
import smartdata.postgres.debezium.event.repository.EventSaver;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JsonEventConsumer implements EventConsumer<String, String> {
    private static final Logger logger = Logger.getLogger(JsonEventConsumer.class);

    private final EventSaver eventSaver;
    private final JsonEventConverter converter;

    public JsonEventConsumer(EventSaver eventSaver, JsonEventConverter converter) {
        this.eventSaver = eventSaver;
        this.converter = converter;
    }

    @Override
    public void handleBatch(List<ChangeEvent<String, String>> records, DebeziumEngine.RecordCommitter<ChangeEvent<String, String>> committer) throws InterruptedException {
        logger.infof("Processing next batch: %s", records.size());

        var convertedEvents = new ArrayList<EventRecord>();
        var eventCommitter = new EventCommitter(committer, () -> {
            try {
                committer.markProcessed(records.getLast());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        for (var record : records) {
            var eventRecord = converter.convert(record);
            convertedEvents.add(eventRecord);

            try {
                // mark each record as processed doesn't commit offsets but needed for final markBatchFinished call
                committer.markProcessed(record);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        eventSaver.save(convertedEvents, eventCommitter);
        logger.infof("Successfully handled batch with `%s` records", records.size());
    }
}
