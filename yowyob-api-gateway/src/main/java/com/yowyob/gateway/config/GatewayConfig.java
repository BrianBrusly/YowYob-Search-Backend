package com.yowyob.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import com.yowyob.gateway.predicate.AuthenticatedUserPredicate;
import com.yowyob.gateway.predicate.RolePredicate;

/**
 * Configuration principale des routes du Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Définit toutes les routes programmatiquement avec Spring Cloud
 *          Gateway
 *          Chaque route configure le routing vers les différents services
 *          backend
 */
@Configuration
public class GatewayConfig {

        @Value("${app.gateway.timeout.connect:5000}")
        private int connectTimeout;

        @Value("${app.gateway.timeout.response:10000}")
        private int responseTimeout;

        @Value("${app.gateway.prefix:/api}")
        private String apiPrefix;

        /**
         * Configuration programmatique des routes du Gateway
         *
         * @param builder          RouteLocatorBuilder pour construire les routes
         * @param redisRateLimiter RedisRateLimiter injecté
         * @param userKeyResolver  KeyResolver injecté
         * @param ipKeyResolver    KeyResolver injecté
         * @return RouteLocator configuré avec toutes les routes
         */
        @Bean
        public RouteLocator customRouteLocator(
                        RouteLocatorBuilder builder,
                        RedisRateLimiter redisRateLimiter,
                        KeyResolver userKeyResolver,
                        KeyResolver ipKeyResolver) {
                return builder.routes()
                                // ============================================================
                                // ROUTE POUR LE SEARCH SERVICE
                                // ============================================================
                                .route("search_service_route", r -> r
                                                .path(apiPrefix + "/search/**")
                                                .and()
                                                .method(HttpMethod.GET, HttpMethod.POST)
                                                .filters(f -> f
                                                                .stripPrefix(1) // Enlève le préfixe /api du chemin
                                                                .circuitBreaker(config -> config
                                                                                .setName("searchCircuitBreaker")
                                                                                .setFallbackUri("forward:/fallback/search"))
                                                                .retry(config -> config
                                                                                .setRetries(3)
                                                                                .setStatuses(HttpStatus.BAD_GATEWAY,
                                                                                                HttpStatus.SERVICE_UNAVAILABLE)
                                                                                .setMethods(HttpMethod.GET)
                                                                                .setBackoff(
                                                                                                Duration.ofMillis(1000),
                                                                                                Duration.ofMillis(5000),
                                                                                                2,
                                                                                                true))
                                                                .requestRateLimiter(config -> config
                                                                                .setRateLimiter(redisRateLimiter)
                                                                                .setKeyResolver(userKeyResolver))
                                                                .addResponseHeader("X-Served-By", "search-service"))
                                                .uri("lb://SEARCH-SERVICE"))

                                // ============================================================
                                // ROUTE POUR LE USER SERVICE
                                // ============================================================
                                .route("user_service_route", r -> r
                                                .path(apiPrefix + "/users/**", apiPrefix + "/auth/**")
                                                .filters(f -> f
                                                                .stripPrefix(1)
                                                                .circuitBreaker(config -> config
                                                                                .setName("userCircuitBreaker")
                                                                                .setFallbackUri("forward:/fallback/user"))
                                                                .addRequestHeader("X-Request-Type", "user-service")
                                                                .addResponseHeader("X-Response-Time",
                                                                                Instant.now().toString()))
                                                .uri("lb://USER-SERVICE"))

                                // ============================================================
                                // ROUTE POUR LE GEO SERVICE
                                // ============================================================
                                .route("geo_service_route", r -> r
                                                .path(apiPrefix + "/geo/**")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f
                                                                .stripPrefix(1)
                                                                .circuitBreaker(config -> config
                                                                                .setName("geoCircuitBreaker")
                                                                                .setFallbackUri("forward:/fallback/geo"))
                                                                .requestRateLimiter(config -> config
                                                                                .setRateLimiter(redisRateLimiter)
                                                                                .setKeyResolver(ipKeyResolver)))
                                                .uri("lb://GEO-SERVICE"))

                                // ============================================================
                                // ROUTE POUR LE CRAWLER SERVICE (Admin seulement)
                                // ============================================================
                                .route("crawler_service_route", r -> {
                                        RolePredicate.Config config = new RolePredicate.Config();
                                        config.setRoles("ADMIN");
                                        return r.path(apiPrefix + "/crawler/**")
                                                        .and()
                                                        .predicate(new RolePredicate().apply(config))
                                                        .filters(f -> f
                                                                        .stripPrefix(1)
                                                                        .circuitBreaker(cb -> cb
                                                                                        .setName("crawlerCircuitBreaker")
                                                                                        .setFallbackUri("forward:/fallback/crawler")))
                                                        .uri("lb://CRAWLER-SERVICE");
                                })

                                // ============================================================
                                // ROUTE POUR LE NOTIFICATION SERVICE
                                // ============================================================
                                .route("notification_service_route", r -> r
                                                .path(apiPrefix + "/notifications/**")
                                                .and()
                                                .predicate(new AuthenticatedUserPredicate()
                                                                .apply(new AuthenticatedUserPredicate.Config()))
                                                .filters(f -> f
                                                                .stripPrefix(1)
                                                                .circuitBreaker(config -> config
                                                                                .setName("notificationCircuitBreaker")
                                                                                .setFallbackUri("forward:/fallback/notification")))
                                                .uri("lb://NOTIFICATION-SERVICE"))

                                // ============================================================
                                // ROUTE POUR LE SHOP SERVICE
                                // ============================================================
                                .route("shop_service_route", r -> r
                                                .path(apiPrefix + "/shop/**")
                                                .filters(f -> f
                                                                .stripPrefix(1)
                                                                .circuitBreaker(config -> config
                                                                                .setName("shopCircuitBreaker")
                                                                                .setFallbackUri("forward:/fallback/shop")))
                                                .uri("lb://SHOP-SERVICE"))

                                // ============================================================
                                // ROUTE POUR LE STATS SERVICE
                                // ============================================================
                                .route("stats_service_route", r -> r
                                                .path(apiPrefix + "/stats/**")
                                                .and()
                                                .predicate(new AuthenticatedUserPredicate()
                                                                .apply(new AuthenticatedUserPredicate.Config()))
                                                .filters(f -> f
                                                                .stripPrefix(1)
                                                                .circuitBreaker(config -> config
                                                                                .setName("statsCircuitBreaker")
                                                                                .setFallbackUri("forward:/fallback/stats")))
                                                .uri("lb://STATS-SERVICE"))

                                .build();
        }
}