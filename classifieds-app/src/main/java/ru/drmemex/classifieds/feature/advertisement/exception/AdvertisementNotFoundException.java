package ru.drmemex.classifieds.feature.advertisement.exception;

import ru.drmemex.classifieds.common.exception.NotFoundException;

public class AdvertisementNotFoundException extends NotFoundException {

    public AdvertisementNotFoundException(Long advertisementId) {
        super("Advertisement with id " + advertisementId + " not found");
    }
}