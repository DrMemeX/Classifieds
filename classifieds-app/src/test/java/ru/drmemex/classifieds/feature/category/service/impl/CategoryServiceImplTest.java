package ru.drmemex.classifieds.feature.category.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void create_ShouldCreateRootCategory() {

        CategoryRequest request = new CategoryRequest(
                "Электроника",
                null
        );

        Category category = new Category();

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Электроника");
        savedCategory.setActive(true);

        CategoryResponse expectedResponse = new CategoryResponse(
                1L,
                "Электроника",
                null,
                true
        );

        when(categoryRepository.existsByName(anyString()))
                .thenReturn(false);

        when(categoryMapper.toEntity(request))
                .thenReturn(category);

        when(categoryRepository.save(category))
                .thenReturn(savedCategory);

        when(categoryMapper.toResponse(savedCategory))
                .thenReturn(expectedResponse);

        CategoryResponse result = categoryService.create(request);

        assertEquals(
                expectedResponse,
                result
        );

        assertEquals(
                "Электроника",
                category.getName()
        );

        assertNull(
                category.getParent()
        );

        assertTrue(
                category.getActive()
        );

        verify(categoryRepository, never())
                .findByName(anyString());

        verify(categoryRepository)
                .save(category);

        verify(categoryMapper)
                .toResponse(savedCategory);
    }

    @Test
    void create_ShouldCreateCategoryWithParent() {

        CategoryRequest request = new CategoryRequest(
                "Ноутбуки",
                "Электроника"
        );

        Category parent = new Category();
        parent.setId(1L);
        parent.setName("Электроника");
        parent.setActive(true);

        Category category = new Category();

        Category savedCategory = new Category();
        savedCategory.setId(2L);
        savedCategory.setName("Ноутбуки");
        savedCategory.setParent(parent);
        savedCategory.setActive(true);

        CategoryResponse expectedResponse = new CategoryResponse(
                2L,
                "Ноутбуки",
                1L,
                true
        );

        when(categoryRepository.existsByName(anyString()))
                .thenReturn(false);

        when(categoryMapper.toEntity(request))
                .thenReturn(category);

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.of(parent));

        when(categoryRepository.save(category))
                .thenReturn(savedCategory);

        when(categoryMapper.toResponse(savedCategory))
                .thenReturn(expectedResponse);

        CategoryResponse result = categoryService.create(request);

        assertEquals(
                expectedResponse,
                result
        );

        assertEquals(
                "Ноутбуки",
                category.getName()
        );

        assertEquals(
                parent,
                category.getParent()
        );

        assertTrue(
                category.getActive()
        );

        verify(categoryRepository)
                .findByName(anyString());

        verify(categoryRepository)
                .save(category);

        verify(categoryMapper)
                .toResponse(savedCategory);
    }

    @Test
    void create_ShouldThrowException_WhenCategoryAlreadyExists() {
        CategoryRequest request = new CategoryRequest(
                "Электроника",
                null
        );

        when(categoryRepository.existsByName(anyString()))
                .thenReturn(true);

        assertThrows(
                CategoryAlreadyExistsException.class,
                () -> categoryService.create(request)
        );

        verify(categoryMapper, never())
                .toEntity(any(CategoryRequest.class));

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void create_ShouldThrowException_WhenParentCategoryNotFound() {

        CategoryRequest request = new CategoryRequest(
                "Ноутбуки",
                "Электроника"
        );

        Category category = new Category();

        when(categoryRepository.existsByName(anyString()))
                .thenReturn(false);

        when(categoryMapper.toEntity(request))
                .thenReturn(category);

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.create(request)
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void create_ShouldThrowException_WhenParentCategoryIsInactive() {

        CategoryRequest request = new CategoryRequest(
                "Ноутбуки",
                "Электроника"
        );

        Category parent = new Category();
        parent.setId(1L);
        parent.setName("Электроника");
        parent.setActive(false);

        Category category = new Category();

        when(categoryRepository.existsByName(anyString()))
                .thenReturn(false);

        when(categoryMapper.toEntity(request))
                .thenReturn(category);

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.of(parent));

        assertThrows(
                ParentCategoryIsInactiveException.class,
                () -> categoryService.create(request)
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void update_ShouldUpdateCategoryWithoutParent() {

        Category oldParent = new Category();
        oldParent.setId(1L);
        oldParent.setName("Электроника");
        oldParent.setActive(true);

        Category existingCategory = new Category();
        existingCategory.setId(2L);
        existingCategory.setName("Ноутбуки");
        existingCategory.setParent(oldParent);
        existingCategory.setActive(true);

        CategoryRequest request = new CategoryRequest(
                "Компьютеры",
                null
        );

        CategoryResponse expectedResponse = new CategoryResponse(
                2L,
                "Компьютеры",
                null,
                true
        );

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.existsByName(anyString()))
                .thenReturn(false);

        when(categoryRepository.update(existingCategory))
                .thenReturn(existingCategory);

        when(categoryMapper.toResponse(existingCategory))
                .thenReturn(expectedResponse);

        CategoryResponse result = categoryService.update(
                2L,
                request
        );

        assertEquals(
                expectedResponse,
                result
        );

        assertEquals(
                "Компьютеры",
                existingCategory.getName()
        );

        assertNull(
                existingCategory.getParent()
        );

        verify(categoryRepository)
                .update(existingCategory);

        verify(categoryRepository, never())
                .findByName(anyString());
    }

    @Test
    void update_ShouldUpdateCategoryWithParent() {

        Category existingCategory = new Category();
        existingCategory.setId(2L);
        existingCategory.setName("Ноутбуки");
        existingCategory.setActive(true);

        Category parent = new Category();
        parent.setId(1L);
        parent.setName("Электроника");
        parent.setActive(true);
        parent.setParent(null);

        CategoryRequest request = new CategoryRequest(
                "Компьютеры",
                "Электроника"
        );

        CategoryResponse expectedResponse = new CategoryResponse(
                2L,
                "Компьютеры",
                1L,
                true
        );

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.existsByName(anyString()))
                .thenReturn(false);

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.of(parent));

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(parent));

        when(categoryRepository.update(existingCategory))
                .thenReturn(existingCategory);

        when(categoryMapper.toResponse(existingCategory))
                .thenReturn(expectedResponse);

        CategoryResponse result = categoryService.update(
                2L,
                request
        );

        assertEquals(
                expectedResponse,
                result
        );

        assertEquals(
                "Компьютеры",
                existingCategory.getName()
        );

        assertEquals(
                parent,
                existingCategory.getParent()
        );

        verify(categoryRepository)
                .findByName(anyString());

        verify(categoryRepository)
                .update(existingCategory);
    }

    @Test
    void update_ShouldThrowException_WhenCategoryNotFound() {

        CategoryRequest request = new CategoryRequest(
                "Компьютеры",
                null
        );

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.update(
                        2L,
                        request
                )
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void update_ShouldThrowException_WhenCategoryIsSystemCategory() {

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Разное");
        existingCategory.setActive(true);

        CategoryRequest request = new CategoryRequest(
                "Другое",
                null
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(existingCategory));

        assertThrows(
                SystemCategoryModificationException.class,
                () -> categoryService.update(
                        1L,
                        request
                )
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void update_ShouldThrowException_WhenCategoryAlreadyExists() {

        Category existingCategory = new Category();
        existingCategory.setId(2L);
        existingCategory.setName("Ноутбуки");
        existingCategory.setActive(true);

        CategoryRequest request = new CategoryRequest(
                "Компьютеры",
                null
        );

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.existsByName(anyString()))
                .thenReturn(true);

        assertThrows(
                CategoryAlreadyExistsException.class,
                () -> categoryService.update(
                        2L,
                        request
                )
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void update_ShouldThrowException_WhenParentCategoryNotFound() {

        Category existingCategory = new Category();
        existingCategory.setId(2L);
        existingCategory.setName("Ноутбуки");
        existingCategory.setActive(true);

        CategoryRequest request = new CategoryRequest(
                "Ноутбуки",
                "Электроника"
        );

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.update(
                        2L,
                        request
                )
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void update_ShouldThrowException_WhenCategoryIsItsOwnParent() {

        Category existingCategory = new Category();
        existingCategory.setId(2L);
        existingCategory.setName("Ноутбуки");
        existingCategory.setActive(true);

        Category parent = new Category();
        parent.setId(2L);
        parent.setName("Ноутбуки");
        parent.setActive(true);

        CategoryRequest request = new CategoryRequest(
                "Ноутбуки",
                "Ноутбуки"
        );

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.of(parent));

        assertThrows(
                CategoryCannotBeItsOwnParentException.class,
                () -> categoryService.update(
                        2L,
                        request
                )
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void update_ShouldThrowException_WhenParentCategoryIsInactive() {

        Category existingCategory = new Category();
        existingCategory.setId(2L);
        existingCategory.setName("Ноутбуки");
        existingCategory.setActive(true);

        Category parent = new Category();
        parent.setId(1L);
        parent.setName("Электроника");
        parent.setActive(false);

        CategoryRequest request = new CategoryRequest(
                "Ноутбуки",
                "Электроника"
        );

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(existingCategory));

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.of(parent));

        assertThrows(
                ParentCategoryIsInactiveException.class,
                () -> categoryService.update(
                        2L,
                        request
                )
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void update_ShouldThrowException_WhenHierarchyCycleDetected() {

        Category electronics = new Category();
        electronics.setId(1L);
        electronics.setName("Электроника");
        electronics.setActive(true);

        Category computers = new Category();
        computers.setId(2L);
        computers.setName("Компьютеры");
        computers.setActive(true);
        computers.setParent(electronics);

        Category laptops = new Category();
        laptops.setId(3L);
        laptops.setName("Ноутбуки");
        laptops.setActive(true);
        laptops.setParent(computers);

        CategoryRequest request = new CategoryRequest(
                "Электроника",
                "Ноутбуки"
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(electronics));

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.of(laptops));

        when(categoryRepository.findById(3L))
                .thenReturn(Optional.of(laptops));

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(computers));

        assertThrows(
                CategoryHierarchyCycleException.class,
                () -> categoryService.update(
                        1L,
                        request
                )
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void getAll_ShouldReturnAllCategories() {

        Category firstCategory = new Category();
        firstCategory.setId(1L);
        firstCategory.setName("Электроника");

        Category secondCategory = new Category();
        secondCategory.setId(2L);
        secondCategory.setName("Одежда");

        CategoryResponse firstResponse = new CategoryResponse(
                1L,
                "Электроника",
                null,
                true
        );

        CategoryResponse secondResponse = new CategoryResponse(
                2L,
                "Одежда",
                null,
                true
        );

        when(categoryRepository.findAll())
                .thenReturn(List.of(
                        firstCategory,
                        secondCategory
                ));

        when(categoryMapper.toResponse(firstCategory))
                .thenReturn(firstResponse);

        when(categoryMapper.toResponse(secondCategory))
                .thenReturn(secondResponse);

        List<CategoryResponse> result = categoryService.getAll();

        assertEquals(
                List.of(
                        firstResponse,
                        secondResponse
                ),
                result
        );

        verify(categoryRepository)
                .findAll();
    }

    @Test
    void getByActive_ShouldReturnCategoriesByActiveStatus() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Электроника");
        category.setActive(true);

        CategoryResponse response = new CategoryResponse(
                1L,
                "Электроника",
                null,
                true
        );

        when(categoryRepository.findByActive(true))
                .thenReturn(List.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        List<CategoryResponse> result =
                categoryService.getByActive(true);

        assertEquals(
                List.of(response),
                result
        );

        verify(categoryRepository)
                .findByActive(true);
    }

    @Test
    void getById_ShouldReturnCategory() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Электроника");

        CategoryResponse expectedResponse = new CategoryResponse(
                1L,
                "Электроника",
                null,
                true
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(expectedResponse);

        CategoryResponse result =
                categoryService.getById(1L);

        assertEquals(
                expectedResponse,
                result
        );

        verify(categoryRepository)
                .findById(1L);

        verify(categoryMapper)
                .toResponse(category);
    }

    @Test
    void getById_ShouldThrowException_WhenCategoryNotFound() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getById(1L)
        );

        verify(categoryMapper, never())
                .toResponse(any(Category.class));
    }

    @Test
    void getActiveById_ShouldReturnActiveCategory() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Электроника");
        category.setActive(true);

        CategoryResponse expectedResponse = new CategoryResponse(
                1L,
                "Электроника",
                null,
                true
        );

        when(categoryRepository.findActiveById(1L))
                .thenReturn(Optional.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(expectedResponse);

        CategoryResponse result =
                categoryService.getActiveById(1L);

        assertEquals(
                expectedResponse,
                result
        );

        verify(categoryRepository)
                .findActiveById(1L);
    }

    @Test
    void getActiveById_ShouldThrowException_WhenCategoryNotFound() {

        when(categoryRepository.findActiveById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getActiveById(1L)
        );

        verify(categoryMapper, never())
                .toResponse(any(Category.class));
    }

    @Test
    void getByName_ShouldReturnCategory() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Электроника");

        CategoryResponse expectedResponse = new CategoryResponse(
                1L,
                "Электроника",
                null,
                true
        );

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(expectedResponse);

        CategoryResponse result =
                categoryService.getByName("Электроника");

        assertEquals(
                expectedResponse,
                result
        );

        verify(categoryRepository)
                .findByName(anyString());

        verify(categoryMapper)
                .toResponse(category);
    }

    @Test
    void getByName_ShouldThrowException_WhenCategoryNotFound() {

        when(categoryRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getByName("Электроника")
        );

        verify(categoryMapper, never())
                .toResponse(any(Category.class));
    }

    @Test
    void getActiveByName_ShouldReturnActiveCategory() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Электроника");
        category.setActive(true);

        CategoryResponse expectedResponse = new CategoryResponse(
                1L,
                "Электроника",
                null,
                true
        );

        when(categoryRepository.findActiveByName(anyString()))
                .thenReturn(Optional.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(expectedResponse);

        CategoryResponse result =
                categoryService.getActiveByName("Электроника");

        assertEquals(
                expectedResponse,
                result
        );

        verify(categoryRepository)
                .findActiveByName(anyString());

        verify(categoryMapper)
                .toResponse(category);
    }

    @Test
    void getActiveByName_ShouldThrowException_WhenCategoryNotFound() {

        when(categoryRepository.findActiveByName(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getActiveByName("Электроника")
        );

        verify(categoryMapper, never())
                .toResponse(any(Category.class));
    }

    @Test
    void getTreeById_ShouldReturnFullTreeFromRoot() {

        Category electronics = new Category();
        electronics.setId(1L);
        electronics.setName("Электроника");
        electronics.setActive(true);

        Category computers = new Category();
        computers.setId(2L);
        computers.setName("Компьютеры");
        computers.setActive(true);
        computers.setParent(electronics);

        Category laptops = new Category();
        laptops.setId(3L);
        laptops.setName("Ноутбуки");
        laptops.setActive(true);
        laptops.setParent(computers);

        CategoryTreeResponse expectedResponse =
                new CategoryTreeResponse(
                        1L,
                        "Электроника",
                        true,
                        List.of(
                                new CategoryTreeResponse(
                                        2L,
                                        "Компьютеры",
                                        true,
                                        List.of(
                                                new CategoryTreeResponse(
                                                        3L,
                                                        "Ноутбуки",
                                                        true,
                                                        List.of()
                                                )
                                        )
                                )
                        )
                );

        when(categoryRepository.findById(3L))
                .thenReturn(Optional.of(laptops));

        when(categoryRepository.findChildren(1L))
                .thenReturn(List.of(computers));

        when(categoryRepository.findChildren(2L))
                .thenReturn(List.of(laptops));

        when(categoryRepository.findChildren(3L))
                .thenReturn(List.of());

        when(categoryMapper.toTreeResponse(electronics))
                .thenReturn(expectedResponse);

        CategoryTreeResponse result =
                categoryService.getTreeById(3L);

        assertEquals(
                expectedResponse,
                result
        );

        assertEquals(
                List.of(computers),
                electronics.getChildren()
        );

        assertEquals(
                List.of(laptops),
                computers.getChildren()
        );

        assertEquals(
                List.of(),
                laptops.getChildren()
        );

        verify(categoryRepository)
                .findById(3L);

        verify(categoryRepository)
                .findChildren(1L);

        verify(categoryRepository)
                .findChildren(2L);

        verify(categoryRepository)
                .findChildren(3L);

        verify(categoryMapper)
                .toTreeResponse(electronics);
    }

    @Test
    void getTreeById_ShouldThrowException_WhenCategoryNotFound() {

        when(categoryRepository.findById(3L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getTreeById(3L)
        );

        verify(categoryRepository, never())
                .findChildren(any());

        verify(categoryMapper, never())
                .toTreeResponse(any(Category.class));
    }

    @Test
    void getActiveTreeById_ShouldReturnActiveTreeFromRoot() {

        Category electronics = new Category();
        electronics.setId(1L);
        electronics.setName("Электроника");
        electronics.setActive(true);

        Category computers = new Category();
        computers.setId(2L);
        computers.setName("Компьютеры");
        computers.setActive(true);
        computers.setParent(electronics);

        Category laptops = new Category();
        laptops.setId(3L);
        laptops.setName("Ноутбуки");
        laptops.setActive(true);
        laptops.setParent(computers);

        CategoryTreeResponse expectedResponse =
                new CategoryTreeResponse(
                        1L,
                        "Электроника",
                        true,
                        List.of(
                                new CategoryTreeResponse(
                                        2L,
                                        "Компьютеры",
                                        true,
                                        List.of(
                                                new CategoryTreeResponse(
                                                        3L,
                                                        "Ноутбуки",
                                                        true,
                                                        List.of()
                                                )
                                        )
                                )
                        )
                );

        when(categoryRepository.findActiveById(3L))
                .thenReturn(Optional.of(laptops));

        when(categoryRepository.findActiveChildren(1L))
                .thenReturn(List.of(computers));

        when(categoryRepository.findActiveChildren(2L))
                .thenReturn(List.of(laptops));

        when(categoryRepository.findActiveChildren(3L))
                .thenReturn(List.of());

        when(categoryMapper.toTreeResponse(electronics))
                .thenReturn(expectedResponse);

        CategoryTreeResponse result =
                categoryService.getActiveTreeById(3L);

        assertEquals(
                expectedResponse,
                result
        );

        assertEquals(
                List.of(computers),
                electronics.getChildren()
        );

        assertEquals(
                List.of(laptops),
                computers.getChildren()
        );

        assertEquals(
                List.of(),
                laptops.getChildren()
        );

        verify(categoryRepository)
                .findActiveById(3L);

        verify(categoryRepository)
                .findActiveChildren(1L);

        verify(categoryRepository)
                .findActiveChildren(2L);

        verify(categoryRepository)
                .findActiveChildren(3L);

        verify(categoryMapper)
                .toTreeResponse(electronics);
    }

    @Test
    void getActiveTreeById_ShouldThrowException_WhenCategoryNotFound() {

        when(categoryRepository.findActiveById(3L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getActiveTreeById(3L)
        );

        verify(categoryRepository, never())
                .findActiveChildren(any());

        verify(categoryMapper, never())
                .toTreeResponse(any(Category.class));
    }

    @Test
    void getRootCategories_ShouldReturnRootCategories() {

        Category firstCategory = new Category();
        firstCategory.setId(1L);
        firstCategory.setName("Электроника");
        firstCategory.setActive(true);

        Category secondCategory = new Category();
        secondCategory.setId(2L);
        secondCategory.setName("Одежда");
        secondCategory.setActive(false);

        CategoryResponse firstResponse = new CategoryResponse(
                1L,
                "Электроника",
                null,
                true
        );

        CategoryResponse secondResponse = new CategoryResponse(
                2L,
                "Одежда",
                null,
                false
        );

        when(categoryRepository.findRootCategories())
                .thenReturn(List.of(
                        firstCategory,
                        secondCategory
                ));

        when(categoryMapper.toResponse(firstCategory))
                .thenReturn(firstResponse);

        when(categoryMapper.toResponse(secondCategory))
                .thenReturn(secondResponse);

        List<CategoryResponse> result =
                categoryService.getRootCategories();

        assertEquals(
                List.of(
                        firstResponse,
                        secondResponse
                ),
                result
        );

        verify(categoryRepository)
                .findRootCategories();
    }

    @Test
    void getActiveRootCategories_ShouldReturnActiveRootCategories() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Электроника");
        category.setActive(true);

        CategoryResponse response = new CategoryResponse(
                1L,
                "Электроника",
                null,
                true
        );

        when(categoryRepository.findActiveRootCategories())
                .thenReturn(List.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        List<CategoryResponse> result =
                categoryService.getActiveRootCategories();

        assertEquals(
                List.of(response),
                result
        );

        verify(categoryRepository)
                .findActiveRootCategories();
    }

    @Test
    void getChildren_ShouldReturnChildren() {

        Category parent = new Category();
        parent.setId(1L);
        parent.setName("Электроника");

        Category firstChild = new Category();
        firstChild.setId(2L);
        firstChild.setName("Ноутбуки");
        firstChild.setParent(parent);

        Category secondChild = new Category();
        secondChild.setId(3L);
        secondChild.setName("Телефоны");
        secondChild.setParent(parent);

        CategoryResponse firstResponse = new CategoryResponse(
                2L,
                "Ноутбуки",
                1L,
                true
        );

        CategoryResponse secondResponse = new CategoryResponse(
                3L,
                "Телефоны",
                1L,
                true
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(parent));

        when(categoryRepository.findChildren(1L))
                .thenReturn(List.of(
                        firstChild,
                        secondChild
                ));

        when(categoryMapper.toResponse(firstChild))
                .thenReturn(firstResponse);

        when(categoryMapper.toResponse(secondChild))
                .thenReturn(secondResponse);

        List<CategoryResponse> result =
                categoryService.getChildren(1L);

        assertEquals(
                List.of(
                        firstResponse,
                        secondResponse
                ),
                result
        );

        verify(categoryRepository)
                .findById(1L);

        verify(categoryRepository)
                .findChildren(1L);
    }

    @Test
    void getChildren_ShouldThrowException_WhenParentNotFound() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getChildren(1L)
        );

        verify(categoryRepository, never())
                .findChildren(1L);
    }

    @Test
    void getActiveChildren_ShouldReturnActiveChildren() {

        Category parent = new Category();
        parent.setId(1L);
        parent.setName("Электроника");
        parent.setActive(true);

        Category child = new Category();
        child.setId(2L);
        child.setName("Ноутбуки");
        child.setParent(parent);
        child.setActive(true);

        CategoryResponse response = new CategoryResponse(
                2L,
                "Ноутбуки",
                1L,
                true
        );

        when(categoryRepository.findActiveById(1L))
                .thenReturn(Optional.of(parent));

        when(categoryRepository.findActiveChildren(1L))
                .thenReturn(List.of(child));

        when(categoryMapper.toResponse(child))
                .thenReturn(response);

        List<CategoryResponse> result =
                categoryService.getActiveChildren(1L);

        assertEquals(
                List.of(response),
                result
        );

        verify(categoryRepository)
                .findActiveById(1L);

        verify(categoryRepository)
                .findActiveChildren(1L);
    }

    @Test
    void getActiveChildren_ShouldThrowException_WhenParentNotFound() {

        when(categoryRepository.findActiveById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.getActiveChildren(1L)
        );

        verify(categoryRepository, never())
                .findActiveChildren(1L);
    }

    @Test
    void activate_ShouldActivateCategory() {

        Category parent = new Category();
        parent.setId(1L);
        parent.setName("Электроника");
        parent.setActive(true);

        Category category = new Category();
        category.setId(2L);
        category.setName("Ноутбуки");
        category.setActive(false);
        category.setParent(parent);

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(category));

        categoryService.activate(2L);

        assertTrue(
                category.getActive()
        );

        verify(categoryRepository)
                .update(category);
    }

    @Test
    void activate_ShouldThrowException_WhenCategoryNotFound() {

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.activate(2L)
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void activate_ShouldThrowException_WhenCategoryAlreadyActive() {

        Category category = new Category();
        category.setId(2L);
        category.setName("Ноутбуки");
        category.setActive(true);

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(category));

        assertThrows(
                CategoryAlreadyActiveException.class,
                () -> categoryService.activate(2L)
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void activate_ShouldThrowException_WhenParentCategoryIsInactive() {

        Category parent = new Category();
        parent.setId(1L);
        parent.setName("Электроника");
        parent.setActive(false);

        Category category = new Category();
        category.setId(2L);
        category.setName("Ноутбуки");
        category.setActive(false);
        category.setParent(parent);

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(category));

        assertThrows(
                ParentCategoryIsInactiveException.class,
                () -> categoryService.activate(2L)
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void deactivate_ShouldDeactivateCategory() {

        Category category = new Category();
        category.setId(2L);
        category.setName("Ноутбуки");
        category.setActive(true);

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.existsActiveChildren(2L))
                .thenReturn(false);

        categoryService.deactivate(2L);

        assertFalse(
                category.getActive()
        );

        verify(categoryRepository)
                .update(category);
    }

    @Test
    void deactivate_ShouldThrowException_WhenCategoryNotFound() {

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.deactivate(2L)
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void deactivate_ShouldThrowException_WhenCategoryIsSystemCategory() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Разное");
        category.setActive(true);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        assertThrows(
                SystemCategoryModificationException.class,
                () -> categoryService.deactivate(1L)
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void deactivate_ShouldThrowException_WhenCategoryAlreadyInactive() {

        Category category = new Category();
        category.setId(2L);
        category.setName("Ноутбуки");
        category.setActive(false);

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(category));

        assertThrows(
                CategoryAlreadyInactiveException.class,
                () -> categoryService.deactivate(2L)
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }

    @Test
    void deactivate_ShouldThrowException_WhenCategoryHasActiveChildren() {

        Category category = new Category();
        category.setId(2L);
        category.setName("Компьютеры");
        category.setActive(true);

        when(categoryRepository.findById(2L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.existsActiveChildren(2L))
                .thenReturn(true);

        assertThrows(
                CategoryHasActiveChildrenException.class,
                () -> categoryService.deactivate(2L)
        );

        verify(categoryRepository, never())
                .update(any(Category.class));
    }
}
