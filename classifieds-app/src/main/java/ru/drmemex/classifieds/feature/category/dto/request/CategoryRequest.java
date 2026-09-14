package ru.drmemex.classifieds.feature.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank(message = "Category name cannot be blank")
        @Size(
                max = 100,
                message = "Category name cannot exceed 100 characters"
        )
        String name,

        @Pattern(
                regexp = ".*\\S.*",
                message = "Parent category name must not be blank"
        )
        @Size(
                max = 100,
                message = "Parent category name cannot exceed 100 characters"
        )
        String parentName
) {
}