package ru.drmemex.classifieds.feature.conversation.repository;

import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.feature.conversation.entity.Conversation;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository {

    Conversation save(Conversation conversation);

    Optional<Conversation> findById(Long id);

    Optional<Conversation> findByAdvertisementIdAndBuyerId(
            Long advertisementId,
            Long buyerId
    );

    List<Conversation> findByParticipantId(
            Long userId,
            PageRequest pageRequest
    );

    long countByParticipantId(
            Long userId
    );
}