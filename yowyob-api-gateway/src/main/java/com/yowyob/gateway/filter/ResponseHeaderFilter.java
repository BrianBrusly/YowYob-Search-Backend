package com.yowyob.gateway.filter;

import net.minidev.json.writer.BeansMapper;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Filtre pour ajouter des headers de réponse standardisés
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Ajoute des headers de sécurité, de cache et de monitoring
 * à toutes les réponses de l'API Gateway
 */
@Component
public class ResponseHeaderFilter extends AbstractGatewayFilterFactory<ResponseHeaderFilter.Config> {

    /**
     * Configuration du filtre
     */
    public static class Config {
        private Map<String, String> headers = new HashMap<>();

        public Map<String, String> getHeaders() { return headers; }
        public void setHeaders(Map<String, String> headers) { this.headers = headers; }

        public Config addHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }
    }

    public ResponseHeaderFilter() {
        super(Config.class);
    }

    /**
     * Configuration par défaut
     */
    @BeansMapper.Bean
    public Config defaultConfig() {
        return new Config()
                .addHeader("X-Content-Type-Options", "nosniff")
                .addHeader("X-Frame-Options", "DENY")
                .addHeader("X-XSS-Protection", "1; mode=block")
                .addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
                .addHeader("Referrer-Policy", "strict-origin-when-cross-origin")
                .addHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()")
                .addHeader("X-Powered-By", "YowYob Search - 4GI ENSPY Promo 2027")
                .addHeader("X-API-Version", "1.0.0")
                .addHeader("X-Response-Time", Instant.now().toString());
    }

    /**
     * Application du filtre
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    var response = exchange.getResponse();

                    // ============================================
                    // HEADERS DE SÉCURITÉ
                    // ============================================
                    response.getHeaders().add("X-Content-Type-Options", "nosniff");
                    response.getHeaders().add("X-Frame-Options", "DENY");
                    response.getHeaders().add("X-XSS-Protection", "1; mode=block");
                    response.getHeaders().add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                    response.getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");
                    response.getHeaders().add("Permissions-Policy", "geolocation=(), microphone=(), camera=()");

                    // ============================================
                    // HEADERS D'API
                    // ============================================
                    response.getHeaders().add("X-Powered-By", "YowYob Search - 4GI ENSPY Promo 2027");
                    response.getHeaders().add("X-API-Version", "1.0.0");
                    response.getHeaders().add("X-API-Gateway", "Spring Cloud Gateway");

                    // ============================================
                    // HEADERS DE PERFORMANCE
                    // ============================================
                    Long startTime = exchange.getAttribute("requestStartTime");
                    if (startTime != null) {
                        long duration = System.currentTimeMillis() - startTime;
                        response.getHeaders().add("X-Response-Time", duration + "ms");
                    }

                    // ============================================
                    // HEADERS DE CACHE
                    // ============================================
                    String path = exchange.getRequest().getPath().value();
                    if (path.startsWith("/api/search") || path.startsWith("/api/geo")) {
                        // Cache public pour les données de recherche et géo
                        response.getHeaders().add("Cache-Control", "public, max-age=60, stale-while-revalidate=30");
                        response.getHeaders().add("ETag", generateETag(response));
                    } else if (path.startsWith("/api/auth") || path.startsWith("/api/users")) {
                        // Pas de cache pour les données d'authentification
                        response.getHeaders().add("Cache-Control", "no-store, no-cache, must-revalidate");
                    } else {
                        // Cache par défaut
                        response.getHeaders().add("Cache-Control", "no-cache");
                    }

                    // ============================================
                    // HEADERS DE CORRÉLATION
                    // ============================================
                    String correlationId = exchange.getAttribute("X-Correlation-ID");
                    if (correlationId != null) {
                        response.getHeaders().add("X-Correlation-ID", correlationId);
                        response.getHeaders().add("X-Request-ID", correlationId);
                    }

                    // ============================================
                    // HEADERS SPÉCIFIQUES DANS LA CONFIG
                    // ============================================
                    if (config != null && config.getHeaders() != null) {
                        config.getHeaders().forEach(response.getHeaders()::add);
                    }

                    // ============================================
                    // HEADERS DE RATE LIMITING (si disponibles)
                    // ============================================
                    String rateLimitRemaining = exchange.getAttribute("X-RateLimit-Remaining");
                    if (rateLimitRemaining != null) {
                        response.getHeaders().add("X-RateLimit-Remaining", rateLimitRemaining);
                    }

                    // ============================================
                    // HEADERS DE COMPRESSION
                    // ============================================
                    if (shouldCompress(response)) {
                        response.getHeaders().add("Content-Encoding", "gzip");
                    }
                }));
    }

    /**
     * Génère un ETag pour le cache
     */
    private String generateETag(org.springframework.http.server.reactive.ServerHttpResponse response) {
        // Générer un ETag basé sur le contenu et la date
        String content = response.getHeaders().getFirst("Content-Length");
        String date = response.getHeaders().getFirst("Date");

        if (content != null && date != null) {
            String input = content + ":" + date;
            return "\"" + Integer.toHexString(input.hashCode()) + "\"";
        }

        return "\"" + java.util.UUID.randomUUID().toString() + "\"";
    }

    /**
     * Détermine si la réponse doit être compressée
     */
    private boolean shouldCompress(org.springframework.http.server.reactive.ServerHttpResponse response) {
        String contentType = response.getHeaders().getFirst("Content-Type");
        Long contentLength = response.getHeaders().getContentLength();

        // Compresser si le contenu est grand et compressible
        if (contentLength != null && contentLength > 1024) {
            if (contentType != null) {
                return contentType.contains("application/json") ||
                        contentType.contains("text/") ||
                        contentType.contains("application/javascript");
            }
        }

        return false;
    }

    /**
     * Filtre pour les réponses d'erreur
     */
    public GatewayFilter applyForErrors() {
        return (exchange, chain) -> chain.filter(exchange)
                .doOnError(error -> {
                    var response = exchange.getResponse();

                    // Headers spécifiques pour les erreurs
                    response.getHeaders().add("X-Error-Type", error.getClass().getSimpleName());
                    response.getHeaders().add("X-Error-Message", error.getMessage());
                    response.getHeaders().add("X-Error-Time", Instant.now().toString());

                    // Log de l'erreur
                    System.err.printf("[ResponseHeaderFilter] Error: %s - %s%n",
                            error.getClass().getName(), error.getMessage());
                });
    }

    /**
     * Filtre pour les réponses de succès
     */
    public GatewayFilter applyForSuccess() {
        return (exchange, chain) -> chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    var response = exchange.getResponse();

                    // Headers spécifiques pour les succès
                    if (response.getStatusCode() != null &&
                            response.getStatusCode().is2xxSuccessful()) {
                        response.getHeaders().add("X-Success", "true");
                        response.getHeaders().add("X-Processed-At", Instant.now().toString());
                    }
                });
    }
}