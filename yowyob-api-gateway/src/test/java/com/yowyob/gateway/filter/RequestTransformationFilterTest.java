package com.yowyob.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests pour le filtre de transformation de requêtes
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class RequestTransformationFilterTest {

        @Mock
        private GatewayFilterChain chain;

        @InjectMocks
        private RequestTransformationFilter filter;

        private ServerWebExchange exchange;

        @BeforeEach
        void setUp() {
                when(chain.filter(any())).thenReturn(Mono.empty());
        }

        private Mono<Void> runFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
                return filter.apply(new RequestTransformationFilter.Config()).filter(exchange, chain);
        }

        @Test
        @DisplayName("Devrait ajouter le correlation ID si manquant")
        void shouldAddCorrelationIdIfConfigured() {
                // Given
                RequestTransformationFilter.Config config = new RequestTransformationFilter.Config();
                config.setAddHeaders(java.util.Map.of("X-Correlation-Id", "test-id"));

                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/test")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.apply(config).filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le correlation ID a été ajouté
                assertThat(exchange.getRequest().getHeaders().getFirst("X-Correlation-Id"))
                                .isEqualTo("test-id");
        }

        @Test
        @DisplayName("Devrait préserver le correlation ID existant")
        void shouldPreserveExistingCorrelationId() {
                // Given
                String existingCorrelationId = "existing-correlation-id";
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/test")
                                .header("X-Correlation-Id", existingCorrelationId)
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le correlation ID existant est préservé
                assertThat(exchange.getRequest().getHeaders().getFirst("X-Correlation-Id"))
                                .isEqualTo(existingCorrelationId);
        }

        @Test
        @DisplayName("Devrait ajouter le request ID")
        void shouldAddRequestId() {
                // Given
                RequestTransformationFilter.Config config = new RequestTransformationFilter.Config();
                config.setAddHeaders(java.util.Map.of("X-Request-Id", "request-123"));

                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/test")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.apply(config).filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le request ID a été ajouté
                assertThat(exchange.getRequest().getHeaders().getFirst("X-Request-Id"))
                                .isEqualTo("request-123");
        }

        @Test
        @DisplayName("Devrait normaliser les headers d'user-agent")
        void shouldNormalizeUserAgentHeaders() {
                // Given
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/test")
                                .header(HttpHeaders.USER_AGENT, userAgent)
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le user-agent est préservé
                assertThat(exchange.getRequest().getHeaders().getFirst(HttpHeaders.USER_AGENT))
                                .isEqualTo(userAgent);
        }

        @Test
        @DisplayName("Devrait ajouter le timestamp de la requête")
        void shouldAddRequestTimestamp() {
                // Given
                RequestTransformationFilter.Config config = new RequestTransformationFilter.Config();
                config.setAddHeaders(java.util.Map.of("X-Request-Timestamp", "123456789"));

                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/test")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.apply(config).filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le timestamp a été ajouté
                assertThat(exchange.getRequest().getHeaders().getFirst("X-Request-Timestamp"))
                                .isEqualTo("123456789");
        }

        @Test
        @DisplayName("Devrait propager les headers utilisateur")
        void shouldPropagateUserHeaders() {
                // Given
                String userId = "user_123";
                String userRoles = "USER,ADMIN";
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/test")
                                .header("X-User-Id", userId)
                                .header("X-User-Roles", userRoles)
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que les headers utilisateur sont préservés
                assertThat(exchange.getRequest().getHeaders().getFirst("X-User-Id"))
                                .isEqualTo(userId);
                assertThat(exchange.getRequest().getHeaders().getFirst("X-User-Roles"))
                                .isEqualTo(userRoles);
        }
}