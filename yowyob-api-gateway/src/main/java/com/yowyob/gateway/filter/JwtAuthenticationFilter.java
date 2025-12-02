package com.yowyob.gateway.filter;

import com.yowyob.gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Filtre d'authentification JWT pour l'API Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Vérifie et valide les tokens JWT pour les requêtes authentifiées
 * Extrait les informations utilisateur et les ajoute aux headers
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    /**
     * Constructeur avec injection de JwtUtil
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    /**
     * Configuration du filtre
     */
    public static class Config {
        // Configuration optionnelle si nécessaire
    }

    /**
     * Application du filtre
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Récupérer la requête
            var request = exchange.getRequest();
            var response = exchange.getResponse();

            // ============================================
            // VÉRIFICATION DES ENDPOINTS PUBLICS
            // ============================================
            String path = request.getPath().value();
            if (isPublicEndpoint(path)) {
                return chain.filter(exchange);
            }

            // ============================================
            // EXTRACTION DU TOKEN
            // ============================================
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(response, "Token manquant ou invalide");
            }

            String token = authHeader.substring(7);

            // ============================================
            // VALIDATION DU TOKEN
            // ============================================
            try {
                // Vérifier la validité du token
                if (!jwtUtil.validateToken(token)) {
                    return unauthorized(response, "Token invalide ou expiré");
                }

                // Extraire les informations du token
                String username = jwtUtil.extractUsername(token);
                List<String> roles = jwtUtil.extractRoles(token);
                String userId = jwtUtil.extractUserId(token);

                // Vérifier si le token est blacklisté (déconnexion)
                if (jwtUtil.isTokenBlacklisted(token)) {
                    return unauthorized(response, "Token révoqué");
                }

                // ============================================
                // AJOUT DES INFORMATIONS AUX HEADERS
                // ============================================
                var mutatedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Name", username)
                        .header("X-User-Roles", String.join(",", roles))
                        .header("X-Authenticated", "true")
                        .build();

                // Vérifier les permissions pour les endpoints admin
                if (isAdminEndpoint(path) && !hasAdminRole(roles)) {
                    return forbidden(response, "Permissions insuffisantes");
                }

                // Vérifier les permissions pour les endpoints marchands
                if (isMerchantEndpoint(path) && !hasMerchantRole(roles)) {
                    return forbidden(response, "Accès réservé aux marchands");
                }

                // Continuer avec la requête modifiée
                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                return unauthorized(response, "Token expiré");
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                return unauthorized(response, "Token malformé");
            } catch (io.jsonwebtoken.SignatureException e) {
                return unauthorized(response, "Signature invalide");
            } catch (Exception e) {
                return unauthorized(response, "Erreur d'authentification: " + e.getMessage());
            }
        };
    }

    /**
     * Vérifie si l'endpoint est public
     */
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/api/auth/refresh") ||
                path.startsWith("/api/search") ||
                path.startsWith("/api/geo") ||
                path.startsWith("/api/stats/public") ||
                path.equals("/actuator/health") ||
                path.equals("/actuator/info") ||
                path.startsWith("/fallback/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api-docs");
    }

    /**
     * Vérifie si l'endpoint est admin
     */
    private boolean isAdminEndpoint(String path) {
        return path.startsWith("/api/crawler") ||
                path.startsWith("/api/stats/admin") ||
                path.startsWith("/api/analytics") ||
                path.startsWith("/actuator/") && !path.equals("/actuator/health");
    }

    /**
     * Vérifie si l'endpoint est marchand
     */
    private boolean isMerchantEndpoint(String path) {
        return path.startsWith("/api/shop/merchants") ||
                path.startsWith("/api/products/manage");
    }

    /**
     * Vérifie si l'utilisateur a un rôle admin
     */
    private boolean hasAdminRole(List<String> roles) {
        return roles.contains("ROLE_ADMIN") || roles.contains("ROLE_MODERATOR");
    }

    /**
     * Vérifie si l'utilisateur a un rôle marchand
     */
    private boolean hasMerchantRole(List<String> roles) {
        return roles.contains("ROLE_MERCHANT") || roles.contains("ROLE_ADMIN");
    }

    /**
     * Retourne une réponse 401 Unauthorized
     */
    private Mono<Void> unauthorized(org.springframework.http.server.reactive.ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        String body = """
            {
                "error": "unauthorized",
                "message": "%s",
                "timestamp": "%s",
                "code": "AUTH_001"
            }
            """.formatted(message, java.time.Instant.now().toString());

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }

    /**
     * Retourne une réponse 403 Forbidden
     */
    private Mono<Void> forbidden(org.springframework.http.server.reactive.ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        String body = """
            {
                "error": "forbidden",
                "message": "%s",
                "timestamp": "%s",
                "code": "AUTH_002"
            }
            """.formatted(message, java.time.Instant.now().toString());

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes()))
        );
    }

    /**
     * Méthode pour ajouter des métriques d'authentification
     */
    private void logAuthentication(String username, boolean success, String path) {
        System.out.printf("[Authentication] User: %s, Success: %s, Path: %s, Time: %s%n",
                username, success, path, java.time.LocalDateTime.now());
    }
}