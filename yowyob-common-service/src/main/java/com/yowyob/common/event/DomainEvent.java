package com.yowyob.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Événement de domaine métier
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Représente un événement survenu dans le domaine métier
 * Utilisé pour Event Sourcing et CQRS
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class DomainEvent extends BaseEvent {

    private String aggregateId;
    private Long version;

    protected DomainEvent(String eventType, String aggregateId, Long version) {
        super(eventType);
        this.aggregateId = aggregateId;
        this.version = version;
    }

    protected DomainEvent(String eventId, Instant timestamp, String eventType,
                          String aggregateId, Long version) {
        super(eventId, timestamp, eventType);
        this.aggregateId = aggregateId;
        this.version = version;
    }
}