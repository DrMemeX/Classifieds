package ru.drmemex.classifieds.kafka.outbox.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.drmemex.classifieds.integration.AbstractIntegrationTest;
import ru.drmemex.classifieds.kafka.outbox.entity.OutboxEvent;
import ru.drmemex.classifieds.kafka.outbox.repository.OutboxEventRepository;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.drmemex.classifieds.kafka.KafkaTopic.ADVERTISEMENT_LIFECYCLE_TOPIC;

class OutboxEventRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void save_ShouldPersistOutboxEvent() {

        UUID eventId = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();

        OutboxEvent event = createEvent(
                eventId,
                createdAt,
                null
        );

        outboxEventRepository.save(event);

        entityManager.flush();
        entityManager.clear();

        OutboxEvent saved =
                entityManager.find(
                        OutboxEvent.class,
                        eventId
                );

        assertNotNull(saved);

        assertEquals(
                eventId,
                saved.getId()
        );

        assertEquals(
                "ADVERTISEMENT",
                saved.getAggregateType()
        );

        assertEquals(
                10L,
                saved.getAggregateId()
        );

        assertEquals(
                "CREATED",
                saved.getEventType()
        );

        assertEquals(
                ADVERTISEMENT_LIFECYCLE_TOPIC,
                saved.getTopic()
        );

        assertEquals(
                "10",
                saved.getEventKey()
        );

        assertEquals(
                "{\"event\":\"advertisement\"}",
                saved.getPayload()
        );

        assertNull(
                saved.getPublishedAt()
        );
    }

    @Test
    void update_ShouldUpdatePublishedAt() {

        UUID eventId = UUID.randomUUID();

        OutboxEvent event = createEvent(
                eventId,
                OffsetDateTime.now(),
                null
        );

        outboxEventRepository.save(event);
        entityManager.flush();

        OffsetDateTime publishedAt = OffsetDateTime.now()
                .truncatedTo(ChronoUnit.MICROS);

        event.setPublishedAt(publishedAt);

        outboxEventRepository.update(event);

        entityManager.flush();
        entityManager.clear();

        OutboxEvent updated = entityManager.find(
                        OutboxEvent.class,
                        eventId
                );

        assertNotNull(
                updated.getPublishedAt()
        );

        assertEquals(
                publishedAt.toInstant().truncatedTo(ChronoUnit.MICROS),
                updated.getPublishedAt().toInstant().truncatedTo(ChronoUnit.MICROS)
        );
    }

    @Test
    void findUnpublished_ShouldReturnPendingEventsOrderedAndLimited() {

        OffsetDateTime now = OffsetDateTime.now();

        OutboxEvent first = createEvent(
                UUID.randomUUID(),
                now.minusSeconds(3),
                null
        );

        OutboxEvent second = createEvent(
                UUID.randomUUID(),
                now.minusSeconds(2),
                null
        );

        OutboxEvent third = createEvent(
                UUID.randomUUID(),
                now.minusSeconds(1),
                null
        );

        OutboxEvent published = createEvent(
                UUID.randomUUID(),
                now.minusSeconds(4),
                now
        );

        outboxEventRepository.save(first);
        outboxEventRepository.save(second);
        outboxEventRepository.save(third);
        outboxEventRepository.save(published);

        entityManager.flush();

        List<OutboxEvent> result =
                outboxEventRepository.findUnpublished(2);

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                first.getId(),
                result.get(0).getId()
        );

        assertEquals(
                second.getId(),
                result.get(1).getId()
        );
    }

    private OutboxEvent createEvent(
            UUID eventId,
            OffsetDateTime createdAt,
            OffsetDateTime publishedAt
    ) {
        return OutboxEvent.builder()
                .id(eventId)
                .aggregateType("ADVERTISEMENT")
                .aggregateId(10L)
                .eventType("CREATED")
                .topic(ADVERTISEMENT_LIFECYCLE_TOPIC)
                .eventKey("10")
                .payload("{\"event\":\"advertisement\"}")
                .createdAt(createdAt)
                .publishedAt(publishedAt)
                .build();
    }
}