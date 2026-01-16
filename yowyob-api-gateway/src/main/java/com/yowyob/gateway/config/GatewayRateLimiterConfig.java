package com.yowyob.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * Configuration du Rate Limiting avec Redis
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Configure la limitation de taux pour protéger les services contre
 *          les abus
 *          Utilise l'algorithme Token Bucket avec stockage Redis distribué
 */
@Configuration
public class GatewayRateLimiterConfig {

    /**
     * Configuration globale du Rate Limiter
     *
     * @return Map des configurations par endpoint
     */
    @Bean
    public Map<String, io.github.resilience4j.ratelimiter.RateLimiterConfig> rateLimitConfigs() {
        return Map.of(
                // Configuration par défaut
                "default", io.github.resilience4j.ratelimiter.RateLimiterConfig.custom()
                        .limitForPeriod(100) // 100 requêtes par période
                        .limitRefreshPeriod(Duration.ofMinutes(1)) // Période de 1 minute
                        .timeoutDuration(Duration.ZERO) // Pas de timeout, rejet immédiat
                        .build(),

                // Recherche
                "search", io.github.resilience4j.ratelimiter.RateLimiterConfig.custom()
                        .limitForPeriod(60) // 60 requêtes par minute
                        .limitRefreshPeriod(Duration.ofMinutes(1))
                        .build(),

                // Authentification
                "auth", io.github.resilience4j.ratelimiter.RateLimiterConfig.custom()
                        .limitForPeriod(5) // 5 tentatives par minute
                        .limitRefreshPeriod(Duration.ofMinutes(1))
                        .build(),

                // Admin
                "admin", io.github.resilience4j.ratelimiter.RateLimiterConfig.custom()
                        .limitForPeriod(1000) // 1000 requêtes par minute
                        .limitRefreshPeriod(Duration.ofMinutes(1))
                        .build());
    }

    /**
     * Rate Limiter Redis pour Spring Cloud Gateway
     *
     * @return RedisRateLimiter configuré
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(
                10, // replenishRate: 10 tokens par seconde
                20, // burstCapacity: maximum 20 tokens
                1 // requestedTokens: 1 token par requête
        );
    }

    /**
     * Resolver pour les clés basé sur l'utilisateur
     *
     * @return KeyResolver basé sur l'ID utilisateur
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-User-Id");

            if (userId != null && !userId.isEmpty()) {
                return Mono.just("user:" + userId);
            }

            // Fallback sur l'adresse IP pour les utilisateurs non authentifiés
            String clientIp = exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();

            return Mono.just("ip:" + clientIp);
        };
    }

    /**
     * Resolver pour les clés basé sur l'adresse IP
     *
     * @return KeyResolver basé sur l'adresse IP
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress());
    }

    /**
     * Resolver pour les clés basé sur l'API Key
     *
     * @return KeyResolver basé sur l'API Key
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> {
            String apiKey = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-API-Key");

            if (apiKey != null && !apiKey.isEmpty()) {
                return Mono.just("api:" + apiKey);
            }

            return userKeyResolver().resolve(exchange);
        };
    }
}