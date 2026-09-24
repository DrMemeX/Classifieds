package ru.drmemex.classifieds.feature.conversation.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.category.entity.Category;
import ru.drmemex.classifieds.feature.conversation.entity.Conversation;
import ru.drmemex.classifieds.feature.conversation.repository.ConversationRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConversationRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private ConversationRepository conversationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void save_ShouldSaveConversation() {

        User seller = createUser(
                "conversation-save-seller"
        );

        User buyer = createUser(
                "conversation-save-buyer"
        );

        Category category = createCategory(
                "Conversation Save Category"
        );

        Region region = createRegion(
                "Conversation Save Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Conversation Save Advertisement",
                seller,
                category,
                region,
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        OffsetDateTime createdAt =
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                );

        Conversation conversation = buildConversation(
                advertisement,
                buyer,
                createdAt
        );

        Conversation savedConversation =
                conversationRepository.save(conversation);

        entityManager.flush();

        Long conversationId =
                savedConversation.getId();

        Long advertisementId =
                advertisement.getId();

        Long buyerId =
                buyer.getId();

        entityManager.clear();

        Conversation actualConversation = entityManager.find(
                Conversation.class,
                conversationId
        );

        assertNotNull(
                actualConversation
        );

        assertEquals(
                advertisementId,
                actualConversation.getAdvertisement().getId()
        );

        assertEquals(
                buyerId,
                actualConversation.getBuyer().getId()
        );

        assertEquals(
                createdAt.toInstant(),
                actualConversation.getCreatedAt().toInstant()
        );
    }

    @Test
    void findById_ShouldReturnConversation() {

        User seller = createUser(
                "conversation-find-seller"
        );

        User buyer = createUser(
                "conversation-find-buyer"
        );

        Category category = createCategory(
                "Conversation Find Category"
        );

        Region region = createRegion(
                "Conversation Find Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Conversation Find Advertisement",
                seller,
                category,
                region,
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        Conversation conversation = createConversation(
                advertisement,
                buyer,
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        entityManager.flush();

        Long conversationId =
                conversation.getId();

        Long advertisementId =
                advertisement.getId();

        Long buyerId =
                buyer.getId();

        entityManager.clear();

        Optional<Conversation> result =
                conversationRepository.findById(
                        conversationId
                );

        assertTrue(
                result.isPresent()
        );

        Conversation actualConversation =
                result.orElseThrow();

        assertEquals(
                conversationId,
                actualConversation.getId()
        );

        assertEquals(
                advertisementId,
                actualConversation.getAdvertisement().getId()
        );

        assertEquals(
                buyerId,
                actualConversation.getBuyer().getId()
        );
    }

    @Test
    void findById_ShouldReturnEmpty_WhenConversationDoesNotExist() {

        Optional<Conversation> result =
                conversationRepository.findById(
                        Long.MAX_VALUE
                );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void findByAdvertisementIdAndBuyerId_ShouldReturnConversation() {

        User seller = createUser(
                "conversation-pair-seller"
        );

        User buyer = createUser(
                "conversation-pair-buyer"
        );

        User anotherBuyer = createUser(
                "conversation-pair-another-buyer"
        );

        Category category = createCategory(
                "Conversation Pair Category"
        );

        Region region = createRegion(
                "Conversation Pair Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Conversation Pair Advertisement",
                seller,
                category,
                region,
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        Conversation conversation = createConversation(
                advertisement,
                buyer,
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        createConversation(
                advertisement,
                anotherBuyer,
                OffsetDateTime.parse(
                        "2026-09-25T12:00:00Z"
                )
        );

        entityManager.flush();

        Long conversationId =
                conversation.getId();

        Long advertisementId =
                advertisement.getId();

        Long buyerId =
                buyer.getId();

        entityManager.clear();

        Optional<Conversation> result =
                conversationRepository.findByAdvertisementIdAndBuyerId(
                        advertisementId,
                        buyerId
                );

        assertTrue(
                result.isPresent()
        );

        Conversation actualConversation =
                result.orElseThrow();

        assertEquals(
                conversationId,
                actualConversation.getId()
        );

        assertEquals(
                advertisementId,
                actualConversation.getAdvertisement().getId()
        );

        assertEquals(
                buyerId,
                actualConversation.getBuyer().getId()
        );
    }

    @Test
    void findByAdvertisementIdAndBuyerId_ShouldReturnEmpty_WhenConversationDoesNotExist() {

        User seller = createUser(
                "conversation-pair-empty-seller"
        );

        User buyer = createUser(
                "conversation-pair-empty-buyer"
        );

        User anotherBuyer = createUser(
                "conversation-pair-empty-another-buyer"
        );

        Category category = createCategory(
                "Conversation Pair Empty Category"
        );

        Region region = createRegion(
                "Conversation Pair Empty Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Conversation Pair Empty Advertisement",
                seller,
                category,
                region,
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createConversation(
                advertisement,
                buyer,
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        entityManager.flush();

        Long advertisementId =
                advertisement.getId();

        Long anotherBuyerId =
                anotherBuyer.getId();

        entityManager.clear();

        Optional<Conversation> result =
                conversationRepository.findByAdvertisementIdAndBuyerId(
                        advertisementId,
                        anotherBuyerId
                );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void findByParticipantId_ShouldReturnConversationsWhereUserIsBuyerOrSellerSortedAndPaginated() {

        User participant = createUser(
                "conversation-participant-user"
        );

        User anotherSeller = createUser(
                "conversation-participant-another-seller"
        );

        User anotherBuyer = createUser(
                "conversation-participant-another-buyer"
        );

        Category category = createCategory(
                "Conversation Participant Category"
        );

        Region region = createRegion(
                "Conversation Participant Region"
        );

        Advertisement participantAdvertisement =
                createAdvertisement(
                        "Participant Seller Advertisement",
                        participant,
                        category,
                        region,
                        OffsetDateTime.parse(
                                "2026-09-25T09:00:00Z"
                        )
                );

        Advertisement buyerAdvertisement =
                createAdvertisement(
                        "Participant Buyer Advertisement",
                        anotherSeller,
                        category,
                        region,
                        OffsetDateTime.parse(
                                "2026-09-25T09:30:00Z"
                        )
                );

        Advertisement oldBuyerAdvertisement =
                createAdvertisement(
                        "Participant Old Buyer Advertisement",
                        anotherSeller,
                        category,
                        region,
                        OffsetDateTime.parse(
                                "2026-09-25T09:45:00Z"
                        )
                );

        Advertisement unrelatedAdvertisement =
                createAdvertisement(
                        "Unrelated Conversation Advertisement",
                        anotherSeller,
                        category,
                        region,
                        OffsetDateTime.parse(
                                "2026-09-25T09:50:00Z"
                        )
                );

        OffsetDateTime sameCreatedAt =
                OffsetDateTime.parse(
                        "2026-09-25T12:00:00Z"
                );

        Conversation sellerConversation = createConversation(
                participantAdvertisement,
                anotherBuyer,
                sameCreatedAt
        );

        Conversation buyerConversation = createConversation(
                buyerAdvertisement,
                participant,
                sameCreatedAt
        );

        createConversation(
                oldBuyerAdvertisement,
                participant,
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createConversation(
                unrelatedAdvertisement,
                anotherBuyer,
                OffsetDateTime.parse(
                        "2026-09-25T13:00:00Z"
                )
        );

        entityManager.flush();

        Long participantId =
                participant.getId();

        Long sellerConversationId =
                sellerConversation.getId();

        Long buyerConversationId =
                buyerConversation.getId();

        entityManager.clear();

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        2
                );

        List<Conversation> result =
                conversationRepository.findByParticipantId(
                        participantId,
                        pageRequest
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                buyerConversationId,
                result.get(0).getId()
        );

        assertEquals(
                sellerConversationId,
                result.get(1).getId()
        );

        assertTrue(
                result.stream()
                        .allMatch(conversation ->
                                conversation.getBuyer()
                                        .getId()
                                        .equals(participantId)
                                        || conversation.getAdvertisement()
                                        .getSeller()
                                        .getId()
                                        .equals(participantId)
                        )
        );
    }

    @Test
    void countByParticipantId_ShouldCountConversationsWhereUserIsBuyerOrSeller() {

        User participant = createUser(
                "conversation-count-participant"
        );

        User anotherSeller = createUser(
                "conversation-count-another-seller"
        );

        User anotherBuyer = createUser(
                "conversation-count-another-buyer"
        );

        Category category = createCategory(
                "Conversation Count Category"
        );

        Region region = createRegion(
                "Conversation Count Region"
        );

        Advertisement participantAdvertisement =
                createAdvertisement(
                        "Conversation Count Seller Advertisement",
                        participant,
                        category,
                        region,
                        OffsetDateTime.parse(
                                "2026-09-25T09:00:00Z"
                        )
                );

        Advertisement buyerAdvertisement =
                createAdvertisement(
                        "Conversation Count Buyer Advertisement",
                        anotherSeller,
                        category,
                        region,
                        OffsetDateTime.parse(
                                "2026-09-25T10:00:00Z"
                        )
                );

        Advertisement unrelatedAdvertisement =
                createAdvertisement(
                        "Conversation Count Unrelated Advertisement",
                        anotherSeller,
                        category,
                        region,
                        OffsetDateTime.parse(
                                "2026-09-25T11:00:00Z"
                        )
                );

        createConversation(
                participantAdvertisement,
                anotherBuyer,
                OffsetDateTime.parse(
                        "2026-09-25T12:00:00Z"
                )
        );

        createConversation(
                buyerAdvertisement,
                participant,
                OffsetDateTime.parse(
                        "2026-09-25T13:00:00Z"
                )
        );

        createConversation(
                unrelatedAdvertisement,
                anotherBuyer,
                OffsetDateTime.parse(
                        "2026-09-25T14:00:00Z"
                )
        );

        entityManager.flush();

        Long participantId =
                participant.getId();

        entityManager.clear();

        long result =
                conversationRepository.countByParticipantId(
                        participantId
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
            Region region,
            OffsetDateTime createdAt
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
                .createdAt(createdAt)
                .build();

        entityManager.persist(advertisement);

        return advertisement;
    }

    private Conversation createConversation(
            Advertisement advertisement,
            User buyer,
            OffsetDateTime createdAt
    ) {

        Conversation conversation = buildConversation(
                advertisement,
                buyer,
                createdAt
        );

        entityManager.persist(conversation);

        return conversation;
    }

    private Conversation buildConversation(
            Advertisement advertisement,
            User buyer,
            OffsetDateTime createdAt
    ) {

        return Conversation.builder()
                .advertisement(advertisement)
                .buyer(buyer)
                .createdAt(createdAt)
                .build();
    }
}