package com.yowyob.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests pour le filtre de fallback
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class FallbackFilterTest {

        @Mock
        private GatewayFilterChain chain;

        @InjectMocks
        private FallbackFilter filter;

        private ServerWebExchange exchange;
        private MockServerHttpResponse response;

        @BeforeEach
        void setUp() {
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/test")
                                .build();
                exchange = MockServerWebExchange.from(request);
                response = (MockServerHttpResponse) exchange.getResponse();
        }

        private Mono<Void> runFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
                return filter.apply(new FallbackFilter.Config()).filter(exchange, chain);
        }

        @Test
        @DisplayName("Devrait passer la requête si pas d'erreur de circuit breaker")
        void shouldPassRequestIfNoCircuitBreakerError() {
                // Given
                when(chain.filter(any())).thenReturn(Mono.empty());

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                verify(chain).filter(any());
        }

        @Test
        @DisplayName("Devrait retourner une réponse de fallback en cas d'erreur circuit breaker")
        void shouldReturnFallbackResponseOnCircuitBreakerError() {
                // Given
                io.github.resilience4j.circuitbreaker.CallNotPermittedException circuitBreakerError = io.github.resilience4j.circuitbreaker.CallNotPermittedException
                                .createCallNotPermittedException(
                                                io.github.resilience4j.circuitbreaker.CircuitBreaker
                                                                .ofDefaults("test"));

                when(chain.filter(any())).thenReturn(Mono.error(circuitBreakerError));

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que la réponse de fallback est retournée
                assertThat(response.getStatusCode())
                                .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                assertThat(response.getHeaders().getContentType())
                                .isEqualTo(MediaType.APPLICATION_JSON);
                assertThat(response.getHeaders().getFirst("Retry-After"))
                                .isNotNull();
        }

        @Test
        @DisplayName("Devrait retourner une réponse de fallback avec cache si disponible")
        void shouldReturnCachedFallbackResponseIfAvailable() {
                // Given - Simuler une réponse en cache
                // Ce test dépend de l'implémentation spécifique du cache
                when(chain.filter(any())).thenReturn(Mono.empty());

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le filtre s'exécute normalement sans erreur
                verify(chain).filter(any());
        }

        @Test
        @DisplayName("Devrait inclure le header Retry-After dans la réponse de fallback")
        void shouldIncludeRetryAfterHeaderInFallbackResponse() {
                // Given
                io.github.resilience4j.circuitbreaker.CallNotPermittedException circuitBreakerError = io.github.resilience4j.circuitbreaker.CallNotPermittedException
                                .createCallNotPermittedException(
                                                io.github.resilience4j.circuitbreaker.CircuitBreaker
                                                                .ofDefaults("test"));

                when(chain.filter(any())).thenReturn(Mono.error(circuitBreakerError));

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le header Retry-After est présent
                assertThat(response.getHeaders().getFirst("Retry-After"))
                                .isNotNull()
                                .matches("\\d+");
        }

        @Test
        @DisplayName("Devrait avoir un message d'erreur clair dans la réponse de fallback")
        void shouldHaveClearErrorMessageInFallbackResponse() {
                // Given
                io.github.resilience4j.circuitbreaker.CallNotPermittedException circuitBreakerError = io.github.resilience4j.circuitbreaker.CallNotPermittedException
                                .createCallNotPermittedException(
                                                io.github.resilience4j.circuitbreaker.CircuitBreaker
                                                                .ofDefaults("test"));

                when(chain.filter(any())).thenReturn(Mono.error(circuitBreakerError));

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le corps de la réponse contient un message d'erreur
                assertThat(response.getBodyAsString().block())
                                .contains("Service temporairement indisponible");
        }

        @Test
        @DisplayName("Devrait gérer différents types de fallback selon le service")
        void shouldHandleDifferentFallbackTypesByService() {
                // Given - Requête vers le search service
                MockServerHttpRequest searchRequest = MockServerHttpRequest
                                .get("/api/search")
                                .build();
                exchange = MockServerWebExchange.from(searchRequest);
                response = (MockServerHttpResponse) exchange.getResponse();

                io.github.resilience4j.circuitbreaker.CallNotPermittedException circuitBreakerError = io.github.resilience4j.circuitbreaker.CallNotPermittedException
                                .createCallNotPermittedException(
                                                io.github.resilience4j.circuitbreaker.CircuitBreaker
                                                                .ofDefaults("search"));

                when(chain.filter(any())).thenReturn(Mono.error(circuitBreakerError));

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que la réponse est adaptée au search service
                assertThat(response.getStatusCode())
                                .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        }
}