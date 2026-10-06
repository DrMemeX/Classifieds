package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class UserAlreadyBlockedException extends ConflictException {

    public UserAlreadyBlockedException() {
        super("User is already blocked");
    }
}
