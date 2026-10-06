package ru.drmemex.classifieds.feature.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.drmemex.classifieds.feature.category.dto.request.CategoryRequest;
import ru.drmemex.classifieds.feature.category.dto.response.CategoryResponse;
import ru.drmemex.classifieds.feature.category.dto.response.CategoryTreeResponse;
import ru.drmemex.classifieds.feature.category.entity.Category;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "active", ignore = true)
    Category toEntity(CategoryRequest request);

    @Mapping(target = "parentId", source = "parent.id")
    CategoryResponse toResponse(Category category);

    CategoryTreeResponse toTreeResponse(Category category);
}
