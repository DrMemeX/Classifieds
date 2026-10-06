package ru.drmemex.classifieds.kafka.outbox.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.kafka.outbox.entity.OutboxEvent;
import ru.drmemex.classifieds.kafka.outbox.repository.OutboxEventRepository;

import java.util.List;

@Repository
public class OutboxEventRepositoryImpl implements OutboxEventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public OutboxEvent save(OutboxEvent event) {
        entityManager.persist(event);
        return event;
    }

    @Override
    public OutboxEvent update(OutboxEvent event) {
        return entityManager.merge(event);
    }

    @Override
    public List<OutboxEvent> findUnpublished(int limit) {
        TypedQuery<OutboxEvent> query = entityManager.createQuery(
                """
                        SELECT e
                        FROM OutboxEvent e
                        WHERE e.publishedAt IS NULL
                        ORDER BY e.createdAt ASC
                        """,
                OutboxEvent.class
        );

        query.setMaxResults(limit);

        return query.getResultList();
    }
}
