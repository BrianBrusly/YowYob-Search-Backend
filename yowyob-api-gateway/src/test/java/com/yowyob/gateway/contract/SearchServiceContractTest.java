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

/**
 * Tests de contrat pour le Search Service
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@ExtendWith(PactConsumerTestExt.class)
class SearchServiceContractTest {

    @Pact(provider = "search-service", consumer = "api-gateway")
    public RequestResponsePact searchEndpointPact(PactDslWithProvider builder) {
        return builder
                .given("search service is available")
                .uponReceiving("a request to search with query")
                .path("/search")
                .method("GET")
                .query("q=test&page=1&size=10")
                .headers(Map.of(
                        "X-User-Id", "user_123",
                        "X-User-Roles", "USER",
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .headers(Map.of(
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .body("""
                        {
                            "results": [
                                {
                                    "id": "doc_1",
                                    "title": "Test Document 1",
                                    "content": "This is a test document",
                                    "score": 0.95
                                },
                                {
                                    "id": "doc_2",
                                    "title": "Test Document 2",
                                    "content": "Another test document",
                                    "score": 0.85
                                }
                            ],
                            "total": 2,
                            "page": 1,
                            "size": 10,
                            "totalPages": 1
                        }
                        """)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "searchEndpointPact")
    void testSearchEndpoint(MockServer mockServer) {
        // Given
        WebClient webClient = WebClient.builder()
                .baseUrl(mockServer.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        // When
        String response = webClient.get()
                .uri("/search?q=test&page=1&size=10")
                .header("X-User-Id", "user_123")
                .header("X-User-Roles", "USER")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Then
        assertThat(response).contains("\"total\":2");
        assertThat(response).contains("\"results\":[");
    }

    @Pact(provider = "search-service", consumer = "api-gateway")
    public RequestResponsePact searchEndpointErrorPact(PactDslWithProvider builder) {
        return builder
                .given("search service encounters an error")
                .uponReceiving("a request to search with invalid query")
                .path("/search")
                .method("GET")
                .query("q=")
                .willRespondWith()
                .status(HttpStatus.BAD_REQUEST.value())
                .headers(Map.of(
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .body("""
                        {
                            "status": 400,
                            "error": "Bad Request",
                            "message": "Query parameter 'q' is required",
                            "path": "/search"
                        }
                        """)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "searchEndpointErrorPact")
    void testSearchEndpointError(MockServer mockServer) {
        // Given
        WebClient webClient = WebClient.builder()
                .baseUrl(mockServer.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        // When
        String response = webClient.get()
                .uri("/search?q=")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Then
        assertThat(response).contains("\"status\":400");
        assertThat(response).contains("\"error\":\"Bad Request\"");
    }

    @Pact(provider = "search-service", consumer = "api-gateway")
    public RequestResponsePact searchEndpointTimeoutPact(PactDslWithProvider builder) {
        return builder
                .given("search service is slow to respond")
                .uponReceiving("a request that times out")
                .path("/search")
                .method("GET")
                .query("q=slow")
                .willRespondWith()
                .status(HttpStatus.REQUEST_TIMEOUT.value())
                .headers(Map.of(
                        "Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .body("""
                        {
                            "status": 408,
                            "error": "Request Timeout",
                            "message": "Search request timed out",
                            "path": "/search"
                        }
                        """)
                .toPact();
    }
}