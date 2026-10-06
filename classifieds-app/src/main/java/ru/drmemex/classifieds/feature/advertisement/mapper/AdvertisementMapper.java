package ru.drmemex.classifieds.feature.advertisement.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.drmemex.classifieds.feature.advertisement.dto.request.AdvertisementRequest;
import ru.drmemex.classifieds.feature.advertisement.dto.response.AdvertisementResponse;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AdvertisementMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "region", ignore = true)
    @Mapping(target = "advertisementStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Advertisement toEntity(AdvertisementRequest request);

    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "regionId", source = "region.id")
    @Mapping(target = "status", source = "advertisementStatus")
    AdvertisementResponse toResponse(Advertisement advertisement);
}
