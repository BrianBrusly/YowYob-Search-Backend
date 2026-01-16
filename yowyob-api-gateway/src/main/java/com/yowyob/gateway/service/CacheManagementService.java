package com.yowyob.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service de gestion du cache pour le Gateway
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Gère les opérations de cache (invalidation, réchauffement,
 *          statistiques)
 *          pour les réponses HTTP mises en cache dans Redis
 */
@Service
public class CacheManagementService {

    private static final Logger log = LoggerFactory.getLogger(CacheManagementService.class);

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ReactiveRedisTemplate<String, Object> redisObjectTemplate;

    public CacheManagementService(ReactiveRedisTemplate<String, String> redisTemplate,
            ReactiveRedisTemplate<String, Object> redisObjectTemplate) {
        this.redisTemplate = redisTemplate;
        this.redisObjectTemplate = redisObjectTemplate;
    }

    // Statistiques en mémoire
    private final Map<String, CacheStatistics> cacheStatistics = new ConcurrentHashMap<>();
    private final AtomicLong totalCacheHits = new AtomicLong(0);
    private final AtomicLong totalCacheMisses = new AtomicLong(0);
    private final AtomicLong totalCacheInvalidations = new AtomicLong(0);

    // Constantes
    private static final String CACHE_PREFIX = "gateway:cache:";
    private static final String RATE_LIMIT_PREFIX = "gateway:ratelimit:";
    private static final String FALLBACK_PREFIX = "gateway:fallback:";
    private static final String STATS_PREFIX = "gateway:cache:stats:";

    /**
     * Invalide le cache selon un pattern
     * 
     * @param pattern Pattern Redis (ex: "gateway:cache:*search*")
     * @return Nombre d'entrées invalidées
     */
    public Mono<Long> invalidateCache(String pattern) {
        log.info("Invalidation du cache avec le pattern: {}", pattern);

        return redisTemplate.keys(pattern)
                .flatMap(key -> {
                    log.debug("Suppression de la clé de cache: {}", key);
                    return redisTemplate.delete(key)
                            .doOnSuccess(deleted -> {
                                if (deleted > 0) {
                                    totalCacheInvalidations.incrementAndGet();
                                    updateCacheStats(key, "invalidation");
                                }
                            });
                })
                .count()
                .doOnSuccess(count -> log.info("{} entrées de cache invalidées avec le pattern: {}", count, pattern))
                .doOnError(error -> log.error("Erreur lors de l'invalidation du cache", error));
    }

    /**
     * Invalide le cache pour une route spécifique
     * 
     * @param routeId ID de la route
     * @return Nombre d'entrées invalidées
     */
    public Mono<Long> invalidateCacheForRoute(String routeId) {
        String pattern = CACHE_PREFIX + "*route:" + routeId + "*";
        return invalidateCache(pattern);
    }

    /**
     * Invalide le cache pour un utilisateur spécifique
     * 
     * @param userId ID de l'utilisateur
     * @return Nombre d'entrées invalidées
     */
    public Mono<Long> invalidateCacheForUser(String userId) {
        String pattern = CACHE_PREFIX + "*user:" + userId + "*";
        return invalidateCache(pattern);
    }

    /**
     * Invalide tout le cache du Gateway
     * 
     * @return Nombre total d'entrées invalidées
     */
    public Mono<Long> invalidateAllCache() {
        String pattern = CACHE_PREFIX + "*";
        return invalidateCache(pattern);
    }

    /**
     * Réchauffe le cache pour des routes spécifiques
     * 
     * @param routes Liste des routes à réchauffer
     * @return Mono<Void>
     */
    public Mono<Void> warmupCache(List<String> routes) {
        log.info("Réchauffement du cache pour {} routes", routes.size());

        return Flux.fromIterable(routes)
                .flatMap(route -> warmupRouteCache(route))
                .then()
                .doOnSuccess(v -> log.info("Cache réchauffé avec succès"))
                .doOnError(error -> log.error("Erreur lors du réchauffement du cache", error));
    }

    /**
     * Réchauffe le cache pour une route spécifique
     * 
     * @param route Route à réchauffer
     * @return Mono<Void>
     */
    private Mono<Void> warmupRouteCache(String route) {
        // Implémentation spécifique selon le type de route
        log.debug("Réchauffement du cache pour la route: {}", route);

        // Ici, on pourrait appeler des endpoints pour pré-charger le cache
        // Pour l'instant, on se contente de logger
        return Mono.fromRunnable(() -> log.debug("Route {} marquée pour réchauffement", route));
    }

    /**
     * Récupère les statistiques du cache
     * 
     * @return CacheStatistics
     */
    public Mono<CacheStatistics> getCacheStats() {
        return Mono.fromCallable(() -> {
            CacheStatistics stats = new CacheStatistics();

            // Statistiques agrégées
            long totalHits = totalCacheHits.get();
            long totalMisses = totalCacheMisses.get();
            long totalRequests = totalHits + totalMisses;

            stats.setTotalHits(totalHits);
            stats.setTotalMisses(totalMisses);
            stats.setTotalRequests(totalRequests);
            stats.setHitRate(totalRequests > 0 ? (double) totalHits / totalRequests : 0.0);

            // Statistiques par type
            stats.setStatsByType(new HashMap<>(cacheStatistics));

            // Taille du cache
            return getCacheSize()
                    .doOnSuccess(size -> stats.setTotalEntries(size))
                    .thenReturn(stats)
                    .block();
        });
    }

    /**
     * Récupère la taille actuelle du cache
     * 
     * @return Nombre d'entrées dans le cache
     */
    public Mono<Long> getCacheSize() {
        String pattern = CACHE_PREFIX + "*";
        return redisTemplate.keys(pattern).count();
    }

    /**
     * Évince les entrées de cache expirées
     * 
     * @return Nombre d'entrées évincées
     */
    public Mono<Long> evictExpiredEntries() {
        log.info("Éviction des entrées de cache expirées...");

        // Redis gère automatiquement l'expiration avec TTL
        // Cette méthode vérifie simplement les entrées expirées
        String pattern = CACHE_PREFIX + "*";

        return redisTemplate.keys(pattern)
                .filterWhen(key -> redisTemplate.getExpire(key)
                        .map(ttl -> ttl != null && ttl.getSeconds() <= 0))
                .flatMap(key -> {
                    log.debug("Éviction de la clé expirée: {}", key);
                    return redisTemplate.delete(key);
                })
                .count()
                .doOnSuccess(count -> log.info("{} entrées expirées évincées", count))
                .doOnError(error -> log.error("Erreur lors de l'éviction des entrées expirées", error));
    }

    /**
     * Purge les entrées de cache anciennes
     * 
     * @param olderThanHours Nombre d'heures
     * @return Nombre d'entrées purgées
     */
    public Mono<Long> purgeOldEntries(int olderThanHours) {
        log.info("Purge des entrées de cache plus anciennes que {} heures", olderThanHours);

        Instant cutoff = Instant.now().minus(Duration.ofHours(olderThanHours));
        String pattern = CACHE_PREFIX + "*";

        return redisTemplate.keys(pattern)
                .filterWhen(key -> getCacheEntryTimestamp(key)
                        .map(timestamp -> timestamp.isBefore(cutoff)))
                .flatMap(key -> {
                    log.debug("Purge de la clé ancienne: {}", key);
                    return redisTemplate.delete(key);
                })
                .count()
                .doOnSuccess(count -> log.info("{} entrées anciennes purgées", count))
                .doOnError(error -> log.error("Erreur lors de la purge des entrées anciennes", error));
    }

    /**
     * Récupère le timestamp d'une entrée de cache
     * 
     * @param key Clé Redis
     * @return Timestamp de l'entrée
     */
    private Mono<Instant> getCacheEntryTimestamp(String key) {
        return redisObjectTemplate.opsForValue().get(key)
                .flatMap(value -> {
                    if (value instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> map = (Map<String, Object>) value;
                        if (map.containsKey("timestamp")) {
                            Object timestamp = map.get("timestamp");
                            if (timestamp instanceof String) {
                                return Mono.just(Instant.parse((String) timestamp));
                            } else if (timestamp instanceof Instant) {
                                return Mono.just((Instant) timestamp);
                            }
                        }
                    }
                    return Mono.just(Instant.now());
                })
                .defaultIfEmpty(Instant.now());
    }

    /**
     * Met à jour les statistiques du cache
     * 
     * @param key       Clé de cache
     * @param operation Type d'opération (hit, miss, invalidation)
     */
    public void updateCacheStats(String key, String operation) {
        String cacheType = extractCacheTypeFromKey(key);

        CacheStatistics stats = cacheStatistics.computeIfAbsent(
                cacheType, k -> new CacheStatistics());

        switch (operation.toLowerCase()) {
            case "hit":
                stats.incrementHits();
                totalCacheHits.incrementAndGet();
                break;
            case "miss":
                stats.incrementMisses();
                totalCacheMisses.incrementAndGet();
                break;
            case "invalidation":
                stats.incrementInvalidations();
                totalCacheInvalidations.incrementAndGet();
                break;
        }

        // Sauvegarder les statistiques périodiquement dans Redis
        saveStatsToRedis();
    }

    /**
     * Extrait le type de cache depuis la clé
     * 
     * @param key Clé Redis
     * @return Type de cache
     */
    private String extractCacheTypeFromKey(String key) {
        if (key.contains(":search:")) {
            return "search";
        } else if (key.contains(":user:")) {
            return "user";
        } else if (key.contains(":geo:")) {
            return "geo";
        } else if (key.contains(":shop:")) {
            return "shop";
        } else if (key.contains(":ratelimit:")) {
            return "ratelimit";
        } else if (key.contains(":fallback:")) {
            return "fallback";
        } else {
            return "other";
        }
    }

    /**
     * Sauvegarde les statistiques dans Redis
     */
    private void saveStatsToRedis() {
        try {
            Map<String, Object> statsData = new HashMap<>();
            statsData.put("totalHits", totalCacheHits.get());
            statsData.put("totalMisses", totalCacheMisses.get());
            statsData.put("totalInvalidations", totalCacheInvalidations.get());
            statsData.put("timestamp", Instant.now().toString());
            statsData.put("statsByType", new HashMap<>(cacheStatistics));

            redisObjectTemplate.opsForValue()
                    .set(STATS_PREFIX + "current", statsData, Duration.ofHours(1))
                    .subscribe(
                            success -> log.debug("Statistiques sauvegardées dans Redis"),
                            error -> log.error("Erreur lors de la sauvegarde des statistiques", error));
        } catch (Exception e) {
            log.error("Erreur lors de la préparation des statistiques", e);
        }
    }

    /**
     * Charge les statistiques depuis Redis
     */
    public Mono<Void> loadStatsFromRedis() {
        return redisObjectTemplate.opsForValue().get(STATS_PREFIX + "current")
                .flatMap(data -> {
                    if (data instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> statsData = (Map<String, Object>) data;

                        totalCacheHits.set(((Number) statsData.getOrDefault("totalHits", 0L)).longValue());
                        totalCacheMisses.set(((Number) statsData.getOrDefault("totalMisses", 0L)).longValue());
                        totalCacheInvalidations
                                .set(((Number) statsData.getOrDefault("totalInvalidations", 0L)).longValue());

                        if (statsData.containsKey("statsByType")) {
                            @SuppressWarnings("unchecked")
                            Map<String, CacheStatistics> loadedStats = (Map<String, CacheStatistics>) statsData
                                    .get("statsByType");
                            cacheStatistics.clear();
                            cacheStatistics.putAll(loadedStats);
                        }

                        log.info("Statistiques chargées depuis Redis");
                    }
                    return Mono.empty();
                })
                .doOnError(error -> log.error("Erreur lors du chargement des statistiques", error))
                .then();
    }

    /**
     * Analyse l'utilisation du cache
     * 
     * @return Map avec l'analyse détaillée
     */
    public Mono<Map<String, Object>> analyzeCacheUsage() {
        String pattern = CACHE_PREFIX + "*";

        return redisTemplate.keys(pattern)
                .collectList()
                .flatMap(keys -> {
                    Map<String, Object> analysis = new HashMap<>();

                    // Analyse par type
                    Map<String, Long> countByType = new HashMap<>();
                    Map<String, Long> sizeByType = new HashMap<>();

                    for (String key : keys) {
                        String type = extractCacheTypeFromKey(key);
                        countByType.merge(type, 1L, Long::sum);

                        // Estimer la taille (approximative)
                        redisTemplate.opsForValue().size(key)
                                .subscribe(size -> sizeByType.merge(type, size, Long::sum));
                    }

                    analysis.put("totalKeys", (long) keys.size());
                    analysis.put("countByType", countByType);
                    analysis.put("sizeByType", sizeByType);

                    // Distribution des TTLs
                    return analyzeTtlDistribution(keys)
                            .map(ttlDistribution -> {
                                analysis.put("ttlDistribution", ttlDistribution);
                                return analysis;
                            });
                });
    }

    /**
     * Analyse la distribution des TTLs
     * 
     * @param keys Liste des clés
     * @return Distribution des TTLs
     */
    private Mono<Map<String, Long>> analyzeTtlDistribution(List<String> keys) {
        Map<String, Long> distribution = new HashMap<>();

        return Flux.fromIterable(keys)
                .flatMap(key -> redisTemplate.getExpire(key)
                        .map(ttl -> {
                            if (ttl == null)
                                return "no-ttl";
                            long seconds = ttl.getSeconds();
                            if (seconds < 60)
                                return "<1min";
                            if (seconds < 300)
                                return "1-5min";
                            if (seconds < 1800)
                                return "5-30min";
                            if (seconds < 3600)
                                return "30-60min";
                            if (seconds < 86400)
                                return "1-24h";
                            return ">24h";
                        })
                        .doOnNext(category -> distribution.merge(category, 1L, Long::sum)))
                .then(Mono.just(distribution));
    }

    /**
     * Tâche planifiée pour l'entretien du cache
     * Exécutée toutes les heures
     */
    @Scheduled(fixedDelay = 3600000) // 1 heure
    public void scheduledCacheMaintenance() {
        log.info("Début de l'entretien planifié du cache");

        // 1. Éviction des entrées expirées
        evictExpiredEntries().subscribe();

        // 2. Purge des entrées anciennes (plus de 24h)
        purgeOldEntries(24).subscribe();

        // 3. Sauvegarde des statistiques
        saveStatsToRedis();

        // 4. Analyse de l'utilisation
        analyzeCacheUsage()
                .doOnSuccess(analysis -> log.debug("Analyse du cache: {}", analysis))
                .subscribe();

        log.info("Entretien du cache terminé");
    }

    /**
     * Classe interne pour les statistiques du cache
     */
    public static class CacheStatistics {
        private long hits;
        private long misses;
        private long invalidations;
        private long totalEntries;
        private double hitRate;
        private long totalRequests;
        private Map<String, CacheStatistics> statsByType;

        public CacheStatistics() {
            this.hits = 0;
            this.misses = 0;
            this.invalidations = 0;
            this.totalEntries = 0;
            this.hitRate = 0.0;
            this.totalRequests = 0;
            this.statsByType = new HashMap<>();
        }

        public void incrementHits() {
            hits++;
            updateTotals();
        }

        public void incrementMisses() {
            misses++;
            updateTotals();
        }

        public void incrementInvalidations() {
            invalidations++;
        }

        private void updateTotals() {
            totalRequests = hits + misses;
            hitRate = totalRequests > 0 ? (double) hits / totalRequests : 0.0;
        }

        // Getters et Setters
        public long getHits() {
            return hits;
        }

        public void setHits(long hits) {
            this.hits = hits;
            updateTotals();
        }

        public long getMisses() {
            return misses;
        }

        public void setMisses(long misses) {
            this.misses = misses;
            updateTotals();
        }

        public void setTotalHits(long totalHits) {
            this.hits = totalHits;
            updateTotals();
        }

        public void setTotalMisses(long totalMisses) {
            this.misses = totalMisses;
            updateTotals();
        }

        public long getInvalidations() {
            return invalidations;
        }

        public void setInvalidations(long invalidations) {
            this.invalidations = invalidations;
        }

        public long getTotalEntries() {
            return totalEntries;
        }

        public void setTotalEntries(long totalEntries) {
            this.totalEntries = totalEntries;
        }

        public double getHitRate() {
            return hitRate;
        }

        public void setHitRate(double hitRate) {
            this.hitRate = hitRate;
        }

        public long getTotalRequests() {
            return totalRequests;
        }

        public void setTotalRequests(long totalRequests) {
            this.totalRequests = totalRequests;
        }

        public Map<String, CacheStatistics> getStatsByType() {
            return statsByType;
        }

        public void setStatsByType(Map<String, CacheStatistics> statsByType) {
            this.statsByType = statsByType;
        }

        @Override
        public String toString() {
            return String.format(
                    "CacheStatistics{hits=%d, misses=%d, hitRate=%.2f, invalidations=%d, totalEntries=%d}",
                    hits, misses, hitRate, invalidations, totalEntries);
        }
    }
}