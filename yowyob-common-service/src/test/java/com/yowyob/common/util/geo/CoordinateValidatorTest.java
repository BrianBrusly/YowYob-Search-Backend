package com.yowyob.common.util.geo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

/**
 * Tests pour CoordinateValidator
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("CoordinateValidator Tests")
class CoordinateValidatorTest {

    private CoordinateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CoordinateValidator();
    }

    @Test
    @DisplayName("Devrait valider une latitude correcte")
    void shouldValidateCorrectLatitude() {
        assertThat(validator.isValidLatitude(0.0)).isTrue();
        assertThat(validator.isValidLatitude(45.5)).isTrue();
        assertThat(validator.isValidLatitude(-45.5)).isTrue();
        assertThat(validator.isValidLatitude(90.0)).isTrue();
        assertThat(validator.isValidLatitude(-90.0)).isTrue();
    }

    @Test
    @DisplayName("Devrait rejeter une latitude invalide")
    void shouldRejectInvalidLatitude() {
        assertThat(validator.isValidLatitude(90.1)).isFalse();
        assertThat(validator.isValidLatitude(-90.1)).isFalse();
        assertThat(validator.isValidLatitude(100.0)).isFalse();
    }

    @Test
    @DisplayName("Devrait valider une longitude correcte")
    void shouldValidateCorrectLongitude() {
        assertThat(validator.isValidLongitude(0.0)).isTrue();
        assertThat(validator.isValidLongitude(90.5)).isTrue();
        assertThat(validator.isValidLongitude(-90.5)).isTrue();
        assertThat(validator.isValidLongitude(180.0)).isTrue();
        assertThat(validator.isValidLongitude(-180.0)).isTrue();
    }

    @Test
    @DisplayName("Devrait rejeter une longitude invalide")
    void shouldRejectInvalidLongitude() {
        assertThat(validator.isValidLongitude(180.1)).isFalse();
        assertThat(validator.isValidLongitude(-180.1)).isFalse();
        assertThat(validator.isValidLongitude(200.0)).isFalse();
    }

    @Test
    @DisplayName("Devrait valider des coordonnées correctes")
    void shouldValidateCorrectCoordinates() {
        assertThat(validator.isValidCoordinate(3.8480, 11.5021)).isTrue();
        assertThat(validator.isValidCoordinate(0.0, 0.0)).isTrue();
    }

    @Test
    @DisplayName("Devrait rejeter des coordonnées invalides")
    void shouldRejectInvalidCoordinates() {
        assertThat(validator.isValidCoordinate(91.0, 11.5021)).isFalse();
        assertThat(validator.isValidCoordinate(3.8480, 181.0)).isFalse();
    }

    @Test
    @DisplayName("Devrait valider une chaîne de coordonnées")
    void shouldValidateCoordinateString() {
        assertThat(validator.isValidCoordinateString("3.8480,11.5021")).isTrue();
        assertThat(validator.isValidCoordinateString("0.0, 0.0")).isTrue();
        assertThat(validator.isValidCoordinateString("-45.5, 90.0")).isTrue();
    }

    @Test
    @DisplayName("Devrait rejeter une chaîne de coordonnées invalide")
    void shouldRejectInvalidCoordinateString() {
        assertThat(validator.isValidCoordinateString("invalid")).isFalse();
        assertThat(validator.isValidCoordinateString("3.8480")).isFalse();
        assertThat(validator.isValidCoordinateString("91.0,11.5021")).isFalse();
        assertThat(validator.isValidCoordinateString(null)).isFalse();
    }

    @Test
    @DisplayName("Devrait parser des coordonnées depuis une chaîne")
    void shouldParseCoordinatesFromString() {
        double[] coords = validator.parseCoordinates("3.8480,11.5021");

        assertThat(coords).hasSize(2);
        assertThat(coords[0]).isEqualTo(3.8480);
        assertThat(coords[1]).isEqualTo(11.5021);
    }

    @Test
    @DisplayName("Devrait retourner null pour une chaîne invalide")
    void shouldReturnNullForInvalidString() {
        assertThat(validator.parseCoordinates("invalid")).isNull();
    }

    @Test
    @DisplayName("Devrait formater des coordonnées")
    void shouldFormatCoordinates() {
        String formatted = validator.formatCoordinates(3.8480, 11.5021);

        assertThat(formatted).isEqualTo("3.848000,11.502100");
    }

    @Test
    @DisplayName("Devrait lancer une exception pour coordonnées invalides lors du formatage")
    void shouldThrowExceptionForInvalidCoordinatesOnFormat() {
        assertThatThrownBy(() -> validator.formatCoordinates(91.0, 11.5021))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalides");
    }

    @Test
    @DisplayName("Devrait normaliser la latitude")
    void shouldNormalizeLatitude() {
        assertThat(validator.normalizeLatitude(95.0)).isCloseTo(-85.0, within(0.1));
        assertThat(validator.normalizeLatitude(-95.0)).isCloseTo(85.0, within(0.1));
    }

    @Test
    @DisplayName("Devrait normaliser la longitude")
    void shouldNormalizeLongitude() {
        assertThat(validator.normalizeLongitude(190.0)).isCloseTo(-170.0, within(0.1));
        assertThat(validator.normalizeLongitude(-190.0)).isCloseTo(170.0, within(0.1));
    }
}