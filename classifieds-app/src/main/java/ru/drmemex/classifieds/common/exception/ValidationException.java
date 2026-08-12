package ru.drmemex.classifieds.common.exception;

public abstract class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(message);
    }
}
