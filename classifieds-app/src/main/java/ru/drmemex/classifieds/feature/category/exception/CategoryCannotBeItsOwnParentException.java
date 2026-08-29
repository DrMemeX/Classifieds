package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class CategoryCannotBeItsOwnParentException extends ConflictException {

    public CategoryCannotBeItsOwnParentException(Long id) {
        super("Category with id " + id + " cannot be its own parent");
    }
}