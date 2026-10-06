package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.ForbiddenException;

public class UserCannotBeUnblockedException extends ForbiddenException {

    public UserCannotBeUnblockedException(Long id) {
        super("User with id " + id + " cannot be unblocked");
    }
}