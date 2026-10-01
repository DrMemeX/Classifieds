CREATE INDEX idx_outbox_events_pending_created_at
    ON outbox_events (created_at)
    WHERE published_at IS NULL;