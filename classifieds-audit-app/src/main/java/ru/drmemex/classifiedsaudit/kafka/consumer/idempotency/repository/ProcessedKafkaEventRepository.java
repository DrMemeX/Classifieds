package ru.drmemex.classifiedsaudit.kafka.consumer.idempotency.repository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface ProcessedKafkaEventRepository {

    boolean tryMarkProcessed(
            String consumerGroup,
            UUID eventId,
            OffsetDateTime processedAt
    );
}