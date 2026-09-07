package ru.drmemex.classifieds.feature.conversation.exception;

import ru.drmemex.classifieds.common.exception.ValidationException;

public class ConversationWithOwnAdvertisementException
        extends ValidationException {

    public ConversationWithOwnAdvertisementException(Long advertisementId) {
        super("Cannot create conversation for own advertisement: " + advertisementId);
    }
}