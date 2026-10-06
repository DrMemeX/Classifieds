package ru.drmemex.classifieds.feature.conversation.message.encryption.service;

import ru.drmemex.classifieds.feature.conversation.message.encryption.EncryptedMessage;

public interface MessageEncryptionService {

    EncryptedMessage encrypt(
            String text,
            Long conversationId
    );

    String decrypt(
            String encryptedText,
            String iv,
            Long conversationId
    );
}