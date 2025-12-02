package com.yowyob.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration CORS pour l'API Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Configure les règles Cross-Origin Resource Sharing
 * pour autoriser les requêtes depuis le frontend et d'autres origines autorisées
 */
@Configuration
public class CorsConfig {

    /**
     * Configuration du filtre CORS
     *
     * @return CorsWebFilter configuré
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ============================================
        // ORIGINES AUTORISÉES
        // ============================================
        List<String> allowedOrigins = Arrays.asList(
                // Développement local
                "http://localhost:3000",    // Frontend React
                "http://localhost:8080",    // API Gateway local
                "http://localhost:8081",    // Alternative

                // Staging
                "https://staging.yowyob.com",

                // Production
                "https://yowyob.com",
                "https://www.yowyob.com",

                // Mobile apps (si nécessaire)
                "capacitor://localhost",
                "ionic://localhost"
        );

        config.setAllowedOrigins(allowedOrigins);

        // ============================================
        // MÉTHODES HTTP AUTORISÉES
        // ============================================
        config.setAllowedMethods(Arrays.asList(
                "GET",      // Lecture
                "POST",     // Création
                "PUT",      // Mise à jour
                "DELETE",   // Suppression
                "OPTIONS",  // Pré-vol CORS
                "PATCH",    // Mise à jour partielle
                "HEAD"      // En-têtes seulement
        ));

        // ============================================
        // HEADERS AUTORISÉS
        // ============================================
        config.setAllowedHeaders(Arrays.asList(
                // Headers standards
                "Origin",
                "Content-Type",
                "Accept",
                "Authorization",
                "X-Requested-With",

                // Headers personnalisés
                "X-API-Key",
                "X-Client-Version",
                "X-Device-ID",
                "X-Session-ID",
                "X-Request-ID",
                "X-Correlation-ID",

                // Headers de cache
                "If-Modified-Since",
                "If-None-Match",
                "Cache-Control",

                // Headers de sécurité
                "X-Forwarded-For",
                "X-Forwarded-Proto",
                "X-Forwarded-Host"
        ));

        // ============================================
        // HEADERS EXPOSÉS AU CLIENT
        // ============================================
        config.setExposedHeaders(Arrays.asList(
                // Headers personnalisés
                "X-Request-ID",
                "X-Correlation-ID",
                "X-RateLimit-Limit",
                "X-RateLimit-Remaining",
                "X-RateLimit-Reset",

                // Headers de pagination
                "X-Total-Count",
                "X-Page",
                "X-Page-Size",
                "X-Total-Pages",

                // Headers de cache
                "ETag",
                "Last-Modified",

                // Headers de localisation
                "Location",
                "Content-Location"
        ));

        // ============================================
        // CONFIGURATIONS SUPPLEMENTAIRES
        // ============================================
        config.setAllowCredentials(true);           // Autoriser les credentials (cookies, auth)
        config.setMaxAge(3600L);                    // Cache des pré-vols: 1 heure

        // ============================================
        // CONFIGURATION SPÉCIFIQUE PAR PATTERN
        // ============================================
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Configuration générale pour toutes les routes
        source.registerCorsConfiguration("/**", config);

        // Configuration spécifique pour les WebSockets
        CorsConfiguration wsConfig = new CorsConfiguration();
        wsConfig.setAllowedOrigins(allowedOrigins);
        wsConfig.setAllowedMethods(Arrays.asList("GET", "POST"));
        wsConfig.setAllowCredentials(true);
        source.registerCorsConfiguration("/ws/**", wsConfig);

        // Configuration spécifique pour les uploads (allow more headers)
        CorsConfiguration uploadConfig = new CorsConfiguration();
        uploadConfig.setAllowedOrigins(allowedOrigins);
        uploadConfig.setAllowedMethods(Arrays.asList("POST", "PUT", "PATCH"));
        uploadConfig.setAllowedHeaders(Arrays.asList(
                "Content-Type", "Authorization", "Content-Length",
                "X-File-Name", "X-File-Size", "X-File-Type"
        ));
        uploadConfig.setAllowCredentials(true);
        source.registerCorsConfiguration("/api/upload/**", uploadConfig);

        return new CorsWebFilter(source);
    }

    /**
     * Configuration CORS alternative pour les endpoints spécifiques
     */
    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.applyPermitDefaultValues();

        // Override des valeurs par défaut
        config.addAllowedOriginPattern("*"); // Pour le développement seulement
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("PATCH");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedHeader("X-Requested-With");
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("Content-Type");
        config.setAllowCredentials(true);

        return config;
    }
}