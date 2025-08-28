--liquibase formatted sql
--changeset setup:1
CREATE TABLE data
(
    id    BIGSERIAL PRIMARY KEY,
    value TEXT
);

CREATE OR REPLACE FUNCTION notify_table_change() RETURNS TRIGGER AS
'
DECLARE
    payload TEXT;
BEGIN
    IF tg_op = ''INSERT'' THEN
        payload := JSON_BUILD_OBJECT(''op'', ''INSERT'', ''id'', new.id, ''value'', new.value)::TEXT;
    ELSIF tg_op = ''UPDATE'' THEN
        payload := JSON_BUILD_OBJECT(''op'', ''UPDATE'', ''id'', new.id, ''value'', new.value)::TEXT;
    ELSIF tg_op = ''DELETE'' THEN
        payload := JSON_BUILD_OBJECT(''op'', ''DELETE'', ''id'', old.id, ''value'', old.value)::TEXT;
    END IF;
    PERFORM pg_notify(''my_channel'', payload);
    RETURN NULL;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER data_notify_trigger
    AFTER INSERT OR UPDATE OR DELETE
    ON data
    FOR EACH ROW
EXECUTE FUNCTION notify_table_change();
