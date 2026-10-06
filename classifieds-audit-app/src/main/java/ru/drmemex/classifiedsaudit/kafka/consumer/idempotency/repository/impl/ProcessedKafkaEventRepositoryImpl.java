package ru.drmemex.classifiedsaudit.kafka.consumer.idempotency.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifiedsaudit.kafka.consumer.idempotency.repository.ProcessedKafkaEventRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public class ProcessedKafkaEventRepositoryImpl
        implements ProcessedKafkaEventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean tryMarkProcessed(
            String consumerGroup,
            UUID eventId,
            OffsetDateTime processedAt
    ) {
        int insertedRows = entityManager.createNativeQuery(
                        """
                                INSERT INTO processed_kafka_events (
                                    consumer_group,
                                    event_id,
                                    processed_at
                                )
                                VALUES (
                                    :consumerGroup,
                                    :eventId,
                                    :processedAt
                                )
                                ON CONFLICT (
                                    consumer_group,
                                    event_id
                                )
                                DO NOTHING
                                """
                )
                .setParameter("consumerGroup", consumerGroup)
                .setParameter("eventId", eventId)
                .setParameter("processedAt", processedAt)
                .executeUpdate();

        return insertedRows == 1;
    }
}