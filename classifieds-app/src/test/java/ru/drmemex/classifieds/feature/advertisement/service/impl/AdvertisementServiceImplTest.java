package ru.drmemex.classifieds.feature.advertisement.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
import ru.drmemex.classifieds.feature.advertisement.dto.request.AdvertisementRequest;
import ru.drmemex.classifieds.feature.advertisement.dto.request.AdvertisementUpdateRequest;
import ru.drmemex.classifieds.feature.advertisement.dto.response.AdvertisementResponse;
import ru.drmemex.classifieds.feature.advertisement.dto.response.AdvertisementSellerResponse;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.exception.AdminCannotCreateAdvertisementException;
import ru.drmemex.classifieds.feature.advertisement.exception.AdvertisementAccessDeniedException;
import ru.drmemex.classifieds.feature.advertisement.exception.AdvertisementCategoryInactiveException;
import ru.drmemex.classifieds.feature.advertisement.exception.AdvertisementNotFoundException;
import ru.drmemex.classifieds.feature.advertisement.exception.AdvertisementRateLimitExceededException;
import ru.drmemex.classifieds.feature.advertisement.exception.InvalidAdvertisementStatusException;
import ru.drmemex.classifieds.feature.advertisement.filter.AdvertisementFilter;
import ru.drmemex.classifieds.feature.advertisement.mapper.AdvertisementMapper;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementSortField;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.model.SortDirection;
import ru.drmemex.classifieds.feature.advertisement.repository.AdvertisementRepository;
import ru.drmemex.classifieds.feature.category.entity.Category;
import ru.drmemex.classifieds.feature.category.exception.CategoryNotFoundException;
import ru.drmemex.classifieds.feature.category.repository.CategoryRepository;
import ru.drmemex.classifieds.feature.region.entity.Region;
import ru.drmemex.classifieds.feature.region.exception.RegionNotFoundException;
import ru.drmemex.classifieds.feature.region.repository.RegionRepository;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.security.provider.CurrentUserProvider;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceImplTest {

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private AdvertisementMapper advertisementMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private AdvertisementServiceImpl advertisementService;

    @Test
    void create_ShouldCreateAdvertisement() {

        User seller = new User();
        seller.setId(1L);
        seller.setRole(UserRole.USER);

        Category category = new Category();
        category.setId(1L);

        Region region = new Region();
        region.setId(1L);

        AdvertisementRequest request = new AdvertisementRequest(
                1L,
                1L,
                "Москва",
                "Ноутбук",
                "Игровой ноутбук",
                BigDecimal.valueOf(100000)
        );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(categoryRepository.findActiveById(1L))
                .thenReturn(Optional.of(category));

        when(regionRepository.findById(1L))
                .thenReturn(Optional.of(region));

        when(advertisementRepository.countBySellerIdAndCreatedAtAfter(
                eq(1L),
                any(OffsetDateTime.class)
        ))
                .thenReturn(2L);

        Advertisement advertisement = new Advertisement();

        Advertisement savedAdvertisement = new Advertisement();
        savedAdvertisement.setId(1L);

        AdvertisementResponse expectedResponse = new AdvertisementResponse(
                1L,
                1L,
                1L,
                1L,
                "Москва",
                "Ноутбук",
                "Игровой ноутбук",
                BigDecimal.valueOf(100000),
                AdvertisementStatus.ACTIVE,
                null,
                null
        );

        when(advertisementMapper.toEntity(request))
                .thenReturn(advertisement);

        when(advertisementRepository.save(advertisement))
                .thenReturn(savedAdvertisement);

        when(advertisementMapper.toResponse(savedAdvertisement))
                .thenReturn(expectedResponse);

        AdvertisementResponse result = advertisementService.create(request);

        assertEquals(
                expectedResponse,
                result
        );

        assertEquals(
                seller,
                advertisement.getSeller()
        );

        assertEquals(
                category,
                advertisement.getCategory()
        );

        assertEquals(
                region,
                advertisement.getRegion()
        );

        assertEquals(
                AdvertisementStatus.ACTIVE,
                advertisement.getAdvertisementStatus()
        );

        assertNotNull(
                advertisement.getCreatedAt()
        );

        verify(advertisementRepository)
                .save(advertisement);
    }

    @ParameterizedTest
    @EnumSource(
            value = UserRole.class,
            names = {"ADMIN", "SUPER_ADMIN"}
    )
    void create_ShouldThrowException_WhenUserIsAdmin(
            UserRole role
    ) {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(role);

        AdvertisementRequest request = new AdvertisementRequest(
                1L,
                1L,
                "Москва",
                "Ноутбук",
                "Игровой ноутбук",
                BigDecimal.valueOf(100000)
        );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        assertThrows(
                AdminCannotCreateAdvertisementException.class,
                () -> advertisementService.create(request)
        );

        verify(categoryRepository, never())
                .findActiveById(any());

        verify(regionRepository, never())
                .findById(any());

        verify(advertisementRepository, never())
                .save(any(Advertisement.class));
    }

    @Test
    void create_ShouldThrowException_WhenCategoryNotFound() {

        User seller = new User();
        seller.setId(1L);
        seller.setRole(UserRole.USER);

        AdvertisementRequest request = new AdvertisementRequest(
                1L,
                1L,
                "Москва",
                "Ноутбук",
                "Игровой ноутбук",
                BigDecimal.valueOf(100000)
        );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(categoryRepository.findActiveById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> advertisementService.create(request)
        );
    }

    @Test
    void create_ShouldThrowException_WhenRegionNotFound() {

        User seller = new User();
        seller.setId(1L);
        seller.setRole(UserRole.USER);

        Category category = new Category();
        category.setId(1L);

        AdvertisementRequest request = new AdvertisementRequest(
                1L,
                1L,
                "Москва",
                "Ноутбук",
                "Игровой ноутбук",
                BigDecimal.valueOf(100000)
        );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(categoryRepository.findActiveById(1L))
                .thenReturn(Optional.of(category));

        when(regionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RegionNotFoundException.class,
                () -> advertisementService.create(request)
        );
    }

    @Test
    void create_ShouldThrowException_WhenRateLimitExceeded() {

        User seller = new User();
        seller.setId(1L);
        seller.setRole(UserRole.USER);

        Category category = new Category();
        category.setId(1L);

        Region region = new Region();
        region.setId(1L);

        AdvertisementRequest request = new AdvertisementRequest(
                1L,
                1L,
                "Москва",
                "Ноутбук",
                "Игровой ноутбук",
                BigDecimal.valueOf(100000)
        );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(categoryRepository.findActiveById(1L))
                .thenReturn(Optional.of(category));

        when(regionRepository.findById(1L))
                .thenReturn(Optional.of(region));

        when(advertisementRepository.countBySellerIdAndCreatedAtAfter(
                eq(1L),
                any(OffsetDateTime.class)
        ))
                .thenReturn(3L);

        assertThrows(
                AdvertisementRateLimitExceededException.class,
                () -> advertisementService.create(request)
        );

        verify(advertisementRepository, never())
                .save(any(Advertisement.class));
    }

    @Test
    void update_ShouldUpdateAdvertisement() {

        User seller = new User();
        seller.setId(1L);
        seller.setRole(UserRole.USER);

        Category oldCategory = new Category();
        oldCategory.setId(1L);

        Category newCategory = new Category();
        newCategory.setId(2L);

        Region oldRegion = new Region();
        oldRegion.setId(1L);

        Region newRegion = new Region();
        newRegion.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setCategory(oldCategory);
        advertisement.setRegion(oldRegion);
        advertisement.setLocality("Москва");
        advertisement.setTitle("Старый ноутбук");
        advertisement.setDescription("Старое описание");
        advertisement.setPrice(BigDecimal.valueOf(100000));
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        AdvertisementUpdateRequest request = new AdvertisementUpdateRequest(
                2L,
                2L,
                "Орёл",
                "Новый ноутбук",
                "Новое описание",
                BigDecimal.valueOf(120000)
        );

        AdvertisementResponse expectedResponse = new AdvertisementResponse(
                1L,
                1L,
                2L,
                2L,
                "Орёл",
                "Новый ноутбук",
                "Новое описание",
                BigDecimal.valueOf(120000),
                AdvertisementStatus.ACTIVE,
                null,
                null
        );

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(categoryRepository.findActiveById(2L))
                .thenReturn(Optional.of(newCategory));

        when(regionRepository.findById(2L))
                .thenReturn(Optional.of(newRegion));

        when(advertisementRepository.update(advertisement))
                .thenReturn(advertisement);

        when(advertisementMapper.toResponse(advertisement))
                .thenReturn(expectedResponse);

        AdvertisementResponse result = advertisementService.update(
                1L,
                request
        );

        assertEquals(
                expectedResponse,
                result
        );

        assertEquals(
                newCategory,
                advertisement.getCategory()
        );

        assertEquals(
                newRegion,
                advertisement.getRegion()
        );

        assertEquals(
                "Орёл",
                advertisement.getLocality()
        );

        assertEquals(
                "Новый ноутбук",
                advertisement.getTitle()
        );

        assertEquals(
                "Новое описание",
                advertisement.getDescription()
        );

        assertEquals(
                BigDecimal.valueOf(120000),
                advertisement.getPrice()
        );

        assertNotNull(
                advertisement.getUpdatedAt()
        );

        verify(advertisementRepository)
                .update(advertisement);
    }

    @Test
    void update_ShouldUpdateOnlyProvidedFields() {

        User seller = new User();
        seller.setId(1L);
        seller.setRole(UserRole.USER);

        Category category = new Category();
        category.setId(1L);

        Region region = new Region();
        region.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setCategory(category);
        advertisement.setRegion(region);
        advertisement.setLocality("Москва");
        advertisement.setTitle("Старый ноутбук");
        advertisement.setDescription("Старое описание");
        advertisement.setPrice(BigDecimal.valueOf(100000));
        advertisement.setAdvertisementStatus(AdvertisementStatus.INACTIVE);

        AdvertisementUpdateRequest request = new AdvertisementUpdateRequest(
                null,
                null,
                null,
                "Новый ноутбук",
                null,
                null
        );

        AdvertisementResponse expectedResponse = new AdvertisementResponse(
                1L,
                1L,
                1L,
                1L,
                "Москва",
                "Новый ноутбук",
                "Старое описание",
                BigDecimal.valueOf(100000),
                AdvertisementStatus.INACTIVE,
                null,
                null
        );

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementRepository.update(advertisement))
                .thenReturn(advertisement);

        when(advertisementMapper.toResponse(advertisement))
                .thenReturn(expectedResponse);

        AdvertisementResponse result = advertisementService.update(
                1L,
                request
        );

        assertEquals(
                expectedResponse,
                result
        );

        assertEquals(
                category,
                advertisement.getCategory()
        );

        assertEquals(
                region,
                advertisement.getRegion()
        );

        assertEquals(
                "Москва",
                advertisement.getLocality()
        );

        assertEquals(
                "Новый ноутбук",
                advertisement.getTitle()
        );

        assertEquals(
                "Старое описание",
                advertisement.getDescription()
        );

        assertEquals(
                BigDecimal.valueOf(100000),
                advertisement.getPrice()
        );

        assertEquals(
                AdvertisementStatus.INACTIVE,
                advertisement.getAdvertisementStatus()
        );

        assertNotNull(
                advertisement.getUpdatedAt()
        );

        verify(categoryRepository, never())
                .findActiveById(any());

        verify(regionRepository, never())
                .findById(any());

        verify(advertisementRepository)
                .update(advertisement);
    }

    @Test
    void update_ShouldThrowException_WhenAdvertisementNotFound() {

        AdvertisementUpdateRequest request = new AdvertisementUpdateRequest(
                null,
                null,
                null,
                "Новый ноутбук",
                null,
                null
        );

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementService.update(
                        1L,
                        request
                )
        );

        verify(currentUserProvider, never())
                .getCurrentUser();

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void update_ShouldThrowException_WhenUserIsNotOwner() {

        User seller = new User();
        seller.setId(1L);

        User currentUser = new User();
        currentUser.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        AdvertisementUpdateRequest request = new AdvertisementUpdateRequest(
                null,
                null,
                null,
                "Новый ноутбук",
                null,
                null
        );

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        assertThrows(
                AdvertisementAccessDeniedException.class,
                () -> advertisementService.update(
                        1L,
                        request
                )
        );

        verify(categoryRepository, never())
                .findActiveById(any());

        verify(regionRepository, never())
                .findById(any());

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void update_ShouldThrowException_WhenAdvertisementStatusIsInvalid() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.BLOCKED);

        AdvertisementUpdateRequest request = new AdvertisementUpdateRequest(
                null,
                null,
                null,
                "Новый ноутбук",
                null,
                null
        );

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        assertThrows(
                InvalidAdvertisementStatusException.class,
                () -> advertisementService.update(
                        1L,
                        request
                )
        );

        verify(categoryRepository, never())
                .findActiveById(any());

        verify(regionRepository, never())
                .findById(any());

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void update_ShouldThrowException_WhenCategoryNotFound() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        AdvertisementUpdateRequest request = new AdvertisementUpdateRequest(
                2L,
                null,
                null,
                null,
                null,
                null
        );

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(categoryRepository.findActiveById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> advertisementService.update(
                        1L,
                        request
                )
        );

        verify(regionRepository, never())
                .findById(any());

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void update_ShouldThrowException_WhenRegionNotFound() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        AdvertisementUpdateRequest request = new AdvertisementUpdateRequest(
                null,
                2L,
                null,
                null,
                null,
                null
        );

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(regionRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                RegionNotFoundException.class,
                () -> advertisementService.update(
                        1L,
                        request
                )
        );

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void getById_ShouldReturnAdvertisement_WhenAdvertisementIsActive() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        AdvertisementResponse expectedResponse = new AdvertisementResponse(
                1L,
                1L,
                1L,
                1L,
                "Москва",
                "Ноутбук",
                "Игровой ноутбук",
                BigDecimal.valueOf(100000),
                AdvertisementStatus.ACTIVE,
                null,
                null
        );

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(advertisementMapper.toResponse(advertisement))
                .thenReturn(expectedResponse);

        AdvertisementResponse result =
                advertisementService.getById(1L);

        assertEquals(
                expectedResponse,
                result
        );

        verify(advertisementRepository)
                .findById(1L);

        verify(advertisementMapper)
                .toResponse(advertisement);
    }

    @ParameterizedTest
    @EnumSource(
            value = UserRole.class,
            names = {"ADMIN", "SUPER_ADMIN"}
    )
    void getById_ShouldReturnAdvertisement_WhenUserIsAdmin(
            UserRole role
    ) {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(role);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.INACTIVE);

        AdvertisementResponse expectedResponse = new AdvertisementResponse(
                1L,
                1L,
                1L,
                1L,
                "Москва",
                "Ноутбук",
                "Игровой ноутбук",
                BigDecimal.valueOf(100000),
                AdvertisementStatus.INACTIVE,
                null,
                null
        );

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        when(advertisementMapper.toResponse(advertisement))
                .thenReturn(expectedResponse);

        AdvertisementResponse result =
                advertisementService.getById(1L);

        assertEquals(
                expectedResponse,
                result
        );

        verify(advertisementMapper)
                .toResponse(advertisement);
    }

    @Test
    void getById_ShouldThrowException_WhenAdvertisementIsNotActiveAndUserIsNotAdmin() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.INACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        assertThrows(
                AdvertisementAccessDeniedException.class,
                () -> advertisementService.getById(1L)
        );

        verify(advertisementMapper, never())
                .toResponse(any(Advertisement.class));
    }

    @Test
    void getById_ShouldThrowException_WhenAdvertisementNotFound() {

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementService.getById(1L)
        );

        verify(currentUserProvider, never())
                .getCurrentUser();

        verify(advertisementMapper, never())
                .toResponse(any(Advertisement.class));
    }

    @Test
    void getSeller_ShouldReturnSeller_WhenAdvertisementIsActive() {

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        AdvertisementSellerResponse expectedResponse =
                new AdvertisementSellerResponse(
                        1L,
                        "Иван",
                        "Иванов"
                );

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(advertisementMapper.toSellerResponse(advertisement))
                .thenReturn(expectedResponse);

        AdvertisementSellerResponse result =
                advertisementService.getSeller(1L);

        assertEquals(
                expectedResponse,
                result
        );

        verify(advertisementRepository)
                .findById(1L);

        verify(advertisementMapper)
                .toSellerResponse(advertisement);
    }

    @Test
    void getSeller_ShouldThrowException_WhenAdvertisementIsNotActive() {

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.INACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        assertThrows(
                AdvertisementAccessDeniedException.class,
                () -> advertisementService.getSeller(1L)
        );

        verify(advertisementMapper, never())
                .toSellerResponse(any(Advertisement.class));
    }

    @Test
    void getSeller_ShouldThrowException_WhenAdvertisementNotFound() {

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementService.getSeller(1L)
        );

        verify(advertisementMapper, never())
                .toSellerResponse(any(Advertisement.class));
    }

    @Test
    void getSellerAdvertisements_ShouldReturnOnlyActiveAdvertisements_WhenUserIsNotAdmin() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);

        PageRequest pageRequest = new PageRequest(
                0,
                5
        );

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        AdvertisementResponse advertisementResponse = new AdvertisementResponse(
                1L,
                2L,
                1L,
                1L,
                "Москва",
                "Ноутбук",
                "Игровой ноутбук",
                BigDecimal.valueOf(100000),
                AdvertisementStatus.ACTIVE,
                null,
                null
        );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(advertisementRepository.findBySellerId(
                2L,
                AdvertisementStatus.ACTIVE,
                pageRequest
        ))
                .thenReturn(List.of(advertisement));

        when(advertisementMapper.toResponse(advertisement))
                .thenReturn(advertisementResponse);

        when(advertisementRepository.countBySellerId(
                2L,
                AdvertisementStatus.ACTIVE
        ))
                .thenReturn(11L);

        PageResponse<AdvertisementResponse> result =
                advertisementService.getSellerAdvertisements(
                        2L,
                        AdvertisementStatus.INACTIVE,
                        pageRequest
                );

        assertEquals(
                List.of(advertisementResponse),
                result.content()
        );

        assertEquals(
                0,
                result.page()
        );

        assertEquals(
                5,
                result.size()
        );

        assertEquals(
                11L,
                result.totalElements()
        );

        assertEquals(
                3,
                result.totalPages()
        );

        verify(advertisementRepository)
                .findBySellerId(
                        2L,
                        AdvertisementStatus.ACTIVE,
                        pageRequest
                );

        verify(advertisementRepository)
                .countBySellerId(
                        2L,
                        AdvertisementStatus.ACTIVE
                );

        verify(advertisementMapper)
                .toResponse(advertisement);
    }

    @ParameterizedTest
    @EnumSource(
            value = UserRole.class,
            names = {"ADMIN", "SUPER_ADMIN"}
    )
    void getSellerAdvertisements_ShouldUseRequestedStatus_WhenUserIsAdmin(
            UserRole role
    ) {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(role);

        PageRequest pageRequest = new PageRequest(
                0,
                10
        );

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.BLOCKED);

        AdvertisementResponse advertisementResponse = new AdvertisementResponse(
                1L,
                2L,
                1L,
                1L,
                "Москва",
                "Ноутбук",
                "Игровой ноутбук",
                BigDecimal.valueOf(100000),
                AdvertisementStatus.BLOCKED,
                null,
                null
        );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        when(advertisementRepository.findBySellerId(
                2L,
                AdvertisementStatus.BLOCKED,
                pageRequest
        ))
                .thenReturn(List.of(advertisement));

        when(advertisementMapper.toResponse(advertisement))
                .thenReturn(advertisementResponse);

        when(advertisementRepository.countBySellerId(
                2L,
                AdvertisementStatus.BLOCKED
        ))
                .thenReturn(1L);

        PageResponse<AdvertisementResponse> result =
                advertisementService.getSellerAdvertisements(
                        2L,
                        AdvertisementStatus.BLOCKED,
                        pageRequest
                );

        assertEquals(
                List.of(advertisementResponse),
                result.content()
        );

        assertEquals(
                1L,
                result.totalElements()
        );

        assertEquals(
                1,
                result.totalPages()
        );

        verify(advertisementRepository)
                .findBySellerId(
                        2L,
                        AdvertisementStatus.BLOCKED,
                        pageRequest
                );

        verify(advertisementRepository)
                .countBySellerId(
                        2L,
                        AdvertisementStatus.BLOCKED
                );
    }

    @Test
    void getSellerAdvertisements_ShouldUseNullStatus_WhenUserIsAdminAndStatusNotProvided() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(UserRole.ADMIN);

        PageRequest pageRequest = new PageRequest(
                0,
                10
        );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        when(advertisementRepository.findBySellerId(
                2L,
                null,
                pageRequest
        ))
                .thenReturn(List.of());

        when(advertisementRepository.countBySellerId(
                2L,
                null
        ))
                .thenReturn(0L);

        PageResponse<AdvertisementResponse> result =
                advertisementService.getSellerAdvertisements(
                        2L,
                        null,
                        pageRequest
                );

        assertEquals(
                List.of(),
                result.content()
        );

        assertEquals(
                0L,
                result.totalElements()
        );

        assertEquals(
                0,
                result.totalPages()
        );

        verify(advertisementRepository)
                .findBySellerId(
                        2L,
                        null,
                        pageRequest
                );

        verify(advertisementRepository)
                .countBySellerId(
                        2L,
                        null
                );
    }

    @Test
    void search_ShouldForceActiveStatus_WhenUserIsNotAdmin() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);

        PageRequest pageRequest = new PageRequest(
                0,
                5
        );

        AdvertisementFilter filter = new AdvertisementFilter(
                AdvertisementStatus.INACTIVE,
                List.of(1L),
                List.of(1L),
                "Москва",
                "Ноутбук",
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(150000),
                AdvertisementSortField.PRICE,
                SortDirection.ASC
        );

        AdvertisementFilter expectedFilter = new AdvertisementFilter(
                AdvertisementStatus.ACTIVE,
                List.of(1L),
                List.of(1L),
                "Москва",
                "Ноутбук",
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(150000),
                AdvertisementSortField.PRICE,
                SortDirection.ASC
        );

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        AdvertisementResponse advertisementResponse = new AdvertisementResponse(
                1L,
                2L,
                1L,
                1L,
                "Москва",
                "Ноутбук",
                "Игровой ноутбук",
                BigDecimal.valueOf(100000),
                AdvertisementStatus.ACTIVE,
                null,
                null
        );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(advertisementRepository.findByFilters(
                expectedFilter,
                pageRequest
        ))
                .thenReturn(List.of(advertisement));

        when(advertisementMapper.toResponse(advertisement))
                .thenReturn(advertisementResponse);

        when(advertisementRepository.countByFilters(expectedFilter))
                .thenReturn(11L);

        PageResponse<AdvertisementResponse> result =
                advertisementService.search(
                        filter,
                        pageRequest
                );

        assertEquals(
                List.of(advertisementResponse),
                result.content()
        );

        assertEquals(
                0,
                result.page()
        );

        assertEquals(
                5,
                result.size()
        );

        assertEquals(
                11L,
                result.totalElements()
        );

        assertEquals(
                3,
                result.totalPages()
        );

        verify(advertisementRepository)
                .findByFilters(
                        expectedFilter,
                        pageRequest
                );

        verify(advertisementRepository)
                .countByFilters(expectedFilter);

        verify(advertisementMapper)
                .toResponse(advertisement);
    }

    @ParameterizedTest
    @EnumSource(
            value = UserRole.class,
            names = {"ADMIN", "SUPER_ADMIN"}
    )
    void search_ShouldPreserveFilter_WhenUserIsAdmin(
            UserRole role
    ) {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(role);

        PageRequest pageRequest = new PageRequest(
                0,
                10
        );

        AdvertisementFilter filter = new AdvertisementFilter(
                AdvertisementStatus.BLOCKED,
                List.of(1L, 2L),
                List.of(1L),
                "Москва",
                "Ноутбук",
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(150000),
                AdvertisementSortField.CREATED_AT,
                SortDirection.DESC
        );

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.BLOCKED);

        AdvertisementResponse advertisementResponse = new AdvertisementResponse(
                1L,
                2L,
                1L,
                1L,
                "Москва",
                "Ноутбук",
                "Игровой ноутбук",
                BigDecimal.valueOf(100000),
                AdvertisementStatus.BLOCKED,
                null,
                null
        );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        when(advertisementRepository.findByFilters(
                filter,
                pageRequest
        ))
                .thenReturn(List.of(advertisement));

        when(advertisementMapper.toResponse(advertisement))
                .thenReturn(advertisementResponse);

        when(advertisementRepository.countByFilters(filter))
                .thenReturn(1L);

        PageResponse<AdvertisementResponse> result =
                advertisementService.search(
                        filter,
                        pageRequest
                );

        assertEquals(
                List.of(advertisementResponse),
                result.content()
        );

        assertEquals(
                1L,
                result.totalElements()
        );

        assertEquals(
                1,
                result.totalPages()
        );

        verify(advertisementRepository)
                .findByFilters(
                        filter,
                        pageRequest
                );

        verify(advertisementRepository)
                .countByFilters(filter);
    }

    @Test
    void activate_ShouldActivateAdvertisement() {

        User seller = new User();
        seller.setId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setActive(true);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setCategory(category);
        advertisement.setAdvertisementStatus(AdvertisementStatus.INACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        advertisementService.activate(1L);

        assertEquals(
                AdvertisementStatus.ACTIVE,
                advertisement.getAdvertisementStatus()
        );

        assertNotNull(
                advertisement.getUpdatedAt()
        );

        verify(advertisementRepository)
                .update(advertisement);
    }

    @Test
    void activate_ShouldThrowException_WhenAdvertisementNotFound() {

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementService.activate(1L)
        );

        verify(currentUserProvider, never())
                .getCurrentUser();

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void activate_ShouldThrowException_WhenUserIsNotOwner() {

        User seller = new User();
        seller.setId(1L);

        User currentUser = new User();
        currentUser.setId(2L);

        Category category = new Category();
        category.setId(1L);
        category.setActive(true);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setCategory(category);
        advertisement.setAdvertisementStatus(AdvertisementStatus.INACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        assertThrows(
                AdvertisementAccessDeniedException.class,
                () -> advertisementService.activate(1L)
        );

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void activate_ShouldThrowException_WhenAdvertisementStatusIsInvalid() {

        User seller = new User();
        seller.setId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setActive(true);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setCategory(category);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        assertThrows(
                InvalidAdvertisementStatusException.class,
                () -> advertisementService.activate(1L)
        );

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void activate_ShouldThrowException_WhenCategoryIsInactive() {

        User seller = new User();
        seller.setId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setActive(false);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setCategory(category);
        advertisement.setAdvertisementStatus(AdvertisementStatus.INACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        assertThrows(
                AdvertisementCategoryInactiveException.class,
                () -> advertisementService.activate(1L)
        );

        assertEquals(
                AdvertisementStatus.INACTIVE,
                advertisement.getAdvertisementStatus()
        );

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void deactivate_ShouldDeactivateAdvertisement() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        advertisementService.deactivate(1L);

        assertEquals(
                AdvertisementStatus.INACTIVE,
                advertisement.getAdvertisementStatus()
        );

        assertNotNull(
                advertisement.getUpdatedAt()
        );

        verify(advertisementRepository)
                .update(advertisement);
    }

    @Test
    void deactivate_ShouldThrowException_WhenAdvertisementNotFound() {

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementService.deactivate(1L)
        );

        verify(currentUserProvider, never())
                .getCurrentUser();

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void deactivate_ShouldThrowException_WhenUserIsNotOwner() {

        User seller = new User();
        seller.setId(1L);

        User currentUser = new User();
        currentUser.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        assertThrows(
                AdvertisementAccessDeniedException.class,
                () -> advertisementService.deactivate(1L)
        );

        assertEquals(
                AdvertisementStatus.ACTIVE,
                advertisement.getAdvertisementStatus()
        );

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void deactivate_ShouldThrowException_WhenAdvertisementStatusIsInvalid() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.INACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        assertThrows(
                InvalidAdvertisementStatusException.class,
                () -> advertisementService.deactivate(1L)
        );

        assertEquals(
                AdvertisementStatus.INACTIVE,
                advertisement.getAdvertisementStatus()
        );

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @ParameterizedTest
    @EnumSource(
            value = UserRole.class,
            names = {"ADMIN", "SUPER_ADMIN"}
    )
    void block_ShouldBlockActiveAdvertisement_WhenUserIsAdmin(
            UserRole role
    ) {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(role);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        advertisementService.block(1L);

        assertEquals(
                AdvertisementStatus.BLOCKED,
                advertisement.getAdvertisementStatus()
        );

        assertNotNull(
                advertisement.getUpdatedAt()
        );

        verify(advertisementRepository)
                .update(advertisement);
    }

    @Test
    void block_ShouldBlockInactiveAdvertisement_WhenUserIsAdmin() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(UserRole.ADMIN);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.INACTIVE);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        advertisementService.block(1L);

        assertEquals(
                AdvertisementStatus.BLOCKED,
                advertisement.getAdvertisementStatus()
        );

        assertNotNull(
                advertisement.getUpdatedAt()
        );

        verify(advertisementRepository)
                .update(advertisement);
    }

    @Test
    void block_ShouldThrowException_WhenUserIsNotAdmin() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        assertThrows(
                AdvertisementAccessDeniedException.class,
                () -> advertisementService.block(1L)
        );

        verify(advertisementRepository, never())
                .findById(any());

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void block_ShouldThrowException_WhenAdvertisementNotFound() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(UserRole.ADMIN);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementService.block(1L)
        );

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void block_ShouldThrowException_WhenAdvertisementStatusIsInvalid() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(UserRole.ADMIN);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.BLOCKED);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        assertThrows(
                InvalidAdvertisementStatusException.class,
                () -> advertisementService.block(1L)
        );

        assertEquals(
                AdvertisementStatus.BLOCKED,
                advertisement.getAdvertisementStatus()
        );

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @ParameterizedTest
    @EnumSource(
            value = UserRole.class,
            names = {"ADMIN", "SUPER_ADMIN"}
    )
    void unblock_ShouldUnblockAdvertisement_WhenUserIsAdmin(
            UserRole role
    ) {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(role);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.BLOCKED);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        advertisementService.unblock(1L);

        assertEquals(
                AdvertisementStatus.INACTIVE,
                advertisement.getAdvertisementStatus()
        );

        assertNotNull(
                advertisement.getUpdatedAt()
        );

        verify(advertisementRepository)
                .update(advertisement);
    }

    @Test
    void unblock_ShouldThrowException_WhenUserIsNotAdmin() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        assertThrows(
                AdvertisementAccessDeniedException.class,
                () -> advertisementService.unblock(1L)
        );

        verify(advertisementRepository, never())
                .findById(any());

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void unblock_ShouldThrowException_WhenAdvertisementNotFound() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(UserRole.ADMIN);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementService.unblock(1L)
        );

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void unblock_ShouldThrowException_WhenAdvertisementStatusIsInvalid() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(UserRole.ADMIN);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(admin);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        assertThrows(
                InvalidAdvertisementStatusException.class,
                () -> advertisementService.unblock(1L)
        );

        assertEquals(
                AdvertisementStatus.ACTIVE,
                advertisement.getAdvertisementStatus()
        );

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void delete_ShouldDeleteActiveAdvertisement() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        advertisementService.delete(1L);

        assertEquals(
                AdvertisementStatus.DELETED,
                advertisement.getAdvertisementStatus()
        );

        assertNotNull(
                advertisement.getUpdatedAt()
        );

        verify(advertisementRepository)
                .update(advertisement);
    }

    @Test
    void delete_ShouldDeleteInactiveAdvertisement() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.INACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        advertisementService.delete(1L);

        assertEquals(
                AdvertisementStatus.DELETED,
                advertisement.getAdvertisementStatus()
        );

        assertNotNull(
                advertisement.getUpdatedAt()
        );

        verify(advertisementRepository)
                .update(advertisement);
    }

    @Test
    void delete_ShouldThrowException_WhenAdvertisementNotFound() {

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementService.delete(1L)
        );

        verify(currentUserProvider, never())
                .getCurrentUser();

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void delete_ShouldThrowException_WhenUserIsNotOwner() {

        User seller = new User();
        seller.setId(1L);

        User currentUser = new User();
        currentUser.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        assertThrows(
                AdvertisementAccessDeniedException.class,
                () -> advertisementService.delete(1L)
        );

        assertEquals(
                AdvertisementStatus.ACTIVE,
                advertisement.getAdvertisementStatus()
        );

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void delete_ShouldThrowException_WhenAdvertisementStatusIsInvalid() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.BLOCKED);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        assertThrows(
                InvalidAdvertisementStatusException.class,
                () -> advertisementService.delete(1L)
        );

        assertEquals(
                AdvertisementStatus.BLOCKED,
                advertisement.getAdvertisementStatus()
        );

        verify(advertisementRepository, never())
                .update(any(Advertisement.class));
    }

    @Test
    void getAdvertisement_ShouldReturnAdvertisement() {

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        Advertisement result =
                advertisementService.getAdvertisement(1L);

        assertEquals(
                advertisement,
                result
        );

        verify(advertisementRepository)
                .findById(1L);
    }

    @Test
    void getAdvertisement_ShouldThrowException_WhenAdvertisementNotFound() {

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementService.getAdvertisement(1L)
        );

        verify(advertisementRepository)
                .findById(1L);
    }
}
