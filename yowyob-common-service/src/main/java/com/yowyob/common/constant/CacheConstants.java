package com.yowyob.common.constant;

/**
 * Constantes pour la gestion du cache Redis
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Définit les noms de cache, TTLs et préfixes de clés
 * pour une utilisation cohérente du cache à travers la plateforme
 */
public final class CacheConstants {

    private CacheConstants() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    public static final String CACHE_PREFIX = "yowyob:";

    public static final String USER_CACHE = "user-cache";
    public static final String SEARCH_CACHE = "search-cache";
    public static final String GEO_CACHE = "geo-cache";
    public static final String SHOP_CACHE = "shop-cache";
    public static final String STATS_CACHE = "stats-cache";

    public static final String USER_CACHE_KEY = CACHE_PREFIX + "user:";
    public static final String SEARCH_CACHE_KEY = CACHE_PREFIX + "search:";
    public static final String GEO_CACHE_KEY = CACHE_PREFIX + "geo:";
    public static final String SHOP_CACHE_KEY = CACHE_PREFIX + "shop:";
    public static final String STATS_CACHE_KEY = CACHE_PREFIX + "stats:";

    public static final long TTL_ONE_MINUTE = 60L;
    public static final long TTL_FIVE_MINUTES = 300L;
    public static final long TTL_TEN_MINUTES = 600L;
    public static final long TTL_THIRTY_MINUTES = 1800L;
    public static final long TTL_ONE_HOUR = 3600L;
    public static final long TTL_SIX_HOURS = 21600L;
    public static final long TTL_ONE_DAY = 86400L;
    public static final long TTL_ONE_WEEK = 604800L;

    public static final long SEARCH_CACHE_TTL = TTL_FIVE_MINUTES;
    public static final long USER_CACHE_TTL = TTL_THIRTY_MINUTES;
    public static final long GEO_CACHE_TTL = TTL_ONE_DAY;
    public static final long SHOP_CACHE_TTL = TTL_TEN_MINUTES;
    public static final long STATS_CACHE_TTL = TTL_ONE_HOUR;

    public static final String RATE_LIMIT_PREFIX = CACHE_PREFIX + "rate-limit:";
    public static final String SESSION_PREFIX = CACHE_PREFIX + "session:";
    public static final String TOKEN_BLACKLIST_PREFIX = CACHE_PREFIX + "token-blacklist:";
}