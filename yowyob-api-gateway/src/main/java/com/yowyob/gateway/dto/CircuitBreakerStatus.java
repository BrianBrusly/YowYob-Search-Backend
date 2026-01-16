package com.yowyob.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * DTO représentant le statut d'un Circuit Breaker
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
public class CircuitBreakerStatus {

    private String name;
    private String state;
    private Instant lastStateChange;
    private Map<String, Object> metrics;
    private Map<String, Object> config;

    public CircuitBreakerStatus() {
    }

    public CircuitBreakerStatus(String name, String state, Instant lastStateChange, Map<String, Object> metrics,
            Map<String, Object> config) {
        this.name = name;
        this.state = state;
        this.lastStateChange = lastStateChange;
        this.metrics = metrics;
        this.config = config;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String state;
        private Instant lastStateChange;
        private Map<String, Object> metrics;
        private Map<String, Object> config;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder lastStateChange(Instant lastStateChange) {
            this.lastStateChange = lastStateChange;
            return this;
        }

        public Builder metrics(Map<String, Object> metrics) {
            this.metrics = metrics;
            return this;
        }

        public Builder config(Map<String, Object> config) {
            this.config = config;
            return this;
        }

        public CircuitBreakerStatus build() {
            return new CircuitBreakerStatus(name, state, lastStateChange, metrics, config);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Instant getLastStateChange() {
        return lastStateChange;
    }

    public void setLastStateChange(Instant lastStateChange) {
        this.lastStateChange = lastStateChange;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public static class Metrics {
        private long totalCalls;
        private long successfulCalls;
        private long failedCalls;
        private long slowCalls;
        private float failureRate;
        private float slowCallRate;
        private long notPermittedCalls;

        public Metrics() {
        }

        public Metrics(long totalCalls, long successfulCalls, long failedCalls, long slowCalls, float failureRate,
                float slowCallRate, long notPermittedCalls) {
            this.totalCalls = totalCalls;
            this.successfulCalls = successfulCalls;
            this.failedCalls = failedCalls;
            this.slowCalls = slowCalls;
            this.failureRate = failureRate;
            this.slowCallRate = slowCallRate;
            this.notPermittedCalls = notPermittedCalls;
        }

        // Getters/Setters omitted if unused, but adding standard ones is safe
        public long getTotalCalls() {
            return totalCalls;
        }

        public void setTotalCalls(long totalCalls) {
            this.totalCalls = totalCalls;
        }
        // ... assuming these inner classes are unused by Service logic which uses Maps.
    }

    public static class Config {
        private int slidingWindowSize;
        private float failureRateThreshold;
        private float slowCallRateThreshold;
        private long slowCallDurationThreshold;
        private long waitDurationInOpenState;
        private int permittedCallsInHalfOpenState;

        public Config() {
        }
        // ... simple POJO
    }
}