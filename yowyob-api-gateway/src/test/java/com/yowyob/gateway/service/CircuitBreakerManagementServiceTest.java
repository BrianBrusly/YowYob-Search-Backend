package com.yowyob.gateway.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CircuitBreakerManagementServiceTest {

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Mock
    private CircuitBreaker circuitBreaker;

    private CircuitBreakerManagementService cbService;

    @BeforeEach
    void setUp() {
        cbService = new CircuitBreakerManagementService(circuitBreakerRegistry);
    }

    @Test
    @DisplayName("Devrait récupérer le statut d'un Circuit Breaker")
    void shouldGetCircuitBreakerStatus() {
        // Given
        String name = "testCB";
        when(circuitBreakerRegistry.circuitBreaker(name)).thenReturn(circuitBreaker);
        when(circuitBreaker.getState()).thenReturn(CircuitBreaker.State.CLOSED);

        CircuitBreaker.Metrics metrics = mock(CircuitBreaker.Metrics.class);
        when(circuitBreaker.getMetrics()).thenReturn(metrics);

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .failureRateThreshold(50)
                .slowCallRateThreshold(50)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .build();
        when(circuitBreaker.getCircuitBreakerConfig()).thenReturn(config);

        // When
        StepVerifier.create(cbService.getCircuitBreakerStatus(name))
                .assertNext(status -> {
                    assertThat(status.getName()).isEqualTo(name);
                    assertThat(status.getState()).isEqualTo("CLOSED");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Devrait forcer l'ouverture d'un Circuit Breaker")
    void shouldForceOpen() {
        // Given
        String name = "testCB";
        when(circuitBreakerRegistry.circuitBreaker(name)).thenReturn(circuitBreaker);

        // When
        StepVerifier.create(cbService.forceOpen(name))
                .verifyComplete();

        // Then
        verify(circuitBreaker).transitionToForcedOpenState();
    }
}
