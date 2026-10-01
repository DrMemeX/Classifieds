package ru.drmemex.classifieds.kafka.consumer.statistics.repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public interface EventStatisticsRepository {

    void increment(
            LocalDate eventDate,
            String aggregateType,
            String eventType,
            OffsetDateTime updatedAt
    );
}