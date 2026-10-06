package ru.drmemex.classifieds.feature.conversation.message.encryption.exception;

public class MessageDecryptionException extends RuntimeException {

    public MessageDecryptionException(Throwable cause) {
        super("Failed to decrypt message", cause);
    }
}