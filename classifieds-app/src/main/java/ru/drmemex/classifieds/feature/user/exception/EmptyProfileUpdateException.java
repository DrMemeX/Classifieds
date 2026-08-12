package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.ValidationException;

public class EmptyProfileUpdateException extends ValidationException {

    public EmptyProfileUpdateException() {
        super("At least one profile field must be provided");
    }
}
