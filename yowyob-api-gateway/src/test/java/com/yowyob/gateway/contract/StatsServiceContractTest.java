package com.yowyob.gateway.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
public class StatsServiceContractTest {

    @Pact(provider = "stats-service", consumer = "api-gateway")
    public RequestResponsePact getGlobalStatsPact(PactDslWithProvider builder) {
        return builder
            .given("stats data exists")
            .uponReceiving("a request for global statistics")
            .path("/stats/global")
            .method("GET")
            .query("period=LAST_30_DAYS")
            .headers(Map.of(
                "X-User-Id", "user_123",
                "X-User-Roles", "USER",
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .headers(Map.of(
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .body("""
                {
                    "period": "LAST_30_DAYS",
                    "summary": {
                        "totalUsers": 12500,
                        "activeUsers": 8500,
                        "totalSearches": 250000,
                        "successfulSearches": 245000
                    },
                    "updatedAt": "2023-12-31T23:59:59Z"
                }
                """)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getGlobalStatsPact")
    void testGetGlobalStats(MockServer mockServer) {
        WebClient webClient = WebClient.builder()
            .baseUrl(mockServer.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        String response = webClient.get()
            .uri("/stats/global?period=LAST_30_DAYS")
            .header("X-User-Id", "user_123")
            .header("X-User-Roles", "USER")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(response).contains("\"totalUsers\":12500");
        assertThat(response).contains("\"totalSearches\":250000");
        assertThat(response).contains("\"period\":\"LAST_30_DAYS\"");
    }

    @Pact(provider = "stats-service", consumer = "api-gateway")
    public RequestResponsePact getSearchStatsPact(PactDslWithProvider builder) {
        return builder
            .given("search stats data exists")
            .uponReceiving("a request for search statistics")
            .path("/stats/search")
            .method("GET")
            .query("period=LAST_7_DAYS")
            .headers(Map.of(
                "X-User-Id", "user_123",
                "X-User-Roles", "USER",
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .headers(Map.of(
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .body("""
                {
                    "period": "LAST_7_DAYS",
                    "totalSearches": 50000,
                    "successfulSearches": 49000,
                    "failedSearches": 1000,
                    "avgResponseTime": 245
                }
                """)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getSearchStatsPact")
    void testGetSearchStats(MockServer mockServer) {
        WebClient webClient = WebClient.builder()
            .baseUrl(mockServer.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        String response = webClient.get()
            .uri("/stats/search?period=LAST_7_DAYS")
            .header("X-User-Id", "user_123")
            .header("X-User-Roles", "USER")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(response).contains("\"totalSearches\":50000");
        assertThat(response).contains("\"avgResponseTime\":245");
    }

    @Pact(provider = "stats-service", consumer = "api-gateway")
    public RequestResponsePact getAdminStatsPact(PactDslWithProvider builder) {
        return builder
            .given("admin stats data exists")
            .uponReceiving("a request for admin statistics")
            .path("/stats/admin/revenue")
            .method("GET")
            .query("period=LAST_QUARTER")
            .headers(Map.of(
                "X-User-Id", "admin_1",
                "X-User-Roles", "ADMIN",
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .headers(Map.of(
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .body("""
                {
                    "period": "LAST_QUARTER",
                    "revenue": {
                        "total": 125000.50,
                        "currency": "USD"
                    },
                    "profit": {
                        "total": 40000.25,
                        "margin": 32.0
                    }
                }
                """)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getAdminStatsPact")
    void testGetAdminStats(MockServer mockServer) {
        WebClient webClient = WebClient.builder()
            .baseUrl(mockServer.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        String response = webClient.get()
            .uri("/stats/admin/revenue?period=LAST_QUARTER")
            .header("X-User-Id", "admin_1")
            .header("X-User-Roles", "ADMIN")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(response).contains("\"revenue\":{");
        assertThat(response).contains("\"total\":125000.50");
        assertThat(response).contains("\"profit\":{");
    }
}
