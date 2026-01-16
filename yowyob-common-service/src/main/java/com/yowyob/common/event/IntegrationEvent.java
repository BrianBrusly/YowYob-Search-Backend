package com.yowyob.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Événement d'intégration inter-services
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Représente un événement échangé entre microservices
 * Publié sur Kafka pour communication distribuée
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class IntegrationEvent extends BaseEvent {

    private String sourceService;
    private String destinationService;
    private String correlationId;

    protected IntegrationEvent(String eventType, String sourceService,
                               String destinationService, String correlationId) {
        super(eventType);
        this.sourceService = sourceService;
        this.destinationService = destinationService;
        this.correlationId = correlationId;
    }

    protected IntegrationEvent(String eventId, Instant timestamp, String eventType,
                               String sourceService, String destinationService,
                               String correlationId) {
        super(eventId, timestamp, eventType);
        this.sourceService = sourceService;
        this.destinationService = destinationService;
        this.correlationId = correlationId;
    }
}