package ru.drmemex.classifieds.feature.category.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.feature.category.entity.Category;
import ru.drmemex.classifieds.feature.category.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Category save(Category category) {
        entityManager.persist(category);
        return category;
    }

    @Override
    public Category update(Category category) {
        return entityManager.merge(category);
    }

    @Override
    public List<Category> findAll() {
        return entityManager.createQuery(
                        "SELECT c FROM Category c ORDER BY c.name",
                        Category.class
                )
                .getResultList();
    }

    @Override
    public List<Category> findByActive(Boolean active) {
        return entityManager.createQuery(
                        "SELECT c FROM Category c WHERE c.active = :active ORDER BY c.name",
                        Category.class
                )
                .setParameter("active", active)
                .getResultList();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(Category.class, id)
        );
    }

    @Override
    public Optional<Category> findActiveById(Long id) {
        return entityManager.createQuery(
                        "SELECT c FROM Category c WHERE c.id = :id AND c.active = TRUE",
                        Category.class
                )
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Category> findByName(String name) {
        return entityManager.createQuery(
                        """
                                SELECT c
                                FROM Category c
                                WHERE LOWER(c.name) = :name
                                """,
                        Category.class
                )
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Category> findActiveByName(String name) {
        return entityManager.createQuery(
                        """
                                SELECT c
                                FROM Category c
                                WHERE LOWER(c.name) = :name
                                AND c.active = TRUE
                                """,
                        Category.class
                )
                .setParameter("name", name)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public List<Category> findRootCategories() {
        return entityManager.createQuery(
                        "SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.name",
                        Category.class
                )
                .getResultList();
    }

    @Override
    public List<Category> findActiveRootCategories() {
        return entityManager.createQuery(
                        """
                                SELECT c FROM Category c
                                WHERE c.parent IS NULL
                                AND c.active = TRUE
                                ORDER BY c.name""",
                        Category.class
                )
                .getResultList();
    }

    @Override
    public List<Category> findChildren(Long parentId) {
        return entityManager.createQuery(
                        "SELECT c FROM Category c WHERE c.parent.id = :parentId ORDER BY c.name",
                        Category.class
                )
                .setParameter("parentId", parentId)
                .getResultList();
    }

    @Override
    public List<Category> findActiveChildren(Long parentId) {
        return entityManager.createQuery(
                        """
                                SELECT c FROM Category c
                                WHERE c.parent.id = :parentId
                                AND c.active = TRUE
                                ORDER BY c.name""",
                        Category.class
                )
                .setParameter("parentId", parentId)
                .getResultList();
    }

    @Override
    public boolean existsActiveChildren(Long parentId) {
        Long count = entityManager.createQuery(
                        """
                                SELECT COUNT(c) FROM Category c
                                WHERE c.parent.id = :parentId
                                AND c.active = TRUE""",
                        Long.class
                )
                .setParameter("parentId", parentId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public boolean existsByName(String name) {
        Long count = entityManager.createQuery(
                        """
                                SELECT COUNT(c)
                                FROM Category c
                                WHERE LOWER(c.name) = :name
                                """,
                        Long.class
                )
                .setParameter("name", name)
                .getSingleResult();

        return count > 0;
    }
}