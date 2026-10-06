package ru.drmemex.classifieds.kafka.exception;

public class KafkaPublishingException extends RuntimeException {

    public KafkaPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}
