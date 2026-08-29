package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class ParentCategoryIsInactiveException extends ConflictException {

    public ParentCategoryIsInactiveException(Long parentId) {
        super("Parent category with id " + parentId + " is inactive");
    }
}