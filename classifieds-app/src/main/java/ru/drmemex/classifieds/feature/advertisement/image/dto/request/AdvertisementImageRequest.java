package ru.drmemex.classifieds.feature.advertisement.image.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdvertisementImageRequest(

        @NotBlank
        String objectKey
) {
}
