package ru.drmemex.classifieds.feature.comment.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class CommentRateLimitExceededException extends ConflictException {

    public CommentRateLimitExceededException() {
        super("Comment rate limit exceeded");
    }
}
