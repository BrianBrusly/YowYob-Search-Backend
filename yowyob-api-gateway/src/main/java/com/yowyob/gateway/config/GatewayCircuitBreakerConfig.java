package com.yowyob.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Configuration des Circuit Breakers avec Resilience4j
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Configure les paramètres de résilience pour chaque service backend
 *          Protection contre les cascades de pannes dans une architecture
 *          microservices
 */
@Configuration
public class GatewayCircuitBreakerConfig {

        /**
         * Configuration personnalisée pour Resilience4j
         *
         * @return Customizer pour configurer les Circuit Breakers
         */
        @Bean
        public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
                return factory -> {
                        // Configuration par défaut pour tous les services
                        factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                                        .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
                                                        .custom()
                                                        .slidingWindowType(SlidingWindowType.COUNT_BASED)
                                                        .slidingWindowSize(10) // Observe les 10 dernières requêtes
                                                        .minimumNumberOfCalls(5) // Minimum avant d'évaluer
                                                        .failureRateThreshold(50.0f) // Ouvre si > 50% d'échecs
                                                        .slowCallRateThreshold(50.0f) // Ouvre si > 50% lentes
                                                        .slowCallDurationThreshold(Duration.ofSeconds(2))
                                                        .waitDurationInOpenState(Duration.ofSeconds(10))
                                                        .permittedNumberOfCallsInHalfOpenState(3)
                                                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                                                        .recordExceptions(
                                                                        IOException.class,
                                                                        TimeoutException.class)
                                                        .ignoreExceptions(
                                                                        com.yowyob.common.exception.BadRequestException.class,
                                                                        com.yowyob.common.exception.UnauthorizedException.class)
                                                        .build())
                                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                                        .timeoutDuration(Duration.ofSeconds(5))
                                                        .cancelRunningFuture(true)
                                                        .build())
                                        .build());

                        // Configuration spécifique pour le Search Service
                        factory.configure(builder -> builder
                                        .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
                                                        .custom()
                                                        .slidingWindowSize(20)
                                                        .failureRateThreshold(40.0f) // Plus sensible pour le service
                                                                                     // critique
                                                        .waitDurationInOpenState(Duration.ofSeconds(5))
                                                        .build())
                                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                                        .timeoutDuration(Duration.ofSeconds(10)) // Timeout plus long
                                                                                                 // pour les recherches
                                                        .build()),
                                        "searchCircuitBreaker");

                        // Configuration pour le User Service
                        factory.configure(builder -> builder
                                        .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
                                                        .custom()
                                                        .slidingWindowSize(50) // Plus d'historique pour service
                                                                               // utilisateur
                                                        .failureRateThreshold(30.0f) // Plus strict pour service
                                                                                     // d'authentification
                                                        .waitDurationInOpenState(Duration.ofSeconds(15))
                                                        .build())
                                        .timeLimiterConfig(TimeLimiterConfig.custom()
                                                        .timeoutDuration(Duration.ofSeconds(3)) // Timeout court pour
                                                                                                // l'authentification
                                                        .build()),
                                        "userCircuitBreaker");
                };
        }
}