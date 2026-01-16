package com.yowyob.gateway.filter;

import com.yowyob.common.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Filtre de gestion globale des erreurs
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Intercepte et gère toutes les exceptions non capturées
 *          Retourne des réponses d'erreur standardisées
 */
@Component
public class ErrorHandlingFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandlingFilter.class);

    private final ObjectMapper objectMapper;

    public ErrorHandlingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Exécute le filtre de gestion d'erreurs
     * 
     * @param exchange ServerWebExchange
     * @param chain    GatewayFilterChain
     * @return Mono<Void> avec la réponse d'erreur si nécessaire
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .onErrorResume(throwable -> {
                    log.error("Erreur non gérée dans le Gateway", throwable);
                    return handleException(exchange, throwable);
                });
    }

    /**
     * Gère une exception et retourne une réponse d'erreur
     * 
     * @param exchange  ServerWebExchange
     * @param throwable Exception
     * @return Mono<Void> avec la réponse d'erreur
     */
    private Mono<Void> handleException(ServerWebExchange exchange, Throwable throwable) {
        ServerHttpResponse response = exchange.getResponse();

        // Déterminer le statut HTTP selon le type d'exception
        HttpStatus status = determineHttpStatus(throwable);
        String errorCode = determineErrorCode(throwable);
        String message = determineErrorMessage(throwable);

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Construire la réponse d'erreur standardisée
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .errorCode(errorCode)
                .message(message)
                .path(exchange.getRequest().getPath().value())
                .timestamp(Instant.now())
                .correlationId(exchange.getRequest().getHeaders().getFirst("X-Correlation-Id"))
                .build();

        // Convertir en JSON
        // Convertir en JSON
        String jsonResponse;
        try {
            jsonResponse = objectMapper.writeValueAsString(errorResponse);
        } catch (JsonProcessingException e) {
            log.error("Erreur lors de la sérialisation de la réponse d'erreur", e);
            jsonResponse = "{\"error\": \"Internal Server Error\"}";
        }
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }

    /**
     * Détermine le statut HTTP selon le type d'exception
     * 
     * @param throwable Exception
     * @return HttpStatus approprié
     */
    private HttpStatus determineHttpStatus(Throwable throwable) {
        if (throwable instanceof com.yowyob.common.exception.UnauthorizedException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (throwable instanceof com.yowyob.common.exception.ForbiddenException) {
            return HttpStatus.FORBIDDEN;
        } else if (throwable instanceof com.yowyob.common.exception.ResourceNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (throwable instanceof com.yowyob.common.exception.BadRequestException) {
            return HttpStatus.BAD_REQUEST;
        } else if (throwable instanceof com.yowyob.common.exception.ConflictException) {
            return HttpStatus.CONFLICT;
        } else if (throwable instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        } else if (throwable instanceof java.util.concurrent.TimeoutException) {
            return HttpStatus.GATEWAY_TIMEOUT;
        } else if (throwable instanceof org.springframework.web.server.ResponseStatusException) {
            return HttpStatus.valueOf(
                    ((org.springframework.web.server.ResponseStatusException) throwable).getStatusCode().value());
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Détermine le code d'erreur
     * 
     * @param throwable Exception
     * @return Code d'erreur
     */
    private String determineErrorCode(Throwable throwable) {
        if (throwable instanceof com.yowyob.common.exception.AppException) {
            return ((com.yowyob.common.exception.AppException) throwable).getErrorCode();
        } else if (throwable instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
            return "CIRCUIT_BREAKER_OPEN";
        } else if (throwable instanceof java.util.concurrent.TimeoutException) {
            return "TIMEOUT";
        } else if (throwable instanceof org.springframework.web.server.ResponseStatusException) {
            return "HTTP_" + ((org.springframework.web.server.ResponseStatusException) throwable)
                    .getStatusCode().value();
        } else {
            return "INTERNAL_ERROR";
        }
    }

    /**
     * Détermine le message d'erreur
     * 
     * @param throwable Exception
     * @return Message d'erreur
     */
    private String determineErrorMessage(Throwable throwable) {
        if (throwable instanceof com.yowyob.common.exception.AppException) {
            return throwable.getMessage();
        } else if (throwable instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
            return "Service temporairement indisponible. Veuillez réessayer plus tard.";
        } else if (throwable instanceof java.util.concurrent.TimeoutException) {
            return "Délai d'attente dépassé. Le service met trop de temps à répondre.";
        } else if (throwable instanceof org.springframework.web.server.ResponseStatusException) {
            return throwable.getMessage();
        } else {
            return "Une erreur interne est survenue. Veuillez réessayer plus tard.";
        }
    }

    /**
     * Définit l'ordre d'exécution du filtre
     * 
     * @return Ordre bas pour s'exécuter en dernier
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}