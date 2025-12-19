# API Gateway - YowYob Search Platform

> **Point d'entrée unique et sécurisé de la plateforme YowYob Search**  
> Gateway réactif construit avec Spring Cloud Gateway, orchestrant le routage intelligent, la sécurité centralisée et la résilience pour tous les microservices

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-4.1+-green.svg)](https://spring.io/projects/spring-cloud-gateway)
[![Resilience4j](https://img.shields.io/badge/Resilience4j-2.2+-blue.svg)](https://resilience4j.readme.io/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.yowyob.yowyob-api-gateway' is invalid and this project uses 'com.yowyob.yowyob_api_gateway' instead.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.8-SNAPSHOT/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.8-SNAPSHOT/maven-plugin/build-image.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.5.8-SNAPSHOT/reference/using/devtools.html)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/3.5.8-SNAPSHOT/reference/web/reactive.html)
* [Gateway](https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway-server-mvc.html)
* [Resilience4J](https://docs.spring.io/spring-cloud-circuitbreaker/reference/spring-cloud-circuitbreaker-resilience4j.html)
* [Spring Security](https://docs.spring.io/spring-boot/3.5.8-SNAPSHOT/reference/web/spring-security.html)
* [OAuth2 Resource Server](https://docs.spring.io/spring-boot/3.5.8-SNAPSHOT/reference/web/spring-security.html#web.security.oauth2.server)
* [Spring Data Redis (Access+Driver)](https://docs.spring.io/spring-boot/3.5.8-SNAPSHOT/reference/data/nosql.html#data.nosql.redis)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/3.5.8-SNAPSHOT/reference/actuator/index.html)
* [Prometheus](https://docs.spring.io/spring-boot/3.5.8-SNAPSHOT/reference/actuator/metrics.html#actuator.metrics.export.prometheus)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Messaging with Redis](https://spring.io/guides/gs/messaging-redis/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.



---

## 📑 Table des matières

- [Vue d'ensemble](#-vue-densemble)
- [Architecture du module](#-architecture-du-module)
- [Structure détaillée](#-structure-détaillée)
- [Composants principaux](#-composants-principaux)
- [Configuration et routage](#-configuration-et-routage)
- [Sécurité et authentification](#-sécurité-et-authentification)
- [Résilience et tolérance aux pannes](#-résilience-et-tolérance-aux-pannes)
- [Rate Limiting et protection](#-rate-limiting-et-protection)
- [Monitoring et observabilité](#-monitoring-et-observabilité)
- [Intégration avec les autres modules](#-intégration-avec-les-autres-modules)
- [Tests et qualité](#-tests-et-qualité)
- [Déploiement et scalabilité](#-déploiement-et-scalabilité)
- [Dépannage et maintenance](#-dépannage-et-maintenance)

---

## 🎯 Vue d'ensemble

### Qu'est-ce que l'API Gateway et pourquoi est-il crucial ?

Dans une architecture microservices comme celle de YowYob Search, où nous avons huit services backend différents (Search, User, Geo, Crawler, Notification, Shop, Stats, plus le Common partagé), permettre à chaque client de communiquer directement avec chaque service créerait un cauchemar de maintenance et de sécurité. Imaginez : chaque application cliente (web, mobile, ou tierce) devrait connaître l'adresse exacte de chaque service, gérer l'authentification séparément pour chacun, et composer avec des politiques de sécurité potentiellement différentes.

C'est exactement le problème que résout l'API Gateway. Pensez-y comme au réceptionniste d'un grand immeuble d'entreprise moderne. Au lieu que chaque visiteur aille frapper directement aux portes des différents bureaux dispersés dans le bâtiment, le réceptionniste accueille tout le monde à l'entrée principale, vérifie leur identité une seule fois, puis les dirige vers le bon service au bon étage. C'est simple, sécurisé et efficace.

L'API Gateway de YowYob agit précisément de cette manière. Il s'agit du point d'entrée unique pour toutes les requêtes qui arrivent vers notre plateforme. Qu'un utilisateur veuille effectuer une recherche, consulter son profil, localiser un commerce à proximité ou comparer des prix de produits, sa requête passe d'abord par l'API Gateway qui se charge de la valider, de l'authentifier, puis de la router vers le microservice approprié.

### Les responsabilités fondamentales de l'API Gateway

Notre API Gateway assume plusieurs responsabilités critiques qui vont bien au-delà du simple routage de requêtes. Chacune de ces responsabilités a été soigneusement implémentée pour garantir une plateforme robuste, sécurisée et performante.

**La sécurité centralisée** est probablement la responsabilité la plus importante. Au lieu que chaque microservice implémente sa propre logique d'authentification, ce qui créerait des incohérences et des risques de sécurité, l'API Gateway vérifie une seule fois l'identité de l'utilisateur dès son arrivée. Il valide le token JWT qui accompagne chaque requête, extrait les informations d'identité et de rôles, puis transmet ces informations aux services backend sous forme de headers sécurisés. Cela signifie que si un utilisateur malveillant essaie d'accéder à nos services, il est bloqué dès la porte d'entrée, avant même d'atteindre nos précieux services métier qui contiennent la logique critique de notre application.

**Le routage intelligent** constitue le cœur fonctionnel du Gateway. Il maintient une carte mentale complète de notre système : il sait que les requêtes vers `/api/search/**` doivent être dirigées vers le Search Service qui écoute sur le port 8082, que `/api/users/**` va vers le User Service sur le port 8083, que `/api/geo/**` pointe vers le Geo Service sur 8084, et ainsi de suite. Mais ce n'est pas tout : le Gateway ne route pas bêtement vers une adresse IP fixe. Il utilise un mécanisme de découverte de services (Service Discovery) qui lui permet de connaître en temps réel toutes les instances disponibles de chaque service. Si nous avons trois instances du Search Service qui tournent pour gérer la charge, le Gateway sait où elles sont toutes les trois et peut distribuer intelligemment les requêtes entre elles.

**La résilience et la tolérance aux pannes** transforment le Gateway en gardien intelligent de la stabilité du système. Grâce au mécanisme de Circuit Breaker (littéralement "disjoncteur" en français, emprunté aux systèmes électriques), le Gateway surveille activement la santé de chaque service backend. Si le Search Service commence à répondre lentement ou à générer des erreurs, le Circuit Breaker le détecte immédiatement. Après un certain nombre d'échecs (configuré à 50% des 10 dernières requêtes), le Circuit Breaker "s'ouvre" automatiquement. Pendant cette période d'ouverture, le Gateway arrête temporairement d'envoyer des requêtes au service défaillant, lui donnant le temps de se rétablir sans être bombardé de requêtes supplémentaires. À la place, le Gateway peut retourner une réponse mise en cache, rediriger vers une instance de secours, ou simplement informer l'utilisateur de manière élégante que le service est temporairement indisponible. C'est infiniment mieux que de laisser l'utilisateur attendre indéfiniment face à un écran qui charge.

**La limitation du débit (Rate Limiting)** protège nos services d'une surcharge, qu'elle soit accidentelle ou malveillante. Le Gateway impose des limites sur le nombre de requêtes qu'un utilisateur ou une adresse IP peut faire par période de temps. C'est comme avoir un videur à l'entrée d'une discothèque qui régule le flux de personnes pour éviter la surpopulation et garantir la sécurité. Sans cette protection, un utilisateur malveillant (ou même simplement un bug dans une application cliente) pourrait envoyer des milliers de requêtes par seconde et mettre notre système à genoux. Le Rate Limiting utilise l'algorithme du Token Bucket stocké dans Redis, ce qui permet une limitation distribuée : même si nous avons plusieurs instances du Gateway qui tournent, elles partagent toutes les mêmes compteurs dans Redis, garantissant que les limites sont respectées globalement.

**Le monitoring et l'observabilité** font du Gateway un point de surveillance privilégié. Puisque toutes les requêtes passent par lui, il peut facilement collecter des métriques détaillées : combien de requêtes par seconde reçoit chaque endpoint, quelle est la latence moyenne de chaque service, quel est le taux d'erreur, quels sont les endpoints les plus sollicités. Ces informations sont précieuses pour identifier les goulots d'étranglement, optimiser les performances et détecter rapidement les problèmes. Le Gateway expose toutes ces métriques au format Prometheus, qui sont ensuite visualisées dans des dashboards Grafana pour un monitoring en temps réel.

### Positionnement dans l'écosystème YowYob

L'API Gateway occupe une position stratégique dans l'architecture globale de YowYob Search. Il se situe à la frontière entre le monde externe (clients, applications tierces) et notre infrastructure interne. Cette position lui confère à la fois un pouvoir et une responsabilité considérables.

En amont du Gateway, nous avons le monde externe : les applications web construites avec Next.js qui tournent dans les navigateurs des utilisateurs, les applications mobiles PWA installées sur leurs smartphones, et potentiellement des applications tierces qui consomment notre API publique. Toutes ces applications envoient leurs requêtes HTTPS vers le Gateway qui écoute sur le port 8080. Le Gateway est exposé publiquement via un Ingress NGINX dans notre cluster Kubernetes, qui gère également la terminaison TLS, les certificats SSL, et le load balancing entre les différentes instances du Gateway.

En aval du Gateway, nous avons nos huit microservices backend. Le Gateway communique avec eux via le réseau interne du cluster Kubernetes, ce qui signifie que ces services ne sont jamais exposés directement à Internet. Ils sont protégés derrière le Gateway qui agit comme une barrière de sécurité. Chaque service écoute sur son propre port : Search Service sur 8082, User Service sur 8083, Geo Service sur 8084, et ainsi de suite. Le Gateway maintient cette carte de routage et sait exactement comment atteindre chaque service.

Le Gateway dépend également de plusieurs composants d'infrastructure partagés. Il utilise Redis pour le cache distribué (stockage des résultats de recherche populaires, des sessions, des compteurs de rate limiting). Il utilise Eureka (ou Consul dans certains déploiements) pour la découverte de services, ce qui lui permet de connaître dynamiquement les adresses de toutes les instances de services disponibles. Il envoie ses logs à Loki pour l'agrégation centralisée, ses métriques à Prometheus pour le monitoring, et ses traces distribuées à Jaeger pour le debugging de problèmes de performance complexes.

---

## 🏗 Architecture du module

### Vision d'ensemble de l'architecture

L'API Gateway de YowYob est construit sur Spring Cloud Gateway, qui est la solution moderne et recommandée par Spring pour créer des passerelles API dans un écosystème Spring Boot. Nous aurions pu choisir d'autres solutions comme Netflix Zuul (qui est maintenant en mode maintenance), Kong (qui nécessite une infrastructure séparée), ou AWS API Gateway (qui nous lierait à un fournisseur cloud spécifique). Nous avons opté pour Spring Cloud Gateway car il s'intègre parfaitement avec notre écosystème Spring Boot existant, offre d'excellentes performances grâce à son architecture réactive, et nous donne un contrôle total sur notre infrastructure sans dépendance à un fournisseur externe.

L'architecture de Spring Cloud Gateway repose sur trois concepts fondamentaux qui travaillent ensemble pour créer une passerelle puissante et flexible.

Les **Routes** (ou routes de routage) définissent comment les requêtes entrantes doivent être mappées vers les services backend. Chaque route est identifiée par un ID unique et contient essentiellement trois informations : un prédicat qui détermine si une requête correspond à cette route (par exemple "toutes les requêtes dont le chemin commence par `/api/search/`"), une URI de destination qui indique vers quel service backend rediriger la requête (par exemple `lb://SEARCH-SERVICE` où `lb` signifie load-balanced), et une liste de filtres à appliquer à cette route.

Les **Predicates** (prédicats en français) sont des conditions qui doivent être satisfaites pour qu'une route soit sélectionnée. Spring Cloud Gateway fournit de nombreux prédicats prédéfinis : Path (le chemin de l'URL correspond à un pattern), Method (la méthode HTTP est GET, POST, etc.), Header (un header spécifique est présent), Query (un paramètre de requête existe), Cookie (un cookie spécifique est présent), et bien d'autres. On peut également combiner plusieurs prédicats avec des opérateurs logiques AND/OR pour créer des conditions complexes.

Les **Filters** (filtres) sont des morceaux de code qui peuvent modifier la requête avant qu'elle ne soit envoyée au service backend, ou modifier la réponse avant qu'elle ne soit renvoyée au client. Spring Cloud Gateway distingue deux types de filtres : les GatewayFilter qui s'appliquent à une route spécifique, et les GlobalFilter qui s'appliquent à toutes les routes. Les filtres sont exécutés dans un ordre déterminé et peuvent faire des choses comme ajouter des headers, modifier le corps de la requête, logger des informations, appliquer du rate limiting, implémenter des circuit breakers, et bien plus encore.

### Architecture réactive : pourquoi c'est crucial pour nos performances

Spring Cloud Gateway utilise une architecture réactive non-bloquante basée sur Project Reactor, qui est l'implémentation Spring de la spécification Reactive Streams. Cette architecture est fondamentalement différente de l'approche traditionnelle bloquante utilisée par des serveurs comme Tomcat en mode standard.

Pour comprendre pourquoi c'est important, imaginons un restaurant traditionnel où chaque serveur ne peut s'occuper que d'une seule table à la fois. Le serveur prend la commande du client, va en cuisine, attend que le plat soit prêt (pendant ce temps il ne fait rien d'autre), puis revient servir le client. Si le restaurant a dix serveurs et qu'ils sont tous occupés à attendre en cuisine, le onzième client devra patienter même si les serveurs sont techniquement disponibles mais simplement bloqués en attente. C'est exactement ce qui se passe avec une architecture bloquante traditionnelle : chaque thread attend de manière passive que l'opération I/O (lecture réseau, accès base de données) se termine, et pendant ce temps le thread ne peut rien faire d'autre.

Maintenant, imaginons un restaurant avec un système moderne où les serveurs prennent plusieurs commandes, les transmettent toutes à la cuisine via un système de tickets, puis servent les plats au fur et à mesure qu'ils sont prêts, sans attendre bêtement devant les cuisiniers. Les serveurs peuvent s'occuper de plusieurs tables simultanément car ils ne bloquent pas en attendant. C'est exactement ce que fait l'architecture réactive : au lieu de bloquer un thread en attendant la réponse d'un microservice, le Gateway peut gérer des milliers de requêtes simultanées avec très peu de threads. Le thread démarre l'opération I/O, s'enregistre pour être notifié quand le résultat est prêt, puis passe immédiatement à traiter une autre requête. Quand le résultat arrive, le thread est notifié et peut reprendre le traitement.

En pratique, cela signifie que notre Gateway peut facilement traiter plus de mille requêtes par seconde par instance tout en maintenant une latence très faible (généralement moins de 50 millisecondes en moyenne). Avec l'architecture bloquante traditionnelle, nous aurions besoin de beaucoup plus d'instances et de ressources pour gérer la même charge. Cette efficacité se traduit directement par des coûts d'infrastructure réduits et une meilleure expérience utilisateur grâce à des temps de réponse plus rapides.

### Mécanisme de découverte de services et load balancing

Notre Gateway ne stocke pas en dur les adresses IP de nos microservices. Ce serait extrêmement fragile et impossible à maintenir dans un environnement cloud moderne où les instances de services démarrent et s'arrêtent dynamiquement selon la charge. Si nous avions codé en dur que le Search Service se trouve à `192.168.1.50:8082`, que se passerait-il si cette instance crashe et redémarre avec une nouvelle adresse IP ? Ou si nous lançons une deuxième instance pour gérer plus de charge ? Le Gateway ne saurait pas où la trouver.

À la place, nous utilisons un mécanisme de découverte de services (Service Discovery) qui maintient un annuaire dynamique et à jour de tous les services et leurs instances. Dans notre cas, nous utilisons soit Eureka (la solution Spring Cloud), soit Consul (une alternative plus moderne), selon le déploiement. Voici comment cela fonctionne concrètement :

Chaque instance de microservice, lorsqu'elle démarre, s'enregistre automatiquement auprès du service de découverte. Elle envoie un message du type "Bonjour, je suis une instance du Search Service, vous pouvez me joindre à l'adresse 10.244.1.15:8082, je suis en bonne santé". Le service de découverte note cette information dans son registre. Périodiquement (généralement toutes les 30 secondes), l'instance envoie un "heartbeat" (battement de cœur) pour indiquer qu'elle est toujours vivante et fonctionnelle. Si le service de découverte ne reçoit pas de heartbeat pendant un certain temps (généralement 90 secondes), il marque l'instance comme indisponible et la retire de son registre.

Côté Gateway, lorsqu'une requête arrive pour `/api/search/documents`, le Gateway regarde sa configuration et voit que cette route pointe vers `lb://SEARCH-SERVICE`. Le préfixe `lb` indique "load-balanced", ce qui signifie que le Gateway doit interroger le service de découverte pour obtenir la liste de toutes les instances disponibles du SEARCH-SERVICE. Le service de découverte répond avec quelque chose comme : "Il y a actuellement trois instances disponibles : 10.244.1.15:8082, 10.244.2.8:8082, 10.244.3.12:8082". Le Gateway choisit alors intelligemment vers quelle instance envoyer cette requête spécifique.

Le choix de l'instance se fait selon un algorithme de load balancing (répartition de charge). Par défaut, nous utilisons l'algorithme Round Robin qui distribue les requêtes de manière circulaire : première requête vers l'instance 1, deuxième vers l'instance 2, troisième vers l'instance 3, quatrième vers l'instance 1, et ainsi de suite. Mais nous pouvons aussi configurer d'autres algorithmes selon nos besoins : Random (choix aléatoire), Weighted Response Time (privilégie les instances qui répondent le plus rapidement), ou même un algorithme personnalisé qui prend en compte des critères métier spécifiques.

Cette approche dynamique est essentielle pour supporter le scaling horizontal automatique (autoscaling). Quand Kubernetes détecte que les instances actuelles du Search Service sont surchargées (CPU > 70% par exemple), il peut automatiquement lancer de nouvelles instances. Ces nouvelles instances s'enregistrent automatiquement auprès du service de découverte, et le Gateway commence immédiatement à leur envoyer des requêtes sans aucune configuration manuelle. De même, quand la charge diminue, Kubernetes peut arrêter les instances excédentaires qui se désenregistrent proprement, et le Gateway cesse de leur envoyer du trafic.

### Diagramme architectural complet

```
┌─────────────────────────────────────────────────────────────────────┐
│                   CLIENTS EXTERNES (World Wide Web)                 │
│  ┌───────────────┐  ┌────────────────┐  ┌──────────────────────┐   │
│  │  Web Browser  │  │  Mobile PWA    │  │  Third-party Apps    │   │
│  │  (Next.js)    │  │  (Installed)   │  │  (API Consumers)     │   │
│  └───────┬───────┘  └────────┬───────┘  └──────────┬───────────┘   │
│          │                   │                     │                │
└──────────┼───────────────────┼─────────────────────┼────────────────┘
           │                   │                     │
           │     HTTPS/TLS     │     HTTPS/TLS       │    HTTPS/TLS
           │     (Port 443)    │     (Port 443)      │    (Port 443)
           │                   │                     │
           └───────────────────┴─────────────────────┘
                              │
┌─────────────────────────────▼──────────────────────────────────────┐
│                    KUBERNETES INGRESS (NGINX)                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  - TLS Termination (certificats Let's Encrypt)              │  │
│  │  - Load Balancing entre instances du Gateway                │  │
│  │  - Rate Limiting global (protection DDoS)                   │  │
│  │  - Request logging et access logs                           │  │
│  └──────────────────────────┬───────────────────────────────────┘  │
└─────────────────────────────┼──────────────────────────────────────┘
                              │ HTTP (interne)
                              │ Port 8080
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      API GATEWAY (Ce module)                        │
│                    Spring Cloud Gateway + Reactor                   │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    GLOBAL FILTERS                            │  │
│  │  ┌────────────────┐  ┌───────────────┐  ┌────────────────┐  │  │
│  │  │ Request Logger │→ │  JWT Validator │→ │ CORS Handler  │  │  │
│  │  └────────────────┘  └───────────────┘  └────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                              │                                      │
│                              ▼                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    ROUTING ENGINE                            │  │
│  │  Predicates: Path, Method, Header, Query, Cookie            │  │
│  │  URI Resolution: Service Discovery (Eureka/Consul)          │  │
│  │  Load Balancing: Round Robin / Weighted / Random            │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                              │                                      │
│                              ▼                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    ROUTE-SPECIFIC FILTERS                    │  │
│  │  ┌─────────────────┐  ┌──────────────┐  ┌────────────────┐  │  │
│  │  │ Rate Limiter    │→ │ Circuit      │→ │ Retry Policy  │  │  │
│  │  │ (Redis-based)   │  │ Breaker      │  │ (Resilience4j)│  │  │
│  │  └─────────────────┘  └──────────────┘  └────────────────┘  │  │
│  │  ┌─────────────────┐  ┌──────────────┐  ┌────────────────┐  │  │
│  │  │ Request         │  │ Response     │  │ Cache Filter  │  │  │
│  │  │ Transformation  │  │ Modification │  │ (Redis)       │  │  │
│  │  └─────────────────┘  └──────────────┘  └────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                              │                                      │
└──────────────────────────────┼──────────────────────────────────────┘
                               │
         ┌─────────────────────┼──────────────────────┐
         │                     │                      │
         │                     │                      │
         ▼                     ▼                      ▼
┌─────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ Search Service  │  │  User Service    │  │  Geo Service     │
│  (Port 8082)    │  │  (Port 8083)     │  │  (Port 8084)     │
│                 │  │                  │  │                  │
│ • Elasticsearch │  │ • PostgreSQL     │  │ • PostGIS        │
│ • Redis Cache   │  │ • JWT Generation │  │ • OSM Nominatim  │
│ • Kafka         │  │ • BCrypt Hash    │  │ • Spatial Calc   │
└─────────────────┘  └──────────────────┘  └──────────────────┘
         │                     │                      │
         └─────────────────────┼──────────────────────┘
                               │
         ┌─────────────────────┼──────────────────────┐
         │                     │                      │
         ▼                     ▼                      ▼
┌─────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ Crawler Service │  │ Notification     │  │  Shop Service    │
│  (Port 8085)    │  │  Service         │  │  (Port 8087)     │
│                 │  │  (Port 8086)     │  │                  │
│ • JSoup/Tika    │  │ • Web Push       │  │ • Price Compare  │
│ • Quartz Sched  │  │ • FCM/SMTP       │  │ • Merchant API   │
│ • Kafka Prod    │  │ • Kafka Consumer │  │ • Product Index  │
└─────────────────┘  └──────────────────┘  └──────────────────┘
                               │
                               ▼
                     ┌──────────────────┐
                     │  Stats Service   │
                     │  (Port 8088)     │
                     │                  │
                     │ • Analytics      │
                     │ • Real-time KPI  │
                     │ • Kafka Streams  │
                     └──────────────────┘
                               │
┌──────────────────────────────┼──────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                             │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐               │
│  │ Redis       │  │ Eureka/      │  │ PostgreSQL   │               │
│  │ (Cache +    │  │ Consul       │  │ + PostGIS    │               │
│  │ Rate Limit) │  │ (Discovery)  │  │              │               │
│  └─────────────┘  └──────────────┘  └──────────────┘               │
│                                                                     │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐               │
│  │Elasticsearch│  │ Kafka +      │  │ Prometheus + │               │
│  │ Cluster     │  │ Zookeeper    │  │ Grafana      │               │
│  │             │  │ (Event Bus)  │  │ (Monitoring) │               │
│  └─────────────┘  └──────────────┘  └──────────────┘               │
└─────────────────────────────────────────────────────────────────────┘

Légende:
  → Flux de requête synchrone
  ┃ Communication asynchrone (Kafka)
  ▼ Dépendance infrastructure
```

---

## 📂 Structure détaillée

### Arborescence complète du module

Le module API Gateway est organisé selon les meilleures pratiques Spring Boot et les principes de séparation des responsabilités. Chaque package a un rôle clairement défini et contient uniquement les classes qui correspondent à ce rôle. Voici l'arborescence complète avec des explications détaillées :

```
yowyob-api-gateway/
│
├── 📄 pom.xml                                    # Configuration Maven du module
│   ├── Parent: yowyob-search-backend (héritage des versions)
│   ├── Dépendances principales:
│   │   ├── spring-cloud-starter-gateway (core du Gateway)
│   │   ├── spring-cloud-starter-netflix-eureka-client (découverte)
│   │   ├── spring-boot-starter-data-redis-reactive (cache + rate limit)
│   │   ├── spring-boot-starter-actuator (monitoring)
│   │   ├── resilience4j-spring-boot3 (circuit breaker + retry)
│   │   ├── micrometer-registry-prometheus (métriques)
│   │   └── yowyob-common (module partagé)
│   └── Plugins:
│       ├── spring-boot-maven-plugin (packaging)
│       ├── maven-surefire-plugin (tests)
│       └── jacoco-maven-plugin (coverage)
│
├── 📁 src/main/java/com/yowyob/gateway/
│   │
│   ├── 📄 GatewayApplication.java                # Point d'entrée Spring Boot
│   │   ├── Annotation: @SpringBootApplication
│   │   ├── Annotation: @EnableDiscoveryClient (activation Eureka)
│   │   ├── Annotation: @EnableCircuitBreaker (activation Resilience4j)
│   │   └── Configuration: Démarre l'application Gateway
│   │
│   ├── 📁 config/                                # Configurations Spring
│   │   │
│   │   ├── 📄 GatewayConfig.java                 # Configuration principale Gateway
│   │   │   ├── Bean: RouteLocator (définition des routes programmatiques)
│   │   │   ├── Configuration: Prefixes, timeouts, buffer sizes
│   │   │   ├── Méthodes: customRouteLocator()
│   │   │   └── Documentation: Explique chaque route configurée
│   │   │
│   │   ├── 📄 CorsConfig.java                    # Configuration CORS
│   │   │   ├── Bean: CorsWebFilter
│   │   │   ├── Allowed Origins: localhost:3000 (dev), yowyob.com (prod)
│   │   │   ├── Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
│   │   │   ├── Allowed Headers: *, Authorization, Content-Type
│   │   │   ├── Allow Credentials: true
│   │   │   └── Max Age: 3600 secondes
│   │   │
│   │   ├── 📄 RedisConfig.java                   # Configuration Redis
│   │   │   ├── Bean: ReactiveRedisTemplate<String, String>
│   │   │   ├── Connection: Redis Reactive Client
│   │   │   ├── Serializers: String/Jackson2JsonRedisSerializer
│   │   │   ├── Connection Pool: min=2, max=10
│   │   │   └── Timeouts: connect=5s, command=10s
│   │   │
│   │   ├── 📄 SecurityConfig.java                # Configuration sécurité
│   │   │   ├── Bean: SecurityWebFilterChain
│   │   │   ├── Configuration: HTTP Security reactive
│   │   │   ├── Endpoints publics: /actuator/health, /api/auth/**
│   │   │   ├── Endpoints protégés: /api/** (nécessitent JWT)
│   │   │   ├── CSRF: désactivé (API stateless)
│   │   │   └── Session: STATELESS
│   │   │
│   │   ├── 📄 CircuitBreakerConfig.java          # Configuration Circuit Breaker
│   │   │   ├── Bean: Customizer<ReactiveResilience4JCircuitBreakerFactory>
│   │   │   ├── Config par service:
│   │   │   │   ├── searchCircuitBreaker (pour Search Service)
│   │   │   │   ├── userCircuitBreaker (pour User Service)
│   │   │   │   └── autres services...
│   │   │   ├── Paramètres:
│   │   │   │   ├── slidingWindowSize: 10 requêtes
│   │   │   │   ├── failureRateThreshold: 50%
│   │   │   │   ├── waitDurationInOpenState: 5 secondes
│   │   │   │   ├── permittedNumberOfCallsInHalfOpenState: 3
│   │   │   │   └── slowCallDurationThreshold: 2 secondes
│   │   │   └── Callbacks: onError, onSuccess, onStateTransition
│   │   │
│   │   ├── 📄 RateLimiterConfig.java             # Configuration Rate Limiting
│   │   │   ├── Bean: RedisRateLimiter
│   │   │   ├── Algorithm: Token Bucket
│   │   │   ├── Storage: Redis (keys avec TTL)
│   │   │   ├── replenishRate: 10 tokens/seconde
│   │   │   ├── burstCapacity: 20 tokens
│   │   │   ├── Key Resolver: IP-based ou User-based
│   │   │   └── Deny Response: HTTP 429 avec Retry-After header
│   │   │
│   │   ├── 📄 RetryConfig.java                   # Configuration Retry Policy
│   │   │   ├── Bean: RetrySpec
│   │   │   ├── Max Attempts: 3
│   │   │   ├── Backoff: Exponential (1s, 2s, 4s)
│   │   │   ├── Retry On: IOException, TimeoutException
│   │   │   ├── Don't Retry: 4xx errors (client errors)
│   │   │   └── Jitter: 0.5 (randomize backoff)
│   │   │
│   │   ├── 📄 LoadBalancerConfig.java            # Configuration Load Balancer
│   │   │   ├── Bean: ReactorLoadBalancerClientFilter
│   │   │   ├── Strategy: Round Robin (default)
│   │   │   ├── Health Check: Eureka heartbeat-based
│   │   │   ├── Fallback: Remove unhealthy instances
│   │   │   └── Sticky Sessions: désactivé (stateless)
│   │   │
│   │   ├── 📄 WebClientConfig.java               # Configuration WebClient
│   │   │   ├── Bean: WebClient.Builder
│   │   │   ├── Codecs: 256KB max in-memory buffer
│   │   │   ├── Connection Timeout: 5 secondes
│   │   │   ├── Read/Write Timeout: 10 secondes
│   │   │   ├── Connection Pool: max 500 connections
│   │   │   └── Keep-Alive: enabled
│   │   │
│   │   ├── 📄 OpenApiConfig.java                 # Configuration OpenAPI/Swagger
│   │   │   ├── Bean: GroupedOpenApi (par service)
│   │   │   ├── Documentation: routes du Gateway
│   │   │   ├── Security Schemes: JWT Bearer
│   │   │   ├── Servers: dev, staging, production
│   │   │   └── Contact Info: équipe YowYob
│   │   │
│   │   └── 📄 ActuatorConfig.java                # Configuration Actuator
│   │       ├── Endpoints exposés: health, info, metrics, prometheus
│   │       ├── Security: /actuator/health public, reste nécessite admin
│   │       ├── Health Indicators: Redis, Eureka, Circuit Breakers
│   │       └── Info: git commit, build time, version
│   │
│   ├── 📁 filter/                                # Filtres Gateway
│   │   │
│   │   ├── 📄 JwtAuthenticationFilter.java       # Filtre validation JWT
│   │   │   ├── Type: GlobalFilter (s'applique à toutes les routes)
│   │   │   ├── Order: -100 (s'exécute tôt)
│   │   │   ├── Responsabilités:
│   │   │   │   ├── Extraction du token du header Authorization
│   │   │   │   ├── Validation de la signature JWT
│   │   │   │   ├── Vérification de l'expiration
│   │   │   │   ├── Extraction des claims (userId, roles)
│   │   │   │   ├── Ajout des headers X-User-Id, X-User-Roles
│   │   │   │   └── Gestion des tokens invalides/expirés
│   │   │   ├── Dépendances: JwtService (depuis common)
│   │   │   ├── Exceptions: UnauthorizedException si token invalide
│   │   │   └── Bypass: endpoints publics (configurables)
│   │   │
│   │   ├── 📄 LoggingFilter.java                 # Filtre de logging
│   │   │   ├── Type: GlobalFilter
│   │   │   ├── Order: Integer.MIN_VALUE (premier à s'exécuter)
│   │   │   ├── Logs:
│   │   │   │   ├── Request: method, path, queryParams, headers
│   │   │   │   ├── Response: status, duration, size
│   │   │   │   ├── User info: userId (si authentifié)
│   │   │   │   └── Correlation: request ID unique (X-Request-Id)
│   │   │   ├── MDC: populate avec requestId, userId pour logs structurés
│   │   │   ├── Format: JSON structuré
│   │   │   └── Niveau: INFO pour succès, WARN pour 4xx, ERROR pour 5xx
│   │   │
│   │   ├── 📄 CacheFilter.java                   # Filtre de cache
│   │   │   ├── Type: GatewayFilter (routes spécifiques)
│   │   │   ├── Order: 0
│   │   │   ├── Stratégie: Cache-Aside
│   │   │   ├── Storage: Redis
│   │   │   ├── Cache Key: hash(method + path + queryParams + userId)
│   │   │   ├── TTL: configurable par endpoint (300s par défaut)
│   │   │   ├── Only GET: cache uniquement les requêtes GET
│   │   │   ├── Invalidation: DELETE sur même resource invalide cache
│   │   │   └── Headers: X-Cache-Status (HIT/MISS/STALE)
│   │   │
│   │   ├── 📄 ErrorHandlingFilter.java           # Filtre gestion erreurs
│   │   │   ├── Type: GlobalFilter
│   │   │   ├── Order: Integer.MAX_VALUE (dernier)
│   │   │   ├── Catch: toutes exceptions non gérées
│   │   │   ├── Conversion: Exception → ErrorResponse (du common)
│   │   │   ├── HTTP Status: mapping selon type d'erreur
│   │   │   ├── Body: JSON structuré avec message, timestamp, path
│   │   │   ├── Headers: correlation ID
│   │   │   └── Logging: log complet avec stack trace
│   │   │
│   │   ├── 📄 MetricsFilter.java                 # Filtre de métriques
│   │   │   ├── Type: GlobalFilter
│   │   │   ├── Order: -50
│   │   │   ├── Métriques collectées:
│   │   │   │   ├── gateway_requests_total (counter)
│   │   │   │   ├── gateway_request_duration_seconds (histogram)
│   │   │   │   ├── gateway_request_size_bytes (histogram)
│   │   │   │   ├── gateway_response_size_bytes (histogram)
│   │   │   │   └── Tags: route, method, status, user_type
│   │   │   ├── Export: Prometheus format
│   │   │   └── Sampling: 100% (pas de sampling pour Gateway)
│   │   │
│   │   ├── 📄 RequestTransformationFilter.java   # Transformation requêtes
│   │   │   ├── Type: GatewayFilter (routes configurables)
│   │   │   ├── Order: 10
│   │   │   ├── Transformations possibles:
│   │   │   │   ├── Add/Remove/Modify headers
│   │   │   │   ├── Add/Remove/Modify query parameters
│   │   │   │   ├── Prefix/Rewrite path
│   │   │   │   ├── Add correlation headers (X-Request-Id, etc.)
│   │   │   │   └── Normalize user-agent, accept headers
│   │   │   └── Configuration: YAML-based per route
│   │   │
│   │   ├── 📄 ResponseTransformationFilter.java  # Transformation réponses
│   │   │   ├── Type: GatewayFilter
│   │   │   ├── Order: -10 (après processing)
│   │   │   ├── Transformations:
│   │   │   │   ├── Add headers (X-Response-Time, X-Served-By)
│   │   │   │   ├── Remove sensitive headers (X-Internal-*)
│   │   │   │   ├── Wrap response dans ApiResponse si nécessaire
│   │   │   │   ├── Add CORS headers (si CorsFilter insuffisant)
│   │   │   │   └── Compression (gzip) si > 1KB
│   │   │   └── Configuration: per-route settings
│   │   │
│   │   └── 📄 FallbackFilter.java                # Filtre fallback
│   │       ├── Type: GatewayFilter
│   │       ├── Activated: quand Circuit Breaker est OPEN
│   │       ├── Stratégies:
│   │       │   ├── Return cached response (si disponible)
│   │       │   ├── Return static fallback (JSON pré-défini)
│   │       │   ├── Redirect to alternative service
│   │       │   └── Return graceful error message
│   │       ├── HTTP Status: 503 Service Unavailable
│   │       ├── Headers: Retry-After: 10
│   │       └── Body: JSON avec message clair et suggestions
│   │
│   ├── 📁 predicate/                             # Prédicats personnalisés
│   │   │
│   │   ├── 📄 AuthenticatedUserPredicate.java    # Prédicat utilisateur authentifié
│   │   │   ├── Check: présence du header X-User-Id
│   │   │   ├── Usage: routes nécessitant authentification
│   │   │   └── Config: .predicate(new AuthenticatedUserPredicate())
│   │   │
│   │   ├── 📄 RolePredicate.java                 # Prédicat basé sur rôle
│   │   │   ├── Check: X-User-Roles contient role spécifié
│   │   │   ├── Roles: USER, ADMIN, MERCHANT, MODERATOR
│   │   │   ├── Usage: routes réservées à certains rôles
│   │   │   └── Config: .predicate(new RolePredicate("ADMIN"))
│   │   │
│   │   └── 📄 ApiVersionPredicate.java           # Prédicat version API
│   │       ├── Check: header Accept-Version ou path prefix /v1/, /v2/
│   │       ├── Usage: routing vers différentes versions de services
│   │       └── Default: v1 si non spécifié
│   │
│   ├── 📁 handler/                               # Gestionnaires spécialisés
│   │   │
│   │   ├── 📄 GlobalErrorHandler.java            # Gestionnaire erreurs global
│   │   │   ├── Extends: AbstractErrorWebExceptionHandler
│   │   │   ├── Handle: toutes exceptions du Gateway
│   │   │   ├── Mapping:
│   │   │   │   ├── NotFoundException → 404
│   │   │   │   ├── UnauthorizedException → 401
│   │   │   │   ├── ForbiddenException → 403
│   │   │   │   ├── TimeoutException → 504
│   │   │   │   ├── CircuitBreakerOpenException → 503
│   │   │   │   └── default → 500
│   │   │   ├── Response: ErrorResponse (du common)
│   │   │   ├── Logging: log avec contexte complet
│   │   │   └── Alerting: déclenche alertes pour erreurs 5xx
│   │   │
│   │   ├── 📄 FallbackHandler.java               # Gestionnaire fallback
│   │   │   ├── Endpoint: /fallback/*
│   │   │   ├── Activated by: Circuit Breaker
│   │   │   ├── Logic:
│   │   │   │   ├── Check cache Redis pour response sauvegardée
│   │   │   │   ├── Si cache hit: return cached + X-Cache-Status: STALE
│   │   │   │   ├── Si cache miss: return static fallback
│   │   │   │   └── Log fallback activation pour monitoring
│   │   │   └── Response: 503 avec message explicatif
│   │   │
│   │   └── 📄 HealthCheckHandler.java            # Gestionnaire health checks
│   │       ├── Endpoint: /actuator/health
│   │       ├── Public: true (pas d'authentification)
│   │       ├── Checks:
│   │       │   ├── Gateway itself: UP
│   │       │   ├── Redis: ping test
│   │       │   ├── Eureka: connection test
│   │       │   ├── Circuit Breakers: état de tous les CBs
│   │       │   └── Downstream services: health via Service Discovery
│   │       ├── Response: UP/DOWN + détails par composant
│   │       └── Timeout: 5 secondes max par check
│   │
│   ├── 📁 dto/                                   # DTOs spécifiques Gateway
│   │   │
│   │   ├── 📄 RouteInfo.java                     # Information sur une route
│   │   │   ├── Champs: id, uri, predicates[], filters[]
│   │   │   ├── Usage: endpoint /actuator/gateway/routes
│   │   │   └── Serialization: JSON
│   │   │
│   │   ├── 📄 CircuitBreakerStatus.java          # Statut Circuit Breaker
│   │   │   ├── Champs: name, state, metrics
│   │   │   ├── States: CLOSED, OPEN, HALF_OPEN
│   │   │   ├── Metrics: failure rate, call counts, timestamps
│   │   │   └── Usage: monitoring et debugging
│   │   │
│   │   ├── 📄 RateLimitInfo.java                 # Info rate limiting
│   │   │   ├── Champs: limit, remaining, reset_at
│   │   │   ├── Headers: X-RateLimit-Limit, X-RateLimit-Remaining
│   │   │   └── Response: quand rate limit dépassé
│   │   │
│   │   └── 📄 GatewayMetrics.java                # Métriques Gateway
│   │       ├── Champs: total_requests, avg_latency, error_rate
│   │       ├── Per route: métriques détaillées
│   │       └── Usage: dashboard monitoring
│   │
│   ├── 📁 util/                                  # Utilitaires Gateway
│   │   │
│   │   ├── 📄 RouteUtils.java                    # Utilitaires routes
│   │   │   ├── extractServiceName(ServerWebExchange): String
│   │   │   ├── buildServiceUri(serviceName): URI
│   │   │   ├── isPublicEndpoint(path): boolean
│   │   │   └── normalizeRouteId(id): String
│   │   │
│   │   ├── 📄 HeaderUtils.java                   # Utilitaires headers
│   │   │   ├── extractJwtToken(exchange): String
│   │   │   ├── addCorrelationId(exchange): void
│   │   │   ├── extractUserInfo(headers): UserInfo
│   │   │   └── sanitizeHeaders(headers): Headers
│   │   │
│   │   ├── 📄 CacheKeyGenerator.java             # Générateur clés cache
│   │   │   ├── generate(method, path, params, user): String
│   │   │   ├── hash: SHA-256
│   │   │   ├── format: "gateway:cache:{hash}"
│   │   │   └── collision: quasi impossible
│   │   │
│   │   └── 📄 MetricsHelper.java                 # Helper métriques
│   │       ├── recordRequest(route, status, duration): void
│   │       ├── incrementCounter(metric, tags): void
│   │       ├── recordHistogram(metric, value, tags): void
│   │       └── getMetricRegistry(): MeterRegistry
│   │
│   ├── 📁 exception/                             # Exceptions spécifiques
│   │   │
│   │   ├── 📄 GatewayException.java              # Exception de base Gateway
│   │   │   ├── Extends: AppException (du common)
│   │   │   ├── Champs additionnels: route, service
│   │   │   └── Usage: exception parent pour Gateway
│   │   │
│   │   ├── 📄 RouteNotFoundException.java        # Route non trouvée
│   │   │   ├── Extends: GatewayException
│   │   │   ├── HTTP Status: 404
│   │   │   └── Message: "No route found for {path}"
│   │   │
│   │   ├── 📄 ServiceUnavailableException.java   # Service indisponible
│   │   │   ├── Extends: GatewayException
│   │   │   ├── HTTP Status: 503
│   │   │   ├── Cause: Circuit Breaker open, timeout, etc.
│   │   │   └── Retry-After: included
│   │   │
│   │   ├── 📄 RateLimitExceededException.java    # Rate limit dépassé
│   │   │   ├── Extends: GatewayException
│   │   │   ├── HTTP Status: 429
│   │   │   ├── Headers: Retry-After, X-RateLimit-*
│   │   │   └── Message: avec info sur limite
│   │   │
│   │   └── 📄 AuthenticationFailedException.java # Échec authentification
│   │       ├── Extends: GatewayException
│   │       ├── HTTP Status: 401
│   │       └── Causes: token invalide, expiré, absent
│   │
│   └── 📁 service/                               # Services métier Gateway
│       │
│       ├── 📄 RouteRefreshService.java           # Service refresh routes
│       │   ├── Méthode: refreshRoutes(): void
│       │   ├── Trigger: manual, scheduled, event-driven
│       │   ├── Source: config server, database, API
│       │   ├── Validation: avant application
│       │   └── Notification: WebSocket vers admins
│       │
│       ├── 📄 CircuitBreakerManagementService.java # Gestion Circuit Breakers
│       │   ├── Méthodes:
│       │   │   ├── getCircuitBreakerStatus(name): Status
│       │   │   ├── forceOpen(name): void (pour maintenance)
│       │   │   ├── forceClose(name): void (après fix)
│       │   │   └── resetMetrics(name): void
│       │   ├── Monitoring: real-time status
│       │   └── Alerting: notifications sur state changes
│       │
│       ├── 📄 MetricsAggregationService.java     # Agrégation métriques
│       │   ├── Méthodes:
│       │   │   ├── getGatewayMetrics(): GatewayMetrics
│       │   │   ├── getRouteMetrics(routeId): RouteMetrics
│       │   │   ├── getServiceMetrics(service): ServiceMetrics
│       │   │   └── getHistoricalMetrics(period): TimeSeries
│       │   ├── Source: Micrometer registry
│       │   ├── Agrégation: per-minute, per-hour, per-day
│       │   └── Storage: Redis (hot), TimescaleDB (cold)
│       │
│       └── 📄 CacheManagementService.java        # Gestion cache
│           ├── Méthodes:
│           │   ├── invalidateCache(pattern): int (count invalidated)
│           │   ├── warmupCache(routes[]): void
│           │   ├── getCacheStats(): CacheStatistics
│           │   └── evictExpiredEntries(): int
│           ├── Patterns: Redis SCAN pour patterns
│           ├── TTL management: différent par type de cache
│           └── Monitoring: hit rate, size, evictions
│
├── 📁 src/main/resources/
│   │
│   ├── 📄 application.yml                        # Configuration principale
│   │   ├── Server:
│   │   │   ├── port: 8080
│   │   │   ├── compression: enabled
│   │   │   └── http2: enabled
│   │   ├── Spring:
│   │   │   ├── application.name: api-gateway
│   │   │   ├── profiles.include: common
│   │   │   ├── cloud.gateway: routes configuration
│   │   │   ├── redis: connection settings
│   │   │   └── security: JWT settings
│   │   ├── Eureka:
│   │   │   ├── client: fetch-registry, register-with-eureka
│   │   │   └── instance: prefer-ip-address, lease-renewal
│   │   ├── Resilience4j:
│   │   │   ├── circuitbreaker: default config
│   │   │   ├── retry: default config
│   │   │   └── ratelimiter: default config
│   │   ├── Management:
│   │   │   ├── endpoints.web.exposure: health,info,metrics,prometheus
│   │   │   ├── metrics.export.prometheus: enabled
│   │   │   └── health.show-details: when-authorized
│   │   └── Logging:
│   │       ├── level.root: INFO
│   │       ├── level.com.yowyob.gateway: DEBUG
│   │       └── pattern: JSON structured
│   │
│   ├── 📄 application-dev.yml                    # Config développement
│   │   ├── Overrides pour dev local
│   │   ├── Services backend: localhost ports
│   │   ├── Redis: localhost:6379
│   │   ├── Eureka: localhost:8761
│   │   ├── Logging: DEBUG level
│   │   └── Security: relaxed (pas de HTTPS requis)
│   │
│   ├── 📄 application-prod.yml                   # Config production
│   │   ├── Services: via Eureka discovery
│   │   ├── Redis: cluster mode
│   │   ├── Security: strict
│   │   ├── Timeouts: production values
│   │   ├── Circuit Breaker: tuned thresholds
│   │   └── Logging: INFO level, structured JSON
│   │
│   ├── 📄 bootstrap.yml                          # Configuration bootstrap
│   │   ├── Spring Cloud Config: si utilisé
│   │   ├── Service Discovery: Eureka/Consul config
│   │   ├── Encryption: jasypt config
│   │   └── Fail-fast: true
│   │
│   ├── 📁 routes/                                # Définitions routes externes
│   │   ├── 📄 search-routes.yml                  # Routes Search Service
│   │   ├── 📄 user-routes.yml                    # Routes User Service
│   │   ├── 📄 geo-routes.yml                     # Routes Geo Service
│   │   └── ... (autres services)
│   │
│   ├── 📁 static/                                # Ressources statiques
│   │   ├── 📄 fallback.json                      # Réponse fallback par défaut
│   │   └── 📄 maintenance.json                   # Message maintenance mode
│   │
│   └── 📄 logback-spring.xml                     # Configuration logging
│       ├── Console Appender: JSON format
│       ├── File Appender: daily rolling
│       ├── Loki Appender: streaming logs
│       ├── Async: true
│       └── MDC: requestId, userId, traceId
│
└── 📁 src/test/java/com/yowyob/gateway/
    │
    ├── 📄 GatewayApplicationTests.java           # Tests application
    │   ├── @SpringBootTest
    │   ├── Context load test
    │   └── Beans creation test
    │
    ├── 📁 config/                                # Tests configuration
    │   ├── 📄 GatewayConfigTest.java
    │   ├── 📄 SecurityConfigTest.java
    │   ├── 📄 CircuitBreakerConfigTest.java
    │   └── 📄 RateLimiterConfigTest.java
    │
    ├── 📁 filter/                                # Tests filtres
    │   ├── 📄 JwtAuthenticationFilterTest.java
    │   │   ├── Test: valid token accepted
    │   │   ├── Test: invalid token rejected
    │   │   ├── Test: expired token rejected
    │   │   ├── Test: missing token handled
    │   │   └── Test: public endpoints bypass
    │   │
    │   ├── 📄 LoggingFilterTest.java
    │   │   ├── Test: logs request/response
    │   │   ├── Test: includes correlation ID
    │   │   ├── Test: includes user info
    │   │   └── Test: proper log level
    │   │
    │   ├── 📄 CacheFilterTest.java
    │   │   ├── Test: cache miss flows through
    │   │   ├── Test: cache hit returns cached
    │   │   ├── Test: cache invalidation works
    │   │   └── Test: only GET cached
    │   │
    │   └── 📄 MetricsFilterTest.java
    │       ├── Test: metrics recorded
    │       ├── Test: correct tags applied
    │       └── Test: histograms populated
    │
    ├── 📁 integration/                           # Tests d'intégration
    │   ├── 📄 RoutingIntegrationTest.java
    │   │   ├── @SpringBootTest(webEnvironment = RANDOM_PORT)
    │   │   ├── Test: route to search service
    │   │   ├── Test: route to user service
    │   │   ├── Test: route not found returns 404
    │   │   └── Test: load balancing works
    │   │
    │   ├── 📄 SecurityIntegrationTest.java
    │   │   ├── Test: authenticated request succeeds
    │   │   ├── Test: unauthenticated request fails
    │   │   ├── Test: wrong role forbidden
    │   │   └── Test: public endpoint accessible
    │   │
    │   ├── 📄 CircuitBreakerIntegrationTest.java
    │   │   ├── @AutoConfigureWireMock (mock backend)
    │   │   ├── Test: CB opens after failures
    │   │   ├── Test: CB stays open
    │   │   ├── Test: CB transitions to half-open
    │   │   ├── Test: CB closes after success
    │   │   └── Test: fallback triggered
    │   │
    │   ├── 📄 RateLimitIntegrationTest.java
    │   │   ├── @AutoConfigureDataRedis
    │   │   ├── Test: requests under limit pass
    │   │   ├── Test: requests over limit rejected
    │   │   ├── Test: rate limit resets
    │   │   └── Test: different users separate limits
    │   │
    │   └── 📄 CacheIntegrationTest.java
    │       ├── Test: response cached
    │       ├── Test: cache hit faster
    │       ├── Test: cache TTL expires
    │       └── Test: cache invalidation
    │
    ├── 📁 performance/                           # Tests de performance
    │   ├── 📄 GatewayLoadTest.java
    │   │   ├── Gatling-based load test
    │   │   ├── Scenario: ramp-up 0→1000 users
    │   │   ├── Duration: 5 minutes
    │   │   ├── Assertions: p95 < 200ms, error < 1%
    │   │   └── Report: HTML + metrics
    │   │
    │   └── 📄 LatencyTest.java
    │       ├── Measure: end-to-end latency│       ├── Percentiles: p50, p95, p99
    │       └── Comparison: with/without cache
    │
    └── 📁 contract/                              # Tests de contrat
        ├── 📄 SearchServiceContractTest.java
        │   ├── Pact Consumer test
        │   ├── Verify: Gateway expectations
        │   └── Generate: pact file
        │
        └── 📄 UserServiceContractTest.java
            ├── Similar for User Service
            └── CI: verify contracts
```

### Description des responsabilités par package

Chaque package du module Gateway a été conçu avec un objectif spécifique et une séparation claire des responsabilités, en respectant rigoureusement les principes SOLID de la programmation orientée objet.

Le package **config** contient toutes les configurations Spring qui définissent comment les différents composants du Gateway doivent être initialisés et câblés ensemble. Ces configurations sont des beans Spring qui sont instanciés au démarrage de l'application. Par exemple, `GatewayConfig` définit programmatiquement toutes les routes (ce qui pourrait aussi être fait en YAML, mais le code offre plus de flexibilité pour les routes dynamiques), `SecurityConfig` configure la chaîne de sécurité reactive avec Spring Security WebFlux, et `CircuitBreakerConfig` établit les paramètres de résilience pour chaque service backend. Ces configurations sont toutes indépendantes les unes des autres, ce qui signifie qu'on peut modifier les paramètres de Circuit Breaker sans affecter la configuration de sécurité, respectant ainsi le principe de responsabilité unique.

Le package **filter** héberge tous les filtres qui modifient les requêtes et les réponses. Ces filtres sont le cœur fonctionnel du Gateway car ils implémentent toutes les fonctionnalités transversales comme l'authentification, le logging, le caching, la limitation de taux, et la gestion des erreurs. Chaque filtre a une responsabilité unique et bien définie. `JwtAuthenticationFilter` ne fait que valider les tokens JWT et ajouter les informations d'utilisateur aux headers, il ne gère pas le logging ou le caching. `LoggingFilter` ne fait que logger les requêtes et réponses, il ne modifie pas le flux de la requête. Cette séparation stricte garantit que chaque filtre peut être testé indépendamment et que les modifications d'un filtre n'affectent pas les autres.

Le package **predicate** contient des prédicats personnalisés qui permettent de router les requêtes selon des critères métier complexes qui vont au-delà des prédicats standards fournis par Spring Cloud Gateway. Par exemple, `AuthenticatedUserPredicate` vérifie si l'utilisateur est authentifié en regardant si le header `X-User-Id` a été ajouté par le `JwtAuthenticationFilter`. `RolePredicate` vérifie si l'utilisateur a un rôle spécifique en examinant le header `X-User-Roles`. Ces prédicats encapsulent la logique de décision de routage et peuvent être réutilisés dans différentes routes sans duplication de code.

Le package **handler** contient des gestionnaires spécialisés pour des cas particuliers comme la gestion des erreurs globales ou le traitement des fallbacks. `GlobalErrorHandler` intercepte toutes les exceptions qui n'ont pas été gérées par les filtres et les convertit en réponses HTTP appropriées avec des messages d'erreur clairs. `FallbackHandler` fournit des réponses de secours lorsque les Circuit Breakers sont ouverts et que les services backend ne sont pas accessibles. Ces handlers séparent la logique de gestion d'erreur de la logique de traitement normale des requêtes.

Le package **dto** définit les structures de données spécifiques au Gateway. Ces DTOs sont différents de ceux du module common car ils représentent des concepts spécifiques au Gateway comme `RouteInfo` qui décrit une route configurée, `CircuitBreakerStatus` qui représente l'état d'un Circuit Breaker, ou `RateLimitInfo` qui contient les informations de limitation de taux. Ces DTOs sont utilisés principalement pour les endpoints d'administration et de monitoring du Gateway.

Le package **util** regroupe des classes utilitaires qui fournissent des fonctions helper spécifiques au Gateway. `RouteUtils` aide à manipuler les routes et extraire des informations, `HeaderUtils` simplifie la manipulation des headers HTTP, `CacheKeyGenerator` génère des clés de cache cohérentes, et `MetricsHelper` facilite l'enregistrement de métriques. Ces utilitaires évitent la duplication de code dans les filtres et handlers.

Le package **exception** définit des exceptions spécifiques au Gateway qui héritent de `AppException` du module common mais ajoutent des informations contextuelles spécifiques au Gateway comme la route concernée ou le service backend impliqué. Ces exceptions permettent une gestion d'erreur plus fine et des messages d'erreur plus informatifs pour les clients.

Le package **service** contient des services métier spécifiques au Gateway qui orchestrent des opérations complexes. `RouteRefreshService` gère le rechargement dynamique des routes sans redémarrer le Gateway, `CircuitBreakerManagementService` permet de contrôler manuellement les Circuit Breakers pour la maintenance, `MetricsAggregationService` agrège les métriques de différentes sources pour fournir une vue consolidée, et `CacheManagementService` gère le cache de manière centralisée. Ces services encapsulent la logique métier complexe et fournissent une API simple pour les contrôleurs.

---

## 🔧 Composants principaux

### Configuration détaillée (Package `config`)

Le package configuration est le point névralgique où tous les composants du Gateway sont configurés et câblés ensemble. Chaque classe de configuration a une responsabilité spécifique et peut être testée indépendamment.

**GatewayConfig.java** est la configuration centrale qui définit toutes les routes du Gateway de manière programmatique. Bien que Spring Cloud Gateway permette de définir les routes en YAML, nous avons choisi l'approche programmatique pour plusieurs raisons importantes. Premièrement, elle offre une meilleure type-safety grâce à TypeScript - si nous faisons une erreur de syntaxe, elle est détectée à la compilation plutôt qu'à l'exécution. Deuxièmement, elle permet de construire des routes de manière dynamique, par exemple en lisant des configurations depuis une base de données ou un service de configuration centralisé. Troisièmement, elle facilite les tests unitaires car on peut instancier directement la configuration dans nos tests.

Voici un exemple concret de route configurée dans cette classe :

```java
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder.routes()
        // Route pour le Search Service
        .route("search_service_route", r -> r
            .path("/api/search/**")
            .filters(f -> f
                .stripPrefix(1) // Enlève "/api" du path
                .circuitBreaker(config -> config
                    .setName("searchCircuitBreaker")
                    .setFallbackUri("forward:/fallback/search"))
                .retry(config -> config
                    .setRetries(3)
                    .setBackoff(Duration.ofSeconds(1), 
                               Duration.ofSeconds(5), 
                               2, 
                               true))
                .requestRateLimiter(config -> config
                    .setRateLimiter(redisRateLimiter())
                    .setKeyResolver(userKeyResolver())))
            .uri("lb://SEARCH-SERVICE")
            .metadata("response-timeout", 5000)
            .metadata("connect-timeout", 2000))
        // Autres routes...
        .build();
}
```

Cette route fait plusieurs choses importantes. Elle matche toutes les requêtes dont le chemin commence par `/api/search/`, applique un filtre qui enlève le préfixe `/api` (donc `/api/search/documents` devient `/search/documents` côté service backend), active un Circuit Breaker nommé `searchCircuitBreaker` qui redirige vers un fallback en cas d'échec, configure une politique de retry avec backoff exponentiel, applique une limitation de taux basée sur l'utilisateur, et enfin route vers le service `SEARCH-SERVICE` via load balancing (le préfixe `lb://` indique que l'adresse sera résolue via Service Discovery).

**SecurityConfig.java** configure la sécurité du Gateway en utilisant Spring Security WebFlux. Cette configuration définit quels endpoints sont publics (accessibles sans authentification) et lesquels nécessitent une authentification. Elle configure également la chaîne de filtres de sécurité qui vérifie les tokens JWT.

Contrairement à une application monolithique traditionnelle où Spring Security s'occupe de tout (génération de tokens, gestion de sessions, etc.), dans notre architecture microservices, le Gateway a une responsabilité plus limitée : il valide simplement les tokens JWT qui ont été générés par le User Service et il s'assure que les utilisateurs ne peuvent pas accéder à des ressources pour lesquelles ils n'ont pas de permissions.

Voici la configuration de sécurité :

```java
@Bean
public SecurityWebFilterChain springSecurityFilterChain(
        ServerHttpSecurity http) {
    return http
        .csrf(csrf -> csrf.disable()) // CSRF désactivé pour API stateless
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeExchange(exchanges -> exchanges
            // Endpoints publics (pas d'authentification requise)
            .pathMatchers(HttpMethod.GET, "/actuator/health").permitAll()
            .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .pathMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
            .pathMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
            .pathMatchers(HttpMethod.GET, "/api/search/**").permitAll()
            .pathMatchers(HttpMethod.GET, "/api/geo/**").permitAll()
            
            // Endpoints nécessitant authentification
            .pathMatchers("/api/users/**").authenticated()
            .pathMatchers("/api/notifications/**").authenticated()
            
            // Endpoints réservés aux admins
            .pathMatchers("/actuator/**").hasRole("ADMIN")
            .pathMatchers("/api/crawler/**").hasRole("ADMIN")
            .pathMatchers("/api/stats/admin/**").hasRole("ADMIN")
            
            // Endpoints réservés aux marchands
            .pathMatchers("/api/shop/merchants/**").hasAnyRole("ADMIN", "MERCHANT")
            
            // Tout le reste nécessite authentification
            .anyExchange().authenticated())
        .httpBasic(httpBasic -> httpBasic.disable())
        .formLogin(formLogin -> formLogin.disable())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .build();
}
```

Cette configuration déclare clairement quels endpoints sont accessibles à qui. Par exemple, n'importe qui peut faire des recherches (`/api/search/**` en GET) sans être authentifié, car nous voulons que les utilisateurs puissent découvrir notre plateforme. En revanche, pour consulter ou modifier son profil (`/api/users/**`), l'utilisateur doit être authentifié. Et pour accéder aux fonctionnalités d'administration comme le contrôle du crawler ou les métriques système, l'utilisateur doit avoir le rôle ADMIN.

**CircuitBreakerConfig.java** configure les Circuit Breakers pour chaque service backend. Un Circuit Breaker surveille les appels vers un service et "ouvre" automatiquement le circuit (arrête temporairement les appels) si le service montre des signes de défaillance. Cela protège à la fois le service défaillant (en lui donnant le temps de se rétablir) et le Gateway (en évitant d'accumuler des requêtes bloquées qui consommeraient des ressources).

Voici comment nous configurons le Circuit Breaker pour le Search Service :

```java
@Bean
public Customizer<ReactiveResilience4JCircuitBreakerFactory> 
        defaultCustomizer() {
    return factory -> {
        factory.configure(builder -> builder
            .circuitBreakerConfig(CircuitBreakerConfig.custom()
                .slidingWindowType(SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10) // Observe les 10 dernières requêtes
                .minimumNumberOfCalls(5) // Minimum avant d'évaluer
                .failureRateThreshold(50.0f) // Ouvre si > 50% d'échecs
                .slowCallRateThreshold(50.0f) // Ouvre si > 50% lentes
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordExceptions(
                    IOException.class, 
                    TimeoutException.class,
                    ServiceUnavailableException.class)
                .ignoreExceptions(
                    BadRequestException.class,
                    NotFoundException.class)
                .build()), 
            "searchCircuitBreaker");
            
        // Callbacks pour observer les changements d'état
        factory.addCircuitBreakerCustomizer(cb -> cb
            .getEventPublisher()
            .onError(event -> log.error(
                "Circuit Breaker error: {}", event))
            .onStateTransition(event -> log.warn(
                "Circuit Breaker state changed: {} -> {}", 
                event.getStateTransition().getFromState(),
                event.getStateTransition().getToState()))
            .onSlowCallRateExceeded(event -> log.warn(
                "Circuit Breaker slow call rate exceeded"))
            .onFailureRateExceeded(event -> log.error(
                "Circuit Breaker failure rate exceeded")), 
            "searchCircuitBreaker");
    };
}
```

Cette configuration définit précisément quand le Circuit Breaker doit s'ouvrir. Il observe une fenêtre glissante des 10 dernières requêtes. Si plus de 50% de ces requêtes échouent (IOException, TimeoutException, ou ServiceUnavailableException), ou si plus de 50% prennent plus de 2 secondes, le Circuit Breaker s'ouvre. Une fois ouvert, il reste dans cet état pendant 10 secondes (donnant au service le temps de se rétablir), puis passe à l'état semi-ouvert où il laisse passer 3 requêtes test. Si ces 3 requêtes réussissent, le Circuit Breaker se referme et revient à la normale. Sinon, il se rouvre pour 10 nouvelles secondes.

Les callbacks enregistrent les changements d'état dans les logs, ce qui est crucial pour le monitoring. Quand un Circuit Breaker s'ouvre, nous voulons immédiatement en être informés pour pouvoir enquêter sur la cause.

**RateLimiterConfig.java** implémente la limitation de taux pour protéger nos services contre les abus. Nous utilisons l'algorithme du Token Bucket stocké dans Redis, ce qui permet une limitation distribuée : même si nous avons plusieurs instances du Gateway, elles partagent toutes les mêmes compteurs dans Redis.

Voici l'implémentation :

```java
@Bean
public RedisRateLimiter redisRateLimiter(
        ReactiveRedisTemplate<String, String> redisTemplate) {
    return new RedisRateLimiter(
        10,  // replenishRate: 10 tokens par seconde
        20   // burstCapacity: maximum 20 tokens
    );
}

@Bean
public KeyResolver userKeyResolver() {
    return exchange -> {
        // Pour les utilisateurs authentifiés, limiter par userId
        String userId = exchange.getRequest()
            .getHeaders()
            .getFirst("X-User-Id");
            
        if (userId != null && !userId.isEmpty()) {
            return Mono.just(userId);
        }
        
        // Pour les utilisateurs non authentifiés, limiter par IP
        String ipAddress = exchange.getRequest()
            .getRemoteAddress()
            .getAddress()
            .getHostAddress();
            
        return Mono.just(ipAddress);
    };
}

@Bean
public RateLimiterGatewayFilterFactory rateLimiterGatewayFilterFactory(
        RedisRateLimiter redisRateLimiter,
        KeyResolver keyResolver) {
    return new RateLimiterGatewayFilterFactory(
        redisRateLimiter, 
        keyResolver,
        // Custom response pour rate limit exceeded
        (exchange, status) -> {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add(
                "Retry-After", "10");
            exchange.getResponse().getHeaders().add(
                "X-RateLimit-Limit", "100");
            exchange.getResponse().getHeaders().add(
                "X-RateLimit-Remaining", "0");
                
            return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse()
                    .bufferFactory()
                    .wrap("""
                        {
                          "error": "rate_limit_exceeded",
                          "message": "Too many requests. Please wait 10 seconds.",
                          "limit": 100,
                          "remaining": 0,
                          "reset_at": "%s"
                        }
                        """.formatted(Instant.now()
                            .plusSeconds(10)
                            .toString())
                        .getBytes())));
        });
}
```

Cette configuration crée un seau de tokens avec une capacité maximale de 20 tokens. Chaque requête consomme 1 token. Le seau se remplit automatiquement à un rythme de 10 tokens par seconde. Cela signifie qu'un utilisateur peut faire jusqu'à 20 requêtes d'un coup (burst), puis est limité à 10 requêtes par seconde en régime permanent. Si un utilisateur essaie de faire plus de requêtes, elles sont rejetées avec un code HTTP 429 et un message clair indiquant combien de temps attendre.

Le `KeyResolver` détermine comment identifier les utilisateurs pour la limitation. Pour les utilisateurs authentifiés (qui ont un header `X-User-Id`), nous limitons par userId, ce qui signifie que chaque utilisateur a sa propre limite indépendante. Pour les utilisateurs non authentifiés, nous limitons par adresse IP. Cela empêche qu'un utilisateur non authentifié monopolise la capacité du système, mais permet aussi à plusieurs utilisateurs derrière le même NAT (comme dans une entreprise) d'avoir chacun leur quota.

### Filtres détaillés (Package `filter`)

Les filtres sont le cœur fonctionnel du Gateway. Ils s'exécutent dans un ordre déterminé et peuvent modifier les requêtes avant qu'elles n'atteignent les services backend, ou modifier les réponses avant qu'elles ne soient renvoyées aux clients.

**JwtAuthenticationFilter.java** est probablement le filtre le plus critique car il gère toute l'authentification. Ce filtre s'exécute très tôt dans la chaîne (order = -100) pour bloquer rapidement les requêtes non autorisées.

Voici son implémentation complète :

```java
@Component
@Order(-100) // S'exécute tôt dans la chaîne
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter {
    
    private final JwtService jwtService; // Depuis yowyob-common
    private final Set<String> publicPaths; // Endpoints publics
    
    public JwtAuthenticationFilter(
            JwtService jwtService,
            @Value("${app.security.public-paths}") Set<String> publicPaths) {
        this.jwtService = jwtService;
        this.publicPaths = publicPaths;
    }
    
    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange, 
            GatewayFilterChain chain) {
        
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        
        // Bypass pour les endpoints publics
        if (isPublicPath(path)) {
            log.debug("Public path accessed: {}", path);
            return chain.filter(exchange);
        }
        
        // Extraction du token depuis le header Authorization
        String authHeader = request.getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);
            
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", 
                path);
            return onError(exchange, "Missing or invalid authorization token", 
                HttpStatus.UNAUTHORIZED);
        }
        
        String token = authHeader.substring(7); // Enlève "Bearer "
        
        try {
            // Validation du token avec JwtService du module common
            if (!jwtService.validateToken(token)) {
                log.warn("Invalid JWT token for path: {}", path);
                return onError(exchange, "Invalid or expired token", 
                    HttpStatus.UNAUTHORIZED);
            }
            
            // Extraction des informations utilisateur
            String userId = jwtService.extractUserId(token);
            Set<Role> roles = jwtService.extractRoles(token);
            
            log.debug("Authenticated user: {} with roles: {}", 
                userId, roles);
            
            // Ajout des headers pour les services backend
            ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-User-Id", userId)
                .header("X-User-Roles", 
                    roles.stream()
                        .map(Role::name)
                        .collect(Collectors.joining(",")))
                .header("X-Authenticated", "true")
                .build();
            
            // Propagation de la requête modifiée
            return chain.filter(
                exchange.mutate()
                    .request(mutatedRequest)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error validating JWT token", e);
            return onError(exchange, "Authentication failed", 
                HttpStatus.UNAUTHORIZED);
        }
    }
    
    private boolean isPublicPath(String path) {
        return publicPaths.stream()
            .anyMatch(pattern -> PathMatcher.match(pattern, path));
    }
    
    private Mono<Void> onError(
            ServerWebExchange exchange, 
            String message, 
            HttpStatus status) {
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(exchange.getRequest().getPath().value())
            .timestamp(Instant.now())
            .build();
        
        byte[] bytes = errorResponse.toJson().getBytes();
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        
        return response.writeWith(Mono.just(buffer));
    }
}
```

Ce filtre fait plusieurs choses importantes. D'abord, il vérifie si le chemin demandé est dans la liste des endpoints publics. Si oui, il laisse passer la requête sans vérification. Sinon, il extrait le token JWT du header `Authorization`, le valide en utilisant le `JwtService` du module common (qui vérifie la signature cryptographique et l'expiration), extrait les informations utilisateur (ID et rôles), puis ajoute ces informations sous forme de headers (`X-User-Id`, `X-User-Roles`) qui seront transmis aux services backend. Cela permet aux services backend de savoir qui est l'utilisateur sans avoir à re-valider le token eux-mêmes.

Si la validation échoue à n'importe quelle étape (token absent, invalide, expiré), le filtre retourne immédiatement une erreur HTTP 401 avec un message clair, empêchant la requête d'atteindre les services backend. C'est ce qu'on appelle le principe "fail-fast" : échouer rapidement et clairement plutôt que de laisser la requête se propager dans le système pour échouer plus tard de manière moins prévisible.

**LoggingFilter.java** enregistre toutes les requêtes et réponses qui passent par le Gateway. Ces logs sont essentiels pour le debugging, l'audit et le monitoring. Le filtre s'exécute en premier (order = Integer.MIN_VALUE) pour capturer toutes les requêtes, même celles qui échouent dans d'autres filtres.

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Premier à s'exécuter
@Slf4j
public class LoggingFilter implements GlobalFilter {
    
    private final MeterRegistry meterRegistry;
    
    public LoggingFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange, 
            GatewayFilterChain chain) {
        
        ServerHttpRequest request = exchange.getRequest();
        
        // Génération d'un ID de requête unique pour le tracing
        String requestId = UUID.randomUUID().toString();
        
        // Ajout de l'ID dans les headers pour propagation
        ServerHttpRequest mutatedRequest = request.mutate()
            .header("X-Request-Id", requestId)
            .build();
        
        // Enrichissement du MDC pour logs structurés
        MDC.put("requestId", requestId);
        MDC.put("method", request.getMethod().name());
        MDC.put("path", request.getPath().value());
        MDC.put("remoteAddress", 
            request.getRemoteAddress().getAddress().getHostAddress());
        
        // Extraction userId si présent (après JwtAuthenticationFilter)
        String userId = request.getHeaders().getFirst("X-User-Id");
        if (userId != null) {
            MDC.put("userId", userId);
        }
        
        long startTime = System.nanoTime();
        
        log.info("Incoming request: {} {} from {}",
            request.getMethod(),
            request.getPath(),
            request.getRemoteAddress());
        
        return chain.filter(exchange.mutate()
                .request(mutatedRequest)
                .build())
            .doOnSuccess(aVoid -> {
                long duration = System.nanoTime() - startTime;
                ServerHttpResponse response = exchange.getResponse();
                HttpStatus status = response.getStatusCode();
                
                log.info("Request completed: {} {} -> {} in {}ms",
                    request.getMethod(),
                    request.getPath(),
                    status != null ? status.value() : "unknown",
                    duration / 1_000_000); // Conversion nano -> milli
                
                // Enregistrement des métriques
                meterRegistry.counter("gateway.requests.total",
                    "method", request.getMethod().name(),
                    "path", request.getPath().value(),
                    "status", status != null ? 
                        String.valueOf(status.value()) : "unknown"
                ).increment();
                
                meterRegistry.timer("gateway.request.duration",
                    "method", request.getMethod().name(),
                    "path", request.getPath().value()
                ).record(duration, TimeUnit.NANOSECONDS);
            })
            .doOnError(error -> {
                long duration = System.nanoTime() - startTime;
                
                log.error("Request failed: {} {} after {}ms - {}",
                    request.getMethod(),
                    request.getPath(),
                    duration / 1_000_000,
                    error.getMessage(),
                    error);
                
                meterRegistry.counter("gateway.requests.errors",
                    "method", request.getMethod().name(),
                    "path", request.getPath().value(),
                    "exception", error.getClass().getSimpleName()
                ).increment();
            })
            .doFinally(signalType -> {
                // Nettoyage du MDC
                MDC.clear();
            });
    }
}
```

Ce filtre génère un ID unique pour chaque requête et l'ajoute au MDC (Mapped Diagnostic Context), ce qui permet d'inclure automatiquement cet ID dans tous les logs générés pendant le traitement de cette requête. Cela facilite grandement le debugging car on peut suivre une requête particulière à travers tous les logs, même si d'autres requêtes sont traitées en parallèle.

Le filtre mesure également le temps de traitement de chaque requête et enregistre des métriques dans Prometheus (via Micrometer). Ces métriques incluent le nombre total de requêtes (avec des tags pour la méthode, le path et le status), et la distribution des temps de réponse. Ces données sont ensuite visualisées dans Grafana pour le monitoring en temps réel.

**CacheFilter.java** implémente un système de cache intelligent qui stocke les réponses fréquemment demandées dans Redis pour améliorer les performances et réduire la charge sur les services backend.

```java
@Component
@Slf4j
public class CacheFilter implements GatewayFilter {
    
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final CacheKeyGenerator cacheKeyGenerator;
    private final ObjectMapper objectMapper;
    
    @Value("${app.cache.ttl:300}") // TTL par défaut: 5 minutes
    private int cacheTtl;
    
    public CacheFilter(
            ReactiveRedisTemplate<String, String> redisTemplate,
            CacheKeyGenerator cacheKeyGenerator,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.cacheKeyGenerator = cacheKeyGenerator;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange, 
            GatewayFilterChain chain) {
        
        ServerHttpRequest request = exchange.getRequest();
        
        // Cache uniquement les requêtes GET
        if (!request.getMethod().equals(HttpMethod.GET)) {
            return chain.filter(exchange);
        }
        
        // Génération de la clé de cache
        String cacheKey = cacheKeyGenerator.generate(
            request.getMethod().name(),
            request.getPath().value(),
            request.getQueryParams(),
            request.getHeaders().getFirst("X-User-Id")
        );
        
        // Tentative de récupération depuis le cache
        return redisTemplate.opsForValue()
            .get(cacheKey)
            .flatMap(cachedResponse -> {
                log.debug("Cache HIT for key: {}", cacheKey);
                
                // Désérialisation de la réponse cachée
                try {
                    CachedResponse cached = objectMapper.readValue(
                        cachedResponse, CachedResponse.class);
                    
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(
                        HttpStatus.valueOf(cached.getStatus()));
                    response.getHeaders().addAll(cached.getHeaders());
                    response.getHeaders().add("X-Cache-Status", "HIT");
                    
                    byte[] body = cached.getBody().getBytes();
                    DataBuffer buffer = response.bufferFactory().wrap(body);
                    
                    return response.writeWith(Mono.just(buffer));
                    
                } catch (Exception e) {log.error("Error deserializing cached response", e);
                    // En cas d'erreur, on passe à la requête normale
                    return chain.filter(exchange);
                }
            })
            .switchIfEmpty(Mono.defer(() -> {
                log.debug("Cache MISS for key: {}", cacheKey);
                
                // Cache miss: on exécute la requête et on cache la réponse
                return cacheResponse(exchange, chain, cacheKey);
            }));
    }
    
    private Mono<Void> cacheResponse(
            ServerWebExchange exchange,
            GatewayFilterChain chain,
            String cacheKey) {
        
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();
        
        // Wrapper pour capturer la réponse
        ServerHttpResponseDecorator decoratedResponse = 
            new ServerHttpResponseDecorator(response) {
            
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    
                    return super.writeWith(fluxBody.buffer()
                        .map(dataBuffers -> {
                            // Assemblage des buffers
                            DataBuffer joinedBuffer = bufferFactory.join(dataBuffers);
                            byte[] content = new byte[joinedBuffer.readableByteCount()];
                            joinedBuffer.read(content);
                            DataBufferUtils.release(joinedBuffer);
                            
                            // Mise en cache si succès (2xx)
                            HttpStatus status = getStatusCode();
                            if (status != null && status.is2xxSuccessful()) {
                                CachedResponse cached = CachedResponse.builder()
                                    .status(status.value())
                                    .headers(getHeaders())
                                    .body(new String(content))
                                    .timestamp(Instant.now())
                                    .build();
                                
                                try {
                                    String serialized = objectMapper
                                        .writeValueAsString(cached);
                                    
                                    // Sauvegarde asynchrone dans Redis
                                    redisTemplate.opsForValue()
                                        .set(cacheKey, serialized, 
                                            Duration.ofSeconds(cacheTtl))
                                        .subscribe(
                                            success -> log.debug(
                                                "Cached response for key: {}", 
                                                cacheKey),
                                            error -> log.error(
                                                "Error caching response", error)
                                        );
                                } catch (Exception e) {
                                    log.error("Error serializing response for cache", e);
                                }
                            }
                            
                            // Retour du buffer pour écriture
                            return bufferFactory.wrap(content);
                        }));
                }
                return super.writeWith(body);
            }
        };
        
        return chain.filter(exchange.mutate()
            .response(decoratedResponse)
            .build());
    }
    
    @Data
    @Builder
    private static class CachedResponse {
        private int status;
        private HttpHeaders headers;
        private String body;
        private Instant timestamp;
    }
}
```

Ce filtre vérifie d'abord si la requête est un GET (seules les requêtes GET sont cachées car les autres méthodes ont des effets de bord). Il génère ensuite une clé de cache basée sur la méthode, le chemin, les paramètres de requête et l'utilisateur (si authentifié). Il essaie de récupérer une réponse cachée depuis Redis. Si trouvée (cache HIT), il retourne cette réponse directement sans appeler le service backend, ce qui est extrêmement rapide. Si non trouvée (cache MISS), il laisse la requête se propager au service backend, capture la réponse, et si c'est un succès (status 2xx), la met en cache dans Redis avec un TTL configurable.

Cette stratégie de cache réduit considérablement la charge sur les services backend pour les requêtes populaires. Par exemple, une recherche pour "restaurants Yaoundé" pourrait être faite des centaines de fois par jour. Au lieu que le Search Service ait à interroger Elasticsearch à chaque fois, le Gateway peut retourner la réponse mise en cache, réduisant le temps de réponse de peut-être 200ms à 5ms et économisant les ressources du Search Service pour traiter d'autres requêtes.

---

## 🔐 Sécurité et authentification

### Architecture de sécurité globale

La sécurité dans YowYob Search suit une approche en couches où chaque niveau ajoute une protection supplémentaire. Le Gateway, en tant que point d'entrée unique, constitue la première ligne de défense et joue un rôle critique dans cette architecture.

Au niveau le plus externe, nous avons le **TLS/SSL** qui chiffre toutes les communications entre les clients et le Gateway. Tous les clients (browsers, applications mobiles) communiquent avec le Gateway via HTTPS sur le port 443. Le certificat SSL est géré par Let's Encrypt avec renouvellement automatique, et la terminaison TLS se fait au niveau de l'Ingress NGINX, avant même d'atteindre le Gateway. Cela signifie que le Gateway reçoit du trafic HTTP non chiffré, mais uniquement depuis l'intérieur du cluster Kubernetes, qui est un réseau privé sécurisé.

La deuxième couche est l'**authentification JWT**. Tous les clients qui veulent accéder à des ressources protégées doivent d'abord s'authentifier auprès du User Service en fournissant leurs credentials (email/password). Le User Service valide ces credentials, et s'ils sont corrects, génère un token JWT signé avec notre clé privée RSA. Ce token contient les informations d'identité de l'utilisateur (userId, roles, expiration) et est retourné au client.

Pour toutes les requêtes ultérieures, le client inclut ce token dans le header `Authorization: Bearer <token>`. Le Gateway extrait ce token, valide sa signature avec notre clé publique RSA (ce qui prouve qu'il a bien été émis par nous et n'a pas été modifié), vérifie qu'il n'est pas expiré, et extrait les informations qu'il contient. Si tout est valide, le Gateway ajoute des headers `X-User-Id` et `X-User-Roles` à la requête avant de la transmettre au service backend. Les services backend peuvent faire confiance à ces headers car ils savent qu'ils ont été ajoutés par le Gateway après validation du JWT.

La troisième couche est l'**autorisation basée sur les rôles**. Même avec un token JWT valide, un utilisateur ne peut pas accéder à n'importe quelle ressource. Le Gateway vérifie que l'utilisateur a les rôles nécessaires pour accéder à l'endpoint demandé. Par exemple, un utilisateur avec le rôle USER peut consulter son propre profil, mais ne peut pas accéder aux endpoints d'administration. Un utilisateur avec le rôle MERCHANT peut gérer ses produits, mais ne peut pas administrer le crawler. Seuls les utilisateurs avec le rôle ADMIN ont accès aux fonctionnalités d'administration complètes.

La quatrième couche est la **limitation de taux (Rate Limiting)** qui protège contre les abus et les attaques par déni de service. Même un utilisateur authentifié ne peut pas faire un nombre illimité de requêtes. Le Gateway impose des limites strictes : 100 requêtes par minute pour les endpoints de lecture, 30 pour les endpoints d'écriture, 5 pour le login (pour empêcher les attaques par force brute sur les mots de passe). Ces limites sont appliquées via Redis qui maintient des compteurs pour chaque utilisateur ou adresse IP.

La cinquième couche est le **filtrage et la validation des entrées**. Avant de transmettre une requête à un service backend, le Gateway peut valider que les paramètres sont dans les plages attendues, que les headers sont conformes, et que le corps de la requête ne contient pas de contenu malveillant. Par exemple, il peut rejeter des requêtes avec des paramètres excessivement longs qui pourraient être des tentatives d'attaque par buffer overflow, ou des requêtes avec des caractères suspects qui pourraient être des tentatives d'injection SQL.

### Flux d'authentification détaillé

Comprenons maintenant le flux complet d'authentification depuis le moment où un utilisateur se connecte jusqu'à ce qu'il puisse accéder aux ressources protégées.

**Étape 1 : Login initial**

L'utilisateur ouvre l'application YowYob dans son navigateur et clique sur "Se connecter". L'application frontend (Next.js) affiche un formulaire où l'utilisateur saisit son email et son mot de passe. Quand il soumet le formulaire, l'application envoie une requête POST au Gateway :

```
POST https://api.yowyob.com/api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

Cette requête arrive au Gateway qui la route vers le User Service selon la configuration de route pour `/api/auth/**`. Le Gateway ne valide pas le JWT ici car `/api/auth/login` est dans la liste des endpoints publics - il serait absurde d'exiger qu'un utilisateur soit déjà authentifié pour se connecter!

**Étape 2 : Validation des credentials**

Le User Service reçoit la requête, extrait l'email et le mot de passe, cherche l'utilisateur dans PostgreSQL par email. Si l'utilisateur est trouvé, le service utilise BCrypt (via le `PasswordService` du module common) pour vérifier que le mot de passe fourni correspond au hash stocké. BCrypt est un algorithme de hachage lent conçu spécifiquement pour les mots de passe, ce qui le rend résistant aux attaques par force brute.

Si le mot de passe est correct, le User Service génère deux tokens JWT :
- Un **access token** valide pendant 15 minutes, utilisé pour les requêtes API
- Un **refresh token** valide pendant 7 jours, utilisé uniquement pour obtenir de nouveaux access tokens

Le User Service retourne ces tokens au Gateway, qui les retourne au client :

```json
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 900,
    "tokenType": "Bearer",
    "user": {
      "id": "user_123",
      "email": "user@example.com",
      "firstName": "Jean",
      "roles": ["USER"]
    }
  }
}
```

**Étape 3 : Stockage des tokens côté client**

L'application frontend stocke ces tokens de manière sécurisée. Pour les applications web, nous utilisons `httpOnly` cookies qui ne sont pas accessibles via JavaScript, ce qui protège contre les attaques XSS. Pour les applications mobiles PWA, nous utilisons le secure storage natif de l'appareil.

**Étape 4 : Utilisation de l'access token**

Pour toutes les requêtes ultérieures vers des endpoints protégés, le client inclut l'access token dans le header Authorization :

```
GET https://api.yowyob.com/api/users/profile
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

Le Gateway intercepte cette requête dans le `JwtAuthenticationFilter`. Le filtre :
1. Extrait le token du header Authorization
2. Valide la signature avec la clé publique RSA
3. Vérifie que le token n'est pas expiré
4. Extrait les claims (userId, roles)
5. Ajoute les headers X-User-Id et X-User-Roles
6. Laisse passer la requête

Le User Service reçoit la requête avec les headers ajoutés et peut faire confiance à l'identité de l'utilisateur sans re-valider le JWT.

**Étape 5 : Refresh du token expiré**

Après 15 minutes, l'access token expire. La prochaine requête avec ce token sera rejetée par le Gateway avec une erreur 401. L'application frontend détecte cette erreur et utilise automatiquement le refresh token pour obtenir un nouvel access token :

```
POST https://api.yowyob.com/api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Le User Service valide le refresh token, et s'il est valide et non expiré, génère un nouvel access token et le retourne. L'application peut alors réessayer la requête originale avec le nouveau token. Ce processus est transparent pour l'utilisateur qui ne voit même pas qu'un refresh a eu lieu.

**Étape 6 : Révocation et déconnexion**

Quand l'utilisateur se déconnecte ou si un token doit être révoqué (par exemple, si un utilisateur signale un appareil volé), le User Service ajoute l'identifiant du token (claim `jti`) à une blacklist dans Redis avec un TTL égal à la durée de vie restante du token. Le `JwtAuthenticationFilter` vérifie cette blacklist lors de la validation de chaque token. Si le token est blacklisté, il est rejeté même s'il est techniquement valide et non expiré.

### Gestion des secrets et des clés

La sécurité de tout notre système d'authentification repose sur la protection des clés cryptographiques. Nous utilisons plusieurs types de clés, chacune avec son propre cycle de vie et ses propres mécanismes de protection.

**Clés RSA pour JWT** : Nous utilisons une paire de clés RSA-2048 pour signer et vérifier les tokens JWT. La clé privée est utilisée uniquement par le User Service pour signer les nouveaux tokens. Elle ne quitte jamais ce service et n'est jamais transmise sur le réseau. La clé publique est utilisée par le Gateway (et potentiellement d'autres services) pour vérifier les signatures. Elle peut être distribuée librement car elle ne permet que de vérifier, jamais de créer des tokens.

Ces clés sont générées lors de l'initialisation du système avec le script `generate-keys.sh` qui utilise OpenSSL :

```bash
# Génération de la clé privée RSA-2048
openssl genrsa -out private.pem 2048

# Extraction de la clé publique
openssl rsa -in private.pem -pubout -out public.pem

# Conversion au format PKCS8 pour Java
openssl pkcs8 -topk8 -inform PEM -outform PEM -in private.pem -out private_pkcs8.pem -nocrypt
```

Dans Kubernetes, ces clés sont stockées dans des Secrets :

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: jwt-keys
  namespace: yowyob
type: Opaque
data:
  private.pem: <base64-encoded-private-key>
  public.pem: <base64-encoded-public-key>
```

Les secrets Kubernetes sont chiffrés au repos avec la clé de chiffrement du cluster et sont montés comme des volumes dans les pods qui en ont besoin. Seul le User Service a accès à `private.pem`, tandis que le Gateway et d'autres services peuvent accéder à `public.pem`.

**Rotation des clés** : Pour maintenir la sécurité à long terme, nous rotons les clés JWT périodiquement (tous les 6 mois). Le processus de rotation est conçu pour être sans interruption de service :

1. Génération d'une nouvelle paire de clés avec un identifiant de version différent
2. Le User Service commence à inclure le nouvel identifiant (`kid` claim) dans tous les nouveaux tokens et les signe avec la nouvelle clé privée
3. Le Gateway maintient les deux clés publiques (ancienne et nouvelle) et vérifie les tokens avec la bonne clé selon leur `kid`
4. Après la durée de vie maximale d'un token (7 jours pour les refresh tokens), tous les tokens signés avec l'ancienne clé ont expiré
5. L'ancienne clé publique peut être retirée en toute sécurité

**Mots de passe Redis** : Redis est utilisé pour le cache et le rate limiting. Bien que les données dans Redis ne soient généralement pas sensibles (ce sont principalement des réponses API mises en cache), nous utilisons quand même un mot de passe fort pour empêcher les accès non autorisés. Ce mot de passe est également stocké dans un Secret Kubernetes et injecté comme variable d'environnement dans les pods qui en ont besoin.

**Secrets d'environnement** : Toutes les informations sensibles (mots de passe de base de données, clés API externes, tokens d'accès) sont stockées dans des Secrets Kubernetes, jamais en clair dans le code source ou les fichiers de configuration. En développement local, nous utilisons un fichier `.env.local` qui n'est jamais commité dans Git (il est dans `.gitignore`). Les développeurs reçoivent ce fichier de manière sécurisée via des canaux privés.

---

## 🛡 Résilience et tolérance aux pannes

### Pourquoi la résilience est critique

Dans une architecture microservices distribuée comme YowYob, les pannes sont inévitables. Un service peut crasher, un réseau peut être congestionné, une base de données peut être temporairement surchargée. Ce qui distingue un système robuste d'un système fragile n'est pas l'absence de pannes (impossible), mais la capacité à détecter, isoler et récupérer des pannes rapidement et gracieusement.

Sans mécanismes de résilience, une panne locale peut facilement se propager et devenir une panne globale. Imaginez ce scénario catastrophe : le Search Service devient lent à cause d'un problème avec Elasticsearch. Les requêtes vers ce service commencent à prendre 30 secondes au lieu de 200ms. Le Gateway continue d'envoyer des requêtes, qui s'accumulent, chacune bloquant un thread pendant 30 secondes. Rapidement, tous les threads du Gateway sont bloqués en attente du Search Service. Le Gateway ne peut plus traiter aucune requête, même celles vers d'autres services parfaitement fonctionnels comme le User Service ou le Geo Service. Toute la plateforme est down à cause d'un problème dans un seul service.

C'est exactement ce que nous évitons avec nos mécanismes de résilience : Circuit Breakers, Retries, Timeouts et Fallbacks.

### Circuit Breaker en détail

Le Circuit Breaker est emprunté au domaine électrique. Dans votre maison, si un appareil défectueux provoque un court-circuit, le disjoncteur "saute" automatiquement pour protéger l'installation. Notre Circuit Breaker logiciel fonctionne de manière similaire mais pour protéger les services au lieu des circuits électriques.

Le Circuit Breaker existe dans trois états : **CLOSED**, **OPEN** et **HALF_OPEN**.

**État CLOSED (fermé)** : C'est l'état normal. Le Circuit Breaker laisse passer toutes les requêtes vers le service backend. Il surveille activement le taux de succès/échec des requêtes. Si tout va bien, il reste dans cet état indéfiniment.

**Transition vers OPEN** : Le Circuit Breaker passe à l'état OPEN quand il détecte que le service backend a des problèmes. Nous configurons deux seuils : le taux d'échec (failure rate) et le taux d'appels lents (slow call rate). Si plus de 50% des 10 dernières requêtes échouent (IOException, TimeoutException, etc.), OU si plus de 50% des requêtes prennent plus de 2 secondes, le Circuit Breaker s'ouvre.

**État OPEN (ouvert)** : Dans cet état, le Circuit Breaker bloque immédiatement toutes les requêtes vers le service backend sans même essayer de les envoyer. À la place, il déclenche le fallback configuré ou retourne immédiatement une erreur. Cet état protège le service backend en lui donnant le temps de se rétablir sans être bombardé de nouvelles requêtes. Le Circuit Breaker reste ouvert pendant une durée configurable (10 secondes dans notre cas).

**Transition vers HALF_OPEN** : Après la durée d'attente, le Circuit Breaker ne se referme pas immédiatement. Il passe d'abord à l'état HALF_OPEN (semi-ouvert) pour tester si le service backend s'est rétabli. Dans cet état, il laisse passer un nombre limité de requêtes test (3 dans notre configuration). Si toutes ces requêtes test réussissent, le Circuit Breaker considère que le service est rétabli et retourne à l'état CLOSED. Si au moins une échoue, il retourne à l'état OPEN pour 10 nouvelles secondes.

Voici un chronogramme illustrant une transition complète :

```
Temps    État          Requêtes    Observation
---------|-------------|-----------|---------------------------
t0       CLOSED        ✓ ✓ ✓ ✓ ✓  | Tout va bien
t1       CLOSED        ✓ ✓ ✗ ✗ ✗  | Début des problèmes
t2       CLOSED        ✗ ✗ ✗ ✗ ✗  | 70% échecs sur 10 dernières
t3       OPEN          ⊗ ⊗ ⊗ ⊗ ⊗  | CB s'ouvre, bloque tout
t4       OPEN          ⊗ ⊗ ⊗ ⊗ ⊗  | Toujours bloqué
t5       OPEN          ⊗ ⊗ ⊗ ⊗ ⊗  | Service se rétablit (on ne sait pas encore)
t6       OPEN          ⊗ ⊗ ⊗ ⊗ ⊗  | Fin de waitDuration (10s)
t7       HALF_OPEN     ? ? ?      | Test avec 3 requêtes
t8       HALF_OPEN     ✓ ✓ ✓      | Les 3 réussissent!
t9       CLOSED        ✓ ✓ ✓ ✓ ✓  | CB se referme, tout normal

Légende:
✓ = Requête réussie
✗ = Requête échouée
⊗ = Requête bloquée par CB
? = Requête test
```

### Stratégies de retry intelligentes

Les retries (nouvelles tentatives) sont essentiels pour gérer les pannes transitoires - des échecs temporaires qui se résolvent d'eux-mêmes si on réessaie. Par exemple, un timeout réseau momentané, une base de données temporairement surchargée qui refuse une connexion, ou un service qui redémarre.

Mais les retries doivent être intelligents. Réessayer aveuglément peut aggraver les problèmes. Si un service est surchargé, lui envoyer encore plus de requêtes (via des retries) va l'enfoncer encore plus. C'est pourquoi nous implémentons plusieurs garde-fous :

**Retry uniquement sur les erreurs appropriées** : Nous réessayons uniquement sur les erreurs qui sont potentiellement transitoires : IOException (erreur réseau), TimeoutException (timeout), ou 503 Service Unavailable (service temporairement indisponible). Nous ne réessayons PAS sur les erreurs 4xx qui indiquent un problème avec la requête elle-même (400 Bad Request, 404 Not Found, 401 Unauthorized, etc.). Ces erreurs ne vont pas se résoudre en réessayant - la requête est fondamentalement problématique.

**Backoff exponentiel** : Au lieu de réessayer immédiatement, nous attendons un délai qui augmente exponentiellement entre chaque tentative. Première tentative immédiate, deuxième après 1 seconde, troisième après 2 secondes, quatrième après 4 secondes. Cela donne au service backend le temps de se rétablir entre les tentatives.

**Jitter (aléatoire)** : Si tous les clients réessayent exactement en même temps avec le même backoff, ils vont tous frapper le service backend simultanément, créant des vagues de charge. Pour éviter cela, nous ajoutons un facteur aléatoire (jitter) au délai de backoff. Au lieu d'attendre exactement 2 secondes, on attend entre 1 et 3 secondes aléatoirement. Cela répartit les retries dans le temps.

**Limite du nombre de retries** : Nous limitons à 3 tentatives maximum. Après 3 échecs, on abandonne et on retourne l'erreur au client. Continuer à réessayer indéfiniment bloquerait les ressources et empêcherait de traiter d'autres requêtes.

**Retry uniquement sur les méthodes idempotentes** : C'est critique. Les méthodes GET, HEAD, OPTIONS, PUT et DELETE sont idempotentes - les exécuter plusieurs fois a le même effet que les exécuter une fois. On peut donc les réessayer sans risque. Mais POST n'est pas idempotent - créer une ressource deux fois crée deux ressources. Nous ne réessayons jamais automatiquement les POST pour éviter les doublons. Si un POST échoue, c'est au client de décider de réessayer ou non.

Voici notre configuration de retry :

```java
@Bean
public RetrySpec retrySpec() {
    return RetrySpec.backoff(3, Duration.ofSeconds(1))
        .maxBackoff(Duration.ofSeconds(5))
        .jitter(0.5) // 50% jitter
        .filter(throwable -> 
            throwable instanceof IOException ||
            throwable instanceof TimeoutException ||
            (throwable instanceof HttpStatusCodeException && 
             ((HttpStatusCodeException) throwable).getStatusCode() == 
                HttpStatus.SERVICE_UNAVAILABLE))
        .doBeforeRetry(signal -> 
            log.warn("Retrying request (attempt {}/3) after error: {}", 
                signal.totalRetries() + 1, 
                signal.failure().getMessage()));
}
```

### Timeouts appropriés

Les timeouts sont notre dernière ligne de défense contre les services qui ne répondent jamais. Sans timeout, une requête pourrait bloquer indéfiniment, consommant des ressources précieuses.

Nous configurons plusieurs niveaux de timeout :

**Connection timeout (2 secondes)** : Le temps maximum pour établir une connexion TCP avec le service backend. Si le service ne répond même pas pour accepter la connexion après 2 secondes, on abandonne. Un timeout de connexion élevé indique généralement que le service est complètement down ou inaccessible.

**Read timeout (10 secondes)** : Le temps maximum pour recevoir une réponse après avoir envoyé la requête. Si le service accepte la connexion mais ne répond pas dans les 10 secondes, on abandonne. C'est notre timeout principal pour la plupart des opérations.

**Write timeout (5 secondes)** : Le temps maximum pour envoyer la requête au service. Rarement atteint sauf si on envoie de très gros payload.

**Total timeout (15 secondes)** : Le temps total maximum pour l'ensemble de l'opération, incluant tous les retries. Même avec 3 retries, si on dépasse 15 secondes au total, on abandonne.

Ces valeurs sont des compromis. Trop courtes, on va timeout sur des opérations légitimes qui sont juste un peu lentes. Trop longues, on va bloquer des ressources trop longtemps sur des services qui ne répondent pas. Nous avons choisi ces valeurs basées sur nos observations en production : 95% de nos requêtes réussissent en moins de 500ms, donc 10 secondes est largement suffisant pour les cas normaux tout en permettant de détecter rapidement les vrais problèmes.

### Fallbacks gracieux

Quand tout le reste échoue - le Circuit Breaker est ouvert, les retries ont épuisé, le timeout est atteint - nous avons besoin d'une stratégie de fallback pour offrir une expérience utilisateur dégradée mais acceptable plutôt qu'une erreur brute.

Nos stratégies de fallback varient selon le type de service et de requête :

**Pour le Search Service** : Si le service de recherche est indisponible, on peut essayer de retourner des résultats mis en cache dans Redis. Pour les recherches populaires (comme "restaurants Yaoundé"), on a probablement des résultats récents en cache. On les retourne avec un header `X-Cache-Status: STALE` pour indiquer qu'ils peuvent ne pas être 100% à jour, mais au moins l'utilisateur obtient quelque chose. Si même le cache est vide, on retourne un message explicatif : "Le service de recherche est temporairement indisponible. Veuillez réessayer dans quelques instants."

**Pour le User Service** : Si l'utilisateur essaie de se connecter et que le service est down, il n'y a pas de fallback possible - on ne peut pas inventer des credentials valides. On retourne un message clair : "Le service d'authentification est temporairement indisponible. Nos équipes travaillent à résoudre le problème. Veuillez réessayer dans 5 minutes."

**Pour le Geo Service** : Si la géolocalisation échoue, on peut utiliser des valeurs par défaut basées sur le pays de l'utilisateur (extrait de son adresse IP) ou lui demander de saisir sa localisation manuellement.

**Pour le Shop Service** : Si la comparaison de prix est indisponible, on peut quand même afficher les produits depuis le cache, mais sans les prix à jour. On avertit l'utilisateur : "Les prix affichés peuvent ne pas être à jour."

L'important est que le fallback offre une dégradation gracieuse - une expérience réduite mais utilisable - plutôt qu'une panne complète.

---

## ⚡ Rate Limiting et protection

### Comprendre l'importance de la limitation de taux

La limitation de taux (rate limiting) est une protection essentielle contre plusieurs types de menaces et d'abus. Sans elle, notre système serait vulnérable à des attaques par déni de service, des abus de notre API gratuite, et des bugs clients qui génèrent des boucles infinies de requêtes.

Imaginez un scénario sans rate limiting : un développeur teste son application cliente et introduit accidentellement un bug qui crée une boucle infinie, envoyant des milliers de requêtes par seconde à notre API. En quelques secondes, ce seul client pourrait saturer complètement notre infrastructure, rendant le service inutilisable pour tous les autres utilisateurs légitimes. Ou encore, un attaquant malveillant pourrait lancer une attaque par force brute sur notre endpoint de login, essayant des millions de combinaisons de mots de passe. Sans rate limiting, il pourrait tester des milliers de mots de passe par seconde.

Le rate limiting protège à la fois nos services (en empêchant la surcharge) et nos utilisateurs (en garantissant un accès équitable pour tous). C'est comme un videur à l'entrée d'une discothèque qui régule le flux de personnes pour garantir que tout le monde puisse profiter de la soirée en toute sécurité.

### Algorithme Token Bucket expliqué

Nous utilisons l'algorithme du Token Bucket (seau à jetons) qui est particulièrement adapté au rate limiting dans les APIs. Voici comment il fonctionne avec une métaphore simple :

Imaginez que chaque utilisateur possède un seau qui peut contenir un maximum de 20 jetons. Chaque fois que l'utilisateur fait une requête, il doit dépenser 1 jeton de son seau. Si son seau est vide, sa requête est rejetée. Le seau se remplit automatiquement à un rythme constant de 10 jetons par seconde. Ce système permet à la fois des pics de trafic (burst) et une limitation en régime permanent.

Concrètement, voici différents scénarios :

**Scénario 1 - Utilisation normale** : Un utilisateur fait 5 requêtes par seconde de manière constante. Son seau reçoit 10 tokens par seconde et il en consomme 5, donc son seau reste toujours bien rempli (autour de 15-20 tokens). Ses requêtes passent toujours sans problème.

**Scénario 2 - Pic de trafic légitime** : Un utilisateur n'a fait aucune requête pendant 10 secondes (son seau est plein avec 20 tokens), puis il rafraîchit une page qui fait 15 requêtes d'un coup. Les 15 premières requêtes passent immédiatement en consommant 15 tokens. Son seau contient maintenant 5 tokens. S'il essaie de faire plus de 5 requêtes dans la seconde qui suit, elles seront rejetées. Mais après une seconde, son seau aura reçu 10 nouveaux tokens et il pourra refaire des requêtes.

**Scénario 3 - Abus ou bug** : Un bot malveillant ou une application buggée essaie d'envoyer 1000 requêtes par seconde. Les 20 premières requêtes passent (burst capacity), consommant tous les tokens. Toutes les requêtes suivantes sont immédiatement rejetées. Le bot peut au maximum envoyer 10 requêtes par seconde (le taux de remplissage), mais pas plus. Les 990 autres requêtes par seconde sont bloquées, protégeant ainsi notre infrastructure.

L'avantage de Token Bucket par rapport à d'autres algorithmes (comme Fixed Window ou Sliding Window) est qu'il permet des pics légitimes tout en imposant une limite stricte sur la moyenne. Un utilisateur peut faire un burst de 20 requêtes pour charger une page complexe, mais ne peut pas maintenir ce rythme indéfiniment.

### Implémentation distribuée avec Redis

Notre implémentation du rate limiting doit être distribuée car nous avons plusieurs instances du Gateway qui tournent en parallèle. Si chaque instance maintenait ses propres compteurs en mémoire locale, un utilisateur pourrait contourner la limite en envoyant ses requêtes à différentes instances. C'est pourquoi nous utilisons Redis comme store partagé pour tous les compteurs.

Voici l'implémentation détaillée :

```java
@Component
public class RedisRateLimiter {
    
    private final ReactiveStringRedisTemplate redisTemplate;
    private final int replenishRate;  // Tokens par seconde
    private final int burstCapacity;  // Capacité maximale
    
    public RedisRateLimiter(
            ReactiveStringRedisTemplate redisTemplate,
            @Value("${app.rate-limit.replenish-rate:10}") int replenishRate,
            @Value("${app.rate-limit.burst-capacity:20}") int burstCapacity) {
        this.redisTemplate = redisTemplate;
        this.replenishRate = replenishRate;
        this.burstCapacity = burstCapacity;
    }
    
    /**
     * Vérifie si une requête est autorisée et met à jour les compteurs.
     * Utilise un script Lua pour garantir l'atomicité des opérations Redis.
     */
    public Mono<RateLimitResult> isAllowed(String key) {
        // Script Lua exécuté atomiquement dans Redis
        String luaScript = """
            -- Clés Redis
            local tokens_key = KEYS[1]
            local timestamp_key = KEYS[2]
            
            -- Paramètres
            local replenish_rate = tonumber(ARGV[1])
            local burst_capacity = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])
            local requested_tokens = tonumber(ARGV[4])
            
            -- Récupération de l'état actuel
            local last_tokens = tonumber(redis.call('get', tokens_key) or burst_capacity)
            local last_refreshed = tonumber(redis.call('get', timestamp_key) or 0)
            
            -- Calcul du delta temps en secondes
            local delta = math.max(0, now - last_refreshed)
            
            -- Calcul des nouveaux tokens (avec limite à burst_capacity)
            local filled_tokens = math.min(burst_capacity, 
                last_tokens + (delta * replenish_rate))
            
            -- Vérification si assez de tokens disponibles
            local allowed = filled_tokens >= requested_tokens
            local new_tokens = filled_tokens
            
            if allowed then
                new_tokens = filled_tokens - requested_tokens
            end
            
            -- Mise à jour Redis
            redis.call('setex', tokens_key, 60, new_tokens)
            redis.call('setex', timestamp_key, 60, now)
            
            -- Retour du résultat
            return {
                allowed,
                new_tokens
            }
            """;
        
        List<String> keys = Arrays.asList(
            "rate_limit:tokens:" + key,
            "rate_limit:timestamp:" + key
        );
        
        long now = Instant.now().getEpochSecond();
        
        return redisTemplate.execute(
            RedisScript.of(luaScript, List.class),
            keys,
            String.valueOf(replenishRate),
            String.valueOf(burstCapacity),
            String.valueOf(now),
            "1"  // requested_tokens = 1
        ).map(result -> {
            Boolean allowed = (Boolean) result.get(0);
            Long tokensLeft = ((Number) result.get(1)).longValue();
            
            return RateLimitResult.builder()
                .allowed(allowed)
                .tokensRemaining(tokensLeft)
                .build();
        });
    }
    
    @Data
    @Builder
    public static class RateLimitResult {
        private boolean allowed;
        private long tokensRemaining;
    }
}
```

L'utilisation d'un script Lua est cruciale ici. Sans cela, nous aurions besoin de plusieurs commandes Redis séparées (GET tokens, GET timestamp, calcul, SET tokens, SET timestamp) qui ne seraient pas atomiques. Entre le GET et le SET, une autre instance du Gateway pourrait modifier les mêmes clés, créant des race conditions. Le script Lua s'exécute atomiquement dans Redis, garantissant la cohérence même avec plusieurs instances du Gateway.

### Limites différenciées par type d'endpoint

Tous les endpoints ne nécessitent pas la même protection. Nous appliquons des limites différentes selon le type d'opération et le niveau de risque :

**Endpoints de lecture (GET)** : Limite généreuse de 100 requêtes par minute. Les opérations de lecture sont relativement peu coûteuses et la plupart des utilisateurs légitimes ne dépassent jamais cette limite en usage normal.

**Endpoints de recherche (GET /api/search/\*\*)** : Limite modérée de 60 requêtes par minute. La recherche est plus coûteuse car elle interroge Elasticsearch, donc nous sommes un peu plus restrictifs. Mais nous voulons quand même permettre une utilisation fluide où un utilisateur peut raffiner sa recherche plusieurs fois.

**Endpoints d'écriture (POST, PUT, DELETE)** : Limite stricte de 30 requêtes par minute. Les opérations d'écriture modifient l'état du système et sont plus coûteuses. De plus, elles sont plus susceptibles d'abus (création de spam, modifications malveillantes).

**Endpoint de login (POST /api/auth/login)** : Limite très stricte de 5 tentatives par minute par IP. Cela empêche efficacement les attaques par force brute sur les mots de passe. Un utilisateur légitime qui se trompe de mot de passe ne dépassera jamais cette limite, mais un attaquant qui essaie des milliers de combinaisons sera bloqué rapidement.

**Endpoints d'administration (GET /actuator/\*\*, POST /api/crawler/\*\*)** : Limite élevée de 1000 requêtes par minute. Les administrateurs ont besoin de plus de liberté pour leurs tâches de maintenance et de monitoring. Mais ils sont identifiés et responsables, donc l'abus est moins probable.

**Endpoints publics non authentifiés** : Pour les endpoints accessibles sans authentification (comme la recherche publique), nous limitons par adresse IP. Limite de 30 requêtes par minute par IP. C'est un compromis : assez permissif pour permettre une utilisation normale, mais assez restrictif pour empêcher les abus.

Voici comment nous configurons ces limites différenciées :

```java
@Configuration
public class RateLimitConfiguration {
    
    @Bean
    public Map<String, RateLimitConfig> rateLimitConfigs() {
        Map<String, RateLimitConfig> configs = new HashMap<>();
        
        // Configuration par défaut
        configs.put("default", RateLimitConfig.builder()
            .replenishRate(100)
            .burstCapacity(150)
            .build());
        
        // Recherche
        configs.put("/api/search/**", RateLimitConfig.builder()
            .replenishRate(60)
            .burstCapacity(80)
            .build());
        
        // Écriture
        configs.put("POST:/api/**", RateLimitConfig.builder()
            .replenishRate(30)
            .burstCapacity(50)
            .build());
        
        // Login
        configs.put("POST:/api/auth/login", RateLimitConfig.builder()
            .replenishRate(5)
            .burstCapacity(10)
            .build());
        
        // Admin
        configs.put("/actuator/**", RateLimitConfig.builder()
            .replenishRate(1000)
            .burstCapacity(1500)
            .build());
        
        return configs;
    }
    
    @Data
    @Builder
    public static class RateLimitConfig {
        private int replenishRate;
        private int burstCapacity;
    }
}
```

### Réponses informatives lors de dépassement

Quand un utilisateur dépasse sa limite, nous ne retournons pas simplement une erreur brute. Nous fournissons des informations détaillées qui lui permettent de comprendre ce qui s'est passé et quand il pourra réessayer :

```java
private Mono<Void> rateLimitExceeded(
        ServerWebExchange exchange,
        RateLimitInfo info) {
    
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
    
    // Headers informatifs
    response.getHeaders().add("X-RateLimit-Limit", 
        String.valueOf(info.getLimit()));
    response.getHeaders().add("X-RateLimit-Remaining", "0");
    response.getHeaders().add("X-RateLimit-Reset", 
        String.valueOf(info.getResetAt().getEpochSecond()));
    response.getHeaders().add("Retry-After", 
        String.valueOf(info.getRetryAfterSeconds()));
    
    // Corps de réponse JSON explicatif
    ErrorResponse errorResponse = ErrorResponse.builder()
        .status(429)
        .error("Too Many Requests")
        .message(String.format(
            "Rate limit exceeded. You have made %d requests in the last minute. " +
            "The limit is %d requests per minute. " +
            "Please wait %d seconds before trying again.",
            info.getRequestsMade(),
            info.getLimit(),
            info.getRetryAfterSeconds()))
        .path(exchange.getRequest().getPath().value())
        .timestamp(Instant.now())
        .details(Map.of(
            "limit", info.getLimit(),
            "remaining", 0,
            "reset_at", info.getResetAt().toString(),
            "retry_after_seconds", info.getRetryAfterSeconds()
        ))
        .build();
    
    byte[] bytes = errorResponse.toJson().getBytes();
    DataBuffer buffer = response.bufferFactory().wrap(bytes);
    
    return response.writeWith(Mono.just(buffer));
}
```

Cette réponse fournit plusieurs niveaux d'information :

**Headers HTTP standards** : `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset` suivent une convention largement adoptée qui permet aux clients de parser facilement les informations de rate limiting. `Retry-After` indique combien de secondes attendre avant de réessayer.

**Message lisible par l'humain** : Un message clair en français explique ce qui s'est passé et quand réessayer. Si l'utilisateur voit cette erreur dans l'interface, il comprendra immédiatement ce qui se passe.

**Détails structurés** : Des informations supplémentaires dans le champ `details` permettent aux applications clientes de parser programmatiquement les informations et d'afficher des messages personnalisés ou d'implémenter un backoff automatique.

### Whitelist et bypass pour cas spéciaux

Certains clients légitimes ont besoin de limites plus élevées ou de pas de limite du tout. Par exemple :

**Nos propres services internes** : Les appels entre microservices (comme le Stats Service qui consomme des événements Kafka et les agrège) ne devraient pas être rate-limited car ils sont contrôlés et ne présentent pas de risque d'abus.

**Partenaires API** : Les marchands qui intègrent notre API de recherche dans leurs sites peuvent avoir négocié des limites plus élevées selon leur contrat.

**Adresses IP de confiance** : Nos serveurs de monitoring, nos tests de charge, ou les bureaux de notre entreprise peuvent être whitelistés.

Nous implémentons cela via un KeyResolver personnalisé qui peut identifier ces cas spéciaux :

```java
@Bean
public KeyResolver customKeyResolver() {
    return exchange -> {
        ServerHttpRequest request = exchange.getRequest();
        
        // Bypass pour les appels internes (header X-Internal-Service)
        String internalService = request.getHeaders()
            .getFirst("X-Internal-Service");
        if (internalService != null) {
            return Mono.just("internal:" + internalService);
        }
        
        // Bypass pour API keys premium
        String apiKey = request.getHeaders().getFirst("X-API-Key");
        if (apiKey != null && isPremiumApiKey(apiKey)) {
            return Mono.just("premium:" + apiKey);
        }
        
        // IP whitelist
        String clientIp = request.getRemoteAddress()
            .getAddress().getHostAddress();
        if (isWhitelistedIp(clientIp)) {
            return Mono.just("whitelist:" + clientIp);
        }
        
        // Utilisateur authentifié standard
        String userId = request.getHeaders().getFirst("X-User-Id");
        if (userId != null) {
            return Mono.just("user:" + userId);
        }
        
        // Utilisateur non authentifié (par IP)
        return Mono.just("ip:" + clientIp);
    };
}
```

Les clés commençant par `internal:`, `premium:` ou `whitelist:` peuvent avoir des configurations de rate limiting différentes (plus élevées ou désactivées) dans notre configuration.

---

## 📊 Monitoring et observabilité

### Les trois piliers de l'observabilité

L'observabilité d'un système distribué repose sur trois piliers fondamentaux : les **métriques**, les **logs** et les **traces**. Chacun fournit un type d'information différent et complémentaire.

**Les métriques** répondent à la question "que se passe-t-il ?" de manière agrégée. Elles nous disent combien de requêtes par seconde nous traitons, quelle est la latence médiane, quel est le taux d'erreur, quelle est l'utilisation CPU et mémoire. Les métriques sont des séries temporelles de nombres qui nous permettent de visualiser des tendances, détecter des anomalies, et créer des alertes. Par exemple, si notre taux d'erreur passe soudainement de 0.1% à 5%, nous voulons le savoir immédiatement.

**Les logs** répondent à la question "que s'est-il passé ?" de manière détaillée pour des événements spécifiques. Ils nous disent qu'à 14:32:15, l'utilisateur user_123 a fait une requête GET vers /api/search avec la query "restaurants", que cette requête a été routée vers l'instance search-service-2, qu'elle a pris 156ms, et qu'elle a retourné 42 résultats. Les logs sont essentiels pour le debugging car ils fournissent le contexte complet d'événements individuels.

**Les traces** répondent à la question "comment s'est déroulée une requête ?" à travers tout le système distribué. Une trace suit une requête depuis son arrivée au Gateway jusqu'à sa réponse finale, en passant par tous les services qu'elle a traversés. Elle nous montre que cette requête a passé 5ms dans le Gateway, puis 150ms dans le Search Service (dont 140ms à interroger Elasticsearch et 10ms à formater les résultats), puis 1ms dans le Gateway pour retourner la réponse. Les traces sont cruciales pour identifier les goulots d'étranglement dans les systèmes distribués.

Notre Gateway contribue activement aux trois piliers, car étant le point d'entrée unique, il a une visibilité complète sur toutes les requêtes.

### Métriques Prometheus détaillées

Nous exposons des métriques au format Prometheus via l'endpoint `/actuator/prometheus`. Ces métriques sont scrappées (collectées) par Prometheus toutes les 15 secondes et stockées dans sa base de données de séries temporelles. Grafana se connecte à Prometheus pour visualiser ces métriques dans des dashboards interactifs.

Voici les principales catégories de métriques que nous collectons :

**Métriques de requêtes HTTP** :

```java
// Counter: nombre total de requêtes
meterRegistry.counter("gateway_requests_total",
    "method", request.getMethod().name(),
    "path", normalizePath(request.getPath().value()),
    "status", String.valueOf(response.getStatusCode().value()),
    "route", routeId
).increment();

// Histogram: distribution des temps de réponse
meterRegistry.timer("gateway_request_duration_seconds",
    "method", request.getMethod().name(),
    "route", routeId
).record(duration, TimeUnit.MILLISECONDS);

// Histogram: taille des requêtes
meterRegistry.summary("gateway_request_size_bytes",
    "method", request.getMethod().name()
).record(requestSize);

// Histogram: taille des réponses
meterRegistry.summary("gateway_response_size_bytes",
    "status", String.valueOf(statusCode)
).record(responseSize);
```

Ces métriques nous permettent de répondre à des questions comme :
- Combien de requêtes par seconde recevons-nous ? (rate de `gateway_requests_total`)
- Quelle est la latence p95 ? (95ème percentile de `gateway_request_duration_seconds`)
- Quel endpoint a le taux d'erreur le plus élevé ? (filtrer `gateway_requests_total` par `status=5xx`)
- Quel est le débit en MB/s ? (rate de `gateway_response_size_bytes`)

**Métriques de Circuit Breaker** :

```java
// Gauge: état actuel du Circuit Breaker (0=CLOSED, 1=OPEN, 2=HALF_OPEN)
meterRegistry.gauge("circuitbreaker_state",
    Tags.of("name", circuitBreakerName),
    circuitBreaker,
    cb -> cb.getState().getOrder());

// Counter: nombre d'appels par état
meterRegistry.counter("circuitbreaker_calls_total",
    "name", circuitBreakerName,
    "kind", "successful"
).increment();

meterRegistry.counter("circuitbreaker_calls_total",
    "name", circuitBreakerName,
    "kind", "failed"
).increment();

// Gauge: taux d'échec actuel
meterRegistry.gauge("circuitbreaker_failure_rate",
    Tags.of("name", circuitBreakerName),
    circuitBreaker,
    cb -> cb.getMetrics().getFailureRate());

// Gauge: taux d'appels lents
meterRegistry.gauge("circuitbreaker_slow_call_rate",
    Tags.of("name", circuitBreakerName),
    circuitBreaker,
    cb -> cb.getMetrics().getSlowCallRate());
```

Ces métriques nous alertent immédiatement quand un Circuit Breaker s'ouvre, indiquant un problème avec un service backend. Nous pouvons voir l'historique des transitions d'état et corréler avec d'autres métriques pour identifier la cause racine.

**Métriques de Rate Limiting** :

```java
// Counter: nombre de requêtes autorisées
meterRegistry.counter("ratelimit_requests_allowed",
    "route", routeId
).increment();

// Counter: nombre de requêtes rejetées
meterRegistry.counter("ratelimit_requests_rejected",
    "route", routeId,
    "reason", "quota_exceeded"
).increment();

// Histogram: tokens restants au moment de la requête
meterRegistry.summary("ratelimit_tokens_remaining",
    "route", routeId
).record(tokensRemaining);
```

Ces métriques nous permettent de détecter les abus potentiels (beaucoup de requêtes rejetées pour un utilisateur ou une IP spécifique) et d'ajuster nos limites si nécessaire (si trop d'utilisateurs légitimes sont rejetés, peut-être que nos limites sont trop strictes).

**Métriques système** :

```java
// JVM memory
meterRegistry.gauge("jvm_memory_used_bytes",
    Tags.of("area", "heap"),
    runtime,
    r -> r.totalMemory() - r.freeMemory());

// JVM threads
meterRegistry.gauge("jvm_threads_current",
    Tags.empty(),
    Thread.activeCount());

// System CPU
meterRegistry.gauge("system_cpu_usage",
    operatingSystem,
    os -> os.getSystemCpuLoad());

// GC pause time
meterRegistry.timer("jvm_gc_pause",
    "action", gcAction,
    "cause", gcCause
).record(duration);
```

Ces métriques nous alertent sur les problèmes de ressources (mémoire insuffisante, CPU saturé, garbage collection excessive) avant qu'ils ne causent des pannes.

### Dashboards Grafana

Nous avons créé plusieurs dashboards Grafana qui visualisent ces métriques de manière intuitive. Voici les dashboards principaux :

**Dashboard "Gateway Overview"** : Vue d'ensemble avec les KPIs essentiels affichés en grand :
- Requêtes par seconde (graphique de ligne avec tendance)
- Latence p50/p95/p99 (graphique de ligne multi-séries)
- Taux d'erreur (gauge avec seuils rouge/orange/vert)
- État des Circuit Breakers (tableau avec couleurs selon l'état)
- Top 10 des endpoints les plus appelés (bar chart)
- Distribution des codes de statut (pie chart)

Ce dashboard est affiché en permanence sur un grand écran dans la salle de monitoring. Un coup d'œil suffit pour savoir si tout va bien ou s'il y a un problème.

**Dashboard "Per-Route Performance"** : Performance détaillée pour chaque route configurée. On peut sélectionner une route spécifique et voir :
- Latence historique (graphique de ligne avec percentiles)
- Volume de requêtes (graphique de ligne)
- Taux d'erreur (graphique de ligne)
- Top erreurs (tableau des erreurs les plus fréquentes)
- Distribution de la latence (heatmap)

Ce dashboard est utilisé pour investiguer les problèmes de performance d'un endpoint spécifique ou pour optimiser les routes les plus sollicitées.

**Dashboard "Circuit Breakers"** : Vue dédiée à la résilience :
- État actuel de tous les Circuit Breakers (table avec état et métriques)
- Historique des transitions d'état (timeline des ouvertures/fermetures)
- Taux d'échec par service (graphique de ligne par service)
- Appels réussis vs échoués (stacked area chart)
- Latence des appels par service (graphique de ligne comparatif)

Quand un Circuit Breaker s'ouvre, ce dashboard nous permet de voir immédiatement quel service a un problème et depuis quand.

**Dashboard "Rate Limiting"** : Surveillance des abus et des limites :
- Taux de rejection global (gauge)
- Top des IPs/utilisateurs rejetés (table)
- Requêtes autorisées vs rejetées par endpoint (stacked bar chart)
- Tendance des rejections (graphique de ligne)
- Tokens moyens restants (graphique de ligne)

Ce dashboard nous aide à identifier les abus (si une IP spécifique génère 90% des rejections, c'est suspect) et à ajuster les limites.

**Dashboard "System Health"** : Santé de l'application :
- Utilisation mémoire JVM (graphique de ligne avec heap/non-heap)
- Threads actifs (graphique de ligne)
- Garbage Collection pauses (graphique de ligne)
- CPU usage (graphique de ligne)
- Open file descriptors (graphique de ligne)

Ce dashboard nous alerte sur les problèmes de ressources avant qu'ils ne causent des OutOfMemoryError ou des crashes.

### Logs structurés avec Loki

Tous nos logs sont au format JSON structuré, ce qui facilite grandement leur analyse. Voici un exemple de log pour une requête :

```json
{
  "timestamp": "2025-12-18T14:32:15.123Z",
  "level": "INFO",
  "logger": "com.yowyob.gateway.filter.LoggingFilter",
  "thread": "reactor-http-nio-3",
  "message": "Request completed",
  "requestId": "req-a1b2c3d4",
  "userId": "user_789",
  "method": "GET",
  "path": "/api/search",
  "query": "q=restaurants+yaounde",
  "status": 200,
  "duration_ms": 156,
  "request_size_bytes": 245,
  "response_size_bytes": 8432,
  "user_agent": "Mozilla/5.0...",
  "client_ip": "192.168.1.100",
  "route": "search_service_route",
  "backend_service": "search-service-2",
  "backend_duration_ms": 150,
  "cache_status": "MISS"
}
```

Ces logs structurés sont envoyés à Loki (notre système d'agrégation de logs) où ils peuvent être requêtés avec LogQL (le langage de requête de Loki). Par exemple :

```logql
# Trouver toutes les requêtes lentes (>1s) dans les dernières 24h
{app="api-gateway"} | json | duration_ms > 1000

# Trouver toutes les erreurs 5xx pour un utilisateur spécifique
{app="api-gateway"} | json | userId="user_789" | status >= 500

# Calculer la latence p95 par endpoint
rate({app="api-gateway"} | json | quantile(duration_ms, 0.95) [5m]) by (path)
```

Cette capacité de requête flexible est cruciale pour le debugging. Quand un utilisateur signale un problème ("ma recherche est lente"), nous pouvons facilement filtrer les logs par son userId et voir exactement ce qui s'est passé pour chacune de ses requêtes.

### Tracing distribué avec Jaeger

Le tracing distribué nous permet de suivre une requête à travers tous les services qu'elle traverse. Chaque requête reçoit un trace ID unique qui est propagé à tous les services via le header `X-B3-TraceId` (format Zipkin B3).

Voici comment une trace typique se déroule :

```
Trace ID: a1b2c3d4e5f6g7h8

Span 1: api-gateway [156ms]
  ├─ Span 2: jwt-authentication [3ms]
  ├─ Span 3: rate-limit-check [2ms]
  └─ Span 4: route-to-search-service [151ms]
      ├─ Span 5: search-service.search [150ms]
      │   ├─ Span 6: elasticsearch.query [140ms]
      │   │   ├─ Span 7: es.index.documents [70ms]
      │   │   └─ Span 8: es.index.products [70ms]
      │   ├─ Span 9: redis.get [1ms]
      │   └─ Span 10: format-results [9ms]
      └─ Span 11: gateway-response [1ms]
```

Cette trace nous montre clairement que les 156ms de latence totale se décomposent ainsi :
- 3ms pour valider le JWT (acceptable)
- 2ms pour vérifier le rate limit (acceptable)
- 151ms pour l'appel au Search Service, dont :
    - 140ms passés dans Elasticsearch (le vrai goulot d'étranglement)
    - 1ms pour vérifier le cache Redis
    - 9ms pour formater les résultats

Cette visibilité nous permet d'identifier précisément où optimiser. Dans cet exemple, c'est clairement la requête Elasticsearch qui prend le plus de temps. Nous pourrions investiguer si les index sont optimaux, si la requête peut être simplifiée, ou si nous devrions mettre en cache ce type de recherche.

**Implémentation du tracing dans le Gateway** :

Nous utilisons Spring Cloud Sleuth (qui s'intègre avec Micrometer Tracing dans Spring Boot 3) pour propager automatiquement les contextes de trace à travers notre architecture réactive :

```java
@Configuration
public class TracingConfig {
    
    /**
     * Configuration du sampler de traces.
     * En production, nous échantillonnons 10% des traces pour réduire l'overhead.
     * En développement, nous traçons 100% des requêtes.
     */
    @Bean
    public Sampler defaultSampler(@Value("${spring.profiles.active}") String profile) {
        return "prod".equals(profile) 
            ? Sampler.traceIdRatioBased(0.1)  // 10% en prod
            : Sampler.alwaysOn();              // 100% en dev
    }
    
    /**
     * Propagation du contexte de trace dans les appels WebClient.
     * Crucial pour suivre les requêtes à travers les services.
     */
    @Bean
    public WebClient.Builder tracedWebClientBuilder(
            ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        return WebClient.builder()
            .filter(lbFunction)
            .filter((request, next) -> {
                // Le contexte de trace est automatiquement propagé par Sleuth
                // via les headers B3 (X-B3-TraceId, X-B3-SpanId, etc.)
                return next.exchange(request);
            });
    }
}
```

**Enrichissement des spans avec des tags personnalisés** :

Nous ajoutons des tags métier aux spans pour faciliter le filtering et l'analyse dans Jaeger :

```java
@Component
@Slf4j
public class TracingFilter implements GlobalFilter {
    
    private final Tracer tracer;
    
    public TracingFilter(Tracer tracer) {
        this.tracer = tracer;
    }
    
    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange, 
            GatewayFilterChain chain) {
        
        // Récupération du span actuel créé par Sleuth
        Span span = tracer.currentSpan();
        
        if (span != null) {
            ServerHttpRequest request = exchange.getRequest();
            
            // Ajout de tags métier
            span.tag("http.method", request.getMethod().name());
            span.tag("http.path", request.getPath().value());
            span.tag("http.url", request.getURI().toString());
            
            // Tags utilisateur si authentifié
            String userId = request.getHeaders().getFirst("X-User-Id");
            if (userId != null) {
                span.tag("user.id", userId);
                span.tag("user.authenticated", "true");
            } else {
                span.tag("user.authenticated", "false");
            }
            
            // Tags route
            Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
            if (route != null) {
                span.tag("gateway.route.id", route.getId());
                span.tag("gateway.route.uri", route.getUri().toString());
            }
        }
        
        return chain.filter(exchange)
            .doOnSuccess(aVoid -> {
                if (span != null) {
                    ServerHttpResponse response = exchange.getResponse();
                    HttpStatus status = response.getStatusCode();
                    
                    if (status != null) {
                        span.tag("http.status_code", String.valueOf(status.value()));
                        
                        // Marquer le span comme erreur si statut 5xx
                        if (status.is5xxServerError()) {
                            span.error(new Exception(
                                "Server error: " + status.getReasonPhrase()));
                        }
                    }
                }
            })
            .doOnError(error -> {
                if (span != null) {
                    // Capture de l'erreur dans le span
                    span.error(error);
                    span.tag("error.type", error.getClass().getSimpleName());
                    span.tag("error.message", error.getMessage());
                }
            });
    }
}
```

**Création de spans personnalisés pour les opérations importantes** :

Pour les opérations critiques, nous créons des spans enfants explicites pour avoir une granularité fine :

```java
@Component
public class CacheFilter implements GatewayFilter {
    
    private final Tracer tracer;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    
    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange, 
            GatewayFilterChain chain) {
        
        String cacheKey = generateCacheKey(exchange);
        
        // Création d'un span pour l'opération de cache
        return Mono.deferContextual(ctx -> {
            Span parentSpan = tracer.currentSpan();
            Span cacheSpan = tracer.nextSpan(parentSpan)
                .name("cache.lookup")
                .tag("cache.key", cacheKey)
                .start();
            
            try (Tracer.SpanInScope ws = tracer.withSpan(cacheSpan)) {
                return redisTemplate.opsForValue()
                    .get(cacheKey)
                    .flatMap(cachedValue -> {
                        cacheSpan.tag("cache.hit", "true");
                        cacheSpan.event("Cache hit - returning cached response");
                        return returnCachedResponse(exchange, cachedValue);
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        cacheSpan.tag("cache.hit", "false");
                        cacheSpan.event("Cache miss - forwarding to backend");
                        return chain.filter(exchange)
                            .then(cacheResponse(exchange, cacheKey, cacheSpan));
                    }))
                    .doFinally(signalType -> {
                        cacheSpan.end();
                    });
            }
        });
    }
    
    private Mono<Void> cacheResponse(
            ServerWebExchange exchange, 
            String cacheKey,
            Span parentSpan) {
        
        // Span pour l'écriture en cache
        Span writeSpan = tracer.nextSpan(parentSpan)
            .name("cache.write")
            .tag("cache.key", cacheKey)
            .start();
        
        return Mono.defer(() -> {
            try (Tracer.SpanInScope ws = tracer.withSpan(writeSpan)) {
                // Logique de mise en cache...
                return redisTemplate.opsForValue()
                    .set(cacheKey, responseBody, Duration.ofMinutes(5))
                    .doOnSuccess(success -> {
                        writeSpan.event("Response cached successfully");
                    })
                    .doOnError(error -> {
                        writeSpan.error(error);
                        writeSpan.tag("error", "Failed to cache response");
                    })
                    .doFinally(signalType -> {
                        writeSpan.end();
                    })
                    .then();
            }
        });
    }
}
```

### Alerting intelligent avec Prometheus Alertmanager

Les métriques ne servent à rien si personne ne les regarde. C'est pourquoi nous avons configuré des alertes automatiques qui nous notifient immédiatement quand quelque chose d'anormal se produit.

**Alertes de disponibilité** :

```yaml
# prometheus-alerts.yml
groups:
  - name: gateway_availability
    interval: 30s
    rules:
      # Alerte si le Gateway est down
      - alert: GatewayDown
        expr: up{job="api-gateway"} == 0
        for: 1m
        labels:
          severity: critical
          component: gateway
        annotations:
          summary: "API Gateway is down"
          description: "The API Gateway instance {{ $labels.instance }} has been down for more than 1 minute."
          runbook: "https://wiki.yowyob.com/runbooks/gateway-down"
      
      # Alerte si trop d'instances sont down
      - alert: GatewayMultipleInstancesDown
        expr: count(up{job="api-gateway"} == 0) > 1
        for: 1m
        labels:
          severity: critical
          component: gateway
        annotations:
          summary: "Multiple API Gateway instances are down"
          description: "{{ $value }} instances of the API Gateway are currently down."
          runbook: "https://wiki.yowyob.com/runbooks/gateway-multiple-down"
```

**Alertes de performance** :

```yaml
  - name: gateway_performance
    interval: 1m
    rules:
      # Alerte si la latence p95 dépasse 1 seconde
      - alert: GatewayHighLatency
        expr: |
          histogram_quantile(0.95, 
            rate(gateway_request_duration_seconds_bucket[5m])
          ) > 1
        for: 5m
        labels:
          severity: warning
          component: gateway
        annotations:
          summary: "API Gateway latency is high"
          description: "The p95 latency for route {{ $labels.route }} is {{ $value }}s (threshold: 1s)."
          dashboard: "https://grafana.yowyob.com/d/gateway-performance"
      
      # Alerte si le taux d'erreur dépasse 5%
      - alert: GatewayHighErrorRate
        expr: |
          sum(rate(gateway_requests_total{status=~"5.."}[5m])) 
          / 
          sum(rate(gateway_requests_total[5m])) 
          > 0.05
        for: 5m
        labels:
          severity: critical
          component: gateway
        annotations:
          summary: "API Gateway error rate is high"
          description: "The error rate is {{ $value | humanizePercentage }} (threshold: 5%)."
          dashboard: "https://grafana.yowyob.com/d/gateway-errors"
```

**Alertes de résilience** :

```yaml
  - name: gateway_resilience
    interval: 1m
    rules:
      # Alerte si un Circuit Breaker est ouvert
      - alert: CircuitBreakerOpen
        expr: circuitbreaker_state == 1
        for: 2m
        labels:
          severity: warning
          component: gateway
        annotations:
          summary: "Circuit breaker is open"
          description: "Circuit breaker {{ $labels.name }} has been open for more than 2 minutes."
          impact: "Requests to {{ $labels.name }} are failing or being served from fallback."
          dashboard: "https://grafana.yowyob.com/d/circuit-breakers"
      
      # Alerte si le taux d'échec d'un service dépasse 50%
      - alert: BackendServiceHighFailureRate
        expr: |
          sum(rate(circuitbreaker_calls_total{kind="failed"}[5m])) by (name)
          /
          sum(rate(circuitbreaker_calls_total[5m])) by (name)
          > 0.5
        for: 5m
        labels:
          severity: critical
          component: gateway
        annotations:
          summary: "Backend service has high failure rate"
          description: "Service {{ $labels.name }} has a failure rate of {{ $value | humanizePercentage }}."
```

**Alertes de sécurité et abus** :

```yaml
  - name: gateway_security
    interval: 1m
    rules:
      # Alerte si beaucoup de requêtes sont rate-limited
      - alert: HighRateLimitRejections
        expr: |
          sum(rate(ratelimit_requests_rejected[5m])) 
          / 
          sum(rate(ratelimit_requests_allowed[5m])) 
          > 0.2
        for: 5m
        labels:
          severity: warning
          component: gateway
        annotations:
          summary: "High rate of rate-limited requests"
          description: "{{ $value | humanizePercentage }} of requests are being rate-limited."
          impact: "Possible abuse or DDoS attack, or legitimate traffic spike."
      
      # Alerte si beaucoup de tentatives de login échouées
      - alert: SuspiciousLoginActivity
        expr: |
          sum(rate(gateway_requests_total{
            path="/api/auth/login",
            status="401"
          }[5m])) > 10
        for: 5m
        labels:
          severity: warning
          component: security
        annotations:
          summary: "Suspicious login activity detected"
          description: "{{ $value }} failed login attempts per second."
          impact: "Possible brute-force attack."
```

**Configuration de l'Alertmanager** :

Les alertes sont routées vers différents canaux selon leur sévérité :

```yaml
# alertmanager.yml
global:
  resolve_timeout: 5m
  slack_api_url: 'https://hooks.slack.com/services/xxx/yyy/zzz'

route:
  group_by: ['alertname', 'component']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 12h
  receiver: 'default'
  routes:
    # Alertes critiques -> PagerDuty + Slack #incidents
    - match:
        severity: critical
      receiver: 'pagerduty-critical'
      continue: true
    
    - match:
        severity: critical
      receiver: 'slack-incidents'
    
    # Alertes warning -> Slack #monitoring
    - match:
        severity: warning
      receiver: 'slack-monitoring'

receivers:
  - name: 'default'
    slack_configs:
      - channel: '#monitoring'
        title: '{{ .GroupLabels.alertname }}'
        text: '{{ range .Alerts }}{{ .Annotations.description }}{{ end }}'
  
  - name: 'pagerduty-critical'
    pagerduty_configs:
      - service_key: '<pagerduty-key>'
        description: '{{ .GroupLabels.alertname }}'
  
  - name: 'slack-incidents'
    slack_configs:
      - channel: '#incidents'
        title: '🚨 {{ .GroupLabels.alertname }}'
        text: |
          *Description:* {{ range .Alerts }}{{ .Annotations.description }}{{ end }}
          *Impact:* {{ range .Alerts }}{{ .Annotations.impact }}{{ end }}
          *Dashboard:* {{ range .Alerts }}{{ .Annotations.dashboard }}{{ end }}
        color: danger
  
  - name: 'slack-monitoring'
    slack_configs:
      - channel: '#monitoring'
        title: '⚠️ {{ .GroupLabels.alertname }}'
        text: '{{ range .Alerts }}{{ .Annotations.description }}{{ end }}'
        color: warning
```

Cette configuration garantit que les alertes critiques (Gateway down, taux d'erreur élevé) réveillent l'équipe d'astreinte via PagerDuty et créent un incident dans Slack, tandis que les alertes moins urgentes (latence élevée, abus détectés) sont simplement postées dans le canal de monitoring pour investigation pendant les heures de travail.

---

## 🔄 Configuration et routage

### Routes statiques vs dynamiques

Notre Gateway supporte deux modes de définition des routes : statique (configuration au démarrage) et dynamique (modification à chaud sans redémarrage).

**Routes statiques** : Définies dans `application.yml` ou programmatiquement dans `GatewayConfig.java`. Ces routes sont chargées au démarrage de l'application et restent fixes pendant toute la durée de vie du processus. C'est approprié pour les routes stables qui ne changent jamais ou rarement.

Exemple de routes statiques en YAML :

```yaml
spring:
  cloud:
    gateway:
      routes:
        # Route pour Search Service
        - id: search_service_route
          uri: lb://SEARCH-SERVICE
          predicates:
            - Path=/api/search/**
            - Method=GET,POST
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: searchCircuitBreaker
                fallbackUri: forward:/fallback/search
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 60
                redis-rate-limiter.burstCapacity: 80
            - name: Retry
              args:
                retries: 3
                statuses: BAD_GATEWAY,SERVICE_UNAVAILABLE
                methods: GET
                backoff:
                  firstBackoff: 1s
                  maxBackoff: 5s
                  factor: 2
                  basedOnPreviousValue: false
          metadata:
            response-timeout: 5000
            connect-timeout: 2000
            description: "Routes search requests to Search Service"
        
        # Route pour User Service
        - id: user_service_route
          uri: lb://USER-SERVICE
          predicates:
            - Path=/api/users/**,/api/auth/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: userCircuitBreaker
                fallbackUri: forward:/fallback/user
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 30
                redis-rate-limiter.burstCapacity: 50
          metadata:
            response-timeout: 3000
            connect-timeout: 2000
        
        # Route pour Geo Service
        - id: geo_service_route
          uri: lb://GEO-SERVICE
          predicates:
            - Path=/api/geo/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: geoCircuitBreaker
                fallbackUri: forward:/fallback/geo
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 150
          metadata:
            response-timeout: 4000
```

**Routes dynamiques** : Stockées dans une source externe (base de données, Config Server, ou Redis) et peuvent être modifiées à chaud. Cela permet d'ajouter, modifier ou supprimer des routes sans redémarrer le Gateway, ce qui est crucial en production pour éviter les interruptions de service.

Implémentation d'un RouteDefinitionRepository personnalisé qui charge les routes depuis Redis :

```java
@Component
@Slf4j
public class RedisRouteDefinitionRepository 
        implements RouteDefinitionRepository {
    
    private static final String ROUTES_KEY = "gateway:routes";
    
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    
    public RedisRouteDefinitionRepository(
            ReactiveRedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            ApplicationEventPublisher eventPublisher) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return redisTemplate.opsForHash()
            .values(ROUTES_KEY)
            .cast(String.class)
            .flatMap(json -> {
                try {
                    RouteDefinition route = objectMapper.readValue(
                        json, RouteDefinition.class);
                    return Mono.just(route);
                } catch (Exception e) {
                    log.error("Failed to deserialize route definition", e);
                    return Mono.empty();
                }
            })
            .doOnComplete(() -> 
                log.info("Loaded routes from Redis"));
    }
    
    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(r -> {
            try {
                String json = objectMapper.writeValueAsString(r);
                return redisTemplate.opsForHash()
                    .put(ROUTES_KEY, r.getId(), json)
                    .doOnSuccess(success -> {
                        log.info("Saved route: {}", r.getId());
                        // Publier un événement pour rafraîchir les routes
                        eventPublisher.publishEvent(
                            new RefreshRoutesEvent(this));
                    })
                    .then();
            } catch (Exception e) {
                return Mono.error(new RuntimeException(
                    "Failed to serialize route", e));
            }
        });
    }
    
    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> 
            redisTemplate.opsForHash()
                .remove(ROUTES_KEY, id)
                .doOnSuccess(removed -> {
                    log.info("Deleted route: {}", id);
                    eventPublisher.publishEvent(
                        new RefreshRoutesEvent(this));
                })
                .then()
        );
    }
}
```

**API d'administration des routes** :

Pour gérer les routes dynamiques, nous exposons des endpoints d'administration protégés :

```java
@RestController
@RequestMapping("/admin/routes")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class RouteAdminController {
    
    private final RouteDefinitionRepository routeRepository;
    private final RouteDefinitionLocator routeLocator;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * Liste toutes les routes actuellement configurées
     */
    @GetMapping
    public Flux<RouteInfo> listRoutes() {
        return routeLocator.getRouteDefinitions()
            .map(route -> RouteInfo.builder()
                .id(route.getId())
                .uri(route.getUri().toString())
                .predicates(route.getPredicates().stream()
                    .map(PredicateDefinition::getName)
                    .collect(Collectors.toList()))
                .filters(route.getFilters().stream()
                    .map(FilterDefinition::getName)
                    .collect(Collectors.toList()))
                .order(route.getOrder())
                .metadata(route.getMetadata())
                .build());
    }
    
    /**
     * Récupère une route spécifique par ID
     */
    @GetMapping("/{id}")
    public Mono<RouteDefinition> getRoute(@PathVariable String id) {
        return routeLocator.getRouteDefinitions()
            .filter(route -> route.getId().equals(id))
            .singleOrEmpty()
            .switchIfEmpty(Mono.error(
                new NotFoundException("Route not found: " + id)));
    }
    
    /**
     * Crée ou met à jour une route
     */
    @PostMapping
    public Mono<ResponseEntity<String>> createOrUpdateRoute(
            @Valid @RequestBody RouteDefinition route) {
        
        // Validation de la route
        return validateRoute(route)
            .flatMap(valid -> routeRepository.save(Mono.just(route)))
            .then(Mono.defer(() -> {
                // Rafraîchir les routes
                eventPublisher.publishEvent(new RefreshRoutesEvent(this));
                
                log.info("Route created/updated: {}", route.getId());
                return Mono.just(ResponseEntity.ok(
                    "Route " + route.getId() + " created/updated successfully"));
            }))
            .onErrorResume(error -> {
                log.error("Failed to create/update route", error);
                return Mono.just(ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create/update route: " + error.getMessage()));
            });
    }
    
    /**
     * Supprime une route
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteRoute(@PathVariable String id) {
        return routeRepository.delete(Mono.just(id))
            .then(Mono.defer(() -> {
                eventPublisher.publishEvent(new RefreshRoutesEvent(this));
                log.info("Route deleted: {}", id);
                return Mono.just(ResponseEntity.ok(
                    "Route " + id + " deleted successfully"));
            }))
            .onErrorResume(error -> {
                log.error("Failed to delete route", error);
                return Mono.just(ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete route: " + error.getMessage()));
            });
    }
    
    /**
     * Rafraîchit toutes les routes (recharge depuis Redis)
     */
    @PostMapping("/refresh")
    public Mono<ResponseEntity<String>> refreshRoutes() {
        eventPublisher.publishEvent(new RefreshRoutesEvent(this));
        log.info("Routes refresh triggered manually");
        return Mono.just(ResponseEntity.ok("Routes refreshed successfully"));
    }
    
    /**
     * Valide une définition de route
     */
    private Mono<Boolean> validateRoute(RouteDefinition route) {
        return Mono.fromCallable(() -> {
            // Validation de l'ID
            if (route.getId() == null || route.getId().isBlank()) {
                throw new IllegalArgumentException("Route ID is required");
            }
            
            // Validation de l'URI
            if (route.getUri() == null) {
                throw new IllegalArgumentException("Route URI is required");
            }
            
            // Validation qu'il y a au moins un prédicat
            if (route.getPredicates() == null || route.getPredicates().isEmpty()) {
                throw new IllegalArgumentException(
                    "Route must have at least one predicate");
            }
            
            // Validation des noms de filtres (doivent exister)
            Set<String> validFilters = Set.of(
                "StripPrefix", "CircuitBreaker", "RequestRateLimiter", 
                "Retry", "AddRequestHeader", "AddResponseHeader",
                "RewritePath", "SetPath", "SetStatus"
            );
            
            for (FilterDefinition filter : route.getFilters()) {
                if (!validFilters.contains(filter.getName())) {
                    throw new IllegalArgumentException(
                        "Unknown filter: " + filter.getName());
                }
            }
            
            return true;
        });
    }
}
```

### Prédicats avancés

Les prédicats déterminent si une route correspond à une requête donnée. Spring Cloud Gateway fournit de nombreux prédicats prédéfinis, mais nous pouvons aussi créer des prédicats personnalisés pour des cas d'usage complexes.

**Prédicates standards les plus utilisés** :

```yaml
predicates:
  # Chemin - le plus courant
  - Path=/api/search/**
  
  # Méthode HTTP
  - Method=GET,POST
  
  # Header présent avec valeur spécifique
  - Header=X-Request-Type, mobile
  
  # Query parameter présent
  - Query=debug, true
  
  # Cookie présent
  - Cookie=session, .+
  
  # Host/domain
  - Host=**.yowyob.com
  
  # Adresse IP source
  - RemoteAddr=192.168.1.0/24
  
  # Timestamp (route active seulement à certaines heures)
  - Between=2025-01-01T00:00:00+00:00,2025-12-31T23:59:59+00:00
  
  # Poids (pour A/B testing)
  - Weight=group1, 80  # 80% du trafic vers cette route
```

**Prédicat personnalisé basé sur l'authentification** :

```java
@Component
public class AuthenticatedRoutePredicateFactory 
        extends AbstractRoutePredicateFactory<AuthenticatedRoutePredicateFactory.Config> {
    
    public AuthenticatedRoutePredicateFactory() {
        super(Config.class);
    }
    
    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return exchange -> {
            String userId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Id");
            
            // Requête est matchée seulement si utilisateur authentifié
            boolean authenticated = userId != null && !userId.isBlank();
            
            // Si config demande l'inverse (unauthenticated), inverser
            return config.isNegate() ? !authenticated : authenticated;
        };
    }
    
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
```

Usage dans la configuration :

```yaml
routes:
  - id: authenticated_only
    uri: lb://USER-SERVICE
    predicates:
      - Path=/api/users/profile/**
      - Authenticated=false  # false = negate, donc unauthenticated only
```

**Prédicat basé sur le rôle utilisateur** :

```java
@Component
public class HasRoleRoutePredicateFactory 
        extends AbstractRoutePredicateFactory<HasRoleRoutePredicateFactory.Config> {
    
    public HasRoleRoutePredicateFactory() {
        super(Config.class);
    }
    
    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return exchange -> {
            String rolesHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Roles");
            
            if (rolesHeader == null || rolesHeader.isBlank()) {
                return false;
            }
            
            Set<String> userRoles = Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
            
            // Vérifier si l'utilisateur a l'un des rôles requis
            return userRoles.stream()
                .anyMatch(role -> config.getRoles().contains(role));
        };
    }
    
    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("roles");
    }
    
    @Validated
    public static class Config {
        private Set<String> roles = new HashSet<>();
        
        public Set<String> getRoles() {
            return roles;
        }
        
        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }
    }
}
```

Usage :

```yamlroutes:
  - id: admin_only
    uri: lb://STATS-SERVICE
    predicates:
      - Path=/api/admin/**
      - HasRole=ADMIN,SUPER_ADMIN
```

**Prédicat pour A/B testing** :

```java
@Component
public class FeatureFlagRoutePredicateFactory 
        extends AbstractRoutePredicateFactory<FeatureFlagRoutePredicateFactory.Config> {
    
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    
    public FeatureFlagRoutePredicateFactory(
            ReactiveRedisTemplate<String, String> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return exchange -> {
            // Vérifier si le feature flag est activé dans Redis
            String flagKey = "feature:flag:" + config.getFeatureName();
            
            Boolean enabled = redisTemplate.opsForValue()
                .get(flagKey)
                .map(Boolean::parseBoolean)
                .defaultIfEmpty(false)
                .block(Duration.ofMillis(100)); // Timeout court
            
            return Boolean.TRUE.equals(enabled);
        };
    }
    
    @Validated
    public static class Config {
        @NotBlank
        private String featureName;
        
        public String getFeatureName() {
            return featureName;
        }
        
        public void setFeatureName(String featureName) {
            this.featureName = featureName;
        }
    }
}
```

Usage pour router vers une nouvelle version d'un service si le feature flag est activé :

```yaml
routes:
  # Route vers la nouvelle version (si feature activé)
  - id: search_v2
    uri: lb://SEARCH-SERVICE-V2
    predicates:
      - Path=/api/search/**
      - FeatureFlag=search_v2
    order: 1
  
  # Route vers la version actuelle (fallback)
  - id: search_v1
    uri: lb://SEARCH-SERVICE
    predicates:
      - Path=/api/search/**
    order: 2
```

### Filtres de transformation

Les filtres permettent de modifier les requêtes et les réponses de manière fine.

**Filtre de réecriture de chemin** :

```java
@Component
@Order(5)
public class PathRewriteGatewayFilterFactory 
        extends AbstractGatewayFilterFactory<PathRewriteGatewayFilterFactory.Config> {
    
    public PathRewriteGatewayFilterFactory() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String originalPath = request.getURI().getPath();
            
            // Appliquer la regex de remplacement
            String newPath = originalPath.replaceAll(
                config.getRegexp(), 
                config.getReplacement());
            
            if (!originalPath.equals(newPath)) {
                ServerHttpRequest mutatedRequest = request.mutate()
                    .path(newPath)
                    .build();
                
                log.debug("Rewrote path: {} -> {}", originalPath, newPath);
                
                return chain.filter(exchange.mutate()
                    .request(mutatedRequest)
                    .build());
            }
            
            return chain.filter(exchange);
        };
    }
    
    @Validated
    public static class Config {
        @NotBlank
        private String regexp;
        
        @NotBlank
        private String replacement;
        
        // Getters/Setters
    }
}
```

**Filtre d'ajout de headers conditionnels** :

```java
@Component
public class ConditionalHeaderGatewayFilterFactory 
        extends AbstractGatewayFilterFactory<ConditionalHeaderGatewayFilterFactory.Config> {
    
    public ConditionalHeaderGatewayFilterFactory() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Évaluer la condition
            boolean conditionMet = evaluateCondition(
                request, config.getCondition());
            
            if (conditionMet) {
                ServerHttpRequest mutatedRequest = request.mutate()
                    .header(config.getHeaderName(), config.getHeaderValue())
                    .build();
                
                return chain.filter(exchange.mutate()
                    .request(mutatedRequest)
                    .build());
            }
            
            return chain.filter(exchange);
        };
    }
    
    private boolean evaluateCondition(
            ServerHttpRequest request, 
            String condition) {
        
        // Format: "header:X-Custom-Header:value"
        // ou "query:param:value"
        // ou "method:GET"
        String[] parts = condition.split(":", 3);
        
        return switch (parts[0]) {
            case "header" -> {
                String headerValue = request.getHeaders().getFirst(parts[1]);
                yield parts.length == 3 && parts[2].equals(headerValue);
            }
            case "query" -> {
                String queryValue = request.getQueryParams().getFirst(parts[1]);
                yield parts.length == 3 && parts[2].equals(queryValue);
            }
            case "method" -> 
                request.getMethod().name().equals(parts[1]);
            default -> false;
        };
    }
    
    @Validated
    public static class Config {
        @NotBlank
        private String condition;
        
        @NotBlank
        private String headerName;
        
        @NotBlank
        private String headerValue;
        
        // Getters/Setters
    }
}
```

Usage :

```yaml
filters:
  # Ajouter header X-Mobile-App: true si User-Agent contient "Mobile"
  - ConditionalHeader=header:User-Agent:Mobile, X-Mobile-App, true
```

---

## 🔗 Intégration avec les autres modules

### Dépendances avec yowyob-common

Le module `yowyob-common` fournit des classes et utilitaires partagés entre tous les services, ce qui évite la duplication de code et garantit la cohérence. Le Gateway utilise intensivement ces composants partagés.

**JwtService** : Service de validation et manipulation des JWT

```java
// Dans yowyob-common
@Service
public class JwtService {
    
    private final PublicKey publicKey;
    private final JwtParser jwtParser;
    
    public JwtService(@Value("${jwt.public-key}") String publicKeyPath) 
            throws Exception {
        this.publicKey = loadPublicKey(publicKeyPath);
        this.jwtParser = Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build();
    }
    
    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    public String extractUserId(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    
    public Set<Role> extractRoles(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        @SuppressWarnings("unchecked")
        List<String> roleStrings = claims.get("roles", List.class);
        return roleStrings.stream()
            .map(Role::valueOf)
            .collect(Collectors.toSet());
    }
    
    public boolean isTokenExpired(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        return claims.getExpiration().before(new Date());
    }
    
    private PublicKey loadPublicKey(String path) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(path));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
```

Le Gateway injecte ce service dans `JwtAuthenticationFilter` pour valider les tokens sans dupliquer la logique de validation.

**ErrorResponse** : Classe standardisée pour les réponses d'erreur

```java
// Dans yowyob-common
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private Instant timestamp;
    private Map<String, Object> details;
    
    public String toJson() {
        try {
            return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Failed to serialize error response\"}";
        }
    }
}
```

Tous les services (Gateway, Search, User, etc.) utilisent cette même classe pour retourner les erreurs, garantissant une interface cohérente pour les clients.

**AppException et ses sous-classes** :

```java
// Dans yowyob-common
public class AppException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;
    private final Map<String, Object> details;
    
    // Constructeurs, getters...
}

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", message, null);
    }
}

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message, null);
    }
}

// ... autres exceptions
```

Le Gateway peut attraper ces exceptions et les convertir automatiquement en réponses HTTP appropriées grâce au `GlobalErrorHandler`.

**Constants** : Constantes partagées

```java
// Dans yowyob-common
public final class SecurityConstants {
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLES = "X-User-Roles";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String HEADER_TRACE_ID = "X-B3-TraceId";
    public static final String TOKEN_PREFIX = "Bearer ";
    
    private SecurityConstants() {}
}

public final class CacheConstants {
    public static final String CACHE_PREFIX = "yowyob:cache:";
    public static final Duration DEFAULT_TTL = Duration.ofMinutes(5);
    public static final Duration SEARCH_TTL = Duration.ofMinutes(10);
    public static final Duration USER_PROFILE_TTL = Duration.ofHours(1);
    
    private CacheConstants() {}
}
```

### Communication avec le Service Discovery

Le Gateway s'enregistre lui-même auprès d'Eureka et utilise Eureka pour découvrir les services backend.

**Configuration Eureka Client** :

```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka-server:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
    registry-fetch-interval-seconds: 10
    
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
    instance-id: ${spring.application.name}:${random.value}
    metadata-map:
      zone: ${ZONE:default}
      version: ${app.version:1.0.0}
      tags: gateway,edge-service
```

**Health Check pour Eureka** :

```java
@Component
public class GatewayHealthIndicator implements HealthIndicator {
    
    private final RouteDefinitionLocator routeLocator;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    
    @Override
    public Health health() {
        try {
            // Vérifier que le Gateway peut charger ses routes
            long routeCount = routeLocator.getRouteDefinitions()
                .count()
                .block(Duration.ofSeconds(2));
            
            if (routeCount == 0) {
                return Health.down()
                    .withDetail("reason", "No routes configured")
                    .build();
            }
            
            // Vérifier la connexion Redis
            String ping = redisTemplate.execute(connection -> 
                connection.ping())
                .block(Duration.ofSeconds(1));
            
            if (!"PONG".equals(ping)) {
                return Health.down()
                    .withDetail("reason", "Redis not responding")
                    .build();
            }
            
            return Health.up()
                .withDetail("routes", routeCount)
                .withDetail("redis", "connected")
                .build();
                
        } catch (Exception e) {
            return Health.down()
                .withException(e)
                .build();
        }
    }
}
```

Ce health check est appelé par Eureka pour déterminer si l'instance du Gateway est saine. Si le health check échoue plusieurs fois consécutivement, Eureka marque l'instance comme DOWN et arrête de router du trafic vers elle.

### Gestion du Load Balancing

Le préfixe `lb://` dans les URIs de route active le load balancing automatique via Spring Cloud LoadBalancer.

**Configuration du LoadBalancer** :

```java
@Configuration
public class LoadBalancerConfiguration {
    
    /**
     * Stratégie Round Robin (par défaut)
     */
    @Bean
    @ConditionalOnMissingBean
    public ReactorServiceInstanceLoadBalancer roundRobinLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory) {
        
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        
        return new RoundRobinLoadBalancer(
            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
            name);
    }
    
    /**
     * Stratégie personnalisée basée sur la charge
     */
    @Bean
    @ConditionalOnProperty(name = "loadbalancer.strategy", havingValue = "least-response-time")
    public ReactorServiceInstanceLoadBalancer leastResponseTimeLoadBalancer(
            Environment environment,
            LoadBalancerClientFactory loadBalancerClientFactory,
            MeterRegistry meterRegistry) {
        
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        
        return new LeastResponseTimeLoadBalancer(
            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
            name,
            meterRegistry);
    }
}
```

**LoadBalancer personnalisé basé sur le temps de réponse** :

```java
public class LeastResponseTimeLoadBalancer 
        implements ReactorServiceInstanceLoadBalancer {
    
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private final String serviceId;
    private final MeterRegistry meterRegistry;
    
    // Map pour stocker les temps de réponse moyens par instance
    private final ConcurrentHashMap<String, Double> responseTimesByInstance 
        = new ConcurrentHashMap<>();
    
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = 
            serviceInstanceListSupplierProvider.getIfAvailable();
        
        if (supplier == null) {
            return Mono.empty();
        }
        
        return supplier.get(request)
            .next()
            .map(serviceInstances -> {
                if (serviceInstances.isEmpty()) {
                    return new EmptyResponse();
                }
                
                // Choisir l'instance avec le temps de réponse le plus faible
                ServiceInstance chosen = serviceInstances.stream()
                    .min(Comparator.comparingDouble(instance -> 
                        responseTimesByInstance.getOrDefault(
                            instance.getInstanceId(), 
                            100.0))) // Défaut 100ms pour nouvelles instances
                    .orElse(serviceInstances.get(0));
                
                return new DefaultResponse(chosen);
            });
    }
    
    /**
     * Met à jour les statistiques de temps de réponse
     */
    public void recordResponseTime(String instanceId, double responseTimeMs) {
        // Moyenne mobile exponentielle (EMA) avec alpha=0.3
        responseTimesByInstance.compute(instanceId, (key, oldValue) -> {
            if (oldValue == null) {
                return responseTimeMs;
            }
            return 0.3 * responseTimeMs + 0.7 * oldValue;
        });
        
        meterRegistry.gauge("loadbalancer.response.time",
            Tags.of("instance", instanceId, "service", serviceId),
            responseTimesByInstance.get(instanceId));
    }
}
```

Cette implémentation route préférentiellement les requêtes vers les instances qui répondent le plus rapidement, optimisant automatiquement les performances.

---

## ✅ Tests et qualité

### Stratégie de test globale

Notre stratégie de test suit la pyramide des tests : beaucoup de tests unitaires (rapides, isolés), moins de tests d'intégration (plus lents, testent plusieurs composants ensemble), et quelques tests end-to-end (très lents, testent tout le système).

```
           /\
          /  \  E2E Tests (5%)
         /    \
        /------\
       /        \ Integration Tests (20%)
      /          \
     /------------\
    /              \ Unit Tests (75%)
   /________________\
```

### Tests unitaires des filtres

Les filtres sont des composants critiques qui doivent être testés exhaustivement.

**Test du JwtAuthenticationFilter** :

```java
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private GatewayFilterChain chain;
    
    @InjectMocks
    private JwtAuthenticationFilter filter;
    
    private ServerWebExchange exchange;
    
    @BeforeEach
    void setUp() {
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/users/profile")
            .build();
        
        MockServerWebExchange mockExchange = MockServerWebExchange.from(request);
        this.exchange = mockExchange;
        
        // Mock chain to return completed Mono
        when(chain.filter(any())).thenReturn(Mono.empty());
    }
    
    @Test
    @DisplayName("Should allow request with valid JWT token")
    void shouldAllowValidToken() {
        // Given
        String token = "valid.jwt.token";
        String userId = "user_123";
        Set<Role> roles = Set.of(Role.USER);
        
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/users/profile")
            .header("Authorization", "Bearer " + token)
            .build();
        exchange = MockServerWebExchange.from(request);
        
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn(userId);
        when(jwtService.extractRoles(token)).thenReturn(roles);
        
        // When
        StepVerifier.create(filter.filter(exchange, chain))
            // Then
            .verifyComplete();
        
        // Verify that chain.filter was called
        verify(chain).filter(any());
        
        // Verify that user headers were added
        ServerHttpRequest mutatedRequest = exchange.getRequest();
        assertThat(mutatedRequest.getHeaders().getFirst("X-User-Id"))
            .isEqualTo(userId);
        assertThat(mutatedRequest.getHeaders().getFirst("X-User-Roles"))
            .contains("USER");
    }
    
    @Test
    @DisplayName("Should reject request with invalid token")
    void shouldRejectInvalidToken() {
        // Given
        String token = "invalid.jwt.token";
        
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/users/profile")
            .header("Authorization", "Bearer " + token)
            .build();
        exchange = MockServerWebExchange.from(request);
        
        when(jwtService.validateToken(token)).thenReturn(false);
        
        // When/Then
        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();
        
        // Verify that chain.filter was NOT called
        verify(chain, never()).filter(any());
        
        // Verify that response is 401
        assertThat(exchange.getResponse().getStatusCode())
            .isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    
    @Test
    @DisplayName("Should reject request with expired token")
    void shouldRejectExpiredToken() {
        // Given
        String token = "expired.jwt.token";
        
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/users/profile")
            .header("Authorization", "Bearer " + token)
            .build();
        exchange = MockServerWebExchange.from(request);
        
        when(jwtService.validateToken(token))
            .thenThrow(new ExpiredJwtException(null, null, "Token expired"));
        
        // When/Then
        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();
        
        verify(chain, never()).filter(any());
        assertThat(exchange.getResponse().getStatusCode())
            .isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    
    @Test
    @DisplayName("Should allow public endpoint without token")
    void shouldAllowPublicEndpoint() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/search")
            .build();
        exchange = MockServerWebExchange.from(request);
        
        // Public paths configured in filter
        filter.addPublicPath("/api/search/**");
        filter.addPublicPath("/api/auth/**");
        
        // When
        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();
        
        // Then
        verify(chain).filter(any());
        verify(jwtService, never()).validateToken(any());
    }
    
    @Test
    @DisplayName("Should reject protected endpoint without token")
    void shouldRejectProtectedEndpointWithoutToken() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/users/profile")
            .build();
        exchange = MockServerWebExchange.from(request);
        
        // When/Then
        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();
        
        verify(chain, never()).filter(any());
        assertThat(exchange.getResponse().getStatusCode())
            .isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
```

**Test du CacheFilter** :

```java
@ExtendWith(MockitoExtension.class)
class CacheFilterTest {
    
    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;
    
    @Mock
    private ReactiveValueOperations<String, String> valueOps;
    
    @Mock
    private GatewayFilterChain chain;
    
    @InjectMocks
    private CacheFilter filter;
    
    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }
    
    @Test
    @DisplayName("Should return cached response on cache hit")
    void shouldReturnCachedResponse() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/search?q=test")
            .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        String cacheKey = "gateway:cache:GET:/api/search:q=test";
        String cachedResponse = """
            {
                "status": 200,
                "headers": {"Content-Type": "application/json"},
                "body": "{\\"results\\": []}"
            }
            """;
        
        when(valueOps.get(cacheKey)).thenReturn(Mono.just(cachedResponse));
        
        // When
        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();
        
        // Then
        verify(chain, never()).filter(any()); // Chain not called
        verify(valueOps).get(cacheKey);
        
        assertThat(exchange.getResponse().getHeaders().getFirst("X-Cache-Status"))
            .isEqualTo("HIT");
    }
    
    @Test
    @DisplayName("Should forward to backend on cache miss")
    void shouldForwardOnCacheMiss() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/search?q=test")
            .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        String cacheKey = "gateway:cache:GET:/api/search:q=test";
        
        when(valueOps.get(cacheKey)).thenReturn(Mono.empty());
        when(chain.filter(any())).thenReturn(Mono.empty());
        
        // When
        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();
        
        // Then
        verify(chain).filter(any()); // Chain called
        verify(valueOps).get(cacheKey);
    }
    
    @Test
    @DisplayName("Should not cache non-GET requests")
    void shouldNotCacheNonGetRequests() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest
            .post("/api/users")
            .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        
        when(chain.filter(any())).thenReturn(Mono.empty());
        
        // When
        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete();
        
        // Then
        verify(chain).filter(any());
        verify(valueOps, never()).get(any()); // No cache lookup
    }
}
```

### Tests d'intégration

Les tests d'intégration vérifient que plusieurs composants fonctionnent correctement ensemble.

**Test d'intégration du routing** :

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class RoutingIntegrationTest {
    
    @Autowired
    private WebTestClient webClient;
    
    @MockBean
    private JwtService jwtService;
    
    @Test
    @DisplayName("Should route search requests to Search Service")
    void shouldRouteToSearchService() {
        // Given
        String validToken = "valid.token";
        when(jwtService.validateToken(validToken)).thenReturn(true);
        when(jwtService.extractUserId(validToken)).thenReturn("user_123");
        when(jwtService.extractRoles(validToken))
            .thenReturn(Set.of(Role.USER));
        
        // Mock Search Service with WireMock
        stubFor(get(urlEqualTo("/search"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"results\": []}")));
        
        // When/Then
        webClient.get()
            .uri("/api/search")
            .header("Authorization", "Bearer " + validToken)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().valueEquals("Content-Type", "application/json")
            .expectBody()
            .jsonPath("$.results").isArray();
        
        // Verify that request was forwarded to Search Service
        verify(getRequestedFor(urlEqualTo("/search"))
            .withHeader("X-User-Id", equalTo("user_123")));
    }
    
    @Test
    @DisplayName("Should return 404 for non-existent route")
    void shouldReturn404ForNonExistentRoute() {
        webClient.get()
            .uri("/api/nonexistent")
            .exchange()
            .expectStatus().isNotFound();
    }
    
    @Test
    @DisplayName("Should apply rate limiting")
    void shouldApplyRateLimiting() {
        // Given - rate limit is 10 requests per minute
        String token = "valid.token";
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn("user_123");
        
        // When - make 11 requests rapidly
        List<Integer> statusCodes = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            webClient.get()
                .uri("/api/search")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectBody()
                .consumeWith(result -> 
                    statusCodes.add(result.getStatus().value()));
        }
        
        // Then - 11th request should be rate limited
        assertThat(statusCodes.subList(0, 10))
            .allMatch(status -> status == 200);
        assertThat(statusCodes.get(10)).isEqualTo(429);
    }
}
```

**Test d'intégration du Circuit Breaker** :

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
class CircuitBreakerIntegrationTest {
    
    @Autowired
    private WebTestClient webClient;
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    
    @Test
    @DisplayName("Should open circuit breaker after multiple failures")
    void shouldOpenCircuitBreakerAfterFailures() {
        // Given - Mock Search Service to always fail
        stubFor(get(urlMatching("/search.*"))
            .willReturn(aResponse()
                .withStatus(500)
                .withFixedDelay(3000))); // Slow response
        
        CircuitBreaker cb = circuitBreakerRegistry
            .circuitBreaker("searchCircuitBreaker");
        
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
        
        // When - Make enough requests to trigger circuit breaker
        for (int i = 0; i < 10; i++) {
            webClient.get()
                .uri("/api/search")
                .exchange()
                .expectStatus().is5xxServerError();
        }
        
        // Then - Circuit breaker should be open
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        
        // Further requests should fail fast without hitting backend
        long startTime = System.currentTimeMillis();
        webClient.get()
            .uri("/api/search")
            .exchange()
            .expectStatus().isEqualTo(503);
        long duration = System.currentTimeMillis() - startTime;
        
        // Should be very fast (< 100ms) because CB is open
        assertThat(duration).isLessThan(100);}
    
    @Test
    @DisplayName("Should transition to half-open and close after recovery")
    void shouldTransitionToHalfOpenAndClose() throws Exception {
        // Given - Circuit breaker is open
        CircuitBreaker cb = circuitBreakerRegistry
            .circuitBreaker("searchCircuitBreaker");
        cb.transitionToOpenState();
        
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        
        // Wait for waitDurationInOpenState
        Thread.sleep(6000);
        
        // Mock successful responses
        stubFor(get(urlMatching("/search.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"results\": []}")));
        
        // When - Make requests that succeed
        for (int i = 0; i < 3; i++) {
            webClient.get()
                .uri("/api/search")
                .exchange()
                .expectStatus().isOk();
        }
        
        // Then - Circuit breaker should be closed
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
    }
}
```

### Tests de performance

Les tests de performance vérifient que le Gateway peut gérer la charge attendue.

**Test de charge avec Gatling** :

```scala
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class GatewayLoadTest extends Simulation {
  
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .header("Authorization", "Bearer ${token}")
  
  val scn = scenario("Gateway Load Test")
    .exec(http("Search Request")
      .get("/api/search?q=test")
      .check(status.is(200))
      .check(responseTimeInMillis.lte(500))) // p95 < 500ms
    .pause(1)
    .exec(http("User Profile")
      .get("/api/users/profile")
      .check(status.is(200)))
    .pause(2)
  
  setUp(
    scn.inject(
      rampUsersPerSec(1) to 100 during (2.minutes),
      constantUsersPerSec(100) during (5.minutes),
      rampUsersPerSec(100) to 1 during (2.minutes)
    )
  ).protocols(httpProtocol)
   .assertions(
     global.responseTime.percentile3.lte(500), // p95 < 500ms
     global.responseTime.percentile4.lte(1000), // p99 < 1s
     global.successfulRequests.percent.gte(99) // Success rate > 99%
   )
}
```

---

## 🚀 Déploiement et scalabilité

### Containerisation avec Docker

Le Gateway est packagé dans une image Docker optimisée pour la production.

**Dockerfile multi-stage** :

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy pom files for dependency resolution
COPY pom.xml .
COPY yowyob-common/pom.xml yowyob-common/
COPY yowyob-api-gateway/pom.xml yowyob-api-gateway/

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY yowyob-common/src yowyob-common/src
COPY yowyob-api-gateway/src yowyob-api-gateway/src

# Build the application
RUN mvn clean package -DskipTests -pl yowyob-api-gateway -am

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -g 1001 -S gateway && \
    adduser -u 1001 -S gateway -G gateway

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/yowyob-api-gateway/target/yowyob-api-gateway-*.jar app.jar

# Copy public key for JWT validation
COPY --chown=gateway:gateway keys/public.pem /app/keys/

# Switch to non-root user
USER gateway

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM optimization flags
ENV JAVA_OPTS="-XX:+UseG1GC \
               -XX:MaxGCPauseMillis=200 \
               -XX:+UseStringDeduplication \
               -XX:+HeapDumpOnOutOfMemoryError \
               -XX:HeapDumpPath=/tmp/heapdump.hprof \
               -Xms512m \
               -Xmx1024m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**Image optimisée** : L'utilisation de builds multi-stage réduit la taille de l'image finale à environ 250MB (vs 800MB+ si on incluait Maven et les dépendances de build). L'image runtime contient uniquement le JRE, le JAR de l'application, et les outils essentiels (curl pour les health checks).

### Déploiement Kubernetes

**Deployment manifest** :

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
  namespace: yowyob
  labels:
    app: api-gateway
    version: v1.0.0
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
        version: v1.0.0
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
        prometheus.io/path: "/actuator/prometheus"
    spec:
      serviceAccountName: api-gateway
      
      # Pod anti-affinity pour distribuer sur différents nodes
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
                        - api-gateway
                topologyKey: kubernetes.io/hostname
      
      containers:
        - name: gateway
          image: registry.yowyob.com/api-gateway:1.0.0
          imagePullPolicy: IfNotPresent
          
          ports:
            - name: http
              containerPort: 8080
              protocol: TCP
          
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            
            - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
              value: "http://eureka-server:8761/eureka/"
            
            - name: SPRING_REDIS_HOST
              valueFrom:
                secretKeyRef:
                  name: redis-credentials
                  key: host
            
            - name: SPRING_REDIS_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: redis-credentials
                  key: password
            
            - name: JWT_PUBLIC_KEY
              value: "/app/keys/public.pem"
          
          resources:
            requests:
              memory: "512Mi"
              cpu: "500m"
            limits:
              memory: "1Gi"
              cpu: "1000m"
          
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 10
            timeoutSeconds: 3
            failureThreshold: 3
          
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 5
            timeoutSeconds: 3
            failureThreshold: 3
          
          volumeMounts:
            - name: jwt-keys
              mountPath: /app/keys
              readOnly: true
      
      volumes:
        - name: jwt-keys
          secret:
            secretName: jwt-keys
            items:
              - key: public.pem
                path: public.pem
```

**Service manifest** :

```yaml
apiVersion: v1
kind: Service
metadata:
  name: api-gateway
  namespace: yowyob
  labels:
    app: api-gateway
spec:
  type: ClusterIP
  ports:
    - port: 8080
      targetPort: http
      protocol: TCP
      name: http
  selector:
    app: api-gateway
```

**HorizontalPodAutoscaler** :

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: api-gateway-hpa
  namespace: yowyob
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: api-gateway
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
    - type: Pods
      pods:
        metric:
          name: http_requests_per_second
        target:
          type: AverageValue
          averageValue: "1000"
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
        - type: Percent
          value: 50
          periodSeconds: 60
        - type: Pods
          value: 2
          periodSeconds: 60
      selectPolicy: Max
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
        - type: Percent
          value: 25
          periodSeconds: 60
      selectPolicy: Min
```

Cette configuration scale automatiquement le Gateway entre 3 et 10 replicas selon la charge CPU, mémoire et le nombre de requêtes par seconde.

---

## 🔧 Dépannage et maintenance

### Problèmes courants et solutions

**Problème : Circuit Breaker s'ouvre fréquemment**

*Symptômes* : Alertes répétées "CircuitBreakerOpen", requêtes retournant 503.

*Diagnostic* :
1. Vérifier les métriques du service backend dans Grafana
2. Examiner les logs du service pour identifier les erreurs
3. Vérifier la latence des dépendances (DB, Elasticsearch, etc.)

*Solutions* :
- Si le service est réellement en panne : corriger le problème sous-jacent
- Si c'est un faux positif (seuil trop sensible) : ajuster `failureRateThreshold` ou `slowCallDurationThreshold`
- Si c'est temporaire : forcer la fermeture du CB via l'API admin

```bash
curl -X POST http://gateway-admin:8080/admin/circuit-breakers/searchCircuitBreaker/force-close \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

**Problème : Latence élevée du Gateway**

*Symptômes* : p95 latency > 1s, plaintes utilisateurs.

*Diagnostic* :
1. Vérifier les traces dans Jaeger pour identifier le goulot
2. Vérifier les métriques JVM (GC pauses, memory usage)
3. Vérifier la latence Redis (cache)

*Solutions* :
- Si GC pauses fréquentes : augmenter heap size ou optimiser GC
- Si latence backend : optimiser le service backend
- Si latence Redis : vérifier la connectivité réseau ou scaler Redis

**Problème : Taux d'erreur élevé**

*Symptômes* : Alertes "HighErrorRate", dashboard montre beaucoup de 5xx.

*Diagnostic* :
1. Filtrer les logs par status=5xx pour voir les erreurs
2. Vérifier quel endpoint a le plus d'erreurs
3. Examiner les stack traces

*Solutions* :
- Si erreurs timeout : augmenter les timeouts ou optimiser backend
- Si erreurs 502/503 : vérifier que les services backend sont up
- Si erreurs 500 : bug dans un service, nécessite fix

**Commandes utiles pour le debugging** :

```bash
# Logs en temps réel
kubectl logs -f -n yowyob -l app=api-gateway

# Logs des 100 dernières erreurs
kubectl logs -n yowyob -l app=api-gateway --tail=1000 | grep ERROR

# Exec dans un pod pour debugging
kubectl exec -it -n yowyob api-gateway-xxx -- /bin/sh

# Port-forward pour accéder aux actuator endpoints localement
kubectl port-forward -n yowyob svc/api-gateway 8080:8080

# Vérifier les métriques
curl http://localhost:8080/actuator/metrics/gateway.requests.total

# Vérifier les routes configurées
curl http://localhost:8080/actuator/gateway/routes

# Health check détaillé
curl http://localhost:8080/actuator/health
```

### Procédures de maintenance

**Rolling restart sans downtime** :

```bash
# Kubernetes fait un rolling restart automatiquement
kubectl rollout restart deployment/api-gateway -n yowyob

# Surveiller le progrès
kubectl rollout status deployment/api-gateway -n yowyob
```

**Mise à jour des routes dynamiques** :

```bash
# Via l'API admin
curl -X POST http://gateway-admin:8080/admin/routes \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d @new-route.json

# Refresh des routes
curl -X POST http://gateway-admin:8080/admin/routes/refresh \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

---

## 📚 Conclusion

L'API Gateway de YowYob Search est bien plus qu'un simple proxy. C'est un composant critique qui orchestre l'ensemble de notre architecture microservices en fournissant :

- **Sécurité centralisée** avec validation JWT et autorisation basée sur les rôles
- **Résilience** via Circuit Breakers, retries et fallbacks
- **Protection** contre les abus avec rate limiting intelligent
- **Performance** grâce au caching et au load balancing
- **Observabilité** complète avec métriques, logs et traces

Son architecture réactive basée sur Spring Cloud Gateway et Project Reactor lui permet de gérer des milliers de requêtes simultanées avec une faible latence et une utilisation efficace des ressources.

La capacité de configurer dynamiquement les routes, de monitorer en temps réel, et de scaler automatiquement selon la charge en fait une solution robuste et production-ready pour notre plateforme YowYob Search.Ce projet a été développé par l'équipe YowYob dans le cadre de notre projet de 4ème année à l'École Nationale Supérieure Polytechnique de Yaoundé (ENSPY).

**Équipe** : YowYob Team 4GI-ENSPY Promo 2027
- Brian Brusly - Lead Backend & Architecture
- yowyob.4gi.enspy.promo.2027@gmail.com

---

**L'API-Gateway Service** - *Le réseau de canneaux intelligents de YowYob Search*