# Module Shop - YowYob Search Platform

> **Agrégateur et comparateur de produits e-commerce intelligent**  
> Microservice spécialisé dans l'agrégation multi-sources, la comparaison de prix, l'indexation produits et l'analyse de tendances marchandes avec support prioritaire du marché camerounais et africain

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)
[![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.x-yellow.svg)](https://www.elastic.co/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

---

## 📑 Table des matières

- [Vue d'ensemble et philosophie](#-vue-densemble-et-philosophie)
- [Architecture du module](#-architecture-du-module)
- [Structure détaillée du projet](#-structure-détaillée-du-projet)
- [Composants principaux](#-composants-principaux)
- [Intégration avec les autres modules](#-intégration-avec-les-autres-modules)
- [Configuration et déploiement](#-configuration-et-déploiement)
- [Tests et qualité](#-tests-et-qualité)
- [Monitoring et observabilité](#-monitoring-et-observabilité)
- [Guide de développement](#-guide-de-développement)
- [Sécurité et éthique](#-sécurité-et-éthique)

---

## 🎯 Vue d'ensemble et philosophie

### Qu'est-ce que le Shop Service ?

Le Shop Service est le module e-commerce de la plateforme YowYob Search. Contrairement à une marketplace traditionnelle qui héberge directement des transactions, ce service agit comme un **agrégateur intelligent et un comparateur de prix** qui scanne le web, collecte les données publiques des sites marchands, normalise ces informations, et présente des comparaisons objectives aux utilisateurs.

Imaginez que vous cherchez un smartphone Samsung Galaxy A54. Au lieu de visiter manuellement Jumia, Kaymu, Amazon, Cdiscount et d'autres sites pour comparer les prix, notre Shop Service effectue cette recherche pour vous en quelques millisecondes. Il collecte les offres disponibles, compare les prix en tenant compte des frais de livraison et taxes, vérifie la disponibilité en temps réel, et présente tout cela dans une interface unifiée. L'utilisateur peut ensuite cliquer sur l'offre qui lui convient et sera redirigé vers le site marchand pour finaliser son achat.

### Pourquoi un service dédié à l'agrégation e-commerce ?

Le commerce en ligne africain, et particulièrement camerounais, présente des défis uniques qui nécessitent une approche spécialisée. Les produits sont dispersés sur de nombreuses plateformes avec des formats de données hétérogènes, les prix changent fréquemment avec des promotions flash limitées dans le temps, les informations de livraison varient grandement selon la localisation, et l'asymétrie d'information rend difficile pour les consommateurs de trouver les meilleures offres.

Notre Shop Service résout ces problèmes en créant une couche d'intelligence qui unifie et enrichit les données e-commerce. Le service ne se contente pas de collecter des prix, il comprend les variations de produits comme les différentes capacités de stockage ou couleurs, détecte les doublons pour éviter de montrer le même produit plusieurs fois, calcule les coûts totaux incluant livraison et taxes, et identifie les meilleures offres en temps réel.

### Focus marché camerounais et africain

Le Shop Service a été conçu avec une expertise approfondie du marché local. Nous indexons prioritairement les plateformes camerounaises et africaines majeures comme Jumia Cameroun qui est le leader du e-commerce en Afrique avec une présence dans quatorze pays, Kaymu qui se concentre sur le marché C2C avec des vendeurs individuels, et Gloopro qui est spécialisé dans l'électronique et le high-tech. Les prix sont automatiquement convertis et affichés en Franc CFA (FCFA) pour les utilisateurs camerounais, et nous prenons en compte les spécificités locales comme les délais de livraison vers Yaoundé ou Douala, les frais de douane pour les imports, et la disponibilité des produits en stock local.

### Positionnement : Agrégateur et non Marketplace

Il est crucial de comprendre que le Shop Service n'est **pas une marketplace**. Nous ne vendons rien directement, nous ne gérons pas de paiements, nous ne stockons pas de produits, et nous ne gérons pas la logistique de livraison. Notre rôle s'arrête à la comparaison et à la redirection vers les sites marchands. Cette approche présente plusieurs avantages majeurs pour les utilisateurs qui peuvent comparer facilement sans créer de comptes multiples, pour les marchands qui reçoivent du trafic qualifié sans avoir à payer des commissions de marketplace, et pour nous qui évitons les complexités légales et logistiques du commerce direct.

---

## 🏗 Architecture du module

### Architecture technique en couches

Le Shop Service suit une architecture hexagonale qui isole complètement la logique métier des détails techniques d'implémentation. Cette séparation permet de changer facilement les technologies sous-jacentes sans affecter le cœur du système.

```
┌─────────────────────────────────────────────────────────────────────┐
│                         YOWYOB SHOP SERVICE                         │
│                    Architecture Hexagonale Complète                 │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                      COUCHE API (Ports Entrants)                    │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │               REST Controllers (Spring MVC)                  │  │
│  │  • ProductController      → Recherche et consultation        │  │
│  │  • CatalogController      → Gestion catalogues marchands     │  │
│  │  • ComparisonController   → Comparaison de prix              │  │
│  │  • MerchantController     → Gestion des marchands            │  │
│  │  • AnalyticsController    → Statistiques et métriques        │  │
│  └──────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────┬─────────────────────────────────┘
                                    │
┌───────────────────────────────────▼─────────────────────────────────┐
│                    COUCHE SERVICE (Logique Métier)                  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                   Services Principaux                        │  │
│  │                                                              │  │
│  │  ┌────────────────────────────────────────────────────┐     │  │
│  │  │  ProductAggregationService                         │     │  │
│  │  │  • Collecte multi-sources (Jumia, Amazon, etc.)    │     │  │
│  │  │  • Normalisation des données produits              │     │  │
│  │  │  • Détection et fusion des doublons                │     │  │
│  │  │  • Enrichissement avec métadonnées                 │     │  │
│  │  └────────────────────────────────────────────────────┘     │  │
│  │                                                              │  │
│  │  ┌────────────────────────────────────────────────────┐     │  │
│  │  │  PriceComparisonService                            │     │  │
│  │  │  • Comparaison multi-marchands                     │     │  │
│  │  │  • Calcul du coût total (produit + livraison)      │     │  │
│  │  │  • Conversion de devises (EUR → FCFA)              │     │  │
│  │  │  • Identification de la meilleure offre            │     │  │
│  │  └────────────────────────────────────────────────────┘     │  │
│  │                                                              │  │
│  │  ┌────────────────────────────────────────────────────┐     │  │
│  │  │  MerchantIntegrationService                        │     │  │
│  │  │  • Gestion des connexions marchands                │     │  │
│  │  │  • Parsing des flux de données (XML, JSON, CSV)    │     │  │
│  │  │  • Validation et nettoyage des données             │     │  │
│  │  │  • Gestion des erreurs et retry logic              │     │  │
│  │  └────────────────────────────────────────────────────┘     │  │
│  │                                                              │  │
│  │  ┌────────────────────────────────────────────────────┐     │  │
│  │  │  ClickTrackingService                              │     │  │
│  │  │  • Tracking des clics sur produits                 │     │  │
│  │  │  • Tracking des redirections marchands             │     │  │
│  │  │  • Calcul du taux de conversion                    │     │  │
│  │  │  • Publication événements analytics                │     │  │
│  │  └────────────────────────────────────────────────────┘     │  │
│  └──────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────┬─────────────────────────────────┘
                                    │
┌───────────────────────────────────▼─────────────────────────────────┐
│              COUCHE REPOSITORY (Ports Sortants)                     │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │           Adaptateurs de Persistance                         │  │
│  │                                                              │  │
│  │  • ProductRepository (Elasticsearch)                         │  │
│  │    → Index produits avec recherche full-text                 │  │
│  │                                                              │  │
│  │  • MerchantRepository (PostgreSQL)                           │  │
│  │    → Informations marchands et configurations                │  │
│  │                                                              │  │
│  │  • OfferRepository (PostgreSQL)                              │  │
│  │    → Historique des offres et prix                           │  │
│  │                                                              │  │
│  │  • ClickEventRepository (PostgreSQL)                         │  │
│  │    → Événements de clics et redirections                     │  │
│  │                                                              │  │
│  │  • CacheRepository (Redis)                                   │  │
│  │    → Cache des résultats de comparaison                      │  │
│  └──────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────┬─────────────────────────────────┘
                                    │
┌───────────────────────────────────▼─────────────────────────────────┐
│                  COUCHE INFRASTRUCTURE EXTERNE                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │              Clients et Adaptateurs Externes                 │  │
│  │                                                              │  │
│  │  ┌────────────────────┐  ┌────────────────────────────┐     │  │
│  │  │  ScrapingClient    │  │  APIClient (OpenFeign)     │     │  │
│  │  │  • JSoup           │  │  • REST calls              │     │  │
│  │  │  • Selenium        │  │  • GraphQL queries         │     │  │
│  │  │  • Apache Tika     │  │  • SOAP services           │     │  │
│  │  └────────────────────┘  └────────────────────────────┘     │  │
│  │                                                              │  │
│  │  ┌────────────────────┐  ┌────────────────────────────┐     │  │
│  │  │ KafkaProducer      │  │  ExchangeRateClient        │     │  │
│  │  │ • product-index    │  │  • Taux EUR/FCFA           │     │  │
│  │  │ • product-clicks   │  │  • API OpenExchangeRates   │     │  │
│  │  │ • redirects        │  │  • Cache local             │     │  │
│  │  └────────────────────┘  └────────────────────────────┘     │  │
│  └──────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                  SOURCES DE DONNÉES EXTERNES                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐       │
│  │  Jumia   │  │  Kaymu   │  │ Gloopro  │  │   Amazon     │       │
│  │   (CM)   │  │   (CM)   │  │   (CM)   │  │   (FR/US)    │       │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────┘       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐       │
│  │  Konga   │  │   eBay   │  │Cdiscount │  │ AliExpress   │       │
│  │   (NG)   │  │   (US)   │  │   (FR)   │  │    (CN)      │       │
│  └──────────┘  └──────────┘  └──────────┘  └──────────────┘       │
└─────────────────────────────────────────────────────────────────────┘

Flux de données typique :
1. Utilisateur cherche "iPhone 14 128Go" via Search Service
2. Search Service détecte qu'il s'agit d'un produit → appelle Shop Service
3. Shop Service :
   a. Vérifie le cache Redis (si hit → retour immédiat)
   b. Collecte les offres depuis les sources (APIs + Scraping)
   c. Normalise et déduplique les produits
   d. Compare les prix et calcule les coûts totaux
   e. Identifie la meilleure offre
   f. Cache le résultat
   g. Publie événement Kafka pour analytics
4. Retourne la comparaison au Search Service
5. Utilisateur clique sur une offre
6. Shop Service track le clic et redirige vers le marchand
```

### Principes architecturaux appliqués

Le Shop Service a été conçu en respectant scrupuleusement les principes SOLID et les best practices de l'architecture hexagonale.

**Single Responsibility Principle** : Chaque classe a une responsabilité unique et clairement délimitée. Par exemple, le ProductAggregationService s'occupe uniquement de l'agrégation des produits depuis différentes sources, sans se préoccuper de la comparaison de prix qui est gérée par le PriceComparisonService. De même, le ScrapingClient ne fait que le scraping HTML sans logique métier, tandis que le MerchantIntegrationService orchestre l'utilisation de ce client selon les règles métier.

**Open/Closed Principle** : L'architecture est ouverte à l'extension mais fermée à la modification. Ajouter un nouveau marchand ne nécessite pas de modifier le code existant, il suffit de créer un nouveau adaptateur qui implémente l'interface MerchantDataSource. De même, ajouter un nouveau type de scraping ou une nouvelle API ne change pas le cœur du système. Les interfaces définissent des contrats stables que les implémentations concrètes respectent.

**Liskov Substitution Principle** : Toutes les implémentations d'une interface peuvent être utilisées de manière interchangeable. Par exemple, n'importe quelle implémentation de MerchantDataSource peut être utilisée par le MerchantIntegrationService sans que celui-ci n'ait besoin de connaître les détails spécifiques de chaque marchand. Un JumiaScraper et un AmazonAPIClient peuvent tous deux être traités comme des sources de données génériques.

**Interface Segregation Principle** : Les interfaces sont petites et focalisées. Au lieu d'avoir une énorme interface MerchantOperations avec toutes les méthodes possibles, nous avons des interfaces séparées comme ProductDataSource pour la récupération de données, PriceDataSource pour les prix en temps réel, et InventoryDataSource pour la disponibilité. Les implémentations n'ont besoin d'implémenter que les interfaces pertinentes pour leur cas d'usage.

**Dependency Inversion Principle** : Les modules de haut niveau ne dépendent pas des modules de bas niveau mais des abstractions. Le ProductAggregationService ne dépend pas directement de JumiaScraper ou AmazonAPIClient, mais de l'abstraction MerchantDataSource. Cela permet de changer facilement l'implémentation concrète sans affecter la logique métier, et facilite grandement les tests en permettant le mocking des dépendances.

### Architecture de collecte de données

La collecte de données depuis les sources externes est l'un des aspects les plus complexes du Shop Service. Elle doit être robuste, éthique et performante.

```
Stratégie de Collecte Multi-Sources :

┌─────────────────────────────────────────────────────────────┐
│              ORCHESTRATEUR DE COLLECTE                      │
│  • Sélection des sources selon priorités                    │
│  • Parallélisation des collectes                            │
│  • Gestion des timeouts et circuit breakers                 │
│  • Agrégation et déduplication des résultats                │
└────────────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
        ▼                ▼                ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   API Call   │  │   Scraping   │  │  Feed Parse  │
│              │  │              │  │              │
│ • REST APIs  │  │ • JSoup      │  │ • XML feeds  │
│ • GraphQL    │  │ • Selenium   │  │ • JSON feeds │
│ • SOAP       │  │ • Puppeteer  │  │ • CSV files  │
└──────────────┘  └──────────────┘  └──────────────┘

Pour chaque source :
1. Vérification robots.txt et respect des délais
2. Rate limiting intelligent (max 1 req/10s par défaut)
3. User-Agent transparent (YowYobBot/1.0)
4. Gestion des erreurs et retry avec backoff
5. Logging complet pour audit et debugging
```

La collecte respecte strictement les règles éthiques et légales. Nous lisons et respectons le fichier robots.txt de chaque site, n'accédons jamais à des sections interdites, respectons les délais imposés (Crawl-delay), et n'effectuons jamais plus d'une requête toutes les dix secondes par site par défaut. Notre User-Agent est transparent et identifie clairement notre bot avec des informations de contact. Nous ne collectons que des données publiquement disponibles, jamais de données derrière authentification, et nous répondons rapidement aux demandes de retrait de sources.

---

## 📂 Structure détaillée du projet

### Arborescence complète du module

```
yowyob-shop-service/
│
├── 📄 pom.xml                                    # Configuration Maven
│   ├── Parent: yowyob-search-backend:1.0.0
│   ├── ArtifactId: yowyob-shop-service
│   ├── Dépendances principales:
│   │   ├── spring-boot-starter-web (API REST)
│   │   ├── spring-boot-starter-data-jpa (PostgreSQL)
│   │   ├── spring-boot-starter-data-elasticsearch (Index produits)
│   │   ├── spring-boot-starter-data-redis (Cache)
│   │   ├── spring-kafka (Événements)
│   │   ├── spring-cloud-starter-openfeign (APIs externes)
│   │   ├── jsoup:1.16.1 (HTML parsing)
│   │   ├── selenium-java:4.15.0 (Browser automation)
│   │   ├── apache-tika:2.9.0 (Content extraction)
│   │   ├── commons-csv:1.10.0 (CSV parsing)
│   │   └── yowyob-common:1.0.0 (Module partagé)
│   └── Plugins:
│       ├── spring-boot-maven-plugin
│       ├── maven-compiler-plugin (Java 21)
│       └── jacoco-maven-plugin (Coverage > 80%)
│
├── 📁 src/main/java/com/yowyob/shop/
│   │
│   ├── 📄 ShopServiceApplication.java            # Point d'entrée Spring Boot
│   │   ├── @SpringBootApplication
│   │   ├── @EnableJpaRepositories
│   │   ├── @EnableElasticsearchRepositories
│   │   ├── @EnableCaching
│   │   ├── @EnableFeignClients
│   │   └── main() : Démarre l'application
│   │
│   ├── 📁 config/                                # Configurations Spring
│   │   ├── 📄 DataSourceConfig.java              # Config PostgreSQL
│   │   │   ├── @Configuration
│   │   │   ├── dataSource() : HikariDataSource
│   │   │   ├── transactionManager() : PlatformTransactionManager
│   │   │   └── Connection pool: max 20, min 5
│   │   │
│   │   ├── 📄 ElasticsearchConfig.java           # Config ES client
│   │   │   ├── @Configuration
│   │   │   ├── elasticsearchClient() : ElasticsearchClient
│   │   │   ├── URL: ${elasticsearch.uris}
│   │   │   ├── Timeout: 30s
│   │   │   └── Retry policy: 3 tentatives
│   │   │
│   │   ├── 📄 RedisConfig.java                   # Config Redis
│   │   │   ├── @Configuration
│   │   │   ├── @EnableCaching
│   │   │   ├── redisTemplate() : RedisTemplate
│   │   │   ├── cacheManager() : RedisCacheManager
│   │   │   └── TTL: 5 minutes par défaut
│   │   │
│   │   ├── 📄 KafkaProducerConfig.java           # Config Kafka producer
│   │   │   ├── @Configuration
│   │   │   ├── producerFactory() : ProducerFactory
│   │   │   ├── kafkaTemplate() : KafkaTemplate
│   │   │   ├── Serializer: JsonSerializer
│   │   │   └── Acks: all, Retries: 3
│   │   │
│   │   ├── 📄 ScrapingConfig.java                # Config scraping
│   │   │   ├── @Configuration
│   │   │   ├── @ConfigurationProperties("scraping")
│   │   │   ├── userAgent: YowYobBot/1.0
│   │   │   ├── timeout: 10000ms
│   │   │   ├── maxRetries: 3
│   │   │   ├── politenessDelay: 10000ms
│   │   │   └── respect robots.txt: true
│   │   │
│   │   ├── 📄 FeignConfig.java                   # Config OpenFeign
│   │   │   ├── @Configuration
│   │   │   ├── Timeout: connect 5s, read 30s
│   │   │   ├── Retry: 2 tentatives
│   │   │   ├── Logger: FULL (en dev)
│   │   │   └── Circuit breaker enabled
│   │   │
│   │   ├── 📄 CurrencyConfig.java                # Config devises
│   │   │   ├── @Configuration
│   │   │   ├── @ConfigurationProperties("currency")
│   │   │   ├── defaultCurrency: FCFA
│   │   │   ├── exchangeRateProvider: OpenExchangeRates
│   │   │   ├── updateInterval: 1 heure
│   │   │   └── fallbackRates: EUR/FCFA = 655
│   │   │
│   │   ├── 📄 ComparisonConfig.java              # Config comparaison
│   │   │   ├── @Configuration
│   │   │   ├── @ConfigurationProperties("comparison")
│   │   │   ├── maxSources: 8
│   │   │   ├── timeout: 5000ms
│   │   │   ├── includedInTotal: [price, shipping, taxes]
│   │   │   └── rankingFactors: {price: 0.6, delivery: 0.25, merchant: 0.15}
│   │   │
│   │   └── 📄 AsyncConfig.java                   # Config async
│   │       ├── @Configuration
│   │       ├── @EnableAsync
│   │       ├── taskExecutor() : ThreadPoolTaskExecutor
│   │       ├── corePoolSize: 10
│   │       ├── maxPoolSize: 50
│   │       └── queueCapacity: 100
│   │
│   ├── 📁 controller/                            # Contrôleurs REST
│   │   ├── 📄 ProductController.java             # API produits
│   │   │   ├── @RestController
│   │   │   ├── @RequestMapping("/api/products")
│   │   │   ├── GET /search : Recherche produits
│   │   │   │   └── Params: q, category, priceMin, priceMax, page, size
│   │   │   ├── GET /{id} : Détails produit
│   │   │   ├── GET /{id}/offers : Offres pour un produit
│   │   │   └── POST /{id}/click : Track clic produit
│   │   │
│   │   ├── 📄 ComparisonController.java          # API comparaison
│   │   │   ├── @RestController
│   │   │   ├── @RequestMapping("/api/comparison")
│   │   │   ├── POST /compare : Comparer produits
│   │   │   │   └── Body: ProductComparisonRequest
│   │   │   ├── GET /best-offers : Meilleures offres
│   │   │   │   └── Params: category, location, limit
│   │   │   └── GET /price-history/{productId} : Historique prix
│   │   │
│   │   ├── 📄 MerchantController.java            # API marchands
│   │   │   ├── @RestController
│   │   │   ├── @RequestMapping("/api/merchants")
│   │   │   ├── GET / : Liste marchands actifs
│   │   │   ├── GET /{id} : Détails marchand
│   │   │   ├── GET /{id}/products : Produits d'un marchand
│   │   │   └── GET /{id}/stats : Stats marchand
│   │   │
│   │   ├── 📄 CatalogController.java             # API catalogues
│   │   │   ├── @RestController
│   │   │   ├── @RequestMapping("/api/catalog")
│   │   │   ├── POST /ingest : Ingestion catalogue
│   │   │   │   └── @PreAuthorize("hasRole('MERCHANT')")
│   │   │   ├── PUT /{id} : Mise à jour catalogue
│   │   │   ├── DELETE /{id} : Suppression catalogue
│   │   │   └── GET /status/{id} : Statut ingestion
│   │   │
│   │   └── 📄 AnalyticsController.java           # API analytics
│   │       ├── @RestController
│   │       ├── @RequestMapping("/api/analytics")
│   │       ├── GET /trending : Produits tendance
│   │       ├── GET /popular : Produits populaires
│   │       ├── GET /merchant/{id}/performance : Performance marchand
│   │       │   └── @PreAuthorize("hasRole('MERCHANT') and #id == principal.id")
│   │       └── GET /conversion-rates : Taux de conversion
│   │
│   ├── 📁 service/                               # Services métier
│   │   ├── 📁 core/                              # Services principaux
│   │   │   ├── 📄 ProductAggregationService.java # Agrégation produits
│   │   │   │   ├── @Service
│   │   │   │   ├── aggregateProducts(query) : List<Product>
│   │   │   │   │   └── Collecte depuis toutes les sources actives
│   │   │   │   ├── normalizeProduct(rawProduct) : Product
│   │   │   │   │   └── Normalisation champs, unités, format
│   │   │   │   ├── detectDuplicates(products) : List<ProductGroup>
│   │   │   │   │   └── Matching par EAN, titre, specs
│   │   │   │   ├── enrichProduct(product) : Product
│   │   │   │   │   └── Ajout images, reviews, metadata
│   │   │   │   └── Parallélisation : CompletableFuture pour chaque source
│   │   │   │
│   │   │   ├── 📄 PriceComparisonService.java    # Comparaison prix
│   │   │   │   ├── @Service
│   │   │   │   ├── compareOffers(productId) : PriceComparison
│   │   │   │   │   └── Compare toutes les offres d'un produit
│   │   │   │   ├── calculateTJe vais poursuivre le README du module Shop en conservant le même niveau de détail, la même profondeur d'explication et le même style pédagogique que le module Search. Je reprends exactement là où le document s'est arrêté.

---

otalCost(offer) : BigDecimal
│   │   │   │   │   └── Calcule prix + frais livraison + taxes
│   │   │   │   ├── identifyBestOffer(offers) : Offer
│   │   │   │   │   └── Sélectionne la meilleure offre selon critères
│   │   │   │   ├── compareOffersByMerchant(productId) : Map<Merchant, Offer>
│   │   │   │   └── calculatePriceHistory(productId, period) : PriceHistory
│   │   │   │
│   │   │   ├── 📄 MerchantIntegrationService.java # Intégration marchands
│   │   │   │   ├── @Service
│   │   │   │   ├── fetchMerchantCatalog(merchantId) : List<Product>
│   │   │   │   │   └── Récupère catalogue complet d'un marchand
│   │   │   │   ├── parseFeed(feedUrl, format) : List<RawProduct>
│   │   │   │   │   └── Parse flux XML/JSON/CSV selon format
│   │   │   │   ├── validateProduct(rawProduct) : ValidationResult
│   │   │   │   │   └── Vérifie conformité des données produit
│   │   │   │   ├── normalizeProduct(rawProduct) : Product
│   │   │   │   │   └── Normalise format, unités, catégories
│   │   │   │   ├── detectDuplicates(products) : Map<String, List<Product>>
│   │   │   │   │   └── Identifie doublons par EAN, titre, specs
│   │   │   │   └── mergeProductVariants(products) : List<Product>
│   │   │   │       └── Regroupe variantes d'un même produit
│   │   │   │
│   │   │   ├── 📄 ScrapingService.java              # Service scraping web
│   │   │   │   ├── @Service
│   │   │   │   ├── scrapeProductPage(url) : ScrapedProduct
│   │   │   │   │   └── Extrait données d'une page produit
│   │   │   │   ├── respectRobotsTxt(domain) : boolean
│   │   │   │   │   └── Vérifie et respecte robots.txt
│   │   │   │   ├── applyPoliteness(domain) : void
│   │   │   │   │   └── Applique délai entre requêtes
│   │   │   │   ├── extractStructuredData(html) : Map<String, Object>
│   │   │   │   │   └── Extrait Schema.org, Open Graph, etc.
│   │   │   │   └── handleCaptcha(response) : ScrapingResult
│   │   │   │       └── Détecte et gère les CAPTCHAs
│   │   │   │
│   │   │   ├── 📄 ProductIndexingService.java       # Indexation ES
│   │   │   │   ├── @Service
│   │   │   │   ├── indexProduct(product) : IndexResponse
│   │   │   │   │   └── Indexe produit dans Elasticsearch
│   │   │   │   ├── bulkIndexProducts(products) : BulkResponse
│   │   │   │   │   └── Indexation bulk pour performance
│   │   │   │   ├── updateProductIndex(productId, updates) : void
│   │   │   │   │   └── Mise à jour partielle d'un produit
│   │   │   │   ├── deleteProductIndex(productId) : void
│   │   │   │   └── reindexAllProducts() : ReindexResult
│   │   │   │       └── Réindexation complète (maintenance)
│   │   │   │
│   │   │   ├── 📄 ClickTrackingService.java         # Tracking clics
│   │   │   │   ├── @Service
│   │   │   │   ├── trackProductClick(productId, userId) : void
│   │   │   │   │   └── Enregistre clic sur produit
│   │   │   │   ├── trackMerchantRedirect(offerId, userId) : String
│   │   │   │   │   └── Track redirect + génère URL tracking
│   │   │   │   ├── recordConversion(clickId, orderAmount) : void
│   │   │   │   │   └── Enregistre conversion (si disponible)
│   │   │   │   └── calculateClickThroughRate(productId) : double
│   │   │   │       └── Calcule CTR pour analytics
│   │   │   │
│   │   │   └── 📄 CurrencyConversionService.java    # Conversion devises
│   │   │       ├── @Service
│   │   │       ├── convert(amount, from, to) : BigDecimal
│   │   │       │   └── Convertit montant entre devises
│   │   │       ├── getExchangeRate(from, to) : BigDecimal
│   │   │       │   └── Récupère taux de change actuel
│   │   │       ├── updateExchangeRates() : void
│   │   │       │   └── Maj taux depuis API externe
│   │   │       └── convertToUserCurrency(amount, userId) : BigDecimal
│   │   │           └── Convertit selon préférence utilisateur
│   │   │
│   │   ├── 📁 client/                               # Clients externes
│   │   │   ├── 📄 JumiaClient.java                  # API Jumia
│   │   │   │   ├── @FeignClient("jumia-api")
│   │   │   │   ├── getProducts(category, page) : ProductResponse
│   │   │   │   ├── getProductDetails(productId) : ProductDetail
│   │   │   │   └── checkAvailability(productId) : AvailabilityStatus
│   │   │   │
│   │   │   ├── 📄 AmazonClient.java                 # API Amazon Product Advertising
│   │   │   │   ├── @FeignClient("amazon-api")
│   │   │   │   ├── searchProducts(keywords) : AmazonSearchResponse
│   │   │   │   ├── getItemDetails(asin) : AmazonItemDetail
│   │   │   │   └── getBrowseNodeInfo(nodeId) : BrowseNode
│   │   │   │
│   │   │   ├── 📄 ExchangeRateClient.java           # API taux de change
│   │   │   │   ├── @FeignClient("exchange-rate-api")
│   │   │   │   ├── getLatestRates(base) : ExchangeRateResponse
│   │   │   │   └── getHistoricalRates(date, base) : ExchangeRateResponse
│   │   │   │
│   │   │   └── 📄 ScrapingClient.java               # Client scraping générique
│   │   │       ├── @Component
│   │   │       ├── fetchPage(url) : Document
│   │   │       ├── fetchPageWithJS(url) : String
│   │   │       └── extractElements(doc, selector) : Elements
│   │   │
│   │   ├── 📁 scraper/                              # Scrapers spécifiques
│   │   │   ├── 📁 impl/                             # Implémentations par site
│   │   │   │   ├── 📄 JumiaScraper.java             # Scraper Jumia
│   │   │   │   │   ├── @Component
│   │   │   │   │   ├── implements MerchantScraper
│   │   │   │   │   ├── scrapeProductList(url) : List<Product>
│   │   │   │   │   ├── scrapeProductDetail(url) : Product
│   │   │   │   │   └── extractPrice(element) : BigDecimal
│   │   │   │   │
│   │   │   │   ├── 📄 KaymuScraper.java             # Scraper Kaymu
│   │   │   │   ├── 📄 GlooproScraper.java           # Scraper Gloopro
│   │   │   │   ├── 📄 CdiscountScraper.java         # Scraper Cdiscount
│   │   │   │   └── 📄 AliExpressScraper.java        # Scraper AliExpress
│   │   │   │
│   │   │   ├── 📄 MerchantScraper.java              # Interface scraper
│   │   │   │   ├── interface MerchantScraper
│   │   │   │   ├── scrapeProductList(url) : List<Product>
│   │   │   │   ├── scrapeProductDetail(url) : Product
│   │   │   │   └── getSupportedDomains() : List<String>
│   │   │   │
│   │   │   └── 📄 ScraperFactory.java               # Factory scrapers
│   │   │       ├── @Component
│   │   │       ├── getScraper(domain) : MerchantScraper
│   │   │       └── getAllScrapers() : List<MerchantScraper>
│   │   │
│   │   ├── 📁 repository/                           # Repositories données
│   │   │   ├── 📄 ProductRepository.java            # Repository JPA produits
│   │   │   │   ├── extends JpaRepository<Product, String>
│   │   │   │   ├── findByCategory(category) : List<Product>
│   │   │   │   ├── findByMerchantId(merchantId) : List<Product>
│   │   │   │   ├── findByEan(ean) : Optional<Product>
│   │   │   │   └── @Query custom pour recherches complexes
│   │   │   │
│   │   │   ├── 📄 ProductElasticsearchRepository.java # Repository ES
│   │   │   │   ├── extends ElasticsearchRepository<ProductDocument, String>
│   │   │   │   ├── findByTitleContaining(title) : List<ProductDocument>
│   │   │   │   ├── findByPriceBetween(min, max) : List<ProductDocument>
│   │   │   │   └── Custom queries avec @Query annotation
│   │   │   │
│   │   │   ├── 📄 OfferRepository.java              # Repository offres
│   │   │   │   ├── extends JpaRepository<Offer, String>
│   │   │   │   ├── findByProductId(productId) : List<Offer>
│   │   │   │   ├── findByMerchantId(merchantId) : List<Offer>
│   │   │   │   ├── findActivOffers(productId) : List<Offer>
│   │   │   │   └── findBestOffer(productId) : Optional<Offer>
│   │   │   │
│   │   │   ├── 📄 MerchantRepository.java           # Repository marchands
│   │   │   │   ├── extends JpaRepository<Merchant, String>
│   │   │   │   ├── findByCountry(country) : List<Merchant>
│   │   │   │   ├── findActiveM erchants() : List<Merchant>
│   │   │   │   └── findByIntegrationType(type) : List<Merchant>
│   │   │   │
│   │   │   ├── 📄 ClickEventRepository.java         # Repository événements
│   │   │   │   ├── extends JpaRepository<ClickEvent, String>
│   │   │   │   ├── findByProductId(productId) : List<ClickEvent>
│   │   │   │   ├── findByUserId(userId) : List<ClickEvent>
│   │   │   │   ├── countByProductId(productId) : long
│   │   │   │   └── calculateCTR(productId, period) : double
│   │   │   │
│   │   │   └── 📄 PriceHistoryRepository.java       # Repository historique prix
│   │   │       ├── extends JpaRepository<PriceHistory, String>
│   │   │       ├── findByProductId(productId) : List<PriceHistory>
│   │   │       ├── findByProductIdAndDateRange() : List<PriceHistory>
│   │   │       └── findLowestPrice(productId) : Optional<PriceHistory>
│   │   │
│   │   ├── 📁 model/                                # Modèles de données
│   │   │   ├── 📁 entity/                           # Entités JPA
│   │   │   │   ├── 📄 Product.java                  # Entité produit
│   │   │   │   │   ├── @Entity
│   │   │   │   │   ├── @Table(name = "products")
│   │   │   │   │   ├── id : String (UUID)
│   │   │   │   │   ├── ean : String                 # Code-barres international
│   │   │   │   │   ├── title : String
│   │   │   │   │   ├── description : String (CLOB)
│   │   │   │   │   ├── category : String
│   │   │   │   │   ├── brand : String
│   │   │   │   │   ├── images : List<String> (JSON)
│   │   │   │   │   ├── specifications : Map<String, String> (JSON)
│   │   │   │   │   ├── averageRating : Double
│   │   │   │   │   ├── reviewCount : Integer
│   │   │   │   │   ├── @OneToMany offers : List<Offer>
│   │   │   │   │   ├── createdAt : LocalDateTime
│   │   │   │   │   ├── updatedAt : LocalDateTime
│   │   │   │   │   └── Méthodes business : getBestOffer(), etc.
│   │   │   │   │
│   │   │   │   ├── 📄 Offer.java                    # Entité offre
│   │   │   │   │   ├── @Entity
│   │   │   │   │   ├── @Table(name = "offers")
│   │   │   │   │   ├── id : String (UUID)
│   │   │   │   │   ├── @ManyToOne product : Product
│   │   │   │   │   ├── @ManyToOne merchant : Merchant
│   │   │   │   │   ├── price : BigDecimal
│   │   │   │   │   ├── currency : String
│   │   │   │   │   ├── priceInFCFA : BigDecimal     # Prix converti
│   │   │   │   │   ├── shippingCost : BigDecimal
│   │   │   │   │   ├── taxes : BigDecimal
│   │   │   │   │   ├── totalCost : BigDecimal       # Calculé
│   │   │   │   │   ├── availability : AvailabilityStatus
│   │   │   │   │   ├── stock : Integer
│   │   │   │   │   ├── url : String                 # URL marchand
│   │   │   │   │   ├── validFrom : LocalDateTime
│   │   │   │   │   ├── validTo : LocalDateTime
│   │   │   │   │   ├── isActive : boolean
│   │   │   │   │   ├── lastChecked : LocalDateTime
│   │   │   │   │   └── @PrePersist calculateTotalCost()
│   │   │   │   │
│   │   │   │   ├── 📄 Merchant.java                 # Entité marchand
│   │   │   │   │   ├── @Entity
│   │   │   │   │   ├── @Table(name = "merchants")
│   │   │   │   │   ├── id : String (UUID)
│   │   │   │   │   ├── name : String
│   │   │   │   │   ├── domain : String              # jumia.cm, etc.
│   │   │   │   │   ├── country : String
│   │   │   │   │   ├── logo : String
│   │   │   │   │   ├── description : String
│   │   │   │   │   ├── integrationType : IntegrationType # API, FEED, SCRAPING
│   │   │   │   │   ├── apiConfig : Map<String, String> (JSON)
│   │   │   │   │   ├── feedUrl : String
│   │   │   │   │   ├── scrapingConfig : ScrapingConfig (JSON)
│   │   │   │   │   ├── isActive : boolean
│   │   │   │   │   ├── priority : Integer           # Pour ranking
│   │   │   │   │   ├── trustScore : Double          # 0-1
│   │   │   │   │   ├── averageDeliveryTime : Integer # jours
│   │   │   │   │   ├── returnPolicy : String
│   │   │   │   │   ├── @OneToMany offers : List<Offer>
│   │   │   │   │   └── contactInfo : ContactInfo (Embedded)
│   │   │   │   │
│   │   │   │   ├── 📄 ClickEvent.java               # Entité événement clic
│   │   │   │   │   ├── @Entity
│   │   │   │   │   ├── @Table(name = "click_events")
│   │   │   │   │   ├── id : String (UUID)
│   │   │   │   │   ├── @ManyToOne product : Product
│   │   │   │   │   ├── @ManyToOne offer : Offer
│   │   │   │   │   ├── userId : String              # Nullable
│   │   │   │   │   ├── sessionId : String
│   │   │   │   │   ├── clickedAt : LocalDateTime
│   │   │   │   │   ├── position : Integer           # Position dans résultats
│   │   │   │   │   ├── query : String               # Requête origine
│   │   │   │   │   ├── redirectUrl : String
│   │   │   │   │   ├── converted : Boolean          # Si achat confirmé
│   │   │   │   │   ├── conversionAmount : BigDecimal
│   │   │   │   │   └── metadata : Map<String, String> (JSON)
│   │   │   │   │
│   │   │   │   ├── 📄 PriceHistory.java             # Historique prix
│   │   │   │   │   ├── @Entity
│   │   │   │   │   ├── @Table(name = "price_history")
│   │   │   │   │   ├── id : String (UUID)
│   │   │   │   │   ├── @ManyToOne product : Product
│   │   │   │   │   ├── @ManyToOne merchant : Merchant
│   │   │   │   │   ├── price : BigDecimal
│   │   │   │   │   ├── priceInFCFA : BigDecimal
│   │   │   │   │   ├── recordedAt : LocalDateTime
│   │   │   │   │   └── @Index sur product_id + recorded_at
│   │   │   │   │
│   │   │   │   └── 📄 CatalogIngestion.java         # Ingestion catalogue
│   │   │   │       ├── @Entity
│   │   │   │       ├── @Table(name = "catalog_ingestions")
│   │   │   │       ├── id : String (UUID)
│   │   │   │       ├── @ManyToOne merchant : Merchant
│   │   │   │       ├── status : IngestionStatus     # PENDING, RUNNING, SUCCESS, FAILED
│   │   │   │       ├── startedAt : LocalDateTime
│   │   │   │       ├── completedAt : LocalDateTime
│   │   │   │       ├── totalProducts : Integer
│   │   │   │       ├── successCount : Integer
│   │   │   │       ├── failureCount : Integer
│   │   │   │       ├── errors : List<String> (JSON)
│   │   │   │       └── metadata : Map<String, Object> (JSON)
│   │   │   │
│   │   │   ├── 📁 document/                         # Documents Elasticsearch
│   │   │   │   └── 📄 ProductDocument.java          # Document ES produit
│   │   │   │       ├── @Document(indexName = "yowyob-products")
│   │   │   │       ├── id : String
│   │   │   │       ├── @Field(type = Text) title : String
│   │   │   │       ├── @Field(type = Text) description : String
│   │   │   │       ├── @Field(type = Keyword) category : String
│   │   │   │       ├── @Field(type = Keyword) brand : String
│   │   │   │       ├── @Field(type = Keyword) ean : String
│   │   │   │       ├── @Field(type = Double) minPrice : Double
│   │   │   │       ├── @Field(type = Double) maxPrice : Double
│   │   │   │       ├── @Field(type = Integer) offerCount : Integer
│   │   │   │       ├── @Field(type = Double) averageRating : Double
│   │   │   │       ├── @Field(type = Integer) reviewCount : Integer
│   │   │   │       ├── @Field(type = Keyword) images : List<String>
│   │   │   │       ├── @Field(type = Nested) offers : List<OfferSummary>
│   │   │   │       ├── @Field(type = Object) specifications : Map<String, String>
│   │   │   │       ├── @Field(type = Date) indexedAt : LocalDateTime
│   │   │   │       └── @Field(type = Date) updatedAt : LocalDateTime
│   │   │   │
│   │   │   ├── 📁 dto/                              # Data Transfer Objects
│   │   │   │   ├── 📄 ProductSearchRequest.java     # Requête recherche produits
│   │   │   │   │   ├── query : String
│   │   │   │   │   ├── category : String
│   │   │   │   │   ├── brand : String
│   │   │   │   │   ├── priceMin : BigDecimal
│   │   │   │   │   ├── priceMax : BigDecimal
│   │   │   │   │   ├── rating : Double              # Note minimale
│   │   │   │   │   ├── availability : AvailabilityStatus
│   │   │   │   │   ├── merchants : List<String>     # Filtrer par marchands
│   │   │   │   │   ├── sortBy : SortField           # PRICE, RATING, RELEVANCE
│   │   │   │   │   ├── sortOrder : SortOrder        # ASC, DESC
│   │   │   │   │   ├── page : int
│   │   │   │   │   ├── size : int
│   │   │   │   │   └── Validation annotations
│   │   │   │   │
│   │   │   │   ├── 📄 ProductResponse.java          # Réponse produit
│   │   │   │   │   ├── id : String
│   │   │   │   │   ├── title : String
│   │   │   │   │   ├── description : String
│   │   │   │   │   ├── category : String
│   │   │   │   │   ├── brand : String
│   │   │   │   │   ├── ean : String
│   │   │   │   │   ├── images : List<String>
│   │   │   │   │   ├── specifications : Map<String, String>
│   │   │   │   │   ├── averageRating : Double
│   │   │   │   │   ├── reviewCount : Integer
│   │   │   │   │   ├── offers : List<OfferResponse>
│   │   │   │   │   ├── bestOffer : OfferResponse
│   │   │   │   │   ├── priceRange : PriceRange
│   │   │   │   │   └── availability : AvailabilityStatus
│   │   │   │   │
│   │   │   │   ├── 📄 OfferResponse.java            # Réponse offre
│   │   │   │   │   ├── id : String
│   │   │   │   │   ├── merchant : MerchantSummary
│   │   │   │   │   ├── price : BigDecimal
│   │   │   │   │   ├── priceInFCFA : BigDecimal
│   │   │   │   │   ├── shippingCost : BigDecimal
│   │   │   │   │   ├── totalCost : BigDecimal
│   │   │   │   │   ├── availability : AvailabilityStatus
│   │   │   │   │   ├── stock : Integer
│   │   │   │   │   ├── deliveryTime : String
│   │   │   │   │   ├── isBestOffer : boolean
│   │   │   │   │   └── trackingUrl : String         # URL avec tracking
│   │   │   │   │
│   │   │   │   ├── 📄 PriceComparisonResponse.java  # Réponse comparaison
│   │   │   │   │   ├── product : ProductResponse
│   │   │   │   │   ├── offers : List<OfferResponse>
│   │   │   │   │   ├── bestOffer : OfferResponse
│   │   │   │   │   ├── priceDifference : BigDecimal # Écart min-max
│   │   │   │   │   ├── savingsPercentage : Double
│   │   │   │   │   ├── priceHistory : List<PricePoint>
│   │   │   │   │   └── recommendations : List<String>
│   │   │   │   │
│   │   │   │   ├── 📄 CatalogIngestRequest.java     # Requête ingestion
│   │   │   │   │   ├── merchantId : String
│   │   │   │   │   ├── source : IngestSource        # API, FEED, FILE
│   │   │   │   │   ├── feedUrl : String
│   │   │   │   │   ├── format : FeedFormat          # XML, JSON, CSV
│   │   │   │   │   ├── file : MultipartFile
│   │   │   │   │   ├── mapping : Map<String, String> # Mapping champs
│   │   │   │   │   └── options : IngestOptions
│   │   │   │   │
│   │   │   │   ├── 📄 IngestStatusResponse.java     # Statut ingestion
│   │   │   │   │   ├── ingestionId : String
│   │   │   │   │   ├── status : IngestionStatus
│   │   │   │   │   ├── progress : Double            # 0-100%
│   │   │   │   │   ├── totalProducts : Integer
│   │   │   │   │   ├── processedProducts : Integer
│   │   │   │   │   ├── successCount : Integer
│   │   │   │   │   ├── failureCount : Integer
│   │   │   │   │   ├── errors : List<String>
│   │   │   │   │   ├── startedAt : LocalDateTime
│   │   │   │   │   ├── estimatedCompletion : LocalDateTime
│   │   │   │   │   └── metadata : Map<String, Object>
│   │   │   │   │
│   │   │   │   └── 📄 ClickTrackingRequest.java     # Requête tracking
│   │   │   │       ├── productId : String
│   │   │   │       ├── offerId : String
│   │   │   │       ├── userId : String
│   │   │   │       ├── sessionId : String
│   │   │   │       ├── position : Integer
│   │   │   │       ├── query : String
│   │   │   │       └── metadata : Map<String, String>
│   │   │   │
│   │   │   ├── 📁 event/                            # Événements Kafka
│   │   │   │   ├── 📄 ProductIndexedEvent.java      # Événement indexation
│   │   │   │   │   ├── productId : String
│   │   │   │   │   ├── merchantId : String
│   │   │   │   │   ├── action : IndexAction         # CREATE, UPDATE, DELETE
│   │   │   │   │   ├── timestamp : Instant
│   │   │   │   │   └── metadata : Map<String, Object>
│   │   │   │   │
│   │   │   │   ├── 📄 ProductClickEvent.java        # Événement clic
│   │   │   │   │   ├── productId : String
│   │   │   │   │   ├── offerId : String
│   │   │   │   │   ├── merchantId : String
│   │   │   │   │   ├── userId : String
│   │   │   │   │   ├── sessionId : String
│   │   │   │   │   ├── clickedAt : Instant
│   │   │   │   │   ├── position : Integer
│   │   │   │   │   ├── query : String
│   │   │   │   │   └── metadata : Map<String, Object>
│   │   │   │   │
│   │   │   │   └── 📄 MerchantRedirectEvent.java    # Événement redirect
│   │   │   │       ├── offerId : String
│   │   ││       ├── merchantId : String
│   │   │   │       ├── userId : String
│   │   │   │       ├── redirectedAt : Instant
│   │   │   │       ├── targetUrl : String
│   │   │   │       └── metadata : Map<String, Object>
│   │   │   │
│   │   │   └── 📁 enums/                            # Énumérations
│   │   │       ├── 📄 AvailabilityStatus.java       # IN_STOCK, OUT_OF_STOCK, PRE_ORDER
│   │   │       ├── 📄 IntegrationType.java          # API, FEED, SCRAPING, MANUAL
│   │   │       ├── 📄 IngestionStatus.java          # PENDING, RUNNING, SUCCESS, FAILED
│   │   │       ├── 📄 SortField.java                # PRICE, RATING, RELEVANCE, DATE
│   │   │       └── 📄 FeedFormat.java               # XML, JSON, CSV
│   │   │
│   │   ├── 📁 mapper/                               # Mappers MapStruct
│   │   │   ├── 📄 ProductMapper.java                # Mapper produit
│   │   │   │   ├── @Mapper(componentModel = "spring")
│   │   │   │   ├── toResponse(Product) : ProductResponse
│   │   │   │   ├── toDocument(Product) : ProductDocument
│   │   │   │   ├── toEntity(ProductRequest) : Product
│   │   │   │   └── updateEntity(ProductRequest, @MappingTarget Product)
│   │   │   │
│   │   │   ├── 📄 OfferMapper.java                  # Mapper offre
│   │   │   ├── 📄 MerchantMapper.java               # Mapper marchand
│   │   │   └── 📄 ClickEventMapper.java             # Mapper événement
│   │   │
│   │   ├── 📁 producer/                             # Producteurs Kafka
│   │   │   ├── 📄 ProductEventProducer.java         # Producer événements produit
│   │   │   │   ├── @Component
│   │   │   │   ├── publishProductIndexed(ProductIndexedEvent) : void
│   │   │   │   ├── publishProductUpdated(ProductIndexedEvent) : void
│   │   │   │   └── publishProductDeleted(ProductIndexedEvent) : void
│   │   │   │
│   │   │   ├── 📄 ClickEventProducer.java           # Producer événements clic
│   │   │   │   ├── @Component
│   │   │   │   ├── publishProductClick(ProductClickEvent) : void
│   │   │   │   └── publishMerchantRedirect(MerchantRedirectEvent) : void
│   │   │   │
│   │   │   └── 📄 KafkaProducerConfig.java          # Configuration producteur
│   │   │       ├── @Configuration
│   │   │       ├── producerFactory() : ProducerFactory
│   │   │       ├── kafkaTemplate() : KafkaTemplate
│   │   │       └── errorHandler() : ProducerErrorHandler
│   │   │
│   │   ├── 📁 scheduler/                            # Tâches planifiées
│   │   │   ├── 📄 CatalogUpdateScheduler.java       # Maj catalogues
│   │   │   │   ├── @Component
│   │   │   │   ├── @Scheduled(cron = "0 0 2 * * *") # 2h du matin
│   │   │   │   └── updateAllCatalogs() : void
│   │   │   │
│   │   │   ├── 📄 PriceUpdateScheduler.java         # Maj prix
│   │   │   │   ├── @Component
│   │   │   │   ├── @Scheduled(fixedRate = 3600000)  # Toutes les heures
│   │   │   │   └── updatePrices() : void
│   │   │   │
│   │   │   ├── 📄 ExchangeRateScheduler.java        # Maj taux de change
│   │   │   │   ├── @Component
│   │   │   │   ├── @Scheduled(cron = "0 0 * * * *") # Toutes les heures
│   │   │   │   └── updateExchangeRates() : void
│   │   │   │
│   │   │   └── 📄 OfferCleanupScheduler.java        # Nettoyage offres expirées
│   │   │       ├── @Component
│   │   │       ├── @Scheduled(cron = "0 0 3 * * *") # 3h du matin
│   │   │       └── cleanupExpiredOffers() : void
│   │   │
│   │   ├── 📁 validator/                            # Validateurs
│   │   │   ├── 📄 ProductValidator.java             # Validation produit
│   │   │   │   ├── @Component
│   │   │   │   ├── validate(Product) : ValidationResult
│   │   │   │   ├── validatePrice(BigDecimal) : boolean
│   │   │   │   ├── validateEAN(String) : boolean
│   │   │   │   └── validateCategory(String) : boolean
│   │   │   │
│   │   │   ├── 📄 FeedValidator.java                # Validation flux
│   │   │   │   ├── @Component
│   │   │   │   ├── validateFeed(Feed) : ValidationResult
│   │   │   │   └── validateFormat(String, FeedFormat) : boolean
│   │   │   │
│   │   │   └── 📄 MerchantValidator.java            # Validation marchand
│   │   │       ├── @Component
│   │   │       └── validateMerchant(Merchant) : ValidationResult
│   │   │
│   │   ├── 📁 normalizer/                           # Normaliseurs données
│   │   │   ├── 📄 ProductNormalizer.java            # Normalisation produit
│   │   │   │   ├── @Component
│   │   │   │   ├── normalize(RawProduct) : Product
│   │   │   │   ├── normalizeTitle(String) : String
│   │   │   │   ├── normalizeCategory(String) : String
│   │   │   │   ├── normalizePrice(String, String) : BigDecimal
│   │   │   │   └── extractSpecifications(Map) : Map<String, String>
│   │   │   │
│   │   │   ├── 📄 PriceNormalizer.java              # Normalisation prix
│   │   │   │   ├── @Component
│   │   │   │   ├── normalize(String, String) : BigDecimal
│   │   │   │   ├── extractNumericValue(String) : BigDecimal
│   │   │   │   └── detectCurrency(String) : String
│   │   │   │
│   │   │   └── 📄 CategoryNormalizer.java           # Normalisation catégorie
│   │   │       ├── @Component
│   │   │       ├── normalize(String) : String
│   │   │       └── mapToStandardCategory(String) : String
│   │   │
│   │   ├── 📁 deduplication/                        # Dédoublonnage
│   │   │   ├── 📄 ProductDeduplicator.java          # Détection doublons
│   │   │   │   ├── @Component
│   │   │   │   ├── findDuplicates(List<Product>) : Map<String, List<Product>>
│   │   │   │   ├── matchByEAN(Product, Product) : boolean
│   │   │   │   ├── matchByTitle(Product, Product) : double # Similarité
│   │   │   │   ├── matchBySpecs(Product, Product) : double
│   │   │   │   └── mergeProducts(List<Product>) : Product
│   │   │   │
│   │   │   └── 📄 FuzzyMatcher.java                 # Matching flou
│   │   │       ├── @Component
│   │   │       ├── calculateSimilarity(String, String) : double
│   │   │       ├── levenshteinDistance(String, String) : int
│   │   │       └── tokenBasedSimilarity(String, String) : double
│   │   │
│   │   ├── 📁 exception/                            # Exceptions métier
│   │   │   ├── 📄 ShopException.java                # Exception base
│   │   │   ├── 📄 ProductNotFoundException.java     # Produit non trouvé
│   │   │   ├── 📄 MerchantNotFoundException.java    # Marchand non trouvé
│   │   │   ├── 📄 IngestionException.java           # Erreur ingestion
│   │   │   ├── 📄 ScrapingException.java            # Erreur scraping
│   │   │   ├── 📄 ValidationException.java          # Erreur validation
│   │   │   └── 📄 PriceConversionException.java     # Erreur conversion
│   │   │
│   │   └── 📁 util/                                 # Utilitaires
│   │       ├── 📄 EANValidator.java                 # Validation code-barres
│   │       ├── 📄 PriceFormatter.java               # Formatage prix
│   │       ├── 📄 UrlBuilder.java                   # Construction URLs tracking
│   │       └── 📄 CategoryMapper.java               # Mapping catégories
│   │
│   ├── 📁 src/main/resources/
│   │   ├── 📄 application.yml                       # Configuration principale
│   │   │   ├── Configuration serveur (port 8087)
│   │   │   ├── Configuration PostgreSQL
│   │   │   ├── Configuration Elasticsearch
│   │   │   ├── Configuration Redis
│   │   │   ├── Configuration Kafka
│   │   │   ├── Configuration scraping
│   │   │   ├── Configuration conversion devises
│   │   │   └── Configuration comparaison prix
│   │   │
│   │   ├── 📄 application-dev.yml                   # Config développement
│   │   ├── 📄 application-prod.yml                  # Config production
│   │   │
│   │   ├── 📁 elasticsearch/                        # Config Elasticsearch
│   │   │   ├── 📄 mappings/
│   │   │   │   └── 📄 product-mapping.json          # Mapping produits
│   │   │   │       ├── Properties : title (text), description (text)
│   │   │   │       ├── category (keyword), brand (keyword)
│   │   │   │       ├── minPrice (double), maxPrice (double)
│   │   │   │       ├── averageRating (double)
│   │   │   │       ├── offers (nested)
│   │   │   │       └── Analyzers : french, standard
│   │   │   │
│   │   │   └── 📄 settings/
│   │   │       └── 📄 product-settings.json         # Settings index
│   │   │           ├── number_of_shards : 3
│   │   │           ├── number_of_replicas : 1
│   │   │           ├── refresh_interval : 1s
│   │   │           └── Analysis configuration
│   │   │
│   │   ├── 📁 scraping/                             # Config scraping
│   │   │   ├── 📄 user-agents.txt                   # User agents
│   │   │   ├── 📄 selectors/                        # Sélecteurs CSS
│   │   │   │   ├── 📄 jumia-selectors.json
│   │   │   │   ├── 📄 kaymu-selectors.json
│   │   │   │   └── 📄 gloopro-selectors.json
│   │   │   │
│   │   │   └── 📄 robots-txt-cache/                 # Cache robots.txt
│   │   │
│   │   ├── 📁 categories/                           # Mapping catégories
│   │   │   ├── 📄 category-mapping.json             # Mapping standard
│   │   │   └── 📄 category-hierarchy.json           # Hiérarchie catégories
│   │   │
│   │   ├── 📁 merchants/                            # Config marchands
│   │   │   ├── 📄 jumia-config.json                 # Config Jumia
│   │   │   ├── 📄 kaymu-config.json                 # Config Kaymu
│   │   │   └── 📄 default-merchant-config.json      # Config par défaut
│   │   │
│   │   └── 📁 db/                                   # Scripts DB
│   │       ├── 📄 schema.sql                        # Schéma tables
│   │       ├── 📄 data.sql                          # Données initiales
│   │       └── 📄 migration/                        # Migrations Liquibase
│   │           ├── 📄 changelog-master.xml
│   │           ├── 📄 v1.0-initial-schema.xml
│   │           └── 📄 v1.1-add-price-history.xml
│   │
│   └── 📁 src/test/java/com/yowyob/shop/
│       ├── 📁 controller/                           # Tests contrôleurs
│       │   ├── 📄 ProductControllerTest.java
│       │   ├── 📄 ComparisonControllerTest.java
│       │   └── 📄 CatalogControllerTest.java
│       │
│       ├── 📁 service/                              # Tests services
│       │   ├── 📄 ProductAggregationServiceTest.java
│       │   ├── 📄 PriceComparisonServiceTest.java
│       │   ├── 📄 ScrapingServiceTest.java
│       │   └── 📄 ProductIndexingServiceTest.java
│       │
│       ├── 📁 integration/                          # Tests intégration
│       │   ├── 📄 ShopIntegrationTest.java          # Test flux complet
│       │   ├── 📄 ElasticsearchIntegrationTest.java # Test ES
│       │   └── 📄 KafkaIntegrationTest.java         # Test Kafka
│       │
│       └── 📁 fixtures/                             # Données test
│           ├── 📄 ProductFixtures.java
│           ├── 📄 MerchantFixtures.java
│           └── 📄 OfferFixtures.java

---

## 🔧 Composants principaux détaillés

### ProductController - API REST principale

Le contrôleur ProductController expose l'API REST publique du Shop Service. Il gère toutes les requêtes liées aux produits et offres, en orchestrant les appels aux services métier appropriés.

```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    
    private final ProductAggregationService aggregationService;
    private final PriceComparisonService comparisonService;
    private final ClickTrackingService trackingService;
    private final ProductMapper productMapper;
    
    /**
     * Recherche de produits avec filtres avancés
     * 
     * Cette méthode est le point d'entrée principal pour les recherches produits.
     * Elle supporte une variété de filtres pour permettre aux utilisateurs de
     * trouver exactement ce qu'ils cherchent.
     * 
     * Exemple d'utilisation :
     * GET /api/products/search?query=iphone 14&priceMin=300000&priceMax=500000&category=electronics&sortBy=PRICE&sortOrder=ASC
     */
    @GetMapping("/search")
    @LogExecutionTime
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(
            @RequestParam @NotBlank String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) AvailabilityStatus availability,
            @RequestParam(required = false) List<String> merchants,
            @RequestParam(defaultValue = "RELEVANCE") SortField sortBy,
            @RequestParam(defaultValue = "DESC") SortOrder sortOrder,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @CurrentUser User user) {
        
        log.info("Received product search request: query={}, category={}, priceRange={}-{}", 
            query, category, priceMin, priceMax);
        
        // Construction de la requête de recherche
        ProductSearchRequest searchRequest = ProductSearchRequest.builder()
            .query(query)
            .category(category)
            .brand(brand)
            .priceMin(priceMin)
            .priceMax(priceMax)
            .rating(rating)
            .availability(availability)
            .merchants(merchants)
            .sortBy(sortBy)
            .sortOrder(sortOrder)
            .page(page)
            .size(size)
            .userId(user != null ? user.getId() : null)
            .build();
        
        // Exécution de la recherche
        PageResponse<ProductResponse> products = 
            aggregationService.searchProducts(searchRequest);
        
        log.info("Search completed: {} products found", products.getTotalElements());
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    /**
     * Récupération des détails d'un produit spécifique
     * 
     * Retourne toutes les informations disponibles sur un produit, y compris
     * toutes les offres de tous les marchands, les spécifications complètes,
     * les images, et l'historique de prix.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable String productId) {
        
        log.debug("Fetching product details: {}", productId);
        
        ProductResponse product = aggregationService.getProductById(productId);
        
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    /**
     * Récupération des offres pour un produit
     * 
     * Cette méthode retourne toutes les offres disponibles pour un produit donné,
     * triées par prix total (incluant livraison et taxes).
     */
    @GetMapping("/{productId}/offers")
    public ResponseEntity<ApiResponse<List<OfferResponse>>> getProductOffers(
            @PathVariable String productId) {
        
        log.debug("Fetching offers for product: {}", productId);
        
        List<OfferResponse> offers = 
            comparisonService.getProductOffers(productId);
        
        return ResponseEntity.ok(ApiResponse.success(offers));
    }
    
    /**
     * Tracking d'un clic sur un produit
     * 
     * Cette méthode est appelée lorsqu'un utilisateur clique sur un produit
     * dans les résultats de recherche. Elle enregistre l'événement pour
     * l'analytique et retourne l'URL de tracking pour la redirection.
     */
    @PostMapping("/{productId}/click")
    public ResponseEntity<ApiResponse<ClickTrackingResponse>> trackProductClick(
            @PathVariable String productId,
            @Valid @RequestBody ClickTrackingRequest request,
            @CurrentUser User user) {
        
        log.debug("Tracking click on product: {} by user: {}", 
            productId, user != null ? user.getId() : "anonymous");
        
        // Enrichissement de la requête avec l'ID utilisateur
        request.setUserId(user != null ? user.getId() : null);
        request.setProductId(productId);
        
        // Enregistrement du clic et génération de l'URL de tracking
        String trackingUrl = trackingService.trackProductClick(request);
        
        ClickTrackingResponse response = ClickTrackingResponse.builder()
            .trackingUrl(trackingUrl)
            .productId(productId)
            .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Produits similaires
     * 
     * Trouve des produits similaires à celui spécifié, basé sur la catégorie,
     * la marque, les spécifications et les patterns d'achat des utilisateurs.
     */
    @GetMapping("/{productId}/similar")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getSimilarProducts(
            @PathVariable String productId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        
        log.debug("Fetching similar products for: {}", productId);
        
        List<ProductResponse> similarProducts = 
            aggregationService.findSimilarProducts(productId, limit);
        
        return ResponseEntity.ok(ApiResponse.success(similarProducts));
    }
    
    /**
     * Historique des prix
     * 
     * Retourne l'historique des prix d'un produit sur une période donnée,
     * permettant aux utilisateurs de voir l'évolution des prix et d'identifier
     * les meilleures périodes pour acheter.
     */
    @GetMapping("/{productId}/price-history")
    public ResponseEntity<ApiResponse<PriceHistoryResponse>> getPriceHistory(
            @PathVariable String productId,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate to) {
        
        log.debug("Fetching price history for product: {} from {} to {}", 
            productId, from, to);
        
        // Dates par défaut : 30 derniers jours si non spécifiées
        if (from == null) {
            from = LocalDate.now().minusDays(30);
        }
        if (to == null) {
            to = LocalDate.now();
        }
        
        PriceHistoryResponse history = 
            comparisonService.getPriceHistory(productId, from, to);
        
        return ResponseEntity.ok(ApiResponse.success(history));
    }
    
    /**
     * Catégories de produits
     * 
     * Retourne l'arbre hiérarchique complet des catégories de produits
     * avec le nombre de produits dans chaque catégorie.
     */
    @GetMapping("/categories")
    @Cacheable(value = "product-categories", key = "'all'")
    public ResponseEntity<ApiResponse<List<CategoryNode>>> getCategories() {
        
        log.debug("Fetching product categories");
        
        List<CategoryNode> categories = aggregationService.getCategoryTree();
        
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    /**
     * Marques populaires
     * 
     * Retourne les marques les plus représentées dans le catalogue,
     * optionnellement filtrées par catégorie.
     */
    @GetMapping("/brands")
    @Cacheable(value = "product-brands", key = "#category ?: 'all'")
    public ResponseEntity<ApiResponse<List<BrandInfo>>> getPopularBrands(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "50") @Min(1) @Max(200) int limit) {
        
        log.debug("Fetching popular brands for category: {}", category);
        
        List<BrandInfo> brands = aggregationService.getPopularBrands(category, limit);
        
        return ResponseEntity.ok(ApiResponse.success(brands));
    }
}
```

### ComparisonController - Comparaison de prix

Le contrôleur ComparisonController expose l'API dédiée à la comparaison de prix entre marchands. Il permet aux utilisateurs d'obtenir des insights détaillés sur les différentes offres disponibles.

```java
@RestController
@RequestMapping("/api/comparison")
@RequiredArgsConstructor
@Slf4j
public class ComparisonController {
    
    private final PriceComparisonService comparisonService;
    
    /**
     * Comparaison de prix pour un produit
     * 
     * Cette méthode retourne une vue détaillée de toutes les offres disponibles
     * pour un produit, avec des informations sur les économies potentielles,
     * les différences de prix, et des recommandations.
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PriceComparisonResponse>> compareProduct(
            @PathVariable String productId,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "false") boolean includeOutOfStock) {
        
        log.info("Comparing prices for product: {} in location: {}", 
            productId, location);
        
        PriceComparisonResponse comparison = 
            comparisonService.compareProduct(productId, location, includeOutOfStock);
        
        return ResponseEntity.ok(ApiResponse.success(comparison));
    }
    
    /**
     * Meilleures offres du moment
     * 
     * Retourne les meilleures offres actuelles basées sur différents critères :
     * plus grande réduction de prix, meilleurs deals, nouveautés en promo, etc.
     */
    @GetMapping("/best-offers")
    @Cacheable(value = "best-offers", key = "#category + '-' + #limit")
    public ResponseEntity<ApiResponse<List<BestOfferResponse>>> getBestOffers(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(defaultValue = "DISCOUNT") OfferType offerType) {
        
        log.info("Fetching best offers: category={}, type={}, limit={}", 
            category, offerType, limit);
        
        List<BestOfferResponse> bestOffers = 
            comparisonService.getBestOffers(category, offerType, limit);
        
        return ResponseEntity.ok(ApiResponse.success(bestOffers));
    }
    
    /**
     * Comparaison multi-produits
     * 
     * Permet de comparer plusieurs produits côte à côte, utile pour comparer
     * différents modèles ou marques avant de prendre une décision d'achat.
     */
    @PostMapping("/compare-multiple")
    public ResponseEntity<ApiResponse<MultiProductComparisonResponse>> compareMultipleProducts(
            @Valid @RequestBody MultiProductComparisonRequest request) {
        
        log.info("Comparing {} products", request.getProductIds().size());
        
        MultiProductComparisonResponse comparison = 
            comparisonService.compareMultipleProducts(request);
        
        return ResponseEntity.ok(ApiResponse.success(comparison));
    }
    
    /**
     * Alertes de prix
     * 
     * Permet à un utilisateur de créer une alerte qui le notifiera lorsque
     * le prix d'un produit descend en dessous d'un certain seuil.
     */
    @PostMapping("/price-alert")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PriceAlertResponse>> createPriceAlert(
            @Valid @RequestBody PriceAlertRequest request,
            @CurrentUser User user) {
        
        log.info("Creating price alert for user: {} on product: {}", 
            user.getId(), request.getProductId());
        
        request.setUserId(user.getId());
        PriceAlertResponse alert = comparisonService.createPriceAlert(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(alert));
    }
    
    /**
     * Statistiques de prix
     * 
     * Fournit des statistiques agrégées sur les prix dans une catégorie :
     * prix moyen, médian, min, max, évolution temporelle.
     */
    @GetMapping("/price-stats")
    public ResponseEntity<ApiResponse<PriceStatisticsResponse>> getPriceStatistics(
            @RequestParam String category,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "30") @Min(1) @Max(365) int days) {
        
        log.info("Fetching price statistics: category={}, brand={}, days={}", 
            category, brand, days);
        
        PriceStatisticsResponse stats = 
            comparisonService.getPriceStatistics(category, brand, days);
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
```

### MerchantController - Gestion des marchands

Le contrôleur MerchantController gère les opérations liées aux marchands, incluant la consultation de leurs catalogues, statistiques, et configurations.

```java
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
@Slf4j
public class MerchantController {
    
    private final MerchantIntegrationService merchantService;
    
    /**
     * Liste des marchands actifs
     * 
     * Retourne tous les marchands actifs avec leurs informations de base.
     */
    @GetMapping
    @Cacheable(value = "merchants", key = "'active-' + #country")
    public ResponseEntity<ApiResponse<List<MerchantResponse>>> getActiveMerchants(
            @RequestParam(required = false) String country) {
        
        log.debug("Fetching active merchants for country: {}", country);
        
        List<MerchantResponse> merchants = 
            merchantService.getActiveMerchants(country);
        
        return ResponseEntity.ok(ApiResponse.success(merchants));
    }
    
    /**
     * Détails d'un marchand
     * 
     * Retourne les informations complètes sur un marchand, incluant ses
     * politiques de retour, délais de livraison, score de confiance, etc.
     */
    @GetMapping("/{merchantId}")
    public ResponseEntity<ApiResponse<MerchantDetailResponse>> getMerchant(
            @PathVariable String merchantId) {
        
        log.debug("Fetching merchant details: {}", merchantId);
        
        MerchantDetailResponse merchant = 
            merchantService.getMerchantDetails(merchantId);
        
        return ResponseEntity.ok(ApiResponse.success  ...  Je reprends depuis la section `MerchantController`.

---

```markdown
(merchant.getId() == principal.id")
      └── GET /conversion-rates : Taux de conversion
```

---

## 🔧 Composants principaux détaillés

### MerchantController - API Marchands (suite)

```java
                (merchant.getId() == principal.id")
      └── GET /conversion-rates : Taux de conversion
```

Le contrôleur MerchantController gère toutes les interactions avec les marchands partenaires. Il expose des endpoints pour la consultation des informations marchands, la gestion de leurs catalogues, et l'accès à leurs statistiques de performance. Ce contrôleur implémente également des mécanismes de sécurité stricts pour s'assurer que chaque marchand ne peut accéder qu'à ses propres données.

```java
@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
@Slf4j
public class MerchantController {
    
    private final MerchantIntegrationService merchantService;
    private final MerchantMapper merchantMapper;
    
    /**
     * Liste tous les marchands actifs
     * GET /api/merchants?country=CM
     */
    @GetMapping
    @Cacheable(value = "merchants", key = "'active-' + #country")
    public ResponseEntity<ApiResponse<List<MerchantResponse>>> getActiveMerchants(
            @RequestParam(required = false) String country) {
        
        log.debug("Fetching active merchants for country: {}", country);
        
        List<MerchantResponse> merchants = 
            merchantService.getActiveMerchants(country);
        
        return ResponseEntity.ok(ApiResponse.success(merchants));
    }
    
    /**
     * Récupère les détails complets d'un marchand
     * Inclut : informations générales, politiques, stats publiques
     */
    @GetMapping("/{merchantId}")
    public ResponseEntity<ApiResponse<MerchantDetailResponse>> getMerchant(
            @PathVariable String merchantId) {
        
        log.debug("Fetching merchant details: {}", merchantId);
        
        MerchantDetailResponse merchant = 
            merchantService.getMerchantDetails(merchantId);
        
        return ResponseEntity.ok(ApiResponse.success(merchant));
    }
    
    /**
     * Liste les produits d'un marchand avec pagination
     * Supporte filtres : catégorie, disponibilité, prix
     */
    @GetMapping("/{merchantId}/products")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> 
            getMerchantProducts(
            @PathVariable String merchantId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String availability,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        
        log.debug("Fetching products for merchant: {} with filters", merchantId);
        
        ProductSearchRequest searchRequest = ProductSearchRequest.builder()
            .merchantId(merchantId)
            .category(category)
            .availability(availability)
            .priceMin(priceMin)
            .priceMax(priceMax)
            .page(page)
            .size(size)
            .build();
        
        PageResponse<ProductResponse> products = 
            merchantService.getMerchantProducts(searchRequest);
        
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    /**
     * Statistiques publiques d'un marchand
     * - Nombre de produits actifs
     * - Note moyenne
     * - Délai de livraison moyen
     * - Taux de disponibilité
     */
    @GetMapping("/{merchantId}/stats")
    public ResponseEntity<ApiResponse<MerchantPublicStatsResponse>> 
            getMerchantPublicStats(@PathVariable String merchantId) {
        
        log.debug("Fetching public stats for merchant: {}", merchantId);
        
        MerchantPublicStatsResponse stats = 
            merchantService.getMerchantPublicStats(merchantId);
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
```

### CatalogController - API Gestion Catalogues

Le CatalogController permet aux marchands d'ingérer leurs catalogues produits dans la plateforme YowYob. Il supporte plusieurs formats de flux (XML, JSON, CSV) et différentes méthodes d'intégration (API REST, flux URL, upload fichier). Ce contrôleur est critique car il constitue le point d'entrée principal des données produits dans le système.

```java
@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@Slf4j
public class CatalogController {
    
    private final CatalogIngestService ingestService;
    private final ProductValidationService validationService;
    private final CatalogMapper catalogMapper;
    
    /**
     * Ingestion d'un catalogue marchand
     * Méthodes supportées :
     * - API : Données JSON directes dans le body
     * - FEED : URL vers flux XML/JSON
     * - FILE : Upload multipart d'un fichier CSV/JSON/XML
     * 
     * POST /api/catalog/ingest
     */
    @PostMapping("/ingest")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<CatalogIngestResponse>> ingestCatalog(
            @Valid @RequestBody CatalogIngestRequest request,
            @CurrentUser User user) {
        
        log.info("Starting catalog ingestion for merchant: {}", 
            request.getMerchantId());
        
        // Vérifier que le marchand ingère bien son propre catalogue
        if (!request.getMerchantId().equals(user.getMerchantId())) {
            throw new ForbiddenException(
                "Cannot ingest catalog for another merchant");
        }
        
        // Valider la configuration d'ingestion
        validationService.validateIngestConfig(request);
        
        // Démarrer l'ingestion asynchrone
        CatalogIngestResponse response = 
            ingestService.startIngestion(request);
        
        log.info("Catalog ingestion started with ID: {}", 
            response.getIngestionId());
        
        return ResponseEntity.accepted()
            .body(ApiResponse.success(response));
    }
    
    /**
     * Ingestion via upload de fichier
     * Formats supportés : CSV, JSON, XML
     * Taille max : 50MB
     */
    @PostMapping(value = "/ingest/file", 
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<CatalogIngestResponse>> ingestCatalogFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam String merchantId,
            @RequestParam FeedFormat format,
            @RequestParam(required = false) String mappingJson,
            @CurrentUser User user) {
        
        log.info("Starting file catalog ingestion for merchant: {}", merchantId);
        
        // Vérifications de sécurité
        if (!merchantId.equals(user.getMerchantId())) {
            throw new ForbiddenException("Cannot ingest for another merchant");
        }
        
        // Validation du fichier
        validationService.validateFile(file, format);
        
        // Parser le mapping si fourni
        Map<String, String> fieldMapping = null;
        if (mappingJson != null) {
            fieldMapping = catalogMapper.parseFieldMapping(mappingJson);
        }
        
        // Construire la requête d'ingestion
        CatalogIngestRequest request = CatalogIngestRequest.builder()
            .merchantId(merchantId)
            .source(IngestSource.FILE)
            .format(format)
            .file(file)
            .mapping(fieldMapping)
            .build();
        
        CatalogIngestResponse response = 
            ingestService.startIngestion(request);
        
        return ResponseEntity.accepted()
            .body(ApiResponse.success(response));
    }
    
    /**
     * Mise à jour d'un catalogue existant
     * Permet de modifier les produits sans réingérer tout le catalogue
     */
    @PutMapping("/{catalogId}")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<CatalogUpdateResponse>> updateCatalog(
            @PathVariable String catalogId,
            @Valid @RequestBody CatalogUpdateRequest request,
            @CurrentUser User user) {
        
        log.info("Updating catalog: {} for merchant: {}", 
            catalogId, user.getMerchantId());
        
        // Vérifier que le catalogue appartient au marchand
        ingestService.verifyOwnership(catalogId, user.getMerchantId());
        
        CatalogUpdateResponse response = 
            ingestService.updateCatalog(catalogId, request);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Suppression d'un catalogue
     * Supprime tous les produits associés de l'index
     */
    @DeleteMapping("/{catalogId}")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<Void>> deleteCatalog(
            @PathVariable String catalogId,
            @CurrentUser User user) {
        
        log.info("Deleting catalog: {} for merchant: {}", 
            catalogId, user.getMerchantId());
        
        ingestService.verifyOwnership(catalogId, user.getMerchantId());
        ingestService.deleteCatalog(catalogId);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * Statut d'une ingestion en cours
     * Retourne la progression, succès, échecs
     */
    @GetMapping("/status/{ingestionId}")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<IngestStatusResponse>> getIngestionStatus(
            @PathVariable String ingestionId,
            @CurrentUser User user) {
        
        log.debug("Fetching ingestion status: {}", ingestionId);
        
        // Vérifier que l'ingestion appartient au marchand
        ingestService.verifyIngestionOwnership(ingestionId, user.getMerchantId());
        
        IngestStatusResponse status = 
            ingestService.getIngestionStatus(ingestionId);
        
        return ResponseEntity.ok(ApiResponse.success(status));
    }
    
    /**
     * Historique des ingestions d'un marchand
     * Avec filtres par statut et période
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<PageResponse<CatalogIngestionHistory>>> 
            getIngestionHistory(
            @CurrentUser User user,
            @RequestParam(required = false) IngestionStatus status,
            @RequestParam(required = false) 
                @DateTimeFormat(iso = ISO.DATE) LocalDate from,
            @RequestParam(required = false) 
                @DateTimeFormat(iso = ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("Fetching ingestion history for merchant: {}", 
            user.getMerchantId());
        
        PageResponse<CatalogIngestionHistory> history = 
            ingestService.getIngestionHistory(
                user.getMerchantId(), status, from, to, page, size);
        
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
```

### AnalyticsController - API Statistiques

Le contrôleur AnalyticsController expose les métriques et statistiques détaillées pour les marchands. Il fournit des insights sur les performances de leurs produits, les tendances de recherche, et le comportement des utilisateurs. Ces données sont essentielles pour que les marchands optimisent leurs catalogues et leur positionnement.

```java
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    private final MerchantAnalyticsService merchantAnalyticsService;
    
    /**
     * Produits tendance du moment
     * Basé sur le volume de recherches et de clics récents
     */
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<TrendingProductResponse>>> 
            getTrendingProducts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @RequestParam(defaultValue = "today") String period) {
        
        log.debug("Fetching trending products for category: {}, period: {}", 
            category, period);
        
        List<TrendingProductResponse> trending = 
            analyticsService.getTrendingProducts(category, limit, period);
        
        return ResponseEntity.ok(ApiResponse.success(trending));
    }
    
    /**
     * Produits populaires
     * Basé sur le volume total de clics et conversions
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopularProductResponse>>> 
            getPopularProducts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "30") int days) {
        
        log.debug("Fetching popular products for last {} days", days);
        
        List<PopularProductResponse> popular = 
            analyticsService.getPopularProducts(category, limit, days);
        
        return ResponseEntity.ok(ApiResponse.success(popular));
    }
    
    /**
     * Performance d'un marchand spécifique
     * SECURISE : Accessible uniquement par le marchand concerné
     * 
     * Métriques fournies :
     * - Impressions totales (combien de fois ses produits apparaissent)
     * - Clics totaux (combien de fois ses offres sont cliquées)
     * - CTR (Click-Through Rate)
     * - Position moyenne dans les résultats
     * - Taux de conversion (si données disponibles)
     * - Évolution temporelle de ces métriques
     */
    @GetMapping("/merchant/{merchantId}/performance")
    @PreAuthorize("hasRole('MERCHANT') and #merchantId == principal.merchantId")
    public ResponseEntity<ApiResponse<MerchantPerformanceResponse>> 
            getMerchantPerformance(
            @PathVariable String merchantId,
            @RequestParam(required = false) 
                @DateTimeFormat(iso = ISO.DATE) LocalDate from,
            @RequestParam(required = false) 
                @DateTimeFormat(iso = ISO.DATE) LocalDate to) {
        
        log.info("Fetching performance metrics for merchant: {}", merchantId);
        
        // Dates par défaut : 30 derniers jours
        if (from == null) {
            from = LocalDate.now().minusDays(30);
        }
        if (to == null) {
            to = LocalDate.now();
        }
        
        MerchantPerformanceResponse performance = 
            merchantAnalyticsService.getMerchantPerformance(
                merchantId, from, to);
        
        return ResponseEntity.ok(ApiResponse.success(performance));
    }
    
    /**
     * Top produits d'un marchand
     * Classés par CTR, conversions ou revenus
     */
    @GetMapping("/merchant/{merchantId}/top-products")
    @PreAuthorize("hasRole('MERCHANT') and #merchantId == principal.merchantId")
    public ResponseEntity<ApiResponse<List<TopProductResponse>>> 
            getMerchantTopProducts(
            @PathVariable String merchantId,
            @RequestParam(defaultValue = "CTR") String sortBy,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "30") int days) {
        
        log.debug("Fetching top products for merchant: {} sorted by {}", 
            merchantId, sortBy);
        
        List<TopProductResponse> topProducts = 
            merchantAnalyticsService.getTopProducts(
                merchantId, sortBy, limit, days);
        
        return ResponseEntity.ok(ApiResponse.success(topProducts));
    }
    
    /**
     * Taux de conversion globaux
     * Métriques agrégées pour tous les marchands (données publiques)
     */
    @GetMapping("/conversion-rates")
    public ResponseEntity<ApiResponse<ConversionRatesResponse>> 
            getConversionRates(
            @RequestParam(required = false) String category) {
        
        log.debug("Fetching conversion rates for category: {}", category);
        
        ConversionRatesResponse rates = 
            analyticsService.getConversionRates(category);
        
        return ResponseEntity.ok(ApiResponse.success(rates));
    }
    
    /**
     * Statistiques de recherche pour un marchand
     * Quelles requêtes mènent à ses produits
     */
    @GetMapping("/merchant/{merchantId}/search-terms")
    @PreAuthorize("hasRole('MERCHANT') and #merchantId == principal.merchantId")
    public ResponseEntity<ApiResponse<List<SearchTermStatsResponse>>> 
            getMerchantSearchTerms(
            @PathVariable String merchantId,
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "50") int limit) {
        
        log.debug("Fetching search terms stats for merchant: {}", merchantId);
        
        List<SearchTermStatsResponse> searchTerms = 
            merchantAnalyticsService.getSearchTermStats(
                merchantId, days, limit);
        
        return ResponseEntity.ok(ApiResponse.success(searchTerms));
    }
}
```

---

## 🧮 Services Métier Principaux

### ProductAggregationService - Agrégation Multi-Sources

Le ProductAggregationService est le service central qui orchestre la collecte de données produits depuis toutes les sources disponibles (APIs, scraping, flux). Il normalise ces données hétérogènes en un format unifié et détecte les doublons pour éviter d'afficher le même produit plusieurs fois.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAggregationService {
    
    private final List<MerchantDataSource> dataSources;
    private final ProductNormalizer productNormalizer;
    private final ProductDeduplicator productDeduplicator;
    private final ProductIndexingService indexingService;
    private final CacheService cacheService;
    
    /**
     * Agrège les produits depuis toutes les sources actives
     * pour une requête de recherche donnée
     * 
     * Processus :
     * 1. Parallélisation des appels aux différentes sources
     * 2. Normalisation des données brutes
     * 3. Détection et fusion des doublons
     * 4. Enrichissement avec métadonnées
     * 5. Mise en cache des résultats
     */
    public List<Product> aggregateProducts(ProductSearchQuery query) {
        log.info("Aggregating products for query: {}", query);
        
        // Vérifier le cache d'abord
        String cacheKey = generateCacheKey(query);
        List<Product> cachedProducts = cacheService.getProducts(cacheKey);
        
        if (cachedProducts != null) {
            log.debug("Cache hit for products query");
            return cachedProducts;
        }
        
        // Collecter depuis toutes les sources en parallèle
        List<CompletableFuture<List<RawProduct>>> futures = dataSources.stream()
            .filter(MerchantDataSource::isActive)
            .map(source -> CompletableFuture.supplyAsync(() -> 
                collectFromSource(source, query)))
            .collect(Collectors.toList());
        
        // Attendre que toutes les sources aient répondu
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .join();
        
        // Agréger tous les résultats
        List<RawProduct> rawProducts = futures.stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .collect(Collectors.toList());
        
        log.info("Collected {} raw products from {} sources", 
            rawProducts.size(), dataSources.size());
        
        // Normaliser les produits
        List<Product> normalizedProducts = rawProducts.stream()
            .map(productNormalizer::normalize)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        // Détecter et fusionner les doublons
        List<Product> deduplicatedProducts = 
            productDeduplicator.deduplicate(normalizedProducts);
        
        log.info("After deduplication: {} unique products", 
            deduplicatedProducts.size());
        
        // Enrichir les produits avec métadonnées
        deduplicatedProducts.forEach(this::enrichProduct);
        
        // Mettre en cache
        cacheService.cacheProducts(cacheKey, deduplicatedProducts, 
            Duration.ofMinutes(15));
        
        return deduplicatedProducts;
    }
    
    /**
     * Collecte les produits depuis une source spécifique
     * avec gestion d'erreurs robuste
     */
    private List<RawProduct> collectFromSource(
            MerchantDataSource source, 
            ProductSearchQuery query) {
        try {
            log.debug("Collecting from source: {}", source.getName());
            
            List<RawProduct> products = source.searchProducts(query);
            
            log.debug("Collected {} products from {}", 
                products.size(), source.getName());
            
            return products;
        } catch (Exception e) {
            log.error("Error collecting from source: {}", 
                source.getName(), e);
            
            // Retourner liste vide plutôt que faire échouer toute l'agrégation
            return Collections.emptyList();
        }
    }
    
    /**
     * Normalise un produit brut en produit standardisé
     * Applique les règles de transformation spécifiques
     */
    public Product normalizeProduct(RawProduct rawProduct) {
        log.debug("Normalizing product: {}", rawProduct.getId());
        
        return productNormalizer.normalize(rawProduct);
    }
    
    /**
     * Détecte les doublons dans une liste de produits
     * Utilise plusieurs stratégies :
     * - Matching par EAN (code-barres) si disponible
     * - Matching par titre et specs similaires
     * - Matching par images similaires (hash perceptuel)
     */
    public List<ProductGroup> detectDuplicates(List<Product> products) {
        log.info("Detecting duplicates in {} products", products.size());
        
        return productDeduplicator.findDuplicates(products);
    }
    
    /**
     * Enrichit un produit avec des données supplémentaires
     * - Ajout d'images manquantes depuis d'autres sources
     * - Complétion des spécifications
     * - Ajout d'avis et notes si disponibles
     * - Métadonnées de catégorisation
     */
    private void enrichProduct(Product product) {
        // Enrichissement des images
        if (product.getImages() == null || product.getImages().isEmpty()) {
            List<String> images = findAdditionalImages(product);
            product.setImages(images);
        }
        
        // Enrichissement des spécifications
        Map<String, String> additionalSpecs = findAdditionalSpecs(product);
        if (product.getSpecifications() == null) {
            product.setSpecifications(additionalSpecs);
        } else {
            product.getSpecifications().putAll(additionalSpecs);
        }
        
        // Ajout de métadonnées de catégorisation
        String normalizedCategory = categorizationService.categorize(product);
        product.setNormalizedCategory(normalizedCategory);
    }
    
    /**
     * Récupère un produit par ID depuis toutes les sources
     */
    public Product getProductById(String productId) {
        log.debug("Fetching product by ID: {}", productId);
        
        // Vérifier le cache
        Product cachedProduct = cacheService.getProduct(productId);
        if (cachedProduct != null) {
            return cachedProduct;
        }
        
        // Chercher dans toutes les sources
        return dataSources.stream()
            .filter(MerchantDataSource::isActive)
            .map(source -> {
                try {
                    return source.getProductById(productId);
                } catch (Exception e) {
                    log.warn("Error fetching product from {}: {}", 
                        source.getName(), e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .findFirst()
            .map(productNormalizer::normalize)
            .orElseThrow(() -> new ProductNotFoundException(productId));
    }
    
    /**
     * Génère une clé de cache unique pour une requête
     */
    private String generateCacheKey(ProductSearchQuery query) {
        return String.format("products:%s:%s:%d:%d",
            query.getQuery(),
            query.getCategory() != null ? query.getCategory() : "all",
            query.getPage(),
            query.getSize()
        );
    }
    
    /**
     * Trouve des images supplémentaires pour un produit
     * depuis d'autres sources ou base de données d'images
     */
    private List<String> findAdditionalImages(Product product) {
        // Implémentation de recherche d'images
        // Peut utiliser reverse image search ou base de données produits
        return Collections.emptyList();
    }
    
    /**
     * Trouve des spécifications supplémentaires
     */
    private Map<String, String> findAdditionalSpecs(Product product) {
        // Implémentation de complétion de specs
        return new HashMap<>();
    }
}
```

### PriceComparisonService - Comparaison Intelligente

Le PriceComparisonService est responsable de comparer les offres de différents marchands pour un même produit. Il calcule le coût total en incluant les frais de livraison et taxes, identifie la meilleure offre selon différents critères, et fournit des recommandations personnalisées.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class PriceComparisonService {
    
    private final OfferRepository offerRepository;
    private final CurrencyConversionService currencyService;
    private final DeliveryCostCalculator deliveryCostCalculator;
    private final ComparisonConfig comparisonConfig;
    
    /**
     * Compare toutes les offres disponibles pour un produit
     * 
     * Prend en compte :
     * - Prix produit
     * - Frais de livraison
     * - Taxes applicables
     * - Délai de livraison
     * - Fiabilité du marchand
     * - Politique de retour
     */
    public PriceComparison compareOffers(String productId, String location) {
        log.info("Comparing offers for product: {} at location: {}", 
            productId, location);
        
        // Récupérer toutes les offres actives pour ce produit
        List<Offer> offers = offerRepository.findActiveOffers(productId);
        
        if (offers.isEmpty()) {
            throw new NoOffersAvailableException(productId);
        }
        
        // Calculer le coût total pour chaque offre
        List<OfferWithTotalCost> offersWithCost = offers.stream()
            .map(offer -> calculateTotalCost(offer, location))
            .collect(Collectors.toList());
        
        // Identifier la meilleure offre selon différents critères
        OfferWithTotalCost bestByPrice = findBestByPrice(offersWithCost);
        OfferWithTotalCost bestByDelivery = findBestByDeliveryTime(offersWithCost);
        OfferWithTotalCost bestOverall = findBestOverall(offersWithCost);
        
        // Calculer les statistiques de prix
        PriceStatistics stats = calculatePriceStats(offersWithCost);
        
        // Construire la réponse de comparaison
        return PriceComparison.builder()
            .productId(productId)
            .offersCount(offers.size())
            .offers(offersWithCost.stream()
                .map(this::toOfferResponse)
                .collect(Collectors.toList()))
            .bestByPrice(toOfferResponse(bestByPrice))
            .bestByDelivery(toOfferResponse(bestByDelivery))
            .bestOverall(toOfferResponse(bestOverall))
            .priceStats(stats)
            .recommendations(generateRecommendations(offersWithCost))
            .build();
    }
    
    /**
     * Calcule le coût total d'une offre
     * Coût total = Prix produit + Livraison + Taxes
     */
    private OfferWithTotalCost calculateTotalCost(Offer offer, String location) {
        // Prix du produit converti en FCFA
        BigDecimal productPrice = currencyService.convertToFCFA(
            offer.getPrice(), offer.getCurrency());
        
        // Calcul des frais de livraison selon la localisation
        BigDecimal shippingCost = deliveryCostCalculator.calculate(
            offer.getMerchant(), location);
        
        // Calcul des taxes applicables
        BigDecimal taxes = calculateTaxes(productPrice, location);
        
        // Coût total
        BigDecimal totalCost = productPrice
            .add(shippingCost)
            .add(taxes);
        
        return OfferWithTotalCost.builder()
            .offer(offer)
            .productPrice(productPrice)
            .shippingCost(shippingCost)
            .taxes(taxes)
            .totalCost(totalCost)
            .build();
    }
    
    /**
     * Identifie l'offre avec le prix total le plus bas
     */
    private OfferWithTotalCost findBestByPrice(List<OfferWithTotalCost> offers) {
        return offers.stream()
            .min(Comparator.comparing(OfferWithTotalCost::getTotalCost))
            .orElse(null);
    }
    
    /**
     * Identifie l'offre avec le délai de livraison le plus court
     */
    private OfferWithTotalCost findBestByDeliveryTime(
            List<OfferWithTotalCost> offers) {
        return offers.stream()
            .filter(o -> o.getOffer().getDeliveryTime() != null)
            .min(Comparator.comparing(o -> 
                o.getOffer().getDeliveryTime().getDays()))Je vais poursuivre le README du module Shop exactement là où il s'est arrêté, en maintenant le même niveau de détail, la même approche pédagogique et en respectant l'architecture du projet.

---

```java
            .orElse(null);
    }
    
    /**
     * Identifie la meilleure offre globale
     * Utilise un système de scoring pondéré :
     * - Prix : 60%
     * - Délai de livraison : 25%
     * - Fiabilité marchand : 15%
     */
    private OfferWithTotalCost findBestOverall(List<OfferWithTotalCost> offers) {
        return offers.stream()
            .max(Comparator.comparing(offer -> calculateOfferScore(offer)))
            .orElse(null);
    }
    
    /**
     * Calcule un score global pour une offre
     * Plus le score est élevé, meilleure est l'offre
     */
    private double calculateOfferScore(OfferWithTotalCost offer) {
        // Normaliser le prix (inverse car prix bas = bon)
        double priceScore = normalizePriceScore(offer.getTotalCost());
        
        // Score de livraison (livraison rapide = bon)
        double deliveryScore = normalizeDeliveryScore(
            offer.getOffer().getDeliveryTime());
        
        // Score de confiance du marchand
        double merchantScore = offer.getOffer().getMerchant().getTrustScore();
        
        // Calcul du score pondéré
        return (priceScore * comparisonConfig.getPriceWeight()) +
               (deliveryScore * comparisonConfig.getDeliveryWeight()) +
               (merchantScore * comparisonConfig.getMerchantWeight());
    }
    
    /**
     * Normalise le prix pour obtenir un score entre 0 et 1
     * Prix bas = score élevé
     */
    private double normalizePriceScore(BigDecimal price) {
        // Logique de normalisation basée sur les prix moyens du marché
        return 1.0 / (1.0 + price.doubleValue() / 100000.0);
    }
    
    /**
     * Normalise le délai de livraison
     * Livraison rapide = score élevé
     */
    private double normalizeDeliveryScore(DeliveryTime deliveryTime) {
        if (deliveryTime == null) return 0.5; // Score neutre si pas d'info
        
        int days = deliveryTime.getDays();
        if (days <= 2) return 1.0;  // Excellent
        if (days <= 5) return 0.8;  // Très bien
        if (days <= 10) return 0.6; // Bien
        return 0.4; // Acceptable
    }
    
    /**
     * Calcule les taxes applicables selon la localisation
     */
    private BigDecimal calculateTaxes(BigDecimal price, String location) {
        // La TVA varie selon les pays
        // Cameroun : 19.25%
        // France : 20%
        // USA : varie selon l'état
        
        double taxRate = getTaxRateForLocation(location);
        return price.multiply(BigDecimal.valueOf(taxRate));
    }
    
    /**
     * Obtient le taux de taxe pour une localisation
     */
    private double getTaxRateForLocation(String location) {
        // Implémentation simplifiée
        // En production, utiliser une base de données de taux fiscaux
        if (location != null && location.startsWith("CM")) {
            return 0.1925; // 19.25% au Cameroun
        }
        return 0.20; // 20% par défaut (France)
    }
    
    /**
     * Calcule des statistiques sur les prix des offres
     */
    private PriceStatistics calculatePriceStats(List<OfferWithTotalCost> offers) {
        List<BigDecimal> prices = offers.stream()
            .map(OfferWithTotalCost::getTotalCost)
            .sorted()
            .collect(Collectors.toList());
        
        BigDecimal min = prices.get(0);
        BigDecimal max = prices.get(prices.size() - 1);
        BigDecimal avg = prices.stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(prices.size()), RoundingMode.HALF_UP);
        
        // Médiane
        BigDecimal median = prices.size() % 2 == 0
            ? prices.get(prices.size() / 2)
                .add(prices.get(prices.size() / 2 - 1))
                .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP)
            : prices.get(prices.size() / 2);
        
        // Économie potentielle
        BigDecimal potentialSavings = max.subtract(min);
        double savingsPercentage = potentialSavings
            .divide(max, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .doubleValue();
        
        return PriceStatistics.builder()
            .minPrice(min)
            .maxPrice(max)
            .avgPrice(avg)
            .medianPrice(median)
            .potentialSavings(potentialSavings)
            .savingsPercentage(savingsPercentage)
            .build();
    }
    
    /**
     * Génère des recommandations personnalisées
     */
    private List<String> generateRecommendations(
            List<OfferWithTotalCost> offers) {
        List<String> recommendations = new ArrayList<>();
        
        OfferWithTotalCost cheapest = findBestByPrice(offers);
        OfferWithTotalCost fastest = findBestByDeliveryTime(offers);
        
        if (cheapest != null) {
            recommendations.add(String.format(
                "L'offre la moins chère est proposée par %s à %s FCFA",
                cheapest.getOffer().getMerchant().getName(),
                cheapest.getTotalCost().setScale(0, RoundingMode.HALF_UP)));
        }
        
        if (fastest != null && !fastest.equals(cheapest)) {
            recommendations.add(String.format(
                "La livraison la plus rapide (%d jours) est proposée par %s",
                fastest.getOffer().getDeliveryTime().getDays(),
                fastest.getOffer().getMerchant().getName()));
        }
        
        // Recommandation sur les économies
        PriceStatistics stats = calculatePriceStats(offers);
        if (stats.getSavingsPercentage() > 20) {
            recommendations.add(String.format(
                "Vous pouvez économiser jusqu'à %.1f%% en choisissant la bonne offre",
                stats.getSavingsPercentage()));
        }
        
        return recommendations;
    }
    
    /**
     * Récupère l'historique des prix d'un produit
     */
    public PriceHistory getPriceHistory(String productId, LocalDate from, LocalDate to) {
        log.debug("Fetching price history for product: {} from {} to {}", 
            productId, from, to);
        
        List<PriceHistoryEntry> history = priceHistoryRepository
            .findByProductIdAndDateRange(productId, from, to);
        
        // Grouper par marchand pour avoir des séries temporelles
        Map<String, List<PricePoint>> merchantSeries = history.stream()
            .collect(Collectors.groupingBy(
                entry -> entry.getMerchant().getId(),
                Collectors.mapping(
                    entry -> new PricePoint(
                        entry.getRecordedAt(),
                        entry.getPriceInFCFA()),
                    Collectors.toList())));
        
        // Calculer les tendances
        PriceTrend trend = calculatePriceTrend(history);
        
        return PriceHistory.builder()
            .productId(productId)
            .from(from)
            .to(to)
            .merchantSeries(merchantSeries)
            .trend(trend)
            .lowestPriceEver(findLowestPrice(history))
            .averagePrice(calculateAveragePrice(history))
            .build();
    }
    
    /**
     * Calcule la tendance générale des prix
     * RISING, FALLING, STABLE
     */
    private PriceTrend calculatePriceTrend(List<PriceHistoryEntry> history) {
        if (history.size() < 2) return PriceTrend.STABLE;
        
        // Calculer la pente de régression linéaire simple
        double slope = calculateLinearRegressionSlope(history);
        
        if (slope > 0.05) return PriceTrend.RISING;
        if (slope < -0.05) return PriceTrend.FALLING;
        return PriceTrend.STABLE;
    }
    
    /**
     * Convertit une offre avec coût en réponse API
     */
    private OfferResponse toOfferResponse(OfferWithTotalCost offerWithCost) {
        Offer offer = offerWithCost.getOffer();
        
        return OfferResponse.builder()
            .id(offer.getId())
            .merchant(merchantMapper.toSummary(offer.getMerchant()))
            .price(offerWithCost.getProductPrice())
            .priceInFCFA(offerWithCost.getProductPrice())
            .shippingCost(offerWithCost.getShippingCost())
            .taxes(offerWithCost.getTaxes())
            .totalCost(offerWithCost.getTotalCost())
            .availability(offer.getAvailability())
            .stock(offer.getStock())
            .deliveryTime(formatDeliveryTime(offer.getDeliveryTime()))
            .trackingUrl(generateTrackingUrl(offer))
            .isBestOffer(false) // Sera mis à true pour la meilleure offre
            .build();
    }
}
```

### MerchantIntegrationService - Intégration des Marchands

Le MerchantIntegrationService gère toute la logique d'intégration avec les marchands partenaires. Il supporte trois modes d'intégration : API REST directe, parsing de flux de données (XML/JSON/CSV), et scraping web éthique. Ce service est conçu pour être extensible et permettre l'ajout facile de nouveaux marchands.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantIntegrationService {
    
    private final MerchantRepository merchantRepository;
    private final FeedParserFactory feedParserFactory;
    private final ScraperFactory scraperFactory;
    private final ProductValidator productValidator;
    private final ProductNormalizer productNormalizer;
    
    /**
     * Récupère le catalogue complet d'un marchand
     * 
     * Le mode d'intégration dépend de la configuration du marchand :
     * - API : Appel REST/GraphQL direct
     * - FEED : Parsing de flux XML/JSON/CSV
     * - SCRAPING : Extraction depuis le site web
     */
    public List<Product> fetchMerchantCatalog(String merchantId) {
        log.info("Fetching catalog for merchant: {}", merchantId);
        
        Merchant merchant = merchantRepository.findById(merchantId)
            .orElseThrow(() -> new MerchantNotFoundException(merchantId));
        
        if (!merchant.isActive()) {
            log.warn("Merchant {} is not active", merchantId);
            return Collections.emptyList();
        }
        
        List<RawProduct> rawProducts;
        
        switch (merchant.getIntegrationType()) {
            case API:
                rawProducts = fetchViaAPI(merchant);
                break;
            case FEED:
                rawProducts = fetchViaFeed(merchant);
                break;
            case SCRAPING:
                rawProducts = fetchViaScraping(merchant);
                break;
            case MANUAL:
                log.info("Merchant {} uses manual integration", merchantId);
                return Collections.emptyList();
            default:
                throw new UnsupportedIntegrationException(
                    merchant.getIntegrationType());
        }
        
        log.info("Fetched {} raw products from {}", 
            rawProducts.size(), merchant.getName());
        
        // Valider et normaliser les produits
        List<Product> validatedProducts = rawProducts.stream()
            .map(this::validateAndNormalize)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        log.info("Successfully validated {} products", validatedProducts.size());
        
        return validatedProducts;
    }
    
    /**
     * Récupère les produits via API REST
     */
    private List<RawProduct> fetchViaAPI(Merchant merchant) {
        log.debug("Fetching via API for merchant: {}", merchant.getName());
        
        // Récupérer la configuration API du marchand
        ApiConfig apiConfig = parseApiConfig(merchant.getApiConfig());
        
        // Utiliser le client Feign approprié
        MerchantApiClient apiClient = 
            apiClientFactory.getClient(merchant.getId());
        
        try {
            // Pagination pour récupérer tout le catalogue
            List<RawProduct> allProducts = new ArrayList<>();
            int page = 0;
            boolean hasMore = true;
            
            while (hasMore) {
                ApiResponse response = apiClient.getProducts(
                    apiConfig.getApiKey(),
                    page,
                    apiConfig.getPageSize());
                
                allProducts.addAll(response.getProducts());
                
                hasMore = response.hasNextPage();
                page++;
                
                // Respecter les limites de rate limiting
                Thread.sleep(apiConfig.getRateLimitDelay());
            }
            
            return allProducts;
            
        } catch (Exception e) {
            log.error("Error fetching via API from {}: {}", 
                merchant.getName(), e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Récupère les produits via parsing de flux
     */
    private List<RawProduct> fetchViaFeed(Merchant merchant) {
        log.debug("Fetching via feed for merchant: {}", merchant.getName());
        
        String feedUrl = merchant.getFeedUrl();
        if (feedUrl == null || feedUrl.isEmpty()) {
            log.warn("No feed URL configured for merchant: {}", 
                merchant.getName());
            return Collections.emptyList();
        }
        
        try {
            // Télécharger le flux
            String feedContent = downloadFeed(feedUrl);
            
            // Détecter le format du flux
            FeedFormat format = detectFeedFormat(feedContent, feedUrl);
            
            // Obtenir le parser approprié
            FeedParser parser = feedParserFactory.getParser(format);
            
            // Parser le flux
            List<RawProduct> products = parser.parse(
                feedContent, 
                merchant.getFeedMapping());
            
            log.info("Parsed {} products from feed", products.size());
            
            return products;
            
        } catch (Exception e) {
            log.error("Error parsing feed from {}: {}", 
                merchant.getName(), e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Récupère les produits via scraping web
     * 
     * IMPORTANT : Cette méthode respecte strictement les règles éthiques :
     * - Lecture et respect du robots.txt
     * - Rate limiting (délai minimum entre requêtes)
     * - User-Agent transparent et identifiable
     * - Pas d'accès aux sections interdites
     */
    private List<RawProduct> fetchViaScraping(Merchant merchant) {
        log.debug("Fetching via scraping for merchant: {}", merchant.getName());
        
        // Vérifier que le scraping est autorisé
        if (!isScrapingAllowed(merchant.getDomain())) {
            log.warn("Scraping not allowed for domain: {}", merchant.getDomain());
            return Collections.emptyList();
        }
        
        // Obtenir le scraper approprié pour ce marchand
        MerchantScraper scraper = scraperFactory.getScraper(merchant.getDomain());
        
        if (scraper == null) {
            log.warn("No scraper available for merchant: {}", merchant.getName());
            return Collections.emptyList();
        }
        
        try {
            // Scraper respecte automatiquement robots.txt et rate limiting
            List<RawProduct> products = scraper.scrapeProductList(
                merchant.getScrapingConfig().getCatalogUrl());
            
            log.info("Scraped {} products from {}", 
                products.size(), merchant.getName());
            
            return products;
            
        } catch (Exception e) {
            log.error("Error scraping from {}: {}", 
                merchant.getName(), e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * Valide et normalise un produit brut
     */
    private Product validateAndNormalize(RawProduct rawProduct) {
        // Validation
        ValidationResult validation = productValidator.validate(rawProduct);
        
        if (!validation.isValid()) {
            log.debug("Product validation failed: {}", validation.getErrors());
            return null;
        }
        
        // Normalisation
        try {
            return productNormalizer.normalize(rawProduct);
        } catch (Exception e) {
            log.error("Error normalizing product: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse un flux de données (XML, JSON, CSV)
     * Applique le mapping de champs défini par le marchand
     */
    public List<RawProduct> parseFeed(String feedUrl, FeedFormat format) {
        log.info("Parsing feed: {} (format: {})", feedUrl, format);
        
        try {
            // Télécharger le contenu du flux
            String feedContent = downloadFeed(feedUrl);
            
            // Obtenir le parser approprié
            FeedParser parser = feedParserFactory.getParser(format);
            
            // Parser sans mapping (utilise les noms de champs par défaut)
            return parser.parse(feedContent, null);
            
        } catch (Exception e) {
            log.error("Error parsing feed: {}", e.getMessage());
            throw new FeedParsingException("Failed to parse feed: " + feedUrl, e);
        }
    }
    
    /**
     * Télécharge le contenu d'un flux depuis une URL
     */
    private String downloadFeed(String feedUrl) throws IOException {
        log.debug("Downloading feed from: {}", feedUrl);
        
        // Utiliser un client HTTP avec timeout approprié
        RestTemplate restTemplate = new RestTemplate();
        
        // Configurer les headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "YowYobBot/1.0 (+https://yowyob.com/bot)");
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // Télécharger avec timeout de 30 secondes
        ResponseEntity<String> response = restTemplate.exchange(
            feedUrl,
            HttpMethod.GET,
            entity,
            String.class);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IOException("Failed to download feed: " + 
                response.getStatusCode());
        }
        
        return response.getBody();
    }
    
    /**
     * Détecte automatiquement le format d'un flux
     */
    private FeedFormat detectFeedFormat(String content, String url) {
        // Détecter par extension d'URL
        if (url.endsWith(".xml")) return FeedFormat.XML;
        if (url.endsWith(".json")) return FeedFormat.JSON;
        if (url.endsWith(".csv")) return FeedFormat.CSV;
        
        // Détecter par contenu
        String trimmed = content.trim();
        if (trimmed.startsWith("<")) return FeedFormat.XML;
        if (trimmed.startsWith("{") || trimmed.startsWith("[")) 
            return FeedFormat.JSON;
        
        // Par défaut, CSV
        return FeedFormat.CSV;
    }
    
    /**
     * Vérifie si le scraping est autorisé pour un domaine
     * en consultant le fichier robots.txt
     */
    private boolean isScrapingAllowed(String domain) {
        try {
            RobotsTxt robotsTxt = robotsTxtCache.get(domain);
            
            if (robotsTxt == null) {
                robotsTxt = fetchAndParseRobotsTxt(domain);
                robotsTxtCache.put(domain, robotsTxt);
            }
            
            return robotsTxt.isAllowed("/", "YowYobBot");
            
        } catch (Exception e) {
            log.error("Error checking robots.txt for {}: {}", 
                domain, e.getMessage());
            // En cas d'erreur, on assume que c'est interdit par précaution
            return false;
        }
    }
    
    /**
     * Détecte les doublons dans une liste de produits
     * 
     * Utilise plusieurs stratégies de matching :
     * 1. EAN/UPC exact (si disponible)
     * 2. Similarité de titre (>85%)
     * 3. Similarité de spécifications clés
     */
    public Map<String, List<Product>> detectDuplicates(List<Product> products) {
        log.info("Detecting duplicates in {} products", products.size());
        
        Map<String, List<Product>> duplicateGroups = new HashMap<>();
        Set<String> processed = new HashSet<>();
        
        for (Product product : products) {
            if (processed.contains(product.getId())) {
                continue;
            }
            
            List<Product> duplicates = findDuplicatesFor(product, products);
            
            if (duplicates.size() > 1) {
                String groupKey = "group_" + UUID.randomUUID();
                duplicateGroups.put(groupKey, duplicates);
                duplicates.forEach(p -> processed.add(p.getId()));
            }
        }
        
        log.info("Found {} duplicate groups", duplicateGroups.size());
        
        return duplicateGroups;
    }
    
    /**
     * Trouve tous les doublons d'un produit donné
     */
    private List<Product> findDuplicatesFor(Product product, List<Product> allProducts) {
        List<Product> duplicates = new ArrayList<>();
        duplicates.add(product);
        
        for (Product other : allProducts) {
            if (product.getId().equals(other.getId())) {
                continue;
            }
            
            if (areProductsDuplicates(product, other)) {
                duplicates.add(other);
            }
        }
        
        return duplicates;
    }
    
    /**
     * Détermine si deux produits sont des doublons
     */
    private boolean areProductsDuplicates(Product p1, Product p2) {
        // Matching exact par EAN
        if (p1.getEan() != null && p2.getEan() != null) {
            if (p1.getEan().equals(p2.getEan())) {
                return true;
            }
        }
        
        // Matching par titre similaire
        double titleSimilarity = calculateTitleSimilarity(
            p1.getTitle(), p2.getTitle());
        
        if (titleSimilarity > 0.85) {
            // Vérifier aussi la similarité des specs clés
            double specsSimilarity = calculateSpecsSimilarity(
                p1.getSpecifications(), p2.getSpecifications());
            
            if (specsSimilarity > 0.70) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Calcule la similarité entre deux titres
     * Utilise la distance de Levenshtein normalisée
     */
    private double calculateTitleSimilarity(String title1, String title2) {
        // Normaliser les titres
        String t1 = normalizeTitle(title1);
        String t2 = normalizeTitle(title2);
        
        // Calculer la distance de Levenshtein
        int distance = levenshteinDistance(t1, t2);
        
        // Normaliser par la longueur du plus long titre
        int maxLength = Math.max(t1.length(), t2.length());
        
        return 1.0 - ((double) distance / maxLength);
    }
    
    /**
     * Normalise un titre pour la comparaison
     * Supprime la ponctuation, convertit en minuscules, etc.
     */
    private String normalizeTitle(String title) {
        return title.toLowerCase()
            .replaceAll("[^a-z0-9\\s]", "")
            .replaceAll("\\s+", " ")
            .trim();
    }
    
    /**
     * Regroupe les variantes d'un même produit
     * Ex: iPhone 14 128Go, iPhone 14 256Go, iPhone 14 512Go
     */
    public List<Product> mergeProductVariants(List<Product> products) {
        log.info("Merging product variants from {} products", products.size());
        
        // Grouper par produit de base (sans la variante)
        Map<String, List<Product>> variantGroups = products.stream()
            .collect(Collectors.groupingBy(this::extractBaseProductKey));
        
        List<Product> mergedProducts = new ArrayList<>();
        
        for (Map.Entry<String, List<Product>> entry : variantGroups.entrySet()) {
            List<Product> variants = entry.getValue();
            
            if (variants.size() == 1) {
                // Pas de variantes, garder tel quel
                mergedProducts.add(variants.get(0));
            } else {
                // Fusionner les variantes
                Product merged = mergeVariants(variants);
                mergedProducts.add(merged);
            }
        }
        
        log.info("Merged into {} base products", mergedProducts.size());
        
        return mergedProducts;
    }
    
    /**
     * Extrait une clé de base pour grouper les variantes
     * Ex: "iPhone 14 128Go" -> "iphone-14"
     */
    private String extractBaseProductKey(Product product) {
        String title = product.getTitle().toLowerCase();
        
        // Supprimer les indicateurs de variantes
        title = title.replaceAll("\\d+\\s*(go|gb|to|tb)", "");
        title = title.replaceAll("(noir|blanc|rouge|bleu|vert|gris)", "");
        title = title.replaceAll("[^a-z0-9]", "-");
        title = title.replaceAll("-+", "-");
        
        return title.trim();
    }
    
    /**
     * Fusionne plusieurs variantes en un seul produit
     */
    private Product mergeVariants(List<Product> variants) {
        // Prendre le premier comme base
        Product base = variants.get(0);
        
        // Créer un nouveau produit avec toutes les variantes
        Product merged = new Product();
        merged.setTitle(base.getTitle());
        merged.setDescription(base.getDescription());
        merged.setCategory(base.getCategory());
        merged.setBrand(base.getBrand());
        merged.setEan(base.getEan());
        
        // Fusionner les images de toutes les variantes
        Set<String> allImages = variants.stream()
            .flatMap(v -> v.getImages().stream())
            .collect(Collectors.toSet());
        merged.setImages(new ArrayList<>(allImages));
        
        // Fusionner les offres de toutes les variantes
        List<Offer> allOffers = variants.stream()
            .flatMap(v -> v.getOffers().stream())
            .collect(Collectors.toList());
        merged.setOffers(allOffers);
        
        // Calculer les statistiques agrégées
        merged.setAverageRating(calculateAverageRating(variants));
        merged.setReviewCount(sumReviewCounts(variants));
        
        return merged;
    }
}
```

### ScrapingService - Service de Scraping Éthique

Le ScrapingService implémente toute la logique de scraping web en respectant strictement les règles éthiques et légales. Ce service est conçu pour être robuste, respectueux des sites cibles, et transparent dans ses opérations.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapingService {
    
    private final ScrapingConfig scrapingConfig;
    private final RobotsTxtCache robotsTxtCache;
    private final RateLimiter rateLimiter;
    private final ScrapingClient scrapingClient;
    
    /**
     * Scrape une page produit et extrait les données structurées
     * 
     * RÈGLES ÉTHIQUES APPLIQUÉES :
     * 1. Vérification du robots.txt avant tout scraping
     * 2. Respect du délai de politesse (Crawl-delay)
     * 3. User-Agent transparent et identifiable
     * 4. Rate limiting strict
     * 5. Pas d'accès aux sections interdites
     */
    public ScrapedProduct scrapeProductPage(String url) {
        log.debug("Scraping product page: {}", url);
        
        // Extraire le domaine
        String domain = extractDomain(url);
        
        // Vérifier que le scraping est autorisé
        if (!isScrapingAllowed(domain, url)) {
            log.warn("Scraping not allowed for URL: {}", url);
            throw new ScrapingNotAllowedException(url);
        }
        
        // Appliquer le rate limiting
        rateLimiter.waitIfNeeded(domain);
        
        // Appliquer le délai de politesse
        applyPolitenessDelay(domain);
        
        try {
            // Récupérer le contenu HTML
            Document document = scrapingClient.fetchPage(url);
            
            // Extraire les données structurées
            ScrapedProduct product = extractProductData(document, url);
            
            log.debug("Successfully scraped product: {}", product.getTitle());
            
            return product;
            
        } catch (Exception e) {
            log.error("Error scraping URL {}: {}", url, e.getMessage());
            throw new ScrapingException("Failed to scrape URL: " + url, e);
        }
    }
    
    /**
     * Vérifie si le scraping est autorisé selon robots.txt
     */
    public boolean respectRobotsTxt(String domain) {
        try {
            RobotsTxt robotsTxt = robotsTxtCache.get(domain);
            
            if (robotsTxt == null) {
                // Télécharger et parser robots.txt
                robotsTxt = fetchAndParseRobotsTxt(domain);
                robotsTxtCache.put(domain, robotsTxt);
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("Error reading robots.txt for {}: {}", 
                domain, e.getMessage());
            return false;}
    }
    
    /**
     * Télécharge et parse le fichier robots.txt d'un domaine
     */
    private RobotsTxt fetchAndParseRobotsTxt(String domain) throws IOException {
        String robotsTxtUrl = "https://" + domain + "/robots.txt";
        
        log.debug("Fetching robots.txt from: {}", robotsTxtUrl);
        
        try {
            String content = scrapingClient.fetchPageContent(robotsTxtUrl);
            return RobotsTxtParser.parse(content);
        } catch (Exception e) {
            log.warn("Could not fetch robots.txt for {}, assuming allowed", 
                domain);
            // Si robots.txt n'existe pas, on assume que tout est autorisé
            return RobotsTxt.allowAll();
        }
    }
    
    /**
     * Vérifie si une URL spécifique est autorisée au scraping
     */
    private boolean isScrapingAllowed(String domain, String url) {
        RobotsTxt robotsTxt = robotsTxtCache.get(domain);
        
        if (robotsTxt == null) {
            respectRobotsTxt(domain);
            robotsTxt = robotsTxtCache.get(domain);
        }
        
        String path = extractPath(url);
        return robotsTxt.isAllowed(path, scrapingConfig.getUserAgent());
    }
    
    /**
     * Applique le délai de politesse entre les requêtes
     * 
     * Le délai est déterminé par :
     * 1. Crawl-delay spécifié dans robots.txt
     * 2. Configuration par défaut (10 secondes)
     */
    public void applyPolitenessDelay(String domain) {
        RobotsTxt robotsTxt = robotsTxtCache.get(domain);
        
        long delay = scrapingConfig.getPolitenessDelay();
        
        if (robotsTxt != null && robotsTxt.getCrawlDelay() != null) {
            delay = robotsTxt.getCrawlDelay() * 1000; // Convertir en ms
        }
        
        try {
            log.debug("Applying politeness delay of {}ms for {}", 
                delay, domain);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Politeness delay interrupted");
        }
    }
    
    /**
     * Extrait les données structurées d'une page produit
     * 
     * Supporte plusieurs formats :
     * - Schema.org JSON-LD
     * - Open Graph meta tags
     * - Microdata
     * - Sélecteurs CSS spécifiques au site
     */
    public Map<String, Object> extractStructuredData(Document html) {
        Map<String, Object> data = new HashMap<>();
        
        // Essayer d'extraire Schema.org JSON-LD
        Map<String, Object> jsonLd = extractJsonLd(html);
        if (!jsonLd.isEmpty()) {
            data.putAll(jsonLd);
        }
        
        // Essayer d'extraire Open Graph
        Map<String, Object> openGraph = extractOpenGraph(html);
        data.putAll(openGraph);
        
        // Essayer d'extraire Microdata
        Map<String, Object> microdata = extractMicrodata(html);
        data.putAll(microdata);
        
        return data;
    }
    
    /**
     * Extrait les données JSON-LD (Schema.org)
     */
    private Map<String, Object> extractJsonLd(Document html) {
        Map<String, Object> data = new HashMap<>();
        
        // Chercher les scripts de type application/ld+json
        Elements jsonLdScripts = html.select("script[type=application/ld+json]");
        
        for (Element script : jsonLdScripts) {
            try {
                String jsonContent = script.html();
                JsonNode json = objectMapper.readTree(jsonContent);
                
                // Vérifier si c'est un Product
                if (json.has("@type") && 
                    json.get("@type").asText().equals("Product")) {
                    
                    if (json.has("name")) {
                        data.put("title", json.get("name").asText());
                    }
                    if (json.has("description")) {
                        data.put("description", json.get("description").asText());
                    }
                    if (json.has("image")) {
                        data.put("image", json.get("image").asText());
                    }
                    if (json.has("offers")) {
                        JsonNode offers = json.get("offers");
                        if (offers.has("price")) {
                            data.put("price", offers.get("price").asText());
                        }
                        if (offers.has("priceCurrency")) {
                            data.put("currency", 
                                offers.get("priceCurrency").asText());
                        }
                    }
                    if (json.has("brand")) {
                        JsonNode brand = json.get("brand");
                        if (brand.has("name")) {
                            data.put("brand", brand.get("name").asText());
                        }
                    }
                    if (json.has("gtin13")) {
                        data.put("ean", json.get("gtin13").asText());
                    }
                }
            } catch (Exception e) {
                log.debug("Error parsing JSON-LD: {}", e.getMessage());
            }
        }
        
        return data;
    }
    
    /**
     * Extrait les données Open Graph
     */
    private Map<String, Object> extractOpenGraph(Document html) {
        Map<String, Object> data = new HashMap<>();
        
        Elements metaTags = html.select("meta[property^=og:]");
        
        for (Element meta : metaTags) {
            String property = meta.attr("property");
            String content = meta.attr("content");
            
            if (property.equals("og:title")) {
                data.put("title", content);
            } else if (property.equals("og:description")) {
                data.put("description", content);
            } else if (property.equals("og:image")) {
                data.put("image", content);
            } else if (property.equals("og:price:amount")) {
                data.put("price", content);
            } else if (property.equals("og:price:currency")) {
                data.put("currency", content);
            }
        }
        
        return data;
    }
    
    /**
     * Gère les CAPTCHAs rencontrés pendant le scraping
     * 
     * Stratégies :
     * 1. Attendre et réessayer plus tard
     * 2. Utiliser une approche différente (API si disponible)
     * 3. Marquer le site comme nécessitant une intervention manuelle
     */
    public ScrapingResult handleCaptcha(HttpResponse response) {
        log.warn("CAPTCHA detected, implementing fallback strategy");
        
        // Pour l'instant, on retourne simplement une erreur
        // Dans une implémentation complète, on pourrait :
        // - Utiliser un service de résolution de CAPTCHA (éthique)
        // - Basculer vers l'API du marchand si disponible
        // - Notifier l'équipe pour une intervention manuelle
        
        return ScrapingResult.failure("CAPTCHA detected");
    }
    
    /**
     * Extrait le domaine d'une URL
     */
    private String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost();
        } catch (Exception e) {
            log.error("Error extracting domain from URL: {}", url);
            return "";
        }
    }
    
    /**
     * Extrait le chemin d'une URL
     */
    private String extractPath(String url) {
        try {
            URI uri = new URI(url);
            return uri.getPath();
        } catch (Exception e) {
            log.error("Error extracting path from URL: {}", url);
            return "/";
        }
    }
    
    /**
     * Extrait les données produit d'un document HTML
     * en utilisant les données structurées et des sélecteurs CSS
     */
    private ScrapedProduct extractProductData(Document document, String url) {
        // Extraire les données structurées
        Map<String, Object> structuredData = extractStructuredData(document);
        
        ScrapedProduct product = new ScrapedProduct();
        product.setSourceUrl(url);
        product.setScrapedAt(Instant.now());
        
        // Remplir avec les données structurées
        product.setTitle((String) structuredData.get("title"));
        product.setDescription((String) structuredData.get("description"));
        product.setPrice((String) structuredData.get("price"));
        product.setCurrency((String) structuredData.get("currency"));
        product.setBrand((String) structuredData.get("brand"));
        product.setEan((String) structuredData.get("ean"));
        
        // Extraire les images
        List<String> images = extractImages(document);
        product.setImages(images);
        
        // Extraire les spécifications
        Map<String, String> specifications = extractSpecifications(document);
        product.setSpecifications(specifications);
        
        return product;
    }
    
    /**
     * Extrait toutes les images produit de la page
     */
    private List<String> extractImages(Document document) {
        List<String> images = new ArrayList<>();
        
        // Chercher les images dans la galerie produit
        Elements imageElements = document.select(
            ".product-gallery img, .product-images img, [data-image-role=product-image]");
        
        for (Element img : imageElements) {
            String src = img.attr("src");
            if (src != null && !src.isEmpty()) {
                images.add(src);
            }
            
            // Vérifier aussi data-src pour lazy loading
            String dataSrc = img.attr("data-src");
            if (dataSrc != null && !dataSrc.isEmpty()) {
                images.add(dataSrc);
            }
        }
        
        return images;
    }
    
    /**
     * Extrait les spécifications techniques du produit
     */
    private Map<String, String> extractSpecifications(Document document) {
        Map<String, String> specs = new HashMap<>();
        
        // Chercher les tableaux de spécifications
        Elements specTables = document.select(
            ".product-specs table, .specifications table, .product-attributes table");
        
        for (Element table : specTables) {
            Elements rows = table.select("tr");
            
            for (Element row : rows) {
                Elements cells = row.select("td, th");
                
                if (cells.size() >= 2) {
                    String key = cells.get(0).text().trim();
                    String value = cells.get(1).text().trim();
                    specs.put(key, value);
                }
            }
        }
        
        return specs;
    }
}
```

---

## 🔗 Intégration avec les autres modules

### Communication avec le Search Service

Le Shop Service est étroitement intégré avec le Search Service pour fournir une expérience de recherche unifiée. Lorsqu'un utilisateur effectue une recherche qui semble être liée à un produit (présence de mots-clés comme "acheter", "prix", noms de marques, etc.), le Search Service interroge automatiquement le Shop Service pour enrichir les résultats.

```
Flux de communication Search ↔ Shop :

┌──────────────────────────────────────────────────────────────────┐
│                    Utilisateur                                   │
│           Recherche : "Samsung Galaxy S23 prix"                  │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                  SEARCH SERVICE (Gateway)                        │
│  1. Analyse la requête                                           │
│  2. Détecte l'intention "shopping" (mots-clés, contexte)         │
│  3. Lance recherches parallèles :                                │
│     - Recherche web classique                                    │
│     - Recherche produits (Shop Service) ← NOUS SOMMES ICI        │
│     - Recherche images                                           │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│              SHOP SERVICE (Product Search)                       │
│  1. Reçoit la requête via HTTP/gRPC                              │
│  2. Parse et enrichit la requête :                               │
│     - Extraction entités (marque, modèle, specs)                 │
│     - Normalisation termes                                       │
│  3. Recherche multi-sources en parallèle :                       │
│     ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│     │   Jumia     │  │   Amazon    │  │   Kaymu     │          │
│     │   (API)     │  │   (API)     │  │ (Scraping)  │          │
│     └─────────────┘  └─────────────┘  └─────────────┘          │
│  4. Agrège et déduplique les résultats                           │
│  5. Compare les prix et identifie la meilleure offre             │
│  6. Retourne les produits avec métadonnées :                     │
│     - Produits correspondants                                    │
│     - Prix min/max/moyen                                         │
│     - Disponibilité                                              │
│     - Meilleures offres                                          │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│              SEARCH SERVICE (Aggregation)                        │
│  1. Reçoit résultats produits                                    │
│  2. Fusionne avec autres résultats :                             │
│     - Résultats web : Articles, reviews, comparatifs            │
│     - Résultats produits : Offres marchands                     │
│     - Résultats images : Photos produits                        │
│  3. Classement unifié selon pertinence                           │
│  4. Enrichissement avec données complémentaires                  │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│                  Résultats affichés à l'utilisateur              │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ 🛍️ Samsung Galaxy S23                                       │ │
│  │ 📊 À partir de 349,000 FCFA                                 │ │
│  │ ✅ Disponible chez 5 marchands                              │ │
│  │ 🏆 Meilleure offre : Jumia (349,000 FCFA, livraison 2j)    │ │
│  │ [Comparer les prix] [Voir les offres]                      │ │
│  └────────────────────────────────────────────────────────────┘ │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ 📰 Review : Samsung Galaxy S23 - Notre test complet        │ │
│  │ 🔗 techradar.com                                            │ │
│  └────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────┘
```

**API d'intégration avec Search Service** :

```java
/**
 * Endpoint appelé par le Search Service pour rechercher des produits
 * Route interne, non exposée publiquement
 */
@PostMapping("/internal/search")
@PreAuthorize("hasRole('SERVICE')")
public ProductSearchResponse searchProductsForSearchService(
        @Valid @RequestBody ProductSearchServiceRequest request) {
    
    // Request enrichie avec contexte de recherche
    // - Requête utilisateur originale
    // - Entités détectées (marque, modèle, etc.)
    // - Préférences utilisateur (localisation, devise)
    // - Contexte de session (historique recherches)
    
    ProductSearchResponse response = productAggregationService
        .searchForSearchService(request);
    
    // Response contient :
    // - Produits correspondants (top 10)
    // - Métadonnées agrégées (prix min/max, disponibilité)
    // - Meilleures offres
    // - Suggestions produits similaires
    // - Statistiques pour affichage (nb marchands, économie possible)
    
    return response;
}
```

### Communication avec le Analytics Service

Le Shop Service publie des événements détaillés sur Kafka que le Analytics Service consomme pour générer des insights et des rapports. Ces événements permettent de tracker le parcours complet de l'utilisateur depuis la recherche jusqu'à la redirection vers le marchand.

```
Flux d'événements Shop → Analytics :

┌──────────────────────────────────────────────────────────────────┐
│                      SHOP SERVICE                                │
│  Événements publiés sur Kafka :                                  │
│                                                                  │
│  Topic: yowyob.shop.events.product-indexed                       │
│  ├─ ProductIndexedEvent                                          │
│  │  - productId, merchantId                                      │
│  │  - category, brand, price                                     │
│  │  - timestamp                                                  │
│  │  ➜ Trigger : Nouveau produit ajouté à l'index                │
│                                                                  │
│  Topic: yowyob.shop.events.product-viewed                        │
│  ├─ ProductViewedEvent                                           │
│  │  - productId, userId, sessionId                               │
│  │  - position in results, query                                 │
│  │  - timestamp                                                  │
│  │  ➜ Trigger : Utilisateur consulte un produit                 │
│                                                                  │
│  Topic: yowyob.shop.events.product-clicked                       │
│  ├─ ProductClickEvent                                            │
│  │  - productId, offerId, merchantId                             │
│  │  - userId, sessionId, position                                │
│  │  - query, source                                              │
│  │  - timestamp                                                  │
│  │  ➜ Trigger : Utilisateur clique sur une offre                │
│                                                                  │
│  Topic: yowyob.shop.events.merchant-redirected                   │
│  ├─ MerchantRedirectEvent                                        │
│  │  - offerId, merchantId                                        │
│  │  - userId, sessionId                                          │
│  │  - targetUrl                                                  │
│  │  - timestamp                                                  │
│  │  ➜ Trigger : Redirection vers site marchand                  │
│                                                                  │
│  Topic: yowyob.shop.events.price-changed                         │
│  ├─ PriceChangeEvent                                             │
│  │  - productId, merchantId                                      │
│  │  - oldPrice, newPrice, currency                               │
│  │  - changePercentage                                           │
│  │  - timestamp                                                  │
│  │  ➜ Trigger : Détection changement de prix                    │
└──────────────────────────────┬───────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                   KAFKA EVENT BUS                                │
│  Partition par merchantId pour garantir l'ordre                  │
│  Retention : 30 jours                                            │
│  Replication factor : 3                                          │
└──────────────────────────────┬───────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                   ANALYTICS SERVICE                              │
│  Consomme les événements en temps réel                           │
│  Processing :                                                    │
│  1. Agrégation temps réel (fenêtres de 5min, 1h, 24h)           │
│  2. Calcul métriques :                                           │
│     - CTR (Click-Through Rate) par produit/marchand             │
│     - Taux de conversion                                         │
│     - Position moyenne dans résultats                            │
│     - Temps moyen avant clic                                     │
│  3. Détection anomalies (spike de prix, rupture stock)          │
│  4. ML : Prédiction tendances, recommandations                   │
│  5. Stockage :                                                   │
│     - ClickHouse (métriques agrégées)                            │
│     - PostgreSQL (rapports persistés)                            │
└──────────────────────────────────────────────────────────────────┘
```

**Exemple d'implémentation du producteur d'événements** :

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ShopEventProducer {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    /**
     * Publie un événement de clic produit
     */
    public void publishProductClick(ProductClickEvent event) {
        log.debug("Publishing product click event: {}", event);
        
        // Utiliser le merchantId comme clé pour garantir l'ordre des événements
        String key = event.getMerchantId();
        
        kafkaTemplate.send(
            "yowyob.shop.events.product-clicked",
            key,
            event
        ).addCallback(
            result -> log.debug("Event sent successfully: {}", event.getProductId()),
            ex -> log.error("Failed to send event: {}", event.getProductId(), ex)
        );
    }
    
    /**
     * Publie un événement de changement de prix
     * Avec métadonnées pour alertes temps réel
     */
    public void publishPriceChange(PriceChangeEvent event) {
        log.info("Publishing price change event: product={}, oldPrice={}, newPrice={}", 
            event.getProductId(), event.getOldPrice(), event.getNewPrice());
        
        // Si changement significatif (>10%), ajouter header pour traitement prioritaire
        if (Math.abs(event.getChangePercentage()) > 10.0) {
            ProducerRecord<String, Object> record = new ProducerRecord<>(
                "yowyob.shop.events.price-changed",
                event.getMerchantId(),
                event
            );
            record.headers().add("priority", "high".getBytes());
            
            kafkaTemplate.send(record);
        } else {
            kafkaTemplate.send(
                "yowyob.shop.events.price-changed",
                event.getMerchantId(),
                event
            );
        }
    }
}
```

### Communication avec le User Service

Le Shop Service communique avec le User Service pour :
1. **Personnaliser les résultats** selon les préférences de l'utilisateur (localisation, devise préférée, historique de navigation)
2. **Gérer les alertes de prix** : notifier l'utilisateur quand un produit qu'il suit baisse de prix
3. **Recommandations personnalisées** basées sur l'historique d'achat et les préférences

```
Flux User Service ↔ Shop Service :

┌──────────────────────────────────────────────────────────────────┐
│                    USER SERVICE                                  │
│  Fournit au Shop Service :                                       │
│  - Profil utilisateur (localisation, langue, devise)             │
│  - Préférences shopping (marchands favoris, budget)              │
│  - Historique recherches et clics                                │
│  - Listes de souhaits et produits suivis                         │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│              SHOP SERVICE (Personalization Layer)                │
│  Utilise les données utilisateur pour :                          │
│  1. Filtrer les marchands selon préférences                      │
│  2. Prioriser offres locales vs internationales                  │
│  3. Ajuster ranking selon historique                             │
│  4. Proposer recommandations personnalisées                      │
│  5. Gérer alertes de prix actives                                │
└──────────────────────────────────────────────────────────────────┘
```

**API de communication avec User Service** :

```java
/**
 * Client Feign pour communiquer avec le User Service
 */
@FeignClient(name = "user-service", path = "/api/users")
public interface UserServiceClient {
    
    /**
     * Récupère les préférences shopping d'un utilisateur
     */
    @GetMapping("/{userId}/preferences/shopping")
    ShoppingPreferences getShoppingPreferences(@PathVariable String userId);
    
    /**
     * Récupère l'historique de navigation produits
     */
    @GetMapping("/{userId}/history/products")
    List<ProductViewHistory> getProductViewHistory(
        @PathVariable String userId,
        @RequestParam(defaultValue = "30") int days);
    
    /**
     * Récupère les alertes de prix actives
     */
    @GetMapping("/{userId}/price-alerts")
    List<PriceAlert> getActivePriceAlerts(@PathVariable String userId);
    
    /**
     * Notifie l'utilisateur d'un changement de prix
     */
    @PostMapping("/{userId}/notifications/price-drop")
    void notifyPriceDrop(
        @PathVariable String userId,
        @RequestBody PriceDropNotification notification);
}
```

---

## ⚙️ Configuration et déploiement

### Configuration par environnement

Le Shop Service utilise des fichiers de configuration séparés pour chaque environnement (développement, staging, production). Cette approche permet d'adapter le comportement du service selon le contexte de déploiement.

**application.yml** (Configuration de base) :

```yaml
# Configuration commune à tous les environnements
spring:
  application:
    name: yowyob-shop-service
  
  profiles:
    active: ${ENV:dev}
  
  # Configuration JPA
  jpa:
    hibernate:
      ddl-auto: validate  # Jamais 'create' ou 'update' en prod
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        use_sql_comments: true
    show-sql: false  # Activé uniquement en dev
  
  # Configuration Elasticsearch
  elasticsearch:
    uris: ${ELASTICSEARCH_URIS:http://localhost:9200}
    socket-timeout: 30s
    connection-timeout: 5s
  
  # Configuration Redis
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 5000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
  
  # Configuration Kafka
  kafka:
    bootstrap-servers: ${KAFKA_BROKERS:localhost:9092}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      retries: 3
      properties:
        max.in.flight.requests.per.connection: 1
  
  # Configuration cache
  cache:
    type: redis
    redis:
      time-to-live: 300000  # 5 minutes par défaut
      cache-null-values: false

# Configuration du serveur
server:
  port: ${SERVER_PORT:8087}
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain
  
# Configuration management (actuator)
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true

# Configuration spécifique Shop Service
yowyob:
  shop:
    # Configuration scraping
    scraping:
      enabled: true
      user-agent: "YowYobBot/1.0 (+https://yowyob.com/bot)"
      timeout: 10000  # 10 secondes
      max-retries: 3
      politeness-delay: 10000  # 10 secondes entre requêtes
      respect-robots-txt: true
      rate-limit:
        max-requests-per-minute: 6  # Max 1 requête toutes les 10 secondes
    
    # Configuration conversion devises
    currency:
      default-currency: XAF  # Franc CFA
      exchange-rate-provider: openexchangerates
      api-key: ${EXCHANGE_RATE_API_KEY:}
      update-interval: 3600000  # 1 heure
      fallback-rates:
        EUR_XAF: 655.957
        USD_XAF: 600.0
        GBP_XAF: 750.0
    
    # Configuration comparaison prix
    comparison:
      max-sources: 10
      timeout: 5000  # 5 secondes
      parallel-execution: true
      include-in-total:
        - PRICE
        - SHIPPING
        - TAXES
      ranking-weights:
        price: 0.60
        delivery-time: 0.25
        merchant-trust: 0.15
    
    # Configuration indexation
    indexing:
      batch-size: 100
      parallel-threads: 4
      refresh-interval: 1s
    
    # Configuration cache
    cache:
      products:
        ttl: 900000  # 15 minutes
      offers:
        ttl: 300000  # 5 minutes
      comparison:
        ttl: 600000  # 10 minutes
      exchange-rates:
        ttl: 3600000  # 1 heure
```

**application-dev.yml** (Développement) :

```yaml
spring:
  # Base de données locale
  datasource:
    url: jdbc:postgresql://localhost:5432/yowyob_shop_dev
    username: dev_user
    password: dev_password
    hikari:
      maximum-pool-size: 10
  
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true# Elasticsearch local
  elasticsearch:
    uris: http://localhost:9200
  
  # Redis local
  redis:
    host: localhost
    port: 6379
  
  # Kafka local
  kafka:
    bootstrap-servers: localhost:9092

# Logging verbeux en dev
logging:
  level:
    com.yowyob.shop: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

yowyob:
  shop:
    scraping:
      # Scraping plus agressif en dev (pour tests)
      politeness-delay: 1000  # 1 seconde
      rate-limit:
        max-requests-per-minute: 60
```

**application-prod.yml** (Production) :

```yaml
spring:
  # Base de données production avec réplication
  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/yowyob_shop_prod?ssl=true&sslmode=require
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    show-sql: false
  
  # Elasticsearch cluster
  elasticsearch:
    uris: ${ELASTICSEARCH_CLUSTER}
  
  # Redis cluster
  redis:
    cluster:
      nodes: ${REDIS_CLUSTER_NODES}
    password: ${REDIS_PASSWORD}
  
  # Kafka cluster
  kafka:
    bootstrap-servers: ${KAFKA_BROKERS}
    producer:
      properties:
        security.protocol: SASL_SSL
        sasl.mechanism: PLAIN
        sasl.jaas.config: ${KAFKA_JAAS_CONFIG}

# Logging production
logging:
  level:
    com.yowyob.shop: INFO
    org.springframework: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  file:
    name: /var/log/yowyob/shop-service.log
    max-size: 100MB
    max-history: 30

yowyob:
  shop:
    scraping:
      # Scraping très respectueux en production
      politeness-delay: 15000  # 15 secondes
      rate-limit:
        max-requests-per-minute: 4
    
    # Circuit breaker pour sources externes
    circuit-breaker:
      enabled: true
      failure-threshold: 5
      timeout: 10000
      reset-timeout: 60000
```

### Déploiement avec Docker

Le Shop Service est conteneurisé pour faciliter le déploiement dans différents environnements. Voici la configuration Docker complète.

**Dockerfile** :

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY ../pom.xml ../pom.xml
COPY ../yowyob-common ../yowyob-common

# Télécharger les dépendances (mise en cache si pom.xml n'change pas)
RUN mvn dependency:go-offline

# Copier le code source
COPY src ./src

# Build l'application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S yowyob && adduser -S yowyob -G yowyob

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/yowyob-shop-service-*.jar app.jar

# Créer les répertoires nécessaires
RUN mkdir -p /app/logs /app/config && \
    chown -R yowyob:yowyob /app

# Changer vers l'utilisateur non-root
USER yowyob

# Exposer le port
EXPOSE 8087

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8087/actuator/health || exit 1

# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV SPRING_PROFILES_ACTIVE=prod

# Démarrer l'application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**docker-compose.yml** (Pour développement local) :

```yaml
version: '3.8'

services:
  # PostgreSQL pour les données structurées
  postgres:
    image: postgres:16-alpine
    container_name: yowyob-shop-postgres
    environment:
      POSTGRES_DB: yowyob_shop_dev
      POSTGRES_USER: dev_user
      POSTGRES_PASSWORD: dev_password
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./db/init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U dev_user"]
      interval: 10s
      timeout: 5s
      retries: 5
  
  # Elasticsearch pour l'indexation produits
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    container_name: yowyob-shop-elasticsearch
    environment:
      - discovery.type=single-node
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - es-data:/usr/share/elasticsearch/data
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:9200/_cluster/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 5
  
  # Redis pour le cache
  redis:
    image: redis:7-alpine
    container_name: yowyob-shop-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    command: redis-server --appendonly yes
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5
  
  # Kafka pour les événements
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: yowyob-shop-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    volumes:
      - kafka-data:/var/lib/kafka/data
  
  # Zookeeper pour Kafka
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: yowyob-shop-zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    volumes:
      - zookeeper-data:/var/lib/zookeeper/data
      - zookeeper-logs:/var/lib/zookeeper/log
  
  # Shop Service
  shop-service:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: yowyob-shop-service
    depends_on:
      postgres:
        condition: service_healthy
      elasticsearch:
        condition: service_healthy
      redis:
        condition: service_healthy
      kafka:
        condition: service_started
    ports:
      - "8087:8087"
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/yowyob_shop_dev
      SPRING_DATASOURCE_USERNAME: dev_user
      SPRING_DATASOURCE_PASSWORD: dev_password
      ELASTICSEARCH_URIS: http://elasticsearch:9200
      REDIS_HOST: redis
      REDIS_PORT: 6379
      KAFKA_BROKERS: kafka:9092
      EXCHANGE_RATE_API_KEY: ${EXCHANGE_RATE_API_KEY}
      JAVA_OPTS: "-Xms512m -Xmx1g"
    volumes:
      - ./logs:/app/logs
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8087/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s

volumes:
  postgres-data:
  es-data:
  redis-data:
  kafka-data:
  zookeeper-data:
  zookeeper-logs:

networks:
  default:
    name: yowyob-network
```

### Déploiement Kubernetes

Pour la production, le Shop Service est déployé sur Kubernetes pour bénéficier de la haute disponibilité, de l'auto-scaling et de la résilience.

**deployment.yaml** :

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: yowyob-shop-service
  namespace: yowyob-prod
  labels:
    app: shop-service
    version: v1
spec:
  replicas: 3  # 3 instances pour la haute disponibilité
  selector:
    matchLabels:
      app: shop-service
  template:
    metadata:
      labels:
        app: shop-service
        version: v1
    spec:
      serviceAccountName: shop-service-sa
      
      # Anti-affinity pour distribuer les pods sur différents nodes
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 100
              podAffinityTerm:
                labelSelector:
                  matchExpressions:
                    - key: app
                      operator: In
                      values:
                        - shop-service
                topologyKey: kubernetes.io/hostname
      
      containers:
        - name: shop-service
          image: registry.yowyob.com/shop-service:latest
          imagePullPolicy: Always
          
          ports:
            - name: http
              containerPort: 8087
              protocol: TCP
          
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            
            - name: SPRING_DATASOURCE_URL
              valueFrom:
                secretKeyRef:
                  name: shop-db-secret
                  key: url
            
            - name: SPRING_DATASOURCE_USERNAME
              valueFrom:
                secretKeyRef:
                  name: shop-db-secret
                  key: username
            
            - name: SPRING_DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: shop-db-secret
                  key: password
            
            - name: ELASTICSEARCH_URIS
              valueFrom:
                configMapKeyRef:
                  name: shop-config
                  key: elasticsearch.uris
            
            - name: REDIS_HOST
              valueFrom:
                configMapKeyRef:
                  name: shop-config
                  key: redis.host
            
            - name: KAFKA_BROKERS
              valueFrom:
                configMapKeyRef:
                  name: shop-config
                  key: kafka.brokers
            
            - name: EXCHANGE_RATE_API_KEY
              valueFrom:
                secretKeyRef:
                  name: shop-secrets
                  key: exchange-rate-api-key
            
            - name: JAVA_OPTS
              value: "-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
          
          resources:
            requests:
              memory: "1Gi"
              cpu: "500m"
            limits:
              memory: "2Gi"
              cpu: "2000m"
          
          # Health checks
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8087
            initialDelaySeconds: 120
            periodSeconds: 30
            timeoutSeconds: 5
            failureThreshold: 3
          
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8087
            initialDelaySeconds: 60
            periodSeconds: 10
            timeoutSeconds: 5
            failureThreshold: 3
          
          # Volume pour les logs
          volumeMounts:
            - name: logs
              mountPath: /app/logs
      
      volumes:
        - name: logs
          emptyDir: {}

---
apiVersion: v1
kind: Service
metadata:
  name: shop-service
  namespace: yowyob-prod
  labels:
    app: shop-service
spec:
  type: ClusterIP
  ports:
    - port: 80
      targetPort: 8087
      protocol: TCP
      name: http
  selector:
    app: shop-service

---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: shop-service-hpa
  namespace: yowyob-prod
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: yowyob-shop-service
  minReplicas: 3
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
        - type: Percent
          value: 50
          periodSeconds: 60
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
        - type: Pods
          value: 1
          periodSeconds: 60

---
apiVersion: v1
kind: ConfigMap
metadata:
  name: shop-config
  namespace: yowyob-prod
data:
  elasticsearch.uris: "http://elasticsearch-cluster:9200"
  redis.host: "redis-cluster"
  kafka.brokers: "kafka-broker-1:9092,kafka-broker-2:9092,kafka-broker-3:9092"
```

---

## 🧪 Tests et qualité

### Stratégie de tests

Le Shop Service implémente une pyramide de tests complète pour garantir la qualité et la fiabilité du code. Cette approche combine tests unitaires, tests d'intégration et tests end-to-end.

```
Pyramide de tests du Shop Service :

                        ┌─────────────┐
                        │   E2E Tests │  (5%)
                        │  Selenium   │
                        └─────────────┘
                    ┌───────────────────────┐
                    │  Integration Tests    │  (20%)
                    │  TestContainers       │
                    │  @SpringBootTest      │
                    └───────────────────────┘
            ┌───────────────────────────────────────┐
            │          Unit Tests                   │  (75%)
            │  JUnit 5, Mockito, AssertJ           │
            │  @WebMvcTest, @DataJpaTest           │
            └───────────────────────────────────────┘

Objectifs de couverture :
- Couverture globale : > 80%
- Couverture services métier : > 90%
- Couverture contrôleurs : > 85%
- Couverture repositories : > 70%
```

### Tests unitaires

Les tests unitaires couvrent chaque composant de manière isolée, en moquant toutes les dépendances externes.

**Exemple : Test du PriceComparisonService** :

```java
@ExtendWith(MockitoExtension.class)
class PriceComparisonServiceTest {
    
    @Mock
    private OfferRepository offerRepository;
    
    @Mock
    private CurrencyConversionService currencyService;
    
    @Mock
    private DeliveryCostCalculator deliveryCostCalculator;
    
    @Mock
    private ComparisonConfig comparisonConfig;
    
    @InjectMocks
    private PriceComparisonService priceComparisonService;
    
    @Test
    @DisplayName("Doit comparer correctement les offres et identifier la meilleure")
    void shouldCompareOffersAndIdentifyBest() {
        // Given
        String productId = "product-123";
        String location = "Yaoundé, Cameroun";
        
        List<Offer> mockOffers = Arrays.asList(
            createOffer("offer-1", BigDecimal.valueOf(350000), "Jumia"),
            createOffer("offer-2", BigDecimal.valueOf(345000), "Amazon"),
            createOffer("offer-3", BigDecimal.valueOf(360000), "Kaymu")
        );
        
        when(offerRepository.findActiveOffers(productId))
            .thenReturn(mockOffers);
        
        when(currencyService.convertToFCFA(any(), any()))
            .thenAnswer(inv -> inv.getArgument(0)); // Pas de conversion
        
        when(deliveryCostCalculator.calculate(any(), eq(location)))
            .thenReturn(BigDecimal.valueOf(2000)); // 2000 FCFA de livraison
        
        when(comparisonConfig.getPriceWeight()).thenReturn(0.6);
        when(comparisonConfig.getDeliveryWeight()).thenReturn(0.25);
        when(comparisonConfig.getMerchantWeight()).thenReturn(0.15);
        
        // When
        PriceComparison result = priceComparisonService.compareOffers(
            productId, location);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getOffersCount()).isEqualTo(3);
        assertThat(result.getOffers()).hasSize(3);
        
        // Vérifier que la meilleure offre est bien Amazon (345000)
        assertThat(result.getBestByPrice()).isNotNull();
        assertThat(result.getBestByPrice().getPrice())
            .isEqualTo(BigDecimal.valueOf(345000));
        
        // Vérifier les statistiques de prix
        assertThat(result.getPriceStats().getMinPrice())
            .isEqualTo(BigDecimal.valueOf(347000)); // 345000 + 2000
        assertThat(result.getPriceStats().getMaxPrice())
            .isEqualTo(BigDecimal.valueOf(362000)); // 360000 + 2000
        
        // Vérifier les appels aux dépendances
        verify(offerRepository).findActiveOffers(productId);
        verify(currencyService, times(3)).convertToFCFA(any(), any());
        verify(deliveryCostCalculator, times(3)).calculate(any(), eq(location));
    }
    
    @Test
    @DisplayName("Doit lancer une exception si aucune offre n'est disponible")
    void shouldThrowExceptionWhenNoOffersAvailable() {
        // Given
        String productId = "product-no-offers";
        
        when(offerRepository.findActiveOffers(productId))
            .thenReturn(Collections.emptyList());
        
        // When & Then
        assertThatThrownBy(() -> 
            priceComparisonService.compareOffers(productId, "Yaoundé"))
            .isInstanceOf(NoOffersAvailableException.class)
            .hasMessageContaining(productId);
    }
    
    @Test
    @DisplayName("Doit calculer correctement le coût total avec livraison et taxes")
    void shouldCalculateTotalCostWithShippingAndTaxes() {
        // Given
        Offer offer = createOffer("offer-1", BigDecimal.valueOf(100000), "Jumia");
        String location = "Yaoundé";
        
        when(currencyService.convertToFCFA(BigDecimal.valueOf(100000), "XAF"))
            .thenReturn(BigDecimal.valueOf(100000));
        
        when(deliveryCostCalculator.calculate(offer.getMerchant(), location))
            .thenReturn(BigDecimal.valueOf(3000));
        
        // When
        OfferWithTotalCost result = priceComparisonService
            .calculateTotalCost(offer, location);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getProductPrice()).isEqualTo(BigDecimal.valueOf(100000));
        assertThat(result.getShippingCost()).isEqualTo(BigDecimal.valueOf(3000));
        
        // Taxes = 19.25% au Cameroun
        BigDecimal expectedTaxes = BigDecimal.valueOf(100000)
            .multiply(BigDecimal.valueOf(0.1925))
            .setScale(2, RoundingMode.HALF_UP);
        assertThat(result.getTaxes()).isEqualByComparingTo(expectedTaxes);
        
        // Coût total = prix + livraison + taxes
        BigDecimal expectedTotal = BigDecimal.valueOf(100000)
            .add(BigDecimal.valueOf(3000))
            .add(expectedTaxes);
        assertThat(result.getTotalCost()).isEqualByComparingTo(expectedTotal);
    }
    
    private Offer createOffer(String id, BigDecimal price, String merchantName) {
        Merchant merchant = new Merchant();
        merchant.setId("merchant-" + id);
        merchant.setName(merchantName);
        merchant.setTrustScore(0.85);
        
        Offer offer = new Offer();
        offer.setId(id);
        offer.setPrice(price);
        offer.setCurrency("XAF");
        offer.setMerchant(merchant);
        offer.setAvailability(AvailabilityStatus.IN_STOCK);
        offer.setDeliveryTime(new DeliveryTime(3, 5));
        
        return offer;
    }
}
```

### Tests d'intégration

Les tests d'intégration vérifient que les différents composants fonctionnent correctement ensemble, en utilisant des bases de données et services réels (via TestContainers).

**Exemple : Test d'intégration du flux complet de recherche** :

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class ProductSearchIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("test_shop_db")
        .withUsername("test")
        .withPassword("test");
    
    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer(
        "docker.elastic.co/elasticsearch/elasticsearch:8.11.0")
        .withEnv("xpack.security.enabled", "false");
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OfferRepository offerRepository;
    
    @Autowired
    private MerchantRepository merchantRepository;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }
    
    @BeforeEach
    void setUp() {
        // Nettoyer les données de test
        offerRepository.deleteAll();
        productRepository.deleteAll();
        merchantRepository.deleteAll();
        
        // Créer des données de test
        setupTestData();
    }
    
    @Test
    @DisplayName("Doit rechercher et retourner des produits avec leurs offres")
    void shouldSearchProductsWithOffers() {
        // Given
        String query = "iPhone 14";
        
        // When
        ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> response = 
            restTemplate.exchange(
                "/api/products/search?query={query}&page=0&size=10",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                query
            );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        
        PageResponse<ProductResponse> pageResponse = response.getBody().getData();
        assertThat(pageResponse.getContent()).isNotEmpty();
        assertThat(pageResponse.getTotalElements()).isGreaterThan(0);
        
        // Vérifier qu'au moins un produit contient "iPhone 14" dans le titre
        assertThat(pageResponse.getContent())
            .anyMatch(p -> p.getTitle().contains("iPhone 14"));
        
        // Vérifier que chaque produit a des offres
        assertThat(pageResponse.getContent())
            .allMatch(p -> p.getOffers() != null && !p.getOffers().isEmpty());
    }
    
    @Test
    @DisplayName("Doit filtrer les produits par catégorie et prix")
    void shouldFilterProductsByCategoryAndPrice() {
        // Given
        String query = "smartphone";
        String category = "electronics";
        BigDecimal minPrice = BigDecimal.valueOf(200000);
        BigDecimal maxPrice = BigDecimal.valueOf(500000);
        
        // When
        ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> response = 
            restTemplate.exchange(
                "/api/products/search?query={query}&category={category}&priceMin={min}&priceMax={max}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                query, category, minPrice, maxPrice
            );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        PageResponse<ProductResponse> pageResponse = response.getBody().getData();
        
        // Vérifier que tous les produits sont dans la bonne catégorie
        assertThat(pageResponse.getContent())
            .allMatch(p -> p.getCategory().equals(category));
        
        // Vérifier que tous les produits sont dans la fourchette de prix
        assertThat(pageResponse.getContent())
            .allMatch(p -> {
                BigDecimal price = p.getBestOffer().getTotalCost();
                return price.compareTo(minPrice) >= 0 && 
                       price.compareTo(maxPrice) <= 0;
            });
    }
    
    @Test
    @DisplayName("Doit comparer les prix et retourner la meilleure offre")
    void shouldComparePricesAndReturnBestOffer() {
        // Given
        String productId = "product-iphone14-128";
        
        // When
        ResponseEntity<ApiResponse<PriceComparisonResponse>> response = 
            restTemplate.exchange(
                "/api/comparison/product/{productId}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                productId
            );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        PriceComparisonResponse comparison = response.getBody().getData();
        assertThat(comparison).isNotNull();
        assertThat(comparison.getOffers()).hasSizeGreaterThan(1);
        
        // Vérifier que la meilleure offre a le prix le plus bas
        OfferResponse bestOffer = comparison.getBestByPrice();
        assertThat(bestOffer).isNotNull();
        
        BigDecimal bestPrice = bestOffer.getTotalCost();
        assertThat(comparison.getOffers())
            .allMatch(o -> o.getTotalCost().compareTo(bestPrice) >= 0);
    }
    
    private void setupTestData() {
        // Créer des marchands de test
        Merchant jumia = createMerchant("Jumia Cameroun", "jumia.cm");
        Merchant amazon = createMerchant("Amazon FR", "amazon.fr");
        Merchant kaymu = createMerchant("Kaymu", "kaymu.cm");
        
        merchantRepository.saveAll(Arrays.asList(jumia, amazon, kaymu));
        
        // Créer des produits de test
        Product iphone14 = createProduct(
            "product-iphone14-128",
            "Apple iPhone 14 128Go Noir",
            "electronics",
            "Apple",
            "0194253397793"
        );
        
        Product samsungS23 = createProduct(
            "product-samsung-s23",
            "Samsung Galaxy S23 256Go",
            "electronics",
            "Samsung",
            "8806094785531"
        );
        
        productRepository.saveAll(Arrays.asList(iphone14, samsungS23));
        
        // Créer des offres de test
        Offer offer1 = createOffer(iphone14, jumia, BigDecimal.valueOf(450000));
        Offer offer2 = createOffer(iphone14, amazon, BigDecimal.valueOf(445000));
        Offer offer3 = createOffer(iphone14, kaymu, BigDecimal.valueOf(455000));
        
        Offer offer4 = createOffer(samsungS23, jumia, BigDecimal.valueOf(380000));
        Offer offer5 = createOffer(samsungS23, amazon, BigDecimal.valueOf(375000));
        
        offerRepository.saveAll(Arrays.asList(
            offer1, offer2, offer3, offer4, offer5));
    }
    
    private Merchant createMerchant(String name, String domain) {
        Merchant merchant = new Merchant();
        merchant.setId(UUID.randomUUID().toString());
        merchant.setName(name);
        merchant.setDomain(domain);
        merchant.setCountry("CM");
        merchant.setActive(true);
        merchant.setIntegrationType(IntegrationType.API);
        merchant.setTrustScore(0.85);
        merchant.setAverageDeliveryTime(3);
        return merchant;
    }
    
    private Product createProduct(String id, String title, String category, 
                                   String brand, String ean) {
        Product product = new Product();
        product.setId(id);
        product.setTitle(title);
        product.setDescription("Description de " + title);
        product.setCategory(category);
        product.setBrand(brand);
        product.setEan(ean);
        product.setImages(Arrays.asList(
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg"
        ));
        product.setAverageRating(4.5);
        product.setReviewCount(150);
        return product;
    }
    
    private Offer createOffer(Product product, Merchant merchant, BigDecimal price) {
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID().toString());
        offer.setProduct(product);
        offer.setMerchant(merchant);
        offer.setPrice(price);
        offer.setCurrency("XAF");
        offer.setPriceInFCFA(price);
        offer.setShippingCost(BigDecimal.valueOf(2000));
        offer.setTaxes(price.multiply(BigDecimal.valueOf(0.1925)));
        offer.setAvailability(AvailabilityStatus.IN_STOCK);
        offer.setStock(50);
        offer.setUrl("https://" + merchant.getDomain() + "/product/" + product.getId());
        offer.setActive(true);
        offer.setValidFrom(LocalDateTime.now());
        offer.setValidTo(LocalDateTime.now().plusDays(30));
        return offer;
    }
}
```

### Tests de scraping

Les tests de scraping vérifient que le service respecte bien les règles éthiques et extrait correctement les données.

```java
@ExtendWith(MockitoExtension.class)
class ScrapingServiceTest {
    
    @Mock
    private RobotsTxtCache robotsTxtCache;
    
    @Mock
    private RateLimiter rateLimiter;
    
    @Mock
    private ScrapingClient scrapingClient;
    
    @InjectMocks
    private ScrapingService scrapingService;
    
    @Test
    @DisplayName("Doit respecter robots.txt avant de scraper")
    void shouldRespectRobotsTxtBeforeScraping() {
        // Given
        String url = "https://example.com/product/123";
        RobotsTxt robotsTxt = RobotsTxt.builder()
            .allow("/product")
            .disallow("/admin")
            .build();
        
        when(robotsTxtCache.get("example.com")).thenReturn(robotsTxt);
        
        Document mockDocument = Jsoup.parse("<html></html>");
        when(scrapingClient.fetchPage(url)).thenReturn(mockDocument);
        
        // When
        assertThatCode(() -> scrapingService.scrapeProductPage(url))
            .doesNotThrowAnyException();
        
        // Then
        verify(robotsTxtCache).get("example.com");
        verify(rateLimiter).waitIfNeeded("example.com");
        verify(scrapingClient).fetchPage(url);
    }
    
    @Test
    @DisplayName("Doit lancer une exception si le scraping est interdit par robots.txt")
    void shouldThrowExceptionIfScrapingNotAllowed() {
        // Given
        String url = "https://example.com/admin/products";
        RobotsTxt robotsTxt = RobotsTxt.builder()
            .disallow("/admin")
            .build();
        
        when(robotsTxtCache.get("example.com")).thenReturn(robotsTxt);
        
        // When & Then
        assertThatThrownBy(() -> scrapingService.scrapeProductPage(url))
            .isInstanceOf(ScrapingNotAllowedException.class);
        
        verify(scrapingClient, never()).fetchPage(any());
    }
    
    @Test
    @DisplayName("Doit appliquer le délai de politesse entre les requêtes")
    void shouldApplyPolitenessDelay() throws InterruptedException {
        // Given
        String domain = "example.com";
        RobotsTxt robotsTxt = RobotsTxt.builder()
            .crawlDelay(10L) // 10 secondes
            .build();
        
        when(robotsTxtCache.get(domain)).thenReturn(robotsTxt);
        
        // When
        long startTime = System.currentTimeMillis();
        scrapingService.applyPolitenessDelay(domain);
        long endTime = System.currentTimeMillis();
        
        // Then
        long elapsed = endTime - startTime;
        assertThat(elapsed).isGreaterThanOrEqualTo(10000); // Au moins 10 secondes
    }
}
```

---

## 📊 Monitoring et observabilité

### Métriques exposées

Le Shop Service expose des métriques détaillées via Prometheus pour le monitoring en temps réel.

**Métriques principales** :

```yaml
# Métriques HTTP
http_server_requests_seconds: Durée des requêtes HTTP
  Tags: uri, method, status, exception

# Métriques JVM
jvm_memory_used_bytes: Mémoire JVM utilisée
jvm_gc_pause_seconds: Durée des pauses GC
jvm_threads_live: Nombre de threads actifs

# Métriques bases de données
hikaricp_connections_active: Connexions DB actives
hikaricp_connections_pending: Connexions en attente

# Métriques Elasticsearch
elasticsearch_search_duration_seconds: Durée des recherches ES
elasticsearch_index_duration_seconds: Durée d'indexation

# Métriques Redis (cache)
cache_gets_total{cache="products",result="hit|miss"}: Cache hits/misses
cache_evictions_total{cache="products"}: Évictions cache

# Métriques Kafka
kafka_producer_record_send_total: Messages Kafka envoyés
kafka_producer_record_error_total: Erreurs Kafka

# Métriques métier (custom)
shop_products_indexed_total: Produits indexés (compteur)
shop_price_comparisons_total: Comparaisons de prix effectuées
shop_merchant_redirects_total: Redirections vers marchands
shop_scraping_requests_total{merchant,status="success|failure"}: Requêtes scraping
shop_product_search_duration_seconds: Durée recherche produits
shop_offers_aggregated_total: Offres agrégées
shop_duplicates_detected_total: Doublons détectés
```

**Configuration des métriques custom** :

```java
@Component
@RequiredArgsConstructor
public class ShopMetrics {
    
    private final MeterRegistry meterRegistry;
    
    // Compteur de produits indexés
    public void incrementProductsIndexed(String merchantId) {
        Counter.builder("shop.products.indexed")
            .tag("merchant", merchantId)
            .register(meterRegistry)
            .increment();
    }
    
    // Compteur de comparaisons de prix
    public void recordPriceComparison(int offersCount) {
        Counter.builder("shop.price.comparisons")
            .tag("offers_count", String.valueOf(offersCount))
            .register(meterRegistry)
            .increment();
    }
    
    // Timer pour la durée des recherches
    public Timer.Sample startSearchTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void recordSearchDuration(Timer.Sample sample, String query, int resultsCount) {
        sample.stop(Timer.builder("shop.product.search.duration")
            .tag("has_results", String.valueOf(resultsCount > 0))
            .register(meterRegistry));
    }
    
    // Gauge pour le nombre d'offres actives
    public void registerActiveOffersGauge(Supplier<Number> valueSupplier) {
        Gauge.builder("shop.offers.active", valueSupplier)
            .description("Number of currently active offers")
            .register(meterRegistry);
    }
}
```

### Dashboards Grafana

Le Shop Service est surveillé via des dashboards Grafana personnalisés qui affichent les métriques clés en temps réel.

**Dashboard principal** :

```
╔══════════════════════════════════════════════════════════════╗
║           YOWYOB SHOP SERVICE - MONITORING DASHBOARD        ║
╠══════════════════════════════════════════════════════════════╣
║                                                              ║
║  📊 Santé Globale                                            ║
║  ┌────────────┬────────────┬────────────┬────────────┐      ║
║  │ Statut     │ Uptime     │ Erreurs/h  │ Latence p95 │      ║
║  │ ✅ UP      │ 99.98%     │ 0.2        │ 245ms      │      ║
║  └────────────┴────────────┴────────────┴────────────┘      ║
║                                                              ║
║  🔍 Activité Recherche (dernières 24h)                       ║
║  ┌─────────────────────────────────────────────────────┐    ║
║  │  Recherches totales : 45,230                        │    ║
║  │  Produits uniques vus : 12,450                      │    ║
║  │  Taux de cache hit : 78%                            │    ║
║  │  Durée moyenne : 235ms                              │    ║
║  └─────────────────────────────────────────────────────┘    ║
║                                                              ║
║  💰 Comparaisons de Prix                                     ║
║  ┌────────────────────────────────────────────┐             ║
║  │ [Graphique : Comparaisons/heure]           │             ║
║  │ Pic à 14h : 2,340 comparaisons             │             ║
║  │ Économie moyenne trouvée : 18,500 FCFA     │             ║
║  └────────────────────────────────────────────┘             ║
║                                                              ║
║  🏪 Marchands Actifs                                         ║
║  ┌──────────┬───────────┬─────────┬──────────┐             ║
║  │ Marchand │ Produits  │ Clics   │ CTR      │             ║
║  ├──────────┼───────────┼─────────┼──────────┤             ║
║  │ Jumia    │ 8,450     │ 1,234   │ 3.8%     │             ║
║  │ Amazon   │ 12,300    │ 2,105   │ 4.2%     │             ║
║  │ Kaymu    │ 3,200     │ 456     │ 2.9%     │             ║
║  │ Gloopro  │ 1,850     │ 287     │ 3.1%     │             ║
║  └──────────┴───────────┴─────────┴──────────┘             ║
║                                                              ║
║  ⚡ Performance Scraping                                     ║
║  ┌────────────────────────────────────────────┐             ║
║  │ Requêtes scraping/h : 145                  │             ║
║  │ Taux de succès : 97.2%                     │             ║
║  │ Durée moyenne : 2.3s                       │             ║
║  │ Robots.txt respecté : 100%                 │             ║
║  └────────────────────────────────────────────┘             ║
║                                                              ║
║  🔧 Ressources Système                                       ║
║  ┌────────────┬────────────┬────────────────┐              ║
║  │ CPU        │ Mémoire    │ Connexions DB  │              ║
║  │ 45%        │ 1.2GB/2GB  │ 12/50          │              ║
║  └────────────┴────────────┴────────────────┘              ║
╚══════════════════════════════════════════════════════════════╝
```

### Logging structuré

Le Shop Service utilise un logging structuré (JSON) pour faciliter l'agrégation et l'analyse des logs.

**Configuration Logback** :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Appender console pour le développement -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Appender JSON pour la production -->
    <appender name="JSON" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/yowyob/shop-service.json</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/yowyob/shop-service.%d{yyyy-MM-dd}.json.gz</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"service":"shop-service","version":"1.0.0"}</customFields>
        </encoder>
    </appender>
    
    <!-- Logger spécifique pour le scraping -->
    <appender name="SCRAPING" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/yowyob/shop-scraping.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/yowyob/shop-scraping.%d{yyyy-MM-dd}.log.gz</fileNamePattern>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Configuration des niveaux de log -->
    <logger name="com.yowyob.shop" level="INFO"/>
    <logger name="com.yowyob.shop.scraper" level="DEBUG" additivity="false">
        <appender-ref ref="SCRAPING"/>
    </logger>
    
    <!-- Profil développement -->
    <springProfile name="dev">
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
    
    <!-- Profil production -->
    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="JSON"/>
        </root>
    </springProfile>
    
</configuration>
```

---

## 🛠 Guide de développement

### Configuration de l'environnement local

**Prérequis** :
- JDK 21+
- Maven 3.9+
- Docker & Docker Compose
- IDE (IntelliJ IDEA recommandé)

**Étapes de setup** :

```bash
# 1. Cloner le repository
git clone https://github.com/yowyob/yowyob-search-backend.git
cd yowyob-search-backend/yowyob-shop-service

# 2. Démarrer les services dépendants avec Docker Compose
docker-compose up -d

# 3. Attendre que tous les services soient prêts
docker-compose ps

# 4. Créer le schéma de base de données
mvn liquibase:update

# 5. Créer l'index Elasticsearch
curl -X PUT "localhost:9200/yowyob-products" -H 'Content-Type: application/json' -d @src/main/resources/elasticsearch/mappings/product-mapping.json

# 6. Compiler et lancer les tests
mvn clean test

# 7. Lancer l'application
mvn spring-boot:run

# L'application est maintenant accessible sur http://localhost:8087
```

### Structure d'une feature complète

Voici comment ajouter une nouvelle feature au Shop Service en suivant l'architecture hexagonale.

**Exemple : Ajouter un système d'alertes de prix**

```
1. Créer le modèle de domaine :
   src/main/java/com/yowyob/shop/model/entity/PriceAlert.java

2. Créer le repository :
   src/main/java/com/yowyob/shop/repository/PriceAlertRepository.java

3. Créer le service métier :
   src/main/java/com/yowyob/shop/service/core/PriceAlertService.java

4. Créer le contrôleur :
   src/main/java/com/yowyob/shop/controller/PriceAlertController.java

5. Créer les DTOs :
   src/main/java/com/yowyob/shop/model/dto/PriceAlertRequest.java
   src/main/java/com/yowyob/shop/model/dto/PriceAlertResponse.java

6. Créer les événements Kafka :
   src/main/java/com/yowyob/shop/model/event/PriceAlertTriggeredEvent.java

7. Créer les tests :
   src/test/java/com/yowyob/shop/service/PriceAlertServiceTest.java
   src/test/java/com/yowyob/shop/controller/PriceAlertControllerTest.java
   src/test/java/com/yowyob/shop/integration/PriceAlertIntegrationTest.java
```

### Conventions de code

Le projet suit des conventions strictes pour maintenir la qualité et la cohérence du code.

**Naming conventions** :
- Classes : PascalCase (`ProductAggregationService`)
- Méthodes : camelCase (`aggregateProducts()`)
- Constantes : UPPER_SNAKE_CASE (`MAX_RETRY_ATTEMPTS`)
- Packages : lowercase (`com.yowyob.shop.service`)

**Annotations ordre** :
```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MyService {
    // Code
}
```

**Documentation** :
- Tous les services publics doivent avoir une Javadoc
- Les méthodes complexes doivent être commentées
- Les constantes magiques doivent être expliquées

**Tests** :
- Un test = un cas d'usage
- Nom de test descriptif avec `@DisplayName`
- Pattern Given-When-Then dans les tests
- Couverture minimale : 80%

---

## 🔒 Sécurité et éthique

### Sécurité de l'application

Le Shop Service implémente plusieurs couches de sécurité pour protéger les données et les opérations sensibles.

**1. Authentification et autorisation** :

```java
/**
 * Configuration de sécurité Spring Security
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf().disable() // API REST stateless
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics
                .requestMatchers("/api/products/**").permitAll()
                .requestMatchers("/api/comparison/**").permitAll()
                .requestMatchers("/api/merchants/*/products").permitAll()
                
                // Endpoints réservés aux marchands
                .requestMatchers("/api/catalog/**").hasRole("MERCHANT")
                .requestMatchers("/api/analytics/merchant/**").hasRole("MERCHANT")
                
                // Endpoints internes (communication inter-services)
                .requestMatchers("/internal/**").hasRole("SERVICE")
                
                // Actuator réservé aux admins
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // Tout le reste nécessite une authentification
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter()))
            )
            .build();
    }
    
    @Bean
    public JwtAuthenticationConverter jwtConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Extraire les rôles du JWT
            List<String> roles = jwt.getClaimAsStringList("roles");
            return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
        });
        return converter;
    }
}
```

**2. Protection contre les injections** :

```java
/**
 * Toutes les requêtes utilisateur sont validées et sanitisées
 */
@PostMapping("/search")
public ResponseEntity<PageResponse<ProductResponse>> search(
        @Valid @RequestBody ProductSearchRequest request) {
    
    // Validation automatique via annotations
    // @NotBlank, @Size, @Pattern, etc.
    
    // Sanitization des entrées
    String sanitizedQuery = inputSanitizer.sanitize(request.getQuery());
    
    // Utilisation de requêtes paramétrées (pas de concatenation SQL)
    // Protection automatique contre SQL Injection via JPA/Hibernate
    
    return ResponseEntity.ok(productService.search(request));
}
```

**3. Rate limiting** :

```java
/**
 * Rate limiting pour prévenir les abus
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final RateLimiter rateLimiter = RateLimiter.create(100.0); // 100 req/s
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        
        String clientId = extractClientId(request);
        
        if (!rateLimiter.tryAcquire()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### Éthique du scraping

Le Shop Service respecte scrupuleusement les règles éthiques et légales du scraping web.

**Principes éthiques** :

1. **Transparence** : Notre bot s'identifie clairement avec un User-Agent descriptif et des informations de contact

2. **Respect du robots.txt** : Nous lisons et respectons systématiquement les directives du fichier robots.txt de chaque site

3. **Rate limiting respectueux** : Nous n'effectuons jamais plus d'une requête toutes les 10 secondes par site (ou selon le Crawl-delay spécifié)

4. **Données publiques uniquement** : Nous ne scrapons que des données publiquement accessibles, jamais de contenu derrière authentification

5. **Opt-out respecté** : Si un site nous demande de ne plus le scraper, nous retirons immédiatement ce site de nos sources

6. **Pas de contournement** : Nous ne cherchons jamais à contourner les mesures de protection (CAPTCHAs, etc.)

**Implémentation** :

```java
/**
 * Vérification systématique du robots.txt avant chaque scraping
 */
public class RobotsTxtChecker {
    
    private final LoadingCache<String, RobotsTxt> cache;
    
    public boolean isScrapingAllowed(String domain, String path) {
        RobotsTxt robotsTxt = getRobotsTxt(domain);
        
        // Vérifier si le path est explicitement interdit
        if (robotsTxt.isDisallowed(path, USER_AGENT)) {
            log.info("Scraping not allowed for {} on {} according to robots.txt",
                path, domain);
            return false;
        }
        
        // Vérifier le Crawl-delay
        Long crawlDelay = robotsTxt.getCrawlDelay(USER_AGENT);
        if (crawlDelay != null) {
            applyPolitenessDelay(domain, crawlDelay * 1000);
        }
        
        return true;
    }
    
    /**
     * Applique un délai de politesse avant la prochaine requête
     */
    private void applyPolitenessDelay(String domain, long delayMs) {
        Long lastRequest = lastRequestTimes.get(domain);
        
        if (lastRequest != null) {
            long elapsed = System.currentTimeMillis() - lastRequest;
            long remaining = delayMs - elapsed;
            
            if (remaining > 0) {
                try {
                    log.debug("Applying politeness delay of {}ms for {}", 
                        remaining, domain);
                    Thread.sleep(remaining);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        lastRequestTimes.put(domain, System.currentTimeMillis());
    }
}
```

---

## 🎯 Conclusion et perspectives

Le Shop Service est un composant essentiel de la plateforme YowYob Search qui permet aux utilisateurs de comparer facilement les prix de produits provenant de multiples sources e-commerce. Son architecture hexagonale, son respect strict des principes éthiques, et son focus sur la performance en font un service robuste et évolutif.

### Points clés à retenir

**Architecture** :
- Architecture hexagonale pour une séparation claire des responsabilités
- Support multi-sources (API, flux, scraping) avec extensibilité facile
- Agrégation et dédoublonnage intelligents pour des résultats de qualité

**Performance** :
- Recherche parallèle dans toutes les sources
- Cache Redis pour des temps de réponse optimaux
- Indexation Elasticsearch pour des recherches rapides et pertinentes

**Éthique** :
- Respect strict des robots.txt et des règles de scraping
- Rate limiting respectueux des sites cibles
- Transparence totale (User-Agent identifiable, opt-out honoré)

**Observabilité** :
- Métriques détaillées exposées via Prometheus
- Logging structuré pour l'analyse
- Dashboards Grafana pour le monitoring temps réel

### Évolutions futures

**Court terme** :
- Support de nouveaux marchands (Alibaba, eBay, etc.)
- Amélioration de la détection de doublons avec ML
- Alertes de prix personnalisées pour les utilisateurs
- Comparaison visuelle des produits (reconnaissance d'images)

**Moyen terme** :
- Prédiction de l'évolution des prix avec ML
- Recommandations personnalisées basées sur l'historique
- Support de plus de devises et marchés internationaux
- API publique pour les développeurs tiers

**Long terme** :
- Analyse de sentiment des reviews produits
- Détection de faux produits et arnaques
- Chatbot intelligent pour assistance shopping
- Blockchain pour la traçabilité et l'authenticité des produits

---

**Ressources utiles** :
- [Documentation API complète](https://docs.yowyob.com/shop)

Le **Shop Service YowYob** est un **comparateur intelligent**, pas une marketplace. Notre valeur est dans :

1. **L'agrégation** : Collecter les offres dispersées sur le web
2. **La normalisation** : Rendre comparables des formats différents
3. **L'intelligence** : Identifier les produits identiques, détecter les tendances
4. **La transparence** : Présenter objectivement, rediriger vers le marchand

**Nous ne vendons rien. Nous aidons à comparer pour mieux acheter.**

Notre engagement :
- Collecte respectueuse et légale
- Comparaisons objectives
- Aucune transaction sur notre plateforme
- Protection de la vie privée des utilisateurs
- Amélioration continue du service

*YowYob Search Team - Équipe 4GI-ENSPY Promo 2027*  
*Contact : yowyob.4gi.enspy.promo.2027@gmail.com*

---

**Le Shop Service** est un pont, pas une destination. Notre rôle s'arrête à la comparaison. L'achat appartient à vous et au marchand que vous choisissez.