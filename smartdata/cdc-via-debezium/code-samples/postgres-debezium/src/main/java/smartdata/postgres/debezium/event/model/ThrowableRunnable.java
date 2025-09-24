package smartdata.postgres.debezium.event.model;

@FunctionalInterface
public interface ThrowableRunnable<E extends Throwable> {
    void run() throws E;
}
