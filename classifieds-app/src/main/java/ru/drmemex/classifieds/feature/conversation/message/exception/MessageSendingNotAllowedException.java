package ru.drmemex.classifieds.feature.conversation.message.exception;

import ru.drmemex.classifieds.common.exception.ValidationException;

public class MessageSendingNotAllowedException extends ValidationException {

    public MessageSendingNotAllowedException(Long conversationId) {
        super("Cannot send message in conversation: " + conversationId);
    }
}