package ru.drmemex.classifieds.feature.advertisement.image.exception;

import ru.drmemex.classifieds.common.exception.ValidationException;

public class AdvertisementImageLimitExceededException
        extends ValidationException {

    public AdvertisementImageLimitExceededException() {
        super("Advertisement cannot contain more than 5 images");
    }
}