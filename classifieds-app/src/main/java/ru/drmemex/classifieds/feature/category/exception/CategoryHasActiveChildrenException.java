package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class CategoryHasActiveChildrenException extends ConflictException {

    public CategoryHasActiveChildrenException() {
        super("Category cannot be deactivated while it has active children");
    }
}
