Votre Shop Service est un **agrégateur et comparateur**, pas une marketplace.

---

# README - Shop Service YowYob Search

## Vue d'ensemble et présentation du service

Bienvenue dans la documentation du **Shop Service**, le comparateur intelligent de la plateforme YowYob Search ! Si l'API Gateway est le réceptionniste qui accueille tous les visiteurs, et le Search Service est le bibliothécaire qui connaît chaque livre, alors le Shop Service est **l'expert comparateur** qui parcourt tous les marchés du web, analyse les offres, compare les prix et caractéristiques, et vous présente une vue synthétique pour vous aider à faire le meilleur choix. **Nous ne vendons rien, nous vous aidons à trouver et comparer.**

**Focus spécifique Cameroun et Afrique** : Notre service ne se limite pas aux plateformes internationales comme Amazon. Nous avons une expertise approfondie du marché camerounais et africain. Nous indexons spécifiquement des plateformes locales comme **Jumia, Kaymu, Gloopro, et d'autres marchés populaires au Cameroun**, avec une attention particulière aux prix en **Franc CFA (FCFA)** et aux spécificités régionales (délais de livraison, disponibilité locale, etc.).

Ce service est un **système d'intelligence comparative distribué** qui scanne le web, collecte les données publiques des sites marchands, normalise les informations, et présente des comparaisons objectives. Imaginez que vous cherchiez un nouveau smartphone. Au lieu de visiter manuellement Amazon, Jumia, eBay, Cdiscount, etc., notre Shop Service fait tout cela pour vous **en moins de 200 millisecondes**, vous présente une comparaison claire, et vous permet de cliquer vers le marchand de votre choix.

### Pourquoi avons-nous besoin d'un Shop Service si sophistiqué ?

Le web marchand moderne présente des défis uniques pour les consommateurs qui veulent comparer avant d'acheter :

**La fragmentation extrême des données** : Le produit "iPhone 14 128Go Noir" peut s'appeler "Apple iPhone 14 - 128 GB - Black" sur Amazon, "iPhone 14 128Go couleur noire" sur Jumia, et "Smartphone Apple iPhone14 128Go Midnight" sur les boutiques locales. Chaque marchand utilise son propre langage, ses propres catégories, ses propres unités de mesure.

**La volatilité des prix** : Les prix changent plusieurs fois par jour. Une promotion flash chez un marchand peut faire baisser un prix de 20% pendant seulement 2 heures. Notre système doit détecter ces changements pour vous les présenter.

**La complexité des offres** : Le prix affiché n'est pas le prix final. Il faut considérer les frais de port, les taxes selon les pays africains, les délais de livraison, les politiques de retour... Notre comparateur prend tout cela en compte.

**L'asymétrie d'information** : Il est difficile de savoir quel marchand est fiable, lequel livre rapidement, lequel a les meilleures conditions. Notre système agrège ces informations publiques (avis, notes, historiques) pour vous aider à décider.

**La dispersion géographique** : Un produit peut être disponible localement à Douala, ou nécessiter une importation internationale. Notre système vous montre toutes les options avec leurs implications.

Notre Shop Service résout ces défis grâce à une architecture multi-couches qui combine :

**L'intelligence artificielle** : Pour comprendre que "128GB", "128 Go", et "128 gigaoctets" c'est la même chose.

**Le traitement distribué** : Pour scanner des milliers de sites simultanément.

**Le cache intelligent** : Pour fournir des résultats instantanés tout en maintenant la fraîcheur des données.

**La normalisation des données** : Pour présenter des comparaisons cohérentes malgré les formats disparates.

## Architecture technique détaillée

### Technologies et frameworks utilisés

Notre Shop Service est construit avec **Spring Boot 3.2** et utilise intensivement **Spring WebFlux** pour son architecture réactive. Pourquoi réactive ? Parce que nous devons lancer des centaines de requêtes HTTP simultanées vers différents sites marchands, chacune avec ses propres timeouts, ses propres formats de réponse.

Le service écoute sur le **port 8087**, mais les utilisateurs n'y accèdent jamais directement. Tout passe par l'API Gateway qui route `/api/shop/**` et `/api/products/**` vers nous.

Notre stack technique est spécialisée pour l'agrégation et la comparaison :

**Pour la collecte de données web** :
- **JSoup** : Pour parser le HTML des sites marchands
- **Apache Tika** : Pour extraire le contenu des documents
- **Selenium WebDriver** : Pour les sites JavaScript lourds (React, Angular, Vue.js)

**Pour l'intégration API** (quand disponibles) :
- **Spring Cloud OpenFeign** : Pour les APIs REST des marchands partenaires
- **GraphQL Java** : Pour les APIs GraphQL modernes
- **Apache CXF** : Pour les anciennes APIs SOAP/XML

**Pour le traitement et la normalisation des données** :
- **Apache Commons Text** : Pour la similarité textuelle
- **OpenCV Java** : Pour la comparaison visuelle des images produits
- **Stanford CoreNLP** : Pour comprendre les descriptions produits

**Pour la persistance et le cache** :
- **Elasticsearch** : Pour l'indexation et la recherche rapide
- **PostgreSQL** : Pour les données structurées (historique de prix, etc.)
- **Redis** : Pour le cache ultra-rapide
- **Apache Kafka** : Pour le streaming des mises à jour

### Architecture en couches : la tour de contrôle de la comparaison

```
┌─────────────────────────────────────────────────────────────────┐
│                      API Gateway (Port 8080)                    │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Shop Controller (Port 8087)                   │
│  - Validation des requêtes de recherche comparative            │
│  - Gestion des sessions utilisateur                            │
│  - Formatage des réponses comparatives JSON                    │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Orchestration Layer                          │
│  - Décide quelles sources interroger                           │
│  - Gère le fallback si une source échoue                       │
│  - Agrège les résultats de multiples sources                   │
│  - Normalise les données pour comparaison cohérente            │
└──────────────┬─────────────────┬────────────────┬──────────────┘
               │                 │                │
               ▼                 ▼                ▼
    ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
    │   API Fetcher   │ │  Web Scraper    │ │   Cache Layer   │
    │  (APIs publiques│ │  (HTML Parser)  │ │    (Redis)      │
    │   des marchands)│ │                 │ │                 │
    └─────────────────┘ └─────────────────┘ └─────────────────┘
               │                 │                │
               ▼                 ▼                ▼
    ┌─────────────────────────────────────────────────────────────┐
    │                    Sources Externes (Web)                   │
    │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐  │
    │  │  Jumia   │ │   Kaymu  │ │ Gloopro  │ │  Amazon      │  │
    │  │   Site   │ │   Site   │ │   Site   │ │    Site      │  │
    │  └──────────┘ └──────────┘ └──────────┘ └──────────────┘  │
    │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐  │
    │  │ Konga    │ │  eBay    │ │Cdiscount │ │  AliExpress  │  │
    │  │  Site    │ │  Site    │ │  Site    │ │    Site      │  │
    │  └──────────┘ └──────────┘ └──────────┘ └──────────────┘  │
    └─────────────────────────────────────────────────────────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   Redirection vers  │
                    │   Site marchand     │
                    │   (Externe à nous)  │
                    └─────────────────────┘
```

**Important** : Nous collectons, comparons et présentons. L'achat se fait **directement sur le site du marchand** après redirection.

### Le flux complet : de la recherche à la redirection

Quand un utilisateur recherche "iPhone 14 128Go Noir" depuis le Cameroun, voici ce qui se passe :

1. **Phase 0 : Cache Check (0-5ms)**  
   Vérification si cette recherche exacte a été faite récemment. Si oui et données < 2 minutes, retour immédiat. **Hit rate: ~40%**.

2. **Phase 1 : Query Understanding (5-20ms)**  
   Notre système comprend que "iPhone 14 128Go Noir" signifie :
    - Marque: Apple
    - Modèle: iPhone 14
    - Capacité: 128GB
    - Couleur: Noir
    - Catégorie: Smartphone haut de gamme

3. **Phase 2 : Source Selection (20-50ms)**  
   Nous décidons quels sites interroger en priorité basé sur :
    - Localisation de l'utilisateur (Cameroun → Jumia prioritaire)
    - Disponibilité historique du produit sur chaque source
    - Temps de réponse habituel de la source
    - Fiabilité de la source

4. **Phase 3 : Parallel Data Collection (50-150ms)**  
   Nous lançons **en parallèle** des collectes vers 8-12 sources différentes :
    - Sites web publics (scraping respectueux)
    - APIs publiques (quand disponibles)
    - Timeout adapté par source

5. **Phase 4 : Data Normalization (150-180ms)**  
   Chaque source retourne des données dans son format. Nous normalisons :
   ```
   Jumia (HTML): "Apple iPhone 14 (128 GB) - Noir" - 650,000 FCFA
   → Normalisé: {
       product: "Apple iPhone 14 128Go Noir",
       source: "Jumia Cameroun",
       price_fcfa: 650000,
       url: "https://jumia.cm/...",
       availability: true,
       shipping_info: "Gratuit"
     }
   
   Amazon (API): "Apple iPhone 14 - 128 GB - Black" - 919€
   → Normalisé: {
       product: "Apple iPhone 14 128Go Noir",
       source: "Amazon France",
       price_fcfa: 602000, // Converti
       price_original: "919€",
       url: "https://amazon.fr/...",
       availability: true,
       shipping_info: "10-15 jours, +45,000 FCFA"
     }
   ```

6. **Phase 5 : Deduplication & Matching (180-190ms)**  
   Nous identifions que plusieurs offres concernent le même produit physique et les groupons pour comparaison.

7. **Phase 6 : Enrichment (190-195ms)**  
   Nous enrichissons avec des données historiques :
    - Évolution du prix (graphiques)
    - Tendances détectées
    - Alertes pertinentes

8. **Phase 7 : Comparison & Ranking (195-198ms)**  
   Nous calculons un score pour chaque offre basé sur :
    - Prix total (produit + livraison + frais)
    - Délai de livraison
    - Fiabilité du marchand
    - Préférences utilisateur (local vs international)

9. **Phase 8 : Response Formatting (198-200ms)**  
   Nous formatons une réponse comparative claire avec :
    - Le produit identifié
    - Toutes les offres triées
    - Prix en FCFA pour utilisateurs camerounais
    - **Liens de redirection vers chaque marchand**
    - Graphiques et insights

10. **Phase 9 : User Redirection (action utilisateur)**  
    L'utilisateur choisit une offre et **clique pour être redirigé** vers le site marchand. **À partir de là, nous ne sommes plus impliqués** - l'achat, le paiement, la livraison se font directement entre l'utilisateur et le marchand.

## Focus sur l'intégration des marchés camerounais et africains

### Sources prioritaires indexées

**Jumia (priorité #1)** :
- **Type d'intégration** : Scraping HTML respectueux (robots.txt compliant)
- **Fréquence** : Toutes les 15 minutes pour produits populaires
- **Données collectées** : Prix, disponibilité, délais livraison, notes/avis
- **Redirection** : Lien direct vers la page produit Jumia

**Kaymu** :
- **Type d'intégration** : Scraping HTML
- **Fréquence** : Toutes les 30 minutes
- **Spécificité** : Marché C2C, petits vendeurs
- **Redirection** : Lien vers offre Kaymu

**Gloopro** :
- **Type d'intégration** : API partenaire (quand disponible) + scraping
- **Fréquence** : Toutes les 20 minutes
- **Focus** : Électronique et high-tech
- **Redirection** : Lien direct vers Gloopro

**Amazon, eBay, Cdiscount** :
- **Type d'intégration** : APIs publiques + scraping
- **Fréquence** : Toutes les 30-60 minutes
- **Position** : Sources secondaires (après marchés locaux)
- **Redirection** : Liens directs vers ces plateformes

**Important** : Nous ne stockons que les données nécessaires à la comparaison. Les transactions se font 100% sur les sites marchands.

### Gestion des prix en Franc CFA

**Conversion automatique** :
- Tous les prix internationaux sont convertis en FCFA pour utilisateurs camerounais
- Taux de change actualisé toutes les heures
- Affichage du prix original + converti

**Calcul du coût total** :
```json
{
  "product": "iPhone 14 128Go Noir",
  "comparison": [
    {
      "merchant": "Jumia Cameroun",
      "merchant_url": "https://jumia.cm/apple-iphone-14...",
      "price_displayed": "650,000 FCFA",
      "shipping": "Gratuit",
      "estimated_delivery": "2-4 jours",
      "total_cost_fcfa": 650000,
      "rank": 1,
      "notes": "Disponible localement, livraison rapide"
    },
    {
      "merchant": "Amazon FR",
      "merchant_url": "https://amazon.fr/Apple-iPhone-14...",
      "price_displayed": "919€",
      "price_fcfa": 602000,
      "shipping": "~45,000 FCFA",
      "customs_estimate": "~80,000 FCFA",
      "estimated_delivery": "10-15 jours",
      "total_cost_fcfa": 727000,
      "rank": 3,
      "notes": "Import international, délais plus longs"
    },
    {
      "merchant": "Boutique Locale Yaoundé",
      "merchant_url": "https://techshop-cm.com/iphone14",
      "price_displayed": "620,000 FCFA",
      "shipping": "5,000 FCFA (moto)",
      "estimated_delivery": "Même jour",
      "total_cost_fcfa": 625000,
      "rank": 2,
      "notes": "Meilleur rapport local, garantie 6 mois"
    }
  ],
  "recommendation": "Boutique locale offre le meilleur rapport qualité/prix/délai"
}
```

**Que fait l'utilisateur ensuite ?**
- Il choisit l'offre qui lui convient
- Il clique sur le lien du marchand (merchant_url)
- **Il est redirigé vers le site externe**
- **Toute transaction se passe là-bas, hors de notre système**

## Le cœur intelligent : l'algorithme de correspondance produit

### Le problème du "même produit, descriptions différentes"

La partie la plus complexe est de comprendre que plusieurs offres concernent le même produit physique. Comment savons-nous que "iPhone 14 128GB Black" et "Apple iPhone 14 - 128 Go - Noir" sont identiques ?

Nous utilisons un **système de scoring multi-critères** :

1. **Similarité textuelle (40% du score)**  
   Comparaison des titres avec plusieurs algorithmes :
    - **Levenshtein Distance** : Nombre de caractères à changer
    - **Jaro-Winkler** : Efficace pour noms courts
    - **Cosine Similarity** : Sur embeddings de mots
    - **TF-IDF** : Les mots rares ont plus de poids

2. **Identifiants techniques (30% du score)**  
   Les identifiants uniques sont décisifs :
    - **EAN** (European Article Number) : 13 chiffres
    - **UPC** (Universal Product Code) : 12 chiffres
    - **MPN** (Manufacturer Part Number)
    - **ISBN** : Pour les livres

3. **Caractéristiques techniques (20% du score)**  
   Comparaison des spécifications :
    - Capacité : 128GB = 128 Go = 128 gigaoctets
    - Couleur : Noir = Black = Midnight
    - Dimensions, poids, etc.

4. **Similarité visuelle (10% du score)**  
   Comparaison des images produits quand disponibles

### L'algorithme de décision

```
Si EAN identique → MATCH CONFIRMÉ (100%)
Sinon si UPC identique → MATCH CONFIRMÉ (100%)
Sinon si similarité textuelle > 90% ET specs identiques → MATCH PROBABLE (95%)
Sinon si similarité textuelle > 80% ET similarité visuelle > 70% → MATCH POSSIBLE (85%)
Sinon → PRODUITS DIFFÉRENTS (< 50%)
```

Seuil de confiance : **85%**. Au-dessus, nous groupons les offres pour comparaison.

### Exemple réel de matching

```
OFFRE 1 (Jumia Cameroun):
Titre: "Apple iPhone 14 (128 GB) - Midnight"
EAN: 0194252767895
Prix: 650,000 FCFA
URL: https://jumia.cm/apple-iphone-14-midnight-128gb

OFFRE 2 (Amazon France):
Titre: "iPhone 14 128Go couleur noire"
EAN: 0194252767895  (identique !)
Prix: 919€ (≈ 602,000 FCFA)
URL: https://amazon.fr/Apple-iPhone-14-128GB-Noir

OFFRE 3 (Boutique locale):
Titre: "Smartphone Apple iPhone14 128Go"
EAN: Non disponible
Similarité textuelle: 87%
Prix: 620,000 FCFA
URL: https://techshop-cm.com/iphone14-black

→ Notre système : MATCH confirmé (EAN identique pour 1 & 2)
→ MATCH probable pour offre 3 (similarité 87%)
→ Présentation groupée pour comparaison
→ Utilisateur choisit et est redirigé vers le site de son choix
```

## Le système d'historique de prix

### Pas juste un prix, mais une tendance

Nous ne nous contentons pas du prix actuel. Nous traçons **l'évolution des prix** pour aider l'utilisateur :

**1. Prix actuel vs historique** :
```
📈 Historique 30 derniers jours (Jumia) :
- 01 Nov : 700,000 FCFA
- 15 Nov : 680,000 FCFA
- 25 Nov : 650,000 FCFA (Black Friday)
- 30 Nov : 650,000 FCFA ← Prix actuel

Tendance : Baisse de 7% ce mois
```

**2. Comparaison multi-sources** :
```
📊 Comparaison actuelle :
- Jumia : 650,000 FCFA (stable depuis 5 jours)
- Amazon : 602,000 FCFA + frais = 727,000 FCFA total
- Local : 620,000 FCFA (nouveau vendeur, pas d'historique)

→ Meilleur prix brut : Amazon (602k)
→ Meilleur prix total : Local (625k tout compris)
```

**3. Alertes intelligentes** :
```
🔔 Alertes pour cet utilisateur :
- Prix actuel Jumia = meilleur des 30 derniers jours
- Boutique locale : nouveau, prix compétitif
- Amazon : attention aux frais de douane possibles
```

### Comment collectons-nous ces prix ?

**Collecte respectueuse du web** :
- Respect strict des `robots.txt` de chaque site
- Rate limiting intelligent (ne jamais surcharger un site)
- User-Agent transparent : "YowYobBot/1.0"
- Collecte uniquement des données publiques

**Fréquence de collecte** :
- **Sites locaux majeurs** (Jumia, Kaymu) : toutes les 15-30 min
- **Sites internationaux** : toutes les 30-60 min
- **Petits marchands** : toutes les 2-4 heures
- **Produits populaires** : fréquence augmentée

**Stockage** :
- Prix actuels : Redis (cache rapide)
- Historique : PostgreSQL (données structurées)
- Métadonnées : Elasticsearch (recherche rapide)

**Important** : Nous ne stockons QUE ce qui est nécessaire pour la comparaison. Pas de données personnelles d'utilisateurs marchands, pas de scraping abusif.

## La recherche comparative avancée

### Compréhension de l'intention utilisateur

Notre moteur comprend **le contexte et l'intention** :

**Recherche "tissu pagne femme"** :
- Compréhension : produit local, textile africain
- Sources prioritaires : marchands locaux, artisans
- Filtres suggérés : type tissu (wax, bazin), prix local
- Résultat : comparaison d'offres locales, liens vers marchands

**Recherche "groupe électrogène silencieux"** :
- Compréhension : besoin d'urgence (coupures fréquentes)
- Critères techniques : puissance, niveau sonore
- Sources : magasins électroniques CM, importateurs
- Résultat : comparaison technique + prix, liens directs

### Filtres intelligents de comparaison

**Filtre prix** :
- Pas juste min-max
- Mais : "< 50k FCFA", "50k-200k", "200k-500k", "> 500k"
- Option : "Inclure frais de port dans le total"

**Filtre localisation** :
- "Disponible localement (Cameroun)"
- "Livraison internationale acceptée"
- "Vendeur basé à [ville]"

**Filtre délai** :
- "Livraison express (< 48h)"
- "Livraison standard (3-7 jours)"
- "Délai accepté (> 7 jours)"

**Tous les filtres affinent la comparaison**, l'achat reste externe.

## Sécurité, éthique et légalité

### Collecte de données 100% légale et éthique

**1. Respect absolu des lois** :
- Conformité Code de la consommation camerounais
- Respect droits propriété intellectuelle
- Collecte limitée aux données publiques

**2. Respect robots.txt** :
- Lecture et respect scrupuleux de chaque robots.txt
- Si une section est interdite, nous ne la collectons pas
- Respect des délais imposés (Crawl-delay)

**3. Rate limiting intelligent** :
- Jamais plus de 1 requête/10 secondes par site (par défaut)
- Réduction pour petits sites locaux
- Pause la nuit pour sites à faible trafic

**4. Identification transparente** :
```
User-Agent: YowYobBot/1.0 (+https://yowyob.com/bot)
Contact: bot@yowyob.com
Policy: https://yowyob.com/robots-policy
```

**5. Données publiques uniquement** :
- Jamais de connexion à des comptes
- Jamais de scraping derrière login
- Jamais de données personnelles collectées

**6. Droit de refus** :
- Tout site peut demander son exclusion
- Délai de réponse : 48h
- Process : email à bot@yowyob.com

### Protection des données utilisateurs

**Ce que nous collectons sur les utilisateurs** :
- Termes de recherche (anonymisés après 30 jours)
- Préférences de filtrage (pour améliorer le service)
- Localisation approximative (pays/ville, pour prioriser sources locales)

**Ce que nous ne collectons JAMAIS** :
- Données de paiement (les achats sont externes)
- Historique d'achat (se passe sur sites marchands)
- Données personnelles identifiables
- Tracking inter-sites

**Transparence** :
- Page "Comment nous collectons les données" publique
- Liste des sources indexées visible
- Possibilité d'opt-out du tracking de préférences

## Performance et scalabilité

### Architecture pour le scale

**Capacité actuelle** :
- 500 requêtes/seconde par instance
- 3 instances = 1,500 req/s
- Latence p95 : < 400ms
- Cache hit rate : 40%

**Infrastructure** :
```
Kubernetes Deployment:
- 3 pods Shop Service
- 6 nœuds Redis Cluster
- 5 nœuds Elasticsearch Cluster
- Load balancing automatique
```

**Optimisations clés** :
1. **Cache multi-niveaux** :
    - L1 : Mémoire locale (10ms)
    - L2 : Redis (5ms)
    - L3 : Elasticsearch (50ms)

2. **Collecte parallèle** :
    - 8-12 sources interrogées simultanément
    - Timeout adaptatif par source
    - Circuit breakers pour sources down

3. **Compression** :
    - Réponses JSON compressées (Brotli)
    - Images optimisées (WebP)
    - Réduction 70% de la bande passante

### Monitoring et alertes

**Dashboards Grafana** :
- Requêtes utilisateurs (volume, latence)
- Collecte de données (sources up/down, latence)
- Cache performance
- Comparaisons effectuées

**Alertes critiques** :
```yaml
- alert: SourceMajorDown
  expr: up{source=~"jumia|amazon"} == 0
  for: 5m
  severity: critical
  
- alert: HighLatency
  expr: p95_latency > 2000ms
  for: 5m
  severity: warning

- alert: LowCacheHitRate
  expr: cache_hit_rate < 0.3
  for: 10m
  severity: warning
```

## Tests et qualité

### Tests unitaires

```java
@Test
void shouldMatchIdenticalProducts() {
    // Given
    ProductOffer offer1 = new ProductOffer(
        "Apple iPhone 14 128GB Black",
        "0194252767895", // EAN
        650000,
        "https://jumia.cm/..."
    );
    
    ProductOffer offer2 = new ProductOffer(
        "iPhone 14 128 Go Noir",
        "0194252767895", // Même EAN
        602000,
        "https://amazon.fr/..."
    );
    
    // When
    boolean isMatch = productMatcher.areIdentical(offer1, offer2);
    
    // Then
    assertThat(isMatch).isTrue();
    assertThat(productMatcher.getConfidence()).isGreaterThan(0.95);
}

@Test
void shouldConvertEuroToFcfa() {
    // Given
    double priceEuro = 919.0;
    double exchangeRate = 655.0;
    
    // When
    double priceFcfa = currencyConverter.convert(priceEuro, "EUR", "FCFA");
    
    // Then
    assertThat(priceFcfa).isCloseTo(602045, Offset.offset(100.0));
}

@Test
void shouldRespectRobotsTxt() {
    // Given
    String siteUrl = "https://example-merchant.com";
    RobotsTxtParser parser = new RobotsTxtParser(siteUrl);
    
    // When
    boolean canCrawl = parser.isAllowed("/products/iphone");
    int crawlDelay = parser.getCrawlDelay();
    
    // Then
    assertThat(canCrawl).isTrue();
    assertThat(crawlDelay).isGreaterThanOrEqualTo(10); // Respectons le délai
}
```

### Tests d'intégration

```java
@Test
@IntegrationTest
void shouldCompareProductsFromMultipleSources() {
    // Given
    String query = "Samsung Galaxy A54";
    User user = new User("CM", "Yaoundé");
    
    // When
    ComparisonResponse response = shopService.compare(query, user);
    
    // Then
    assertThat(response.getProduct()).isNotNull();
    assertThat(response.getOffers()).hasSizeGreaterThan(1);
    assertThat(response.getOffers())
        .allMatch(offer -> offer.getMerchantUrl().startsWith("http"))
        .allMatch(offer -> offer.getPriceFcfa() > 0);
    
    // Vérifier que nous n'avons pas d'URL vers notre propre système
    assertThat(response.getOffers())
        .noneMatch(offer -> offer.getMerchantUrl().contains("yowyob.com"));
}

@Test
void shouldHandleSourceTimeout() {
    // Given: Une source est lente (timeout)
    mockSource("jumia").withDelay(10000); // 10s délai
    mockSource("amazon").withDelay(200); // 200ms normal
    
    // When
    ComparisonResponse response = shopService.compare("iPhone", user);
    
    // Then: Résultat retourné malgré timeout d'une source
    assertThat(response.getOffers()).isNotEmpty();
    assertThat(response.getPartialResults()).isTrue();
    assertThat(response.getFailedSources()).contains("jumia");
}
```

## Déploiement et opérations

### Configuration Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: shop-service
  namespace: yowyob
spec:
  replicas: 3
  selector:
    matchLabels:
      app: shop-service
  template:
    metadata:
      labels:
        app: shop-service
    spec:
      containers:
      - name: shop-service
        image: yowyob/shop-service:1.2.0
        ports:
        - containerPort: 8087
        env:
        - name: ELASTICSEARCH_URL
          value: "http://elasticsearch:9200"
        - name: REDIS_URL
          value: "redis://redis-cluster:6379"
        - name: DEFAULT_REGION
          value: "CM"
        - name: DEFAULT_CURRENCY
          value: "FCFA"
        resources:
          requests:
            memory: "1Gi"
            cpu: "1000m"
          limits:
            memory: "2Gi"
            cpu: "2000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8087
          initialDelaySeconds: 120
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8087
          initialDelaySeconds: 30
          periodSeconds: 5
```

### Jobs planifiés

```yaml
# Mise à jour prix toutes les 15 minutes (produits populaires)
apiVersion: batch/v1
kind: CronJob
metadata:
  name: price-updater-popular
spec:
  schedule: "*/15 * * * *"
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: price-updater
            image: yowyob/price-updater:1.0.0
            args: ["--category", "popular"]
          restartPolicy: OnFailure

# Nettoyage cache (3h du matin)
apiVersion: batch/v1
kind: CronJob
metadata:
  name: cache-cleaner
spec:
  schedule: "0 3 * * *"
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: cache-cleaner
            image: yowyob/cache-cleaner:1.0.0
```

## Dépannage

### Problème : Prix non mis à jour

```
🔍 Symptômes :
- Prix affichés vieux de +2h
- Différence avec le site marchand

🎯 Causes possibles :
1. Source marchand inaccessible
2. Changement structure HTML du site
3. Rate limiting atteint
4. Cache obsolète

🔧 Solutions :
1. Vérifier logs collecte : kubectl logs shop-service-xxx | grep "scraper"
2. Tester manuellement : ./scripts/test-scraper.sh [source]
3. Forcer rafraîchissement : POST /api/admin/refresh/[source]
4. Invalidate cache : POST /api/admin/cache/clear/[product-id]
```

### Problème : Produits en double

```
🔍 Symptômes :
- Même produit apparaît plusieurs fois séparément
- Pas de groupement pour comparaison

🎯 Causes :
1. Algorithme matching trop strict
2. Données insuffisantes (pas d'EAN)
3. Descriptions trop différentes

🔧 Solutions :
1. Ajuster seuils similarité : config.matching.threshold
2. Améliorer extraction EAN du HTML
3. Enrichir avec données externes (APIs constructeurs)
4. Mode manuel temporaire : groupement assisté
```

## Roadmap et évolution

### Fonctionnalités prévues 2024

**Q1 2024** :
- [x] Intégration Jumia, Kaymu, Gloopro, Amazon
- [x] Conversion automatique FCFA
- [ ] Recherche vocale (français)
- [ ] Alertes prix personnalisées

**Q2 2024** :
- [ ] Extension sources (10+ nouveaux marchands africains)
- [ ] Comparaison visuelle améliorée (IA)
- [ ] API publique pour développeurs
- [ ] Support multilingue (anglais, pidgin)

**Q3 2024** :
- [ ] Prédiction de prix (ML)
- [ ] Recommandations personnalisées
- [ ] Intégration assistants vocaux
- [ ] Expansion Afrique de l'Ouest

**Q4 2024** :
- [ ] 50+ sources intégrées
- [ ] Couverture 5 pays africains
- [ ] Application mobile native
- [ ] Partenariats officiels marchands

## FAQ - Questions fréquentes

**Q : Vendez-vous des produits ?**  
R : **Non, absolument pas.** Nous sommes un comparateur. Nous collectons, comparons et présentons les offres. L'achat se fait directement sur le site du marchand que vous choisissez.

**Q : Prenez-vous une commission sur les achats ?**  
R : Pour l'instant, non. Notre service est 100% gratuit pour les utilisateurs. À l'avenir, nous pourrions avoir des partenariats d'affiliation avec certains marchands, mais cela n'affectera jamais l'objectivité de nos comparaisons.

**Q : Comment gagnez-vous de l'argent ?**  
R : Actuellement en phase de développement. Modèles envisagés : publicité contextuelle, partenariats marchands (sans biais), services premium pour professionnels.

**Q : Mes données d'achat sont-elles collectées ?**  
R : Non ! Dès que vous cliquez pour aller sur le site marchand, vous quittez notre système. Nous ne voyons pas vos achats, paiements, ou données personnelles liées aux transactions.

**Q : Pourquoi certains sites ne sont-ils pas indexés ?**  
R : Soit ils interdisent le scraping (robots.txt), soit nous n'avons pas encore intégré cette source. Vous pouvez suggérer des sources : suggestions@yowyob.com

**Q : Les prix sont-ils garantis ?**  
R : Non. Nous affichons les prix collectés à un instant T. Les prix peuvent changer entre notre collecte et votre visite sur le site marchand. Vérifiez toujours sur le site final.

**Q : Puis-je faire confiance aux comparaisons ?**  
R : Nous faisons de notre mieux pour être objectifs et précis. Mais nous recommandons toujours de vérifier sur le site marchand avant achat, et de lire les avis/conditions.

**Q : Un site demande mon retrait de son index, que faites-vous ?**  
R : Nous retirons le site sous 48h, sans question. Contact : bot@yowyob.com

## Contribution

### Comment contribuer ?

1. **Fork** le repository
2. **Créer une branche** : `git checkout -b feature/add-new-merchant-source`
3. **Développer** avec tests
4. **Pull Request** avec description claire

### Standards de code

```java
// Bon
class JumiaProductScraper implements ProductScraper {
    @Override
    public List<ProductOffer> scrape(String query) {
        // Respecter robots.txt
        if (!robotsTxtChecker.isAllowed(JUMIA_URL, "/search")) {
            throw new ScrapingNotAllowedException("Jumia disallows /search");
        }

        // Rate limiting
        rateLimiter.acquire();

        // Scraping respectueux
        Document doc = Jsoup.connect(buildSearchUrl(query))
                .userAgent(USER_AGENT)
                .timeout(5000)
                .get();

        // Extraction et retour
        return extractProducts(doc);
    }
}
```

### Lignes directrices éthiques

**Toute contribution doit** :
- Respecter robots.txt
- Implémenter rate limiting approprié
- Ne collecter que données publiques
- Avoir un User-Agent transparent
- Inclure des tests

**Est refusé** :
- Scraping abusif (>1 req/10s par site)
- Contournement de robots.txt
- Collecte de données personnelles
- Scraping derrière authentification

## Support

**Documentation** : https://docs.yowyob.com/shop-service

**Email** : support@yowyob.com

**GitHub Issues** : https://github.com/yowyob/shop-service/issues

**Communauté** : Discord YowYob Developers

## Conclusion

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