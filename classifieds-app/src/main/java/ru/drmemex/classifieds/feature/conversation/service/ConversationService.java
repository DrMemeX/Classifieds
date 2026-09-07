package ru.drmemex.classifieds.feature.conversation.service;

import ru.drmemex.classifieds.feature.conversation.dto.ConversationResponse;

import java.util.List;

public interface ConversationService {

    ConversationResponse create(Long advertisementId);

    ConversationResponse getById(Long conversationId);

    List<ConversationResponse> getMyConversations();
}
