package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class DeletedUserCannotBeActivatedException extends ConflictException {

    public DeletedUserCannotBeActivatedException(Long id) {
        super("Deleted user with id " + id + " cannot be activated");
    }
}