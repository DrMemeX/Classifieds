package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class SystemCategoryModificationException extends ConflictException {

    public SystemCategoryModificationException() {
        super("System category cannot be modified");
    }
}
