package ru.drmemex.classifieds.feature.advertisement.dto.response;

import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AdvertisementResponse(

        Long id,
        Long sellerId,
        Long categoryId,
        Long regionId,
        String locality,
        String title,
        String description,
        BigDecimal price,
        AdvertisementStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
