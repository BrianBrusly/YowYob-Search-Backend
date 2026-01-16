package com.yowyob.common.security.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests pour EncryptionService
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("EncryptionService Tests")
class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
    }

    @Test
    @DisplayName("Devrait générer une clé AES")
    void shouldGenerateAesKey() {
        SecretKey key = encryptionService.generateKey();

        assertThat(key).isNotNull();
        assertThat(key.getAlgorithm()).isEqualTo("AES");
        assertThat(key.getEncoded()).hasSize(32); // 256 bits = 32 bytes
    }

    @Test
    @DisplayName("Devrait chiffrer et déchiffrer correctement")
    void shouldEncryptAndDecryptCorrectly() {
        String plaintext = "Données sensibles à protéger";
        SecretKey key = encryptionService.generateKey();

        String encrypted = encryptionService.encrypt(plaintext, key);
        String decrypted = encryptionService.decrypt(encrypted, key);

        assertThat(encrypted).isNotNull();
        assertThat(encrypted).isNotEqualTo(plaintext);
        assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Devrait produire des chiffrements différents pour le même texte")
    void shouldProduceDifferentEncryptionsForSameText() {
        String plaintext = "Données sensibles";
        SecretKey key = encryptionService.generateKey();

        String encrypted1 = encryptionService.encrypt(plaintext, key);
        String encrypted2 = encryptionService.encrypt(plaintext, key);

        // Les chiffrements doivent être différents (IV différent)
        assertThat(encrypted1).isNotEqualTo(encrypted2);

        // Mais le déchiffrement doit donner le même résultat
        assertThat(encryptionService.decrypt(encrypted1, key)).isEqualTo(plaintext);
        assertThat(encryptionService.decrypt(encrypted2, key)).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Devrait échouer avec une mauvaise clé")
    void shouldFailWithWrongKey() {
        String plaintext = "Données sensibles";
        SecretKey correctKey = encryptionService.generateKey();
        SecretKey wrongKey = encryptionService.generateKey();

        String encrypted = encryptionService.encrypt(plaintext, correctKey);

        assertThatThrownBy(() -> encryptionService.decrypt(encrypted, wrongKey))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Devrait convertir une clé en String et vice-versa")
    void shouldConvertKeyToStringAndBack() {
        SecretKey originalKey = encryptionService.generateKey();

        String keyString = encryptionService.keyToString(originalKey);
        SecretKey restoredKey = encryptionService.keyFromString(keyString);

        assertThat(keyString).isNotNull();
        assertThat(restoredKey).isNotNull();
        assertThat(restoredKey.getEncoded()).isEqualTo(originalKey.getEncoded());
    }

    @Test
    @DisplayName("Devrait chiffrer et déchiffrer avec clé restaurée depuis String")
    void shouldEncryptAndDecryptWithRestoredKey() {
        String plaintext = "Message secret";
        SecretKey originalKey = encryptionService.generateKey();

        // Convertir la clé en String
        String keyString = encryptionService.keyToString(originalKey);

        // Chiffrer avec la clé originale
        String encrypted = encryptionService.encrypt(plaintext, originalKey);

        // Restaurer la clé depuis le String
        SecretKey restoredKey = encryptionService.keyFromString(keyString);

        // Déchiffrer avec la clé restaurée
        String decrypted = encryptionService.decrypt(encrypted, restoredKey);

        assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Devrait gérer les caractères spéciaux et unicode")
    void shouldHandleSpecialAndUnicodeCharacters() {
        String plaintext = "Données avec émojis 🔐 et caractères spéciaux !@#$%^&*()";
        SecretKey key = encryptionService.generateKey();

        String encrypted = encryptionService.encrypt(plaintext, key);
        String decrypted = encryptionService.decrypt(encrypted, key);

        assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Devrait gérer les textes longs")
    void shouldHandleLongTexts() {
        String plaintext = "Lorem ipsum ".repeat(1000); // Texte de ~12000 caractères
        SecretKey key = encryptionService.generateKey();

        String encrypted = encryptionService.encrypt(plaintext, key);
        String decrypted = encryptionService.decrypt(encrypted, key);

        assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Devrait gérer les textes vides")
    void shouldHandleEmptyText() {
        String plaintext = "";
        SecretKey key = encryptionService.generateKey();

        String encrypted = encryptionService.encrypt(plaintext, key);
        String decrypted = encryptionService.decrypt(encrypted, key);

        assertThat(decrypted).isEqualTo(plaintext);
    }

    @Test
    @DisplayName("Devrait chiffrer des données JSON")
    void shouldEncryptJsonData() {
        String jsonData = "{\"userId\":\"123\",\"email\":\"user@example.com\",\"password\":\"secret\"}";
        SecretKey key = encryptionService.generateKey();

        String encrypted = encryptionService.encrypt(jsonData, key);
        String decrypted = encryptionService.decrypt(encrypted, key);

        assertThat(decrypted).isEqualTo(jsonData);
    }

    @Test
    @DisplayName("Devrait générer des clés différentes à chaque appel")
    void shouldGenerateDifferentKeys() {
        SecretKey key1 = encryptionService.generateKey();
        SecretKey key2 = encryptionService.generateKey();

        assertThat(key1.getEncoded()).isNotEqualTo(key2.getEncoded());
    }
}