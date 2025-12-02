package com.yowyob.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

/**
 * Configuration principale des routes de l'API Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Définit toutes les routes vers les microservices backend
 * avec leurs prédicats, filtres et configurations spécifiques.
 */
@Configuration
public class GatewayConfig {

    /**
     * Configuration des routes principales
     *
     * @param builder RouteLocatorBuilder pour construire les routes
     * @return RouteLocator configuré
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ============================================
                // ROUTES DE RECHERCHE (Search Service)
                // ============================================
                .route("search-service", r -> r
                        .path("/api/search/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .circuitBreaker(config -> config
                                        .setName("searchCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/search")
                                        .setRouteId("search-service-fallback"))
                                .addRequestHeader("X-Service", "search-service")
                                .modifyResponseBody(String.class, String.class,
                                        (exchange, body) -> {
                                            // Ajouter des headers de cache pour les résultats de recherche
                                            exchange.getResponse().getHeaders()
                                                    .add("Cache-Control", "public, max-age=60");
                                            return Mono.just(body);
                                        }))
                        .uri("lb://SEARCH-SERVICE"))

                // ============================================
                // ROUTES D'AUTHENTIFICATION (User Service)
                // ============================================
                .route("auth-service-public", r -> r
                        .path("/api/auth/login", "/api/auth/register", "/api/auth/refresh")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Service", "user-service"))
                        .uri("lb://USER-SERVICE"))

                .route("auth-service-secure", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(new JwtAuthenticationFilter())
                                .addRequestHeader("X-Service", "user-service"))
                        .uri("lb://USER-SERVICE"))

                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(new JwtAuthenticationFilter())
                                .addRequestHeader("X-Service", "user-service"))
                        .uri("lb://USER-SERVICE"))

                // ============================================
                // ROUTES DE GÉOLOCALISATION (Geo Service)
                // ============================================
                .route("geo-service", r -> r
                        .path("/api/geo/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .addRequestHeader("X-Service", "geo-service")
                                .addResponseHeader("Access-Control-Allow-Origin", "*"))
                        .uri("lb://GEO-SERVICE"))

                // ============================================
                // ROUTES E-COMMERCE (Shop Service)
                // ============================================
                .route("shop-service-products", r -> r
                        .path("/api/products/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .circuitBreaker(config -> config
                                        .setName("shopCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/shop"))
                                .addRequestHeader("X-Service", "shop-service"))
                        .uri("lb://SHOP-SERVICE"))

                .route("shop-service-merchants", r -> r
                        .path("/api/shop/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(new JwtAuthenticationFilter())
                                .circuitBreaker(config -> config
                                        .setName("shopCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/shop"))
                                .addRequestHeader("X-Service", "shop-service"))
                        .uri("lb://SHOP-SERVICE"))

                // ============================================
                // ROUTES CRAWLER (Crawler Service)
                // ============================================
                .route("crawler-service", r -> r
                        .path("/api/crawler/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(new JwtAuthenticationFilter())
                                .addRequestHeader("X-Service", "crawler-service"))
                        .uri("lb://CRAWLER-SERVICE"))

                // ============================================
                // ROUTES NOTIFICATIONS (Notification Service)
                // ============================================
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(new JwtAuthenticationFilter())
                                .addRequestHeader("X-Service", "notification-service"))
                        .uri("lb://NOTIFICATION-SERVICE"))

                // ============================================
                // ROUTES STATISTIQUES (Stats Service)
                // ============================================
                .route("stats-service-public", r -> r
                        .path("/api/stats/public/**")
                        .filters(f -> f
                                .stripPrefix(2)
                                .addRequestHeader("X-Service", "stats-service"))
                        .uri("lb://STATS-SERVICE"))

                .route("stats-service-admin", r -> r
                        .path("/api/stats/admin/**", "/api/analytics/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(new JwtAuthenticationFilter())
                                .addRequestHeader("X-Service", "stats-service"))
                        .uri("lb://STATS-SERVICE"))

                // ============================================
                // ROUTES ACTUATOR (pour monitoring)
                // ============================================
                .route("actuator-health", r -> r
                        .path("/actuator/health")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "gateway-actuator"))
                        .uri("http://localhost:${server.port}"))

                .route("actuator-secure", r -> r
                        .path("/actuator/**")
                        .filters(f -> f
                                .filter(new JwtAuthenticationFilter())
                                .addRequestHeader("X-Service", "gateway-actuator"))
                        .uri("http://localhost:${server.port}"))

                // ============================================
                // ROUTES DE FALLBACK
                // ============================================
                .route("fallback-search", r -> r
                        .path("/fallback/search")
                        .filters(f -> f
                                .setStatus(503)
                                .modifyResponseBody(String.class, String.class,
                                        (exchange, body) -> Mono.just("""
                            {
                                "error": "search_service_unavailable",
                                "message": "Le service de recherche est temporairement indisponible. Veuillez réessayer dans quelques instants.",
                                "timestamp": "%s",
                                "status": 503
                            }
                            """.formatted(java.time.Instant.now().toString()))))
                        .uri("no://op"))

                .route("fallback-shop", r -> r
                        .path("/fallback/shop")
                        .filters(f -> f
                                .setStatus(503)
                                .modifyResponseBody(String.class, String.class,
                                        (exchange, body) -> Mono.just("""
                            {
                                "error": "shop_service_unavailable",
                                "message": "Le service e-commerce est temporairement indisponible. Veuillez réessayer ultérieurement.",
                                "timestamp": "%s",
                                "status": 503
                            }
                            """.formatted(java.time.Instant.now().toString()))))
                        .uri("no://op"))

                .build();
    }

    /**
     * Configuration des routes spécifiques pour les méthodes HTTP
     */
    @Bean
    public RouteLocator methodSpecificRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Routes GET publiques
                .route("public-get-routes", r -> r
                        .method(HttpMethod.GET)
                        .and()
                        .path("/api/search/**", "/api/geo/**", "/api/stats/public/**")
                        .filters(f -> f
                                .addRequestHeader("X-Request-Type", "public-read")
                                .addResponseHeader("X-Cacheable", "true"))
                        .uri("lb://RESPECTIVE-SERVICE"))

                // Routes POST nécessitant authentication
                .route("secure-post-routes", r -> r
                        .method(HttpMethod.POST)
                        .and()
                        .path("/api/**")
                        .filters(f -> f
                                .filter(new JwtAuthenticationFilter())
                                .addRequestHeader("X-Request-Type", "secure-write"))
                        .uri("lb://RESPECTIVE-SERVICE"))

                // Routes PUT/DELETE nécessitant authentication
                .route("secure-modify-routes", r -> r
                        .method(HttpMethod.PUT, HttpMethod.DELETE)
                        .and()
                        .path("/api/**")
                        .filters(f -> f
                                .filter(new JwtAuthenticationFilter())
                                .addRequestHeader("X-Request-Type", "secure-modify"))
                        .uri("lb://RESPECTIVE-SERVICE"))

                .build();
    }
}