package ru.drmemex.classifieds.feature.advertisement.image.service;

import ru.drmemex.classifieds.feature.advertisement.image.dto.request.AdvertisementImageRequest;
import ru.drmemex.classifieds.feature.advertisement.image.dto.response.AdvertisementImageResponse;

import java.util.List;

public interface AdvertisementImageService {

    AdvertisementImageResponse create(
            Long advertisementId,
            AdvertisementImageRequest request
    );

    AdvertisementImageResponse reorder(
            Long advertisementId,
            Long imageId,
            Short displayOrder
    );

    List<AdvertisementImageResponse> getByAdvertisementId(
            Long advertisementId
    );

    List<AdvertisementImageResponse> getByAdvertisementIdForAdmin(
            Long advertisementId
    );

    void delete(
            Long advertisementId,
            Long imageId
    );
}
