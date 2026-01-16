package com.yowyob.gateway.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import com.yowyob.gateway.dto.CircuitBreakerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service de gestion des Circuit Breakers
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@Service
public class CircuitBreakerManagementService {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerManagementService.class);

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerManagementService(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    private final Map<String, CircuitBreakerStateHistory> stateHistory = new ConcurrentHashMap<>();

    /**
     * Récupère le statut d'un Circuit Breaker
     * 
     * @param name Nom du Circuit Breaker
     * @return Statut du Circuit Breaker
     */
    public Mono<CircuitBreakerStatus> getCircuitBreakerStatus(String name) {
        return Mono.fromCallable(() -> {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);

            if (circuitBreaker == null) {
                throw new IllegalArgumentException("Circuit Breaker non trouvé: " + name);
            }

            CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();

            return CircuitBreakerStatus.builder()
                    .name(name)
                    .state(circuitBreaker.getState().name())
                    .lastStateChange(getLastStateChange(name))
                    .metrics(Map.of(
                            "totalCalls", metrics.getNumberOfBufferedCalls(),
                            "successfulCalls", metrics.getNumberOfSuccessfulCalls(),
                            "failedCalls", metrics.getNumberOfFailedCalls(),
                            "slowCalls", metrics.getNumberOfSlowCalls(),
                            "failureRate", metrics.getFailureRate(),
                            "slowCallRate", metrics.getSlowCallRate(),
                            "notPermittedCalls", metrics.getNumberOfNotPermittedCalls()))
                    .config(Map.of(
                            "slidingWindowSize", circuitBreaker.getCircuitBreakerConfig().getSlidingWindowSize(),
                            "failureRateThreshold", circuitBreaker.getCircuitBreakerConfig().getFailureRateThreshold(),
                            "slowCallRateThreshold",
                            circuitBreaker.getCircuitBreakerConfig().getSlowCallRateThreshold(),
                            "slowCallDurationThreshold",
                            circuitBreaker.getCircuitBreakerConfig().getSlowCallDurationThreshold().toMillis(),

                            "permittedCallsInHalfOpenState",
                            circuitBreaker.getCircuitBreakerConfig().getPermittedNumberOfCallsInHalfOpenState()))
                    .build();
        });
    }

    /**
     * Force l'ouverture d'un Circuit Breaker
     * 
     * @param name Nom du Circuit Breaker
     * @return Mono<Void>
     */
    public Mono<Void> forceOpen(String name) {
        return Mono.fromRunnable(() -> {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);

            if (circuitBreaker == null) {
                throw new IllegalArgumentException("Circuit Breaker non trouvé: " + name);
            }

            circuitBreaker.transitionToForcedOpenState();
            recordStateChange(name, "FORCED_OPEN");
            log.warn("Circuit Breaker {} forcé à l'état OPEN", name);
        });
    }

    /**
     * Force la fermeture d'un Circuit Breaker
     * 
     * @param name Nom du Circuit Breaker
     * @return Mono<Void>
     */
    public Mono<Void> forceClose(String name) {
        return Mono.fromRunnable(() -> {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);

            if (circuitBreaker == null) {
                throw new IllegalArgumentException("Circuit Breaker non trouvé: " + name);
            }

            circuitBreaker.transitionToClosedState();
            recordStateChange(name, "FORCED_CLOSED");
            log.warn("Circuit Breaker {} forcé à l'état CLOSED", name);
        });
    }

    /**
     * Réinitialise un Circuit Breaker
     * 
     * @param name Nom du Circuit Breaker
     * @return Mono<Void>
     */
    public Mono<Void> reset(String name) {
        return Mono.fromRunnable(() -> {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);

            if (circuitBreaker == null) {
                throw new IllegalArgumentException("Circuit Breaker non trouvé: " + name);
            }

            circuitBreaker.reset();
            recordStateChange(name, "RESET");
            log.info("Circuit Breaker {} réinitialisé", name);
        });
    }

    /**
     * Réinitialise les métriques d'un Circuit Breaker
     * 
     * @param name Nom du Circuit Breaker
     * @return Mono<Void>
     */
    public Mono<Void> resetMetrics(String name) {
        return Mono.fromRunnable(() -> {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(name);

            if (circuitBreaker == null) {
                throw new IllegalArgumentException("Circuit Breaker non trouvé: " + name);
            }

            circuitBreaker.reset();
            log.info("Métriques du Circuit Breaker {} réinitialisées", name);
        });
    }

    /**
     * Récupère la liste de tous les Circuit Breakers
     * 
     * @return Liste des Circuit Breakers
     */
    public Mono<List<String>> getAllCircuitBreakers() {
        return Mono.fromCallable(() -> circuitBreakerRegistry.getAllCircuitBreakers()
                .stream()
                .map(CircuitBreaker::getName)
                .collect(Collectors.toList()));
    }

    /**
     * Récupère l'historique des changements d'état
     * 
     * @param name Nom du Circuit Breaker
     * @return Historique des états
     */
    public Mono<List<CircuitBreakerStateHistory>> getStateHistory(String name) {
        return Mono.fromCallable(() -> {
            CircuitBreakerStateHistory history = stateHistory.get(name);
            return history != null ? List.of(history) : List.of();
        });
    }

    /**
     * Enregistre un changement d'état
     * 
     * @param name     Nom du Circuit Breaker
     * @param newState Nouvel état
     */
    private void recordStateChange(String name, String newState) {
        CircuitBreakerStateHistory history = stateHistory.computeIfAbsent(name,
                k -> new CircuitBreakerStateHistory(name));

        history.addStateChange(newState);
    }

    /**
     * Récupère le dernier changement d'état
     * 
     * @param name Nom du Circuit Breaker
     * @return Instant du dernier changement
     */
    private Instant getLastStateChange(String name) {
        CircuitBreakerStateHistory history = stateHistory.get(name);
        return history != null ? history.getLastChangeTime() : Instant.now();
    }

    /**
     * Classe pour l'historique des états
     */
    public static class CircuitBreakerStateHistory {
        private final String circuitBreakerName;
        private final List<StateChange> stateChanges = new java.util.ArrayList<>();

        public CircuitBreakerStateHistory(String circuitBreakerName) {
            this.circuitBreakerName = circuitBreakerName;
        }

        public void addStateChange(String state) {
            stateChanges.add(new StateChange(state, Instant.now()));
        }

        public Instant getLastChangeTime() {
            if (stateChanges.isEmpty()) {
                return Instant.now();
            }
            return stateChanges.get(stateChanges.size() - 1).timestamp;
        }

        public List<StateChange> getStateChanges() {
            return List.copyOf(stateChanges);
        }

        public String getCircuitBreakerName() {
            return circuitBreakerName;
        }

        public static class StateChange {
            private final String state;
            private final Instant timestamp;

            public StateChange(String state, Instant timestamp) {
                this.state = state;
                this.timestamp = timestamp;
            }

            public String getState() {
                return state;
            }

            public Instant getTimestamp() {
                return timestamp;
            }
        }
    }
}