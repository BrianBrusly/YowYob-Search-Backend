package com.yowyob.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Filtre de transformation des requêtes
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Transforme les requêtes avant qu'elles n'atteignent les services
 *          backend
 *          Peut modifier les headers, les paramètres, le body, etc.
 */
@Component
public class RequestTransformationFilter extends AbstractGatewayFilterFactory<RequestTransformationFilter.Config> {
    private static final Logger log = LoggerFactory.getLogger(RequestTransformationFilter.class);

    public RequestTransformationFilter() {
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
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpRequest.Builder requestBuilder = request.mutate();

            // Ajouter des headers
            if (config.getAddHeaders() != null) {
                config.getAddHeaders().forEach((key, value) -> requestBuilder.header(key, value));
            }

            // Supprimer des headers
            if (config.getRemoveHeaders() != null) {
                requestBuilder.headers(headers -> config.getRemoveHeaders().forEach(headers::remove));
            }

            // Remplacer des headers
            if (config.getReplaceHeaders() != null) {
                requestBuilder.headers(headers -> config.getReplaceHeaders().forEach((key, value) -> {
                    headers.set(key, value);
                }));
            }

            // Transformer le chemin
            if (config.getPathRewrite() != null && !config.getPathRewrite().isEmpty()) {
                String newPath = transformPath(request.getURI().getPath(), config.getPathRewrite());
                requestBuilder.path(newPath);

                // Mettre à jour l'URI
                URI newUri = request.getURI().resolve(newPath);
                exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, newUri);
            }

            // Ajouter des paramètres de requête
            if (config.getAddQueryParams() != null) {
                URI uri = requestBuilder.build().getURI();
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(uri);
                config.getAddQueryParams().forEach(uriBuilder::queryParam);
                requestBuilder.uri(uriBuilder.build().toUri());
            }

            ServerHttpRequest transformedRequest = requestBuilder.build();
            log.debug("Requête transformée: {} -> {}",
                    request.getURI(), transformedRequest.getURI());

            return chain.filter(exchange.mutate().request(transformedRequest).build());
        };
    }

    /**
     * Transforme un chemin selon une règle de réécriture
     * 
     * @param originalPath Chemin original
     * @param rewriteRule  Règle de réécriture
     * @return Chemin transformé
     */
    private String transformPath(String originalPath, String rewriteRule) {
        // Format: /old-path/{segment}/** -> /new-path/{segment}
        String[] parts = rewriteRule.split("->");
        if (parts.length != 2) {
            return originalPath;
        }

        String pattern = parts[0].trim();
        String replacement = parts[1].trim();

        // Remplacer les segments dynamiques
        return originalPath.replaceAll(pattern, replacement);
    }

    /**
     * Configuration du filtre
     */
    public static class Config {
        private Map<String, String> addHeaders;
        private List<String> removeHeaders;
        private Map<String, String> replaceHeaders;
        private String pathRewrite;
        private Map<String, String> addQueryParams;

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

        public Map<String, String> getReplaceHeaders() {
            return replaceHeaders;
        }

        public void setReplaceHeaders(Map<String, String> replaceHeaders) {
            this.replaceHeaders = replaceHeaders;
        }

        public String getPathRewrite() {
            return pathRewrite;
        }

        public void setPathRewrite(String pathRewrite) {
            this.pathRewrite = pathRewrite;
        }

        public Map<String, String> getAddQueryParams() {
            return addQueryParams;
        }

        public void setAddQueryParams(Map<String, String> addQueryParams) {
            this.addQueryParams = addQueryParams;
        }
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(
                "addHeaders",
                "removeHeaders",
                "replaceHeaders",
                "pathRewrite",
                "addQueryParams");
    }
}