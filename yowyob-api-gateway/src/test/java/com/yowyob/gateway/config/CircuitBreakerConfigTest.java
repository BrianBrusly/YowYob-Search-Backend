package com.yowyob.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de configuration des Circuit Breakers
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Vérifie que les Circuit Breakers sont configurés correctement
 */
@SpringBootTest
@ActiveProfiles("test")
class CircuitBreakerConfigTest {

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker searchCircuitBreaker;
    private CircuitBreaker userCircuitBreaker;

    @BeforeEach
    void setUp() {
        searchCircuitBreaker = circuitBreakerRegistry.circuitBreaker("searchCircuitBreaker");
        userCircuitBreaker = circuitBreakerRegistry.circuitBreaker("userCircuitBreaker");
    }

    @Test
    @DisplayName("Devrait créer des Circuit Breakers pour chaque service")
    void shouldCreateCircuitBreakersForEachService() {
        assertThat(searchCircuitBreaker).isNotNull();
        assertThat(userCircuitBreaker).isNotNull();

        // Vérifier que d'autres Circuit Breakers existent
        assertThat(circuitBreakerRegistry.getAllCircuitBreakers())
                .extracting(CircuitBreaker::getName)
                .contains("searchCircuitBreaker", "userCircuitBreaker", "geoCircuitBreaker");
    }

    @Test
    @DisplayName("Devrait configurer les paramètres par défaut du Circuit Breaker")
    void shouldConfigureDefaultCircuitBreakerSettings() {
        CircuitBreakerConfig config = searchCircuitBreaker.getCircuitBreakerConfig();

        assertThat(config.getSlidingWindowSize()).isEqualTo(10);
        assertThat(config.getMinimumNumberOfCalls()).isEqualTo(5);
        assertThat(config.getFailureRateThreshold()).isEqualTo(50.0f);
        assertThat(config.getSlowCallDurationThreshold()).isEqualTo(Duration.ofSeconds(2));
        assertThat(config.getWaitIntervalFunctionInOpenState().apply(1)).isEqualTo(Duration.ofSeconds(10));
        assertThat(config.getPermittedNumberOfCallsInHalfOpenState()).isEqualTo(3);
    }

    @Test
    @DisplayName("Devrait configurer des paramètres spécifiques pour le Search Service")
    void shouldConfigureSpecificSettingsForSearchService() {
        CircuitBreakerConfig config = searchCircuitBreaker.getCircuitBreakerConfig();

        assertThat(config.getSlidingWindowSize()).isEqualTo(20);
        assertThat(config.getFailureRateThreshold()).isEqualTo(40.0f);
        assertThat(config.getWaitIntervalFunctionInOpenState().apply(1)).isEqualTo(Duration.ofSeconds(5));
    }

    @Test
    @DisplayName("Devrait configurer des paramètres spécifiques pour le User Service")
    void shouldConfigureSpecificSettingsForUserService() {
        CircuitBreakerConfig config = userCircuitBreaker.getCircuitBreakerConfig();

        assertThat(config.getSlidingWindowSize()).isEqualTo(50);
        assertThat(config.getFailureRateThreshold()).isEqualTo(30.0f);
        assertThat(config.getWaitIntervalFunctionInOpenState().apply(1)).isEqualTo(Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("Devrait enregistrer les bonnes exceptions")
    void shouldRecordCorrectExceptions() {
        CircuitBreakerConfig config = searchCircuitBreaker.getCircuitBreakerConfig();

        // En Resilience4j 2.x, on vérifie via le predicate car les listes ne sont plus
        // directement exposées
        assertThat(config.getRecordExceptionPredicate().test(new java.io.IOException())).isTrue();
        assertThat(config.getRecordExceptionPredicate().test(new java.util.concurrent.TimeoutException())).isTrue();
    }

    @Test
    @DisplayName("Devrait ignorer les exceptions client")
    void shouldIgnoreClientExceptions() {
        CircuitBreakerConfig config = searchCircuitBreaker.getCircuitBreakerConfig();

        assertThat(
                config.getIgnoreExceptionPredicate().test(new com.yowyob.common.exception.BadRequestException("test")))
                .isTrue();
        assertThat(config.getIgnoreExceptionPredicate()
                .test(new com.yowyob.common.exception.ResourceNotFoundException("test"))).isTrue();
        assertThat(config.getIgnoreExceptionPredicate()
                .test(new com.yowyob.common.exception.UnauthorizedException("test"))).isTrue();
    }

    @Test
    @DisplayName("Devrait démarrer avec l'état CLOSED")
    void shouldStartWithClosedState() {
        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
        assertThat(userCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    @DisplayName("Devrait avoir la transition automatique de OPEN à HALF_OPEN activée")
    void shouldHaveAutomaticTransitionFromOpenToHalfOpenEnabled() {
        CircuitBreakerConfig config = searchCircuitBreaker.getCircuitBreakerConfig();
        assertThat(config.isAutomaticTransitionFromOpenToHalfOpenEnabled()).isTrue();
    }

    @Test
    @DisplayName("Devrait configurer le seuil d'appels lents")
    void shouldConfigureSlowCallThreshold() {
        CircuitBreakerConfig config = searchCircuitBreaker.getCircuitBreakerConfig();

        assertThat(config.getSlowCallRateThreshold()).isEqualTo(50.0f);
        assertThat(config.getSlowCallDurationThreshold()).isEqualTo(Duration.ofSeconds(2));
    }
}