package ru.drmemex.classifieds.feature.category.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.drmemex.classifieds.feature.category.entity.Category;
import ru.drmemex.classifieds.feature.category.repository.CategoryRepository;
import ru.drmemex.classifieds.integration.AbstractIntegrationTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void save_ShouldSaveCategory() {

        Category category = buildCategory(
                "Test Category",
                true,
                null
        );

        Category savedCategory =
                categoryRepository.save(category);

        entityManager.flush();
        entityManager.clear();

        Category actualCategory = entityManager.find(
                Category.class,
                savedCategory.getId()
        );

        assertNotNull(
                actualCategory
        );

        assertEquals(
                "Test Category",
                actualCategory.getName()
        );

        assertTrue(
                actualCategory.getActive()
        );

        assertNull(
                actualCategory.getParent()
        );
    }

    @Test
    void update_ShouldUpdateCategory() {

        Category category = createCategory(
                "Old Category",
                true
        );

        entityManager.flush();
        entityManager.clear();

        category.setName("Updated Category");
        category.setActive(false);

        Category updatedCategory =
                categoryRepository.update(category);

        entityManager.flush();
        entityManager.clear();

        Category actualCategory = entityManager.find(
                Category.class,
                updatedCategory.getId()
        );

        assertNotNull(
                actualCategory
        );

        assertEquals(
                "Updated Category",
                actualCategory.getName()
        );

        assertFalse(
                actualCategory.getActive()
        );
    }

    @Test
    void findAll_ShouldReturnCategoriesSortedByName() {

        createCategory(
                "B Category",
                true
        );

        createCategory(
                "A Category",
                true
        );

        createCategory(
                "C Category",
                false
        );

        entityManager.flush();
        entityManager.clear();

        List<Category> result =
                categoryRepository.findAll();

        List<String> names = result.stream()
                .map(Category::getName)
                .toList();

        assertTrue(
                names.contains("A Category")
        );

        assertTrue(
                names.contains("B Category")
        );

        assertTrue(
                names.contains("C Category")
        );

        assertTrue(
                names.indexOf("A Category")
                        < names.indexOf("B Category")
        );

        assertTrue(
                names.indexOf("B Category")
                        < names.indexOf("C Category")
        );
    }

    @Test
    void findByActive_ShouldReturnCategoriesWithSpecifiedActiveStatus() {

        createCategory(
                "B Inactive Category",
                false
        );

        createCategory(
                "A Inactive Category",
                false
        );

        createCategory(
                "Active Category",
                true
        );

        entityManager.flush();
        entityManager.clear();

        List<Category> result =
                categoryRepository.findByActive(false);

        List<String> names = result.stream()
                .map(Category::getName)
                .toList();

        assertTrue(
                result.stream()
                        .allMatch(category ->
                                !category.getActive()
                        )
        );

        assertTrue(
                names.contains("A Inactive Category")
        );

        assertTrue(
                names.contains("B Inactive Category")
        );

        assertFalse(
                names.contains("Active Category")
        );

        assertTrue(
                names.indexOf("A Inactive Category")
                        < names.indexOf("B Inactive Category")
        );
    }

    @Test
    void findById_ShouldReturnCategory() {

        Category category = createCategory(
                "Find By Id Category",
                true
        );

        entityManager.flush();

        Long categoryId = category.getId();

        entityManager.clear();

        Optional<Category> result =
                categoryRepository.findById(categoryId);

        assertTrue(
                result.isPresent()
        );

        Category actualCategory =
                result.orElseThrow();

        assertEquals(
                categoryId,
                actualCategory.getId()
        );

        assertEquals(
                "Find By Id Category",
                actualCategory.getName()
        );

        assertTrue(
                actualCategory.getActive()
        );
    }

    @Test
    void findById_ShouldReturnEmpty_WhenCategoryDoesNotExist() {

        Optional<Category> result =
                categoryRepository.findById(Long.MAX_VALUE);

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void findActiveById_ShouldReturnCategory_WhenCategoryIsActive() {

        Category category = createCategory(
                "Active Category By Id",
                true
        );

        entityManager.flush();

        Long categoryId = category.getId();

        entityManager.clear();

        Optional<Category> result =
                categoryRepository.findActiveById(categoryId);

        assertTrue(
                result.isPresent()
        );

        Category actualCategory =
                result.orElseThrow();

        assertEquals(
                categoryId,
                actualCategory.getId()
        );

        assertTrue(
                actualCategory.getActive()
        );
    }

    @Test
    void findActiveById_ShouldReturnEmpty_WhenCategoryIsInactive() {

        Category category = createCategory(
                "Inactive Category By Id",
                false
        );

        entityManager.flush();

        Long categoryId = category.getId();

        entityManager.clear();

        Optional<Category> result =
                categoryRepository.findActiveById(categoryId);

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void findByName_ShouldReturnCategory() {

        createCategory(
                "Electronics",
                true
        );

        entityManager.flush();
        entityManager.clear();

        Optional<Category> result =
                categoryRepository.findByName(
                        "electronics"
                );

        assertTrue(
                result.isPresent()
        );

        Category actualCategory =
                result.orElseThrow();

        assertEquals(
                "Electronics",
                actualCategory.getName()
        );
    }

    @Test
    void findByName_ShouldReturnEmpty_WhenCategoryDoesNotExist() {

        Optional<Category> result =
                categoryRepository.findByName(
                        "non-existent-category"
                );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void findActiveByName_ShouldReturnCategory_WhenCategoryIsActive() {

        createCategory(
                "Active Electronics",
                true
        );

        entityManager.flush();
        entityManager.clear();

        Optional<Category> result =
                categoryRepository.findActiveByName(
                        "active electronics"
                );

        assertTrue(
                result.isPresent()
        );

        Category actualCategory =
                result.orElseThrow();

        assertEquals(
                "Active Electronics",
                actualCategory.getName()
        );

        assertTrue(
                actualCategory.getActive()
        );
    }

    @Test
    void findActiveByName_ShouldReturnEmpty_WhenCategoryIsInactive() {

        createCategory(
                "Inactive Electronics",
                false
        );

        entityManager.flush();
        entityManager.clear();

        Optional<Category> result =
                categoryRepository.findActiveByName(
                        "inactive electronics"
                );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void findRootCategories_ShouldReturnOnlyRootCategoriesSortedByName() {

        Category rootB = createCategory(
                "B Root Category",
                true
        );

        Category rootA = createCategory(
                "A Root Category",
                true
        );

        createCategory(
                "Child Category",
                true,
                rootA
        );

        entityManager.flush();
        entityManager.clear();

        List<Category> result =
                categoryRepository.findRootCategories();

        List<String> names = result.stream()
                .map(Category::getName)
                .toList();

        assertTrue(
                names.contains("A Root Category")
        );

        assertTrue(
                names.contains("B Root Category")
        );

        assertFalse(
                names.contains("Child Category")
        );

        assertTrue(
                names.indexOf("A Root Category")
                        < names.indexOf("B Root Category")
        );
    }

    @Test
    void findActiveRootCategories_ShouldReturnOnlyActiveRootCategoriesSortedByName() {

        createCategory(
                "B Active Root Category",
                true
        );

        Category activeRootA = createCategory(
                "A Active Root Category",
                true
        );

        createCategory(
                "Inactive Root Category",
                false
        );

        createCategory(
                "Active Child Category",
                true,
                activeRootA
        );

        entityManager.flush();
        entityManager.clear();

        List<Category> result =
                categoryRepository.findActiveRootCategories();

        List<String> names = result.stream()
                .map(Category::getName)
                .toList();

        assertTrue(
                names.contains("A Active Root Category")
        );

        assertTrue(
                names.contains("B Active Root Category")
        );

        assertFalse(
                names.contains("Inactive Root Category")
        );

        assertFalse(
                names.contains("Active Child Category")
        );

        assertTrue(
                result.stream()
                        .allMatch(category ->
                                category.getParent() == null
                                        && category.getActive()
                        )
        );

        assertTrue(
                names.indexOf("A Active Root Category")
                        < names.indexOf("B Active Root Category")
        );
    }

    @Test
    void findChildren_ShouldReturnOnlyDirectChildrenOfSpecifiedParent() {

        Category parentA = createCategory(
                "Parent A",
                true
        );

        Category parentB = createCategory(
                "Parent B",
                true
        );

        Category childB = createCategory(
                "B Child",
                true,
                parentA
        );

        Category childA = createCategory(
                "A Child",
                true,
                parentA
        );

        createCategory(
                "Another Parent Child",
                true,
                parentB
        );

        createCategory(
                "Grandchild",
                true,
                childA
        );

        entityManager.flush();

        Long parentId = parentA.getId();

        entityManager.clear();

        List<Category> result =
                categoryRepository.findChildren(parentId);

        List<String> names = result.stream()
                .map(Category::getName)
                .toList();

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                names.contains("A Child")
        );

        assertTrue(
                names.contains("B Child")
        );

        assertFalse(
                names.contains("Another Parent Child")
        );

        assertFalse(
                names.contains("Grandchild")
        );

        assertEquals(
                "A Child",
                result.get(0).getName()
        );

        assertEquals(
                "B Child",
                result.get(1).getName()
        );
    }

    @Test
    void findActiveChildren_ShouldReturnOnlyActiveChildrenOfSpecifiedParent() {

        Category parent = createCategory(
                "Parent Category",
                true
        );

        createCategory(
                "B Active Child",
                true,
                parent
        );

        createCategory(
                "A Active Child",
                true,
                parent
        );

        createCategory(
                "Inactive Child",
                false,
                parent
        );

        entityManager.flush();

        Long parentId = parent.getId();

        entityManager.clear();

        List<Category> result =
                categoryRepository.findActiveChildren(parentId);

        List<String> names = result.stream()
                .map(Category::getName)
                .toList();

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                names.contains("A Active Child")
        );

        assertTrue(
                names.contains("B Active Child")
        );

        assertFalse(
                names.contains("Inactive Child")
        );

        assertEquals(
                "A Active Child",
                result.get(0).getName()
        );

        assertEquals(
                "B Active Child",
                result.get(1).getName()
        );
    }

    @Test
    void existsActiveChildren_ShouldReturnTrue_WhenActiveChildExists() {

        Category parent = createCategory(
                "Parent With Active Child",
                true
        );

        createCategory(
                "Active Child For Exists",
                true,
                parent
        );

        entityManager.flush();

        Long parentId = parent.getId();

        entityManager.clear();

        boolean result =
                categoryRepository.existsActiveChildren(parentId);

        assertTrue(
                result
        );
    }

    @Test
    void existsActiveChildren_ShouldReturnFalse_WhenOnlyInactiveChildExists() {

        Category parent = createCategory(
                "Parent With Inactive Child",
                true
        );

        createCategory(
                "Inactive Child For Exists",
                false,
                parent
        );

        entityManager.flush();

        Long parentId = parent.getId();

        entityManager.clear();

        boolean result =
                categoryRepository.existsActiveChildren(parentId);

        assertFalse(
                result
        );
    }

    @Test
    void existsByName_ShouldReturnTrue_WhenCategoryExists() {

        createCategory(
                "Existing Category",
                true
        );

        entityManager.flush();
        entityManager.clear();

        boolean result =
                categoryRepository.existsByName(
                        "existing category"
                );

        assertTrue(
                result
        );
    }

    @Test
    void existsByName_ShouldReturnFalse_WhenCategoryDoesNotExist() {

        boolean result =
                categoryRepository.existsByName(
                        "non-existent-category"
                );

        assertFalse(
                result
        );
    }

    private Category createCategory(
            String name
    ) {

        return createCategory(
                name,
                true,
                null
        );
    }

    private Category createCategory(
            String name,
            boolean active
    ) {

        return createCategory(
                name,
                active,
                null
        );
    }

    private Category createCategory(
            String name,
            boolean active,
            Category parent
    ) {

        Category category = buildCategory(
                name,
                active,
                parent
        );

        entityManager.persist(category);

        return category;
    }

    private Category buildCategory(
            String name,
            boolean active,
            Category parent
    ) {

        return Category.builder()
                .name(name)
                .active(active)
                .parent(parent)
                .build();
    }
}