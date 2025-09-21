package smartdata.postgres.debezium.event.repository;

public interface DataRepository {
    void save(String location, String path, byte[] data);
}
