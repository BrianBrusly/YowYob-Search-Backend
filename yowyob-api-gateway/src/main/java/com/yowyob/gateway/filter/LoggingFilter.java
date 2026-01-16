package com.yowyob.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

/**
 * Filtre de logging pour tracer toutes les requêtes et réponses
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Enregistre les détails des requêtes entrantes et des réponses
 *          sortantes
 *          Ajoute un ID de corrélation pour le tracing distribué
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoggingFilter.class);

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    /**
     * Exécute le filtre de logging
     *
     * @param exchange ServerWebExchange contenant la requête et la réponse
     * @param chain    GatewayFilterChain pour continuer le traitement
     * @return Mono<Void> pour le traitement asynchrone
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();

        // Générer des IDs uniques pour le tracing
        String correlationId = getOrGenerateCorrelationId(request);
        String requestId = UUID.randomUUID().toString();

        // Ajouter les IDs aux headers
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(CORRELATION_ID_HEADER, correlationId)
                .header(REQUEST_ID_HEADER, requestId)
                .build();

        // Ajouter au MDC pour les logs structurés
        MDC.put("correlationId", correlationId);
        MDC.put("requestId", requestId);
        MDC.put("method", request.getMethod().name());
        MDC.put("path", request.getPath().value());
        MDC.put("remoteAddress",
                request.getRemoteAddress().getAddress().getHostAddress());

        // Logger la requête entrante
        logRequest(request, correlationId, requestId);

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doOnSuccess(aVoid -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logResponse(exchange, correlationId, requestId, duration, null);
                    cleanupMDC();
                })
                .doOnError(throwable -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logResponse(exchange, correlationId, requestId, duration, throwable);
                    cleanupMDC();
                });
    }

    /**
     * Log les détails de la requête entrante
     *
     * @param request       Requête HTTP
     * @param correlationId ID de corrélation
     * @param requestId     ID de la requête
     */
    private void logRequest(ServerHttpRequest request, String correlationId, String requestId) {
        log.info("Requête entrante - ID: {}, Corrélation: {}, Méthode: {}, Chemin: {}, "
                + "Client: {}, User-Agent: {}, Headers: {}",
                requestId,
                correlationId,
                request.getMethod(),
                request.getPath(),
                request.getRemoteAddress(),
                request.getHeaders().getFirst("User-Agent"),
                filterSensitiveHeaders(request.getHeaders()));
    }

    /**
     * Log les détails de la réponse sortante
     *
     * @param exchange      ServerWebExchange
     * @param correlationId ID de corrélation
     * @param requestId     ID de la requête
     * @param duration      Durée du traitement en ms
     * @param throwable     Exception si erreur
     */
    private void logResponse(ServerWebExchange exchange,
            String correlationId,
            String requestId,
            long duration,
            Throwable throwable) {

        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        HttpStatusCode status = exchange.getResponse().getStatusCode();

        if (throwable != null) {
            log.error("Requête échouée - ID: {}, Corrélation: {}, Utilisateur: {}, "
                    + "Durée: {}ms, Statut: {}, Erreur: {}",
                    requestId, correlationId, userId, duration, status, throwable.getMessage(),
                    throwable);
        } else if (status != null && status.is5xxServerError()) {
            log.error("Erreur serveur - ID: {}, Corrélation: {}, Utilisateur: {}, "
                    + "Durée: {}ms, Statut: {}",
                    requestId, correlationId, userId, duration, status);
        } else if (status != null && status.is4xxClientError()) {
            log.warn("Erreur client - ID: {}, Corrélation: {}, Utilisateur: {}, "
                    + "Durée: {}ms, Statut: {}",
                    requestId, correlationId, userId, duration, status);
        } else {
            log.info("Requête réussie - ID: {}, Corrélation: {}, Utilisateur: {}, "
                    + "Durée: {}ms, Statut: {}",
                    requestId, correlationId, userId, duration, status);
        }
    }

    /**
     * Récupère ou génère un ID de corrélation
     *
     * @param request Requête HTTP
     * @return ID de corrélation
     */
    private String getOrGenerateCorrelationId(ServerHttpRequest request) {
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        return correlationId != null ? correlationId : UUID.randomUUID().toString();
    }

    /**
     * Filtre les headers sensibles pour les logs
     *
     * @param headers Headers HTTP
     * @return Map filtrée des headers
     */
    private Map<String, List<String>> filterSensitiveHeaders(HttpHeaders headers) {
        Map<String, List<String>> filteredHeaders = new HashMap<>(headers);

        // Supprimer les headers sensibles
        filteredHeaders.remove("Authorization");
        filteredHeaders.remove("Cookie");
        filteredHeaders.remove("Set-Cookie");

        // Anonymiser certains headers
        if (filteredHeaders.containsKey("X-User-Id")) {
            filteredHeaders.put("X-User-Id", List.of("[REDACTED]"));
        }

        return filteredHeaders;
    }

    /**
     * Nettoie le MDC après traitement
     */
    private void cleanupMDC() {
        MDC.remove("correlationId");
        MDC.remove("requestId");
        MDC.remove("method");
        MDC.remove("path");
        MDC.remove("remoteAddress");
        MDC.remove("userId");
    }

    /**
     * Définit l'ordre d'exécution du filtre
     *
     * @return Ordre bas pour s'exécuter tôt
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}