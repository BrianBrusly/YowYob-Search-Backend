package com.yowyob.gateway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteRefreshServiceTest {

    @Mock
    private RouteDefinitionWriter routeDefinitionWriter;

    @Mock
    private RouteDefinitionLocator routeDefinitionLocator;

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private ReactiveHashOperations<String, Object, Object> hashOperations;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private RouteRefreshService routeRefreshService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        routeRefreshService = new RouteRefreshService(routeDefinitionWriter, routeDefinitionLocator, redisTemplate);
        routeRefreshService.setApplicationEventPublisher(eventPublisher);
    }

    @Test
    @DisplayName("Devrait rafraîchir les routes depuis Redis")
    void shouldRefreshRoutesFromRedis() {
        // Given
        when(hashOperations.values(anyString())).thenReturn(Flux.empty());

        // When
        StepVerifier.create(routeRefreshService.refreshRoutes())
                .verifyComplete();

        // Then
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    @DisplayName("Devrait ajouter une route")
    void shouldAddRoute() {
        // Given
        RouteDefinition route = new RouteDefinition();
        route.setId("test_route");
        when(hashOperations.put(anyString(), anyString(), anyString())).thenReturn(Mono.just(true));
        when(hashOperations.values(anyString())).thenReturn(Flux.empty());

        // When
        StepVerifier.create(routeRefreshService.addOrUpdateRoute(route))
                .verifyComplete();

        // Then
        verify(hashOperations).put(eq("gateway:dynamic:routes"), eq("test_route"), anyString());
    }
}
