package com.yowyob.gateway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CacheManagementServiceTest {

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private ReactiveRedisTemplate<String, Object> redisObjectTemplate;

    private CacheManagementService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CacheManagementService(redisTemplate, redisObjectTemplate);
    }

    @Test
    @DisplayName("Devrait invalider le cache par pattern")
    void shouldInvalidateCacheByPattern() {
        // Given
        when(redisTemplate.keys(anyString())).thenReturn(Flux.just("key1", "key2"));
        when(redisTemplate.delete(anyString())).thenReturn(Mono.just(1L));

        // When
        StepVerifier.create(cacheService.invalidateCache("test:*"))
                .expectNext(2L)
                .verifyComplete();

        // Then
        verify(redisTemplate, times(2)).delete(anyString());
    }

    @Test
    @DisplayName("Devrait invalider le cache pour une route")
    void shouldInvalidateCacheForRoute() {
        // Given
        when(redisTemplate.keys(anyString())).thenReturn(Flux.empty());

        // When
        StepVerifier.create(cacheService.invalidateCacheForRoute("route1"))
                .expectNext(0L)
                .verifyComplete();

        // Then
        verify(redisTemplate).keys(contains("route1"));
    }
}
