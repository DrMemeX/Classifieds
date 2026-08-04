CREATE TABLE advertisements
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY,

    seller_id   BIGINT         NOT NULL,
    category_id BIGINT         NOT NULL,
    region_id   BIGINT         NOT NULL,

    locality    VARCHAR(100)   NOT NULL,

    title       VARCHAR(100)   NOT NULL,
    description VARCHAR(1500)  NOT NULL,

    price       NUMERIC(10, 2) NOT NULL,

    status      VARCHAR(20)    NOT NULL,

    created_at  TIMESTAMPTZ    NOT NULL,
    updated_at  TIMESTAMPTZ,

    CONSTRAINT pk_advertisements
        PRIMARY KEY (id),

    CONSTRAINT fk_advertisements_seller
        FOREIGN KEY (seller_id)
            REFERENCES users (id),

    CONSTRAINT fk_advertisements_category
        FOREIGN KEY (category_id)
            REFERENCES categories (id),

    CONSTRAINT fk_advertisements_region
        FOREIGN KEY (region_id)
            REFERENCES regions (id),

    CONSTRAINT chk_advertisements_title_length
        CHECK (char_length(title) BETWEEN 1 AND 100),

    CONSTRAINT chk_advertisements_description_length
        CHECK (char_length(description) BETWEEN 1 AND 1500),

    CONSTRAINT chk_advertisements_locality_length
        CHECK (char_length(locality) BETWEEN 2 AND 100),

    CONSTRAINT chk_advertisements_price
        CHECK (price >= 0),

    CONSTRAINT chk_advertisements_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'BLOCKED', 'DELETED'))
);