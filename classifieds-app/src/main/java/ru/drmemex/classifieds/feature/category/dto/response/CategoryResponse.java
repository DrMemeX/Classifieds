package ru.drmemex.classifieds.feature.category.dto.response;

public record CategoryResponse(

        Long id,
        String name,
        Long parentId,
        Boolean active
) {
}
