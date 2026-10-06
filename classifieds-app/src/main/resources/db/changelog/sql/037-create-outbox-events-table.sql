CREATE TABLE outbox_events
(
    id             UUID         NOT NULL,
    aggregate_type VARCHAR(50)  NOT NULL,
    aggregate_id   BIGINT       NOT NULL,
    event_type     VARCHAR(50)  NOT NULL,
    topic          VARCHAR(200) NOT NULL,
    event_key      VARCHAR(100) NOT NULL,
    payload        TEXT         NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL,
    published_at   TIMESTAMPTZ,

    CONSTRAINT pk_outbox_events
        PRIMARY KEY (id)
);