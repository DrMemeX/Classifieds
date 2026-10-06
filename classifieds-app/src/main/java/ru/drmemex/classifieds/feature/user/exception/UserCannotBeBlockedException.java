package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.ForbiddenException;

public class UserCannotBeBlockedException extends ForbiddenException {

    public UserCannotBeBlockedException(Long id) {
        super("User with id " + id + " cannot be blocked");
    }
}