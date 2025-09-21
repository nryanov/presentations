package smartdata.postgres.debezium.event.converter;

import io.debezium.engine.ChangeEvent;
import smartdata.postgres.debezium.event.model.EventRecord;

public interface EventConverter<K, V> {
    EventRecord convert(ChangeEvent<K, V> event);
}
