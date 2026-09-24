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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private ConversationService conversationService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void send_ShouldSendMessage_WhenRecentMessagesCountIs19() {

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

        MessageResponse expectedResponse =
                mock(MessageResponse.class);

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

        when(messageRepository.save(any(Message.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(messageMapper.toResponse(any(Message.class)))
                .thenReturn(expectedResponse);

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
                "Здравствуйте",
                savedMessage.getText()
        );

        assertNotNull(
                savedMessage.getCreatedAt()
        );

        verify(conversationService)
                .getAccessibleConversation(
                        1L,
                        currentUser
                );

        verify(messageMapper)
                .toResponse(savedMessage);
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

        verify(messageMapper, never())
                .toResponse(any(Message.class));
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

        verify(messageMapper, never())
                .toResponse(any(Message.class));
    }

    @Test
    void getByConversationId_ShouldReturnPageOfMessages() {

        User currentUser = new User();
        currentUser.setId(1L);

        PageRequest pageRequest =
                new PageRequest(
                        1,
                        2
                );

        Message message1 = new Message();
        Message message2 = new Message();

        MessageResponse response1 =
                mock(MessageResponse.class);

        MessageResponse response2 =
                mock(MessageResponse.class);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(messageRepository.findByConversationId(
                1L,
                pageRequest
        )).thenReturn(List.of(
                message1,
                message2
        ));

        when(messageMapper.toResponse(message1))
                .thenReturn(response1);

        when(messageMapper.toResponse(message2))
                .thenReturn(response2);

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
                .toResponse(message1);

        verify(messageMapper)
                .toResponse(message2);

        verify(messageRepository)
                .countByConversationId(1L);
    }

    @Test
    void searchByConversationAndText_ShouldReturnPageOfMessages() {

        User currentUser = new User();
        currentUser.setId(1L);

        PageRequest pageRequest =
                new PageRequest(
                        1,
                        2
                );

        Message message1 = new Message();
        Message message2 = new Message();

        MessageResponse response1 =
                mock(MessageResponse.class);

        MessageResponse response2 =
                mock(MessageResponse.class);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(messageRepository.searchByConversationIdAndText(
                1L,
                "привет",
                pageRequest
        )).thenReturn(List.of(
                message1,
                message2
        ));

        when(messageMapper.toResponse(message1))
                .thenReturn(response1);

        when(messageMapper.toResponse(message2))
                .thenReturn(response2);

        when(messageRepository.countByConversationIdAndText(
                1L,
                "привет"
        )).thenReturn(5L);

        PageResponse<MessageResponse> result =
                messageService.searchByConversationAndText(
                        1L,
                        "привет",
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
                .searchByConversationIdAndText(
                        1L,
                        "привет",
                        pageRequest
                );

        verify(messageMapper)
                .toResponse(message1);

        verify(messageMapper)
                .toResponse(message2);

        verify(messageRepository)
                .countByConversationIdAndText(
                        1L,
                        "привет"
                );
    }
}