package ru.drmemex.classifieds.kafka.outbox.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.kafka.event.AdvertisementLifecycleEvent;
import ru.drmemex.classifieds.kafka.event.UserLifecycleEvent;
import ru.drmemex.classifieds.kafka.outbox.entity.OutboxEvent;
import ru.drmemex.classifieds.kafka.outbox.repository.OutboxEventRepository;
import ru.drmemex.classifieds.kafka.outbox.service.OutboxService;

import java.time.OffsetDateTime;

import static ru.drmemex.classifieds.kafka.config.KafkaTopicConfig.ADVERTISEMENT_LIFECYCLE_TOPIC;
import static ru.drmemex.classifieds.kafka.config.KafkaTopicConfig.USER_LIFECYCLE_TOPIC;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private static final String ADVERTISEMENT_AGGREGATE_TYPE = "ADVERTISEMENT";
    private static final String USER_AGGREGATE_TYPE = "USER";

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void saveAdvertisementEvent(AdvertisementLifecycleEvent event) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(event.eventId())
                .aggregateType(ADVERTISEMENT_AGGREGATE_TYPE)
                .aggregateId(event.advertisementId())
                .eventType(event.eventType().name())
                .topic(ADVERTISEMENT_LIFECYCLE_TOPIC)
                .eventKey(event.advertisementId().toString())
                .payload(toJson(event))
                .createdAt(OffsetDateTime.now())
                .build();

        outboxEventRepository.save(outboxEvent);
    }

    @Override
    @Transactional
    public void saveUserEvent(UserLifecycleEvent event) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .id(event.eventId())
                .aggregateType(USER_AGGREGATE_TYPE)
                .aggregateId(event.userId())
                .eventType(event.eventType().name())
                .topic(USER_LIFECYCLE_TOPIC)
                .eventKey(event.userId().toString())
                .payload(toJson(event))
                .createdAt(OffsetDateTime.now())
                .build();

        outboxEventRepository.save(outboxEvent);
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox event", e);
        }
    }
}
