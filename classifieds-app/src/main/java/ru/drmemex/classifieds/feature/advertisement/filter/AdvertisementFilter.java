package ru.drmemex.classifieds.feature.advertisement.filter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementSortField;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.model.SortDirection;

import java.math.BigDecimal;
import java.util.List;

public record AdvertisementFilter(
        AdvertisementStatus status,
        @Size(min = 1)
        List<@NotNull @Positive Long> categoryIds,

        @Size(min = 1)
        List<@NotNull @Positive Long> regionIds,

        String locality,
        String title,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        AdvertisementSortField sortField,
        SortDirection sortDirection
) {
}
