package com.yowyob.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yowyob.common.dto.ErrorResponse;
import com.yowyob.common.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

/**
 * Gestionnaire global d'erreurs pour le Gateway
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Intercepte toutes les exceptions non gérées et retourne des réponses
 *          standardisées
 *          S'exécute avant le gestionnaire d'erreurs par défaut de Spring
 */
@Component
@Order(-2) // S'exécute avant le gestionnaire d'erreurs par défaut (-1)
public class GlobalErrorHandler extends AbstractErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);

    private final ObjectMapper objectMapper;

    /**
     * Constructeur
     */
    public GlobalErrorHandler(ErrorAttributes errorAttributes,
            WebProperties.Resources resources,
            ApplicationContext applicationContext,
            ServerCodecConfigurer serverCodecConfigurer,
            ObjectMapper objectMapper) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
        this.setMessageReaders(serverCodecConfigurer.getReaders());
        this.objectMapper = objectMapper;
    }

    /**
     * Définit le routage des erreurs
     * 
     * @param errorAttributes ErrorAttributes
     * @return RouterFunction pour le routage des erreurs
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * Rend la réponse d'erreur
     * 
     * @param request ServerRequest
     * @return Mono<ServerResponse> avec la réponse d'erreur
     */
    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
                ErrorAttributeOptions.of(
                        ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.EXCEPTION,
                        ErrorAttributeOptions.Include.BINDING_ERRORS));

        Throwable error = getError(request);
        HttpStatus status = determineHttpStatus(error);
        String errorCode = determineErrorCode(error);
        String message = determineErrorMessage(error, errorPropertiesMap);
        String path = (String) errorPropertiesMap.getOrDefault("path", request.path());

        // Logger l'erreur avec le contexte approprié
        logError(error, request, status, path);

        // Construire la réponse d'erreur standardisée
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .timestamp(Instant.now())
                .correlationId(request.headers().firstHeader("X-Correlation-Id"))
                .details(errorPropertiesMap)
                .build();

        // Retourner la réponse avec le statut HTTP approprié
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorResponse));
    }

    /**
     * Détermine le statut HTTP selon le type d'exception
     * 
     * @param error Exception
     * @return HttpStatus approprié
     */
    private HttpStatus determineHttpStatus(Throwable error) {
        if (error instanceof AppException) {
            return ((AppException) error).getHttpStatus();
        } else if (error instanceof UnauthorizedException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (error instanceof ForbiddenException) {
            return HttpStatus.FORBIDDEN;
        } else if (error instanceof BadRequestException) {
            return HttpStatus.BAD_REQUEST;
        } else if (error instanceof ValidationException) {
            return HttpStatus.BAD_REQUEST;
        } else if (error instanceof ConflictException) {
            return HttpStatus.CONFLICT;
        } else if (error instanceof ServiceUnavailableException) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        } else if (error instanceof RateLimitException) {
            return HttpStatus.TOO_MANY_REQUESTS;
        } else if (error instanceof org.springframework.security.access.AccessDeniedException) {
            return HttpStatus.FORBIDDEN;
        } else if (error instanceof org.springframework.security.core.AuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (error instanceof ResponseStatusException) {
            return HttpStatus.valueOf(((ResponseStatusException) error).getStatusCode().value());
        } else if (error instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        } else if (error instanceof java.util.concurrent.TimeoutException) {
            return HttpStatus.GATEWAY_TIMEOUT;
        } else if (error instanceof org.springframework.web.server.ServerWebInputException) {
            return HttpStatus.BAD_REQUEST;
        } else if (error instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
            org.springframework.web.reactive.function.client.WebClientResponseException webClientEx = (org.springframework.web.reactive.function.client.WebClientResponseException) error;
            return HttpStatus.valueOf(webClientEx.getStatusCode().value());
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Détermine le code d'erreur
     * 
     * @param error Exception
     * @return Code d'erreur
     */
    private String determineErrorCode(Throwable error) {
        if (error instanceof AppException) {
            return ((AppException) error).getErrorCode();
        } else if (error instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
            return "CIRCUIT_BREAKER_OPEN";
        } else if (error instanceof java.util.concurrent.TimeoutException) {
            return "TIMEOUT";
        } else if (error instanceof ResponseStatusException) {
            return "HTTP_" + ((ResponseStatusException) error).getStatusCode().value();
        } else if (error instanceof org.springframework.web.server.ServerWebInputException) {
            return "BAD_REQUEST";
        } else if (error instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
            org.springframework.web.reactive.function.client.WebClientResponseException webClientEx = (org.springframework.web.reactive.function.client.WebClientResponseException) error;
            return "BACKEND_" + webClientEx.getStatusCode().value();
        } else if (error instanceof org.springframework.security.access.AccessDeniedException) {
            return "ACCESS_DENIED";
        } else if (error instanceof org.springframework.security.core.AuthenticationException) {
            return "AUTHENTICATION_FAILED";
        } else {
            return "INTERNAL_ERROR";
        }
    }

    /**
     * Détermine le message d'erreur
     * 
     * @param error              Exception
     * @param errorPropertiesMap Map des propriétés d'erreur
     * @return Message d'erreur
     */
    private String determineErrorMessage(Throwable error, Map<String, Object> errorPropertiesMap) {
        if (error instanceof AppException) {
            return error.getMessage();
        } else if (error instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
            return "Service temporairement indisponible. Veuillez réessayer plus tard.";
        } else if (error instanceof java.util.concurrent.TimeoutException) {
            return "Délai d'attente dépassé. Le service met trop de temps à répondre.";
        } else if (error instanceof ResponseStatusException) {
            return error.getMessage();
        } else if (error instanceof org.springframework.web.server.ServerWebInputException) {
            return "Requête invalide. Vérifiez les données envoyées.";
        } else if (error instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
            org.springframework.web.reactive.function.client.WebClientResponseException webClientEx = (org.springframework.web.reactive.function.client.WebClientResponseException) error;

            if (webClientEx.getStatusCode().is5xxServerError()) {
                return "Erreur interne du service backend. Veuillez réessayer plus tard.";
            } else if (webClientEx.getStatusCode().is4xxClientError()) {
                return "Erreur de la requête vers le service backend.";
            }
        } else if (error instanceof org.springframework.security.access.AccessDeniedException) {
            return "Accès refusé. Vous n'avez pas les permissions nécessaires.";
        } else if (error instanceof org.springframework.security.core.AuthenticationException) {
            return "Authentification requise. Veuillez vous connecter.";
        } else {
            String message = (String) errorPropertiesMap.getOrDefault("message",
                    "Une erreur interne est survenue. Veuillez réessayer plus tard.");

            // Ne pas exposer les détails techniques en production pour les erreurs internes
            if (message.contains("Internal Server Error") ||
                    message.contains("java.") ||
                    message.contains("org.springframework")) {
                return "Une erreur interne est survenue. Veuillez réessayer plus tard.";
            }

            return message;
        }

        return "Une erreur est survenue.";
    }

    /**
     * Log l'erreur avec le contexte
     * 
     * @param error   Exception
     * @param request ServerRequest
     * @param status  HttpStatus
     * @param path    Chemin de la requête
     */
    private void logError(Throwable error, ServerRequest request, HttpStatus status, String path) {
        String correlationId = request.headers().firstHeader("X-Correlation-Id");
        String requestId = request.headers().firstHeader("X-Request-Id");
        String method = request.method().name();
        String query = request.uri().getQuery();
        String userAgent = request.headers().firstHeader("User-Agent");
        String clientIp = request.headers().firstHeader("X-Forwarded-For");

        if (clientIp == null) {
            clientIp = request.headers().firstHeader("X-Real-IP");
        }

        String logMessage = String.format(
                "Erreur %s - Correlation: %s, Request: %s, %s %s%s, Client: %s, User-Agent: %s",
                status.value(),
                correlationId != null ? correlationId : "N/A",
                requestId != null ? requestId : "N/A",
                method,
                path,
                query != null ? "?" + query : "",
                clientIp != null ? clientIp : "N/A",
                userAgent != null ? userAgent : "N/A");

        // Log différent selon le type d'erreur
        if (status.is5xxServerError()) {
            if (error instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
                log.warn("{} - Circuit Breaker ouvert: {}", logMessage, error.getMessage());
            } else if (error instanceof java.util.concurrent.TimeoutException) {
                log.warn("{} - Timeout: {}", logMessage, error.getMessage());
            } else {
                log.error("{} - Erreur serveur: {}", logMessage, error.getMessage(), error);
            }
        } else if (status.is4xxClientError()) {
            if (status == HttpStatus.UNAUTHORIZED) {
                log.warn("{} - Non autorisé: {}", logMessage, error.getMessage());
            } else if (status == HttpStatus.FORBIDDEN) {
                log.warn("{} - Accès interdit: {}", logMessage, error.getMessage());
            } else if (status == HttpStatus.NOT_FOUND) {
                log.debug("{} - Non trouvé: {}", logMessage, error.getMessage());
            } else if (status == HttpStatus.TOO_MANY_REQUESTS) {
                log.warn("{} - Rate limit dépassé: {}", logMessage, error.getMessage());
            } else {
                log.warn("{} - Erreur client: {}", logMessage, error.getMessage());
            }
        } else {
            log.debug("{} - Erreur: {}", logMessage, error.getMessage());
        }
    }

    /**
     * Méthode utilitaire pour créer une réponse d'erreur en cas d'échec de
     * sérialisation
     * 
     * @param status            HttpStatus
     * @param message           Message d'erreur
     * @param dataBufferFactory DataBufferFactory
     * @return DataBuffer avec l'erreur
     */
    private DataBuffer createErrorBuffer(HttpStatus status, String message,
            DataBufferFactory dataBufferFactory) {
        try {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(status.value())
                    .error(status.getReasonPhrase())
                    .message(message)
                    .timestamp(Instant.now())
                    .build();

            String json = objectMapper.writeValueAsString(errorResponse);
            return dataBufferFactory.wrap(json.getBytes(StandardCharsets.UTF_8));

        } catch (JsonProcessingException e) {
            // En cas d'échec de sérialisation JSON, retourner une réponse simple
            String simpleJson = String.format(
                    "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                    status.value(),
                    "Internal Server Error",
                    "Failed to serialize error response");
            return dataBufferFactory.wrap(simpleJson.getBytes(StandardCharsets.UTF_8));
        }
    }
}