# postgres-debezium
## 1. Environments
### 1.1. Postgres single node
```shell
make jar
docker-compose -f ./environment/docker/single-node/docker-compose.yml up

psql -h localhost -p 5432 -U postgres -d postgres -c SELECT PG_CREATE_LOGICAL_REPLICATION_SLOT('debezium_slot', 'pgoutput');
psql -h localhost -p 5432 -U postgres -d postgres -c CREATE PUBLICATION debezium_publication WITH (publish_via_partition_root= TRUE);
psql -h localhost -p 5432 -U postgres -d postgres -c ALTER PUBLICATION debezium_publication ADD TABLE public.data;

java -Dquarkus.config.locations=./environment/debezium/console-sink.properties -jar ./build/quarkus-app/quarkus-run.jar
# or fom idea using `.idea/runConfigurations`.
```

### 1.2. Postgres shards node
```shell
make jar
docker-compose -f ./environment/docker/shards/docker-compose.yml up

psql -h localhost -p 5432 -U postgres -d postgres -c SELECT PG_CREATE_LOGICAL_REPLICATION_SLOT('debezium_slot', 'pgoutput');
psql -h localhost -p 5432 -U postgres -d postgres -c CREATE PUBLICATION debezium_publication WITH (publish_via_partition_root= TRUE);
psql -h localhost -p 5432 -U postgres -d postgres -c ALTER PUBLICATION debezium_publication ADD TABLE public.data;

psql -h localhost -p 5433 -U postgres -d postgres -c SELECT PG_CREATE_LOGICAL_REPLICATION_SLOT('debezium_slot', 'pgoutput');
psql -h localhost -p 5433 -U postgres -d postgres -c CREATE PUBLICATION debezium_publication WITH (publish_via_partition_root= TRUE);
psql -h localhost -p 5433 -U postgres -d postgres -c ALTER PUBLICATION debezium_publication ADD TABLE public.data;

java -Dquarkus.config.locations=./environment/debezium/console-sink.properties -jar ./build/quarkus-app/quarkus-run.jar
# or fom idea using `.idea/runConfigurations`.
```

### 1.3. Patroni cluster
1. clone https://github.com/patroni/patroni
2. `cd ./patroni`
3. Build single image with patroni + etcd + haproxy + postgres: `docker build --build-arg PG_MAJOR=17 -t patroni-17 .`

```shell
make jar
docker-compose -f ./environment/docker/patroni/docker-compose-patroni-17.yml up

psql -h localhost -p 5000 -U postgres -d postgres -c SELECT PG_CREATE_LOGICAL_REPLICATION_SLOT('debezium_slot', 'pgoutput');
psql -h localhost -p 5000 -U postgres -d postgres -c CREATE PUBLICATION debezium_publication WITH (publish_via_partition_root= TRUE);
psql -h localhost -p 5000 -U postgres -d postgres -c ALTER PUBLICATION debezium_publication ADD TABLE public.data;

java -Dquarkus.config.locations=./environment/debezium/console-sink.properties -jar ./build/quarkus-app/quarkus-run.jar
# or fom idea using `.idea/runConfigurations`.
```

### 1.4. TimescaleDB (single node)
```shell
make jar
docker-compose -f ./environment/docker/timescaledb/docker-compose.yml up

psql -h localhost -p 5432 -U postgres -d postgres -c SELECT PG_CREATE_LOGICAL_REPLICATION_SLOT('debezium_slot', 'pgoutput');
psql -h localhost -p 5432 -U postgres -d postgres -c CREATE PUBLICATION debezium_publication FOR TABLES IN SCHEMA _timescaledb_internal;

java -Dquarkus.config.locations=./environment/debezium/console-sink.properties -jar ./build/quarkus-app/quarkus-run.jar
# or fom idea using `.idea/runConfigurations`.
```

### 1.5. Zalando (minikube)
[minikube](./minikube.md)

## 2. Config example
- [console sink](./environment/debezium/console-sink.properties)
- [s3 sink](./environment/debezium/s3-sink.properties)

### 2.1. Additional configuration
- debezium.event-format=[CONFLUENT_AVRO | AVRO | JSON]
- event-saver.type=[CONSOLE | S3]

## 3. Ad-hoc snapshot signals examples
```sql
INSERT INTO signals(id, type, data)
VALUES (
        gen_random_uuid(),
        'execute-snapshot',
        '{"type":"INCREMENTAL", "data-collections": ["public.data"] }'
);


INSERT INTO signals(id, type, data)
VALUES (
        gen_random_uuid(),
        'execute-snapshot',
        '{"type": "BLOCKING",' ||
        ' "data-collections": ["public.data"], ' ||
        '"additional-conditions": ' ||
            '[{"data-collection": "public.data", "filter": "SELECT * FROM public.data WHERE id IN (...)"}]}'
);
```
