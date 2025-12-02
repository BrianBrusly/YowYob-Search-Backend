# README - Shop Service YowYob Search

## Vue d'ensemble et présentation du service

Bienvenue dans la documentation du **Shop Service**, le comparateur intelligent de la plateforme YowYob Search !

Imaginez que vous cherchez à acheter un nouveau téléphone. Vous ouvrez votre navigateur, vous tapez "iPhone 14" dans Google, et là... c'est le chaos. Des dizaines d'onglets ouverts : Amazon, Jumia, eBay, Cdiscount, des petites boutiques dont vous n'avez jamais entendu parler. Vous passez littéralement des heures à noter les prix sur un bout de papier, à calculer les frais de livraison, à essayer de comprendre si ce vendeur sur Kaymu est fiable ou pas. Et au final, vous n'êtes même pas sûr d'avoir trouvé la meilleure offre.

C'est exactement ce problème que notre Shop Service résout. Mais attention, on ne vend rien nous-mêmes ! On n'est pas une boutique en ligne, on n'est pas un intermédiaire commercial. On est plutôt comme ce pote ultra-organisé qui, quand vous lui dites "je cherche tel produit", passe son après-midi à parcourir tous les sites web du monde, note tout dans un tableau Excel hyper détaillé, et vous dit : "Voilà, regarde, chez Jumia c'est à ce prix-là, chez Amazon c'est moins cher mais attention aux frais de douane, et tiens, y'a une petite boutique à Yaoundé qui a le meilleur rapport qualité-prix". Et après ? Ben après, vous choisissez où vous voulez acheter, vous cliquez sur le lien qu'on vous donne, et vous vous retrouvez directement sur le site du vendeur. À partir de là, on n'a plus rien à voir avec votre achat. C'est entre vous et le marchand.

**Focus spécifique Cameroun et Afrique** : Contrairement à beaucoup de comparateurs qui se concentrent uniquement sur les grosses plateformes américaines ou européennes, nous, on connaît vraiment le marché africain. On sait que Jumia c'est le roi ici, que Kaymu c'est là où on trouve les petits vendeurs avec les prix imbattables, que Gloopro c'est fiable pour l'électronique. On sait aussi que quand vous voyez un prix en euros ou en dollars, vous voulez savoir combien ça fait en **Franc CFA (FCFA)** parce que c'est avec ça que vous allez payer votre Orange Money ou MTN Mobile Money. On sait que les frais de douane peuvent vous surprendre, que la livraison depuis la France prend deux semaines, et que parfois, la petite boutique du coin de la rue a un meilleur deal que Amazon même si elle n'a pas de site web fancy.

### Pourquoi avons-nous besoin d'un Shop Service si sophistiqué ?

Vous vous demandez peut-être : "Mais pourquoi c'est si compliqué ? C'est juste comparer des prix, non ?" Eh bien, laissez-moi vous raconter ce qui se passe vraiment dans les coulisses du e-commerce moderne, et vous allez comprendre pourquoi c'est en fait un vrai casse-tête.

**Le problème de la tour de Babel du e-commerce** : Chaque vendeur parle sa propre langue. Sérieusement. Prenez l'iPhone 14 en 128 gigaoctets, couleur noire. Sur Amazon France, ils l'appellent "Apple iPhone 14 - 128 GB - Black". Sur Jumia Cameroun, c'est "iPhone 14 (128 Go) - Noir". Sur le site d'une boutique locale, vous verrez "Smartphone Apple iPhone14 128GB couleur Midnight". Et le pire ? Sur certains sites, ils écrivent "128Go", sur d'autres "128 GB", et d'autres encore "128 gigaoctets". C'est le même téléphone, exactement le même, mais essayez de les comparer manuellement... Bon courage. C'est comme si chaque marchand utilisait son propre dialecte pour décrire ses produits. Et nous, notre boulot, c'est d'être les traducteurs universels.

**Le problème du prix qui joue à cache-cache** : Vous savez ce qui est vraiment frustrant ? Vous voyez un prix affiché, vous vous dites "super, c'est dans mon budget", et ensuite... boom ! Les frais de livraison apparaissent. Puis les taxes. Puis les frais de traitement. Et si c'est un achat international, surprise, les frais de douane ! Au final, ce iPhone à 600,000 FCFA devient mystérieusement un iPhone à 750,000 FCFA. Et ça, c'est quand tout va bien. Parce que certains vendeurs changent leurs prix plusieurs fois par jour. Vraiment. Vous regardez le matin, c'est 650,000 FCFA. Vous revenez le soir, c'est 680,000 FCFA. Le lendemain matin, hop, promotion flash à 620,000 FCFA mais seulement pendant 2 heures. Comment vous êtes censé suivre tout ça ? Notre système, lui, garde un œil sur tous ces prix en permanence, comme un vigile qui ne dort jamais.

**Le problème de la réputation invisible** : Bon, admettons que vous avez trouvé le meilleur prix. Mais est-ce que le vendeur est fiable ? Est-ce qu'il va vraiment vous livrer le produit ? Est-ce que c'est pas une contrefaçon ? Combien de temps ça prend pour livrer ? Qu'est-ce qui se passe si le produit est défectueux ? Ces questions, elles sont cruciales, mais les réponses sont éparpillées partout : des avis clients cachés au fond du site, des discussions sur des forums, des commentaires sur Facebook. Nous, on agrège tout ça. On vous dit : "Attention, ce vendeur a 2 étoiles sur 5, les gens se plaignent qu'il livre jamais à temps" ou au contraire "Ce vendeur est top, 4.8 étoiles, tout le monde dit qu'il est sérieux".

**Le problème de la géographie complexe** : Acheter en ligne au Cameroun, ce n'est pas comme acheter en France ou aux États-Unis. Ici, vous avez plusieurs options qui viennent avec leurs propres complications. Vous pouvez acheter local chez Jumia – c'est rapide, c'est fiable, mais parfois c'est plus cher. Vous pouvez commander sur Amazon – c'est peut-être moins cher, mais ça prend deux semaines, il faut payer la livraison internationale, et personne ne vous dit clairement combien vont coûter les frais de douane. Vous pouvez aussi aller chez un petit vendeur local qui importe lui-même – les prix sont souvent imbattables, mais est-ce qu'il a une vraie garantie ? Est-ce que c'est vraiment un produit neuf ? Notre service vous montre toutes ces options côte à côte, avec les vrais coûts, les vrais délais, et vous laisse décider ce qui vous convient le mieux.

**Le problème de la connexion internet intermittente** : Parlons franchement. Au Cameroun, la connexion internet, c'est pas toujours stable. Des fois ça marche super bien, des fois vous attendez trois minutes pour qu'une page se charge. Imaginez que vous êtes en train de comparer des prix, vous avez ouvert 10 onglets, et paf, coupure de courant, coupure internet. Vous perdez tout. Vous devez tout recommencer. Nous, on a construit notre système pour qu'il soit ultra-rapide, qu'il utilise le minimum de données possibles, et qu'il garde en mémoire ce que vous cherchiez même si votre connexion coupe.

Notre Shop Service résout tous ces défis grâce à une architecture technique assez sophistiquée, mais je vais vous l'expliquer simplement. On combine plusieurs technologies :

**L'intelligence artificielle** : C'est elle qui comprend que quand un site écrit "128GB", un autre "128 Go", et un troisième "128 gigaoctets", c'est exactement la même chose. Elle comprend aussi que "Noir", "Black", et "Midnight" pour un iPhone, c'est la même couleur. Sans ça, impossible de comparer les produits correctement.

**Le traitement distribué** : C'est un peu technique, mais en gros, ça veut dire qu'au lieu de visiter les sites web un par un (ce qui prendrait des heures), notre système les visite tous en même temps, en parallèle. Comme si vous aviez 50 assistants qui partaient chacun visiter un site différent et revenaient tous en même temps avec les infos. Résultat : en moins de 200 millisecondes (même pas une seconde !), on a scanné des dizaines de sites.

**Le système de cache intelligent** : Imaginez un carnet de notes super organisé. Quand quelqu'un cherche "iPhone 14" et qu'on collecte toutes les infos, on les note dans ce carnet. Si cinq minutes plus tard, quelqu'un d'autre cherche "iPhone 14", au lieu de tout recommencer, on regarde juste dans notre carnet. C'est instantané. Mais attention, on met à jour ce carnet régulièrement pour que les infos soient toujours fraîches.

**L'analyse en temps réel** : Notre système ne se contente pas de vous montrer les prix actuels. Il analyse aussi les tendances. "Tiens, ce produit baisse de prix depuis deux semaines, ça va probablement continuer" ou "Attention, c'est le Black Friday, les prix sont anormalement bas, profitez-en maintenant".

## Architecture technique détaillée

Bon, là on va rentrer un peu plus dans les détails techniques, mais promis, je vais rester compréhensible. Si vous êtes développeur, vous allez adorer. Si vous êtes juste curieux, vous allez comprendre comment tout ça fonctionne dans les coulisses.

### Technologies et frameworks utilisés

Notre Shop Service est construit avec **Spring Boot 3.2**, qui est en quelque sorte la Rolls-Royce des frameworks Java pour construire des applications web robustes. On utilise aussi **Spring WebFlux**, qui est la version "super-vitesse" de Spring, optimisée pour faire plein de choses en même temps. Pourquoi on a choisi ça ? Parce que notre service doit lancer des centaines, voire des milliers de requêtes simultanément vers différents sites web. Si on utilisait une architecture classique, ça ralentirait tout. Avec WebFlux, c'est comme si on avait une équipe d'acrobates ultra-synchronisés au lieu d'une file d'attente à la poste.

Le service écoute sur le **port 8087** de notre serveur. Mais rassurez-vous, les utilisateurs n'ont jamais besoin de savoir ça. Tout passe par l'API Gateway (sur le port 8080) qui joue le rôle de réceptionniste et qui route automatiquement les requêtes vers nous quand quelqu'un cherche des produits.

Notre stack technique, c'est un peu comme une boîte à outils de mécanicien, mais pour le web :

**Pour la collecte de données sur les sites web** :
- **JSoup** : C'est notre outil principal pour "lire" les pages web. Vous savez, quand vous regardez une page Amazon, vous voyez un beau design, des images, des boutons. Mais derrière, c'est du code HTML. JSoup, c'est comme des lunettes spéciales qui permettent à notre ordinateur de lire ce code HTML et d'en extraire les informations qui nous intéressent : le nom du produit, son prix, sa disponibilité, etc.

- **Apache Tika** : Parfois, les informations sur les produits ne sont pas sur des pages web normales. Elles sont dans des PDFs, des documents Word, des feuilles Excel. Tika, c'est notre traducteur universel qui peut ouvrir tous ces formats et en extraire le texte.

- **Selenium WebDriver** : Certains sites web modernes sont très sophistiqués. Ils utilisent beaucoup de JavaScript pour charger le contenu dynamiquement. C'est beau pour l'utilisateur, mais c'est un cauchemar pour notre système. Selenium, c'est comme un navigateur web automatisé. Il simule un vrai utilisateur qui clique, qui scrolle, qui attend que la page se charge complètement, et ça nous permet de récupérer les données même sur ces sites compliqués.

**Pour l'intégration avec les APIs des marchands** (quand ils nous en donnent accès) :
- **Spring Cloud OpenFeign** : Certains gros marchands comme Amazon proposent des APIs officielles. Une API, c'est comme une porte dérobée qui nous donne accès direct à leurs données sans avoir à "lire" leur site web. C'est beaucoup plus propre, plus rapide, et plus fiable. Feign nous facilite la vie pour utiliser ces APIs.

- **GraphQL Java** : GraphQL, c'est un peu le langage moderne pour demander des données. Au lieu de dire "donne-moi toutes les infos sur tous les produits" (et recevoir une tonne de données dont on n'a pas besoin), on peut dire précisément "donne-moi juste le nom, le prix et la disponibilité de ce produit spécifique". C'est économique en bande passante, ce qui est crucial au Cameroun.

- **Apache CXF** : Certains vieux systèmes utilisent encore SOAP et XML. C'est un peu comme les vieilles cassettes VHS à l'époque des Netflix. C'est dépassé, mais ça existe encore, et on doit pouvoir lire ça aussi.

**Pour le traitement intelligent des données** :
- **Apache Commons Text** : Cet outil nous aide à comparer des textes et à voir s'ils sont similaires. Par exemple, "iPhone 14 Noir" et "iPhone14 Black", pour un humain c'est évidemment le même produit, mais pour un ordinateur, ce sont deux chaînes de caractères complètement différentes. Commons Text nous aide à calculer un "score de similarité" entre ces textes.

- **OpenCV Java** : OpenCV, c'est le champion de la vision par ordinateur. Quand on a les photos de produits, on peut les comparer visuellement. Si deux vendeurs vendent le même iPhone mais avec des noms différents, les photos seront similaires. OpenCV peut détecter ça.

- **Stanford CoreNLP** : C'est un peu le professeur de linguistique de notre équipe. Il comprend le langage naturel. Par exemple, dans la description "Smartphone dernier cri avec appareil photo haute résolution", il comprend que c'est un téléphone récent avec une bonne caméra. Ça nous aide à catégoriser les produits correctement.

**Pour stocker et retrouver les données rapidement** :
- **Elasticsearch** : Imaginez une bibliothèque avec des millions de livres. Si les livres sont rangés n'importe comment, ça prend des heures pour trouver celui que vous voulez. Si par contre vous avez un système de classement ultra-performant avec des fiches détaillées, vous trouvez n'importe quel livre en quelques secondes. Elasticsearch, c'est ça pour nos données de produits. On peut retrouver n'importe quel produit parmi des millions en quelques millisecondes.

- **PostgreSQL** : C'est notre base de données principale. Solide, fiable, éprouvée depuis des décennies. On y stocke toutes les données structurées : l'historique des prix, les informations sur les marchands, les métadonnées des produits. C'est comme le coffre-fort de nos données importantes.

- **Redis** : Redis, c'est la mémoire à court terme ultra-rapide de notre système. Tout ce qui doit être accessible en quelques millisecondes est là. Par exemple, les résultats de recherche récents, les prix des produits populaires, etc. C'est comme avoir un bloc-notes à portée de main plutôt que d'aller chercher dans un classeur au fond du bureau.

- **Apache Kafka** : Kafka, c'est notre système de messagerie en temps réel. Imaginez une chaîne de montage dans une usine. Les informations arrivent en continu (nouveaux prix détectés, nouveaux produits trouvés) et elles doivent être traitées dans l'ordre. Kafka organise tout ça.

### Architecture en couches : comment tout s'articule

Je vais vous expliquer l'architecture de notre système avec une analogie simple. Imaginez un grand restaurant. Vous avez la salle (où les clients arrivent), la cuisine (où on prépare), le garde-manger (où on stocke), et les fournisseurs (d'où viennent les ingrédients). Notre Shop Service, c'est pareil :

```
┌─────────────────────────────────────────────────────────────────┐
│                      API Gateway (Port 8080)                    │
│                    "L'accueil du restaurant"                    │
│    C'est la première porte. Tous les clients passent par là.   │
│      L'accueil décide : "Ah, vous cherchez des produits ?      │
│              Je vous envoie au Shop Service."                   │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Shop Controller (Port 8087)                   │
│                    "Le serveur du restaurant"                   │
│   C'est lui qui prend votre commande (votre recherche de       │
│   produit), vérifie qu'elle est bien formulée, et la transmet  │
│   à la cuisine. C'est aussi lui qui vous apporte le résultat   │
│   final joliment présenté dans une belle assiette (JSON).      │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Orchestration Layer                          │
│                     "Le chef de cuisine"                        │
│   C'est le cerveau de l'opération. Il décide :                 │
│   - Quels sites web interroger (les "fournisseurs")            │
│   - Dans quel ordre                                             │
│   - Que faire si un site ne répond pas (plan B)                │
│   - Comment assembler toutes les infos reçues                  │
│   - Comment normaliser pour que tout soit comparable           │
└──────────────┬─────────────────┬────────────────┬──────────────┘
               │                 │                │
               ▼                 ▼                ▼
    ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
    │   API Fetcher   │ │  Web Scraper    │ │   Cache Layer   │
    │ "Les commis de  │ │ "Les commis de  │ │ "Le frigidaire" │
    │  cuisine avec   │ │  cuisine qui    │ │                 │
    │  téléphone"     │ │  vont faire     │ │  Tout ce qu'on  │
    │                 │ │  les courses"   │ │  a déjà préparé │
    │ Ils appellent   │ │                 │ │  récemment et   │
    │ les fournisseurs│ │ Ils vont        │ │  qui est encore │
    │ qui ont un      │ │ physiquement    │ │  frais          │
    │ numéro direct   │ │ sur les sites   │ │                 │
    │ (API)           │ │ collecter infos │ │                 │
    └─────────────────┘ └─────────────────┘ └─────────────────┘
               │                 │                │
               ▼                 ▼                ▼
    ┌─────────────────────────────────────────────────────────────┐
    │              Le monde extérieur (les fournisseurs)          │
    │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐  │
    │  │  Jumia   │ │   Kaymu  │ │ Gloopro  │ │   Amazon     │  │
    │  │   Site   │ │   Site   │ │   Site   │ │    Site      │  │
    │  │  public  │ │  public  │ │  public  │ │   public     │  │
    │  └──────────┘ └──────────┘ └──────────┘ └──────────────┘  │
    │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐  │
    │  │  Konga   │ │   eBay   │ │Cdiscount │ │ AliExpress   │  │
    │  │   Site   │ │   Site   │ │   Site   │ │    Site      │  │
    │  │  public  │ │  public  │ │  public  │ │   public     │  │
    │  └──────────┘ └──────────┘ └──────────┘ └──────────────┘  │
    │                                                              │
    │  On collecte UNIQUEMENT les données publiques.              │
    │  On ne se connecte jamais à des comptes privés.             │
    │  On respecte scrupuleusement les règles (robots.txt).       │
    └─────────────────────────────────────────────────────────────┘
                               │
                               ▼
              ┌───────────────────────────────────┐
              │                                   │
              │   L'utilisateur reçoit une belle  │
              │   comparaison et clique sur le    │
              │   lien du marchand qui l'intéresse│
              │                                   │
              │   → REDIRECTION vers site externe │
              │                                   │
              │   À partir de là, nous ne sommes  │
              │   plus du tout impliqués.         │
              │   L'achat se passe entre          │
              │   l'utilisateur et le marchand.   │
              │                                   │
              └───────────────────────────────────┘
```

**Point crucial à comprendre** : Notre rôle s'arrête à la comparaison et à la redirection. On ne touche jamais à l'argent, on ne stocke jamais les informations de paiement, on n'est pas intermédiaire dans la transaction. On est juste l'annuaire intelligent qui vous dit "regarde, voilà toutes les options que j'ai trouvées pour toi".

### Le flux complet : du clavier de l'utilisateur jusqu'au site marchand

Maintenant, je vais vous raconter exactement ce qui se passe quand quelqu'un tape "iPhone 14 128Go Noir" dans notre moteur de recherche. C'est un voyage fascinant de quelques fractions de secondes, mais tellement de choses se passent en coulisses !

**Phase 0 : Le coup d'œil dans le frigidaire (0-5ms)**

Avant même de commencer à chercher partout sur le web, on jette un coup d'œil dans notre cache (notre "frigidaire" pour reprendre l'analogie du restaurant). Est-ce que quelqu'un a déjà cherché exactement la même chose récemment ? Si oui, et si les données ont moins de 2 minutes (donc encore bien fraîches), hop, on retourne directement ce résultat. C'est instantané, et l'utilisateur ne voit même pas de différence. Environ 40% de nos recherches se terminent déjà là. C'est énorme ! Ça veut dire que presque la moitié du temps, on n'a même pas besoin d'aller déranger les sites web.

**Phase 1 : Comprendre ce que l'utilisateur veut vraiment (5-20ms)**

Là, c'est la phase d'intelligence artificielle. L'utilisateur a tapé "iPhone 14 128Go Noir". Simple, non ? Oui, pour un humain. Mais pour un ordinateur, il faut décomposer ça. Notre système analyse et comprend :

- **Marque** : Apple (même si l'utilisateur n'a pas écrit "Apple", notre système sait qu'iPhone = Apple)
- **Type de produit** : Smartphone
- **Gamme** : Haut de gamme (l'iPhone 14, c'est pas un téléphone à 50,000 FCFA)
- **Modèle précis** : iPhone 14 (pas le 13, pas le 14 Pro, le 14 normal)
- **Capacité de stockage** : 128 gigaoctets (et notre système sait que 128Go = 128GB = 128 gigaoctets)
- **Couleur** : Noir (et il sait aussi que Noir = Black = Midnight pour ce modèle)
- **Catégorie technique** : Électronique > Téléphonie > Smartphones > Apple

Pourquoi c'est important de tout décomposer comme ça ? Parce que chaque site web utilise ses propres mots. Amazon dira "Apple iPhone 14 - 128 GB - Black", Jumia dira "iPhone 14 (128 Go) - Noir", et une boutique locale dira peut-être "Smartphone iPhone14 128Go couleur noire". Mais nous, on sait que c'est exactement le même appareil.

**Phase 2 : Choisir où aller chercher (20-50ms)**

Maintenant qu'on sait ce que l'utilisateur cherche, il faut décider quels sites web on va aller visiter. On ne peut pas visiter TOUS les sites web du monde, ça prendrait des années. Donc on fait des choix stratégiques en fonction de plusieurs critères :

- **La localisation de l'utilisateur** : Si la personne cherche depuis le Cameroun, on va donner la priorité à Jumia Cameroun, Kaymu, les boutiques locales. Pourquoi ? Parce que ces sites livrent rapidement, acceptent les paiements locaux (Mobile Money), et les prix sont affichés directement en FCFA. Amazon sera quand même interrogé, mais en deuxième priorité.

- **L'historique de fiabilité** : On garde en mémoire quels sites ont habituellement ce genre de produit. Par exemple, on sait que Gloopro est excellent pour l'électronique, donc pour un iPhone, on va certainement le consulter. Par contre, un site spécialisé dans les vêtements, on ne va pas le consulter pour un téléphone.

- **Le temps de réponse habituel** : Certains sites répondent en 100 millisecondes, d'autres en 2 secondes. On prend ça en compte pour ne pas ralentir notre recherche globale.

- **Les quotas et limitations** : Certains sites nous disent "ne nous envoyez pas plus de 10 requêtes par minute". On respecte scrupuleusement ces règles. Si on a déjà atteint la limite, on skip ce site et on passe au suivant.

- **Le coût** : Certaines APIs officielles sont payantes. On les utilise quand c'est vraiment pertinent, mais on optimise.

**Phase 3 : La grande collecte parallèle (50-150ms)**

C'est là que la magie opère. On ne va pas visiter les sites un par un, ce serait beaucoup trop lent. On lance des **requêtes en parallèle** vers 8 à 12 sites différents en même temps. C'est comme si vous aviez 10 assistants et que vous leur disiez à tous en même temps "Toi, va chez Jumia. Toi, va chez Amazon. Toi, va sur Kaymu..." et ils partaient tous au galop en même temps.

Pour chaque site, on définit un **timeout** (un délai maximum d'attente) :
- Sites locaux rapides (Jumia, Kaymu) : on attend maximum 500 millisecondes
- Amazon et autres sites internationaux : on attend jusqu'à 2 secondes
- Si un site ne répond pas dans ce délai, tant pis, on continue sans lui

Pendant ces 100 millisecondes magiques, voici ce qui se passe :

- Un assistant va sur Jumia.cm, parse la page de résultats de recherche, trouve l'iPhone 14, extrait le prix (650,000 FCFA), la disponibilité ("En stock"), les options de livraison ("Gratuit > 50,000 FCFA"), et revient avec toutes ces infos.

- Un autre va sur Amazon.fr, fait exactement la même chose, trouve le prix (919€), vérifie la disponibilité, note les frais de livraison vers le Cameroun.

- Un troisième va sur une boutique locale (qu'on a identifiée comme fiable), scrape discrètement les infos de leur catalogue.

- Et ainsi de suite pour tous les autres sites.

Tout ça, en parallèle, en même temps. C'est pour ça que c'est si rapide.

**Phase 4 : La normalisation des données (150-180ms)**

Bon, maintenant nos assistants reviennent tous avec leurs infos. Mais le problème, c'est que chacun a noté les choses à sa manière. L'un a noté "650000 FCFA", l'autre "919 EUR", un troisième "620K FCFA". Les formats sont différents, les devises sont différentes, certains ont mis des espaces, d'autres des virgules.

Notre système doit tout normaliser, c'est-à-dire tout mettre au même format pour qu'on puisse comparer. Voici un exemple :

```
Ce que Jumia nous a donné (format HTML) :
<div class="product">
  <h2>Apple iPhone 14 (128 GB) - Noir</h2>
  <span class="price">650,000 FCFA</span>
  <div class="delivery">Livraison gratuite</div>
  <div class="seller">Vendeur : Jumia</div>
</div>

Ce qu'on transforme (format normalisé) :
{
  productName: "Apple iPhone 14 128Go Noir",
  source: "Jumia Cameroun",
  sourceUrl: "https://www.jumia.cm/apple-iphone-14-128gb...",
  price: {
    amount: 650000,
    currency: "FCFA",
    displayText: "650,000 FCFA"
  },
  shipping: {
    cost: 0,
    description: "Gratuit",
    estimatedDays: "2-4 jours"
  },
  availability: true,
  lastUpdated: "2024-12-02T14:30:00Z"
}
```

Et la même chose pour Amazon :

```
Ce qu'Amazon nous a donné (via API) :
{
  "title": "Apple iPhone 14 - 128 GB - Black",
  "price": {"amount": 919, "currencyCode": "EUR"},
  "availability": "In Stock",
  "shipping": {"amount": 45, "currencyCode": "EUR"}
}

Ce qu'on transforme :
{
  productName: "Apple iPhone 14 128Go Noir",
  source: "Amazon France",
  sourceUrl: "https://www.amazon.fr/dp/B0XXXXXX",
  price: {
    amount: 602000,  // Converti en FCFA (919€ × 655 = 602,000)
    currency: "FCFA",
    displayText: "919€ (≈ 602,000 FCFA)",
    originalAmount: 919,
    originalCurrency: "EUR"
  },
  shipping: {
    cost: 45000,  // 45€ × 655 = 29,475 FCFA, arrondi à 45,000 avec taxes
    description: "Livraison internationale",
    estimatedDays: "10-15 jours"
  },
  customsEstimate: 80000,  // Estimation frais de douane
  availability: true,
  lastUpdated: "2024-12-02T14:30:05Z"
}
```

Vous voyez la différence ? Maintenant, tout est au même format, et on peut facilement comparer.

**Phase 5 : Le détective des doublons (180-190ms)**

C'est là qu'intervient notre algorithme de matching super intelligent. Son boulot : identifier quand plusieurs offres concernent exactement le même produit physique. Parce que regardez, on a maintenant :

- "Apple iPhone 14 (128 GB) - Noir" de Jumia
- "Apple iPhone 14 - 128 GB - Black" d'Amazon
- "Smartphone iPhone14 128Go couleur noire" d'une boutique locale

Pour un humain, c'est évident que c'est le même téléphone. Mais pour un ordinateur, ce sont trois chaînes de caractères complètement différentes. Notre algorithme va :

1. Comparer les codes-barres (EAN, UPC) si disponibles. Si Jumia et Amazon ont le même code "0194252767895", bingo, c'est forcément le même produit.

2. Si pas de code-barres, il analyse la similarité textuelle. Il calcule que "iPhone 14 128GB Black" et "iPhone 14 128Go Noir" ont une similarité de 92%. C'est au-dessus du seuil de 85%, donc très probablement le même produit.

3. Il compare les caractéristiques techniques : capacité (128GB), marque (Apple), modèle (14), type (smartphone). Tout matche.

4. Si disponible, il compare même les photos des produits pour être sûr à 100%.

Résultat : il groupe ces trois offres ensemble.

**Phase 6 : L'enrichissement avec l'historique (190-195ms)**

Maintenant qu'on sait quelles offres on a et pour quel produit, on va enrichir ça avec des données historiques qu'on a stockées. Notre système garde en mémoire l'évolution des prix sur les 30 derniers jours, parfois plus. Donc on peut dire des choses comme :

- "Le prix actuel chez Jumia (650,000 FCFA) est le plus bas des 30 derniers jours"
- "Il y a 2 semaines, ce produit était à 700,000 FCFA chez Jumia"
- "Tendance : le prix baisse régulièrement, environ 2% par semaine"
- "Attention : le prix a fortement baissé hier, c'est peut-être une promotion temporaire"

Ces infos sont super précieuses pour l'utilisateur. Ça lui permet de savoir s'il doit acheter maintenant ou attendre.

**Phase 7 : Le classement et le calcul du coût réel (195-198ms)**

Maintenant, on va calculer le **coût total réel** de chaque offre, pas juste le prix affiché. Parce que comme je vous le disais, le prix affiché, c'est jamais le prix final. Voici ce qu'on prend en compte :

Pour l'offre Jumia :
- Prix de base : 650,000 FCFA
- Livraison : 0 FCFA (gratuit car > 50,000 FCFA)
- Taxes : déjà incluses dans le prix
- **TOTAL : 650,000 FCFA**

Pour l'offre Amazon :
- Prix de base : 919€ = 602,000 FCFA (taux de change actuel)
- Livraison internationale : ~45,000 FCFA
- Frais de douane estimés : ~80,000 FCFA (pour l'électronique au Cameroun, c'est environ 30% de la valeur)
- **TOTAL : 727,000 FCFA**

Pour la boutique locale :
- Prix de base : 620,000 FCFA
- Livraison par moto : 5,000 FCFA
- Taxes : déjà incluses
- **TOTAL : 625,000 FCFA**

Ensuite, on classe les offres. On ne classe pas juste par prix. On prend en compte :
- Le prix total (bien sûr)
- La fiabilité du vendeur (ses notes, ses avis)
- Le délai de livraison (2 jours vs 15 jours, ça compte)
- La facilité de retour en cas de problème
- Les options de paiement (Mobile Money vs carte bancaire internationale)
- La garantie offerte

Dans cet exemple, la boutique locale (625,000 FCFA total) arrive en tête parce que :
- Meilleur rapport qualité/prix
- Livraison rapide (même jour)
- Paiement facile (Mobile Money accepté)
- Boutique physique accessible en cas de problème

**Phase 8 : La mise en forme de la réponse (198-200ms)**

Maintenant, on prépare une belle réponse JSON bien organisée qu'on va envoyer à l'utilisateur. Cette réponse contient :

```json
{
  "query": "iPhone 14 128Go Noir",
  "executionTime": "198ms",
  "product": {
    "name": "Apple iPhone 14 128Go Noir",
    "category": "Smartphones",
    "brand": "Apple",
    "ean": "0194252767895"
  },
  "offers": [
    {
      "rank": 1,
      "merchant": {
        "name": "TechShop Yaoundé",
        "rating": 4.2,
        "location": "Yaoundé, Cameroun",
        "isLocal": true
      },
      "price": {
        "base": 620000,
        "shipping": 5000,
        "total": 625000,
        "currency": "FCFA"
      },
      "delivery": {
        "time": "Même jour",
        "method": "Livraison moto"
      },
      "payment": ["Cash", "MTN Mobile Money", "Orange Money"],
      "url": "https://techshop-cm.com/iphone14-noir-128go",
      "lastVerified": "Il y a 15 minutes"
    },
    {
      "rank": 2,
      "merchant": {
        "name": "Jumia Cameroun",
        "rating": 4.7,
        "location": "Cameroun",
        "isLocal": true
      },
      "price": {
        "base": 650000,
        "shipping": 0,
        "total": 650000,
        "currency": "FCFA"
      },
      "delivery": {
        "time": "2-4 jours",
        "method": "Livraison standard"
      },
      "payment": ["Cash à livraison", "Mobile Money", "Carte bancaire"],
      "url": "https://www.jumia.cm/apple-iphone-14-128gb-noir",
      "lastVerified": "Il y a 3 minutes",
      "benefits": ["Garantie 1 an", "Retours faciles"]
    },
    {
      "rank": 3,
      "merchant": {
        "name": "Amazon France",
        "rating": 4.5,
        "location": "France",
        "isLocal": false
      },
      "price": {
        "base": 602000,
        "baseOriginal": "919€",
        "shipping": 45000,
        "customsEstimate": 80000,
        "total": 727000,
        "currency": "FCFA"
      },
      "delivery": {
        "time": "10-15 jours",
        "method": "Livraison internationale"
      },
      "payment": ["Carte bancaire internationale"],
      "url": "https://www.amazon.fr/dp/B0XXXXXX",
      "lastVerified": "Il y a 8 minutes",
      "warnings": [
        "Frais de douane peuvent varier",
        "Délai de livraison long",
        "Garantie internationale (peut être difficile à utiliser localement)"
      ]
    }
  ],
  "priceHistory": {
    "currentPrice": 625000,
    "lowestPrice30Days": 625000,
    "averagePrice30Days": 670000,
    "trend": "decreasing",
    "savingsVsAverage": 45000
  },
  "recommendations": {
    "bestValue": "TechShop Yaoundé - Meilleur rapport qualité/prix/délai",
    "mostTrusted": "Jumia Cameroun - Vendeur le plus fiable",
    "analysis": "Le prix actuel est excellent. C'est le moment d'acheter car la tendance des prix est à la baisse depuis 2 semaines."
  }
}
```

**Phase 9 : L'utilisateur fait son choix et est redirigé (action utilisateur)**

Voilà, notre travail est terminé. L'utilisateur reçoit cette belle comparaison. Il voit les trois offres, il lit nos recommandations, il regarde l'historique des prix. Il décide que la boutique locale lui convient parfaitement. Il clique sur le bouton "Voir sur TechShop Yaoundé".

À ce moment-là, on le **redirige** vers le site du marchand : `https://techshop-cm.com/iphone14-noir-128go`

Et nous ? On ne fait RIEN d'autre. On ne suit pas ce qui se passe après. On ne sait pas s'il a acheté ou pas. On ne touche pas son argent. On n'est pas impliqué dans la livraison. **Notre rôle s'arrête là, à la redirection.**

L'utilisateur est maintenant sur le site du marchand. Il peut :
- Lire plus de détails sur le produit
- Ajouter au panier
- Choisir sa méthode de paiement (Mobile Money, cash, etc.)
- Passer commande
- Suivre sa livraison
- Contacter le service client du marchand si besoin

Tout ça, c'est entre lui et le marchand. Nous, on a juste été l'intermédiaire qui lui a permis de trouver facilement la meilleure offre parmi des dizaines de possibilités.

**Phase 10 : L'apprentissage continu (en arrière-plan, asynchrone)**

Même si notre travail visible est terminé, il se passe encore des choses en coulisses. Notre système apprend continuellement :

- On enregistre que quelqu'un a cherché "iPhone 14" depuis le Cameroun. Ça nous permet d'améliorer nos algorithmes pour les prochaines fois.
- On met à jour nos statistiques : "TechShop Yaoundé a eu X clics aujourd'hui, son offre était populaire".
- On ajuste nos modèles de prédiction de prix en fonction des nouvelles données collectées.
- Si on détecte des anomalies (par exemple, un prix qui change de 50% d'un coup), on enregistre ça pour investigation.

Mais attention, tout ça c'est anonymisé. On ne garde jamais de données personnelles identifiables. On ne sait pas QUI a cherché, juste QUE quelqu'un a cherché.

Voilà, c'est fini ! De votre clavier à la redirection vers le marchand, tout ça en moins de 200 millisecondes. Impressionnant, non ?

## Focus sur l'intégration des marchés camerounais et africains

Bon, maintenant je vais vous parler de quelque chose qui nous tient vraiment à cœur et qui fait notre différence : notre connaissance approfondie du marché africain, et plus particulièrement du marché camerounais.

La plupart des comparateurs de prix du monde se concentrent uniquement sur les gros poissons : Amazon, eBay, AliExpress. C'est bien, mais ça ne suffit pas pour les utilisateurs africains. Pourquoi ? Parce que ces plateformes internationales ont des inconvénients majeurs pour quelqu'un qui vit à Yaoundé ou à Douala :

**Le problème des délais de livraison** : Commander sur Amazon.fr depuis le Cameroun, c'est bien beau, mais vous allez attendre 10 à 15 jours, parfois 3 semaines. Et pendant tout ce temps, vous angoissez : "Est-ce que mon colis est bien parti ? Est-ce qu'il va arriver intact ? Est-ce que la douane va me bloquer ça ?"

**Le problème des frais cachés** : Le prix affiché sur Amazon, c'est jamais le prix final pour un Camerounais. Il faut ajouter les frais de port internationaux (souvent chers), les frais de douane (30% pour l'électronique, parfois plus), les frais de conversion de devises, et des fois même des "frais de service" surprises.

**Le problème du paiement** : Beaucoup de plateformes internationales n'acceptent que les cartes bancaires internationales Visa ou Mastercard. Mais ici, beaucoup de gens utilisent Mobile Money (MTN, Orange Money). C'est pratique, c'est rapide, c'est sécurisé, mais Amazon n'accepte pas ça.

**Le problème du service après-vente** : Imaginez que votre produit arrive cassé ou défectueux. Si vous avez acheté sur une plateforme internationale, bon courage pour le retour. Il faut renvoyer le produit en France, attendre le remboursement, et ça peut prendre des mois. Alors que si vous avez acheté local, vous pouvez juste retourner à la boutique.

C'est pour ça qu'on a décidé de mettre l'accent sur les plateformes locales et africaines.

### Les sources prioritaires qu'on a intégrées

**Jumia (notre priorité numéro 1)**

Jumia, c'est un peu l'Amazon africain. Ils sont présents dans plusieurs pays d'Afrique, et au Cameroun, ils sont vraiment leaders. Presque tout le monde connaît Jumia. Pourquoi on les adore ?

- **Ils acceptent Mobile Money** : Vous pouvez payer avec MTN Mobile Money ou Orange Money. Super pratique.
- **Ils font du cash à la livraison** : Vous préférez ne payer qu'à la réception du colis ? Pas de problème, Jumia propose ça.
- **Ils livrent rapidement** : Généralement 2-4 jours pour Douala et Yaoundé, parfois même le lendemain.
- **Ils ont un service client local** : Un problème ? Vous pouvez les appeler, leur parler en français ou en anglais, et ils comprennent votre situation.
- **Ils offrent des garanties** : La plupart des produits ont une garantie d'au moins 6 mois, souvent 1 an.

**Comment on les intègre** :  
Jumia a une API officielle, mais elle est limitée. Alors on combine : on utilise leur API quand c'est possible (c'est plus rapide et plus fiable), et on complète avec du scraping respectueux de leur site public pour avoir toutes les infos. On met à jour les prix Jumia toutes les 15 minutes pour les produits populaires.

**Les données qu'on collecte** :
- Prix en FCFA (pas besoin de conversion !)
- Disponibilité en stock
- Délais de livraison estimés
- Options de paiement disponibles
- Notes et avis clients
- Photos des produits
- Détails techniques

**Exemple concret** :  
Quand vous cherchez "Samsung Galaxy A54", on va sur Jumia.cm, on trouve le produit, on voit qu'il est à 215,000 FCFA, qu'il y a 156 avis avec une note de 4.3/5, que la livraison est gratuite, et que vous pouvez payer en Mobile Money. On note tout ça, on formate, et on vous présente. Si vous cliquez pour aller chez Jumia, on vous redirige directement vers leur page produit.

**Kaymu**

Kaymu, c'est un peu comme le marché traditionnel mais en ligne. C'est une plateforme où plein de petits vendeurs (des particuliers, des petites boutiques) viennent vendre leurs produits. L'avantage ? Les prix sont souvent très compétitifs. L'inconvénient ? C'est moins standardisé que Jumia, chaque vendeur a ses propres conditions.

**Pourquoi c'est important** :  
Parfois, vous trouvez des perles rares sur Kaymu. Des vendeurs qui importent directement et qui vendent avec une petite marge, des particuliers qui vendent des produits quasi neufs, des artisans locaux qui fabriquent des produits uniques.

**Comment on les intègre** :  
Kaymu n'a pas d'API publique, donc on fait du scraping respectueux. On visite leur site, on lit les annonces, on extrait les infos. On fait attention à ne pas les surcharger : une requête toutes les 20-30 secondes maximum.

**Les défis avec Kaymu** :  
C'est plus compliqué que Jumia parce que chaque vendeur présente ses produits différemment. Certains mettent des photos, d'autres non. Certains donnent plein de détails, d'autres juste un titre et un prix. Notre système doit être flexible et intelligent pour extraire quand même les infos importantes.

**Gloopro**

Gloopro, c'est un spécialiste de l'électronique et du high-tech au Cameroun. Ils sont connus pour être des importateurs directs, ce qui veut dire qu'ils ont souvent de bons prix et qu'ils offrent des garanties constructeur.

**Ce qui les rend spéciaux** :
- Spécialisés en électronique (téléphones, ordinateurs, TV, etc.)
- Importateurs officiels pour certaines marques
- Garanties constructeur (pas juste vendeur)
- Service après-vente technique

**Comment on les intègre** :  
Ils nous ont donné accès à une API partenaire (c'est cool !), ce qui nous permet d'avoir des données fraîches et fiables. Quand leur API ne suffit pas, on complète avec du scraping.

**Autres plateformes locales qu'on intègre** :

- **Konga** : C'est plutôt pour le Nigeria, mais ils livrent parfois au Cameroun, alors on les consulte.
- **Boutiques locales en ligne** : Il y a plein de petites boutiques camerounaises qui ont créé leurs sites web. On les identifie progressivement et on les intègre.
- **Marchés spécialisés** : Des sites spécialisés dans les tissus africains, les cosmétiques, les produits artisanaux, etc.

**Et les plateformes internationales ?**

Attention, on ne les ignore pas ! Amazon, eBay, AliExpress, Cdiscount, on les consulte aussi. Mais ils viennent en deuxième position, après les sources locales. Pourquoi ? Parce qu'on veut d'abord montrer à l'utilisateur ce qui est accessible facilement, rapidement, et avec des modes de paiement locaux.

Mais si l'offre internationale est vraiment beaucoup plus intéressante (genre 30-40% moins cher même avec tous les frais), on la montre quand même, avec tous les avertissements nécessaires.

### La gestion intelligente des prix en Franc CFA

Bon, parlons d'un sujet crucial : **les prix en Franc CFA (FCFA)**.

Quand vous vivez au Cameroun, vous pensez en FCFA. Votre salaire est en FCFA, vos courses sont en FCFA, votre forfait téléphonique est en FCFA. Alors quand un site vous affiche un prix en euros ou en dollars, c'est pénible. Il faut sortir la calculatrice, chercher le taux de change du jour, faire la conversion... et encore, c'est jamais le vrai taux que votre banque va utiliser.

Notre système gère tout ça automatiquement et intelligemment.

**La conversion automatique**

On maintient un taux de change actualisé toutes les heures. On ne le prend pas n'importe où : on consulte les taux officiels de la BEAC (Banque des États de l'Afrique Centrale), et on croise avec les taux pratiqués par les banques camerounaises. Parce que oui, il y a parfois une différence entre le "taux officiel" et le "taux réel" que vous allez effectivement payer.

**Exemple** :  
Le taux officiel BEAC dit : 1€ = 655,957 FCFA  
Le taux moyen des banques camerounaises : 1€ = 655 FCFA  
Le taux qu'on utilise : 655 FCFA (plus réaliste pour l'utilisateur)

Quand on voit un produit à 919€ sur Amazon, on calcule automatiquement : 919 × 655 = 601,945 FCFA, qu'on arrondit à 602,000 FCFA pour la présentation.

**L'affichage intelligent**

Mais on ne se contente pas de convertir. On affiche les deux informations pour que l'utilisateur ait la transparence totale :

```
Prix : 919€ (≈ 602,000 FCFA)
          ↑
    Prix original      Conversion au taux actuel
```

Et on ajoute une petite note : "Taux de change au 02/12/2024 : 1€ = 655 FCFA. Le taux réel peut varier légèrement selon votre banque."

**Le calcul du coût total réel en FCFA**

C'est là qu'on se démarque vraiment. On ne vous montre pas juste le prix du produit converti. On calcule le **coût total réel** que vous allez payer si vous commandez sur cette plateforme internationale. Et croyez-moi, ça change tout !

Reprenons notre exemple de l'iPhone 14 à 919€ sur Amazon France :

```
📱 iPhone 14 128Go Noir - Amazon France

Prix affiché : 919€ (≈ 602,000 FCFA)
    ↓
Mais attendez, ce n'est pas fini...

+ Frais de livraison internationale : 45€ (≈ 29,500 FCFA)
  → Souvent, Amazon affiche "livraison gratuite" en France, 
     mais vers l'Afrique, c'est payant

+ Frais de douane estimés : ~80,000 FCFA
  → Au Cameroun, l'électronique importé est taxé à environ 30%
  → Calcul : (602,000 + 29,500) × 30% ≈ 189,450 FCFA
  → Mais il y a aussi des frais de dédouanement (~15,000 FCFA)
  → Total douanes : ~80,000 FCFA (estimation prudente)

+ Frais de change bancaire : ~2% si vous payez par carte
  → 602,000 × 0.02 = 12,040 FCFA

= COÛT TOTAL RÉEL : 727,000 FCFA
  (au lieu des 602,000 FCFA qu'on voyait au départ)
```

Vous voyez ? Le prix a augmenté de 125,000 FCFA entre ce qui était affiché et ce que vous allez réellement payer. C'est énorme ! Et beaucoup de gens ne calculent pas tout ça avant de commander, puis ils ont la mauvaise surprise.

**Notre engagement de transparence**

On ne cache rien. On affiche tout clairement :

```json
{
  "merchant": "Amazon France",
  "pricing_breakdown": {
    "product_price": {
      "original": "919€",
      "fcfa": 602000,
      "note": "Converti au taux du jour : 1€ = 655 FCFA"
    },
    "shipping": {
      "fcfa": 29500,
      "note": "Livraison internationale vers Cameroun"
    },
    "customs": {
      "fcfa": 80000,
      "note": "Estimation basée sur taux douanier électronique (30%) + frais dédouanement",
      "warning": "Montant peut varier selon évaluation douanière"
    },
    "payment_fees": {
      "fcfa": 12000,
      "note": "Frais de change bancaire estimés (2%)"
    },
    "total": {
      "fcfa": 727000,
      "confidence": "high",
      "last_verified": "Il y a 10 minutes"
    }
  }
}
```

Et on ajoute toujours des avertissements honnêtes :

⚠️ **Points d'attention pour achat international** :
- Les frais de douane sont des estimations. Le montant réel peut varier.
- Délai de livraison : 10-15 jours (parfois plus selon douanes)
- En cas de problème, le service après-vente est en France (compliqué)
- Vérifiez que votre carte bancaire accepte les paiements internationaux
- Certaines banques camerounaises bloquent les paiements en ligne internationaux par sécurité

**Les alertes de prix intelligentes**

On va encore plus loin. Notre système compare intelligemment et vous donne des alertes contextualisées :

```
💡 Analyse comparative pour vous :

✅ Offre locale (Jumia) : 650,000 FCFA
   → 77,000 FCFA de plus que Amazon (après tous frais)
   → MAIS : livraison 2-4 jours vs 10-15 jours
   → MAIS : garantie locale facile à utiliser
   → MAIS : paiement Mobile Money accepté
   → MAIS : service client en français sur place

🎯 Notre recommandation :
Jumia est plus cher de 77,000 FCFA (12% de plus), mais vous offre :
- Une tranquillité d'esprit totale
- Une livraison 5× plus rapide
- Pas de surprise de frais cachés
- Un SAV accessible

77,000 FCFA, c'est le prix de cette tranquillité. À vous de décider si ça vaut le coup !
```

**La gestion multi-devises pour toute l'Afrique**

Notre système ne se limite pas au FCFA. On gère aussi :

- **XOF** (Franc CFA Ouest-Africain) : Pour le Sénégal, Côte d'Ivoire, Mali, etc.
- **NGN** (Naira nigérian) : Pour le Nigeria
- **GHS** (Cedi ghanéen) : Pour le Ghana
- **USD** et **EUR** : Pour les expatriés ou les achats internationaux

Et on détecte automatiquement où vous êtes :
- Vous êtes au Cameroun ? → Affichage en FCFA par défaut
- Vous êtes au Sénégal ? → Affichage en XOF par défaut
- Vous êtes au Nigeria ? → Affichage en NGN par défaut

Mais vous pouvez toujours changer manuellement si vous préférez voir les prix dans une autre devise.

**L'historique des taux de change**

On garde aussi l'historique des taux de change. Pourquoi ? Parce que ça nous permet de faire des analyses intéressantes :

```
📊 Impact du taux de change sur ce produit :

Il y a 3 mois : 1€ = 660 FCFA
Aujourd'hui : 1€ = 655 FCFA

Ce produit à 919€ :
- Il y a 3 mois : 606,540 FCFA
- Aujourd'hui : 602,000 FCFA

→ Économie grâce à l'évolution du change : 4,540 FCFA
→ Tendance : Euro légèrement en baisse vs FCFA
→ Prédiction : Probablement stable sur les 2 prochaines semaines
```

C'est utile surtout pour les gros achats. Si vous voulez acheter un ordinateur portable à 1,500€, une variation de 5 FCFA par euro peut représenter 7,500 FCFA de différence !

## Le cœur intelligent : l'algorithme de correspondance produit

Bon, maintenant on arrive à la partie vraiment technique et fascinante : comment notre système arrive-t-il à comprendre que "iPhone 14 128GB Black" et "Apple iPhone 14 - 128 Go - Noir" sont exactement le même produit ?

C'est un problème plus complexe qu'il n'y paraît. Parce que les marchands ne se mettent pas d'accord sur comment nommer leurs produits. Chacun a sa propre façon de faire. Et si notre système se trompe, vous vous retrouvez avec soit des doublons (le même produit apparaît plusieurs fois séparément), soit des fusions incorrectes (des produits différents sont groupés ensemble).

### Le problème du "même produit, descriptions différentes"

Laissez-moi vous donner un exemple concret pour que vous compreniez bien le défi.

**Scénario réel** : Un utilisateur cherche "Samsung Galaxy A54"

Voici ce que nos différents scrapers ramènent :

```
SOURCE 1 - Jumia Cameroun :
Titre : "Samsung Galaxy A54 5G (8GB/128GB) - Awesome Violet"
Prix : 235,000 FCFA
EAN : 8806094936254
Description : "Le nouveau Samsung Galaxy A54 5G combine performance..."

SOURCE 2 - Amazon France :
Titre : "SAMSUNG Galaxy A54 5G Smartphone, Android..."
Prix : 349€ (≈ 228,695 FCFA)
ASIN : B0BQX2QVDQ
EAN : 8806094936254
Description : "SAMSUNG Galaxy A54 5G Smartphone. Android 13..."

SOURCE 3 - Boutique Locale "TechMaster Yaoundé" :
Titre : "Galaxy A54 8Go 128Go violet"
Prix : 225,000 FCFA
EAN : Non disponible
Description : "Samsung A54 neuf scellé garantie 6 mois"

SOURCE 4 - Kaymu :
Titre : "Samsung A54 5g 8/128 couleur violette"
Prix : 220,000 FCFA
EAN : Non disponible
Description : "telephone Samsung a54 importation directe..."

SOURCE 5 - Gloopro :
Titre : "SAMSUNG GALAXY A54 5G - 8GB RAM - 128GB - VIOLET"
Prix : 240,000 FCFA
SKU : SM-A546BLVDEUC
EAN : 8806094936254
Description : "Samsung Galaxy A54 5G - Version Européenne..."
```

Pour un humain, c'est évident : ce sont tous des Samsung Galaxy A54, même modèle, même couleur. Mais pour un ordinateur qui ne voit que des chaînes de caractères, c'est beaucoup moins clair. Regardez les différences :

- Les titres sont tous différents
- Certains écrivent "A54", d'autres "A54 5G", d'autres "A 54"
- Certains mentionnent "8GB/128GB", d'autres "8Go 128Go", d'autres "8/128"
- La couleur : "Awesome Violet" vs "violet" vs "violette" vs "VIOLET"
- Les majuscules et minuscules varient
- Certains ont un EAN (code-barres), d'autres non

Comment fait-on pour les matcher correctement ?

### Notre système de scoring multi-critères

On utilise ce qu'on appelle un **système de scoring**. En gros, on donne des points pour chaque similarité détectée, et à la fin, on regarde le score total. Si c'est au-dessus d'un certain seuil (85%), on considère que c'est le même produit.

**Critère 1 : Les identifiants techniques (30% du score total)**

C'est le critère le plus fiable. Si deux produits ont le même code-barres (EAN), c'est forcément le même produit physique. C'est mathématique.

```
Jumia : EAN = 8806094936254
Amazon : EAN = 8806094936254
→ MATCH PARFAIT ! Score = 100/100

Gloopro : EAN = 8806094936254
→ MATCH PARFAIT aussi ! Score = 100/100

TechMaster : EAN = Non disponible
→ On ne peut pas utiliser ce critère. Score = 0/100 (mais pas éliminatoire)

Kaymu : EAN = Non disponible
→ Pareil, Score = 0/100
```

**Les différents types d'identifiants qu'on reconnaît** :
- **EAN-13** : Code-barres européen (13 chiffres)
- **UPC** : Code-barres américain (12 chiffres)
- **ISBN** : Pour les livres
- **ASIN** : Code Amazon (utile mais pas universel)
- **MPN** : Numéro de pièce du fabricant
- **SKU** : Référence interne du marchand (moins fiable car chaque marchand a son propre SKU)

Si on trouve un EAN ou UPC identique, bingo, c'est quasiment sûr que c'est le même produit. On donne 100 points sur ce critère.

**Critère 2 : La similarité textuelle (40% du score total)**

Maintenant, pour les produits sans EAN (comme TechMaster et Kaymu dans notre exemple), on doit analyser le texte. On utilise plusieurs algorithmes complémentaires :

**a) Levenshtein Distance** : Cet algorithme compte combien de lettres il faut changer pour transformer une chaîne en une autre.

```
"Samsung Galaxy A54 5G" 
vs 
"Samsung A54 5g"

Différences :
- "Galaxy " est enlevé (7 caractères)
- "G" majuscule devient "g" minuscule (1 changement)

Distance de Levenshtein : 8
Longueur moyenne : (20 + 13) / 2 = 16.5
Similarité : 1 - (8 / 16.5) = 51.5%

C'est moyen. Pas terrible seul.
```

**b) Jaro-Winkler** : Cet algorithme est meilleur pour les noms courts et donne plus d'importance au début de la chaîne (les premiers mots).

```
"Samsung Galaxy A54 5G"
vs
"Samsung A54 5g"

Jaro-Winkler : 82%

Beaucoup mieux ! Parce qu'il reconnaît que "Samsung A54" au début est identique.
```

**c) Normalisation + Tokenization + Cosine Similarity** : Là, c'est plus sophistiqué. On décompose les titres en "mots importants" et on compare.

```
"Samsung Galaxy A54 5G (8GB/128GB) - Awesome Violet"

Étape 1 - Normalisation :
→ Tout en minuscules : "samsung galaxy a54 5g (8gb/128gb) - awesome violet"
→ Suppression ponctuation : "samsung galaxy a54 5g 8gb 128gb awesome violet"

Étape 2 - Tokenization (découpage en mots) :
→ ["samsung", "galaxy", "a54", "5g", "8gb", "128gb", "awesome", "violet"]

Étape 3 - Suppression des mots peu importants (stop words) :
→ ["samsung", "galaxy", "a54", "5g", "8gb", "128gb", "violet"]
   (on garde pas "awesome" car c'est juste marketing)

Étape 4 - Extraction des tokens clés avec leur importance :
→ samsung: poids 10 (marque, très important)
→ galaxy: poids 5 (série)
→ a54: poids 10 (modèle précis, très important)
→ 5g: poids 7 (caractéristique technique importante)
→ 8gb: poids 6 (RAM)
→ 128gb: poids 6 (stockage)
→ violet: poids 8 (couleur, important pour différencier les variants)

Étape 5 - Comparaison avec l'autre titre :
"Samsung A54 5g 8/128 couleur violette"
→ Tokens: ["samsung", "a54", "5g", "8", "128", "violet"]

Comparaison :
✓ samsung → samsung (match parfait, poids 10)
✗ galaxy → absent (mais c'est pas grave, c'est sous-entendu)
✓ a54 → a54 (match parfait, poids 10)
✓ 5g → 5g (match parfait, poids 7)
✓ 8gb → 8 (on comprend que c'est équivalent, poids 6)
✓ 128gb → 128 (pareil, poids 6)
✓ violet → violette (variante linguistique, on accepte, poids 8)

Score de similarité : 89%
```

Vous voyez ? Même si les titres sont très différents en surface, notre algorithme comprend qu'ils parlent du même produit.

**d) TF-IDF (Term Frequency-Inverse Document Frequency)** : Ça, c'est pour donner plus d'importance aux mots rares.

L'idée : Le mot "Samsung" apparaît dans des milliers de titres de produits. C'est pas très discriminant. Par contre, "A54" est beaucoup plus spécifique. Et "8806094936254" (un EAN) est unique.

```
Dans notre base de données :
- "Samsung" apparaît dans 45,000 produits → peu discriminant
- "Galaxy" apparaît dans 12,000 produits → moyennement discriminant  
- "A54" apparaît dans 150 produits → très discriminant !
- "5G" apparaît dans 8,000 produits → moyennement discriminant
- "8GB" apparaît dans 20,000 produits → peu discriminant
- "Violet" apparaît dans 5,000 produits → moyennement discriminant

Donc si deux produits partagent "A54", ça pèse BEAUCOUP plus lourd que s'ils partagent juste "Samsung".
```

**On combine tout ça** et on obtient un score de similarité textuelle final.

Pour notre exemple :
- Jumia vs Amazon : 94% (très similaires)
- Jumia vs TechMaster : 87% (similaires)
- Jumia vs Kaymu : 82% (assez similaires)
- Jumia vs Gloopro : 91% (très similaires)

**Critère 3 : Les caractéristiques techniques (20% du score total)**

Maintenant, on extrait et compare les spécifications techniques. Notre système sait que :

```
Capacité de stockage :
128GB = 128 Go = 128go = 128 gigaoctets = 128G

Mémoire RAM :
8GB = 8 Go = 8go = 8 gigaoctets

Couleurs (pour Samsung) :
Violet = Awesome Violet = Violette = Mauve = Purple

Connectivité :
5G = 5g = cinq G

Taille écran :
6.4" = 6.4 pouces = 6,4" = 162.56mm
```

Notre système a une base de connaissances qui contient toutes ces équivalences. Quand il voit "8GB/128GB", il extrait :
- RAM : 8GB
- Stockage : 128GB

Quand il voit "8/128", il comprend que c'est probablement :
- RAM : 8GB
- Stockage : 128GB

Et il compare :

```
Jumia : RAM 8GB, Stockage 128GB, Couleur Violet, 5G
Amazon : RAM 8GB, Stockage 128GB, Couleur Violet, 5G
TechMaster : RAM 8GB, Stockage 128GB, Couleur Violet, (5G non mentionné mais probable)
Kaymu : RAM 8GB, Stockage 128GB, Couleur Violet, 5G
Gloopro : RAM 8GB, Stockage 128GB, Couleur Violet, 5G

→ Toutes les specs matchent ! Score = 95/100 pour tous
```

**Critère 4 : La similarité visuelle des images (10% du score total)**

Pour les produits qui n'ont pas d'EAN et dont les titres sont ambigus, on peut aussi comparer les photos du produit.

On utilise des techniques de vision par ordinateur :

**a) Perceptual Hash (pHash)** : On calcule une "empreinte digitale" de l'image.

```
Image 1 (Jumia) : Samsung A54 violet vu de face
→ pHash : 8f7d3c2a1b...

Image 2 (Amazon) : Samsung A54 violet, même angle
→ pHash : 8f7d3c2a1c...

Différence : 1 bit sur 64 → 98.4% similaires

Image 3 (TechMaster) : Samsung A54 violet, angle différent
→ pHash : 8e7d3d2a1b...

Différence : 5 bits sur 64 → 92.2% similaires
```

Si les images sont très similaires (> 85%), c'est probablement le même produit.

**b) Histogramme de couleurs** : On analyse la distribution des couleurs dans l'image.

```
Samsung A54 Violet :
- Violet/Mauve : 40% de l'image
- Noir (écran éteint) : 30%
- Blanc (fond) : 25%
- Autres : 5%

Si deux images ont des histogrammes très proches, c'est probablement le même produit photographié différemment.
```

**c) Détection de features (SIFT)** : On identifie les points caractéristiques de l'image.

```
Pour un Samsung A54 :
- Les 3 objectifs de caméra (disposition triangulaire)
- Le logo Samsung
- La forme générale (coins arrondis, proportions)
- Les boutons sur la tranche

Si deux images ont les mêmes features aux mêmes endroits, match !
```

Mais attention, on utilise ce critère avec prudence. Parfois, deux téléphones différents se ressemblent beaucoup (Samsung A54 vs A34 par exemple). Donc on donne seulement 10% du poids total à ce critère.

### L'algorithme de décision final

Maintenant qu'on a tous nos scores, voici comment on décide :

```python
def are_same_product(product1, product2):
    # Étape 1 : Check identifiants uniques
    if product1.ean and product2.ean:
        if product1.ean == product2.ean:
            return True, 100  # Match confirmé !
    
    # Étape 2 : Calcul du score combiné
    score_identifiers = calculate_identifier_similarity(product1, product2)  # 30%
    score_text = calculate_text_similarity(product1, product2)  # 40%
    score_specs = calculate_specs_similarity(product1, product2)  # 20%
    score_visual = calculate_visual_similarity(product1, product2)  # 10%
    
    total_score = (
        score_identifiers * 0.30 +
        score_text * 0.40 +
        score_specs * 0.20 +
        score_visual * 0.10
    )
    
    # Étape 3 : Décision basée sur le seuil
    if total_score >= 85:
        return True, total_score  # Match probable
    elif total_score >= 70:
        return "Maybe", total_score  # Doute, investigation manuelle
    else:
        return False, total_score  # Produits différents
```

**Résultat pour notre exemple Samsung A54** :

```
Jumia vs Amazon :
- Identifiants : 100/100 (même EAN)
- Texte : 94/100
- Specs : 95/100
- Visuel : 96/100
→ Score final : 96.3% → MATCH CONFIRMÉ

Jumia vs TechMaster :
- Identifiants : 0/100 (pas d'EAN chez TechMaster)
- Texte : 87/100
- Specs : 95/100
- Visuel : 88/100
→ Score final : 77.8% → MATCH PROBABLE (mais on demande confirmation)

Jumia vs Kaymu :
- Identifiants : 0/100
- Texte : 82/100
- Specs : 93/100
- Visuel : 85/100
→ Score final : 75.6% → MATCH PROBABLE

Jumia vs Gloopro :
- Identifiants : 100/100 (même EAN)
- Texte : 91/100
- Specs : 95/100
- Visuel : 94/100
→ Score final : 95.1% → MATCH CONFIRMÉ
```

**Notre décision** : On groupe Jumia, Amazon et Gloopro ensemble (EAN identique, c'est sûr). Pour TechMaster et Kaymu, on les groupe aussi mais avec une confiance un peu moindre (pas d'EAN, mais tout le reste matche bien).

Au final, l'utilisateur voit :

```
📱 Samsung Galaxy A54 5G (8GB/128GB) - Violet

5 offres trouvées :

🥇 Kaymu : 220,000 FCFA (meilleur prix)
🥈 TechMaster Yaoundé : 225,000 FCFA
🥉 Jumia Cameroun : 235,000 FCFA
4️⃣ Gloopro : 240,000 FCFA
5️⃣ Amazon France : 349€ (≈ 557,000 FCFA total avec frais)
```

Propre, clair, et exact !

###### Les cas difficiles et comment on les gère

Bon, je vous ai expliqué le cas idéal où tout matche bien. Mais la réalité, c'est qu'on tombe parfois sur des cas vraiment compliqués. Laissez-moi vous en donner quelques exemples concrets et vous expliquer comment on s'en sort.

**Cas difficile #1 : Les variantes de produits**

Imaginez qu'un utilisateur cherche juste "iPhone 14". Mais l'iPhone 14 existe en plusieurs versions :
- Capacité : 64GB, 128GB, 256GB, 512GB
- Couleur : Noir (Midnight), Blanc (Starlight), Bleu, Violet, Rouge (Product RED)
- État : Neuf, Reconditionné, Occasion

Ça fait potentiellement 4 × 5 × 3 = 60 produits différents ! Est-ce qu'on les groupe tous ensemble ou on les sépare ?

**Notre stratégie** :

On crée des **niveaux de groupement** :

```
Niveau 1 - Produit générique : "Apple iPhone 14"
    │
    ├─ Niveau 2 - Par capacité : "Apple iPhone 14 128GB"
    │   │
    │   ├─ Niveau 3 - Par couleur : "Apple iPhone 14 128GB Noir"
    │   │   │
    │   │   ├─ Niveau 4 - Par état : "Apple iPhone 14 128GB Noir Neuf"
    │   │   └─ Niveau 4 - Par état : "Apple iPhone 14 128GB Noir Reconditionné"
    │   │
    │   └─ Niveau 3 - Par couleur : "Apple iPhone 14 128GB Bleu"
    │       └─ ...
    │
    └─ Niveau 2 - Par capacité : "Apple iPhone 14 256GB"
        └─ ...
```

Quand l'utilisateur cherche sans préciser, on lui montre le niveau 1 avec un message :

```
📱 Apple iPhone 14

⚠️ Ce produit existe en plusieurs variantes. Voici les prix par configuration :

128GB Noir : à partir de 620,000 FCFA (5 offres)
128GB Bleu : à partir de 625,000 FCFA (3 offres)
256GB Noir : à partir de 750,000 FCFA (4 offres)
...

👉 Précisez votre recherche pour voir les offres détaillées :
   Ex: "iPhone 14 128GB Noir"
```

Si l'utilisateur précise "iPhone 14 128GB Noir", on descend au niveau 3 et on lui montre toutes les offres pour cette configuration exacte.

**Cas difficile #2 : Les produits reconditionnés vs neufs**

C'est un gros piège. Sur certains sites, on trouve "iPhone 14 128GB Noir" à 400,000 FCFA. Wow, super deal ! Sauf que... c'est un produit reconditionné, pas neuf. Et sur un autre site, le même à 650,000 FCFA neuf.

Si on les groupe ensemble, l'utilisateur pourrait croire qu'il y a une offre incroyable à 400,000 FCFA et être déçu en cliquant.

**Notre solution** :

On détecte automatiquement les mots-clés qui indiquent un produit reconditionné :
- "Reconditionné", "Refurbished", "Occasion", "Used"
- "Grade A", "Grade B", "Grade C" (classifications de reconditionnement)
- "Comme neuf", "Très bon état", "État correct"
- "Seconde main", "Deuxième main"

Et on les sépare clairement :

```
📱 Apple iPhone 14 128GB Noir

PRODUITS NEUFS (4 offres) :
1. Jumia : 650,000 FCFA ⭐ Garantie 1 an
2. TechShop : 620,000 FCFA ⭐ Garantie 6 mois
3. Gloopro : 670,000 FCFA ⭐ Garantie constructeur
4. Amazon : 919€ (≈ 727,000 FCFA total)

PRODUITS RECONDITIONNÉS (2 offres) :
1. Kaymu : 420,000 FCFA ⚠️ Grade A, garantie 3 mois
2. BackMarket : 380,000 FCFA ⚠️ Grade B, garantie 6 mois

💡 Les produits reconditionnés sont jusqu'à 40% moins chers, mais ont été utilisés puis remis à neuf. Vérifiez bien l'état et la garantie avant d'acheter.
```

**Cas difficile #3 : Les faux positifs (produits qui semblent similaires mais ne le sont pas)**

Parfois, notre algorithme se trompe. Par exemple :

```
Produit A : "Samsung Galaxy A54 5G 128GB"
Produit B : "Samsung Galaxy A34 5G 128GB"

Similarité textuelle : 91% (très proche !)
Specs : RAM 8GB, 128GB, 5G (identiques)
Visuel : Ils se ressemblent beaucoup

→ Notre algorithme pourrait les confondre
```

Mais ce sont deux téléphones différents ! Le A54 est plus récent et plus performant que le A34.

**Comment on évite ça** :

On maintient une **liste de modèles connus** pour chaque marque :

```
Samsung Galaxy A-series (2023-2024) :
- A04
- A14
- A24
- A34 ← Modèle distinct
- A54 ← Modèle distinct
- A74
```

Quand notre algorithme détecte "A34" dans un titre et "A54" dans un autre, il sait que ce sont des modèles différents, même si tout le reste est similaire. Il force la séparation.

**Cas difficile #4 : Les versions régionales**

Certains produits ont plusieurs versions selon les régions :

```
iPhone 14 (Version USA) :
- Compatibilité 5G : mmWave + Sub-6GHz
- Dual SIM : Non (sauf eSIM)
- Garantie : Américaine

iPhone 14 (Version Internationale/Europe) :
- Compatibilité 5G : Sub-6GHz uniquement
- Dual SIM : Oui (physique + eSIM)
- Garantie : Internationale

iPhone 14 (Version Chine) :
- Compatibilité 5G : Différente
- Dual SIM : Dual physique
- Logiciel : Limité (pas Google)
```

C'est le même nom, même capacité, même couleur, mais ce ne sont pas exactement les mêmes produits !

**Notre approche** :

On extrait la région mentionnée dans la description ou on la déduit du site :

```
Jumia Cameroun : "iPhone 14 128GB - Version Internationale"
→ Région : International/Europe

Amazon USA : "iPhone 14 128GB - Model A2882"
→ Région : USA (d'après le numéro de modèle)

Site chinois : "苹果 iPhone 14 128GB"
→ Région : Chine
```

Et on ajoute cette info dans la comparaison :

```
📱 Apple iPhone 14 128GB Noir

⚠️ Attention : Ce produit existe en plusieurs versions régionales

VERSION INTERNATIONALE (recommandée pour Cameroun) :
- Jumia : 650,000 FCFA
- Gloopro : 670,000 FCFA
✓ Compatible réseaux africains
✓ Garantie utilisable localement

VERSION USA :
- Amazon USA : 820$ (≈ 520,000 FCFA)
⚠️ Compatibilité 5G limitée en Afrique
⚠️ Garantie américaine uniquement
⚠️ Frais douane + livraison : +150,000 FCFA
```

**Cas difficile #5 : Les bundles et packs**

Parfois, les vendeurs créent des packs :

```
Offre A : iPhone 14 seul - 650,000 FCFA
Offre B : iPhone 14 + Coque + Protège-écran - 670,000 FCFA
Offre C : iPhone 14 + AirPods - 850,000 FCFA
```

Est-ce qu'on les groupe ensemble ? Non, parce que ce ne sont pas exactement les mêmes offres.

**Notre solution** :

On les classe dans des catégories différentes :

```
📱 Apple iPhone 14 128GB Noir SEUL (4 offres)
📦 Apple iPhone 14 128GB Noir EN PACK (3 offres)

Voir d'abord le produit seul ▼

[Bouton : Voir aussi les packs avec accessoires]
```

Si l'utilisateur clique pour voir les packs :

```
📦 Packs avec Apple iPhone 14 128GB Noir

Pack 1 : iPhone 14 + Coque + Protège-écran
→ TechShop : 670,000 FCFA
   (Économie : 20,000 FCFA vs achat séparé)

Pack 2 : iPhone 14 + AirPods
→ Jumia : 850,000 FCFA
   (Économie : 50,000 FCFA vs achat séparé)

Pack 3 : iPhone 14 + Chargeur rapide + Câble
→ Gloopro : 685,000 FCFA
   (Économie : 15,000 FCFA vs achat séparé)
```

**Cas difficile #6 : Les différences subtiles de specs**

Parfois, deux produits ont presque le même nom mais des specs légèrement différentes :

```
Produit A : "Samsung Galaxy A54 5G 128GB 8GB RAM"
Produit B : "Samsung Galaxy A54 5G 128GB 6GB RAM"
```

Ce sont des variantes du même modèle, mais avec différentes quantités de RAM. Certains marchés ont le modèle 6GB, d'autres le 8GB.

**Comment on gère** :

On les considère comme des **variantes du même produit** mais on les distingue clairement :

```
📱 Samsung Galaxy A54 5G 128GB

⚠️ Ce modèle existe avec 2 options de RAM :

Variante 8GB RAM (recommandée - Plus performante) :
- Jumia : 235,000 FCFA
- Gloopro : 240,000 FCFA

Variante 6GB RAM :
- Kaymu : 205,000 FCFA
- TechMaster : 210,000 FCFA

💡 La version 8GB RAM est 10-15% plus chère mais offre de meilleures performances pour le multitâche et les jeux.
```

## Le système d'historique et de prédiction de prix

Maintenant, parlons d'une fonctionnalité que nos utilisateurs adorent : **l'historique des prix et les prédictions**.

Vous savez ce qui est frustrant ? Acheter un produit à 650,000 FCFA et découvrir une semaine plus tard qu'il est descendu à 580,000 FCFA. Vous avez l'impression de vous être fait avoir. Notre système vous aide à éviter ça.

### Comment on collecte l'historique des prix

**La collecte continue**

On ne collecte pas les prix juste quand un utilisateur fait une recherche. Non, on collecte en permanence, en arrière-plan, pour les produits populaires.

Voici notre stratégie :

```
PRODUITS TRÈS POPULAIRES (Top 1000) :
- Collecte toutes les 15 minutes
- Exemple : iPhone 14, Samsung Galaxy A54, AirPods, etc.
- Stockage : Chaque point de prix

PRODUITS POPULAIRES (Top 10,000) :
- Collecte toutes les heures
- Exemple : Casques Sony, Montres connectées moyennes gammes
- Stockage : Moyenne horaire

PRODUITS MOYENNEMENT RECHERCHÉS :
- Collecte toutes les 4 heures
- Exemple : Accessoires spécifiques, produits de niche
- Stockage : Moyenne par 4h

PRODUITS RAREMENT RECHERCHÉS :
- Collecte à la demande (quand quelqu'un cherche)
- Stockage : Snapshots ponctuels
```

**Le stockage intelligent**

On ne peut pas garder TOUS les prix de TOUS les produits pendant des années. Ça prendrait des pétaoctets de stockage ! Alors on utilise une stratégie d'agrégation :

```
Dernières 24 heures : 
→ Tous les points collectés (granularité maximale)

Derniers 7 jours :
→ Moyenne par heure

Derniers 30 jours :
→ Moyenne par jour

Derniers 6 mois :
→ Moyenne par semaine

Au-delà :
→ Moyenne par mois
```

Ça nous permet d'avoir une précision maximale pour le court terme (ce qui intéresse l'utilisateur qui veut acheter maintenant) tout en gardant les tendances long terme.

### L'affichage de l'historique

Quand un utilisateur regarde un produit, on lui montre un graphique clair :

```
📊 Historique du prix - iPhone 14 128GB Noir chez Jumia

                        ↗ 720,000 (25 Oct)
                    ↗
700,000 ────────────────────────────── Pic
                ↗               ↘
650,000 ────────────────────────────── Prix actuel ⭐
            ↗                       ↘
600,000 ───────────────────────────────
        ↗
550,000 ───────────────────────────────

    01 Oct    15 Oct    01 Nov    15 Nov    01 Dec

📉 Tendance : Baisse de 9.7% sur 30 jours
💰 Prix actuel : 650,000 FCFA
🏆 Meilleur prix historique : 650,000 FCFA (aujourd'hui !)
📊 Prix moyen 30 jours : 685,000 FCFA
💡 Économie vs moyenne : 35,000 FCFA
```

**Les insights automatiques**

Notre système génère automatiquement des insights intelligents basés sur l'historique :

```
✅ BON MOMENT POUR ACHETER
Le prix actuel (650,000 FCFA) est le plus bas des 30 derniers jours.
La tendance est à la baisse depuis 3 semaines.

OU

⏳ ATTENDEZ UN PEU
Le prix actuel (720,000 FCFA) est 12% au-dessus de la moyenne.
Historiquement, ce produit baisse souvent début du mois.
Prochaine baisse probable : dans 3-5 jours

OU

🔥 PROMOTION EXCEPTIONNELLE
Le prix a baissé de 15% en 24h !
C'est 25% moins cher que le prix moyen.
⚠️ Promotion temporaire, probablement limitée dans le temps

OU

⚠️ HAUSSE INHABITUELLE
Le prix a augmenté de 20% cette semaine.
Cause probable : Rupture de stock chez la concurrence
Recommandation : Attendez que les stocks se reconstituent
```

### La détection des promotions flash

Les promotions flash, c'est le cauchemar de l'acheteur qui n'est pas au courant. Un produit passe de 700,000 FCFA à 550,000 FCFA pendant 4 heures, puis retourne à 700,000 FCFA. Si vous n'étiez pas connecté à ce moment-là, vous l'avez loupée.

**Notre système de détection**

On surveille les variations brutales de prix :

```python
def detect_flash_sale(product_id, current_price, previous_prices):
    # Prix moyen des 7 derniers jours
    avg_7d = calculate_average(previous_prices, days=7)
    
    # Prix moyen des 24 dernières heures
    avg_24h = calculate_average(previous_prices, hours=24)
    
    # Détection : baisse brutale
    if current_price < avg_7d * 0.85:  # 15% moins cher que la moyenne
        if previous_prices[-1] > avg_7d * 0.95:  # Le prix précédent était normal
            # C'est probablement une promo flash !
            return {
                "type": "FLASH_SALE",
                "discount_percentage": ((avg_7d - current_price) / avg_7d) * 100,
                "started": get_timestamp_when_drop_happened(),
                "urgency": "HIGH",
                "message": f"🔥 PROMO FLASH ! -{discount}% (Économie: {avg_7d - current_price:,} FCFA)"
            }
```

Et on affiche ça de façon très visible :

```
🔥🔥🔥 ALERTE PROMOTION FLASH 🔥🔥🔥

iPhone 14 128GB chez Jumia
Prix normal : 700,000 FCFA
Prix actuel : 550,000 FCFA
ÉCONOMIE : 150,000 FCFA (-21%)

⏰ Promotion détectée il y a 37 minutes
⚠️ Durée inconnue - Peut se terminer à tout moment !

[VOIR L'OFFRE SUR JUMIA] ←── Bouton très visible
```

### La prédiction de prix (Machine Learning)

Maintenant, la partie vraiment cool : on essaie de **prédire** où le prix va aller.

**Comment on fait ?**

On utilise un modèle de machine learning (réseau de neurones LSTM - Long Short-Term Memory) entraîné sur :

1. **L'historique des prix du produit** (évolution passée)
2. **Les patterns saisonniers** (Black Friday, Noël, rentrée scolaire, etc.)
3. **Les tendances du marché** (tous les iPhone baissent généralement après 6 mois)
4. **Les événements externes** (sortie du nouvel iPhone → ancien baisse)
5. **Le comportement de la concurrence** (si un vendeur baisse, les autres suivent souvent)

```
Exemple - Prédiction pour iPhone 14 en décembre :

Facteurs analysés :
✓ Historique : Baisse régulière de 2% par semaine depuis 2 mois
✓ Saisonnier : Période de Noël = promotions habituelles
✓ Marché : iPhone 15 sorti il y a 3 mois → pression à la baisse sur iPhone 14
✓ Concurrence : Jumia a baissé hier, Amazon va probablement suivre
✓ Stock : Niveaux de stock élevés = pression à la baisse

Prédiction :
📉 Probabilité 70% : Baisse à 620,000-630,000 FCFA d'ici 7 jours
📊 Probabilité 20% : Prix stable autour de 650,000 FCFA
📈 Probabilité 10% : Hausse à 670,000-680,000 FCFA (peu probable)

Recommandation :
Si vous pouvez attendre 1 semaine, vous économiserez probablement 20,000-30,000 FCFA.
Si vous avez besoin du produit maintenant, le prix actuel est déjà bon (meilleur des 30 jours).
```

**Niveau de confiance**

On ne prétend jamais prédire avec 100% de certitude. On affiche toujours notre niveau de confiance :

```
Confiance ÉLEVÉE (>75%) :
→ Basée sur patterns clairs et répétitifs
→ Exemple : Baisse systématique le 1er du mois chez un marchand

Confiance MOYENNE (50-75%) :
→ Basée sur tendances générales
→ Exemple : Baisse probable mais timing incertain

Confiance FAIBLE (<50%) :
→ Trop d'incertitude
→ On ne fait pas de prédiction, juste une analyse de risque
```

### Les alertes de prix personnalisées

Les utilisateurs peuvent créer des alertes :

```
📬 Créer une alerte pour ce produit

Options :
□ M'alerter si le prix baisse de 10% ou plus
□ M'alerter si le prix descend sous 600,000 FCFA
□ M'alerter en cas de promotion flash
□ M'alerter si ce produit arrive en stock chez [marchand]

Recevoir par :
☑ Email
☑ SMS (si mobile fourni)
□ Notification push (app mobile)

Durée de l'alerte :
● 30 jours  ○ 60 jours  ○ 90 jours  ○ Jusqu'à désactivation
```

Quand les conditions sont remplies, on envoie immédiatement :

```
📧 [YowYob] Alerte Prix : iPhone 14 a baissé !

Bonjour,

Bonne nouvelle ! Le produit que vous surveillez a baissé de prix :

iPhone 14 128GB Noir chez Jumia
Ancien prix : 650,000 FCFA
Nouveau prix : 590,000 FCFA ⭐
ÉCONOMIE : 60,000 FCFA (-9%)

Cette promotion a été détectée il y a 12 minutes.

[VOIR L'OFFRE MAINTENANT]

⏰ Les promotions peuvent être limitées dans le temps.

---
Vous recevez cet email car vous avez créé une alerte prix sur YowYob Search.
Pour gérer vos alertes : https://yowyob.com/mon-compte/alertes
```

## Sécurité, éthique et légalité

Bon, maintenant parlons d'un sujet super important : comment on fait tout ça de manière éthique, légale et respectueuse.

Parce que voyez-vous, scraper le web, c'est pas le Far West. On ne peut pas juste dire "je vais prendre toutes les données que je veux où je veux". Il y a des règles, il y a des lois, et surtout, il y a une éthique à respecter.

### Notre philosophie : Le scraping respectueux

**Principe #1 : On ne collecte QUE des données publiques**

C'est la base absolue. On ne collecte que ce qui est visible par n'importe qui sur internet sans avoir à se connecter.

```
✅ CE QU'ON COLLECTE :
- Prix affichés publiquement
- Descriptions de produits
- Photos de produits
- Avis clients publics
- Informations de livraison publiques

❌ CE QU'ON NE COLLECTE JAMAIS :
- Données derrière un login
- Informations personnelles d'utilisateurs
- Données de paiement
- Emails ou téléphones de clients
- Historiques d'achat
- Paniers d'achat
- Données internes des marchands
```

**Principe #2 : Respect strict du robots.txt**

Chaque site web a un fichier appelé `robots.txt` qui dit aux robots (comme nous) ce qu'ils peuvent ou ne peuvent pas faire.

Exemple du robots.txt de Jumia (fictif pour l'exemple) :

```
# robots.txt de jumia.cm

User-agent: *
Allow: /
Disallow: /admin/
Disallow: /checkout/
Disallow: /mon-compte/
Crawl-delay: 10

User-agent: YowYobBot
Allow: /produits/
Allow: /search/
Crawl-delay: 5
```

Ce fichier nous dit :
- On peut accéder à la plupart du site
- On ne peut PAS accéder à `/admin/`, `/checkout/`, `/mon-compte/` (logique, c'est privé)
- On doit attendre au moins 10 secondes entre chaque requête
- Pour YowYobBot spécifiquement, ils sont plus généreux : 5 secondes suffisent

**Notre engagement** : On lit et respecte SCRUPULEUSEMENT chaque robots.txt. Si un site dit "ne scrapez pas /products/", on ne le fait pas. Point.

```java
@Service
public class RobotsTxtChecker {
    
    public boolean isAllowed(String siteUrl, String path) {
        // Lire le robots.txt du site
        RobotsTxt robotsTxt = fetchAndParse(siteUrl + "/robots.txt");
        
        // Vérifier si notre bot est autorisé sur ce chemin
        if (!robotsTxt.isAllowed("YowYobBot", path)) {
            log.warn("Path {} is disallowed by robots.txt for {}", path, siteUrl);
            return false;
        }
        
        return true;
    }
    
    public int getCrawlDelay(String siteUrl) {
        RobotsTxt robotsTxt = fetchAndParse(siteUrl + "/robots.txt");
        int delay = robotsTxt.getCrawlDelay("YowYobBot");
        
        // On ajoute toujours une marge de sécurité
        return Math.max(delay, 10); // Minimum 10 secondes
    }
}
```

**Principe #3 : Rate limiting intelligent et adaptatif**

On ne bombarde jamais un site de requêtes. On y va doucement, respectueusement.

```
SITES MAJEURS (Jumia, Amazon, etc.) :
- Maximum 1 requête toutes les 10 secondes
- En réalité, souvent plus espacé (15-20 secondes)
- Si le site répond lentement, on ralentit encore plus

PETITS SITES LOCAUX :
- Maximum 1 requête toutes les 30-60 secondes
- On fait très attention à ne pas les surcharger
- Si on détecte des signes de charge (réponses lentes), on arrête temporairement

SITES QUI NOUS ONT DONNÉ UNE API :
- On utilise l'API, pas le scraping
- On respecte les quotas qu'ils nous ont donnés
```

Et on s'adapte dynamiquement :

```python
class AdaptiveRateLimiter:
    def should_wait_before_next_request(self, site):
        # Temps d'attente de base (du robots.txt)
        base_delay = self.get_crawl_delay(site)
        
        # Si le site répond lentement (>3s), on augmente le délai
        if self.get_avg_response_time(site) > 3000:
            base_delay *= 2
            
        # Si le site a renvoyé des erreurs 429 (Too Many Requests), on augmente beaucoup
        if self.got_rate_limit_error(site, last_hour=True):
            base_delay *= 5
            
        # Si c'est une heure de pointe (12h-14h, 18h-21h), on ralentit
        if self.is_peak_hour():
            base_delay *= 1.5
            
        return base_delay
```

**Principe #4 : Identification transparente**

On ne se cache pas. Chaque requête qu'on envoie contient un User-Agent clair qui dit qui on est :

```
User-Agent: YowYobBot/1.0 (+https://yowyob.com/bot-info; contact: bot@yowyob.com)
```

Si un administrateur de site voit notre bot dans ses logs et veut nous contacter, il peut. On a une page dédiée qui explique :
- Qui on est
- Ce qu'on fait
- Pourquoi on le fait
- Comment on le fait
- Comment nous contacter
- Comment demander l'exclusion

**Principe #5 : Droit de refus absolu**

N'importe quel site peut nous demander de ne plus le scraper. On a un email dédié : `bot-exclusion@yowyob.com`

Quand on reçoit une demande :
1. On accuse réception sous 2 heures (pendant heures ouvrables)
2. On arrête tout scraping de ce site sous 24 heures maximum
3. On confirme l'arrêt par email
4. On supprime toutes les données collectées de ce site (sur demande)

Pas de discussion, pas de négociation. Si un site nous demande de partir, on part.

### Conformité légale au Cameroun et en Afrique

**Les lois camerounaises**

On respecte :
- **La loi sur la protection des données personnelles** : On ne collecte aucune donnée personnelle
- **Le Code de la consommation** : Notre comparaison aide les consommateurs, on ne les trompe jamais
- **Les droits de propriété intellectuelle** : On ne reproduisons pas le contenu, on faisons juste des liens vers les sources

**Les règles internationales**

- **RGPD européen** (important car beaucoup de sites qu'on scrape sont européens)
- **Fair Use / Usage loyal** : Notre usage est transformatif (agrégation, comparaison, analyse) et non substitutif
- **Terms of Service** des sites : On les lit et les respecte

### Protection de la vie privée des utilisateurs

**Ce qu'on collecte sur les utilisateurs** (très peu !) :

```
DONNÉES COLLECTÉES :
- Termes de recherche (anonymisés après 30 jours)
- Pays/Ville approximative (pour prioriser sources locales)
- Préférences de filtrage (pour améliorer l'expérience)

DONNÉES JAMAIS COLLECTÉES :
- Nom, prénom, adresse exacte
- Email (sauf si l'utilisateur crée un compte volontairement pour les alertes)
- Numéro de téléphone
- Données de paiement (on gère aucun paiement !)
- Historique d'achat (ça se passe sur les sites marchands, pas chez nous)
- Tracking entre sites
```

**Anonymisation stricte**

Au bout de 30 jours, on anonymise complètement :

```
Jour 0 (recherche) :
{
  "user_session": "abc123xyz",
  "query": "iPhone 14",
  "location": "Yaoundé, CM",
  "timestamp": "2024-12-02T14:30:00Z"
}

Jour 30 (anonymisation automatique) :
{
  "query_hash": "d8e7f9...", // Hash irréversible
  "location_region": "Centre, CM", // Seulement la région
  "timestamp_month": "2024-12" // Seulement le mois
}
```

On garde juste les statistiques agrégées pour améliorer le service, mais impossible de remonter à un utilisateur individuel.

**Cookies et tracking**

On utilise le minimum de cookies :

```
COOKIES ESSENTIELS (obligatoires pour fonctionner) :
- Session utilisateur (expire après 24h)
- Préférences de langue et devise

COOKIES ANALYTIQUES (optionnels)
Voici la suite logique de ce README, dans le même format et style :

## Partenariats et intégrations API avancées

### Stratégie d'intégration progressive avec les marchands

Bien que notre scraping respectueux nous permette de collecter les données publiques, nous visons à développer des **partenariats officiels** avec les principaux marchands camerounais et africains. Ces partenariats offrent des avantages mutuels :

**Pour les marchands** :
- Augmentation du trafic qualifié vers leur site
- Meilleure visibilité dans les comparaisons
- Données structurées et normalisées (moins d'erreurs que le scraping)
- Possibilité de mettre en avant leurs promotions spécifiques

**Pour nous** :
- Accès à des données fraîches et fiables via API
- Pas de risque légal lié au scraping
- Meilleure performance (réponses plus rapides)
- Informations plus détaillées (stocks en temps réel, variantes précises)

**Notre approche progressive** :

```
Niveau 1 : Scraping respectueux (actuel)
↓
Niveau 2 : API publique disponible (si le marchand en a une)
↓
Niveau 3 : Partenariat d'affiliation (liens tracés, pas de commission obligatoire)
↓
Niveau 4 : Partenariat API avancé (accès privilégié, données enrichies)
```

### API Gateway pour marchands partenaires

Nous développons une **API Gateway dédiée aux marchands** qui souhaitent intégrer directement leurs catalogues :

```java
@RestController
@RequestMapping("/api/merchant/v1")
public class MerchantIntegrationController {
    
    /**
     * Endpoint pour que les marchands poussent leurs produits
     * Format standardisé, validation automatique
     */
    @PostMapping("/products")
    public ResponseEntity<?> receiveProductData(
        @RequestBody @Valid List<ProductDTO> products,
        @RequestHeader("X-Merchant-Id") String merchantId,
        @RequestHeader("X-Api-Key") String apiKey
    ) {
        // Authentification et validation
        Merchant merchant = merchantService.authenticate(merchantId, apiKey);
        
        // Traitement asynchrone pour ne pas bloquer le marchand
        productIngestionService.ingestProducts(merchant, products);
        
        return ResponseEntity.accepted().build();
    }
    
    /**
     * Webhook pour notifications en temps réel
     * Ex: changement de prix, rupture de stock
     */
    @PostMapping("/webhooks/price-update")
    public void handlePriceUpdate(
        @RequestBody PriceUpdateEvent event,
        @RequestHeader("X-Webhook-Signature") String signature
    ) {
        // Vérification signature
        if (!webhookService.verifySignature(event, signature, merchant)) {
            throw new UnauthorizedException("Signature invalide");
        }
        
        // Mise à jour immédiate dans notre cache
        cacheService.updateProductPrice(event.getProductId(), event.getNewPrice());
    }
}
```

### Système d'affiliation transparent

Pour les marchands qui le souhaitent, nous mettons en place un **système d'affiliation transparent** :

**Principe** : Quand un utilisateur clique sur un lien vers un marchand partenaire, nous ajoutons un identifiant d'affiliation à l'URL. Si l'utilisateur achète, le marchand nous reverse une petite commission.

**Important** : Cela n'influence PAS nos comparaisons ! Les algorithmes de classement restent objectifs. L'affiliation est simplement un moyen de rémunérer notre service tout en restant gratuit pour les utilisateurs.

**Transparence totale** :
```json
{
  "offers": [
    {
      "merchant": "Jumia Cameroun",
      "price": "650,000 FCFA",
      "merchant_url": "https://jumia.cm/apple-iphone-14...",
      "affiliation": {
        "is_affiliated": true,
        "disclosure": "Ce lien contient un identifiant d'affiliation. Si vous achetez via ce lien, YowYob peut recevoir une petite commission sans surcoût pour vous.",
        "policy_url": "https://yowyob.com/affiliation-policy"
      }
    },
    {
      "merchant": "TechShop Yaoundé",
      "price": "620,000 FCFA", 
      "merchant_url": "https://techshop-cm.com/iphone14",
      "affiliation": {
        "is_affiliated": false,
        "disclosure": "Pas de partenariat d'affiliation avec ce marchand."
      }
    }
  ]
}
```

## Optimisations avancées pour le marché africain

### Gestion des coupures internet et faible bande passante

En Afrique, et particulièrement au Cameroun, la connectivité internet peut être intermittente. Notre système est conçu pour fonctionner même dans ces conditions difficiles :

**1. Cache offline-first** :
```java
@Service
public class OfflineCacheService {
    
    public ComparisonResponse getCachedOrOfflineResponse(String query) {
        // Essayer d'abord le cache standard
        ComparisonResponse response = cacheService.get(query);
        
        if (response != null) {
            return response;
        }
        
        // Si pas en cache et pas de connexion internet
        if (!networkService.isConnected()) {
            // Retourner une version allégée avec données locales
            return getOfflineComparison(query);
        }
        
        return null;
    }
    
    private ComparisonResponse getOfflineComparison(String query) {
        // Utiliser des données pré-téléchargées (produits populaires)
        // Données compressées, minimum d'images
        // Suggestions basées sur historique local
        return OfflineResponse.builder()
            .query(query)
            .offers(getPreloadedOffersForQuery(query))
            .disclaimer("⚠️ Mode hors ligne - Données pouvant être obsolètes")
            .lastUpdated(getLastSyncDate())
            .build();
    }
}
```

**2. Compression adaptative** :
- Connexion 4G rapide → Données complètes, images en qualité normale
- Connexion 3G lente → Données allégées, images compressées
- Connexion Edge très lente → Pas d'images, juste texte, JSON minifié

**3. Service Worker pour PWA** :
```javascript
// Service Worker qui pré-cache les produits populaires
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open('yowyob-popular-v1').then(cache => {
      return cache.addAll([
        '/api/cache/products/top-100',
        '/api/cache/prices/trending',
        '/static/fallback-comparison.json'
      ]);
    })
  );
});

self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request).then(response => {
      // Retourner depuis cache si disponible
      if (response) {
        return response;
      }
      
      // Sinon essayer réseau, avec timeout court
      return fetchWithTimeout(event.request, 5000).catch(() => {
        // Fallback générique si réseau échoue
        return caches.match('/static/fallback-comparison.json');
      });
    })
  );
});
```

### Support des méthodes de paiement locales

Notre système comprend et catégorise les **méthodes de paiement spécifiques à l'Afrique** :

```java
public enum PaymentMethod {
    // International
    CREDIT_CARD("Carte bancaire internationale"),
    PAYPAL("PayPal"),
    
    // Afrique de l'Ouest et Centrale
    MTN_MOBILE_MONEY("MTN Mobile Money"),
    ORANGE_MONEY("Orange Money"),
    MOOV_MONEY("Moov Money"),
    WAVE("Wave"),
    
    // Nigeria
    PAYSTACK("Paystack"),
    FLUTTERWAVE("Flutterwave"),
    
    // Kenya et Afrique de l'Est
    M_PESA("M-Pesa"),
    
    // Autres
    CASH_ON_DELIVERY("Cash à la livraison"),
    BANK_TRANSFER("Virement bancaire"),
    CRYPTO("Cryptomonnaie")
}

@Service
public class PaymentMethodAnalyzer {
    
    public List<PaymentMethod> extractFromDescription(String description) {
        // Analyse textuelle pour détecter les méthodes de paiement
        List<PaymentMethod> methods = new ArrayList<>();
        
        String descLower = description.toLowerCase();
        
        if (descLower.contains("mobile money") || descLower.contains("mtn money")) {
            methods.add(PaymentMethod.MTN_MOBILE_MONEY);
        }
        
        if (descLower.contains("orange money") || descLower.contains("omoney")) {
            methods.add(PaymentMethod.ORANGE_MONEY);
        }
        
        if (descLower.contains("cash on delivery") || descLower.contains("paiement à la livraison")) {
            methods.add(PaymentMethod.CASH_ON_DELIVERY);
        }
        
        return methods;
    }
}
```

**Filtrage par méthode de paiement** :
```
Filtres disponibles :
☑ MTN Mobile Money
☑ Orange Money  
☑ Cash à la livraison
☑ Carte bancaire
☑ PayPal

Résultat : "Montrer seulement les offres acceptant Mobile Money"
```

### Estimation intelligente des frais de douane

Pour les achats internationaux, nous avons développé un **estimateur de frais de douane spécifique à chaque pays africain** :

```java
@Service
public class CustomsDutyCalculator {
    
    private static final Map<String, Map<String, Double>> DUTY_RATES = Map.of(
        "CM", Map.of( // Cameroun
            "electronics", 0.30, // 30% pour l'électronique
            "clothing", 0.20,    // 20% pour les vêtements
            "books", 0.05,       // 5% pour les livres
            "default", 0.25      // 25% par défaut
        ),
        "SN", Map.of( // Sénégal
            "electronics", 0.25,
            "clothing", 0.18,
            "default", 0.20
        ),
        "CI", Map.of( // Côte d'Ivoire
            "electronics", 0.28,
            "default", 0.22
        )
    );
    
    public CustomsEstimate calculateEstimate(
        String countryCode,
        String productCategory,
        double productValueFcfa,
        double shippingCostFcfa
    ) {
        // Récupérer le taux pour cette catégorie dans ce pays
        double dutyRate = DUTY_RATES
            .getOrDefault(countryCode, DUTY_RATES.get("CM"))
            .getOrDefault(productCategory, 0.25);
        
        // Calcul de base
        double baseDuty = (productValueFcfa + shippingCostFcfa) * dutyRate;
        
        // Ajouter frais fixes (dédouanement, etc.)
        double fixedFees = getFixedFees(countryCode);
        
        // Marge d'erreur selon la fiabilité des données
        double margin = baseDuty * 0.15; // ±15%
        
        return CustomsEstimate.builder()
            .estimatedTotal(baseDuty + fixedFees)
            .confidence(0.85) // 85% de confiance
            .rangeMin(baseDuty + fixedFees - margin)
            .rangeMax(baseDuty + fixedFees + margin)
            .breakdown(Map.of(
                "duty", baseDuty,
                "fixed_fees", fixedFees,
                "tax_rate", dutyRate * 100
            ))
            .disclaimer("Estimation indicative. Les frais réels peuvent varier.")
            .build();
    }
}
```

**Affichage dans la comparaison** :
```
📦 Achat international (Amazon France → Cameroun)

Prix produit : 602,000 FCFA
Livraison : 45,000 FCFA
──────────────
Sous-total : 647,000 FCFA

Frais de douane estimés : 
  - Droits d'importation (30%) : 194,100 FCFA
  - Frais de dédouanement : 15,000 FCFA
  - TVA locale (19.25%) : 124,548 FCFA
  ──────────────
  Total estimation : 333,648 FCFA
  (fourchette : 280,000 - 380,000 FCFA)

💰 COÛT TOTAL ESTIMÉ : 980,648 FCFA
   (soit 58% de plus que le prix affiché !)

⚠️ Ces frais sont payables à la réception en FCFA
⚠️ Estimation basée sur la catégorie "électronique"
```

## Analytics et insights pour les marchands

### Tableau de bord analytics pour partenaires

Nous fournissons aux marchands partenaires un **tableau de bord analytics** montrant comment leurs produits apparaissent dans nos comparaisons :

```
📊 Tableau de bord - TechShop Yaoundé
Période : 1-30 Nov 2024

📈 Visibilité générale :
- Apparitions dans les résultats : 1,245 fois
- Clics vers votre site : 342 (taux de clic : 27.5%)
- Position moyenne dans les classements : 2.3/5

💰 Performance par produit :
1. iPhone 14 128GB Noir :
   - Apparitions : 215
   - Clics : 78 (36.3%)
   - Prix moyen chez vous : 620,000 FCFA
   - Prix moyen marché : 650,000 FCFA
   → Votre avantage prix : -4.6%

2. Samsung Galaxy A54 :
   - Apparitions : 189
   - Clics : 42 (22.2%)
   - Votre prix : 225,000 FCFA
   - Prix moyen marché : 235,000 FCFA
   → Votre avantage prix : -4.3%

🎯 Insights pour améliorer :
- Votre iPhone 14 est bien positionné prix
- Votre Samsung A54 manque de visibilité (titre peu descriptif)
- Les photos produits pourraient être améliorées
- Pensez à mentionner "Mobile Money accepté" dans les titres
```

### API d'analytics pour marchands

```java
@RestController
@RequestMapping("/api/analytics/v1")
public class MerchantAnalyticsController {
    
    @GetMapping("/performance")
    public MerchantPerformanceReport getPerformanceReport(
        @RequestParam String merchantId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return analyticsService.generateReport(merchantId, startDate, endDate);
    }
    
    @GetMapping("/competitors")
    public CompetitorAnalysis getCompetitorAnalysis(
        @RequestParam String merchantId,
        @RequestParam String productCategory
    ) {
        return analyticsService.analyzeCompetitors(merchantId, productCategory);
    }
    
    @GetMapping("/price-recommendations")
    public List<PriceRecommendation> getPriceRecommendations(
        @RequestParam String merchantId
    ) {
        return pricingService.generateRecommendations(merchantId);
    }
}
```

### Recommandations de prix intelligentes

Basé sur l'analyse du marché, nous pouvons suggérer aux marchands des ajustements de prix :

```java
@Service
public class PriceRecommendationEngine {
    
    public PriceRecommendation analyzeAndRecommend(
        String merchantId, 
        String productId, 
        double currentPrice
    ) {
        // Analyser la concurrence
        List<CompetitorPrice> competitors = competitorService.getCompetitorPrices(productId);
        
        // Analyser la demande
        DemandMetrics demand = demandService.getDemandMetrics(productId);
        
        // Calculer le prix optimal
        OptimalPrice optimal = calculateOptimalPrice(currentPrice, competitors, demand);
        
        return PriceRecommendation.builder()
            .currentPrice(currentPrice)
            .recommendedPrice(optimal.getPrice())
            .confidence(optimal.getConfidence())
            .expectedImpact(Map.of(
                "clicks_increase", optimal.getExpectedClicksIncrease(),
                "conversion_increase", optimal.getExpectedConversionIncrease(),
                "revenue_impact", optimal.getExpectedRevenueImpact()
            ))
            .reasoning(optimal.getReasoning())
            .build();
    }
    
    private OptimalPrice calculateOptimalPrice(
        double currentPrice,
        List<CompetitorPrice> competitors,
        DemandMetrics demand
    ) {
        // Algorithme de pricing intelligent
        double avgCompetitorPrice = calculateAverage(competitors);
        double minCompetitorPrice = calculateMin(competitors);
        double maxCompetitorPrice = calculateMax(competitors);
        
        // Stratégie selon positionnement
        if (demand.isPriceSensitive()) {
            // Marché très concurrentiel, prix sensibles
            // Recommander un prix légèrement sous la moyenne
            return new OptimalPrice(
                avgCompetitorPrice * 0.95,
                0.85,
                "Marché très concurrentiel. Un prix 5% sous la moyenne devrait augmenter les clics de 15-20%."
            );
        } else {
            // Produit avec peu de concurrence, moins sensible au prix
            // Garder prix actuel ou légère hausse
            return new OptimalPrice(
                currentPrice,
                0.70,
                "Peu de concurrence directe. Le prix actuel est optimal."
            );
        }
    }
}
```

## Expansion vers d'autres pays africains

### Architecture multi-pays

Notre système est conçu pour s'adapter facilement à différents pays africains :

```yaml
# Configuration par pays
countries:
  cm: # Cameroun
    currency: FCFA
    language: fr
    default_sources:
      - jumia.cm
      - kaymu.cm
      - gloopro.cm
    payment_methods:
      - MTN_MOBILE_MONEY
      - ORANGE_MONEY
      - CASH_ON_DELIVERY
    customs_rates:
      electronics: 0.30
      clothing: 0.20
      default: 0.25
      
  sn: # Sénégal
    currency: XOF
    language: fr
    default_sources:
      - jumia.sn
      - kaay.sn
    payment_methods:
      - ORANGE_MONEY
      - WAVE
      - CASH_ON_DELIVERY
    customs_rates:
      electronics: 0.25
      default: 0.20
      
  ng: # Nigeria
    currency: NGN
    language: en
    default_sources:
      - jumia.ng
      - konga.com
    payment_methods:
      - PAYSTACK
      - FLUTTERWAVE
    customs_rates:
      electronics: 0.35
      default: 0.30
      
  ke: # Kenya
    currency: KES
    language: en
    default_sources:
      - jumia.co.ke
    payment_methods:
      - M_PESA
    customs_rates:
      electronics: 0.25
      default: 0.20
```

### Adaptation linguistique et culturelle

**Support multilingue** :
- Français (Cameroun, Sénégal, Côte d'Ivoire...)
- Anglais (Nigeria, Kenya, Ghana...)
- Portugais (Angola, Mozambique...)
- Langues locales (enrichissement progressif)

**Adaptation culturelle des produits** :
```java
@Service
public class CulturalAdaptationService {
    
    public Product adaptForCountry(Product product, String countryCode) {
        Product adapted = product.copy();
        
        switch (countryCode) {
            case "CM":
                // Cameroun : mettre en avant les téléphones compatibles Dual SIM
                if (isDualSim(adapted)) {
                    adapted.addTag("Dual SIM");
                    adapted.increaseRelevance(1.2);
                }
                break;
                
            case "NG":
                // Nigeria : préférer les téléphones avec bon appareil photo
                if (hasGoodCamera(adapted)) {
                    adapted.addTag("Excellent Camera");
                    adapted.increaseRelevance(1.3);
                }
                break;
                
            case "KE":
                // Kenya : mettre en avant compatibilité M-Pesa
                if (supportsMPesa(adapted)) {
                    adapted.addTag("M-Pesa Ready");
                    adapted.increaseRelevance(1.4);
                }
                break;
        }
        
        return adapted;
    }
}
```

### Déploiement régional progressif

**Phase 1 (actuelle)** : Cameroun - Validation du concept
**Phase 2 (Q2 2024)** : Sénégal, Côte d'Ivoire - Expansion Afrique francophone
**Phase 3 (Q3 2024)** : Nigeria, Ghana - Marchés anglophones
**Phase 4 (Q4 2024)** : Kenya, Tanzanie - Afrique de l'Est
**Phase 5 (2025)** : Reste de l'Afrique - Couverture panafricaine

## Sécurité renforcée et conformité

### Protection contre les abus

**Rate limiting par IP** :
```java
@Configuration
public class RateLimitConfig {
    
    @Bean
    public RateLimiter userRateLimiter() {
        return RateLimiter.create(
            RateLimiterConfig.custom()
                .limitForPeriod(100) // 100 requêtes
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(5))
                .build()
        );
    }
    
    @Bean
    public RateLimiter apiRateLimiter() {
        // Plus restrictif pour l'API
        return RateLimiter.create(
            RateLimiterConfig.custom()
                .limitForPeriod(1000)
                .limitRefreshPeriod(Duration.ofHours(1))
                .build()
        );
    }
}

@RestController
public class SearchController {
    
    @RateLimited(name = "userSearch")
    @GetMapping("/api/search")
    public ComparisonResponse search(@RequestParam String q) {
        // Votre logique de recherche
    }
}
```

**Détection de scraping malveillant** :
```java
@Service
public class AbuseDetectionService {
    
    public boolean isPotentialAbuser(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        
        // Vérifier patterns d'abus
        if (isSuspiciousUserAgent(userAgent)) {
            return true;
        }
        
        if (hasHighRequestRate(ip)) {
            return true;
        }
        
        if (isMakingParallelRequests(ip)) {
            return true;
        }
        
        return false;
    }
    
    private boolean isSuspiciousUserAgent(String userAgent) {
        // Détecter les bots malveillants
        List<String> maliciousPatterns = Arrays.asList(
            "python-requests",
            "scrapy",
            "curl",
            "wget",
            "java",
            "node-fetch"
        );
        
        if (userAgent == null) return true;
        
        String uaLower = userAgent.toLowerCase();
        return maliciousPatterns.stream().anyMatch(uaLower::contains);
    }
}
```

### Conformité RGPD et lois locales

**Gestion des données personnelles** :
```java
@Service
public class DataPrivacyService {
    
    @Scheduled(cron = "0 0 3 * * ?") // Tous les jours à 3h du matin
    public void anonymizeOldData() {
        // Anonymiser les recherches vieilles de plus de 30 jours
        searchLogRepository.anonymizeOlderThan(30, ChronoUnit.DAYS);
        
        // Supprimer les logs d'accès vieux de 90 jours
        accessLogRepository.deleteOlderThan(90, ChronoUnit.DAYS);
    }
    
    public UserDataExport exportUserData(String userId) {
        // Exporter toutes les données d'un utilisateur (droit à la portabilité)
        return UserDataExport.builder()
            .searches(searchLogRepository.findByUserId(userId))
            .preferences(userPreferencesRepository.findByUserId(userId))
            .alerts(alertRepository.findByUserId(userId))
            .build();
    }
    
    public void deleteUserData(String userId) {
        // Suppression complète (droit à l'oubli)
        searchLogRepository.deleteByUserId(userId);
        userPreferencesRepository.deleteByUserId(userId);
        alertRepository.deleteByUserId(userId);
    }
}
```

**Politique de cookies transparente** :
```javascript
// Banner de cookies avec options granularies
const cookieConsent = {
  necessary: true, // Toujours activés
  analytics: false, // Optionnel
  personalization: false, // Optionnel
  marketing: false // Optionnel
  
  showBanner() {
    if (!this.hasConsent()) {
      // Afficher bannière détaillée
      this.displayBannerWithOptions();
    }
  },
  
  setPreferences(prefs) {
    // Sauvegarder les préférences
    localStorage.setItem('cookieConsent', JSON.stringify(prefs));
    
    // Appliquer les préférences
    this.applyPreferences(prefs);
  },
  
  applyPreferences(prefs) {
    // Activer/désactiver Google Analytics
    if (prefs.analytics) {
      this.enableGoogleAnalytics();
    } else {
      this.disableGoogleAnalytics();
    }
    
    // Activer/désactiver la personnalisation
    if (prefs.personalization) {
      this.enablePersonalization();
    } else {
      this.disablePersonalization();
    }
  }
};
```

## Monitoring avancé et observabilité

### Métriques business critiques

En plus des métriques techniques, nous suivons des **métriques business** :

```java
@Service
public class BusinessMetricsService {
    
    @Timed(value = "comparison.success.rate", description = "Taux de succès des comparaisons")
    @Metered(value = "comparison.requests", description = "Nombre de requêtes de comparaison")
    public ComparisonResponse compareProducts(String query, User user) {
        // Logique de comparaison
    }
    
    @Scheduled(fixedRate = 60000) // Toutes les minutes
    public void collectBusinessMetrics() {
        // Taux de clic global
        double globalCtr = clickRepository.getClickThroughRate();
        Metrics.gauge("business.ctr.global", globalCtr);
        
        // Taux de conversion estimé (redirections qui mènent à des achats)
        double estimatedConversion = estimateConversionRate();
        Metrics.gauge("business.conversion.estimated", estimatedConversion);
        
        // Satisfaction utilisateur (basé sur les retours)
        double userSatisfaction = calculateSatisfactionScore();
        Metrics.gauge("business.satisfaction.score", userSatisfaction);
        
        // Économies réalisées par les utilisateurs
        double totalSavings = calculateTotalSavings();
        Metrics.gauge("business.savings.total", totalSavings);
    }
}
```

### Dashboards Grafana complets

**Dashboard technique** :
- Latence par endpoint
- Taux d'erreur par service
- Utilisation CPU/mémoire
- Taux de cache hit/miss
- État des sources externes

**Dashboard business** :
- Nombre de comparaisons par jour
- Taux de clic moyen
- Produits les plus comparés
- Marchands les plus populaires
- Économies moyennes par utilisateur

**Dashboard géographique** :
- Requêtes par pays
- Requêtes par ville (Cameroun)
- Sources locales vs internationales
- Conversion par région

### Alertes intelligentes

```yaml
alerting:
  rules:
    - alert: PriceAnomalyDetected
      expr: |
        avg(price_change_percentage{source="jumia.cm"}) > 20
      for: 5m
      annotations:
        summary: "Anomalie de prix détectée chez Jumia"
        description: |
          Les prix chez Jumia ont changé de plus de 20% en 5 minutes.
          Vérifier si c'est une promotion flash ou une erreur.
          
    - alert: LowAfricanMerchantVisibility
      expr: |
        avg(merchant_rank{continent="africa"}) > 3.5
      for: 1h
      annotations:
        summary: "Faible visibilité des marchands africains"
        description: |
          Les marchands africains sont en moyenne au-delà de la 3ème position.
          Vérifier l'algorithme de ranking.
          
    - alert: HighInternationalShippingCosts
      expr: |
        avg(shipping_cost_percentage{source=~"amazon|ebay"}) > 0.4
      for: 2h
      annotations:
        summary: "Frais de port internationaux élevés"
        description: |
          Les frais de port internationaux dépassent 40% du prix des produits.
          Avertir les utilisateurs dans les comparaisons.
```

## Évolution future et vision

### Roadmap technologique 2025-2026

**Q1 2025 : Consolidation**
- [ ] Amélioration algorithmes de matching (95%+ précision)
- [ ] API publique beta pour développeurs
- [ ] Application mobile progressive (PWA)

**Q2 2026 : Intelligence**
- [ ] Recommandations personnalisées (machine learning)
- [ ] Prédiction de prix avancée
- [ ] Chatbot d'aide à l'achat

**Q3 2026 : Expansion**
- [ ] Support 5 pays africains additionnels
- [ ] Intégration 20+ nouveaux marchands
- [ ] API marchands avancée

**Q4 2026 : Innovation**
- [ ] Comparaison visuelle IA (photos produits)
- [ ] Alertes prix en temps réel (push notifications)
- [ ] Marketplace virtuelle (vue unifiée)

**2026 : Vision panafricaine**
- [ ] Couverture 15+ pays africains
- [ ] 100+ marchands intégrés
- [ ] Solution B2B pour entreprises
- [ ] Certification et partenariats institutionnels

### Vision à long terme

Notre vision est de devenir **le comparateur de référence pour l'Afrique**, en résolvant les défis uniques du e-commerce sur le continent :

1. **Démocratiser l'accès aux meilleurs prix** : Aider chaque Africain à acheter au meilleur prix, peu importe où il se trouve.

2. **Stimuler le commerce intra-africain** : Mettre en avant les marchands locaux et favoriser les échanges entre pays africains.

3. **Éduquer les consommateurs** : Aider à comprendre les coûts réels, les garanties, les droits des consommateurs.

4. **Soutenir les petites entreprises** : Donner de la visibilité aux petits marchands locaux qui n'ont pas les moyens de se faire connaître.

5. **Créer un écosystème vertueux** : Connecter intelligemment acheteurs et vendeurs, avec transparence et confiance.

## Conclusion

Le **Shop Service YowYob** est bien plus qu'un simple comparateur de prix. C'est une **plateforme d'intelligence comparative conçue pour et par l'Afrique**.

**Nos principes fondamentaux** :
1. **Neutre et objectif** : Nos comparaisons ne sont jamais influencées par des partenariats commerciaux.
2. **Respectueux et légal** : Nous respectons les sites web que nous analysons et les lois en vigueur.
3. **Utile et pertinent** : Nous nous adaptons aux réalités africaines (FCFA, Mobile Money, douanes...).
4. **Transparent et honnête** : Nous expliquons comment nous fonctionnons et ce que nous faisons des données.

**Notre promesse** :
> "Nous vous aidons à trouver le meilleur produit au meilleur prix, en toute transparence, sans jamais intervenir dans votre achat."

**Prochaine étape** :
L'expansion à d'autres pays africains et le développement de fonctionnalités encore plus intelligentes pour aider les consommateurs africains à faire des choix éclairés.

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

NB : J'ai a lesprit que l'on ne va pas coder pour un marchant/commercant (site comericial ou autre) en particulier ce qui nous limitera , donc envisager une approche plus gllobale , plus generale ... (sous réserve de conseils experts)

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

**Le Shop Service** est un pont, pas une destination. Notre rôle s'arrête à la comparaison. L'achat appartient à vous et au marchand que vous choisissez ; *"Comparer pour mieux acheter, acheter en toute confiance"* .
