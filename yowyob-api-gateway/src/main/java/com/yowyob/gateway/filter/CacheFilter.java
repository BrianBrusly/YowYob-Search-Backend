package com.yowyob.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Builder;
import com.yowyob.gateway.util.CacheKeyGenerator;
import java.time.Duration;
import java.time.Instant;

/**
 * Filtre de cache pour les réponses HTTP
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Met en cache les réponses des services backend pour améliorer les
 *          performances
 *          Utilise Redis comme store de cache distribué
 */
@Component
public class CacheFilter implements GatewayFilter, Ordered {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CacheFilter.class);

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheKeyGenerator cacheKeyGenerator;

    @Value("${app.cache.default-ttl:300}") // 5 minutes par défaut
    private long defaultTtl;

    @Value("${app.cache.enabled:true}")
    private boolean cacheEnabled;

    public CacheFilter(ReactiveRedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper,
            CacheKeyGenerator cacheKeyGenerator) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    /**
     * Exécute le filtre de cache
     *
     * @param exchange ServerWebExchange contenant la requête et la réponse
     * @param chain    GatewayFilterChain pour continuer le traitement
     * @return Mono<Void> pour le traitement asynchrone
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Vérifier si le cache est activé et si la méthode est cacheable
        if (!cacheEnabled || !isCacheableRequest(request)) {
            return chain.filter(exchange);
        }

        // Générer la clé de cache
        String cacheKey = cacheKeyGenerator.generateFromRequest(request);

        log.debug("Tentative de récupération depuis le cache avec la clé: {}", cacheKey);

        // Essayer de récupérer depuis le cache
        return redisTemplate.opsForValue().get(cacheKey)
                .flatMap(cachedResponse -> {
                    log.debug("Cache HIT pour la clé: {}", cacheKey);
                    return serveFromCache(exchange, chain, cachedResponse);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Cache MISS pour la clé: {}", cacheKey);
                    return fetchAndCache(exchange, chain, cacheKey);
                }));
    }

    /**
     * Vérifie si une requête est cacheable
     *
     * @param request Requête HTTP
     * @return true si la requête est cacheable
     */
    private boolean isCacheableRequest(ServerHttpRequest request) {
        // Seulement les requêtes GET sont cacheables
        if (!HttpMethod.GET.equals(request.getMethod())) {
            return false;
        }

        String path = request.getPath().value();

        // Exclure certains endpoints du cache
        List<String> nonCacheablePaths = Arrays.asList(
                "/api/auth/",
                "/api/users/profile",
                "/api/notifications",
                "/actuator/");

        return nonCacheablePaths.stream()
                .noneMatch(path::startsWith);
    }

    /**
     * Sert la réponse depuis le cache
     *
     * @param exchange       ServerWebExchange
     * @param cachedResponse Réponse mise en cache
     * @return Mono<Void> avec la réponse depuis le cache
     */
    private Mono<Void> serveFromCache(ServerWebExchange exchange, GatewayFilterChain chain, String cachedResponse) {
        try {
            CachedResponse cached = objectMapper.readValue(cachedResponse, CachedResponse.class);

            ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(
                    exchange.getResponse()) {

                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    // Remplacer le body par la réponse cachée
                    byte[] bytes = cached.getBody().getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = exchange.getResponse()
                            .bufferFactory()
                            .wrap(bytes);

                    // Ajouter les headers de la réponse cachée
                    cached.getHeaders().forEach((name, values) -> {
                        if (values != null) {
                            values.forEach(value -> exchange.getResponse().getHeaders().add(name, value));
                        }
                    });

                    // Ajouter le header de cache
                    exchange.getResponse().getHeaders()
                            .add("X-Cache-Status", "HIT");
                    exchange.getResponse().getHeaders()
                            .add("X-Cache-Age",
                                    String.valueOf(Duration.between(
                                            cached.getTimestamp(), Instant.now()).getSeconds()));

                    return super.writeWith(Mono.just(buffer));
                }
            };

            // Définir le statut HTTP
            exchange.getResponse().setStatusCode(
                    HttpStatus.valueOf(cached.getStatus()));

            return responseDecorator.setComplete();

        } catch (Exception e) {
            log.error("Erreur lors de la désérialisation de la réponse cachée", e);
            // En cas d'erreur, ignorer le cache et faire la requête normale
            return chain.filter(exchange);
        }
    }

    /**
     * Récupère la réponse depuis le backend et la met en cache
     *
     * @param exchange ServerWebExchange
     * @param chain    GatewayFilterChain
     * @param cacheKey Clé de cache
     * @return Mono<Void> avec la réponse mise en cache
     */
    private Mono<Void> fetchAndCache(ServerWebExchange exchange,
            GatewayFilterChain chain,
            String cacheKey) {

        ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(exchange.getResponse()) {

            private final List<DataBuffer> bodyBuffers = new ArrayList<>();

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                return super.writeWith(Flux.from(body)
                        .doOnNext(bodyBuffers::add)
                        .doOnComplete(() -> cacheResponse(cacheKey)));
            }

            private void cacheResponse(String cacheKey) {
                if (exchange.getResponse().getStatusCode() != null &&
                        exchange.getResponse().getStatusCode().is2xxSuccessful()) {

                    // Construire la réponse à cacher
                    String responseBody = bodyBuffers.stream()
                            .map(buffer -> {
                                byte[] bytes = new byte[buffer.readableByteCount()];
                                buffer.read(bytes);
                                DataBufferUtils.release(buffer);
                                return new String(bytes, StandardCharsets.UTF_8);
                            })
                            .collect(Collectors.joining());

                    CachedResponse cached = CachedResponse.builder()
                            .status(exchange.getResponse().getStatusCode().value())
                            .headers(new HashMap<>(exchange.getResponse().getHeaders()))
                            .body(responseBody)
                            .timestamp(Instant.now())
                            .build();

                    try {
                        String serialized = objectMapper.writeValueAsString(cached);

                        // Déterminer le TTL selon le type de contenu
                        long ttl = determineTtl(exchange.getRequest());

                        // Mettre en cache dans Redis
                        redisTemplate.opsForValue()
                                .set(cacheKey, serialized, Duration.ofSeconds(ttl))
                                .doOnSuccess(success -> log.debug("Réponse mise en cache avec la clé: {} (TTL: {}s)",
                                        cacheKey, ttl))
                                .doOnError(error -> log.error("Erreur lors de la mise en cache", error))
                                .subscribe();

                    } catch (Exception e) {
                        log.error("Erreur lors de la sérialisation pour le cache", e);
                    }
                }

                exchange.getResponse().getHeaders()
                        .add("X-Cache-Status", "MISS");
            }
        };

        return chain.filter(exchange.mutate().response(responseDecorator).build());
    }

    /**
     * Détermine le TTL selon le type de contenu
     *
     * @param request Requête HTTP
     * @return TTL en secondes
     */
    private long determineTtl(ServerHttpRequest request) {
        String path = request.getPath().value();

        // TTL différents selon les types de contenu
        if (path.startsWith("/api/search")) {
            return 60; // 1 minute pour les recherches
        } else if (path.startsWith("/api/geo")) {
            return 300; // 5 minutes pour la géolocalisation
        } else if (path.startsWith("/api/shop")) {
            return 1800; // 30 minutes pour les produits
        } else {
            return defaultTtl;
        }
    }

    /**
     * Définit l'ordre d'exécution du filtre
     *
     * @return Ordre moyen
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }

    /**
     * Classe interne pour représenter une réponse mise en cache
     */
    public static class CachedResponse {
        private int status;
        private Map<String, List<String>> headers;
        private String body;
        private Instant timestamp;

        public CachedResponse() {
        }

        public CachedResponse(int status, Map<String, List<String>> headers, String body, Instant timestamp) {
            this.status = status;
            this.headers = headers;
            this.body = body;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Map<String, List<String>> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, List<String>> headers) {
            this.headers = headers;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
        }

        public static CachedResponseBuilder builder() {
            return new CachedResponseBuilder();
        }

        public static class CachedResponseBuilder {
            private int status;
            private Map<String, List<String>> headers;
            private String body;
            private Instant timestamp;

            public CachedResponseBuilder status(int status) {
                this.status = status;
                return this;
            }

            public CachedResponseBuilder headers(Map<String, List<String>> headers) {
                this.headers = headers;
                return this;
            }

            public CachedResponseBuilder body(String body) {
                this.body = body;
                return this;
            }

            public CachedResponseBuilder timestamp(Instant timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public CachedResponse build() {
                return new CachedResponse(status, headers, body, timestamp);
            }
        }
    }
}