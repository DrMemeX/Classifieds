package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(Long id) {
        super("Category with id " + id + " not found");
    }

    public CategoryNotFoundException(String name) {
        super("Category with name '" + name + "' not found");
    }
}