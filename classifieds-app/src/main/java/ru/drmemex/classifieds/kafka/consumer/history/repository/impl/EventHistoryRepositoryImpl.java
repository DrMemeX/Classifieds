package ru.drmemex.classifieds.kafka.consumer.history.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.kafka.consumer.history.entity.EventHistory;
import ru.drmemex.classifieds.kafka.consumer.history.repository.EventHistoryRepository;

@Repository
public class EventHistoryRepositoryImpl implements EventHistoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public EventHistory save(EventHistory eventHistory) {
        entityManager.persist(eventHistory);
        return eventHistory;
    }
}