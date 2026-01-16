package com.yowyob.common.security.password;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests pour PasswordService
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("PasswordService Tests")
class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService();
    }

    @Test
    @DisplayName("Devrait hacher un mot de passe")
    void shouldHashPassword() {
        String rawPassword = "MySecureP@ssw0rd";

        String hashedPassword = passwordService.hashPassword(rawPassword);

        assertThat(hashedPassword).isNotNull();
        assertThat(hashedPassword).isNotEqualTo(rawPassword);
        assertThat(hashedPassword).startsWith("$2a$");
    }

    @Test
    @DisplayName("Devrait générer des hashes différents pour le même mot de passe")
    void shouldGenerateDifferentHashesForSamePassword() {
        String rawPassword = "MySecureP@ssw0rd";

        String hash1 = passwordService.hashPassword(rawPassword);
        String hash2 = passwordService.hashPassword(rawPassword);

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("Devrait vérifier un mot de passe correct")
    void shouldVerifyCorrectPassword() {
        String rawPassword = "MySecureP@ssw0rd";
        String hashedPassword = passwordService.hashPassword(rawPassword);

        boolean isValid = passwordService.verifyPassword(rawPassword, hashedPassword);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Devrait rejeter un mot de passe incorrect")
    void shouldRejectIncorrectPassword() {
        String rawPassword = "MySecureP@ssw0rd";
        String wrongPassword = "WrongPassword123";
        String hashedPassword = passwordService.hashPassword(rawPassword);

        boolean isValid = passwordService.verifyPassword(wrongPassword, hashedPassword);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Devrait lancer une exception pour mot de passe vide")
    void shouldThrowExceptionForEmptyPassword() {
        assertThatThrownBy(() -> passwordService.hashPassword(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mot de passe ne peut pas être vide");
    }

    @Test
    @DisplayName("Devrait lancer une exception pour mot de passe null")
    void shouldThrowExceptionForNullPassword() {
        assertThatThrownBy(() -> passwordService.hashPassword(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Devrait retourner false pour vérification avec password null")
    void shouldReturnFalseForNullPasswordVerification() {
        String hashedPassword = passwordService.hashPassword("password");

        boolean isValid = passwordService.verifyPassword(null, hashedPassword);

        assertThat(isValid).isFalse();
    }
}