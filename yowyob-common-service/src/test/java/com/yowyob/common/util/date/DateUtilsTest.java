package com.yowyob.common.util.date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour DateUtils
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("DateUtils Tests")
class DateUtilsTest {

    @Test
    @DisplayName("Devrait retourner l'instant actuel")
    void shouldReturnCurrentInstant() {
        Instant now = DateUtils.now();

        assertThat(now).isNotNull();
        assertThat(now).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    @DisplayName("Devrait formater un Instant")
    void shouldFormatInstant() {
        Instant instant = Instant.parse("2025-01-15T10:30:00Z");

        String formatted = DateUtils.formatInstant(instant);

        assertThat(formatted).isEqualTo("2025-01-15T10:30:00Z");
    }

    @Test
    @DisplayName("Devrait parser un Instant")
    void shouldParseInstant() {
        String dateString = "2025-01-15T10:30:00Z";

        Instant instant = DateUtils.parseInstant(dateString);

        assertThat(instant).isNotNull();
        assertThat(instant.toString()).isEqualTo(dateString);
    }

    @Test
    @DisplayName("Devrait ajouter des jours à un Instant")
    void shouldAddDaysToInstant() {
        Instant instant = Instant.parse("2025-01-15T10:30:00Z");

        Instant result = DateUtils.addDays(instant, 5);

        assertThat(result).isAfter(instant);
        assertThat(DateUtils.daysBetween(instant, result)).isEqualTo(5);
    }

    @Test
    @DisplayName("Devrait ajouter des heures à un Instant")
    void shouldAddHoursToInstant() {
        Instant instant = Instant.parse("2025-01-15T10:30:00Z");

        Instant result = DateUtils.addHours(instant, 3);

        assertThat(DateUtils.hoursBetween(instant, result)).isEqualTo(3);
    }

    @Test
    @DisplayName("Devrait calculer les jours entre deux instants")
    void shouldCalculateDaysBetween() {
        Instant start = Instant.parse("2025-01-15T10:30:00Z");
        Instant end = Instant.parse("2025-01-20T10:30:00Z");

        long days = DateUtils.daysBetween(start, end);

        assertThat(days).isEqualTo(5);
    }

    @Test
    @DisplayName("Devrait vérifier si un instant est avant un autre")
    void shouldCheckIfBefore() {
        Instant instant1 = Instant.parse("2025-01-15T10:30:00Z");
        Instant instant2 = Instant.parse("2025-01-20T10:30:00Z");

        assertThat(DateUtils.isBefore(instant1, instant2)).isTrue();
        assertThat(DateUtils.isBefore(instant2, instant1)).isFalse();
    }

    @Test
    @DisplayName("Devrait vérifier si un instant est après un autre")
    void shouldCheckIfAfter() {
        Instant instant1 = Instant.parse("2025-01-15T10:30:00Z");
        Instant instant2 = Instant.parse("2025-01-20T10:30:00Z");

        assertThat(DateUtils.isAfter(instant2, instant1)).isTrue();
        assertThat(DateUtils.isAfter(instant1, instant2)).isFalse();
    }

    @Test
    @DisplayName("Devrait convertir Date en Instant")
    void shouldConvertDateToInstant() {
        Date date = new Date();

        Instant instant = DateUtils.toInstant(date);

        assertThat(instant).isNotNull();
    }

    @Test
    @DisplayName("Devrait convertir Instant en Date")
    void shouldConvertInstantToDate() {
        Instant instant = Instant.now();

        Date date = DateUtils.toDate(instant);

        assertThat(date).isNotNull();
    }

    @Test
    @DisplayName("Devrait vérifier si une date est expirée")
    void shouldCheckIfExpired() {
        Instant past = Instant.now().minusSeconds(3600);
        Instant future = Instant.now().plusSeconds(3600);

        assertThat(DateUtils.isExpired(past)).isTrue();
        assertThat(DateUtils.isExpired(future)).isFalse();
    }

    @Test
    @DisplayName("Devrait obtenir le début de la journée")
    void shouldGetStartOfDay() {
        LocalDate date = LocalDate.of(2025, 1, 15);

        Instant startOfDay = DateUtils.startOfDay(date);
        LocalDateTime localDateTime = DateUtils.toLocalDateTime(startOfDay);

        assertThat(localDateTime.getHour()).isEqualTo(0);
        assertThat(localDateTime.getMinute()).isEqualTo(0);
        assertThat(localDateTime.getSecond()).isEqualTo(0);
    }

    @Test
    @DisplayName("Devrait obtenir la fin de la journée")
    void shouldGetEndOfDay() {
        LocalDate date = LocalDate.of(2025, 1, 15);

        Instant endOfDay = DateUtils.endOfDay(date);
        LocalDateTime localDateTime = DateUtils.toLocalDateTime(endOfDay);

        assertThat(localDateTime.getHour()).isEqualTo(23);
        assertThat(localDateTime.getMinute()).isEqualTo(59);
        assertThat(localDateTime.getSecond()).isEqualTo(59);
    }

    @Test
    @DisplayName("Devrait gérer les valeurs null de manière sûre")
    void shouldHandleNullValuesSafely() {
        assertThat(DateUtils.formatInstant(null)).isNull();
        assertThat(DateUtils.parseInstant(null)).isNull();
        assertThat(DateUtils.addDays(null, 5)).isNull();
        assertThat(DateUtils.daysBetween(null, Instant.now())).isEqualTo(0);
    }
}