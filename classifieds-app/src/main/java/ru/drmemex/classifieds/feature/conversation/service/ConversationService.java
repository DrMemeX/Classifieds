package ru.drmemex.classifieds.feature.conversation.service;

import ru.drmemex.classifieds.feature.conversation.dto.ConversationResponse;
import ru.drmemex.classifieds.feature.conversation.entity.Conversation;
import ru.drmemex.classifieds.feature.user.entity.User;

import java.util.List;

public interface ConversationService {

    ConversationResponse create(Long advertisementId);

    ConversationResponse getById(Long conversationId);

    List<ConversationResponse> getMyConversations();

    Conversation getAccessibleConversation(
            Long conversationId,
            User currentUser
    );
}
