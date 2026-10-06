package ru.drmemex.classifieds.feature.conversation.message.service;

import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
import ru.drmemex.classifieds.feature.conversation.message.dto.MessageRequest;
import ru.drmemex.classifieds.feature.conversation.message.dto.MessageResponse;

public interface MessageService {

    MessageResponse send(
            Long conversationId,
            MessageRequest request
    );

    PageResponse<MessageResponse> getByConversationId(
            Long conversationId,
            PageRequest pageRequest
    );

    PageResponse<MessageResponse> searchByConversationAndText(
            Long conversationId,
            String text,
            PageRequest pageRequest
    );
}
