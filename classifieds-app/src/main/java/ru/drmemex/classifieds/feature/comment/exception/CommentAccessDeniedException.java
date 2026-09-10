package ru.drmemex.classifieds.feature.comment.exception;

import ru.drmemex.classifieds.common.exception.ForbiddenException;

public class CommentAccessDeniedException extends ForbiddenException {

    public CommentAccessDeniedException(Long commentId) {
        super("Access denied to comment: " + commentId);
    }
}