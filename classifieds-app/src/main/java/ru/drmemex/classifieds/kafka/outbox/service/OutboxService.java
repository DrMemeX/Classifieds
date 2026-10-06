package ru.drmemex.classifieds.kafka.outbox.service;

import ru.drmemex.classifieds.kafka.event.AdvertisementLifecycleEvent;
import ru.drmemex.classifieds.kafka.event.UserLifecycleEvent;

public interface OutboxService {

    void saveAdvertisementEvent(AdvertisementLifecycleEvent event);

    void saveUserEvent(UserLifecycleEvent event);
}
