package com.yowyob.common.security.jwt;

import com.yowyob.common.constant.SecurityConstants;
import com.yowyob.common.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Service de gestion des tokens JWT
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Génère et valide les tokens JWT pour l'authentification
 *          Utilise HMAC-SHA256 pour la signature des tokens
 */
@Slf4j
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String issuer;
    private final String audience;

    public JwtService(
            @Value("${app.security.jwt.secret:yowyob-secret-key-change-in-production-minimum-256-bits}") String secret,
            @Value("${app.security.jwt.expiration-ms:900000}") long accessTokenExpiration,
            @Value("${app.security.jwt.refresh-expiration-ms:604800000}") long refreshTokenExpiration,
            @Value("${app.security.jwt.issuer:yowyob-search}") String issuer,
            @Value("${app.security.jwt.audience:yowyob-clients}") String audience) {

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.issuer = issuer;
        this.audience = audience;
    }

    public String generateAccessToken(String userId, Set<String> roles) {
        return generateToken(userId, roles, accessTokenExpiration);
    }

    public String generateRefreshToken(String userId) {
        return generateToken(userId, Set.of(), refreshTokenExpiration);
    }

    private String generateToken(String userId, Set<String> roles, long expiration) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(expiration);

        return Jwts.builder()
                .subject(userId)
                .claim(SecurityConstants.JWT_CLAIM_ROLES, roles)
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .id(java.util.UUID.randomUUID().toString())
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token JWT invalide: {}", e.getMessage());
            return false;
        }
    }

    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        Claims claims = extractClaims(token);
        Object rolesObj = claims.get(SecurityConstants.JWT_CLAIM_ROLES);

        if (rolesObj instanceof java.util.Collection) {
            return new java.util.HashSet<>((java.util.Collection<String>) rolesObj);
        }
        return java.util.Set.of();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw UnauthorizedException.expiredToken();
        } catch (JwtException | IllegalArgumentException e) {
            throw UnauthorizedException.invalidToken();
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public String generateTokenWithClaims(String userId, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(accessTokenExpiration);

        return Jwts.builder()
                .subject(userId)
                .claims(claims)
                .issuer(issuer)
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .id(java.util.UUID.randomUUID().toString())
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }
}