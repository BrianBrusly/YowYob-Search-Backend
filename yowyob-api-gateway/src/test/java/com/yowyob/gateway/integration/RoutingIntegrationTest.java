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
 * Tests d'intégration pour le routage du Gateway
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Tests end-to-end du routage vers les différents services
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class RoutingIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        WireMock.reset();
    }

    @Test
    @DisplayName("Devrait router vers le Search Service")
    void shouldRouteToSearchService() {
        // Given - Mock du Search Service
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON.toString())
                        .withBody("""
                                {
                                    "results": [],
                                    "total": 0,
                                    "page": 1
                                }
                                """)));

        // When/Then
        webTestClient.get()
                .uri("/api/search?q=test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.results").isArray()
                .jsonPath("$.total").isEqualTo(0);

        // Verify que la requête a été forwardée au Search Service
        verify(getRequestedFor(urlEqualTo("/search"))
                .withQueryParam("q", equalTo("test")));
    }

    @Test
    @DisplayName("Devrait router vers le User Service")
    void shouldRouteToUserService() {
        // Given - Mock du User Service
        stubFor(get(urlEqualTo("/users/profile"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON.toString())
                        .withBody("""
                                {
                                    "id": "user_123",
                                    "email": "test@example.com",
                                    "firstName": "John",
                                    "lastName": "Doe"
                                }
                                """)));

        // When/Then
        webTestClient.get()
                .uri("/api/users/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .header("X-User-Id", "user_123")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo("user_123")
                .jsonPath("$.email").isEqualTo("test@example.com");

        // Verify que la requête a été forwardée au User Service
        verify(getRequestedFor(urlEqualTo("/users/profile"))
                .withHeader("X-User-Id", equalTo("user_123")));
    }

    @Test
    @DisplayName("Devrait router vers le Geo Service")
    void shouldRouteToGeoService() {
        // Given - Mock du Geo Service
        stubFor(get(urlEqualTo("/geo/reverse?lat=4.05&lon=9.7"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON.toString())
                        .withBody("""
                                {
                                    "address": "Yaoundé, Cameroon",
                                    "latitude": 4.05,
                                    "longitude": 9.7
                                }
                                """)));

        // When/Then
        webTestClient.get()
                .uri("/api/geo/reverse?lat=4.05&lon=9.7")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.address").isEqualTo("Yaoundé, Cameroon");

        // Verify que la requête a été forwardée au Geo Service
        verify(getRequestedFor(urlEqualTo("/geo/reverse"))
                .withQueryParam("lat", equalTo("4.05"))
                .withQueryParam("lon", equalTo("9.7")));
    }

    @Test
    @DisplayName("Devrait retourner 404 pour une route inexistante")
    void shouldReturn404ForNonExistentRoute() {
        // When/Then
        webTestClient.get()
                .uri("/api/nonexistent")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Devrait appliquer le rate limiting")
    void shouldApplyRateLimiting() {
        // Given - Mock du Search Service
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        // When - Faire plus de requêtes que la limite
        for (int i = 0; i < 11; i++) {
            webTestClient.get()
                    .uri("/api/search")
                    .header("X-User-Id", "test-user")
                    .exchange();
        }

        // Then - La 11ème requête devrait être rate limited
        webTestClient.get()
                .uri("/api/search")
                .header("X-User-Id", "test-user")
                .exchange()
                .expectStatus().isEqualTo(429) // Too Many Requests
                .expectBody()
                .jsonPath("$.error").isEqualTo("Too Many Requests");
    }

    @Test
    @DisplayName("Devrait respecter les CORS headers")
    void shouldRespectCorsHeaders() {
        // When/Then
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
    @DisplayName("Devrait gérer les erreurs de timeout")
    void shouldHandleTimeoutErrors() {
        // Given - Mock un service qui répond lentement
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(10000) // 10 secondes de délai
                        .withBody("{}")));

        // When/Then - Devrait timeout après le délai configuré
        webTestClient.get()
                .uri("/api/search")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("TIMEOUT");
    }

    @Test
    @DisplayName("Devrait gérer les erreurs de circuit breaker")
    void shouldHandleCircuitBreakerErrors() {
        // Given - Mock un service qui retourne des erreurs
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // When - Faire assez de requêtes pour ouvrir le circuit breaker
        for (int i = 0; i < 10; i++) {
            webTestClient.get()
                    .uri("/api/search")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                    .exchange();
        }

        // Then - Le circuit breaker devrait être ouvert
        webTestClient.get()
                .uri("/api/search")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .exchange()
                .expectStatus().isEqualTo(503) // Service Unavailable
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("CIRCUIT_BREAKER_OPEN");
    }

    @Test
    @DisplayName("Devrait appliquer les transformations de chemin")
    void shouldApplyPathTransformations() {
        // Given - Mock du Search Service
        stubFor(get(urlEqualTo("/search?q=test"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        // When/Then - Le chemin devrait être transformé
        webTestClient.get()
                .uri("/api/search?q=test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .exchange()
                .expectStatus().isOk();

        // Verify que le préfixe /api a été enlevé
        verify(getRequestedFor(urlEqualTo("/search")));
    }

    @Test
    @DisplayName("Devrait ajouter les headers de réponse")
    void shouldAddResponseHeaders() {
        // Given - Mock du Search Service
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("X-Backend-Version", "1.0.0")
                        .withBody("{}")));

        // When/Then
        webTestClient.get()
                .uri("/api/search")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Served-By")
                .expectHeader().exists("X-Response-Time");
    }
}