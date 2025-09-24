--liquibase formatted sql
--changeset setup:1

CREATE TABLE offsets
(
    id                TEXT PRIMARY KEY,
    offset_key        TEXT,
    offset_val        TEXT,
    record_insert_ts  TIMESTAMP NOT NULL,
    record_insert_seq INTEGER   NOT NULL
);

CREATE TABLE if not exists heartbeat
(
    single_row  bool PRIMARY KEY DEFAULT TRUE,
    last_update TIMESTAMP NOT NULL,
    CONSTRAINT single_row_check CHECK (single_row)
);

CREATE TABLE signals
(
    id   TEXT NOT NULL PRIMARY KEY,
    type TEXT NOT NULL,
    data TEXT
);

CREATE TABLE generations
(
    full_table_name TEXT   NOT NULL PRIMARY KEY,
    generation_id   BIGINT NOT NULL
);

-- user tables
CREATE TABLE data
(
    id    BIGSERIAL PRIMARY KEY,
    field1 TEXT,
    field2 TEXT,
    field3 TEXT
);
