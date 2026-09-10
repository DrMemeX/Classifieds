package ru.drmemex.classifieds.feature.comment.dto;

import java.time.OffsetDateTime;

public record CommentResponse(

        Long id,
        Long advertisementId,
        Long authorId,
        String text,
        OffsetDateTime createdAt
) {
}