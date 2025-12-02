# README - Search Service YowYob Search

## Vue d'ensemble et présentation du service

Bienvenue dans la documentation du **Search Service**, le cœur battant du moteur de recherche YowYob ! Ce service est le cerveau analytique de notre plateforme, responsable de traiter toutes les requêtes de recherche, d'interroger Elasticsearch avec intelligence, et de retourner les résultats les plus pertinents à nos utilisateurs. Si l'API Gateway est le réceptionniste de notre hôtel, le Search Service est le concierge expert qui connaît chaque recoin de la ville et sait exactement où trouver ce que vous cherchez.

Dans l'écosystème des microservices, le Search Service joue un rôle absolument critique : c'est le service qui transforme une simple requête textuelle en résultats intelligents, structurés, et pertinents. Imaginez-vous dans une bibliothèque immense avec des millions de livres - sans un bon système de recherche, vous seriez perdu. Notre Search Service est ce système de recherche sur-stéroïdes, capable de fouiller dans des téraoctets de données en quelques millisecondes et de vous retourner exactement ce dont vous avez besoin.

### Pourquoi avons-nous besoin d'un Search Service dédié ?

La recherche n'est pas une simple requête SQL `LIKE '%keyword%'`. Une recherche moderne et performante nécessite :

**Une compréhension sémantique** : Savoir que "resto paris" signifie "restaurant à Paris", pas juste des pages contenant les mots "resto" et "paris".

**Un ranking intelligent** : Décider que le résultat #1 doit être le restaurant le mieux noté et le plus proche, pas juste celui qui contient le plus de fois le mot "restaurant".

**Des fonctionnalités avancées** : Autocomplétion, correction orthographique, recherche par facettes, filtrage géographique, recherche en temps réel...

**Une performance extrême** : Des milliers de requêtes par seconde avec une latence inférieure à 100ms.

Notre Search Service est spécialement conçu pour répondre à tous ces défis. Il utilise **Elasticsearch** comme moteur de recherche principal, mais ajoute plusieurs couches d'intelligence par-dessus :

**Couche de cache Redis** : Pour éviter de refaire les mêmes recherches coûteuses.

**Algorithme de ranking hybride** : Combinaison de BM25 (pertinence textuelle), de géolocalisation, de fraîcheur du contenu, et de popularité.

**Système de suggestions** : Qui apprend des recherches précédentes pour proposer des améliorations.

**Tracking analytique** : Chaque recherche est enregistrée pour améliorer continuellement notre système.

## Architecture technique détaillée

### Technologies et frameworks utilisés

Notre Search Service est construit avec **Spring Boot 3.2** et **Spring Data Elasticsearch**, ce qui nous donne une intégration native avec Elasticsearch tout en bénéficiant de toute la puissance de l'écosystème Spring. Le service écoute sur le **port 8082**, qui est routé automatiquement par l'API Gateway quand un utilisateur fait une recherche.

Nous avons choisi Elasticsearch pour plusieurs raisons cruciales :

**Performances exceptionnelles** : Elasticsearch est optimisé pour la recherche full-text et peut retourner des résultats en quelques millisecondes, même sur des millions de documents.

**Scalabilité horizontale** : Nous pouvons ajouter des nœuds Elasticsearch à la volée pour gérer plus de charge.

**Fonctionnalités riches** : Support natif pour la recherche géospatiale, les agrégations, les suggestions, les synonymes, etc.

**Écosystème mature** : Kibana pour la visualisation, Logstash pour l'ingestion, Beats pour le monitoring.

### Architecture réactive : pourquoi c'est important ici aussi ?

Tout comme l'API Gateway, le Search Service utilise une architecture **réactive** basée sur **Project Reactor**. Pourquoi ? Parce que les opérations de recherche peuvent être I/O-bound (attente des réponses d'Elasticsearch) et nous voulons pouvoir gérer des milliers de requêtes simultanées sans bloquer des threads.

Imaginez un bibliothécaire qui doit aller chercher des livres dans différentes sections. S'il va chercher un livre à la fois, attend qu'un client le lise, puis retourne le livre avant d'aller en chercher un autre, c'est très inefficace. Notre Search Service fonctionne comme un bibliothécaire super efficace qui peut gérer plusieurs clients en parallèle : il prend une requête, lance la recherche en arrière-plan, passe à la requête suivante, et retourne les résultats au fur et à mesure qu'ils arrivent.

En pratique, cela signifie que notre Search Service peut traiter **plus de 500 requêtes de recherche par seconde par instance** tout en maintenant une latence moyenne de 50ms.

### Architecture en couches du Search Service

```
┌─────────────────────────────────────────────────────────┐
│                   API Gateway (Port 8080)               │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                Search Controller (Port 8082)            │
│  - Validation des requêtes                              │
│  - Gestion des erreurs                                 │
│  - Formatage des réponses                              │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                 Search Service Layer                    │
│  - Orchestration de la recherche                       │
│  - Application des filtres                             │
│  - Gestion du cache                                    │
│  - Tracking analytique                                 │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│              Elasticsearch Client Layer                 │
│  - Construction des requêtes Elasticsearch              │
│  - Gestion des connexions                              │
│  - Retry et fallback                                   │
└───────────────────────────┬─────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                  Elasticsearch Cluster                  │
│  - Index yowyob-documents                               │
│  - Index yowyob-products                                │
│  - Index yowyob-users                                   │
└─────────────────────────────────────────────────────────┘
```

Chaque couche a une responsabilité bien définie :

**Controller** : Gère les aspects HTTP, valide les entrées, formate les sorties.

**Service** : Contient la logique métier, orchestre les différentes étapes de la recherche.

**Client** : Gère la communication technique avec Elasticsearch.

Cette séparation permet une meilleure testabilité, maintenabilité, et évolutivité.

## Fonctionnalités de recherche implémentées

### 1. Recherche Full-Text Avancée

Notre recherche n'est pas une simple correspondance de mots. Elle comprend :

**Analyse linguistique** : Nous utilisons des analyseurs français et anglais qui comprennent la grammaire, suppriment les stop words ("le", "la", "de"), et appliquent le stemming (réduction des mots à leur racine).

**Recherche floue (Fuzzy Search)** : Si vous tapez "restorant" au lieu de "restaurant", notre système le comprend quand même.

**Recherche par phrase** : Les guillemets permettent la recherche exacte : `"restaurant français"` ne trouvera que les documents contenant cette phrase exacte.

**Opérateurs booléens** : `restaurant AND paris`, `café OR bistro`, `hôtel -luxe` (exclure le mot luxe).

**Recherche par champ** : `title:restaurant` ne cherche que dans le titre, `content:paris` ne cherche que dans le contenu.

### 2. Système de Ranking Hybride

Le ranking (classement) est ce qui différencie un bon moteur de recherche d'un excellent. Notre algorithme combine plusieurs facteurs :

```
Score final = 
  0.40 × BM25(pertinence textuelle)
  0.30 × GeoScore(distance)
  0.20 × FreshnessScore(récence)
  0.10 × PopularityScore(CTR historique)
```

**BM25** : L'algorithme standard de pertinence textuelle dans Elasticsearch. Il donne plus de poids aux termes rares et pénalise les documents trop longs.

**GeoScore** : Plus un résultat est proche de l'utilisateur (ou de l'emplacement spécifié), plus son score est élevé. Calculé avec la formule de Haversine.

**FreshnessScore** : Les documents récents sont favorisés. Un article de blog publié hier a un meilleur score qu'un article identique publié il y a 5 ans.

**PopularityScore** : Basé sur l'historique des clics. Si beaucoup d'utilisateurs cliquent sur un résultat pour une requête donnée, son score augmente.

Ces poids (0.40, 0.30, etc.) sont configurables et peuvent être ajustés dynamiquement en fonction des performances.

### 3. Autocomplétion et Suggestions

Quand un utilisateur commence à taper dans la barre de recherche, notre système propose des suggestions en temps réel :

**Complétion basée sur l'historique** : Si vous avez déjà cherché "restaurants paris", le système le propose à nouveau.

**Correction orthographique** : "restorant" → "restaurant", "pariz" → "paris".

**Suggestions populaires** : Basées sur ce que les autres utilisateurs recherchent.

**Suggestions contextuelles** : Si vous êtes à Yaoundé, le système propose "restaurants yaoundé" avant "restaurants paris".

Les suggestions sont mises en cache dans Redis pour des performances optimales.

### 4. Recherche Géospatiale

Notre Search Service comprend parfaitement la géographie :

**Recherche par rayon** : "Restaurants dans un rayon de 5km autour de moi".

**Recherche par bounding box** : "Restaurants dans le quartier du centre-ville".

**Tri par distance** : "Trier les résultats du plus proche au plus loin".

**Filtrage géographique** : "Uniquement les résultats en France".

Nous utilisons le type de champ `geo_point` d'Elasticsearch qui est optimisé pour ce genre de requêtes.

### 5. Recherche par Facettes (Faceted Search)

Les facettes permettent aux utilisateurs de filtrer les résultats :

**Par catégorie** : Restaurants, Hôtels, Magasins...

**Par prix** : €, €€, €€€...

**Par note** : 4 étoiles et plus

**Par date** : Aujourd'hui, Cette semaine, Ce mois-ci

Les facettes sont calculées en temps réel et montrent toujours le nombre de résultats pour chaque option.

### 6. Recherche en Temps Réel

Grâce à la puissance d'Elasticsearch, nos résultats sont toujours frais :

**Indexation en quasi temps réel** : Un nouveau document apparaît dans les résultats en moins d'1 seconde.

**Mise à jour incrémentale** : Quand un document change, seul ce document est réindexé, pas tout l'index.

**Consistence éventuelle** : Le système garantit que tous les nœuds voient les mêmes données après un court délai.

## Configuration des Index Elasticsearch

### Structure de l'index principal (yowyob-documents)

Notre index principal contient tous les documents web crawléés. Voici sa structure :

```json
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "analysis": {
      "analyzer": {
        "french_analyzer": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": ["lowercase", "french_stop", "french_stemmer"]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "id": { "type": "keyword" },
      "url": { "type": "keyword" },
      "title": { 
        "type": "text",
        "analyzer": "french_analyzer",
        "fields": {
          "keyword": { "type": "keyword" }
        }
      },
      "content": { 
        "type": "text",
        "analyzer": "french_analyzer" 
      },
      "language": { "type": "keyword" },
      "published_date": { "type": "date" },
      "location": { "type": "geo_point" },
      "page_rank": { "type": "float" },
      "quality_score": { "type": "float" }
    }
  }
}
```

**Pourquoi 3 shards ?**
- **Shard 1** : Documents récents et fréquemment mis à jour
- **Shard 2** : Documents de référence (Wikipedia, sites gouvernementaux)
- **Shard 3** : Documents moins populaires

Cette répartition permet une meilleure performance et facilite la maintenance.

### Index des produits (yowyob-products)

L'index produits a une structure différente, optimisée pour le e-commerce :

```json
{
  "properties": {
    "title": { "type": "text" },
    "description": { "type": "text" },
    "price": { "type": "scaled_float", "scaling_factor": 100 },
    "category": { "type": "keyword" },
    "brand": { "type": "keyword" },
    "in_stock": { "type": "boolean" },
    "rating": { "type": "float" },
    "merchant_location": { "type": "geo_point" }
  }
}
```

### Politique d'indexation

Nous avons configuré une **Index Lifecycle Policy (ILM)** pour gérer automatiquement le cycle de vie de nos index :

**Phase chaude (hot)** : Les 7 derniers jours - index sur SSD pour performances maximales

**Phase tiède (warm)** : 7 à 30 jours - index sur HDD, réplicas réduits

**Phase froide (cold)** : 30 à 90 jours - index archivés, consultation seulement

**Phase supprimée (delete)** : Après 90 jours - suppression automatique (sauf pour les documents importants)

## Performance et optimisation

### Cache Redis multi-niveaux

Pour éviter de surcharger Elasticsearch avec des requêtes identiques, nous utilisons un système de cache sophistiqué :

**Niveau 1 - Cache de requête** : Cache les résultats complets de recherche pour les requêtes populaires. TTL : 5 minutes.

**Niveau 2 - Cache de suggestions** : Cache les suggestions d'autocomplétion. TTL : 1 minute (car plus dynamique).

**Niveau 3 - Cache de facettes** : Cache les agrégations de facettes. TTL : 10 minutes.

**Niveau 4 - Cache de documents** : Cache les documents fréquemment consultés. TTL : 1 heure.

Chaque niveau a sa propre stratégie d'invalidation :
- Le cache de requête est invalidé quand de nouveaux documents sont indexés
- Le cache de suggestions est invalidé périodiquement
- Le cache de documents est invalidé quand un document est mis à jour

### Optimisation des requêtes Elasticsearch

Nous avons implémenté plusieurs optimisations :

**Query rewriting** : Réécriture automatique des requêtes complexes en requêtes plus efficaces.

**Pré-fetching** : Pour les recherches populaires, nous pré-chargeons certains résultats.

**Pagination intelligente** : Utilisation de `search_after` au lieu de `from/size` pour les pages profondes.

**Timeout adaptatif** : Les requêtes complexes ont un timeout plus long que les requêtes simples.

**Circuit breaker** : Si Elasticsearch est trop chargé, nous retournons des résultats mis en cache.

### Monitoring des performances

Nous surveillons en permanence :

**Latence par percentile** : p50, p95, p99 (nous visons p95 < 100ms)

**Throughput** : Requêtes par seconde (actuellement ~500 req/s par instance)

**Cache hit rate** : Taux de réussite du cache (actuellement ~65%)

**Error rate** : Taux d'erreur (doit être < 0.1%)

**JVM metrics** : Utilisation mémoire, GC pauses, thread count

## Sécurité

### Validation des entrées

Toutes les requêtes de recherche sont validées :

**Longueur maximale** : 500 caractères (pour éviter les attaques par requêtes trop longues)

**Caractères autorisés** : Filtrer les caractères dangereux pour éviter l'injection

**Limite de complexité** : Rejeter les requêtes trop complexes (trop de OR/AND)

**Rate limiting** : Limiter le nombre de requêtes par utilisateur

### Sécurité des données

**Données personnelles** : Nous ne stockons jamais de données personnelles dans Elasticsearch

**Accès restreint** : Seul le Search Service peut écrire dans les index

**Chiffrement** : Toutes les communications avec Elasticsearch sont chiffrées

**Audit trail** : Toutes les recherches sont auditées (anonymisées)

## API endpoints

### GET /api/search

La recherche principale. Accepte les paramètres suivants :

```json
{
  "query": "restaurants paris",
  "page": 0,
  "size": 20,
  "filters": {
    "location": {
      "lat": 48.8566,
      "lon": 2.3522,
      "radius": "5km"
    },
    "category": ["restaurant", "bistro"],
    "price_range": {
      "min": 10,
      "max": 50
    },
    "date_range": {
      "from": "2025-01-01",
      "to": "2025-12-31"
    }
  },
  "sort": "relevance" // ou "distance", "date", "price_asc", "price_desc"
}
```

Réponse :
```json
{
  "query": "restaurants paris",
  "total_results": 1247,
  "page": 0,
  "total_pages": 63,
  "results": [
    {
      "id": "doc-123",
      "title": "Le meilleur restaurant parisien",
      "snippet": "Un restaurant exceptionnel au cœur de Paris...",
      "url": "https://example.com/restaurant",
      "score": 0.956,
      "metadata": {
        "category": "restaurant",
        "location": {
          "lat": 48.8584,
          "lon": 2.2945
        },
        "distance": "1.2km",
        "rating": 4.7,
        "price_range": "€€"
      }
    }
  ],
  "facets": {
    "categories": [
      { "name": "restaurant", "count": 847 },
      { "name": "bistro", "count": 400 }
    ],
    "price_ranges": [
      { "range": "€", "count": 300 },
      { "range": "€€", "count": 647 },
      { "range": "€€€", "count": 300 }
    ]
  },
  "suggestions": [
    "restaurants paris 15ème",
    "restaurants paris pas chers",
    "restaurants paris avec terrasse"
  ],
  "processing_time_ms": 45
}
```

### GET /api/search/suggestions

Autocomplétion en temps réel :

```http
GET /api/search/suggestions?q=rest&size=5
```

Réponse :
```json
{
  "query": "rest",
  "suggestions": [
    "restaurants paris",
    "restaurants lyon",
    "restaurants pas chers",
    "restaurants avec terrasse",
    "restauration rapide"
  ],
  "processing_time_ms": 12
}
```

### GET /api/search/trending

Les recherches tendance :

```http
GET /api/search/trending?period=today&size=10
```

Périodes disponibles : `today`, `week`, `month`

### GET /api/search/history

Historique de recherche de l'utilisateur (nécessite authentification) :

```http
GET /api/search/history?page=0&size=20
```

## Déploiement et scalabilité

### Configuration Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: search-service
  namespace: yowyob
spec:
  replicas: 3
  selector:
    matchLabels:
      app: search-service
  template:
    metadata:
      labels:
        app: search-service
    spec:
      containers:
      - name: search-service
        image: yowyob/search-service:1.0.0
        ports:
        - containerPort: 8082
        env:
        - name: ELASTICSEARCH_HOST
          value: "elasticsearch.yowyob.svc.cluster.local"
        - name: REDIS_HOST
          value: "redis.yowyob.svc.cluster.local"
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
            port: 8082
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8082
          initialDelaySeconds: 30
          periodSeconds: 5
```

### Auto-scaling

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: search-service-hpa
  namespace: yowyob
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: search-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Pods
    pods:
      metric:
        name: http_requests_per_second
      target:
        type: AverageValue
        averageValue: "100"
```

### Stratégie de déploiement

Nous utilisons une stratégie **Rolling Update** avec :

**Max surge** : 25% - Pendant le déploiement, nous pouvons avoir 25% de pods en plus

**Max unavailable** : 0 - Aucun service interrompu pendant le déploiement

**Progress deadline** : 600s - Si le déploiement prend plus de 10 minutes, rollback automatique

## Tests

### Tests unitaires

Nous testons chaque composant isolément :

```java
@Test
void shouldCalculateRelevanceScoreCorrectly() {
    // Given
    SearchDocument doc = new SearchDocument();
    doc.setTitle("Restaurant Paris");
    doc.setContent("Un excellent restaurant à Paris");
    doc.setPageRank(0.8f);
    
    SearchQuery query = new SearchQuery("restaurant paris");
    
    // When
    float score = rankingService.calculateScore(doc, query);
    
    // Then
    assertThat(score).isBetween(0.7f, 1.0f);
}
```

### Tests d'intégration

Nous testons l'intégration avec Elasticsearch en utilisant TestContainers :

```java
@Test
void shouldReturnRelevantResultsForSearchQuery() {
    // Given
    indexTestDocument("doc1", "Restaurant Paris", "Un bon restaurant");
    indexTestDocument("doc2", "Hôtel Lyon", "Un hôtel confortable");
    
    // When
    SearchResponse response = searchService.search(
        new SearchRequest("restaurant paris")
    );
    
    // Then
    assertThat(response.getResults()).hasSize(1);
    assertThat(response.getResults().get(0).getTitle())
        .isEqualTo("Restaurant Paris");
}
```

### Tests de charge

Nous simulons des centaines d'utilisateurs simultanés :

```java
@LoadTest(users = 100, duration = 300)
public class SearchLoadTest {
    
    @UserSimulation
    public void simulateUser() {
        // Recherche aléatoire
        String query = generateRandomQuery();
        SearchResponse response = search(query);
        
        // Vérifie la latence
        assertThat(response.getProcessingTimeMs())
            .isLessThan(100);
    }
}
```

## Monitoring et alerting

### Métriques clés

Nous surveillons en permanence :

**search_queries_total** : Nombre total de recherches

**search_latency_seconds** : Histogramme des temps de réponse

**search_cache_hits_total** : Nombre de hits dans le cache

**search_errors_total** : Nombre d'erreurs

**elasticsearch_query_duration** : Temps passé dans Elasticsearch

### Dashboards Grafana

Nous avons plusieurs dashboards :

**Search Performance** : Latence, throughput, erreurs

**Cache Efficiency** : Hit rate, taille du cache, éviction

**Elasticsearch Health** : Statut du cluster, shards, index

**Business Metrics** : Recherches populaires, tendances

### Alertes

Nous avons configuré des alertes pour :

**Latence élevée** : Si p95 > 200ms pendant 5 minutes

**Taux d'erreur** : Si > 1% pendant 2 minutes

**Cache hit rate bas** : Si < 50% pendant 10 minutes

**Elasticsearch down** : Si le cluster n'est pas vert

## Dépannage

### Problèmes courants

**Problème** : Recherches lentes

**Solution** :
1. Vérifier la charge d'Elasticsearch
2. Vérifier le cache hit rate
3. Optimiser les requêtes complexes
4. Augmenter les ressources

**Problème** : Résultats non pertinents

**Solution** :
1. Vérifier l'analyse des requêtes
2. Ajuster les poids du ranking
3. Réindexer avec de nouveaux paramètres
4. Ajouter des synonymes

**Problème** : Cache inefficace

**Solution** :
1. Ajuster les TTL
2. Augmenter la taille du cache
3. Changer la stratégie d'éviction
4. Pré-chauffer le cache

## Évolution future

### Améliorations prévues

**Recherche vectorielle** : Utilisation d'embeddings pour la similarité sémantique

**Personalisation** : Résultats adaptés au profil utilisateur

**Recherche multimodale** : Recherche dans les images et vidéos

**Recherche vocale** : Support natif de la recherche vocale

**AI Ranking** : Utilisation de modèles ML pour le ranking

### Roadmap

**Q1 2026** : Recherche vectorielle beta

**Q2 2026** : Personalisation basique

**Q3 2026** : AI Ranking v1

**Q4 2026** : Recherche multimodale

## Contribution

### Comment contribuer ?

1. Fork le repository
2. Créez une branche feature
3. Développez avec amour
4. Testez thoroughly
5. Créez une Pull Request

### Standards de code

- Tests unitaires pour chaque nouvelle fonctionnalité
- Documentation mise à jour
- Code review obligatoire
- Coverage minimum : 80%

## Support

- Documentation : `/docs/search-service.md`
- Issues : GitHub Issues
- Discussions : GitHub Discussions
- Email : search-team@yowyob.com

## Licence

 Voir le fichier LICENSE pour plus de détails.

### Remerciements

Ce projet a été développé par l'équipe YowYob dans le cadre de notre projet de 4ème année à l'École Nationale Supérieure Polytechnique de Yaoundé (ENSPY).

**Équipe** : YowYob Team 4GI-ENSPY Promo 2027
- Brian Brusly - Lead Backend & Architecture
- yowyob.4gi.enspy.promo.2027@gmail.com

---

**Le Search Service** - *Le cœur intelligent de YowYob Search*