package com.yowyob.gateway.util;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.server.ServerWebExchange;

import java.util.regex.Pattern;

/**
 * Utilitaires pour la manipulation des routes
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
public final class RouteUtils {

    private RouteUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");

    private static final Pattern NUMERIC_ID_PATTERN = Pattern.compile("\\d+");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    /**
     * Extrait le nom du service depuis une route
     * 
     * @param exchange ServerWebExchange
     * @return Nom du service
     */
    public static String extractServiceName(ServerWebExchange exchange) {
        Route route = exchange
                .getAttribute(org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if (route == null) {
            return "unknown";
        }

        String uri = route.getUri().toString();
        if (uri.startsWith("lb://")) {
            return uri.substring(5); // Enlever "lb://"
        }

        return uri;
    }

    /**
     * Normalise un chemin pour les métriques
     * Remplace les IDs par des placeholders
     * 
     * @param path Chemin original
     * @return Chemin normalisé
     */
    public static String normalizePathForMetrics(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }

        String normalized = path;

        // Remplacer les UUIDs
        normalized = UUID_PATTERN.matcher(normalized).replaceAll("{uuid}");

        // Remplacer les IDs numériques
        normalized = NUMERIC_ID_PATTERN.matcher(normalized).replaceAll("{id}");

        // Remplacer les emails
        normalized = EMAIL_PATTERN.matcher(normalized).replaceAll("{email}");

        return normalized;
    }

    /**
     * Vérifie si un chemin est un endpoint public
     * 
     * @param path Chemin à vérifier
     * @return true si c'est un endpoint public
     */
    public static boolean isPublicEndpoint(String path) {
        if (path == null) {
            return false;
        }

        return path.startsWith("/actuator/health") ||
                path.startsWith("/actuator/info") ||
                path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/api/auth/refresh") ||
                path.startsWith("/api/search") ||
                path.startsWith("/api/geo") ||
                path.startsWith("/api/shop");
    }

    /**
     * Vérifie si un chemin nécessite l'authentification
     * 
     * @param path Chemin à vérifier
     * @return true si l'authentification est requise
     */
    public static boolean requiresAuthentication(String path) {
        if (path == null) {
            return false;
        }

        return !isPublicEndpoint(path) && (path.startsWith("/api/users") ||
                path.startsWith("/api/notifications") ||
                path.startsWith("/api/stats") ||
                path.startsWith("/api/crawler"));
    }

    /**
     * Vérifie si un chemin est réservé aux administrateurs
     * 
     * @param path Chemin à vérifier
     * @return true si c'est un endpoint admin
     */
    public static boolean isAdminEndpoint(String path) {
        if (path == null) {
            return false;
        }

        return path.startsWith("/actuator") ||
                path.startsWith("/api/crawler") ||
                path.startsWith("/api/stats/admin");
    }

    /**
     * Extrait le type d'API depuis le chemin
     * 
     * @param path Chemin
     * @return Type d'API (search, user, geo, etc.)
     */
    public static String extractApiType(String path) {
        if (path == null || !path.startsWith("/api/")) {
            return "unknown";
        }

        String[] parts = path.split("/");
        if (parts.length >= 3) {
            return parts[2]; // /api/[type]/...
        }

        return "unknown";
    }
}