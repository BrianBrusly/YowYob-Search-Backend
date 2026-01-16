package com.yowyob.common.util.date;

import com.yowyob.common.constant.AppConstants;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Utilitaires pour la manipulation de dates
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Toutes les méthodes sont null-safe et thread-safe
 * Utilise UTC comme timezone par défaut
 */
@Slf4j
public final class DateUtils {

    private DateUtils() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    private static final ZoneId UTC_ZONE = ZoneId.of(AppConstants.DEFAULT_TIMEZONE);

    public static Instant now() {
        return Instant.now();
    }

    public static LocalDateTime nowLocalDateTime() {
        return LocalDateTime.now(UTC_ZONE);
    }

    public static LocalDate nowLocalDate() {
        return LocalDate.now(UTC_ZONE);
    }

    public static String formatInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static String formatLocalDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static Instant parseInstant(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            return Instant.parse(dateString);
        } catch (DateTimeParseException e) {
            log.error("Impossible de parser l'instant: {}", dateString, e);
            return null;
        }
    }

    public static LocalDateTime parseLocalDateTime(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            log.error("Impossible de parser la date/heure: {}", dateString, e);
            return null;
        }
    }

    public static LocalDate parseLocalDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            log.error("Impossible de parser la date: {}", dateString, e);
            return null;
        }
    }

    public static Instant addDays(Instant instant, long days) {
        if (instant == null) {
            return null;
        }
        return instant.plus(days, ChronoUnit.DAYS);
    }

    public static Instant addHours(Instant instant, long hours) {
        if (instant == null) {
            return null;
        }
        return instant.plus(hours, ChronoUnit.HOURS);
    }

    public static Instant addMinutes(Instant instant, long minutes) {
        if (instant == null) {
            return null;
        }
        return instant.plus(minutes, ChronoUnit.MINUTES);
    }

    public static long daysBetween(Instant start, Instant end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    public static long hoursBetween(Instant start, Instant end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }

    public static long minutesBetween(Instant start, Instant end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    public static boolean isBefore(Instant instant1, Instant instant2) {
        if (instant1 == null || instant2 == null) {
            return false;
        }
        return instant1.isBefore(instant2);
    }

    public static boolean isAfter(Instant instant1, Instant instant2) {
        if (instant1 == null || instant2 == null) {
            return false;
        }
        return instant1.isAfter(instant2);
    }

    public static Instant toInstant(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant();
    }

    public static Date toDate(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Date.from(instant);
    }

    public static LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, UTC_ZONE);
    }

    public static Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(UTC_ZONE).toInstant();
    }

    public static boolean isToday(Instant instant) {
        if (instant == null) {
            return false;
        }
        LocalDate instantDate = toLocalDateTime(instant).toLocalDate();
        return instantDate.equals(nowLocalDate());
    }

    public static boolean isExpired(Instant expiryDate) {
        if (expiryDate == null) {
            return true;
        }
        return expiryDate.isBefore(now());
    }

    public static Instant startOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay(UTC_ZONE).toInstant();
    }

    public static Instant endOfDay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atTime(23, 59, 59, 999999999).atZone(UTC_ZONE).toInstant();
    }
}