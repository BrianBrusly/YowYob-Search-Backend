package com.yowyob.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yowyob.gateway.dto.GatewayMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsAggregationServiceTest {

    private MeterRegistry meterRegistry;

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private ObjectMapper objectMapper;
    private MetricsAggregationService metricsService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        objectMapper = new ObjectMapper();
        metricsService = new MetricsAggregationService(meterRegistry, redisTemplate, circuitBreakerRegistry,
                objectMapper);
    }

    @Test
    @DisplayName("Devrait récupérer les métriques du Gateway")
    void shouldGetGatewayMetrics() {
        // When
        StepVerifier.create(metricsService.getGatewayMetrics())
                .assertNext(metrics -> {
                    assertThat(metrics).isNotNull();
                    assertThat(metrics.getOverview()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Devrait mettre en cache les métriques")
    void shouldCacheMetrics() {
        // First call
        metricsService.getGatewayMetrics().block();

        // Second call should use cache
        StepVerifier.create(metricsService.getGatewayMetrics())
                .expectNextCount(1)
                .verifyComplete();
    }
}
