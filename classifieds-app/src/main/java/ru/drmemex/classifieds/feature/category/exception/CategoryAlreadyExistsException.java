package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class CategoryAlreadyExistsException extends ConflictException {

    public CategoryAlreadyExistsException() {
        super("Category already exists");
    }
}
