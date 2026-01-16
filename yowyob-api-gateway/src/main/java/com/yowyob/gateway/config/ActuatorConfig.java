package com.yowyob.gateway.config;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration des endpoints Actuator
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Configure les endpoints de monitoring et de gestion
 *          Sécurise l'accès aux endpoints sensibles
 */
@Configuration
@EnableWebFluxSecurity
public class ActuatorConfig {

        /**
         * Configuration de sécurité pour les endpoints Actuator
         *
         * @param http ServerHttpSecurity
         * @return SecurityWebFilterChain configuré
         */
        @Bean
        public SecurityWebFilterChain actuatorSecurityFilterChain(ServerHttpSecurity http) {
                return http
                                .securityMatcher(
                                                org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
                                                                .pathMatchers("/actuator/**"))
                                .authorizeExchange(exchanges -> exchanges
                                                // Endpoints publics (sans authentification)
                                                .pathMatchers("/actuator/health").permitAll()
                                                .pathMatchers("/actuator/health/**").permitAll()
                                                .pathMatchers("/actuator/info").permitAll()
                                                .pathMatchers("/actuator/metrics").hasRole("MONITOR")
                                                .pathMatchers("/actuator/prometheus").hasRole("MONITOR")

                                                // Endpoints sensibles (admin seulement)
                                                .pathMatchers("/actuator/**").hasRole("ADMIN")

                                                // Tout autre endpoint nécessite authentification
                                                .anyExchange().authenticated())
                                .httpBasic(httpBasic -> {
                                }) // Basic auth pour les outils de monitoring
                                .csrf(csrf -> csrf.disable()) // CSRF désactivé pour les APIs
                                .build();
        }

        // Health indicators are auto-configured by Spring Boot 3.4
        // No need for manual HealthIndicatorRegistry or HealthEndpoint beans

        // Info endpoint is auto-configured by Spring Boot 3.4
        // Configure via application.yml: management.info.* properties

        // Metrics endpoint is auto-configured by Spring Boot 3.4

        // Management port is auto-configured via application.yml:
        // management.server.port or management.port-type

        // Path mapping is auto-configured via application.yml:
        // management.endpoints.web.base-path
}