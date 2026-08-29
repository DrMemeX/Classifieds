package ru.drmemex.classifieds.feature.advertisement.filter;

import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementSortField;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.model.SortDirection;

import java.math.BigDecimal;
import java.util.List;

public record AdvertisementFilter(
        AdvertisementStatus status,
        List<Long> categoryIds,
        List<Long> regionIds,
        String locality,
        String title,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        AdvertisementSortField sortField,
        SortDirection sortDirection
) {
}
