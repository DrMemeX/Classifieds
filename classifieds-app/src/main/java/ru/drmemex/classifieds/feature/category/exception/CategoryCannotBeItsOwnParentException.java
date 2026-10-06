package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class CategoryCannotBeItsOwnParentException extends ConflictException {

    public CategoryCannotBeItsOwnParentException() {
        super("Category cannot be its own parent");
    }
}
