package ru.drmemex.classifieds.feature.conversation.service;

import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
import ru.drmemex.classifieds.feature.conversation.dto.ConversationResponse;
import ru.drmemex.classifieds.feature.conversation.entity.Conversation;
import ru.drmemex.classifieds.feature.user.entity.User;

public interface ConversationService {

    ConversationResponse create(Long advertisementId);

    ConversationResponse getById(Long conversationId);

    PageResponse<ConversationResponse> getMyConversations(
            PageRequest pageRequest
    );

    Conversation getAccessibleConversation(
            Long conversationId,
            User currentUser
    );
}
