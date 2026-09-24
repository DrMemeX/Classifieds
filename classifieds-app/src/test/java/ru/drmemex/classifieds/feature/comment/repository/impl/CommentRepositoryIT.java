package ru.drmemex.classifieds.feature.comment.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.category.entity.Category;
import ru.drmemex.classifieds.feature.comment.entity.Comment;
import ru.drmemex.classifieds.feature.comment.repository.CommentRepository;
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

class CommentRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private CommentRepository commentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void save_ShouldSaveComment() {

        User seller = createUser(
                "comment-save-seller"
        );

        User author = createUser(
                "comment-save-author"
        );

        Category category = createCategory(
                "Comment Save Category"
        );

        Region region = createRegion(
                "Comment Save Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Comment Save Advertisement",
                seller,
                category,
                region
        );

        OffsetDateTime createdAt =
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                );

        Comment comment = buildComment(
                advertisement,
                author,
                "Test comment",
                createdAt
        );

        Comment savedComment =
                commentRepository.save(comment);

        entityManager.flush();

        Long commentId =
                savedComment.getId();

        Long advertisementId =
                advertisement.getId();

        Long authorId =
                author.getId();

        entityManager.clear();

        Comment actualComment = entityManager.find(
                Comment.class,
                commentId
        );

        assertNotNull(
                actualComment
        );

        assertEquals(
                advertisementId,
                actualComment.getAdvertisement().getId()
        );

        assertEquals(
                authorId,
                actualComment.getAuthor().getId()
        );

        assertEquals(
                "Test comment",
                actualComment.getText()
        );

        assertEquals(
                createdAt.toInstant(),
                actualComment.getCreatedAt().toInstant()
        );
    }

    @Test
    void findById_ShouldReturnComment() {

        User seller = createUser(
                "comment-find-seller"
        );

        User author = createUser(
                "comment-find-author"
        );

        Category category = createCategory(
                "Comment Find Category"
        );

        Region region = createRegion(
                "Comment Find Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Comment Find Advertisement",
                seller,
                category,
                region
        );

        Comment comment = createComment(
                advertisement,
                author,
                "Comment for find",
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        entityManager.flush();

        Long commentId =
                comment.getId();

        Long advertisementId =
                advertisement.getId();

        Long authorId =
                author.getId();

        entityManager.clear();

        Optional<Comment> result =
                commentRepository.findById(
                        commentId
                );

        assertTrue(
                result.isPresent()
        );

        Comment actualComment =
                result.orElseThrow();

        assertEquals(
                commentId,
                actualComment.getId()
        );

        assertEquals(
                advertisementId,
                actualComment.getAdvertisement().getId()
        );

        assertEquals(
                authorId,
                actualComment.getAuthor().getId()
        );

        assertEquals(
                "Comment for find",
                actualComment.getText()
        );
    }

    @Test
    void findById_ShouldReturnEmpty_WhenCommentDoesNotExist() {

        Optional<Comment> result =
                commentRepository.findById(
                        Long.MAX_VALUE
                );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void findByAdvertisementId_ShouldReturnCommentsSortedAndPaginated() {

        User seller = createUser(
                "comment-list-seller"
        );

        User author = createUser(
                "comment-list-author"
        );

        User anotherAuthor = createUser(
                "comment-list-another-author"
        );

        Category category = createCategory(
                "Comment List Category"
        );

        Region region = createRegion(
                "Comment List Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Comment List Advertisement",
                seller,
                category,
                region
        );

        Advertisement anotherAdvertisement = createAdvertisement(
                "Comment List Another Advertisement",
                seller,
                category,
                region
        );

        Comment newestComment = createComment(
                advertisement,
                author,
                "Newest comment",
                OffsetDateTime.parse(
                        "2026-09-25T13:00:00Z"
                )
        );

        createComment(
                advertisement,
                author,
                "Oldest comment",
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createComment(
                advertisement,
                author,
                "First same time comment",
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        Comment secondSameTimeComment = createComment(
                advertisement,
                anotherAuthor,
                "Second same time comment",
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        createComment(
                advertisement,
                author,
                "Actually newest comment",
                OffsetDateTime.parse(
                        "2026-09-25T14:00:00Z"
                )
        );

        createComment(
                anotherAdvertisement,
                anotherAuthor,
                "Another advertisement comment",
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        entityManager.flush();

        Long advertisementId =
                advertisement.getId();

        Long secondSameTimeCommentId =
                secondSameTimeComment.getId();

        Long newestCommentId =
                newestComment.getId();

        entityManager.clear();

        PageRequest pageRequest =
                new PageRequest(
                        1,
                        2
                );

        List<Comment> result =
                commentRepository.findByAdvertisementId(
                        advertisementId,
                        pageRequest
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                secondSameTimeCommentId,
                result.get(0).getId()
        );

        assertEquals(
                newestCommentId,
                result.get(1).getId()
        );

        assertTrue(
                result.stream()
                        .allMatch(comment ->
                                comment.getAdvertisement()
                                        .getId()
                                        .equals(advertisementId)
                        )
        );
    }

    @Test
    void countByAdvertisementId_ShouldCountOnlyCommentsOfSpecifiedAdvertisement() {

        User seller = createUser(
                "comment-count-seller"
        );

        User author = createUser(
                "comment-count-author"
        );

        Category category = createCategory(
                "Comment Count Category"
        );

        Region region = createRegion(
                "Comment Count Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Comment Count Advertisement",
                seller,
                category,
                region
        );

        Advertisement anotherAdvertisement = createAdvertisement(
                "Comment Count Another Advertisement",
                seller,
                category,
                region
        );

        createComment(
                advertisement,
                author,
                "First comment",
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createComment(
                advertisement,
                author,
                "Second comment",
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        createComment(
                anotherAdvertisement,
                author,
                "Another advertisement comment",
                OffsetDateTime.parse(
                        "2026-09-25T12:00:00Z"
                )
        );

        entityManager.flush();

        Long advertisementId =
                advertisement.getId();

        entityManager.clear();

        long result =
                commentRepository.countByAdvertisementId(
                        advertisementId
                );

        assertEquals(
                2L,
                result
        );
    }

    @Test
    void delete_ShouldDeleteComment() {

        User seller = createUser(
                "comment-delete-seller"
        );

        User author = createUser(
                "comment-delete-author"
        );

        Category category = createCategory(
                "Comment Delete Category"
        );

        Region region = createRegion(
                "Comment Delete Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Comment Delete Advertisement",
                seller,
                category,
                region
        );

        Comment comment = createComment(
                advertisement,
                author,
                "Comment to delete",
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        entityManager.flush();

        Long commentId =
                comment.getId();

        commentRepository.delete(comment);

        entityManager.flush();
        entityManager.clear();

        Comment actualComment = entityManager.find(
                Comment.class,
                commentId
        );

        assertNull(
                actualComment
        );
    }

    @Test
    void countByAuthorIdAndCreatedAtAfter_ShouldCountCommentsFromBoundaryForSpecifiedAuthor() {

        User seller = createUser(
                "comment-rate-seller"
        );

        User author = createUser(
                "comment-rate-author"
        );

        User anotherAuthor = createUser(
                "comment-rate-another-author"
        );

        Category category = createCategory(
                "Comment Rate Category"
        );

        Region region = createRegion(
                "Comment Rate Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Comment Rate Advertisement",
                seller,
                category,
                region
        );

        OffsetDateTime createdAfter =
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                );

        createComment(
                advertisement,
                author,
                "Older comment",
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createComment(
                advertisement,
                author,
                "Boundary comment",
                createdAfter
        );

        createComment(
                advertisement,
                author,
                "Newer comment",
                OffsetDateTime.parse(
                        "2026-09-25T12:00:00Z"
                )
        );

        createComment(
                advertisement,
                anotherAuthor,
                "Another author comment",
                OffsetDateTime.parse(
                        "2026-09-25T13:00:00Z"
                )
        );

        entityManager.flush();

        Long authorId =
                author.getId();

        entityManager.clear();

        long result =
                commentRepository.countByAuthorIdAndCreatedAtAfter(
                        authorId,
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
                                "2026-09-25T08:00:00Z"
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
            String title,
            User seller,
            Category category,
            Region region
    ) {

        Advertisement advertisement = Advertisement.builder()
                .seller(seller)
                .category(category)
                .region(region)
                .locality("Test Locality")
                .title(title)
                .description("Test Description")
                .price(new BigDecimal("100000.00"))
                .advertisementStatus(AdvertisementStatus.ACTIVE)
                .createdAt(
                        OffsetDateTime.parse(
                                "2026-09-25T08:30:00Z"
                        )
                )
                .build();

        entityManager.persist(advertisement);

        return advertisement;
    }

    private Comment createComment(
            Advertisement advertisement,
            User author,
            String text,
            OffsetDateTime createdAt
    ) {

        Comment comment = buildComment(
                advertisement,
                author,
                text,
                createdAt
        );

        entityManager.persist(comment);

        return comment;
    }

    private Comment buildComment(
            Advertisement advertisement,
            User author,
            String text,
            OffsetDateTime createdAt
    ) {

        return Comment.builder()
                .advertisement(advertisement)
                .author(author)
                .text(text)
                .createdAt(createdAt)
                .build();
    }
}