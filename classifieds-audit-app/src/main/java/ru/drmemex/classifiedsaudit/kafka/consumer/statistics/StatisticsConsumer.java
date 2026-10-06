package ru.drmemex.classifiedsaudit.kafka.consumer.statistics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.drmemex.classifieds.kafka.event.AdvertisementLifecycleEvent;
import ru.drmemex.classifieds.kafka.event.UserLifecycleEvent;
import ru.drmemex.classifiedsaudit.kafka.consumer.statistics.service.EventStatisticsService;
import ru.drmemex.classifiedsaudit.kafka.exception.KafkaEventDeserializationException;

import static ru.drmemex.classifieds.kafka.KafkaTopic.ADVERTISEMENT_LIFECYCLE_TOPIC;
import static ru.drmemex.classifieds.kafka.KafkaTopic.USER_LIFECYCLE_TOPIC;
import static ru.drmemex.classifiedsaudit.kafka.config.KafkaConsumerGroup.STATISTICS_GROUP;

@Component
@RequiredArgsConstructor
public class StatisticsConsumer {

    private final EventStatisticsService eventStatisticsService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = ADVERTISEMENT_LIFECYCLE_TOPIC,
            groupId = STATISTICS_GROUP
    )
    public void consumeAdvertisementEvent(
            ConsumerRecord<String, String> record
    ) {
        AdvertisementLifecycleEvent event =
                readAdvertisementEvent(record.value());

        eventStatisticsService.processAdvertisementEvent(event);
    }

    @KafkaListener(
            topics = USER_LIFECYCLE_TOPIC,
            groupId = STATISTICS_GROUP
    )
    public void consumeUserEvent(
            ConsumerRecord<String, String> record
    ) {
        UserLifecycleEvent event =
                readUserEvent(record.value());

        eventStatisticsService.processUserEvent(event);
    }

    private AdvertisementLifecycleEvent readAdvertisementEvent(
            String payload
    ) {
        try {
            return objectMapper.readValue(
                    payload,
                    AdvertisementLifecycleEvent.class
            );
        } catch (JsonProcessingException exception) {
            throw new KafkaEventDeserializationException(
                    "Failed to deserialize advertisement event",
                    exception
            );
        }
    }

    private UserLifecycleEvent readUserEvent(String payload) {
        try {
            return objectMapper.readValue(
                    payload,
                    UserLifecycleEvent.class
            );
        } catch (JsonProcessingException exception) {
            throw new KafkaEventDeserializationException(
                    "Failed to deserialize user event",
                    exception
            );
        }
    }
}