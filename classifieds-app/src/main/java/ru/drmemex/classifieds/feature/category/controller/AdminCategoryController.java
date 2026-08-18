package ru.drmemex.classifieds.feature.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.feature.category.dto.request.CategoryRequest;
import ru.drmemex.classifieds.feature.category.dto.response.CategoryResponse;
import ru.drmemex.classifieds.feature.category.dto.response.CategoryTreeResponse;
import ru.drmemex.classifieds.feature.category.service.CategoryService;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/active")
    public List<CategoryResponse> getByActive(
            @RequestParam
            Boolean active
    ) {
        return categoryService.getByActive(active);
    }

    @GetMapping("/{id}")
    public CategoryResponse getById(
            @PathVariable
            Long id
    ) {
        return categoryService.getById(id);
    }

    @GetMapping("/by-name")
    public CategoryResponse getByName(
            @RequestParam
            String name
    ) {
        return categoryService.getByName(name);
    }

    @GetMapping("/{id}/tree")
    public CategoryTreeResponse getTreeById(
            @PathVariable
            Long id
    ) {
        return categoryService.getTreeById(id);
    }

    @GetMapping("/root")
    public List<CategoryResponse> getRootCategories() {
        return categoryService.getRootCategories();
    }

    @GetMapping("/{id}/children")
    public List<CategoryResponse> getChildren(
            @PathVariable
            Long id
    ) {
        return categoryService.getChildren(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(
            @Valid
            @RequestBody
            CategoryRequest request
    ) {
        return categoryService.create(request);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(
            @PathVariable
            Long id,
            @Valid
            @RequestBody
            CategoryRequest request
    ) {
        return categoryService.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(
            @PathVariable
            Long id
    ) {
        categoryService.activate(id);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(
            @PathVariable
            Long id
    ) {
        categoryService.deactivate(id);
    }
}
