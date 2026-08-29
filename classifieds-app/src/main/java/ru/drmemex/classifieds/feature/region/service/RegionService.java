package ru.drmemex.classifieds.feature.region.service;

import ru.drmemex.classifieds.feature.region.dto.RegionResponse;

import java.util.List;

public interface RegionService {

    List<RegionResponse> getAll();

    RegionResponse getById(Long id);

    List<RegionResponse> getByName(String name);
}
