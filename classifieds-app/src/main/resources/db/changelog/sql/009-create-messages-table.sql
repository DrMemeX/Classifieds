CREATE TABLE messages
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY,

    conversation_id BIGINT        NOT NULL,
    author_id       BIGINT        NOT NULL,

    text            VARCHAR(2000) NOT NULL,

    created_at      TIMESTAMPTZ   NOT NULL,

    CONSTRAINT pk_messages
        PRIMARY KEY (id),

    CONSTRAINT fk_messages_conversation
        FOREIGN KEY (conversation_id)
            REFERENCES conversations (id),

    CONSTRAINT fk_messages_author
        FOREIGN KEY (author_id)
            REFERENCES users (id),

    CONSTRAINT chk_messages_text_length
        CHECK (char_length(text) BETWEEN 1 AND 2000)
);