package com.yowyob.common.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour IntegrationEvent
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("IntegrationEvent Tests")
class IntegrationEventTest {

    private static class TestIntegrationEvent extends IntegrationEvent {
        public TestIntegrationEvent(String eventType, String sourceService,
                                    String destinationService, String correlationId) {
            super(eventType, sourceService, destinationService, correlationId);
        }

        public TestIntegrationEvent(String eventId, Instant timestamp, String eventType,
                                    String sourceService, String destinationService,
                                    String correlationId) {
            super(eventId, timestamp, eventType, sourceService, destinationService, correlationId);
        }
    }

    @Test
    @DisplayName("Devrait créer un événement d'intégration")
    void shouldCreateIntegrationEvent() {
        String sourceService = "user-service";
        String destinationService = "notification-service";
        String correlationId = "corr-123";

        TestIntegrationEvent event = new TestIntegrationEvent(
                "USER_REGISTERED", sourceService, destinationService, correlationId
        );

        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getEventType()).isEqualTo("USER_REGISTERED");
        assertThat(event.getSourceService()).isEqualTo(sourceService);
        assertThat(event.getDestinationService()).isEqualTo(destinationService);
        assertThat(event.getCorrelationId()).isEqualTo(correlationId);
        assertThat(event.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Devrait créer un événement d'intégration avec tous les paramètres")
    void shouldCreateIntegrationEventWithAllParameters() {
        String eventId = "event-123";
        Instant timestamp = Instant.parse("2025-01-15T10:30:00Z");
        String sourceService = "user-service";
        String destinationService = "notification-service";
        String correlationId = "corr-123";

        TestIntegrationEvent event = new TestIntegrationEvent(
                eventId, timestamp, "USER_REGISTERED",
                sourceService, destinationService, correlationId
        );

        assertThat(event.getEventId()).isEqualTo(eventId);
        assertThat(event.getTimestamp()).isEqualTo(timestamp);
        assertThat(event.getEventType()).isEqualTo("USER_REGISTERED");
        assertThat(event.getSourceService()).isEqualTo(sourceService);
        assertThat(event.getDestinationService()).isEqualTo(destinationService);
        assertThat(event.getCorrelationId()).isEqualTo(correlationId);
    }
}