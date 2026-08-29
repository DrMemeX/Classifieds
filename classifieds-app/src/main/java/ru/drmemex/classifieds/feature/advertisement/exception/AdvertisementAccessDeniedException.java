package ru.drmemex.classifieds.feature.advertisement.exception;

import ru.drmemex.classifieds.common.exception.UnauthorizedException;

public class AdvertisementAccessDeniedException extends UnauthorizedException {

    public AdvertisementAccessDeniedException(Long advertisementId) {
        super("Access denied for advertisement with id " + advertisementId);
    }
}