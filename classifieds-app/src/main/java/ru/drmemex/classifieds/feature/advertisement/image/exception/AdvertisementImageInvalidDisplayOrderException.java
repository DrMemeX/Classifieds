package ru.drmemex.classifieds.feature.advertisement.image.exception;

import ru.drmemex.classifieds.common.exception.ValidationException;

public class AdvertisementImageInvalidDisplayOrderException
        extends ValidationException {

    public AdvertisementImageInvalidDisplayOrderException(Short displayOrder) {
        super("Invalid advertisement image display order: " + displayOrder);
    }
}