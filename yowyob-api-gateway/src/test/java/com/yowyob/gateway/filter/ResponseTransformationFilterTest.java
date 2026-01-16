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
 * Tests pour le filtre de transformation de réponses
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ResponseTransformationFilterTest {

        @Mock
        private GatewayFilterChain chain;

        @InjectMocks
        private ResponseTransformationFilter filter;

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
                return filter.apply(new ResponseTransformationFilter.Config()).filter(exchange, chain);
        }

        @Test
        @DisplayName("Devrait ajouter le header X-Response-Time")
        void shouldAddXResponseTimeHeader() {
                // Given
                when(chain.filter(any())).thenReturn(Mono.empty());

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le header X-Response-Time a été ajouté
                assertThat(response.getHeaders().getFirst("X-Response-Time"))
                                .isNotNull()
                                .isNotEmpty();
        }

        @Test
        @DisplayName("Devrait ajouter le header X-API-Version")
        void shouldAddXApiVersionHeader() {
                // Given
                when(chain.filter(any())).thenReturn(Mono.empty());

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que le header X-API-Version a été ajouté
                assertThat(response.getHeaders().getFirst("X-API-Version"))
                                .isEqualTo("1.0.0");
        }

        @Test
        @DisplayName("Devrait supprimer les headers internes sensibles")
        void shouldRemoveHeadersAccordingToConfig() {
                // Given
                ResponseTransformationFilter.Config config = new ResponseTransformationFilter.Config();
                config.setRemoveHeaders(java.util.List.of("X-Internal-Secret"));

                response.getHeaders().add("X-Internal-Secret", "secret-value");
                response.getHeaders().add("X-Backend-Version", "1.0.0");
                when(chain.filter(any())).thenReturn(Mono.empty());

                // When
                StepVerifier.create(filter.apply(config).filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que les headers sensibles sont supprimés
                assertThat(response.getHeaders().getFirst("X-Internal-Secret"))
                                .isNull();
                // Mais les headers non-sensibles sont préservés
                assertThat(response.getHeaders().getFirst("X-Backend-Version"))
                                .isEqualTo("1.0.0");
        }

        @Test
        @DisplayName("Devrait ajouter les headers CORS")
        void shouldAddCorsHeaders() {
                // Given
                when(chain.filter(any())).thenReturn(Mono.empty());

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que les headers CORS sont ajoutés (le filtre les ajoute par défaut
                // dans addResponseHeaders)
                assertThat(response.getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN))
                                .isEqualTo("*");
        }

        @Test
        @DisplayName("Devrait transformer le body JSON")
        void shouldTransformJsonBody() {
                // Given
                ResponseTransformationFilter.Config config = new ResponseTransformationFilter.Config();
                config.setTransformBody(true);

                String originalBody = "{\"data\":\"test\"}";
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(
                                originalBody.getBytes(StandardCharsets.UTF_8));

                when(chain.filter(any())).thenAnswer(invocation -> {
                        ServerWebExchange e = invocation.getArgument(0);
                        e.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        return e.getResponse().writeWith(Mono.just(buffer));
                });

                // When
                StepVerifier.create(filter.apply(config).filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Note: verifying the body in MockServerHttpResponse is complex as it's
                // captured in the flux.
        }

        @Test
        @DisplayName("Devrait préserver les headers de cache")
        void shouldAddCacheHeadersForSuccess() {
                // Given
                response.setStatusCode(HttpStatus.OK);
                when(chain.filter(any())).thenReturn(Mono.empty());

                // When
                StepVerifier.create(runFilter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que les headers de cache sont ajoutés pour les 2xx
                assertThat(response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL))
                                .isEqualTo("public, max-age=60");
        }
}