package ru.drmemex.classifieds.feature.category.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.common.util.string.StringNormalizer;
import ru.drmemex.classifieds.feature.category.dto.request.CategoryRequest;
import ru.drmemex.classifieds.feature.category.dto.response.CategoryResponse;
import ru.drmemex.classifieds.feature.category.dto.response.CategoryTreeResponse;
import ru.drmemex.classifieds.feature.category.entity.Category;
import ru.drmemex.classifieds.feature.category.exception.CategoryAlreadyActiveException;
import ru.drmemex.classifieds.feature.category.exception.CategoryAlreadyExistsException;
import ru.drmemex.classifieds.feature.category.exception.CategoryAlreadyInactiveException;
import ru.drmemex.classifieds.feature.category.exception.CategoryCannotBeItsOwnParentException;
import ru.drmemex.classifieds.feature.category.exception.CategoryHasActiveChildrenException;
import ru.drmemex.classifieds.feature.category.exception.CategoryHierarchyCycleException;
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
public class CategoryServiceImpl implements CategoryService {

    private static final String MISC_CATEGORY_NAME = "Разное";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {

        String name = StringNormalizer.normalizeDisplayName(request.name());
        String comparableName = StringNormalizer.normalizeComparable(name);

        if (categoryRepository.existsByName(comparableName)) {
            throw new CategoryAlreadyExistsException(name);
        }

        Category category = categoryMapper.toEntity(request);
        category.setName(name);

        if (request.parentName() != null) {

            String parentName = StringNormalizer.normalizeComparable(request.parentName());

            Category parent = categoryRepository.findByName(parentName)
                    .orElseThrow(() -> new CategoryNotFoundException(parentName));

            if (!parent.getActive()) {
                throw new ParentCategoryIsInactiveException(parent.getId());
            }

            category.setParent(parent);
        }

        category.setActive(true);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        String name = StringNormalizer.normalizeDisplayName(request.name());

        if (MISC_CATEGORY_NAME.equals(existingCategory.getName())
                && !MISC_CATEGORY_NAME.equals(name)) {
            throw new SystemCategoryModificationException(
                    existingCategory.getName()
            );
        }

        if (!existingCategory.getName().equals(name)
                && categoryRepository.existsByName(
                StringNormalizer.normalizeComparable(name)
        )) {
            throw new CategoryAlreadyExistsException(name);
        }

        Category parent = null;

        if (request.parentName() != null) {

            String parentName =
                    StringNormalizer.normalizeComparable(request.parentName());

            parent = categoryRepository.findByName(parentName)
                    .orElseThrow(() -> new CategoryNotFoundException(parentName));

            if (parent.getId().equals(id)) {
                throw new CategoryCannotBeItsOwnParentException(id);
            }

            if (existingCategory.getActive() && !parent.getActive()) {
                throw new ParentCategoryIsInactiveException(parent.getId());
            }

            checkForCycle(id, parent.getId());
        }

        existingCategory.setName(name);
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
        return toResponse(categoryRepository.findById(id), id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getActiveById(Long id) {
        return toResponse(categoryRepository.findActiveById(id), id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getByName(String name) {

        String normalizedName = StringNormalizer.normalizeComparable(name);

        return toResponse(
                categoryRepository.findByName(normalizedName),
                normalizedName
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getActiveByName(String name) {

        String normalizedName =
                StringNormalizer.normalizeComparable(name);

        return toResponse(
                categoryRepository.findActiveByName(normalizedName),
                normalizedName
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryTreeResponse getTreeById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        return buildTreeResponse(category, false);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryTreeResponse getActiveTreeById(Long id) {

        Category category = categoryRepository.findActiveById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

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
                .orElseThrow(() -> new CategoryNotFoundException(parentId));

        return toResponses(
                categoryRepository.findChildren(parentId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveChildren(Long parentId) {

        categoryRepository.findActiveById(parentId)
                .orElseThrow(() -> new CategoryNotFoundException(parentId));

        return toResponses(categoryRepository.findActiveChildren(parentId));
    }

    @Override
    @Transactional
    public void activate(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (category.getActive()) {
            throw new CategoryAlreadyActiveException(id);
        }

        if (category.getParent() != null
                && !category.getParent().getActive()) {
            throw new ParentCategoryIsInactiveException(
                    category.getParent().getId()
            );
        }

        category.setActive(true);

        categoryRepository.update(category);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (MISC_CATEGORY_NAME.equals(category.getName())) {
            throw new SystemCategoryModificationException(
                    category.getName()
            );
        }

        if (!category.getActive()) {
            throw new CategoryAlreadyInactiveException(id);
        }

        if (categoryRepository.existsActiveChildren(category.getId())) {
            throw new CategoryHasActiveChildrenException(id);
        }

        category.setActive(false);

        categoryRepository.update(category);
    }

    private CategoryResponse toResponse(
            Optional<Category> category,
            Long id
    ) {
        return categoryMapper.toResponse(
                category.orElseThrow(() -> new CategoryNotFoundException(id))
        );
    }

    private CategoryResponse toResponse(
            Optional<Category> category,
            String name
    ) {
        return categoryMapper.toResponse(
                category.orElseThrow(() -> new CategoryNotFoundException(name))
        );
    }

    private List<CategoryResponse> toResponses(
            List<Category> categories
    ) {
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
                throw new CategoryHierarchyCycleException(categoryId);
            }

            if (currentParentId.equals(categoryId)) {
                throw new CategoryCannotBeItsOwnParentException(categoryId);
            }

            Long currentId = currentParentId;

            Category parent = categoryRepository.findById(currentId)
                    .orElseThrow(() -> new CategoryNotFoundException(currentId));

            currentParentId = parent.getParent() != null
                    ? parent.getParent().getId()
                    : null;
        }
    }
}