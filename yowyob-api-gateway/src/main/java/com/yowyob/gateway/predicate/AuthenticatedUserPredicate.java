package com.yowyob.gateway.predicate;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Prédicat pour vérifier si l'utilisateur est authentifié
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Utilisé pour router vers des endpoints qui nécessitent une
 *          authentification
 *          Vérifie la présence du header X-User-Id
 */
@Component
public class AuthenticatedUserPredicate extends AbstractRoutePredicateFactory<AuthenticatedUserPredicate.Config> {

    public AuthenticatedUserPredicate() {
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
                String userId = exchange.getRequest()
                        .getHeaders()
                        .getFirst("X-User-Id");

                boolean isAuthenticated = userId != null && !userId.isEmpty();

                // Inverser si configuré (pour les endpoints publics)
                return config.isNegate() ? !isAuthenticated : isAuthenticated;
            }

            @Override
            public String toString() {
                return String.format("Authenticated: %s (negate: %s)",
                        Config.class.getSimpleName(), config.isNegate());
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
        return Arrays.asList("negate");
    }

    /**
     * Configuration du prédicat
     */
    @Validated
    public static class Config {
        private boolean negate = false;

        public boolean isNegate() {
            return negate;
        }

        public void setNegate(boolean negate) {
            this.negate = negate;
        }
    }
}