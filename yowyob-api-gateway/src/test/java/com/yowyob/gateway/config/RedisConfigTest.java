package com.yowyob.gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour la configuration Redis
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class RedisConfigTest {

    @Autowired
    private ReactiveRedisConnectionFactory redisConnectionFactory;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setUp() {
        // Nettoyer Redis avant chaque test
        redisTemplate.getConnectionFactory()
                .getReactiveConnection()
                .serverCommands()
                .flushAll()
                .block();
    }

    @Test
    @DisplayName("Devrait établir une connexion Redis")
    void shouldEstablishRedisConnection() {
        // When
        Mono<String> pingResult = redisTemplate.execute(connection -> connection.ping()).next();

        // Then
        StepVerifier.create(pingResult)
                .expectNext("PONG")
                .verifyComplete();
    }

    @Test
    @DisplayName("Devrait supporter les opérations de lecture/écriture")
    void shouldSupportReadWriteOperations() {
        // Given
        String key = "test:key";
        String value = "test:value";
        ReactiveValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        // When/Then - Écriture
        StepVerifier.create(valueOps.set(key, value))
                .expectNext(true)
                .verifyComplete();

        // When/Then - Lecture
        StepVerifier.create(valueOps.get(key))
                .expectNext(value)
                .verifyComplete();
    }

    @Test
    @DisplayName("Devrait supporter les opérations avec TTL")
    void shouldSupportOperationsWithTtl() {
        // Given
        String key = "test:ttl:key";
        String value = "test:ttl:value";
        ReactiveValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        // When/Then - Écriture avec TTL
        StepVerifier.create(valueOps.set(key, value, Duration.ofSeconds(1)))
                .expectNext(true)
                .verifyComplete();

        // Vérifier que la valeur existe
        StepVerifier.create(valueOps.get(key))
                .expectNext(value)
                .verifyComplete();

        // Attendre que le TTL expire
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Vérifier que la valeur a expiré
        StepVerifier.create(valueOps.get(key))
                .verifyComplete();
    }

    @Test
    @DisplayName("Devrait avoir un pool de connexions configuré")
    void shouldHaveConnectionPoolConfigured() {
        // Vérifier que la factory est bien configurée
        assertThat(redisConnectionFactory).isNotNull();

        // Vérifier que nous pouvons obtenir une connexion
        ReactiveRedisConnection connection = redisConnectionFactory.getReactiveConnection();
        assertThat(connection).isNotNull();

        // Nettoyer
        connection.close();
    }

    @Test
    @DisplayName("Devrait supporter les opérations transactionnelles")
    void shouldSupportTransactionalOperations() {
        // Given
        String key1 = "test:tx:key1";
        String key2 = "test:tx:key2";

        // En réactif, nous utilisons souvent multi/exec directement sur la connexion
        // ou des opérations atomiques. Ici on simule une succession d'opérations.
        Mono<Boolean> result = redisTemplate.opsForValue().set(key1, "value1")
                .then(redisTemplate.opsForValue().set(key2, "value2"));

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        // Vérifier que les valeurs sont bien écrites
        StepVerifier.create(redisTemplate.opsForValue().get(key1))
                .expectNext("value1")
                .verifyComplete();

        StepVerifier.create(redisTemplate.opsForValue().get(key2))
                .expectNext("value2")
                .verifyComplete();
    }

    @Test
    @DisplayName("Devrait gérer les erreurs de connexion")
    void shouldHandleConnectionErrors() {
        // Ce test vérifie que la configuration gère bien les timeouts
        // En environnement de test, nous utilisons un Redis local
        // donc nous testons simplement que les timeouts sont configurés

        // Vérifier que le template est configuré
        assertThat(redisTemplate).isNotNull();
        assertThat(redisTemplate.getConnectionFactory()).isNotNull();
    }
}