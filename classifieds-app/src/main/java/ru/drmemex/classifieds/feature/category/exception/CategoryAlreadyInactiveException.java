package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class CategoryAlreadyInactiveException extends ConflictException {

    public CategoryAlreadyInactiveException() {
        super("Category already inactive");
    }
}
