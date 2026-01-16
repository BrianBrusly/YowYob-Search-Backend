package com.yowyob.common.constant;

import java.util.regex.Pattern;

/**
 * Expressions régulières pré-compilées pour validation
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Centralise tous les patterns regex utilisés dans l'application
 * Les patterns sont pré-compilés pour de meilleures performances
 */
public final class RegexPatterns {

    private RegexPatterns() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static final String PHONE_REGEX = "^\\+?[1-9]\\d{1,14}$";
    public static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    public static final String CAMEROON_PHONE_REGEX = "^(\\+237|237)?[26]\\d{8}$";
    public static final Pattern CAMEROON_PHONE_PATTERN = Pattern.compile(CAMEROON_PHONE_REGEX);

    public static final String URL_REGEX = "^https?://[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$";
    public static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public static final String IPV4_REGEX = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    public static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    public static final String IPV6_REGEX = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";
    public static final Pattern IPV6_PATTERN = Pattern.compile(IPV6_REGEX);

    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_-]{3,50}$";
    public static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

    public static final String SLUG_REGEX = "^[a-z0-9]+(?:-[a-z0-9]+)*$";
    public static final Pattern SLUG_PATTERN = Pattern.compile(SLUG_REGEX);

    public static final String ALPHA_NUMERIC_REGEX = "^[a-zA-Z0-9]+$";
    public static final Pattern ALPHA_NUMERIC_PATTERN = Pattern.compile(ALPHA_NUMERIC_REGEX);

    public static final String ALPHA_REGEX = "^[a-zA-Z]+$";
    public static final Pattern ALPHA_PATTERN = Pattern.compile(ALPHA_REGEX);

    public static final String NUMERIC_REGEX = "^[0-9]+$";
    public static final Pattern NUMERIC_PATTERN = Pattern.compile(NUMERIC_REGEX);

    public static final String UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    public static final Pattern UUID_PATTERN = Pattern.compile(UUID_REGEX);

    public static final String COORDINATES_REGEX = "^-?([1-8]?[0-9]\\.\\d+|90\\.0+),\\s*-?(1[0-7][0-9]\\.\\d+|180\\.0+|[1-9]?[0-9]\\.\\d+)$";
    public static final Pattern COORDINATES_PATTERN = Pattern.compile(COORDINATES_REGEX);

    public static final String PASSWORD_UPPERCASE_REGEX = ".*[A-Z].*";
    public static final Pattern PASSWORD_UPPERCASE_PATTERN = Pattern.compile(PASSWORD_UPPERCASE_REGEX);

    public static final String PASSWORD_LOWERCASE_REGEX = ".*[a-z].*";
    public static final Pattern PASSWORD_LOWERCASE_PATTERN = Pattern.compile(PASSWORD_LOWERCASE_REGEX);

    public static final String PASSWORD_DIGIT_REGEX = ".*\\d.*";
    public static final Pattern PASSWORD_DIGIT_PATTERN = Pattern.compile(PASSWORD_DIGIT_REGEX);

    public static final String PASSWORD_SPECIAL_CHAR_REGEX = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*";
    public static final Pattern PASSWORD_SPECIAL_CHAR_PATTERN = Pattern.compile(PASSWORD_SPECIAL_CHAR_REGEX);
}