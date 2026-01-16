package com.yowyob.gateway.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Générateur de clés de cache pour le Gateway
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@Component("appCacheKeyGenerator")
public class CacheKeyGenerator {

    private static final Logger log = LoggerFactory.getLogger(CacheKeyGenerator.class);

    /**
     * Génère une clé de cache unique pour une requête
     * 
     * @param method      Méthode HTTP
     * @param path        Chemin de la requête
     * @param queryParams Paramètres de requête
     * @param userId      ID de l'utilisateur (peut être null)
     * @return Clé de cache
     */
    public String generate(
            String method,
            String path,
            Map<String, String> queryParams,
            String userId) {

        // Construire la chaîne de base
        StringBuilder baseString = new StringBuilder();

        // Méthode et chemin
        baseString.append(method).append(":").append(path);

        // Paramètres de requête triés (pour la cohérence)
        if (queryParams != null && !queryParams.isEmpty()) {
            String sortedParams = new TreeMap<>(queryParams)
                    .entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));

            baseString.append("?").append(sortedParams);
        }

        // Utilisateur (si authentifié)
        if (userId != null && !userId.isEmpty()) {
            baseString.append(":user:").append(userId);
        }

        String keyBase = baseString.toString();

        // Générer un hash MD5 pour une taille fixe
        String hash = DigestUtils.md5DigestAsHex(
                keyBase.getBytes(StandardCharsets.UTF_8));

        // Clé finale formatée
        String cacheKey = String.format("gateway:cache:%s", hash);

        log.debug("Clé de cache générée: {} -> {}", keyBase, cacheKey);

        return cacheKey;
    }

    /**
     * Génère une clé de cache depuis une requête HTTP
     * 
     * @param request Requête HTTP
     * @return Clé de cache
     */
    public String generateFromRequest(ServerHttpRequest request) {
        String method = request.getMethod().name();
        String path = request.getPath().value();

        // Extraire les paramètres de requête
        Map<String, String> queryParams = request.getQueryParams()
                .toSingleValueMap();

        // Extraire l'ID utilisateur
        String userId = request.getHeaders().getFirst("X-User-Id");

        return generate(method, path, queryParams, userId);
    }

    /**
     * Génère une clé de cache pour le rate limiting
     * 
     * @param key   Clé originale (userId, ip, etc.)
     * @param route Route ID
     * @return Clé de cache pour rate limiting
     */
    public String generateRateLimitKey(String key, String route) {
        String base = String.format("ratelimit:%s:%s", route, key);
        String hash = DigestUtils.md5DigestAsHex(
                base.getBytes(StandardCharsets.UTF_8));

        return String.format("gateway:ratelimit:%s", hash);
    }

    /**
     * Génère une clé de cache pour le fallback
     * 
     * @param service Nom du service
     * @param path    Chemin de la requête
     * @param userId  ID de l'utilisateur
     * @return Clé de cache pour fallback
     */
    public String generateFallbackKey(String service, String path, String userId) {
        String userPart = userId != null ? userId : "anonymous";
        String base = String.format("fallback:%s:%s:%s", service, path, userPart);
        String hash = DigestUtils.md5DigestAsHex(
                base.getBytes(StandardCharsets.UTF_8));

        return String.format("gateway:fallback:%s", hash);
    }

    /**
     * Génère un pattern pour rechercher des clés de cache
     * 
     * @param service Nom du service (optionnel)
     * @param pattern Pattern supplémentaire
     * @return Pattern de recherche Redis
     */
    public String generateCachePattern(String service, String pattern) {
        if (service != null && !service.isEmpty()) {
            return String.format("gateway:cache:*%s*%s*", service, pattern);
        }

        return String.format("gateway:cache:*%s*", pattern);
    }
}