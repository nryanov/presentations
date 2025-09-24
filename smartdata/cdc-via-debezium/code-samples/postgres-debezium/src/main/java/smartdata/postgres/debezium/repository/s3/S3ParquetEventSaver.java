package smartdata.postgres.debezium.repository.s3;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.parquet.hadoop.ParquetWriter;
import org.jboss.logging.Logger;
import smartdata.postgres.debezium.configuration.EventSaverConfiguration;
import smartdata.postgres.debezium.event.model.EventCommitter;
import smartdata.postgres.debezium.event.model.EventRecord;
import smartdata.postgres.debezium.repository.EventSaver;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@ApplicationScoped
public class S3ParquetEventSaver implements EventSaver {
    private static final Logger logger = Logger.getLogger(S3ParquetEventSaver.class);

    private final S3ParquetWriterBuilder s3ParquetWriterBuilder;

    private final List<EventCommitter> committers;
    private final Map<String, ParquetWriter> openedDescriptors;

    private final Duration timeoutThreshold;
    private final int totalRecordsThreshold;

    private final ScheduledExecutorService scheduledExecutor;

    private int currentRecords;

    public S3ParquetEventSaver(
            S3ParquetWriterBuilder s3ParquetWriterBuilder,
            EventSaverConfiguration configuration
    ) {
        this.s3ParquetWriterBuilder = s3ParquetWriterBuilder;

        this.committers = new ArrayList<>();
        this.openedDescriptors = new HashMap<>();

        this.timeoutThreshold = configuration.threshold().timeout();
        this.totalRecordsThreshold = configuration.threshold().totalRecords();

        this.currentRecords = 0;

        this.scheduledExecutor = Executors.newScheduledThreadPool(1);
        this.scheduledExecutor.scheduleWithFixedDelay(() -> attemptToDumpCurrentData(true), timeoutThreshold.toMillis(), timeoutThreshold.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void save(Stream<EventRecord> events, EventCommitter committer) {
        attemptToDumpCurrentData(false);
        backlogData(events, committer);
    }

    @SuppressWarnings({"unchecked", "resource"})
    private void backlogData(Stream<EventRecord> events, EventCommitter committer) {
        synchronized (this) {
            logger.info("Append records (stream)");
            events.forEach(event -> {
                var destination = event.destination();
                var location = generateLocation("warehouse", event.destination());
                var currentEvents = openedDescriptors.computeIfAbsent(destination, ignored -> s3ParquetWriterBuilder.create(location, event.schema()));

                try {
                    currentEvents.write(event.record());
                    currentRecords++;
                } catch (IOException e) {
                    logger.errorf(e, "Error happened while adding new avro to parquet writer: %s", e.getLocalizedMessage());
                    throw new RuntimeException(e);
                }
            });

            committers.add(committer);
            logger.infof("Successfully appended records (stream)");
        }
    }

    private void attemptToDumpCurrentData(boolean byTime) {
        synchronized (this) {
            if (!byTime && currentRecords < totalRecordsThreshold) {
                return;
            }

            if (byTime) {
                logger.infof("Dump current events by time");
            } else {
                logger.infof("Dump current events by exceeded records threshold");
            }

            // save events
            for (var entry : openedDescriptors.entrySet()) {
                try {
                    var writer = entry.getValue();
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // commit every hold batch
            committers.forEach(EventCommitter::commit);
            logger.infof("Successfully saved %s total records", currentRecords);

            openedDescriptors.clear();
            committers.clear();
            currentRecords = 0;
            logger.infof("Successfully reset records backlog");
        }
    }

    private String generateLocation(String bucket, String destination) {
        return String.format(
                "s3a://%s/%s/%s.parquet",
                bucket,
                destination,
                System.currentTimeMillis()
        );
    }
}
