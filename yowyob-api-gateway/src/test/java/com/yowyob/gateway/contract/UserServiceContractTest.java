package com.yowyob.gateway.contract;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.util.Map;

@ExtendWith(PactConsumerTestExt.class)
class UserServiceContractTest {

    @Pact(provider = "user-service", consumer = "api-gateway")
    public RequestResponsePact loginEndpointPact(PactDslWithProvider builder) {
        return builder
                .given("user exists with credentials")
                .uponReceiving("a login request")
                .path("/auth/login")
                .method("POST")
                .body("""
                        {
                            "email": "test@example.com",
                            "password": "password123"
                        }
                        """, MediaType.APPLICATION_JSON_VALUE)
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .body("""
                        {
                            "accessToken": "eyJhbGciOiJSUzI1NiIs...",
                            "refreshToken": "eyJhbGciOiJSUzI1NiIs...",
                            "expiresIn": 900,
                            "user": {
                                "id": "user_123",
                                "email": "test@example.com",
                                "roles": ["USER"]
                            }
                        }
                        """)
                .toPact();
    }

    @Pact(provider = "user-service", consumer = "api-gateway")
    public RequestResponsePact profileEndpointPact(PactDslWithProvider builder) {
        return builder
                .given("user exists with id user_123")
                .uponReceiving("a request for user profile")
                .path("/users/profile")
                .method("GET")
                .headers(Map.of(
                        "X-User-Id", "user_123",
                        "Authorization", "Bearer valid-token"))
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .body("""
                        {
                            "id": "user_123",
                            "email": "test@example.com",
                            "firstName": "John",
                            "lastName": "Doe"
                        }
                        """)
                .toPact();
    }
}