--liquibase formatted sql
--changeset setup:1

CREATE TABLE table_one
(
    id    UUID PRIMARY KEY,
    value TEXT
);

CREATE TABLE table_two
(
    id      BIGSERIAL PRIMARY KEY,
    field1  TEXT,
    field2  TEXT,
    field3  TEXT
);

CREATE TABLE heartbeat
(
    single_row  bool PRIMARY KEY DEFAULT TRUE,
    "timestamp" TIMESTAMP NOT NULL,
    CONSTRAINT single_row_check CHECK (single_row)
);

CREATE TABLE signals
(
    id   TEXT NOT NULL PRIMARY KEY,
    type TEXT NOT NULL,
    data TEXT
);
