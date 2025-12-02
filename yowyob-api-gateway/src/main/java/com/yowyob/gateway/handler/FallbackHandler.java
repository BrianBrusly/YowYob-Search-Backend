package com.yowyob.gateway.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * Handler pour les fallbacks et erreurs de l'API Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Gère les réponses de fallback lorsque les services backend sont indisponibles
 * ou lorsque des erreurs spécifiques se produisent
 */
@Component
public class FallbackHandler {

    /**
     * Fallback pour le Search Service
     */
    public Mono<ServerResponse> searchServiceFallback(ServerRequest request) {
        return createFallbackResponse(
                "search_service_unavailable",
                "Le service de recherche est temporairement indisponible. Veuillez réessayer dans quelques instants.",
                "Nous travaillons à résoudre le problème. En attendant, vous pouvez essayer de rafraîchir la page ou réessayer plus tard.",
                HttpStatus.SERVICE_UNAVAILABLE,
                Map.of(
                        "estimatedRecoveryTime", "5 minutes",
                        "service", "search",
                        "alternative", "Vous pouvez essayer une recherche plus générale ou utiliser les suggestions de recherche."
                )
        );
    }

    /**
     * Fallback pour le Shop Service
     */
    public Mono<ServerResponse> shopServiceFallback(ServerRequest request) {
        return createFallbackResponse(
                "shop_service_unavailable",
                "Le service e-commerce est temporairement indisponible.",
                "Nous ne pouvons pas afficher les produits pour le moment. Le service sera rétabli sous peu.",
                HttpStatus.SERVICE_UNAVAILABLE,
                Map.of(
                        "estimatedRecoveryTime", "10 minutes",
                        "service", "shop",
                        "alternative", "Vous pouvez continuer à naviguer sur le site et réessayer plus tard."
                )
        );
    }

    /**
     * Fallback pour le User Service
     */
    public Mono<ServerResponse> userServiceFallback(ServerRequest request) {
        return createFallbackResponse(
                "user_service_unavailable",
                "Le service d'authentification est temporairement indisponible.",
                "Vous ne pouvez pas vous connecter ou créer un compte pour le moment.",
                HttpStatus.SERVICE_UNAVAILABLE,
                Map.of(
                        "estimatedRecoveryTime", "3 minutes",
                        "service", "user",
                        "alternative", "Vous pouvez continuer à utiliser les fonctionnalités publiques du site."
                )
        );
    }

    /**
     * Fallback générique pour tous les services
     */
    public Mono<ServerResponse> genericFallback(ServerRequest request) {
        return createFallbackResponse(
                "service_unavailable",
                "Le service demandé est temporairement indisponible.",
                "Nous rencontrons des difficultés techniques. Notre équipe travaille à résoudre le problème.",
                HttpStatus.SERVICE_UNAVAILABLE,
                Map.of(
                        "estimatedRecoveryTime", "15 minutes",
                        "supportContact", "support@yowyob.com",
                        "statusPage", "https://status.yowyob.com"
                )
        );
    }

    /**
     * Fallback pour les timeouts
     */
    public Mono<ServerResponse> timeoutFallback(ServerRequest request) {
        return createFallbackResponse(
                "request_timeout",
                "La requête a expiré.",
                "Le service met trop de temps à répondre. Veuillez réessayer avec une requête plus simple.",
                HttpStatus.GATEWAY_TIMEOUT,
                Map.of(
                        "timeout", "10 seconds",
                        "suggestion", "Essayez de réduire le nombre de filtres ou la complexité de votre requête."
                )
        );
    }

    /**
     * Fallback pour les erreurs de circuit breaker
     */
    public Mono<ServerResponse> circuitBreakerFallback(ServerRequest request) {
        String serviceName = request.path().substring(request.path().lastIndexOf('/') + 1);

        return createFallbackResponse(
                "circuit_breaker_open",
                "Le service " + serviceName + " est actuellement protégé par le circuit breaker.",
                "Trop d'erreurs ont été détectées. Le service est temporairement en mode dégradé.",
                HttpStatus.SERVICE_UNAVAILABLE,
                Map.of(
                        "service", serviceName,
                        "circuitState", "OPEN",
                        "recoveryEstimate", "30 seconds",
                        "recommendation", "Veuillez patienter quelques instants avant de réessayer."
                )
        );
    }

    /**
     * Crée une réponse de fallback standardisée
     */
    private Mono<ServerResponse> createFallbackResponse(
            String errorCode,
            String message,
            String details,
            HttpStatus status,
            Map<String, Object> metadata) {

        Map<String, Object> response = Map.of(
                "error", errorCode,
                "message", message,
                "details", details,
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "metadata", metadata,
                "documentation", "https://docs.yowyob.com/errors/" + errorCode,
                "support", Map.of(
                        "email", "support@yowyob.com",
                        "phone", "+237 XXX XXX XXX",
                        "hours", "Lundi - Vendredi, 9h - 18h"
                )
        );

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Fallback", "true")
                .header("X-Error-Code", errorCode)
                .header("Retry-After", "60") // Réessayer après 60 secondes
                .bodyValue(response);
    }

    /**
     * Handler pour les erreurs 404 (Not Found)
     */
    public Mono<ServerResponse> notFound(ServerRequest request) {
        Map<String, Object> response = Map.of(
                "error", "endpoint_not_found",
                "message", "L'endpoint demandé n'existe pas.",
                "requestedPath", request.path(),
                "timestamp", Instant.now().toString(),
                "status", 404,
                "suggestions", Map.of(
                        "checkSpelling", "Vérifiez l'orthographe de l'URL",
                        "apiDocs", "Consultez la documentation à https://docs.yowyob.com/api",
                        "supportedEndpoints", Map.of(
                                "search", "/api/search",
                                "auth", "/api/auth",
                                "geo", "/api/geo",
                                "shop", "/api/shop"
                        )
                )
        );

        return ServerResponse.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }

    /**
     * Handler pour les erreurs 429 (Too Many Requests)
     */
    public Mono<ServerResponse> tooManyRequests(ServerRequest request) {
        Map<String, Object> response = Map.of(
                "error", "rate_limit_exceeded",
                "message", "Vous avez dépassé la limite de requêtes autorisée.",
                "timestamp", Instant.now().toString(),
                "status", 429,
                "limits", Map.of(
                        "requestsPerMinute", 100,
                        "burstCapacity", 20,
                        "windowSeconds", 60
                ),
                "recommendations", Map.of(
                        "wait", "Attendez 60 secondes avant de réessayer",
                        "optimize", "Optimisez vos requêtes pour réduire le nombre d'appels",
                        "contact", "Contactez-nous si vous avez besoin de limites plus élevées"
                )
        );

        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Retry-After", "60")
                .bodyValue(response);
    }

    /**
     * Handler pour les erreurs 503 (Service Unavailable)
     */
    public Mono<ServerResponse> serviceUnavailable(ServerRequest request) {
        Map<String, Object> response = Map.of(
                "error", "service_unavailable",
                "message", "Le service est temporairement indisponible pour maintenance.",
                "timestamp", Instant.now().toString(),
                "status", 503,
                "maintenance", Map.of(
                        "scheduled", false,
                        "estimatedCompletion", Instant.now().plusSeconds(1800).toString(),
                        "progress", "70%"
                ),
                "statusPage", "https://status.yowyob.com",
                "updates", Map.of(
                        "twitter", "@YowYobStatus",
                        "email", "status@yowyob.com"
                )
        );

        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Retry-After", "300") // 5 minutes
                .bodyValue(response);
    }

}

