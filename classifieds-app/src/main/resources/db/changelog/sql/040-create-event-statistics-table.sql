CREATE TABLE event_statistics
(
    id             BIGSERIAL    NOT NULL,
    event_date     DATE         NOT NULL,
    aggregate_type VARCHAR(50)  NOT NULL,
    event_type     VARCHAR(50)  NOT NULL,
    event_count    BIGINT       NOT NULL,
    updated_at     TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_event_statistics
        PRIMARY KEY (id),

    CONSTRAINT uq_event_statistics_date_aggregate_event
        UNIQUE (event_date, aggregate_type, event_type)
);