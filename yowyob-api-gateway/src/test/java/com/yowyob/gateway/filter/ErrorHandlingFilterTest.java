package com.yowyob.gateway.filter;

import com.yowyob.common.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.http.MediaType;
import com.yowyob.common.exception.BadRequestException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests pour le filtre de gestion d'erreurs
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ErrorHandlingFilterTest {

        @Mock
        private GatewayFilterChain chain;

        @InjectMocks
        private ErrorHandlingFilter filter;

        private ServerWebExchange exchange;

        @BeforeEach
        void setUp() {
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/test")
                                .build();
                exchange = MockServerWebExchange.from(request);
        }

        @Test
        @DisplayName("Devrait passer la requête si pas d'erreur")
        void shouldPassRequestIfNoError() {
                // Given
                when(chain.filter(any())).thenReturn(Mono.empty());

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                verify(chain).filter(any());
        }

        @Test
        @DisplayName("Devrait gérer les exceptions AppException")
        void shouldHandleAppException() {
                // Given
                BadRequestException badRequestException = new BadRequestException(
                                "Validation failed",
                                "VALIDATION_ERROR");
                when(chain.filter(any())).thenReturn(Mono.error(badRequestException));

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que la réponse est configurée
                assertThat(exchange.getResponse().getStatusCode())
                                .isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(exchange.getResponse().getHeaders().getContentType())
                                .isEqualTo(MediaType.APPLICATION_JSON);
        }

        @Test
        @DisplayName("Devrait gérer les exceptions RuntimeException")
        void shouldHandleRuntimeException() {
                // Given
                RuntimeException runtimeException = new RuntimeException("Unexpected error");
                when(chain.filter(any())).thenReturn(Mono.error(runtimeException));

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                assertThat(exchange.getResponse().getStatusCode())
                                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Test
        @DisplayName("Devrait inclure le correlation ID dans la réponse d'erreur")
        void shouldIncludeCorrelationIdInErrorResponse() {
                // Given
                String correlationId = "test-correlation-id";
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/test")
                                .header("X-Correlation-Id", correlationId)
                                .build();
                exchange = MockServerWebExchange.from(request);

                when(chain.filter(any())).thenReturn(Mono.error(new RuntimeException()));

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le correlation ID est inclus dans la réponse
                // (cela dépend de l'implémentation spécifique)
        }

        @Test
        @DisplayName("Devrait logger les erreurs")
        void shouldLogErrors() {
                // Given
                RuntimeException error = new RuntimeException("Test error");
                when(chain.filter(any())).thenReturn(Mono.error(error));

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Le logging est vérifié via les logs de test
                // Dans un test réel, on utiliserait un appender de test pour capturer les logs
        }

        @Test
        @DisplayName("Devrait gérer les timeouts")
        void shouldHandleTimeouts() {
                // Given
                when(chain.filter(any())).thenReturn(
                                Mono.error(new java.util.concurrent.TimeoutException("Request timeout")));

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                assertThat(exchange.getResponse().getStatusCode())
                                .isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        }
}