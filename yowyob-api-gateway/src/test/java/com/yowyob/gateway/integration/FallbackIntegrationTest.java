package com.yowyob.gateway.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.containsString;

/**
 * Tests d'intégration pour les fallbacks
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class FallbackIntegrationTest {

        @Autowired
        private WebTestClient webTestClient;

        @BeforeEach
        void setUp() {
                WireMock.reset();
        }

        @Test
        @DisplayName("Devrait retourner une réponse de fallback quand le circuit breaker est ouvert")
        void shouldReturnFallbackResponseWhenCircuitBreakerIsOpen() {
                // Given - Mock le Search Service pour échouer
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse()
                                                .withStatus(500)
                                                .withFixedDelay(3000))); // Réponse lente pour déclencher le circuit
                                                                         // breaker

                // Faire assez de requêtes pour ouvrir le circuit breaker
                for (int i = 0; i < 10; i++) {
                        webTestClient.get()
                                        .uri("/api/search?q=test")
                                        .exchange()
                                        .expectStatus().is5xxServerError();
                }

                // Attendre que le circuit breaker s'ouvre
                await().atMost(10, SECONDS).untilAsserted(() -> {
                        // Les requêtes suivantes devraient retourner le fallback
                        webTestClient.get()
                                        .uri("/api/search?q=test")
                                        .exchange()
                                        .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                                        .expectBody()
                                        .jsonPath("$.error").isEqualTo("Service Unavailable")
                                        .jsonPath("$.message")
                                        .value(containsString("Service temporairement indisponible"))
                                        .jsonPath("$.retryAfter").exists();
                });
        }

        @Test
        @DisplayName("Devrait retourner des fallbacks différents selon le service")
        void shouldReturnDifferentFallbacksByService() {
                // Given - Mock différents services pour échouer
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse().withStatus(500)));

                stubFor(get(urlEqualTo("/users/profile"))
                                .willReturn(aResponse().withStatus(500)));

                // When/Then - Fallback pour le search service
                webTestClient.get()
                                .uri("/api/search?q=test")
                                .exchange()
                                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                                .expectBody()
                                .jsonPath("$.message").value(containsString("recherche"));

                // When/Then - Fallback pour le user service
                webTestClient.get()
                                .uri("/api/users/profile")
                                .header("Authorization", "Bearer test-token")
                                .exchange()
                                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                                .expectBody()
                                .jsonPath("$.message").value(containsString("utilisateur"));
        }

        @Test
        @DisplayName("Devrait inclure le header Retry-After dans les réponses de fallback")
        void shouldIncludeRetryAfterHeaderInFallbackResponses() {
                // Given
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse().withStatus(500)));

                // Faire des requêtes pour ouvrir le circuit breaker
                for (int i = 0; i < 10; i++) {
                        webTestClient.get()
                                        .uri("/api/search?q=test")
                                        .exchange();
                }

                // When/Then
                webTestClient.get()
                                .uri("/api/search?q=test")
                                .exchange()
                                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                                .expectHeader().exists("Retry-After")
                                .expectHeader().valueMatches("Retry-After", "\\d+");
        }

        @Test
        @DisplayName("Devrait retourner au service normal après la fermeture du circuit breaker")
        void shouldReturnToNormalServiceAfterCircuitBreakerCloses() {
                // Given - Ouvrir le circuit breaker
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse().withStatus(500)));

                for (int i = 0; i < 10; i++) {
                        webTestClient.get()
                                        .uri("/api/search?q=test")
                                        .exchange();
                }

                // Attendre que le circuit breaker soit ouvert
                await().atMost(5, SECONDS).untilAsserted(() -> {
                        webTestClient.get()
                                        .uri("/api/search?q=test")
                                        .exchange()
                                        .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                });

                // Attendre que le circuit breaker passe en half-open
                await().atMost(15, SECONDS).untilAsserted(() -> {
                        // Configurer le service pour réussir
                        WireMock.reset();
                        stubFor(get(urlEqualTo("/search"))
                                        .willReturn(aResponse()
                                                        .withStatus(200)
                                                        .withBody("{\"results\": []}")));

                        // Le circuit breaker devrait être en half-open et laisser passer quelques
                        // requêtes
                        webTestClient.get()
                                        .uri("/api/search?q=test")
                                        .exchange()
                                        .expectStatus().isOk();
                });
        }

        @Test
        @DisplayName("Devrait retourner des réponses de fallback avec cache quand disponible")
        void shouldReturnCachedFallbackResponsesWhenAvailable() {
                // Given - Faire une requête réussie d'abord pour mettre en cache
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody("{\"results\": [\"cached\"]}")));

                webTestClient.get()
                                .uri("/api/search?q=cachetest")
                                .exchange()
                                .expectStatus().isOk();

                // Ensuite faire échouer le service
                WireMock.reset();
                stubFor(get(urlEqualTo("/search"))
                                .willReturn(aResponse().withStatus(500)));

                // When/Then - Devrait retourner la réponse en cache avec statut STALE
                webTestClient.get()
                                .uri("/api/search?q=cachetest")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().valueEquals("X-Cache-Status", "STALE");
        }
}