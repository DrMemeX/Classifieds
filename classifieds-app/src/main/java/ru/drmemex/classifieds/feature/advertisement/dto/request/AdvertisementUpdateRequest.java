package ru.drmemex.classifieds.feature.advertisement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AdvertisementUpdateRequest(

        @Positive(message = "Category id must be positive")
        Long categoryId,

        @Positive(message = "Region id must be positive")
        Long regionId,

        @Pattern(
                regexp = ".*\\S.*",
                message = "Locality must not be blank"
        )
        @Size(
                max = 100,
                message = "Locality cannot exceed 100 characters"
        )
        String locality,

        @Pattern(
                regexp = ".*\\S.*",
                message = "Title must not be blank"
        )
        @Size(
                max = 100,
                message = "Title cannot exceed 100 characters"
        )
        String title,

        @Pattern(
                regexp = ".*\\S.*",
                message = "Description must not be blank"
        )
        @Size(
                max = 1500,
                message = "Description cannot exceed 1500 characters"
        )
        String description,

        @DecimalMin(
                value = "0.0",
                message = "Price cannot be negative"
        )
        BigDecimal price
) {
}