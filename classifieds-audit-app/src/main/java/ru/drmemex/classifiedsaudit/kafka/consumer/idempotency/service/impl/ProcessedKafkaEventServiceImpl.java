package ru.drmemex.classifiedsaudit.kafka.consumer.idempotency.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.drmemex.classifiedsaudit.kafka.consumer.idempotency.repository.ProcessedKafkaEventRepository;
import ru.drmemex.classifiedsaudit.kafka.consumer.idempotency.service.ProcessedKafkaEventService;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessedKafkaEventServiceImpl implements ProcessedKafkaEventService {

    private final ProcessedKafkaEventRepository processedKafkaEventRepository;

    @Override
    public boolean tryMarkProcessed(
            String consumerGroup,
            UUID eventId
    ) {
        return processedKafkaEventRepository.tryMarkProcessed(
                consumerGroup,
                eventId,
                OffsetDateTime.now()
        );
    }
}