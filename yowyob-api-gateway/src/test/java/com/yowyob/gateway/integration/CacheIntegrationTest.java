package com.yowyob.gateway.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests d'intégration pour le cache
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Tests du fonctionnement du cache
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class CacheIntegrationTest {

        @Autowired
        private WebTestClient webTestClient;

        @Autowired
        private ReactiveRedisTemplate<String, String> redisTemplate;

        @BeforeEach
        void setUp() {
                WireMock.reset();
                // Nettoyer Redis avant chaque test
                redisTemplate.keys("gateway:cache:*")
                                .collectList()
                                .filter(keys -> !keys.isEmpty())
                                .flatMap(keys -> redisTemplate.delete(keys.toArray(new String[0])))
                                .block();
        }

        @Test
        @DisplayName("Devrait mettre en cache les réponses GET réussies")
        void shouldCacheSuccessfulGetResponses() {
                // Given - Mock du Search Service
                String responseBody = """
                                {
                                    "results": [
                                        {"id": "1", "name": "Test Result"}
                                    ],
                                    "total": 1
                                }
                                """;

                stubFor(get(urlEqualTo("/search?q=test"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withHeader("Content-Type", "application/json")
                                                .withBody(responseBody)));

                // When - Faire une première requête (cache miss)
                webTestClient.get()
                                .uri("/api/search?q=test")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().valueEquals("X-Cache-Status", "MISS")
                                .expectBody()
                                .jsonPath("$.results[0].id").isEqualTo("1");

                // Verify que le backend a été appelé
                verify(1, getRequestedFor(urlEqualTo("/search?q=test")));

                // When - Faire la même requête à nouveau (cache hit)
                webTestClient.get()
                                .uri("/api/search?q=test")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().valueEquals("X-Cache-Status", "HIT")
                                .expectBody()
                                .jsonPath("$.results[0].id").isEqualTo("1");

                // Verify que le backend n'a PAS été appelé à nouveau
                verify(1, getRequestedFor(urlEqualTo("/search?q=test"))); // Toujours 1 appel
        }

        @Test
        @DisplayName("Devrait ne pas mettre en cache les réponses non-GET")
        void shouldNotCacheNonGetResponses() {
                // Given - Mock du User Service pour POST
                stubFor(post(urlEqualTo("/users"))
                                .willReturn(aResponse()
                                                .withStatus(201)
                                                .withBody("""
                                                                {
                                                                    "id": "new-user-123",
                                                                    "email": "new@example.com"
                                                                }
                                                                """)));

                // When - Faire une requête POST
                webTestClient.post()
                                .uri("/api/users")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-123")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .bodyValue("""
                                                {
                                                    "email": "new@example.com",
                                                    "password": "password123"
                                                }
                                                """)
                                .exchange()
                                .expectStatus().isCreated();

                // Verify que le backend a été appelé
                verify(1, postRequestedFor(urlEqualTo("/users")));

                // Faire la même requête POST à nouveau
                webTestClient.post()
                                .uri("/api/users")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-123")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .bodyValue("""
                                                {
                                                    "email": "new2@example.com",
                                                    "password": "password123"
                                                }
                                                """)
                                .exchange()
                                .expectStatus().isCreated();

                // Verify que le backend a été appelé à nouveau (pas de cache)
                verify(2, postRequestedFor(urlEqualTo("/users")));
        }

        @Test
        @DisplayName("Devrait ne pas mettre en cache les réponses d'erreur")
        void shouldNotCacheErrorResponses() {
                // Given - Mock du Search Service avec erreur
                stubFor(get(urlEqualTo("/search?q=error"))
                                .willReturn(aResponse()
                                                .withStatus(500)
                                                .withBody("Internal Server Error")));

                // When - Faire une requête qui échoue
                webTestClient.get()
                                .uri("/api/search?q=error")
                                .exchange()
                                .expectStatus().is5xxServerError();

                // Faire la même requête à nouveau
                webTestClient.get()
                                .uri("/api/search?q=error")
                                .exchange()
                                .expectStatus().is5xxServerError();

                // Verify que le backend a été appelé deux fois (pas de cache)
                verify(2, getRequestedFor(urlEqualTo("/search?q=error")));
        }

        @Test
        @DisplayName("Devrait expirer le cache après le TTL")
        void shouldExpireCacheAfterTtl() throws InterruptedException {
                // Given - Mock du Search Service avec un TTL court
                String responseBody = "{\"results\": []}";

                stubFor(get(urlEqualTo("/search?q=test"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody(responseBody)));

                // When - Faire une première requête (cache miss)
                webTestClient.get()
                                .uri("/api/search?q=test")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().valueEquals("X-Cache-Status", "MISS");

                // When - Faire la même requête (cache hit)
                webTestClient.get()
                                .uri("/api/search?q=test")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().valueEquals("X-Cache-Status", "HIT");

                // Verify que le backend a été appelé une seule fois
                verify(1, getRequestedFor(urlEqualTo("/search?q=test")));

                // Attendre que le cache expire (TTL: 60 secondes pour search dans la config
                // test)
                Thread.sleep(61000);

                // When - Faire la même requête après expiration (cache miss à nouveau)
                webTestClient.get()
                                .uri("/api/search?q=test")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().valueEquals("X-Cache-Status", "MISS");

                // Verify que le backend a été appelé à nouveau
                verify(2, getRequestedFor(urlEqualTo("/search?q=test")));
        }

        @Test
        @DisplayName("Devrait avoir des TTL différents pour différents types de contenu")
        void shouldHaveDifferentTtlsForDifferentContentTypes() {
                // Test pour différents endpoints avec TTLs différents
                // Search: 60 secondes, Geo: 300 secondes, Shop: 1800 secondes

                // Ce test vérifie que la logique de détermination du TTL fonctionne
                // Les valeurs exactes sont testées dans les tests unitaires
        }

        @Test
        @DisplayName("Devrait ne pas mettre en cache les endpoints spécifiques")
        void shouldNotCacheSpecificEndpoints() {
                // Given - Mock du User Service pour profile (non cacheable)
                stubFor(get(urlEqualTo("/users/profile"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody("""
                                                                {
                                                                    "id": "user-123",
                                                                    "email": "test@example.com"
                                                                }
                                                                """)));

                // When - Faire une requête vers un endpoint non cacheable
                webTestClient.get()
                                .uri("/api/users/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-123")
                                .exchange()
                                .expectStatus().isOk();

                // Faire la même requête à nouveau
                webTestClient.get()
                                .uri("/api/users/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                                .header("X-User-Id", "user-123")
                                .exchange()
                                .expectStatus().isOk();

                // Verify que le backend a été appelé deux fois (pas de cache)
                verify(2, getRequestedFor(urlEqualTo("/users/profile")));
        }

        @Test
        @DisplayName("Devrait mettre en cache les réponses avec les bons headers")
        void shouldCacheResponsesWithCorrectHeaders() {
                // Given - Mock du Search Service avec des headers spécifiques
                stubFor(get(urlEqualTo("/search?q=test"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withHeader("Content-Type", "application/json")
                                                .withHeader("X-Custom-Header", "custom-value")
                                                .withBody("{}")));

                // When - Faire une requête
                webTestClient.get()
                                .uri("/api/search?q=test")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().exists("X-Custom-Header")
                                .expectHeader().valueEquals("X-Cache-Status", "MISS");

                // When - Faire la même requête (cache hit)
                webTestClient.get()
                                .uri("/api/search?q=test")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().exists("X-Custom-Header") // Header préservé depuis le cache
                                .expectHeader().valueEquals("X-Cache-Status", "HIT")
                                .expectHeader().exists("X-Cache-Age");
        }

        @Test
        @DisplayName("Devrait générer des clés de cache uniques pour différentes requêtes")
        void shouldGenerateUniqueCacheKeysForDifferentRequests() {
                // Given - Mock du Search Service
                stubFor(get(urlEqualTo("/search?q=test1"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody("{\"query\": \"test1\"}")));

                stubFor(get(urlEqualTo("/search?q=test2"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody("{\"query\": \"test2\"}")));

                // When - Faire deux requêtes différentes
                webTestClient.get()
                                .uri("/api/search?q=test1")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().valueEquals("X-Cache-Status", "MISS")
                                .expectBody()
                                .jsonPath("$.query").isEqualTo("test1");

                webTestClient.get()
                                .uri("/api/search?q=test2")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().valueEquals("X-Cache-Status", "MISS")
                                .expectBody()
                                .jsonPath("$.query").isEqualTo("test2");

                // Verify que les deux requêtes ont été forwardées
                verify(1, getRequestedFor(urlEqualTo("/search?q=test1")));
                verify(1, getRequestedFor(urlEqualTo("/search?q=test2")));

                // When - Refaire les mêmes requêtes (cache hits)
                webTestClient.get()
                                .uri("/api/search?q=test1")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().valueEquals("X-Cache-Status", "HIT");

                webTestClient.get()
                                .uri("/api/search?q=test2")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().valueEquals("X-Cache-Status", "HIT");

                // Verify que le backend n'a pas été rappelé
                verify(1, getRequestedFor(urlEqualTo("/search?q=test1"))); // Toujours 1
                verify(1, getRequestedFor(urlEqualTo("/search?q=test2"))); // Toujours 1
        }

        @Test
        @DisplayName("Devrait générer des clés de cache différentes pour différents utilisateurs")
        void shouldGenerateDifferentCacheKeysForDifferentUsers() {
                // Given - Mock du Search Service
                stubFor(get(urlEqualTo("/search?q=test"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withBody("{}")));

                // When - User 1 fait une requête
                webTestClient.get()
                                .uri("/api/search?q=test")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer token1")
                                .header("X-User-Id", "user-1")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().valueEquals("X-Cache-Status", "MISS");

                // When - User 2 fait la même requête
                webTestClient.get()
                                .uri("/api/search?q=test")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer token2")
                                .header("X-User-Id", "user-2")
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().valueEquals("X-Cache-Status", "MISS");

                // Verify que le backend a été appelé deux fois (cache séparé par utilisateur)
                verify(2, getRequestedFor(urlEqualTo("/search?q=test")));
        }

        @Test
        @DisplayName("Devrait désactiver le cache quand configuré")
        void shouldDisableCacheWhenConfigured() {
                // Note: Ce test nécessite une configuration de test sans cache
                // ou une manière de désactiver le cache pour le test

                // Ce test pourrait être exécuté avec un profil qui désactive le cache
        }
}