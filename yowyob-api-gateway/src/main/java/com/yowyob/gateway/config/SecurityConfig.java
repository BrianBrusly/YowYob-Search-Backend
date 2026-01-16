package com.yowyob.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import com.yowyob.gateway.filter.JwtAuthenticationFilter;

/**
 * Configuration de sécurité pour le Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Configure la sécurité réactive avec WebFlux Security
 *          Définit les endpoints publics vs protégés et intègre le filtre JWT
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

        /**
         * Configuration de la chaîne de sécurité WebFlux
         *
         * @param http                    ServerHttpSecurity pour configurer la sécurité
         * @param jwtAuthenticationFilter Filtre JWT personnalisé
         * @return SecurityWebFilterChain configuré
         */
        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(
                        ServerHttpSecurity http,
                        JwtAuthenticationFilter jwtAuthenticationFilter) {

                return http
                                // Désactivation CSRF pour API stateless
                                .csrf(csrf -> csrf.disable())

                                // Configuration CORS
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // Configuration des autorisations
                                .authorizeExchange(exchanges -> exchanges
                                                // Endpoints publics (sans authentification)
                                                .pathMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                                                .pathMatchers(HttpMethod.GET, "/actuator/info").permitAll()
                                                .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                                                .pathMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                                                .pathMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                                                .pathMatchers(HttpMethod.GET, "/api/search/**").permitAll()
                                                .pathMatchers(HttpMethod.GET, "/api/geo/**").permitAll()
                                                .pathMatchers(HttpMethod.GET, "/api/shop/**").permitAll()

                                                // Endpoints nécessitant authentification
                                                .pathMatchers(HttpMethod.GET, "/api/users/profile").authenticated()
                                                .pathMatchers(HttpMethod.PUT, "/api/users/**").authenticated()
                                                .pathMatchers(HttpMethod.DELETE, "/api/users/**").authenticated()
                                                .pathMatchers("/api/notifications/**").authenticated()
                                                .pathMatchers("/api/stats/**").authenticated()

                                                // Endpoints admin
                                                .pathMatchers("/actuator/**").hasRole("ADMIN")
                                                .pathMatchers("/api/crawler/**").hasRole("ADMIN")
                                                .pathMatchers("/api/stats/admin/**").hasRole("ADMIN")

                                                // Tout autre endpoint nécessite authentification
                                                .anyExchange().authenticated())

                                // Désactivation des mécanismes d'authentification par défaut
                                .httpBasic(httpBasic -> httpBasic.disable())
                                .formLogin(formLogin -> formLogin.disable())
                                .logout(logout -> logout.disable())

                                // Configuration des sessions (stateless)
                                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                                .build();
        }

        /**
         * Configuration CORS pour le Gateway
         *
         * @return CorsConfigurationSource configuré
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(Arrays.asList(
                                "http://localhost:3000", // Dev frontend
                                "http://localhost:8081", // Dev autre
                                "https://yowyob.com", // Production
                                "https://*.yowyob.com" // Sous-domaines
                ));

                configuration.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

                configuration.setAllowedHeaders(Arrays.asList(
                                "Authorization", "Content-Type", "X-Requested-With",
                                "Accept", "Origin", "X-User-Id", "X-User-Roles",
                                "X-Request-Id", "X-Correlation-Id"));

                configuration.setExposedHeaders(Arrays.asList(
                                "X-Request-Id", "X-Correlation-Id", "X-RateLimit-Limit",
                                "X-RateLimit-Remaining", "X-RateLimit-Reset"));

                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L); // 1 heure

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}