package ru.drmemex.classifieds.feature.advertisement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AdvertisementRequest(

        @NotNull(message = "Category id cannot be null")
        @Positive(message = "Category id must be positive")
        Long categoryId,

        @NotNull(message = "Region id cannot be null")
        @Positive(message = "Region id must be positive")
        Long regionId,

        @NotBlank(message = "Locality cannot be blank")
        @Size(
                max = 100,
                message = "Locality cannot exceed 100 characters"
        )
        String locality,

        @NotBlank(message = "Title cannot be blank")
        @Size(
                max = 100,
                message = "Title cannot exceed 100 characters"
        )
        String title,

        @NotBlank(message = "Description cannot be blank")
        @Size(
                max = 1500,
                message = "Description cannot exceed 1500 characters"
        )
        String description,

        @NotNull(message = "Price cannot be null")
        @DecimalMin(
                value = "0.0",
                message = "Price cannot be negative"
        )
        BigDecimal price
) {
}