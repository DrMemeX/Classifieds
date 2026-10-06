package ru.drmemex.classifieds.feature.category.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.feature.category.dto.request.CategoryRequest;
import ru.drmemex.classifieds.feature.category.dto.response.CategoryResponse;
import ru.drmemex.classifieds.feature.category.dto.response.CategoryTreeResponse;
import ru.drmemex.classifieds.feature.category.entity.Category;
import ru.drmemex.classifieds.feature.category.exception.CategoryAlreadyActiveException;
import ru.drmemex.classifieds.feature.category.exception.CategoryAlreadyExistsException;
import ru.drmemex.classifieds.feature.category.exception.CategoryAlreadyInactiveException;
import ru.drmemex.classifieds.feature.category.exception.CategoryCannotBeItsOwnParentException;
import ru.drmemex.classifieds.feature.category.exception.CategoryHasActiveChildrenException;
import ru.drmemex.classifieds.feature.category.exception.CategoryNotFoundException;
import ru.drmemex.classifieds.feature.category.exception.ParentCategoryIsInactiveException;
import ru.drmemex.classifieds.feature.category.exception.SystemCategoryModificationException;
import ru.drmemex.classifieds.feature.category.mapper.CategoryMapper;
import ru.drmemex.classifieds.feature.category.repository.CategoryRepository;
import ru.drmemex.classifieds.feature.category.service.CategoryService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final String MISC_CATEGORY_NAME = "Разное";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse create(CategoryRequest request) {

        if (categoryRepository.existsByName(request.name())) {
            throw new CategoryAlreadyExistsException();
        }

        Category category = categoryMapper.toEntity(request);

        if (request.parentName() != null) {
            Category parent = categoryRepository.findByName(request.parentName())
                    .orElseThrow(CategoryNotFoundException::new);

            if (!parent.getActive()) {
                throw new ParentCategoryIsInactiveException();
            }

            category.setParent(parent);
        }

        category.setActive(true);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        if (MISC_CATEGORY_NAME.equals(existingCategory.getName())
                && !MISC_CATEGORY_NAME.equals(request.name())) {
            throw new SystemCategoryModificationException();
        }

        if (!existingCategory.getName().equals(request.name())
                && categoryRepository.existsByName(request.name())) {
            throw new CategoryAlreadyExistsException();
        }

        Category parent = null;

        if (request.parentName() != null) {

            parent = categoryRepository.findByName(request.parentName())
                    .orElseThrow(CategoryNotFoundException::new);

            if (parent.getId().equals(id)) {
                throw new CategoryCannotBeItsOwnParentException();
            }

            if (existingCategory.getActive() && !parent.getActive()) {
                throw new ParentCategoryIsInactiveException();
            }

            checkForCycle(id, parent.getId());
        }

        existingCategory.setName(request.name());
        existingCategory.setParent(parent);

        return categoryMapper.toResponse(
                categoryRepository.update(existingCategory)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return toResponses(categoryRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getByActive(Boolean active) {
        return toResponses(categoryRepository.findByActive(active));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return toResponse(categoryRepository.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getActiveById(Long id) {
        return toResponse(categoryRepository.findActiveById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getByName(String name) {
        return toResponse(categoryRepository.findByName(name));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getActiveByName(String name) {
        return toResponse(categoryRepository.findActiveByName(name));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryTreeResponse getTreeById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        return buildTreeResponse(category, false);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryTreeResponse getActiveTreeById(Long id) {

        Category category = categoryRepository.findActiveById(id)
                .orElseThrow(CategoryNotFoundException::new);

        return buildTreeResponse(category, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories() {
        return toResponses(categoryRepository.findRootCategories());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveRootCategories() {
        return toResponses(categoryRepository.findActiveRootCategories());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getChildren(Long parentId) {

        categoryRepository.findById(parentId)
                .orElseThrow(CategoryNotFoundException::new);

        return toResponses(categoryRepository.findChildren(parentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveChildren(Long parentId) {

        categoryRepository.findActiveById(parentId)
                .orElseThrow(CategoryNotFoundException::new);

        return toResponses(categoryRepository.findActiveChildren(parentId));
    }

    @Override
    public void activate(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        if (category.getActive()) {
            throw new CategoryAlreadyActiveException();
        }

        if (category.getParent() != null
                && !category.getParent().getActive()) {
            throw new ParentCategoryIsInactiveException();
        }

        category.setActive(true);

        categoryRepository.update(category);
    }

    @Override
    public void deactivate(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        if (MISC_CATEGORY_NAME.equals(category.getName())) {
            throw new SystemCategoryModificationException();
        }

        if (!category.getActive()) {
            throw new CategoryAlreadyInactiveException();
        }

        if (categoryRepository.existsActiveChildren(category.getId())) {
            throw new CategoryHasActiveChildrenException();
        }

        category.setActive(false);

        categoryRepository.update(category);
    }

    private CategoryResponse toResponse(Optional<Category> category) {
        return categoryMapper.toResponse(
                category.orElseThrow(CategoryNotFoundException::new)
        );
    }

    private List<CategoryResponse> toResponses(List<Category> categories) {
        return categories.stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    private CategoryTreeResponse buildTreeResponse(
            Category category,
            boolean activeOnly
    ) {
        while (category.getParent() != null) {
            category = category.getParent();
        }

        if (activeOnly) {
            buildActiveTree(category);
        } else {
            buildTree(category);
        }

        return categoryMapper.toTreeResponse(category);
    }

    private void buildTree(Category category) {

        List<Category> children =
                categoryRepository.findChildren(category.getId());

        category.setChildren(children);

        for (Category child : children) {
            buildTree(child);
        }
    }

    private void buildActiveTree(Category category) {

        List<Category> children =
                categoryRepository.findActiveChildren(category.getId());

        category.setChildren(children);

        for (Category child : children) {
            buildActiveTree(child);
        }
    }

    private void checkForCycle(Long categoryId, Long parentId) {

        Set<Long> visited = new HashSet<>();
        Long currentParentId = parentId;

        while (currentParentId != null) {

            if (!visited.add(currentParentId)) {
                throw new CategoryCannotBeItsOwnParentException();
            }

            if (currentParentId.equals(categoryId)) {
                throw new CategoryCannotBeItsOwnParentException();
            }

            Category parent = categoryRepository.findById(currentParentId)
                    .orElseThrow(CategoryNotFoundException::new);

            currentParentId = parent.getParent() != null
                    ? parent.getParent().getId()
                    : null;
        }
    }
}