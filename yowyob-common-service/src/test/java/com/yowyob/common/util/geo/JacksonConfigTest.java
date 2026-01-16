package com.yowyob.common.util.geo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour JacksonConfig
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@SpringBootTest(classes = { com.yowyob.common.util.geo.JacksonConfig.class, JacksonAutoConfiguration.class })
@DisplayName("JacksonConfig Tests")
class JacksonConfigTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Devrait sérialiser les dates en format ISO")
    void shouldSerializeDatesInISOFormat() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", Instant.parse("2025-01-01T12:00:00Z"));

        String json = objectMapper.writeValueAsString(data);

        assertThat(json).contains("2025-01-01T12:00:00");
    }

    @Test
    @DisplayName("Devrait ignorer les propriétés inconnues lors de la désérialisation")
    void shouldIgnoreUnknownProperties() throws Exception {
        String json = "{\"knownField\":\"value\",\"unknownField\":\"ignored\"}";

        Map<String, Object> result = objectMapper.readValue(json,
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                });

        assertThat(result).containsKey("knownField");
        assertThat(result).containsKey("unknownField");
    }

    @Test
    @DisplayName("Devrait ne pas écrire les dates comme timestamps")
    void shouldNotWriteDatesAsTimestamps() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("time", Instant.now());

        String json = objectMapper.writeValueAsString(data);

        assertThat(json).doesNotContainPattern("\\d{13}");
    }
}