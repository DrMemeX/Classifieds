package ru.drmemex.classifieds.kafka.outbox.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import ru.drmemex.classifieds.kafka.exception.KafkaPublishingException;
import ru.drmemex.classifieds.kafka.outbox.entity.OutboxEvent;
import ru.drmemex.classifieds.kafka.outbox.repository.OutboxEventRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static ru.drmemex.classifieds.kafka.KafkaTopic.ADVERTISEMENT_LIFECYCLE_TOPIC;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private OutboxPublisher outboxPublisher;

    @Test
    void publishPending_ShouldPublishEventAndMarkAsPublished() {

        OutboxEvent event = createEvent();

        CompletableFuture<SendResult<String, String>> future =
                CompletableFuture.completedFuture(null);

        when(outboxEventRepository.findUnpublished(100))
                .thenReturn(List.of(event));

        when(kafkaTemplate.send(
                event.getTopic(),
                event.getEventKey(),
                event.getPayload()
        )).thenReturn(future);

        outboxPublisher.publishPending();

        verify(kafkaTemplate)
                .send(
                        event.getTopic(),
                        event.getEventKey(),
                        event.getPayload()
                );

        assertNotNull(
                event.getPublishedAt()
        );

        verify(outboxEventRepository)
                .update(event);
    }

    @Test
    void publishPending_ShouldDoNothing_WhenNoUnpublishedEvents() {

        when(outboxEventRepository.findUnpublished(100))
                .thenReturn(List.of());

        outboxPublisher.publishPending();

        verify(outboxEventRepository)
                .findUnpublished(100);

        verifyNoInteractions(kafkaTemplate);

        verify(outboxEventRepository, never())
                .update(any(OutboxEvent.class));
    }

    @Test
    void publishPending_ShouldThrowException_WhenKafkaPublishingFails() {

        OutboxEvent event = createEvent();

        CompletableFuture<SendResult<String, String>> future =
                new CompletableFuture<>();

        future.completeExceptionally(
                new RuntimeException("Kafka unavailable")
        );

        when(outboxEventRepository.findUnpublished(100))
                .thenReturn(List.of(event));

        when(kafkaTemplate.send(
                event.getTopic(),
                event.getEventKey(),
                event.getPayload()
        )).thenReturn(future);

        KafkaPublishingException result = assertThrows(
                KafkaPublishingException.class,
                () -> outboxPublisher.publishPending()
        );

        assertEquals(
                "Failed to publish outbox event",
                result.getMessage()
        );

        assertNull(
                event.getPublishedAt()
        );

        verify(outboxEventRepository, never())
                .update(event);
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishPending_ShouldRestoreInterruptFlag_WhenPublishingIsInterrupted()
            throws Exception {

        OutboxEvent event = createEvent();

        CompletableFuture<SendResult<String, String>> future =
                mock(CompletableFuture.class);

        when(outboxEventRepository.findUnpublished(100))
                .thenReturn(List.of(event));

        when(kafkaTemplate.send(
                event.getTopic(),
                event.getEventKey(),
                event.getPayload()
        )).thenReturn(future);

        when(future.get())
                .thenThrow(new InterruptedException("Interrupted"));

        try {
            KafkaPublishingException result = assertThrows(
                    KafkaPublishingException.class,
                    () -> outboxPublisher.publishPending()
            );

            assertEquals(
                    "Outbox event publishing interrupted",
                    result.getMessage()
            );

            assertTrue(
                    Thread.currentThread().isInterrupted()
            );

            assertNull(
                    event.getPublishedAt()
            );

            verify(outboxEventRepository, never())
                    .update(event);
        } finally {
            Thread.interrupted();
        }
    }

    private OutboxEvent createEvent() {
        return OutboxEvent.builder()
                .id(UUID.randomUUID())
                .topic(ADVERTISEMENT_LIFECYCLE_TOPIC)
                .eventKey("10")
                .payload("{\"event\":\"advertisement\"}")
                .build();
    }
}