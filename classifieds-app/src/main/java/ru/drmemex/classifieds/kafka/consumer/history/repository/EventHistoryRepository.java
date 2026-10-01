package ru.drmemex.classifieds.kafka.consumer.history.repository;

import ru.drmemex.classifieds.kafka.consumer.history.entity.EventHistory;

public interface EventHistoryRepository {

    EventHistory save(EventHistory eventHistory);
}
