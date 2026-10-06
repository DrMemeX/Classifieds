package ru.drmemex.classifieds.kafka.outbox.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.kafka.exception.KafkaPublishingException;
import ru.drmemex.classifieds.kafka.outbox.entity.OutboxEvent;
import ru.drmemex.classifieds.kafka.outbox.repository.OutboxEventRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final int BATCH_SIZE = 100;

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${outbox.publisher.delay-ms:5000}")
    @Transactional
    public void publishPending() {

        List<OutboxEvent> events = outboxEventRepository.findUnpublished(BATCH_SIZE);

        for (OutboxEvent event : events) {
            publish(event);
        }
    }

    private void publish(OutboxEvent event) {

        try {
            kafkaTemplate.send(
                    event.getTopic(),
                    event.getEventKey(),
                    event.getPayload()
            ).get();

            event.setPublishedAt(OffsetDateTime.now());

            outboxEventRepository.update(event);

            log.info(
                    "Outbox event published: eventId={}, topic={}, key={}",
                    event.getId(),
                    event.getTopic(),
                    event.getEventKey()
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new KafkaPublishingException(
                    "Outbox event publishing interrupted",
                    exception
            );
        } catch (ExecutionException exception) {
            throw new KafkaPublishingException(
                    "Failed to publish outbox event",
                    exception
            );
        }
    }
}