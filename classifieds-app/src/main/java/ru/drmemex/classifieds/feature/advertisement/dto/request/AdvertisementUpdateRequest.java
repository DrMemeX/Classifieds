package ru.drmemex.classifieds.feature.advertisement.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AdvertisementUpdateRequest(

        Long categoryId,

        Long regionId,

        @Size(max = 100)
        String locality,

        @Size(max = 100)
        String title,

        @Size(max = 1500)
        String description,

        @DecimalMin(value = "0.0")
        BigDecimal price
) {
}