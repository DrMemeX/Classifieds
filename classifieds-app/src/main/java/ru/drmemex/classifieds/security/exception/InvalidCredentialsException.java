package ru.drmemex.classifieds.security.exception;

import ru.drmemex.classifieds.common.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException {

    public InvalidCredentialsException() {
        super("Invalid login or password");
    }
}