package ru.drmemex.classifieds.feature.region.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.drmemex.classifieds.feature.region.dto.RegionResponse;
import ru.drmemex.classifieds.feature.region.entity.Region;
import ru.drmemex.classifieds.feature.region.exception.RegionNotFoundException;
import ru.drmemex.classifieds.feature.region.mapper.RegionMapper;
import ru.drmemex.classifieds.feature.region.repository.RegionRepository;
import ru.drmemex.classifieds.feature.region.service.RegionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;

    @Override
    public List<RegionResponse> getAll() {
        return regionRepository.findAll()
                .stream()
                .map(regionMapper::toResponse)
                .toList();
    }

    @Override
    public RegionResponse getById(Long id) {

        Region region = regionRepository.findById(id)
                .orElseThrow(RegionNotFoundException::new);

        return regionMapper.toResponse(region);
    }

    @Override
    public List<RegionResponse> findByName(String name) {
        return regionRepository.findByName(name)
                .stream()
                .map(regionMapper::toResponse)
                .toList();
    }
}
