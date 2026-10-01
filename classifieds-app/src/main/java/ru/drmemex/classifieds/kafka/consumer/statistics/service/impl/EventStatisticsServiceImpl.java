package ru.drmemex.classifieds.kafka.consumer.statistics.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.kafka.consumer.idempotency.service.ProcessedKafkaEventService;
import ru.drmemex.classifieds.kafka.consumer.statistics.repository.EventStatisticsRepository;
import ru.drmemex.classifieds.kafka.consumer.statistics.service.EventStatisticsService;
import ru.drmemex.classifieds.kafka.event.AdvertisementLifecycleEvent;
import ru.drmemex.classifieds.kafka.event.UserLifecycleEvent;

import java.time.OffsetDateTime;

import static ru.drmemex.classifieds.kafka.config.KafkaConsumerGroup.STATISTICS_GROUP;

@Service
@RequiredArgsConstructor
public class EventStatisticsServiceImpl implements EventStatisticsService {

    private static final String ADVERTISEMENT_AGGREGATE_TYPE = "ADVERTISEMENT";
    private static final String USER_AGGREGATE_TYPE = "USER";

    private final EventStatisticsRepository eventStatisticsRepository;

    private final ProcessedKafkaEventService processedKafkaEventService;

    @Override
    @Transactional
    public void processAdvertisementEvent(AdvertisementLifecycleEvent event) {

        boolean firstProcessing = processedKafkaEventService.tryMarkProcessed(
                STATISTICS_GROUP,
                event.eventId()
        );

        if (!firstProcessing) {
            return;
        }

        eventStatisticsRepository.increment(
                event.occurredAt().toLocalDate(),
                ADVERTISEMENT_AGGREGATE_TYPE,
                event.eventType().name(),
                OffsetDateTime.now()
        );
    }

    @Override
    @Transactional
    public void processUserEvent(UserLifecycleEvent event) {

        boolean firstProcessing = processedKafkaEventService.tryMarkProcessed(
                STATISTICS_GROUP,
                event.eventId()
        );

        if (!firstProcessing) {
            return;
        }

        eventStatisticsRepository.increment(
                event.occurredAt().toLocalDate(),
                USER_AGGREGATE_TYPE,
                event.eventType().name(),
                OffsetDateTime.now()
        );
    }
}