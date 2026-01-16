package com.yowyob.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * Filtre de fallback pour les Circuit Breakers
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Fournit des réponses de secours quand les services backend sont
 *          indisponibles
 *          Peut retourner des réponses mises en cache ou des réponses statiques
 */
@Component
public class FallbackFilter extends AbstractGatewayFilterFactory<FallbackFilter.Config> {
    private static final Logger log = LoggerFactory.getLogger(FallbackFilter.class);
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public FallbackFilter(ReactiveRedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Exécute le filtre de fallback
     * 
     * @param config Configuration du filtre
     * @return GatewayFilter configuré
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return chain.filter(exchange)
                    .onErrorResume(throwable -> {
                        // Vérifier si c'est une erreur de Circuit Breaker
                        if (throwable instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
                            log.warn("Circuit Breaker ouvert, utilisation du fallback pour: {}",
                                    exchange.getRequest().getPath());

                            return handleFallback(exchange, config);
                        }

                        // Pour les autres erreurs, propager
                        return Mono.error(throwable);
                    });
        };
    }

    /**
     * Gère le fallback
     * 
     * @param exchange ServerWebExchange
     * @param config   Configuration du filtre
     * @return Mono<Void> avec la réponse de fallback
     */
    private Mono<Void> handleFallback(ServerWebExchange exchange, Config config) {
        String fallbackKey = generateFallbackKey(exchange);

        // Essayer de récupérer depuis le cache fallback
        return redisTemplate.opsForValue().get(fallbackKey)
                .flatMap(cachedFallback -> {
                    log.debug("Fallback depuis le cache pour: {}", fallbackKey);
                    return serveCachedFallback(exchange, cachedFallback);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Fallback statique pour: {}", fallbackKey);
                    return serveStaticFallback(exchange, config);
                }));
    }

    /**
     * Génère une clé pour le cache fallback
     * 
     * @param exchange ServerWebExchange
     * @return Clé de cache
     */
    private String generateFallbackKey(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");

        String userPart = userId != null ? userId : "anonymous";
        return String.format("fallback:%s:%s:%s", method, path, userPart);
    }

    /**
     * Sert un fallback depuis le cache
     * 
     * @param exchange       ServerWebExchange
     * @param cachedFallback Fallback mis en cache
     * @return Mono<Void> avec la réponse
     */
    private Mono<Void> serveCachedFallback(ServerWebExchange exchange, String cachedFallback) {
        try {
            Map<String, Object> fallbackData = objectMapper.readValue(cachedFallback, Map.class);

            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.OK);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            // Ajouter le header de fallback
            response.getHeaders().add("X-Fallback", "cached");
            response.getHeaders().add("X-Cache-Status", "FALLBACK_HIT");

            byte[] bytes = cachedFallback.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);

            return response.writeWith(Mono.just(buffer));

        } catch (Exception e) {
            log.error("Erreur lors du traitement du fallback caché", e);
            return serveStaticFallback(exchange, new Config());
        }
    }

    /**
     * Sert un fallback statique
     * 
     * @param exchange ServerWebExchange
     * @param config   Configuration du filtre
     * @return Mono<Void> avec la réponse
     */
    private Mono<Void> serveStaticFallback(ServerWebExchange exchange, Config config) {
        String path = exchange.getRequest().getPath().value();

        // Déterminer le type de fallback selon le chemin
        String fallbackResponse;
        if (path.contains("/search")) {
            fallbackResponse = getSearchFallback();
        } else if (path.contains("/users")) {
            fallbackResponse = getUserFallback();
        } else if (path.contains("/geo")) {
            fallbackResponse = getGeoFallback();
        } else {
            fallbackResponse = getGenericFallback();
        }

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Ajouter le header de fallback
        response.getHeaders().add("X-Fallback", "static");
        response.getHeaders().add("Retry-After", "30"); // Réessayer après 30 secondes

        byte[] bytes = fallbackResponse.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }

    /**
     * Fallback pour le service de recherche
     * 
     * @return JSON de fallback
     */
    private String getSearchFallback() {
        return """
                {
                    "status": "degraded",
                    "message": "Le service de recherche est temporairement indisponible.",
                    "suggestions": [
                        "Veuillez réessayer dans quelques instants",
                        "Essayez une recherche plus simple",
                        "Consultez les résultats en cache si disponibles"
                    ],
                    "timestamp": "%s",
                    "fallback": true
                }
                """.formatted(Instant.now().toString());
    }

    /**
     * Fallback pour le service utilisateur
     * 
     * @return JSON de fallback
     */
    private String getUserFallback() {
        return """
                {
                    "status": "degraded",
                    "message": "Le service utilisateur est temporairement indisponible.",
                    "instructions": "Vos données sont en sécurité. Le service sera rétabli rapidement.",
                    "timestamp": "%s",
                    "fallback": true
                }
                """.formatted(Instant.now().toString());
    }

    /**
     * Fallback pour le service géolocalisation
     * 
     * @return JSON de fallback
     */
    private String getGeoFallback() {
        return """
                {
                    "status": "degraded",
                    "message": "Le service de géolocalisation est temporairement indisponible.",
                    "alternative": "Utilisez l'adresse manuelle ou réessayez plus tard.",
                    "timestamp": "%s",
                    "fallback": true
                }
                """.formatted(Instant.now().toString());
    }

    /**
     * Fallback générique
     * 
     * @return JSON de fallback
     */
    private String getGenericFallback() {
        return """
                {
                    "status": "service_unavailable",
                    "message": "Le service est temporairement indisponible.",
                    "error_code": "SERVICE_UNAVAILABLE",
                    "timestamp": "%s",
                    "retry_after": 30,
                    "fallback": true
                }
                """.formatted(Instant.now().toString());
    }

    /**
     * Configuration du filtre
     */
    public static class Config {
        private boolean cacheFallback = true;
        private Duration cacheTtl = Duration.ofMinutes(5);
        private boolean staticFallbackEnabled = true;

        public boolean isCacheFallback() {
            return cacheFallback;
        }

        public void setCacheFallback(boolean cacheFallback) {
            this.cacheFallback = cacheFallback;
        }

        public Duration getCacheTtl() {
            return cacheTtl;
        }

        public void setCacheTtl(Duration cacheTtl) {
            this.cacheTtl = cacheTtl;
        }

        public boolean isStaticFallbackEnabled() {
            return staticFallbackEnabled;
        }

        public void setStaticFallbackEnabled(boolean staticFallbackEnabled) {
            this.staticFallbackEnabled = staticFallbackEnabled;
        }
    }
}