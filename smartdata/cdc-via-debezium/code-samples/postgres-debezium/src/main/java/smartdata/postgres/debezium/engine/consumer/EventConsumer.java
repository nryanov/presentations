package smartdata.postgres.debezium.engine.consumer;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;

public interface EventConsumer<K, V> extends DebeziumEngine.ChangeConsumer<ChangeEvent<K, V>> {}
