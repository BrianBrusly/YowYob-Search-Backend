# YowYob Search PWA - Backend

> **Plateforme de recherche intelligente distribuée** - Architecture microservices Spring Boot avec Java 21, Elasticsearch, Kafka et géolocalisation avancée

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

---

##  Table des matières

- [ Vue d'ensemble](#-vue-densemble)
- [ Architecture](#️-architecture)
- [ Stack technique](#️-stack-technique)
- [ Microservices](#-microservices)
- [ Prérequis](#-prérequis)
- [ Installation](#-installation)
- [ Configuration](#️-configuration)
- [ Lancement local](#-lancement-local)
- [ Tests](#-tests)
- [ Build & Déploiement](#-build--déploiement)
- [ API Documentation](#-api-documentation)
- [ Conventions de code](#-conventions-de-code)
- [ Monitoring](#-monitoring)
- [ Roadmap](#️-roadmap)
- [ Extension Commerce](#-extension-commerce)
- [ FAQ & Dépannage](#-faq--dépannage)

---

##  Vue d'ensemble

**YowYob Search Backend** est le cœur du moteur de recherche intelligent distribué, fournissant :

-  **Recherche full-text avancée** avec ranking hybride (BM25 + ML)
-  **Géolocalisation temps réel** avec PostGIS et OpenStreetMap
-  **Web crawling respectueux** (robots.txt, politeness, sitemap)
-  **Authentification sécurisée** (JWT + Refresh Tokens + BCrypt)
-  **Notifications multi-canal** (Email SMTP, Web Push VAPID, FCM)
-  **Architecture événementielle** (Kafka pour event-driven microservices)
-  **Cache distribué intelligent** (Redis pour performance optimale)
-  **Scalabilité horizontale** (design cloud-native Kubernetes-ready)

###  Connexion logique avec les autres repositories

```
┌─────────────────────────────────────────────────────────────┐
│                     ARCHITECTURE GLOBALE                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌────────────────┐    ┌──────────────────┐   ┌──────────┐  │
│  │   FRONTEND     │───▶│   API GATEWAY    │◀──│ NGINX    │  │
│  │   (Next.js)    │    │  (Port 8080)     │   │ Ingress  │  │
│  └────────────────┘    └──────────────────┘   └──────────┘  │
│         │                       │                           │
│         │                       ▼                           │
│         │              ┌─────────────────┐                  │
│         │              │  MICROSERVICES  │                  │
│         │              │   - Search      │                  │
│         │              │   - Crawler     │                  │
│         │              │   - User        │                  │
│         │              │   - Geo         │                  │
│         │              │   - Notification│                  │
│         │              │   - Shop        │                  │
│         │              │   - Stats       │                  │
│         │              └─────────────────┘                  │
│         │                       │                           │
│         ▼                       ▼                           │
│  ┌─────────────────────────────────────────┐                │
│  │           DATA LAYER                    │                │
│  │  ┌──────────┐ ┌──────────┐ ┌─────────┐  │                │
│  │  │PostgreSQL│ │Elasticsearch│ Redis  │  │                │
│  │  │+ PostGIS │ │   Cluster  │ Cache   │  │                │
│  │  └──────────┘ └──────────┘ └─────────┘  │                │
│  │  ┌──────────────────────────────────┐   │                │
│  │  │     Apache Kafka (Event Bus)     │   │                │
│  │  └──────────────────────────────────┘   │                │
│  └─────────────────────────────────────────┘                │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Ce repository gère** : Toute la logique métier, les APIs REST, la persistance, le crawling, la recherche et les événements.

**Dépend de** : `YowYob-Search-Infrastructure` pour l'orchestration (Docker/Kubernetes/Monitoring).

**Est consommé par** : `YowYob-Search-Frontend` via l'API Gateway.

---

##  Architecture

### Structure modulaire Maven Multi-Module

```
yowyob-search-backend/
│
├── pom.xml                           # Parent POM (dependency management)
│
├── yowyob-common/                    #  Module commun partagé
│   ├── src/main/java/
│   │   └── com/yowyob/common/
│   │       ├── dto/                  # DTOs standards (ApiResponse, PageResponse)
│   │       ├── exception/            # Exceptions métier + GlobalExceptionHandler
│   │       ├── security/             # Utils JWT, BCrypt, SecurityConfig
│   │       ├── mapper/               # MapStruct mappers
│   │       └── util/                 # DateUtils, StringUtils, Validators
│   └── pom.xml
│
├── yowyob-api-gateway/               #  API Gateway (Spring Cloud Gateway)
│   ├── src/main/java/
│   │   └── com/yowyob/gateway/
│   │       ├── config/               # Routes, CORS, Rate Limiting, Circuit Breaker
│   │       ├── filter/               # JWT Filter, Logging Filter
│   │       └── GatewayApplication.java
│   ├── src/main/resources/
│   │   └── application.yml           # Routes dynamiques vers microservices
│   └── pom.xml
│
├── yowyob-search-service/            #  Search Engine Core
│   ├── src/main/java/
│   │   └── com/yowyob/search/
│   │       ├── controller/           # SearchController, SuggestionController
│   │       ├── service/              # SearchService, RankingService, CacheService
│   │       ├── repository/           # ElasticsearchRepository, SearchHistoryRepo
│   │       ├── model/
│   │       │   ├── document/         # SearchDocument (ES)
│   │       │   └── dto/              # SearchRequest, SearchResponse, Filters
│   │       └── config/               # ElasticsearchConfig, RedisConfig, KafkaProducerConfig
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── elasticsearch/
│   │       └── mappings.json         # Index mapping pour documents
│   └── pom.xml
│
├── yowyob-crawler-service/           #  Web Crawler (JSoup + Tika)
│   ├── src/main/java/
│   │   └── com/yowyob/crawler/
│   │       ├── controller/           # CrawlerController (start/stop/status)
│   │       ├── service/              # CrawlOrchestrator, IndexingService
│   │       ├── crawler/              # YowYobBot, URLFrontier, ContentParser
│   │       ├── scheduler/            # Quartz Jobs pour crawls périodiques
│   │       ├── repository/           # CrawlJobRepo, URLQueueRepo
│   │       └── model/
│   │           ├── entity/           # CrawlJob, URLQueue, CrawledPage, RobotsTxt
│   │           └── dto/              # CrawlRequest, CrawlStatus
│   └── pom.xml
│
├── yowyob-user-service/              #  Auth & User Management
│   ├── src/main/java/
│   │   └── com/yowyob/user/
│   │       ├── controller/           # AuthController, UserController
│   │       ├── service/              # AuthService, JwtService, RefreshTokenService
│   │       ├── repository/           # UserRepository, RefreshTokenRepository
│   │       ├── model/
│   │       │   ├── entity/           # User, RefreshToken, Role
│   │       │   └── dto/              # LoginRequest, RegisterRequest, AuthResponse
│   │       └── security/             # SecurityConfig, JwtAuthenticationFilter
│   └── pom.xml
│
├── yowyob-geo-service/               #  Géolocalisation (PostGIS + OSM)
│   ├── src/main/java/
│   │   └── com/yowyob/geo/
│   │       ├── controller/           # GeoController
│   │       ├── service/              # GeocodingService, SpatialSearchService
│   │       ├── client/               # NominatimClient (Feign), OSMClient
│   │       ├── repository/           # GeoLocationRepository (PostGIS)
│   │       └── model/
│   │           ├── entity/           # GeoLocation (géométries PostGIS)
│   │           └── dto/              # Location, Address, GeoSearchRequest
│   └── pom.xml
│
├── yowyob-notification-service/      #  Notifications multi-canal
│   ├── src/main/java/
│   │   └── com/yowyob/notification/
│   │       ├── controller/           # NotificationController
│   │       ├── service/              # WebPushService, FcmService, EmailService
│   │       ├── consumer/             # KafkaConsumer (écoute événements)
│   │       ├── repository/           # PushSubscriptionRepository
│   │       └── model/
│   │           ├── entity/           # PushSubscription, NotificationHistory
│   │           └── dto/              # SubscriptionRequest, NotificationRequest
│   └── pom.xml
│
├── yowyob-shop-service/              #  Agrégateur E-commerce
│   ├── src/main/java/
│   │   └── com/yowyob/shop/
│   │       ├── controller/           # CatalogIngestController, ProductController
│   │       ├── service/              # IngestService, ProductService, ScrapingService
│   │       ├── repository/           # ProductRepository, OfferRepository
│   │       ├── model/
│   │       │   ├── entity/           # Product, Offer, Merchant
│   │       │   └── dto/              # IngestRequest, ProductResponse
│   │       └── producer/             # KafkaProducer (product-index events)
│   └── pom.xml
│
├── yowyob-stats-service/             #  Analytics & Statistiques
│   ├── src/main/java/
│   │   └── com/yowyob/stats/
│   │       ├── controller/           # StatsController, MetricsController
│   │       ├── service/              # AnalyticsService, MetricsService
│   │       ├── consumer/             # KafkaConsumer (search, clicks, redirects)
│   │       ├── repository/           # SearchStatsRepository, ClickStatsRepository
│   │       └── model/
│   │           ├── entity/           # SearchEvent, ClickEvent, MerchantStats
│   │           └── dto/              # MetricsResponse, RealtimeMetrics
│   └── pom.xml
│
├── .github/
│   └── workflows/
│       ├── build.yml                 # CI: Build + Tests
│       ├── docker.yml                # Build images Docker
│       └── deploy.yml                # CD: Deploy vers Kubernetes
│
├── docker/                           # Dockerfiles par service
│   ├── api-gateway.Dockerfile
│   ├── search-service.Dockerfile
│   ├── crawler-service.Dockerfile
│   ├── user-service.Dockerfile
│   ├── geo-service.Dockerfile
│   ├── notification-service.Dockerfile
│   ├── shop-service.Dockerfile
│   └── stats-service.Dockerfile
│
├── docs/                             # Documentation supplémentaire
│   ├── API.md                        # Endpoints détaillés
│   ├── ARCHITECTURE.md               # Diagrammes C4
│   └── DEPLOYMENT.md                 # Guide déploiement
│
├── scripts/
│   ├── init-db.sql                   # Init PostgreSQL schemas
│   ├── init-es.sh                    # Init Elasticsearch indexes
│   └── generate-keys.sh              # Génération clés JWT/VAPID
│
├── .gitignore
├── LICENSE
└── README.md                         # Ce fichier
```

###  Pourquoi cette structure ?

1. **Séparation des préoccupations** : Chaque microservice a une responsabilité unique (SRP)
2. **Réutilisabilité** : `yowyob-common` évite la duplication de code
3. **Scalabilité** : Chaque service peut être déployé, scalé et versionné indépendamment
4. **Testabilité** : Tests unitaires par module, tests d'intégration isolés avec TestContainers
5. **Maintenance** : Structure claire = onboarding rapide, debugging facilité

---

##  Stack technique

### Backend Framework
- **Java 21** (LTS, Virtual Threads, Pattern Matching)
- **Spring Boot 3.2.x** (Spring 6, Jakarta EE)
- **Spring Cloud Gateway** (Reactive, non-blocking)
- **Spring WebFlux** (Reactive Programming pour Search/Crawler/Geo)
- **Spring Data JPA** (User Service)
- **Spring Data Elasticsearch**
- **Spring Kafka**

### Bases de données & Stockage
- **PostgreSQL 15** (Données relationnelles)
- **PostGIS 3.3** (Extension spatiale pour Geo Service)
- **Elasticsearch 8.x** (Full-text search, geo queries)
- **Redis 7** (Cache, sessions, rate limiting, blacklist tokens)

### Messaging & Events
- **Apache Kafka 3.5** (Event streaming, CQRS, event sourcing)
- **Topics** : `search-queries`, `document-indexed`, `product-index`, `product-clicks`, `merchant-redirects`, `notifications`, `user-events`

### Crawling & Parsing
- **JSoup 1.16** (HTML parsing)
- **Apache Tika 2.9** (Document extraction, language detection)
- **Quartz Scheduler** (Crawl jobs planifiés)

### Géolocalisation
- **OpenStreetMap Nominatim API** (Géocodage)
- **PostGIS** (Requêtes spatiales, calculs de distance)
- **GeoTools** (Transformations de coordonnées)

### Sécurité
- **JWT (JJWT 0.12)** (Access tokens RS256, 15 min TTL)
- **BCrypt** (Password hashing, 12 rounds)
- **Spring Security 6**
- **CORS Configuration** (Contrôle des origines)

### Notifications
- **JavaMail (Spring Boot Starter Mail)** (SMTP)
- **Web Push (webpush-java)** (VAPID)
- **Firebase Admin SDK** (FCM pour mobile)

### Observabilité
- **Spring Boot Actuator** (Health checks, metrics)
- **Micrometer** (Métriques Prometheus)
- **Logback + SLF4J** (Logging structuré JSON)
- **OpenTelemetry** (Distributed tracing)

### Tests
- **JUnit 5** (Tests unitaires)
- **Mockito** (Mocking)
- **TestContainers** (Tests d'intégration avec vrais conteneurs)
- **WireMock** (Mock HTTP externe)
- **Embedded Kafka** (Tests asynchrones)

### Build & CI/CD
- **Maven 3.9+** (Build, dependency management)
- **GitHub Actions** (CI/CD automatisé)
- **Docker** (Containerisation)
- **Kubernetes** (Orchestration, voir repo Infrastructure)

---

## Microservices

###  API Gateway (`yowyob-api-gateway`)

**Rôle** : Point d'entrée unique pour tous les clients

**Port** : `8080`

**Configuration** :
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: search-service
          uri: lb://SEARCH-SERVICE
          predicates:
            - Path=/api/search/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: searchCircuitBreaker
                fallbackUri: forward:/fallback/search
```

###  Search Service (`yowyob-search-service`)

**Rôle** : Cœur du moteur de recherche

**Port** : `8082`

**Endpoints** :
```http
GET  /search                     # Recherche principale
GET  /search/suggestions         # Autocomplétion
GET  /search/trending            # Tendances
GET  /search/history             # Historique utilisateur
```

**Algorithme de ranking** :
```
Score final = 
  0.40 × BM25(pertinence textuelle)
  0.30 × GeoScore(distance)
  0.20 × FreshnessScore(récence)
  0.10 × PopularityScore(CTR historique)
```

###  Crawler Service (`yowyob-crawler-service`)

**Rôle** : Robot d'indexation web (YowYobBot)

**Port** : `8085`

**Architecture crawling** :
```
┌──────────────────┐
│  CrawlScheduler  │  ← Quartz Jobs
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ CrawlOrchestrator│
└────────┬─────────┘
         │
         ▼
┌──────────────────────────────┐
│      URL Frontier            │
│ (Priority Queue PostgreSQL)  │
└────────┬─────────────────────┘
         │
         ▼
┌──────────────────────────────┐
│       YowYobBot              │
│  - RobotsTxtManager (Redis)  │
│  - ContentParser (JSoup)     │
│  - QualityFilter (Tika)      │
└────────┬─────────────────────┘
         │
         ▼
┌──────────────────────────────┐
│  IndexingService             │
│  (Bulk to Elasticsearch)     │
└──────────────────────────────┘
```

###  User Service (`yowyob-user-service`)

**Rôle** : Gestion des utilisateurs et authentification

**Port** : `8083`

**Flux JWT** :
```
┌─────────┐     Login (email/password)       ┌──────────────┐
│ Client  │─────────────────────────────────▶│ User Service │
└─────────┘                                  └──────┬───────┘
     │                                              │
     │           BCrypt.verify()                    │
     │                  ✓                           │
     │                                              ▼
     │                                  ┌──────────────────────┐
     │                                  │  Generate JWT Tokens │
     │                                  │  - Access: 15 min    │
     │                                  │  - Refresh: 7 days   │
     │                                  └──────────┬───────────┘
     │                                             │
     │   { accessToken, refreshToken }             │
     │◀────────────────────────────────────────────┘
     │
     │  Store in localStorage
     ▼
┌─────────────────┐
│  API Calls      │
│  Authorization: │
│  Bearer <token> │
└─────────────────┘
```

###  Geo Service (`yowyob-geo-service`)

**Rôle** : Géolocalisation et recherches spatiales

**Port** : `8084`

**Exemple géocodage** :
```http
GET /api/geo/geocode?address=Yaoundé, Cameroun

Response:
{
  "latitude": 3.8667,
  "longitude": 11.5167,
  "address": "Yaoundé, Mfoundi, Centre, Cameroun",
  "city": "Yaoundé",
  "country": "Cameroun",
  "source": "nominatim"
}
```

###  Notification Service (`yowyob-notification-service`)

**Rôle** : Envoi de notifications multi-canal

**Port** : `8086`

**Kafka Consumer** :
```java
@KafkaListener(topics = "search-queries")
public void handleSearchEvent(SearchQueryEvent event) {
    if (isTrending(event.getQuery())) {
        sendTrendingNotification(event);
    }
}
```

###  Shop Service (`yowyob-shop-service`)

**Rôle** : Agrégation et normalisation produits e-commerce

**Port** : `8087`

**Responsabilités** :
- Scraping produits (JSoup + politeness)
- API connectors (OpenFeign)
- Normalisation et déduplication
- Price comparison
- Publication événements `product-index`

### Stats Service (`yowyob-stats-service`)

**Rôle** : Analytics et métriques temps réel

**Port** : `8088`

**Fonctionnalités** :
- Collecte événements (search, clicks, redirects)
- Métriques temps réel (Redis counters)
- Aggrégations Kafka Streams
- API SSE pour dashboards merchants

---

##  Prérequis

### Développement local
- **JDK 21** (OpenJDK ou Oracle)
- **Maven 3.9+**
- **Docker** & **Docker Compose**
- **Git**

### Services externes (via Infrastructure repo)
- PostgreSQL 15 + PostGIS
- Elasticsearch 8.x
- Redis 7
- Apache Kafka 3.5

---

##  Installation

### 1. Cloner le repository
```bash
git clone https://github.com/BrianBrusly/YowYob-Search-Backend.git
cd YowYob-Search-Backend
```

### 2. Installer les dépendances
```bash
mvn clean install -DskipTests
```

### 3. Générer les clés JWT et VAPID
```bash
./scripts/generate-keys.sh
```

### 4. Lancer l'infrastructure
```bash
cd ../YowYob-Search-Infrastructure
docker-compose up -d postgres elasticsearch redis kafka
```

### 5. Initialiser les bases
```bash
# PostgreSQL schemas
docker exec -i yowyob-postgres psql -U postgres < ../YowYob-Search-Backend/scripts/init-db.sql

# Elasticsearch indexes
./scripts/init-es.sh
```

---

##  Configuration

### Variables d'environnement (.env)
```bash
# Database
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_USER=yowyob
POSTGRES_PASSWORD=your_secure_password

# Elasticsearch
ELASTICSEARCH_HOST=localhost
ELASTICSEARCH_PORT=9200

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# JWT
JWT_PRIVATE_KEY_PATH=classpath:keys/private.pem
JWT_PUBLIC_KEY_PATH=classpath:keys/public.pem
JWT_EXPIRATION_MS=900000

# OpenStreetMap Nominatim
NOMINATIM_BASE_URL=https://nominatim.openstreetmap.org
```

### Profils Spring
- `dev` : Développement local
- `test` : Tests automatisés
- `prod` : Production

---

##  Lancement local

### Option 1 : Script de démarrage
```bash
./scripts/start-all-services.sh
```

### Option 2 : Docker Compose (recommandé)
```bash
mvn clean package -DskipTests
docker-compose up --build
```

### Option 3 : Services individuels
```bash
# Terminal 1 - API Gateway
cd yowyob-api-gateway
mvn spring-boot:run

# Terminal 2 - Search Service  
cd yowyob-search-service
mvn spring-boot:run

# Répéter pour chaque service...
```

### Vérification santé
```bash
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8082/actuator/health  # Search Service
# ... tous les services doivent répondre {"status":"UP"}
```

---

##  Tests

### Tests unitaires
```bash
mvn test
```

### Tests d'intégration
```bash
mvn verify
```

### Coverage
```bash
mvn jacoco:report
# Rapport dans : target/site/jacoco/index.html
```

---

##  Build & Déploiement

### Build production
```bash
mvn clean package -Pprod -DskipTests
```

### Build images Docker
```bash
docker-compose build
```

### Déploiement Kubernetes
```bash
cd ../YowYob-Search-Infrastructure
kubectl apply -f k8s/backend/
```

---

##  API Documentation

### Swagger UI (Dev)
- http://localhost:8082/swagger-ui.html  # Search Service
- http://localhost:8083/swagger-ui.html  # User Service
- http://localhost:8084/swagger-ui.html  # Geo Service
- http://localhost:8085/swagger-ui.html  # Crawler Service
- http://localhost:8086/swagger-ui.html  # Notification Service
- http://localhost:8087/swagger-ui.html  # Shop Service
- http://localhost:8088/swagger-ui.html  # Stats Service

### Collection Postman
Voir : `/docs/postman/YowYob-Backend.postman_collection.json`

---

##  Conventions de code

### Style Java
```java
public class SearchService {
    private static final int MAX_RESULTS = 100;
    private final ElasticsearchClient elasticsearchClient;
    
    public SearchResponse executeSearch(SearchRequest request) {
        // ...
    }
}
```

### Structure des packages
```
com.yowyob.{service}/
├── controller/        # REST endpoints
├── service/           # Business logic
├── repository/        # Data access
├── model/
│   ├── entity/        # JPA entities
│   ├── document/      # ES documents
│   └── dto/           # Data Transfer Objects
├── config/            # Spring configuration
├── exception/         # Custom exceptions
└── util/              # Utilities
```

### Gestion des erreurs
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(ex.getMessage()));
    }
}
```

---

##  Monitoring

### Actuator Endpoints
```http
GET /actuator/health         # Santé globale
GET /actuator/info           # Infos application
GET /actuator/metrics        # Métriques Micrometer
GET /actuator/prometheus     # Format Prometheus
```

### Métriques personnalisées
- `search_queries_total`
- `search_latency_seconds`
- `crawler_pages_indexed_total`
- `shop_offers_ingested_total`
- `product_clicks_total`

### Distributed Tracing
Trace IDs propagés via headers : `X-Trace-Id: 5f9c8a3b-1e2d-4c7f-9a6e-8b5d3f2e1c0a`

---

##  Roadmap

### Phase 1 (MVP) -  Complété
- [x] Architecture microservices
- [x] API Gateway + Services core
- [x] Search avec Elasticsearch
- [x] Auth JWT + Sécurité
- [x] Monitoring + Observabilité

### Phase 2 ( En cours)
- [ ] Machine Learning pour ranking personnalisé
- [ ] Vector Search avec embeddings
- [ ] Rate limiting avancé
- [ ] Analytics dashboard temps réel

### Phase 3 ( Futur)
- [ ] Support multi-langues (5+ langues)
- [ ] Recherche d'images (computer vision)
- [ ] Recherche vocale
- [ ] Knowledge Graph

---

##  Extension Commerce

### Nouveau microservice : `yowyob-shop-service`

**Responsabilités** :
- Catalogue produits (indexation Elasticsearch)
- Recherche produits (filtres prix, catégories, avis)
- Agrégation offres multi-merchants
- Dédoublonnage intelligent
- Price comparison

**Exemple recherche produits** :
```json
POST /api/search
{
  "query": "laptop gaming",
  "type": "PRODUCT",
  "filters": {
    "priceMin": 500,
    "priceMax": 2000,
    "category": "electronics",
    "brand": ["Dell", "HP", "Lenovo"],
    "rating": 4,
    "availability": "in_stock"
  },
  "sortBy": "PRICE_ASC"
}
```

---

##  FAQ & Dépannage

### Problème : Services ne démarrent pas
```bash
# Vérifier ports occupés
lsof -i :8080
# Tuer processus si nécessaire
kill -9 <PID>
```

### Problème : Connexion base de données
```bash
# Vérifier PostgreSQL
docker-compose ps postgres
# Tester connexion
psql -h localhost -U yowyob -d yowyob_db
```

### Problème : Elasticsearch inaccessible
```bash
# Vérifier status
curl http://localhost:9200/_cluster/health
```

### Problème : Kafka ne consomme pas
```bash
# Lister consumer groups
kafka-consumer-groups --bootstrap-server localhost:9092 --list
# Voir lag
kafka-consumer-groups --bootstrap-server localhost:9092 --group yowyob-notification-service --describe
```

---

##  License

MIT License - voir [LICENSE](LICENSE)

---

##  Contribution

1. Fork le repository
2. Créer une branche : `git checkout -b feature/amazing-feature`
3. Commit : `git commit -m 'feat: add amazing feature'`
4. Push : `git push origin feature/amazing-feature`
5. Ouvrir une Pull Request

**Conventions commit** : [Conventional Commits](https://www.conventionalcommits.org/)

---

**Développé par l'équipe YowYob** 

*Pour toute question technique, consultez d'abord `/docs/` ou ouvrez une issue GitHub.*
