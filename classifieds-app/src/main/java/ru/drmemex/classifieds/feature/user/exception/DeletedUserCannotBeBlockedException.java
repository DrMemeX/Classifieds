package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class DeletedUserCannotBeBlockedException extends ConflictException {

    public DeletedUserCannotBeBlockedException(Long id) {
        super("Deleted user cannot be blocked: " + id);
    }
}