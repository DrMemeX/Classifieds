package ru.drmemex.classifieds.feature.advertisement.image.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdvertisementImageRequest(

        @NotBlank(message = "Object key cannot be blank")
        String objectKey
) {
}