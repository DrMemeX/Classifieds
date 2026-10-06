package ru.drmemex.classifieds.feature.advertisement.image.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AdvertisementImageOrderRequest(

        @NotNull(message = "Display order cannot be null")
        @Min(
                value = 1,
                message = "Display order must be at least 1"
        )
        @Max(
                value = 5,
                message = "Display order cannot exceed 5"
        )
        Short displayOrder
) {
}