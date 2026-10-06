package ru.drmemex.classifieds.feature.conversation.message.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.feature.conversation.message.entity.Message;
import ru.drmemex.classifieds.feature.conversation.message.repository.MessageRepository;

import java.time.OffsetDateTime;
import java.util.List;

import static ru.drmemex.classifieds.common.util.pagination.PaginationUtils.applyPagination;

@Repository
public class MessageRepositoryImpl implements MessageRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Message save(Message message) {
        entityManager.persist(message);
        return message;
    }

    @Override
    public List<Message> findByConversationId(
            Long conversationId,
            PageRequest pageRequest
    ) {
        TypedQuery<Message> query = entityManager.createQuery(
                """
                        SELECT m
                        FROM Message m
                        WHERE m.conversation.id = :conversationId
                        ORDER BY m.createdAt ASC, m.id ASC
                        """,
                Message.class
        );

        query.setParameter("conversationId", conversationId);

        applyPagination(query, pageRequest);

        return query.getResultList();
    }

    @Override
    public long countByConversationId(Long conversationId) {
        return entityManager.createQuery(
                        """
                                SELECT COUNT(m)
                                FROM Message m
                                WHERE m.conversation.id = :conversationId
                                """,
                        Long.class
                )
                .setParameter("conversationId", conversationId)
                .getSingleResult();
    }

    @Override
    public List<Message> searchByConversationIdAndText(
            Long conversationId,
            String text,
            PageRequest pageRequest
    ) {
        TypedQuery<Message> query = entityManager.createQuery(
                """
                        SELECT m
                        FROM Message m
                        WHERE m.conversation.id = :conversationId
                        AND LOWER(m.text) LIKE LOWER(:text)
                        ORDER BY m.createdAt ASC, m.id ASC
                        """,
                Message.class
        );

        query.setParameter("conversationId", conversationId);

        query.setParameter("text", "%" + text + "%");

        applyPagination(
                query,
                pageRequest
        );

        return query.getResultList();
    }

    @Override
    public long countByConversationIdAndText(
            Long conversationId,
            String text
    ) {
        return entityManager.createQuery(
                        """
                                SELECT COUNT(m)
                                FROM Message m
                                WHERE m.conversation.id = :conversationId
                                AND LOWER(m.text) LIKE LOWER(:text)
                                """,
                        Long.class
                )
                .setParameter("conversationId", conversationId)
                .setParameter("text", "%" + text + "%")
                .getSingleResult();
    }

    @Override
    public long countByAuthorIdAndCreatedAtAfter(
            Long authorId,
            OffsetDateTime createdAfter
    ) {
        return entityManager.createQuery(
                        """
                                SELECT COUNT(m)
                                FROM Message m
                                WHERE m.author.id = :authorId
                                AND m.createdAt >= :createdAfter
                                """,
                        Long.class
                )
                .setParameter("authorId", authorId)
                .setParameter("createdAfter", createdAfter)
                .getSingleResult();
    }
}