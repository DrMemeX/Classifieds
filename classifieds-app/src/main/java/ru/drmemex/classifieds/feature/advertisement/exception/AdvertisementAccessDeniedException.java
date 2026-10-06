package ru.drmemex.classifieds.feature.advertisement.exception;

import ru.drmemex.classifieds.common.exception.ForbiddenException;

public class AdvertisementAccessDeniedException extends ForbiddenException {

    public AdvertisementAccessDeniedException(Long advertisementId) {
        super("Access denied for advertisement with id " + advertisementId);
    }
}