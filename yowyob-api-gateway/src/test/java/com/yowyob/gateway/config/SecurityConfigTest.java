package com.yowyob.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Tests de configuration de sécurité
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Vérifie que la sécurité est correctement configurée
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Devrait autoriser l'accès aux endpoints publics sans authentification")
    void shouldAllowPublicEndpointsWithoutAuth() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");

        webTestClient.get()
                .uri("/api/search?q=test")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Devrait bloquer l'accès aux endpoints protégés sans authentification")
    void shouldBlockProtectedEndpointsWithoutAuth() {
        webTestClient.get()
                .uri("/api/users/profile")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(401)
                .jsonPath("$.error").isEqualTo("Unauthorized");
    }

    @Test
    @DisplayName("Devrait autoriser l'accès aux endpoints protégés avec un token valide")
    void shouldAllowProtectedEndpointsWithValidToken() {
        webTestClient.get()
                .uri("/api/users/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .header("X-User-Id", "test-user")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Devrait bloquer l'accès aux endpoints admin pour les utilisateurs non-admin")
    void shouldBlockAdminEndpointsForNonAdminUsers() {
        webTestClient.get()
                .uri("/actuator/metrics")
                .header(HttpHeaders.AUTHORIZATION, "Bearer user-token")
                .header("X-User-Id", "regular-user")
                .header("X-User-Roles", "USER")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("Devrait autoriser l'accès aux endpoints admin pour les utilisateurs admin")
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminEndpointsForAdminUsers() {
        webTestClient.get()
                .uri("/actuator/metrics")
                .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token")
                .header("X-User-Id", "admin-user")
                .header("X-User-Roles", "ADMIN")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Devrait respecter la configuration CORS")
    void shouldRespectCorsConfiguration() {
        webTestClient.options()
                .uri("/api/search")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("Access-Control-Allow-Origin")
                .expectHeader().exists("Access-Control-Allow-Methods")
                .expectHeader().exists("Access-Control-Allow-Headers");
    }

    @Test
    @DisplayName("Devrait rejeter les origines CORS non autorisées")
    void shouldRejectUnauthorizedCorsOrigins() {
        webTestClient.options()
                .uri("/api/search")
                .header("Origin", "http://evil.com")
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("Devrait désactiver CSRF pour les APIs stateless")
    void shouldDisableCsrfForStatelessApis() {
        webTestClient.post()
                .uri("/api/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"email\":\"test@example.com\"}")
                .exchange()
                .expectStatus().isOk();
    }
}