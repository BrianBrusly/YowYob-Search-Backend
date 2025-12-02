package com.yowyob.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Filtre de logging pour l'API Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Log toutes les requêtes et réponses avec des informations détaillées
 * pour le debugging, le monitoring et l'audit
 */
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> implements Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String REQUEST_START_TIME = "requestStartTime";

    /**
     * Configuration du filtre
     */
    public static class Config {
        private boolean logHeaders = false;
        private boolean logBody = false;
        private int maxBodyLength = 1000;

        public boolean isLogHeaders() { return logHeaders; }
        public void setLogHeaders(boolean logHeaders) { this.logHeaders = logHeaders; }

        public boolean isLogBody() { return logBody; }
        public void setLogBody(boolean logBody) { this.logBody = logBody; }

        public int getMaxBodyLength() { return maxBodyLength; }
        public void setMaxBodyLength(int maxBodyLength) { this.maxBodyLength = maxBodyLength; }
    }

    public LoggingFilter() {
        super(Config.class);
    }

    /**
     * Application du filtre
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Enregistrer le temps de début
            exchange.getAttributes().put(REQUEST_START_TIME, Instant.now());

            // Log de la requête entrante
            logRequest(exchange, config);

            // Continuer la chaîne de filtres
            return chain.filter(exchange)
                    .doOnSuccess(aVoid -> {
                        // Log de la réponse réussie
                        logResponse(exchange, config, true, null);
                    })
                    .doOnError(throwable -> {
                        // Log de l'erreur
                        logResponse(exchange, config, false, throwable);
                    })
                    .doFinally(signalType -> {
                        // Nettoyage si nécessaire
                        cleanup(exchange);
                    });
        };
    }

    /**
     * Log de la requête entrante
     */
    private void logRequest(ServerWebExchange exchange, Config config) {
        var request = exchange.getRequest();
        var attributes = exchange.getAttributes();

        // Générer un ID de corrélation si absent
        String correlationId = request.getHeaders().getFirst("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = java.util.UUID.randomUUID().toString();
            exchange.getAttributes().put("X-Correlation-ID", correlationId);
        }

        // Informations de base de la requête
        String method = request.getMethod().name();
        String path = request.getPath().value();
        String query = request.getURI().getQuery();
        String clientIp = IpAddressUtil.getClientIp(request);
        String userAgent = request.getHeaders().getFirst("User-Agent");
        String referer = request.getHeaders().getFirst("Referer");

        // Construire le log structuré
        var logBuilder = new java.util.HashMap<String, Object>();
        logBuilder.put("type", "request");
        logBuilder.put("correlationId", correlationId);
        logBuilder.put("timestamp", Instant.now().toString());
        logBuilder.put("method", method);
        logBuilder.put("path", path);
        logBuilder.put("query", query);
        logBuilder.put("clientIp", clientIp);
        logBuilder.put("userAgent", userAgent);
        logBuilder.put("referer", referer);

        // Ajouter les headers si configuré
        if (config.isLogHeaders()) {
            var headers = new java.util.HashMap<String, String>();
            request.getHeaders().forEach((key, values) ->
                    headers.put(key, String.join(",", values)));
            logBuilder.put("headers", headers);
        }

        // Log au format JSON
        try {
            String jsonLog = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(logBuilder);
            logger.info("Incoming request: {}", jsonLog);
        } catch (Exception e) {
            logger.warn("Failed to serialize request log: {}", e.getMessage());
        }
    }

    /**
     * Log de la réponse
     */
    private void logResponse(ServerWebExchange exchange, Config config, boolean success, Throwable error) {
        var request = exchange.getRequest();
        var response = exchange.getResponse();
        var attributes = exchange.getAttributes();

        // Récupérer le temps de début
        Instant startTime = (Instant) attributes.get(REQUEST_START_TIME);
        Duration duration = startTime != null ?
                Duration.between(startTime, Instant.now()) : Duration.ZERO;

        // Informations de base
        String correlationId = (String) attributes.get("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = "unknown";
        }

        String method = request.getMethod().name();
        String path = request.getPath().value();
        int statusCode = response.getStatusCode() != null ?
                response.getStatusCode().value() : 0;
        long contentLength = response.getHeaders().getContentLength();

        // Construire le log structuré
        var logBuilder = new java.util.HashMap<String, Object>();
        logBuilder.put("type", "response");
        logBuilder.put("correlationId", correlationId);
        logBuilder.put("timestamp", Instant.now().toString());
        logBuilder.put("method", method);
        logBuilder.put("path", path);
        logBuilder.put("statusCode", statusCode);
        logBuilder.put("durationMs", duration.toMillis());
        logBuilder.put("success", success);
        logBuilder.put("contentLength", contentLength);

        // Ajouter l'erreur si présente
        if (error != null) {
            logBuilder.put("error", error.getMessage());
            logBuilder.put("errorType", error.getClass().getName());
        }

        // Ajouter les headers de réponse si configuré
        if (config.isLogHeaders()) {
            var headers = new java.util.HashMap<String, String>();
            response.getHeaders().forEach((key, values) ->
                    headers.put(key, String.join(",", values)));
            logBuilder.put("responseHeaders", headers);
        }

        // Déterminer le niveau de log
        if (statusCode >= 400) {
            // Erreurs client (4xx) ou serveur (5xx)
            try {
                String jsonLog = new com.fasterxml.jackson.databind.ObjectMapper()
                        .writeValueAsString(logBuilder);
                logger.error("Error response: {}", jsonLog);
            } catch (Exception e) {
                logger.error("Failed to serialize error log: {}", e.getMessage());
            }
        } else {
            // Succès (2xx, 3xx)
            try {
                String jsonLog = new com.fasterxml.jackson.databind.ObjectMapper()
                        .writeValueAsString(logBuilder);
                logger.info("Response: {}", jsonLog);
            } catch (Exception e) {
                logger.warn("Failed to serialize response log: {}", e.getMessage());
            }
        }
    }

    /**
     * Nettoyage après la requête
     */
    private void cleanup(ServerWebExchange exchange) {
        exchange.getAttributes().remove(REQUEST_START_TIME);
        exchange.getAttributes().remove("X-Correlation-ID");
    }

    /**
     * Priorité d'exécution du filtre (plus bas = exécuté plus tôt)
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * Méthode pour obtenir les statistiques de performance
     */
    public void logPerformanceMetrics(ServerWebExchange exchange, Duration duration) {
        var request = exchange.getRequest();
        String path = request.getPath().value();
        int statusCode = exchange.getResponse().getStatusCode() != null ?
                exchange.getResponse().getStatusCode().value() : 0;

        // Log des métriques de performance
        if (duration.toMillis() > 1000) {
            logger.warn("Slow request: {} {} took {}ms",
                    request.getMethod(), path, duration.toMillis());
        }

        // Métriques pour monitoring
        io.micrometer.core.instrument.Metrics.counter("gateway.requests.total",
                        "method", request.getMethod().name(),
                        "path", path,
                        "status", String.valueOf(statusCode))
                .increment();

        io.micrometer.core.instrument.Metrics.timer("gateway.request.duration",
                        "method", request.getMethod().name(),
                        "path", path)
                .record(duration);
    }
}