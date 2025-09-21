package smartdata.postgres.debezium.engine.offset;

import io.debezium.storage.jdbc.offset.JdbcOffsetBackingStore;
import org.apache.kafka.connect.util.Callback;
import org.jboss.logging.Logger;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

public class LoggedJdbcOffsetBackingStore extends JdbcOffsetBackingStore {
    private static final Logger logger = Logger.getLogger(LoggedJdbcOffsetBackingStore.class);

    public LoggedJdbcOffsetBackingStore() {
    }

    @Override
    protected void save() {
        logger.info("Saving data to state table");
        super.save();
        logger.info("Successfully saved data to store table");
    }

    @Override
    public Future<Void> set(Map<ByteBuffer, ByteBuffer> values, Callback<Void> callback) {
        logger.info("Set offsets");
        return super.set(values, callback);
    }

    @Override
    public Future<Map<ByteBuffer, ByteBuffer>> get(Collection<ByteBuffer> keys) {
        logger.info("Get offsets");
        return super.get(keys);
    }
}
