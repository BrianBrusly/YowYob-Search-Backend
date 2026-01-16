package com.yowyob.gateway.filter;

import com.yowyob.common.security.jwt.JwtService;
import com.yowyob.common.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.Instant;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.core.io.buffer.DataBuffer;
import com.yowyob.common.dto.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

/**
 * Filtre global pour l'authentification JWT
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Valide les tokens JWT et extrait les informations utilisateur
 *          S'exécute tôt dans la chaîne de filtres pour protéger les endpoints
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    // Endpoints publics qui ne nécessitent pas d'authentification
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/actuator/health",
            "/actuator/info",
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/search",
            "/api/geo",
            "/api/shop");

    /**
     * Exécute le filtre d'authentification JWT
     *
     * @param exchange ServerWebExchange contenant la requête et la réponse
     * @param chain    GatewayFilterChain pour continuer le traitement
     * @return Mono<Void> pour le traitement asynchrone
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Vérifier si le chemin est public
        if (isPublicPath(path)) {
            log.debug("Path public: {} - pas d'authentification requise", path);
            return chain.filter(exchange);
        }

        // Extraire le token du header Authorization
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Token JWT manquant ou mal formé pour le chemin: {}", path);
            return unauthorizedResponse(exchange, "Token d'authentification manquant");
        }

        String token = authHeader.substring(7); // Enlever "Bearer "

        try {
            // Valider le token avec le service JWT du module common
            if (!jwtService.validateToken(token)) {
                log.warn("Token JWT invalide pour le chemin: {}", path);
                return unauthorizedResponse(exchange, "Token d'authentification invalide");
            }

            // Extraire les informations utilisateur
            String userId = jwtService.extractUserId(token);
            Set<String> roles = jwtService.extractRoles(token);

            log.debug("Utilisateur authentifié: {} avec rôles: {} pour le chemin: {}",
                    userId, roles, path);

            // Ajouter les informations utilisateur aux headers
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Roles", String.join(",", roles))
                    .header("X-Authenticated", "true")
                    .build();

            // Continuer avec la requête modifiée
            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expiré pour le chemin: {}", path);
            return unauthorizedResponse(exchange, "Token d'authentification expiré");
        } catch (JwtException e) {
            log.error("Erreur lors de la validation du token JWT pour le chemin: {}", path, e);
            return unauthorizedResponse(exchange, "Erreur d'authentification");
        }
    }

    /**
     * Vérifie si un chemin est dans la liste des chemins publics
     *
     * @param path Chemin à vérifier
     * @return true si le chemin est public
     */
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(publicPath -> path.startsWith(publicPath) ||
                        path.matches(publicPath.replace("**", ".*")));
    }

    /**
     * Retourne une réponse HTTP 401 Unauthorized
     *
     * @param exchange ServerWebExchange
     * @param message  Message d'erreur
     * @return Mono<Void> avec la réponse d'erreur
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message(message)
                .path(exchange.getRequest().getPath().value())
                .timestamp(Instant.now())
                .build();

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsString(errorResponse).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            bytes = "{}".getBytes(StandardCharsets.UTF_8);
        }
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    /**
     * Définit l'ordre d'exécution du filtre
     *
     * @return Ordre élevé pour s'exécuter tôt
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}