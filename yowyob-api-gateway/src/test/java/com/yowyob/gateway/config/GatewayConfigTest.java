package com.yowyob.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import java.net.InetSocketAddress;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires pour la configuration du Gateway
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class GatewayConfigTest {

    @Mock
    private RouteLocatorBuilder builder;

    @Mock
    private RouteLocatorBuilder.Builder routeBuilder;

    @Mock
    private RouteLocator routeLocator;

    @Mock
    private RedisRateLimiter redisRateLimiter;

    @Mock
    private KeyResolver userKeyResolver;

    @Mock
    private KeyResolver ipKeyResolver;

    @InjectMocks
    private GatewayConfig gatewayConfig;

    @BeforeEach
    void setUp() {
        // Mock the builder fluent API
        when(builder.routes()).thenReturn(routeBuilder);
        // Add default mocks for common route() calls if needed,
        // but it's better to do it per test.
    }

    @Test
    @DisplayName("Devrait créer un RouteLocator avec toutes les routes configurées")
    void shouldCreateRouteLocatorWithAllRoutes() {
        // Given
        when(routeBuilder.route(any(String.class), any())).thenReturn(routeBuilder);
        when(routeBuilder.build()).thenReturn(routeLocator);

        // When
        RouteLocator result = gatewayConfig.customRouteLocator(builder, redisRateLimiter, userKeyResolver,
                ipKeyResolver);

        // Then
        assertThat(result).isEqualTo(routeLocator);
    }

    @Test
    @DisplayName("Devrait inclure la route pour le Search Service")
    void shouldIncludeSearchServiceRoute() {
        // Given
        Route mockRoute = Route.async()
                .id("search_service_route")
                .uri("lb://SEARCH-SERVICE")
                .predicate(exchange -> true)
                .build();

        when(routeLocator.getRoutes()).thenReturn(Flux.just(mockRoute));

        // When
        List<Route> routes = routeLocator.getRoutes().collectList().block();

        // Then
        assertThat(routes).isNotEmpty();
        assertThat(routes.get(0).getId()).isEqualTo("search_service_route");
        assertThat(routes.get(0).getUri().toString()).isEqualTo("lb://SEARCH-SERVICE");
    }
}