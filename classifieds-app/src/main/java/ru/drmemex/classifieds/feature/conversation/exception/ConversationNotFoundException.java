package ru.drmemex.classifieds.feature.conversation.exception;

import ru.drmemex.classifieds.common.exception.NotFoundException;

public class ConversationNotFoundException extends NotFoundException {

    public ConversationNotFoundException(Long conversationId) {
        super("Conversation not found: " + conversationId);
    }
}