package ru.drmemex.classifieds.kafka.consumer.history;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.drmemex.classifieds.kafka.consumer.history.service.EventHistoryService;
import ru.drmemex.classifieds.kafka.event.AdvertisementLifecycleEvent;
import ru.drmemex.classifieds.kafka.event.UserLifecycleEvent;

import static ru.drmemex.classifieds.kafka.config.KafkaConsumerGroup.HISTORY_GROUP;
import static ru.drmemex.classifieds.kafka.config.KafkaTopicConfig.ADVERTISEMENT_LIFECYCLE_TOPIC;
import static ru.drmemex.classifieds.kafka.config.KafkaTopicConfig.USER_LIFECYCLE_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryConsumer {

    private final EventHistoryService eventHistoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = ADVERTISEMENT_LIFECYCLE_TOPIC,
            groupId = HISTORY_GROUP
    )
    public void consumeAdvertisementEvent(
            ConsumerRecord<String, String> record
    ) {
        AdvertisementLifecycleEvent event =
                readAdvertisementEvent(record.value());

        eventHistoryService.saveAdvertisementEvent(
                event,
                record.topic(),
                record.key(),
                record.value()
        );

        log.info(
                "Advertisement event saved to history: eventId={}, type={}",
                event.eventId(),
                event.eventType()
        );
    }

    @KafkaListener(
            topics = USER_LIFECYCLE_TOPIC,
            groupId = HISTORY_GROUP
    )
    public void consumeUserEvent(
            ConsumerRecord<String, String> record
    ) {
        UserLifecycleEvent event =
                readUserEvent(record.value());

        eventHistoryService.saveUserEvent(
                event,
                record.topic(),
                record.key(),
                record.value()
        );

        log.info(
                "User event saved to history: eventId={}, type={}",
                event.eventId(),
                event.eventType()
        );
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
            throw new IllegalStateException(
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
            throw new IllegalStateException(
                    "Failed to deserialize user event",
                    exception
            );
        }
    }
}