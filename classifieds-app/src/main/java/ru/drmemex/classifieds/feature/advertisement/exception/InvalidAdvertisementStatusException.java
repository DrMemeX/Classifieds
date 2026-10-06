package ru.drmemex.classifieds.feature.advertisement.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;

public class InvalidAdvertisementStatusException extends ConflictException {

    public InvalidAdvertisementStatusException(
    ) {
        super(
                "Invalid status transition for advertisement"
        );
    }
}