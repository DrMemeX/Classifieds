package ru.drmemex.classifieds.feature.conversation.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.exception.AdvertisementNotFoundException;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.repository.AdvertisementRepository;
import ru.drmemex.classifieds.feature.conversation.dto.ConversationResponse;
import ru.drmemex.classifieds.feature.conversation.entity.Conversation;
import ru.drmemex.classifieds.feature.conversation.exception.ConversationAccessDeniedException;
import ru.drmemex.classifieds.feature.conversation.exception.ConversationAlreadyExistsException;
import ru.drmemex.classifieds.feature.conversation.exception.ConversationCreationNotAllowedException;
import ru.drmemex.classifieds.feature.conversation.exception.ConversationNotFoundException;
import ru.drmemex.classifieds.feature.conversation.exception.ConversationWithOwnAdvertisementException;
import ru.drmemex.classifieds.feature.conversation.mapper.ConversationMapper;
import ru.drmemex.classifieds.feature.conversation.repository.ConversationRepository;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.security.provider.CurrentUserProvider;

import java.util.List;
import java.util.Optional;

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
class ConversationServiceImplTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private ConversationMapper conversationMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    @Test
    void create_ShouldCreateConversation() {

        User seller = new User();
        seller.setId(1L);

        User buyer = new User();
        buyer.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        ConversationResponse expectedResponse =
                mock(ConversationResponse.class);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(buyer);

        when(conversationRepository.findByAdvertisementIdAndBuyerId(
                1L,
                2L
        )).thenReturn(Optional.empty());

        when(conversationRepository.save(any(Conversation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(conversationMapper.toResponse(any(Conversation.class)))
                .thenReturn(expectedResponse);

        ConversationResponse result =
                conversationService.create(1L);

        assertEquals(
                expectedResponse,
                result
        );

        ArgumentCaptor<Conversation> conversationCaptor =
                ArgumentCaptor.forClass(Conversation.class);

        verify(conversationRepository)
                .save(conversationCaptor.capture());

        Conversation savedConversation =
                conversationCaptor.getValue();

        assertSame(
                advertisement,
                savedConversation.getAdvertisement()
        );

        assertSame(
                buyer,
                savedConversation.getBuyer()
        );

        assertNotNull(
                savedConversation.getCreatedAt()
        );

        verify(conversationRepository)
                .findByAdvertisementIdAndBuyerId(
                        1L,
                        2L
                );

        verify(conversationMapper)
                .toResponse(savedConversation);
    }

    @Test
    void create_ShouldThrowException_WhenAdvertisementNotFound() {

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> conversationService.create(1L)
        );

        verify(currentUserProvider, never())
                .getCurrentUser();

        verify(conversationRepository, never())
                .save(any(Conversation.class));

        verify(conversationMapper, never())
                .toResponse(any(Conversation.class));
    }

    @Test
    void create_ShouldThrowException_WhenAdvertisementIsNotActive() {

        Advertisement advertisement = new Advertisement();
        advertisement.setAdvertisementStatus(AdvertisementStatus.INACTIVE);

        User buyer = new User();
        buyer.setId(2L);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(buyer);

        assertThrows(
                ConversationCreationNotAllowedException.class,
                () -> conversationService.create(1L)
        );

        verify(conversationRepository, never())
                .findByAdvertisementIdAndBuyerId(
                        any(),
                        any()
                );

        verify(conversationRepository, never())
                .save(any(Conversation.class));

        verify(conversationMapper, never())
                .toResponse(any(Conversation.class));
    }

    @Test
    void create_ShouldThrowException_WhenBuyerIsAdvertisementSeller() {

        User user = new User();
        user.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setSeller(user);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        assertThrows(
                ConversationWithOwnAdvertisementException.class,
                () -> conversationService.create(1L)
        );

        verify(conversationRepository, never())
                .findByAdvertisementIdAndBuyerId(
                        any(),
                        any()
                );

        verify(conversationRepository, never())
                .save(any(Conversation.class));

        verify(conversationMapper, never())
                .toResponse(any(Conversation.class));
    }

    @Test
    void create_ShouldThrowException_WhenConversationAlreadyExists() {

        User seller = new User();
        seller.setId(1L);

        User buyer = new User();
        buyer.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        Conversation existingConversation =
                new Conversation();

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(buyer);

        when(conversationRepository.findByAdvertisementIdAndBuyerId(
                1L,
                2L
        )).thenReturn(Optional.of(existingConversation));

        assertThrows(
                ConversationAlreadyExistsException.class,
                () -> conversationService.create(1L)
        );

        verify(conversationRepository, never())
                .save(any(Conversation.class));

        verify(conversationMapper, never())
                .toResponse(any(Conversation.class));
    }

    @Test
    void getById_ShouldReturnConversation() {

        User currentUser = new User();
        currentUser.setId(2L);

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setSeller(seller);

        Conversation conversation = new Conversation();
        conversation.setBuyer(currentUser);
        conversation.setAdvertisement(advertisement);

        ConversationResponse expectedResponse =
                mock(ConversationResponse.class);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(conversationRepository.findById(1L))
                .thenReturn(Optional.of(conversation));

        when(conversationMapper.toResponse(conversation))
                .thenReturn(expectedResponse);

        ConversationResponse result =
                conversationService.getById(1L);

        assertEquals(
                expectedResponse,
                result
        );

        verify(currentUserProvider)
                .getCurrentUser();

        verify(conversationRepository)
                .findById(1L);

        verify(conversationMapper)
                .toResponse(conversation);
    }

    @Test
    void getMyConversations_ShouldReturnPageOfConversations() {

        User currentUser = new User();
        currentUser.setId(1L);

        PageRequest pageRequest =
                new PageRequest(
                        1,
                        2
                );

        Conversation conversation1 =
                new Conversation();

        Conversation conversation2 =
                new Conversation();

        ConversationResponse response1 =
                mock(ConversationResponse.class);

        ConversationResponse response2 =
                mock(ConversationResponse.class);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(conversationRepository.findByParticipantId(
                1L,
                pageRequest
        )).thenReturn(List.of(
                conversation1,
                conversation2
        ));

        when(conversationMapper.toResponse(conversation1))
                .thenReturn(response1);

        when(conversationMapper.toResponse(conversation2))
                .thenReturn(response2);

        when(conversationRepository.countByParticipantId(1L))
                .thenReturn(5L);

        PageResponse<ConversationResponse> result =
                conversationService.getMyConversations(pageRequest);

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

        verify(currentUserProvider)
                .getCurrentUser();

        verify(conversationRepository)
                .findByParticipantId(
                        1L,
                        pageRequest
                );

        verify(conversationMapper)
                .toResponse(conversation1);

        verify(conversationMapper)
                .toResponse(conversation2);

        verify(conversationRepository)
                .countByParticipantId(1L);
    }

    @Test
    void getAccessibleConversation_ShouldReturnConversation_WhenUserIsBuyer() {

        User buyer = new User();
        buyer.setId(1L);

        User seller = new User();
        seller.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setSeller(seller);

        Conversation conversation = new Conversation();
        conversation.setBuyer(buyer);
        conversation.setAdvertisement(advertisement);

        when(conversationRepository.findById(1L))
                .thenReturn(Optional.of(conversation));

        Conversation result =
                conversationService.getAccessibleConversation(
                        1L,
                        buyer
                );

        assertSame(
                conversation,
                result
        );

        verify(conversationRepository)
                .findById(1L);
    }

    @Test
    void getAccessibleConversation_ShouldReturnConversation_WhenUserIsSeller() {

        User buyer = new User();
        buyer.setId(1L);

        User seller = new User();
        seller.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setSeller(seller);

        Conversation conversation = new Conversation();
        conversation.setBuyer(buyer);
        conversation.setAdvertisement(advertisement);

        when(conversationRepository.findById(1L))
                .thenReturn(Optional.of(conversation));

        Conversation result =
                conversationService.getAccessibleConversation(
                        1L,
                        seller
                );

        assertSame(
                conversation,
                result
        );

        verify(conversationRepository)
                .findById(1L);
    }

    @Test
    void getAccessibleConversation_ShouldThrowException_WhenConversationNotFound() {

        User currentUser = new User();

        when(conversationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ConversationNotFoundException.class,
                () -> conversationService.getAccessibleConversation(
                        1L,
                        currentUser
                )
        );
    }

    @Test
    void getAccessibleConversation_ShouldThrowException_WhenUserHasNoAccess() {

        User buyer = new User();
        buyer.setId(1L);

        User seller = new User();
        seller.setId(2L);

        User currentUser = new User();
        currentUser.setId(3L);

        Advertisement advertisement = new Advertisement();
        advertisement.setSeller(seller);

        Conversation conversation = new Conversation();
        conversation.setBuyer(buyer);
        conversation.setAdvertisement(advertisement);

        when(conversationRepository.findById(1L))
                .thenReturn(Optional.of(conversation));

        assertThrows(
                ConversationAccessDeniedException.class,
                () -> conversationService.getAccessibleConversation(
                        1L,
                        currentUser
                )
        );
    }


}
