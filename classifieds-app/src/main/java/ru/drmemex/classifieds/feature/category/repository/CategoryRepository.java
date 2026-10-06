package ru.drmemex.classifieds.feature.category.repository;

import ru.drmemex.classifieds.feature.category.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Category update(Category category);

    List<Category> findAll();
    List<Category> findByActive(Boolean active);

    Optional<Category> findById(Long id);
    Optional<Category> findActiveById(Long id);

    Optional<Category> findByName(String name);
    Optional<Category> findActiveByName(String name);

    List<Category> findRootCategories();
    List<Category> findActiveRootCategories();

    List<Category> findChildren(Long parentId);
    List<Category> findActiveChildren(Long parentId);

    boolean existsActiveChildren(Long parentId);

    boolean existsByName(String name);
}
