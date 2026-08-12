package ru.drmemex.classifieds.common.exception;

public abstract class AccessDeniedException extends BusinessException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
