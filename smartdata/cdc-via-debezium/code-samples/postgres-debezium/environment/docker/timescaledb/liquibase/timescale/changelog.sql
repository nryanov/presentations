--liquibase formatted sql
--changeset setup:1

CREATE TABLE values
(
    time        timestamptz      NOT NULL,
    location    TEXT             NOT NULL,
    temperature DOUBLE PRECISION NULL,
    humidity    DOUBLE PRECISION NULL
)
    WITH (
        timescaledb.hypertable,
        timescaledb.partition_column = 'time',
        timescaledb.segmentby = 'location'
        );