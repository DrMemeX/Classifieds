package ru.drmemex.classifieds.feature.advertisement.image.exception;

import ru.drmemex.classifieds.common.exception.ForbiddenException;

public class AdvertisementImageAccessDeniedException
        extends ForbiddenException {

    public AdvertisementImageAccessDeniedException(Long imageId) {
        super("Access denied to advertisement image: " + imageId);
    }
}