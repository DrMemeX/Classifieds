CREATE TABLE event_history
(
    event_id       UUID         PRIMARY KEY,
    aggregate_type VARCHAR(50)  NOT NULL,
    aggregate_id   BIGINT       NOT NULL,
    event_type     VARCHAR(50)  NOT NULL,
    topic          VARCHAR(200) NOT NULL,
    event_key      VARCHAR(100) NOT NULL,
    payload        TEXT         NOT NULL,
    occurred_at    TIMESTAMPTZ  NOT NULL,
    consumed_at    TIMESTAMPTZ  NOT NULL
);