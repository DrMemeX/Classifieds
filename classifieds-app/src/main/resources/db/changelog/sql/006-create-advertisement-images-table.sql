CREATE TABLE advertisement_images
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY,

    advertisement_id BIGINT       NOT NULL,

    object_key       VARCHAR(255) NOT NULL,

    display_order    SMALLINT     NOT NULL,

    CONSTRAINT pk_advertisement_images
        PRIMARY KEY (id),

    CONSTRAINT fk_advertisement_images_advertisement
        FOREIGN KEY (advertisement_id)
            REFERENCES advertisements (id),

    CONSTRAINT uq_advertisement_images_display_order
        UNIQUE (advertisement_id, display_order),

    CONSTRAINT chk_advertisement_images_display_order
        CHECK (display_order BETWEEN 1 AND 5)
);