ALTER TABLE advertisement_images
DROP CONSTRAINT uq_advertisement_images_display_order;

ALTER TABLE advertisement_images
    ADD CONSTRAINT uq_advertisement_images_display_order
        UNIQUE (advertisement_id, display_order)
    DEFERRABLE INITIALLY DEFERRED;