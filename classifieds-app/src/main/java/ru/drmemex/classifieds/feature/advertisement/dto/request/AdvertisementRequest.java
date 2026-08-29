package ru.drmemex.classifieds.feature.advertisement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AdvertisementRequest(

        @NotNull
        @Positive
        Long categoryId,

        @NotNull
        @Positive
        Long regionId,

        @NotBlank
        @Size(max = 100)
        String locality,

        @NotBlank
        @Size(max = 100)
        String title,

        @NotBlank
        @Size(max = 1500)
        String description,

        @NotNull
        @DecimalMin(value = "0.0")
        BigDecimal price
) {
}