CREATE TABLE processed_kafka_events
(
    consumer_group VARCHAR(100) NOT NULL,
    event_id       UUID         NOT NULL,
    processed_at   TIMESTAMPTZ  NOT NULL,

    PRIMARY KEY (consumer_group, event_id)
);