package ru.drmemex.classifiedsaudit.kafka.consumer.history.service;

import ru.drmemex.classifieds.kafka.event.AdvertisementLifecycleEvent;
import ru.drmemex.classifieds.kafka.event.UserLifecycleEvent;

public interface EventHistoryService {

    void saveAdvertisementEvent(
            AdvertisementLifecycleEvent event,
            String topic,
            String eventKey,
            String payload
    );

    void saveUserEvent(
            UserLifecycleEvent event,
            String topic,
            String eventKey,
            String payload
    );
}