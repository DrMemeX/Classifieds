package ru.drmemex.classifieds.feature.advertisement.service;

import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
import ru.drmemex.classifieds.feature.advertisement.dto.request.AdvertisementRequest;
import ru.drmemex.classifieds.feature.advertisement.dto.request.AdvertisementUpdateRequest;
import ru.drmemex.classifieds.feature.advertisement.dto.response.AdvertisementResponse;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.filter.AdvertisementFilter;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;

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

    PageResponse<AdvertisementResponse> getSellerAdvertisements(
            Long sellerId,
            AdvertisementStatus status,
            PageRequest pageRequest
    );

    PageResponse<AdvertisementResponse> search(
            AdvertisementFilter filter,
            PageRequest pageRequest
    );

    void activate(Long advertisementId);

    void deactivate(Long advertisementId);

    void block(Long advertisementId);

    void unblock(Long advertisementId);

    void delete(Long advertisementId);

    Advertisement getAdvertisement(Long advertisementId);
}