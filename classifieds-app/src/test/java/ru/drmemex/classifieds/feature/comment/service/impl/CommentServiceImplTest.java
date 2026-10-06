package ru.drmemex.classifieds.feature.comment.service.impl;

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
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.security.provider.CurrentUserProvider;

import java.time.OffsetDateTime;
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
class CommentServiceImplTest {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AdvertisementService advertisementService;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void create_ShouldCreateComment_WhenRecentCommentsCountIs4() {

        CommentRequest request =
                new CommentRequest("Хорошее объявление");

        User currentUser = new User();
        currentUser.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setAdvertisementStatus(
                AdvertisementStatus.ACTIVE
        );

        CommentResponse expectedResponse =
                mock(CommentResponse.class);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(advertisementService.getAdvertisement(1L))
                .thenReturn(advertisement);

        when(commentRepository.countByAuthorIdAndCreatedAtAfter(
                any(),
                any(OffsetDateTime.class)
        )).thenReturn(4L);

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(commentMapper.toResponse(any(Comment.class)))
                .thenReturn(expectedResponse);

        CommentResponse result =
                commentService.create(
                        1L,
                        request
                );

        assertEquals(
                expectedResponse,
                result
        );

        ArgumentCaptor<Comment> commentCaptor =
                ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository)
                .save(commentCaptor.capture());

        Comment savedComment =
                commentCaptor.getValue();

        assertSame(
                advertisement,
                savedComment.getAdvertisement()
        );

        assertSame(
                currentUser,
                savedComment.getAuthor()
        );

        assertEquals(
                "Хорошее объявление",
                savedComment.getText()
        );

        assertNotNull(
                savedComment.getCreatedAt()
        );

        verify(commentMapper)
                .toResponse(savedComment);
    }

    @Test
    void create_ShouldThrowException_WhenAdvertisementIsNotActive() {

        CommentRequest request =
                new CommentRequest("Комментарий");

        User currentUser = new User();

        Advertisement advertisement = new Advertisement();
        advertisement.setAdvertisementStatus(
                AdvertisementStatus.INACTIVE
        );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(advertisementService.getAdvertisement(1L))
                .thenReturn(advertisement);

        assertThrows(
                CommentCreationNotAllowedException.class,
                () -> commentService.create(
                        1L,
                        request
                )
        );

        verify(commentRepository, never())
                .countByAuthorIdAndCreatedAtAfter(
                        any(),
                        any(OffsetDateTime.class)
                );

        verify(commentRepository, never())
                .save(any(Comment.class));

        verify(commentMapper, never())
                .toResponse(any(Comment.class));
    }

    @Test
    void create_ShouldThrowException_WhenRateLimitExceeded() {

        CommentRequest request =
                new CommentRequest("Комментарий");

        User currentUser = new User();
        currentUser.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setAdvertisementStatus(
                AdvertisementStatus.ACTIVE
        );

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(advertisementService.getAdvertisement(1L))
                .thenReturn(advertisement);

        when(commentRepository.countByAuthorIdAndCreatedAtAfter(
                any(),
                any(OffsetDateTime.class)
        )).thenReturn(5L);

        assertThrows(
                CommentRateLimitExceededException.class,
                () -> commentService.create(
                        1L,
                        request
                )
        );

        verify(commentRepository, never())
                .save(any(Comment.class));

        verify(commentMapper, never())
                .toResponse(any(Comment.class));
    }

    @Test
    void getByAdvertisementId_ShouldReturnPageOfComments() {

        PageRequest pageRequest =
                new PageRequest(
                        1,
                        2
                );

        Comment comment1 = new Comment();
        Comment comment2 = new Comment();

        CommentResponse response1 =
                mock(CommentResponse.class);

        CommentResponse response2 =
                mock(CommentResponse.class);

        when(commentRepository.findByAdvertisementId(
                1L,
                pageRequest
        )).thenReturn(List.of(
                comment1,
                comment2
        ));

        when(commentMapper.toResponse(comment1))
                .thenReturn(response1);

        when(commentMapper.toResponse(comment2))
                .thenReturn(response2);

        when(commentRepository.countByAdvertisementId(1L))
                .thenReturn(5L);

        PageResponse<CommentResponse> result =
                commentService.getByAdvertisementId(
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

        verify(advertisementService)
                .getById(1L);

        verify(commentRepository)
                .findByAdvertisementId(
                        1L,
                        pageRequest
                );

        verify(commentMapper)
                .toResponse(comment1);

        verify(commentMapper)
                .toResponse(comment2);

        verify(commentRepository)
                .countByAdvertisementId(1L);
    }

    @Test
    void delete_ShouldDeleteComment_WhenUserIsAuthor() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);

        Comment comment = new Comment();
        comment.setAuthor(currentUser);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        commentService.delete(1L);

        verify(commentRepository)
                .delete(comment);
    }

    @Test
    void delete_ShouldDeleteComment_WhenUserIsAdmin() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.ADMIN);

        Comment comment = new Comment();

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        commentService.delete(1L);

        verify(commentRepository)
                .delete(comment);
    }

    @Test
    void delete_ShouldThrowException_WhenCommentNotFound() {

        User currentUser = new User();

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(commentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CommentNotFoundException.class,
                () -> commentService.delete(1L)
        );

        verify(commentRepository, never())
                .delete(any(Comment.class));
    }

    @Test
    void delete_ShouldThrowException_WhenUserHasNoAccess() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.USER);

        User author = new User();
        author.setId(2L);

        Comment comment = new Comment();
        comment.setAuthor(author);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        assertThrows(
                CommentAccessDeniedException.class,
                () -> commentService.delete(1L)
        );

        verify(commentRepository, never())
                .delete(any(Comment.class));
    }
}