package ru.drmemex.classifieds.feature.comment.exception;

import ru.drmemex.classifieds.common.exception.NotFoundException;

public class CommentNotFoundException extends NotFoundException {

    public CommentNotFoundException(Long commentId) {
        super("Comment not found: " + commentId);
    }
}