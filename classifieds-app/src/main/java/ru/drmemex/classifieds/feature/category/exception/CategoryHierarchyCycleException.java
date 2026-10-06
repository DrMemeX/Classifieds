package ru.drmemex.classifieds.feature.category.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class CategoryHierarchyCycleException extends ConflictException {

    public CategoryHierarchyCycleException(Long id) {
        super("Category hierarchy for category with id " + id
                + " contains a cycle");
    }
}