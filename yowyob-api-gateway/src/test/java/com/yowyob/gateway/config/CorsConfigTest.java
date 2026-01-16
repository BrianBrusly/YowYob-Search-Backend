package com.yowyob.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;

import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_MAX_AGE;

/**
 * Tests pour la configuration CORS
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CorsConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Devrait accepter les requêtes CORS depuis les origines autorisées")
    void shouldAcceptCorsRequestsFromAllowedOrigins() {
        // Given
        String allowedOrigin = "http://localhost:3000";

        // When/Then
        webTestClient.options()
                .uri("/api/search")
                .header(ORIGIN, allowedOrigin)
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin)
                .expectHeader().exists(ACCESS_CONTROL_ALLOW_METHODS)
                .expectHeader().exists(ACCESS_CONTROL_ALLOW_HEADERS)
                .expectHeader().valueEquals(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                .expectHeader().exists(ACCESS_CONTROL_MAX_AGE);
    }

    @Test
    @DisplayName("Devrait rejeter les requêtes CORS depuis les origines non autorisées")
    void shouldRejectCorsRequestsFromDisallowedOrigins() {
        // Given
        String disallowedOrigin = "http://malicious-site.com";

        // When/Then
        webTestClient.options()
                .uri("/api/search")
                .header(ORIGIN, disallowedOrigin)
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    @Test
    @DisplayName("Devrait inclure les headers exposés dans la réponse")
    void shouldIncludeExposedHeadersInResponse() {
        webTestClient.get()
                .uri("/api/search")
                .header(ORIGIN, "http://localhost:3000")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Correlation-ID")
                .expectHeader().exists("X-Request-ID")
                .expectHeader().exists("X-Total-Count");
    }

    @Test
    @DisplayName("Devrait accepter les méthodes HTTP autorisées")
    void shouldAcceptAllowedHttpMethods() {
        String[] allowedMethods = { "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH" };

        for (String method : allowedMethods) {
            webTestClient.options()
                    .uri("/api/search")
                    .header(ORIGIN, "http://localhost:3000")
                    .header("Access-Control-Request-Method", method)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().value(ACCESS_CONTROL_ALLOW_METHODS,
                            value -> value.contains(method));
        }
    }

    @Test
    @DisplayName("Devrait avoir un max-age configuré")
    void shouldHaveConfiguredMaxAge() {
        webTestClient.options()
                .uri("/api/search")
                .header(ORIGIN, "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(ACCESS_CONTROL_MAX_AGE, "3600");
    }
}