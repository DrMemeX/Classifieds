package ru.drmemex.classifiedsaudit.kafka.consumer.statistics.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifiedsaudit.kafka.consumer.statistics.repository.EventStatisticsRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Repository
public class EventStatisticsRepositoryImpl implements EventStatisticsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void increment(
            LocalDate eventDate,
            String aggregateType,
            String eventType,
            OffsetDateTime updatedAt
    ) {
        entityManager.createNativeQuery(
                        """
                                INSERT INTO event_statistics (
                                    event_date,
                                    aggregate_type,
                                    event_type,
                                    event_count,
                                    updated_at
                                )
                                VALUES (
                                    :eventDate,
                                    :aggregateType,
                                    :eventType,
                                    1,
                                    :updatedAt
                                )
                                ON CONFLICT (
                                    event_date,
                                    aggregate_type,
                                    event_type
                                )
                                DO UPDATE
                                SET event_count = event_statistics.event_count + 1,
                                    updated_at = EXCLUDED.updated_at
                                """
                )
                .setParameter("eventDate", eventDate)
                .setParameter("aggregateType", aggregateType)
                .setParameter("eventType", eventType)
                .setParameter("updatedAt", updatedAt)
                .executeUpdate();
    }
}