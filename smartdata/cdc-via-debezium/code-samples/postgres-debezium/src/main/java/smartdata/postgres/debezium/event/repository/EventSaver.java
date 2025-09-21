package smartdata.postgres.debezium.event.repository;


import smartdata.postgres.debezium.event.model.EventCommitter;
import smartdata.postgres.debezium.event.model.EventRecord;

import java.util.List;
import java.util.stream.Stream;

public interface EventSaver {
    void save(List<EventRecord> events, EventCommitter committer);

    void save(Stream<EventRecord> events, EventCommitter committer);
}
