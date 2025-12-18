# Module Common - YowYob Search Platform

> **Module fondamental de l'architecture microservices de YowYob Search**  
> Fournit les composants réutilisables, les configurations standardisées et les utilitaires partagés pour l'ensemble de la plateforme

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

---

## 📑 Table des matières

- [Vue d'ensemble](#-vue-densemble)
- [Architecture du module](#-architecture-du-module)
- [Structure détaillée](#-structure-détaillée)
- [Composants principaux](#-composants-principaux)
- [Configuration et utilisation](#-configuration-et-utilisation)
- [Intégration avec les autres modules](#-intégration-avec-les-autres-modules)
- [Tests et qualité](#-tests-et-qualité)
- [Principes de conception](#-principes-de-conception)
- [Guide de développement](#-guide-de-développement)
- [Maintenance et évolution](#-maintenance-et-évolution)

---

## 🎯 Vue d'ensemble

### Introduction au module Common

Le module **yowyob-common** représente le socle technique de toute la plateforme YowYob Search. Conçu selon une approche modulaire et orientée réutilisation, ce module centralise l'ensemble des fonctionnalités transversales qui sont communes à tous les microservices de l'application. En adoptant cette architecture, nous évitons la duplication de code, nous garantissons une cohérence technique à travers toute la plateforme, et nous facilitons grandement la maintenance et l'évolution du système.

Ce module a été pensé pour être complètement autonome et testable indépendamment. Chaque composant qu'il contient peut fonctionner de manière isolée, ce qui permet aux développeurs de travailler sur le module common sans avoir besoin de déployer l'ensemble de la plateforme. Cette approche modulaire facilite également l'intégration continue et le déploiement continu, en permettant des tests unitaires rapides et des builds indépendants.

### Rôle dans l'écosystème YowYob

Dans l'architecture globale de YowYob Search, qui se compose de trois repositories principaux (Backend, Frontend et Infrastructure), le module common joue un rôle de bibliothèque partagée au sein du repository Backend. Il fournit une boîte à outils complète que tous les autres microservices (API Gateway, Search Service, User Service, Geo Service, Crawler Service, Notification Service, Shop Service et Stats Service) peuvent utiliser pour accéder à des fonctionnalités standardisées.

Grâce à ce module, nous avons créé un écosystème cohérent où tous les services partagent les mêmes conventions de nommage, les mêmes structures de données, les mêmes mécanismes de gestion d'erreurs et les mêmes outils de sécurité. Cette uniformité technique réduit considérablement la courbe d'apprentissage pour les nouveaux développeurs qui rejoignent le projet, et elle simplifie le débogage en offrant des comportements prévisibles à travers toute la plateforme.

### Objectifs et bénéfices

Le module common a été créé avec plusieurs objectifs clés en tête. Premièrement, il vise à éliminer complètement la duplication de code en centralisant toutes les fonctionnalités qui sont utilisées par plusieurs microservices. Au lieu que chaque service réimplémente sa propre logique de validation d'email, de génération de tokens JWT ou de gestion des erreurs, tous ces composants sont fournis de manière centralisée et testée.

Deuxièmement, le module garantit une cohérence technique à travers toute la plateforme. Tous les services utilisent les mêmes DTOs pour la communication, les mêmes formats de réponse API, les mêmes codes d'erreur et les mêmes mécanismes de logging. Cette uniformité améliore considérablement l'expérience utilisateur et facilite le débogage et la maintenance.

Troisièmement, le module améliore la qualité du code en fournissant des composants bien testés et éprouvés. Plutôt que de réinventer la roue à chaque fois qu'un nouveau service a besoin d'une fonctionnalité commune, les développeurs peuvent s'appuyer sur des composants qui ont déjà été testés de manière approfondie et qui sont utilisés en production.

Enfin, le module facilite l'évolution de la plateforme. Lorsqu'une amélioration ou une correction de bug est apportée au module common, tous les services qui l'utilisent en bénéficient automatiquement après une simple mise à jour de dépendance. Cette approche centralisée accélère considérablement le déploiement d'améliorations à travers toute la plateforme.

---

## 🏗 Architecture du module

### Diagramme de dépendances

```
┌─────────────────────────────────────────────────────────────────┐
│                    YOWYOB SEARCH PLATFORM                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────────┐    ┌──────────────────────────────────┐   │
│  │   API GATEWAY    │    │     MICROSERVICES BACKEND        │   │
│  │   (Port 8080)    │    │  ┌────────────────────────────┐  │   │
│  └────────┬─────────┘    │  │  Search Service (8082)     │  │   │
│           │              │  │  User Service (8083)       │  │   │
│           │              │  │  Geo Service (8084)        │  │   │
│           │              │  │  Crawler Service (8085)    │  │   │
│           │              │  │  Notification Service(8086)│  │   │
│           │              │  │  Shop Service (8087)       │  │   │
│           │              │  │  Stats Service (8088)      │  │   │
│           │              │  └────────────┬───────────────┘  │   │
│           │              │               │                  │   │
│           └──────────────┼───────────────┘                  │   │
│                          │                                  │   │
│                          ▼                                  │   │
│           ┌────────────────────────────────────────────┐    │   │
│           │        YOWYOB-COMMON MODULE                │    │   │
│           │  (Bibliothèque partagée transversale)      │    │   │
│           │                                            │    │   │
│           │  ┌──────────────────────────────────────┐  │    │   │
│           │  │  Configuration partagée              │  │    │   │
│           │  │  • CORS, Jackson, DateTime           │  │    │   │
│           │  │  • Redis, Kafka, Elasticsearch       │  │    │   │
│           │  │  • Async, WebClient                  │  │    │   │
│           │  └──────────────────────────────────────┘  │    │   │
│           │                                            │    │   │
│           │  ┌──────────────────────────────────────┐  │    │   │
│           │  │  DTOs standardisés                   │  │    │   │
│           │  │  • ApiResponse<T>                    │  │    │   │
│           │  │  • PageResponse<T>                   │  │    │   │
│           │  │  • ErrorResponse                     │  │    │   │
│           │  └──────────────────────────────────────┘  │    │   │
│           │                                            │    │   │
│           │  ┌──────────────────────────────────────┐  │    │   │
│           │  │  Gestion des exceptions              │  │    │   │
│           │  │  • AppException (hiérarchie)         │  │    │   │
│           │  │  • GlobalExceptionHandler            │  │    │   │
│           │  └──────────────────────────────────────┘  │    │   │
│           │                                            │    │   │
│           │  ┌──────────────────────────────────────┐  │    │   │
│           │  │  Sécurité                            │  │    │   │
│           │  │  • JWT (génération/validation)       │  │    │   │
│           │  │  • Password (hashing/validation)     │  │    │   │
│           │  │  • Encryption (AES, RSA)             │  │    │   │
│           │  └──────────────────────────────────────┘  │    │   │
│           │                                            │    │   │
│           │  ┌──────────────────────────────────────┐  │    │   │
│           │  │  Utilitaires                         │  │    │   │
│           │  │  • Date/Time, String, Validation     │  │    │   │
│           │  │  • File, Network, Geo                │  │    │   │
│           │  └──────────────────────────────────────┘  │    │   │
│           │                                            │    │   │
│           │  ┌──────────────────────────────────────┐  │    │   │
│           │  │  Mappers (MapStruct)                 │  │    │   │
│           │  │  • Entity ↔ DTO conversions          │  │    │   │
│           │  └──────────────────────────────────────┘  │    │   │
│           │                                            │    │   │
│           │  ┌──────────────────────────────────────┐  │    │   │
│           │  │  Annotations personnalisées          │  │    │   │
│           │  │  • @LogExecutionTime, @RateLimited   │  │    │   │
│           │  └──────────────────────────────────────┘  │    │   │
│           │                                            │    │   │
│           │  ┌──────────────────────────────────────┐  │    │   │
│           │  │  Constantes globales                 │  │    │   │
│           │  │  • App, Error, HTTP, Cache, etc.     │  │    │   │
│           │  └──────────────────────────────────────┘  │    │   │
│           │                                            │    │   │
│           │  ┌──────────────────────────────────────┐  │    │   │
│           │  │  Système d'événements                │  │    │   │
│           │  │  • BaseEvent, DomainEvent            │  │    │   │
│           │  │  • EventPublisher, EventListener     │  │    │   │
│           │  └──────────────────────────────────────┘  │    │   │
│           └────────────────────────────────────────────┘    │   │
│                                                             │   │
└─────────────────────────────────────────────────────────────────┘

Légende:
  ┌─┐  Module/Composant
  │ │  
  └─┘
  
  ───▶ Dépendance directe
  
  • Point de fonctionnalité
```

### Positionnement dans l'architecture globale

Le module common se situe au cœur de l'architecture backend de YowYob Search. Alors que le repository complet est divisé en trois grandes parties (Backend, Frontend et Infrastructure), le module common fait partie intégrante du Backend et sert de bibliothèque partagée pour tous les microservices.

Dans cette architecture, le module common ne communique pas directement avec les bases de données, les services de messagerie ou d'autres composants d'infrastructure. Au lieu de cela, il fournit les abstractions et les configurations nécessaires pour que les microservices puissent interagir avec ces composants de manière standardisée. Par exemple, le module common définit la configuration Redis de base, mais c'est chaque microservice qui instancie et utilise réellement le client Redis selon ses besoins spécifiques.

Cette séparation des responsabilités garantit que le module common reste léger, testable et facile à maintenir. Il ne contient que du code qui est réellement partagé entre plusieurs services, sans inclure de logique métier spécifique à un service particulier. Cette approche respecte le principe de responsabilité unique (Single Responsibility Principle) et facilite grandement l'évolution indépendante de chaque composant.

### Principe d'indépendance et de testabilité

Un des principes fondamentaux du module common est son indépendance complète. Le module peut être construit, testé et déployé sans avoir besoin d'aucun autre module du projet. Cette indépendance est garantie par une gestion stricte des dépendances : le module common ne dépend que de bibliothèques externes bien établies (Spring Framework, Jackson, MapStruct, etc.) et n'a aucune dépendance vers les autres modules du projet YowYob.

Cette indépendance facilite considérablement le développement et les tests. Les développeurs peuvent travailler sur le module common sans avoir besoin de lancer l'ensemble de la plateforme. Ils peuvent exécuter les tests unitaires en quelques secondes et obtenir un feedback immédiat sur leurs modifications. Cette rapidité de feedback accélère le développement et réduit le nombre de bugs introduits.

De plus, l'indépendance du module permet une meilleure parallélisation du travail. Différents développeurs peuvent travailler simultanément sur le module common et sur les différents microservices sans créer de conflits ou de blocages. Chaque modification du module common peut être testée et validée avant d'être intégrée dans les microservices, ce qui réduit les risques de régression.

---

## 📂 Structure détaillée

### Vue d'ensemble de l'arborescence

Le module common est organisé en une structure claire et logique qui reflète les différentes catégories de fonctionnalités qu'il fournit. Voici l'arborescence complète du module avec une description de chaque élément :

```
yowyob-common/
│
├── 📄 pom.xml                                 # Définition Maven du module
│   └── Gère les dépendances Spring Boot, sécurité, validation
│
├── 📁 src/main/java/com/yowyob/common/
│   │
│   ├── 📁 config/                             # Configurations Spring partagées
│   │   ├── 📄 CorsConfig.java                 # Configuration CORS pour tous les services
│   │   │   └── Définit les origines autorisées, méthodes HTTP, headers
│   │   │
│   │   ├── 📄 JacksonConfig.java              # Configuration JSON avec Jackson
│   │   │   └── Formats de date, gestion des nulls, naming strategy
│   │   │
│   │   ├── 📄 DateTimeConfig.java             # Configuration dates/heures
│   │   │   └── Timezone UTC, formateurs, convertisseurs
│   │   │
│   │   ├── 📄 RedisConfig.java                # Configuration client Redis
│   │   │   └── Pool de connexions, sérialiseurs, timeouts
│   │   │
│   │   ├── 📄 KafkaConfig.java                # Configuration Kafka
│   │   │   └── Producteurs, consommateurs, sérialisation
│   │   │
│   │   ├── 📄 ElasticsearchConfig.java        # Configuration Elasticsearch
│   │   │   └── Client, index, timeouts, retry policy
│   │   │
│   │   ├── 📄 AsyncConfig.java                # Configuration exécution asynchrone
│   │   │   └── Thread pools, executors, gestion des erreurs async
│   │   │
│   │   └── 📄 WebClientConfig.java            # Configuration clients HTTP
│   │       └── Timeouts, retry, load balancing, circuit breaker
│   │
│   ├── 📁 dto/                                # Data Transfer Objects standardisés
│   │   ├── 📄 ApiResponse.java                # Réponse API générique
│   │   │   ├── Champs: success, message, data, timestamp
│   │   │   ├── Méthodes: success(), error(), with...()
│   │   │   └── Générique: ApiResponse<T> pour type-safety
│   │   │
│   │   ├── 📄 PageResponse.java               # Réponse paginée standardisée
│   │   │   ├── Champs: content, page, size, totalElements
│   │   │   ├── Navigation: first, last, empty
│   │   │   └── Méthode: of(Page<T>) pour conversion Spring Data
│   │   │
│   │   ├── 📄 ErrorResponse.java              # Réponse d'erreur détaillée
│   │   │   ├── Champs: status, error, message, path, timestamp
│   │   │   ├── Liste: validationErrors
│   │   │   └── Builder pattern pour construction fluide
│   │   │
│   │   ├── 📄 ValidationError.java            # Erreur de validation de champ
│   │   │   ├── Champs: field, message, rejectedValue
│   │   │   └── Utilisé dans ErrorResponse
│   │   │
│   │   └── 📄 Metadata.java                   # Métadonnées des réponses
│   │       ├── Champs: version, executionTime, serverId
│   │       └── Informations de contexte pour les réponses API
│   │
│   ├── 📁 exception/                          # Gestion complète des exceptions
│   │   ├── 📄 AppException.java               # Exception de base (abstraite)
│   │   │   ├── Champs: errorCode, httpStatus, details
│   │   │   ├── Méthode: withDetail(key, value) pour enrichissement
│   │   │   └── Toutes les exceptions métier en héritent
│   │   │
│   │   ├── 📄 ResourceNotFoundException.java  # Exception ressource non trouvée
│   │   │   ├── Hérite de: AppException
│   │   │   ├── HTTP Status: 404 NOT_FOUND
│   │   │   └── Usage: when(resource not found)
│   │   │
│   │   ├── 📄 ValidationException.java        # Exception validation de données
│   │   │   ├── Hérite de: AppException
│   │   │   ├── HTTP Status: 400 BAD_REQUEST
│   │   │   ├── Contient: List<ValidationError>
│   │   │   └── Usage: validation failures
│   │   │
│   │   ├── 📄 UnauthorizedException.java      # Exception non autorisé
│   │   │   ├── Hérite de: AppException
│   │   │   ├── HTTP Status: 401 UNAUTHORIZED
│   │   │   └── Usage: authentication failures
│   │   │
│   │   ├── 📄 ForbiddenException.java         # Exception accès interdit
│   │   │   ├── Hérite de: AppException
│   │   │   ├── HTTP Status: 403 FORBIDDEN
│   │   │   └── Usage: authorization failures
│   │   │
│   │   ├── 📄 ConflictException.java          # Exception conflit de données
│   │   │   ├── Hérite de: AppException
│   │   │   ├── HTTP Status: 409 CONFLICT
│   │   │   └── Usage: duplicate resources, concurrent modifications
│   │   │
│   │   ├── 📄 BadRequestException.java        # Exception requête invalide
│   │   │   ├── Hérite de: AppException
│   │   │   ├── HTTP Status: 400 BAD_REQUEST
│   │   │   └── Usage: malformed requests
│   │   │
│   │   ├── 📄 ServiceUnavailableException.java # Exception service indisponible
│   │   │   ├── Hérite de: AppException
│   │   │   ├── HTTP Status: 503 SERVICE_UNAVAILABLE
│   │   │   └── Usage: temporary service failures
│   │   │
│   │   ├── 📄 GlobalExceptionHandler.java     # Gestionnaire global Spring
│   │   │   ├── Annotation: @RestControllerAdvice
│   │   │   ├── Intercepte: toutes les exceptions non gérées
│   │   │   ├── Convertit: exceptions → ErrorResponse standardisé
│   │   │   ├── Logs: erreurs avec contexte (MDC)
│   │   │   └── Méthodes: @ExceptionHandler pour chaque type
│   │   │
│   │   └── 📄 ExceptionTranslator.java        # Traducteur d'exceptions
│   │       ├── Convertit: exceptions techniques → exceptions métier
│   │       ├── Exemple: SQLException → ResourceNotFoundException
│   │       └── Centralise: la logique de translation
│   │
│   ├── 📁 security/                           # Composants de sécurité
│   │   │
│   │   ├── 📁 jwt/                            # Gestion JWT complète
│   │   │   ├── 📄 JwtService.java             # Service principal JWT
│   │   │   │   ├── Méthodes: generateToken(), validateToken()
│   │   │   │   ├── Extraction: claims, userId, roles
│   │   │   │   ├── Utilise: JJWT library
│   │   │   │   └── Configuration: via JwtConfig
│   │   │   │
│   │   │   ├── 📄 JwtClaims.java              # Claims JWT standardisés
│   │   │   │   ├── Constantes: USER_ID, ROLES, ISSUED_AT
│   │   │   │   └── Type-safe claim keys
│   │   │   │
│   │   │   ├── 📄 TokenProvider.java          # Interface fournisseur tokens
│   │   │   │   ├── Méthodes: createAccessToken(), createRefreshToken()
│   │   │   │   └── Permet: différentes implémentations
│   │   │   │
│   │   │   └── 📄 JwtConfig.java              # Configuration JWT
│   │   │       ├── Properties: secret, expiration, issuer
│   │   │       ├── Validation: au démarrage
│   │   │       └── Utilisé par: JwtService
│   │   │
│   │   ├── 📁 auth/                           # Authentification
│   │   │   ├── 📄 CurrentUser.java            # Annotation utilisateur courant
│   │   │   │   ├── Annotation: @CurrentUser
│   │   │   │   ├── Résolution: via Spring Security Context
│   │   │   │   └── Usage: @CurrentUser User user
│   │   │   │
│   │   │   ├── 📄 SecurityContextHelper.java  # Utilitaire contexte sécurité
│   │   │   │   ├── Méthodes: getCurrentUser(), hasRole()
│   │   │   │   ├── Accès: simplifié au SecurityContext
│   │   │   │   └── Thread-safe
│   │   │   │
│   │   │   └── 📄 Role.java                   # Énumération des rôles
│   │   │       ├── Valeurs: USER, ADMIN, MERCHANT, MODERATOR
│   │   │       ├── Méthodes: getAuthority(), hasPermission()
│   │   │       └── Hiérarchie: des permissions
│   │   │
│   │   ├── 📁 password/                       # Gestion mots de passe
│   │   │   ├── 📄 PasswordService.java        # Service hachage
│   │   │   │   ├── Algorithme: BCrypt
│   │   │   │   ├── Méthodes: hash(), verify()
│   │   │   │   ├── Configuration: rounds (12 par défaut)
│   │   │   │   └── Thread-safe
│   │   │   │
│   │   │   └── 📄 PasswordValidator.java      # Validateur mots de passe
│   │   │       ├── Règles: longueur, complexité, dictionnaire
│   │   │       ├── Méthodes: validate(), getStrength()
│   │   │       └── Configurable: via properties
│   │   │
│   │   └── 📁 crypto/                         # Cryptographie
│   │       ├── 📄 EncryptionService.java      # Service chiffrement
│   │       │   ├── Algorithmes: AES-256, RSA
│   │       │   ├── Méthodes: encrypt(), decrypt()
│   │       │   ├── Gestion: des clés sécurisée
│   │       │   └── Usage: données sensibles
│   │       │
│   │       └── 📄 KeyGenerator.java           # Générateur de clés
│   │           ├── Génère: clés AES, RSA, EC
│   │           ├── Méthodes: generateAesKey(), generateRsaKeyPair()
│   │           └── Secure random: pour l'aléatoire
│   │
│   ├── 📁 mapper/                             # Mappers avec MapStruct
│   │   ├── 📄 BaseMapper.java                 # Interface mapper de base
│   │   │   ├── Méthodes: toDto(), toEntity()
│   │   │   ├── Générique: <E, D>
│   │   │   └── Étendu par: tous les mappers
│   │   │
│   │   ├── 📄 DtoMapper.java                  # Mapper générique DTOs
│   │   │   ├── Implémente: BaseMapper
│   │   │   ├── Configuration: MapStruct
│   │   │   └── Auto-généré: par annotation processor
│   │   │
│   │   ├── 📄 EntityMapper.java               # Mapper entités
│   │   │   ├── Opérations: sur entités JPA
│   │   │   ├── Gestion: des relations
│   │   │   └── Null-safe
│   │   │
│   │   ├── 📄 SearchMapper.java               # Mapper recherches
│   │   │   ├── Conversion: critères de recherche
│   │   │   ├── Mappings: Elasticsearch documents
│   │   │   └── Filtres: et sorts
│   │   │
│   │   └── 📄 DateMapper.java                 # Mapper dates
│   │       ├── Conversions: formats de dates
│   │       ├── Timezone: handling
│   │       └── Utilisé par: autres mappers
│   │
│   ├── 📁 util/                               # Utilitaires par domaine
│   │   │
│   │   ├── 📁 date/                           # Manipulation dates
│   │   │   ├── 📄 DateUtils.java              # Utilitaires dates
│   │   │   │   ├── Méthodes: format(), parse(), add(), subtract()
│   │   │   │   ├── Timezone: UTC par défaut
│   │   │   │   ├── Null-safe
│   │   │   │   └── Thread-safe
│   │   │   │
│   │   │   ├── 📄 TimeUtils.java              # Utilitaires temps
│   │   │   │   ├── Méthodes: measureDuration(), formatDuration()
│   │   │   │   ├── Conversions: unités de temps
│   │   │   │   └── High-precision: pour benchmarking
│   │   │   │
│   │   │   └── 📄 DateTimeFormatters.java     # Formateurs standardisés
│   │   │       ├── Constantes: ISO_DATE, ISO_DATETIME, etc.
│   │   │       ├── Custom: formatters pour l'application
│   │   │       └── Réutilisables: partout
│   │   │
│   │   ├── 📁 string/                         # Manipulation chaînes
│   │   │   ├── 📄 StringUtils.java            # Utilitaires chaînes
│   │   │   │   ├── Méthodes: isEmpty(), capitalize(), truncate()
│   │   │   │   ├── Null-safe: toutes les méthodes
│   │   │   │   └── Extensions: des commons-lang
│   │   │   │
│   │   │   ├── 📄 TextSanitizer.java          # Nettoyage de texte
│   │   │   │   ├── Méthodes: sanitizeHtml(), removeSpecialChars()
│   │   │   │   ├── Protection: XSS, injection SQL
│   │   │   │   └── Configurable: whitelist de tags
│   │   │   │
│   │   │   └── 📄 SlugGenerator.java          # Générateur de slugs
│   │   │       ├── Méthodes: toSlug(), uniqueSlug()
│   │   │       ├── Translitération: accents, caractères spéciaux
│   │   │       └── URL-friendly: garantit
│   │   │
│   │   ├── 📁 validation/                     # Validation de données
│   │   │   ├── 📄 Validators.java             # Collection de validateurs
│   │   │   │   ├── Méthodes: isValid...(...)
│   │   │   │   ├── Types: email, phone, URL, IP, etc.
│   │   │   │   └── Messages: d'erreur explicites
│   │   │   │
│   │   │   ├── 📄 EmailValidator.java         # Validateur emails
│   │   │   │   ├── Regex: RFC 5322 compliant
│   │   │   │   ├── DNS: verification optionnelle
│   │   │   │   └── Blacklist: domains
│   │   │   │
│   │   │   ├── 📄 PhoneValidator.java         # Validateur téléphones
│   │   │   │   ├── Formats: internationaux (E.164)
│   │   │   │   ├── Validation: par pays
││   │   │   └── Normalisation: des numéros
│   │   │   │
│   │   │   └── 📄 PasswordValidator.java      # Validateur mots de passe
│   │   │       ├── Règles: configurables
│   │   │       ├── Score: de force du mot de passe
│   │   │       └── Messages: suggestions d'amélioration
│   │   │
│   │   ├── 📁 file/                           # Manipulation fichiers
│   │   │   ├── 📄 FileUtils.java              # Utilitaires fichiers
│   │   │   │   ├── Méthodes: read(), write(), copy(), move()
│   │   │   │   ├── Gestion: des erreurs I/O
│   │   │   │   └── Atomic: operations
│   │   │   │
│   │   │   ├── 📄 FileValidator.java          # Validateur fichiers
│   │   │   │   ├── Validation: type MIME, extension, taille
│   │   │   │   ├── Magic numbers: vérification
│   │   │   │   └── Security: checks
│   │   │   │
│   │   │   └── 📄 FileStorageHelper.java      # Gestion stockage
│   │   │       ├── Abstraction: du stockage (local, S3, etc.)
│   │   │       ├── Méthodes: store(), retrieve(), delete()
│   │   │       └── URL generation: pour accès
│   │   │
│   │   ├── 📁 network/                        # Utilitaires réseau
│   │   │   ├── 📄 IpUtils.java                # Utilitaires IP
│   │   │   │   ├── Méthodes: isValid(), isPrivate(), getCountry()
│   │   │   │   ├── IPv4 et IPv6: support
│   │   │   │   └── Geolocation: lookup
│   │   │   │
│   │   │   ├── 📄 UrlUtils.java               # Utilitaires URLs
│   │   │   │   ├── Méthodes: parse(), build(), normalize()
│   │   │   │   ├── Validation: protocole, domaine, chemin
│   │   │   │   └── Encoding: correct
│   │   │   │
│   │   │   └── 📄 NetworkValidator.java       # Validateur réseau
│   │   │       ├── Validation: IP, URL, port
│   │   │       ├── Checks: accessibility
│   │   │       └── Security: validation
│   │   │
│   │   └── 📁 geo/                            # Utilitaires géographiques
│   │       ├── 📄 GeoUtils.java               # Calculs géographiques
│   │       │   ├── Méthodes: distance(), bearing(), midpoint()
│   │       │   ├── Formules: Haversine, Vincenty
│   │       │   └── Précision: haute
│   │       │
│   │       ├── 📄 DistanceCalculator.java     # Calculateur distances
│   │       │   ├── Unités: km, miles, nautical miles
│   │       │   ├── Algorithmes: optimisés
│   │       │   └── Caching: des résultats
│   │       │
│   │       └── 📄 CoordinateValidator.java    # Validateur coordonnées
│   │           ├── Validation: latitude, longitude
│   │           ├── Ranges: [-90, 90], [-180, 180]
│   │           └── Format: checking
│   │
│   ├── 📁 annotation/                         # Annotations personnalisées
│   │   ├── 📄 ValidateEnum.java               # Validation énumérations
│   │   │   ├── Annotation: @ValidateEnum(EnumClass.class)
│   │   │   ├── Validator: custom
│   │   │   └── Usage: sur champs enum
│   │   │
│   │   ├── 📄 LogExecutionTime.java           # Log temps d'exécution
│   │   │   ├── Annotation: @LogExecutionTime
│   │   │   ├── Aspect: AOP
│   │   │   ├── Mesure: temps avec précision
│   │   │   └── Log: niveau INFO
│   │   │
│   │   ├── 📄 Cacheable.java                  # Annotation cache custom
│   │   │   ├── Annotation: @Cacheable("cacheName")
│   │   │   ├── Support: Redis, caffeine
│   │   │   ├── TTL: configurable
│   │   │   └── Key generation: custom
│   │   │
│   │   ├── 📄 RateLimited.java                # Limitation de taux
│   │   │   ├── Annotation: @RateLimited(value=100)
│   │   │   ├── Implémentation: token bucket
│   │   │   ├── Storage: Redis
│   │   │   └── Response: 429 Too Many Requests
│   │   │
│   │   └── 📄 AuditLog.java                   # Log d'audit
│   │       ├── Annotation: @AuditLog
│   │       ├── Capture: méthode, args, user
│   │       ├── Stockage: dans audit log
│   │       └── Async: processing
│   │
│   ├── 📁 constant/                           # Constantes globales
│   │   ├── 📄 AppConstants.java               # Constantes application
│   │   │   ├── APP_NAME, APP_VERSION
│   │   │   ├── DEFAULT_TIMEZONE, DEFAULT_LOCALE
│   │   │   └── MAX_..., MIN_..., DEFAULT_...
│   │   │
│   │   ├── 📄 ErrorConstants.java             # Codes d'erreur
│   │   │   ├── Structure: CATEGORY_SPECIFIC
│   │   │   ├── Exemple: AUTH_INVALID_TOKEN
│   │   │   └── Messages: associés
│   │   │
│   │   ├── 📄 HttpConstants.java              # Constantes HTTP
│   │   │   ├── Status codes: custom
│   │   │   ├── Headers: standards
│   │   │   └── Content types: MIME
│   │   │
│   │   ├── 📄 CacheConstants.java             # Constantes cache
│   │   │   ├── Cache names: "user", "search", etc.
│   │   │   ├── TTLs: par type de cache
│   │   │   └── Key prefixes: standardisés
│   │   │
│   │   ├── 📄 SearchConstants.java            # Constantes recherche
│   │   │   ├── PAGE_SIZE, MAX_PAGE_SIZE
│   │   │   ├── DEFAULT_SORT, ALLOWED_SORTS
│   │   │   └── SEARCH_TIMEOUT
│   │   │
│   │   ├── 📄 SecurityConstants.java          # Constantes sécurité
│   │   │   ├── JWT_EXPIRATION, REFRESH_EXPIRATION
│   │   │   ├── BCRYPT_ROUNDS
│   │   │   └── ALLOWED_ORIGINS
│   │   │
│   │   └── 📄 RegexPatterns.java              # Expressions régulières
│   │       ├── EMAIL_PATTERN
│   │       ├── PHONE_PATTERN
│   │       ├── URL_PATTERN
│   │       └── Autres patterns réutilisables
│   │
│   └── 📁 event/                              # Système d'événements
│       ├── 📄 BaseEvent.java                  # Événement de base
│       │   ├── Champs: id, timestamp, type
│       │   ├── Abstract: classe
│       │   └── Tous les events: en héritent
│       │
│       ├── 📄 DomainEvent.java                # Événement de domaine
│       │   ├── Hérite de: BaseEvent
│       │   ├── Champs: aggregateId, version
│       │   └── Usage: event sourcing
│       │
│       ├── 📄 IntegrationEvent.java           # Événement d'intégration
│       │   ├── Hérite de: BaseEvent
│       │   ├── Champs: sourceService, destinationService
│       │   └── Usage: communication inter-services
│       │
│       ├── 📄 EventPublisher.java             # Émetteur d'événements
│       │   ├── Méthodes: publish(Event)
│       │   ├── Support: Kafka, RabbitMQ
│       │   └── Async: par défaut
│       │
│       └── 📄 EventListener.java              # Écouteur d'événements
│           ├── Interface: pour listeners
│           ├── Méthode: onEvent(Event)
│           └── Registration: automatique
│
├── 📁 src/main/resources/
│   ├── 📁 templates/                          # Templates réutilisables
│   │   ├── 📁 email-templates/                # Templates emails
│   │   │   ├── 📄 welcome.html                # Email de bienvenue
│   │   │   ├── 📄 password-reset.html         # Reset mot de passe
│   │   │   └── 📄 notification.html           # Notification générique
│   │   │
│   │   └── 📁 error-templates/                # Templates erreurs
│   │       ├── 📄 404.html                    # Not found
│   │       ├── 📄 500.html                    # Internal error
│   │       └── 📄 generic-error.html          # Erreur générique
│   │
│   ├── 📁 i18n/                               # Internationalisation
│   │   ├── 📄 messages.properties             # Messages par défaut (EN)
│   │   ├── 📄 messages_fr.properties          # Messages français
│   │   └── 📄 messages_en.properties          # Messages anglais
│   │
│   └── 📄 application-common.yml              # Configuration commune
│       ├── Spring: configuration de base
│       ├── Jackson: serialization settings
│       ├── Logging: patterns et levels
│       └── App: custom properties
│
└── 📁 src/test/java/com/yowyob/common/
    ├── 📁 config/                             # Tests configuration
    │   └── Tests de chargement des configs Spring
    │
    ├── 📁 dto/                                # Tests DTOs
    │   └── Serialization/deserialization tests
    │
    ├── 📁 exception/                          # Tests exceptions
    │   └── Tests du GlobalExceptionHandler
    │
    ├── 📁 security/                           # Tests sécurité
    │   ├── Tests JWT generation/validation
    │   ├── Tests password hashing
    │   └── Tests encryption/decryption
    │
    ├── 📁 util/                               # Tests utilitaires
    │   ├── Tests de tous les utilitaires
    │   └── Edge cases et validation
    │
    └── 📁 integration/                        # Tests d'intégration
        └── Tests des interactions entre composants
```

### Description des responsabilités par package

Chaque package du module common a été conçu avec un objectif spécifique et une séparation claire des responsabilités. Cette organisation permet aux développeurs de trouver rapidement le composant dont ils ont besoin et garantit que chaque fonctionnalité est placée à l'endroit logique.

Le package `config` contient toutes les configurations Spring qui sont partagées entre les microservices. Ces configurations définissent comment les composants de l'infrastructure (Redis, Kafka, Elasticsearch) doivent être configurés, comment les données JSON doivent être sérialisées, et comment les opérations asynchrones doivent être gérées. En centralisant ces configurations, nous garantissons que tous les services utilisent les mêmes paramètres et peuvent communiquer efficacement.

Le package `dto` fournit les structures de données standardisées pour la communication. Ces DTOs définissent comment les réponses API doivent être formatées, comment les erreurs doivent être représentées, et comment les données paginées doivent être structurées. Tous les microservices utilisent ces mêmes DTOs, ce qui garantit une expérience cohérente pour les clients de l'API.

Le package `exception` implémente un système complet de gestion des erreurs. Il définit une hiérarchie d'exceptions métier qui représentent différents types d'erreurs, et fournit un gestionnaire global qui intercepte toutes les exceptions et les convertit en réponses API standardisées. Ce système garantit que les erreurs sont toujours présentées de manière cohérente aux clients, avec des messages clairs et des codes de statut appropriés.

Le package `security` contient tous les composants liés à la sécurité de l'application. Il fournit des services pour la génération et la validation de tokens JWT, pour le hachage et la vérification des mots de passe, et pour le chiffrement de données sensibles. Ces composants sont essentiels pour garantir la sécurité de toute la plateforme et sont utilisés par tous les services qui gèrent des données utilisateur.

Le package `mapper` utilise MapStruct pour fournir des conversions performantes entre différentes représentations des données. Ces mappers sont essentiels pour convertir les entités de base de données en DTOs pour l'API, et vice versa. En utilisant MapStruct, nous bénéficions de conversions générées au moment de la compilation, ce qui élimine les erreurs d'exécution et améliore les performances.

Le package `util` contient des classes utilitaires organisées par domaine fonctionnel. Chaque sous-package (`date`, `string`, `validation`, `file`, `network`, `geo`) fournit des méthodes spécialisées pour manipuler un type de données particulier. Ces utilitaires sont conçus pour être thread-safe, null-safe et performants, et sont largement utilisés dans toute la plateforme.

Le package `annotation` définit des annotations personnalisées qui ajoutent des fonctionnalités transversales aux méthodes et aux classes. Ces annotations utilisent la programmation orientée aspect (AOP) pour ajouter automatiquement des comportements comme le logging du temps d'exécution, la limitation de taux ou l'audit des appels. Cette approche déclarative rend le code plus lisible et maintenable.

Le package `constant` regroupe toutes les constantes utilisées dans l'application. En centralisant ces valeurs, nous évitons la duplication de "magic numbers" dans le code et facilitons la maintenance. Si une valeur doit changer, elle peut être modifiée en un seul endroit et tous les services en bénéficient.

Le package `event` fournit un système d'événements pour la communication asynchrone. Ce système permet aux différents composants de l'application de réagir aux changements sans créer de couplage fort. Les événements peuvent être publiés localement ou distribués via Kafka pour la communication inter-services.

---

## 🔧 Composants principaux

### Configuration (Package `config`)

Le package configuration contient les beans Spring qui configurent les composants partagés de l'infrastructure. Ces configurations sont conçues pour être extensibles et peuvent être personnalisées via des propriétés d'environnement.

**CorsConfig.java** configure la politique Cross-Origin Resource Sharing (CORS) qui permet au frontend Next.js de communiquer avec les différents microservices backend. Cette configuration définit quelles origines sont autorisées à faire des requêtes, quelles méthodes HTTP sont acceptées (GET, POST, PUT, DELETE, etc.), et quels headers peuvent être envoyés. La configuration CORS est cruciale pour la sécurité de l'application, car elle empêche les sites malveillants de faire des requêtes non autorisées à notre API.

**JacksonConfig.java** configure la bibliothèque Jackson qui est utilisée pour la sérialisation et la désérialisation JSON dans toute l'application. Cette configuration définit comment les dates doivent être formatées (format ISO-8601 avec timezone UTC), comment les valeurs null doivent être gérées (elles peuvent être incluses ou omises selon le contexte), et quelle stratégie de nommage doit être utilisée pour les champs JSON (snake_case ou camelCase). En standardisant ces paramètres, nous garantissons que toutes les APIs de la plateforme génèrent et acceptent du JSON dans le même format.

**DateTimeConfig.java** configure la gestion des dates et heures dans toute l'application. Cette configuration définit que toutes les dates doivent être stockées et manipulées en UTC pour éviter les problèmes de fuseau horaire. Elle fournit également des convertisseurs qui permettent de convertir automatiquement entre les types de date Java (LocalDate, LocalDateTime, Instant) et les formats de base de données ou JSON. Cette configuration garantit que les dates sont gérées de manière cohérente partout dans l'application.

**RedisConfig.java** configure le client Redis qui est utilisé pour le cache distribué. Cette configuration définit les paramètres de connexion au serveur Redis (host, port, password), la configuration du pool de connexions (taille maximale, timeout, etc.), et les sérialiseurs à utiliser pour stocker différents types de données. Redis est essentiel pour les performances de la plateforme, car il permet de mettre en cache les résultats de recherche, les sessions utilisateur et d'autres données fréquemment accédées.

**KafkaConfig.java** configure les producteurs et consommateurs Kafka qui sont utilisés pour la communication asynchrone entre services. Cette configuration définit comment les messages doivent être sérialisés (généralement en JSON), quels sont les paramètres de performance (batch size, linger time, compression), et comment les erreurs doivent être gérées. Kafka est le backbone de l'architecture événementielle de YowYob, permettant aux services de communiquer de manière découplée et résiliente.

**ElasticsearchConfig.java** configure le client Elasticsearch qui est utilisé pour les fonctionnalités de recherche full-text. Cette configuration définit comment se connecter au cluster Elasticsearch, quels sont les timeouts appropriés pour les différentes opérations, et comment gérer les erreurs et les retries. Elasticsearch est au cœur de la fonctionnalité de recherche de YowYob, permettant des recherches rapides et pertinentes à travers des millions de documents.

**AsyncConfig.java** configure l'exécution asynchrone dans l'application. Cette configuration définit plusieurs thread pools pour différents types de tâches (I/O, CPU-intensive, etc.), ce qui permet d'optimiser les performances et d'éviter les blocages. L'exécution asynchrone est essentielle pour améliorer les temps de réponse de l'API en permettant de traiter plusieurs requêtes en parallèle.

**WebClientConfig.java** configure le client HTTP réactif (WebClient de Spring WebFlux) qui est utilisé pour les appels entre services. Cette configuration définit les timeouts, les stratégies de retry en cas d'échec, et le load balancing entre plusieurs instances d'un service. Ce client est préféré à RestTemplate car il est non-bloquant et permet de meilleures performances dans une architecture microservices.

### DTOs (Package `dto`)

Les Data Transfer Objects (DTOs) sont des structures de données standardisées qui définissent comment l'information circule entre les différentes couches de l'application et entre les microservices.

**ApiResponse.java** est le DTO générique qui encapsule toutes les réponses de l'API. Il contient un champ `success` qui indique si l'opération a réussi, un champ `message` pour fournir des informations contextuelles, un champ `data` qui contient les données de la réponse (généralement un objet ou une liste), et un `timestamp` qui indique quand la réponse a été générée. Ce format standardisé permet aux clients de l'API de traiter toutes les réponses de la même manière, qu'elles viennent du service de recherche, du service utilisateur ou de tout autre service.

**PageResponse.java** est une spécialisation d'ApiResponse pour les réponses paginées. Il contient la liste des éléments de la page actuelle, le numéro de page, la taille de la page, le nombre total d'éléments disponibles, et des indicateurs pour savoir si c'est la première ou la dernière page. Ce DTO facilite grandement l'implémentation de la pagination côté client, car toutes les informations nécessaires sont fournies de manière standardisée.

**ErrorResponse.java** définit le format standardisé pour les réponses d'erreur. Il contient le code de statut HTTP, un code d'erreur application spécifique (par exemple "AUTH_INVALID_TOKEN"), un message lisible par l'utilisateur, le chemin de la requête qui a causé l'erreur, et un timestamp. Pour les erreurs de validation, il peut également contenir une liste détaillée des champs qui ont échoué la validation avec leurs messages d'erreur respectifs.

**ValidationError.java** représente une erreur de validation sur un champ spécifique. Il contient le nom du champ, le message d'erreur, et la valeur qui a été rejetée. Ce niveau de détail permet aux clients de l'API d'afficher des messages d'erreur précis à côté de chaque champ de formulaire, améliorant ainsi l'expérience utilisateur.

**Metadata.java** contient des métadonnées supplémentaires sur les réponses API, comme la version de l'API utilisée, le temps d'exécution de la requête, l'identifiant du serveur qui a traité la requête (utile pour le débogage dans un environnement distribué), et d'autres informations contextuelles qui peuvent être utiles pour le monitoring ou le debugging.

### Exceptions (Package `exception`)

Le système de gestion des exceptions du module common fournit une hiérarchie d'exceptions métier et un mécanisme centralisé pour les convertir en réponses API cohérentes.

**AppException.java** est la classe de base abstraite pour toutes les exceptions métier de l'application. Elle contient un code d'erreur (par exemple "RESOURCE_NOT_FOUND"), un code de statut HTTP associé (404, 400, 500, etc.), et un map de détails supplémentaires qui peuvent enrichir le contexte de l'erreur. Cette classe fournit également une méthode `withDetail()` qui permet d'ajouter des informations supplémentaires de manière fluide avant de lancer l'exception.

**ResourceNotFoundException.java** est levée lorsqu'une ressource demandée n'existe pas dans le système. Par exemple, si un utilisateur demande un document avec un ID qui n'existe pas, ou si un merchant essaie d'accéder à un produit qui a été supprimé. Cette exception est automatiquement convertie en une réponse HTTP 404 avec un message approprié.

**ValidationException.java** est levée lorsque les données fournies par le client ne respectent pas les règles de validation. Cette exception peut contenir une liste détaillée de tous les champs qui ont échoué la validation, avec leurs messages d'erreur respectifs. Elle est convertie en une réponse HTTP 400 avec une liste structurée des erreurs de validation.

**UnauthorizedException.java** est levée lorsqu'un utilisateur tente d'accéder à une ressource sans être authentifié, ou avec un token d'authentification invalide ou expiré. Cette exception est convertie en une réponse HTTP 401 qui indique au client qu'il doit s'authentifier avant de pouvoir accéder à la ressource.

**ForbiddenException.java** est levée lorsqu'un utilisateur authentifié tente d'accéder à une ressource pour laquelle il n'a pas les permissions nécessaires. Par exemple, un utilisateur normal qui tente d'accéder à une fonctionnalité réservée aux administrateurs. Cette exception est convertie en une réponse HTTP 403.

**ConflictException.java** est levée lorsqu'une opération ne peut pas être complétée à cause d'un conflit avec l'état actuel du système. Par exemple, si un utilisateur essaie de créer un compte avec un email qui existe déjà, ou si deux utilisateurs tentent de modifier la même ressource en même temps. Cette exception est convertie en une réponse HTTP 409.

**BadRequestException.java** est levée lorsque la requête du client est malformée ou contient des paramètres invalides. C'est une exception générique pour les erreurs de requête qui ne tombent pas dans les autres catégories. Elle est convertie en une réponse HTTP 400.

**ServiceUnavailableException.java** est levée lorsqu'un service externe (comme Elasticsearch ou Redis) est temporairement indisponible. Cette exception permet de gérer gracieusement les pannes temporaires et est convertie en une réponse HTTP 503 qui indique au client de réessayer plus tard.

**GlobalExceptionHandler.java** est le composant central qui intercepte toutes les exceptions non gérées dans l'application. Il utilise l'annotation `@RestControllerAdvice` de Spring pour capturer les exceptions, les logger avec leur contexte complet (incluant les informations du MDC comme l'ID de requête et l'ID utilisateur), et les convertir en réponses ErrorResponse standardisées. Ce gestionnaire garantit qu'aucune exception technique (comme SQLException ou NullPointerException) ne soit exposée directement aux clients de l'API.

**ExceptionTranslator.java** est un composant utilitaire qui traduit les exceptions techniques en exceptions métier appropriées. Par exemple, si une SQLException indique qu'une contrainte d'unicité a été violée, le traducteur la convertit en ConflictException avec un message approprié. Cette traduction centralisée garantit que les exceptions métier sont toujours lancées avec le bon contexte.

### Sécurité (Package `security`)

Le package sécurité fournit tous les composants nécessaires pour sécuriser l'application, de l'authentification à l'autorisation en passant par le chiffrement.

#### Sous-package JWT

**JwtService.java** est le service principal pour la manipulation des JSON Web Tokens. Il fournit des méthodes pour générer des tokens d'accès et des refresh tokens, valider la signature et l'expiration des tokens, et extraire les informations (claims) qu'ils contiennent. Le service utilise l'algorithme RS256 (RSA avec SHA-256) pour signer les tokens, ce qui permet de vérifier leur authenticité sans partager de secret entre les services. Les tokens sont générés avec une durée de vie courte (15 minutes pour les access tokens, 7 jours pour les refresh tokens) pour limiter la fenêtre d'exploitation en cas de compromission.

**JwtClaims.java** définit les claims standardisés qui sont inclus dans les tokens JWT de l'application. Ces claims incluent l'identifiant utilisateur, les rôles de l'utilisateur, la date d'émission du token, la date d'expiration, l'émetteur du token (yowyob-search), et l'audience ciblée. En standardisant ces claims, nous garantissons que tous les services peuvent interpréter les tokens de la même manière.

**TokenProvider.java** est une interface qui définit le contrat pour les fournisseurs de tokens. Cette abstraction permet d'avoir différentes implémentations de génération de tokens (par exemple, une implémentation pour les tokens JWT et une autre pour les tokens opaques) sans changer le code des services qui les utilisent. Cette flexibilité est importante pour faciliter l'évolution du système de sécurité.

**JwtConfig.java** encapsule toutes les propriétés de configuration JWT dans un seul objet. Ces propriétés incluent les chemins vers les clés RSA (privée pour signer, publique pour vérifier), les durées d'expiration des tokens, l'émetteur et l'audience. Cette configuration est chargée au démarrage de l'application et validée pour s'assurer que toutes les propriétés nécessaires sont présentes.

#### Sous-package Auth

**CurrentUser.java** est une annotation personnalisée qui peut être utilisée sur les paramètres de méthodes de contrôleur pour injecter automatiquement l'utilisateur courant. Par exemple, au lieu d'avoir à extraire manuellement l'utilisateur du SecurityContext dans chaque méthode, un développeur peut simplement annoter un paramètre avec `@CurrentUser` et l'utilisateur sera automatiquement résolu. Cette approche réduit le code boilerplate et rend les contrôleurs plus lisibles.

**SecurityContextHelper.java** est une classe utilitaire qui simplifie l'accès au SecurityContext de Spring Security. Elle fournit des méthodes statiques pour obtenir l'utilisateur courant, vérifier si un utilisateur a un rôle spécifique, et accéder à d'autres informations de sécurité. Toutes ces méthodes sont thread-safe et gèrent correctement les cas où aucun utilisateur n'est authentifié.

**Role.java** est une énumération qui définit tous les rôles disponibles dans l'application (USER, ADMIN, MERCHANT, MODERATOR). Chaque rôle a une méthode `getAuthority()` qui retourne le nom du rôle au format attendu par Spring Security ("ROLE_USER", etc.). L'énumération définit également une hiérarchie de permissions, où certains rôles héritent des permissions d'autres rôles (par exemple, ADMIN a toutes les permissions de USER).

#### Sous-package Password

**PasswordService.java** fournit des méthodes pour hacher et vérifier les mots de passe de manière sécurisée. Le service utilise l'algorithme BCrypt avec 12 rounds de hachage, ce qui offre un bon équilibre entre sécurité et performance. BCrypt est un algorithme adaptatif qui peut être configuré pour devenir plus lent au fil du temps à mesure que les ordinateurs deviennent plus rapides, ce qui le rend résistant aux attaques par force brute. Le service est thread-safe et peut être utilisé en parallèle sans problème.

**PasswordValidator.java** valide que les mots de passe respectent les règles de complexité définies par l'application. Ces règles incluent une longueur minimale (généralement 8 caractères), la présence d'au moins une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial. Le validateur peut également vérifier que le mot de passe n'est pas dans une liste de mots de passe communs (comme "password123"). Il fournit un score de force du mot de passe et des suggestions pour l'améliorer.

#### Sous-package Crypto

**EncryptionService.java** fournit des services de chiffrement pour protéger les données sensibles. Le service supporte plusieurs algorithmes : AES-256 en mode GCM pour le chiffrement symétrique (utilisé pour les données au repos), et RSA pour le chiffrement asymétrique (utilisé pour échanger des clés de session). Le service gère également la génération de vecteurs d'initialisation aléatoires et la vérification de l'intégrité des données chiffrées. Toutes les opérations cryptographiques utilisent les implémentations du JCE (Java Cryptography Extension).

**KeyGenerator.java** génère des clés cryptographiques sécurisées pour différents algorithmes. Pour AES, il génère des clés de 256 bits enutilisant un générateur de nombres aléatoires cryptographiquement sûr. Pour RSA, il génère des paires de clés (publique/privée) de 2048 bits minimum. Le générateur peut également créer des clés elliptiques pour l'algorithme ECDSA. Toutes les clés générées sont suffisamment fortes pour résister aux attaques actuelles et futures prévisibles.

### Mappers (Package `mapper`)

Les mappers utilisent MapStruct pour générer automatiquement du code de conversion performant entre différentes représentations des données.

**BaseMapper.java** est une interface générique qui définit les méthodes de base que tous les mappers doivent implémenter. Ces méthodes incluent `toDto()` pour convertir une entité en DTO, `toEntity()` pour la conversion inverse, et `toDtoList()` pour convertir des listes. L'interface est paramétrable avec deux types génériques : le type de l'entité (E) et le type du DTO (D). Cette abstraction permet d'écrire du code générique qui fonctionne avec n'importe quel type de mapper.

**DtoMapper.java** est l'implémentation générique de BaseMapper. Il utilise les annotations MapStruct pour indiquer au processeur d'annotations de générer le code de conversion au moment de la compilation. Cette génération de code garantit que les conversions sont type-safe et performantes, sans utiliser de réflexion à l'exécution. Le mapper gère automatiquement les conversions de types simples et peut être configuré avec des mappings personnalisés pour les cas complexes.

**EntityMapper.java** est un mapper spécialisé pour les entités JPA. Il sait comment gérer les relations entre entités (OneToMany, ManyToOne, etc.) et peut être configuré pour charger paresseusement ou avidement ces relations selon le contexte. Le mapper gère également le cycle de vie des entités, en s'assurant que les entités détachées sont correctement rattachées au contexte de persistance si nécessaire.

**SearchMapper.java** est spécialisé dans la conversion des critères de recherche et des documents Elasticsearch. Il sait comment convertir les paramètres de requête HTTP en objets de critères de recherche Elasticsearch, et comment convertir les documents Elasticsearch en DTOs de résultats de recherche. Le mapper gère également la conversion des facettes, des agrégations et des suggestions de recherche.

**DateMapper.java** fournit des méthodes de conversion pour différents formats de date. Il peut convertir entre les types de date Java (Date, LocalDate, LocalDateTime, Instant), les timestamps Unix, et les chaînes de caractères ISO-8601. Le mapper gère automatiquement les fuseaux horaires et garantit que toutes les conversions sont effectuées de manière cohérente.

### Utilitaires (Package `util`)

Le package util contient des classes utilitaires organisées par domaine fonctionnel. Chaque sous-package fournit des méthodes spécialisées pour manipuler un type de données particulier.

#### Sous-package Date

**DateUtils.java** fournit des méthodes utilitaires pour la manipulation de dates. Ces méthodes incluent le formatage de dates selon différents formats (ISO-8601, formats localisés), le parsing de chaînes de caractères en dates, l'ajout ou la soustraction de périodes (jours, mois, années), et le calcul de différences entre dates. Toutes les méthodes sont null-safe et utilisent UTC comme fuseau horaire par défaut pour éviter les problèmes de conversion. Les méthodes sont également thread-safe et peuvent être utilisées en parallèle sans synchronisation.

**TimeUtils.java** se spécialise dans les opérations sur le temps plutôt que sur les dates. Il fournit des méthodes pour mesurer des durées avec haute précision (en utilisant System.nanoTime()), formater des durées en chaînes lisibles ("2 hours 15 minutes"), convertir entre différentes unités de temps (millisecondes, secondes, minutes), et calculer des statistiques sur des séries de mesures de temps (moyenne, médiane, percentiles). Ces utilitaires sont particulièrement utiles pour le monitoring et le benchmarking.

**DateTimeFormatters.java** définit des formateurs de dates prêts à l'emploi pour les formats les plus courants utilisés dans l'application. Ces formateurs incluent ISO_DATE pour les dates seules, ISO_DATETIME pour les dates avec heures, et des formateurs personnalisés pour les formats spécifiques à l'application. Tous les formateurs sont thread-safe et peuvent être réutilisés sans problème de concurrence.

#### Sous-package String

**StringUtils.java** étend les fonctionnalités de base de manipulation de chaînes de caractères. Il fournit des méthodes pour vérifier si une chaîne est vide ou null (avec gestion des espaces), mettre en majuscule la première lettre d'une chaîne, tronquer une chaîne à une longueur maximale en ajoutant des points de suspension, comparer des chaînes de manière insensible à la casse, et diviser des chaînes selon des délimiteurs complexes. Toutes les méthodes sont null-safe et retournent des valeurs sensées même avec des entrées invalides.

**TextSanitizer.java** nettoie le texte fourni par les utilisateurs pour le rendre sûr. Il peut supprimer ou échapper les tags HTML pour prévenir les attaques XSS (Cross-Site Scripting), échapper les caractères spéciaux SQL pour prévenir les injections SQL, supprimer les caractères de contrôle invisibles, normaliser les espaces multiples, et supprimer les caractères non imprimables. Le sanitizer peut être configuré avec une liste blanche de tags HTML autorisés pour les cas où du HTML limité est acceptable.

**SlugGenerator.java** génère des slugs URL-friendly à partir de chaînes de texte arbitraires. Il effectue la translitération des caractères accentués en leurs équivalents non accentués (é → e, ñ → n), convertit tous les caractères en minuscules, remplace les espaces par des tirets, supprime les caractères non alphanumériques, et peut ajouter un suffixe numérique pour garantir l'unicité. Les slugs générés sont optimisés pour le SEO et la lisibilité des URLs.

#### Sous-package Validation

**Validators.java** est une collection de validateurs pour différents types de données. Il fournit des méthodes de validation pour les emails (avec vérification RFC), les numéros de téléphone (supportant plusieurs formats internationaux), les URLs (avec vérification du protocole et du domaine), les adresses IP (IPv4 et IPv6), les codes postaux, les numéros de carte de crédit (algorithme de Luhn), et d'autres types de données courantes. Chaque validateur retourne un booléen indiquant si la donnée est valide, et peut optionnellement retourner un message d'erreur détaillé.

**EmailValidator.java** valide les adresses email selon les standards RFC 5322. Il vérifie la structure syntaxique de l'email (présence du @, format du domaine), peut effectuer une vérification DNS pour s'assurer que le domaine a un enregistrement MX, et peut vérifier si le domaine est dans une liste noire de domaines temporaires ou suspects. Le validateur gère correctement les adresses email internationalisées (avec caractères Unicode) et les formats complexes (avec commentaires, quotes, etc.).

**PhoneValidator.java** valide les numéros de téléphone selon le standard E.164. Il peut valider des numéros avec ou sans indicatif pays, normaliser les numéros dans un format standardisé, extraire l'indicatif pays et le numéro local, et formatter les numéros selon les conventions locales. Le validateur supporte plus de 200 pays et peut être configuré pour être strict ou permissif dans sa validation.

**PasswordValidator.java** implémente les règles de validation des mots de passe. Il vérifie la longueur minimale (configurable, généralement 8 caractères), la présence de différents types de caractères (majuscules, minuscules, chiffres, symboles), l'absence de patterns courants (123456, qwerty, password), et peut vérifier si le mot de passe a été compromis dans des fuites de données connues (via l'API Have I Been Pwned). Le validateur calcule également un score de force du mot de passe et fournit des suggestions spécifiques pour l'améliorer.

#### Sous-package File

**FileUtils.java** fournit des opérations de manipulation de fichiers. Il peut lire et écrire des fichiers de manière sûre (avec gestion atomique des écritures), copier et déplacer des fichiers (avec gestion des collisions), calculer des hashes de fichiers (MD5, SHA-256) pour vérifier l'intégrité, et effectuer des opérations récursives sur des répertoires. Toutes les opérations gèrent correctement les erreurs d'I/O et les permissions de fichiers.

**FileValidator.java** valide les fichiers uploadés par les utilisateurs. Il vérifie le type MIME du fichier en examinant les magic numbers (les premiers bytes) plutôt que de se fier à l'extension, valide que la taille du fichier est dans les limites acceptables, vérifie que le nom de fichier ne contient pas de caractères dangereux (comme des path traversal), et peut scanner les fichiers pour détecter des contenus malveillants. Le validateur peut être configuré avec des listes blanches et noires de types MIME.

**FileStorageHelper.java** abstrait le stockage de fichiers pour supporter différents backends (système de fichiers local, Amazon S3, MinIO, etc.). Il fournit des méthodes pour stocker un fichier et obtenir une URL pour y accéder, récupérer un fichier à partir de son identifiant, supprimer un fichier, et lister les fichiers dans un répertoire. L'abstraction permet de changer de backend de stockage sans modifier le code des services qui l'utilisent.

#### Sous-package Network

**IpUtils.java** manipule les adresses IP. Il peut valider qu'une chaîne représente une adresse IP valide (IPv4 ou IPv6), vérifier si une adresse IP est privée (dans les plages 10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16), déterminer si une adresse est dans un sous-réseau donné, et effectuer une recherche de géolocalisation pour obtenir le pays et la ville associés à une adresse IP. Les utilitaires supportent pleinement IPv6 et ses différentes notations.

**UrlUtils.java** parse et manipule les URLs. Il peut extraire les différents composants d'une URL (protocole, hôte, port, chemin, paramètres de requête), construire des URLs de manière programmatique, normaliser les URLs (résoudre les chemins relatifs, supprimer les fragments), et encoder/décoder correctement les composants d'URL. L'utilitaire gère correctement les caractères spéciaux et les caractères Unicode dans les URLs.

**NetworkValidator.java** valide différents aspects réseau. Il peut vérifier qu'un port est dans la plage valide (1-65535) et n'est pas réservé, tester si un hôte est accessible via ping ou connexion TCP, valider des masques de sous-réseau, et vérifier des patterns d'URL selon différents critères (présence de HTTPS, domaine autorisé, etc.). Le validateur peut effectuer des validations actives (qui tentent réellement de se connecter) ou passives (qui valident seulement la syntaxe).

#### Sous-package Geo

**GeoUtils.java** effectue des calculs géographiques. Il implémente la formule de Haversine pour calculer la distance à vol d'oiseau entre deux points sur la surface terrestre, la formule de Vincenty pour des calculs plus précis sur de longues distances, et peut calculer le bearing (direction) entre deux points, le point médian, et les points d'une bounding box. Toutes les coordonnées sont exprimées en degrés décimaux (WGS84) et les distances en kilomètres.

**DistanceCalculator.java** est spécialisé dans le calcul de distances. Il peut calculer des distances en différentes unités (kilomètres, miles, miles nautiques), utiliser différents algorithmes selon la précision requise (Haversine pour la rapidité, Vincenty pour la précision), et mettre en cache les résultats pour éviter de recalculer des distances déjà connues. Le calculateur est optimisé pour les calculs en batch et peut traiter efficacement de grandes listes de coordonnées.

**CoordinateValidator.java** valide les coordonnées géographiques. Il vérifie que la latitude est dans la plage valide (-90 à 90 degrés), que la longitude est dans la plage valide (-180 à 180 degrés), que les coordonnées ne représentent pas un point invalide (comme 0,0 qui est au milieu de l'océan Atlantique), et peut normaliser les coordonnées en les arrondissant à une précision appropriée. Le validateur supporte différents formats de coordonnées (degrés décimaux, degrés-minutes-secondes).

### Annotations (Package `annotation`)

Les annotations personnalisées utilisent la programmation orientée aspect (AOP) pour ajouter des fonctionnalités transversales de manière déclarative.

**ValidateEnum.java** est une annotation de validation qui peut être utilisée sur les champs ou paramètres de type enum. Elle vérifie que la valeur fournie correspond à une des valeurs de l'énumération spécifiée. Cette annotation est particulièrement utile pour valider les paramètres de requête HTTP qui sont passés comme chaînes de caractères mais doivent correspondre à des valeurs enum. Si la validation échoue, une ValidationException est levée avec un message listant les valeurs acceptables.

**LogExecutionTime.java** est une annotation qui peut être placée sur des méthodes pour mesurer et logger automatiquement leur temps d'exécution. L'annotation utilise un aspect AOP qui entoure l'exécution de la méthode, mesure le temps avec précision nanoseconde, et log le résultat. Le log inclut le nom complet de la méthode, ses paramètres (si configuré), et le temps d'exécution formaté. Cette annotation est très utile pour identifier les goulots d'étranglement de performance.

**Cacheable.java** est une annotation custom pour le caching qui étend les capacités de @Cacheable de Spring. Elle permet de spécifier des TTLs différents selon le contexte, de définir des stratégies de génération de clés personnalisées, et de configurer des comportements de cache différents (cache-aside, write-through, etc.). L'annotation supporte plusieurs backends de cache (Redis, Caffeine) et peut être configurée pour invalider automatiquement le cache lors de certains événements.

**RateLimited.java** implémente la limitation de taux pour protéger les APIs contre les abus. L'annotation spécifie le nombre maximum de requêtes autorisées dans une fenêtre de temps (par exemple, 100 requêtes par minute). L'implémentation utilise l'algorithme token bucket stocké dans Redis pour permettre une limitation distribuée à travers plusieurs instances de service. Lorsque la limite est dépassée, une RateLimitException est levée, qui est convertie en une réponse HTTP 429 (Too Many Requests) avec un header Retry-After indiquant quand réessayer.

**AuditLog.java** capture automatiquement les informations sur les appels de méthode pour l'audit. L'annotation enregistre qui a appelé la méthode (utilisateur courant), quand (timestamp précis), avec quels paramètres, et quel a été le résultat (succès ou échec). Ces informations sont envoyées de manière asynchrone à un système de log d'audit centralisé (généralement Elasticsearch via Logstash). L'audit est essentiel pour la conformité et pour enquêter sur les incidents de sécurité.

### Constantes (Package `constant`)

Le package constant centralise toutes les valeurs constantes utilisées dans l'application, éliminant les "magic numbers" et facilitant la maintenance.

**AppConstants.java** contient les constantes générales de l'application comme le nom de l'application ("YowYob Search"), la version actuelle, le fuseau horaire par défaut (UTC), la locale par défaut, et d'autres paramètres globaux. Ces constantes sont utilisées dans les logs, les headers HTTP, les templates d'email, et partout où des informations sur l'application elle-même sont nécessaires.

**ErrorConstants.java** définit tous les codes d'erreur utilisés dans l'application. Les codes suivent une convention de nommage hiérarchique (CATEGORY_SPECIFIC_ERROR) pour faciliter leur organisation et leur recherche. Par exemple, AUTH_INVALID_TOKEN, SEARCH_QUERY_TOO_LONG, USER_EMAIL_ALREADY_EXISTS. Chaque code est associé à un message par défaut (localisable) et à un code de statut HTTP. Cette centralisation garantit qu'il n'y a pas de codes d'erreur dupliqués ou conflictuels.

**HttpConstants.java** définit des constantes liées au protocole HTTP qui ne sont pas déjà fournies par Spring. Cela inclut des noms de headers personnalisés (X-Request-ID, X-User-ID), des types MIME spécifiques à l'application, des codes de statut personnalisés, et des valeurs de cookies. Ces constantes garantissent la cohérence dans les communications HTTP à travers toute la plateforme.

**CacheConstants.java** définit les noms de caches utilisés dans l'application ("user-cache", "search-cache", "geo-cache", etc.), leurs TTLs respectifs (durée avant expiration), les préfixes de clés standardisés (pour éviter les collisions entre différents types de données), et d'autres paramètres de cache. Cette centralisation facilite la configuration et le tuning des performances du cache.

**SearchConstants.java** contient les constantes spécifiques à la fonctionnalité de recherche. Cela inclut la taille de page par défaut (10 résultats), la taille de page maximale (100 résultats pour éviter les abus), le tri par défaut (pertinence décroissante), les tris autorisés (pertinence, date, popularité), les timeouts de requête Elasticsearch, et les paramètres de ranking. Ces constantes sont utilisées par le service de recherche et peuvent être ajustées pour optimiser les performances et la pertinence.

**SecurityConstants.java** définit les constantes de sécurité comme les durées d'expiration des tokens JWT (15 minutes pour l'access token, 7 jours pour le refresh token), le nombre de rounds BCrypt pour le hachage de mots de passe (12 rounds), les origines CORS autorisées, les algorithmes cryptographiques utilisés (AES-256-GCM, RSA-2048), et d'autres paramètres de sécurité. Ces constantes sont critiques pour la sécurité de l'application et doivent être soigneusement configurées.

**RegexPatterns.java** centralise toutes les expressions régulières utilisées dans l'application. Cela inclut des patterns pour la validation d'emails (RFC 5322), de numéros de téléphone (E.164), d'URLs, d'adresses IP (IPv4 et IPv6), de codes postaux, et d'autres formats. Les patterns sont pré-compilés pour améliorer les performances lors de leur utilisation répétée. Cette centralisation évite la duplication de regex complexes et garantit que les validations sont cohérentes.

### Événements (Package `event`)

Le package event fournit un système d'événements pour la communication asynchrone et découplée entre composants.

**BaseEvent.java** est la classe abstraite de base pour tous les événements dans l'application. Elle contient les champs communs à tous les événements : un identifiant unique (UUID), un timestamp précis indiquant quand l'événement s'est produit, un type d'événement (représenté par une chaîne ou une énumération), et potentiellement d'autres métadonnées comme l'identifiant du service source. Tous les événements concrets héritent de cette classe.

**DomainEvent.java** représente les événements qui se produisent dans le domaine métier de l'application. Par exemple, "UserRegistered", "OrderPlaced", "SearchPerformed". Ces événements sont utilisés pour implémenter le pattern Event Sourcing, où l'état du système est dérivé de la séquence d'événements qui se sont produits. Les domain events contiennent généralement l'identifiant de l'agrégat concerné et un numéro de version pour gérer la concurrence optimiste.

**IntegrationEvent.java** représente les événements qui sont échangés entre différents microservices. Ces événements sont publiés sur Kafka et consommés par les services intéressés. Par exemple, lorsqu'un nouveau produit est ajouté dans le Shop Service, un IntegrationEvent "ProductAdded" est publié, et le Search Service le consomme pour indexer le produit dans Elasticsearch. Les integration events incluent le service source et potentiellement le service destination.

**EventPublisher.java** est l'interface pour publier des événements. Elle fournit des méthodes comme `publish(Event event)` qui publie un événement de manière asynchrone. L'implémentation concrète peut publier les événements localement (via Spring ApplicationEventPublisher) ou de manière distribuée (via Kafka). Le publisher garantit que les événements sont publiés de manière fiable, avec retries en cas d'échec temporaire.

**EventListener.java** est l'interface que les composants implémentent pour écouter des événements. Elle définit une méthode `onEvent(Event event)` qui est appelée lorsqu'un événement correspondant se produit. Les listeners peuvent filtrer les événements qu'ils traitent selon leur type, et peuvent s'abonner à plusieurs types d'événements. Le framework gère automatiquement l'enregistrement des listeners et la distribution des événements.

---

## ⚙ Configuration et utilisation

### Fichier pom.xml

Le fichier pom.xml du module common définit toutes les dépendances nécessaires et configure le build Maven. Voici la structure et les dépendances principales :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- Informations du projet -->
    <parent>
        <groupId>com.yowyob</groupId>
        <artifactId>yowyob-search-backend</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>yowyob-common</artifactId>
    <name>YowYob Common Module</name>
    <description>
        Module commun fournissant les composants réutilisables,
        les configurations standardisées et les utilitaires partagés
        pour tous les microservices de la plateforme YowYob Search
    </description>

    <!-- Propriétés -->
    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        
        <!-- Versions des dépendances -->
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <jjwt.version>0.12.3</jjwt.version>
        <lombok.version>1.18.30</lombok.version>
        <commons-lang3.version>3.14.0</commons-lang3.version>
        <commons-io.version>2.15.1</commons-io.version>
        <guava.version>33.0.0-jre</guava.version>
    </properties>

    <!-- Dépendances -->
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <!-- Jackson pour JSON -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>

        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- MapStruct pour les mappers -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${mapstruct.version}</version>
        </dependency>

        <!-- Lombok pour réduire le boilerplate -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>

        <!-- Apache Commons pour utilitaires -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>${commons-lang3.version}</version>
        </dependency>

        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>${commons-io.version}</version>
        </dependency>

        <!-- Guava pour collections et utilitaires -->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>${guava.version}</version>
        </dependency>

        <!-- Dépendances de test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <!-- Configuration du build -->
    <build>
        <plugins>
            <!-- Compiler plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.12.1</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>${mapstruct.version}</version>
                        </path>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok-mapstruct-binding</artifactId>
                            <version>0.2.0</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>

            <!-- Test plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
                <configuration>
                    <includes>
                        <include>**/*Test.java</include>
                        <include>**/*Tests.java</include>
                    </includes>
                </configuration>
            </plugin>

            <!-- Coverage plugin (JaCoCo) -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.11</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>jacoco-check</id>
                        <goals>
                            <goal>check</goal>
                        </goals>
                        <configuration>
                            <rules>
                                <rule>
                                    <element>PACKAGE</element>
                                    <limits>
                                        <limit>
                                            <counter>LINE</counter>
                                            <value>COVEREDRATIO</value>
                                            <minimum>0.80</minimum>
                                        </limit>
                                    </limits>
                                </rule>
                            </rules>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- Checkstyle plugin pour conformité au style -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-checkstyle-plugin</artifactId>
                <version>3.3.1</version>
                <configuration>
                    <configLocation>checkstyle.xml</configLocation>
                    <consoleOutput>true</consoleOutput>
                    <failsOnError>true</failsOnError><linkXRef>false</linkXRef>
                </configuration>
                <executions>
                    <execution>
                        <id>validate</id>
                        <phase>validate</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- PMD plugin pour qualité de code -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-pmd-plugin</artifactId>
                <version>3.21.2</version>
                <configuration>
                    <failOnViolation>true</failOnViolation>
                    <printFailingErrors>true</printFailingErrors>
                </configuration>
                <executions>
                    <execution>
                        <phase>verify</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Fichier application-common.yml

Le fichier de configuration YAML définit les paramètres par défaut pour tous les microservices qui utilisent le module common :

```yaml
# Configuration commune pour YowYob Search Platform
# Ce fichier définit les paramètres partagés entre tous les microservices

spring:
  application:
    name: yowyob-common

  # Configuration Jackson pour sérialisation JSON
  jackson:
    date-format: yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
    time-zone: UTC
    serialization:
      write-dates-as-timestamps: false
      fail-on-empty-beans: false
      indent-output: false
    deserialization:
      fail-on-unknown-properties: false
      adjust-dates-to-context-time-zone: false
    default-property-inclusion: non_null

  # Configuration des uploads de fichiers
  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 10MB
      file-size-threshold: 2KB

# Configuration du logging
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  
  level:
    root: INFO
    com.yowyob: DEBUG
    org.springframework.web: INFO
    org.springframework.security: INFO
    org.hibernate: WARN
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

# Configuration de l'application
app:
  name: YowYob Search Platform
  version: 1.0.0
  description: Plateforme de recherche intelligente distribuée
  
  # Configuration de sécurité
  security:
    jwt:
      expiration-ms: 900000        # 15 minutes
      refresh-expiration-ms: 604800000  # 7 jours
      issuer: yowyob-search
      audience: yowyob-clients
    
    password:
      min-length: 8
      require-uppercase: true
      require-lowercase: true
      require-digit: true
      require-special-char: true
      bcrypt-rounds: 12
    
    cors:
      allowed-origins:
        - http://localhost:3000
        - http://localhost:8080
      allowed-methods:
        - GET
        - POST
        - PUT
        - DELETE
        - OPTIONS
      allowed-headers: "*"
      allow-credentials: true
      max-age: 3600
  
  # Configuration du cache
  cache:
    redis:
      ttl:
        search: 300         # 5 minutes
        user: 1800          # 30 minutes
        geo: 86400          # 24 heures
      key-prefix: "yowyob:"
  
  # Configuration du rate limiting
  rate-limiting:
    enabled: true
    requests-per-minute: 100
    burst-capacity: 20
    
  # Configuration de validation
  validation:
    email:
      check-dns: false
      blacklist-enabled: true
    phone:
      default-country: CM
      strict: false
  
  # Configuration des fichiers
  file:
    storage:
      type: local  # local, s3, minio
      local-path: ./uploads
      max-size: 10485760  # 10 MB
    allowed-types:
      - image/jpeg
      - image/png
      - image/gif
      - application/pdf
      - text/plain

# Configuration du monitoring
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
```

### Utilisation dans un microservice

Pour utiliser le module common dans un microservice, il suffit d'ajouter la dépendance Maven et d'importer les configurations nécessaires. Voici un exemple complet :

**1. Ajout de la dépendance dans pom.xml du microservice :**

```xml
<dependency>
    <groupId>com.yowyob</groupId>
    <artifactId>yowyob-common</artifactId>
    <version>${project.version}</version>
</dependency>
```

**2. Activation de la configuration commune dans application.yml :**

```yaml
spring:
  profiles:
    include: common
```

**3. Exemple d'utilisation dans un contrôleur :**

```java
package com.yowyob.search.controller;

import com.yowyob.common.dto.ApiResponse;
import com.yowyob.common.dto.PageResponse;
import com.yowyob.common.exception.ValidationException;
import com.yowyob.common.annotation.LogExecutionTime;
import com.yowyob.common.annotation.RateLimited;
import com.yowyob.common.security.auth.CurrentUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    
    /**
     * Exemple d'utilisation d'ApiResponse pour encapsuler la réponse
     * et de @CurrentUser pour injecter l'utilisateur courant
     */
    @GetMapping
    @LogExecutionTime
    @RateLimited(value = 50)  // Limite à 50 requêtes/minute
    public ApiResponse<PageResponse<SearchResult>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @CurrentUser User user) {
        
        // Validation utilisant les utilitaires du module common
        if (query == null || query.trim().isEmpty()) {
            throw new ValidationException("query", 
                "Query cannot be empty", query);
        }
        
        // Logique de recherche...
        List<SearchResult> results = searchService.search(query, page, size);
        
        // Création de la réponse paginée
        PageResponse<SearchResult> pageResponse = PageResponse.<SearchResult>builder()
            .content(results)
            .page(page)
            .size(size)
            .totalElements(searchService.count(query))
            .totalPages((int) Math.ceil((double) searchService.count(query) / size))
            .first(page == 0)
            .last(results.size() < size)
            .empty(results.isEmpty())
            .build();
        
        // Retour de la réponse standardisée
        return ApiResponse.success(pageResponse);
    }
}
```

**4. Exemple d'utilisation des utilitaires :**

```java
package com.yowyob.search.service;

import com.yowyob.common.util.date.DateUtils;
import com.yowyob.common.util.string.StringUtils;
import com.yowyob.common.util.validation.Validators;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    
    public void processSearch(SearchRequest request) {
        // Utilisation des utilitaires de validation
        if (!Validators.isValidEmail(request.getUserEmail())) {
            throw new ValidationException("email", 
                "Invalid email format", request.getUserEmail());
        }
        
        // Utilisation des utilitaires de chaînes
        String sanitizedQuery = StringUtils.sanitize(request.getQuery());
        String slug = SlugGenerator.toSlug(sanitizedQuery);
        
        // Utilisation des utilitaires de dates
        LocalDateTime now = DateUtils.now();
        String formattedDate = DateUtils.format(now, 
            DateTimeFormatters.ISO_DATETIME);
        
        // Logique métier...
    }
}
```

**5. Exemple d'utilisation du système d'exceptions :**

```java
package com.yowyob.search.service;

import com.yowyob.common.exception.ResourceNotFoundException;
import com.yowyob.common.exception.UnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    public User findById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User", userId));
    }
    
    public void verifyPermission(User user, String resource) {
        if (!user.hasPermission(resource)) {
            throw new UnauthorizedException(
                "User does not have permission to access " + resource)
                .withDetail("userId", user.getId())
                .withDetail("resource", resource);
        }
    }
}
```

---

## 🔗 Intégration avec les autres modules

### Dépendances entrantes

Le module common est utilisé par tous les autres modules du backend YowYob. Voici comment chaque microservice l'utilise :

**API Gateway (yowyob-api-gateway)** :
- Utilise les configurations CORS et JWT pour sécuriser les routes
- Utilise ApiResponse pour formater les réponses
- Utilise GlobalExceptionHandler pour gérer les erreurs
- Utilise les annotations @RateLimited pour protéger contre les abus

**Search Service (yowyob-search-service)** :
- Utilise PageResponse pour paginer les résultats de recherche
- Utilise les utilitaires de validation pour valider les requêtes
- Utilise le système d'événements pour publier les événements de recherche
- Utilise les mappers pour convertir entre entités Elasticsearch et DTOs

**User Service (yowyob-user-service)** :
- Utilise JwtService pour générer et valider les tokens
- Utilise PasswordService pour hacher les mots de passe
- Utilise les validateurs d'email et de mot de passe
- Utilise les exceptions d'authentification et d'autorisation

**Geo Service (yowyob-geo-service)** :
- Utilise les utilitaires géographiques pour calculer les distances
- Utilise CoordinateValidator pour valider les coordonnées
- Utilise le cache Redis configuré dans le module common

**Crawler Service (yowyob-crawler-service)** :
- Utilise les utilitaires d'URL pour parser et valider les URLs
- Utilise TextSanitizer pour nettoyer le contenu HTML
- Utilise les événements pour publier les documents indexés

**Notification Service (yowyob-notification-service)** :
- Utilise les templates d'email définis dans le module common
- Utilise le système d'événements pour écouter les notifications
- Utilise les configurations Kafka

**Shop Service (yowyob-shop-service)** :
- Utilise les mappers pour convertir les données produits
- Utilise les validateurs pour valider les données de produits
- Utilise le système d'événements pour publier les ajouts de produits

**Stats Service (yowyob-stats-service)** :
- Utilise les utilitaires de dates pour les agrégations temporelles
- Utilise le cache Redis pour stocker les statistiques précalculées
- Utilise les configurations Kafka pour consommer les événements

### Diagramme d'intégration

```
┌─────────────────────────────────────────────────────────────────┐
│                   MODULE COMMON (yowyob-common)                 │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Configuration   │   DTOs   │   Exceptions   │  Security │   │
│  └──────────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │   Mappers   │   Utilitaires   │   Annotations   │  Events│   │
│  └──────────────────────────────────────────────────────────┘   │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           │ Dépendance Maven
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
         ▼                 ▼                 ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│  API Gateway    │ │ Search Service  │ │  User Service   │
│                 │ │                 │ │                 │
│ • CORS Config   │ │ • PageResponse  │ │ • JwtService    │
│ • JWT Filter    │ │ • Search Utils  │ │ • Password Hash │
│ • Rate Limit    │ │ • Event Pub     │ │ • Validators    │
└─────────────────┘ └─────────────────┘ └─────────────────┘
         │                 │                 │
         └─────────────────┼─────────────────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
         ▼                 ▼                 ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│  Geo Service    │ │ Crawler Service │ │ Notif Service   │
│                 │ │                 │ │                 │
│ • Geo Utils     │ │ • URL Utils     │ │ • Templates     │
│ • Coord Valid   │ │ • Text Sanitize │ │ • Event Listen  │
│ • Cache Config  │ │ • Event Pub     │ │ • Kafka Config  │
└─────────────────┘ └─────────────────┘ └─────────────────┘
         │                 │                 │
         └─────────────────┼─────────────────┘
                           │
         ┌─────────────────┴─────────────────┐
         │                                   │
         ▼                                   ▼
┌─────────────────┐                 ┌─────────────────┐
│  Shop Service   │                 │ Stats Service   │
│                 │                 │                 │
│ • Mappers       │                 │ • Date Utils    │
│ • Validators    │                 │ • Redis Cache   │
│ • Event Pub     │                 │ • Event Listen  │
└─────────────────┘                 └─────────────────┘
```

### Principe de non-intrusion

Un principe clé de l'architecture du module common est la non-intrusion. Le module ne force aucun comportement ou configuration sur les microservices qui l'utilisent. Au lieu de cela, il fournit des composants que les services peuvent choisir d'utiliser selon leurs besoins.

Par exemple, un service peut choisir d'utiliser le GlobalExceptionHandler en l'annotant avec `@Import(GlobalExceptionHandler.class)`, mais il peut également fournir son propre gestionnaire d'exceptions s'il a des besoins spécifiques. De même, les configurations peuvent être surchargées par les services individuels en définissant leurs propres beans avec des noms identiques.

Cette approche garantit que le module common reste un outil flexible plutôt qu'un framework contraignant. Les développeurs peuvent adopter progressivement les fonctionnalités du module sans être forcés de tout utiliser d'un coup.

---

## ✅ Tests et qualité

### Stratégie de test

Le module common adopte une stratégie de test complète qui garantit la fiabilité et la qualité de tous ses composants. Les tests sont organisés en plusieurs catégories :

**Tests unitaires** : Chaque classe utilitaire, chaque service et chaque composant a ses propres tests unitaires qui vérifient son comportement de manière isolée. Les tests unitaires utilisent des mocks pour isoler la classe testée de ses dépendances. Par exemple, les tests de JwtService mockent la lecture des clés RSA pour se concentrer uniquement sur la logique de génération et validation de tokens.

**Tests d'intégration** : Ces tests vérifient que les différents composants du module fonctionnent correctement ensemble. Par exemple, un test d'intégration pourrait vérifier que le GlobalExceptionHandler intercepte correctement une ValidationException et la convertit en ErrorResponse avec le bon format. Ces tests utilisent des contextes Spring réels mais limités aux composants nécessaires.

**Tests de configuration** : Ces tests spéciaux vérifient que les configurations Spring sont correctement chargées et que tous les beans sont créés avec les bonnes propriétés. Ils garantissent qu'il n'y a pas de conflits de beans et que les configurations peuvent être surchargées correctement.

**Tests de sécurité** : Les composants de sécurité ont des tests dédiés qui vérifient leur robustesse. Par exemple, les tests de JwtService essaient de valider des tokens avec des signatures invalides, des tokens expirés, des tokens malformés, etc. Les tests de PasswordService vérifient que les hash sont différents pour le même mot de passe (grâce au salt), et que les vérifications fonctionnent correctement.

### Structure des tests

```
src/test/java/com/yowyob/common/
├── config/
│   ├── CorsConfigTest.java
│   ├── JacksonConfigTest.java
│   ├── DateTimeConfigTest.java
│   ├── RedisConfigTest.java
│   └── AllConfigsIntegrationTest.java
│
├── dto/
│   ├── ApiResponseTest.java
│   ├── PageResponseTest.java
│   ├── ErrorResponseTest.java
│   └── SerializationTest.java
│
├── exception/
│   ├── GlobalExceptionHandlerTest.java
│   ├── AppExceptionTest.java
│   ├── ExceptionTranslatorTest.java
│   └── ExceptionHierarchyTest.java
│
├── security/
│   ├── jwt/
│   │   ├── JwtServiceTest.java
│   │   ├── JwtSecurityTest.java
│   │   └── TokenProviderTest.java
│   ├── auth/
│   │   ├── CurrentUserResolverTest.java
│   │   └── RoleTest.java
│   ├── password/
│   │   ├── PasswordServiceTest.java
│   │   ├── PasswordValidatorTest.java
│   │   └── PasswordSecurityTest.java
│   └── crypto/
│       ├── EncryptionServiceTest.java
│       ├── KeyGeneratorTest.java
│       └── CryptoSecurityTest.java
│
├── mapper/
│   ├── BaseMapperTest.java
│   ├── DtoMapperTest.java
│   ├── EntityMapperTest.java
│   └── MapperIntegrationTest.java
│
├── util/
│   ├── date/
│   │   ├── DateUtilsTest.java
│   │   ├── TimeUtilsTest.java
│   │   └── DateTimeFormattersTest.java
│   ├── string/
│   │   ├── StringUtilsTest.java
│   │   ├── TextSanitizerTest.java
│   │   ├── TextSanitizerSecurityTest.java
│   │   └── SlugGeneratorTest.java
│   ├── validation/
│   │   ├── ValidatorsTest.java
│   │   ├── EmailValidatorTest.java
│   │   ├── PhoneValidatorTest.java
│   │   └── PasswordValidatorTest.java
│   ├── file/
│   │   ├── FileUtilsTest.java
│   │   ├── FileValidatorTest.java
│   │   ├── FileValidatorSecurityTest.java
│   │   └── FileStorageHelperTest.java
│   ├── network/
│   │   ├── IpUtilsTest.java
│   │   ├── UrlUtilsTest.java
│   │   └── NetworkValidatorTest.java
│   └── geo/
│       ├── GeoUtilsTest.java
│       ├── DistanceCalculatorTest.java
│       └── CoordinateValidatorTest.java
│
├── annotation/
│   ├── ValidateEnumTest.java
│   ├── LogExecutionTimeTest.java
│   ├── CacheableTest.java
│   ├── RateLimitedTest.java
│   └── AuditLogTest.java
│
├── constant/
│   ├── AppConstantsTest.java
│   ├── ErrorConstantsTest.java
│   └── RegexPatternsTest.java
│
├── event/
│   ├── BaseEventTest.java
│   ├── DomainEventTest.java
│   ├── IntegrationEventTest.java
│   ├── EventPublisherTest.java
│   └── EventListenerTest.java
│
└── integration/
    ├── FullStackIntegrationTest.java
    ├── SecurityIntegrationTest.java
    └── CachingIntegrationTest.java
```

### Exemple de test unitaire

Voici un exemple de test unitaire complet pour le JwtService :

```java
package com.yowyob.common.security.jwt;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour JwtService
 * 
 * Ce test vérifie:
 * - La génération correcte de tokens JWT
 * - La validation des tokens valides et invalides
 * - L'extraction des claims
 * - La gestion des tokens expirés
 * - La gestion des signatures invalides
 */
@DisplayName("JwtService Tests")
class JwtServiceTest {
    
    private JwtService jwtService;
    private JwtConfig jwtConfig;
    private KeyPair keyPair;
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // Génération d'une paire de clés RSA pour les tests
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        keyPair = keyGen.generateKeyPair();
        
        // Configuration du JWT pour les tests
        jwtConfig = new JwtConfig();
        jwtConfig.setPrivateKey(keyPair.getPrivate());
        jwtConfig.setPublicKey(keyPair.getPublic());
        jwtConfig.setExpirationMs(900000L); // 15 minutes
        jwtConfig.setIssuer("yowyob-test");
        jwtConfig.setAudience("yowyob-clients");
        
        jwtService = new JwtService(jwtConfig);
    }
    
    @Test
    @DisplayName("Should generate valid JWT token")
    void shouldGenerateValidToken() {
        // Given
        String userId = "user123";
        Set<Role> roles = Set.of(Role.USER);
        
        // When
        String token = jwtService.generateToken(userId, roles);
        
        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.startsWith("eyJ")); // JWT commence toujours par eyJ
        
        // Le token doit avoir 3 parties séparées par des points
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts: header, payload, signature");
    }
    
    @Test
    @DisplayName("Should validate correct token successfully")
    void shouldValidateCorrectToken() {
        // Given
        String userId = "user123";
        Set<Role> roles = Set.of(Role.USER);
        String token = jwtService.generateToken(userId, roles);
        
        // When
        boolean isValid = jwtService.validateToken(token);
        
        // Then
        assertTrue(isValid, "Generated token should be valid");
    }
    
    @Test
    @DisplayName("Should reject token with invalid signature")
    void shouldRejectInvalidSignature() {
        // Given
        String userId = "user123";
        Set<Role> roles = Set.of(Role.USER);
        String token = jwtService.generateToken(userId, roles);
        
        // Modification de la signature
        String[] parts = token.split("\\.");
        String invalidToken = parts[0] + "." + parts[1] + ".invalidsignature";
        
        // When
        boolean isValid = jwtService.validateToken(invalidToken);
        
        // Then
        assertFalse(isValid, "Token with invalid signature should be rejected");
    }
    
    @Test
    @DisplayName("Should extract user ID from token")
    void shouldExtractUserId() {
        // Given
        String userId = "user123";
        Set<Role> roles = Set.of(Role.USER);
        String token = jwtService.generateToken(userId, roles);
        
        // When
        String extractedUserId = jwtService.extractUserId(token);
        
        // Then
        assertEquals(userId, extractedUserId, "Extracted user ID should match original");
    }
    
    @Test
    @DisplayName("Should extract roles from token")
    void shouldExtractRoles() {
        // Given
        String userId = "user123";
        Set<Role> roles = Set.of(Role.USER, Role.MERCHANT);
        String token = jwtService.generateToken(userId, roles);
        
        // When
        Set<Role> extractedRoles = jwtService.extractRoles(token);
        
        // Then
        assertEquals(roles, extractedRoles, "Extracted roles should match original");
    }
    
    @Test
    @DisplayName("Should reject expired token")
    void shouldRejectExpiredToken() throws Exception {
        // Given - Configuration avec expiration très courte
        JwtConfig shortConfig = new JwtConfig();
        shortConfig.setPrivateKey(keyPair.getPrivate());
        shortConfig.setPublicKey(keyPair.getPublic());
        shortConfig.setExpirationMs(1L); // 1 milliseconde
        shortConfig.setIssuer("yowyob-test");
        shortConfig.setAudience("yowyob-clients");
        
        JwtService shortJwtService = new JwtService(shortConfig);
        
        String userId = "user123";
        Set<Role> roles = Set.of(Role.USER);
        String token = shortJwtService.generateToken(userId, roles);
        
        // Attente pour que le token expire
        Thread.sleep(10);
        
        // When
        boolean isValid = shortJwtService.validateToken(token);
        
        // Then
        assertFalse(isValid, "Expired token should be rejected");
    }
    
    @Test
    @DisplayName("Should handle null token gracefully")
    void shouldHandleNullToken() {
        // When & Then
        assertFalse(jwtService.validateToken(null), 
            "Null token should be invalid");
        assertThrows(IllegalArgumentException.class, 
            () -> jwtService.extractUserId(null),
            "Extracting user ID from null token should throw exception");
    }
    
    @Test
    @DisplayName("Should handle malformed token gracefully")
    void shouldHandleMalformedToken() {
        // Given
        String malformedToken = "this.is.not.a.valid.jwt";
        
        // When & Then
        assertFalse(jwtService.validateToken(malformedToken), 
            "Malformed token should be invalid");
        assertThrows(IllegalArgumentException.class, 
            () -> jwtService.extractUserId(malformedToken),
            "Extracting user ID from malformed token should throw exception");
    }
    
    @Test
    @DisplayName("Should include correct issuer and audience")
    void shouldIncludeCorrectIssuerAndAudience() {
        // Given
        String userId = "user123";
        Set<Role> roles = Set.of(Role.USER);
        String token = jwtService.generateToken(userId, roles);
        
        // When
        Claims claims = jwtService.extractClaims(token);
        
        // Then
        assertEquals("yowyob-test", claims.getIssuer(), 
            "Token should have correct issuer");
        assertEquals("yowyob-clients", claims.getAudience(), 
            "Token should have correct audience");
    }
    
    @Test
    @DisplayName("Should generate different tokens for same user")
    void shouldGenerateDifferentTokensForSameUser() {
        // Given
        String userId = "user123";
        Set<Role> roles = Set.of(Role.USER);
        
        // When
        String token1 = jwtService.generateToken(userId, roles);
        // Petite pause pour s'assurer que le timestamp est différent
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        String token2 = jwtService.generateToken(userId, roles);
        
        // Then
        assertNotEquals(token1, token2, 
            "Two tokens for same user should be different (different timestamps)");
    }
}
```

### Coverage et qualité

Le module common vise un coverage de test minimum de 80%. Ce seuil est vérifié automatiquement lors du build Maven grâce au plugin JaCoCo. Si le coverage tombe en dessous de 80%, le build échoue, ce qui garantit que le module maintient un niveau élevé de qualité.

En plus du coverage, le module utilise plusieurs outils d'analyse statique pour garantir la qualité du code :

- **Checkstyle** : Vérifie que le code respecte les conventions de style définies
- **PMD** : Détecte les problèmes potentiels comme les variables inutilisées, le code mort, etc.
- **SpotBugs** : Recherche les bugs potentiels comme les null pointer exceptions, les problèmes de concurrence, etc.

Tous ces outils sont configurés pour échouer le build en cas de violation, ce qui garantit que seul du code de haute qualité est intégré dans le module.

### Exécution des tests

Pour exécuter les tests du module common :

```bash
# Tests unitaires seulement
mvn test

# Tests avec rapport de coverage
mvn clean test jacoco:report

# Tests d'intégration
mvn verify

# Tests avec tous les plugins de qualité
mvn clean verify site

# Voir le rapport de coverage
# Le rapport HTML est généré dans : target/site/jacoco/index.html
```

---

## 📐 Principes de conception

### Principes SOLID

Le module common a été conçu en respectant rigoureusement les principes SOLID, qui sont fondamentaux pour créer un code maintenable et évolutif.

**Single Responsibility Principle (Principe de responsabilité unique)** : Chaque classe du module common a une seule responsabilité clairement définie. Par exemple, JwtService est uniquement responsable de la génération et validation de tokens JWT. Il ne gère pas le stockage des utilisateurs, ni l'envoi d'emails, ni aucune autre fonctionnalité. De même, DateUtils ne s'occupe que de la manipulation de dates, sans mélanger cela avec d'autres préoccupations.

Cette séparation stricte des responsabilités facilite la compréhension du code, car un développeur peut rapidement identifier quelle classe fait quoi. Elle facilite également les tests, car chaque classe peut être testée indépendamment sans avoir à mocker de nombreuses dépendances. Enfin, elle facilite la maintenance, car les modifications dans une responsabilité n'affectent pas les autres.

**Open/Closed Principle (Principe ouvert/fermé)** : Les classes du module common sont ouvertes à l'extension mais fermées à la modification. Par exemple, la classe BaseEvent peut être étendue par DomainEvent et IntegrationEvent sans modifier son code. De même, AppException peut être étendue par toutes les exceptions spécifiques sans nécessiter de changement dans la classe de base.

Ce principe est particulièrement visible dans le système de mappers. L'interface BaseMapper définit le contrat de base, et différentes implémentations peuvent être créées sans modifier l'interface. Les services qui utilisent les mappers dépendent de l'interface, pas des implémentations concrètes, ce qui permet d'ajouter de nouveaux types de mappers sans affecter le code existant.

**Liskov Substitution Principle (Principe de substitution de Liskov)** : Toutes les sous-classes peuvent être utilisées à la place de leurs classes parentes sans affecter le comportement du programme. Par exemple, n'importe quelle exception qui hérite d'AppException peut être levée et sera correctement gérée par le GlobalExceptionHandler. De même, n'importe quel Event qui hérite de BaseEvent peut être publié via l'EventPublisher.

Ce principe garantit que les abstractions sont correctement conçues et que les hiérarchies de classes sont cohérentes. Il permet également une grande flexibilité, car de nouvelles implémentations peuvent être ajoutées sans risquer de casser le code existant.

**Interface Segregation Principle (Principe de ségrégation des interfaces)** : Les interfaces du module common sont petites et spécifiques à un besoin particulier. Par exemple, TokenProvider définit uniquement les méthodes nécessaires pour créer des tokens, sans inclure de méthodes non liées. De même, EventListener ne définit qu'une méthode onEvent(), sans forcer les implémentations à fournir d'autres fonctionnalités non pertinentes.

Ce principe évite les "fat interfaces" qui forcent les implémentations à fournir des méthodes qu'elles n'utilisent pas. Il rend également le code plus facile à comprendre, car les interfaces sont focalisées et leur rôle est immédiatement clair.

**Dependency Inversion Principle (Principe d'inversion des dépendances)** : Les modules de haut niveau ne dépendent pas des modules de bas niveau, mais des abstractions. Par exemple, les services qui utilisent JwtService dépendent de l'interface TokenProvider, pas de l'implémentation concrète JwtService. Cela permet de changer l'implémentation (par exemple, passer de JWT à un autre type de token) sans modifier les services qui l'utilisent.

Ce principe favorise le découplage et facilite les tests, car les dépendances peuvent être facilement mockées. Il favorise également la réutilisabilité, car les composants ne sont pas liés à des implémentations spécifiques.

### Autres principes appliqués

**DRY (Don't Repeat Yourself)** : Le module common élimine complètement la duplication de code. Toute fonctionnalité qui est utilisée par plusieurs services est centralisée dans le module common. Par exemple, au lieu que chaque service implémente sa propre logique de validation d'email, tous utilisent EmailValidator du module common.

Cette approche réduit considérablement les efforts de maintenance. Si un bug est découvert dans la validation d'email, il suffit de le corriger une seule fois dans le module common, et tous les services en bénéficient automatiquement après mise à jour de la dépendance.

**KISS (Keep It Simple, Stupid)** : Les implémentations du module common sont aussi simples que possible tout en restant complètes. Les méthodes font une seule chose et la font bien. Les noms sont clairs et explicites. Le code évite les optimisations prématurées et préfère la clarté à la performance, sauf dans les cas où la performance est critique (comme les opérations cryptographiques).

Par exemple, la méthode `StringUtils.isEmpty(String)` a une implémentation très simple : elle vérifie si la chaîne est null ou vide après avoir enlevé les espaces. Il n'y a pas de logique complexe ou de cas spéciaux, juste le comportement le plus simple et le plus prévisible.

**YAGNI (You Aren't Gonna Need It)** : Le module common ne contient que les fonctionnalités qui sont réellement nécessaires maintenant. Il ne prédit pas les besoins futurs et n'inclut pas de code "au cas où". Chaque classe, chaque méthode a été ajoutée parce qu'au moins un service en avait besoin.

Cette approche évite le sur-engineering et garde le module léger et focalisé. Si une nouvelle fonctionnalité devient nécessaire, elle peut être ajoutée à ce moment-là, avec une meilleure compréhension des besoins réels.

**Separation of Concerns (Séparation des préoccupations)** : Les différentes préoccupations sont clairement séparées dans des packages distincts. La configuration est dans `config`, les DTOs dans `dto`, la sécurité dans `security`, etc. Cette organisation facilite la navigation dans le code et rend l'architecture immédiatement compréhensible.

Cette séparation est également reflétée au niveau des classes. Par exemple, JwtService se concentre uniquement sur la manipulation des tokens JWT, tandis que JwtConfig gère la configuration. Ces deux préoccupations auraient pu être mélangées dans une seule classe, mais leur séparation rend le code plus clair et plus testable.

**Fail-Fast Principle (Principe d'échec rapide)** : Le module common échoue rapidement en cas d'erreur plutôt que d'essayer de continuer avec des données invalides. Par exemple, si un token JWT est invalide, JwtService lève immédiatement une exception plutôt que de retourner des données partielles ou incorrectes.

Ce principe facilite le débogage car les erreurs sont détectées au plus tôt, près de leur source. Il améliore également la fiabilité car le système ne se met pas dans un état incohérent en continuant avec des données invalides.

---

## 👨‍💻 Guide de développement

### Ajout d'un nouveau composant

Lorsque vous devez ajouter un nouveau composant au module common, suivez cette procédure pour garantir la cohérence avec le reste du module.

**Étape 1 : Identifier le besoin**

Avant d'ajouter quoi que ce soit au module common, assurez-vous que la fonctionnalité est réellement partagée entre plusieurs services. Le module common n'est pas un fourre-tout pour tout le code utilitaire. Il doit contenir uniquement ce qui est vraiment commun à plusieurs microservices.

Posez-vous ces questions :
- Cette fonctionnalité est-elle utilisée par au moins deux microservices ?
- Cette fonctionnalité est-elle vraiment transversale ou spécifique à un domaine métier ?
- Cette fonctionnalité ne contient-elle que de la logique technique, sans logique métier spécifique ?

Si la réponse est "oui" à toutes ces questions, la fonctionnalité a sa place dans le module common.

**Étape 2 : Choisir le package approprié**

Selon la nature de la fonctionnalité, choisissez le package approprié :
- Configuration Spring → `config`
- Structure de données pour API → `dto`
- Exception métier → `exception`
- Composant de sécurité → `security`
- Conversion de données → `mapper`
- Méthode utilitaire → `util` (avec le sous-package approprié)
- Annotation AOP → `annotation`
- Valeur constante → `constant`
- Événement → `event`

**Étape 3 : Créer la classe**

Créez la classe avec les bonnes pratiques :

```java
package com.yowyob.common.util.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utilitaire pour le hachage cryptographique.
 * 
 * Cette classe fournit des méthodes pour calculer des hashes
 * sécurisés de données en utilisant différents algorithmes.
 * 
 * Tous les algorithmes utilisés sont thread-safe.
 * 
 * @author YowYob Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class HashUtils {
    
    /**
     * Calcule le hash SHA-256 d'une chaîne de caractères.
     * 
     * @param input la chaîne à hacher
     * @return le hash encodé en Base64
     * @throws IllegalArgumentException si input est null
     */
    public static String sha256(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Calcule le hash MD5 d'une chaîne de caractères.
     * 
     * Note : MD5 n'est pas considéré comme cryptographiquement sûr
     * et ne devrait être utilisé que pour des checksums non critiques.
     * 
     * @param input la chaîne à hacher
     * @return le hash encodé en Base64
     * @throws IllegalArgumentException si input est null
     */
    public static String md5(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 algorithm not available", e);
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
}
```

**Étape 4 : Créer les tests**

Chaque nouveau composant doit avoir ses tests :

```java
package com.yowyob.common.util.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HashUtils Tests")
class HashUtilsTest {
    
    @Test
    @DisplayName("Should calculate consistent SHA-256 hash")
    void shouldCalculateConsistentSha256() {
        // Given
        String input = "test data";
        
        // When
        String hash1 = HashUtils.sha256(input);
        String hash2 = HashUtils.sha256(input);
        
        // Then
        assertNotNull(hash1);
        assertEquals(hash1, hash2, "Same input should produce same hash");
    }
    
    @Test
    @DisplayName("Should produce different hashes for different inputs")
    void shouldProduceDifferentHashes() {
        // Given
        String input1 = "test data 1";
        String input2 = "test data 2";
        
        // When
        String hash1 = HashUtils.sha256(input1);
        String hash2 = HashUtils.sha256(input2);
        
        // Then
        assertNotEquals(hash1, hash2, "Different inputs should produce different hashes");
    }
    
    @Test
    @DisplayName("Should throw exception for null input")
    void shouldThrowExceptionForNullInput() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> HashUtils.sha256(null),
            "Should throw exception for null input");
    }
}
```

**Étape 5 : Documenter**

Ajoutez la documentation nécessaire :
- Javadoc complète sur la classe et les méthodes publiques
- Commentaires dans le code pour les parties complexes
- Mise à jour du README si c'est une fonctionnalité importante
- Exemples d'utilisation si approprié

**Étape 6 : Intégrer**

Suivez le processus Git standard :

```bash
# Créer une branche feature
git checkout -b feature/add-hash-utils

# Commiter les changements
git add .
git commit -m "feat(util): add HashUtils for cryptographic hashing

- Add SHA-256 and MD5 hashing methods
- Include comprehensive unit tests
- Document usage and security considerations"

# Pousser et créer une pull request
git push origin feature/add-hash-utils
```

### Conventions de codage

Le module common suit des conventions de codage strictes pour garantir la cohérence et la lisibilité.

**Nommage** :
- Classes : PascalCase (`UserService`, `JwtConfig`)
- Méthodes : camelCase (`generateToken`, `validateEmail`)
- Constantes : UPPER_SNAKE_CASE (`MAX_PAGE_SIZE`, `JWT_EXPIRATION_MS`)
- Variables : camelCase (`userId`, `tokenExpiration`)
- Packages : lowercase (`com.yowyob.common.security.jwt`)

**Organisation du code** :
- Imports organisés : java, javax, puis spring, puis autres, puis package local
- Pas d'imports avec wildcards (*)
- Champs privés en haut, puis constructeurs, puis méthodes publiques, puis méthodes privées
- Méthodes statiques au début de la section des méthodes

**Documentation** :
- Toutes les classes publiques ont une Javadoc
- Toutes les méthodes publiques ont une Javadoc avec @param et @return
- Les exceptions levées sont documentées avec @throws
- Le code complexe a des commentaires explicatifs

**Formatage** :
- Indentation : 4 espaces (pas de tabs)
- Longueur de ligne : 120 caractères maximum
- Accolades : style Java (ouverture sur la même ligne)
- Espaces autour des opérateurs

Ces conventions sont vérifiées automatiquement par Checkstyle lors du build.

### Processus de revue de code

Toute modification du module common doit passer par une revue de code avant d'être intégrée. Voici les points vérifiés lors de la revue :

**Qualité du code** :
- Le code respecte-t-il les conventions de codage ?
- Le code est-il lisible et bien structuré ?
- Les noms sont-ils explicites et significatifs ?
- Y a-t-il du code mort ou commenté à supprimer ?

**Tests** :
- Tous les nouveaux composants ont-ils des tests ?
- Les tests couvrent-ils les cas normaux et les cas d'erreur ?
- Le coverage reste-t-il au-dessus de 80% ?
- Les tests sont-ils stables (pas de flakey tests) ?

**Documentation** :
- La Javadoc est-elle complète et claire ?
- Les changements importants sont-ils documentés dans le README ?
- Les exemples d'utilisation sont-ils fournis si nécessaire ?

**Architecture** :
- Le nouveau composant respecte-t-il les principes SOLID ?
- Le composant est-il vraiment partagé ou spécifique à un service ?
- Le composant a-t-il des dépendances appropriées ?
- Le composant est-il testable indépendamment ?

**Sécurité** :
- Y a-t-il des risques de sécurité (injection, XSS, etc.) ?
- Les données sensibles sont-elles correctement protégées ?
- Les erreurs sont-elles gérées sans exposer d'informations sensibles ?

**Performance** :
- Y a-t-il des problèmes de performance évidents ?
- Les ressources (connexions, fichiers) sont-elles correctement fermées ?
- Les boucles et collections sont-elles optimisées ?

---

## 🔄 Maintenance et évolution

### Gestion des versions

Le module common suit le versionnement sémantique (SemVer) : MAJOR.MINOR.PATCH

- **MAJOR** : Changements incompatibles avec les versions précédentes (breaking changes)
- **MINOR** : Nouvelles fonctionnalités rétrocompatibles
- **PATCH** : Corrections de bugs rétrocompatibles

Exemples :
- Version 1.0.0 → 1.0.1 : Correction d'un bug dans DateUtils
- Version 1.0.1 → 1.1.0 : Ajout de nouveaux utilitaires de validation
- Version 1.1.0 → 2.0.0 : Changement de la signature de JwtService.generateToken()

### Compatibilité ascendante

Le module common s'engage à maintenir la compatibilité ascendante autant que possible. Cela signifie que les services peuvent mettre à jour vers une nouvelle version MINOR ou PATCH sans avoir à modifier leur code.

Les breaking changes (changements de version MAJOR) sont évités autant que possible et ne sont introduits que lorsque c'est absolument nécessaire. Lorsqu'un breaking change est inévitable, il est :
- Annoncé à l'avance dans les release notes
- Documenté clairement avec un guide de migration
- Accompagné d'une période de dépréciation si possible

### Procédure de dépréciation

Lorsqu'une fonctionnalité doit être retirée ou remplacée, suivez cette procédure :

1. **Marquer comme @Deprecated** : Annotez la méthode ou classe avec @Deprecated
2. **Ajouter une Javadoc** : Expliquez pourquoi c'est déprécié et quelle alternative utiliser
3. **Logger un warning** : La première utilisation log un warning
4. **Attendre deux versions MINOR** : Gardez la fonctionnalité deprecated pendant au moins deux versions
5. **Retirer** : Retirez dans une version MAJOR suivante

Exemple :

```java
/**
 * @deprecated Utiliser {@link #newMethod(String, int)} à la place.
 * Cette méthode sera retirée dans la version 2.0.0.
 */
@Deprecated(since = "1.5.0", forRemoval = true)
public void oldMethod(String param) {
    log.warn("oldMethod is deprecated. Use newMethod instead.");
    // Implementation...
}
```

### Changelog

Toutes les modifications sont documentées dans un fichier CHANGELOG.md à la racine du module. Le changelog suit le format Keep a Changelog et inclut :

- **Added** : Nouvelles fonctionnalités
- **Changed** : Modifications de fonctionnalités existantes
- **Deprecated** : Fonctionnalités dépréciées
- **Removed** : Fonctionnalités retirées
- **Fixed** : Corrections de bugs
- **Security** : Corrections de vulnérabilités de sécurité

Exemple de changelog :

```markdown
# Changelog

Tous les changements notables du module yowyob-common seront documentés dans ce fichier.

Le format est basé sur [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
et ce projet adhère au [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.0] - 2026-01-15

### Added
- Ajout de HashUtils pour le hachage cryptographique (SHA-256, MD5)
- Ajout de FileValidator.validateMimeType() pour validation par magic numbers
- Ajout de CoordinateValidator pour validation de coordonnées GPS

### Changed
- Amélioration des performances de StringUtils.sanitize() (30% plus rapide)
- Mise à jour de JJWT vers 0.12.3 pour support des derniers algorithmes

### Fixed
- Correction du bug dans DateUtils.parse() qui ne gérait pas correctement les leap seconds
- Correction de la fuite mémoire dans EventPublisher lors de publication massive

### Security
- Correction de la vulnérabilité XSS dans TextSanitizer.sanitizeHtml()

## [1.1.0] - 2025-12-01

### Added
- Ajout de PhoneValidator avec support international
- Ajout de @RateLimited annotation pour limitation de taux

### Deprecated
- JwtService.oldGenerateToken() est maintenant déprécié, utiliser generateToken() à la place

## [1.0.0] - 2025-11-01

### Added
- Version initiale du module common
- Configuration Spring complète (CORS, Jackson, Redis, Kafka, etc.)
- DTOs standardisés (ApiResponse, PageResponse, ErrorResponse)
- Système complet de gestion des exceptions
- Composants de sécurité (JWT, Password, Encryption)
- Mappers MapStruct
- Utilitaires complets (Date, String, Validation, File, Network, Geo)
- Annotations personnalisées (@LogExecutionTime, @Cacheable, etc.)
- Constantes globales
- Système d'événements
```

### Support et assistance

Pour obtenir de l'aide sur le module common :

1. **Documentation** : Consultez d'abord ce README et la Javadoc
2. **Exemples** : Regardez les tests unitaires pour des exemples d'utilisation
3. **Issues GitHub** : Ouvrez une issue pour rapporter un bug ou suggérer une amélioration
4. **Contact direct** : **ATTENTION - INFORMATIONS SENSIBLES**

Pour des raisons de sécurité, les véritables informations de contact (numéros de téléphone, emails personnels) **ne sont jamais incluses dans les fichiers publics du repository**. Si vous avez besoin de contacter l'équipe technique :

- Utilisez le système d'issues GitHub du projet
- Contactez votre chef de projet ou coordinateur d'équipe
- Pour les membres de l'équipe 4GI-ENSPY Promo 2027, référez-vous aux canaux de communication internes (Teams, WhatsApp groupe)

Les vraies coordonnées sont partagées **uniquement** de manière privée et sécurisée entre les membres actifs du projet.

---

## 📝 Conclusion

Le module yowyob-common est le fondement technique sur lequel repose toute la plateforme YowYob Search. En centralisant les fonctionnalités transversales, les configurations standardisées et les utilitaires partagés, il permet aux différents microservices de se concentrer sur leur logique métier spécifique tout en bénéficiant d'une base technique solide et cohérente.

Ce module a été conçu avec soin pour respecter les principes de génie logiciel modernes (SOLID, DRY, KISS, YAGNI), pour être facilement testable et maintenable, et pour évoluer de manière contrôlée à travers un versionnement sémantique strict.

En suivant les directives de ce README, les développeurs peuvent contribuer au module common de manière cohérente et professionnelle, garantissant que la qualité et la fiabilité du module restent élevées au fil du temps.

Le succès de la plateforme YowYob repose en grande partie sur la solidité de ce module commun. C'est pourquoi chaque ajout, chaque modification, chaque correction doit être fait avec le plus grand soin, en gardant toujours à l'esprit que ce module sert de fondation à l'ensemble du système.

---

## 📚 Références et ressources

### Documentation technique

- [Spring Framework Documentation](https://docs.spring.io/spring-framework/reference/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [MapStruct Documentation](https://mapstruct.org/documentation/stable/reference/html/)
- [JJWT Documentation](https://github.com/jwtk/jjwt#documentation)

### Standards et bonnes pratiques

- [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Clean Code Principles](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)

### Outils de développement

- [Maven Central Repository](https://search.maven.org/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

---

**Document préparé par l'équipe YowYob - 4GI ENSPY Promo 2027**

**Date de dernière mise à jour** : Décembre 2025  
**Version du document** : 1.0.0  
**Version du module** : 1.0.0

---

*Ce README a été conçu pour servir de référence complète lors de la génération automatique du module common par une IA ou lors du développement manuel par l'équipe. Tous les détails techniques, architecturaux et organisationnels ont été inclus pour faciliter une implémentation fidèle aux spécifications du projet YowYob Search.*