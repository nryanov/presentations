package smartdata.postgres.debezium.event.model;

public record EventCommitter(
        ThrowableRunnable<Exception> commitBatch,
        ThrowableRunnable<Exception> commitLastRecord
) {
    public void commit() {
        try {
            commitLastRecord.run();
            commitBatch.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
