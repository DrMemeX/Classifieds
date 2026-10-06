package ru.drmemex.classifieds.kafka.outbox.repository;

import ru.drmemex.classifieds.kafka.outbox.entity.OutboxEvent;

import java.util.List;

public interface OutboxEventRepository {

    OutboxEvent save(OutboxEvent event);

    OutboxEvent update(OutboxEvent event);

    List<OutboxEvent> findUnpublished(int limit);
}
