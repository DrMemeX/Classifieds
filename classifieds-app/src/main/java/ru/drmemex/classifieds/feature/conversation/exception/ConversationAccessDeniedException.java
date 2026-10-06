package ru.drmemex.classifieds.feature.conversation.exception;

import ru.drmemex.classifieds.common.exception.ForbiddenException;

public class ConversationAccessDeniedException extends ForbiddenException {

    public ConversationAccessDeniedException(Long conversationId) {
        super("Access denied to conversation: " + conversationId);
    }
}