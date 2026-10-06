package ru.drmemex.classifieds.feature.conversation.message.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
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
import ru.drmemex.classifieds.feature.conversation.message.service.MessageService;
import ru.drmemex.classifieds.feature.conversation.service.ConversationService;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.security.provider.CurrentUserProvider;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;

import static ru.drmemex.classifieds.common.util.pagination.PaginationUtils.buildPageResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private static final int MESSAGE_RATE_LIMIT = 20;
    private static final int MESSAGE_RATE_LIMIT_WINDOW_MINUTES = 1;

    private final CurrentUserProvider currentUserProvider;
    private final ConversationService conversationService;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final MessageEncryptionService messageEncryptionService;

    @Override
    @Transactional
    public MessageResponse send(
            Long conversationId,
            MessageRequest request
    ) {

        User currentUser =
                currentUserProvider.getCurrentUser();

        Conversation conversation =
                conversationService.getAccessibleConversation(
                        conversationId,
                        currentUser
                );

        if (conversation
                .getAdvertisement()
                .getAdvertisementStatus()
                != AdvertisementStatus.ACTIVE) {

            throw new MessageSendingNotAllowedException(
                    conversationId
            );
        }

        OffsetDateTime rateLimitWindowStart =
                OffsetDateTime.now().minusMinutes(
                        MESSAGE_RATE_LIMIT_WINDOW_MINUTES
                );

        long recentMessages =
                messageRepository
                        .countByAuthorIdAndCreatedAtAfter(
                                currentUser.getId(),
                                rateLimitWindowStart
                        );

        if (recentMessages >= MESSAGE_RATE_LIMIT) {

            log.warn(
                    "Message rate limit exceeded: userId={}",
                    currentUser.getId()
            );

            throw new MessageRateLimitExceededException();
        }

        EncryptedMessage encryptedMessage =
                messageEncryptionService.encrypt(
                        request.text(),
                        conversationId
                );

        Message message = Message.builder()
                .conversation(conversation)
                .author(currentUser)
                .encryptedText(
                        encryptedMessage.encryptedText()
                )
                .iv(
                        encryptedMessage.iv()
                )
                .createdAt(
                        OffsetDateTime.now()
                )
                .build();

        Message savedMessage =
                messageRepository.save(message);

        log.info(
                "Message sent: messageId={}, conversationId={}, authorId={}",
                savedMessage.getId(),
                conversationId,
                currentUser.getId()
        );

        return toResponse(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> getByConversationId(
            Long conversationId,
            PageRequest pageRequest
    ) {

        User currentUser =
                currentUserProvider.getCurrentUser();

        conversationService.getAccessibleConversation(
                conversationId,
                currentUser
        );

        List<MessageResponse> content =
                messageRepository
                        .findByConversationId(
                                conversationId,
                                pageRequest
                        )
                        .stream()
                        .map(this::toResponse)
                        .toList();

        long totalElements =
                messageRepository.countByConversationId(
                        conversationId
                );

        return buildPageResponse(
                content,
                pageRequest,
                totalElements
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> searchByConversationAndText(
            Long conversationId,
            String text,
            PageRequest pageRequest
    ) {

        User currentUser =
                currentUserProvider.getCurrentUser();

        conversationService.getAccessibleConversation(
                conversationId,
                currentUser
        );

        String normalizedText =
                text.toLowerCase(Locale.ROOT);

        List<MessageResponse> filteredMessages =
                messageRepository
                        .findAllByConversationId(
                                conversationId
                        )
                        .stream()
                        .map(this::toResponse)
                        .filter(message ->
                                message.text()
                                        .toLowerCase(Locale.ROOT)
                                        .contains(normalizedText)
                        )
                        .toList();

        long totalElements =
                filteredMessages.size();

        int fromIndex = Math.min(
                pageRequest.page()
                        * pageRequest.size(),
                filteredMessages.size()
        );

        int toIndex = Math.min(
                fromIndex + pageRequest.size(),
                filteredMessages.size()
        );

        List<MessageResponse> content =
                filteredMessages.subList(
                        fromIndex,
                        toIndex
                );

        return buildPageResponse(
                content,
                pageRequest,
                totalElements
        );
    }

    private MessageResponse toResponse(
            Message message
    ) {

        String text =
                messageEncryptionService.decrypt(
                        message.getEncryptedText(),
                        message.getIv(),
                        message.getConversation().getId()
                );

        return messageMapper.toResponse(
                message,
                text
        );
    }
}