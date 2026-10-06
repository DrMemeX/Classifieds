package ru.drmemex.classifieds.common.exception;

public abstract class ForbiddenException extends BusinessException {

    public ForbiddenException(String message) {
        super(message);
    }
}
