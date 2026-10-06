package ru.drmemex.classifieds.common.exception;

public abstract class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(message);
    }
}
