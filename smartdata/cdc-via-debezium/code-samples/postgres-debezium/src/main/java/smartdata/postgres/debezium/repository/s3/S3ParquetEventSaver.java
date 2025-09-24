package smartdata.postgres.debezium.repository.s3;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.hadoop.util.HadoopOutputFile;
import org.jboss.logging.Logger;
import smartdata.postgres.debezium.configuration.EventSaverConfiguration;
import smartdata.postgres.debezium.configuration.S3Configuration;
import smartdata.postgres.debezium.event.model.EventCommitter;
import smartdata.postgres.debezium.event.model.EventRecord;
import smartdata.postgres.debezium.repository.EventSaver;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

    private final S3DataRepository dataRepository;

    private final List<EventCommitter> committers;
    private final Map<String, ParquetWriter> openedDescriptors;
    private final S3Configuration s3Configuration;

    private final Duration timeoutThreshold;
    private final int totalRecordsThreshold;

    private final ScheduledExecutorService scheduledExecutor;

    private int currentRecords;

    public S3ParquetEventSaver(
            S3DataRepository dataRepository,
            EventSaverConfiguration configuration,
            S3Configuration s3Configuration
    ) {
        this.dataRepository = dataRepository;
        this.s3Configuration = s3Configuration;

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
                var currentEvents = openedDescriptors.computeIfAbsent(destination, ignored -> createWriter(location, event.schema()));

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

    private ParquetWriter<GenericRecord> createWriter(String location, Schema schema) {
        try {
            logger.infof("Opening parquet writer for `%s`", location);
            var path = new Path(new URI(location));

            var config = new Configuration();
            config.set("fs.s3a.access.key", s3Configuration.accessKey());
            config.set("fs.s3a.secret.key", s3Configuration.secretAccessKey());
            config.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
            config.set("fs.s3a.path.style.access", String.valueOf(s3Configuration.forcePathAccess()));
            config.set("fs.s3a.endpoint", s3Configuration.endpoint());

            var builder = AvroParquetWriter
                    .<GenericRecord>builder(HadoopOutputFile.fromPath(path, config))
                    // todo: bloom, NDV, stats
                    .withCompressionCodec(CompressionCodecName.ZSTD)
                    .withSchema(schema);
            var writer = builder.build();

            logger.infof("Successfully opened writer for `%s`", location);

            return writer;
        } catch (URISyntaxException e) {
            // todo: domain URI error
            throw new RuntimeException(e);
        } catch (IOException e) {
            // todo: domain IO error
            logger.errorf(e, "Error happened while creating parquet writer: %s", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    private String generateLocation(String bucket, String destination) {
        return String.format(
                "s3a://%s/%s/%s_%s.parquet",
                bucket,
                destination, // todo: -> schema/table/[file1, file2, .., fileN]
                destination,
                System.currentTimeMillis()
        );
    }
}
