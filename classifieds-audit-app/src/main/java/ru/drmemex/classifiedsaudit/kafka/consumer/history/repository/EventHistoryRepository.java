package ru.drmemex.classifiedsaudit.kafka.consumer.history.repository;

import ru.drmemex.classifiedsaudit.kafka.consumer.history.entity.EventHistory;

public interface EventHistoryRepository {

    EventHistory save(EventHistory eventHistory);
}
