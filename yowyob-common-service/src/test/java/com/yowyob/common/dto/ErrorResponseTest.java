package com.yowyob.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour ErrorResponse
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("ErrorResponse Tests")
class ErrorResponseTest {

    @Test
    @DisplayName("Devrait créer une ErrorResponse basique")
    void shouldCreateBasicErrorResponse() {
        ErrorResponse response = ErrorResponse.of(400, "Bad Request", "Invalid input");

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getError()).isEqualTo("Bad Request");
        assertThat(response.getMessage()).isEqualTo("Invalid input");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Devrait créer une ErrorResponse avec path")
    void shouldCreateErrorResponseWithPath() {
        ErrorResponse response = ErrorResponse.of(404, "Not Found", "Resource not found", "/api/users/123");

        assertThat(response.getPath()).isEqualTo("/api/users/123");
    }

    @Test
    @DisplayName("Devrait ajouter un code d'erreur")
    void shouldAddErrorCode() {
        ErrorResponse response = ErrorResponse.of(400, "Bad Request", "Invalid input")
                .withErrorCode("VALIDATION_FAILED");

        assertThat(response.getErrorCode()).isEqualTo("VALIDATION_FAILED");
    }

    @Test
    @DisplayName("Devrait ajouter des erreurs de validation")
    void shouldAddValidationErrors() {
        List<ValidationError> validationErrors = List.of(
                ValidationError.of("email", "Invalid email format"),
                ValidationError.of("password", "Password too short")
        );

        ErrorResponse response = ErrorResponse.of(400, "Bad Request", "Validation failed")
                .withValidationErrors(validationErrors);

        assertThat(response.getValidationErrors()).hasSize(2);
        assertThat(response.getValidationErrors().get(0).getField()).isEqualTo("email");
    }

    @Test
    @DisplayName("Devrait ajouter des détails supplémentaires")
    void shouldAddAdditionalDetails() {
        Map<String, Object> details = Map.of(
                "userId", "123",
                "attemptCount", 3
        );

        ErrorResponse response = ErrorResponse.of(401, "Unauthorized", "Invalid credentials")
                .withDetails(details);

        assertThat(response.getDetails()).containsKeys("userId", "attemptCount");
        assertThat(response.getDetails().get("userId")).isEqualTo("123");
    }
}