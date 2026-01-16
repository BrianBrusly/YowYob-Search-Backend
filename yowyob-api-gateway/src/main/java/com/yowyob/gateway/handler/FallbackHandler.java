package com.yowyob.gateway.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

/**
 * Gestionnaire de fallback pour les Circuit Breakers
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Gère les requêtes de fallback quand les Circuit Breakers sont
 *          ouverts
 *          Peut servir des réponses depuis le cache ou des réponses statiques
 */
@Component
public class FallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(FallbackHandler.class);

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public FallbackHandler(ReactiveRedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Gère une requête de fallback
     * 
     * @param request ServerRequest
     * @return ServerResponse avec la réponse de fallback
     */
    public Mono<ServerResponse> handleFallback(ServerRequest request) {
        String service = request.pathVariable("service");
        String fallbackKey = "fallback:" + service + ":" + request.path();

        log.warn("Fallback activé pour le service: {}, chemin: {}",
                service, request.path());

        // Essayer de récupérer depuis le cache
        return redisTemplate.opsForValue().get(fallbackKey)
                .flatMap(cachedResponse -> {
                    log.debug("Fallback depuis le cache pour: {}", fallbackKey);
                    return serveCachedFallback(cachedResponse);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Fallback statique pour le service: {}", service);
                    return serveStaticFallback(service, request);
                }));
    }

    /**
     * Sert un fallback depuis le cache
     * 
     * @param cachedResponse Réponse mise en cache
     * @return ServerResponse
     */
    private Mono<ServerResponse> serveCachedFallback(String cachedResponse) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> responseData = objectMapper.readValue(cachedResponse, Map.class);

            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Fallback", "cached")
                    .header("X-Cache-Status", "FALLBACK_HIT")
                    .bodyValue(responseData);

        } catch (Exception e) {
            log.error("Erreur lors du traitement du fallback caché", e);
            return serveGenericFallback();
        }
    }

    /**
     * Sert un fallback statique
     * 
     * @param service Nom du service
     * @param request ServerRequest
     * @return ServerResponse
     */
    private Mono<ServerResponse> serveStaticFallback(String service, ServerRequest request) {
        String fallbackResponse;

        switch (service.toLowerCase()) {
            case "search":
                fallbackResponse = getSearchFallbackResponse();
                break;
            case "user":
                fallbackResponse = getUserFallbackResponse();
                break;
            case "geo":
                fallbackResponse = getGeoFallbackResponse();
                break;
            case "shop":
                fallbackResponse = getShopFallbackResponse();
                break;
            default:
                fallbackResponse = getGenericFallbackResponse();
        }

        // Mettre en cache la réponse de fallback
        cacheFallbackResponse("fallback:" + service + ":" + request.path(), fallbackResponse);

        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Fallback", "static")
                .header("Retry-After", "30")
                .bodyValue(parseJson(fallbackResponse));
    }

    /**
     * Met en cache une réponse de fallback
     * 
     * @param key      Clé de cache
     * @param response Réponse à mettre en cache
     */
    private void cacheFallbackResponse(String key, String response) {
        redisTemplate.opsForValue()
                .set(key, response, java.time.Duration.ofMinutes(5))
                .subscribe(
                        success -> log.debug("Fallback mis en cache: {}", key),
                        error -> log.error("Erreur lors de la mise en cache du fallback", error));
    }

    /**
     * Parse une chaîne JSON en Map
     * 
     * @param json Chaîne JSON
     * @return Map des données
     */
    private Map<String, Object> parseJson(String json) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = objectMapper.readValue(json, Map.class);
            return result;
        } catch (Exception e) {
            log.error("Erreur lors du parsing JSON", e);
            return Map.of(
                    "error", "Failed to parse fallback response",
                    "timestamp", Instant.now().toString());
        }
    }

    /**
     * Génère une réponse de fallback pour la recherche
     * 
     * @return JSON de fallback
     */
    private String getSearchFallbackResponse() {
        return """
                {
                    "status": "degraded",
                    "message": "Le service de recherche est temporairement indisponible.",
                    "suggestions": [
                        "Réessayez dans quelques instants",
                        "Utilisez des termes de recherche plus simples",
                        "Consultez l'historique de vos recherches"
                    ],
                    "timestamp": "%s",
                    "fallback": true,
                    "service": "search"
                }
                """.formatted(Instant.now().toString());
    }

    /**
     * Génère une réponse de fallback pour les utilisateurs
     * 
     * @return JSON de fallback
     */
    private String getUserFallbackResponse() {
        return """
                {
                    "status": "degraded",
                    "message": "Le service utilisateur est temporairement indisponible.",
                    "instructions": "Vos données sont en sécurité. Vous pouvez continuer à utiliser l'application en mode hors ligne.",
                    "timestamp": "%s",
                    "fallback": true,
                    "service": "user"
                }
                """
                .formatted(Instant.now().toString());
    }

    /**
     * Génère une réponse de fallback pour la géolocalisation
     * 
     * @return JSON de fallback
     */
    private String getGeoFallbackResponse() {
        return """
                {
                    "status": "degraded",
                    "message": "Le service de géolocalisation est temporairement indisponible.",
                    "alternative": "Vous pouvez saisir votre adresse manuellement.",
                    "default_location": {
                        "latitude": 3.8480,
                        "longitude": 11.5021,
                        "city": "Yaoundé",
                        "country": "Cameroun"
                    },
                    "timestamp": "%s",
                    "fallback": true,
                    "service": "geo"
                }
                """.formatted(Instant.now().toString());
    }

    /**
     * Génère une réponse de fallback pour les boutiques
     * 
     * @return JSON de fallback
     */
    private String getShopFallbackResponse() {
        return """
                {
                    "status": "degraded",
                    "message": "Le service boutique est temporairement indisponible.",
                    "instructions": "Les prix affichés peuvent ne pas être à jour.",
                    "timestamp": "%s",
                    "fallback": true,
                    "service": "shop"
                }
                """.formatted(Instant.now().toString());
    }

    /**
     * Génère une réponse de fallback générique
     * 
     * @return JSON de fallback
     */
    private String getGenericFallbackResponse() {
        return """
                {
                    "status": "service_unavailable",
                    "message": "Le service est temporairement indisponible.",
                    "error_code": "SERVICE_UNAVAILABLE",
                    "timestamp": "%s",
                    "retry_after": 30,
                    "fallback": true
                }
                """.formatted(Instant.now().toString());
    }

    /**
     * Sert un fallback générique
     * 
     * @return ServerResponse
     */
    private Mono<ServerResponse> serveGenericFallback() {
        String fallbackResponse = getGenericFallbackResponse();

        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Fallback", "generic")
                .header("Retry-After", "30")
                .bodyValue(parseJson(fallbackResponse));
    }
}