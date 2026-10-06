package ru.drmemex.classifiedsaudit.kafka.exception;

public class KafkaEventDeserializationException extends RuntimeException {

    public KafkaEventDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
