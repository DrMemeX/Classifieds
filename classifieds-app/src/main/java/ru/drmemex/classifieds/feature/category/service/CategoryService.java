package ru.drmemex.classifieds.feature.category.service;

import ru.drmemex.classifieds.feature.category.dto.request.CategoryRequest;
import ru.drmemex.classifieds.feature.category.dto.response.CategoryResponse;
import ru.drmemex.classifieds.feature.category.dto.response.CategoryTreeResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    List<CategoryResponse> getAll();
    List<CategoryResponse> getByActive(Boolean active);

    CategoryResponse getById(Long id);
    CategoryResponse getActiveById(Long id);

    CategoryResponse getByName(String name);
    CategoryResponse getActiveByName(String name);

    CategoryTreeResponse getTreeById(Long id);
    CategoryTreeResponse getActiveTreeById(Long id);

    List<CategoryResponse> getRootCategories();
    List<CategoryResponse> getActiveRootCategories();

    List<CategoryResponse> getChildren(Long parentId);
    List<CategoryResponse> getActiveChildren(Long parentId);

    void activate(Long id);
    void deactivate(Long id);
}
