# 🔵 YowYob Search PWA - Backend

> **Plateforme de recherche intelligente distribuée** - Projet backend monorepo Spring Boot multi-microservices pour la recherche intelligente YowYob, incluant API Gateway, Search, Crawler, User, Geo, Notification.
 Architecture microservices avec Spring Boot 3.x, Java 21, Elasticsearch, Kafka et géolocalisation avancée

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

---

## 📋 Table des matières

- [Vue d'ensemble](#-vue-densemble)
- [Architecture](#-architecture)
- [Stack technique](#-stack-technique)
- [Microservices](#-microservices)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Lancement local](#-lancement-local)
- [Tests](#-tests)
- [Build & Déploiement](#-build--déploiement)
- [API Documentation](#-api-documentation)
- [Conventions de code](#-conventions-de-code)
- [Monitoring](#-monitoring)
- [Roadmap](#-roadmap)

---

## 🎯 Vue d'ensemble

**YowYob Search Backend** est le cœur du moteur de recherche intelligent. Il fournit :

- ✅ **Recherche full-text avancée** avec ranking personnalisé (BM25 + ML)
- ✅ **Géolocalisation temps réel** avec PostGIS et OpenStreetMap
- ✅ **Web crawling respectueux** (robots.txt, politeness, sitemap)
- ✅ **Authentification sécurisée** (JWT + Refresh Tokens + BCrypt)
- ✅ **Notifications multi-canal** (Email SMTP, Web Push VAPID, FCM)
- ✅ **Architecture événementielle** (Kafka pour event-driven microservices)
- ✅ **Cache distribué intelligent** (Redis pour performance optimale)
- ✅ **Scalabilité horizontale** (design cloud-native Kubernetes-ready)

### 🔗 Connexion logique avec les autres repositories
```
┌─────────────────────────────────────────────────────────────┐
│                     ARCHITECTURE GLOBALE                     │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌────────────────┐    ┌──────────────────┐   ┌──────────┐ │
│  │   FRONTEND     │───▶│   API GATEWAY    │◀──│ NGINX    │ │
│  │   (Next.js)    │    │  (Port 8080)     │   │ Ingress  │ │
│  └────────────────┘    └──────────────────┘   └──────────┘ │
│         │                       │                            │
│         │                       ▼                            │
│         │              ┌─────────────────┐                  │
│         │              │  MICROSERVICES  │                  │
│         │              │   - Search      │                  │
│         │              │   - Crawler     │                  │
│         │              │   - User        │                  │
│         │              │   - Geo         │                  │
│         │              │   - Notification│                  │
│         │              └─────────────────┘                  │
│         │                       │                            │
│         ▼                       ▼                            │
│  ┌─────────────────────────────────────────┐               │
│  │           DATA LAYER                     │               │
│  │  ┌──────────┐ ┌──────────┐ ┌─────────┐│               │
│  │  │PostgreSQL│ │Elasticsearch│ │ Redis  ││               │
│  │  │+ PostGIS │ │   Cluster  │ │ Cache  ││               │
│  │  └──────────┘ └──────────┘ └─────────┘│               │
│  │  ┌──────────────────────────────────┐  │               │
│  │  │     Apache Kafka (Event Bus)     │  │               │
│  │  └──────────────────────────────────┘  │               │
│  └─────────────────────────────────────────┘               │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

**Ce repository gère** : Toute la logique métier, les APIs REST, la persistance, le crawling, la recherche et les événements.

**Dépend de** : `YowYob-Search-Infrastructure` pour l'orchestration (Docker/Kubernetes/Monitoring).

**Est consommé par** : `YowYob-Search-Frontend` via l'API Gateway.

---

## 🏗 Architecture

### Structure modulaire Maven Multi-Module
```
yowyob-search-backend/
│
├── pom.xml                           # Parent POM (dependency management)
│
├── yowyob-common/                    # 🔧 Module commun partagé
│   ├── src/main/java/
│   │   └── com/yowyob/common/
│   │       ├── dto/                  # DTOs standards (ApiResponse, PageResponse)
│   │       ├── exception/            # Exceptions métier + GlobalExceptionHandler
│   │       ├── security/             # Utils JWT, BCrypt, SecurityConfig
│   │       └── util/                 # DateUtils, StringUtils, Validators
│   └── pom.xml
│
├── yowyob-api-gateway/               # 🚪 API Gateway (Spring Cloud Gateway)
│   ├── src/main/java/
│   │   └── com/yowyob/gateway/
│   │       ├── config/               # Routes, CORS, Rate Limiting, Circuit Breaker
│   │       ├── filter/               # JWT Filter, Logging Filter
│   │       └── GatewayApplication.java
│   ├── src/main/resources/
│   │   └── application.yml           # Routes dynamiques vers microservices
│   └── pom.xml
│
├── yowyob-search-service/            # 🔍 Search Engine Core
│   ├── src/main/java/
│   │   └── com/yowyob/search/
│   │       ├── controller/           # SearchController, SuggestionController
│   │       ├── service/              # SearchService, RankingService, CacheService
│   │       ├── repository/           # ElasticsearchRepository, SearchHistoryRepo
│   │       ├── model/
│   │       │   ├── entity/           # Document, SearchHistory, TrendingSearch
│   │       │   └── dto/              # SearchRequest, SearchResponse, Filters
│   │       └── config/               # ElasticsearchConfig, RedisConfig, KafkaProducerConfig
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── elasticsearch/
│   │       └── mappings.json         # Index mapping pour documents
│   └── pom.xml
│
├── yowyob-crawler-service/           # 🕷️ Web Crawler (JSoup + Tika)
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
├── yowyob-user-service/              # 👤 Auth & User Management
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
├── yowyob-geo-service/               # 🌍 Géolocalisation (PostGIS + OSM)
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
├── yowyob-notification-service/      # 📬 Notifications multi-canal
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
│   └── notification-service.Dockerfile
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

### 🧩 Pourquoi cette structure ?

1. **Séparation des préoccupations** : Chaque microservice a une responsabilité unique (SRP).
2. **Réutilisabilité** : `yowyob-common` évite la duplication de code (DTOs, utils, exceptions).
3. **Scalabilité** : Chaque service peut être déployé, scalé et versionné indépendamment.
4. **Testabilité** : Tests unitaires par module, tests d'intégration isolés avec TestContainers.
5. **Maintenance** : Structure claire = onboarding rapide, debugging facilité.

---

## 🛠 Stack technique

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
    - Topics : `search-queries`, `document-indexed`, `notifications`, `user-events`

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

## 🎯 Microservices

### 1️⃣ API Gateway (`yowyob-api-gateway`)

**Rôle** : Point d'entrée unique pour tous les clients (Frontend, Mobile, API externe)

**Responsabilités** :
- ✅ Routage intelligent vers microservices
- ✅ Authentification JWT (vérification signature)
- ✅ Rate limiting global (Redis-based)
- ✅ Circuit breaker (résilience)
- ✅ Load balancing
- ✅ CORS configuration
- ✅ Logging centralisé des requêtes

**Port** : `8080`

**Endpoints exposés** :
```
/api/search/**      → Search Service
/api/auth/**        → User Service
/api/users/**       → User Service
/api/crawler/**     → Crawler Service
/api/geo/**         → Geo Service
/api/notifications/**→ Notification Service
```

**Configuration clé** (`application.yml`) :
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

**Apport au système** :
- Simplifie l'architecture client (1 seul endpoint à connaître)
- Centralise la sécurité et le rate limiting
- Améliore la résilience avec circuit breaker

---

### 2️⃣ Search Service (`yowyob-search-service`)

**Rôle** : Cœur du moteur de recherche

**Responsabilités** :
- ✅ Recherche full-text (Elasticsearch DSL)
- ✅ Ranking hybride (BM25 + Geo + Freshness + Popularity)
- ✅ Suggestions autocomplete
- ✅ Spelling correction ("Did you mean")
- ✅ Trending searches
- ✅ Cache intelligent (Redis, TTL: 5 min)
- ✅ Historique de recherche (PostgreSQL)
- ✅ Publication d'événements Kafka

**Port** : `8082`

**Endpoints principaux** :
```
GET  /search                     # Recherche principale
GET  /search/suggestions         # Autocomplétion
GET  /search/trending            # Tendances
GET  /search/history             # Historique utilisateur
DELETE /search/history           # Supprimer historique
```

**Technologies clés** :
- **Spring Data Elasticsearch** (Requêtes DSL)
- **Redis** (Cache distribué)
- **Kafka Producer** (Événements `search-queries`)

**Exemple de requête** :
```json
POST /api/search
{
  "query": "restaurants yaoundé",
  "page": 0,
  "size": 10,
  "filters": {
    "language": "fr",
    "dateFrom": "2024-01-01",
    "radius": 5
  },
  "location": {
    "latitude": 3.8667,
    "longitude": 11.5167
  },
  "sortBy": "RELEVANCE"
}
```

**Algorithme de ranking** :
```
Score final = 
  0.40 × BM25(pertinence textuelle)
  0.30 × GeoScore(distance)
  0.20 × FreshnessScore(récence)
  0.10 × PopularityScore(CTR historique)
```

**Apport au système** :
- Recherche ultra-rapide (< 100ms P95)
- Pertinence optimale grâce au scoring hybride
- Cache hit rate élevé (60-70%)
- Géolocalisation native dans les résultats

---

### 3️⃣ Crawler Service (`yowyob-crawler-service`)

**Rôle** : Robot d'indexation web (YowYobBot)

**Responsabilités** :
- ✅ Crawling respectueux (robots.txt, politeness delay)
- ✅ Parsing HTML (JSoup) et extraction de contenu
- ✅ Filtrage qualité (longueur, langue, spam)
- ✅ Indexation bulk dans Elasticsearch
- ✅ Gestion de la frontier (queue d'URLs avec priorité)
- ✅ Scheduleur Quartz pour crawls périodiques
- ✅ Support sitemap.xml

**Port** : `8085`

**Endpoints principaux** :
```
POST /crawler/start              # Démarrer crawl
POST /crawler/stop/:id           # Arrêter crawl
GET  /crawler/jobs               # Liste des jobs
GET  /crawler/jobs/:id           # Détails job
GET  /crawler/stats              # Statistiques globales
```

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

**Respect des standards** :
- User-Agent: `YowYobBot/1.0 (+https://yowyob.com/bot)`
- Cache robots.txt (Redis, TTL: 24h)
- Politeness delay : 1 seconde par défaut (configurable)
- Max 1 requête simultanée par domaine

**Apport au système** :
- Indexation automatique du web
- Respect des serveurs crawlés (éthique)
- Qualité des données (filtrage spam/doublons)
- Scalabilité (workers parallèles par domaine)

---

### 4️⃣ User Service (`yowyob-user-service`)

**Rôle** : Gestion des utilisateurs et authentification

**Responsabilités** :
- ✅ Inscription (email + password + BCrypt)
- ✅ Connexion (JWT access + refresh tokens)
- ✅ Vérification email (token expirable 24h)
- ✅ Mot de passe oublié (reset token)
- ✅ Gestion des profils
- ✅ RBAC (rôles : USER, WEBMASTER, ADMIN)
- ✅ Refresh token rotation (sécurité)
- ✅ Blacklist tokens révoqués (Redis)

**Port** : `8083`

**Endpoints principaux** :
```
POST /auth/register              # Inscription
POST /auth/login                 # Connexion
POST /auth/logout                # Déconnexion
POST /auth/refresh               # Refresh access token
POST /auth/verify-email          # Vérifier email
POST /auth/forgot-password       # Demande reset
POST /auth/reset-password        # Reset password

GET  /users/me                   # Profil actuel
PUT  /users/me                   # Modifier profil
DELETE /users/me                 # Supprimer compte
```

**Flux JWT** :
```
┌─────────┐     Login (email/password)      ┌──────────────┐
│ Client  │─────────────────────────────────▶│ User Service │
└─────────┘                                   └──────┬───────┘
     │                                                │
     │           BCrypt.verify()                     │
     │                  ✓                             │
     │                                                ▼
     │                                  ┌──────────────────────┐
     │                                  │  Generate JWT Tokens │
     │                                  │  - Access: 15 min    │
     │                                  │  - Refresh: 7 days   │
     │                                  └──────────┬───────────┘
     │                                             │
     │   { accessToken, refreshToken }            │
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

**Sécurité** :
- BCrypt (12 salt rounds)
- JWT RS256 (clés asymétriques)
- Refresh token rotation (nouveau token à chaque refresh)
- Blacklist Redis (tokens révoqués avant expiration)

**Apport au système** :
- Authentification robuste et sécurisée
- Expérience utilisateur fluide (auto-refresh transparent)
- Conformité GDPR (suppression compte possible)

---

### 5️⃣ Geo Service (`yowyob-geo-service`)

**Rôle** : Géolocalisation et recherches spatiales

**Responsabilités** :
- ✅ Géocodage (adresse → coordonnées)
- ✅ Géocodage inverse (coordonnées → adresse)
- ✅ Recherches spatiales (proximité, dans un rayon)
- ✅ Calcul de distances
- ✅ Intégration OpenStreetMap Nominatim
- ✅ Cache Redis (géocodage = 24h TTL)
- ✅ PostGIS pour requêtes spatiales complexes

**Port** : `8084`

**Endpoints principaux** :
```
GET /geo/geocode                 # Adresse → Coordonnées
GET /geo/reverse                 # Coordonnées → Adresse
GET /geo/nearby                  # Recherche proximité
GET /geo/distance                # Calcul distance
```

**Exemple géocodage** :
```
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

**PostGIS** :
- Stockage de géométries (POINT, POLYGON)
- Index spatial (GIST)
- Requêtes : ST_DWithin, ST_Distance, ST_Contains

**Apport au système** :
- Enrichissement géographique des résultats de recherche
- Performance (cache Redis, index PostGIS)
- Précision (OpenStreetMap + PostGIS)

---

### 6️⃣ Notification Service (`yowyob-notification-service`)

**Rôle** : Envoi de notifications multi-canal

**Responsabilités** :
- ✅ Email (SMTP : vérification, reset password, alertes)
- ✅ Web Push (VAPID pour PWA)
- ✅ Mobile Push (Firebase Cloud Messaging)
- ✅ Consommation événements Kafka (event-driven)
- ✅ Gestion des subscriptions push
- ✅ Templates d'emails (Thymeleaf)

**Port** : `8086`

**Endpoints principaux** :
```
POST /notifications/subscribe    # S'abonner web push
DELETE /notifications/subscribe  # Se désabonner
GET  /notifications/vapid-key    # Clé publique VAPID
PUT  /notifications/preferences  # Gérer préférences
```

**Kafka Consumer** :
```java
@KafkaListener(topics = "search-queries")
public void handleSearchEvent(SearchQueryEvent event) {
    // Détection trending topic
    if (isTrending(event.getQuery())) {
        sendTrendingNotification(event);
    }
}

@KafkaListener(topics = "user-events")
public void handleUserEvent(UserEvent event) {
    switch (event.getType()) {
        case USER_REGISTERED -> sendWelcomeEmail(event);
        case PASSWORD_RESET_REQUESTED -> sendResetEmail(event);
    }
}
```

**Web Push (VAPID)** :
```
┌─────────┐   Subscribe    ┌──────────────────┐
│   PWA   │───────────────▶│ Notification Svc │
└─────────┘                 └────────┬─────────┘
     │                               │
     │  Push Subscription            │ Store in DB
     │  {endpoint, keys}             ▼
     │                      ┌──────────────────┐
     │                      │   PostgreSQL     │
     │                      └──────────────────┘
     │
     │  Event: Trending topic
     ▼
┌────────────────────────────────────────────┐
│  Kafka: topic=search-queries               │
└────────────────────────────────────────────┘
     │
     ▼  Consumer
┌──────────────────┐
│ Notification Svc │──────▶ Push Service (FCM/Mozilla)
└──────────────────┘              │
                                   ▼
                            ┌─────────────┐
                            │   Browser   │
                            │  (shows 🔔) │
                            └─────────────┘
```

**Apport au système** :
- Engagement utilisateur (notifications temps réel)
- Event-driven (découplage via Kafka)
- Multi-canal (email + web + mobile)

---

## ⚙️ Prérequis

### Développement local

- **JDK 21** (OpenJDK ou Oracle)
```bash
  java -version  # doit afficher 21.x
```

- **Maven 3.9+**
```bash
  mvn -version
```

- **Docker** & **Docker Compose** (pour bases de données locales)
```bash
  docker --version
  docker-compose --version
```

- **Git**
```bash
  git --version
```

### Services externes (fournis par Infrastructure repo)

- PostgreSQL 15 + PostGIS
- Elasticsearch 8.x
- Redis 7
- Apache Kafka 3.5

**Option 1** : Docker Compose (fourni dans repo Infrastructure)
**Option 2** : Services cloud (AWS RDS, Elastic Cloud, etc.)

---

## 🚀 Installation

### 1. Cloner le repository
```bash
git clone https://github.com/votre-org/YowYob-Search-Backend.git
cd YowYob-Search-Backend
```

### 2. Installer les dépendances
```bash
mvn clean install -DskipTests
```

Ceci compile tous les modules et installe `yowyob-common` dans le repository Maven local.

### 3. Générer les clés JWT et VAPID
```bash
./scripts/generate-keys.sh
```

Ceci génère :
- **JWT Keys** (RS256 : private.pem, public.pem) → `yowyob-user-service/src/main/resources/keys/`
- **VAPID Keys** (pour Web Push) → `yowyob-notification-service/src/main/resources/keys/`

### 4. Lancer les services d'infrastructure (Docker Compose)

*Note : Ces services sont dans le repo `YowYob-Search-Infrastructure`*
```bash
cd ../YowYob-Search-Infrastructure
docker-compose up -d postgres elasticsearch redis kafka
```

Attendez que tous les services soient healthy :
```bash
docker-compose ps
```

### 5. Initialiser les bases de données
```bash
# PostgreSQL schemas
docker exec -i yowyob-postgres psql -U postgres < ../YowYob-Search-Backend/scripts/init-db.sql

# Elasticsearch indexes
cd ../YowYob-Search-Backend
---

## 🔧 Configuration

Chaque microservice a son `application.yml` dans `src/main/resources/`.

### Variables d'environnement

Créer un fichier `.env` à la racine (ignoré par Git) :
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
JWT_EXPIRATION_MS=900000  # 15 min

# OpenStreetMap Nominatim
NOMINATIM_BASE_URL=https://nominatim.openstreetmap.org

# SMTP
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password

# Web Push (VAPID)
VAPID_PUBLIC_KEY=<generated_key>
VAPID_PRIVATE_KEY=<generated_key>
VAPID_SUBJECT=mailto:admin@yowyob.com

# Firebase (optionnel)
FIREBASE_CREDENTIALS_PATH=classpath:firebase-credentials.json
```

### Configuration Spring Profiles

**Profils disponibles** :
- `dev` : Développement local
- `test` : Tests automatisés
- `staging` : Pré-production
- `prod` : Production

Activer un profil :
```bash
export SPRING_PROFILES_ACTIVE=dev
# ou
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Fichiers de configuration** :
application.yml          # Configuration commune
application-dev.yml      # Overrides pour dev
application-prod.yml     # Overrides pour prod

---

## 🎮 Lancement local

### Option 1 : Lancer tous les services (Maven)
```bash
# Terminal 1 - API Gateway
cd yowyob-api-gateway
mvn spring-boot:run

# Terminal 2 - Search Service
cd yowyob-search-service
mvn spring-boot:run

# Terminal 3 - User Service
cd yowyob-user-service
mvn spring-boot:run

# Terminal 4 - Geo Service
cd yowyob-geo-service
mvn spring-boot:run

# Terminal 5 - Crawler Service
cd yowyob-crawler-service
mvn spring-boot:run

# Terminal 6 - Notification Service
cd yowyob-notification-service
mvn spring-boot:run
```

### Option 2 : Script de démarrage
```bash
./scripts/start-all-services.sh
```

### Option 3 : Docker Compose (recommandé)
```bash
# Build des images
mvn clean package -DskipTests
docker-compose up --build
```

### Vérification santé des services
```bash
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8082/actuator/health  # Search Service
curl http://localhost:8083/actuator/health  # User Service
curl http://localhost:8084/actuator/health  # Geo Service
curl http://localhost:8085/actuator/health  # Crawler Service
curl http://localhost:8086/actuator/health  # Notification Service
```

Tous doivent répondre :
```json
{"status":"UP"}
```

---

## 🧪 Tests

### Tests unitaires
```bash
mvn test
```

### Tests d'intégration (avec TestContainers)
```bash
mvn verify
```

Ceci lance des conteneurs Docker automatiquement pour PostgreSQL, Elasticsearch, Redis et Kafka.

### Tests par module
```bash
cd yowyob-search-service
mvn test
```

### Coverage
```bash
mvn jacoco:report
```

Rapport dans : `target/site/jacoco/index.html`

### Tests de performance (optionnel)
```bash
cd tests/performance
./run-load-test.sh
```

Utilise **JMeter** ou **Gatling** pour simuler charge.

---

## 📦 Build & Déploiement

### Build production
```bash
mvn clean package -Pprod -DskipTests
```

Génère des JARs dans `target/` de chaque module.

### Build images Docker
```bash
# Build toutes les images
docker-compose build

# Ou individuellement
cd yowyob-search-service
docker build -t yowyob/search-service:latest -f ../docker/search-service.Dockerfile .
```

### Push vers registry
```bash
docker login
docker tag yowyob/search-service:latest registry.yowyob.com/search-service:1.0.0
docker push registry.yowyob.com/search-service:1.0.0
```

### Déploiement Kubernetes

*Voir repository `YowYob-Search-Infrastructure` pour les manifests Kubernetes*
```bash
cd ../YowYob-Search-Infrastructure
kubectl apply -f k8s/backend/
```

---

## 📚 API Documentation

### Swagger UI (Dev uniquement)

Accessible sur chaque service :
http://localhost:8082/swagger-ui.html  # Search Service
http://localhost:8083/swagger-ui.html  # User Service
http://localhost:8084/swagger-ui.html  # Geo Service
http://localhost:8085/swagger-ui.html  # Crawler Service
http://localhost:8086/swagger-ui.html  # Notification Service

### Documentation complète

Voir `/docs/API.md` pour :
- Tous les endpoints
- Exemples de requêtes/réponses
- Codes d'erreur
- Rate limiting
- Authentification

### Collection Postman

Importer : `/docs/postman/YowYob-Backend.postman_collection.json`

---

## 📐 Conventions de code

### Style Java

Basé sur **Google Java Style Guide** avec adaptations :
```java
// Classe
public class SearchService {
    
    // Constants: UPPER_SNAKE_CASE
    private static final int MAX_RESULTS = 100;
    
    // Fields: camelCase
    private final ElasticsearchClient elasticsearchClient;
    
    // Methods: camelCase
    public SearchResponse executeSearch(SearchRequest request) {
        // ...
    }
}
```

### Nommage

- **Classes** : `PascalCase` (ex: `SearchService`)
- **Interfaces** : `I` préfixe (ex: `ISearchService`) *(optionnel)*
- **Methods** : `camelCase` (ex: `executeSearch`)
- **Constants** : `UPPER_SNAKE_CASE` (ex: `MAX_RESULTS`)
- **Packages** : `lowercase` (ex: `com.yowyob.search`)

### Structure des packages

com.yowyob.{service}/
├── controller/        # REST endpoints
├── service/           # Business logic
├── repository/        # Data access
├── model/
│   ├── entity/        # JPA entities
│   ├── dto/           # Data Transfer Objects
│   └── enums/         # Enumerations
├── config/            # Spring configuration
├── exception/         # Custom exceptions
└── util/              # Utilities

### DTOs vs Entities

**Jamais** exposer les entités JPA dans les APIs !
```java
// ❌ Mauvais
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    return userRepository.findById(id);
}

// ✅ Bon
@GetMapping("/users/{id}")
public UserDto getUser(@PathVariable Long id) {
    User user = userRepository.findById(id);
    return userMapper.toDto(user);
}
```

### Gestion des erreurs

Utiliser `GlobalExceptionHandler` dans `yowyob-common` :
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

### Logging
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchService {
    private static final Logger log = LoggerFactory.getLogger(SearchService.class);
    
    public SearchResponse executeSearch(SearchRequest request) {
        log.info("Executing search: query={}, user={}", request.getQuery(), request.getUserId());
        
        try {
            // ...
        } catch (Exception e) {
            log.error("Search failed: query={}", request.getQuery(), e);
            throw new SearchException("Search execution failed", e);
        }
    }
}
```

**Niveaux de log** :
- `ERROR` : Erreurs nécessitant intervention
- `WARN` : Situations anormales mais gérées
- `INFO` : Événements importants (startup, shutdown, business events)
- `DEBUG` : Détails pour debugging (désactivé en prod)
- `TRACE` : Détails très fins (désactivé en prod)

---

## 📊 Monitoring

### Actuator Endpoints
GET /actuator/health         # Santé globale
GET /actuator/info           # Infos application
GET /actuator/metrics        # Métriques Micrometer
GET /actuator/prometheus     # Format Prometheus
GET /actuator/loggers        # Configuration logs

### Métriques Prometheus

Exposées sur `/actuator/prometheus` :

##JVM
jvm_memory_used_bytes
jvm_threads_live_threads
jvm_gc_pause_seconds

##HTTP
http_server_requests_seconds_count
http_server_requests_seconds_sum

##Custom
search_queries_total
crawler_pages_indexed_total

### Grafana Dashboards

*Fournis dans le repository Infrastructure*

- **JVM Dashboard** : Heap, GC, Threads
- **API Dashboard** : Latence P50/P95/P99, Error rate, Throughput
- **Search Dashboard** : Queries/min, Cache hit rate, Response time
- **Crawler Dashboard** : Pages/min, Errors, Queue size

### Distributed Tracing (OpenTelemetry)

Trace IDs propagés via headers HTTP : X-Trace-Id: 5f9c8a3b-1e2d-4c7f-9a6e-8b5d3f2e1c0a

Visualisation dans **Jaeger** ou **Tempo**.

---

## 🛣 Roadmap

### ✅ Phase 1 (MVP) - Complété

- [x] Architecture microservices
- [x] API Gateway
- [x] Search avec Elasticsearch
- [x] Crawler JSoup
- [x] Auth JWT
- [x] Géolocalisation PostGIS
- [x] Notifications email + web push
- [x] Tests automatisés
- [x] Docker + Kubernetes

### 🚧 Phase 2 (En cours)

- [ ] Machine Learning pour ranking personnalisé
- [ ] Vector Search avec embeddings (Spring AI)
- [ ] Rate limiting avancé (per-user quotas)
- [ ] Analytics dashboard temps réel
- [ ] API publique pour webmasters (API keys)

### 🔮 Phase 3 (Futur)

- [ ] Support multi-langues (50+ langues)
- [ ] Recherche d'images (computer vision)
- [ ] Recherche vocale
- [ ] Knowledge Graph
- [ ] Blockchain pour réputation webmasters
- [ ] Edge computing (CDN avec compute)

---

## 🤝 Extension Commerce

### 💼 Adaptation pour e-commerce

Le backend YowYob peut être étendu pour des cas d'usage e-commerce :

#### Nouveau microservice : `yowyob-commerce-service`

**Responsabilités** :
- Catalogue produits (indexation Elasticsearch)
- Recherche produits (filtres prix, catégories, avis)
- Panier & Checkout
- Commandes & Paiements (Stripe, PayPal)
- Inventory management
- Avis clients & Ratings

**Architecture supplémentaire** :

yowyob-commerce-service/
├── controller/
│   ├── ProductController
│   ├── CartController
│   ├── OrderController
│   └── PaymentController
├── service/
│   ├── ProductSearchService  # Utilise Search Service
│   ├── CartService
│   ├── OrderService
│   └── PaymentService
├── repository/
│   ├── ProductRepository
│   ├── OrderRepository
│   └── CartRepository
└── integration/
├── StripeClient
├── PayPalClient
└── ShippingProviders

**Modifications Search Service** :
- Nouveau type de document : `PRODUCT`
- Filtres : prix, catégories, marques, notes
- Facettes : prix ranges, disponibilité, livraison
- Boost produits sponsorisés (ads)

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

**Intégration paiement** :
```java
@RestController
@RequestMapping("/api/commerce/payments")
public class PaymentController {
    
    @PostMapping("/checkout")
    public PaymentResponse checkout(@RequestBody CheckoutRequest request) {
        // 1. Vérifier stock
        // 2. Calculer total + shipping
        // 3. Créer payment intent (Stripe)
        // 4. Retourner client_secret pour frontend
    }
    
    @PostMapping("/webhook/stripe")
    public void handleStripeWebhook(@RequestBody String payload, 
                                     @RequestHeader("Stripe-Signature") String signature) {
        // Vérifier signature
        // Traiter événement (payment_succeeded, etc.)
        // Mettre à jour commande
        // Publier événement Kafka: order-completed
    }
}
```

**Événements Kafka supplémentaires** :
- `product-views` : Tracking vues produits
- `cart-actions` : Ajout/retrait panier
- `order-events` : Commandes (created, paid, shipped, delivered)
- `payment-events` : Paiements

**Notifications** :
- Confirmation commande (email)
- Suivi livraison (SMS + push)
- Recommandations personnalisées (ML-based)

---

## 🐛 Troubleshooting

### Problème : Services ne démarrent pas

**Solution** :
```bash
# Vérifier que les ports ne sont pas occupés
lsof -i :8080  # API Gateway
lsof -i :8082  # Search Service

# Tuer les processus si nécessaire
kill -9 <PID>
```

### Problème : Connexion base de données échoue

**Solution** :
```bash
# Vérifier que PostgreSQL est lancé
docker-compose ps postgres

# Vérifier les logs
docker-compose logs postgres

# Tester connexion
psql -h localhost -U yowyob -d yowyob_db
```

### Problème : Elasticsearch inaccessible

**Solution** :
```bash
# Vérifier status
curl http://localhost:9200/_cluster/health

# Augmenter heap si nécessaire (docker-compose.yml)
environment:
  - "ES_JAVA_OPTS=-Xms2g -Xmx2g"
```

### Problème : Kafka ne consomme pas les messages

**Solution** :
```bash
# Lister les consumer groups
kafka-consumer-groups --bootstrap-server localhost:9092 --list

# Voir le lag
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group yowyob-notification-service --describe

# Reset offset si nécessaire
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group yowyob-notification-service --reset-offsets \
  --to-earliest --topic search-queries --execute
```

---

## 📞 Support & Contribution

### Questions

- **Issues GitHub** : https://github.com/BrianBrusly/YowYob-Search-Backend/issues
- **Discussions** : https://github.com/BrianBrusly/YowYob-Search-Backend/discussions
- **Email** : backend@yowyob.com

### Contribuer

1. Fork le repository
2. Créer une branche : `git checkout -b feature/amazing-feature`
3. Commit : `git commit -m 'feat: add amazing feature'`
4. Push : `git push origin feature/amazing-feature`
5. Ouvrir une Pull Request

**Conventions commit** : [Conventional Commits](https://www.conventionalcommits.org/)

feat: nouvelle fonctionnalité
fix: correction bug
docs: documentation
style: formatage code
refactor: refactoring
test: ajout/modification tests
chore: tâches diverses

### Code Review Checklist

- [ ] Tests unitaires passent
- [ ] Tests d'intégration passent
- [ ] Coverage > 80%
- [ ] Pas de code commenté
- [ ] Logs appropriés
- [ ] Documentation mise à jour
- [ ] Pas de secrets en dur

---

## 📄 License

MIT License - voir [LICENSE](LICENSE)

---

## 🙏 Remerciements

- **Spring Boot Team** pour l'excellent framework
- **Elasticsearch** pour le moteur de recherche
- **OpenStreetMap** pour les données cartographiques
- **Communauté Open Source**

---

**Développé par l'équipe YowYob**

*Pour toute question technique, consultez d'abord `/docs/` ou ouvrez une issue GitHub.*
