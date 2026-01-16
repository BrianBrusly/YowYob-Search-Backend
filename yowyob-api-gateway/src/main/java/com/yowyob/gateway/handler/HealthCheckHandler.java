package com.yowyob.gateway.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.lang.management.ManagementFactory;

/**
 * Gestionnaire de health checks personnalisés
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Fournit des health checks détaillés pour le Gateway
 *          Vérifie la connectivité avec les dépendances (Redis, Eureka, etc.)
 */
@Component
public class HealthCheckHandler {

    private static final Logger log = LoggerFactory.getLogger(HealthCheckHandler.class);

    private final ReactiveRedisConnectionFactory redisConnectionFactory;

    public HealthCheckHandler(ReactiveRedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    /**
     * Gère une requête de health check
     * 
     * @param request ServerRequest
     * @return ServerResponse avec le statut de santé
     */
    public Mono<ServerResponse> handleHealthCheck(ServerRequest request) {
        Map<String, Object> healthDetails = new HashMap<>();
        Instant startTime = Instant.now();

        // Vérifier la santé de Redis
        Mono<Map<String, Object>> redisHealth = checkRedisHealth();

        // Vérifier la santé du Gateway lui-même
        Map<String, Object> gatewayHealth = checkGatewayHealth();

        return redisHealth.flatMap(redisStatus -> {
            healthDetails.put("gateway", gatewayHealth);
            healthDetails.put("redis", redisStatus);
            healthDetails.put("timestamp", Instant.now().toString());
            healthDetails.put("responseTime", Duration.between(startTime, Instant.now()).toMillis() + "ms");

            // Déterminer le statut global
            boolean allHealthy = "UP".equals(gatewayHealth.get("status")) &&
                    "UP".equals(redisStatus.get("status"));

            Map<String, Object> response = Map.of(
                    "status", allHealthy ? "UP" : "DOWN",
                    "details", healthDetails);

            return ServerResponse.ok()
                    .bodyValue(response);
        }).onErrorResume(error -> {
            log.error("Erreur lors du health check", error);

            Map<String, Object> errorResponse = Map.of(
                    "status", "DOWN",
                    "error", error.getMessage(),
                    "timestamp", Instant.now().toString());

            return ServerResponse.status(503) // Service Unavailable
                    .bodyValue(errorResponse);
        });
    }

    /**
     * Vérifie la santé de Redis
     * 
     * @return Mono<Map> avec le statut Redis
     */
    private Mono<Map<String, Object>> checkRedisHealth() {
        return Mono.from(redisConnectionFactory.getReactiveConnection().ping())
                .timeout(Duration.ofSeconds(3))
                .map(pong -> Map.<String, Object>of(
                        "status", "UP",
                        "message", "Redis is responding",
                        "response", pong))
                .onErrorResume(error -> Mono.just(Map.<String, Object>of(
                        "status", "DOWN",
                        "error", error.getMessage(),
                        "message", "Redis is not responding")));
    }

    /**
     * Vérifie la santé du Gateway
     * 
     * @return Map avec le statut du Gateway
     */
    private Map<String, Object> checkGatewayHealth() {
        try {
            Runtime runtime = Runtime.getRuntime();

            return Map.of(
                    "status", "UP",
                    "version", "1.0.0",
                    "memory", Map.of(
                            "free", runtime.freeMemory(),
                            "total", runtime.totalMemory(),
                            "max", runtime.maxMemory(),
                            "used", runtime.totalMemory() - runtime.freeMemory()),
                    "threads", Thread.activeCount(),
                    "uptime", ManagementFactory.getRuntimeMXBean().getUptime() + "ms");
        } catch (Exception e) {
            return Map.of(
                    "status", "DOWN",
                    "error", e.getMessage());
        }
    }

    /**
     * Gère une requête de liveness probe
     * 
     * @param request ServerRequest
     * @return ServerResponse
     */
    public Mono<ServerResponse> handleLiveness(ServerRequest request) {
        Map<String, Object> response = Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString());

        return ServerResponse.ok()
                .bodyValue(response);
    }

    /**
     * Gère une requête de readiness probe
     * 
     * @param request ServerRequest
     * @return ServerResponse
     */
    public Mono<ServerResponse> handleReadiness(ServerRequest request) {
        return checkRedisHealth()
                .flatMap(redisStatus -> {
                    boolean isReady = "UP".equals(redisStatus.get("status"));

                    Map<String, Object> response = Map.of(
                            "status", isReady ? "UP" : "DOWN",
                            "timestamp", Instant.now().toString(),
                            "dependencies", Map.of(
                                    "redis", redisStatus));

                    return ServerResponse.ok()
                            .bodyValue(response);
                });
    }
}