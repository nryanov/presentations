package smartdata.postgres.debezium.event.model;

import io.debezium.engine.DebeziumEngine;

public record EventCommitter(DebeziumEngine.RecordCommitter<?> delegate, Runnable commitLastRecord) {
    public void commitBatch() {
        try {
            delegate.markBatchFinished();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void commitRecord() {
        commitLastRecord.run();
    }
}
