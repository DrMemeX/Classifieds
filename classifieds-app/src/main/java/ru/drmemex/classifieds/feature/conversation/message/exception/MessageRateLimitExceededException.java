package ru.drmemex.classifieds.feature.conversation.message.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class MessageRateLimitExceededException extends ConflictException {

    public MessageRateLimitExceededException() {
        super("Message rate limit exceeded");
    }
}
