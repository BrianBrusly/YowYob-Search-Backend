package com.yowyob.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration du Circuit Breaker pour l'API Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Configure les paramètres de résilience pour chaque service backend
 * avec des configurations spécifiques selon la criticité du service
 */
@Configuration
public class CircuitBreakerConfig {

    /**
     * Configuration du Circuit Breaker pour le Search Service
     * (Service critique - tolérance aux pannes élevée)
     */
    @Bean
    public CircuitBreakerConfig searchServiceCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                // Configuration de la fenêtre glissante
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)  // Observer les 10 dernières requêtes

                // Seuil d'échec
                .failureRateThreshold(50.0f)  // 50% d'échecs = ouvrir le circuit

                // Configuration de l'état ouvert
                .waitDurationInOpenState(Duration.ofSeconds(5))

                // Configuration de l'état semi-ouvert
                .permittedNumberOfCallsInHalfOpenState(3)
                .maxWaitDurationInHalfOpenState(Duration.ofSeconds(30))

                // Configuration des exceptions
                .ignoreExceptions(
                        java.net.SocketTimeoutException.class,
                        org.springframework.web.server.ResponseStatusException.class
                )
                .recordExceptions(
                        java.io.IOException.class,
                        java.util.concurrent.TimeoutException.class,
                        org.springframework.cloud.gateway.support.NotFoundException.class
                )

                // Configuration des thresholds
                .slowCallRateThreshold(80.0f)  // 80% de requêtes lentes
                .slowCallDurationThreshold(Duration.ofSeconds(2))  // >2s = lent

                // Configuration du buffer
                .minimumNumberOfCalls(5)  // Minimum 5 appels avant de décider

                // Build
                .build();
    }

    /**
     * Configuration du Circuit Breaker pour le Shop Service
     * (Service important mais moins critique)
     */
    @Bean
    public CircuitBreakerConfig shopServiceCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(20)  // Fenêtre plus grande
                .failureRateThreshold(60.0f)  // Plus tolérant
                .waitDurationInOpenState(Duration.ofSeconds(10))  // Plus long en état ouvert
                .permittedNumberOfCallsInHalfOpenState(5)
                .slowCallRateThreshold(70.0f)
                .slowCallDurationThreshold(Duration.ofSeconds(3))
                .minimumNumberOfCalls(10)
                .build();
    }

    /**
     * Configuration du Circuit Breaker pour les services moins critiques
     */
    @Bean
    public CircuitBreakerConfig defaultCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(50)
                .failureRateThreshold(70.0f)  // Très tolérant
                .waitDurationInOpenState(Duration.ofSeconds(30))  // Longue attente
                .permittedNumberOfCallsInHalfOpenState(10)
                .slowCallRateThreshold(90.0f)
                .slowCallDurationThreshold(Duration.ofSeconds(5))
                .minimumNumberOfCalls(20)
                .build();
    }

    /**
     * Configuration du Time Limiter
     */
    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(10))  // Timeout global de 10s
                .cancelRunningFuture(true)  // Annuler les futures en cours
                .build();
    }

    /**
     * Registre des Circuit Breakers
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        return CircuitBreakerRegistry.of(
                Map.of(
                        "searchCircuitBreaker", searchServiceCircuitBreakerConfig(),
                        "shopCircuitBreaker", shopServiceCircuitBreakerConfig(),
                        "defaultCircuitBreaker", defaultCircuitBreakerConfig()
                )
        );
    }

    /**
     * Personnalisation du Circuit Breaker Factory
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(defaultCircuitBreakerConfig())
                .timeLimiterConfig(timeLimiterConfig())
                .build());
    }

    /**
     * Personnalisation spécifique pour le Search Service
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> searchServiceCustomizer() {
        return factory -> factory.configure(
                builder -> builder
                        .circuitBreakerConfig(searchServiceCircuitBreakerConfig())
                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                .timeoutDuration(Duration.ofSeconds(5))
                                .build()),
                "searchCircuitBreaker");
    }

    /**
     * Personnalisation spécifique pour le Shop Service
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> shopServiceCustomizer() {
        return factory -> factory.configure(
                builder -> builder
                        .circuitBreakerConfig(shopServiceCircuitBreakerConfig())
                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                .timeoutDuration(Duration.ofSeconds(8))
                                .build()),
                "shopCircuitBreaker");
    }

    /**
     * Configuration des événements du Circuit Breaker
     */
    @Bean
    public io.github.resilience4j.circuitbreaker.event.CircuitBreakerEventPublisher circuitBreakerEventPublisher() {
        return event -> {
            switch (event.getEventType()) {
                case STATE_TRANSITION:
                    logStateTransition(event);
                    break;
                case ERROR:
                    logError(event);
                    break;
                case SLOW_CALL_RATE_EXCEEDED:
                    logSlowCallRate(event);
                    break;
                case FAILURE_RATE_EXCEEDED:
                    logFailureRate(event);
                    break;
                default:
                    // Ignorer les autres événements
                    break;
            }
        };
    }

    private void logStateTransition(io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent event) {
        System.out.printf("[Circuit Breaker] %s transitioned from %s to %s%n",
                event.getCircuitBreakerName(),
                event.getStateTransition().getFromState(),
                event.getStateTransition().getToState());
    }

    private void logError(io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent event) {
        System.err.printf("[Circuit Breaker] %s error: %s%n",
                event.getCircuitBreakerName(),
                event.getThrowable().getMessage());
    }

    private void logSlowCallRate(io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent event) {
        System.out.printf("[Circuit Breaker] %s slow call rate exceeded: %.2f%%%n",
                event.getCircuitBreakerName(),
                event.getSlowCallRate());
    }

    private void logFailureRate(io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent event) {
        System.err.printf("[Circuit Breaker] %s failure rate exceeded: %.2f%%%n",
                event.getCircuitBreakerName(),
                event.getFailureRate());
    }
}