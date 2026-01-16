package com.yowyob.gateway.filter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import io.micrometer.core.instrument.Tag;
import java.util.stream.StreamSupport;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour le filtre de métriques
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class MetricsFilterTest {

        @Mock
        private MeterRegistry meterRegistry;

        @Mock
        private Counter counter;

        @Mock
        private Timer timer;

        @Mock
        private GatewayFilterChain chain;

        @InjectMocks
        private MetricsFilter filter;

        private ServerWebExchange exchange;

        @BeforeEach
        void setUp() {
                when(chain.filter(any())).thenReturn(Mono.empty());
                when(meterRegistry.counter(anyString(), any(Iterable.class))).thenReturn(counter);
                when(meterRegistry.timer(anyString(), any(Iterable.class))).thenReturn(timer);
        }

        @Test
        @DisplayName("Devrait incrémenter le compteur de requêtes totales")
        void shouldIncrementTotalRequestsCounter() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le compteur est incrémenté
                verify(meterRegistry).counter(eq("gateway.requests.total"), any(Iterable.class));
                verify(counter).increment();
        }

        @Test
        @DisplayName("Devrait enregistrer la durée des requêtes réussies")
        void shouldRecordDurationForSuccessfulRequests() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search")
                                .build();
                exchange = MockServerWebExchange.from(request);

                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.OK);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le timer est appelé
                verify(timer).record(anyLong(), eq(TimeUnit.NANOSECONDS));
        }

        @Test
        @DisplayName("Devrait enregistrer les métriques pour les erreurs client (4xx)")
        void shouldRecordMetricsForClientErrors() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search")
                                .build();
                exchange = MockServerWebExchange.from(request);

                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.BAD_REQUEST);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que les métriques sont enregistrées avec le bon statut
                verify(meterRegistry).timer(eq("gateway.request.duration"),
                                argThat((Iterable<Tag> tags) -> StreamSupport.stream(tags.spliterator(), false)
                                                .anyMatch(tag -> tag.getKey().equals("status")
                                                                && tag.getValue().equals("400"))));
        }

        @Test
        @DisplayName("Devrait enregistrer les métriques pour les erreurs serveur (5xx)")
        void shouldRecordMetricsForServerErrors() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search")
                                .build();
                exchange = MockServerWebExchange.from(request);

                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que les métriques sont enregistrées avec le bon statut
                verify(meterRegistry).timer(eq("gateway.request.duration"),
                                argThat((Iterable<Tag> tags) -> StreamSupport.stream(tags.spliterator(), false)
                                                .anyMatch(tag -> tag.getKey().equals("status")
                                                                && tag.getValue().equals("500"))));
        }

        @Test
        @DisplayName("Devrait enregistrer les métriques pour les requêtes échouées")
        void shouldRecordMetricsForFailedRequests() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search")
                                .build();
                exchange = MockServerWebExchange.from(request);

                RuntimeException exception = new RuntimeException("Test exception");
                when(chain.filter(any())).thenReturn(Mono.error(exception));

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                .expectError(RuntimeException.class)
                                .verify();

                // Vérifier que les compteurs d'erreur sont incrémentés
                verify(meterRegistry).counter(eq("gateway.errors.total"), any(Iterable.class));
                verify(counter, atLeastOnce()).increment();
        }

        @Test
        @DisplayName("Devrait extraire les tags corrects de la requête")
        void shouldExtractCorrectTagsFromRequest() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search?q=test")
                                .header("X-User-Id", "user-123")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que les tags sont extraits correctement
                verify(meterRegistry).counter(eq("gateway.requests.total"),
                                argThat((Iterable<Tag> tags) -> StreamSupport.stream(tags.spliterator(), false)
                                                .anyMatch(tag -> tag.getKey().equals("method")
                                                                && tag.getValue().equals("GET"))
                                                &&
                                                StreamSupport.stream(tags.spliterator(), false)
                                                                .anyMatch(tag -> tag.getKey().equals("path") &&
                                                                                tag.getValue().equals("/api/search"))
                                                &&
                                                StreamSupport.stream(tags.spliterator(), false)
                                                                .anyMatch(tag -> tag.getKey().equals("user_type") &&
                                                                                tag.getValue().equals(
                                                                                                "authenticated"))));
        }

        @Test
        @DisplayName("Devrait normaliser les chemins pour les métriques")
        void shouldNormalizePathsForMetrics() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/users/123")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le chemin est normalisé
                verify(meterRegistry).counter(eq("gateway.requests.total"),
                                argThat((Iterable<Tag> tags) -> StreamSupport.stream(tags.spliterator(), false)
                                                .anyMatch(tag -> tag.getKey().equals("path") &&
                                                                tag.getValue().equals("/api/users/{id}"))));
        }

        @Test
        @DisplayName("Devrait gérer les utilisateurs anonymes")
        void shouldHandleAnonymousUsers() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le user_type est "anonymous"
                verify(meterRegistry).counter(eq("gateway.requests.total"),
                                argThat((Iterable<Tag> tags) -> StreamSupport.stream(tags.spliterator(), false)
                                                .anyMatch(tag -> tag.getKey().equals("user_type") &&
                                                                tag.getValue().equals("anonymous"))));
        }

        @Test
        @DisplayName("Devrait enregistrer la taille des réponses")
        void shouldRecordResponseSize() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search")
                                .build();
                exchange = MockServerWebExchange.from(request);

                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.OK);
                exchange.getResponse().getHeaders().setContentLength(1024L);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le summary de taille est enregistré
                verify(meterRegistry).summary(eq("gateway.response.size.bytes"), any(Iterable.class));
        }
}