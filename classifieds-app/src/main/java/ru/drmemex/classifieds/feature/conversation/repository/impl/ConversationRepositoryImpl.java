package ru.drmemex.classifieds.feature.conversation.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.feature.conversation.entity.Conversation;
import ru.drmemex.classifieds.feature.conversation.repository.ConversationRepository;

import java.util.List;
import java.util.Optional;

import static ru.drmemex.classifieds.common.util.pagination.PaginationUtils.applyPagination;

@Repository
public class ConversationRepositoryImpl implements ConversationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Conversation save(Conversation conversation) {
        entityManager.persist(conversation);
        return conversation;
    }

    @Override
    public Optional<Conversation> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(Conversation.class, id)
        );
    }

    @Override
    public Optional<Conversation> findByAdvertisementIdAndBuyerId(
            Long advertisementId,
            Long buyerId
    ) {
        TypedQuery<Conversation> query = entityManager.createQuery(
                """
                        SELECT c
                        FROM Conversation c
                        WHERE c.advertisement.id = :advertisementId
                        AND c.buyer.id = :buyerId
                        """, Conversation.class
        );

        query.setParameter("advertisementId", advertisementId);
        query.setParameter("buyerId", buyerId);

        return query.getResultStream().findFirst();
    }

    @Override
    public List<Conversation> findByParticipantId(
            Long userId,
            PageRequest pageRequest
    ) {
        TypedQuery<Conversation> query = entityManager.createQuery(
                """
                        SELECT c
                        FROM Conversation c
                        WHERE c.buyer.id = :userId
                        OR c.advertisement.seller.id = :userId
                        ORDER BY c.createdAt DESC, c.id DESC
                        """,
                Conversation.class
        );

        query.setParameter("userId", userId);

        applyPagination(
                query,
                pageRequest
        );

        return query.getResultList();
    }

    @Override
    public long countByParticipantId(Long userId) {
        return entityManager.createQuery(
                        """
                                SELECT COUNT(c)
                                FROM Conversation c
                                WHERE c.buyer.id = :userId
                                OR c.advertisement.seller.id = :userId
                                """,
                        Long.class
                )
                .setParameter("userId", userId)
                .getSingleResult();
    }
}