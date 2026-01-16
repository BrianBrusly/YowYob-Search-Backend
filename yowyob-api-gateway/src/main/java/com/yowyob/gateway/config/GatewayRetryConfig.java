package com.yowyob.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import io.github.resilience4j.core.IntervalFunction;

/**
 * Configuration des politiques de retry
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Configure les nouvelles tentatives pour les échecs transitoires
 *          Utilise un backoff exponentiel avec jitter
 */
@Configuration
public class GatewayRetryConfig {

        /**
         * Configuration globale des retries
         *
         * @return RetryConfig configuré
         */
        @Bean
        public io.github.resilience4j.retry.RetryConfig defaultRetryConfig() {
                return io.github.resilience4j.retry.RetryConfig.custom()
                                .maxAttempts(3) // Maximum 3 tentatives
                                .waitDuration(Duration.ofMillis(500)) // Attente initiale de 500ms
                                .intervalFunction(IntervalFunction.ofExponentialBackoff(
                                                Duration.ofMillis(500), 2.0))

                                .retryExceptions(
                                                IOException.class,
                                                TimeoutException.class,
                                                org.springframework.web.reactive.function.client.WebClientResponseException.ServiceUnavailable.class)
                                .ignoreExceptions(
                                                com.yowyob.common.exception.BadRequestException.class,
                                                com.yowyob.common.exception.UnauthorizedException.class,
                                                com.yowyob.common.exception.ForbiddenException.class)
                                .failAfterMaxAttempts(true) // Échoue après le nombre max de tentatives
                                .build();
        }

        /**
         * Configuration pour les opérations de lecture
         *
         * @return RetryConfig pour les opérations GET
         */
        @Bean
        public io.github.resilience4j.retry.RetryConfig readRetryConfig() {
                return io.github.resilience4j.retry.RetryConfig.from(defaultRetryConfig())
                                .maxAttempts(2) // Moins de retries pour les lectures
                                .waitDuration(Duration.ofMillis(100)) // Attente plus courte
                                .build();
        }

        /**
         * Configuration pour les opérations d'écriture
         *
         * @return RetryConfig pour les opérations POST/PUT/DELETE
         */
        @Bean
        public io.github.resilience4j.retry.RetryConfig writeRetryConfig() {
                return io.github.resilience4j.retry.RetryConfig.from(defaultRetryConfig())
                                .maxAttempts(1) // Pas de retry pour les écritures (idempotence)
                                .build();
        }

        /**
         * Configuration pour l'authentification
         *
         * @return RetryConfig pour les endpoints d'authentification
         */
        @Bean
        public io.github.resilience4j.retry.RetryConfig authRetryConfig() {
                return io.github.resilience4j.retry.RetryConfig.from(defaultRetryConfig())
                                .maxAttempts(1) // Pas de retry pour l'authentification
                                .build();
        }
}