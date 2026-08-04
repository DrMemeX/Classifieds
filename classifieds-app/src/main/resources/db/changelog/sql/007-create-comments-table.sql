CREATE TABLE comments
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY,

    advertisement_id BIGINT       NOT NULL,
    author_id        BIGINT       NOT NULL,

    text             VARCHAR(500) NOT NULL,

    created_at       TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_comments
        PRIMARY KEY (id),

    CONSTRAINT fk_comments_advertisement
        FOREIGN KEY (advertisement_id)
            REFERENCES advertisements (id),

    CONSTRAINT fk_comments_author
        FOREIGN KEY (author_id)
            REFERENCES users (id),

    CONSTRAINT chk_comments_text_length
        CHECK (char_length(text) BETWEEN 1 AND 500)
);