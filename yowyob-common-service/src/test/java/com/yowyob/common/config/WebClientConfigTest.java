package com.yowyob.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour WebClientConfig
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@SpringBootTest(classes = WebClientConfig.class)
@DisplayName("WebClientConfig Tests")
class WebClientConfigTest {

    @Autowired
    private WebClient webClient;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Test
    @DisplayName("Devrait créer un WebClient configuré")
    void shouldCreateConfiguredWebClient() {
        assertThat(webClient).isNotNull();
    }

    @Test
    @DisplayName("Devrait créer un WebClient.Builder configuré")
    void shouldCreateConfiguredWebClientBuilder() {
        assertThat(webClientBuilder).isNotNull();
    }

    @Test
    @DisplayName("Devrait créer un nouveau WebClient depuis le builder")
    void shouldCreateNewWebClientFromBuilder() {
        WebClient newClient = webClientBuilder.build();

        assertThat(newClient).isNotNull();
        assertThat(newClient).isNotSameAs(webClient);
    }
}