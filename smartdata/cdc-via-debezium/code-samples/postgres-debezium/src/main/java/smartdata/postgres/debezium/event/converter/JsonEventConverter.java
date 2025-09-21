package smartdata.postgres.debezium.event.converter;

import io.debezium.engine.ChangeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DecoderFactory;
import org.apache.parquet.cli.json.AvroJson;
import smartdata.postgres.debezium.event.model.EventRecord;

import java.io.IOException;

@ApplicationScoped
public class JsonEventConverter implements EventConverter<String, String> {
    @Override
    public EventRecord convert(ChangeEvent<String, String> event) {
        var rawJson = event.value();

        try {
            var parsedJson = AvroJson.parse(rawJson);
            var schema = AvroJson.inferSchema(parsedJson, event.destination() + "_value");
            var reader = new GenericDatumReader<>(schema);

            var object = reader.read(null, DecoderFactory.get().jsonDecoder(schema, rawJson));
            return new EventRecord(
                    (GenericRecord) object,
                    event.destination()
            );
        } catch (IOException e) {
            // todo: domain IO error
            throw new RuntimeException(e);
        } catch (Exception e) {
            // todo: domain error
            throw new RuntimeException(e);
        }
    }
}
