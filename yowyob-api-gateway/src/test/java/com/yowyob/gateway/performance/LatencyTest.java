package com.yowyob.gateway.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de latence pour le Gateway
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class LatencyTest {

    @Autowired
    private WebTestClient webTestClient;

    private List<Long> latencies;

    @BeforeEach
    void setUp() {
        latencies = new ArrayList<>();
    }

    @Test
    @DisplayName("Devrait avoir une latence p95 inférieure à 500ms pour les requêtes simples")
    void shouldHaveP95LatencyUnder500msForSimpleRequests() throws Exception {
        // Given
        int requestCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // When - Exécuter les requêtes en parallèle
        List<CompletableFuture<Long>> futures = IntStream.range(0, requestCount)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    long startTime = System.currentTimeMillis();
                    webTestClient.get()
                            .uri("/actuator/health")
                            .exchange()
                            .expectStatus().isOk();
                    return System.currentTimeMillis() - startTime;
                }, executor))
                .collect(Collectors.toList());

        // Collecter les latences
        latencies = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Then - Calculer les percentiles
        latencies.sort(Long::compareTo);

        long p50 = latencies.get((int) (requestCount * 0.50));
        long p95 = latencies.get((int) (requestCount * 0.95));
        long p99 = latencies.get((int) (requestCount * 0.99));

        System.out.println("Latency Results:");
        System.out.println("P50: " + p50 + "ms");
        System.out.println("P95: " + p95 + "ms");
        System.out.println("P99: " + p99 + "ms");
        System.out.println("Min: " + latencies.get(0) + "ms");
        System.out.println("Max: " + latencies.get(latencies.size() - 1) + "ms");

        // Assertions
        assertThat(p95).isLessThan(500); // p95 < 500ms
        assertThat(p99).isLessThan(1000); // p99 < 1s
    }

    @Test
    @DisplayName("Devrait avoir une latence constante sous charge")
    void shouldHaveConsistentLatencyUnderLoad() throws Exception {
        // Given
        int concurrentUsers = 50;
        int requestsPerUser = 20;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);

        // When - Simulation de charge soutenue
        List<CompletableFuture<List<Long>>> userFutures = IntStream.range(0, concurrentUsers)
                .mapToObj(userId -> CompletableFuture.supplyAsync(() -> {
                    List<Long> userLatencies = new ArrayList<>();
                    for (int i = 0; i < requestsPerUser; i++) {
                        long startTime = System.currentTimeMillis();
                        webTestClient.get()
                                .uri("/actuator/health")
                                .exchange()
                                .expectStatus().isOk();
                        userLatencies.add(System.currentTimeMillis() - startTime);

                        // Petit délai entre les requêtes
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    return userLatencies;
                }, executor))
                .collect(Collectors.toList());

        // Collecter toutes les latences
        latencies = userFutures.stream()
                .flatMap(future -> future.join().stream())
                .collect(Collectors.toList());

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        // Then - Analyser la distribution
        double average = latencies.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        double stdDev = Math.sqrt(
                latencies.stream()
                        .mapToDouble(l -> Math.pow(l - average, 2))
                        .average()
                        .orElse(0.0));

        System.out.println("Load Test Results:");
        System.out.println("Total requests: " + latencies.size());
        System.out.println("Average latency: " + average + "ms");
        System.out.println("Std deviation: " + stdDev + "ms");

        // La déviation standard ne devrait pas être trop élevée
        assertThat(stdDev).isLessThan(average * 0.5); // Moins de 50% de la moyenne
    }

    @Test
    @DisplayName("Devrait mesurer l'impact du cache sur la latence")
    void shouldMeasureCacheImpactOnLatency() throws Exception {
        // Given
        String testQuery = "cache-test-query";
        List<Long> cacheMissLatencies = new ArrayList<>();
        List<Long> cacheHitLatencies = new ArrayList<>();

        // When - Première requête (cache miss)
        for (int i = 0; i < 10; i++) {
            long startTime = System.currentTimeMillis();
            webTestClient.get()
                    .uri("/api/search?q=" + testQuery + i)
                    .exchange()
                    .expectStatus().isOk();
            cacheMissLatencies.add(System.currentTimeMillis() - startTime);
        }

        // Deuxième requête (cache hit)
        for (int i = 0; i < 10; i++) {
            long startTime = System.currentTimeMillis();
            webTestClient.get()
                    .uri("/api/search?q=" + testQuery + i)
                    .exchange()
                    .expectStatus().isOk();
            cacheHitLatencies.add(System.currentTimeMillis() - startTime);
        }

        // Then - Comparer les latences
        double avgCacheMiss = cacheMissLatencies.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        double avgCacheHit = cacheHitLatencies.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        System.out.println("Cache Performance Results:");
        System.out.println("Average cache miss latency: " + avgCacheMiss + "ms");
        System.out.println("Average cache hit latency: " + avgCacheHit + "ms");
        System.out.println("Improvement: " + (avgCacheMiss - avgCacheHit) + "ms");
        System.out.println("Improvement %: " +
                ((avgCacheMiss - avgCacheHit) / avgCacheMiss * 100) + "%");

        // Le cache devrait améliorer significativement la latence
        assertThat(avgCacheHit).isLessThan(avgCacheMiss * 0.5); // Au moins 50% plus rapide
    }

    @Test
    @DisplayName("Devrait mesurer la latence avec différents types de requêtes")
    void shouldMeasureLatencyWithDifferentRequestTypes() throws Exception {
        // Given
        List<Long> healthLatencies = new ArrayList<>();
        List<Long> searchLatencies = new ArrayList<>();
        List<Long> authLatencies = new ArrayList<>();

        // When - Mesurer différents endpoints
        for (int i = 0; i < 20; i++) {
            // Health endpoint (le plus simple)
            long startTime = System.currentTimeMillis();
            webTestClient.get()
                    .uri("/actuator/health")
                    .exchange()
                    .expectStatus().isOk();
            healthLatencies.add(System.currentTimeMillis() - startTime);

            // Search endpoint (avec cache potentiel)
            startTime = System.currentTimeMillis();
            webTestClient.get()
                    .uri("/api/search?q=test" + i)
                    .exchange()
                    .expectStatus().isOk();
            searchLatencies.add(System.currentTimeMillis() - startTime);

            // Auth endpoint (avec validation JWT)
            startTime = System.currentTimeMillis();
            webTestClient.post()
                    .uri("/api/auth/login")
                    .bodyValue("{\"email\":\"test@test.com\",\"password\":\"test\"}")
                    .exchange();
            authLatencies.add(System.currentTimeMillis() - startTime);
        }

        // Then - Analyser les différences
        System.out.println("Endpoint Comparison:");
        System.out.println("Health avg: " + average(healthLatencies) + "ms");
        System.out.println("Search avg: " + average(searchLatencies) + "ms");
        System.out.println("Auth avg: " + average(authLatencies) + "ms");

        // Les endpoints plus complexes devraient être plus lents
        assertThat(average(authLatencies)).isGreaterThan(average(healthLatencies));
        assertThat(average(searchLatencies)).isGreaterThan(average(healthLatencies));
    }

    private double average(List<Long> values) {
        return values.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
    }
}