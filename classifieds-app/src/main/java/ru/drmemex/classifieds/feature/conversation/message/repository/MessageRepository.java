package ru.drmemex.classifieds.feature.conversation.message.repository;

import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.feature.conversation.message.entity.Message;

import java.time.OffsetDateTime;
import java.util.List;

public interface MessageRepository {

    Message save(Message message);

    List<Message> findByConversationId(
            Long conversationId,
            PageRequest pageRequest
    );

    long countByConversationId(
            Long conversationId
    );

    List<Message> searchByConversationIdAndText(
            Long conversationId,
            String text,
            PageRequest pageRequest
    );

    long countByConversationIdAndText(
            Long conversationId,
            String text
    );

    long countByAuthorIdAndCreatedAtAfter(
            Long authorId,
            OffsetDateTime createdAfter
    );
}