# Module Search - YowYob Search Platform

> **Cœur du moteur de recherche intelligent YowYob**  
> Microservice spécialisé dans la recherche full-text, le ranking intelligent et la gestion des résultats avec Elasticsearch, Redis et Kafka

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)
[![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.x-yellow.svg)](https://www.elastic.co/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 📑 Table des matières

- [Vue d'ensemble](#-vue-densemble)
- [Architecture du module](#-architecture-du-module)
- [Structure détaillée](#-structure-détaillée)
- [Composants principaux](#-composants-principaux)
- [Algorithmes de recherche](#-algorithmes-de-recherche)
- [Intégration avec les autres modules](#-intégration-avec-les-autres-modules)
- [Configuration et déploiement](#-configuration-et-déploiement)
- [Tests et qualité](#-tests-et-qualité)
- [Monitoring et observabilité](#-monitoring-et-observabilité)
- [Guide de développement](#-guide-de-développement)

---

## 🎯 Vue d'ensemble

### Qu'est-ce que le Search Service et pourquoi est-il fondamental ?

Dans l'écosystème YowYob Search, le Search Service représente le cerveau de toute la plateforme. C'est le composant qui transforme une simple requête textuelle en une liste pertinente de résultats, intelligemment classés et adaptés au contexte de l'utilisateur. Sans lui, YowYob ne serait qu'un simple proxy vers d'autres moteurs de recherche.

Imaginez que vous cherchez "restaurants chinois à Yaoundé ouvert tard le soir". Le Search Service doit comprendre non seulement les mots clés, mais aussi l'intention derrière cette requête : vous voulez des restaurants, spécifiquement chinois, situés à Yaoundé, et qui ont des horaires d'ouverture tardifs. Il doit ensuite interroger différentes sources de données (pages web indexées, fiches commerce, avis utilisateurs), appliquer un algorithme de ranking sophistiqué pour déterminer quels résultats sont les plus pertinents, et enfin présenter ces résultats de manière organisée avec des facettes pour affiner la recherche.

Ce service est construit avec une attention particulière aux performances. Dans un moteur de recherche, la latence est critique : les études montrent qu'une augmentation de 100ms dans le temps de réponse peut réduire de 1% le taux de conversion. Notre Search Service est donc optimisé pour retourner des résultats pertinents en moins de 200ms, même avec des requêtes complexes sur des millions de documents.

### Positionnement dans l'architecture globale

Le Search Service occupe une position centrale dans l'architecture YowYob. Il reçoit les requêtes de recherche via l'API Gateway, traite ces requêtes en interagissant avec plusieurs composants, et retourne des résultats enrichis.

Voici comment il s'intègre avec les autres services :

- **API Gateway** : Route toutes les requêtes `/api/search/**` vers ce service
- **Elasticsearch** : Stocke et indexe tous les documents web, produits, et contenus
- **Redis** : Cache les résultats populaires pour améliorer les performances
- **Kafka** : Reçoit les événements de document indexés du Crawler Service
- **Geo Service** : Fournit des informations de géolocalisation pour les recherches spatiales
- **Stats Service** : Reçoit les métriques de recherche pour l'analytique
- **Shop Service** : Fournit les données produits pour les recherches e-commerce

### Responsabilités principales

Le Search Service assume plusieurs responsabilités critiques :

**Traitement des requêtes** : Parse et valide les requêtes entrantes, les normalise (correction orthographique, stemming, suppression des stop words), et les transforme en requêtes Elasticsearch optimisées.

**Recherche multi-index** : Interroge simultanément plusieurs index Elasticsearch (documents web, produits, lieux) et agrège les résultats de manière cohérente.

**Ranking intelligent** : Applique un algorithme de scoring hybride qui combine la pertinence textuelle (BM25), la popularité (CTR historique), la fraîcheur (date de publication), et la proximité géographique (pour les recherches localisées).

**Faceting et filtrage** : Génère des facettes (catégories, prix, localisations, dates) qui permettent aux utilisateurs d'affiner leurs recherches.

**Autocomplétion et suggestions** : Fournit des suggestions de recherche en temps réel basées sur l'historique, les tendances, et les requêtes similaires.

**Cache intelligent** : Met en cache les résultats des recherches populaires pour réduire la charge sur Elasticsearch et améliorer les temps de réponse.

**Analytique des recherches** : Track les métriques de performance (temps de réponse, taux de clic, satisfaction utilisateur) pour l'amélioration continue.

---

## 🏗 Architecture du module

### Architecture technique détaillée

Le Search Service est construit selon une architecture en couches clairement définie, chaque couche ayant des responsabilités spécifiques et des dépendances maîtrisées :

```
┌─────────────────────────────────────────────────────────────────────┐
│                         COUCHE API (REST)                           │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    SearchController                          │  │
│  │  - @RestController                                           │  │
│  │  - Gestion des endpoints REST                               │  │
│  │  - Validation des requêtes                                   │  │
│  │  - Transformation des réponses                               │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                 │                                    │
│                                 ▼                                    │
└─────────────────────────────────────────────────────────────────────┘
                                 │
┌─────────────────────────────────────────────────────────────────────┐
│                     COUCHE SERVICE (Métier)                         │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    SearchService                             │  │
│  │  - Orchestration de la recherche                            │  │
│  │  - Logique métier complexe                                  │  │
│  │  - Composition des résultats multi-sources                  │  │
│  │                                                              │  │
│  ├──────────────────────────────────────────────────────────────┤  │
│  │                    RankingService                            │  │
│  │  - Algorithme de scoring                                     │  │
│  │  - Personnalisation des résultats                            │  │
│  │  - Apprentissage automatique (future extension)              │  │
│  │                                                              │  │
│  ├──────────────────────────────────────────────────────────────┤  │
│  │                    SuggestionService                         │  │
│  │  - Génération de suggestions                                 │  │
│  │  - Autocomplétion                                            │  │
│  │  - Correction orthographique                                 │  │
│  │                                                              │  │
│  ├──────────────────────────────────────────────────────────────┤  │
│  │                    CacheService                              │  │
│  │  - Gestion du cache Redis                                    │  │
│  │  - Stratégies d'invalidation                                 │  │
│  │  - Statistiques de cache                                     │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                 │                                    │
│                                 ▼                                    │
└─────────────────────────────────────────────────────────────────────┘
                                 │
┌─────────────────────────────────────────────────────────────────────┐
│                   COUCHE RÉPOSITORY (Accès données)                 │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │            ElasticsearchRepository                           │  │
│  │  - Requêtes Elasticsearch optimisées                         │  │
│  │  - Gestion des connexions                                    │  │
│  │  - Agrégations et facettes                                   │  │
│  │                                                              │  │
│  ├──────────────────────────────────────────────────────────────┤  │
│  │            SearchHistoryRepository                           │  │
│  │  - Historique des recherches                                 │  │
│  │  - Métriques utilisateur                                     │  │
│  │  - Tendances                                                 │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                 │                                    │
│                                 ▼                                    │
└─────────────────────────────────────────────────────────────────────┘
                                 │
┌─────────────────────────────────────────────────────────────────────┐
│                   COUCHE INFRASTRUCTURE (Données)                   │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────────┐  │
│  │ Elasticsearch│  │     Redis     │  │      Kafka         │  │
│  │   Cluster    │  │    Cache      │  │   Event Bus        │  │
│  └──────────────┘  └──────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
```

### Principes de conception appliqués

**Single Responsibility Principle (SRP)** : Chaque classe a une responsabilité unique et clairement définie. Par exemple, `SearchController` ne s'occupe que de la gestion des requêtes HTTP, `SearchService` de l'orchestration de la recherche, et `ElasticsearchRepository` uniquement de la communication avec Elasticsearch.

**Dependency Injection (DI)** : Toutes les dépendances sont injectées via le constructeur, ce qui facilite les tests et rend les dépendances explicites.

**Interface Segregation** : Les interfaces sont petites et ciblées. Par exemple, nous avons des interfaces séparées pour les différents types de recherches (full-text, géospatiale, produit).

**Domain-Driven Design (DDD)** : Le domaine de la recherche est modélisé avec des entités riches (`SearchQuery`, `SearchResult`, `SearchFilter`) qui encapsulent la logique métier.

**Hexagonal Architecture** : L'application est structurée en sorte que la logique métier ne dépende pas des détails techniques (Elasticsearch, Redis). Ces détails sont injectés via des ports et adaptateurs.

### Flux de données typique

```
1. Requête HTTP → API Gateway → SearchController
2. SearchController valide la requête → DTO SearchRequest
3. SearchController appelle SearchService.search(SearchRequest)
4. SearchService :
   a. Vérifie le cache via CacheService (si hit, retourne immédiatement)
   b. Construit une requête Elasticsearch optimisée
   c. Appelle ElasticsearchRepository pour exécuter la requête
   d. Applique l'algorithme de ranking via RankingService
   e. Génère des facettes et suggestions
   f. Met en cache le résultat
   g. Publie un événement Kafka pour l'analytique
5. Retourne SearchResponse au contrôleur
6. Le contrôleur transforme en ApiResponse standardisé
7. Retourne la réponse HTTP
```

---

## 📂 Structure détaillée

### Arborescence complète du module

```
yowyob-search-service/
│
├── 📄 pom.xml                                    # Configuration Maven
│   ├── Parent: yowyob-search-backend
│   ├── Dépendances principales:
│   │   ├── spring-boot-starter-web
│   │   ├── spring-boot-starter-data-elasticsearch
│   │   ├── spring-boot-starter-data-redis
│   │   ├── spring-boot-starter-actuator
│   │   ├── spring-kafka
│   │   └── yowyob-common (module partagé)
│   └── Plugins: maven-compiler-plugin, spring-boot-maven-plugin
│
├── 📁 src/main/java/com/yowyob/search/
│   │
│   ├── 📄 SearchServiceApplication.java          # Point d'entrée Spring Boot
│   │   ├── @SpringBootApplication
│   │   ├── @EnableElasticsearchRepositories
│   │   ├── @EnableCaching
│   │   └── Configuration: démarre l'application
│   │
│   ├── 📁 config/                                # Configurations Spring
│   │   ├── 📄 ElasticsearchConfig.java           # Configuration ES client
│   │   ├── 📄 RedisConfig.java                   # Configuration Redis
│   │   ├── 📄 KafkaConfig.java                   # Configuration Kafka
│   │   ├── 📄 CacheConfig.java                   # Configuration cache
│   │   ├── 📄 RankingConfig.java                 # Configuration ranking
│   │   └── 📄 SearchConfig.java                  # Configuration générale
│   │
│   ├── 📁 controller/                            # Contrôleurs REST
│   │   ├── 📄 SearchController.java              # Recherche principale
│   │   ├── 📄 SuggestionController.java          # Suggestions/autocomplétion
│   │   ├── 📄 SearchHistoryController.java       # Historique utilisateur
│   │   └── 📄 AdminSearchController.java         # Endpoints admin
│   │
│   ├── 📁 service/                               # Services métier
│   │   ├── 📁 core/                              # Services principaux
│   │   │   ├── 📄 SearchService.java             # Service principal
│   │   │   ├── 📄 RankingService.java            # Service de ranking
│   │   │   ├── 📄 SuggestionService.java         # Service suggestions
│   │   │   └── 📄 CacheService.java              # Service cache
│   │   │
│   │   ├── 📁 indexing/                          # Services d'indexation
│   │   │   ├── 📄 DocumentIndexingService.java   # Indexation documents
│   │   │   ├── 📄 ProductIndexingService.java    # Indexation produits
│   │   │   └── 📄 IndexingOrchestrator.java      # Orchestrateur indexation
│   │   │
│   │   ├── 📁 query/                             # Services de requêtes
│   │   │   ├── 📄 QueryParserService.java        # Parseur requêtes
│   │   │   ├── 📄 QueryBuilderService.java       # Builder requêtes ES
│   │   │   └── 📄 QueryOptimizerService.java     # Optimiseur requêtes
│   │   │
│   │   └── 📁 analytics/                         # Services analytiques
│   │       ├── 📄 SearchAnalyticsService.java    # Analytique recherches
│   │       ├── 📄 ClickTrackingService.java      # Tracking clics
│   │       └── 📄 TrendAnalysisService.java      # Analyse tendances
│   │
│   ├── 📁 repository/                            # Répositories données
│   │   ├── 📄 SearchDocumentRepository.java      # Repository ES documents
│   │   ├── 📄 ProductDocumentRepository.java     # Repository ES produits
│   │   ├── 📄 SearchHistoryRepository.java       # Repository historique
│   │   └── 📄 SearchStatsRepository.java         # Repository statistiques
│   │
│   ├── 📁 model/                                 # Modèles de données
│   │   ├── 📁 document/                          # Documents Elasticsearch
│   │   │   ├── 📄 SearchDocument.java            # Document de recherche
│   │   │   ├── 📄 ProductDocument.java           # Document produit
│   │   │   ├── 📄 GeoDocument.java               # Document géospatial
│   │   │   └── 📄 IndexMetadata.java             # Métadonnées index
│   │   │
│   │   ├── 📁 dto/                               # Data Transfer Objects
│   │   │   ├── 📄 SearchRequest.java             # Requête recherche
│   │   │   ├── 📄 SearchResponse.java            # Réponse recherche
│   │   │   ├── 📄 SearchResult.java              # Résultat individuel
│   │   │   ├── 📄 Facet.java                     # Facette de filtrage
│   │   │   ├── 📄 SuggestionRequest.java         # Requête suggestion
│   │   │   ├── 📄 SuggestionResponse.java        # Réponse suggestion
│   │   │   └── 📄 SearchHistoryItem.java         # Élément historique
│   │   │
│   │   ├── 📁 query/                             # Modèles de requêtes
│   │   │   ├── 📄 SearchQuery.java               # Requête de recherche
│   │   │   ├── 📄 FilterQuery.java               # Requête de filtrage
│   │   │   ├── 📄 GeoQuery.java                  # Requête géospatiale
│   │   │   └── 📄 BoostQuery.java                # Requête boostée
│   │   │
│   │   └── 📁 event/                             # Événements Kafka
│   │       ├── 📄 SearchEvent.java               # Événement recherche
│   │       ├── 📄 ClickEvent.java                # Événement clic
│   │       ├── 📄 IndexEvent.java                # Événement indexation
│   │       └── 📄 RankingEvent.java              # Événement ranking
│   │
│   ├── 📁 mapper/                                # Mappers MapStruct
│   │   ├── 📄 SearchMapper.java                  # Mapper principal
│   │   ├── 📄 DocumentMapper.java                # Mapper documents
│   │   ├── 📄 ResponseMapper.java                # Mapper réponses
│   │   └── 📄 QueryMapper.java                   # Mapper requêtes
│   │
│   ├── 📁 exception/                             # Exceptions spécifiques
│   │   ├── 📄 SearchException.java               # Exception base
│   │   ├── 📄 IndexingException.java             # Exception indexation
│   │   ├── 📄 QueryParsingException.java         # Exception parsing
│   │   └── 📄 RankingException.java              # Exception ranking
│   │
│   ├── 📁 util/                                  # Utilitaires
│   │   ├── 📁 query/                             # Utilitaires requêtes
│   │   │   ├── 📄 QueryUtils.java                # Utilitaires généraux
│   │   │   ├── 📄 QueryNormalizer.java           # Normalisateur requêtes
│   │   │   └── 📄 QueryValidator.java            # Validateur requêtes
│   │   │
│   │   ├── 📁 ranking/                           # Utilitaires ranking
│   │   │   ├── 📄 ScoreCalculator.java           # Calculateur scores
│   │   │   ├── 📄 RelevanceUtils.java            # Utilitaires pertinence
│   │   │   └── 📄 FreshnessCalculator.java       # Calculateur fraîcheur
│   │   │
│   │   ├── 📁 text/                              # Utilitaires texte
│   │   │   ├── 📄 TextAnalyzer.java              # Analyseur texte
│   │   │   ├── 📄 Stemmer.java                   # Stemmer français
│   │   │   └── 📄 Tokenizer.java                 # Tokenizer intelligent
│   │   │
│   │   └── 📁 geo/                               # Utilitaires géo
│   │       ├── 📄 GeoUtils.java                  # Utilitaires généraux
│   │       ├── 📄 DistanceCalculator.java        # Calculateur distances
│   │       └── 📄 BoundingBoxUtils.java          # Utilitaires bounding box
│   │
│   ├── 📁 listener/                              # Listeners Kafka
│   │   ├── 📄 SearchEventListener.java          # Listener événements recherche
│   │   ├── 📄 ClickEventListener.java           # Listener événements clic
│   │   ├── 📄 IndexingEventListener.java        # Listener indexation
│   │   └── 📄 RankingEventListener.java         # Listener ranking
│   │
│   ├── 📁 scheduler/                             # Tâches planifiées
│   │   ├── 📄 CacheWarmupScheduler.java         # Pré-chauffage cache
│   │   ├── 📄 IndexOptimizationScheduler.java   # Optimisation index
│   │   ├── 📄 AnalyticsAggregationScheduler.java # Agrégation analytique
│   │   └── 📄 TrendAnalysisScheduler.java       # Analyse tendances
│   │
│   └── 📁 processor/                             # Processeurs de données
│       ├── 📄 DocumentProcessor.java            # Processeur documents
│       ├── 📄 ProductProcessor.java             # Processeur produits
│       ├── 📄 QueryProcessor.java               # Processeur requêtes
│       └── 📄 ResultProcessor.java              # Processeur résultats
│
├── 📁 src/main/resources/
│   ├── 📄 application.yml                       # Configuration principale
│   ├── 📄 application-dev.yml                   # Configuration dev
│   ├── 📄 application-prod.yml                  # Configuration prod
│   ├── 📁 elasticsearch/                        # Configurations ES
│   │   ├── 📄 mappings/                         # Mappings d'index
│   │   │   ├── 📄 search-document-mapping.json  # Mapping documents
│   │   │   ├── 📄 product-document-mapping.json # Mapping produits
│   │   │   └── 📄 geo-document-mapping.json     # Mapping géo
│   │   │
│   │   ├── 📄 settings/                         # Paramètres index
│   │   │   ├── 📄 index-settings.json           # Paramètres généraux
│   │   │   ├── 📄 analysis-settings.json        # Paramètres analyse
│   │   │   └── 📄 similarity-settings.json      # Paramètres similarité
│   │   │
│   │   └── 📄 templates/                        # Templates d'index
│   │       ├── 📄 search-template.json          # Template recherche
│   │       └── 📄 product-template.json         # Template produits
│   │
│   ├── 📁 ranking/                              # Configurations ranking
│   │   ├── 📄 ranking-rules.json                # Règles de ranking
│   │   ├── 📄 boosting-factors.json             # Facteurs de boost
│   │   └── 📄 personalization-weights.json      # Poids personnalisation
│   │
│   └── 📁 stopwords/                            # Listes stop words
│       ├── 📄 french-stopwords.txt              # Stop words français
│       ├── 📄 english-stopwords.txt             # Stop words anglais
│       └── 📄 custom-stopwords.txt              # Stop words personnalisés
│
└── 📁 src/test/java/com/yowyob/search/
    ├── 📄 SearchServiceApplicationTests.java    # Tests application
    ├── 📁 controller/                           # Tests contrôleurs
    ├── 📁 service/                              # Tests services
    ├── 📁 repository/                           # Tests repositories
    ├── 📁 integration/                          # Tests d'intégration
    ├── 📁 performance/                          # Tests performance
    └── 📁 contract/                             # Tests contrat API
```

### Description des responsabilités par package

**Package `controller`** : Contient les contrôleurs REST qui exposent les endpoints API. Chaque contrôleur gère un aspect spécifique de la recherche et délègue la logique métier aux services appropriés.

**Package `service`** : Implémente la logique métier complexe de la recherche. Organisé en sous-packages par domaine :
- `core` : Services fondamentaux de recherche, ranking, suggestions et cache
- `indexing` : Services dédiés à l'indexation et la mise à jour des données
- `query` : Services spécialisés dans la construction et l'optimisation des requêtes
- `analytics` : Services d'analytique et de suivi des comportements de recherche

**Package `repository`** : Gère l'accès aux données via Elasticsearch et d'autres sources. Abstraction Spring Data sur Elasticsearch avec des méthodes de requête personnalisées.

**Package `model`** : Contient toutes les classes de modèle organisées par type :
- `document` : Classes annotées pour l'indexation Elasticsearch
- `dto` : Data Transfer Objects pour la communication API
- `query` : Modèles de requêtes internes
- `event` : Événements Kafka pour la communication asynchrone

**Package `mapper`** : Utilise MapStruct pour mapper automatiquement entre différents types de modèles (DTO ↔ Document ↔ Entity).

**Package `util`** : Classes utilitaires organisées par domaine fonctionnel :
- `query` : Normalisation, validation et transformation des requêtes
- `ranking` : Calcul de scores et évaluation de pertinence
- `text` : Analyse et traitement du texte
- `geo` : Calculs géospatiales et distances

**Package `listener`** : Consommateurs Kafka qui écoutent les événements des autres services et réagissent en conséquence.

**Package `scheduler`** : Tâches planifiées pour la maintenance, l'optimisation et l'analytique.

**Package `processor`** : Processeurs de données pour transformer et enrichir les données avant indexation ou après récupération.

---

## 🔧 Composants principaux

### SearchController (Couche API)

Le contrôleur principal qui expose les endpoints REST pour la recherche :

```java
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {
    
    private final SearchService searchService;
    private final SuggestionService suggestionService;
    private final SearchHistoryService searchHistoryService;
    private final SearchMapper searchMapper;
    
    /**
     * Endpoint principal de recherche
     * GET /api/search?q=restaurants+yaounde&page=0&size=10
     */
    @GetMapping
    @LogExecutionTime
    public ResponseEntity<ApiResponse<SearchResponse>> search(
            @RequestParam @NotBlank String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Integer radius,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @CurrentUser User user) {
        
        // Construction de la requête de recherche
        SearchRequest searchRequest = SearchRequest.builder()
            .query(q)
            .page(page)
            .size(size)
            .location(location)
            .coordinates(lat != null && lng != null ? 
                new Coordinates(lat, lng) : null)
            .radius(radius)
            .language(language != null ? language : "fr")
            .dateRange(dateFrom != null && dateTo != null ? 
                new DateRange(
                    LocalDate.parse(dateFrom),
                    LocalDate.parse(dateTo)
                ) : null)
            .userId(user != null ? user.getId() : null)
            .build();
        
        // Exécution de la recherche
        SearchResponse response = searchService.search(searchRequest);
        
        // Enregistrement dans l'historique si utilisateur authentifié
        if (user != null) {
            searchHistoryService.recordSearch(user.getId(), q, response);
        }
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Recherche avancée avec filtres complexes
     * POST /api/search/advanced
     */
    @PostMapping("/advanced")
    public ResponseEntity<ApiResponse<SearchResponse>> advancedSearch(
            @Valid @RequestBody AdvancedSearchRequest request,
            @CurrentUser User user) {
        
        // Conversion de la requête avancée
        SearchRequest searchRequest = searchMapper.toSearchRequest(request);
        searchRequest.setUserId(user != null ? user.getId() : null);
        
        SearchResponse response = searchService.search(searchRequest);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Endpoint d'autocomplétion
     * GET /api/search/suggestions?q=restau&limit=5
     */
    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<SuggestionResponse>> getSuggestions(
            @RequestParam @NotBlank String q,
            @RequestParam(defaultValue = "5") @Min(1) @Max(20) int limit,
            @RequestParam(required = false) String language,
            @CurrentUser User user) {
        
        SuggestionRequest request = SuggestionRequest.builder()
            .query(q)
            .limit(limit)
            .language(language != null ? language : "fr")
            .userId(user != null ? user.getId() : null)
            .build();
        
        SuggestionResponse response = suggestionService.getSuggestions(request);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Endpoint pour les tendances de recherche
     * GET /api/search/trending
     */
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<TrendingSearch>>> getTrendingSearches(
            @RequestParam(defaultValue = "today") String period,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String location) {
        
        List<TrendingSearch> trending = searchService.getTrendingSearches(
            period, limit, location);
        
        return ResponseEntity.ok(ApiResponse.success(trending));
    }
    
    /**
     * Endpoint pour l'historique de recherche de l'utilisateur
     * GET /api/search/history
     */
    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SearchHistoryItem>>> getSearchHistory(
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<SearchHistoryItem> history = searchHistoryService
            .getUserSearchHistory(user.getId(), page, size);
        
        return ResponseEntity.ok(ApiResponse.success(history));
    }
    
    /**
     * Endpoint pour vider l'historique de recherche
     * DELETE /api/search/history
     */
    @DeleteMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> clearSearchHistory(
            @CurrentUser User user) {
        
        searchHistoryService.clearUserSearchHistory(user.getId());
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * Endpoint admin pour les statistiques de recherche
     * GET /api/search/admin/stats
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SearchStats>> getSearchStats(
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate toDate) {
        
        SearchStats stats = searchService.getSearchStats(fromDate, toDate);
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
```

### SearchService (Couche Service)

Le service principal qui orchestre toute la logique de recherche :

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    
    private final ElasticsearchRepository elasticsearchRepository;
    private final RankingService rankingService;
    private final CacheService cacheService;
    private final QueryParserService queryParserService;
    private final QueryBuilderService queryBuilderService;
    private final SearchAnalyticsService analyticsService;
    private final SearchMapper searchMapper;
    private final SearchConfig searchConfig;
    
    /**
     * Exécute une recherche complète avec tous les traitements
     */
    public SearchResponse search(SearchRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("Processing search request: {}", request);
            
            // 1. Vérifier le cache
            String cacheKey = generateCacheKey(request);
            SearchResponse cachedResponse = cacheService.getSearchResult(cacheKey);
            
            if (cachedResponse != null) {
                log.debug("Cache hit for key: {}", cacheKey);
                cachedResponse.setCacheHit(true);
                cachedResponse.setProcessingTime(
                    System.currentTimeMillis() - startTime);
                return cachedResponse;
            }
            
            // 2. Normaliser et parser la requête
            SearchQuery searchQuery = queryParserService.parse(request);
            
            // 3. Construire la requête Elasticsearch
            org.elasticsearch.index.query.QueryBuilder esQuery = 
                queryBuilderService.buildSearchQuery(searchQuery);
            
            // 4. Construire l'agrégation pour les facettes
            List<AggregationBuilder> aggregations = 
                queryBuilderService.buildAggregations(searchQuery);
            
            // 5. Exécuter la recherche Elasticsearch
            SearchHits<SearchDocument> searchHits = 
                elasticsearchRepository.search(esQuery, aggregations, 
                    searchQuery.getPage(), searchQuery.getSize());
            
            // 6. Appliquer l'algorithme de ranking
            List<SearchResult> rankedResults = rankingService.rankResults(
                searchHits.getSearchHits(), searchQuery);
            
            // 7. Extraire les facettes
            List<Facet> facets = extractFacets(searchHits.getAggregations());
            
            // 8. Générer les suggestions
            List<String> suggestions = generateSuggestions(searchQuery);
            
            // 9. Construire la réponse
            SearchResponse response = SearchResponse.builder()
                .query(request.getQuery())
                .totalResults(searchHits.getTotalHits())
                .page(request.getPage())
                .size(request.getSize())
                .totalPages((int) Math.ceil(
                    (double) searchHits.getTotalHits() / request.getSize()))
                .results(rankedResults)
                .facets(facets)
                .suggestions(suggestions)
                .processingTime(System.currentTimeMillis() - startTime)
                .cacheHit(false)
                .build();
            
            // 10. Mettre en cache (si approprié)
            if (shouldCacheResponse(request, response)) {
                cacheService.cacheSearchResult(cacheKey, response, 
                    searchConfig.getCacheTtl());
            }
            
            // 11. Publier l'événement d'analytique
            publishSearchEvent(request, response, startTime);
            
            log.info("Search completed in {}ms with {} results", 
                response.getProcessingTime(), response.getTotalResults());
            
            return response;
            
        } catch (Exception e) {
            log.error("Error processing search request: {}", request, e);
            throw new SearchException("Failed to process search request", e);
        }
    }
    
    /**
     * Recherche multi-index (documents + produits + lieux)
     */
    public MultiSearchResponse multiSearch(MultiSearchRequest request) {
        log.info("Processing multi-search request");
        
        // Exécuter les recherches en parallèle
        CompletableFuture<SearchResponse> webSearchFuture = 
            CompletableFuture.supplyAsync(() -> 
                searchWebDocuments(request.getWebSearchRequest()));
        
        CompletableFuture<SearchResponse> productSearchFuture = 
            CompletableFuture.supplyAsync(() -> 
                searchProducts(request.getProductSearchRequest()));
        
        CompletableFuture<SearchResponse> geoSearchFuture = 
            CompletableFuture.supplyAsync(() -> 
                searchGeoLocations(request.getGeoSearchRequest()));
        
        // Attendre tous les résultats
        CompletableFuture.allOf(
            webSearchFuture, productSearchFuture, geoSearchFuture).join();
        
        try {
            // Combiner les résultats
            return MultiSearchResponse.builder()
                .webResults(webSearchFuture.get())
                .productResults(productSearchFuture.get())
                .geoResults(geoSearchFuture.get())
                .totalResults(
                    webSearchFuture.get().getTotalResults() +
                    productSearchFuture.get().getTotalResults() +
                    geoSearchFuture.get().getTotalResults()
                )
                .build();
        } catch (Exception e) {
            log.error("Error in multi-search", e);
            throw new SearchException("Failed to execute multi-search", e);
        }
    }
    
    /**
     * Génère une clé de cache unique basée sur les paramètres de recherche
     */
    private String generateCacheKey(SearchRequest request) {
        return String.format("search:%s:%d:%d:%s:%s",
            request.getQuery().toLowerCase(),
            request.getPage(),
            request.getSize(),
            request.getLocation() != null ? request.getLocation() : "any",
            request.getLanguage()
        );
    }
    
    /**
     * Détermine si une réponse doit être mise en cache
     */
    private boolean shouldCacheResponse(SearchRequest request, 
                                       SearchResponse response) {
        // Ne pas cacher si peu de résultats
        if (response.getTotalResults() < 3) {
            return false;
        }
        
        // Ne pas cacher les recherches personnalisées pour utilisateur connecté
        if (request.getUserId() != null && 
            searchConfig.isPersonalizationEnabled()) {
            return false;
        }
        
        // Ne pas cacher les recherches avec filtres temporels récents
        if (request.getDateRange() != null && 
            request.getDateRange().getFrom()
                .isAfter(LocalDate.now().minusDays(1))) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Publie un événement de recherche pour l'analytique
     */
    private void publishSearchEvent(SearchRequest request, 
                                    SearchResponse response, 
                                    long startTime) {
        SearchEvent event = SearchEvent.builder()
            .query(request.getQuery())
            .userId(request.getUserId())
            .totalResults(response.getTotalResults())
            .page(request.getPage())
            .size(request.getSize())
            .processingTime(response.getProcessingTime())
            .timestamp(Instant.now())
            .location(request.getLocation())
            .language(request.getLanguage())
            .cacheHit(response.isCacheHit())
            .build();
        
        analyticsService.publishSearchEvent(event);
    }
    
    /**
     * Extrait les facettes des agrégations Elasticsearch
     */
    private List<Facet> extractFacets(Aggregations aggregations) {
        List<Facet> facets = new ArrayList<>();
        
        if (aggregations == null) {
            return facets;
        }
        
        // Facette des catégories
        Terms categoryAgg = aggregations.get("categories");
        if (categoryAgg != null) {
            Facet categoryFacet = Facet.builder()
                .name("category")
                .displayName("Catégorie")
                .values(categoryAgg.getBuckets().stream()
                    .map(bucket -> FacetValue.builder()
                        .value(bucket.getKeyAsString())
                        .count(bucket.getDocCount())
                        .build())
                    .collect(Collectors.toList()))
                .build();
            facets.add(categoryFacet);
        }
        
        // Facette des dates
        DateHistogram dateAgg = aggregations.get("dates");
        if (dateAgg != null) {
            Facet dateFacet = Facet.builder()
                .name("date")
                .displayName("Date de publication")
                .type(FacetType.DATE_RANGE)
                .values(dateAgg.getBuckets().stream()
                    .map(bucket -> FacetValue.builder()
                        .value(bucket.getKeyAsString())
                        .count(bucket.getDocCount())
                        .build())
                    .collect(Collectors.toList()))
                .build();
            facets.add(dateFacet);
        }
        
        // Facette des localisations
        Terms locationAgg = aggregations.get("locations");
        if (locationAgg != null) {
            Facet locationFacet = Facet.builder()
                .name("location")
                .displayName("Localisation")
                .values(locationAgg.getBuckets().stream()
                    .map(bucket -> FacetValue.builder()
                        .value(bucket.getKeyAsString())
                        .count(bucket.getDocCount())
                        .build())
                    .collect(Collectors.toList()))
                .build();
            facets.add(locationFacet);
        }
        
        return facets;
    }
    
    /**
     * Génère des suggestions de recherche basées sur la requête
     */
    private List<String> generateSuggestions(SearchQuery query) {
        // Cette méthode est un wrapper qui délègue au SuggestionService
        // mais peut aussi inclure une logique simple de suggestions
        return List.of(); // Implémentation déléguée au SuggestionService
    }
}
```

### RankingService (Service de Scoring)

Le service responsable de l'algorithme de ranking intelligent qui détermine l'ordre des résultats de recherche. C'est l'un des composants les plus critiques car il impacte directement la pertinence perçue du moteur de recherche.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class RankingService {
    
    private final RankingConfig rankingConfig;
    private final SearchAnalyticsService analyticsService;
    private final GeoService geoService;
    
    /**
     * Algorithme principal de ranking hybride
     * 
     * Combine plusieurs facteurs de scoring:
     * - Pertinence textuelle BM25 (40%)
     * - Score géospatial (30%)
     * - Score de fraîcheur (20%)
     * - Score de popularité CTR (10%)
     * 
     * Ces pondérations sont configurables via rankingConfig
     */
    public List<SearchResult> rankResults(
            List<SearchHit<SearchDocument>> hits,
            SearchQuery query) {
        
        log.debug("Ranking {} results for query: {}", 
            hits.size(), query.getQuery());
        
        // Convertir les hits en résultats avec scoring
        List<ScoredResult> scoredResults = hits.stream()
            .map(hit -> calculateScore(hit, query))
            .collect(Collectors.toList());
        
        // Trier par score décroissant
        scoredResults.sort(Comparator
            .comparing(ScoredResult::getFinalScore)
            .reversed());
        
        // Appliquer les boosts configurés
        applyConfiguredBoosts(scoredResults, query);
        
        // Appliquer la personnalisation si disponible
        if (query.getUserId() != null && 
            rankingConfig.isPersonalizationEnabled()) {
            applyPersonalization(scoredResults, query.getUserId());
        }
        
        // Convertir en SearchResult final
        return scoredResults.stream()
            .map(this::toSearchResult)
            .collect(Collectors.toList());
    }
    
    /**
     * Calcule tous les scores pour un document
     */
    private ScoredResult calculateScore(
            SearchHit<SearchDocument> hit,
            SearchQuery query) {
        
        SearchDocument doc = hit.getContent();
        
        // Score de pertinence textuelle (BM25 d'Elasticsearch)
        double relevanceScore = hit.getScore();
        double normalizedRelevance = normalizeScore(
            relevanceScore, 0, 100);
        
        // Score géospatial
        double geoScore = calculateGeoScore(doc, query);
        
        // Score de fraîcheur
        double freshnessScore = calculateFreshnessScore(doc);
        
        // Score de popularité
        double popularityScore = calculatePopularityScore(doc);
        
        // Score de qualité du contenu
        double qualityScore = calculateQualityScore(doc);
        
        // Calcul du score final pondéré
        double finalScore = 
            (normalizedRelevance * rankingConfig.getRelevanceWeight()) +
            (geoScore * rankingConfig.getGeoWeight()) +
            (freshnessScore * rankingConfig.getFreshnessWeight()) +
            (popularityScore * rankingConfig.getPopularityWeight()) +
            (qualityScore * rankingConfig.getQualityWeight());
        
        return ScoredResult.builder()
            .document(doc)
            .relevanceScore(normalizedRelevance)
            .geoScore(geoScore)
            .freshnessScore(freshnessScore)
            .popularityScore(popularityScore)
            .qualityScore(qualityScore)
            .finalScore(finalScore)
            .build();
    }
    
    /**
     * Calcule le score géospatial basé sur la distance
     */
    private double calculateGeoScore(
            SearchDocument doc,
            SearchQuery query) {
        
        if (query.getCoordinates() == null || 
            doc.getLocation() == null) {
            return 0.5; // Score neutre si pas de géolocalisation
        }
        
        // Calculer la distance en kilomètres
        double distance = geoService.calculateDistance(
            query.getCoordinates(),
            doc.getLocation().getCoordinates()
        );
        
        // Score décroissant avec la distance
        // 0km = score 1.0
        // 10km = score 0.5
        // 50km+ = score 0.0
        double maxDistance = rankingConfig.getMaxRelevantDistance();
        double score = Math.max(0, 1.0 - (distance / maxDistance));
        
        return score;
    }
    
    /**
     * Calcule le score de fraîcheur basé sur la date de publication
     */
    private double calculateFreshnessScore(SearchDocument doc) {
        if (doc.getPublishedDate() == null) {
            return 0.5; // Score neutre si pas de date
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime published = doc.getPublishedDate();
        
        long hoursSincePublication = 
            ChronoUnit.HOURS.between(published, now);
        
        // Score décroissant avec le temps
        // 0-24h = score 1.0
        // 1 semaine = score 0.7
        // 1 mois = score 0.4
        // 6 mois+ = score 0.0
        if (hoursSincePublication < 24) {
            return 1.0;
        } else if (hoursSincePublication < 24 * 7) {
            return 0.9 - (hoursSincePublication / (24.0 * 7.0)) * 0.2;
        } else if (hoursSincePublication < 24 * 30) {
            return 0.7 - (hoursSincePublication / (24.0 * 30.0)) * 0.3;
        } else if (hoursSincePublication < 24 * 30 * 6) {
            return 0.4 - (hoursSincePublication / (24.0 * 30.0 * 6.0)) * 0.4;
        } else {
            return 0.0;
        }
    }
    
    /**
     * Calcule le score de popularité basé sur les métriques d'engagement
     */
    private double calculatePopularityScore(SearchDocument doc) {
        // Récupérer les métriques d'engagement depuis le service analytics
        EngagementMetrics metrics = 
            analyticsService.getDocumentMetrics(doc.getId());
        
        if (metrics == null) {
            return 0.5; // Score neutre si pas de données
        }
        
        // Calculer le CTR (Click-Through Rate)
        double ctr = metrics.getClicks() > 0 ? 
            (double) metrics.getClicks() / metrics.getImpressions() : 0;
        
        // Normaliser le CTR (CTR de 10% = score 1.0)
        double ctrScore = Math.min(1.0, ctr * 10);
        
        // Calculer le temps de visite moyen
        double avgTimeScore = normalizeScore(
            metrics.getAverageTimeOnPage(),
            0, 300 // 5 minutes max
        );
        
        // Calculer le taux de rebond (inversé)
        double bounceScore = 1.0 - metrics.getBounceRate();
        
        // Combiner les métriques
        return (ctrScore * 0.5) + 
               (avgTimeScore * 0.3) + 
               (bounceScore * 0.2);
    }
    
    /**
     * Calcule le score de qualité basé sur les attributs du document
     */
    private double calculateQualityScore(SearchDocument doc) {
        double score = 0.5; // Score de base
        
        // Bonus pour titre descriptif
        if (doc.getTitle() != null && 
            doc.getTitle().length() > 20) {
            score += 0.1;
        }
        
        // Bonus pour description complète
        if (doc.getDescription() != null && 
            doc.getDescription().length() > 100) {
            score += 0.1;
        }
        
        // Bonus pour contenu substantiel
        if (doc.getContent() != null && 
            doc.getContent().length() > 500) {
            score += 0.1;
        }
        
        // Bonus pour images
        if (doc.getImages() != null && 
            !doc.getImages().isEmpty()) {
            score += 0.1;
        }
        
        // Bonus pour metadata complète
        if (doc.getMetadata() != null && 
            doc.getMetadata().size() > 5) {
            score += 0.1;
        }
        
        return Math.min(1.0, score);
    }
    
    /**
     * Applique les boosts configurés pour certains types de documents
     */
    private void applyConfiguredBoosts(
            List<ScoredResult> results,
            SearchQuery query) {
        
        Map<String, Double> boosts = rankingConfig.getCategoryBoosts();
        
        results.forEach(result -> {
            String category = result.getDocument().getCategory();
            if (category != null && boosts.containsKey(category)) {
                double boost = boosts.get(category);
                result.setFinalScore(result.getFinalScore() * boost);
            }
        });
    }
    
    /**
     * Applique la personnalisation basée sur l'historique utilisateur
     */
    private void applyPersonalization(
            List<ScoredResult> results,
            String userId) {
        
        // Récupérer les préférences utilisateur
        UserPreferences prefs = 
            analyticsService.getUserPreferences(userId);
        
        if (prefs == null) {
            return;
        }
        
        results.forEach(result -> {
            SearchDocument doc = result.getDocument();
            
            // Boost basé sur les catégories préférées
            if (prefs.getFavoriteCategories()
                    .contains(doc.getCategory())) {
                result.setFinalScore(
                    result.getFinalScore() * 
                    rankingConfig.getPersonalizationBoost());
            }
            
            // Boost basé sur les sources préférées
            if (prefs.getFavoriteSources()
                    .contains(doc.getSource())) {
                result.setFinalScore(
                    result.getFinalScore() * 
                    rankingConfig.getSourceBoost());
            }
        });
    }
    
    /**
     * Normalise un score dans la plage [0, 1]
     */
    private double normalizeScore(double value, double min, double max) {
        if (value <= min) return 0.0;
        if (value >= max) return 1.0;
        return (value - min) / (max - min);
    }
    
    /**
     * Convertit un ScoredResult en SearchResult pour la réponse API
     */
    private SearchResult toSearchResult(ScoredResult scored) {
        SearchDocument doc = scored.getDocument();
        
        return SearchResult.builder()
            .id(doc.getId())
            .title(doc.getTitle())
            .url(doc.getUrl())
            .snippet(generateSnippet(doc))
            .imageUrl(doc.getImages() != null && 
                     !doc.getImages().isEmpty() ? 
                     doc.getImages().get(0) : null)
            .source(doc.getSource())
            .publishedDate(doc.getPublishedDate())
            .score(scored.getFinalScore())
            .location(doc.getLocation())
            .category(doc.getCategory())
            .metadata(doc.getMetadata())
            .build();
    }
    
    /**
     * Génère un snippet pertinent du contenu
     */
    private String generateSnippet(SearchDocument doc) {
        if (doc.getDescription() != null && 
            !doc.getDescription().isEmpty()) {
            return truncateText(doc.getDescription(), 200);
        }
        
        if (doc.getContent() != null && 
            !doc.getContent().isEmpty()) {
            return truncateText(doc.getContent(), 200);
        }
        
        return "";
    }
    
    /**
     * Tronque un texte à une longueur maximale
     */
    private String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        
        // Tronquer au dernier espace avant maxLength
        int lastSpace = text.lastIndexOf(' ', maxLength);
        if (lastSpace > 0) {
            return text.substring(0, lastSpace) + "...";
        }
        
        return text.substring(0, maxLength) + "...";
    }
}
```

### SuggestionService (Service d'Autocomplétion)

Le service qui fournit des suggestions de recherche intelligentes en temps réel. Il combine plusieurs sources de données pour offrir des suggestions pertinentes et personnalisées.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class SuggestionService {
    
    private final ElasticsearchRepository elasticsearchRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final CacheService cacheService;
    private final TrendAnalysisService trendAnalysisService;
    private final SuggestionConfig suggestionConfig;
    
    /**
     * Génère des suggestions d'autocomplétion
     */
    public SuggestionResponse getSuggestions(SuggestionRequest request) {
        log.debug("Generating suggestions for query: {}", 
            request.getQuery());
        
        // Vérifier le cache
        String cacheKey = generateSuggestionCacheKey(request);
        SuggestionResponse cachedResponse = 
            cacheService.getSuggestions(cacheKey);
        
        if (cachedResponse != null) {
            return cachedResponse;
        }
        
        // Générer les suggestions depuis différentes sources
        List<Suggestion> suggestions = new ArrayList<>();
        
        // 1. Suggestions depuis l'historique de l'utilisateur
        if (request.getUserId() != null) {
            suggestions.addAll(getUserHistorySuggestions(
                request.getQuery(), request.getUserId(), 3));
        }
        
        // 2. Suggestions depuis les recherches populaires
        suggestions.addAll(getPopularSuggestions(
            request.getQuery(), 5));
        
        // 3. Suggestions depuis l'index Elasticsearch
        suggestions.addAll(getIndexSuggestions(
            request.getQuery(), request.getLanguage(), 5));
        
        // 4. Suggestions depuis les tendances actuelles
        suggestions.addAll(getTrendingSuggestions(
            request.getQuery(), 3));
        
        // Dédupliquer et limiter le nombre de suggestions
        List<Suggestion> uniqueSuggestions = deduplicateSuggestions(
            suggestions, request.getLimit());
        
        // Construire la réponse
        SuggestionResponse response = SuggestionResponse.builder()
            .query(request.getQuery())
            .suggestions(uniqueSuggestions)
            .build();
        
        // Mettre en cache
        cacheService.cacheSuggestions(cacheKey, response,
            suggestionConfig.getCacheTtl());
        
        return response;
    }
    
    /**
     * Récupère des suggestions depuis l'historique de l'utilisateur
     */
    private List<Suggestion> getUserHistorySuggestions(
            String query,
            String userId,
            int limit) {
        
        List<SearchHistoryItem> history = 
            searchHistoryRepository.findRecentSearches(
                userId, query, limit);
        
        return history.stream()
            .map(item -> Suggestion.builder()
                .text(item.getQuery())
                .type(SuggestionType.PERSONAL_HISTORY)
                .score(calculateHistoryScore(item))
                .build())
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère des suggestions depuis les recherches populaires
     */
    private List<Suggestion> getPopularSuggestions(
            String query,
            int limit) {
        
        // Requête Elasticsearch pour trouver des termes similaires populaires
        SearchRequest searchRequest = new SearchRequest("search_queries");
        
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery(
            "query", query));
        sourceBuilder.aggregation(AggregationBuilders
            .terms("popular_queries")
            .field("query.keyword")
            .size(limit)
            .order(BucketOrder.count(false)));
        
        searchRequest.source(sourceBuilder);
        
        try {
            SearchResponse response = 
                elasticsearchRepository.search(searchRequest);
            
            Terms agg = response.getAggregations().get("popular_queries");
            
            return agg.getBuckets().stream()
                .map(bucket -> Suggestion.builder()
                    .text(bucket.getKeyAsString())
                    .type(SuggestionType.POPULAR)
                    .score(normalizeCount(bucket.getDocCount()))
                    .metadata(Map.of("count", bucket.getDocCount()))
                    .build())
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting popular suggestions", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Récupère des suggestions depuis l'index principal
     */
    private List<Suggestion> getIndexSuggestions(
            String query,
            String language,
            int limit) {
        
        // Utiliser la fonctionnalité de completion suggester d'Elasticsearch
        CompletionSuggestionBuilder suggestionBuilder = 
            SuggestBuilders.completionSuggestion("title.suggest")
                .prefix(query)
                .size(limit);
        
        SuggestBuilder suggestBuilder = new SuggestBuilder()
            .addSuggestion("title_suggest", suggestionBuilder);
        
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
            .suggest(suggestBuilder);
        
        SearchRequest searchRequest = new SearchRequest("search_documents")
            .source(sourceBuilder);
        
        try {
            SearchResponse response = 
                elasticsearchRepository.search(searchRequest);
            
            CompletionSuggestion suggestion = 
                response.getSuggest().getSuggestion("title_suggest");
            
            return suggestion.getOptions().stream()
                .map(option -> Suggestion.builder()
                    .text(option.getText().string())
                    .type(SuggestionType.COMPLETION)
                    .score(option.getScore())
                    .build())
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting index suggestions", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Récupère des suggestions depuis les tendances actuelles
     */
    private List<Suggestion> getTrendingSuggestions(
            String query,
            int limit) {
        
        List<TrendingSearch> trends = 
            trendAnalysisService.getCurrentTrends(limit * 2);
        
        return trends.stream()
            .filter(trend -> trend.getQuery()
                .toLowerCase()
                .contains(query.toLowerCase()))
            .limit(limit)
            .map(trend -> Suggestion.builder()
                .text(trend.getQuery())
                .type(SuggestionType.TRENDING)
                .score(trend.getTrendScore())
                .metadata(Map.of(
                    "growth", trend.getGrowthRate(),
                    "volume", trend.getSearchVolume()))
                .build())
            .collect(Collectors.toList());
    }
    
    /**
     * Déduplique les suggestions et garde les meilleures
     */
    private List<Suggestion> deduplicateSuggestions(
            List<Suggestion> suggestions,
            int limit) {
        
        return suggestions.stream()
            // Grouper par texte et garder celle avec le meilleur score
            .collect(Collectors.toMap(
                Suggestion::getText,
                Function.identity(),
                (s1, s2) -> s1.getScore() > s2.getScore() ? s1 : s2
            ))
            .values()
            .stream()
            // Trier par score décroissant
            .sorted(Comparator.comparing(Suggestion::getScore).reversed())
            // Limiter le nombre
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * Calcule le score d'une suggestion depuis l'historique
     */
    private double calculateHistoryScore(SearchHistoryItem item) {
        // Score basé sur la récence et la fréquence
        long hoursSinceLastSearch = ChronoUnit.HOURS.between(
            item.getLastSearchedAt(), LocalDateTime.now());
        
        double recencyScore = Math.max(0, 1.0 - (hoursSinceLastSearch / 168.0));
        double frequencyScore = Math.min(1.0, item.getSearchCount() / 10.0);
        
        return (recencyScore * 0.6) + (frequencyScore * 0.4);
    }
    
    /**
     * Normalise un comptage en score entre 0 et 1
     */
    private double normalizeCount(long count) {
        // Normaliser avec une fonction logarithmique
        return Math.min(1.0, Math.log(count + 1) / Math.log(1000));
    }
    
    /**
     * Génère une clé de cache pour les suggestions
     */
    private String generateSuggestionCacheKey(SuggestionRequest request) {
        return String.format("suggestions:%s:%s:%d",
            request.getQuery().toLowerCase(),
            request.getLanguage(),
            request.getLimit());
    }
}
```

### CacheService (Service de Cache)

Le service qui gère le cache Redis pour optimiser les performances des recherches. Il implémente des stratégies intelligentes de mise en cache et d'invalidation.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheConfig cacheConfig;
    private final ObjectMapper objectMapper;
    
    /**
     * Récupère un résultat de recherche depuis le cache
     */
    public SearchResponse getSearchResult(String cacheKey) {
        try {
            String json = (String) redisTemplate.opsForValue().get(cacheKey);
            
            if (json != null) {
                log.debug("Cache hit for key: {}", cacheKey);
                return objectMapper.readValue(json, SearchResponse.class);
            }
            
            log.debug("Cache miss for key: {}", cacheKey);
            return null;
        } catch (Exception e) {
            log.error("Error reading from cache", e);
            return null;
        }
    }
    
    /**
     * Met en cache un résultat de recherche
     */
    public void cacheSearchResult(
            String cacheKey,
            SearchResponse response,
            Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(cacheKey, json, ttl);
            
            log.debug("Cached search result with key: {} (TTL: {})", 
                cacheKey, ttl);
            
            // Incrémenter les statistiques de cache
            incrementCacheStats("search_results", "write");
        } catch (Exception e) {
            log.error("Error writing to cache", e);
        }
    }
    
    /**
     * Récupère des suggestions depuis le cache
     */
    public SuggestionResponse getSuggestions(String cacheKey) {
        try {
            String json = (String) redisTemplate.opsForValue().get(cacheKey);
            
            if (json != null) {
                return objectMapper.readValue(json, 
                    SuggestionResponse.class);
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error reading suggestions from cache", e);
            return null;
        }
    }
    
    /**
     * Met en cache des suggestions
     */
    public void cacheSuggestions(
            String cacheKey,
            SuggestionResponse response,
            Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(cacheKey, json, ttl);
            
            incrementCacheStats("suggestions", "write");
        } catch (Exception e) {
            log.error("Error caching suggestions", e);
        }
    }
    
    /**
     * Invalide le cache pour une clé spécifique
     */
    public void invalidate(String cacheKey) {
        try {
            Boolean deleted = redisTemplate.delete(cacheKey);
            
            if (Boolean.TRUE.equals(deleted)) {
                log.debug("Invalidated cache key: {}", cacheKey);
                incrementCacheStats("all", "invalidate");
            }
        } catch (Exception e) {
            log.error("Error invalidating cache", e);
        }
    }
    
    /**
     * Invalide tous les caches correspondant à un pattern
     */
    public void invalidatePattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            
            if (keys != null && !keys.isEmpty()) {
                Long deleted = redisTemplate.delete(keys);
                log.debug("Invalidate{} keys matching pattern: {}", 
                    deleted, pattern);
                
                incrementCacheStats("all", "invalidate_pattern", 
                    deleted != null ? deleted : 0);
            }
        } catch (Exception e) {
            log.error("Error invalidating cache pattern", e);
        }
    }
    
    /**
     * Préchauffe le cache avec les recherches populaires
     */
    public void warmupCache(List<String> popularQueries) {
        log.info("Warming up cache with {} popular queries", 
            popularQueries.size());
        
        popularQueries.forEach(query -> {
            try {
                // Construire une requête de recherche basique
                SearchRequest request = SearchRequest.builder()
                    .query(query)
                    .page(0)
                    .size(10)
                    .language("fr")
                    .build();
                
                // Cette logique devrait appeler le SearchService
                // mais pour éviter une dépendance circulaire,
                // on utilise un événement ou un autre mécanisme
                publishWarmupRequest(request);
            } catch (Exception e) {
                log.error("Error warming up cache for query: {}", query, e);
            }
        });
    }
    
    /**
     * Récupère les statistiques du cache
     */
    public CacheStats getCacheStats() {
        try {
            Map<String, Long> searchStats = 
                getCategoryStats("search_results");
            Map<String, Long> suggestionStats = 
                getCategoryStats("suggestions");
            
            return CacheStats.builder()
                .searchResultsHits(searchStats.get("hits"))
                .searchResultsMisses(searchStats.get("misses"))
                .searchResultsWrites(searchStats.get("writes"))
                .suggestionsHits(suggestionStats.get("hits"))
                .suggestionsMisses(suggestionStats.get("misses"))
                .suggestionsWrites(suggestionStats.get("writes"))
                .totalInvalidations(getCategoryStats("all").get("invalidates"))
                .hitRate(calculateHitRate(searchStats))
                .build();
        } catch (Exception e) {
            log.error("Error getting cache stats", e);
            return CacheStats.empty();
        }
    }
    
    /**
     * Incrémente les statistiques de cache
     */
    private void incrementCacheStats(String category, String operation) {
        incrementCacheStats(category, operation, 1);
    }
    
    /**
     * Incrémente les statistiques de cache avec un count
     */
    private void incrementCacheStats(
            String category, 
            String operation, 
            long count) {
        String key = String.format("cache:stats:%s:%s", category, operation);
        redisTemplate.opsForValue().increment(key, count);
    }
    
    /**
     * Récupère les statistiques d'une catégorie
     */
    private Map<String, Long> getCategoryStats(String category) {
        Map<String, Long> stats = new HashMap<>();
        
        stats.put("hits", getStatValue(category, "read_hit"));
        stats.put("misses", getStatValue(category, "read_miss"));
        stats.put("writes", getStatValue(category, "write"));
        stats.put("invalidates", getStatValue(category, "invalidate"));
        
        return stats;
    }
    
    /**
     * Récupère une valeur de statistique
     */
    private Long getStatValue(String category, String operation) {
        String key = String.format("cache:stats:%s:%s", category, operation);
        String value = (String) redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0L;
    }
    
    /**
     * Calcule le taux de hit du cache
     */
    private double calculateHitRate(Map<String, Long> stats) {
        long hits = stats.get("hits");
        long misses = stats.get("misses");
        long total = hits + misses;
        
        return total > 0 ? (double) hits / total : 0.0;
    }
    
    /**
     * Publie une requête de pré-chauffage
     */
    private void publishWarmupRequest(SearchRequest request) {
        // Implémentation via un événement Spring ou Kafka
        // pour éviter la dépendance circulaire avec SearchService
    }
}
```

### ElasticsearchRepository (Couche Accès Données)

Le repository qui gère toutes les interactions avec Elasticsearch. Il encapsule la complexité des requêtes ES et fournit une interface simple aux services.

```java
@Repository
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchRepository {
    
    private final ElasticsearchClient elasticsearchClient;
    private final ElasticsearchConfig elasticsearchConfig;
    private final SearchDocumentMapper documentMapper;
    
    /**
     * Exécute une recherche simple dans un index
     */
    public SearchHits<SearchDocument> search(
            QueryBuilder query,
            List<AggregationBuilder> aggregations,
            int page,
            int size) {
        
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(query)
                .from(page * size)
                .size(size)
                .timeout(TimeValue.timeValueSeconds(
                    elasticsearchConfig.getSearchTimeout()));
            
            // Ajouter les agrégations
            if (aggregations != null) {
                aggregations.forEach(sourceBuilder::aggregation);
            }
            
            // Ajouter le highlighting
            sourceBuilder.highlighter(createHighlighter());
            
            // Ajouter les champs à retourner
            sourceBuilder.fetchSource(
                elasticsearchConfig.getIncludeFields(),
                elasticsearchConfig.getExcludeFields());
            
            SearchRequest searchRequest = new SearchRequest(
                elasticsearchConfig.getSearchIndexName())
                .source(sourceBuilder);
            
            SearchResponse response = 
                elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
            
            return mapSearchResponse(response);
        } catch (Exception e) {
            log.error("Error executing search", e);
            throw new SearchException("Failed to execute search", e);
        }
    }
    
    /**
     * Exécute une recherche multi-index
     */
    public Map<String, SearchHits<SearchDocument>> multiIndexSearch(
            Map<String, QueryBuilder> indexQueries,
            int page,
            int size) {
        
        Map<String, SearchHits<SearchDocument>> results = new HashMap<>();
        
        indexQueries.forEach((indexName, query) -> {
            try {
                SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .query(query)
                    .from(page * size)
                    .size(size);
                
                SearchRequest searchRequest = new SearchRequest(indexName)
                    .source(sourceBuilder);
                
                SearchResponse response = elasticsearchClient.search(
                    searchRequest, RequestOptions.DEFAULT);
                
                results.put(indexName, mapSearchResponse(response));
            } catch (Exception e) {
                log.error("Error searching index: {}", indexName, e);
            }
        });
        
        return results;
    }
    
    /**
     * Exécute une recherche avec scroll pour grands volumes
     */
    public ScrollSearchResponse scrollSearch(
            QueryBuilder query,
            String scrollId,
            Duration scrollTimeout) {
        
        try {
            if (scrollId == null) {
                // Première requête scroll
                SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .query(query)
                    .size(elasticsearchConfig.getScrollSize());
                
                SearchRequest searchRequest = new SearchRequest(
                    elasticsearchConfig.getSearchIndexName())
                    .source(sourceBuilder)
                    .scroll(scrollTimeout);
                
                SearchResponse response = elasticsearchClient.search(
                    searchRequest, RequestOptions.DEFAULT);
                
                return ScrollSearchResponse.builder()
                    .scrollId(response.getScrollId())
                    .hits(mapSearchResponse(response))
                    .hasMore(response.getHits().getHits().length > 0)
                    .build();
            } else {
                // Requêtes suivantes
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId)
                    .scroll(scrollTimeout);
                
                SearchResponse response = elasticsearchClient.scroll(
                    scrollRequest, RequestOptions.DEFAULT);
                
                return ScrollSearchResponse.builder()
                    .scrollId(response.getScrollId())
                    .hits(mapSearchResponse(response))
                    .hasMore(response.getHits().getHits().length > 0)
                    .build();
            }
        } catch (Exception e) {
            log.error("Error in scroll search", e);
            throw new SearchException("Failed to execute scroll search", e);
        }
    }
    
    /**
     * Indexe un document
     */
    public void indexDocument(SearchDocument document) {
        try {
            IndexRequest request = new IndexRequest(
                elasticsearchConfig.getSearchIndexName())
                .id(document.getId())
                .source(documentMapper.toMap(document))
                .timeout(TimeValue.timeValueSeconds(
                    elasticsearchConfig.getIndexTimeout()));
            
            IndexResponse response = elasticsearchClient.index(
                request, RequestOptions.DEFAULT);
            
            log.debug("Indexed document: {} with result: {}", 
                document.getId(), response.getResult());
        } catch (Exception e) {
            log.error("Error indexing document: {}", document.getId(), e);
            throw new IndexingException("Failed to index document", e);
        }
    }
    
    /**
     * Indexe plusieurs documents en bulk
     */
    public BulkIndexResponse bulkIndexDocuments(
            List<SearchDocument> documents) {
        
        try {
            BulkRequest bulkRequest = new BulkRequest()
                .timeout(TimeValue.timeValueMinutes(
                    elasticsearchConfig.getBulkTimeout()));
            
            documents.forEach(doc -> {
                IndexRequest request = new IndexRequest(
                    elasticsearchConfig.getSearchIndexName())
                    .id(doc.getId())
                    .source(documentMapper.toMap(doc));
                bulkRequest.add(request);
            });
            
            BulkResponse bulkResponse = elasticsearchClient.bulk(
                bulkRequest, RequestOptions.DEFAULT);
            
            int successCount = 0;
            int failureCount = 0;
            List<BulkItemResponse> failures = new ArrayList<>();
            
            for (BulkItemResponse item : bulkResponse.getItems()) {
                if (item.isFailed()) {
                    failureCount++;
                    failures.add(item);
                } else {
                    successCount++;
                }
            }
            
            log.info("Bulk indexing completed: {} success, {} failures", 
                successCount, failureCount);
            
            return BulkIndexResponse.builder()
                .totalDocuments(documents.size())
                .successCount(successCount)
                .failureCount(failureCount)
                .failures(failures)
                .took(bulkResponse.getTook())
                .build();
        } catch (Exception e) {
            log.error("Error in bulk indexing", e);
            throw new IndexingException("Failed to bulk index documents", e);
        }
    }
    
    /**
     * Supprime un document par ID
     */
    public void deleteDocument(String documentId) {
        try {
            DeleteRequest request = new DeleteRequest(
                elasticsearchConfig.getSearchIndexName(), documentId);
            
            DeleteResponse response = elasticsearchClient.delete(
                request, RequestOptions.DEFAULT);
            
            log.debug("Deleted document: {} with result: {}", 
                documentId, response.getResult());
        } catch (Exception e) {
            log.error("Error deleting document: {}", documentId, e);
            throw new IndexingException("Failed to delete document", e);
        }
    }
    
    /**
     * Met à jour un document partiellement
     */
    public void updateDocument(String documentId, Map<String, Object> updates) {
        try {
            UpdateRequest request = new UpdateRequest(
                elasticsearchConfig.getSearchIndexName(), documentId)
                .doc(updates)
                .timeout(TimeValue.timeValueSeconds(
                    elasticsearchConfig.getIndexTimeout()));
            
            UpdateResponse response = elasticsearchClient.update(
                request, RequestOptions.DEFAULT);
            
            log.debug("Updated document: {} with result: {}", 
                documentId, response.getResult());
        } catch (Exception e) {
            log.error("Error updating document: {}", documentId, e);
            throw new IndexingException("Failed to update document", e);
        }
    }
    
    /**
     * Effectue une recherche de suggestion (completion suggester)
     */
    public List<String> suggest(String field, String prefix, int size) {
        try {
            CompletionSuggestionBuilder suggestionBuilder = 
                SuggestBuilders.completionSuggestion(field)
                    .prefix(prefix)
                    .size(size);
            
            SuggestBuilder suggestBuilder = new SuggestBuilder()
                .addSuggestion("autocomplete", suggestionBuilder);
            
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .suggest(suggestBuilder);
            
            SearchRequest searchRequest = new SearchRequest(
                elasticsearchConfig.getSearchIndexName())
                .source(sourceBuilder);
            
            SearchResponse response = elasticsearchClient.search(
                searchRequest, RequestOptions.DEFAULT);
            
            CompletionSuggestion suggestion = response.getSuggest()
                .getSuggestion("autocomplete");
            
            return suggestion.getOptions().stream()
                .map(option -> option.getText().string())
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting suggestions", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Crée un highlighter pour mettre en évidence les termes de recherche
     */
    private HighlightBuilder createHighlighter() {
        return new HighlightBuilder()
            .field("title")
            .field("content")
            .field("description")
            .preTags("<mark>")
            .postTags("</mark>")
            .fragmentSize(150)
            .numOfFragments(3)
            .noMatchSize(150);
    }
    
    /**
     * Mappe la réponse Elasticsearch en SearchHits
     */
    private SearchHits<SearchDocument> mapSearchResponse(
            SearchResponse response) {
        
        List<SearchHit<SearchDocument>> hits = Arrays.stream(
            response.getHits().getHits())
            .map(this::mapSearchHit)
            .collect(Collectors.toList());
        
        return new SearchHits<>(
            hits,
            response.getHits().getTotalHits().value,
            response.getAggregations()
        );
    }
    
    /**
     * Mappe un SearchHit Elasticsearch en SearchHit du domaine
     */
    private SearchHit<SearchDocument> mapSearchHit(
            org.elasticsearch.search.SearchHit hit) {
        
        SearchDocument document = documentMapper.fromMap(
            hit.getSourceAsMap());
        
        // Ajouter les highlights si disponibles
        if (hit.getHighlightFields() != null) {
            Map<String, List<String>> highlights = new HashMap<>();
            hit.getHighlightFields().forEach((field, highlightField) -> {
                highlights.put(field, Arrays.stream(highlightField.fragments())
                    .map(Text::string)
                    .collect(Collectors.toList()));
            });
            document.setHighlights(highlights);
        }
        
        return SearchHit.<SearchDocument>builder()
            .id(hit.getId())
            .score(hit.getScore())
            .content(document)
            .build();
    }
}
```

---

## 🧮 Algorithmes de recherche

### Algorithme de Ranking Hybride

L'algorithme de ranking de YowYob Search combine plusieurs facteurs de scoring pour produire un classement optimal des résultats. Cet algorithme est au cœur de la pertinence du moteur et a été conçu après analyse des meilleures pratiques de l'industrie.

**Formule de scoring finale :**

```
Score_final = (w1 × Score_BM25) + (w2 × Score_Geo) + 
              (w3 × Score_Fraîcheur) + (w4 × Score_Popularité) + 
              (w5 × Score_Qualité)

Où les poids par défaut sont :
w1 = 0.40 (Pertinence textuelle BM25)
w2 = 0.30 (Distance géographique)
w3 = 0.20 (Fraîcheur du contenu)
w4 = 0.10 (Popularité/Engagement)
w5 = 0.10 (Qualité du document)

Ces poids sont configurables et peuvent être ajustés selon le contexte
```

**Détails de chaque composante :**

**Score BM25 (Best Matching 25)** : C'est l'algorithme de pertinence textuelle standard d'Elasticsearch. Il calcule la similarité entre la requête et le document en tenant compte de la fréquence des termes (TF), de la fréquence inverse dans le corpus (IDF), et de la longueur du document. BM25 améliore TF-IDF en ajoutant une saturation qui empêche les documents très longs ou avec beaucoup de répétitions d'être sur-scorés.

**Score Géospatial** : Pour les recherches avec composante de localisation, ce score diminue avec la distance. La formule utilisée est une décroissance linéaire jusqu'à une distance maximale configurable (par défaut 50km). Un document à 0km du point de recherche obtient un score de 1.0, à 25km un score de 0.5, et au-delà de 50km un score de 0.0. Pour les recherches sans composante géographique, ce score est fixé à 0.5 (neutre).

**Score de Fraîcheur** : Les documents récents sont privilégiés selon une fonction décroissante dans le temps. Les documents de moins de 24h obtiennent un score de 1.0, ceux de moins d'une semaine entre 0.9 et 0.7, ceux de moins d'un mois entre 0.7 et 0.4, et ceux de plus de 6 mois un score proche de 0. Ce mécanisme favorise l'information fraîche tout en permettant aux contenus evergreen de rester pertinents.

**Score de Popularité** : Basé sur les métriques d'engagement collectées par le Stats Service, ce score combine le CTR (Click-Through Rate), le temps moyen passé sur la page, et le taux de rebond. Un CTR élevé, un temps de visite long, et un faible taux de rebond indiquent que les utilisateurs trouvent ce résultat satisfaisant. Ces signaux comportementaux sont des indicateurs puissants de qualité.

**Score de Qualité** : Ce score évalue les attributs intrinsèques du document comme la présence d'un titre descriptif, d'une description complète, de contenu substantiel, d'images, et de métadonnées riches. Un document bien structuré et complet obtient un score plus élevé qu'un document minimal.

### Algorithme de Suggestions

L'algorithme de suggestions combine plusieurs sources de données pour fournir des recommandations pertinentes et personnalisées :

**Sources de suggestions :**

**Historique Personnel** : Pour les utilisateurs authentifiés, leurs recherches précédentes sont utilisées pour suggérer des requêtes similaires. Le score de ces suggestions décroît avec le temps depuis la dernière recherche et augmente avec la fréquence de recherche de ce terme.

**Recherches Populaires** : Les termes fréquemment recherchés par l'ensemble des utilisateurs sont suggérés. Ces suggestions sont calculées par agrégation dans l'index des requêtes de recherche avec un poids basé sur le volume de recherches.

**Completion Suggester d'Elasticsearch** : Utilise l'index de completion d'Elasticsearch pour fournir des suggestions ultra-rapides basées sur les titres et contenus indexés. Cette approche utilise une structure de données optimisée (FST - Finite State Transducer) qui permet des suggestions en temps quasi-constant.

**Tendances** : Les requêtes qui connaissent une augmentation soudaine de volume sont suggérées pour capturer les sujets d'actualité. Le TrendAnalysisService calcule le taux de croissance des recherches et booste les termes émergents.

**Processus de combinaison :**

Toutes ces sources sont interrogées en parallèle, puis leurs résultats sont fusionnés. Les suggestions sont dédupliquées en gardant celle avec le meilleur score pour chaque texte unique. Le score final de chaque suggestion est normalisé entre 0 et 1, et les suggestions sont triées par score décroissant avant d'être limitées au nombre demandé.

### Algorithme de Normalisation de Requêtes

Avant d'exécuter une recherche, la requête utilisateur passe par plusieurs étapes de normalisation pour améliorer la pertinence :

**Tokenisation** : La requête est découpée en tokens (mots) en tenant compte des délimiteurs standards et de certains cas spéciaux comme les URLs, les emails, et les nombres.

**Conversion en minuscules** : Tous les tokens sont convertis en minuscules pour une recherche insensible à la casse, sauf pour les termes qui sont intentionnellement en majuscules (acronymes).

**Suppression des stop words** : Les mots très courants qui n'apportent pas de valeur sémantique (le, la, de, à, etc.) sont retirés. Les listes de stop words sont personnalisées pour le français et peuvent être étendues.

**Stemming** : Les mots sont réduits à leur racine (radical) pour matcher les variations. Par exemple, "rechercher", "recherche", "recherché" deviennent tous "recherch". Cela permet de trouver des documents même si les formes exactes diffèrent.

**Correction orthographique** : Les fautes d'orthographe sont détectées et corrigées automatiquement en utilisant un dictionnaire et des distances de Levenshtein. Si une correction est appliquée, l'utilisateur en est informé avec "Recherche pour X au lieu de Y".

**Détection d'entités** : Les entités nommées (noms de lieux, de personnes, d'organisations) sont identifiées et peuvent être traitées spécifiquement, par exemple en les boostant ou en activant des filtres géographiques automatiques.

**Expansion de synonymes** : Les synonymes connus sont ajoutés à la requête avec un boost réduit. Par exemple, une recherche pour "resto" inclura aussi "restaurant" avec un poids moindre.

---

## 🔗 Intégration avec les autres modules

### Interactions avec Common Module

Le Search Service dépend du module Common pour de nombreuses fonctionnalités transversales :

**Configurations partagées** : Utilise les configurations Redis, Kafka et Elasticsearch définies dans Common. Ces configurations garantissent la cohérence des paramètres de connexion à travers tous les services.

**DTOs standardisés** : Utilise ApiResponse et PageResponse pour formater les réponses API de manière cohérente avec tous les autres services. Cela simplifie grandement le développement côté frontend.

**Gestion des exceptions** : Étend les exceptions du Common Module (AppException, ValidationException, etc.) et bénéficie du GlobalExceptionHandler qui convertit automatiquement les exceptions en réponses HTTP appropriées.

**Utilitaires** : Utilise les utilitaires de validation, de manipulation de texte, et de calculs géographiques fournis par Common. Cela évite la duplication de code et garantit des comportements cohérents.

**Mappers** : Utilise l'infrastructure MapStruct du Common Module pour les conversions entre DTOs et entités. Les mappers sont générés automatiquement à la compilation, éliminant les erreurs de runtime.

**Annotations** : Utilise les annotations personnalisées comme @LogExecutionTime pour mesurer les performances et @RateLimited pour protéger les endpoints contre les abus.

### Interactions avec Crawler Service

Le Search Service consomme les documents indexés par le Crawler Service via des événements Kafka :

**Événements d'indexation** : Le Crawler Service publie un événement "DocumentIndexed" sur Kafka chaque fois qu'une nouvelle page web est crawlée et analysée. Le Search Service écoute ces événements et indexe les documents dans Elasticsearch.

**Mise à jour de documents** : Lorsque le Crawler détecte qu'une page a changé, il publie un événement "DocumentUpdated" avec les nouvelles données. Le Search Service met à jour le document correspondant dans l'index.

**Suppression de documents** : Si une page n'est plus accessible (404) ou est explicitement supprimée, le Crawler publie un événement "DocumentDeleted" et le Search Service retire le document de l'index.

**Métadonnées de crawling** : Le Search Service utilise les métadonnées de crawling (date de dernière visite, fréquence de mise à jour, autorité du site) pour ajuster le scoring et la fraîcheur des résultats.

### Interactions avec Geo Service

Le Search Service délègue tous les calculs géospatiaux au Geo Service :

**Géocodage** : Lorsqu'une recherche inclut un nom de lieu textuel (par exemple "restaurants à Yaoundé"), le Search Service appelle le Geo Service pour obtenir les coordonnées GPS correspondantes.

**Calcul de distances** : Pour le scoring géospatial, le Search Service demande au Geo Service de calculer les distances entre le point de recherche et les localisations des documents.

**Recherche de proximité** : Le Search Service peut demander au Geo Service de fournir tous les lieux d'intérêt dans un rayon donné, qui seront ensuite utilisés pour filtrer ou booster les résultats.

**Enrichissement de résultats** : Les résultats de recherche qui ont une composante géographique sont enrichis avec des informations détaillées (adresse complète, carte, itinéraire) fournies par le Geo Service.

### Interactions avec User Service

Le Search Service s'intègre avec le User Service pour la personnalisation et l'authentification :

**Authentification** : Les endpoints qui nécessitent une authentification (comme l'historique de recherche) vérifient le token JWT fourni en appelant le User Service.

**Profil utilisateur** : Pour la personnalisation des résultats, le Search Service récupère les préférences utilisateur (langues préférées, catégories favorites, localisation par défaut) depuis le User Service.

**Historique de recherche** : Le Search Service enregistre les recherches des utilisateurs authentifiés et peut synchroniser cet historique avec le User Service pour l'afficher dans le profil utilisateur.

**Permissions** : Certains résultats de recherche peuvent être restreints selon les permissions de l'utilisateur. Le Search Service vérifie ces permissions via le User Service avant d'inclure ces résultats.

### Interactions avec Shop Service

Pour les recherches de produits e-commerce, le Search Service collabore étroitement avec le Shop Service :

**Index produits** : Le Shop Service publie des événements Kafka lorsque de nouveaux produits sont ajoutés, mis à jour ou supprimés. Le Search Service maintient un index Elasticsearch dédié aux produits basé sur ces événements.

**Recherche produits** : Lorsqu'une requête de recherche est identifiée comme liée au commerce (présence de mots-clés comme "acheter", "prix", "promo"), le Search Service interroge l'index produits en plus de l'index documents classique.

**Comparaison de prix** : Les résultats de recherche de produits incluent des informations de comparaison de prix agrégées depuis le Shop Service, permettant aux utilisateurs de voir les meilleures offres directement dans les résultats.

**Tracking des clics** : Lorsqu'un utilisateur clique sur un résultat produit, le Search Service publie un événement qui est consommé à la fois par le Shop Service (pour les statistiques marchands) et le Stats Service (pour l'analytique globale).

### Interactions avec Stats Service

Le Search Service fournit de nombreuses données au Stats Service pour l'analytique :

**Événements de recherche** : Chaque recherche effectuée génère un événement Kafka "SearchPerformed" qui contient la requête, le nombre de résultats, le temps de traitement, et les métadonnées contextuelles. Le Stats Service consomme ces événements pour les analyses.

**Événements de clic** : Lorsqu'un utilisateur clique sur un résultat, un événement "ResultClicked" est publié avec l'ID du résultat, sa position dans la liste, et le temps passé sur la page de résultats avant le clic.

**Métriques de performance** : Le Search Service expose des métriques Prometheus que le Stats Service collecte pour monitorer la santé du service (latence, taux d'erreur, utilisation du cache, etc.).

**Feedback utilisateur** : Les signaux comportementaux comme le temps passé sur une page de résultat, les clics sur la pagination, les raffinements de recherche sont tous capturés et envoyés au Stats Service pour améliorer le ranking.

### Interactions avec Notification Service

Le Search Service peut déclencher des notifications basées sur certains événements de recherche :

**Alertes de recherche** : Les utilisateurs peuvent configurer des alertes pour être notifiés lorsque de nouveaux résultats apparaissent pour une requête sauvegardée. Le Search Service vérifie périodiquement ces alertes et demande au Notification Service d'envoyer des notifications.

**Suggestions personnalisées** : Lorsque le Search Service détecte qu'un utilisateur pourrait être intéressé par certains sujets tendance (basé sur son historique), il peut demander au Notification Service d'envoyer une notification push.

**Résultats sponsorisés** : Pour les recherches commerciales, le Notification Service peut être utilisé pour envoyer des notifications promotionnelles pertinentes (avec le consentement de l'utilisateur).

---

## ⚙ Configuration et déploiement

### Configuration application.yml

Le fichier de configuration principal du Search Service définit tous les paramètres nécessaires pour son fonctionnement :

```yaml
# Configuration YowYob Search Service
spring:
  application:
    name: yowyob-search-service
  
  # Configuration Elasticsearch
  elasticsearch:
    uris: ${ELASTICSEARCH_URIS:http://localhost:9200}
    username: ${ELASTICSEARCH_USERNAME:elastic}
    password: ${ELASTICSEARCH_PASSWORD:changeme}
    connection-timeout: 10s
    socket-timeout: 30s
    
  # Configuration Redis pour le cache
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 0
    timeout: 2000
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
  
  # Configuration Kafka pour les événements
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: search-service-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka. ...etc

   
spring:
  kafka:
    consumer:
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    listener:
      concurrency: 3
      ack-mode: manual

# Configuration du serveur
server:
  port: ${SERVER_PORT:8082}
  compression:
    enabled: true
    mime-types: application/json,text/html,text/xml,text/plain
  
# Configuration des topics Kafka
kafka:
  topics:
    search-events: search-events
    document-indexed: document-indexed
    document-updated: document-updated
    document-deleted: document-deleted
    search-analytics: search-analytics
    click-events: click-events

# Configuration Elasticsearch spécifique
elasticsearch:
  indices:
    documents: yowyob-documents
    products: yowyob-products
    suggestions: yowyob-suggestions
  
  # Paramètres de recherche
  search:
    default-size: 10
    max-size: 100
    timeout: 30s
    scroll-size: 1000
    scroll-timeout: 5m
    
  # Paramètres d'indexation
  indexing:
    bulk-size: 500
    bulk-timeout: 10s
    refresh-interval: 1s
    number-of-shards: 3
    number-of-replicas: 1
    
  # Champs à inclure/exclure
  fields:
    include: ["id", "title", "url", "snippet", "publishedDate", "source", "category", "location", "images"]
    exclude: ["rawContent", "internalMetadata"]

# Configuration du ranking
ranking:
  # Poids des différents facteurs de scoring
  weights:
    relevance: 0.40      # Pertinence textuelle BM25
    geo: 0.30            # Distance géographique
    freshness: 0.20      # Fraîcheur du contenu
    popularity: 0.10     # Popularité/engagement
    quality: 0.10        # Qualité du document
  
  # Boosts par catégorie
  category-boosts:
    news: 1.5
    official: 1.3
    commercial: 0.8
    social: 0.7
  
  # Paramètres géospatiaux
  geo:
    max-relevant-distance: 50  # en kilomètres
    decay-function: linear
  
  # Paramètres de personnalisation
  personalization:
    enabled: true
    boost-factor: 1.2
    source-boost: 1.1

# Configuration du cache
cache:
  redis:
    ttl:
      search-results: 300      # 5 minutes
      suggestions: 600         # 10 minutes
      trending: 1800           # 30 minutes
      user-history: 3600       # 1 heure
    key-prefix: "search:"
    
  # Stratégies de cache
  strategies:
    search:
      min-results: 3           # Minimum de résultats pour cacher
      exclude-personalized: true
      exclude-recent-filters: true

# Configuration des suggestions
suggestions:
  max-suggestions: 10
  min-query-length: 2
  sources:
    user-history:
      enabled: true
      weight: 0.4
      max-items: 3
    popular:
      enabled: true
      weight: 0.3
      max-items: 5
    index:
      enabled: true
      weight: 0.2
      max-items: 5
    trending:
      enabled: true
      weight: 0.1
      max-items: 3

# Configuration de l'analytique
analytics:
  enabled: true
  sampling-rate: 1.0  # 100% des événements
  batch-size: 100
  flush-interval: 10s
  
  # Métriques à collecter
  metrics:
    search-latency: true
    cache-hit-rate: true
    result-relevance: true
    user-engagement: true

# Configuration des tâches planifiées
scheduling:
  cache-warmup:
    enabled: true
    cron: "0 0 */4 * * *"  # Toutes les 4 heures
    popular-queries-count: 100
  
  index-optimization:
    enabled: true
    cron: "0 0 2 * * *"    # Tous les jours à 2h du matin
  
  trend-analysis:
    enabled: true
    cron: "0 */30 * * * *" # Toutes les 30 minutes
  
  analytics-aggregation:
    enabled: true
    cron: "0 */15 * * * *" # Toutes les 15 minutes

# Configuration du monitoring
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
      probes:
        enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
      service: search

# Configuration des logs
logging:
  level:
    root: INFO
    com.yowyob.search: DEBUG
    org.elasticsearch: WARN
    org.springframework.data.elasticsearch: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/search-service.log
    max-size: 10MB
    max-history: 30

# Configuration des limites
limits:
  search:
    max-query-length: 500
    max-results-per-page: 100
    max-aggregations: 20
  
  rate-limiting:
    enabled: true
    anonymous:
      requests-per-minute: 30
      burst: 10
    authenticated:
      requests-per-minute: 100
      burst: 20
    premium:
      requests-per-minute: 500
      burst: 50

# Configuration de sécurité
security:
  cors:
    allowed-origins:
      - http://localhost:3000
      - https://yowyob.com
    allowed-methods:
      - GET
      - POST
      - OPTIONS
  
  # Protection contre les abus
  abuse-prevention:
    enabled: true
    max-similar-queries: 10
    time-window: 60  # secondes
    block-duration: 300  # secondes

# Configuration spécifique par environnement
---
spring:
  config:
    activate:
      on-profile: dev

elasticsearch:
  uris: http://localhost:9200

logging:
  level:
    com.yowyob.search: TRACE

---
spring:
  config:
    activate:
      on-profile: prod

elasticsearch:
  uris: 
    - https://es-node-1.yowyob.com:9200
    - https://es-node-2.yowyob.com:9200
    - https://es-node-3.yowyob.com:9200

logging:
  level:
    com.yowyob.search: INFO
```

### Configuration Elasticsearch - Mapping des documents

Le mapping Elasticsearch définit précisément comment les documents sont indexés et analysés. Cette configuration est cruciale pour la qualité de la recherche.

**Fichier : src/main/resources/elasticsearch/mappings/search-document-mapping.json**

Ce fichier définit la structure exacte de l'index Elasticsearch pour les documents web crawlés. Chaque champ est soigneusement configuré avec son type, ses analyseurs, et ses paramètres de recherche. Le mapping inclut des champs pour le texte full-text avec analyse linguistique avancée, des champs keyword pour les facettes et filtres exacts, des champs de type date pour les tris temporels, des champs géospatiaux de type geo_point pour les recherches de proximité, et des champs de completion pour l'autocomplétion ultra-rapide. Les analyseurs custom permettent de gérer les spécificités du français comme les accents, les elisions et le stemming. Les paramètres de similarité sont configurés pour utiliser BM25 avec des paramètres optimisés pour notre cas d'usage. Les champs multi-fields permettent d'indexer le même contenu de différentes manières pour supporter différents types de requêtes.

**Fichier : src/main/resources/elasticsearch/mappings/product-document-mapping.json**

Le mapping des produits est optimisé pour les recherches e-commerce. Il inclut des champs spécifiques comme le prix avec type scaled_float pour les calculs numériques précis, les variations de produit avec type nested pour les requêtes sur les attributs de variantes, les ratings et reviews avec agrégations statistiques, les catégories et tags avec type keyword et normalizer pour les facettes, les images avec URLs et métadonnées, les données marchands avec informations de disponibilité et livraison, et les champs de promotion avec dates de validité. Le mapping supporte également les recherches multi-attributs complexes comme filtrer par gamme de prix ET catégorie ET note minimum, rechercher des produits similaires basés sur les attributs, et trouver les meilleures offres par marchand.

**Fichier : src/main/resources/elasticsearch/settings/index-settings.json**

Les settings d'index définissent les paramètres au niveau de l'index qui affectent les performances et le comportement de la recherche. Cela inclut le nombre de shards primaires qui détermine le parallélisme des opérations, le nombre de replicas pour la haute disponibilité et la charge de lecture, l'intervalle de refresh qui contrôle la rapidité de visibilité des nouvelles données, les paramètres de merge pour optimiser les segments Elasticsearch, les slow logs pour identifier les requêtes lentes, et les politiques de lifecycle pour gérer automatiquement les données anciennes. Les settings incluent également la configuration des analyseurs custom qui définissent comment le texte est traité pendant l'indexation et la recherche, avec des tokenizers, des token filters et des char filters spécifiques à nos besoins linguistiques.

### Configuration du déploiement

**Dockerfile du Search Service**

Le Dockerfile utilise un build multi-stage pour optimiser la taille de l'image finale et la sécurité. La première étape utilise une image Maven avec JDK 21 pour compiler le code source et créer le JAR exécutable. Cette étape inclut toutes les dépendances de build et génère les classes MapStruct. La deuxième étape utilise une image JRE 21 minimal pour l'exécution, ne contenant que le runtime Java sans les outils de développement. Le JAR compilé est copié depuis la première étape, ainsi que les fichiers de configuration et ressources nécessaires. L'image finale est configurée avec un utilisateur non-root pour la sécurité, des healthchecks pour Kubernetes, des variables d'environnement pour la configuration dynamique, et des volumes pour les logs et données persistantes. L'exposition du port 8082 permet la communication avec l'API Gateway et les autres services.

**Fichiers Kubernetes**

Le déploiement Kubernetes du Search Service comprend plusieurs manifests YAML qui définissent l'infrastructure complète du service dans le cluster. Le Deployment définit le nombre de replicas pour la haute disponibilité, généralement 3 instances minimum en production, les ressources CPU et mémoire avec requests et limits pour garantir les performances et éviter l'éviction, les stratégies de rolling update pour les déploiements sans interruption, les probes de liveness et readiness pour la gestion automatique de la santé, et les volumes pour les configurations et secrets. Le Service expose le Deployment en interne au cluster sur le port 8082, avec un type ClusterIP pour l'accès depuis l'API Gateway uniquement, et des labels de sélection pour router le trafic vers les pods corrects. Le HorizontalPodAutoscaler ajuste automatiquement le nombre de replicas basé sur l'utilisation CPU moyenne cible de soixante-dix pour cent et les métriques custom comme le nombre de requêtes par seconde, avec un minimum de deux replicas et un maximum de dix replicas pour gérer les pics de charge. Les ConfigMaps contiennent la configuration applicative non-sensible comme les paramètres de ranking, les timeouts Elasticsearch, et les settings de cache. Les Secrets contiennent les informations sensibles comme les mots de passe Elasticsearch et Redis, et les tokens d'authentification, tous encodés en base64 et chiffrés au repos par Kubernetes.

### Scripts de déploiement

**Script d'initialisation Elasticsearch**

Ce script Bash initialise les index Elasticsearch nécessaires pour le Search Service avant le premier déploiement ou après une réinitialisation. Il commence par vérifier que le cluster Elasticsearch est accessible et en santé avec un statut yellow ou green. Ensuite, il crée les index avec les mappings et settings appropriés en utilisant les fichiers JSON de configuration. Pour chaque index créé, le script configure les aliases pour permettre la réindexation sans downtime, définit les index templates pour les patterns d'index futurs, et configure les ILM policies pour la gestion du lifecycle des données. Le script inclut également la gestion d'erreurs robuste pour éviter les problèmes si les index existent déjà, avec des options pour forcer la recréation ou simplement mettre à jour les settings. Des logs détaillés sont produits pour faciliter le debugging et l'audit des opérations.

**Script de déploiement continu**

Le script de déploiement CI/CD automatise complètement le processus de mise à jour du Search Service en production. Il commence par builder une nouvelle image Docker avec un tag basé sur le commit SHA ou la version sémantique, puis pousse cette image vers le registry Docker privé. Ensuite, il met à jour les manifests Kubernetes avec la nouvelle version d'image et applique les changements au cluster. Le déploiement utilise une stratégie de rolling update qui démarre progressivement les nouveaux pods tout en gardant les anciens actifs, attend que chaque nouveau pod soit ready avant de continuer, ne supprime les anciens pods qu'une fois les nouveaux validés, et peut automatiquement rollback en cas d'échec détecté. Le script inclut des points de contrôle de santé après déploiement qui vérifient que les endpoints health retournent OK, que les métriques Prometheus sont collectées correctement, que le cache Redis est accessible, et que les événements Kafka sont consommés. En cas de problème, le script peut automatiquement rollback vers la version précédente et notifier l'équipe via Slack ou email.

---

## ✅ Tests et qualité

### Stratégie de test complète

Le Search Service adopte une approche de test pyramidale avec une large base de tests unitaires rapides, une couche intermédiaire de tests d'intégration plus lents mais essentiels, et un sommet de tests end-to-end critiques qui valident les parcours utilisateur complets. Cette structure garantit une couverture de test élevée tout en maintenant des temps de build raisonnables.

**Tests unitaires**

Les tests unitaires forment la fondation de notre stratégie de qualité avec une couverture cible de quatre-vingt-cinq pour cent minimum du code. Chaque service, repository et utilitaire dispose de sa propre suite de tests qui valide son comportement de manière isolée. Les dépendances externes sont systématiquement mockées en utilisant Mockito pour garantir que les tests sont rapides, déterministes et ne dépendent pas de l'état du système. Les tests unitaires vérifient non seulement les cas nominaux où tout fonctionne correctement, mais aussi et surtout les cas d'erreur et les edge cases qui représentent la majorité des bugs en production. Par exemple, les tests du RankingService vérifient que le calcul de score fonctionne avec des documents normaux, mais aussi avec des documents sans date de publication, sans localisation, avec des valeurs extrêmes, et avec des combinaisons inhabituelles de paramètres. Les tests utilisent des fixtures de données réalistes qui reflètent la diversité des données en production. L'exécution complète de tous les tests unitaires prend moins de deux minutes, ce qui permet de les exécuter fréquemment pendant le développement et systématiquement dans la CI/CD.

**Tests d'intégration**

Les tests d'intégration vérifient que les différents composants du Search Service fonctionnent correctement ensemble et avec les dépendances externes réelles. Ces tests utilisent TestContainers pour démarrer des instances Docker d'Elasticsearch, Redis et Kafka pendant les tests, garantissant que les tests s'exécutent contre des versions exactes des dépendances production. Les tests d'intégration couvrent des scénarios complets comme l'indexation d'un document dans Elasticsearch et sa récupération via une recherche, la mise en cache d'un résultat dans Redis et sa récupération sur une requête identique, la publication d'un événement sur Kafka et sa consommation par le listener approprié, et la coordination entre plusieurs services pour un workflow end-to-end. Ces tests sont plus lents que les tests unitaires car ils impliquent de vraies opérations réseau et I/O, prenant typiquement cinq à dix minutes pour une exécution complète. Ils sont exécutés automatiquement dans la CI/CD mais peuvent être skipés localement pendant le développement rapide pour gagner du temps.

**Tests de performance**

Les tests de performance mesurent les caractéristiques de vitesse, throughput et utilisation de ressources du Search Service sous différentes charges. Ces tests utilisent JMeter ou Gatling pour simuler des charges réalistes avec des profils de trafic variés. Les tests de charge vérifient que le service maintient ses SLAs sous charge normale et modérée, typiquement mille à cinq mille requêtes par minute. Les tests de stress poussent le service jusqu'à sa limite pour identifier son point de rupture et comprendre son comportement sous charge extrême. Les tests de soak exécutent une charge constante pendant plusieurs heures pour détecter les fuites mémoire et autres problèmes qui n'apparaissent qu'après une exécution prolongée. Les tests de spike simulent des augmentations soudaines de trafic pour vérifier que l'autoscaling fonctionne correctement et que le service se remet rapidement. Les résultats des tests de performance sont trackés au fil du temps pour détecter les régressions et identifier les opportunités d'optimisation. Les métriques clés incluent le temps de réponse au percentile quatre-vingt-quinze qui doit rester sous deux cents millisecondes, le throughput maximum en requêtes par seconde, l'utilisation CPU et mémoire sous charge, et le taux d'erreur qui doit rester sous un pour cent.

**Tests de contrat API**

Les tests de contrat vérifient que l'API du Search Service respecte son contrat public et ne casse pas la compatibilité avec les clients existants. Ces tests utilisent Spring Cloud Contract ou Pact pour définir formellement le contrat de l'API avec des exemples de requêtes et réponses attendues. Les tests vérifient que tous les endpoints exposés retournent les codes de statut HTTP corrects pour les différents scénarios, que les structures de réponse JSON correspondent exactement au contrat avec tous les champs requis présents, que les validations de paramètres fonctionnent comme documenté, et que les messages d'erreur sont informatifs et suivent le format standardisé. Les contrats sont versionnés et les changements incompatibles déclenchent des alertes dans la CI/CD. Cette approche permet d'évoluer l'API en toute sécurité sans casser les intégrations existantes.

**Tests de sécurité**

Les tests de sécurité identifient les vulnérabilités potentielles et vérifient que les protections de sécurité fonctionnent correctement. Ces tests incluent l'analyse statique du code avec SpotBugs et Find Security Bugs pour détecter les patterns de code dangereux comme les injections SQL ou XSS, les tests de fuzzing qui envoient des entrées aléatoires malformées pour identifier les crashs ou comportements inattendus, les tests d'authentification et autorisation qui vérifient que les endpoints protégés rejettent les requêtes non autorisées, et les tests de rate limiting qui confirment que les limites de taux sont appliquées correctement. Les vulnérabilités des dépendances sont scannées automatiquement avec OWASP Dependency Check et les dépendances vulnérables sont mises à jour rapidement. Les tests de sécurité font partie intégrante de la CI/CD et tout problème de sécurité identifié bloque le déploiement.

### Outils de qualité de code

**SonarQube**

SonarQube analyse en continu la qualité du code du Search Service et identifie les bugs, vulnérabilités de sécurité, code smells et duplications. L'analyse produit des métriques détaillées sur la complexité cyclomatique, la couverture de test, la dette technique et la maintenabilité du code. Des quality gates sont configurées pour bloquer les merges qui introduisent une dette technique importante ou réduisent la couverture de test. Les développeurs peuvent voir directement dans leur IDE les issues SonarQube grâce aux plugins SonarLint, permettant de corriger les problèmes avant même de committer. Les dashboards SonarQube fournissent une vue d'ensemble de l'évolution de la qualité au fil du temps, aidant à prioriser les efforts de refactoring et d'amélioration.

**Checkstyle et SpotBugs**

Checkstyle vérifie que le code respecte les conventions de style définies pour le projet, garantissant une cohérence visuelle et syntaxique à travers toute la codebase. Les règles Checkstyle couvrent le formatage du code comme l'indentation et les espaces, le nommage des variables, classes et méthodes selon les conventions Java, l'organisation des imports et des déclarations, la documentation Javadoc des classes et méthodes publiques, et la complexité des méthodes et classes. SpotBugs complète Checkstyle en détectant des bugs potentiels par analyse statique comme les null pointer exceptions non gérées, les problèmes de concurrence et synchronisation, les ressources non fermées correctement, les comparaisons d'objets incorrectes avec double égal au lieu de equals, et les conversions de types dangereuses. Les deux outils sont intégrés dans le build Maven et font échouer la compilation si des violations sont détectées, garantissant que seul du code propre est intégré.

**JaCoCo pour la couverture**

JaCoCo mesure la couverture de test du code en instrumentant les classes compilées et en trackant quelles lignes sont exécutées pendant les tests. Les rapports JaCoCo montrent la couverture par package, classe et méthode, avec des visualisations colorées qui identifient rapidement les zones non testées. La configuration JaCoCo du Search Service impose un minimum de quatre-vingt pour cent de couverture au niveau des packages et soixante-quinze pour cent au niveau des branches, avec des exclusions pour le code généré automatiquement et les classes de configuration. Les rapports de couverture sont publiés dans SonarQube pour un suivi centralisé et peuvent être visualisés localement en HTML après chaque build. La couverture de test est un indicateur important de qualité mais n'est pas le seul critère car une couverture élevée ne garantit pas que les tests sont effectivement utiles et testent les bons comportements.

---

## 📊 Monitoring et observabilité

### Métriques applicatives

Le Search Service expose de nombreuses métriques via Micrometer qui sont collectées par Prometheus et visualisées dans Grafana. Ces métriques fournissent une visibilité complète sur le comportement du service en production.

**Métriques de recherche**

Les métriques de recherche mesurent l'activité et les performances des opérations de recherche. Le compteur de recherches totales track le volume de requêtes avec des tags pour différencier les types de recherche comme full-text, géospatiale, produit et multi-index. L'histogramme de latence de recherche mesure le temps de traitement complet d'une recherche du parsing de la requête jusqu'à la génération de la réponse, avec des percentiles calculés automatiquement pour identifier les outliers. Le compteur de résultats retournés track combien de documents matchent chaque recherche, permettant d'identifier les recherches qui ne retournent aucun résultat et nécessitent peut-être une amélioration. Les métriques de cache incluent le taux de hit et miss pour évaluer l'efficacité du caching, le temps moyen d'accès au cache, et la taille actuelle du cache. Les métriques de pagination trackent combien d'utilisateurs naviguent au-delà de la première page de résultats, indiquant si les résultats de la première page sont suffisamment pertinents.

**Métriques Elasticsearch**

Les métriques spécifiques à Elasticsearch mesurent la santé et les performances de la couche de stockage et indexation. Le temps de réponse des requêtes Elasticsearch est mesuré séparément du temps total de recherche pour identifier si les lenteurs viennent d'Elasticsearch ou du traitement applicatif. Le nombre de documents indexés par seconde mesure le throughput d'indexation. Les erreurs Elasticsearch sont comptées et catégorisées par type comme timeout, connection refused, index not found pour identifier rapidement les problèmes d'infrastructure. Les métriques de connexion incluent le nombre de connexions actives au pool Elasticsearch, les tentatives de connexion échouées, et les temps d'attente pour obtenir une connexion du pool. La taille des index en bytes et le nombre de documents par index permettent de planifier la capacité et d'identifier la croissance anormale.

**Métriques de ranking**

Les métriques de ranking fournissent des insights sur l'algorithme de scoring et permettent de mesurer son efficacité. La distribution des scores finaux montre si les résultats sont bien différenciés ou si beaucoup de résultats ont des scores similaires, ce qui rendrait le ranking moins significatif. Les contributions relatives des différents facteurs de scoring comme BM25, géospatial, fraîcheur, popularité, qualité sont mesurées pour comprendre quel facteur domine et si l'équilibre des poids est approprié. Le temps de calcul de ranking mesure combien de temps prend l'application de l'algorithme par rapport au temps total de recherche. Les métriques de personnalisation trackent combien de recherches bénéficient de la personnalisation et si les résultats personnalisés ont un meilleur engagement.

**Métriques de suggestions**

Les métriques de suggestions mesurent l'utilisation et la performance du système d'autocomplétion. Le nombre de requêtes de suggestions par seconde indique l'utilisation de l'autocomplétion. La latence de génération de suggestions doit être très faible car ces requêtes sont envoyées à chaque frappe de touche, donc chaque milliseconde compte pour l'expérience utilisateur. Le nombre de suggestions générées par requête et leur diversité de sources identifient si toutes les sources contribuent équitablement. Le taux de clic sur suggestions mesure combien de suggestions générées sont effectivement utilisées par les utilisateurs, indiquant leur pertinence.

**Métriques d'engagement utilisateur**

Les métriques d'engagement mesurent comment les utilisateurs interagissent avec les résultats de recherche. Le click-through rate global mesure le pourcentage de recherches qui résultent en au moins un clic sur un résultat. La position moyenne du premier clic indique si les résultats les plus pertinents sont bien positionnés en haut. Le temps avant premier clic mesure combien de temps l'utilisateur scanne les résultats avant de cliquer. Le taux de rebond après clic mesure combien de clics sont suivis d'un retour immédiat aux résultats, indiquant que le résultat n'était pas satisfaisant. Le nombre de raffinements de recherche compte combien de fois les utilisateurs modifient leur requête ou appliquent des filtres, indiquant si la première recherche était suffisamment bonne.

### Logs structurés

Le Search Service produit des logs structurés en JSON qui facilitent l'analyse et l'agrégation dans les systèmes de logging centralisés comme Elasticsearch via Logstash ou Fluentd. Chaque ligne de log contient des champs standardisés qui permettent les recherches et filtres efficaces.

**Logs de recherche**

Chaque recherche effectuée génère un log d'audit qui contient l'identifiant unique de la requête pour le tracking distribué, le timestamp précis de la requête, la requête textuelle de l'utilisateur, l'identifiant de l'utilisateur si authentifié ou une session ID anonyme, le nombre de résultats retournés, le temps de traitement total en millisecondes décomposé par étapes comme parsing, elasticsearch, ranking, cache, les paramètres de recherche comme page, size, filtres, localisation, et les métadonnées de contexte comme l'adresse IP, le user-agent, le referrer. Ces logs permettent de reconstruire exactement ce qui s'est passé pour chaque recherche et de débugger les problèmes reportés par les utilisateurs.

**Logs d'erreur**

Les erreurs sont loggées avec un niveau de détail élevé pour faciliter le debugging. Chaque log d'erreur inclut le message d'erreur lisible, la stack trace complète pour identifier précisément où l'erreur s'est produite, le contexte de la requête qui a causé l'erreur avec tous les paramètres, l'état du système au moment de l'erreur comme utilisation mémoire, nombre de connexions actives, et les tentatives de récupération effectuées comme retries, fallbacks. Les erreurs sont catégorisées par type et sévérité pour permettre l'alerting approprié. Les erreurs critiques qui empêchent le service de fonctionner déclenchent des alertes immédiates, tandis que les erreurs bénignes sont simplement loggées pour analyse ultérieure.

**Logs de performance**

Les logs de performance enregistrent les métriques de temps pour les opérations importantes. L'annotation LogExecutionTime du module Common mesure automatiquement le temps d'exécution des méthodes annotées et log le résultat. Les logs de performance incluent le nom de la méthode exécutée, les paramètres de la méthode en format anonymisé, le temps d'exécution total et décomposé par sous-opérations, et les métriques de ressources comme CPU et mémoire. Ces logs permettent d'identifier les goulots d'étranglement de performance et de mesurer l'impact des optimisations.

### Distributed tracing

Le Search Service participe au distributed tracing via OpenTelemetry pour permettre le suivi des requêtes à travers tout l'écosystème de microservices. Chaque requête se voit attribuer un trace ID unique qui est propagé à travers tous les services qu'elle traverse. Les spans individuels représentent les opérations dans chaque service avec leurs durées et métadonnées. Les traces complètes montrent le chemin exact d'une requête depuis son entrée dans l'API Gateway, à travers le Search Service, ses appels à Elasticsearch et Redis, ses publications d'événements Kafka, et jusqu'à la réponse finale au client. Les traces permettent d'identifier rapidement où le temps est passé dans une requête lente et de comprendre les dépendances entre services. Le tracing est échantillonné en production pour limiter l'overhead, typiquement un pour cent des requêtes sont tracées complètement.

### Dashboards Grafana

Des dashboards Grafana préconfigurés fournissent une vue d'ensemble de la santé et des performances du Search Service. Le dashboard principal affiche les métriques clés comme le nombre de recherches par minute avec tendance et comparaison par rapport aux périodes précédentes, la latence moyenne et percentiles avec seuils d'alerte visuels, le taux d'erreur avec breakdown par type d'erreur, l'utilisation des ressources CPU, mémoire, connexions, et le statut des dépendances comme Elasticsearch, Redis, Kafka. Des dashboards spécialisés fournissent des vues détaillées sur des aspects spécifiques comme le dashboard de ranking qui montre la distribution des scores et l'impact de chaque facteur, le dashboard de cache qui visualise les taux de hit/miss et l'évolution de la taille du cache, le dashboard d'engagement qui affiche les métriques de clic et conversion, et le dashboard d'infrastructure qui montre les métriques Elasticsearch détaillées.

### Alerting

Des alertes automatiques notifient l'équipe lorsque des problèmes sont détectés. Les alertes sont configurées dans Prometheus AlertManager et envoyées via Slack, PagerDuty ou email selon la sévérité. Les alertes critiques incluent le service down détecté par plusieurs health checks échoués consécutifs, le taux d'erreur élevé au-dessus de cinq pour cent sur une fenêtre de cinq minutes, la latence très élevée avec le percentile quatre-vingt-quinze au-dessus de cinq cents millisecondes, et l'Elasticsearch inaccessible détecté par des connexions échouées. Les alertes warning incluent la latence modérément élevée avec le percentile quatre-vingt-quinze au-dessus de trois cents millisecondes, l'utilisation des ressources élevée avec CPU ou mémoire au-dessus de quatre-vingt pour cent, et le taux de cache hit faible en dessous de cinquante pour cent. Les alertes sont configurées avec des périodes de grace pour éviter les faux positifs dus à des fluctuations temporaires.

---

## 👨‍💻 Guide de développement

### Ajout d'une nouvelle fonctionnalité de recherche

Pour ajouter une nouvelle fonctionnalité de recherche au Search Service, suivez cette méthodologie structurée qui garantit la qualité et la maintenabilité du code.

**Analyse des besoins**

Commencez par documenter clairement le besoin de la nouvelle fonctionnalité avec des user stories qui décrivent le comportement attendu du point de vue de l'utilisateur. Identifiez les cas d'usage principaux et les edge cases à supporter. Définissez les critères d'acceptation mesurables qui permettront de valider que la fonctionnalité fonctionne correctement. Considérez l'impact sur les performances et la scalabilité, en particulier si la fonctionnalité nécessite de nouveaux calculs intensifs ou accès base de données. Identifiez les dépendances sur d'autres services ou composants du système. Évaluez si la fonctionnalité nécessite des changements dans le modèle de données Elasticsearch ou dans les index existants.

**Design de la solution**

Concevez l'architecture de la solution en identifiant les composants qui devront être modifiés ou créés. Déterminez si un nouveau service est nécessaire ou si la logique peut être ajoutée à un service existant. Définissez les nouvelles classes de modèle et DTOs nécessaires pour représenter les données de la fonctionnalité. Spécifiez les nouvelles méthodes de repository si des requêtes Elasticsearch spécifiques sont nécessaires. Concevez l'API REST en définissant les nouveaux endpoints ou modifications des endpoints existants, avec leurs paramètres et formats de réponse. Identifiez les points d'intégration avec les autres services et les événements Kafka à publier ou consommer. Planifiez les tests nécessaires à différents niveaux unitaire, intégration, performance.

**Implémentation**

Implémentez la fonctionnalité en suivant les bonnes pratiques et standards du projet. Commencez par créer les classes de modèle et DTOs avec leurs annotations de validation. Implémentez les méthodes de repository pour les nouvelles requêtes Elasticsearch en testant les requêtes dans Kibana avant de les coder. Créez les services qui contiennent la logique métier de la fonctionnalité en gardant les méthodes focalisées et testables. Ajoutez les nouveaux endpoints dans les contrôleurs avec validation appropriée des paramètres. Configurez les nouveaux paramètres dans application.yml si la fonctionnalité a des settings configurables. Ajoutez les métriques et logs appropriés pour observer le comportement de la fonctionnalité en production. Documentez le code avec Javadoc et commentaires pour faciliter la maintenance future.

**Tests**

Écrivez des tests complets pour valider la fonctionnalité. Les tests unitaires vérifient chaque composant de manière isolée en mockant les dépendances et testant tous les chemins de code incluant les cas d'erreur. Les tests d'intégration valident que les composants fonctionnent correctement ensemble et avec les vraies dépendances Elasticsearch et Redis via TestContainers. Les tests de contrat API vérifient que les nouveaux endpoints respectent leur contrat et retournent les bonnes structures de réponse. Les tests de performance mesurent l'impact de la fonctionnalité sur la latence et le throughput pour s'assurer qu'elle ne dégrade pas les performances globales. Les tests de régression s'assurent que la fonctionnalité n'a pas cassé les comportements existants du service.

**Documentation**

Documentez la nouvelle fonctionnalité de manière complète. Mettez à jour ce README pour décrire la fonctionnalité et comment l'utiliser. Ajoutez des exemples de requêtes et réponses dans la documentation API. Documentez les nouveaux paramètres de configuration et leurs valeurs par défaut. Si la fonctionnalité nécessite des changements dans les index Elasticsearch, documentez les nouvelles étapes d'initialisation. Créez des runbooks pour les opérations qui décrivent comment monitorer, débugger et maintenir la fonctionnalité en production. Partagez la connaissance avec l'équipe via une présentation ou un document de design.

### Optimisation des performances

Les performances du Search Service sont critiques pour l'expérience utilisateur. Voici les domaines clés d'optimisation et comment les aborder.

**Optimisation des requêtes Elasticsearch**

Les requêtes Elasticsearch peuvent être optimisées de nombreuses manières. Utilisez des filtres au lieu de requêtes quand la pertinence n'est pas nécessaire car les filtres sont cachés et plus rapides. Limitez la taille des documents retournés en utilisant source filtering pour ne récupérer que les champs nécessaires. Évitez les scripts pendant les requêtes car ils sont lents et non cachés, préférez des champs calculés à l'indexation. Utilisez les agrégations avec parcimonie et limitez leur cardinalité pour éviter l'utilisation excessive de mémoire. Pour les recherches géospatiales, utilisez les filtres geo_distance plutôt que geo_distance_sort qui est plus lent. Optimisez les analyzers custom pour qu'ils ne fassent que le traitement nécessaire sans étapes superflues. Utilisez des requêtes multi-match avec les bons types comme best_fields, most_fields, cross_fields selon le cas d'usage. Configurez les similarity settings comme BM25 avec les paramètres k1 et b optimaux pour votre domaine.

**Optimisation du cache**

Le cache Redis est essentiel pour réduire la charge sur Elasticsearch. Identifiez les requêtes les plus fréquentes et assurez-vous qu'elles sont bien cachées. Ajustez les TTLs du cache selon la fraîcheur requise des données, avec des TTLs plus courts pour les données dynamiques et plus longs pour les données statiques. Implémentez un cache multi-niveau avec un cache local en mémoire pour les données ultra-fréquentes et Redis pour le cache distribué. Utilisez des stratégies d'invalidation intelligentes qui invalident seulement les entrées nécessaires plutôt que tout le cache. Mesurez le taux de hit du cache et investiguez pourquoi certaines requêtes ne sont pas cachées si le taux est trop bas. Pré-chauffez le cache au démarrage avec les recherches populaires pour améliorer les performances des premiers utilisateurs.

**Optimisation du ranking**

L'algorithme de ranking peut être optimisé sans sacrifier la qualité. Calculez et cachez les scores qui ne changent pas souvent comme le score de qualité et le score de popularité historique. Utilisez des approximations rapides pour les calculs coûteux comme les distances géographiques en calculant d'abord une bounding box pour éliminer les documents éloignés. Parallélisez les calculs de score indépendants pour utiliser tous les cores CPU disponibles. Limitez le nombre de documents sur lesquels le ranking complexe est appliqué en faisant d'abord un filtrage rapide puis le scoring détaillé uniquement sur les top candidats. Profilez l'algorithme de ranking pour identifier les goulots d'étranglement et optimisez ces parties critiques. Considérez des approximations de machine learning comme les fast scoring functions qui approximent des modèles complexes avec des calculs plus rapides.

**Optimisation des index Elasticsearch**

Les index Elasticsearch peuvent être optimisés pour améliorer les performances de recherche et d'indexation. Utilisez le bon nombre de shards basé sur la taille des données et le nombre de nœuds, évitant trop de shards qui causent de l'overhead ou trop peu qui limitent le parallélisme. Configurez les replicas selon les besoins de disponibilité et charge de lecture. Forcez le merge des segments régulièrement pour réduire leur nombre et améliorer les performances de recherche. Utilisez les index lifecycle policies pour archiver automatiquement les anciennes données vers des index moins performants mais moins coûteux. Activez le best compression pour réduire l'utilisation du disque si les performances de recherche sont acceptables. Désactivez les fonctionnalités non utilisées comme les doc values, norms, term vectors sur les champs qui n'en ont pas besoin pour réduire la taille de l'index.

### Débogage des problèmes de recherche

Lorsque des problèmes de recherche sont reportés, suivez cette méthodologie pour les identifier et résoudre rapidement.

**Reproduction du problème**

Commencez par reproduire le problème de manière fiable. Collectez toutes les informations nécessaires de l'utilisateur comme la requête exacte qui pose problème, les paramètres utilisés comme filtres, localisation, pagination, et le comportement observé versus le comportement attendu. Tentez de reproduire le problème en environnement de dev ou staging avant de débugger en production. Vérifiez si le problème affecte tous les utilisateurs ou seulement certains, et si c'est lié à des données spécifiques ou à un timing particulier.

**Analyse des logs**

Examinez les logs pour comprendre ce qui s'est passé pendant la requête problématique. Recherchez le trace ID ou request ID de la requête pour trouver tous les logs associés. Vérifiez les logs d'erreur pour voir si des exceptions ont été levées. Examinez les logs de performance pour identifier si une étape particulière a pris trop de temps. Vérifiez les logs des dépendances comme Elasticsearch pour voir si le problème vient de la couche de stockage. Utilisez les logs structurés et les outils de requête comme Kibana pour filtrer et analyser efficacement les grands volumes de logs.

**Analyse des traces distribuées**

Si le problème implique potentiellement plusieurs services, utilisez les traces distribuées pour visualiser le flux complet de la requête. Identifiez dans quelle span le temps est dépensé pour localiser le goulot d'étranglement. Vérifiez si des appels à d'autres services ont échoué ou pris trop de temps. Examinez les métadonnées des spans pour voir les paramètres et résultats de chaque étape. Les traces permettent souvent d'identifier rapidement si le problème est dans le Search Service lui-même ou dans une de ses dépendances.

**Tests d'hypothèses**

Formulez des hypothèses sur la cause du problème basées sur les logs et traces. Testez chaque hypothèse de manière systématique. Par exemple, si vous suspectez que le problème vient d'une requête Elasticsearch mal formée, copiez la requête exacte depuis les logs et exécutez-la directement dans Kibana pour voir le résultat. Si vous pensez qu'un filtre cause le problème, testez la recherche sans ce filtre. Si le cache est suspecté, désactivez-le temporairement et réessayez. Testez les hypothèses une à la fois en changeant une seule variable pour isoler la cause.

**Correction et validation**

Une fois la cause identifiée, implémentez la correction appropriée. Écrivez un test qui reproduit le problème et valide que la correction le résout. Vérifiez que la correction ne casse pas les fonctionnalités existantes en exécutant toute la suite de tests. Déployez la correction en staging et validez que le problème est résolu avant de déployer en production. Documentez la cause du problème et la solution dans un post-mortem ou incident report pour éviter que le problème se reproduise et pour partager les apprentissages avec l'équipe.

### Contribution au projet

Pour contribuer du code au Search Service, suivez le workflow Git standard de l'équipe avec des pull requests et code review.

**Création d'une branche**

Créez une branche depuis develop avec un nom descriptif qui indique le type de changement et un résumé court. Utilisez des préfixes standard comme feature slash pour une nouvelle fonctionnalité, bugfix slash pour une correction de bug, hotfix slash pour une correction urgente en production, refactor slash pour du refactoring sans changement de comportement, docs slash pour des changements de documentation uniquement. Par exemple feature slash advanced-filters ou bugfix slash ranking-null-pointer. Gardez les branches de courte durée, idéalement moins d'une semaine, pour faciliter les merges et réduire les conflits.

**Commits**

Faites des commits atomiques qui représentent chacun un changement logique complet. Utilisez des messages de commit descriptifs qui suivent le format Conventional Commits avec un type comme feat, fix, docs, style, refactor, test, chore, un scope optionnel qui indique le composant affecté, et une description concise au présent de l'impératif. Par exemple feat(ranking): add geo distance decay function ou fix(cache): handle null values in redis. Incluez un corps de message plus détaillé si nécessaire pour expliquer le pourquoi du changement, pas seulement le quoi. Référencez les issues ou tickets liés avec Closes hash vingt-trois ou Refs hash quarante-cinq.

**Tests**

Assurez-vous que tous les tests passent avant de pousser le code. Exécutez mvn clean verify localement pour exécuter tous les tests unitaires et d'intégration. Ajoutez des tests pour toute nouvelle fonctionnalité ou correction de bug. Vérifiez que la couverture de test reste au-dessus des seuils configurés. Si des tests existants échouent à cause de vos changements, mettez-les à jour de manière appropriée plutôt que de les supprimer.

**Pull request**

Créez une pull request vers develop avec un titre descriptif et une description complète. La description doit expliquer quel problème la PR résout, comment elle le résout, et inclure tout contexte nécessaire pour les reviewers. Ajoutez des captures d'écran ou GIFs pour les changements UI si applicable. Listez les étapes pour tester manuellement les changements. Mentionnez tout changement de configuration ou migration de données nécessaire. Assignez des reviewers appropriés qui ont l'expertise sur le code modifié. Répondez aux commentaires de review de manière constructive et faites les changements demandés. Une fois approuvée, la PR peut être merged via squash and merge pour garder l'historique git propre.

**Code review**

Lorsque vous reviewez une PR, vérifiez que le code suit les standards et best practices du projet, que la logique est correcte et gère tous les cas incluant les edge cases, que les tests sont adéquats et passent, que la performance n'est pas dégradée, que la sécurité n'est pas compromise, et que la documentation est mise à jour si nécessaire. Laissez des commentaires constructifs qui expliquent le problème et suggèrent des solutions plutôt que juste pointer les erreurs. Approuvez la PR seulement si vous êtes confiant qu'elle peut être merged en toute sécurité. Si des changements majeurs sont nécessaires, demandez une nouvelle review après les modifications.

---

## 🔄 Évolution et roadmap

### Améliorations planifiées

Le Search Service est en constante évolution pour améliorer la pertinence, les performances et l'expérience utilisateur. Voici les améliorations majeures planifiées pour les prochaines versions.

**Machine Learning pour le ranking**

L'intégration de machine learning dans l'algorithme de ranking permettra d'améliorer significativement la pertinence des résultats. Un modèle de Learning to Rank sera entraîné sur les données d'engagement historiques pour apprendre quels facteurs prédisent le mieux si un utilisateur va cliquer sur un résultat. Le modèle pourra capturer des interactions complexes entre les facteurs de scoring que l'algorithme manuel actuel ne peut pas représenter. Les features du modèle incluront tous les scores existants plus des features additionnelles comme les embeddings sémantiques de la requête et du document, les statistiques de qualité du domaine source, et l'historique de préférence de l'utilisateur. Le modèle sera entraîné offline sur des données historiques et mis à jour régulièrement avec les nouvelles données. Le déploiement utilisera un système A/B testing pour comparer les performances du nouveau modèle avec l'algorithme actuel avant de le rollout complètement.

**Recherche sémantique avec embeddings**

La recherche sémantique basée sur les embeddings permettra de trouver des résultats conceptuellement similaires même si les mots exacts ne matchent pas. Les documents seront représentés comme des vecteurs denses dans un espace sémantique où les documents similaires sont proches. Les embeddings seront générés par des modèles de langue pré-entraînés comme BERT ou Sentence-BERT fine-tunés sur notre domaine. Elasticsearch supporte les recherches de vecteurs via le type dense_vector et les requêtes knn. Les résultats de la recherche traditionnelle basée sur les mots-clés et de la recherche sémantique seront combinés via un mécanisme de hybrid scoring pour obtenir le meilleur des deux approches. La recherche sémantique améliorera particulièrement les résultats pour les requêtes longues, complexes ou mal formulées.

**Personnalisation avancée**

La personnalisation sera approfondie en utilisant plus de signaux utilisateur et des techniques de recommandation. Un profil utilisateur riche sera construit basé sur l'historique de recherche, les clics, le temps passé sur les pages, les catégories préférées, les sources fiables, et les topics d'intérêt. Des algorithmes de collaborative filtering identifieront des utilisateurs similaires et utiliseront leurs comportements pour personnaliser les résultats. Les résultats seront re-rankés en temps réel basé sur le profil de l'utilisateur actuel. La personnalisation sera transparente et expliquable, avec la possibilité pour l'utilisateur de voir pourquoi un résultat lui est recommandé et de désactiver la personnalisation s'il le souhaite. Des expérimentations A/B mesureront l'impact de la personnalisation sur les métriques d'engagement.

**Support multi-langue**

Le support multi-langue permettra de servir des utilisateurs francophones et anglophones avec la même qualité. Des analyzers spécifiques à chaque langue seront configurés dans Elasticsearch avec les bon stemming, stopwords et dictionnaires. Les documents seront indexés avec détection automatique de langue et stockés dans des index séparés par langue ou avec des champs multi-langue. Les requêtes seront automatiquement traduites ou les résultats multi-langues seront agrégés intelligemment. Les synonymes cross-lingues permettront de trouver des résultats dans d'autres langues si les résultats dans la langue de la requête sont insuffisants. L'interface utilisateur s'adaptera automatiquement à la langue du navigateur ou des préférences utilisateur.

**Recherche vocale**

L'intégration de la recherche vocale permettra aux utilisateurs de chercher en parlant plutôt qu'en tapant. Le frontend capturera l'audio via l'API Web Speech et l'enverra au backend pour transcription. La transcription sera effectuée par un service de speech-to-text optimisé pour le français avec support du vocabulaire local. Les requêtes vocales seront normalisées différemment des requêtes textuelles car elles ont tendance à être plus longues et plus conversationnelles. L'interface montrera la transcription à l'utilisateur avec la possibilité de la corriger avant de lancer la recherche. Les résultats pourront optionnellement être lus à voix haute pour une expérience complètement hands-free.

**Recherche d'images**

La recherche d'images permettra de trouver des images similaires ou de chercher du texte dans les images. Les images seront analysées pour extraire des features visuelles comme les couleurs dominantes, les formes, les objets détectés, et les embeddings profonds générés par des CNN. Un index dédié stockera ces features avec des liens vers les documents originaux. Les recherches pourront être par image où l'utilisateur upload une image pour trouver des images similaires, par description textuelle où l'utilisateur décrit ce qu'il cherche et les embeddings texte/image cross-modaux trouvent les matches, ou par OCR où le texte visible dans les images est extrait et indexé pour la recherche full-text. La recherche d'images sera particulièrement utile pour les produits e-commerce et les résultats visuels.

### Architecture future

L'architecture du Search Service évoluera pour supporter ces nouvelles fonctionnalités et améliorer la scalabilité.

**Micro-frontend pour l'interface de recherche**

L'interface de recherche actuelle dans le frontend monolithique sera extraite en micro-frontend indépendant. Ce micro-frontend pourra être développé, déployé et versionné indépendamment du reste du frontend. Il communiquera avec le Search Service via une API bien définie. Cette architecture permettra à différentes équipes de travailler sur différentes parties de l'expérience utilisateur sans conflits. Le micro-frontend sera intégrable dans d'autres applications via un web component standard.

**Service de Feature Store**

Un service de feature store centralisé sera créé pour gérer les features utilisées par les modèles de machine learning. Le feature store calculera et cachez les features qui sont coûteuses à calculer en temps réel. Il servira les features avec une latence très faible pour l'inférence en temps réel. Le versioning des features permettra de reproduire les prédictions du modèle et de débugger les problèmes. Le feature store facilitera le partage de features entre différents modèles et équipes.

**Pipeline de données temps réel**

Un pipeline de données temps réel basé sur Kafka Streams ou Apache Flink traitera les événements de recherche en streaming. Ce pipeline calculera des agrégations et métriques en temps réel comme les trending searches, les métriques de performance par région, et les anomalies de trafic. Les résultats seront stockés dans des stores optimisés pour les lectures rapides et utilisés pour alimenter les dashboards temps réel et influencer le ranking dynamiquement.

**Architecture serverless pour les pics**

Les composants stateless du Search Service seront déployables en mode serverless pour gérer les pics de trafic de manière économique. Les fonctions serverless seront déclenchées par les requêtes HTTP et scalent automatiquement de zéro à des milliers d'instances selon la charge. Cette architecture hybride combinera des instances always-on pour la charge de base et des fonctions serverless pour les pics, optimisant le coût tout en garantissant la performance.

---

## 📚 Ressources et références

### Documentation technique

**Elasticsearch officiel** : La documentation complète d'Elasticsearch couvre tous les aspects de la recherche full-text, des index, des requêtes, des agrégations, et du clustering. Les guides de référence des APIs REST détaillent tous les endpoints disponibles et leurs paramètres. Les best practices guides fournissent des recommandations d'experts sur l'architecture, la performance et la scalabilité. La documentation des mappings explique comment configurer les types de champs et les analyzers. Les guides de tuning performance identifient les goulots d'étranglement communs et comment les résoudre.

**Spring Data Elasticsearch** : La documentation Spring Data Elasticsearch explique comment intégrer Elasticsearch dans des applications Spring Boot. Les guides de référence couvrent les annotations pour les entités, les interfaces de repository, les opérations CRUD, et les requêtes custom. Les exemples de code montrent les patterns courants et les best practices. La documentation des templates et builders facilite la construction de requêtes complexes de manière type-safe.

**Redis officiel** : La documentation Redis couvre tous les types de données, commandes et patterns d'utilisation. Les guides de cache expliquent les stratégies de caching comme cache-aside, write-through, et write-behind. La documentation de persistence détaille les options RDB et AOF pour la durabilité des données. Les guides de clustering et réplication expliquent comment construire un setup Redis haute disponibilité.

**Kafka officiel** : La documentation Apache Kafka couvre les concepts fondamentaux des topics, partitions, producers et consumers. Les guides d'architecture expliquent les patterns comme event sourcing, CQRS, et streaming ETL. La documentation Kafka Streams facilite le traitement de données streaming. Les guides d'opérations couvrent le monitoring, tuning et troubleshooting des clusters Kafka.

### Articles et blogs techniques

**Elasticsearch blog** : Le blog officiel Elasticsearch publie régulièrement des articles sur les nouvelles features, les études de cas clients, et les best practices. Les articles techniques détaillent des sujets avancés comme le scoring personnalisé, les architectures distribuées, et l'optimisation des performances. Les annonces de releases expliquent les nouveaux développements et comment migrer depuis les versions précédentes.

**Martin Fowler sur les microservices** : Les articles de Martin Fowler définissent les principes fondamentaux des architectures microservices. Ses guides expliquent les patterns essentiels comme service discovery, circuit breaker, API gateway, et event-driven architecture. Les trade-offs entre microservices et monolithes sont analysés objectivement. Les anti-patterns communs sont identifiés avec des solutions alternatives.

**High Scalability** : Le site High Scalability partage des architecture breakdowns de systèmes à grande échelle comme Google, Facebook, Netflix. Les lessons learned de ces organisations fournissent des insights précieux sur la construction de systèmes robustes. Les patterns de scalabilité comme sharding, caching, load balancing sont expliqués avec des exemples concrets.

### Livres recommandés

**Elasticsearch: The Definitive Guide** : Ce livre officiel d'Elasticsearch couvre en profondeur tous les aspects de la recherche et l'analyse de données avec Elasticsearch. Les chapitres sur le relevance scoring expliquent comment fonctionne BM25 et comment le personnaliser. Les sections sur la modélisation des données donnent des guidelines pour structurer les index efficacement. Les guides de performance identifient les optimisations clés pour les charges de production.

**Designing Data-Intensive Applications** : Ce livre de Martin Kleppmann est la référence sur les systèmes de données distribués. Les chapitres couvrent les fondamentaux des bases de données, du caching, de la messagerie et du streaming. Les concepts de consistency, availability, partition tolerance sont expliqués clairement. Les patterns d'architecture pour les systèmes hautement disponibles sont détaillés avec leurs trade-offs.

**Release It!** : Ce livre de Michael Nygard se concentre sur la production readiness des systèmes. Les patterns de stabilité comme circuit breaker, bulkheads, timeouts sont expliqués avec des exemples de failure en production. Les anti-patterns qui causent des cascading failures sont identifiés. Les stratégies de deployment, monitoring et capacity planning sont couvertes en détail.

### Communauté et support

**Stack Overflow** : La communauté Stack Overflow a des milliers de questions et réponses sur Elasticsearch, Spring, Redis et les technologies utilisées par le Search Service. Les tags elasticsearch et spring-data-elasticsearch ont beaucoup d'activité avec des réponses d'experts. Avant de poster une nouvelle question, recherchez si elle a déjà été posée et répondue.

**Elastic forums** : Les forums officiels Elastic sont le meilleur endroit pour des questions spécifiques à Elasticsearch. Les ingénieurs Elastic répondent régulièrement aux questions. Les discussions couvrent l'utilisation avancée, le troubleshooting, et les annonces de produits. Les discussions archivées contiennent beaucoup de connaissances historiques sur les problèmes communs et leurs solutions.

**GitHub issues** : Le repository GitHub du Search Service est le point central pour reporter des bugs, demander des features, et discuter des changements d'architecture. Les issues doivent être descriptives avec des steps to reproduce pour les bugs et des use cases clairs pour les features. Les pull requests sont encouragées pour contribuer du code, de la documentation ou des tests.

---

## 🎓 Conclusion

Le module Search Service représente le cœur technique de la plateforme YowYob Search, implémentant un moteur de recherche moderne qui combine la puissance d'Elasticsearch avec des algorithmes de ranking intelligents et une architecture microservices scalable. Ce document a fourni une vue complète et détaillée de tous les aspects du service, depuis son architecture et ses composants jusqu'à sa configuration, son déploiement et son évolution future.

La qualité du code et l'excellence technique sont au centre de ce module, avec une couverture de test élevée, un monitoring complet, et des pratiques de développement rigoureuses. L'architecture a été conçue pour la scalabilité, la résilience et la maintenabilité, permettant au service de gérer des millions de recherches tout en restant facile à faire évoluer.

Les développeurs qui travaillent sur ce module peuvent se référer à cette documentation comme guide de référence complet. Chaque section a été rédigée avec suffisamment de détails pour permettre une compréhension profonde du système et faciliter le développement de nouvelles fonctionnalités ou le débogage de problèmes complexes.

Le Search Service continuera d'évoluer avec l'ajout de machine learning, de recherche sémantique, et de personnalisation avancée, tout en maintenant les standards de qualité et de performance qui font sa force aujourd'hui.

---

**Document préparé par l'équipe YowYob - 4GI ENSPY Promo 2027**

**Version** : 1.0.0  
**Dernière mise à jour** : Décembre 2025  
**Status** : Documentation de référence complète et autonome

---

