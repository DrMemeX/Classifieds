package ru.drmemex.classifieds.kafka.outbox.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.drmemex.classifieds.kafka.event.AdvertisementLifecycleEvent;
import ru.drmemex.classifieds.kafka.event.UserLifecycleEvent;
import ru.drmemex.classifieds.kafka.event.model.AdvertisementEventType;
import ru.drmemex.classifieds.kafka.event.model.UserEventType;
import ru.drmemex.classifieds.kafka.exception.KafkaEventSerializationException;
import ru.drmemex.classifieds.kafka.outbox.entity.OutboxEvent;
import ru.drmemex.classifieds.kafka.outbox.repository.OutboxEventRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.drmemex.classifieds.kafka.KafkaTopic.ADVERTISEMENT_LIFECYCLE_TOPIC;
import static ru.drmemex.classifieds.kafka.KafkaTopic.USER_LIFECYCLE_TOPIC;

@ExtendWith(MockitoExtension.class)
class OutboxServiceImplTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxServiceImpl outboxService;

    @Test
    void saveAdvertisementEvent_ShouldSaveOutboxEvent()
            throws JsonProcessingException {

        UUID eventId = UUID.randomUUID();
        OffsetDateTime occurredAt = OffsetDateTime.now();

        AdvertisementLifecycleEvent event =
                new AdvertisementLifecycleEvent(
                        eventId,
                        10L,
                        AdvertisementEventType.CREATED,
                        occurredAt
                );

        String payload = "{\"event\":\"advertisement\"}";

        when(objectMapper.writeValueAsString(event))
                .thenReturn(payload);

        outboxService.saveAdvertisementEvent(event);

        ArgumentCaptor<OutboxEvent> eventCaptor =
                ArgumentCaptor.forClass(OutboxEvent.class);

        verify(outboxEventRepository).save(eventCaptor.capture());

        OutboxEvent outboxEvent = eventCaptor.getValue();

        assertEquals(
                eventId,
                outboxEvent.getId()
        );

        assertEquals(
                "ADVERTISEMENT",
                outboxEvent.getAggregateType()
        );

        assertEquals(
                10L,
                outboxEvent.getAggregateId()
        );

        assertEquals(
                AdvertisementEventType.CREATED.name(),
                outboxEvent.getEventType()
        );

        assertEquals(
                ADVERTISEMENT_LIFECYCLE_TOPIC,
                outboxEvent.getTopic()
        );

        assertEquals(
                "10",
                outboxEvent.getEventKey()
        );

        assertEquals(
                payload,
                outboxEvent.getPayload()
        );

        assertNotNull(
                outboxEvent.getCreatedAt()
        );

        assertNull(
                outboxEvent.getPublishedAt()
        );
    }

    @Test
    void saveUserEvent_ShouldSaveOutboxEvent()
            throws JsonProcessingException {

        UUID eventId = UUID.randomUUID();
        OffsetDateTime occurredAt = OffsetDateTime.now();

        UserLifecycleEvent event =
                new UserLifecycleEvent(
                        eventId,
                        20L,
                        UserEventType.CREATED,
                        occurredAt
                );

        String payload = "{\"event\":\"user\"}";

        when(objectMapper.writeValueAsString(event))
                .thenReturn(payload);

        outboxService.saveUserEvent(event);

        ArgumentCaptor<OutboxEvent> eventCaptor =
                ArgumentCaptor.forClass(OutboxEvent.class);

        verify(outboxEventRepository)
                .save(eventCaptor.capture());

        OutboxEvent outboxEvent = eventCaptor.getValue();

        assertEquals(
                eventId,
                outboxEvent.getId()
        );

        assertEquals(
                "USER",
                outboxEvent.getAggregateType()
        );

        assertEquals(
                20L,
                outboxEvent.getAggregateId()
        );

        assertEquals(
                UserEventType.CREATED.name(),
                outboxEvent.getEventType()
        );

        assertEquals(
                USER_LIFECYCLE_TOPIC,
                outboxEvent.getTopic()
        );

        assertEquals(
                "20",
                outboxEvent.getEventKey()
        );

        assertEquals(
                payload,
                outboxEvent.getPayload()
        );

        assertNotNull(
                outboxEvent.getCreatedAt()
        );

        assertNull(
                outboxEvent.getPublishedAt()
        );
    }

    @Test
    void saveAdvertisementEvent_ShouldThrowException_WhenSerializationFails()
            throws JsonProcessingException {

        AdvertisementLifecycleEvent event =
                new AdvertisementLifecycleEvent(
                        UUID.randomUUID(),
                        10L,
                        AdvertisementEventType.CREATED,
                        OffsetDateTime.now()
                );

        JsonProcessingException exception =
                new JsonProcessingException("serialization failed") {
                };

        when(objectMapper.writeValueAsString(event))
                .thenThrow(exception);

        KafkaEventSerializationException result = assertThrows(
                KafkaEventSerializationException.class,
                () -> outboxService.saveAdvertisementEvent(event)
        );

        assertEquals(
                "Failed to serialize outbox event",
                result.getMessage()
        );

        verify(outboxEventRepository, never())
                .save(any(OutboxEvent.class));
    }
}