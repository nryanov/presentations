package smartdata.postgres.debezium.event.converter;

import io.debezium.engine.ChangeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import smartdata.postgres.debezium.event.model.EventRecord;

import java.io.IOException;

@ApplicationScoped
public class AvroEventConverter implements EventConverter<Object, Object> {

    public AvroEventConverter() {

    }

    @Override
    public EventRecord convert(ChangeEvent<Object, Object> event) {
        try {
            var value = (byte[]) event.value();
            var bin = new SeekableByteArrayInput(value);

            var reader = new DataFileReader<GenericRecord>(bin, new GenericDatumReader<>());
            var parsedRecord = reader.next();

            return new EventRecord(parsedRecord, event.destination());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
