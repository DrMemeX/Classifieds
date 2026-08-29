package ru.drmemex.classifieds.feature.advertisement.service;

import ru.drmemex.classifieds.feature.advertisement.dto.request.AdvertisementRequest;
import ru.drmemex.classifieds.feature.advertisement.dto.request.AdvertisementUpdateRequest;
import ru.drmemex.classifieds.feature.advertisement.dto.response.AdvertisementResponse;
import ru.drmemex.classifieds.feature.advertisement.filter.AdvertisementFilter;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;

import java.util.List;

public interface AdvertisementService {

    AdvertisementResponse create(
            AdvertisementRequest request
    );

    AdvertisementResponse update(
            Long advertisementId,
            AdvertisementUpdateRequest request
    );

    AdvertisementResponse getById(
            Long advertisementId
    );

    List<AdvertisementResponse> getSellerAdvertisements(
            Long sellerId,
            AdvertisementStatus status
    );

    List<AdvertisementResponse> search(
            AdvertisementFilter filter
    );

    void activate(Long advertisementId);

    void deactivate(Long advertisementId);

    void block(Long advertisementId);

    void unblock(Long advertisementId);

    void delete(Long advertisementId);

}
