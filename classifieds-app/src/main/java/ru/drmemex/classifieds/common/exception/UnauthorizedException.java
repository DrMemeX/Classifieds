package ru.drmemex.classifieds.common.exception;

public abstract class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
