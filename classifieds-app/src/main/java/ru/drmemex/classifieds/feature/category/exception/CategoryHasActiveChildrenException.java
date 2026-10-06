package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class CategoryHasActiveChildrenException extends ConflictException {

    public CategoryHasActiveChildrenException(Long id) {
        super("Category with id " + id
                + " cannot be deactivated while it has active children");
    }
}