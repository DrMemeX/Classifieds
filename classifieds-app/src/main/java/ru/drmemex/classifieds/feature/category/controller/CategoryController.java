package ru.drmemex.classifieds.feature.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.feature.category.dto.response.CategoryResponse;
import ru.drmemex.classifieds.feature.category.dto.response.CategoryTreeResponse;
import ru.drmemex.classifieds.feature.category.service.CategoryService;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getByActive() {
        return categoryService.getByActive(true);
    }

    @GetMapping("/{id}")
    public CategoryResponse getActiveById(
            @PathVariable
            Long id
    ) {
        return categoryService.getActiveById(id);
    }

    @GetMapping("/by-name")
    public CategoryResponse getActiveByName(
            @RequestParam
            String name
    ) {
        return categoryService.getActiveByName(name);
    }

    @GetMapping("/{id}/tree")
    public CategoryTreeResponse getActiveTreeById(
            @PathVariable
            Long id
    ) {
        return categoryService.getActiveTreeById(id);
    }

    @GetMapping("/root")
    public List<CategoryResponse> getActiveRootCategories() {
        return categoryService.getActiveRootCategories();
    }

    @GetMapping("/{parentId}/children")
    public List<CategoryResponse> getActiveChildren(
            @PathVariable
            Long parentId
    ) {
        return categoryService.getActiveChildren(parentId);
    }
}
