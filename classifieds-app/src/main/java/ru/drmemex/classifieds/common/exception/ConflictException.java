package ru.drmemex.classifieds.common.exception;

public abstract class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(message);
    }
}
