package smartdata.postgres.debezium.event.converter;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.debezium.engine.ChangeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.generic.GenericRecord;
import smartdata.postgres.debezium.configuration.SchemaRegistryConfiguration;
import smartdata.postgres.debezium.event.model.EventRecord;

import java.util.Map;

@ApplicationScoped
public class ConfluentAvroEventConverter implements EventConverter<byte[], byte[]> {
    private final KafkaAvroDeserializer valueDeserializer;
    // currently not used
    private final KafkaAvroDeserializer keyDeserializer;

    public ConfluentAvroEventConverter(SchemaRegistryConfiguration schemaRegistryConfiguration) {
        var schemaRegistryClient = new CachedSchemaRegistryClient(schemaRegistryConfiguration.url(), 100);
        this.valueDeserializer = new KafkaAvroDeserializer(schemaRegistryClient, Map.of("schema.registry.url", "fake"));
        this.keyDeserializer = new KafkaAvroDeserializer(schemaRegistryClient, Map.of("schema.registry.url", "fake"), true);
    }

    @Override
    public EventRecord convert(ChangeEvent<byte[], byte[]> event) {
        return new EventRecord(
                (GenericRecord) valueDeserializer.deserialize(event.destination(), event.value()),
                event.destination()
        );
    }
}
