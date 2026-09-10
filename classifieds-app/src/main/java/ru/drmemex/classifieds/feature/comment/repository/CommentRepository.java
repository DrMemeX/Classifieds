package ru.drmemex.classifieds.feature.comment.repository;

import ru.drmemex.classifieds.feature.comment.entity.Comment;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    Comment save(Comment comment);

    Optional<Comment> findById(Long id);

    List<Comment> findByAdvertisementId(Long advertisementId);

    void delete(Comment comment);

    long countByAuthorIdAndCreatedAtAfter(
            Long authorId,
            OffsetDateTime createdAfter
    );
}