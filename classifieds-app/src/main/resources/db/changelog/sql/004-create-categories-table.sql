CREATE TABLE categories
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY,

    parent_id BIGINT,
    name      VARCHAR(100) NOT NULL,

    active    BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_categories
        PRIMARY KEY (id),

    CONSTRAINT fk_categories_parent
        FOREIGN KEY (parent_id)
            REFERENCES categories (id),

    CONSTRAINT uq_categories_name
        UNIQUE (name),

    CONSTRAINT chk_categories_name_length
        CHECK (char_length(name) BETWEEN 2 AND 100)
);