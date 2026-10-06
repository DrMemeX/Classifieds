package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class LoginAlreadyExistsException extends ConflictException {

    public LoginAlreadyExistsException() {
        super("Login already exists");
    }
}
