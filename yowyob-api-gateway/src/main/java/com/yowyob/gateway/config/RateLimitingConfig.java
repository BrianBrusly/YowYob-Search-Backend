package com.yowyob.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * Configuration du Rate Limiting pour l'API Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Configure les limites de débit pour protéger les services backend
 * contre les abus et les surcharges
 */
@Configuration
public class RateLimitingConfig {

    @Value("${rate.limit.requests.per.minute:100}")
    private int requestsPerMinute;

    @Value("${rate.limit.burst.capacity:20}")
    private int burstCapacity;

    /**
     * Resolver de clé pour le rate limiting basé sur l'adresse IP
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ipAddress = IpAddressUtil.getClientIp(exchange.getRequest());
            return Mono.just(ipAddress);
        };
    }

    /**
     * Resolver de clé pour le rate limiting basé sur l'utilisateur
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .flatMap(principal -> {
                    String username = principal.getName();
                    return Mono.just(username);
                })
                .switchIfEmpty(Mono.just("anonymous"));
    }

    /**
     * Rate Limiter global pour les requêtes non authentifiées
     */
    @Bean
    public RedisRateLimiter globalRateLimiter() {
        return new RedisRateLimiter(
                requestsPerMinute,        // 100 requêtes par minute
                burstCapacity,            // Capacité de burst: 20
                1                         // 1 token par requête
        ) {
            @Override
            public Mono<Response> isAllowed(String routeId, String id) {
                // Logique personnalisée pour le rate limiting
                return super.isAllowed(routeId, id)
                        .doOnNext(response -> {
                            if (!response.isAllowed()) {
                                logRateLimitExceeded(id, routeId);
                            }
                        });
            }
        };
    }

    /**
     * Rate Limiter strict pour les endpoints de login
     */
    @Bean(name = "loginRateLimiter")
    public RedisRateLimiter loginRateLimiter() {
        return new RedisRateLimiter(
                5,      // 5 tentatives de login par minute
                3,      // Burst de 3
                1
        );
    }

    /**
     * Rate Limiter pour les recherches
     */
    @Bean(name = "searchRateLimiter")
    public RedisRateLimiter searchRateLimiter() {
        return new RedisRateLimiter(
                60,     // 60 recherches par minute
                20,     // Burst de 20
                1
        );
    }

    /**
     * Rate Limiter pour les API d'administration
     */
    @Bean(name = "adminRateLimiter")
    public RedisRateLimiter adminRateLimiter() {
        return new RedisRateLimiter(
                1000,   // 1000 requêtes par minute pour les admins
                100,    // Burst de 100
                1
        );
    }

    /**
     * Configuration du script Redis Lua pour le rate limiting personnalisé
     */
    @Bean
    public RedisScript<List<Long>> rateLimitScript() {
        String script = """
            local key = KEYS[1]
            local burst = tonumber(ARGV[1])
            local rate = tonumber(ARGV[2])
            local period = tonumber(ARGV[3])
            local cost = tonumber(ARGV[4])
            
            local current_time = redis.call('TIME')
            local now = tonumber(current_time[1]) + tonumber(current_time[2]) / 1000000
            
            -- Initialiser ou récupérer le bucket
            local bucket = redis.call('HMGET', key, 'tokens', 'last_refill')
            local tokens = tonumber(bucket[1])
            local last_refill = tonumber(bucket[2])
            
            if tokens == nil then
                tokens = burst
                last_refill = now
            end
            
            -- Calculer les tokens à ajouter
            local time_passed = now - last_refill
            local refill_tokens = math.floor(time_passed * rate / period)
            
            if refill_tokens > 0 then
                tokens = math.min(burst, tokens + refill_tokens)
                last_refill = now
            end
            
            -- Vérifier si assez de tokens
            local allowed = tokens >= cost
            local remaining = tokens
            
            if allowed then
                tokens = tokens - cost
                remaining = tokens
            end
            
            -- Sauvegarder l'état
            redis.call('HMSET', key, 
                'tokens', tokens,
                'last_refill', last_refill)
            redis.call('EXPIRE', key, math.ceil(period * 2))
            
            -- Retourner les résultats
            return {allowed and 1 or 0, remaining, math.ceil((burst - tokens) / rate * period)}
            """;

        return RedisScript.of(script, List.class);
    }

    /**
     * Configuration du template Redis réactif
     */
    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            org.springframework.data.redis.connection.ReactiveRedisConnectionFactory factory) {
        org.springframework.data.redis.serializer.RedisSerializationContext<String, String> context =
                org.springframework.data.redis.serializer.RedisSerializationContext
                        .<String, String>newSerializationContext(
                                org.springframework.data.redis.serializer.StringRedisSerializer.UTF_8)
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    /**
     * Configuration des headers de rate limiting
     */
    @Bean
    public org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory rateLimiterFilterFactory(
            org.springframework.cloud.gateway.filter.ratelimit.RateLimiter rateLimiter,
            KeyResolver keyResolver) {
        return new org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory(
                rateLimiter, keyResolver) {

            @Override
            public GatewayFilter apply(Config config) {
                return (exchange, chain) -> {
                    String routeId = config.getRouteId();
                    String key = keyResolver.resolve(exchange).block();

                    return rateLimiter.isAllowed(routeId, key).flatMap(response -> {
                        // Ajouter les headers de rate limiting
                        exchange.getResponse().getHeaders().add("X-RateLimit-Limit",
                                String.valueOf(response.getRequestedTokens()));
                        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining",
                                String.valueOf(response.getTokensLeft()));
                        exchange.getResponse().getHeaders().add("X-RateLimit-Reset",
                                String.valueOf(System.currentTimeMillis() +
                                        Duration.ofSeconds(response.getSeconds()).toMillis()));

                        if (response.isAllowed()) {
                            return chain.filter(exchange);
                        } else {
                            // Rate limit exceeded
                            exchange.getResponse().setStatusCode(
                                    org.springframework.http.HttpStatus.TOO_MANY_REQUESTS);
                            exchange.getResponse().getHeaders().setContentType(
                                    org.springframework.http.MediaType.APPLICATION_JSON);

                            String body = """
                                {
                                    "error": "rate_limit_exceeded",
                                    "message": "Vous avez dépassé la limite de %d requêtes par minute. Veuillez attendre %d secondes avant de réessayer.",
                                    "limit": %d,
                                    "remaining": 0,
                                    "reset_in": %d,
                                    "timestamp": "%s"
                                }
                                """.formatted(
                                    requestsPerMinute,
                                    response.getSeconds(),
                                    requestsPerMinute,
                                    response.getSeconds(),
                                    java.time.Instant.now().toString()
                            );

                            return exchange.getResponse().writeWith(
                                    Mono.just(exchange.getResponse()
                                            .bufferFactory()
                                            .wrap(body.getBytes()))
                            );
                        }
                    });
                };
            }
        };
    }

    private void logRateLimitExceeded(String clientId, String routeId) {
        System.err.printf("[Rate Limiting] Client %s exceeded rate limit for route %s at %s%n",
                clientId, routeId, java.time.LocalDateTime.now());
    }
}