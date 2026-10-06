package ru.drmemex.classifieds.feature.conversation.message.service;

import ru.drmemex.classifieds.feature.conversation.message.dto.MessageRequest;
import ru.drmemex.classifieds.feature.conversation.message.dto.MessageResponse;

import java.util.List;

public interface MessageService {

    MessageResponse send(
            Long conversationId,
            MessageRequest request
    );

    List<MessageResponse> getByConversationId(
            Long conversationId
    );

    List<MessageResponse> searchByConversationAndText(
            Long conversationId,
            String text
    );
}
