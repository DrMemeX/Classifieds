package ru.drmemex.classifieds.feature.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 100)
        String parentName
) {
}
