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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Tests d'intégration pour le rate limiting
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Tests de limitation de taux
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class RateLimitIntegrationTest {

        @Autowired
        private WebTestClient webTestClient;

        @BeforeEach
        void setUp() {
                WireMock.reset();
        }

        @Test
        @DisplayName("Devrait appliquer le rate limiting pour les utilisateurs authentifiés")
        void shouldApplyRateLimitingForAuthenticatedUsers() {
                // Given - Mock du Search Service
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody("{}")));

                // When - Faire plus de requêtes que la limite
                for (int i = 0; i < 11; i++) {
                        webTestClient.get()
                                        .uri("/api/search")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                        .header("X-User-Id", "user-123")
                                        .exchange();
                }

                // Then - La 11ème requête devrait être rate limited
                webTestClient.get()
                                .uri("/api/search")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-123")
                                .exchange()
                                .expectStatus().isEqualTo(429) // Too Many Requests
                                .expectHeader().exists("X-RateLimit-Limit")
                                .expectHeader().exists("X-RateLimit-Remaining")
                                .expectHeader().exists("X-RateLimit-Reset")
                                .expectHeader().exists("Retry-After")
                                .expectBody()
                                .jsonPath("$.status").isEqualTo(429)
                                .jsonPath("$.error").isEqualTo("Too Many Requests")
                                .jsonPath("$.details.limit").isNumber()
                                .jsonPath("$.details.remaining").isEqualTo(0);
        }

        @Test
        @DisplayName("Devrait appliquer le rate limiting pour les utilisateurs anonymes par IP")
        void shouldApplyRateLimitingForAnonymousUsersByIp() {
                // Given - Mock du Search Service
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody("{}")));

                // When - Faire plus de requêtes que la limite sans authentification
                for (int i = 0; i < 11; i++) {
                        webTestClient.get()
                                        .uri("/api/search")
                                        .exchange();
                }

                // Then - La 11ème requête devrait être rate limited
                webTestClient.get()
                                .uri("/api/search")
                                .exchange()
                                .expectStatus().isEqualTo(429);
        }

        @Test
        @DisplayName("Devrait avoir des limites différentes pour différents endpoints")
        void shouldHaveDifferentLimitsForDifferentEndpoints() {
                // Given - Mock des services
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse().withStatus(200).withBody("{}")));

                stubFor(get(urlEqualTo("/users/profile"))
                                .willReturn(aResponse().withStatus(200).withBody("{}")));

                // Test Search endpoint (limite: 10/min)
                for (int i = 0; i < 11; i++) {
                        webTestClient.get()
                                        .uri("/api/search")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                        .header("X-User-Id", "user-123")
                                        .exchange();
                }

                // La 11ème devrait être rate limited
                webTestClient.get()
                                .uri("/api/search")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-123")
                                .exchange()
                                .expectStatus().isEqualTo(429);

                // Test User endpoint (limite différente)
                // Faire quelques requêtes vers l'endpoint user
                for (int i = 0; i < 5; i++) {
                        webTestClient.get()
                                        .uri("/api/users/profile")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                        .header("X-User-Id", "user-123")
                                        .exchange()
                                        .expectStatus().isOk(); // Devrait encore être OK
                }
        }

        @Test
        @DisplayName("Devrait avoir des limites plus strictes pour le login")
        void shouldHaveStricterLimitsForLogin() {
                // Given - Mock du User Service pour login
                stubFor(post(urlEqualTo("/auth/login"))
                                .willReturn(aResponse()
                                                .withStatus(401)
                                                .withBody("Invalid credentials")));

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

                // Then - La 6ème tentative devrait être rate limited (limite: 5/min pour login)
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
                                .expectStatus().isEqualTo(429);
        }

        @Test
        @DisplayName("Devrait reset le rate limit après la période")
        void shouldResetRateLimitAfterPeriod() throws InterruptedException {
                // Given - Mock du Search Service
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody("{}")));

                // Épuiser la limite
                for (int i = 0; i < 10; i++) {
                        webTestClient.get()
                                        .uri("/api/search")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                        .header("X-User-Id", "user-123")
                                        .exchange()
                                        .expectStatus().isOk();
                }

                // Vérifier que la limite est épuisée
                webTestClient.get()
                                .uri("/api/search")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-123")
                                .exchange()
                                .expectStatus().isEqualTo(429);

                // Attendre que la période se reset (60 secondes dans la config)
                // Note: Dans les tests, nous pourrions configurer une période plus courte
                Thread.sleep(61000); // 61 secondes pour être sûr

                // Après le reset, devrait pouvoir faire à nouveau des requêtes
                webTestClient.get()
                                .uri("/api/search")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-123")
                                .exchange()
                                .expectStatus().isOk();
        }

        @Test
        @DisplayName("Devrait avoir des limites séparées pour différents utilisateurs")
        void shouldHaveSeparateLimitsForDifferentUsers() {
                // Given - Mock du Search Service
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody("{}")));

                // User 1 épuise sa limite
                for (int i = 0; i < 10; i++) {
                        webTestClient.get()
                                        .uri("/api/search")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                        .header("X-User-Id", "user-1")
                                        .exchange()
                                        .expectStatus().isOk();
                }

                // User 1 devrait être rate limited
                webTestClient.get()
                                .uri("/api/search")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-1")
                                .exchange()
                                .expectStatus().isEqualTo(429);

                // User 2 devrait encore avoir sa limite complète
                for (int i = 0; i < 10; i++) {
                        webTestClient.get()
                                        .uri("/api/search")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                        .header("X-User-Id", "user-2")
                                        .exchange()
                                        .expectStatus().isOk();
                }

                // Maintenant user 2 devrait être rate limited aussi
                webTestClient.get()
                                .uri("/api/search")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-2")
                                .exchange()
                                .expectStatus().isEqualTo(429);
        }

        @Test
        @DisplayName("Devrait avoir des limites plus élevées pour les admins")
        void shouldHaveHigherLimitsForAdmins() {
                // Given - Mock du endpoint admin
                stubFor(get(urlEqualTo("/metrics"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody("{}")));

                // Admin peut faire beaucoup plus de requêtes
                for (int i = 0; i < 50; i++) { // Limite admin: 100/min
                        webTestClient.get()
                                        .uri("/actuator/metrics")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token")
                                        .header("X-User-Id", "admin-user")
                                        .header("X-User-Roles", "ADMIN")
                                        .exchange()
                                        .expectStatus().isOk();
                }

                // Admin ne devrait pas être rate limited après 50 requêtes
                webTestClient.get()
                                .uri("/actuator/metrics")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer admin-token")
                                .header("X-User-Id", "admin-user")
                                .header("X-User-Roles", "ADMIN")
                                .exchange()
                                .expectStatus().isOk();
        }

        @Test
        @DisplayName("Devrait permettre le burst jusqu'à la capacité de burst")
        void shouldAllowBurstUpToBurstCapacity() {
                // Given - Mock du Search Service
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody("{}")));

                // Burst capacity: 20 tokens
                // Faire 20 requêtes rapides
                for (int i = 0; i < 20; i++) {
                        webTestClient.get()
                                        .uri("/api/search")
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                        .header("X-User-Id", "user-123")
                                        .exchange()
                                        .expectStatus().isOk();
                }

                // La 21ème devrait être rate limited (dépassement de burst capacity)
                webTestClient.get()
                                .uri("/api/search")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-123")
                                .exchange()
                                .expectStatus().isEqualTo(429);
        }

        @Test
        @DisplayName("Devrait inclure les headers d'information de rate limit")
        void shouldIncludeRateLimitInfoHeaders() {
                // Given - Mock du Search Service
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody("{}")));

                // Faire une requête
                webTestClient.get()
                                .uri("/api/search")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-123")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().exists("X-RateLimit-Limit")
                                .expectHeader().exists("X-RateLimit-Remaining")
                                .expectHeader().exists("X-RateLimit-Reset");

                // Vérifier les valeurs des headers
                webTestClient.get()
                                .uri("/api/search")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-123")
                                .exchange()
                                .expectHeader().value("X-RateLimit-Limit", value -> {
                                        int limit = Integer.parseInt(value);
                                        assertThat(limit).isEqualTo(10); // Limite par défaut
                                })
                                .expectHeader().value("X-RateLimit-Remaining", value -> {
                                        int remaining = Integer.parseInt(value);
                                        assertThat(remaining).isEqualTo(8); // 10 - 2 requêtes faites
                                });
        }

        @Test
        @DisplayName("Devrait désactiver le rate limiting quand configuré")
        void shouldDisableRateLimitingWhenConfigured() {
                // Note: Ce test nécessite une configuration de test sans rate limiting
                // ou une manière de désactiver le rate limiting pour le test

                // Ce test pourrait être exécuté avec un profil qui désactive le rate limiting
                // ou en modifiant la configuration pour le test spécifique
        }
}