package smartdata.postgres.debezium.event.model;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

public record EventRecord(GenericRecord record, String destination) {
    public Schema schema() {
        return record.getSchema();
    }
}
