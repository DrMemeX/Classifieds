package ru.drmemex.classifieds.kafka.consumer.statistics.service;

import ru.drmemex.classifieds.kafka.event.AdvertisementLifecycleEvent;
import ru.drmemex.classifieds.kafka.event.UserLifecycleEvent;

public interface EventStatisticsService {

    void processAdvertisementEvent(AdvertisementLifecycleEvent event);

    void processUserEvent(UserLifecycleEvent event);
}