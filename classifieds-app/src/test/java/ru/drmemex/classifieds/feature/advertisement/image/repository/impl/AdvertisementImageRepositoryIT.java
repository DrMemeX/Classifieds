package ru.drmemex.classifieds.feature.advertisement.image.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.image.entity.AdvertisementImage;
import ru.drmemex.classifieds.feature.advertisement.image.repository.AdvertisementImageRepository;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
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

class AdvertisementImageRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private AdvertisementImageRepository advertisementImageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void save_ShouldSaveAdvertisementImage() {

        User seller = createUser(
                "advertisement-image-save-user"
        );

        Category category = createCategory(
                "Advertisement Image Save Category"
        );

        Region region = createRegion(
                "Advertisement Image Save Region"
        );

        Advertisement advertisement = createAdvertisement(
                seller,
                category,
                region,
                "Advertisement Image Save",
                new BigDecimal("100000.00"),
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        AdvertisementImage image = buildAdvertisementImage(
                advertisement,
                "advertisements/test/image-1.jpg",
                (short) 1
        );

        AdvertisementImage savedImage =
                advertisementImageRepository.save(image);

        entityManager.flush();

        Long imageId = savedImage.getId();
        Long advertisementId = advertisement.getId();

        entityManager.clear();

        AdvertisementImage actualImage = entityManager.find(
                AdvertisementImage.class,
                imageId
        );

        assertNotNull(
                actualImage
        );

        assertEquals(
                advertisementId,
                actualImage.getAdvertisement().getId()
        );

        assertEquals(
                "advertisements/test/image-1.jpg",
                actualImage.getObjectKey()
        );

        assertEquals(
                (short) 1,
                actualImage.getDisplayOrder()
        );
    }

    @Test
    void findById_ShouldReturnAdvertisementImage() {

        User seller = createUser(
                "advertisement-image-find-user"
        );

        Category category = createCategory(
                "Advertisement Image Find Category"
        );

        Region region = createRegion(
                "Advertisement Image Find Region"
        );

        Advertisement advertisement = createAdvertisement(
                seller,
                category,
                region,
                "Advertisement Image Find",
                new BigDecimal("100000.00"),
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        AdvertisementImage image = createAdvertisementImage(
                advertisement,
                "advertisements/test/find-image.jpg",
                (short) 1
        );

        entityManager.flush();

        Long imageId = image.getId();

        entityManager.clear();

        Optional<AdvertisementImage> result =
                advertisementImageRepository.findById(imageId);

        assertTrue(
                result.isPresent()
        );

        AdvertisementImage actualImage =
                result.orElseThrow();

        assertEquals(
                imageId,
                actualImage.getId()
        );

        assertEquals(
                "advertisements/test/find-image.jpg",
                actualImage.getObjectKey()
        );

        assertEquals(
                (short) 1,
                actualImage.getDisplayOrder()
        );
    }

    @Test
    void findById_ShouldReturnEmpty_WhenAdvertisementImageDoesNotExist() {

        Optional<AdvertisementImage> result =
                advertisementImageRepository.findById(Long.MAX_VALUE);

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void findByAdvertisementId_ShouldReturnImagesOfAdvertisementSortedByDisplayOrder() {

        User seller = createUser(
                "advertisement-image-list-user"
        );

        Category category = createCategory(
                "Advertisement Image List Category"
        );

        Region region = createRegion(
                "Advertisement Image List Region"
        );

        Advertisement advertisement = createAdvertisement(
                seller,
                category,
                region,
                "Advertisement With Images",
                new BigDecimal("100000.00"),
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        Advertisement anotherAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Another Advertisement With Images",
                new BigDecimal("110000.00"),
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        AdvertisementImage secondImage = createAdvertisementImage(
                advertisement,
                "advertisements/test/image-2.jpg",
                (short) 2
        );

        AdvertisementImage firstImage = createAdvertisementImage(
                advertisement,
                "advertisements/test/image-1.jpg",
                (short) 1
        );

        AdvertisementImage thirdImage = createAdvertisementImage(
                advertisement,
                "advertisements/test/image-3.jpg",
                (short) 3
        );

        createAdvertisementImage(
                anotherAdvertisement,
                "advertisements/test/another-image.jpg",
                (short) 1
        );

        entityManager.flush();

        Long advertisementId = advertisement.getId();

        Long firstImageId = firstImage.getId();
        Long secondImageId = secondImage.getId();
        Long thirdImageId = thirdImage.getId();

        entityManager.clear();

        List<AdvertisementImage> result =
                advertisementImageRepository.findByAdvertisementId(
                        advertisementId
                );

        assertEquals(
                3,
                result.size()
        );

        assertEquals(
                firstImageId,
                result.get(0).getId()
        );

        assertEquals(
                secondImageId,
                result.get(1).getId()
        );

        assertEquals(
                thirdImageId,
                result.get(2).getId()
        );

        assertTrue(
                result.stream()
                        .allMatch(image ->
                                image.getAdvertisement()
                                        .getId()
                                        .equals(advertisementId)
                        )
        );
    }

    @Test
    void countByAdvertisementId_ShouldCountImagesOfSpecifiedAdvertisement() {

        User seller = createUser(
                "advertisement-image-count-user"
        );

        Category category = createCategory(
                "Advertisement Image Count Category"
        );

        Region region = createRegion(
                "Advertisement Image Count Region"
        );

        Advertisement advertisement = createAdvertisement(
                seller,
                category,
                region,
                "Advertisement Image Count",
                new BigDecimal("100000.00"),
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        Advertisement anotherAdvertisement = createAdvertisement(
                seller,
                category,
                region,
                "Another Advertisement Image Count",
                new BigDecimal("110000.00"),
                OffsetDateTime.parse(
                        "2026-09-24T11:00:00Z"
                )
        );

        createAdvertisementImage(
                advertisement,
                "advertisements/test/count-image-1.jpg",
                (short) 1
        );

        createAdvertisementImage(
                advertisement,
                "advertisements/test/count-image-2.jpg",
                (short) 2
        );

        createAdvertisementImage(
                anotherAdvertisement,
                "advertisements/test/count-another-image.jpg",
                (short) 1
        );

        entityManager.flush();

        Long advertisementId = advertisement.getId();

        entityManager.clear();

        long result =
                advertisementImageRepository.countByAdvertisementId(
                        advertisementId
                );

        assertEquals(
                2L,
                result
        );
    }

    @Test
    void delete_ShouldDeleteAdvertisementImage() {

        User seller = createUser(
                "advertisement-image-delete-user"
        );

        Category category = createCategory(
                "Advertisement Image Delete Category"
        );

        Region region = createRegion(
                "Advertisement Image Delete Region"
        );

        Advertisement advertisement = createAdvertisement(
                seller,
                category,
                region,
                "Advertisement Image Delete",
                new BigDecimal("100000.00"),
                OffsetDateTime.parse(
                        "2026-09-24T10:00:00Z"
                )
        );

        AdvertisementImage image = createAdvertisementImage(
                advertisement,
                "advertisements/test/delete-image.jpg",
                (short) 1
        );

        entityManager.flush();

        Long imageId = image.getId();

        advertisementImageRepository.delete(image);

        entityManager.flush();
        entityManager.clear();

        AdvertisementImage actualImage = entityManager.find(
                AdvertisementImage.class,
                imageId
        );

        assertNull(
                actualImage
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
            OffsetDateTime createdAt
    ) {

        Advertisement advertisement = Advertisement.builder()
                .seller(seller)
                .category(category)
                .region(region)
                .locality("Test Locality")
                .title(title)
                .description("Test Description")
                .price(price)
                .advertisementStatus(AdvertisementStatus.ACTIVE)
                .createdAt(createdAt)
                .build();

        entityManager.persist(advertisement);

        return advertisement;
    }

    private AdvertisementImage createAdvertisementImage(
            Advertisement advertisement,
            String objectKey,
            short displayOrder
    ) {

        AdvertisementImage image = buildAdvertisementImage(
                advertisement,
                objectKey,
                displayOrder
        );

        entityManager.persist(image);

        return image;
    }

    private AdvertisementImage buildAdvertisementImage(
            Advertisement advertisement,
            String objectKey,
            short displayOrder
    ) {

        return AdvertisementImage.builder()
                .advertisement(advertisement)
                .objectKey(objectKey)
                .displayOrder(displayOrder)
                .build();
    }
}