package ru.drmemex.classifieds.feature.advertisement.image.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.drmemex.classifieds.feature.advertisement.image.dto.request.AdvertisementImageRequest;
import ru.drmemex.classifieds.feature.advertisement.image.dto.response.AdvertisementImageResponse;
import ru.drmemex.classifieds.feature.advertisement.image.entity.AdvertisementImage;

@Mapper(componentModel = "spring")
public interface AdvertisementImageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "advertisement", ignore = true)
    @Mapping(target = "displayOrder", ignore = true)
    AdvertisementImage toEntity(AdvertisementImageRequest request);

    @Mapping(target = "advertisementId", source = "advertisement.id")
    AdvertisementImageResponse toResponse(AdvertisementImage image);
}
