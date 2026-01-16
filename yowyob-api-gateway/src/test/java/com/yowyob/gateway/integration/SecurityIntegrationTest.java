package com.yowyob.gateway.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Tests d'intégration pour la sécurité du Gateway
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Tests d'authentification et d'autorisation
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    @DisplayName("Devrait autoriser l'accès aux endpoints publics sans authentification")
    void shouldAllowAccessToPublicEndpointsWithoutAuth() {
        // Given - Mock du Search Service
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        // When/Then
        webTestClient.get()
                .uri("/api/search?q=test")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Devrait bloquer l'accès aux endpoints protégés sans authentification")
    void shouldBlockAccessToProtectedEndpointsWithoutAuth() {
        // When/Then
        webTestClient.get()
                .uri("/api/users/profile")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(401)
                .jsonPath("$.error").isEqualTo("Unauthorized")
                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    @DisplayName("Devrait bloquer l'accès avec un token invalide")
    void shouldBlockAccessWithInvalidToken() {
        // When/Then
        webTestClient.get()
                .uri("/api/users/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.message")
                .value(message -> ((String) message).contains("Token d'authentification invalide"));
    }

    @Test
    @DisplayName("Devrait autoriser l'accès avec un token valide")
    void shouldAllowAccessWithValidToken() {
        // Given - Mock du User Service
        stubFor(get(urlEqualTo("/users/profile"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("""
                                {
                                    "id": "user_123",
                                    "email": "test@example.com"
                                }
                                """)));

        // When/Then
        webTestClient.get()
                .uri("/api/users/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .header("X-User-Id", "user_123")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Devrait bloquer l'accès aux endpoints admin pour les utilisateurs non-admin")
    void shouldBlockAdminEndpointsForNonAdminUsers() {
        // When/Then
        webTestClient.get()
                .uri("/actuator/metrics")
                .header(HttpHeaders.AUTHORIZATION, "Bearer user-token")
                .header("X-User-Id", "regular-user")
                .header("X-User-Roles", "USER")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("Forbidden");
    }

    @Test
    @DisplayName("Devrait autoriser l'accès aux endpoints admin pour les utilisateurs admin")
    void shouldAllowAdminEndpointsForAdminUsers() {
        // Given - Mock du endpoint Actuator
        stubFor(get(urlEqualTo("/metrics"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        // When/Then
        webTestClient.get()
                .uri("/actuator/metrics")
                .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token")
                .header("X-User-Id", "admin-user")
                .header("X-User-Roles", "ADMIN")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("Devrait bloquer l'accès aux endpoints avec des rôles insuffisants")
    void shouldBlockAccessToEndpointsWithInsufficientRoles() {
        // When/Then - USER essaie d'accéder à un endpoint MERCHANT
        webTestClient.get()
                .uri("/api/shop/merchants/dashboard")
                .header(HttpHeaders.AUTHORIZATION, "Bearer user-token")
                .header("X-User-Id", "regular-user")
                .header("X-User-Roles", "USER")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("Devrait respecter la configuration CORS")
    void shouldRespectCorsConfiguration() {
        // When/Then - Origine autorisée
        webTestClient.options()
                .uri("/api/search")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "http://localhost:3000")
                .expectHeader().exists("Access-Control-Allow-Methods")
                .expectHeader().exists("Access-Control-Allow-Headers");

        // When/Then - Origine non autorisée
        webTestClient.options()
                .uri("/api/search")
                .header("Origin", "http://evil.com")
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("Devrait valider la longueur des tokens")
    void shouldValidateTokenLength() {
        // When/Then - Token trop court
        webTestClient.get()
                .uri("/api/users/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer short")
                .exchange()
                .expectStatus().isUnauthorized();

        // When/Then - Token mal formé
        webTestClient.get()
                .uri("/api/users/profile")
                .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNz")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("Devrait propager les headers d'authentification aux services backend")
    void shouldPropagateAuthHeadersToBackendServices() {
        // Given - Mock du User Service
        stubFor(get(urlEqualTo("/users/profile"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        // When
        webTestClient.get()
                .uri("/api/users/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .header("X-User-Id", "user_123")
                .header("X-User-Roles", "USER,PREMIUM")
                .exchange()
                .expectStatus().isOk();

        // Then - Vérifier que les headers sont propagés
        verify(getRequestedFor(urlEqualTo("/users/profile"))
                .withHeader("X-User-Id", equalTo("user_123"))
                .withHeader("X-User-Roles", equalTo("USER,PREMIUM"))
                .withHeader("X-Authenticated", equalTo("true")));
    }

    @Test
    @DisplayName("Devrait gérer les tokens expirés")
    void shouldHandleExpiredTokens() {
        // When/Then
        webTestClient.get()
                .uri("/api/users/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer expired-token")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.message").value(message -> ((String) message).contains("Token d'authentification expiré"));
    }

    @Test
    @DisplayName("Devrait permettre l'authentification sur les endpoints d'authentification")
    void shouldAllowAuthOnAuthenticationEndpoints() {
        // Given - Mock du User Service pour login
        stubFor(post(urlEqualTo("/auth/login"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("""
                                {
                                    "accessToken": "new-token",
                                    "refreshToken": "refresh-token"
                                }
                                """)));

        // When/Then
        webTestClient.post()
                .uri("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .bodyValue("""
                        {
                            "email": "test@example.com",
                            "password": "password123"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo("new-token");
    }

    @Test
    @DisplayName("Devrait limiter les tentatives de login")
    void shouldLimitLoginAttempts() {
        // Given - Mock du User Service pour login avec échec
        stubFor(post(urlEqualTo("/auth/login"))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withBody("""
                                {
                                    "error": "Invalid credentials"
                                }
                                """)));

        // When - Faire plusieurs tentatives de login échouées
        for (int i = 0; i < 6; i++) {
            webTestClient.post()
                    .uri("/api/auth/login")
                    .contentType(APPLICATION_JSON)
                    .bodyValue("""
                            {
                                "email": "test@example.com",
                                "password": "wrongpassword"
                            }
                            """)
                    .exchange()
                    .expectStatus().is4xxClientError();
        }

        // Then - La 6ème tentative devrait être rate limited
        webTestClient.post()
                .uri("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .bodyValue("""
                        {
                            "email": "test@example.com",
                            "password": "wrongpassword"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(429); // Too Many Requests
    }
}