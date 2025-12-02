package com.yowyob.gateway.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuration du WebClient pour l'API Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Configure le client HTTP réactif pour communiquer avec les services backend
 */
@Configuration
public class WebClientConfig {

    /**
     * WebClient load balanced pour la découverte de services
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .filter(logRequest())
                .filter(logResponse())
                .filter(errorHandler())
                .defaultHeader("User-Agent", "YowYob-API-Gateway/1.0.0")
                .defaultHeader("X-Forwarded-By", "gateway");
    }

    /**
     * Configuration du HttpClient Reactor Netty
     */
    @Bean
    public HttpClient httpClient() {
        ConnectionProvider provider = ConnectionProvider.builder("yowyob-pool")
                .maxConnections(500)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofMinutes(5))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        return HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(30))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS)))
                .compress(true)
                .keepAlive(true);
    }

    /**
     * Filtre pour logger les requêtes
     */
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.printf("[WebClient] Request: %s %s%n",
                    clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) ->
                    values.forEach(value ->
                            System.out.printf("[WebClient] Header: %s=%s%n", name, value)));
            return Mono.just(clientRequest);
        });
    }

    /**
     * Filtre pour logger les réponses
     */
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            System.out.printf("[WebClient] Response: %s%n",
                    clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

    /**
     * Gestionnaire d'erreurs
     */
    private ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            System.err.printf("[WebClient] Error %s: %s%n",
                                    clientResponse.statusCode(), errorBody);
                            return Mono.error(new RuntimeException(
                                    "HTTP Error " + clientResponse.statusCode() + ": " + errorBody));
                        });
            }
            return Mono.just(clientResponse);
        });
    }

    /**
     * WebClient spécifique pour le Search Service
     */
    @Bean(name = "searchWebClient")
    public WebClient searchWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("lb://SEARCH-SERVICE")
                .defaultHeader("X-Service-Type", "search")
                .build();
    }

    /**
     * WebClient spécifique pour le User Service
     */
    @Bean(name = "userWebClient")
    public WebClient userWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("lb://USER-SERVICE")
                .defaultHeader("X-Service-Type", "user")
                .build();
    }

    /**
     * WebClient spécifique pour le Geo Service
     */
    @Bean(name = "geoWebClient")
    public WebClient geoWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("lb://GEO-SERVICE")
                .defaultHeader("X-Service-Type", "geo")
                .build();
    }

    /**
     * WebClient spécifique pour le Crawler Service
     */
    @Bean(name = "crawlerWebClient")
    public WebClient crawlerWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("lb://CRAWLER-SERVICE")
                .defaultHeader("X-Service-Type", "crawler")
                .build();
    }

    /**
     * WebClient spécifique pour le Notification Service
     */
    @Bean(name = "notificationWebClient")
    public WebClient notificationWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("lb://NOTIFICATION-SERVICE")
                .defaultHeader("X-Service-Type", "notification")
                .build();
    }

    /**
     * WebClient spécifique pour le Shop Service
     */
    @Bean(name = "shopWebClient")
    public WebClient shopWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("lb://SHOP-SERVICE")
                .defaultHeader("X-Service-Type", "shop")
                .build();
    }

    /**
     * WebClient spécifique pour le Stats Service
     */
    @Bean(name = "statsWebClient")
    public WebClient statsWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("lb://STATS-SERVICE")
                .defaultHeader("X-Service-Type", "stats")
                .build();
    }
}