package ru.drmemex.classifieds.feature.conversation.message.encryption.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.drmemex.classifieds.feature.conversation.message.encryption.EncryptedMessage;
import ru.drmemex.classifieds.feature.conversation.message.encryption.exception.MessageDecryptionException;
import ru.drmemex.classifieds.feature.conversation.message.encryption.exception.MessageEncryptionException;
import ru.drmemex.classifieds.feature.conversation.message.encryption.service.MessageEncryptionService;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class MessageEncryptionServiceImpl
        implements MessageEncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int KEY_LENGTH_BYTES = 32;
    private static final int IV_LENGTH_BYTES = 12;
    private static final int TAG_LENGTH_BITS = 128;

    private final SecureRandom secureRandom = new SecureRandom();
    private final SecretKey secretKey;

    public MessageEncryptionServiceImpl(
            @Value("${message-encryption.key}")
            String encodedKey
    ) {

        byte[] key = Base64.getDecoder().decode(encodedKey);

        if (key.length != KEY_LENGTH_BYTES) {
            throw new IllegalStateException(
                    "Message encryption key must contain 32 bytes"
            );
        }

        this.secretKey = new SecretKeySpec(
                key,
                ALGORITHM
        );
    }

    @Override
    public EncryptedMessage encrypt(
            String text,
            Long conversationId
    ) {

        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            cipher.init(Cipher.ENCRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(
                            TAG_LENGTH_BITS,
                            iv
                    )
            );

            cipher.updateAAD(conversationId
                            .toString()
                            .getBytes(StandardCharsets.UTF_8)
            );

            byte[] encrypted = cipher.doFinal(
                    text.getBytes(StandardCharsets.UTF_8)
            );

            return new EncryptedMessage(
                    Base64.getEncoder().encodeToString(encrypted),
                    Base64.getEncoder().encodeToString(iv)
            );
        } catch (GeneralSecurityException exception) {
            throw new MessageEncryptionException(exception);
        }
    }

    @Override
    public String decrypt(
            String encryptedText,
            String iv,
            Long conversationId
    ) {

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            byte[] decodedIv = Base64.getDecoder().decode(iv);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(
                            TAG_LENGTH_BITS,
                            decodedIv
                    )
            );

            cipher.updateAAD(conversationId
                            .toString()
                            .getBytes(StandardCharsets.UTF_8)
            );

            byte[] decrypted = cipher.doFinal(
                    Base64.getDecoder().decode(encryptedText)
            );

            return new String(
                    decrypted,
                    StandardCharsets.UTF_8
            );
        } catch (GeneralSecurityException exception) {
            throw new MessageDecryptionException(exception);
        }
    }
}