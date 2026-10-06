package ru.drmemex.classifieds.feature.conversation.message.servise.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.conversation.entity.Conversation;
import ru.drmemex.classifieds.feature.conversation.message.dto.MessageRequest;
import ru.drmemex.classifieds.feature.conversation.message.dto.MessageResponse;
import ru.drmemex.classifieds.feature.conversation.message.encryption.EncryptedMessage;
import ru.drmemex.classifieds.feature.conversation.message.encryption.service.MessageEncryptionService;
import ru.drmemex.classifieds.feature.conversation.message.entity.Message;
import ru.drmemex.classifieds.feature.conversation.message.exception.MessageRateLimitExceededException;
import ru.drmemex.classifieds.feature.conversation.message.exception.MessageSendingNotAllowedException;
import ru.drmemex.classifieds.feature.conversation.message.mapper.MessageMapper;
import ru.drmemex.classifieds.feature.conversation.message.repository.MessageRepository;
import ru.drmemex.classifieds.feature.conversation.message.service.impl.MessageServiceImpl;
import ru.drmemex.classifieds.feature.conversation.service.ConversationService;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.security.provider.CurrentUserProvider;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    private static final String TEST_IV = "0123456789abcdef";

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private ConversationService conversationService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private MessageEncryptionService messageEncryptionService;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void send_ShouldSendMessage_WhenRecentMessagesCountIs19() {

        MessageRequest request = new MessageRequest("Здравствуйте");

        User currentUser = new User();
        currentUser.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        Conversation conversation = new Conversation();
        conversation.setId(1L);
        conversation.setAdvertisement(advertisement);

        EncryptedMessage encryptedMessage =
                new EncryptedMessage(
                        "encrypted-message",
                        TEST_IV
                );

        MessageResponse expectedResponse =
                new MessageResponse(
                        1L,
                        1L,
                        1L,
                        "Здравствуйте",
                        OffsetDateTime.now()
                );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(conversationService.getAccessibleConversation(
                1L,
                currentUser
        )).thenReturn(conversation);

        when(messageRepository.countByAuthorIdAndCreatedAtAfter(
                any(),
                any(OffsetDateTime.class)
        )).thenReturn(19L);

        when(messageEncryptionService.encrypt(
                "Здравствуйте",
                1L
        )).thenReturn(encryptedMessage);

        when(messageRepository.save(any(Message.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(messageEncryptionService.decrypt(
                "encrypted-message",
                TEST_IV,
                1L
        )).thenReturn("Здравствуйте");

        when(messageMapper.toResponse(
                any(Message.class),
                eq("Здравствуйте")
        )).thenReturn(expectedResponse);

        MessageResponse result =
                messageService.send(
                        1L,
                        request
                );

        assertEquals(
                expectedResponse,
                result
        );

        ArgumentCaptor<Message> messageCaptor =
                ArgumentCaptor.forClass(Message.class);

        verify(messageRepository)
                .save(messageCaptor.capture());

        Message savedMessage =
                messageCaptor.getValue();

        assertSame(
                conversation,
                savedMessage.getConversation()
        );

        assertSame(
                currentUser,
                savedMessage.getAuthor()
        );

        assertEquals(
                "encrypted-message",
                savedMessage.getEncryptedText()
        );

        assertEquals(
                TEST_IV,
                savedMessage.getIv()
        );

        assertNotNull(
                savedMessage.getCreatedAt()
        );

        verify(messageEncryptionService)
                .encrypt(
                        "Здравствуйте",
                        1L
                );

        verify(messageEncryptionService)
                .decrypt(
                        "encrypted-message",
                        TEST_IV,
                        1L
                );

        verify(messageMapper)
                .toResponse(
                        savedMessage,
                        "Здравствуйте"
                );
    }

    @Test
    void send_ShouldThrowException_WhenAdvertisementIsNotActive() {

        MessageRequest request =
                new MessageRequest("Здравствуйте");

        User currentUser = new User();

        Advertisement advertisement = new Advertisement();
        advertisement.setAdvertisementStatus(
                AdvertisementStatus.INACTIVE
        );

        Conversation conversation = new Conversation();
        conversation.setAdvertisement(advertisement);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(conversationService.getAccessibleConversation(
                1L,
                currentUser
        )).thenReturn(conversation);

        assertThrows(
                MessageSendingNotAllowedException.class,
                () -> messageService.send(
                        1L,
                        request
                )
        );

        verify(messageRepository, never())
                .countByAuthorIdAndCreatedAtAfter(
                        any(),
                        any(OffsetDateTime.class)
                );

        verify(messageRepository, never())
                .save(any(Message.class));

        verify(messageEncryptionService, never())
                .encrypt(
                        anyString(),
                        any()
                );

        verify(messageMapper, never())
                .toResponse(
                        any(Message.class),
                        anyString()
                );
    }

    @Test
    void send_ShouldThrowException_WhenRateLimitExceeded() {

        MessageRequest request =
                new MessageRequest("Здравствуйте");

        User currentUser = new User();
        currentUser.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setAdvertisementStatus(
                AdvertisementStatus.ACTIVE
        );

        Conversation conversation = new Conversation();
        conversation.setAdvertisement(advertisement);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(conversationService.getAccessibleConversation(
                1L,
                currentUser
        )).thenReturn(conversation);

        when(messageRepository.countByAuthorIdAndCreatedAtAfter(
                any(),
                any(OffsetDateTime.class)
        )).thenReturn(20L);

        assertThrows(
                MessageRateLimitExceededException.class,
                () -> messageService.send(
                        1L,
                        request
                )
        );

        verify(messageRepository, never())
                .save(any(Message.class));

        verify(messageEncryptionService, never())
                .encrypt(
                        anyString(),
                        any()
                );

        verify(messageMapper, never())
                .toResponse(
                        any(Message.class),
                        anyString()
                );
    }

    @Test
    void getByConversationId_ShouldReturnPageOfMessages() {

        User currentUser = new User();
        currentUser.setId(1L);

        Conversation conversation = new Conversation();
        conversation.setId(1L);

        PageRequest pageRequest =
                new PageRequest(
                        1,
                        2
                );

        Message message1 = createMessage(
                1L,
                conversation,
                "encrypted-first"
        );

        Message message2 = createMessage(
                2L,
                conversation,
                "encrypted-second"
        );

        MessageResponse response1 =
                createResponse(
                        1L,
                        "First message"
                );

        MessageResponse response2 =
                createResponse(
                        2L,
                        "Second message"
                );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(messageRepository.findByConversationId(
                1L,
                pageRequest
        )).thenReturn(List.of(
                message1,
                message2
        ));

        when(messageEncryptionService.decrypt(
                "encrypted-first",
                TEST_IV,
                1L
        )).thenReturn("First message");

        when(messageEncryptionService.decrypt(
                "encrypted-second",
                TEST_IV,
                1L
        )).thenReturn("Second message");

        when(messageMapper.toResponse(
                message1,
                "First message"
        )).thenReturn(response1);

        when(messageMapper.toResponse(
                message2,
                "Second message"
        )).thenReturn(response2);

        when(messageRepository.countByConversationId(1L))
                .thenReturn(5L);

        PageResponse<MessageResponse> result =
                messageService.getByConversationId(
                        1L,
                        pageRequest
                );

        assertEquals(
                List.of(response1, response2),
                result.content()
        );

        assertEquals(
                1,
                result.page()
        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                5L,
                result.totalElements()
        );

        assertEquals(
                3,
                result.totalPages()
        );

        verify(conversationService)
                .getAccessibleConversation(
                        1L,
                        currentUser
                );

        verify(messageRepository)
                .findByConversationId(
                        1L,
                        pageRequest
                );

        verify(messageMapper)
                .toResponse(
                        message1,
                        "First message"
                );

        verify(messageMapper)
                .toResponse(
                        message2,
                        "Second message"
                );

        verify(messageRepository)
                .countByConversationId(1L);
    }

    @Test
    void searchByConversationAndText_ShouldReturnFilteredAndPaginatedMessages() {

        User currentUser = new User();
        currentUser.setId(1L);

        Conversation conversation = new Conversation();
        conversation.setId(1L);

        PageRequest pageRequest =
                new PageRequest(
                        1,
                        2
                );

        Message message1 = createMessage(
                1L,
                conversation,
                "encrypted-1"
        );

        Message message2 = createMessage(
                2L,
                conversation,
                "encrypted-2"
        );

        Message message3 = createMessage(
                3L,
                conversation,
                "encrypted-3"
        );

        Message message4 = createMessage(
                4L,
                conversation,
                "encrypted-4"
        );

        Message message5 = createMessage(
                5L,
                conversation,
                "encrypted-5"
        );

        Message message6 = createMessage(
                6L,
                conversation,
                "encrypted-6"
        );

        MessageResponse response1 =
                createResponse(
                        1L,
                        "Привет один"
                );

        MessageResponse response2 =
                createResponse(
                        2L,
                        "Сообщение без совпадения"
                );

        MessageResponse response3 =
                createResponse(
                        3L,
                        "ПРИВЕТ два"
                );

        MessageResponse response4 =
                createResponse(
                        4L,
                        "Это привет три"
                );

        MessageResponse response5 =
                createResponse(
                        5L,
                        "ПрИвЕт четыре"
                );

        MessageResponse response6 =
                createResponse(
                        6L,
                        "привет пять"
                );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(messageRepository.findAllByConversationId(1L))
                .thenReturn(List.of(
                        message1,
                        message2,
                        message3,
                        message4,
                        message5,
                        message6
                ));

        mockDecryption(
                message1,
                "Привет один",
                response1
        );

        mockDecryption(
                message2,
                "Сообщение без совпадения",
                response2
        );

        mockDecryption(
                message3,
                "ПРИВЕТ два",
                response3
        );

        mockDecryption(
                message4,
                "Это привет три",
                response4
        );

        mockDecryption(
                message5,
                "ПрИвЕт четыре",
                response5
        );

        mockDecryption(
                message6,
                "привет пять",
                response6
        );

        PageResponse<MessageResponse> result =
                messageService.searchByConversationAndText(
                        1L,
                        "привет",
                        pageRequest
                );

        assertEquals(
                List.of(
                        response4,
                        response5
                ),
                result.content()
        );

        assertEquals(
                1,
                result.page()
        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                5L,
                result.totalElements()
        );

        assertEquals(
                3,
                result.totalPages()
        );

        verify(conversationService)
                .getAccessibleConversation(
                        1L,
                        currentUser
                );

        verify(messageRepository)
                .findAllByConversationId(1L);
    }

    private Message createMessage(
            Long id,
            Conversation conversation,
            String encryptedText
    ) {

        Message message = new Message();

        message.setId(id);
        message.setConversation(conversation);
        message.setEncryptedText(encryptedText);
        message.setIv(TEST_IV);

        return message;
    }

    private MessageResponse createResponse(
            Long id,
            String text
    ) {

        return new MessageResponse(
                id,
                1L,
                1L,
                text,
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );
    }

    private void mockDecryption(
            Message message,
            String text,
            MessageResponse response
    ) {

        when(messageEncryptionService.decrypt(
                message.getEncryptedText(),
                TEST_IV,
                1L
        )).thenReturn(text);

        when(messageMapper.toResponse(
                message,
                text
        )).thenReturn(response);
    }
}