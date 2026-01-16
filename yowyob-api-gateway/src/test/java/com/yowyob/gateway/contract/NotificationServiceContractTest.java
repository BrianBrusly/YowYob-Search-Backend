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
public class NotificationServiceContractTest {

    @Pact(provider = "notification-service", consumer = "api-gateway")
    public RequestResponsePact sendNotificationPact(PactDslWithProvider builder) {
        return builder
            .given("notification service is available")
            .uponReceiving("a request to send notification")
            .path("/notifications/send")
            .method("POST")
            .headers(Map.of(
                "X-User-Id", "user_123",
                "X-User-Roles", "USER",
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .body("""
                {
                    "type": "EMAIL",
                    "recipient": "user@example.com",
                    "subject": "Test Notification",
                    "content": "This is a test notification",
                    "metadata": {
                        "priority": "HIGH",
                        "template": "welcome"
                    }
                }
                """, MediaType.APPLICATION_JSON_VALUE)
            .willRespondWith()
            .status(HttpStatus.ACCEPTED.value())
            .headers(Map.of(
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .body("""
                {
                    "id": "notif_123",
                    "status": "ACCEPTED",
                    "message": "Notification accepted for processing",
                    "timestamp": "2024-01-01T12:00:00Z"
                }
                """)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "sendNotificationPact")
    void testSendNotification(MockServer mockServer) {
        WebClient webClient = WebClient.builder()
            .baseUrl(mockServer.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        String response = webClient.post()
            .uri("/notifications/send")
            .header("X-User-Id", "user_123")
            .header("X-User-Roles", "USER")
            .bodyValue("""
                {
                    "type": "EMAIL",
                    "recipient": "user@example.com",
                    "subject": "Test Notification",
                    "content": "This is a test notification",
                    "metadata": {
                        "priority": "HIGH",
                        "template": "welcome"
                    }
                }
                """)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(response).contains("\"id\":\"notif_123\"");
        assertThat(response).contains("\"status\":\"ACCEPTED\"");
    }

    @Pact(provider = "notification-service", consumer = "api-gateway")
    public RequestResponsePact getNotificationsPact(PactDslWithProvider builder) {
        return builder
            .given("user has notifications")
            .uponReceiving("a request to get user notifications")
            .path("/notifications")
            .method("GET")
            .query("page=1&size=10&unreadOnly=true")
            .headers(Map.of(
                "X-User-Id", "user_123",
                "X-User-Roles", "USER",
                "Content-Type", MediaType.APPLICATION_JSON_VALUE
            ))
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .headers(Map.of(
                "Content-Type", MediaType.APPLICATION_JSON_VALUE,
                "X-Total-Count", "5"
            ))
            .body("""
                {
                    "notifications": [
                        {
                            "id": "notif_1",
                            "type": "EMAIL",
                            "subject": "Welcome to YowYob",
                            "content": "Welcome to our platform!",
                            "status": "READ",
                            "createdAt": "2024-01-01T10:00:00Z"
                        }
                    ],
                    "page": 1,
                    "size": 10,
                    "total": 5,
                    "totalPages": 1
                }
                """)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getNotificationsPact")
    void testGetNotifications(MockServer mockServer) {
        WebClient webClient = WebClient.builder()
            .baseUrl(mockServer.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        String response = webClient.get()
            .uri("/notifications?page=1&size=10&unreadOnly=true")
            .header("X-User-Id", "user_123")
            .header("X-User-Roles", "USER")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        assertThat(response).contains("\"total\":5");
        assertThat(response).contains("\"notifications\":[");
    }
}
