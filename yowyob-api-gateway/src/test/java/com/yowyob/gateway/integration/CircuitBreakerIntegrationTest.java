package com.yowyob.gateway.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
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

/**
 * Tests d'intégration pour les Circuit Breakers
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Tests du fonctionnement des Circuit Breakers
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class CircuitBreakerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker searchCircuitBreaker;

    @BeforeEach
    void setUp() {
        WireMock.reset();
        searchCircuitBreaker = circuitBreakerRegistry.circuitBreaker("searchCircuitBreaker");

        // Réinitialiser le Circuit Breaker avant chaque test
        if (searchCircuitBreaker != null) {
            searchCircuitBreaker.reset();
        }
    }

    @Test
    @DisplayName("Devrait ouvrir le Circuit Breaker après plusieurs échecs")
    void shouldOpenCircuitBreakerAfterMultipleFailures() throws InterruptedException {
        // Given - Mock un service qui retourne toujours des erreurs
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withFixedDelay(100) // Petit délai
                        .withBody("Internal Server Error")));

        // Vérifier que le Circuit Breaker est initialement fermé
        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);

        // When - Faire assez de requêtes pour ouvrir le Circuit Breaker
        for (int i = 0; i < 10; i++) {
            webTestClient.get()
                    .uri("/api/search?q=test")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                    .exchange()
                    .expectStatus().is5xxServerError();
        }

        // Then - Le Circuit Breaker devrait être ouvert
        // Note: Il peut y avoir un délai avant l'ouverture
        Thread.sleep(1000); // Attendre un peu pour la transition

        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        // Les requêtes suivantes devraient échouer rapidement sans atteindre le backend
        webTestClient.get()
                .uri("/api/search?q=test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .exchange()
                .expectStatus().isEqualTo(503) // Service Unavailable
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("CIRCUIT_BREAKER_OPEN");
    }

    @Test
    @DisplayName("Devrait rester fermé quand le taux d'échec est bas")
    void shouldStayClosedWhenFailureRateIsLow() {
        // Given - Mock un service qui réussit la plupart du temps
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        // Faire quelques échecs puis beaucoup de succès
        for (int i = 0; i < 3; i++) {
            // Une requête qui échoue occasionnellement
            if (i < 2) {
                stubFor(get(urlEqualTo("/search"))
                        .willReturn(aResponse()
                                .withStatus(500)));
            } else {
                stubFor(get(urlEqualTo("/search"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("{}")));
            }

            webTestClient.get()
                    .uri("/api/search?q=test")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                    .exchange();
        }

        // Faire beaucoup de succès
        for (int i = 0; i < 20; i++) {
            stubFor(get(urlEqualTo("/search"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withBody("{}")));

            webTestClient.get()
                    .uri("/api/search?q=test")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                    .exchange()
                    .expectStatus().isOk();
        }

        // Then - Le Circuit Breaker devrait rester fermé
        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    @DisplayName("Devrait ouvrir le Circuit Breaker pour les appels lents")
    void shouldOpenCircuitBreakerForSlowCalls() throws InterruptedException {
        // Given - Mock un service très lent
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(3000) // 3 secondes > seuil de 2 secondes
                        .withBody("{}")));

        // When - Faire assez de requêtes lentes
        for (int i = 0; i < 10; i++) {
            webTestClient.get()
                    .uri("/api/search?q=test")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                    .exchange();
        }

        // Then - Le Circuit Breaker devrait s'ouvrir pour appels lents
        Thread.sleep(1000); // Attendre pour la transition

        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    @DisplayName("Devrait passer à HALF_OPEN après le temps d'attente")
    void shouldTransitionToHalfOpenAfterWaitDuration() throws InterruptedException {
        // Given - Ouvrir le Circuit Breaker
        searchCircuitBreaker.transitionToOpenState();
        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        // When - Attendre le temps d'attente
        Thread.sleep(6000); // WaitDurationInOpenState est 5 secondes

        // Then - Le Circuit Breaker devrait être en HALF_OPEN
        // Note: La transition automatique est activée
        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
    }

    @Test
    @DisplayName("Devrait refermer le Circuit Breaker après des succès en HALF_OPEN")
    void shouldCloseCircuitBreakerAfterSuccessInHalfOpen() throws InterruptedException {
        // Given - Mettre le Circuit Breaker en HALF_OPEN
        searchCircuitBreaker.transitionToHalfOpenState();
        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);

        // Mock des réponses réussies
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        // When - Faire des requêtes qui réussissent
        for (int i = 0; i < 3; i++) { // PermittedNumberOfCallsInHalfOpenState = 3
            webTestClient.get()
                    .uri("/api/search?q=test")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                    .exchange()
                    .expectStatus().isOk();
        }

        // Then - Le Circuit Breaker devrait se refermer
        Thread.sleep(100); // Petite attente pour la transition
        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    @DisplayName("Devrait rouvrir le Circuit Breaker après des échecs en HALF_OPEN")
    void shouldReopenCircuitBreakerAfterFailuresInHalfOpen() throws InterruptedException {
        // Given - Mettre le Circuit Breaker en HALF_OPEN
        searchCircuitBreaker.transitionToHalfOpenState();
        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);

        // Mock des réponses en échec
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // When - Faire des requêtes qui échouent
        for (int i = 0; i < 3; i++) { // PermittedNumberOfCallsInHalfOpenState = 3
            webTestClient.get()
                    .uri("/api/search?q=test")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                    .exchange()
                    .expectStatus().is5xxServerError();
        }

        // Then - Le Circuit Breaker devrait se rouvrir
        Thread.sleep(100); // Petite attente pour la transition
        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    @DisplayName("Devrait déclencher le fallback quand le Circuit Breaker est ouvert")
    void shouldTriggerFallbackWhenCircuitBreakerIsOpen() {
        // Given - Circuit Breaker ouvert
        searchCircuitBreaker.transitionToOpenState();
        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        // Mock le fallback endpoint
        stubFor(get(urlEqualTo("/fallback/search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("""
                                {
                                    "message": "Service temporairement indisponible",
                                    "fallback": true
                                }
                                """)));

        // When
        webTestClient.get()
                .uri("/api/search?q=test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.fallback").isEqualTo(true)
                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    @DisplayName("Devrait ignorer les erreurs 4xx pour le Circuit Breaker")
    void shouldIgnore4xxErrorsForCircuitBreaker() {
        // Given - Mock des erreurs client (4xx)
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(400) // Bad Request - erreur client
                        .withBody("Bad Request")));

        // When - Faire plusieurs requêtes avec erreurs client
        for (int i = 0; i < 20; i++) {
            webTestClient.get()
                    .uri("/api/search?q=test")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        // Then - Le Circuit Breaker ne devrait PAS s'ouvrir
        // (les erreurs 4xx sont ignorées dans la configuration)
        assertThat(searchCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    @DisplayName("Devrait avoir des Circuit Breakers différents pour différents services")
    void shouldHaveDifferentCircuitBreakersForDifferentServices() {
        CircuitBreaker userCircuitBreaker = circuitBreakerRegistry.circuitBreaker("userCircuitBreaker");
        CircuitBreaker geoCircuitBreaker = circuitBreakerRegistry.circuitBreaker("geoCircuitBreaker");

        assertThat(searchCircuitBreaker).isNotNull();
        assertThat(userCircuitBreaker).isNotNull();
        assertThat(geoCircuitBreaker).isNotNull();

        assertThat(searchCircuitBreaker).isNotSameAs(userCircuitBreaker);
        assertThat(searchCircuitBreaker).isNotSameAs(geoCircuitBreaker);
    }

    @Test
    @DisplayName("Devrait suivre les métriques du Circuit Breaker")
    void shouldTrackCircuitBreakerMetrics() {
        // Faire quelques requêtes
        stubFor(get(urlEqualTo("/search"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{}")));

        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                    .uri("/api/search?q=test")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                    .exchange()
                    .expectStatus().isOk();
        }

        // Vérifier les métriques
        CircuitBreaker.Metrics metrics = searchCircuitBreaker.getMetrics();
        assertThat(metrics.getNumberOfSuccessfulCalls()).isGreaterThan(0);
        assertThat(metrics.getFailureRate()).isGreaterThanOrEqualTo(0);
        assertThat(metrics.getNumberOfBufferedCalls()).isGreaterThan(0);
    }
}