package ru.drmemex.classifieds.feature.advertisement.image.repository;

import ru.drmemex.classifieds.feature.advertisement.image.entity.AdvertisementImage;

import java.util.List;
import java.util.Optional;

public interface AdvertisementImageRepository {

    AdvertisementImage save(AdvertisementImage image);

    Optional<AdvertisementImage> findById(Long id);

    List<AdvertisementImage> findByAdvertisementId(Long advertisementId);

    long countByAdvertisementId(Long advertisementId);

    void delete(AdvertisementImage image);
}
