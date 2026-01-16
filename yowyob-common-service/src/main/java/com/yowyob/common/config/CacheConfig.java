package com.yowyob.common.config;

import com.yowyob.common.constant.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration du cache applicatif
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Configure le gestionnaire de cache (in-memory par défaut)
 * Les services peuvent surcharger avec Redis si nécessaire
 */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
                CacheConstants.USER_CACHE,
                CacheConstants.SEARCH_CACHE,
                CacheConstants.GEO_CACHE,
                CacheConstants.SHOP_CACHE,
                CacheConstants.STATS_CACHE
        );

        log.info("CacheManager configuré avec {} caches",
                cacheManager.getCacheNames().size());

        return cacheManager;
    }
}