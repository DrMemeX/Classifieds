package ru.drmemex.classifiedsaudit.kafka.consumer.idempotency.service;

import java.util.UUID;

public interface ProcessedKafkaEventService {

    boolean tryMarkProcessed(
            String consumerGroup,
            UUID eventId
    );
}