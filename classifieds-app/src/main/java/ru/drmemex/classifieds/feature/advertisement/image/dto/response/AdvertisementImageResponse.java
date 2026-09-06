package ru.drmemex.classifieds.feature.advertisement.image.dto.response;

public record AdvertisementImageResponse(

        Long id,
        Long advertisementId,
        String objectKey,
        Short displayOrder
) {
}
