package com.yowyob.gateway.util;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Helper pour la collecte des métriques
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@Component
public class MetricsHelper {

    private static final Logger log = LoggerFactory.getLogger(MetricsHelper.class);

    private final MeterRegistry meterRegistry;

    public MetricsHelper(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    private static final String METRICS_PREFIX = "gateway.";
    private final Map<String, Long> requestCounters = new ConcurrentHashMap<>();
    private final Map<String, Long> errorCounters = new ConcurrentHashMap<>();

    /**
     * Enregistre une requête
     * 
     * @param route      Route ID
     * @param status     Code de statut HTTP
     * @param durationMs Durée en millisecondes
     */
    public void recordRequest(String route, int status, long durationMs) {
        try {
            // Tags communs
            Tags tags = Tags.of(
                    "route", route,
                    "status", String.valueOf(status),
                    "status_category", getStatusCategory(status));

            // Counter total
            meterRegistry.counter(METRICS_PREFIX + "requests.total", tags)
                    .increment();

            // Timer pour la durée
            meterRegistry.timer(METRICS_PREFIX + "request.duration", tags)
                    .record(durationMs, TimeUnit.MILLISECONDS);

            // Mettre à jour les compteurs internes
            String routeKey = "route:" + route;
            requestCounters.merge(routeKey, 1L, Long::sum);

            if (status >= 400) {
                errorCounters.merge(routeKey, 1L, Long::sum);
            }

        } catch (Exception e) {
            log.error("Erreur lors de l'enregistrement des métriques", e);
        }
    }

    /**
     * Enregistre une erreur
     * 
     * @param route         Route ID
     * @param exceptionType Type d'exception
     * @param errorMessage  Message d'erreur
     */
    public void recordError(String route, String exceptionType, String errorMessage) {
        try {
            Tags tags = Tags.of(
                    "route", route,
                    "exception", exceptionType,
                    "error", truncateErrorMessage(errorMessage));

            meterRegistry.counter(METRICS_PREFIX + "errors.total", tags)
                    .increment();

        } catch (Exception e) {
            log.error("Erreur lors de l'enregistrement des métriques d'erreur", e);
        }
    }

    /**
     * Enregistre une opération de cache
     * 
     * @param route       Route ID
     * @param cacheType   Type de cache (HIT, MISS, STALE)
     * @param cacheTimeMs Temps de cache en ms
     */
    public void recordCacheOperation(String route, String cacheType, long cacheTimeMs) {
        try {
            Tags tags = Tags.of(
                    "route", route,
                    "cache_type", cacheType);

            meterRegistry.counter(METRICS_PREFIX + "cache.operations", tags)
                    .increment();

            if ("HIT".equals(cacheType)) {
                meterRegistry.timer(METRICS_PREFIX + "cache.hit.time", tags)
                        .record(cacheTimeMs, TimeUnit.MILLISECONDS);
            }

        } catch (Exception e) {
            log.error("Erreur lors de l'enregistrement des métriques de cache", e);
        }
    }

    /**
     * Enregistre une opération de rate limiting
     * 
     * @param route   Route ID
     * @param keyType Type de clé (USER, IP, API_KEY)
     * @param allowed true si la requête est autorisée
     */
    public void recordRateLimit(String route, String keyType, boolean allowed) {
        try {
            Tags tags = Tags.of(
                    "route", route,
                    "key_type", keyType,
                    "allowed", String.valueOf(allowed));

            meterRegistry.counter(METRICS_PREFIX + "ratelimit.requests", tags)
                    .increment();

        } catch (Exception e) {
            log.error("Erreur lors de l'enregistrement des métriques de rate limiting", e);
        }
    }

    /**
     * Enregistre un changement d'état de Circuit Breaker
     * 
     * @param circuitBreakerName Nom du Circuit Breaker
     * @param oldState           Ancien état
     * @param newState           Nouvel état
     */
    public void recordCircuitBreakerStateChange(
            String circuitBreakerName,
            String oldState,
            String newState) {

        try {
            Tags tags = Tags.of(
                    "circuit_breaker", circuitBreakerName,
                    "old_state", oldState,
                    "new_state", newState);

            meterRegistry.counter(METRICS_PREFIX + "circuitbreaker.state_changes", tags)
                    .increment();

            // Gauge pour l'état actuel
            meterRegistry.gauge(METRICS_PREFIX + "circuitbreaker.state",
                    Tags.of("circuit_breaker", circuitBreakerName),
                    getStateValue(newState));

        } catch (Exception e) {
            log.error("Erreur lors de l'enregistrement des métriques de Circuit Breaker", e);
        }
    }

    /**
     * Récupère le registre de métriques
     * 
     * @return MeterRegistry
     */
    public MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }

    /**
     * Récupère les statistiques agrégées
     * 
     * @return Map des statistiques
     */
    public Map<String, Object> getAggregatedStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();

        long totalRequests = requestCounters.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        long totalErrors = errorCounters.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        stats.put("total_requests", totalRequests);
        stats.put("total_errors", totalErrors);
        stats.put("error_rate", totalRequests > 0 ? (double) totalErrors / totalRequests : 0.0);
        stats.put("routes_monitored", requestCounters.size());

        return stats;
    }

    /**
     * Détermine la catégorie d'un code de statut HTTP
     * 
     * @param status Code de statut
     * @return Catégorie (1xx, 2xx, 3xx, 4xx, 5xx)
     */
    private String getStatusCategory(int status) {
        if (status >= 100 && status < 200)
            return "1xx";
        if (status >= 200 && status < 300)
            return "2xx";
        if (status >= 300 && status < 400)
            return "3xx";
        if (status >= 400 && status < 500)
            return "4xx";
        if (status >= 500 && status < 600)
            return "5xx";
        return "unknown";
    }

    /**
     * Tronque un message d'erreur pour les métriques
     * 
     * @param errorMessage Message d'erreur
     * @return Message tronqué
     */
    private String truncateErrorMessage(String errorMessage) {
        if (errorMessage == null) {
            return "null";
        }

        if (errorMessage.length() > 100) {
            return errorMessage.substring(0, 100) + "...";
        }

        return errorMessage;
    }

    /**
     * Convertit un état de Circuit Breaker en valeur numérique
     * 
     * @param state État (CLOSED, OPEN, HALF_OPEN)
     * @return Valeur numérique
     */
    private int getStateValue(String state) {
        return switch (state) {
            case "CLOSED" -> 0;
            case "HALF_OPEN" -> 1;
            case "OPEN" -> 2;
            case "DISABLED" -> 3;
            default -> -1;
        };
    }
}