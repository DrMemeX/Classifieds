package ru.drmemex.classifieds.feature.conversation.message.encryption;

public record EncryptedMessage(
        String encryptedText,
        String iv
) {
}