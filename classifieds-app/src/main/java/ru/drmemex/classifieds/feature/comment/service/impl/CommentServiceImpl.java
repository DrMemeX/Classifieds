package ru.drmemex.classifieds.feature.comment.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.service.AdvertisementService;
import ru.drmemex.classifieds.feature.comment.dto.CommentRequest;
import ru.drmemex.classifieds.feature.comment.dto.CommentResponse;
import ru.drmemex.classifieds.feature.comment.entity.Comment;
import ru.drmemex.classifieds.feature.comment.exception.CommentAccessDeniedException;
import ru.drmemex.classifieds.feature.comment.exception.CommentCreationNotAllowedException;
import ru.drmemex.classifieds.feature.comment.exception.CommentNotFoundException;
import ru.drmemex.classifieds.feature.comment.exception.CommentRateLimitExceededException;
import ru.drmemex.classifieds.feature.comment.mapper.CommentMapper;
import ru.drmemex.classifieds.feature.comment.repository.CommentRepository;
import ru.drmemex.classifieds.feature.comment.service.CommentService;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.security.provider.CurrentUserProvider;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CurrentUserProvider currentUserProvider;
    private final CommentRepository commentRepository;
    private final AdvertisementService advertisementService;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentResponse create(
            Long advertisementId,
            CommentRequest request
    ) {

        User currentUser = currentUserProvider.getCurrentUser();

        Advertisement advertisement = advertisementService
                .getAdvertisement(advertisementId);

        if (advertisement.getAdvertisementStatus()
                != AdvertisementStatus.ACTIVE) {
            throw new CommentCreationNotAllowedException(
                    advertisementId
            );
        }

        OffsetDateTime oneMinuteAgo =
                OffsetDateTime.now().minusMinutes(1);

        long recentComments = commentRepository
                .countByAuthorIdAndCreatedAtAfter(
                        currentUser.getId(),
                        oneMinuteAgo
                );

        if (recentComments >= 5) {
            throw new CommentRateLimitExceededException();
        }

        Comment comment = Comment.builder()
                .advertisement(advertisement)
                .author(currentUser)
                .text(request.text())
                .createdAt(OffsetDateTime.now())
                .build();

        return commentMapper.toResponse(
                commentRepository.save(comment)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getByAdvertisementId(
            Long advertisementId
    ) {

        advertisementService.getById(advertisementId);

        return commentRepository
                .findByAdvertisementId(advertisementId)
                .stream()
                .map(commentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long commentId) {

        User currentUser = currentUserProvider.getCurrentUser();

        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() ->
                        new CommentNotFoundException(commentId)
                );

        if (currentUser.getRole() != UserRole.ADMIN
                && !comment.getAuthor()
                .getId()
                .equals(currentUser.getId())) {
            throw new CommentAccessDeniedException(
                    commentId
            );
        }

        commentRepository.delete(comment);
    }
}