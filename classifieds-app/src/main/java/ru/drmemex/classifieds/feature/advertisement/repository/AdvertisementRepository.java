package ru.drmemex.classifieds.feature.advertisement.repository;

import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.filter.AdvertisementFilter;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;

import java.util.List;
import java.util.Optional;

public interface AdvertisementRepository {

    Advertisement save(Advertisement advertisement);
    Advertisement update(Advertisement advertisement);

    Optional<Advertisement> findById(Long id);

    List<Advertisement> findBySellerId(
            Long sellerId,
            AdvertisementStatus status
    );

    List<Advertisement> findByFilters(AdvertisementFilter filter);
}
