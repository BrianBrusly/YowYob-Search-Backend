package com.yowyob.common.util.validation;

import com.yowyob.common.constant.RegexPatterns;
import com.yowyob.common.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Collection de validateurs pour différents types de données
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Fournit des méthodes de validation rapides et thread-safe
 * pour les types de données courants
 */
@Slf4j
public final class Validators {

    private Validators() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    public static boolean isValidEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        return RegexPatterns.EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        return RegexPatterns.PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidCameroonPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        return RegexPatterns.CAMEROON_PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        return RegexPatterns.URL_PATTERN.matcher(url).matches();
    }

    public static boolean isValidIPv4(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }
        return RegexPatterns.IPV4_PATTERN.matcher(ip).matches();
    }

    public static boolean isValidIPv6(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }
        return RegexPatterns.IPV6_PATTERN.matcher(ip).matches();
    }

    public static boolean isValidUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return false;
        }
        return RegexPatterns.USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidUUID(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            return false;
        }
        return RegexPatterns.UUID_PATTERN.matcher(uuid).matches();
    }

    public static boolean isValidSlug(String slug) {
        if (StringUtils.isEmpty(slug)) {
            return false;
        }
        return RegexPatterns.SLUG_PATTERN.matcher(slug).matches();
    }

    public static boolean isValidCoordinates(String coordinates) {
        if (StringUtils.isEmpty(coordinates)) {
            return false;
        }
        return RegexPatterns.COORDINATES_PATTERN.matcher(coordinates).matches();
    }

    public static boolean isValidLatitude(double latitude) {
        return latitude >= -90.0 && latitude <= 90.0;
    }

    public static boolean isValidLongitude(double longitude) {
        return longitude >= -180.0 && longitude <= 180.0;
    }

    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean isInRange(long value, long min, long max) {
        return value >= min && value <= max;
    }

    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }

    public static boolean isPositive(int value) {
        return value > 0;
    }

    public static boolean isPositive(long value) {
        return value > 0;
    }

    public static boolean isPositive(double value) {
        return value > 0.0;
    }

    public static boolean isNonNegative(int value) {
        return value >= 0;
    }

    public static boolean isNonNegative(long value) {
        return value >= 0;
    }

    public static boolean isNonNegative(double value) {
        return value >= 0.0;
    }

    public static boolean hasMinLength(String str, int minLength) {
        return str != null && str.length() >= minLength;
    }

    public static boolean hasMaxLength(String str, int maxLength) {
        return str != null && str.length() <= maxLength;
    }

    public static boolean hasLengthBetween(String str, int minLength, int maxLength) {
        return str != null && str.length() >= minLength && str.length() <= maxLength;
    }
}