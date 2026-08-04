CREATE TABLE regions
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY,

    name VARCHAR(100) NOT NULL,

    CONSTRAINT pk_regions
        PRIMARY KEY (id),

    CONSTRAINT uq_regions_name
        UNIQUE (name),

    CONSTRAINT chk_regions_name_length
        CHECK (char_length(name) BETWEEN 2 AND 100)
);