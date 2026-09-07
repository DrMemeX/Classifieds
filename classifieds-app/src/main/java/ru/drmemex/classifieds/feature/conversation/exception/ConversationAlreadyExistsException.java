package ru.drmemex.classifieds.feature.conversation.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class ConversationAlreadyExistsException extends ConflictException {

    public ConversationAlreadyExistsException(Long advertisementId) {
        super("Conversation already exists for advertisement: " + advertisementId);
    }
}