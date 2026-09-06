package ru.drmemex.classifieds.feature.advertisement.image.exception;

import ru.drmemex.classifieds.common.exception.NotFoundException;

public class AdvertisementImageNotFoundException
        extends NotFoundException {

    public AdvertisementImageNotFoundException(Long imageId) {
        super("Advertisement image not found: " + imageId);
    }
}