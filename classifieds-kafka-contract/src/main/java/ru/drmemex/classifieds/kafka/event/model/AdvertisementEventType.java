package ru.drmemex.classifieds.kafka.event.model;

public enum AdvertisementEventType {

    CREATED,
    UPDATED,
    ACTIVATED,
    DEACTIVATED,
    BLOCKED,
    UNBLOCKED,
    DELETED
}
