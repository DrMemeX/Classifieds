package ru.drmemex.classifieds.feature.region.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.common.util.string.StringNormalizer;
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
    @Transactional(readOnly = true)
    public List<RegionResponse> getAll() {
        return regionRepository.findAll()
                .stream()
                .map(regionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RegionResponse getById(Long id) {

        Region region = regionRepository.findById(id)
                .orElseThrow(RegionNotFoundException::new);

        return regionMapper.toResponse(region);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegionResponse> getByName(String name) {

        String normalizedName = StringNormalizer.normalizeComparable(name);

        return regionRepository.findByName(normalizedName)
                .stream()
                .map(regionMapper::toResponse)
                .toList();
    }
}
