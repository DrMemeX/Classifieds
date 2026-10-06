package ru.drmemex.classifieds.feature.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(

        @NotBlank(message = "Comment text must not be blank")
        @Size(max = 500, message = "Comment text must not exceed 500 characters")
        String text
) {
}