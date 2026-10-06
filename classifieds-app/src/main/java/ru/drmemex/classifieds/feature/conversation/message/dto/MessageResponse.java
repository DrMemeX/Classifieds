package ru.drmemex.classifieds.feature.conversation.message.dto;

import java.time.OffsetDateTime;

public record MessageResponse(

        Long id,
        Long conversationId,
        Long authorId,
        String text,
        OffsetDateTime createdAt
) {
}
