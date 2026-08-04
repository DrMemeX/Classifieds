CREATE TABLE conversations
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY,

    advertisement_id BIGINT      NOT NULL,
    buyer_id         BIGINT      NOT NULL,

    created_at       TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_conversations
        PRIMARY KEY (id),

    CONSTRAINT fk_conversations_advertisement
        FOREIGN KEY (advertisement_id)
            REFERENCES advertisements (id),

    CONSTRAINT fk_conversations_buyer
        FOREIGN KEY (buyer_id)
            REFERENCES users (id),

    CONSTRAINT uq_conversations_advertisement_buyer
        UNIQUE (advertisement_id, buyer_id)
);