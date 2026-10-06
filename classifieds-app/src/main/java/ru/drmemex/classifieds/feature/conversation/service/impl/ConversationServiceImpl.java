package ru.drmemex.classifieds.feature.conversation.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.drmemex.classifieds.feature.conversation.service.ConversationService;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.security.provider.CurrentUserProvider;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final AdvertisementRepository advertisementRepository;
    private final ConversationMapper conversationMapper;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public ConversationResponse create(Long advertisementId) {

        Advertisement advertisement = advertisementRepository
                .findById(advertisementId)
                .orElseThrow(() ->
                        new AdvertisementNotFoundException(advertisementId));

        User buyer = currentUserProvider.getCurrentUser();

        if (advertisement.getAdvertisementStatus() != AdvertisementStatus.ACTIVE) {
            throw new ConversationCreationNotAllowedException(
                    advertisementId
            );
        }

        if (advertisement.getSeller().getId().equals(buyer.getId())) {
            throw new ConversationWithOwnAdvertisementException(
                    advertisementId
            );
        }

        if (conversationRepository.findByAdvertisementIdAndBuyerId(
                        advertisementId,
                        buyer.getId()
                )
                .isPresent()) {
            throw new ConversationAlreadyExistsException(
                    advertisementId
            );
        }

        Conversation conversation = Conversation.builder()
                .advertisement(advertisement)
                .buyer(buyer)
                .createdAt(OffsetDateTime.now())
                .build();

        conversationRepository.save(conversation);

        return conversationMapper.toResponse(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getById(Long conversationId) {

        User currentUser = currentUserProvider.getCurrentUser();

        Conversation conversation = getAccessibleConversation(
                conversationId,
                currentUser
        );

        return conversationMapper.toResponse(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getMyConversations() {

        User currentUser = currentUserProvider.getCurrentUser();

        return conversationRepository
                .findByParticipantId(currentUser.getId())
                .stream()
                .map(conversationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Conversation getAccessibleConversation(
            Long conversationId,
            User currentUser
    ) {
        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() ->
                        new ConversationNotFoundException(conversationId));

        boolean isBuyer = conversation.getBuyer()
                .getId()
                .equals(currentUser.getId());

        boolean isSeller = conversation.getAdvertisement()
                .getSeller()
                .getId()
                .equals(currentUser.getId());

        if (!isBuyer && !isSeller) {
            throw new ConversationAccessDeniedException(
                    conversationId
            );
        }

        return conversation;
    }
}
