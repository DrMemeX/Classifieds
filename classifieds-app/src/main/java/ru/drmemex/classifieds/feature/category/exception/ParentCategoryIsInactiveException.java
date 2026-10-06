package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class ParentCategoryIsInactiveException extends ConflictException {

    public ParentCategoryIsInactiveException() {
        super("Category cannot be activated while its parent is inactive");
    }
}
