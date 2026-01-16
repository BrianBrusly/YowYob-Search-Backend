package com.yowyob.common.util.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour Validators
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("Validators Tests")
class ValidatorsTest {

    @Test
    @DisplayName("Devrait valider un email correct")
    void shouldValidateCorrectEmail() {
        assertThat(Validators.isValidEmail("user@example.com")).isTrue();
        assertThat(Validators.isValidEmail("user.name@example.co.uk")).isTrue();
        assertThat(Validators.isValidEmail("user+tag@example.com")).isTrue();
    }

    @Test
    @DisplayName("Devrait rejeter un email invalide")
    void shouldRejectInvalidEmail() {
        assertThat(Validators.isValidEmail("invalid.email")).isFalse();
        assertThat(Validators.isValidEmail("@example.com")).isFalse();
        assertThat(Validators.isValidEmail("user@")).isFalse();
        assertThat(Validators.isValidEmail("")).isFalse();
        assertThat(Validators.isValidEmail(null)).isFalse();
    }

    @Test
    @DisplayName("Devrait valider un numéro de téléphone international")
    void shouldValidateInternationalPhone() {
        assertThat(Validators.isValidPhone("+237612345678")).isTrue();
        assertThat(Validators.isValidPhone("+33612345678")).isTrue();
        assertThat(Validators.isValidPhone("+12025551234")).isTrue();
    }

    @Test
    @DisplayName("Devrait valider un numéro de téléphone camerounais")
    void shouldValidateCameroonPhone() {
        assertThat(Validators.isValidCameroonPhone("+237612345678")).isTrue();
        assertThat(Validators.isValidCameroonPhone("237612345678")).isTrue();
        assertThat(Validators.isValidCameroonPhone("612345678")).isTrue();
    }

    @Test
    @DisplayName("Devrait valider une URL")
    void shouldValidateUrl() {
        assertThat(Validators.isValidUrl("https://www.example.com")).isTrue();
        assertThat(Validators.isValidUrl("http://example.com/path")).isTrue();
        assertThat(Validators.isValidUrl("https://example.com:8080/path?query=1")).isTrue();
    }

    @Test
    @DisplayName("Devrait valider une adresse IPv4")
    void shouldValidateIPv4() {
        assertThat(Validators.isValidIPv4("192.168.1.1")).isTrue();
        assertThat(Validators.isValidIPv4("10.0.0.1")).isTrue();
        assertThat(Validators.isValidIPv4("255.255.255.255")).isTrue();
        assertThat(Validators.isValidIPv4("256.1.1.1")).isFalse();
    }

    @Test
    @DisplayName("Devrait valider un UUID")
    void shouldValidateUUID() {
        assertThat(Validators.isValidUUID("550e8400-e29b-41d4-a716-446655440000")).isTrue();
        assertThat(Validators.isValidUUID("invalid-uuid")).isFalse();
    }

    @Test
    @DisplayName("Devrait valider la latitude")
    void shouldValidateLatitude() {
        assertThat(Validators.isValidLatitude(0.0)).isTrue();
        assertThat(Validators.isValidLatitude(45.5)).isTrue();
        assertThat(Validators.isValidLatitude(-45.5)).isTrue();
        assertThat(Validators.isValidLatitude(90.0)).isTrue();
        assertThat(Validators.isValidLatitude(-90.0)).isTrue();
        assertThat(Validators.isValidLatitude(90.1)).isFalse();
        assertThat(Validators.isValidLatitude(-90.1)).isFalse();
    }

    @Test
    @DisplayName("Devrait valider la longitude")
    void shouldValidateLongitude() {
        assertThat(Validators.isValidLongitude(0.0)).isTrue();
        assertThat(Validators.isValidLongitude(90.5)).isTrue();
        assertThat(Validators.isValidLongitude(-90.5)).isTrue();
        assertThat(Validators.isValidLongitude(180.0)).isTrue();
        assertThat(Validators.isValidLongitude(-180.0)).isTrue();
        assertThat(Validators.isValidLongitude(180.1)).isFalse();
        assertThat(Validators.isValidLongitude(-180.1)).isFalse();
    }

    @Test
    @DisplayName("Devrait vérifier si une valeur est dans une plage")
    void shouldCheckIfInRange() {
        assertThat(Validators.isInRange(5, 1, 10)).isTrue();
        assertThat(Validators.isInRange(1, 1, 10)).isTrue();
        assertThat(Validators.isInRange(10, 1, 10)).isTrue();
        assertThat(Validators.isInRange(0, 1, 10)).isFalse();
        assertThat(Validators.isInRange(11, 1, 10)).isFalse();
    }

    @Test
    @DisplayName("Devrait vérifier si une valeur est positive")
    void shouldCheckIfPositive() {
        assertThat(Validators.isPositive(1)).isTrue();
        assertThat(Validators.isPositive(100)).isTrue();
        assertThat(Validators.isPositive(0)).isFalse();
        assertThat(Validators.isPositive(-1)).isFalse();
    }

    @Test
    @DisplayName("Devrait vérifier la longueur minimale")
    void shouldCheckMinLength() {
        assertThat(Validators.hasMinLength("hello", 3)).isTrue();
        assertThat(Validators.hasMinLength("hello", 5)).isTrue();
        assertThat(Validators.hasMinLength("hello", 6)).isFalse();
    }

    @Test
    @DisplayName("Devrait vérifier la longueur maximale")
    void shouldCheckMaxLength() {
        assertThat(Validators.hasMaxLength("hello", 10)).isTrue();
        assertThat(Validators.hasMaxLength("hello", 5)).isTrue();
        assertThat(Validators.hasMaxLength("hello", 4)).isFalse();
    }
}