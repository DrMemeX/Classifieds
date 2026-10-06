package ru.drmemex.classifieds.feature.conversation.message.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.category.entity.Category;
import ru.drmemex.classifieds.feature.conversation.entity.Conversation;
import ru.drmemex.classifieds.feature.conversation.message.entity.Message;
import ru.drmemex.classifieds.feature.conversation.message.repository.MessageRepository;
import ru.drmemex.classifieds.feature.region.entity.Region;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.integration.AbstractIntegrationTest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MessageRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private MessageRepository messageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void save_ShouldSaveMessage() {

        User seller = createUser(
                "message-save-seller"
        );

        User buyer = createUser(
                "message-save-buyer"
        );

        Category category = createCategory(
                "Message Save Category"
        );

        Region region = createRegion(
                "Message Save Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Message Save Advertisement",
                seller,
                category,
                region
        );

        Conversation conversation = createConversation(
                advertisement,
                buyer,
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        OffsetDateTime createdAt =
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                );

        Message message = buildMessage(
                conversation,
                buyer,
                "Test message",
                createdAt
        );

        Message savedMessage =
                messageRepository.save(message);

        entityManager.flush();

        Long messageId =
                savedMessage.getId();

        Long conversationId =
                conversation.getId();

        Long authorId =
                buyer.getId();

        entityManager.clear();

        Message actualMessage = entityManager.find(
                Message.class,
                messageId
        );

        assertNotNull(
                actualMessage
        );

        assertEquals(
                conversationId,
                actualMessage.getConversation().getId()
        );

        assertEquals(
                authorId,
                actualMessage.getAuthor().getId()
        );

        assertEquals(
                "Test message",
                actualMessage.getText()
        );

        assertEquals(
                createdAt.toInstant(),
                actualMessage.getCreatedAt().toInstant()
        );
    }

    @Test
    void findByConversationId_ShouldReturnMessagesSortedAndPaginated() {

        User seller = createUser(
                "message-find-seller"
        );

        User buyer = createUser(
                "message-find-buyer"
        );

        User anotherBuyer = createUser(
                "message-find-another-buyer"
        );

        Category category = createCategory(
                "Message Find Category"
        );

        Region region = createRegion(
                "Message Find Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Message Find Advertisement",
                seller,
                category,
                region
        );

        Advertisement anotherAdvertisement = createAdvertisement(
                "Message Find Another Advertisement",
                seller,
                category,
                region
        );

        Conversation conversation = createConversation(
                advertisement,
                buyer,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        Conversation anotherConversation = createConversation(
                anotherAdvertisement,
                anotherBuyer,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        Message newestMessage = createMessage(
                conversation,
                seller,
                "Newest message",
                OffsetDateTime.parse(
                        "2026-09-25T13:00:00Z"
                )
        );

        createMessage(
                conversation,
                buyer,
                "Oldest message",
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createMessage(
                conversation,
                seller,
                "First same time message",
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        Message secondSameTimeMessage = createMessage(
                conversation,
                buyer,
                "Second same time message",
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        createMessage(
                anotherConversation,
                anotherBuyer,
                "Another conversation message",
                OffsetDateTime.parse(
                        "2026-09-25T09:30:00Z"
                )
        );

        entityManager.flush();

        Long conversationId =
                conversation.getId();

        Long secondSameTimeMessageId =
                secondSameTimeMessage.getId();

        Long newestMessageId =
                newestMessage.getId();

        entityManager.clear();

        PageRequest pageRequest =
                new PageRequest(
                        1,
                        2
                );

        List<Message> result =
                messageRepository.findByConversationId(
                        conversationId,
                        pageRequest
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                secondSameTimeMessageId,
                result.get(0).getId()
        );

        assertEquals(
                newestMessageId,
                result.get(1).getId()
        );
    }

    @Test
    void countByConversationId_ShouldCountOnlyMessagesOfSpecifiedConversation() {

        User seller = createUser(
                "message-count-seller"
        );

        User buyer = createUser(
                "message-count-buyer"
        );

        User anotherBuyer = createUser(
                "message-count-another-buyer"
        );

        Category category = createCategory(
                "Message Count Category"
        );

        Region region = createRegion(
                "Message Count Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Message Count Advertisement",
                seller,
                category,
                region
        );

        Advertisement anotherAdvertisement = createAdvertisement(
                "Message Count Another Advertisement",
                seller,
                category,
                region
        );

        Conversation conversation = createConversation(
                advertisement,
                buyer,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        Conversation anotherConversation = createConversation(
                anotherAdvertisement,
                anotherBuyer,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        createMessage(
                conversation,
                buyer,
                "First message",
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createMessage(
                conversation,
                seller,
                "Second message",
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        createMessage(
                anotherConversation,
                anotherBuyer,
                "Another conversation message",
                OffsetDateTime.parse(
                        "2026-09-25T12:00:00Z"
                )
        );

        entityManager.flush();

        Long conversationId =
                conversation.getId();

        entityManager.clear();

        long result =
                messageRepository.countByConversationId(
                        conversationId
                );

        assertEquals(
                2L,
                result
        );
    }

    @Test
    void searchByConversationIdAndText_ShouldReturnMatchingMessagesIgnoringCase() {

        User seller = createUser(
                "message-search-seller"
        );

        User buyer = createUser(
                "message-search-buyer"
        );

        User anotherBuyer = createUser(
                "message-search-another-buyer"
        );

        Category category = createCategory(
                "Message Search Category"
        );

        Region region = createRegion(
                "Message Search Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Message Search Advertisement",
                seller,
                category,
                region
        );

        Advertisement anotherAdvertisement = createAdvertisement(
                "Message Search Another Advertisement",
                seller,
                category,
                region
        );

        Conversation conversation = createConversation(
                advertisement,
                buyer,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        Conversation anotherConversation = createConversation(
                anotherAdvertisement,
                anotherBuyer,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        Message firstMatchingMessage = createMessage(
                conversation,
                buyer,
                "Hello, how are you?",
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createMessage(
                conversation,
                seller,
                "Goodbye",
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        Message secondMatchingMessage = createMessage(
                conversation,
                seller,
                "Say HELLO again",
                OffsetDateTime.parse(
                        "2026-09-25T12:00:00Z"
                )
        );

        createMessage(
                anotherConversation,
                anotherBuyer,
                "Hello from another conversation",
                OffsetDateTime.parse(
                        "2026-09-25T09:30:00Z"
                )
        );

        entityManager.flush();

        Long conversationId =
                conversation.getId();

        Long firstMatchingMessageId =
                firstMatchingMessage.getId();

        Long secondMatchingMessageId =
                secondMatchingMessage.getId();

        entityManager.clear();

        PageRequest pageRequest =
                new PageRequest(
                        0,
                        10
                );

        List<Message> result =
                messageRepository.searchByConversationIdAndText(
                        conversationId,
                        "hello",
                        pageRequest
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                firstMatchingMessageId,
                result.get(0).getId()
        );

        assertEquals(
                secondMatchingMessageId,
                result.get(1).getId()
        );
    }

    @Test
    void countByConversationIdAndText_ShouldCountMatchingMessagesIgnoringCase() {

        User seller = createUser(
                "message-search-count-seller"
        );

        User buyer = createUser(
                "message-search-count-buyer"
        );

        User anotherBuyer = createUser(
                "message-search-count-another-buyer"
        );

        Category category = createCategory(
                "Message Search Count Category"
        );

        Region region = createRegion(
                "Message Search Count Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Message Search Count Advertisement",
                seller,
                category,
                region
        );

        Advertisement anotherAdvertisement = createAdvertisement(
                "Message Search Count Another Advertisement",
                seller,
                category,
                region
        );

        Conversation conversation = createConversation(
                advertisement,
                buyer,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        Conversation anotherConversation = createConversation(
                anotherAdvertisement,
                anotherBuyer,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        createMessage(
                conversation,
                buyer,
                "Hello world",
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createMessage(
                conversation,
                seller,
                "Another HELLO message",
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        createMessage(
                conversation,
                seller,
                "No match here",
                OffsetDateTime.parse(
                        "2026-09-25T12:00:00Z"
                )
        );

        createMessage(
                anotherConversation,
                anotherBuyer,
                "Hello from another conversation",
                OffsetDateTime.parse(
                        "2026-09-25T13:00:00Z"
                )
        );

        entityManager.flush();

        Long conversationId =
                conversation.getId();

        entityManager.clear();

        long result =
                messageRepository.countByConversationIdAndText(
                        conversationId,
                        "hello"
                );

        assertEquals(
                2L,
                result
        );
    }

    @Test
    void countByAuthorIdAndCreatedAtAfter_ShouldCountMessagesFromBoundaryForSpecifiedAuthor() {

        User seller = createUser(
                "message-rate-seller"
        );

        User buyer = createUser(
                "message-rate-buyer"
        );

        Category category = createCategory(
                "Message Rate Category"
        );

        Region region = createRegion(
                "Message Rate Region"
        );

        Advertisement advertisement = createAdvertisement(
                "Message Rate Advertisement",
                seller,
                category,
                region
        );

        Conversation conversation = createConversation(
                advertisement,
                buyer,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        OffsetDateTime createdAfter =
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                );

        createMessage(
                conversation,
                buyer,
                "Older message",
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createMessage(
                conversation,
                buyer,
                "Boundary message",
                createdAfter
        );

        createMessage(
                conversation,
                buyer,
                "Newer message",
                OffsetDateTime.parse(
                        "2026-09-25T12:00:00Z"
                )
        );

        createMessage(
                conversation,
                seller,
                "Another author message",
                OffsetDateTime.parse(
                        "2026-09-25T13:00:00Z"
                )
        );

        entityManager.flush();

        Long authorId =
                buyer.getId();

        entityManager.clear();

        long result =
                messageRepository.countByAuthorIdAndCreatedAtAfter(
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

    private Conversation createConversation(
            Advertisement advertisement,
            User buyer,
            OffsetDateTime createdAt
    ) {

        Conversation conversation = Conversation.builder()
                .advertisement(advertisement)
                .buyer(buyer)
                .createdAt(createdAt)
                .build();

        entityManager.persist(conversation);

        return conversation;
    }

    private Message createMessage(
            Conversation conversation,
            User author,
            String text,
            OffsetDateTime createdAt
    ) {

        Message message = buildMessage(
                conversation,
                author,
                text,
                createdAt
        );

        entityManager.persist(message);

        return message;
    }

    private Message buildMessage(
            Conversation conversation,
            User author,
            String text,
            OffsetDateTime createdAt
    ) {

        return Message.builder()
                .conversation(conversation)
                .author(author)
                .text(text)
                .createdAt(createdAt)
                .build();
    }
}