package ru.drmemex.classifiedsaudit.kafka.consumer.history.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventHistory {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private Long aggregateId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(nullable = false, length = 200)
    private String topic;

    @Column(name = "event_key", nullable = false, length = 100)
    private String eventKey;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "consumed_at", nullable = false)
    private OffsetDateTime consumedAt;
}