package ru.drmemex.classifieds.kafka.exception;

public class KafkaEventSerializationException extends RuntimeException {

    public KafkaEventSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
