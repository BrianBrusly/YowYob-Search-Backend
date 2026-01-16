package com.yowyob.common.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour DomainEvent
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("DomainEvent Tests")
class DomainEventTest {

    private static class TestDomainEvent extends DomainEvent {
        public TestDomainEvent(String eventType, String aggregateId, Long version) {
            super(eventType, aggregateId, version);
        }

        public TestDomainEvent(String eventId, Instant timestamp, String eventType,
                               String aggregateId, Long version) {
            super(eventId, timestamp, eventType, aggregateId, version);
        }
    }

    @Test
    @DisplayName("Devrait créer un événement de domaine")
    void shouldCreateDomainEvent() {
        String aggregateId = "user-123";
        Long version = 1L;

        TestDomainEvent event = new TestDomainEvent("USER_CREATED", aggregateId, version);

        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getEventType()).isEqualTo("USER_CREATED");
        assertThat(event.getAggregateId()).isEqualTo(aggregateId);
        assertThat(event.getVersion()).isEqualTo(version);
        assertThat(event.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Devrait créer un événement de domaine avec tous les paramètres")
    void shouldCreateDomainEventWithAllParameters() {
        String eventId = "event-123";
        Instant timestamp = Instant.parse("2025-01-15T10:30:00Z");
        String aggregateId = "user-123";
        Long version = 2L;

        TestDomainEvent event = new TestDomainEvent(
                eventId, timestamp, "USER_UPDATED", aggregateId, version
        );

        assertThat(event.getEventId()).isEqualTo(eventId);
        assertThat(event.getTimestamp()).isEqualTo(timestamp);
        assertThat(event.getEventType()).isEqualTo("USER_UPDATED");
        assertThat(event.getAggregateId()).isEqualTo(aggregateId);
        assertThat(event.getVersion()).isEqualTo(version);
    }
}