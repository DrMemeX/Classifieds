package ru.drmemex.classifieds.kafka.event;

import ru.drmemex.classifieds.kafka.event.model.AdvertisementEventType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AdvertisementLifecycleEvent(

        UUID eventId,
        Long advertisementId,
        AdvertisementEventType eventType,
        OffsetDateTime occurredAt
) {
}