package com.yowyob.gateway.filter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Filtre de métriques pour monitorer les performances du Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Collecte des métriques détaillées sur les requêtes et réponses
 *          Intégration avec Micrometer pour export vers Prometheus
 */
@Component
public class MetricsFilter implements GlobalFilter, Ordered {
        private static final Logger log = LoggerFactory.getLogger(MetricsFilter.class);
        private final MeterRegistry meterRegistry;

        public MetricsFilter(MeterRegistry meterRegistry) {
                this.meterRegistry = meterRegistry;
        }

        private static final String METRICS_PREFIX = "gateway.";

        /**
         * Exécute le filtre de métriques
         *
         * @param exchange ServerWebExchange contenant la requête et la réponse
         * @param chain    GatewayFilterChain pour continuer le traitement
         * @return Mono<Void> pour le traitement asynchrone
         */
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                long startTime = System.nanoTime();
                ServerHttpRequest request = exchange.getRequest();

                // Extraire les tags pour les métriques
                Tags tags = extractTags(exchange);

                // Incrémenter le compteur de requêtes
                meterRegistry.counter(METRICS_PREFIX + "requests.total", tags)
                                .increment();

                return chain.filter(exchange)
                                .doOnSuccess(aVoid -> {
                                        long duration = System.nanoTime() - startTime;
                                        recordSuccessfulRequest(exchange, duration, tags);
                                })
                                .doOnError(throwable -> {
                                        long duration = System.nanoTime() - startTime;
                                        recordFailedRequest(exchange, duration, tags, throwable);
                                });
        }

        /**
         * Extrait les tags pour les métriques depuis la requête
         *
         * @param exchange ServerWebExchange
         * @return Tags pour les métriques
         */
        private Tags extractTags(ServerWebExchange exchange) {
                ServerHttpRequest request = exchange.getRequest();
                String routeId = exchange.getAttributeOrDefault(
                                org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR,
                                "unknown").toString();

                String userId = request.getHeaders().getFirst("X-User-Id");
                String userType = userId != null ? "authenticated" : "anonymous";

                return Tags.of(
                                "method", request.getMethod().name(),
                                "path", normalizePath(request.getPath().value()),
                                "route", routeId,
                                "user_type", userType);
        }

        /**
         * Enregistre les métriques pour une requête réussie
         *
         * @param exchange ServerWebExchange
         * @param duration Durée de la requête en nanosecondes
         * @param tags     Tags pour les métriques
         */
        private void recordSuccessfulRequest(ServerWebExchange exchange,
                        long duration,
                        Tags tags) {
                org.springframework.http.HttpStatusCode status = exchange.getResponse().getStatusCode();

                if (status != null) {
                        // Timer pour la durée de la requête
                        meterRegistry.timer(METRICS_PREFIX + "request.duration",
                                        tags.and("status", String.valueOf(status.value())))
                                        .record(duration, TimeUnit.NANOSECONDS);

                        // Histogram pour la taille de la réponse
                        long responseSize = exchange.getResponse().getHeaders()
                                        .getContentLength();
                        if (responseSize > 0) {
                                meterRegistry.summary(METRICS_PREFIX + "response.size.bytes",
                                                tags.and("status", String.valueOf(status.value())))
                                                .record(responseSize);
                        }

                        // Compteur par code de statut
                        meterRegistry.counter(METRICS_PREFIX + "responses.by.status",
                                        tags.and("status", String.valueOf(status.value())))
                                        .increment();
                }
        }

        /**
         * Enregistre les métriques pour une requête échouée
         *
         * @param exchange  ServerWebExchange
         * @param duration  Durée de la requête en nanosecondes
         * @param tags      Tags pour les métriques
         * @param throwable Exception
         */
        private void recordFailedRequest(ServerWebExchange exchange,
                        long duration,
                        Tags tags,
                        Throwable throwable) {
                // Timer pour la durée de la requête échouée
                meterRegistry.timer(METRICS_PREFIX + "request.duration",
                                tags.and("status", "error")
                                                .and("exception", throwable.getClass().getSimpleName()))
                                .record(duration, TimeUnit.NANOSECONDS);

                // Compteur d'erreurs
                meterRegistry.counter(METRICS_PREFIX + "errors.total",
                                tags.and("exception", throwable.getClass().getSimpleName()))
                                .increment();

                // Compteur d'erreurs par type
                meterRegistry.counter(METRICS_PREFIX + "errors.by.type",
                                tags.and("exception_type", throwable.getClass().getSimpleName()))
                                .increment();
        }

        /**
         * Normalise le chemin pour les métriques
         *
         * @param path Chemin original
         * @return Chemin normalisé
         */
        private String normalizePath(String path) {
                // Remplacer les IDs par des placeholders
                return path.replaceAll("/\\d+", "/{id}")
                                .replaceAll("/[a-fA-F0-9-]{36}", "/{uuid}")
                                .replaceAll("/[^/]+@[^/]+", "/{email}");
        }

        /**
         * Définit l'ordre d'exécution du filtre
         *
         * @return Ordre élevé
         */
        @Override
        public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE + 50;
        }
}