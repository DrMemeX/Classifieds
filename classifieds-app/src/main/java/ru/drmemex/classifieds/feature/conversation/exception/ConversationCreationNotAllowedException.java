package ru.drmemex.classifieds.feature.conversation.exception;

import ru.drmemex.classifieds.common.exception.ValidationException;

public class ConversationCreationNotAllowedException
        extends ValidationException {

    public ConversationCreationNotAllowedException(Long advertisementId) {
        super("Cannot create conversation for inactive advertisement: "
                + advertisementId);
    }
}