package com.yowyob.gateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

/**
 * Configuration OpenAPI/Swagger pour la documentation de l'API
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Configure la documentation OpenAPI pour le Gateway
 * Documente toutes les routes et endpoints disponibles
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "YowYob Search Platform - API Gateway",
                version = "1.0.0",
                description = """
            API Gateway réactif pour la plateforme de recherche YowYob.
            Point d'entrée unique pour tous les microservices.
            
            ## Microservices disponibles:
            - **Search Service** - Recherche avancée
            - **User Service** - Gestion des utilisateurs et authentification
            - **Geo Service** - Géolocalisation et adresses
            - **Crawler Service** - Indexation web (Admin seulement)
            - **Notification Service** - Notifications en temps réel
            - **Shop Service** - Comparaison de prix et boutiques
            - **Stats Service** - Statistiques et analytics
            """,
                contact = @Contact(
                        name = "YowYob Team 4GI-ENSPY",
                        email = "yowyob.4gi.enspy.promo.2027@gmail.com",
                        url = "https://yowyob.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Serveur de développement local"
                ),
                @Server(
                        url = "https://api.staging.yowyob.com",
                        description = "Serveur de staging"
                ),
                @Server(
                        url = "https://api.yowyob.com",
                        description = "Serveur de production"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Authentification JWT. Ajouter 'Bearer {token}' dans le header Authorization"
)
public class OpenApiConfig {

    /**
     * Groupe OpenAPI pour le Search Service
     *
     * @return GroupedOpenApi pour le Search Service
     */
    @Bean
    public GroupedOpenApi searchApi() {
        return GroupedOpenApi.builder()
                .group("search-service")
                .pathsToMatch("/api/search/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new io.swagger.v3.oas.models.info.Info()
                                .title("Search Service API")
                                .description("API pour la recherche avancée de contenu")
                                .version("1.0.0")
                        )
                )
                .build();
    }

    /**
     * Groupe OpenAPI pour le User Service
     *
     * @return GroupedOpenApi pour le User Service
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user-service")
                .pathsToMatch("/api/users/**", "/api/auth/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new io.swagger.v3.oas.models.info.Info()
                                .title("User Service API")
                                .description("API pour la gestion des utilisateurs et l'authentification")
                                .version("1.0.0")
                        )
                )
                .build();
    }

    /**
     * Groupe OpenAPI pour le Geo Service
     *
     * @return GroupedOpenApi pour le Geo Service
     */
    @Bean
    public GroupedOpenApi geoApi() {
        return GroupedOpenApi.builder()
                .group("geo-service")
                .pathsToMatch("/api/geo/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new io.swagger.v3.oas.models.info.Info()
                                .title("Geo Service API")
                                .description("API pour la géolocalisation et la gestion des adresses")
                                .version("1.0.0")
                        )
                )
                .build();
    }

    /**
     * Groupe OpenAPI pour le Gateway lui-même
     *
     * @return GroupedOpenApi pour le Gateway
     */
    @Bean
    public GroupedOpenApi gatewayApi() {
        return GroupedOpenApi.builder()
                .group("gateway")
                .pathsToMatch("/actuator/**", "/fallback/**")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new io.swagger.v3.oas.models.info.Info()
                                .title("Gateway Management API")
                                .description("API pour la gestion et le monitoring du Gateway")
                                .version("1.0.0")
                        )
                )
                .build();
    }
}