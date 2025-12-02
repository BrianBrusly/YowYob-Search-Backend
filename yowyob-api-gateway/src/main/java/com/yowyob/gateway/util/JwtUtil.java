package com.yowyob.gateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utilitaire pour la gestion des tokens JWT
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Fournit les fonctionnalités de génération, validation et extraction
 * des informations depuis les tokens JWT
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret:yowyob.4gi.enspy27}")
    private String jwtSecret;

    @Value("${jwt.expiration:900000}")
    private long jwtExpirationMs;

    @Value("${jwt.issuer:yowyob-search}")
    private String jwtIssuer;

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    // Métriques
    private final AtomicLong tokensGeneratedCount = new AtomicLong(0);
    private final AtomicLong tokensValidatedCount = new AtomicLong(0);
    private final AtomicLong tokensBlacklistedCount = new AtomicLong(0);

    /**
     * Constructeur avec injection de ReactiveRedisTemplate
     */
    public JwtUtil(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Génère une clé secrète à partir de la chaîne de configuration
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Génère un token JWT pour un utilisateur
     *
     * @param username Nom d'utilisateur
     * @param userId ID de l'utilisateur
     * @param roles Liste des rôles de l'utilisateur
     * @return Token JWT signé
     */
    public String generateToken(String username, String userId, List<String> roles) {
        Instant now = Instant.now();
        Instant expirationTime = now.plusMillis(jwtExpirationMs);

        String token = Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("roles", roles)
                .setIssuer(jwtIssuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        tokensGeneratedCount.incrementAndGet();
        log.debug("Token généré pour l'utilisateur: {} (ID: {})", username, userId);

        return token;
    }

    /**
     * Génère un token JWT simple (surcharge pour compatibilité)
     */
    public String generateToken(String username, String userId) {
        return generateToken(username, userId, List.of("ROLE_USER"));
    }

    /**
     * Valide un token JWT
     *
     * @param token Token à valider
     * @return true si le token est valide, false sinon
     */
    public boolean validateToken(String token) {
        tokensValidatedCount.incrementAndGet();

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            log.debug("Token validé avec succès");
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("Token expiré: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Token malformé: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("Signature invalide: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("Token non supporté: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("Token vide ou null: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extrait le nom d'utilisateur depuis un token
     */
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    /**
     * Extrait l'ID utilisateur depuis un token
     */
    public String extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", String.class);
    }

    /**
     * Extrait les rôles depuis un token
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    /**
     * Extrait la date d'expiration d'un token
     */
    public Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    /**
     * Extrait la date de création d'un token
     */
    public Date extractIssuedAt(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getIssuedAt();
    }

    /**
     * Extrait toutes les claims d'un token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si un token est expiré
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Ajoute un token à la blacklist (pour déconnexion)
     *
     * @param token Token à blacklister
     * @return Mono<Boolean> indiquant le succès de l'opération
     */
    public Mono<Boolean> blacklistToken(String token) {
        try {
            Date expiration = extractExpiration(token);
            long ttlSeconds = (expiration.getTime() - System.currentTimeMillis()) / 1000;

            if (ttlSeconds > 0) {
                tokensBlacklistedCount.incrementAndGet();

                return redisTemplate.opsForValue()
                        .set("blacklist:token:" + token, "revoked", Duration.ofSeconds(ttlSeconds))
                        .doOnSuccess(success ->
                                log.info("Token ajouté à la blacklist avec TTL de {} secondes", ttlSeconds))
                        .doOnError(error ->
                                log.error("Erreur lors de l'ajout du token à la blacklist", error));
            }
        } catch (Exception e) {
            log.error("Erreur lors du blacklisting du token", e);
        }

        return Mono.just(false);
    }

    /**
     * Vérifie si un token est blacklisté
     *
     * @param token Token à vérifier
     * @return true si le token est blacklisté, false sinon
     */
    public boolean isTokenBlacklisted(String token) {
        try {
            Boolean exists = redisTemplate.hasKey("blacklist:token:" + token).block();
            return exists != null && exists;
        } catch (Exception e) {
            log.error("Erreur lors de la vérification du blacklist", e);
            return false;
        }
    }

    /**
     * Calcule le temps restant avant expiration d'un token
     *
     * @param token Token à analyser
     * @return Durée restante en secondes, ou 0 si expiré
     */
    public long getRemainingValidity(String token) {
        try {
            Date expiration = extractExpiration(token);
            long remainingMs = expiration.getTime() - System.currentTimeMillis();
            return remainingMs > 0 ? remainingMs / 1000 : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Vérifie si la configuration JWT est correcte
     */
    public boolean isConfigured() {
        return jwtSecret != null && !jwtSecret.isEmpty() && jwtExpirationMs > 0;
    }

    /**
     * Retourne le nombre de tokens générés
     */
    public long getTokensGeneratedCount() {
        return tokensGeneratedCount.get();
    }

    /**
     * Retourne le nombre de tokens validés
     */
    public long getTokensValidatedCount() {
        return tokensValidatedCount.get();
    }

    /**
     * Retourne le nombre de tokens blacklistés
     */
    public long getBlacklistedTokensCount() {
        return tokensBlacklistedCount.get();
    }

    /**
     * Retourne le nombre estimé de tokens actifs
     * (générés - blacklistés)
     */
    public long getActiveTokensCount() {
        return tokensGeneratedCount.get() - tokensBlacklistedCount.get();
    }

    /**
     * Parse un token sans validation complète (pour extraction d'infos)
     * Attention : Ne pas utiliser pour la sécurité !
     */
    public Claims parseTokenUnsafe(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format");
            }

            // Decode le payload (partie 2)
            String payload = new String(
                    java.util.Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            // Parse le JSON (simplifié - en production utiliser Jackson)
            log.debug("Token payload (unsafe): {}", payload);

            return extractAllClaims(token);
        } catch (Exception e) {
            log.error("Erreur lors du parsing unsafe du token", e);
            throw new IllegalArgumentException("Cannot parse token", e);
        }
    }

    /**
     * Génère un token de test (seulement pour les tests unitaires)
     */
    public String generateTestToken(String username) {
        return generateToken(username, "test-user-id", List.of("ROLE_TEST"));
    }
}