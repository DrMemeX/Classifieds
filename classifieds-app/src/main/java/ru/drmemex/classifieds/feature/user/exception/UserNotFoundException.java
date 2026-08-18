package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException() {
        super("User not found");
    }
}
