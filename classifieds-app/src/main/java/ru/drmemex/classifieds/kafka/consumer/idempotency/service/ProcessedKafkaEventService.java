package ru.drmemex.classifieds.kafka.consumer.idempotency.service;

import java.util.UUID;

public interface ProcessedKafkaEventService {

    boolean tryMarkProcessed(
            String consumerGroup,
            UUID eventId
    );
}