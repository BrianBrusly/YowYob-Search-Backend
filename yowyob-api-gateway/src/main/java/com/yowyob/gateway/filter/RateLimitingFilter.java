package com.yowyob.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Filtre de Rate Limiting personnalisé pour l'API Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Implémente le rate limiting avec Redis pour protéger les services
 * contre les abus et les attaques DDoS
 */
@Component
public class RateLimitingFilter extends AbstractGatewayFilterFactory<RateLimitingFilter.Config> {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    /**
     * Constructeur avec injection de RedisTemplate
     */
    public RateLimitingFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }

    /**
     * Configuration du filtre
     */
    public static class Config {
        private int requestsPerMinute = 100;
        private int burstCapacity = 20;
        private String rateLimitKey;

        public int getRequestsPerMinute() { return requestsPerMinute; }
        public void setRequestsPerMinute(int requestsPerMinute) { this.requestsPerMinute = requestsPerMinute; }

        public int getBurstCapacity() { return burstCapacity; }
        public void setBurstCapacity(int burstCapacity) { this.burstCapacity = burstCapacity; }

        public String getRateLimitKey() { return rateLimitKey; }
        public void setRateLimitKey(String rateLimitKey) { this.rateLimitKey = rateLimitKey; }
    }

    /**
     * Application du filtre
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Récupérer la clé de rate limiting
            String rateLimitKey = getRateLimitKey(exchange, config);

            // Vérifier le rate limit
            return checkRateLimit(rateLimitKey, config)
                    .flatMap(allowed -> {
                        if (allowed) {
                            // Rate limit OK, continuer
                            addRateLimitHeaders(exchange.getResponse(), config);
                            return chain.filter(exchange);
                        } else {
                            // Rate limit dépassé
                            return rateLimitExceeded(exchange.getResponse(), config);
                        }
                    });
        };
    }

    /**
     * Obtient la clé de rate limiting
     */
    private String getRateLimitKey(org.springframework.web.server.ServerWebExchange exchange, Config config) {
        var request = exchange.getRequest();
        String clientIp = IpAddressUtil.getClientIp(request);
        String path = request.getPath().value();
        String method = request.getMethod().name();

        // Si une clé spécifique est configurée, l'utiliser
        if (config.getRateLimitKey() != null) {
            return config.getRateLimitKey();
        }

        // Sinon, générer une clé basée sur IP + path + method
        return String.format("ratelimit:%s:%s:%s", clientIp, path, method);
    }

    /**
     * Vérifie le rate limit dans Redis
     */
    private Mono<Boolean> checkRateLimit(String key, Config config) {
        String requestsKey = key + ":requests";
        String timestampKey = key + ":timestamp";

        long now = System.currentTimeMillis();
        long windowSize = 60000; // 1 minute en millisecondes

        return redisTemplate.opsForValue().get(timestampKey)
                .flatMap(timestampStr -> {
                    long timestamp = timestampStr != null ? Long.parseLong(timestampStr) : 0;

                    // Si la fenêtre de temps est expirée, réinitialiser
                    if (now - timestamp > windowSize) {
                        return redisTemplate.opsForValue().set(requestsKey, "1", Duration.ofMinutes(2))
                                .then(redisTemplate.opsForValue().set(timestampKey, String.valueOf(now), Duration.ofMinutes(2)))
                                .thenReturn(true);
                    }

                    // Sinon, incrémenter le compteur
                    return redisTemplate.opsForValue().increment(requestsKey)
                            .flatMap(currentCount -> {
                                if (currentCount > config.getRequestsPerMinute()) {
                                    // Vérifier la capacité de burst
                                    if (currentCount <= config.getRequestsPerMinute() + config.getBurstCapacity()) {
                                        return Mono.just(true); // Autoriser le burst
                                    } else {
                                        return Mono.just(false); // Rate limit dépassé
                                    }
                                } else {
                                    return Mono.just(true); // Dans la limite
                                }
                            });
                })
                .switchIfEmpty(
                        // Première requête dans la fenêtre
                        redisTemplate.opsForValue().set(requestsKey, "1", Duration.ofMinutes(2))
                                .then(redisTemplate.opsForValue().set(timestampKey, String.valueOf(now), Duration.ofMinutes(2)))
                                .thenReturn(true)
                );
    }

    /**
     * Ajoute les headers de rate limiting à la réponse
     */
    private void addRateLimitHeaders(org.springframework.http.server.reactive.ServerHttpResponse response, Config config) {
        response.getHeaders().add("X-RateLimit-Limit", String.valueOf(config.getRequestsPerMinute()));
        response.getHeaders().add("X-RateLimit-Policy",
                String.format("%d;w=60;burst=%d", config.getRequestsPerMinute(), config.getBurstCapacity()));
    }

    /**
     * Gère le cas où le rate limit est dépassé
     */
    private Mono<Void> rateLimitExceeded(org.springframework.http.server.reactive.ServerHttpResponse response, Config config) {
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        response.getHeaders().add("Retry-After", "60");

        String body = """
            {
                "error": "rate_limit_exceeded",
                "message": "Vous avez dépassé la limite de %d requêtes par minute. Veuillez attendre 60 secondes avant de réessayer.",
                "limit": %d,
                "burst_capacity": %d,
                "window_seconds": 60,
                "timestamp": "%s",
                "documentation": "https://docs.yowyob.com/rate-limiting"
            }
            """.formatted(
                config.getRequestsPerMinute(),
                config.getRequestsPerMinute(),
                config.getBurstCapacity(),
                java.time.Instant.now().toString()
        );

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }

    /**
     * Méthode pour logger les événements de rate limiting
     */
    private void logRateLimitEvent(String key, boolean allowed, int currentCount) {
        if (!allowed) {
            System.err.printf("[Rate Limiting] Rate limit exceeded for key: %s, count: %d at %s%n",
                    key, currentCount, java.time.LocalDateTime.now());
        }
    }
}