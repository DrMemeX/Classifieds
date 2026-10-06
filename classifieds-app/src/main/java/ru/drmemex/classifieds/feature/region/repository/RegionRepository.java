package ru.drmemex.classifieds.feature.region.repository;

import ru.drmemex.classifieds.feature.region.entity.Region;

import java.util.List;
import java.util.Optional;

public interface RegionRepository {

    List<Region> findAll();

    Optional<Region> findById(Long id);

    List<Region> findByName(String name);
}
