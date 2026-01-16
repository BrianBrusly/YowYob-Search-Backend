package com.yowyob.gateway.predicate;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import jakarta.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Prédicat pour vérifier la version de l'API
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Utilisé pour router vers différentes versions d'APIs
 *          Supporte les versions dans le chemin (/v1/, /v2/) ou dans les
 *          headers
 */
@Component
public class ApiVersionPredicate extends AbstractRoutePredicateFactory<ApiVersionPredicate.Config> {

    public ApiVersionPredicate() {
        super(Config.class);
    }

    /**
     * Applique le prédicat
     * 
     * @param config Configuration du prédicat
     * @return Predicate<ServerWebExchange> configuré
     */
    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return new GatewayPredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {
                String path = exchange.getRequest().getURI().getPath();
                String acceptVersion = exchange.getRequest()
                        .getHeaders()
                        .getFirst("Accept-Version");

                // Vérifier la version dans le chemin
                boolean pathMatches = path.matches("/" + config.getVersion() + "/.*");

                // Vérifier la version dans le header
                boolean headerMatches = config.getVersion().equals(acceptVersion);

                // Utiliser l'opérateur logique configuré
                if ("OR".equalsIgnoreCase(config.getOperator())) {
                    return pathMatches || headerMatches;
                } else { // AND par défaut
                    return pathMatches && headerMatches;
                }
            }

            @Override
            public String toString() {
                return String.format("ApiVersion: %s (operator: %s)",
                        config.getVersion(), config.getOperator());
            }
        };
    }

    /**
     * Définit l'ordre des champs pour la configuration
     * 
     * @return Liste des noms de champs
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("version", "operator");
    }

    /**
     * Configuration du prédicat
     */
    @Validated
    public static class Config {
        @NotEmpty
        private String version = "v1";

        private String operator = "OR";

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }
    }
}