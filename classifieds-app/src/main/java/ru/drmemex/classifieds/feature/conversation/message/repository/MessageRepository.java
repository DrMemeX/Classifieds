package ru.drmemex.classifieds.feature.conversation.message.repository;

import ru.drmemex.classifieds.feature.conversation.message.entity.Message;

import java.util.List;

public interface MessageRepository {

    Message save(Message message);

    List<Message> findByConversationId(Long conversationId);

    List<Message> searchByConversationIdAndText(
            Long conversationId,
            String text
    );
}