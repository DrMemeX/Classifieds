package ru.drmemex.classifieds.feature.conversation.dto;

import java.time.OffsetDateTime;

public record ConversationResponse(

        Long id,
        Long advertisementId,
        Long buyerId,
        Long sellerId,
        OffsetDateTime createdAt
) {
}