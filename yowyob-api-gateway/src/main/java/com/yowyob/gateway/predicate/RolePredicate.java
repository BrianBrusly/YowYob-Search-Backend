package com.yowyob.gateway.predicate;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import jakarta.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Prédicat pour vérifier les rôles utilisateur
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Utilisé pour router vers des endpoints qui nécessitent des rôles
 *          spécifiques
 *          Vérifie les rôles dans le header X-User-Roles
 */
@Component
public class RolePredicate extends AbstractRoutePredicateFactory<RolePredicate.Config> {

    public RolePredicate() {
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
                String rolesHeader = exchange.getRequest()
                        .getHeaders()
                        .getFirst("X-User-Roles");

                if (rolesHeader == null || rolesHeader.isEmpty()) {
                    return false;
                }

                // Parser les rôles (format: ROLE1,ROLE2,ROLE3)
                Set<String> userRoles = Arrays.stream(rolesHeader.split(","))
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .collect(Collectors.toSet());

                // Vérifier si l'utilisateur a au moins un des rôles requis
                boolean hasRequiredRole = config.getRoles().stream()
                        .anyMatch(requiredRole -> userRoles.contains(requiredRole.toUpperCase()));

                return hasRequiredRole;
            }

            @Override
            public String toString() {
                return String.format("Roles: %s",
                        String.join(",", config.getRoles()));
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
        return Arrays.asList("roles");
    }

    /**
     * Configuration du prédicat
     */
    @Validated
    public static class Config {
        @NotEmpty
        private List<String> roles;

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public void setRoles(String roles) {
            this.roles = Stream.of(roles.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
    }
}