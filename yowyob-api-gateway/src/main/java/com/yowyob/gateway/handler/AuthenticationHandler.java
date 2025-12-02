package com.yowyob.gateway.handler;

import com.yowyob.gateway.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * Handler pour les opérations d'authentification
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Gère les endpoints d'authentification comme la validation de token,
 * le refresh, et la révocation
 */
@Component
public class AuthenticationHandler {

    private final JwtUtil jwtUtil;

    /**
     * Constructeur avec injection de JwtUtil
     */
    public AuthenticationHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Valide un token JWT
     */
    public Mono<ServerResponse> validateToken(ServerRequest request) {
        return request.bodyToMono(Map.class)
                .flatMap(body -> {
                    String token = (String) body.get("token");

                    if (token == null || token.isEmpty()) {
                        return badRequest("Token manquant");
                    }

                    try {
                        boolean isValid = jwtUtil.validateToken(token);
                        String username = jwtUtil.extractUsername(token);

                        return ServerResponse.ok()
                                .bodyValue(Map.of(
                                        "valid", isValid,
                                        "username", username,
                                        "expiresAt", jwtUtil.extractExpiration(token),
                                        "issuedAt", jwtUtil.extractIssuedAt(token)
                                ));
                    } catch (Exception e) {
                        return ServerResponse.ok()
                                .bodyValue(Map.of(
                                        "valid", false,
                                        "error", e.getMessage(),
                                        "timestamp", Instant.now().toString()
                                ));
                    }
                })
                .switchIfEmpty(badRequest("Body manquant"));
    }

    /**
     * Rafraîchit un token JWT
     */
    public Mono<ServerResponse> refreshToken(ServerRequest request) {
        return request.bodyToMono(Map.class)
                .flatMap(body -> {
                    String refreshToken = (String) body.get("refreshToken");

                    if (refreshToken == null || refreshToken.isEmpty()) {
                        return badRequest("Refresh token manquant");
                    }

                    try {
                        // Valider le refresh token
                        if (!jwtUtil.validateToken(refreshToken)) {
                            return unauthorized("Refresh token invalide ou expiré");
                        }

                        // Extraire les informations utilisateur
                        String username = jwtUtil.extractUsername(refreshToken);
                        String userId = jwtUtil.extractUserId(refreshToken);

                        // Générer un nouveau token d'accès
                        String newAccessToken = jwtUtil.generateToken(username, userId);

                        return ServerResponse.ok()
                                .bodyValue(Map.of(
                                        "accessToken", newAccessToken,
                                        "tokenType", "Bearer",
                                        "expiresIn", 900, // 15 minutes en secondes
                                        "refreshToken", refreshToken,
                                        "timestamp", Instant.now().toString()
                                ));
                    } catch (Exception e) {
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue(Map.of(
                                        "error", "Erreur lors du refresh du token",
                                        "message", e.getMessage(),
                                        "timestamp", Instant.now().toString()
                                ));
                    }
                });
    }

    /**
     * Révoque un token JWT
     */
    public Mono<ServerResponse> revokeToken(ServerRequest request) {
        return request.bodyToMono(Map.class)
                .flatMap(body -> {
                    String token = (String) body.get("token");

                    if (token == null || token.isEmpty()) {
                        return badRequest("Token manquant");
                    }

                    try {
                        // Ajouter le token à la blacklist
                        jwtUtil.blacklistToken(token);

                        return ServerResponse.ok()
                                .bodyValue(Map.of(
                                        "success", true,
                                        "message", "Token révoqué avec succès",
                                        "timestamp", Instant.now().toString()
                                ));
                    } catch (Exception e) {
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue(Map.of(
                                        "error", "Erreur lors de la révocation du token",
                                        "message", e.getMessage(),
                                        "timestamp", Instant.now().toString()
                                ));
                    }
                });
    }

    /**
     * Obtient les informations utilisateur depuis un token
     */
    public Mono<ServerResponse> getUserInfo(ServerRequest request) {
        return request.headers().header("Authorization").stream()
                .findFirst()
                .map(authHeader -> {
                    if (!authHeader.startsWith("Bearer ")) {
                        return Mono.just(badRequest("Format d'authentification invalide"));
                    }

                    String token = authHeader.substring(7);

                    try {
                        String username = jwtUtil.extractUsername(token);
                        String userId = jwtUtil.extractUserId(token);

                        return ServerResponse.ok()
                                .bodyValue(Map.of(
                                        "username", username,
                                        "userId", userId,
                                        "authenticated", true,
                                        "tokenValid", jwtUtil.validateToken(token),
                                        "timestamp", Instant.now().toString()
                                ));
                    } catch (Exception e) {
                        return ServerResponse.ok()
                                .bodyValue(Map.of(
                                        "authenticated", false,
                                        "error", e.getMessage(),
                                        "timestamp", Instant.now().toString()
                                ));
                    }
                })
                .orElse(badRequest("Header Authorization manquant"));
    }

    /**
     * Health check de l'authentification
     */
    public Mono<ServerResponse> healthCheck(ServerRequest request) {
        return ServerResponse.ok()
                .bodyValue(Map.of(
                        "service", "authentication",
                        "status", "UP",
                        "jwtConfigured", jwtUtil.isConfigured(),
                        "timestamp", Instant.now().toString(),
                        "version", "1.0.0"
                ));
    }

    /**
     * Helper pour les réponses Bad Request
     */
    private Mono<ServerResponse> badRequest(String message) {
        return ServerResponse.badRequest()
                .bodyValue(Map.of(
                        "error", "bad_request",
                        "message", message,
                        "timestamp", Instant.now().toString()
                ));
    }

    /**
     * Helper pour les réponses Unauthorized
     */
    private Mono<ServerResponse> unauthorized(String message) {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .bodyValue(Map.of(
                        "error", "unauthorized",
                        "message", message,
                        "timestamp", Instant.now().toString()
                ));
    }

    /**
     * Endpoint pour les métriques d'authentification
     */
    public Mono<ServerResponse> getMetrics(ServerRequest request) {
        // Récupérer les métriques d'authentification
        Map<String, Object> metrics = Map.of(
                "tokensGenerated", jwtUtil.getTokensGeneratedCount(),
                "tokensValidated", jwtUtil.getTokensValidatedCount(),
                "tokensBlacklisted", jwtUtil.getBlacklistedTokensCount(),
                "activeTokens", jwtUtil.getActiveTokensCount(),
                "timestamp", Instant.now().toString()
        );

        return ServerResponse.ok()
                .bodyValue(metrics);
    }
}