package smartdata.postgres.debezium.repository;

public interface DataRepository {
    void save(String location, String path, byte[] data);
}
