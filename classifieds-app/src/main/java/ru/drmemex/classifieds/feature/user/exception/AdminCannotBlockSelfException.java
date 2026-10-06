package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class AdminCannotBlockSelfException extends ConflictException {

    public AdminCannotBlockSelfException() {
        super("Admin cannot block himself");
    }
}
