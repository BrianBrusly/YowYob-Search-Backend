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
public class CrawlerServiceContractTest {

    @Pact(provider = "crawler-service", consumer = "api-gateway")
    public RequestResponsePact startCrawlingJobPact(PactDslWithProvider builder) {
        return builder
            .given("crawler service is available")
            .uponReceiving("a request to start crawling job")
            .path("/crawler/jobs")
            .method("POST")
            .headers(Map.of(
                "X-User-Id", "admin_1",
                "X-User-Roles", "ADMIN",
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .body("""
                {
                    "name": "E-commerce sites crawl",
                    "type": "WEB_CRAWL",
                    "targets": ["https://example-shop.com"]
                }
                """, MediaType.APPLICATION_JSON_VALUE)
            .willRespondWith()
            .status(HttpStatus.ACCEPTED.value())
            .headers(Map.of(
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .body("""
                {
                    "id": "job_123",
                    "name": "E-commerce sites crawl",
                    "status": "SCHEDULED",
                    "type": "WEB_CRAWL",
                    "createdBy": "admin_1",
                    "message": "Crawling job scheduled successfully"
                }
                """)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "startCrawlingJobPact")
    void testStartCrawlingJob(MockServer mockServer) {
        WebClient webClient = WebClient.builder()
            .baseUrl(mockServer.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        String response = webClient.post()
            .uri("/crawler/jobs")
            .header("X-User-Id", "admin_1")
            .header("X-User-Roles", "ADMIN")
            .bodyValue("""
                {
                    "name": "E-commerce sites crawl",
                    "type": "WEB_CRAWL",
                    "targets": ["https://example-shop.com"]
                }
                """)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(response).contains("\"id\":\"job_123\"");
        assertThat(response).contains("\"status\":\"SCHEDULED\"");
        assertThat(response).contains("\"type\":\"WEB_CRAWL\"");
    }

    @Pact(provider = "crawler-service", consumer = "api-gateway")
    public RequestResponsePact listCrawlingJobsPact(PactDslWithProvider builder) {
        return builder
            .given("crawling jobs exist")
            .uponReceiving("a request to list crawling jobs")
            .path("/crawler/jobs")
            .method("GET")
            .query("page=1&size=10")
            .headers(Map.of(
                "X-User-Id", "admin_1",
                "X-User-Roles", "ADMIN",
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .headers(Map.of(
                "Content-Type", MediaType.APPLICATION_JSON_VALUE,
                "X-Total-Count", "25"
            ))
            .body("""
                {
                    "jobs": [
                        {
                            "id": "job_1",
                            "name": "Product catalog update",
                            "type": "WEB_CRAWL",
                            "status": "RUNNING",
                            "progress": 65.5
                        }
                    ],
                    "page": 1,
                    "size": 10,
                    "total": 25,
                    "totalPages": 3
                }
                """)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "listCrawlingJobsPact")
    void testListCrawlingJobs(MockServer mockServer) {
        WebClient webClient = WebClient.builder()
            .baseUrl(mockServer.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        String response = webClient.get()
            .uri("/crawler/jobs?page=1&size=10")
            .header("X-User-Id", "admin_1")
            .header("X-User-Roles", "ADMIN")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(response).contains("\"total\":25");
        assertThat(response).contains("\"jobs\":[");
        assertThat(response).contains("\"id\":\"job_1\"");
    }
}
