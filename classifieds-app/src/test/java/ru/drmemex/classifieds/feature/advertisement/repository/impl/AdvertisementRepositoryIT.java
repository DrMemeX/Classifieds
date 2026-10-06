package ru.drmemex.classifieds.feature.advertisement.repository.impl;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.filter.AdvertisementFilter;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementSortField;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.model.SortDirection;
import ru.drmemex.classifieds.feature.advertisement.repository.AdvertisementRepository;
import ru.drmemex.classifieds.feature.category.entity.Category;
import ru.drmemex.classifieds.feature.region.entity.Region;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.integration.AbstractIntegrationTest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdvertisementRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void save_ShouldSaveAdvertisement() {

        User seller = createUser(
                "advertisement-test-user"
        );

        Category category = createCategory(
                "Advertisement Test Category"
        );

        Region region = createRegion(
                "Advertisement Test Region"
        );

        OffsetDateTime createdAt =
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                );

        Advertisement advertisement = buildAdvertisement(
                seller,
                category,
                region,
                "Test Locality",
                "Test Advertisement",
                "Test Description",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                createdAt
        );

        Advertisement savedAdvertisement =
                advertisementRepository.save(advertisement);

        entityManager.flush();

        Long advertisementId = savedAdvertisement.getId();
        Long sellerId = seller.getId();
        Long categoryId = category.getId();
        Long regionId = region.getId();

        entityManager.clear();

        Advertisement actualAdvertisement = entityManager.find(
                Advertisement.class,
                advertisementId
        );

        assertNotNull(
                actualAdvertisement
        );

        assertEquals(
                sellerId,
                actualAdvertisement.getSeller().getId()
        );

        assertEquals(
                categoryId,
                actualAdvertisement.getCategory().getId()
        );

        assertEquals(
                regionId,
                actualAdvertisement.getRegion().getId()
        );

        assertEquals(
                "Test Locality",
                actualAdvertisement.getLocality()
        );

        assertEquals(
                "Test Advertisement",
                actualAdvertisement.getTitle()
        );

        assertEquals(
                "Test Description",
                actualAdvertisement.getDescription()
        );

        assertEquals(
                new BigDecimal("100000.00"),
                actualAdvertisement.getPrice()
        );

        assertEquals(
                AdvertisementStatus.ACTIVE,
                actualAdvertisement.getAdvertisementStatus()
        );

        assertEquals(
                createdAt.toInstant(),
                actualAdvertisement.getCreatedAt().toInstant()
        );

        assertNull(
                actualAdvertisement.getUpdatedAt()
        );
    }

    @Test
    void update_ShouldUpdateAdvertisement() {

        User seller = createUser(
                "advertisement-update-user"
        );

        Category category = createCategory(
                "Advertisement Update Category"
        );

        Region region = createRegion(
                "Advertisement Update Region"
        );

        Advertisement advertisement = createAdvertisement(
                seller,
                category,
                region,
                "Old Locality",
                "Old Advertisement",
                "Old Description",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        entityManager.flush();

        Long advertisementId = advertisement.getId();

        entityManager.clear();

        OffsetDateTime updatedAt =
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                );

        advertisement.setLocality("Updated Locality");
        advertisement.setTitle("Updated Advertisement");
        advertisement.setDescription("Updated Description");
        advertisement.setPrice(new BigDecimal("120000.00"));
        advertisement.setAdvertisementStatus(
                AdvertisementStatus.INACTIVE
        );
        advertisement.setUpdatedAt(updatedAt);

        Advertisement updatedAdvertisement =
                advertisementRepository.update(advertisement);

        entityManager.flush();
        entityManager.clear();

        Advertisement actualAdvertisement = entityManager.find(
                Advertisement.class,
                updatedAdvertisement.getId()
        );

        assertNotNull(
                actualAdvertisement
        );

        assertEquals(
                advertisementId,
                actualAdvertisement.getId()
        );

        assertEquals(
                "Updated Locality",
                actualAdvertisement.getLocality()
        );

        assertEquals(
                "Updated Advertisement",
                actualAdvertisement.getTitle()
        );

        assertEquals(
                "Updated Description",
                actualAdvertisement.getDescription()
        );

        assertEquals(
                new BigDecimal("120000.00"),
                actualAdvertisement.getPrice()
        );

        assertEquals(
                AdvertisementStatus.INACTIVE,
                actualAdvertisement.getAdvertisementStatus()
        );

        assertEquals(
                updatedAt.toInstant(),
                actualAdvertisement.getUpdatedAt().toInstant()
        );
    }

    @Test
    void findById_ShouldReturnAdvertisement() {

        User seller = createUser(
                "advertisement-find-user"
        );

        Category category = createCategory(
                "Advertisement Find Category"
        );

        Region region = createRegion(
                "Advertisement Find Region"
        );

        Advertisement advertisement = createAdvertisement(
                seller,
                category,
                region,
                "Find Advertisement",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        entityManager.flush();

        Long advertisementId = advertisement.getId();

        entityManager.clear();

        Optional<Advertisement> result =
                advertisementRepository.findById(advertisementId);

        assertTrue(
                result.isPresent()
        );

        Advertisement actualAdvertisement =
                result.orElseThrow();

        assertEquals(
                advertisementId,
                actualAdvertisement.getId()
        );

        assertEquals(
                "Find Advertisement",
                actualAdvertisement.getTitle()
        );

        assertEquals(
                AdvertisementStatus.ACTIVE,
                actualAdvertisement.getAdvertisementStatus()
        );
    }

    @Test
    void findById_ShouldReturnEmpty_WhenAdvertisementDoesNotExist() {

        Optional<Advertisement> result =
                advertisementRepository.findById(Long.MAX_VALUE);

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void findBySellerId_ShouldReturnAdvertisementsBySellerAndStatusSortedByCreatedAtAndId() {

        User seller = createUser(
                "advertisement-seller-user"
        );

        User anotherSeller = createUser(
                "another-advertisement-seller"
        );

        Category category = createCategory(
                "Advertisement Seller Category"
        );

        Region region = createRegion(
                "Advertisement Seller Region"
        );

        Advertisement olderAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Older Advertisement",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        Advertisement firstNewAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "First New Advertisement",
                new BigDecimal("110000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        Advertisement secondNewAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Second New Advertisement",
                new BigDecimal("120000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Inactive Advertisement",
                new BigDecimal("130000.00"),
                AdvertisementStatus.INACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T13:00:00Z"
                )
        );

        createAdvertisement(
                anotherSeller,
                category,
                region,
                "Another Seller Advertisement",
                new BigDecimal("140000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T14:00:00Z"
                )
        );

        entityManager.flush();

        Long sellerId = seller.getId();

        Long olderAdvertisementId =
                olderAdvertisement.getId();

        Long firstNewAdvertisementId =
                firstNewAdvertisement.getId();

        Long secondNewAdvertisementId =
                secondNewAdvertisement.getId();

        entityManager.clear();

        List<Advertisement> result =
                advertisementRepository.findBySellerId(
                        sellerId,
                        AdvertisementStatus.ACTIVE
                );

        assertEquals(
                3,
                result.size()
        );

        assertEquals(
                secondNewAdvertisementId,
                result.get(0).getId()
        );

        assertEquals(
                firstNewAdvertisementId,
                result.get(1).getId()
        );

        assertEquals(
                olderAdvertisementId,
                result.get(2).getId()
        );

        assertTrue(
                result.stream()
                        .allMatch(advertisement ->
                                advertisement.getSeller()
                                        .getId()
                                        .equals(sellerId)
                                        && advertisement.getAdvertisementStatus()
                                        == AdvertisementStatus.ACTIVE
                        )
        );
    }

    @Test
    void findBySellerId_ShouldReturnPageOfAdvertisements_WhenStatusIsSpecified() {

        User seller = createUser(
                "advertisement-page-status-user"
        );

        Category category = createCategory(
                "Advertisement Page Status Category"
        );

        Region region = createRegion(
                "Advertisement Page Status Region"
        );

        Advertisement oldAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Old Active Advertisement",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Middle Active Advertisement",
                new BigDecimal("110000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "New Active Advertisement",
                new BigDecimal("120000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Inactive Advertisement",
                new BigDecimal("130000.00"),
                AdvertisementStatus.INACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T13:00:00Z"
                )
        );

        entityManager.flush();

        Long sellerId = seller.getId();
        Long oldAdvertisementId = oldAdvertisement.getId();

        entityManager.clear();

        PageRequest pageRequest =
                new PageRequest(
                        1,
                        2
                );

        List<Advertisement> result =
                advertisementRepository.findBySellerId(
                        sellerId,
                        AdvertisementStatus.ACTIVE,
                        pageRequest
                );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                oldAdvertisementId,
                result.get(0).getId()
        );

        assertTrue(
                result.stream()
                        .allMatch(advertisement ->
                                advertisement.getSeller()
                                        .getId()
                                        .equals(sellerId)
                                        && advertisement.getAdvertisementStatus()
                                        == AdvertisementStatus.ACTIVE
                        )
        );
    }

    @Test
    void findBySellerId_ShouldReturnAdvertisementsOfAllStatuses_WhenStatusIsNull() {

        User seller = createUser(
                "advertisement-page-null-status-user"
        );

        Category category = createCategory(
                "Advertisement Page Null Status Category"
        );

        Region region = createRegion(
                "Advertisement Page Null Status Region"
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Active Advertisement",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        Advertisement inactiveAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Inactive Advertisement",
                new BigDecimal("110000.00"),
                AdvertisementStatus.INACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        Advertisement blockedAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Blocked Advertisement",
                new BigDecimal("120000.00"),
                AdvertisementStatus.BLOCKED,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        entityManager.flush();

        Long sellerId = seller.getId();
        Long inactiveAdvertisementId = inactiveAdvertisement.getId();
        Long blockedAdvertisementId = blockedAdvertisement.getId();

        entityManager.clear();

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        2
                );

        List<Advertisement> result =
                advertisementRepository.findBySellerId(
                        sellerId,
                        null,
                        pageRequest
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                blockedAdvertisementId,
                result.get(0).getId()
        );

        assertEquals(
                inactiveAdvertisementId,
                result.get(1).getId()
        );

        assertTrue(
                result.stream()
                        .allMatch(advertisement ->
                                advertisement.getSeller()
                                        .getId()
                                        .equals(sellerId)
                        )
        );
    }

    @Test
    void countBySellerId_ShouldCountAdvertisementsWithSpecifiedStatus() {

        User seller = createUser(
                "advertisement-count-status-user"
        );

        User anotherSeller = createUser(
                "another-advertisement-count-user"
        );

        Category category = createCategory(
                "Advertisement Count Status Category"
        );

        Region region = createRegion(
                "Advertisement Count Status Region"
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Active Advertisement One",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Active Advertisement Two",
                new BigDecimal("110000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Inactive Advertisement",
                new BigDecimal("120000.00"),
                AdvertisementStatus.INACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        createAdvertisement(
                anotherSeller,
                category,
                region,
                "Another Seller Advertisement",
                new BigDecimal("130000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T13:00:00Z"
                )
        );

        entityManager.flush();

        Long sellerId = seller.getId();

        entityManager.clear();

        long result =
                advertisementRepository.countBySellerId(
                        sellerId,
                        AdvertisementStatus.ACTIVE
                );

        assertEquals(
                2L,
                result
        );
    }

    @Test
    void countBySellerId_ShouldCountAllAdvertisements_WhenStatusIsNull() {

        User seller = createUser(
                "advertisement-count-null-status-user"
        );

        Category category = createCategory(
                "Advertisement Count Null Status Category"
        );

        Region region = createRegion(
                "Advertisement Count Null Status Region"
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Active Advertisement",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Inactive Advertisement",
                new BigDecimal("110000.00"),
                AdvertisementStatus.INACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Blocked Advertisement",
                new BigDecimal("120000.00"),
                AdvertisementStatus.BLOCKED,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        entityManager.flush();

        Long sellerId = seller.getId();

        entityManager.clear();

        long result =
                advertisementRepository.countBySellerId(
                        sellerId,
                        null
                );

        assertEquals(
                3L,
                result
        );
    }

    @Test
    void findByFilters_ShouldFilterByStatus() {

        User seller = createUser(
                "advertisement-filter-status-user"
        );

        Category category = createCategory(
                "Advertisement Filter Status Category"
        );

        Region region = createRegion(
                "Advertisement Filter Status Region"
        );

        Advertisement activeAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Active Filter Advertisement",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Inactive Filter Advertisement",
                new BigDecimal("110000.00"),
                AdvertisementStatus.INACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        entityManager.flush();

        Long activeAdvertisementId =
                activeAdvertisement.getId();

        entityManager.clear();

        AdvertisementFilter filter =
                new AdvertisementFilter(
                        AdvertisementStatus.ACTIVE,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        AdvertisementSortField.CREATED_AT,
                        SortDirection.DESC
                );

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        10
                );

        List<Advertisement> result =
                advertisementRepository.findByFilters(
                        filter,
                        pageRequest
                );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                activeAdvertisementId,
                result.get(0).getId()
        );

        assertEquals(
                AdvertisementStatus.ACTIVE,
                result.get(0).getAdvertisementStatus()
        );
    }

    @Test
    void findByFilters_ShouldFilterByCategoryIds() {

        User seller = createUser(
                "advertisement-filter-category-user"
        );

        Category categoryA = createCategory(
                "Advertisement Filter Category A"
        );

        Category categoryB = createCategory(
                "Advertisement Filter Category B"
        );

        Category categoryC = createCategory(
                "Advertisement Filter Category C"
        );

        Region region = createRegion(
                "Advertisement Filter Category Region"
        );

        createAdvertisement(
                seller,
                categoryA,
                region,
                "Advertisement Category A",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                categoryB,
                region,
                "Advertisement Category B",
                new BigDecimal("110000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                categoryC,
                region,
                "Advertisement Category C",
                new BigDecimal("120000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        entityManager.flush();

        Long categoryAId = categoryA.getId();
        Long categoryBId = categoryB.getId();

        entityManager.clear();

        AdvertisementFilter filter =
                new AdvertisementFilter(
                        null,
                        List.of(
                                categoryAId,
                                categoryBId
                        ),
                        null,
                        null,
                        null,
                        null,
                        null,
                        AdvertisementSortField.CREATED_AT,
                        SortDirection.ASC
                );

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        10
                );

        List<Advertisement> result =
                advertisementRepository.findByFilters(
                        filter,
                        pageRequest
                );

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                result.stream()
                        .allMatch(advertisement ->
                                advertisement.getCategory()
                                        .getId()
                                        .equals(categoryAId)
                                        || advertisement.getCategory()
                                        .getId()
                                        .equals(categoryBId)
                        )
        );

        assertEquals(
                categoryAId,
                result.get(0).getCategory().getId()
        );

        assertEquals(
                categoryBId,
                result.get(1).getCategory().getId()
        );
    }

    @Test
    void findByFilters_ShouldFilterByRegionIds() {

        User seller = createUser(
                "advertisement-filter-region-user"
        );

        Category category = createCategory(
                "Advertisement Filter Region Category"
        );

        Region regionA = createRegion(
                "Advertisement Filter Region A"
        );

        Region regionB = createRegion(
                "Advertisement Filter Region B"
        );

        Region regionC = createRegion(
                "Advertisement Filter Region C"
        );

        createAdvertisement(
                seller,
                category,
                regionA,
                "Advertisement Region A",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                regionB,
                "Advertisement Region B",
                new BigDecimal("110000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                regionC,
                "Advertisement Region C",
                new BigDecimal("120000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        entityManager.flush();

        Long regionAId = regionA.getId();
        Long regionBId = regionB.getId();

        entityManager.clear();

        AdvertisementFilter filter =
                new AdvertisementFilter(
                        null,
                        null,
                        List.of(
                                regionAId,
                                regionBId
                        ),
                        null,
                        null,
                        null,
                        null,
                        AdvertisementSortField.CREATED_AT,
                        SortDirection.ASC
                );

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        10
                );

        List<Advertisement> result =
                advertisementRepository.findByFilters(
                        filter,
                        pageRequest
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                regionAId,
                result.get(0).getRegion().getId()
        );

        assertEquals(
                regionBId,
                result.get(1).getRegion().getId()
        );

        assertTrue(
                result.stream()
                        .allMatch(advertisement ->
                                advertisement.getRegion()
                                        .getId()
                                        .equals(regionAId)
                                        || advertisement.getRegion()
                                        .getId()
                                        .equals(regionBId)
                        )
        );
    }

    @Test
    void findByFilters_ShouldFilterByLocalityUsingFuzzySearch() {

        User seller = createUser(
                "advertisement-filter-locality-user"
        );

        Category category = createCategory(
                "Advertisement Filter Locality Category"
        );

        Region region = createRegion(
                "Advertisement Filter Locality Region"
        );

        Advertisement moscowAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Москва",
                "Moscow Advertisement",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Владивосток",
                "Another Advertisement",
                new BigDecimal("110000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        entityManager.flush();

        Long moscowAdvertisementId =
                moscowAdvertisement.getId();

        entityManager.clear();

        AdvertisementFilter filter =
                new AdvertisementFilter(
                        null,
                        null,
                        null,
                        "  МАСКВА  ",
                        null,
                        null,
                        null,
                        AdvertisementSortField.CREATED_AT,
                        SortDirection.ASC
                );

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        10
                );

        List<Advertisement> result =
                advertisementRepository.findByFilters(
                        filter,
                        pageRequest
                );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                moscowAdvertisementId,
                result.get(0).getId()
        );

        assertEquals(
                "Москва",
                result.get(0).getLocality()
        );
    }

    @Test
    void findByFilters_ShouldFilterByTitleUsingFuzzySearchAndSortBySimilarity() {

        User seller = createUser(
                "advertisement-filter-title-user"
        );

        Category category = createCategory(
                "Advertisement Filter Title Category"
        );

        Region region = createRegion(
                "Advertisement Filter Title Region"
        );

        Advertisement firstAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Игровой ноутбук",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        Advertisement secondAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Игровой ноутбук Lenovo",
                new BigDecimal("110000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Холодильник",
                new BigDecimal("120000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        entityManager.flush();

        Long firstAdvertisementId =
                firstAdvertisement.getId();

        Long secondAdvertisementId =
                secondAdvertisement.getId();

        entityManager.clear();

        AdvertisementFilter filter =
                new AdvertisementFilter(
                        null,
                        null,
                        null,
                        null,
                        "игровой ноутбк",
                        null,
                        null,
                        AdvertisementSortField.CREATED_AT,
                        SortDirection.DESC
                );

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        10
                );

        List<Advertisement> result =
                advertisementRepository.findByFilters(
                        filter,
                        pageRequest
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                firstAdvertisementId,
                result.get(0).getId()
        );

        assertEquals(
                secondAdvertisementId,
                result.get(1).getId()
        );
    }

    @Test
    void findByFilters_ShouldFilterByPriceRangeIncludingBoundaries() {

        User seller = createUser(
                "advertisement-filter-price-user"
        );

        Category category = createCategory(
                "Advertisement Filter Price Category"
        );

        Region region = createRegion(
                "Advertisement Filter Price Region"
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Min Price Advertisement",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Max Price Advertisement",
                new BigDecimal("200000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Expensive Advertisement",
                new BigDecimal("300000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        entityManager.flush();
        entityManager.clear();

        AdvertisementFilter filter =
                new AdvertisementFilter(
                        null,
                        null,
                        null,
                        null,
                        null,
                        new BigDecimal("100000.00"),
                        new BigDecimal("200000.00"),
                        AdvertisementSortField.PRICE,
                        SortDirection.ASC
                );

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        10
                );

        List<Advertisement> result =
                advertisementRepository.findByFilters(
                        filter,
                        pageRequest
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                new BigDecimal("100000.00"),
                result.get(0).getPrice()
        );

        assertEquals(
                new BigDecimal("200000.00"),
                result.get(1).getPrice()
        );
    }

    @Test
    void findByFilters_ShouldApplyMultipleFiltersTogether() {

        User seller = createUser(
                "advertisement-filter-combined-user"
        );

        Category matchingCategory = createCategory(
                "Advertisement Combined Matching Category"
        );

        Category anotherCategory = createCategory(
                "Advertisement Combined Another Category"
        );

        Region matchingRegion = createRegion(
                "Advertisement Combined Matching Region"
        );

        Region anotherRegion = createRegion(
                "Advertisement Combined Another Region"
        );

        Advertisement matchingAdvertisement = createAdvertisement(
                seller,
                matchingCategory,
                matchingRegion,
                "Москва",
                "Matching Advertisement",
                new BigDecimal("150000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                matchingCategory,
                matchingRegion,
                "Москва",
                "Wrong Status Advertisement",
                new BigDecimal("150000.00"),
                AdvertisementStatus.INACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                anotherCategory,
                matchingRegion,
                "Москва",
                "Wrong Category Advertisement",
                new BigDecimal("150000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                matchingCategory,
                anotherRegion,
                "Москва",
                "Wrong Region Advertisement",
                new BigDecimal("150000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T13:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                matchingCategory,
                matchingRegion,
                "Москва",
                "Wrong Price Advertisement",
                new BigDecimal("300000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T14:00:00Z"
                )
        );

        entityManager.flush();

        Long matchingAdvertisementId =
                matchingAdvertisement.getId();

        Long matchingCategoryId =
                matchingCategory.getId();

        Long matchingRegionId =
                matchingRegion.getId();

        entityManager.clear();

        AdvertisementFilter filter =
                new AdvertisementFilter(
                        AdvertisementStatus.ACTIVE,
                        List.of(
                                matchingCategoryId
                        ),
                        List.of(
                                matchingRegionId
                        ),
                        null,
                        null,
                        new BigDecimal("100000.00"),
                        new BigDecimal("200000.00"),
                        AdvertisementSortField.CREATED_AT,
                        SortDirection.ASC
                );

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        10
                );

        List<Advertisement> result =
                advertisementRepository.findByFilters(
                        filter,
                        pageRequest
                );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                matchingAdvertisementId,
                result.get(0).getId()
        );
    }

    @Test
    void findByFilters_ShouldSortByPriceAscAndApplyPagination() {

        User seller = createUser(
                "advertisement-filter-price-sort-user"
        );

        Category category = createCategory(
                "Advertisement Filter Price Sort Category"
        );

        Region region = createRegion(
                "Advertisement Filter Price Sort Region"
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Advertisement 400",
                new BigDecimal("400000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Advertisement 100",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Advertisement 300",
                new BigDecimal("300000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Advertisement 200",
                new BigDecimal("200000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T13:00:00Z"
                )
        );

        entityManager.flush();
        entityManager.clear();

        AdvertisementFilter filter =
                new AdvertisementFilter(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        AdvertisementSortField.PRICE,
                        SortDirection.ASC
                );

        PageRequest pageRequest =
                new PageRequest(
                        1,
                        2
                );

        List<Advertisement> result =
                advertisementRepository.findByFilters(
                        filter,
                        pageRequest
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                new BigDecimal("300000.00"),
                result.get(0).getPrice()
        );

        assertEquals(
                new BigDecimal("400000.00"),
                result.get(1).getPrice()
        );
    }

    @Test
    void findByFilters_ShouldSortByCreatedAtDescAndIdDesc() {

        User seller = createUser(
                "advertisement-filter-created-sort-user"
        );

        Category category = createCategory(
                "Advertisement Filter Created Sort Category"
        );

        Region region = createRegion(
                "Advertisement Filter Created Sort Region"
        );

        Advertisement olderAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Older Advertisement",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        Advertisement firstNewAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "First New Advertisement",
                new BigDecimal("110000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        Advertisement secondNewAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Second New Advertisement",
                new BigDecimal("120000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        entityManager.flush();

        Long olderAdvertisementId =
                olderAdvertisement.getId();

        Long firstNewAdvertisementId =
                firstNewAdvertisement.getId();

        Long secondNewAdvertisementId =
                secondNewAdvertisement.getId();

        entityManager.clear();

        AdvertisementFilter filter =
                new AdvertisementFilter(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        AdvertisementSortField.CREATED_AT,
                        SortDirection.DESC
                );

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        10
                );

        List<Advertisement> result =
                advertisementRepository.findByFilters(
                        filter,
                        pageRequest
                );

        assertEquals(
                3,
                result.size()
        );

        assertEquals(
                secondNewAdvertisementId,
                result.get(0).getId()
        );

        assertEquals(
                firstNewAdvertisementId,
                result.get(1).getId()
        );

        assertEquals(
                olderAdvertisementId,
                result.get(2).getId()
        );
    }

    @Test
    void findByFilters_ShouldIgnoreEmptyAndBlankFilters() {

        User seller = createUser(
                "advertisement-filter-empty-user"
        );

        Category category = createCategory(
                "Advertisement Filter Empty Category"
        );

        Region region = createRegion(
                "Advertisement Filter Empty Region"
        );

        Advertisement firstAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Moscow",
                "First Advertisement",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        Advertisement secondAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Orel",
                "Second Advertisement",
                new BigDecimal("110000.00"),
                AdvertisementStatus.INACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        entityManager.flush();

        Long firstAdvertisementId =
                firstAdvertisement.getId();

        Long secondAdvertisementId =
                secondAdvertisement.getId();

        entityManager.clear();

        AdvertisementFilter filter =
                new AdvertisementFilter(
                        null,
                        List.of(),
                        List.of(),
                        "   ",
                        "   ",
                        null,
                        null,
                        AdvertisementSortField.CREATED_AT,
                        SortDirection.ASC
                );

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        10
                );

        List<Advertisement> result =
                advertisementRepository.findByFilters(
                        filter,
                        pageRequest
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                firstAdvertisementId,
                result.get(0).getId()
        );

        assertEquals(
                secondAdvertisementId,
                result.get(1).getId()
        );
    }

    @Test
    void countByFilters_ShouldCountAllAdvertisements_WhenFilterHasNoConditions() {

        User seller = createUser(
                "advertisement-count-filter-all-user"
        );

        Category category = createCategory(
                "Advertisement Count Filter All Category"
        );

        Region region = createRegion(
                "Advertisement Count Filter All Region"
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Moscow",
                "First Advertisement",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Orel",
                "Second Advertisement",
                new BigDecimal("200000.00"),
                AdvertisementStatus.INACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Kursk",
                "Third Advertisement",
                new BigDecimal("300000.00"),
                AdvertisementStatus.BLOCKED,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        entityManager.flush();
        entityManager.clear();

        AdvertisementFilter filter =
                new AdvertisementFilter(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        AdvertisementSortField.CREATED_AT,
                        SortDirection.DESC
                );

        long result =
                advertisementRepository.countByFilters(filter);

        assertEquals(
                3L,
                result
        );
    }

    @Test
    void countByFilters_ShouldCountAdvertisementsMatchingCombinedFilters() {

        User seller = createUser(
                "advertisement-count-filter-combined-user"
        );

        Category matchingCategory = createCategory(
                "Advertisement Count Matching Category"
        );

        Category anotherCategory = createCategory(
                "Advertisement Count Another Category"
        );

        Region matchingRegion = createRegion(
                "Advertisement Count Matching Region"
        );

        Region anotherRegion = createRegion(
                "Advertisement Count Another Region"
        );

        createAdvertisement(
                seller,
                matchingCategory,
                matchingRegion,
                "Moscow",
                "Matching Advertisement",
                new BigDecimal("150000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                matchingCategory,
                matchingRegion,
                "Moscow",
                "Wrong Status Advertisement",
                new BigDecimal("150000.00"),
                AdvertisementStatus.INACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                anotherCategory,
                matchingRegion,
                "Moscow",
                "Wrong Category Advertisement",
                new BigDecimal("150000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                matchingCategory,
                anotherRegion,
                "Moscow",
                "Wrong Region Advertisement",
                new BigDecimal("150000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T13:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                matchingCategory,
                matchingRegion,
                "Moscow",
                "Too Cheap Advertisement",
                new BigDecimal("50000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T14:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                matchingCategory,
                matchingRegion,
                "Moscow",
                "Too Expensive Advertisement",
                new BigDecimal("300000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T15:00:00Z"
                )
        );

        entityManager.flush();

        Long matchingCategoryId =
                matchingCategory.getId();

        Long matchingRegionId =
                matchingRegion.getId();

        entityManager.clear();

        AdvertisementFilter filter =
                new AdvertisementFilter(
                        AdvertisementStatus.ACTIVE,
                        List.of(
                                matchingCategoryId
                        ),
                        List.of(
                                matchingRegionId
                        ),
                        null,
                        null,
                        new BigDecimal("100000.00"),
                        new BigDecimal("200000.00"),
                        AdvertisementSortField.CREATED_AT,
                        SortDirection.DESC
                );

        long result =
                advertisementRepository.countByFilters(filter);

        assertEquals(
                1L,
                result
        );
    }

    @Test
    void countBySellerIdAndCreatedAtAfter_ShouldCountAdvertisementsFromSpecifiedDateIncludingBoundary() {

        User seller = createUser(
                "advertisement-count-created-after-user"
        );

        Category category = createCategory(
                "Advertisement Count Created After Category"
        );

        Region region = createRegion(
                "Advertisement Count Created After Region"
        );

        OffsetDateTime createdAfter =
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                );

        createAdvertisement(
                seller,
                category,
                region,
                "Older Advertisement",
                new BigDecimal("100000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Boundary Advertisement",
                new BigDecimal("110000.00"),
                AdvertisementStatus.ACTIVE,
                createdAfter
        );

        createAdvertisement(
                seller,
                category,
                region,
                "Newer Advertisement",
                new BigDecimal("120000.00"),
                AdvertisementStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-24T12:00:00Z"
                )
        );

        entityManager.flush();

        Long sellerId = seller.getId();

        entityManager.clear();

        long result =
                advertisementRepository.countBySellerIdAndCreatedAtAfter(
                        sellerId,
                        createdAfter
                );

        assertEquals(
                2L,
                result
        );
    }

    private User createUser(String login) {

        User user = User.builder()
                .login(login)
                .password("password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(
                        OffsetDateTime.parse(
                                "2026-09-24T09:00:00Z"
                        )
                )
                .build();

        entityManager.persist(user);

        return user;
    }

    private Category createCategory(String name) {

        Category category = Category.builder()
                .name(name)
                .active(true)
                .build();

        entityManager.persist(category);

        return category;
    }

    private Region createRegion(String name) {

        Region region = Region.builder()
                .name(name)
                .build();

        entityManager.persist(region);

        return region;
    }

    private Advertisement createAdvertisement(
            User seller,
            Category category,
            Region region,
            String title,
            BigDecimal price,
            AdvertisementStatus advertisementStatus,
            OffsetDateTime createdAt
    ) {

        return createAdvertisement(
                seller,
                category,
                region,
                "Test Locality",
                title,
                price,
                advertisementStatus,
                createdAt
        );
    }

    private Advertisement createAdvertisement(
            User seller,
            Category category,
            Region region,
            String locality,
            String title,
            BigDecimal price,
            AdvertisementStatus advertisementStatus,
            OffsetDateTime createdAt
    ) {

        return createAdvertisement(
                seller,
                category,
                region,
                locality,
                title,
                "Test Description",
                price,
                advertisementStatus,
                createdAt
        );
    }

    private Advertisement createAdvertisement(
            User seller,
            Category category,
            Region region,
            String locality,
            String title,
            String description,
            BigDecimal price,
            AdvertisementStatus advertisementStatus,
            OffsetDateTime createdAt
    ) {

        Advertisement advertisement = buildAdvertisement(
                seller,
                category,
                region,
                locality,
                title,
                description,
                price,
                advertisementStatus,
                createdAt
        );

        entityManager.persist(advertisement);

        return advertisement;
    }

    private Advertisement buildAdvertisement(
            User seller,
            Category category,
            Region region,
            String locality,
            String title,
            String description,
            BigDecimal price,
            AdvertisementStatus advertisementStatus,
            OffsetDateTime createdAt
    ) {

        return Advertisement.builder()
                .seller(seller)
                .category(category)
                .region(region)
                .locality(locality)
                .title(title)
                .description(description)
                .price(price)
                .advertisementStatus(advertisementStatus)
                .createdAt(createdAt)
                .build();
    }
}