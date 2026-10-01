package ru.drmemex.classifieds.kafka.event;

import ru.drmemex.classifieds.kafka.event.model.UserEventType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserLifecycleEvent(

        UUID eventId,
        Long userId,
        UserEventType eventType,
        OffsetDateTime occurredAt
) {
}
