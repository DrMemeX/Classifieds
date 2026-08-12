package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class DeletedUserCannotBeActivatedException extends ConflictException {

    public DeletedUserCannotBeActivatedException() {
        super("Deleted user cannot be activated");
    }
}
