package com.yowyob.common.constant;

/**
 * Codes d'erreur standardisés pour toute la plateforme
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Convention de nommage: CATEGORY_SPECIFIC_ERROR
 * Permet une identification rapide de la source et du type d'erreur
 */
public final class ErrorConstants {

    private ErrorConstants() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    public static final String AUTH_INVALID_TOKEN = "AUTH_INVALID_TOKEN";
    public static final String AUTH_EXPIRED_TOKEN = "AUTH_EXPIRED_TOKEN";
    public static final String AUTH_MISSING_TOKEN = "AUTH_MISSING_TOKEN";
    public static final String AUTH_INVALID_CREDENTIALS = "AUTH_INVALID_CREDENTIALS";
    public static final String AUTH_INSUFFICIENT_PERMISSIONS = "AUTH_INSUFFICIENT_PERMISSIONS";
    public static final String AUTH_ACCOUNT_LOCKED = "AUTH_ACCOUNT_LOCKED";
    public static final String AUTH_ACCOUNT_DISABLED = "AUTH_ACCOUNT_DISABLED";

    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String VALIDATION_INVALID_EMAIL = "VALIDATION_INVALID_EMAIL";
    public static final String VALIDATION_INVALID_PHONE = "VALIDATION_INVALID_PHONE";
    public static final String VALIDATION_INVALID_PASSWORD = "VALIDATION_INVALID_PASSWORD";
    public static final String VALIDATION_INVALID_URL = "VALIDATION_INVALID_URL";
    public static final String VALIDATION_INVALID_DATE = "VALIDATION_INVALID_DATE";
    public static final String VALIDATION_INVALID_COORDINATES = "VALIDATION_INVALID_COORDINATES";

    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String RESOURCE_ALREADY_EXISTS = "RESOURCE_ALREADY_EXISTS";
    public static final String RESOURCE_CONFLICT = "RESOURCE_CONFLICT";
    public static final String RESOURCE_GONE = "RESOURCE_GONE";

    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    public static final String USER_EMAIL_ALREADY_EXISTS = "USER_EMAIL_ALREADY_EXISTS";
    public static final String USER_USERNAME_ALREADY_EXISTS = "USER_USERNAME_ALREADY_EXISTS";

    public static final String SEARCH_QUERY_TOO_LONG = "SEARCH_QUERY_TOO_LONG";
    public static final String SEARCH_QUERY_TOO_SHORT = "SEARCH_QUERY_TOO_SHORT";
    public static final String SEARCH_INVALID_FILTERS = "SEARCH_INVALID_FILTERS";
    public static final String SEARCH_TIMEOUT = "SEARCH_TIMEOUT";

    public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
    public static final String SERVICE_TIMEOUT = "SERVICE_TIMEOUT";
    public static final String SERVICE_INTERNAL_ERROR = "SERVICE_INTERNAL_ERROR";

    public static final String DATABASE_CONNECTION_ERROR = "DATABASE_CONNECTION_ERROR";
    public static final String DATABASE_QUERY_ERROR = "DATABASE_QUERY_ERROR";
    public static final String DATABASE_CONSTRAINT_VIOLATION = "DATABASE_CONSTRAINT_VIOLATION";

    public static final String CACHE_ERROR = "CACHE_ERROR";
    public static final String CACHE_CONNECTION_ERROR = "CACHE_CONNECTION_ERROR";

    public static final String MESSAGING_ERROR = "MESSAGING_ERROR";
    public static final String MESSAGING_PUBLISH_ERROR = "MESSAGING_PUBLISH_ERROR";
    public static final String MESSAGING_CONSUME_ERROR = "MESSAGING_CONSUME_ERROR";

    public static final String FILE_TOO_LARGE = "FILE_TOO_LARGE";
    public static final String FILE_INVALID_TYPE = "FILE_INVALID_TYPE";
    public static final String FILE_UPLOAD_ERROR = "FILE_UPLOAD_ERROR";
    public static final String FILE_NOT_FOUND = "FILE_NOT_FOUND";

    public static final String RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED";
    public static final String QUOTA_EXCEEDED = "QUOTA_EXCEEDED";

    public static final String EXTERNAL_API_ERROR = "EXTERNAL_API_ERROR";
    public static final String EXTERNAL_API_TIMEOUT = "EXTERNAL_API_TIMEOUT";
    public static final String EXTERNAL_API_UNAVAILABLE = "EXTERNAL_API_UNAVAILABLE";
}