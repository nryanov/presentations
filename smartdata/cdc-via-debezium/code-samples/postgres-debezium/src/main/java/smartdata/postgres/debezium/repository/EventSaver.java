package smartdata.postgres.debezium.repository;


import smartdata.postgres.debezium.event.model.EventCommitter;
import smartdata.postgres.debezium.event.model.EventRecord;
import java.util.stream.Stream;

public interface EventSaver {
    void save(Stream<EventRecord> events, EventCommitter committer);
}
