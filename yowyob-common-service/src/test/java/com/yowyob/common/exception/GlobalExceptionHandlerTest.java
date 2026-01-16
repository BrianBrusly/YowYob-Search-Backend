package com.yowyob.common.exception;

import com.yowyob.common.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour GlobalExceptionHandler
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        exchange = MockServerWebExchange.from(request);
    }

    @Test
    @DisplayName("Devrait gérer ResourceNotFoundException")
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("User", "123");

        Mono<ResponseEntity<ErrorResponse>> responseMono =
                exceptionHandler.handleAppException(exception, exchange);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getMessage()).contains("User");
                    assertThat(response.getBody().getMessage()).contains("123");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Devrait gérer ValidationException")
    void shouldHandleValidationException() {
        ValidationException exception = new ValidationException("email", "Invalid email", "bad@email");

        Mono<ResponseEntity<ErrorResponse>> responseMono =
                exceptionHandler.handleValidationException(exception, exchange);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getValidationErrors()).isNotEmpty();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Devrait gérer UnauthorizedException")
    void shouldHandleUnauthorizedException() {
        UnauthorizedException exception = UnauthorizedException.invalidToken();

        Mono<ResponseEntity<ErrorResponse>> responseMono =
                exceptionHandler.handleAppException(exception, exchange);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    assertThat(response.getBody()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Devrait gérer les exceptions génériques")
    void shouldHandleGenericException() {
        Exception exception = new RuntimeException("Unexpected error");

        Mono<ResponseEntity<ErrorResponse>> responseMono =
                exceptionHandler.handleGenericException(exception, exchange);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getMessage()).isNotNull();
                })
                .verifyComplete();
    }
}