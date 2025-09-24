package smartdata.postgres.debezium.engine.consumer;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import smartdata.postgres.debezium.event.converter.AvroEventConverter;
import smartdata.postgres.debezium.event.model.EventCommitter;
import smartdata.postgres.debezium.repository.EventSaver;

import java.util.List;

@ApplicationScoped
public class AvroEventConsumer implements EventConsumer<Object, Object> {
    private static final Logger logger = Logger.getLogger(AvroEventConsumer.class);

    private final EventSaver eventSaver;
    private final AvroEventConverter converter;

    public AvroEventConsumer(EventSaver eventSaver, AvroEventConverter converter) {
        this.eventSaver = eventSaver;
        this.converter = converter;
    }

    @Override
    public void handleBatch(List<ChangeEvent<Object, Object>> records, DebeziumEngine.RecordCommitter<ChangeEvent<Object, Object>> committer) {
        logger.infof("Processing next batch: %s", records.size());

        // to allow GC free space
        var fullClone = records.getLast();
        var eventCommitter = new EventCommitter(committer::markBatchFinished, () -> committer.markProcessed(fullClone));
        var stream = records.stream().map(converter::convert);

        eventSaver.save(stream, eventCommitter);
        logger.infof("Successfully handled batch with `%s` records", records.size());
    }
}
