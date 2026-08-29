package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class CategoryAlreadyActiveException extends ConflictException {

    public CategoryAlreadyActiveException(Long id) {
        super("Category with id " + id + " is already active");
    }
}