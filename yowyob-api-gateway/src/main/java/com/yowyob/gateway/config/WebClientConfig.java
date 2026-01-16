package com.yowyob.gateway.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuration du WebClient réactif
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Configure WebClient pour les communications HTTP réactives
 * Optimisé pour les performances et la résilience
 */
@Configuration
public class WebClientConfig {

    /**
     * HttpClient Reactor Netty configuré
     *
     * @return HttpClient avec timeouts et connection pooling
     */
    @Bean
    public HttpClient httpClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("gateway")
                .maxConnections(500)                // Nombre max de connexions
                .maxIdleTime(Duration.ofSeconds(20)) // Temps max d'inactivité
                .maxLifeTime(Duration.ofMinutes(5)) // Durée de vie max des connexions
                .pendingAcquireTimeout(Duration.ofSeconds(60)) // Timeout d'acquisition
                .evictInBackground(Duration.ofSeconds(120)) // Éviction en background
                .build();

        return HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // Timeout connexion: 5s
                .responseTimeout(Duration.ofSeconds(10))           // Timeout réponse: 10s
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS))
                )
                .compress(true)                     // Compression
                .followRedirect(true)              // Redirections automatiques
                .keepAlive(true);                  // Keep-alive activé
    }

    /**
     * WebClient pour les communications internes
     *
     * @param httpClient HttpClient configuré
     * @return WebClient configuré
     */
    @Bean
    public WebClient webClient(HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024) // 16MB max en mémoire
                )
                .filter((request, next) -> {
                    // Ajouter des headers de corrélation
                    return next.exchange(request);
                })
                .build();
    }

    /**
     * WebClient pour les communications externes
     *
     * @return WebClient pour les APIs externes
     */
    @Bean
    public WebClient externalWebClient() {
        HttpClient externalHttpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // Timeout plus long
                .responseTimeout(Duration.ofSeconds(30))            // Timeout réponse: 30s
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(externalHttpClient))
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(32 * 1024 * 1024) // 32MB max en mémoire
                )
                .build();
    }
}