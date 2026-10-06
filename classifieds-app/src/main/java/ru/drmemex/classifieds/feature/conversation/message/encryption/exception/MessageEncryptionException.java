package ru.drmemex.classifieds.feature.conversation.message.encryption.exception;

public class MessageEncryptionException extends RuntimeException {

    public MessageEncryptionException(Throwable cause) {
        super("Failed to encrypt message", cause);
    }
}