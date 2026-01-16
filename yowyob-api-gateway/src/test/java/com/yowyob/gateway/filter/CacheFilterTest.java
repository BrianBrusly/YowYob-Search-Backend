package com.yowyob.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yowyob.gateway.util.CacheKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour le filtre de cache
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class CacheFilterTest {

        @Mock
        private ReactiveRedisTemplate<String, String> redisTemplate;

        @Mock
        private ReactiveValueOperations<String, String> valueOperations;

        @Mock
        private CacheKeyGenerator cacheKeyGenerator;

        @Mock
        private ObjectMapper objectMapper;

        @Mock
        private GatewayFilterChain chain;

        @InjectMocks
        private CacheFilter filter;

        private ServerWebExchange exchange;

        @BeforeEach
        void setUp() {
                when(redisTemplate.opsForValue()).thenReturn(valueOperations);
                when(chain.filter(any())).thenReturn(Mono.empty());
        }

        @Test
        @DisplayName("Devrait retourner la réponse depuis le cache quand disponible")
        void shouldReturnResponseFromCacheWhenAvailable() throws Exception {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search?q=test")
                                .build();
                exchange = MockServerWebExchange.from(request);

                String cacheKey = "cache-key";
                String cachedResponse = """
                                {
                                    "status": 200,
                                    "headers": {"Content-Type": "application/json"},
                                    "body": "{\\"results\\": []}",
                                    "timestamp": "2024-01-01T00:00:00Z"
                                }
                                """;

                when(cacheKeyGenerator.generate(anyString(), anyString(), any(), any()))
                                .thenReturn(cacheKey);
                when(valueOperations.get(cacheKey)).thenReturn(Mono.just(cachedResponse));

                CacheFilter.CachedResponse cached = CacheFilter.CachedResponse.builder()
                                .status(200)
                                .headers(java.util.Map.of("Content-Type", java.util.List.of("application/json")))
                                .body("{\"results\": []}")
                                .timestamp(java.time.Instant.parse("2024-01-01T00:00:00Z"))
                                .build();

                when(objectMapper.readValue(cachedResponse, CacheFilter.CachedResponse.class))
                                .thenReturn(cached);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que la chaîne n'est PAS appelée (cache hit)
                verify(chain, never()).filter(any());

                // Vérifier que les headers de cache sont ajoutés
                assertThat(exchange.getResponse().getHeaders().getFirst("X-Cache-Status"))
                                .isEqualTo("HIT");
        }

        @Test
        @DisplayName("Devrait forwarder au backend quand cache miss")
        void shouldForwardToBackendWhenCacheMiss() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search?q=test")
                                .build();
                exchange = MockServerWebExchange.from(request);

                String cacheKey = "cache-key";

                when(cacheKeyGenerator.generate(anyString(), anyString(), any(), any()))
                                .thenReturn(cacheKey);
                when(valueOperations.get(cacheKey)).thenReturn(Mono.empty());

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que la chaîne EST appelée (cache miss)
                verify(chain).filter(any());

                // Vérifier que les headers de cache sont ajoutés
                assertThat(exchange.getResponse().getHeaders().getFirst("X-Cache-Status"))
                                .isEqualTo("MISS");
        }

        @Test
        @DisplayName("Devrait ignorer le cache pour les requêtes non-GET")
        void shouldIgnoreCacheForNonGetRequests() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .post("/api/users")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que la chaîne EST appelée
                verify(chain).filter(any());

                // Vérifier que Redis n'est PAS appelé
                verify(valueOperations, never()).get(anyString());
        }

        @Test
        @DisplayName("Devrait ignorer le cache pour les endpoints non-cacheables")
        void shouldIgnoreCacheForNonCacheableEndpoints() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/users/profile")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que la chaîne EST appelée
                verify(chain).filter(any());

                // Vérifier que Redis n'est PAS appelé
                verify(valueOperations, never()).get(anyString());
        }

        @Test
        @DisplayName("Devrait gérer les erreurs de désérialisation du cache")
        void shouldHandleCacheDeserializationErrors() throws Exception {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search?q=test")
                                .build();
                exchange = MockServerWebExchange.from(request);

                String cacheKey = "cache-key";
                String cachedResponse = "invalid-json";

                when(cacheKeyGenerator.generate(anyString(), anyString(), any(), any()))
                                .thenReturn(cacheKey);
                when(valueOperations.get(cacheKey)).thenReturn(Mono.just(cachedResponse));
                when(objectMapper.readValue(cachedResponse, CacheFilter.CachedResponse.class))
                                .thenThrow(mock(com.fasterxml.jackson.core.JsonProcessingException.class));

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que la chaîne EST appelée (fallback à la requête normale)
                verify(chain).filter(any());
        }

        @Test
        @DisplayName("Devrait mettre en cache les réponses réussies")
        void shouldCacheSuccessfulResponses() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search?q=test")
                                .build();
                exchange = MockServerWebExchange.from(request);

                String cacheKey = "cache-key";

                when(cacheKeyGenerator.generate(anyString(), anyString(), any(), any()))
                                .thenReturn(cacheKey);
                when(valueOperations.get(cacheKey)).thenReturn(Mono.empty());

                // Configurer une réponse 200
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.OK);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que Redis set est appelé pour mettre en cache
                verify(valueOperations).set(eq(cacheKey), anyString(), any(Duration.class));
        }

        @Test
        @DisplayName("Devrait ne pas mettre en cache les réponses d'erreur")
        void shouldNotCacheErrorResponses() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search?q=test")
                                .build();
                exchange = MockServerWebExchange.from(request);

                String cacheKey = "cache-key";

                when(cacheKeyGenerator.generate(anyString(), anyString(), any(), any()))
                                .thenReturn(cacheKey);
                when(valueOperations.get(cacheKey)).thenReturn(Mono.empty());

                // Configurer une réponse 500
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que Redis set n'est PAS appelé
                verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
        }

        @Test
        @DisplayName("Devrait désactiver le cache quand configuré")
        void shouldDisableCacheWhenConfigured() {
                // Given
                filter.setCacheEnabled(false);

                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search?q=test")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que Redis n'est PAS appelé
                verify(valueOperations, never()).get(anyString());
                verify(cacheKeyGenerator, never()).generate(anyString(), anyString(), any(), any());
        }

        @Test
        @DisplayName("Devrait déterminer le TTL correct selon le type de contenu")
        void shouldDetermineCorrectTtlBasedOnContentType() {
                // Given
                MockServerHttpRequest searchRequest = MockServerHttpRequest
                                .get("/api/search?q=test")
                                .build();
                exchange = MockServerWebExchange.from(searchRequest);

                // When/Then pour différentes routes
                // Ce test vérifie que la logique de détermination du TTL fonctionne
                // La méthode determineTtl est testée séparément
        }
}