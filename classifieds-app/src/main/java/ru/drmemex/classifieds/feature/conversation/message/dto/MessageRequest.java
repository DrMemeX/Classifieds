package ru.drmemex.classifieds.feature.conversation.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageRequest(

        @NotBlank(message = "Message text must not be blank")
        @Size(max = 2000, message = "Message text must not exceed 2000 characters")
        String text
) {
}
