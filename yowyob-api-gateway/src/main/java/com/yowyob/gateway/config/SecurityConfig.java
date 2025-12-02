package com.yowyob.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

/**
 * Configuration de sécurité pour l'API Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Configure la sécurité Spring Security pour WebFlux
 * Définit les endpoints publics vs protégés
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructeur avec injection des dépendances
     *
     * @param jwtAuthenticationFilter Filtre d'authentification JWT
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configuration de la chaîne de sécurité
     *
     * @param http SecurityWebFilterChain builder
     * @return SecurityWebFilterChain configuré
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Désactiver CSRF pour les APIs
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Configuration des autorisations
                .authorizeExchange(exchanges -> exchanges
                        // ============================================
                        // ENDPOINTS PUBLICS (pas d'authentification)
                        // ============================================
                        .pathMatchers(
                                // Authentication
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/refresh",

                                // Recherche publique
                                "/api/search/**",

                                // Géolocalisation publique
                                "/api/geo/**",

                                // Statistiques publiques
                                "/api/stats/public/**",

                                // Health checks
                                "/actuator/health",
                                "/actuator/info",

                                // Documentation
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",

                                // WebSocket pour notifications publiques
                                "/ws/**",

                                // Fallback endpoints
                                "/fallback/**"
                        ).permitAll()

                        // ============================================
                        // ENDPOINTS ADMINISTRATEURS
                        // ============================================
                        .pathMatchers(
                                "/api/crawler/**",
                                "/api/stats/admin/**",
                                "/api/analytics/**",
                                "/actuator/**"
                        ).hasAnyRole("ADMIN", "MODERATOR")

                        // ============================================
                        // ENDPOINTS MARCHANDS
                        // ============================================
                        .pathMatchers(
                                "/api/shop/merchants/**",
                                "/api/products/manage/**"
                        ).hasAnyRole("MERCHANT", "ADMIN")

                        // ============================================
                        // TOUS LES AUTRES ENDPOINTS NÉCESSITENT UNE AUTHENTIFICATION
                        // ============================================
                        .anyExchange().authenticated()
                )

                // Ajouter le filtre JWT avant l'authentification
                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                // Configuration de la gestion des exceptions
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((exchange, ex) -> {
                            // Retourner une erreur 401 pour les authentifications échouées
                            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                            exchange.getResponse().getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                            String body = """
                        {
                            "error": "unauthorized",
                            "message": "Authentification requise. Veuillez vous connecter.",
                            "timestamp": "%s",
                            "path": "%s"
                        }
                        """.formatted(
                                    java.time.Instant.now().toString(),
                                    exchange.getRequest().getPath().value()
                            );
                            return exchange.getResponse().writeWith(
                                    Mono.just(exchange.getResponse()
                                            .bufferFactory()
                                            .wrap(body.getBytes()))
                            );
                        })
                        .accessDeniedHandler((exchange, ex) -> {
                            // Retourner une erreur 403 pour les accès refusés
                            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
                            exchange.getResponse().getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                            String body = """
                        {
                            "error": "access_denied",
                            "message": "Vous n'avez pas les permissions nécessaires pour accéder à cette ressource.",
                            "timestamp": "%s",
                            "path": "%s",
                            "required_roles": "%s"
                        }
                        """.formatted(
                                    java.time.Instant.now().toString(),
                                    exchange.getRequest().getPath().value(),
                                    ex.getMessage()
                            );
                            return exchange.getResponse().writeWith(
                                    Mono.just(exchange.getResponse()
                                            .bufferFactory()
                                            .wrap(body.getBytes()))
                            );
                        })
                )

                // Build de la configuration
                .build();
    }

    /**
     * Configuration du contexte de sécurité réactif
     */
    @Bean
    public org.springframework.security.core.context.ReactiveSecurityContextHolderStrategy reactiveSecurityContextHolderStrategy() {
        return new org.springframework.security.core.context.ReactiveSecurityContextHolderStrategy();
    }
}