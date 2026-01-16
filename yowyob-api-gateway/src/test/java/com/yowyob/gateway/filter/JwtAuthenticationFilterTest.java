package com.yowyob.gateway.filter;

import com.yowyob.common.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour le filtre d'authentification JWT
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

        @Mock
        private JwtService jwtService;

        @Mock
        private GatewayFilterChain chain;

        @InjectMocks
        private JwtAuthenticationFilter filter;

        private ServerWebExchange exchange;

        @BeforeEach
        void setUp() {
                when(chain.filter(any())).thenReturn(Mono.empty());
        }

        @Test
        @DisplayName("Devrait autoriser la requête avec un token JWT valide")
        void shouldAllowRequestWithValidToken() {
                // Given
                String token = "valid.jwt.token";
                String userId = "user_123";
                Set<String> roles = Set.of("USER");

                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/users/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .build();
                exchange = MockServerWebExchange.from(request);

                when(jwtService.validateToken(token)).thenReturn(true);
                when(jwtService.extractUserId(token)).thenReturn(userId);
                when(jwtService.extractRoles(token)).thenReturn(roles);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que la chaîne est appelée
                verify(chain).filter(any());

                // Vérifier que les headers utilisateur sont ajoutés
                assertThat(exchange.getRequest().getHeaders().getFirst("X-User-Id"))
                                .isEqualTo(userId);
                assertThat(exchange.getRequest().getHeaders().getFirst("X-User-Roles"))
                                .contains("USER");
                assertThat(exchange.getRequest().getHeaders().getFirst("X-Authenticated"))
                                .isEqualTo("true");
        }

        @Test
        @DisplayName("Devrait rejeter la requête avec un token invalide")
        void shouldRejectRequestWithInvalidToken() {
                // Given
                String token = "invalid.jwt.token";

                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/users/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .build();
                exchange = MockServerWebExchange.from(request);

                when(jwtService.validateToken(token)).thenReturn(false);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que la chaîne n'est PAS appelée
                verify(chain, never()).filter(any());

                // Vérifier que le statut est 401
                assertThat(exchange.getResponse().getStatusCode())
                                .isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Devrait rejeter la requête avec un token expiré")
        void shouldRejectRequestWithExpiredToken() {
                // Given
                String token = "expired.jwt.token";

                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/users/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .build();
                exchange = MockServerWebExchange.from(request);

                when(jwtService.validateToken(token))
                                .thenThrow(new io.jsonwebtoken.ExpiredJwtException(null, null, "Token expiré"));

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                verify(chain, never()).filter(any());
                assertThat(exchange.getResponse().getStatusCode())
                                .isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Devrait autoriser les endpoints publics sans token")
        void shouldAllowPublicEndpointsWithoutToken() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/search")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que la chaîne est appelée
                verify(chain).filter(any());

                // Vérifier que le service JWT n'est PAS appelé
                verify(jwtService, never()).validateToken(anyString());
        }

        @Test
        @DisplayName("Devrait rejeter les endpoints protégés sans token")
        void shouldRejectProtectedEndpointsWithoutToken() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/users/profile")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                verify(chain, never()).filter(any());
                assertThat(exchange.getResponse().getStatusCode())
                                .isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Devrait rejeter la requête avec un header Authorization mal formé")
        void shouldRejectRequestWithMalformedAuthHeader() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/users/profile")
                                .header(HttpHeaders.AUTHORIZATION, "InvalidPrefix token")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                verify(chain, never()).filter(any());
                assertThat(exchange.getResponse().getStatusCode())
                                .isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Devrait rejeter la requête sans header Authorization")
        void shouldRejectRequestWithoutAuthHeader() {
                // Given
                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/users/profile")
                                .build();
                exchange = MockServerWebExchange.from(request);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                verify(chain, never()).filter(any());
                assertThat(exchange.getResponse().getStatusCode())
                                .isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("Devrait extraire correctement les rôles du token")
        void shouldExtractRolesCorrectlyFromToken() {
                // Given
                String token = "valid.jwt.token";
                String userId = "user_123";
                Set<String> roles = Set.of("USER", "PREMIUM");

                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/users/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .build();
                exchange = MockServerWebExchange.from(request);

                when(jwtService.validateToken(token)).thenReturn(true);
                when(jwtService.extractUserId(token)).thenReturn(userId);
                when(jwtService.extractRoles(token)).thenReturn(roles);

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                // Vérifier que les rôles sont correctement formatés
                String rolesHeader = exchange.getRequest().getHeaders().getFirst("X-User-Roles");
                assertThat(rolesHeader).contains("USER", "PREMIUM");
        }

        @Test
        @DisplayName("Devrait gérer les exceptions lors de la validation du token")
        void shouldHandleExceptionsDuringTokenValidation() {
                // Given
                String token = "invalid.jwt.token";

                MockServerHttpRequest request = MockServerHttpRequest
                                .get("/api/users/profile")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .build();
                exchange = MockServerWebExchange.from(request);

                when(jwtService.validateToken(token))
                                .thenThrow(new RuntimeException("Erreur de validation"));

                // When
                StepVerifier.create(filter.filter(exchange, chain))
                                // Then
                                .verifyComplete();

                verify(chain, never()).filter(any());
                assertThat(exchange.getResponse().getStatusCode())
                                .isEqualTo(HttpStatus.UNAUTHORIZED);
        }
}