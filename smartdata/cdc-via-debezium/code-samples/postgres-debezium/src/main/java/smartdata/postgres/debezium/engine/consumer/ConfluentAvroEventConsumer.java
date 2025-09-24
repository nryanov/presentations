package smartdata.postgres.debezium.engine.consumer;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import smartdata.postgres.debezium.event.converter.ConfluentAvroEventConverter;
import smartdata.postgres.debezium.event.model.EventCommitter;
import smartdata.postgres.debezium.service.EventSaverService;

import java.util.List;

@ApplicationScoped
public class ConfluentAvroEventConsumer implements EventConsumer<byte[], byte[]> {
    private static final Logger logger = Logger.getLogger(ConfluentAvroEventConsumer.class);

    private final EventSaverService eventSaver;
    private final ConfluentAvroEventConverter converter;

    public ConfluentAvroEventConsumer(EventSaverService eventSaver, ConfluentAvroEventConverter converter) {
        this.eventSaver = eventSaver;
        this.converter = converter;
    }

    @Override
    public void handleBatch(List<ChangeEvent<byte[], byte[]>> records, DebeziumEngine.RecordCommitter<ChangeEvent<byte[], byte[]>> committer) throws InterruptedException {
        logger.infof("Processing next batch: %s", records.size());

        // to allow GC free space
        var fullClone = records.getLast();
        var eventCommitter = new EventCommitter(committer::markBatchFinished, () -> committer.markProcessed(fullClone));
        var stream = records.stream().map(converter::convert);

        eventSaver.save(stream, eventCommitter);
        logger.infof("Successfully handled batch with `%s` records", records.size());
    }
}
