package ru.drmemex.classifieds.feature.comment.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.feature.comment.entity.Comment;
import ru.drmemex.classifieds.feature.comment.repository.CommentRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Comment save(Comment comment) {
        entityManager.persist(comment);
        return comment;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(Comment.class, id)
        );
    }

    @Override
    public List<Comment> findByAdvertisementId(Long advertisementId) {
        TypedQuery<Comment> query = entityManager.createQuery(
                """
                        SELECT c
                        FROM Comment c
                        WHERE c.advertisement.id = :advertisementId
                        ORDER BY c.createdAt ASC, c.id ASC
                        """,
                Comment.class
        );

        query.setParameter("advertisementId", advertisementId);

        return query.getResultList();
    }

    @Override
    public void delete(Comment comment) {
        entityManager.remove(comment);
    }

    @Override
    public long countByAuthorIdAndCreatedAtAfter(
            Long authorId,
            OffsetDateTime createdAfter
    ) {
        return entityManager.createQuery(
                        """
                                SELECT COUNT(c)
                                FROM Comment c
                                WHERE c.author.id = :authorId
                                AND c.createdAt >= :createdAfter
                                """,
                        Long.class
                )
                .setParameter("authorId", authorId)
                .setParameter("createdAfter", createdAfter)
                .getSingleResult();
    }
}