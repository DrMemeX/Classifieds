package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class PhoneAlreadyExistsException extends ConflictException {

    public PhoneAlreadyExistsException() {
        super("Phone already exists");
    }
}
