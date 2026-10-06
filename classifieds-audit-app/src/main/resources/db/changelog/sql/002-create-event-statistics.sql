CREATE TABLE event_statistics
(
    id             BIGSERIAL    PRIMARY KEY,
    event_date     DATE         NOT NULL,
    aggregate_type VARCHAR(50)  NOT NULL,
    event_type     VARCHAR(50)  NOT NULL,
    event_count    BIGINT       NOT NULL,
    updated_at     TIMESTAMPTZ  NOT NULL,

    CONSTRAINT uq_event_statistics
        UNIQUE (event_date, aggregate_type, event_type)
);