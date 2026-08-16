package ru.drmemex.classifieds.feature.category.dto.request;

public record CategoryRequest(
        String name,
        String parentName
) {
}
