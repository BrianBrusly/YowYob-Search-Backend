package com.yowyob.gateway.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import com.yowyob.gateway.dto.GatewayMetrics;
import com.yowyob.gateway.dto.GatewayMetrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.management.ManagementFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service d'agrégation des métriques
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@Service
public class MetricsAggregationService {

    private static final Logger log = LoggerFactory.getLogger(MetricsAggregationService.class);

    private final MeterRegistry meterRegistry;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public MetricsAggregationService(MeterRegistry meterRegistry,
            ReactiveRedisTemplate<String, String> redisTemplate,
            CircuitBreakerRegistry circuitBreakerRegistry,
            com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        this.meterRegistry = meterRegistry;
        this.redisTemplate = redisTemplate;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.objectMapper = objectMapper;
    }

    private static final String METRICS_PREFIX = "gateway:metrics:";
    private static final int RETENTION_DAYS = 30;

    // Cache pour les métriques agrégées
    private final Map<String, AggregatedMetrics> metricsCache = new ConcurrentHashMap<>();
    private Instant lastAggregationTime = Instant.now().minus(Duration.ofMinutes(5));

    /**
     * Récupère les métriques agrégées du Gateway
     * 
     * @return Métriques agrégées
     */
    public Mono<GatewayMetrics> getGatewayMetrics() {
        return Mono.fromCallable(() -> {
            Instant now = Instant.now();

            // Vérifier si le cache est frais (moins de 30 secondes)
            if (Duration.between(lastAggregationTime, now).getSeconds() < 30) {
                return getCachedMetrics();
            }

            // Calculer les nouvelles métriques
            GatewayMetrics metrics = calculateAggregatedMetrics();

            // Mettre en cache
            cacheMetrics(metrics);
            lastAggregationTime = now;

            return metrics;
        });
    }

    /**
     * Récupère les métriques détaillées d'une route
     * 
     * @param routeId ID de la route
     * @return Métriques de la route
     */
    public Mono<RouteMetrics> getRouteMetrics(String routeId) {
        return Mono.fromCallable(() -> {
            // Récupérer les métriques depuis Micrometer
            Map<String, Long> statusCounts = getStatusCountsForRoute(routeId);
            Timer timer = meterRegistry.find("gateway.request.duration")
                    .tag("route", routeId)
                    .timer();

            if (timer == null) {
                return RouteMetrics.builder()
                        .routeId(routeId)
                        .totalRequests(0)
                        .successfulRequests(0)
                        .failedRequests(0)
                        .averageResponseTime(0)
                        .statusCodes(statusCounts)
                        .cacheHits(0)
                        .cacheMisses(0)
                        .build();
            }

            long totalRequests = statusCounts.values().stream()
                    .mapToLong(Long::longValue)
                    .sum();

            long successfulRequests = statusCounts.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("2"))
                    .mapToLong(Map.Entry::getValue)
                    .sum();

            long failedRequests = totalRequests - successfulRequests;

            return RouteMetrics.builder()
                    .routeId(routeId)
                    .totalRequests(totalRequests)
                    .successfulRequests(successfulRequests)
                    .failedRequests(failedRequests)
                    .averageResponseTime(timer.mean(TimeUnit.MILLISECONDS))
                    .statusCodes(statusCounts)
                    .cacheHits(getCacheHitsForRoute(routeId))
                    .cacheMisses(getCacheMissesForRoute(routeId))
                    .build();
        });
    }

    /**
     * Récupère les métriques historiques
     * 
     * @param period Période (1h, 24h, 7d, 30d)
     * @return Série temporelle des métriques
     */
    public Mono<TimeSeriesMetrics> getHistoricalMetrics(String period) {
        Duration duration = parsePeriod(period);
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(duration);
        double startScore = startTime.toEpochMilli();
        double endScore = endTime.toEpochMilli();

        return redisTemplate.opsForZSet()
                .rangeByScore(METRICS_PREFIX + "history", Range.closed(startScore, endScore))
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, java.util.Map.class);
                    } catch (Exception e) {
                        return Collections.emptyMap();
                    }
                })
                .collectList()
                .map(list -> {
                    TimeSeriesMetrics timeSeries = new TimeSeriesMetrics();
                    timeSeries.setPeriod(period);
                    timeSeries.setStartTime(startTime);
                    timeSeries.setEndTime(endTime);

                    Map<Instant, Map<String, Double>> data = new TreeMap<>();
                    for (Object obj : list) {
                        if (obj instanceof Map) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> map = (Map<String, Object>) obj;
                            String tsStr = (String) map.get("timestamp");
                            if (tsStr != null) {
                                Instant ts = Instant.parse(tsStr);
                                Map<String, Double> metrics = new HashMap<>();
                                metrics.put("requests_per_second", getDouble(map, "requestsPerSecond"));
                                metrics.put("average_response_time", getDouble(map, "averageResponseTime"));
                                metrics.put("error_rate", getDouble(map, "errorRate"));
                                data.put(ts, metrics);
                            }
                        }
                    }
                    timeSeries.setData(data);
                    return timeSeries;
                });
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number)
            return ((Number) val).doubleValue();
        return 0.0;
    }

    @Scheduled(fixedRate = 300000)
    public void scheduledMetricsSnapshot() {
        GatewayMetrics metrics = calculateAggregatedMetrics();
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("timestamp", Instant.now().toString());
        snapshot.put("totalRequests", metrics.getOverview().getTotalRequests());
        snapshot.put("averageResponseTime", metrics.getOverview().getAverageResponseTime());
        snapshot.put("errorRate", metrics.getOverview().getErrorRate());
        snapshot.put("requestsPerSecond", metrics.getOverview().getRequestsPerSecond());

        try {
            String json = objectMapper.writeValueAsString(snapshot);
            double score = Instant.now().toEpochMilli();
            redisTemplate.opsForZSet().add(METRICS_PREFIX + "history", json, score).subscribe();

            // Clean up old metrics
            double minScore = Instant.now().minus(Duration.ofDays(RETENTION_DAYS)).toEpochMilli();
            redisTemplate.opsForZSet().removeRangeByScore(METRICS_PREFIX + "history",
                    Range.from(Range.Bound.<Double>unbounded())
                            .to(Range.Bound.inclusive(minScore)))
                    .subscribe();
        } catch (Exception e) {
            log.error("Failed to save metrics snapshot", e);
        }
    }

    /**
     * Récupère les métriques par service
     * 
     * @param service Nom du service
     * @return Métriques du service
     */
    public Mono<ServiceMetrics> getServiceMetrics(String service) {
        return Mono.fromCallable(() -> {
            // Trouver toutes les routes pour ce service
            List<String> routes = findRoutesForService(service);

            long totalRequests = 0;
            long successfulRequests = 0;
            double totalResponseTime = 0;
            int routeCount = 0;

            for (String route : routes) {
                RouteMetrics routeMetrics = getRouteMetrics(route).block();
                if (routeMetrics != null) {
                    totalRequests += routeMetrics.getTotalRequests();
                    successfulRequests += routeMetrics.getSuccessfulRequests();
                    totalResponseTime += routeMetrics.getAverageResponseTime();
                    routeCount++;
                }
            }

            double averageResponseTime = routeCount > 0 ? totalResponseTime / routeCount : 0;
            double errorRate = totalRequests > 0 ? (double) (totalRequests - successfulRequests) / totalRequests * 100
                    : 0;

            return ServiceMetrics.builder()
                    .serviceName(service)
                    .totalRequests(totalRequests)
                    .successfulRequests(successfulRequests)
                    .failedRequests(totalRequests - successfulRequests)
                    .averageResponseTime(averageResponseTime)
                    .errorRate(errorRate)
                    .activeRoutes(routeCount)
                    .circuitBreakerStatus(getCircuitBreakerStatusForService(service))
                    .build();
        });
    }

    /**
     * Calcule les métriques agrégées
     * 
     * @return Métriques agrégées
     */
    private GatewayMetrics calculateAggregatedMetrics() {
        GatewayMetrics.Overview overview = calculateOverviewMetrics();
        List<RouteMetrics> routes = calculateAllRouteMetrics();
        List<CircuitBreakerMetrics> circuitBreakers = calculateCircuitBreakerMetrics();
        CacheMetrics cache = calculateCacheMetrics();
        SystemMetrics system = calculateSystemMetrics();

        return GatewayMetrics.builder()
                .overview(overview)
                .routes(routes)
                .circuitBreakers(circuitBreakers)
                .cache(cache)
                .system(system)
                .build();
    }

    /**
     * Calcule les métriques d'overview
     * 
     * @return Métriques d'overview
     */
    private GatewayMetrics.Overview calculateOverviewMetrics() {
        long totalRequests = getTotalRequests();
        long successfulRequests = getSuccessfulRequests();
        long failedRequests = totalRequests - successfulRequests;

        return GatewayMetrics.Overview.builder()
                .totalRequests(totalRequests)
                .successfulRequests(successfulRequests)
                .failedRequests(failedRequests)
                .averageResponseTime(getAverageResponseTime())
                .p95ResponseTime(getPercentileResponseTime(95))
                .p99ResponseTime(getPercentileResponseTime(99))
                .errorRate(totalRequests > 0 ? (double) failedRequests / totalRequests * 100 : 0)
                .activeConnections(getActiveConnections())
                .requestsPerSecond(getRequestsPerSecond())
                .build();
    }

    /**
     * Récupère les métriques depuis le cache
     * 
     * @return Métriques en cache
     */
    private GatewayMetrics getCachedMetrics() {
        AggregatedMetrics cached = metricsCache.get("global");
        if (cached != null && !cached.isExpired()) {
            return cached.getMetrics();
        }

        // Recalculer si expiré
        GatewayMetrics metrics = calculateAggregatedMetrics();
        cacheMetrics(metrics);
        return metrics;
    }

    /**
     * Met en cache les métriques
     * 
     * @param metrics Métriques à mettre en cache
     */
    private void cacheMetrics(GatewayMetrics metrics) {
        AggregatedMetrics cached = new AggregatedMetrics(metrics, Duration.ofSeconds(30));
        metricsCache.put("global", cached);
    }

    // Méthodes utilitaires pour récupérer les métriques spécifiques

    private long getTotalRequests() {
        var search = meterRegistry.find("gateway.requests.total").counter();
        return search != null ? (long) search.count() : 0;
    }

    private long getSuccessfulRequests() {
        var search = meterRegistry.find("gateway.requests.total")
                .tag("status_category", "2xx")
                .counter();
        return search != null ? (long) search.count() : 0;
    }

    private double getAverageResponseTime() {
        Timer timer = meterRegistry.find("gateway.request.duration").timer();
        return timer != null ? timer.mean(TimeUnit.MILLISECONDS) : 0;
    }

    private double getPercentileResponseTime(int percentile) {
        Timer timer = meterRegistry.find("gateway.request.duration").timer();
        return timer != null ? timer.percentile(percentile / 100.0, TimeUnit.MILLISECONDS) : 0;
    }

    private long getActiveConnections() {
        var search = meterRegistry.find("http.server.requests.active")
                .gauge();
        return search != null ? (long) search.value() : 0;
    }

    private long getRequestsPerSecond() {
        // Calculer le taux sur les 60 dernières secondes
        Timer timer = meterRegistry.find("gateway.request.duration").timer();
        return timer != null ? (long) timer.count() / 60 : 0;
    }

    private Map<String, Long> getStatusCountsForRoute(String routeId) {
        Map<String, Long> counts = new HashMap<>();

        meterRegistry.find("gateway.requests.total")
                .tag("route", routeId)
                .counters()
                .forEach(counter -> {
                    String status = counter.getId().getTag("status");
                    if (status != null) {
                        counts.put(status, (long) counter.count());
                    }
                });

        return counts;
    }

    private long getCacheHitsForRoute(String routeId) {
        var search = meterRegistry.find("gateway.cache.operations")
                .tag("route", routeId)
                .tag("cache_type", "HIT")
                .counter();
        return search != null ? (long) search.count() : 0;
    }

    private long getCacheMissesForRoute(String routeId) {
        var search = meterRegistry.find("gateway.cache.operations")
                .tag("route", routeId)
                .tag("cache_type", "MISS")
                .counter();
        return search != null ? (long) search.count() : 0;
    }

    private List<String> findRoutesForService(String service) {
        return meterRegistry.find("gateway.requests.total")
                .counters()
                .stream()
                .map(counter -> counter.getId().getTag("route"))
                .filter(Objects::nonNull)
                .filter(route -> route.contains(service))
                .distinct()
                .collect(Collectors.toList());
    }

    private String getCircuitBreakerStatusForService(String service) {
        var search = meterRegistry.find("gateway.circuitbreaker.state")
                .tag("circuit_breaker", service + "CircuitBreaker")
                .gauge();
        return (search != null && search.value() == 0) ? "CLOSED" : "OPEN";
    }

    private List<RouteMetrics> calculateAllRouteMetrics() {
        return meterRegistry.find("gateway.requests.total")
                .counters()
                .stream()
                .map(counter -> counter.getId().getTag("route"))
                .filter(Objects::nonNull)
                .distinct()
                .map(routeId -> getRouteMetrics(routeId).block())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<CircuitBreakerMetrics> calculateCircuitBreakerMetrics() {
        return circuitBreakerRegistry.getAllCircuitBreakers().stream()
                .map(cb -> {
                    CircuitBreaker.Metrics metrics = cb.getMetrics();
                    return CircuitBreakerMetrics.builder()
                            .name(cb.getName())
                            .state(cb.getState().name())
                            .totalCalls(metrics.getNumberOfBufferedCalls())
                            .failureRate(metrics.getFailureRate())
                            .notPermittedCalls(metrics.getNumberOfNotPermittedCalls())
                            .lastStateChange(Instant.now()) // Note: resilience4j doesn't easily expose last state
                                                            // change time directly on the object without a listener,
                                                            // using now() as placeholder or we could track it
                                                            // separately if critical.
                            .build();
                })
                .collect(Collectors.toList());
    }

    private CacheMetrics calculateCacheMetrics() {
        return CacheMetrics.builder()
                .totalEntries(0)
                .hitCount(0)
                .missCount(0)
                .hitRate(0)
                .evictionCount(0)
                .entriesByType(Map.of())
                .build();
    }

    private SystemMetrics calculateSystemMetrics() {
        Runtime runtime = Runtime.getRuntime();

        return SystemMetrics.builder()
                .cpuUsage(getCpuUsage())
                .usedMemory(runtime.totalMemory() - runtime.freeMemory())
                .totalMemory(runtime.totalMemory())
                .maxMemory(runtime.maxMemory())
                .activeThreads(Thread.activeCount())
                .uptime(ManagementFactory.getRuntimeMXBean().getUptime())
                .build();
    }

    private double getCpuUsage() {
        com.sun.management.OperatingSystemMXBean osBean = ManagementFactory
                .getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);
        return osBean.getProcessCpuLoad() * 100;
    }

    private Duration parsePeriod(String period) {
        return switch (period) {
            case "1h" -> Duration.ofHours(1);
            case "24h" -> Duration.ofDays(1);
            case "7d" -> Duration.ofDays(7);
            case "30d" -> Duration.ofDays(30);
            default -> Duration.ofHours(1);
        };
    }

    // Classes DTO internes

    public static class TimeSeriesMetrics {
        private String period;
        private Instant startTime;
        private Instant endTime;
        private Map<Instant, Map<String, Double>> data;

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public Instant getStartTime() {
            return startTime;
        }

        public void setStartTime(Instant startTime) {
            this.startTime = startTime;
        }

        public Instant getEndTime() {
            return endTime;
        }

        public void setEndTime(Instant endTime) {
            this.endTime = endTime;
        }

        public Map<Instant, Map<String, Double>> getData() {
            return data;
        }

        public void setData(Map<Instant, Map<String, Double>> data) {
            this.data = data;
        }
    }

    public static class ServiceMetrics {
        private String serviceName;
        private long totalRequests;
        private long successfulRequests;
        private long failedRequests;
        private double averageResponseTime;
        private double errorRate;
        private int activeRoutes;
        private String circuitBreakerStatus;

        public ServiceMetrics() {
        }

        public ServiceMetrics(String serviceName, long totalRequests, long successfulRequests, long failedRequests,
                double averageResponseTime, double errorRate, int activeRoutes, String circuitBreakerStatus) {
            this.serviceName = serviceName;
            this.totalRequests = totalRequests;
            this.successfulRequests = successfulRequests;
            this.failedRequests = failedRequests;
            this.averageResponseTime = averageResponseTime;
            this.errorRate = errorRate;
            this.activeRoutes = activeRoutes;
            this.circuitBreakerStatus = circuitBreakerStatus;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String serviceName;
            private long totalRequests;
            private long successfulRequests;
            private long failedRequests;
            private double averageResponseTime;
            private double errorRate;
            private int activeRoutes;
            private String circuitBreakerStatus;

            public Builder serviceName(String serviceName) {
                this.serviceName = serviceName;
                return this;
            }

            public Builder totalRequests(long totalRequests) {
                this.totalRequests = totalRequests;
                return this;
            }

            public Builder successfulRequests(long successfulRequests) {
                this.successfulRequests = successfulRequests;
                return this;
            }

            public Builder failedRequests(long failedRequests) {
                this.failedRequests = failedRequests;
                return this;
            }

            public Builder averageResponseTime(double averageResponseTime) {
                this.averageResponseTime = averageResponseTime;
                return this;
            }

            public Builder errorRate(double errorRate) {
                this.errorRate = errorRate;
                return this;
            }

            public Builder activeRoutes(int activeRoutes) {
                this.activeRoutes = activeRoutes;
                return this;
            }

            public Builder circuitBreakerStatus(String circuitBreakerStatus) {
                this.circuitBreakerStatus = circuitBreakerStatus;
                return this;
            }

            public ServiceMetrics build() {
                return new ServiceMetrics(serviceName, totalRequests, successfulRequests, failedRequests,
                        averageResponseTime, errorRate, activeRoutes, circuitBreakerStatus);
            }
        }

        // Getters
        public String getServiceName() {
            return serviceName;
        }

        public long getTotalRequests() {
            return totalRequests;
        }

        public long getSuccessfulRequests() {
            return successfulRequests;
        }

        public long getFailedRequests() {
            return failedRequests;
        }

        public double getAverageResponseTime() {
            return averageResponseTime;
        }

        public double getErrorRate() {
            return errorRate;
        }

        public int getActiveRoutes() {
            return activeRoutes;
        }

        public String getCircuitBreakerStatus() {
            return circuitBreakerStatus;
        }
    }

    private static class AggregatedMetrics {
        private final GatewayMetrics metrics;
        private final Instant expirationTime;

        public AggregatedMetrics(GatewayMetrics metrics, Duration ttl) {
            this.metrics = metrics;
            this.expirationTime = Instant.now().plus(ttl);
        }

        public GatewayMetrics getMetrics() {
            return metrics;
        }

        public boolean isExpired() {
            return Instant.now().isAfter(expirationTime);
        }
    }
}