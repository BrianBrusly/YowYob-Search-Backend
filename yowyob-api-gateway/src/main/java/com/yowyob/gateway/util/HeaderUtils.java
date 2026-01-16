package com.yowyob.gateway.util;

import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utilitaires pour la manipulation des headers HTTP
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
public final class HeaderUtils {

    private HeaderUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Extraire le token JWT depuis les headers
     * 
     * @param exchange ServerWebExchange
     * @return Token JWT ou null
     */
    public static String extractJwtToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    /**
     * Ajouter un ID de corrélation aux headers
     * 
     * @param exchange ServerWebExchange
     * @return ID de corrélation
     */
    public static String addCorrelationId(ServerWebExchange exchange) {
        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Correlation-Id");

        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
            exchange.getRequest().mutate()
                    .header("X-Correlation-Id", correlationId);
        }

        return correlationId;
    }

    /**
     * Extraire les informations utilisateur depuis les headers
     * 
     * @param exchange ServerWebExchange
     * @return Map contenant userId et roles
     */
    public static Map<String, String> extractUserInfo(ServerWebExchange exchange) {
        String userId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Id");
        String roles = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Roles");

        return Map.of(
                "userId", userId != null ? userId : "anonymous",
                "roles", roles != null ? roles : "",
                "authenticated", userId != null ? "true" : "false");
    }

    /**
     * Nettoyer les headers sensibles pour les logs
     * 
     * @param headers Headers originaux
     * @return Headers nettoyés
     */
    public static HttpHeaders sanitizeHeaders(HttpHeaders headers) {
        HttpHeaders sanitized = new HttpHeaders();
        sanitized.putAll(headers);

        // Supprimer les headers sensibles
        sanitized.remove(HttpHeaders.AUTHORIZATION);
        sanitized.remove("Cookie");
        sanitized.remove("Set-Cookie");

        // Anonymiser certains headers
        if (sanitized.containsKey("X-User-Id")) {
            sanitized.set("X-User-Id", "[REDACTED]");
        }

        if (sanitized.containsKey("X-API-Key")) {
            sanitized.set("X-API-Key", "[REDACTED]");
        }

        return sanitized;
    }

    /**
     * Ajouter des headers de réponse standard
     * 
     * @param exchange         ServerWebExchange
     * @param processingTimeMs Temps de traitement en ms
     */
    public static void addStandardResponseHeaders(
            ServerWebExchange exchange,
            long processingTimeMs) {

        HttpHeaders headers = exchange.getResponse().getHeaders();

        // Headers de performance
        headers.add("X-Response-Time", processingTimeMs + "ms");
        headers.add("X-API-Version", "1.0.0");

        // Headers de sécurité
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("X-XSS-Protection", "1; mode=block");

        // Header CORS
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.add("Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With");

        // Propagation du correlation ID
        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Correlation-Id");
        if (correlationId != null) {
            headers.add("X-Correlation-Id", correlationId);
        }
    }

    /**
     * Vérifier si une requête vient d'un service interne
     * 
     * @param exchange ServerWebExchange
     * @return true si c'est une requête interne
     */
    public static boolean isInternalRequest(ServerWebExchange exchange) {
        String internalHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Internal-Service");

        return internalHeader != null &&
                List.of("search-service", "user-service", "geo-service",
                        "crawler-service", "notification-service",
                        "shop-service", "stats-service")
                        .contains(internalHeader.toLowerCase());
    }
}