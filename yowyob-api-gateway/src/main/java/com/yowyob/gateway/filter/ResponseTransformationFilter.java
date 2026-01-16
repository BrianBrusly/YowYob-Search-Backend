package com.yowyob.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Filtre de transformation des réponses
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Transforme les réponses avant qu'elles ne soient retournées aux
 *          clients
 *          Peut modifier les headers, le body, ajouter des métriques, etc.
 */
@Component
public class ResponseTransformationFilter extends AbstractGatewayFilterFactory<ResponseTransformationFilter.Config> {
    private static final Logger log = LoggerFactory.getLogger(ResponseTransformationFilter.class);

    public ResponseTransformationFilter() {
        super(Config.class);
    }

    /**
     * Exécute le filtre de transformation
     * 
     * @param config Configuration du filtre
     * @return GatewayFilter configuré
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            Instant startTime = Instant.now();

            ServerHttpResponse originalResponse = exchange.getResponse();
            ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(originalResponse) {

                @Override
                public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
                    return super.writeWith(Flux.from(body)
                            .map(dataBuffer -> {
                                // Transformer le body si nécessaire
                                if (config.isTransformBody()) {
                                    return transformBody(dataBuffer, exchange);
                                }
                                return dataBuffer;
                            }));
                }

                @Override
                public boolean setStatusCode(org.springframework.http.HttpStatusCode status) {
                    boolean result = super.setStatusCode(status);

                    // Ajouter des headers de réponse
                    addResponseHeaders(exchange, startTime, config);

                    return result;
                }
            };

            return chain.filter(exchange.mutate().response(responseDecorator).build());
        };
    }

    /**
     * Transforme le body de la réponse
     * 
     * @param dataBuffer Buffer de données
     * @param exchange   ServerWebExchange
     * @return DataBuffer transformé
     */
    private DataBuffer transformBody(DataBuffer dataBuffer, ServerWebExchange exchange) {
        // Lire le contenu
        byte[] content = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(content);

        String body = new String(content, StandardCharsets.UTF_8);

        // Transformer selon le Content-Type
        HttpHeaders headers = exchange.getResponse().getHeaders();
        String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);

        if (contentType != null && contentType.contains("application/json")) {
            // Transformer le JSON si nécessaire
            String transformedBody = transformJson(body, exchange);
            byte[] transformedBytes = transformedBody.getBytes(StandardCharsets.UTF_8);
            return exchange.getResponse().bufferFactory().wrap(transformedBytes);
        }

        // Retourner le body original
        return exchange.getResponse().bufferFactory().wrap(content);
    }

    /**
     * Transforme le JSON de la réponse
     * 
     * @param json     JSON original
     * @param exchange ServerWebExchange
     * @return JSON transformé
     */
    private String transformJson(String json, ServerWebExchange exchange) {
        // Ici on pourrait utiliser Jackson pour des transformations complexes
        // Pour l'instant, on retourne le JSON tel quel

        // Exemple: Ajouter un champ metadata
        if (json.startsWith("{") && json.endsWith("}")) {
            String metadata = String.format("\"metadata\":{\"gateway\":\"yowyob-api-gateway\",\"timestamp\":\"%s\"},",
                    Instant.now().toString());
            return json.substring(0, json.length() - 1) + "," + metadata + "}";
        }

        return json;
    }

    /**
     * Ajoute des headers à la réponse
     * 
     * @param exchange  ServerWebExchange
     * @param startTime Temps de début du traitement
     * @param config    Configuration du filtre
     */
    private void addResponseHeaders(ServerWebExchange exchange, Instant startTime, Config config) {
        ServerHttpResponse response = exchange.getResponse();

        // Ajouter des headers selon la configuration
        if (config.getAddHeaders() != null) {
            config.getAddHeaders().forEach((key, value) -> response.getHeaders().add(key, value));
        }

        // Supprimer des headers
        if (config.getRemoveHeaders() != null) {
            config.getRemoveHeaders().forEach(response.getHeaders()::remove);
        }

        // Header de temps de réponse
        Duration processingTime = Duration.between(startTime, Instant.now());
        response.getHeaders().add("X-Response-Time", processingTime.toMillis() + "ms");

        // Header de version
        response.getHeaders().add("X-API-Version", "1.0.0");

        // Header de cache
        if (response.getStatusCode() != null && response.getStatusCode().is2xxSuccessful()) {
            response.getHeaders().add("Cache-Control", "public, max-age=60");
        }

        // Header CORS
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    /**
     * Configuration du filtre
     */
    public static class Config {
        private boolean transformBody = false;
        private Map<String, String> addHeaders;
        private List<String> removeHeaders;
        private boolean addPerformanceHeaders = true;

        public boolean isTransformBody() {
            return transformBody;
        }

        public void setTransformBody(boolean transformBody) {
            this.transformBody = transformBody;
        }

        public Map<String, String> getAddHeaders() {
            return addHeaders;
        }

        public void setAddHeaders(Map<String, String> addHeaders) {
            this.addHeaders = addHeaders;
        }

        public List<String> getRemoveHeaders() {
            return removeHeaders;
        }

        public void setRemoveHeaders(List<String> removeHeaders) {
            this.removeHeaders = removeHeaders;
        }

        public boolean isAddPerformanceHeaders() {
            return addPerformanceHeaders;
        }

        public void setAddPerformanceHeaders(boolean addPerformanceHeaders) {
            this.addPerformanceHeaders = addPerformanceHeaders;
        }
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(
                "transformBody",
                "addHeaders",
                "removeHeaders",
                "addPerformanceHeaders");
    }
}