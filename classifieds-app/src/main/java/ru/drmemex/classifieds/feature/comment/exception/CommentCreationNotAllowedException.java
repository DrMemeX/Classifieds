package ru.drmemex.classifieds.feature.comment.exception;

import ru.drmemex.classifieds.common.exception.ValidationException;

public class CommentCreationNotAllowedException extends ValidationException {

    public CommentCreationNotAllowedException(Long advertisementId) {
        super("Cannot create comment for advertisement: " + advertisementId);
    }
}