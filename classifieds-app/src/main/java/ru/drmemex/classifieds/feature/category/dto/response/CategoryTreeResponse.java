package ru.drmemex.classifieds.feature.category.dto.response;

import java.util.List;

public record CategoryTreeResponse(
        Long id,
        String name,
        Boolean active,
        List<CategoryTreeResponse> children
) {
}
