package ru.drmemex.classifieds.feature.conversation.message.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.conversation.entity.Conversation;
import ru.drmemex.classifieds.feature.conversation.message.dto.MessageRequest;
import ru.drmemex.classifieds.feature.conversation.message.dto.MessageResponse;
import ru.drmemex.classifieds.feature.conversation.message.entity.Message;
import ru.drmemex.classifieds.feature.conversation.message.exception.MessageSendingNotAllowedException;
import ru.drmemex.classifieds.feature.conversation.message.mapper.MessageMapper;
import ru.drmemex.classifieds.feature.conversation.message.repository.MessageRepository;
import ru.drmemex.classifieds.feature.conversation.message.service.MessageService;
import ru.drmemex.classifieds.feature.conversation.service.ConversationService;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.security.provider.CurrentUserProvider;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final CurrentUserProvider currentUserProvider;
    private final ConversationService conversationService;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;


    @Override
    @Transactional
    public MessageResponse send(
            Long conversationId,
            MessageRequest request
    ) {

        User currentUser = currentUserProvider.getCurrentUser();

        Conversation conversation = conversationService.getAccessibleConversation(
                conversationId,
                currentUser
        );

        if (conversation.getAdvertisement().getAdvertisementStatus()
                != AdvertisementStatus.ACTIVE) {
            throw new MessageSendingNotAllowedException(
                    conversationId
            );
        }

        Message message = Message.builder()
                .conversation(conversation)
                .author(currentUser)
                .text(request.text())
                .createdAt(OffsetDateTime.now())
                .build();

        messageRepository.save(message);

        return messageMapper.toResponse(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getByConversationId(Long conversationId) {

        User currentUser = currentUserProvider.getCurrentUser();

        conversationService.getAccessibleConversation(
                conversationId,
                currentUser
        );

        return messageRepository
                .findByConversationId(conversationId)
                .stream()
                .map(messageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> searchByConversationAndText(
            Long conversationId,
            String text
    ) {

        User currentUser = currentUserProvider.getCurrentUser();

        conversationService.getAccessibleConversation(
                conversationId,
                currentUser
        );

        return messageRepository
                .searchByConversationIdAndText(
                        conversationId,
                        text
                )
                .stream()
                .map(messageMapper::toResponse)
                .toList();
    }
}