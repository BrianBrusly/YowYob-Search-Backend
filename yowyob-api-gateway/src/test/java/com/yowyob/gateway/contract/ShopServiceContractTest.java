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
public class ShopServiceContractTest {

    @Pact(provider = "shop-service", consumer = "api-gateway")
    public RequestResponsePact searchProductsPact(PactDslWithProvider builder) {
        return builder
            .given("products exist in database")
            .uponReceiving("a request to search products")
            .path("/shop/products/search")
            .method("GET")
            .query("query=phone&category=electronics&page=1&size=20")
            .headers(Map.of(
                "X-User-Id", "user_123",
                "X-User-Roles", "USER",
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .headers(Map.of(
                "Content-Type", MediaType.APPLICATION_JSON_VALUE,
                "X-Total-Count", "15"
            ))
            .body("""
                {
                    "products": [
                        {
                            "id": "prod_1",
                            "name": "Smartphone X",
                            "description": "Latest smartphone model",
                            "price": 799.99,
                            "currency": "USD",
                            "category": "electronics",
                            "brand": "TechBrand",
                            "rating": 4.5,
                            "available": true
                        }
                    ],
                    "page": 1,
                    "size": 20,
                    "total": 15,
                    "totalPages": 1
                }
                """)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "searchProductsPact")
    void testSearchProducts(MockServer mockServer) {
        WebClient webClient = WebClient.builder()
            .baseUrl(mockServer.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        String response = webClient.get()
            .uri("/shop/products/search?query=phone&category=electronics&page=1&size=20")
            .header("X-User-Id", "user_123")
            .header("X-User-Roles", "USER")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(response).contains("\"total\":15");
        assertThat(response).contains("\"products\":[");
        assertThat(response).contains("\"id\":\"prod_1\"");
    }

    @Pact(provider = "shop-service", consumer = "api-gateway")
    public RequestResponsePact getProductDetailsPact(PactDslWithProvider builder) {
        return builder
            .given("product prod_1 exists")
            .uponReceiving("a request for product details")
            .path("/shop/products/prod_1")
            .method("GET")
            .headers(Map.of(
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .headers(Map.of(
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .body("""
                {
                    "id": "prod_1",
                    "name": "Smartphone X",
                    "description": "Latest smartphone model",
                    "price": 799.99,
                    "currency": "USD",
                    "category": "electronics",
                    "brand": "TechBrand",
                    "rating": 4.5,
                    "available": true
                }
                """)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getProductDetailsPact")
    void testGetProductDetails(MockServer mockServer) {
        WebClient webClient = WebClient.builder()
            .baseUrl(mockServer.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        String response = webClient.get()
            .uri("/shop/products/prod_1")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(response).contains("\"id\":\"prod_1\"");
        assertThat(response).contains("\"name\":\"Smartphone X\"");
        assertThat(response).contains("\"price\":799.99");
    }
}
