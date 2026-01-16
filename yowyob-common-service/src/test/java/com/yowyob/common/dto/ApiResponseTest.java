package com.yowyob.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour ApiResponse
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("ApiResponse Tests")
class ApiResponseTest {

    @Test
    @DisplayName("Devrait créer une réponse de succès avec données")
    void shouldCreateSuccessResponseWithData() {
        String data = "test data";

        ApiResponse<String> response = ApiResponse.success(data);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(data);
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Devrait créer une réponse de succès avec message")
    void shouldCreateSuccessResponseWithMessage() {
        String data = "test data";
        String message = "Operation successful";

        ApiResponse<String> response = ApiResponse.success(data, message);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(data);
        assertThat(response.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Devrait créer une réponse d'erreur")
    void shouldCreateErrorResponse() {
        String errorMessage = "An error occurred";

        ApiResponse<String> response = ApiResponse.error(errorMessage);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo(errorMessage);
        assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("Devrait ajouter un correlationId")
    void shouldAddCorrelationId() {
        String correlationId = "corr-123";

        ApiResponse<String> response = ApiResponse.success("data")
                .withCorrelationId(correlationId);

        assertThat(response.getCorrelationId()).isEqualTo(correlationId);
    }

    @Test
    @DisplayName("Devrait ajouter des metadata")
    void shouldAddMetadata() {
        Metadata metadata = Metadata.withExecutionTime(100L);

        ApiResponse<String> response = ApiResponse.success("data")
                .withMetadata(metadata);

        assertThat(response.getMetadata()).isEqualTo(metadata);
        assertThat(response.getMetadata().getExecutionTimeMs()).isEqualTo(100L);
    }
}