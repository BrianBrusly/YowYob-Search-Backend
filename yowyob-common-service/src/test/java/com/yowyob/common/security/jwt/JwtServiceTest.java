package com.yowyob.common.security.jwt;

import com.yowyob.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests pour JwtService
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String secret = "test-secret-key-minimum-256-bits-for-hs256-algorithm-security";
        jwtService = new JwtService(secret, 900000L, 604800000L, "yowyob-test", "yowyob-clients");
    }

    @Test
    @DisplayName("Devrait générer un token d'accès valide")
    void shouldGenerateValidAccessToken() {
        String userId = "user123";
        Set<String> roles = Set.of("ROLE_USER");

        String token = jwtService.generateAccessToken(userId, roles);

        assertThat(token).isNotNull();
        assertThat(token).startsWith("eyJ");
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Devrait valider un token correct")
    void shouldValidateCorrectToken() {
        String userId = "user123";
        Set<String> roles = Set.of("ROLE_USER");
        String token = jwtService.generateAccessToken(userId, roles);

        boolean isValid = jwtService.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Devrait rejeter un token invalide")
    void shouldRejectInvalidToken() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtService.validateToken(invalidToken);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Devrait extraire l'userId du token")
    void shouldExtractUserIdFromToken() {
        String userId = "user123";
        Set<String> roles = Set.of("ROLE_USER");
        String token = jwtService.generateAccessToken(userId, roles);

        String extractedUserId = jwtService.extractUserId(token);

        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("Devrait extraire les rôles du token")
    void shouldExtractRolesFromToken() {
        String userId = "user123";
        Set<String> roles = Set.of("ROLE_USER", "ROLE_ADMIN");
        String token = jwtService.generateAccessToken(userId, roles);

        Set<String> extractedRoles = jwtService.extractRoles(token);

        assertThat(extractedRoles).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("Devrait extraire les claims du token")
    void shouldExtractClaimsFromToken() {
        String userId = "user123";
        Set<String> roles = Set.of("ROLE_USER");
        String token = jwtService.generateAccessToken(userId, roles);

        Claims claims = jwtService.extractClaims(token);

        assertThat(claims.getSubject()).isEqualTo(userId);
        assertThat(claims.getIssuer()).isEqualTo("yowyob-test");

        // MODIFICATION: getAudience() retourne maintenant un Set<String>
        assertThat(claims.getAudience()).contains("yowyob-clients");
    }

    @Test
    @DisplayName("Devrait lancer une exception pour un token expiré")
    void shouldThrowExceptionForExpiredToken() {
        JwtService shortExpiryService = new JwtService(
                "test-secret-key-minimum-256-bits-for-hs256-algorithm-security",
                1L,
                1L,
                "yowyob-test",
                "yowyob-clients"
        );

        String token = shortExpiryService.generateAccessToken("user123", Set.of("ROLE_USER"));

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThatThrownBy(() -> shortExpiryService.extractClaims(token))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("Devrait générer des tokens différents pour le même utilisateur")
    void shouldGenerateDifferentTokensForSameUser() {
        String userId = "user123";
        Set<String> roles = Set.of("ROLE_USER");

        String token1 = jwtService.generateAccessToken(userId, roles);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String token2 = jwtService.generateAccessToken(userId, roles);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Devrait générer un refresh token")
    void shouldGenerateRefreshToken() {
        String userId = "user123";

        String refreshToken = jwtService.generateRefreshToken(userId);

        assertThat(refreshToken).isNotNull();
        assertThat(jwtService.validateToken(refreshToken)).isTrue();
        assertThat(jwtService.extractUserId(refreshToken)).isEqualTo(userId);
    }
}