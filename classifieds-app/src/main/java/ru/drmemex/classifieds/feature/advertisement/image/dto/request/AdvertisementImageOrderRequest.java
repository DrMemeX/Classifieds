package ru.drmemex.classifieds.feature.advertisement.image.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AdvertisementImageOrderRequest(

        @NotNull
        @Min(1)
        @Max(5)
        Short displayOrder
) {
}