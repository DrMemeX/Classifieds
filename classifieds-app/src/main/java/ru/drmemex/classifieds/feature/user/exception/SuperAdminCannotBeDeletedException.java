package ru.drmemex.classifieds.feature.user.exception;

import ru.drmemex.classifieds.common.exception.ForbiddenException;

public class SuperAdminCannotBeDeletedException extends ForbiddenException {

    public SuperAdminCannotBeDeletedException() {
        super("Super admin cannot be deleted");
    }
}