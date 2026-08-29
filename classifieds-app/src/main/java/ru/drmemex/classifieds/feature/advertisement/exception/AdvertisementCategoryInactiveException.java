package ru.drmemex.classifieds.feature.advertisement.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class AdvertisementCategoryInactiveException extends ConflictException {

    public AdvertisementCategoryInactiveException(Long advertisementId) {
        super(
                "Advertisement with id "
                        + advertisementId
                        + " cannot be activated because its category is inactive"
        );
    }
}