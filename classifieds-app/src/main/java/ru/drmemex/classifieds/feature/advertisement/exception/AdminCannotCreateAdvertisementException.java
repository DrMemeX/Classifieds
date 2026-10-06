package ru.drmemex.classifieds.feature.advertisement.exception;

import ru.drmemex.classifieds.common.exception.ForbiddenException;

public class AdminCannotCreateAdvertisementException extends ForbiddenException {

    public AdminCannotCreateAdvertisementException() {
        super("Administrator cannot create advertisements");
    }
}