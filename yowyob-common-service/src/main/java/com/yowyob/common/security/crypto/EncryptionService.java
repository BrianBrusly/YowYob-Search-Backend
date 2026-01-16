package com.yowyob.common.security.crypto;

import com.yowyob.common.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service de chiffrement/déchiffrement
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Utilise AES-256-GCM pour chiffrer les données sensibles
 * Thread-safe et sécurisé pour production
 */
@Slf4j
@Service
public class EncryptionService {

    private final SecureRandom secureRandom;

    public EncryptionService() {
        this.secureRandom = new SecureRandom();
    }

    public String encrypt(String plaintext, SecretKey key) {
        try {
            byte[] iv = new byte[SecurityConstants.GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(SecurityConstants.AES_TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(
                    SecurityConstants.GCM_TAG_LENGTH,
                    iv
            );
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            log.error("Erreur lors du chiffrement", e);
            throw new RuntimeException("Échec du chiffrement", e);
        }
    }

    public String decrypt(String ciphertext, SecretKey key) {
        try {
            byte[] decoded = Base64.getDecoder().decode(ciphertext);

            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[SecurityConstants.GCM_IV_LENGTH];
            byteBuffer.get(iv);

            byte[] encrypted = new byte[byteBuffer.remaining()];
            byteBuffer.get(encrypted);

            Cipher cipher = Cipher.getInstance(SecurityConstants.AES_TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(
                    SecurityConstants.GCM_TAG_LENGTH,
                    iv
            );
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

            byte[] plaintext = cipher.doFinal(encrypted);
            return new String(plaintext);
        } catch (Exception e) {
            log.error("Erreur lors du déchiffrement", e);
            throw new RuntimeException("Échec du déchiffrement", e);
        }
    }

    public SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(SecurityConstants.AES_ALGORITHM);
            keyGenerator.init(SecurityConstants.AES_KEY_SIZE, secureRandom);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            log.error("Erreur lors de la génération de clé", e);
            throw new RuntimeException("Échec de la génération de clé", e);
        }
    }

    public SecretKey keyFromString(String keyString) {
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, SecurityConstants.AES_ALGORITHM);
    }

    public String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}