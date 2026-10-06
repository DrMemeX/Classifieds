package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found");
    }
}