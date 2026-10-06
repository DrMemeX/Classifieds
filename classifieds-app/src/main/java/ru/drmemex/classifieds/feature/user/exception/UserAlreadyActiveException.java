package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class UserAlreadyActiveException extends ConflictException {

    public UserAlreadyActiveException() {
        super("User is already active");
    }
}
