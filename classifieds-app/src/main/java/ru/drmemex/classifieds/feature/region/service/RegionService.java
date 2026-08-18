package ru.drmemex.classifieds.feature.region.service;

import ru.drmemex.classifieds.feature.region.dto.RegionResponse;

import java.util.List;
import java.util.Optional;

public interface RegionService {

    List<RegionResponse> getAll();

    RegionResponse getById(Long id);

    List<RegionResponse> findByName(String name);
}
