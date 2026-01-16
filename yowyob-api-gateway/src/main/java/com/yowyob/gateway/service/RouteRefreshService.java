package com.yowyob.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service pour le rafraîchissement dynamique des routes
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@Service
public class RouteRefreshService implements ApplicationEventPublisherAware {

    private static final Logger log = LoggerFactory.getLogger(RouteRefreshService.class);

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final RouteDefinitionLocator routeDefinitionLocator;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public RouteRefreshService(RouteDefinitionWriter routeDefinitionWriter,
            RouteDefinitionLocator routeDefinitionLocator,
            ReactiveRedisTemplate<String, String> redisTemplate) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.routeDefinitionLocator = routeDefinitionLocator;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    private ApplicationEventPublisher eventPublisher;
    private final Map<String, RouteDefinition> dynamicRoutes = new ConcurrentHashMap<>();

    private static final String ROUTES_KEY = "gateway:dynamic:routes";
    private static final String ROUTES_VERSION_KEY = "gateway:routes:version";

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    /**
     * Rafraîchit toutes les routes
     * 
     * @return Mono<Void>
     */
    public Mono<Void> refreshRoutes() {
        log.info("Rafraîchissement des routes...");

        return loadDynamicRoutesFromRedis()
                .collectList()
                .flatMap(routes -> {
                    // Supprimer les anciennes routes dynamiques
                    return deleteExistingDynamicRoutes()
                            .then(saveDynamicRoutes(Flux.fromIterable(routes)))
                            .then(Mono.fromRunnable(() -> eventPublisher.publishEvent(new RefreshRoutesEvent(this))));
                })
                .then()
                .doOnSuccess(v -> log.info("Routes rafraîchies avec succès"))
                .doOnError(error -> log.error("Erreur lors du rafraîchissement des routes", error));
    }

    /**
     * Charge les routes dynamiques depuis Redis
     * 
     * @return Flux<RouteDefinition>
     */
    private Flux<RouteDefinition> loadDynamicRoutesFromRedis() {
        return redisTemplate.opsForHash()
                .values(ROUTES_KEY)
                .cast(String.class)
                .flatMap(this::parseRouteDefinition)
                .doOnSubscribe(s -> log.debug("Chargement des routes depuis Redis..."));
    }

    /**
     * Parse une définition de route depuis JSON
     * 
     * @param json JSON de la route
     * @return Mono<RouteDefinition>
     */
    private Mono<RouteDefinition> parseRouteDefinition(String json) {
        try {
            RouteDefinition route = objectMapper.readValue(json, RouteDefinition.class);
            return Mono.just(route);

        } catch (Exception e) {
            log.error("Erreur lors du parsing de la route", e);
            return Mono.empty();
        }
    }

    /**
     * Supprime les routes dynamiques existantes
     * 
     * @return Mono<Void>
     */
    private Mono<Void> deleteExistingDynamicRoutes() {
        return Flux.fromIterable(dynamicRoutes.keySet())
                .flatMap(routeId -> routeDefinitionWriter.delete(Mono.just(routeId)))
                .then()
                .doOnSubscribe(s -> log.debug("Suppression des routes dynamiques existantes..."));
    }

    /**
     * Sauvegarde les routes dynamiques
     * 
     * @param routes Routes à sauvegarder
     * @return Mono<Void>
     */
    private Mono<Void> saveDynamicRoutes(Flux<RouteDefinition> routes) {
        return routes
                .doOnNext(route -> dynamicRoutes.put(route.getId(), route))
                .flatMap(route -> routeDefinitionWriter.save(Mono.just(route)))
                .then()
                .doOnSubscribe(s -> log.debug("Sauvegarde des nouvelles routes..."));
    }

    /**
     * Ajoute ou met à jour une route dynamique
     * 
     * @param route Définition de la route
     * @return Mono<Void>
     */
    public Mono<Void> addOrUpdateRoute(RouteDefinition route) {
        return serializeRouteDefinition(route)
                .flatMap(json -> redisTemplate.opsForHash()
                        .put(ROUTES_KEY, route.getId(), json)
                        .then(refreshRoutes()))
                .doOnSuccess(v -> log.info("Route {} ajoutée/mise à jour", route.getId()))
                .doOnError(error -> log.error("Erreur lors de l'ajout de la route", error));
    }

    /**
     * Supprime une route dynamique
     * 
     * @param routeId ID de la route
     * @return Mono<Void>
     */
    public Mono<Void> deleteRoute(String routeId) {
        return redisTemplate.opsForHash()
                .remove(ROUTES_KEY, routeId)
                .then(refreshRoutes())
                .doOnSuccess(v -> log.info("Route {} supprimée", routeId))
                .doOnError(error -> log.error("Erreur lors de la suppression de la route", error));
    }

    /**
     * Sérialise une définition de route en JSON
     * 
     * @param route Définition de la route
     * @return Mono<String> JSON
     */
    private Mono<String> serializeRouteDefinition(RouteDefinition route) {
        try {
            String json = objectMapper.writeValueAsString(route);
            return Mono.just(json);

        } catch (Exception e) {
            log.error("Erreur lors de la sérialisation de la route", e);
            return Mono.error(e);
        }
    }

    /**
     * Récupère toutes les routes dynamiques
     * 
     * @return List<RouteDefinition>
     */
    public List<RouteDefinition> getDynamicRoutes() {
        return List.copyOf(dynamicRoutes.values());
    }

    /**
     * Récupère une route dynamique par son ID
     * 
     * @param routeId ID de la route
     * @return RouteDefinition ou null
     */
    public RouteDefinition getDynamicRoute(String routeId) {
        return dynamicRoutes.get(routeId);
    }

    /**
     * Vérifie si une route existe
     * 
     * @param routeId ID de la route
     * @return true si la route existe
     */
    public boolean routeExists(String routeId) {
        return dynamicRoutes.containsKey(routeId);
    }

    /**
     * Tâche planifiée pour vérifier les mises à jour des routes
     * Exécutée toutes les 30 secondes
     */
    @Scheduled(fixedDelay = 30000)
    public void scheduledRouteRefresh() {
        redisTemplate.opsForValue().get(ROUTES_VERSION_KEY)
                .subscribe(version -> {
                    String currentVersion = getCurrentRoutesVersion();
                    if (!version.equals(currentVersion)) {
                        log.info("Nouvelle version des routes détectée: {} -> {}",
                                currentVersion, version);
                        refreshRoutes().subscribe();
                    }
                });
    }

    /**
     * Récupère la version actuelle des routes
     * 
     * @return Version des routes
     */
    private String getCurrentRoutesVersion() {
        return String.valueOf(dynamicRoutes.hashCode());
    }
}