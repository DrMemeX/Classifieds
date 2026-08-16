package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException() {
        super("Category not found");
    }
}
