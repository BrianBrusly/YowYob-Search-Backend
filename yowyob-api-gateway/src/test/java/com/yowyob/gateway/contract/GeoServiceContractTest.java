package com.yowyob.gateway.contract;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;

@ExtendWith(PactConsumerTestExt.class)
class GeoServiceContractTest {

    @Pact(provider = "geo-service", consumer = "api-gateway")
    public RequestResponsePact geocodeEndpointPact(PactDslWithProvider builder) {
        return builder
                .given("address exists in database")
                .uponReceiving("a geocode request")
                .path("/geo/geocode")
                .method("GET")
                .query("address=Yaounde")
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .body("""
                        {
                            "latitude": 3.8480,
                            "longitude": 11.5021,
                            "address": "Yaoundé, Cameroon"
                        }
                        """)
                .toPact();
    }
}