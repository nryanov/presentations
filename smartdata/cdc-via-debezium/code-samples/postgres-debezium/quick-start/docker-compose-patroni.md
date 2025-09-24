# Postgres with Patroni

## Setup
1. clone https://github.com/patroni/patroni
2. `cd ./patroni`
3. Build single image with patroni + etcd + haproxy + postgres
    1. docker build --build-arg PG_MAJOR=15 -t patroni-15 .
    2. docker build --build-arg PG_MAJOR=16 -t patroni-16 .
    3. docker build --build-arg PG_MAJOR=17 -t patroni-17 .
4. Start
    1. docker-compose -f docker-compose-patroni-15.yml up -d
    2. docker-compose -f docker-compose-patroni-16.yml up -d
    3. docker-compose -f docker-compose-patroni-17.yml up -d


## Queries
```sql
CREATE TABLE data
(
   id    BIGSERIAL PRIMARY KEY,
   field1  TEXT,
   field2  TEXT,
   field3  TEXT
);

SELECT pg_is_in_recovery();

SELECT pg_create_logical_replication_slot('debezium_slot', 'pgoutput');
CREATE PUBLICATION debezium_publication FOR TABLE public.data WITH (PUBLISH = 'insert, update, delete, truncate');


INSERT INTO data (field1, field2, field3) VALUES ('new', 'new', 'new');
```