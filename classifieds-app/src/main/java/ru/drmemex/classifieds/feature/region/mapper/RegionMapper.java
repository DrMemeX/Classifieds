package ru.drmemex.classifieds.feature.region.mapper;

import org.mapstruct.Mapper;
import ru.drmemex.classifieds.feature.region.dto.RegionResponse;
import ru.drmemex.classifieds.feature.region.entity.Region;

@Mapper(componentModel = "spring")
public interface RegionMapper {

    RegionResponse toResponse(Region region);
}
