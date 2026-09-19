package ru.drmemex.classifieds.feature.comment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
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

import static ru.drmemex.classifieds.common.util.pagination.PaginationUtils.buildPageResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final int COMMENT_RATE_LIMIT = 5;
    private static final int COMMENT_RATE_LIMIT_WINDOW_MINUTES = 1;

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

        OffsetDateTime rateLimitWindowStart =
                OffsetDateTime.now().minusMinutes(
                        COMMENT_RATE_LIMIT_WINDOW_MINUTES
                );

        long recentComments = commentRepository
                .countByAuthorIdAndCreatedAtAfter(
                        currentUser.getId(),
                        rateLimitWindowStart
                );

        if (recentComments >= COMMENT_RATE_LIMIT) {

            log.warn(
                    "Comment rate limit exceeded: userId={}",
                    currentUser.getId()
            );

            throw new CommentRateLimitExceededException();
        }

        Comment comment = Comment.builder()
                .advertisement(advertisement)
                .author(currentUser)
                .text(request.text())
                .createdAt(OffsetDateTime.now())
                .build();

        Comment savedComment =
                commentRepository.save(comment);

        log.info(
                "Comment created: commentId={}, advertisementId={}, authorId={}",
                savedComment.getId(),
                advertisementId,
                currentUser.getId()
        );

        return commentMapper.toResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> getByAdvertisementId(
            Long advertisementId,
            PageRequest pageRequest
    ) {

        advertisementService.getById(advertisementId);

        List<CommentResponse> content = commentRepository
                .findByAdvertisementId(
                        advertisementId,
                        pageRequest
                )
                .stream()
                .map(commentMapper::toResponse)
                .toList();

        long totalElements = commentRepository
                .countByAdvertisementId(advertisementId);

        return buildPageResponse(
                content,
                pageRequest,
                totalElements
        );
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
                && currentUser.getRole() != UserRole.SUPER_ADMIN
                && !comment.getAuthor()
                .getId()
                .equals(currentUser.getId())) {
            throw new CommentAccessDeniedException(
                    commentId
            );
        }

        commentRepository.delete(comment);

        log.info(
                "Comment deleted: commentId={}, deletedByUserId={}",
                commentId,
                currentUser.getId()
        );
    }
}