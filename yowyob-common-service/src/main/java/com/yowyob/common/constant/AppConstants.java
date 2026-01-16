package com.yowyob.common.constant;

/**
 * Constantes globales de l'application YowYob Search
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Centralise toutes les constantes utilisées à travers la plateforme
 * pour garantir la cohérence et faciliter la maintenance
 */
public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    public static final String APP_NAME = "YowYob Search Platform";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_DESCRIPTION = "Plateforme de recherche intelligente distribuée";

    public static final String DEFAULT_TIMEZONE = "UTC";
    public static final String DEFAULT_LOCALE = "fr_CM";
    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int MIN_PAGE_SIZE = 1;

    public static final String DEFAULT_SORT_DIRECTION = "DESC";
    public static final String DEFAULT_SORT_FIELD = "createdAt";

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 128;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;

    public static final long MAX_FILE_SIZE_BYTES = 10485760L;
    public static final long MAX_REQUEST_SIZE_BYTES = 10485760L;

    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api/" + API_VERSION;

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String USER_ID_HEADER = "X-User-ID";
    public static final String TRACE_ID_HEADER = "X-Trace-ID";

    public static final int DEFAULT_THREAD_POOL_SIZE = 10;
    public static final int MAX_THREAD_POOL_SIZE = 50;
    public static final int THREAD_POOL_QUEUE_CAPACITY = 100;

    public static final long DEFAULT_TIMEOUT_MS = 30000L;
    public static final long SHORT_TIMEOUT_MS = 5000L;
    public static final long LONG_TIMEOUT_MS = 60000L;

    public static final String SYSTEM_USER = "SYSTEM";
    public static final String ANONYMOUS_USER = "ANONYMOUS";
}