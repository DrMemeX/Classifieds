package ru.drmemex.classifiedsaudit.kafka.consumer.history.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.kafka.event.AdvertisementLifecycleEvent;
import ru.drmemex.classifieds.kafka.event.UserLifecycleEvent;
import ru.drmemex.classifiedsaudit.kafka.consumer.history.entity.EventHistory;
import ru.drmemex.classifiedsaudit.kafka.consumer.history.repository.EventHistoryRepository;
import ru.drmemex.classifiedsaudit.kafka.consumer.history.service.EventHistoryService;
import ru.drmemex.classifiedsaudit.kafka.consumer.idempotency.service.ProcessedKafkaEventService;

import java.time.OffsetDateTime;

import static ru.drmemex.classifiedsaudit.kafka.config.KafkaConsumerGroup.HISTORY_GROUP;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventHistoryServiceImpl implements EventHistoryService {

    private static final String ADVERTISEMENT_AGGREGATE_TYPE = "ADVERTISEMENT";
    private static final String USER_AGGREGATE_TYPE = "USER";

    private final EventHistoryRepository eventHistoryRepository;

    private final ProcessedKafkaEventService processedKafkaEventService;

    @Override
    @Transactional
    public void saveAdvertisementEvent(
            AdvertisementLifecycleEvent event,
            String topic,
            String eventKey,
            String payload
    ) {
        boolean firstProcessing = processedKafkaEventService.tryMarkProcessed(
                HISTORY_GROUP,
                event.eventId()
        );

        if (!firstProcessing) {
            return;
        }

        EventHistory eventHistory = EventHistory.builder()
                .eventId(event.eventId())
                .aggregateType(ADVERTISEMENT_AGGREGATE_TYPE)
                .aggregateId(event.advertisementId())
                .eventType(event.eventType().name())
                .topic(topic)
                .eventKey(eventKey)
                .payload(payload)
                .occurredAt(event.occurredAt())
                .consumedAt(OffsetDateTime.now())
                .build();

        eventHistoryRepository.save(eventHistory);

        log.info(
                "Advertisement event saved to history: eventId={}, advertisementId={}, type={}",
                event.eventId(),
                event.advertisementId(),
                event.eventType()
        );
    }

    @Override
    @Transactional
    public void saveUserEvent(
            UserLifecycleEvent event,
            String topic,
            String eventKey,
            String payload
    ) {
        boolean firstProcessing = processedKafkaEventService.tryMarkProcessed(
                HISTORY_GROUP,
                event.eventId()
        );

        if (!firstProcessing) {
            return;
        }

        EventHistory eventHistory = EventHistory.builder()
                .eventId(event.eventId())
                .aggregateType(USER_AGGREGATE_TYPE)
                .aggregateId(event.userId())
                .eventType(event.eventType().name())
                .topic(topic)
                .eventKey(eventKey)
                .payload(payload)
                .occurredAt(event.occurredAt())
                .consumedAt(OffsetDateTime.now())
                .build();

        eventHistoryRepository.save(eventHistory);

        log.info(
                "User event saved to history: eventId={}, userId={}, type={}",
                event.eventId(),
                event.userId(),
                event.eventType()
        );
    }
}