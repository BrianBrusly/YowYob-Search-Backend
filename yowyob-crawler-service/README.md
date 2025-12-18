# YowYob Search - Crawler Service (Module de Crawling Web Intelligent)

## Vue d'ensemble et présentation du service

Bienvenue dans la documentation complète du Crawler Service, l'un des composants les plus fascinants et techniquement exigeants de notre plateforme YowYob Search. Ce service représente le cœur battant de notre moteur de recherche - c'est lui qui parcourt inlassablement le web pour découvrir, extraire et indexer le contenu qui alimentera ensuite nos résultats de recherche.

Imaginez le web comme une immense bibliothèque avec des milliards de livres, constamment en mouvement, où de nouveaux ouvrages apparaissent chaque seconde tandis que d'autres disparaissent ou changent de place. Notre Crawler Service est comme une équipe infatigable de bibliothécaires robots qui explorent méthodiquement cette bibliothèque vivante, cataloguent chaque ouvrage qu'ils trouvent, et tiennent à jour un index permettant de retrouver instantanément n'importe quelle information.

Ce n'est pas simplement un robot qui télécharge bêtement toutes les pages web qu'il trouve. C'est un système sophistiqué qui doit naviguer dans un environnement hostile et imprévisible : certains sites web tentent délibérément de bloquer les robots, d'autres sont trop lents, certains contiennent du contenu de mauvaise qualité ou du spam, et le web dans son ensemble est tellement vaste qu'il serait impossible de tout indexer même avec des milliers de serveurs. Notre Crawler doit donc être intelligent, respectueux, efficace et sélectif.

### Pourquoi avons-nous besoin d'un Crawler sophistiqué ?

Dans les premiers jours d'Internet, on aurait pu simplement télécharger toutes les pages qu'on trouvait et les indexer. Mais aujourd'hui, le web est devenu extraordinairement complexe et plusieurs défis se posent :

**Le volume astronomique** : Il existe plus de 5 milliards de pages web indexables. Si nous devions les crawler toutes en une fois, cela prendrait des années et coûterait des millions en infrastructure. Nous devons donc prioriser intelligemment : crawler d'abord les pages les plus importantes, les plus populaires, ou les plus pertinentes pour notre audience camerounaise.

**La politesse et l'éthique** : Quand nous crawlons un site web, nous utilisons ses ressources (bande passante, CPU, mémoire). Si nous envoyons des milliers de requêtes par seconde, nous pouvons littéralement faire tomber un petit site web. C'est pour cela que nous avons implémenté ce qu'on appelle "politeness policies" - des règles de courtoisie qui garantissent que notre robot ne surcharge jamais les sites qu'il visite. Nous respectons également le fichier `robots.txt` que les propriétaires de sites utilisent pour indiquer quelles parties de leur site ne doivent pas être crawlées.

**La qualité du contenu** : Tous les contenus ne se valent pas. Une page spam remplie de publicités et de liens cassés n'a pas sa place dans notre index. Une page qui est presque identique à une autre page que nous avons déjà indexée (contenu dupliqué) ne mérite pas non plus d'être stockée. Notre Crawler inclut donc des mécanismes sophistiqués pour évaluer la qualité du contenu et ne garder que ce qui a vraiment de la valeur pour nos utilisateurs.

**Les changements constants** : Le web n'est pas statique. Des pages disparaissent, d'autres sont mises à jour, de nouvelles apparaissent. Un bon moteur de recherche doit refléter l'état actuel du web, pas celui d'il y a six mois. Notre Crawler implémente donc un système de "recrawling" qui revisite périodiquement les pages déjà indexées pour détecter les changements.

**La diversité des formats** : Le web ne se limite pas au HTML. Il y a des PDF, des documents Word, des présentations PowerPoint, des feuilles Excel. Notre Crawler utilise Apache Tika, une bibliothèque incroyablement puissante capable d'extraire le texte de plus de 1000 formats de fichiers différents. Que ce soit un vieux fichier WordPerfect des années 90 ou un document Google Docs moderne, Tika sait le lire.

### L'architecture conceptuelle de notre Crawler

Notre Crawler n'est pas un simple script qui boucle sur des URLs. C'est une architecture distribuée sophistiquée composée de plusieurs sous-systèmes qui travaillent ensemble de manière orchestrée. Laissez-moi vous expliquer les composants principaux et comment ils interagissent.

**Le Scheduler (Planificateur)** : C'est le chef d'orchestre. Il décide quand lancer des crawls, combien de ressources allouer, et quelles parties du web explorer en priorité. Il peut lancer des crawls ponctuels (à la demande) ou périodiques (par exemple, recrawler tous les sites d'actualités camerounais toutes les 6 heures). Le Scheduler utilise Quartz, une bibliothèque Java de scheduling extrêmement robuste utilisée par des milliers d'entreprises dans le monde.

**L'URL Frontier (File d'attente d'URLs)** : C'est la liste de toutes les URLs que nous devons encore visiter. Mais ce n'est pas une simple liste - c'est une file à priorités sophistiquée stockée dans PostgreSQL. Les URLs les plus importantes (sites d'actualités, sites gouvernementaux, sites très populaires) sont traitées en premier. Les URLs moins importantes attendent leur tour. Cette file peut contenir des millions d'URLs en attente, toutes organisées et priorisées.

**Le YowYobBot** : C'est notre robot crawler proprement dit. Il prend des URLs depuis la Frontier, télécharge les pages web, extrait le contenu, découvre de nouveaux liens, et envoie tout ça au système d'indexation. Mais avant de visiter une page, il vérifie consciencieusement le fichier robots.txt du site (ces règles que les propriétaires de sites définissent pour dire aux robots ce qu'ils peuvent et ne peuvent pas crawler), respecte les délais de politesse entre deux requêtes au même site, et gère intelligemment les erreurs et les timeouts.

**Le Content Parser (Analyseur de Contenu)** : Une fois qu'une page est téléchargée, il faut en extraire le contenu utile. Ce n'est pas trivial ! Une page web moderne contient du HTML, du CSS, du JavaScript, des images, des vidéos, de la publicité, des menus de navigation, des footers... Le Content Parser utilise JSoup pour analyser le HTML et identifier le contenu principal (l'article, le texte réellement intéressant) en ignorant tout le bruit autour. Pour les documents non-HTML (PDF, Word, etc.), il délègue à Apache Tika.

**Le Quality Filter (Filtre de Qualité)** : Avant d'indexer du contenu, nous le passons à travers plusieurs filtres de qualité. Ce filtre détecte et rejette le spam (pages avec trop de publicités, pages avec du contenu autogénéré sans valeur), les pages presque vides (moins de 100 mots de texte), les pages dupliquées (nous utilisons un algorithme de "shingling" pour calculer la similarité entre pages), et les pages dans des langues non supportées (pour l'instant nous nous concentrons sur le français et l'anglais).

**L'Indexing Service (Service d'Indexation)** : Une fois que le contenu a passé tous les filtres, il est envoyé à Elasticsearch pour indexation. Mais nous ne l'envoyons pas page par page - ce serait inefficace. Nous utilisons le système de "bulk indexing" d'Elasticsearch qui nous permet d'envoyer 1000 documents à la fois. C'est beaucoup plus rapide et utilise beaucoup moins de ressources réseau.

**Le système d'événements Kafka** : Tout ce qui se passe dans le Crawler génère des événements : "nouvelle page découverte", "page crawlée avec succès", "erreur lors du crawl", "contenu indexé", etc. Ces événements sont publiés sur Kafka, notre bus de messages distribué. D'autres services peuvent s'abonner à ces événements. Par exemple, le Stats Service s'abonne pour tenir à jour les statistiques de crawling, et le Notification Service pourrait envoyer une alerte si le taux d'erreur devient trop élevé.

## Architecture technique détaillée

### Technologies et frameworks utilisés

Notre Crawler Service est construit avec un stack technologique moderne et éprouvé qui nous donne à la fois puissance et flexibilité. Voici les choix technologiques que nous avons faits et surtout pourquoi nous les avons faits :

**Java 21 avec Virtual Threads** : Nous utilisons la toute dernière version LTS de Java, qui introduit les Virtual Threads (aussi appelés Project Loom). C'est une révolution pour les applications comme la nôtre qui doivent gérer des milliers de connexions simultanées. Traditionnellement, créer un thread Java coûte cher en mémoire (environ 1MB par thread), ce qui limite le nombre de crawls simultanés qu'on peut faire. Avec les Virtual Threads, on peut créer des millions de threads légers qui ne coûtent que quelques kilobytes chacun. Concrètement, cela signifie que nous pouvons crawler des milliers de pages simultanément avec une seule instance de notre service.

**Spring Boot 3.2** avec **Spring WebFlux** : Au lieu du traditionnel Spring MVC (qui est bloquant), nous utilisons WebFlux, la stack réactive de Spring. Qu'est-ce que cela signifie concrètement ? Imaginez un serveur de restaurant. Avec une approche bloquante (Spring MVC), chaque serveur prend une commande, va en cuisine, attend que le plat soit prêt sans rien faire d'autre, puis revient servir. Avec une approche réactive (WebFlux), le serveur prend plusieurs commandes, les transmet toutes à la cuisine, et s'occupe d'autres tâches en attendant. Quand un plat est prêt, il le sert immédiatement. C'est beaucoup plus efficace, surtout pour des opérations I/O intensives comme le crawling web où on passe beaucoup de temps à attendre les réponses des serveurs web.

**JSoup 1.16** : C'est LA bibliothèque Java de référence pour parser du HTML. Elle est incroyablement robuste et tolérante aux erreurs - même face à du HTML malformé ou complètement cassé (et croyez-moi, il y a beaucoup de HTML cassé sur le web), JSoup arrive à extraire du sens. Elle supporte les sélecteurs CSS, ce qui rend l'extraction de contenu très intuitive. Par exemple, pour extraire tous les liens d'une page, on écrit simplement `document.select("a[href]")`. C'est à la fois puissant et élégant.

**Apache Tika 2.9** : Tika est un projet de la Apache Software Foundation qui peut détecter et extraire le contenu de plus de 1000 types de fichiers. PDF avec du texte scanné ? Tika peut utiliser l'OCR. Document Word avec des macros et du formatage complexe ? Pas de problème. Email avec des pièces jointes ? Tika peut extraire le tout. Pour nous, c'est essentiel car nous voulons indexer non seulement les pages HTML mais aussi tous les documents que les gens mettent en ligne - rapports gouvernementaux en PDF, présentations académiques en PowerPoint, feuilles de calcul d'entreprises, etc.

**Quartz Scheduler 2.3** : C'est le système de planification de tâches le plus mature et robuste de l'écosystème Java. Utilisé dans des systèmes critiques depuis plus de 20 ans. Quartz nous permet de définir des jobs complexes (par exemple : "Crawle tous les sites d'actualités camerounais toutes les 6 heures, mais seulement entre 6h et 22h, et skip le dimanche"). Il gère automatiquement les situations où un job prend plus de temps que prévu, où le serveur redémarre en plein milieu d'un job, ou où plusieurs instances du service doivent coordonner leurs tâches. C'est de la fiabilité industrielle.

**PostgreSQL 15** pour la persistence : Nous stockons toutes nos métadonnées dans PostgreSQL - la liste des URLs à crawler (URL Frontier), l'historique des crawls, les informations sur les robots.txt, les statistiques par domaine, etc. Pourquoi PostgreSQL plutôt qu'une base NoSQL ? Parce que nos données ont une structure bien définie et des relations importantes (par exemple, une page appartient à un domaine, qui a un fichier robots.txt associé). PostgreSQL excelle dans la gestion de ces relations complexes, et sa robustesse légendaire nous garantit qu'on ne perdra jamais de données.

**Elasticsearch 8.x** pour l'indexation : Une fois qu'on a extrait le contenu d'une page, on doit le rendre cherchable. Elasticsearch est le meilleur moteur de recherche full-text au monde. Il peut indexer des millions de documents et répondre à des requêtes complexes en quelques millisecondes. Il supporte la recherche floue (typos), les synonymes, le ranking par pertinence, les agrégations statistiques, et des dizaines d'autres fonctionnalités que nous utilisons.

**Redis 7** pour le caching : Nous utilisons Redis pour deux choses principales. D'abord, pour mettre en cache les fichiers robots.txt - au lieu de les télécharger à chaque fois qu'on visite un site, on les garde en cache pendant 24 heures. Ensuite, pour détecter rapidement les URLs déjà visitées - avant d'ajouter une URL à la Frontier, on vérifie dans Redis si on l'a déjà vue. Redis est parfait pour ça car il est extrêmement rapide (il garde tout en mémoire) et supporte nativement des structures de données avancées comme les ensembles (sets) qui sont idéaux pour notre cas d'usage.

**Apache Kafka 3.5** pour le messaging événementiel : Kafka est notre système nerveux distribué. Chaque événement important (nouvelle page découverte, crawl réussi, erreur, contenu indexé) est publié sur Kafka. Cela découple nos composants - le Crawler n'a pas besoin de connaître tous les systèmes qui s'intéressent à ses événements. Si demain nous voulons ajouter un nouveau service qui écoute les événements de crawl, on n'a pas à modifier le Crawler. C'est l'essence même d'une architecture microservices réussie.

### Architecture en couches : séparation des responsabilités

Notre Crawler est organisé en couches bien définies, chacune ayant une responsabilité précise. Cette organisation n'est pas juste pour faire joli dans les diagrammes UML - c'est fondamental pour la maintenabilité et l'évolution du système.

**La couche Controller (API REST)** : C'est l'interface publique du Crawler. Elle expose des endpoints REST qui permettent de contrôler le Crawler depuis l'extérieur. Par exemple, `POST /api/crawler/jobs` pour lancer un nouveau job de crawl, `GET /api/crawler/jobs/{id}` pour voir l'état d'un job en cours, `DELETE /api/crawler/jobs/{id}` pour l'annuler. Ces endpoints sont sécurisés - seuls les administrateurs peuvent les appeler. La couche Controller ne contient aucune logique métier, elle se contente de valider les entrées, d'appeler la couche Service appropriée, et de formater les réponses.

**La couche Service (logique métier)** : C'est ici que vit l'intelligence du système. Le `CrawlOrchestrator` coordonne l'ensemble du processus de crawl. Il interroge l'URL Frontier pour obtenir les prochaines URLs à crawler, lance les workers de crawl en parallèle (en respectant les limites de politesse), gère les erreurs et les retries, collecte les statistiques, et publie les événements sur Kafka. Le `IndexingService` s'occupe d'envoyer le contenu extrait vers Elasticsearch, en gérant intelligemment le batching et les erreurs. Le `DomainManager` gère tout ce qui concerne les domaines - fichiers robots.txt, délais de politesse, statistiques par domaine, etc.

**La couche Crawler (workers de crawl)** : C'est le cœur technique du système. Le `YowYobBot` est le composant qui effectue réellement le travail de crawl. Il prend une URL, vérifie les permissions dans robots.txt, télécharge la page (avec timeout et gestion des erreurs), détecte automatiquement l'encodage (UTF-8, ISO-8859-1, etc.), extrait le contenu avec JSoup pour les pages HTML ou Tika pour les autres formats, découvre tous les liens dans la page et les ajoute à la Frontier si c'est pertinent, analyse la qualité du contenu, et enfin transmet le contenu valide au service d'indexation.

**La couche Repository (accès aux données)** : C'est notre interface avec la couche de persistance. Nous utilisons Spring Data JPA qui nous génère automatiquement la plupart du code d'accès à la base de données. Le `CrawlJobRepository` gère les jobs de crawl, le `URLQueueRepository` gère la Frontier, le `CrawledPageRepository` garde l'historique de toutes les pages crawlées, et le `RobotsTxtRepository` stocke et retrieve les fichiers robots.txt. Cette séparation est cruciale - si demain nous décidons de changer PostgreSQL pour une autre base de données, nous n'aurons à modifier que cette couche, pas toute l'application.

**La couche Model (domaine métier)** : Ce sont nos entités JPA (Java Persistence API) qui représentent notre modèle de domaine. `CrawlJob` représente un job de crawl avec ses paramètres (URLs de départ, profondeur max, filtres, etc.) et son statut. `URLQueue` représente une entrée dans notre file d'attente avec sa priorité, son statut, et ses métadonnées. `CrawledPage` représente une page qui a été crawlée avec toutes ses informations (URL, contenu, métadonnées HTTP, timestamp, statut, erreurs éventuelles). `RobotsTxt` représente les règles d'un fichier robots.txt parsées et prêtes à être consultées.

**La couche DTO (Data Transfer Objects)** : Quand on expose des données via notre API REST, on ne veut pas exposer directement nos entités JPA (ça créerait un couplage fort, et on exposerait potentiellement des détails internes qu'on ne veut pas rendre publics). On utilise donc des DTOs - des objets simples spécialement conçus pour transférer des données. Par exemple, `CrawlJobDTO` contient uniquement les informations qu'un client de l'API a besoin de connaître sur un job, pas tous les détails internes. La conversion entre entités et DTOs est gérée par MapStruct, un framework qui génère automatiquement le code de mapping, ce qui nous évite d'écrire du code boilerplate ennuyeux.

## Le système d'URL Frontier : gestion intelligente de la file d'attente

L'URL Frontier est probablement le composant le plus critique de notre Crawler. C'est elle qui détermine dans quel ordre nous visitons le web, et une mauvaise stratégie ici peut complètement ruiner l'efficacité de tout le système.

### La structure de la Frontier

Notre Frontier n'est pas une simple file FIFO (First In First Out). C'est une **file à priorités multi-niveaux** avec plusieurs queues internes organisées par ordre d'importance. Voici comment nous l'avons architecturée :

**Queue de priorité critique (Priority 1)** : Cette queue contient les URLs qui doivent absolument être crawlées immédiatement. Par exemple, si un utilisateur soumet manuellement une URL via notre interface admin en disant "indexe cette page maintenant", elle va dans la queue Priority 1. De même, les pages d'actualités des grands médias camerounais sont dans cette queue car on veut les indexer le plus rapidement possible après leur publication.

**Queue de priorité haute (Priority 2)** : Ici on trouve les sites web importants qui changent fréquemment - sites gouvernementaux, universités, grandes entreprises, sites e-commerce populaires. On veut les crawler souvent (disons une fois par jour) pour garder notre index à jour, mais ce n'est pas ultra-urgent.

**Queue de priorité normale (Priority 3)** : C'est la majorité du web - des millions de sites de petite et moyenne taille. Blogs personnels, sites d'associations, petits commerces, etc. On les crawle périodiquement (disons une fois par semaine ou par mois) mais ils ne monopolisent pas nos ressources.

**Queue de priorité basse (Priority 4)** : Pages découvertes mais jugées peu importantes - par exemple des pages de tags, des archives anciennes, des pages générées automatiquement. On les crawlera éventuellement si on a du temps de calcul disponible, mais ce n'est vraiment pas une priorité.

Cette organisation nous permet d'être à la fois réactifs (pour les contenus importants) et exhaustifs (on finit par crawler même les pages moins importantes). Le Crawler tire toujours d'abord de la queue de plus haute priorité non vide.

### L'algorithme de priorisation

Mais comment décidons-nous de la priorité d'une URL ? Nous utilisons un algorithme de scoring sophistiqué qui prend en compte plusieurs facteurs :

**PageRank du domaine** : Nous calculons un score de popularité pour chaque domaine basé sur le nombre et la qualité des liens qu'il reçoit depuis d'autres sites. Un domaine avec un PageRank élevé voit ses pages priorisées. Par exemple, un article sur `Wikipedia.org` aura une priorité plus haute qu'une page sur un blog inconnu.

**Fraîcheur du contenu** : Les pages qui changent souvent doivent être recrawlées plus fréquemment. Nous gardons des statistiques sur la fréquence de changement de chaque page. Un site d'actualités qui publie 50 articles par jour aura un score de fraîcheur élevé. Un site vitrine d'entreprise qui ne change que quelques fois par an aura un score bas.

**Profondeur dans le graphe de liens** : Plus une page est "profonde" (beaucoup de clics depuis la homepage), moins elle est prioritaire. La plupart des contenus importants sont à 2-3 clics de la page d'accueil. Une page à 10 clics de profondeur est probablement peu importante.

**Type de contenu** : Nous donnons plus de priorité à certains types de contenu. Par exemple, les articles de blog sont plus prioritaires que les pages de mentions légales. Les pages de produits e-commerce sont plus prioritaires que les pages de catégories. Les PDF de rapports officiels sont très prioritaires.

**Signaux de qualité** : Nous avons un score de qualité estimé pour chaque domaine basé sur des métriques comme le ratio texte/code, la présence de publicités, les signaux de spam, etc. Les domaines de haute qualité sont prioritaires.

Le score final est une combinaison pondérée de tous ces facteurs :

```
Score = 0.30 × PageRank
      + 0.25 × FreshnessScore
      + 0.20 × (1 / Depth)
      + 0.15 × ContentTypeScore
      + 0.10 × QualityScore
```

Les poids ont été calibrés empiriquement après des mois de tests et d'ajustements.

### Déduplication intelligente

Un problème majeur dans le crawling web est la déduplication. Le web est plein d'URLs différentes qui pointent vers le même contenu. Par exemple :

```
https://example.com/article?id=123
https://example.com/article?id=123&utm_source=facebook
https://example.com/article?id=123&utm_source=twitter
https://www.example.com/article?id=123
http://example.com/article?id=123
```

Toutes ces URLs pointent probablement vers le même article, mais elles sont techniquement différentes. Si nous les crawlions toutes, nous gaspillerions énormément de ressources.

Notre stratégie de déduplication se fait à plusieurs niveaux :

**Normalisation d'URL** : Avant d'ajouter une URL à la Frontier, nous la normalisons. On enlève les paramètres de tracking (utm_source, utm_medium, etc.), on convertit tout en minuscules, on ajoute www si manquant (ou on l'enlève selon le canonical), on trie les paramètres de query dans un ordre standard. Après normalisation, les 5 URLs ci-dessus deviendraient une seule : `https://example.com/article?id=123`.

**Bloom Filter** : Avant même de toucher à la base de données, nous utilisons un Bloom Filter en mémoire qui nous dit instantanément si une URL a déjà été vue. Un Bloom Filter est une structure de données probabiliste très efficace en mémoire qui peut répondre "cette URL n'a certainement jamais été vue" ou "cette URL a probablement été vue". Il peut avoir des faux positifs (dire qu'une URL a été vue alors que non) mais jamais de faux négatifs. Pour notre cas d'usage c'est parfait - si le Bloom Filter dit "jamais vue", on peut l'ajouter directement. S'il dit "probablement vue", on vérifie dans la base de données. Avec notre Bloom Filter configuré à 1% de taux de faux positifs, on économise 99% des requêtes à la base de données.

**Hash de contenu** : Même après la déduplication d'URLs, on peut avoir des pages avec du contenu identique mais des URLs différentes (syndication, mirrors, etc.). Après avoir crawlé une page, nous calculons un hash SHA-256 de son contenu. Si ce hash existe déjà dans notre base, nous ne réindexons pas la page. On garde juste une référence "l'URL B a le même contenu que l'URL A".

Cette triple stratégie nous permet de réduire drastiquement le travail inutile. Dans nos tests, la déduplication élimine environ 40% des URLs potentielles.

### Politeness policies : être un citoyen respectueux du web

C'est peut-être la partie la plus importante éthiquement. Nous avons le pouvoir technique de crawler des milliers de pages par seconde, mais cela ne signifie pas que nous devons le faire. Voici nos règles strictes de politesse :

**Respect du robots.txt** : C'est la règle numéro 1. Le fichier robots.txt est un standard établi depuis 1994 qui permet aux propriétaires de sites de communiquer avec les robots. Nous téléchargeons et respectons scrupuleusement ce fichier pour chaque domaine. Si un site dit "ne crawle pas /admin/", nous ne le faisons jamais, même si nous avons techniquement accès. Si un site dit "attends au moins 10 secondes entre deux requêtes", nous attendons 10 secondes.

**Délai de politesse configurable** : Par défaut, nous attendons 1 seconde entre deux requêtes au même domaine. C'est largement au-dessus de ce que la plupart des robots respectent (beaucoup ne font que 0.1 seconde), mais nous préférons être plus respectueux que nécessaire. Pour les gros sites qui peuvent gérer la charge (Google, Facebook, Wikipedia), nous descendons à 0.5 seconde. Pour les petits sites ou ceux qui nous l'ont demandé explicitement, nous montons jusqu'à 5 secondes.

**Limitation du nombre de connexions simultanées** : Même avec nos délais de politesse, si nous ouvrions 1000 connexions simultanées vers le même serveur, nous le surchargerions. Nous limitons donc à maximum 5 connexions simultanées par domaine. Si nous avons 100 pages à crawler sur example.com, nous ne les crawlons pas toutes d'un coup, mais 5 à la fois.

**Identification claire** : Notre User-Agent (l'identifiant que nous envoyons avec chaque requête HTTP) est explicite : `Mozilla/5.0 (compatible; YowYobBot/1.0; +https://yowyob.com/bot)`. N'importe quel webmaster peut voir dans ses logs que c'est notre bot qui visite son site. La page `https://yowyob.com/bot` explique qui nous sommes, ce que nous faisons, et comment nous contacter pour être retiré de notre index ou demander des ajustements.

**Respect des heures de pointe** : Nous avons identifié les heures de pointe pour les sites camerounais (typiquement 9h-17h en semaine) et nous réduisons notre intensité de crawl pendant ces périodes. Les crawls intensifs sont programmés la nuit et le week-end quand les serveurs sont moins sollicités par les utilisateurs réels.

**Rate limiting adaptatif** : Si nous détectons qu'un site répond lentement ou renvoie des erreurs, nous réduisons automatiquement notre taux de crawl pour ce domaine. Si nous recevons un code 429 (Too Many Requests) ou 503 (Service Unavailable), nous stoppons immédiatement le crawl de ce domaine pendant une heure et réessayons plus tard.

Ces politiques font de nous des "bons citoyens" du web. Nous n'avons jamais reçu de plainte de webmasters se plaignant que notre bot les surcharge, et plusieurs nous ont même contactés pour nous remercier d'être aussi respectueux.

## Le processus de crawl : du début à la fin

Suivons ensemble le parcours complet d'une page web depuis sa découverte jusqu'à son indexation dans Elasticsearch. Cela vous donnera une compréhension profonde de tous les mécanismes à l'œuvre.

### Phase 1 : Découverte et ajout à la Frontier

Tout commence quand nous découvrons une nouvelle URL. Il y a plusieurs sources possibles de découverte :

**Seeds (URLs de départ)** : Quand nous lançons un nouveau crawl, nous partons d'une liste de "seed URLs" - typiquement des pages d'accueil de sites importants. Par exemple, si nous voulons crawler les sites d'actualités camerounais, nos seeds seraient `https://www.cameroon-tribune.cm`, `https://www.crtv.cm`, etc.

**Liens découverts** : C'est la source principale. Quand nous crawlons une page, nous extrayons tous ses liens (balises `<a href="...">`) et nous les ajoutons à la Frontier. C'est ainsi que nous découvrons progressivement tout un site web, puis d'autres sites via les liens sortants.

**Soumissions manuelles** : Via notre interface admin, les administrateurs peuvent soumettre manuellement des URLs à crawler. C'est utile pour indexer rapidement des sites nouveaux ou important qui ne sont pas encore dans notre graphe de liens.

**Sitemaps XML** : Beaucoup de sites web fournissent un fichier sitemap.xml qui liste toutes leurs URLs importantes. Nous téléchargeons et parsons ces sitemaps pour découvrir massivement des URLs. Un seul sitemap peut contenir des milliers d'URLs.

Une fois qu'une URL est découverte, voici ce qui se passe :

1. **Normalisation** : L'URL est normalisée selon nos règles (minuscules, enlever les paramètres de tracking, etc.)
2. **Vérification Bloom Filter** : On vérifie si l'URL a déjà été vue. Si oui, on arrête là.

3. **Calcul de priorité** : On calcule le score de priorité selon notre algorithme de scoring.

4. **Vérification des limites** : On vérifie qu'on n'a pas déjà trop d'URLs en attente pour ce domaine (pour éviter qu'un seul gros site monopolise la Frontier).

5. **Insertion dans la Frontier** : L'URL est insérée dans PostgreSQL dans la bonne queue de priorité avec toutes ses métadonnées.

6. **Événement Kafka** : On publie un événement `url-discovered` sur Kafka pour informer les autres systèmes.

### Phase 2 : Extraction depuis la Frontier et préparation

Le `CrawlOrchestrator` tourne en boucle et extrait régulièrement des batches d'URLs depuis la Frontier. Le processus est le suivant :

1. **Extraction par batch** : On extrait 100 URLs à la fois (configuré dans `crawler.batch-size`). Pourquoi par batch ? C'est beaucoup plus efficace que de faire 100 requêtes SQL individuelles.

2. **Groupement par domaine** : On groupe ces URLs par domaine. Si on a 20 URLs de example.com et 30 de another-site.com, on les sépare en deux groupes.

3. **Vérification des slots disponibles** : Pour chaque domaine, on vérifie combien de connexions simultanées nous avons déjà ouvertes. Si example.com a déjà 5 connexions ouvertes (notre maximum), on met ces URLs de côté pour plus tard.

4. **Vérification du dernier crawl** : On vérifie quand nous avons crawlé pour la dernière fois ce domaine. Si c'était il y a moins d'une seconde (notre délai de politesse), on attend.

5. **Téléchargement du robots.txt** : Si nous n'avons pas le robots.txt de ce domaine en cache, on le télécharge et on le parse. On le garde en cache pendant 24 heures.

6. **Vérification des permissions** : On vérifie que le robots.txt nous autorise à crawler ces URLs spécifiques.

7. **Assignment aux workers** : Les URLs qui passent toutes ces vérifications sont assignées à des workers de crawl (threads ou virtual threads).

### Phase 3 : Téléchargement de la page

C'est maintenant qu'un worker YowYobBot entre en action et télécharge réellement la page :

1. **Préparation de la requête HTTP** : On construit une requête HTTP GET avec tous les headers appropriés :
   ```
   GET /article/123 HTTP/1.1
   Host: example.com
   User-Agent: Mozilla/5.0 (compatible; YowYobBot/1.0; +https://yowyob.com/bot)
   Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
   Accept-Language: fr,en;q=0.8
   Accept-Encoding: gzip, deflate
   Connection: keep-alive
   ```

2. **Timeout configuré** : On configure un timeout de 10 secondes pour la connexion et 30 secondes pour la réception complète. Si le site est trop lent, on abandonne pour ne pas bloquer nos resources.

3. **Suivi des redirections** : Si le serveur répond avec une redirection (code 301, 302, 307, etc.), on suit automatiquement jusqu'à 5 redirections. Au-delà, on considère que c'est suspect (boucle de redirection) et on abandonne.

4. **Gestion des erreurs HTTP** : Selon le code de statut reçu, nous agissons différemment :
    - **200 OK** : Parfait, on continue
    - **404 Not Found** : On marque l'URL comme morte, on ne la retente plus
    - **403 Forbidden** : On respecte cette interdiction et on ne crawle plus ce site pour un moment
    - **429 Too Many Requests** : On a été trop gourmands, on ralentit notre crawl de ce domaine
    - **500, 502, 503** : Erreur serveur, on va retenter dans quelques heures
    - **Autres codes** : On enregistre et on décide au cas par cas

5. **Décompression** : Si le contenu est compressé (gzip ou deflate), on le décompresse.

6. **Détection d'encodage** : On détecte l'encodage du texte (UTF-8, ISO-8859-1, Windows-1252, etc.) en examinant le header `Content-Type` et la balise `<meta charset="...">`. Si on ne peut pas détecter, on assume UTF-8.

7. **Vérification de taille** : Si la page fait plus de 10MB, on ne la télécharge pas complètement. C'est probablement un fichier vidéo ou un dump de base de données, pas du contenu cherchable.

### Phase 4 : Parsing et extraction du contenu

Une fois la page téléchargée, il faut en extraire l'information utile. C'est un processus en plusieurs étapes :

**Détection du type de contenu** : On regarde le header `Content-Type` pour savoir si c'est du HTML, un PDF, un Word, etc.

Pour le **HTML** (le cas le plus courant) :

1. **Parsing avec JSoup** : On passe le HTML à JSoup qui construit un arbre DOM (Document Object Model). JSoup est tolérant aux erreurs - même du HTML horrible avec des balises non fermées sera parsé correctement.

2. **Extraction du titre** : On cherche la balise `<title>` et les balises `<h1>`. Si les deux existent et sont différents, on privilégie le `<h1>` car c'est généralement le vrai titre de l'article.

3. **Extraction des métadonnées** : On extrait les balises meta importantes :
    - `<meta name="description">` : résumé de la page
    - `<meta name="keywords">` : mots-clés (bien que peu utilisés aujourd'hui)
    - `<meta property="og:...">` : Open Graph pour les réseaux sociaux
    - `<meta name="author">` : auteur de l'article
    - `<meta name="publish-date">` : date de publication

4. **Nettoyage du HTML** : On supprime toutes les balises `<script>`, `<style>`, `<nav>`, `<footer>`, `<aside>` qui contiennent généralement du bruit plutôt que du contenu principal.

5. **Extraction du contenu principal** : C'est la partie la plus délicate. On utilise des heuristiques pour identifier le contenu principal :
    - Chercher des éléments avec id/class contenant "content", "article", "post", "main"
    - Calculer le ratio texte/balises pour chaque élément (le contenu principal a beaucoup de texte, peu de balises)
    - Chercher le plus long bloc de texte continu
    - Exclure les éléments qui ressemblent à des menus, commentaires, sidebars

6. **Extraction des images** : On extrait toutes les images avec leur attribut `alt` (important pour l'accessibilité et le SEO) et leur URL. On garde les 5 premières images pertinentes (pas les logos, pas les boutons).

7. **Extraction des liens** : On extrait tous les liens (`<a href="...">`) avec leur texte d'ancre. Ces liens seront ajoutés à la Frontier pour de futurs crawls. On convertit les URLs relatives en absolues (par exemple, `href="/article/456"` devient `https://example.com/article/456`).

8. **Détection de la langue** : On utilise Apache Tika pour détecter automatiquement la langue du texte. Nous nous concentrons sur le français et l'anglais. Si c'est une autre langue, on l'indexe quand même mais avec une priorité plus basse.

Pour les **PDF** et autres documents :

1. **Extraction avec Tika** : On passe le fichier à Apache Tika qui retourne le texte extrait et les métadonnées.

2. **Métadonnées structurées** : Les PDFs contiennent souvent des métadonnées riches (auteur, titre, sujet, date de création, nombre de pages). On extrait tout ça.

3. **OCR si nécessaire** : Si le PDF contient des images scannées plutôt que du texte, Tika peut appeler Tesseract OCR pour extraire le texte. C'est lent mais nécessaire pour les documents scannés.

### Phase 5 : Évaluation de la qualité

Avant d'indexer le contenu, nous le passons à travers notre système d'évaluation de qualité. Nous avons développé une série de filtres qui rejettent automatiquement le contenu de basse qualité :

**Filtre de longueur minimale** : Si la page contient moins de 100 mots de texte, on la rejette. C'est probablement une page vide, une page d'erreur, ou du contenu sans substance. Exception : les pages produits e-commerce qui peuvent être courtes mais riches en informations structurées.

**Filtre de spam** : On calcule plusieurs indicateurs de spam :
- **Ratio liens/texte** : Si plus de 30% du contenu est des liens, c'est probablement une ferme de liens (link farm)
- **Densité de mots-clés** : Si un mot-clé apparaît dans plus de 5% du texte, c'est probablement du keyword stuffing
- **Présence de mots-spam** : On a une liste de mots et expressions typiques du spam ("cliquez ici", "offre limitée", "crédit rapide", etc.)
- **Ratio publicités/contenu** : On détecte les blocs publicitaires et on calcule leur proportion

**Filtre de duplication** : Pour détecter le contenu dupliqué, on utilise une technique appelée **shingling** :
1. On découpe le texte en "shingles" (séquences de 5 mots consécutifs)
2. On calcule un hash pour chaque shingle
3. On garde uniquement les X premiers hashes (disons 200) triés
4. C'est notre "signature" de la page
5. On compare cette signature avec celles des pages déjà indexées
6. Si plus de 80% des hashes correspondent, les pages sont considérées comme dupliquées

**Filtre de lisibilité** : On calcule un score de lisibilité basé sur :
- Longueur moyenne des phrases (les phrases trop longues sont dures à lire)
- Longueur moyenne des mots (les mots trop longs aussi)
- Présence de structure (paragraphes, listes, titres)
- On utilise l'indice de Flesch-Kincaid adapté au français

**Filtre de pertinence géographique** : Puisque nous nous concentrons sur le Cameroun, on donne la priorité au contenu camerounais. On détecte la géolocalisation par :
- Domaine de premier niveau (.cm)
- Mentions de villes camerounaises dans le texte
- Langue et expressions locales
- IP du serveur hébergeant le site

Si le contenu passe tous ces filtres, on lui attribue un **score de qualité global** entre 0 et 100. Seul le contenu avec un score supérieur à 50 est indexé.

### Phase 6 : Indexation dans Elasticsearch

Enfin, le contenu qui a survécu à tous les filtres est prêt à être indexé :

1. **Construction du document** : On construit un document JSON avec toutes les informations extraites :
```json
{
  "url": "https://example.com/article/123",
  "title": "Les nouvelles technologies au Cameroun",
  "content": "Le texte complet de l'article...",
  "summary": "Un résumé de 200 caractères...",
  "author": "Jean Dupont",
  "publish_date": "2025-12-01T10:30:00Z",
  "crawl_date": "2025-12-01T15:45:30Z",
  "language": "fr",
  "domain": "example.com",
  "page_rank": 0.75,
  "quality_score": 85,
  "images": [
    {"url": "https://example.com/img1.jpg", "alt": "Description image 1"},
    {"url": "https://example.com/img2.jpg", "alt": "Description image 2"}
  ],
  "outbound_links": ["https://other-site.com", "https://another.com"],
  "metadata": {
    "word_count": 1250,
    "reading_time_minutes": 5,
    "has_video": false
  }
}
```

2. **Buffering** : Au lieu d'envoyer chaque document individuellement à Elasticsearch, on les accumule dans un buffer jusqu'à avoir 1000 documents (ou jusqu'à ce que 30 secondes se soient écoulées).

3. **Bulk indexing** : On envoie les 1000 documents d'un coup avec l'API Bulk d'Elasticsearch. C'est beaucoup plus efficace :
    - Une seule connexion HTTP au lieu de 1000
    - Moins d'overhead réseau
    - Elasticsearch peut optimiser l'indexation par batch

4. **Gestion des erreurs** : Si l'indexation échoue (Elasticsearch down, erreur de validation, etc.) :
    - Les documents sont sauvegardés dans une queue de retry
    - On les retente 3 fois avec un délai exponentiel (1 minute, 5 minutes, 15 minutes)
    - Si toujours en échec, on les stocke dans un fichier "failed-to-index.json" pour investigation manuelle

5. **Publication d'événement** : Une fois l'indexation réussie, on publie un événement `document-indexed` sur Kafka avec les informations clés (URL, timestamp, nombre de mots, score de qualité).

6. **Mise à jour de l'historique** : On met à jour notre table `crawled_pages` dans PostgreSQL avec le statut "indexed", le timestamp, et les métadonnées.

### Phase 7 : Post-crawl et découverte de nouveaux liens

Le crawl d'une page n'est pas terminé après l'indexation. Il reste une étape importante : traiter tous les liens découverts.

1. **Filtrage des liens** : Parmi tous les liens extraits de la page, on ne garde que ceux qui sont intéressants :
    - On retire les liens vers des fichiers non crawlables (images, vidéos, css, js)
    - On retire les liens "nofollow" (indication que le propriétaire du site ne veut pas qu'on suive ce lien)
    - On retire les liens vers des domaines blacklistés (sites de spam connus)
    - On retire les liens en double

2. **Calcul de la priorité héritée** : Les nouveaux liens héritent partiellement de la priorité de la page parent. Si on découvre un lien depuis une page importante, ce lien est probablement important aussi. Mais la priorité diminue avec la profondeur - un lien à 5 clics de la homepage sera moins prioritaire qu'un lien sur la homepage.

3. **Ajout à la Frontier** : Chaque nouveau lien passe par le processus complet de découverte qu'on a décrit en Phase 1.

4. **Mise à jour du graphe de liens** : On met à jour notre représentation du web comme un graphe de liens. Cela nous permet de calculer le PageRank et d'identifier les clusters de sites liés.

C'est un cycle sans fin - chaque page crawlée découvre de nouveaux liens qui mènent à de nouvelles pages, etc. C'est ainsi qu'on explore progressivement l'ensemble du web accessible.

## Gestion des erreurs et retry logic

Le crawling web est intrinsèquement instable. Les serveurs tombent, les connexions échouent, les pages sont supprimées, les redirections créent des boucles. Un bon crawler doit gérer gracieusement toutes ces situations sans s'arrêter.

### Taxonomie des erreurs

Nous catégorisons les erreurs en plusieurs types, chacun avec sa stratégie de gestion :

**Erreurs transitoires (temporaires)** : Ce sont des erreurs qui peuvent se résoudre en réessayant plus tard.
- Timeout réseau
- Serveur occupé (503 Service Unavailable)
- Erreur DNS temporaire
- Connexion refusée (serveur surchargé)

**Action** : On marque l'URL comme "à retenter" et on la remet dans la Frontier avec une priorité légèrement réduite. On attend au moins 1 heure avant de réessayer. On réessaie jusqu'à 3 fois. Si ça échoue 3 fois, on passe à "erreur permanente".

**Erreurs permanentes** : Erreurs qui ne se résoudront probablement pas en réessayant.
- 404 Not Found
- 410 Gone
- 403 Forbidden
- Erreur de parsing (HTML complètement cassé)
- Contenu non textuel (vidéo, audio, exécutable)

**Action** : On marque l'URL comme "morte" et on ne la retente plus. On garde l'historique pour ne pas essayer de la recrawler si on la redécouvre via un lien.

**Erreurs de politesse** : Situations où nous devons ralentir ou arrêter temporairement.
- 429 Too Many Requests
- Robots.txt nous interdit ce path
- Trop de requêtes en peu de temps

**Action** : On respecte immédiatement. On marque le domaine comme "rate-limited" et on n'y envoie plus de requêtes pendant une période (configurée par le site via le header Retry-After ou par défaut 1 heure).

**Erreurs de contenu** : Le téléchargement a réussi mais le contenu pose problème.
- Contenu dupliqué
- Spam détecté
- Qualité trop basse
- Langue non supportée

**Action** : On marque l'URL comme "crawlée mais non indexée" et on enregistre la raison. On ne réindexe pas, mais on peut recrawler dans 3-6 mois pour voir si le contenu a changé.

### Algorithme de retry avec backoff exponentiel

Pour les erreurs transitoires, nous utilisons un algorithme de retry intelligent appelé "exponential backoff with jitter" :

```
Délai de retry = min(max_delay, base_delay × 2^(attempt-1)) + random(0, jitter)

Où :
- base_delay = 1 minute
- max_delay = 1 heure
- jitter = 10% du délai calculé
```

Concrètement :
- **Tentative 1** : on essaie immédiatement
- **Tentative 2** : on réessaie après 1 minute ± 6 secondes
- **Tentative 3** : on réessaie après 2 minutes ± 12 secondes
- **Tentative 4** : on réessaie après 4 minutes ± 24 secondes
- **Tentative 5** : on réessaie après 8 minutes ± 48 secondes
- Au-delà, on attend toujours 1 heure maximum

Le "jitter" (bruit aléatoire) est important. Si 1000 requêtes échouent au même moment (par exemple parce qu'un serveur redémarre), sans jitter elles réessaieraient toutes exactement 1 minute plus tard, créant un nouveau pic de charge. Avec le jitter, elles sont espacées sur une fenêtre de 20 secondes, lissant la charge.

### Circuit breaker pour les domaines problématiques

Nous utilisons aussi le pattern Circuit Breaker au niveau des domaines. Si un domaine a trop d'erreurs, on "ouvre le circuit" - on arrête temporairement de le crawler pour ne pas gaspiller nos ressources :

**État CLOSED (fermé - normal)** : On crawle normalement le domaine.

**Détection de problèmes** : Si sur les 100 dernières requêtes à ce domaine, plus de 50 ont échoué, on passe à l'état OPEN.

**État OPEN (ouvert - circuit ouvert)** : On ne fait plus de requêtes à ce domaine pendant 1 heure. Toutes les URLs de ce domaine restent dans la Frontier mais sont skippées.

**État HALF-OPEN (semi-ouvert - test)** : Après 1 heure, on essaie 5 requêtes tests. Si 4 sur 5 réussissent, on retourne à CLOSED. Sinon, retour à OPEN pour 1 heure.

Ce mécanisme nous permet de ne pas gaspiller nos ressources sur des domaines qui ont des problèmes techniques prolongés. Quand le domaine se rétablit, on le détecte automatiquement et on recommence à le crawler.

### Dead letter queue pour les cas désespérés

Malgré tous nos mécanismes de retry, certaines URLs résistent à toutes nos tentatives. Plutôt que de les perdre ou de les retenter indéfiniment, on les met dans une **Dead Letter Queue** (DLQ) :

C'est une table PostgreSQL séparée qui contient toutes les URLs qui ont échoué après nos 3 tentatives de retry. Chaque entrée contient :
- L'URL problématique
- Le type d'erreur
- Le message d'erreur complet
- Le nombre de tentatives
- Les timestamps de toutes les tentatives
- Le contexte (depuis quelle page on a découvert ce lien)

Périodiquement (une fois par semaine), notre équipe technique review cette DLQ :
- Certaines erreurs révèlent des bugs dans notre code (on les fixe)
- Certaines révèlent des sites qui ont des protections anti-bot trop agressives (on les contacte)
- Certaines sont vraiment des liens morts (on les garde juste pour ne pas réessayer)

C'est aussi une source d'amélioration continue - en analysant les patterns d'erreurs, on peut améliorer notre robustesse.

## Performance et scalabilité

Un crawler doit être extrêmement performant et scalable pour crawler efficacement des millions de pages. Voici toutes les optimisations que nous avons implémentées.

### Architecture multi-threaded avec Virtual Threads

Traditionnellement, on aurait créé un pool de threads fixes (disons 200 threads) et chaque thread crawlerait des URLs en boucle. Le problème : chaque thread Java consomme environ 1MB de mémoire, donc 200 threads = 200MB juste pour les stacks de threads. Et si on veut crawler 10 domaines simultanément avec 20 connexions par domaine, 200 threads ne suffisent plus.

Avec Java 21 et les Virtual Threads (Project Loom), on peut créer des millions de threads légers. Notre architecture :

1. **Pool de Virtual Threads illimité** : On crée un Virtual Thread pour chaque URL à crawler. Si on a 10,000 URLs à crawler simultanément, on crée 10,000 Virtual Threads. Chacun ne consomme que quelques KB de mémoire.

2. **Scheduler au niveau JVM** : La JVM se charge automatiquement de scheduler ces Virtual Threads sur les threads platform (traditionnels). Si on a 8 CPU cores, la JVM créera environ 8-16 threads platform et y exécutera les millions de Virtual Threads.

3. **Blocking devient OK** : Avec des threads traditionnels, un appel HTTP bloquant (qui attend la réponse du serveur) bloque tout le thread. Avec Virtual Threads, quand un thread fait un appel bloquant, la JVM le "unmount" du thread platform et "mount" un autre Virtual Thread à la place. Résultat : nos 8 threads platform sont toujours occupés même si des milliers de Virtual Threads attendent des réponses réseau.

Concrètement, avec notre serveur qui a 8 cores et 16GB de RAM, on peut crawler **2000-3000 pages simultanément**, en respectant toutes nos limites de politesse par domaine.

### Connection pooling et keep-alive HTTP

Établir une connexion HTTP est coûteux :
1. Résolution DNS : 10-100ms
2. TCP handshake (SYN, SYN-ACK, ACK) : 30-100ms
3. TLS handshake si HTTPS (le cas le plus courant) : 50-200ms
4. Total : 90-400ms avant même d'avoir envoyé la requête HTTP !

Si on crawle 1000 pages sur le même domaine, répéter ce processus 1000 fois serait un gâchis monumental. Solutions :

**Connection pooling** : On maintient un pool de connexions persistantes vers chaque domaine. Configuration :
- **Max connections par domaine** : 10
- **Idle timeout** : 60 secondes (on garde la connexion ouverte 60s après la dernière utilisation)
- **Connection lifetime** : 30 minutes (même si utilisée, on renouvelle après 30 minutes pour éviter les connexions "stales")

Quand on doit crawler example.com :
1. On demande une connexion au pool
2. Si une connexion est disponible (ouverte et idle), on la réutilise
3. Sinon, on en crée une nouvelle (si on n'a pas atteint le max de 10)
4. Après utilisation, on retourne la connexion au pool au lieu de la fermer

**HTTP Keep-Alive** : On envoie le header `Connection: keep-alive` qui dit au serveur de garder la connexion ouverte. La plupart des serveurs modernes supportent ceci et gardent la connexion ouverte pendant 60-120 secondes.

Résultat : Pour crawler 1000 pages sur example.com, on établit seulement 10 connexions au lieu de 1000. Gain de temps : énorme.

### DNS caching et résolution parallèle

La résolution DNS (convertir "example.com" en adresse IP "192.0.2.1") peut prendre 10-100ms. Si on crawle des dizaines de domaines différents, ces latences s'additionnent.

**Cache DNS local** : On maintient un cache local des résolutions DNS :
- **TTL** : On respecte le TTL du DNS (généralement 300-3600 secondes)
- **Taille du cache** : 10,000 domaines
- **Préchargement** : On résout proactivement les DNS des prochains domaines qu'on va crawler

**Résolution DNS parallèle** : Au lieu de résoudre les DNS au moment où on en a besoin, on a un thread dédié qui résout en avance les DNS de tous les domaines dans notre Frontier. Quand on veut crawler example.com, son DNS est déjà résolu et en cache.

### Compression HTTP et économie de bande passante

La bande passante réseau peut facilement devenir un goulot d'étranglement. Une page HTML moyenne fait 50-100KB non compressée. À 1000 pages/seconde, ça fait 50-100 MB/seconde, soit 360-720 GB/heure ! Notre bande passante serveur ne pourrait pas suivre.

**Negotiation de compression** : On envoie le header `Accept-Encoding: gzip, deflate, br` qui indique aux serveurs qu'on accepte du contenu compressé. La plupart des serveurs modernes compressent automatiquement leur contenu, réduisant la taille de 70-90%.

**Brotli quand disponible** : Brotli est un algorithme de compression plus récent et plus efficace que Gzip (10-20% meilleur). On le préfère quand le serveur le supporte.

**Conditional requests** : Quand on recrawle une page qu'on a déjà visitée, on envoie les headers `If-Modified-Since` et `If-None-Match` (ETag). Si la page n'a pas changé, le serveur répond 304 Not Modified avec un body vide au lieu de renvoyer toute la page. Énorme économie !

Résultat : Notre bande passante réelle est environ 5 fois moindre que sans compression.

### Batching et bulk operations

Partout où c'est possible, on groupe les opérations par batch plutôt que de les faire une par une :

**Extraction depuis la Frontier** : Au lieu de `SELECT * FROM url_queue ORDER BY priority LIMIT 1` exécuté 1000 fois, on fait `LIMIT 1000` une fois. Gain : 1 requête SQL au lieu de 1000.

**Insertion dans la Frontier** : Quand on découvre 100 nouveaux liens, au lieu de 100 `INSERT`, on fait un seul `INSERT ... VALUES (...), (...), ...` avec 100 tuples. PostgreSQL optimise bien mieux cette requête unique.

**Indexation Elasticsearch** : Le bulk indexing dont on a déjà parlé (1000 documents à la fois).

**Mise à jour du Bloom Filter** : Au lieu d'ajouter les URLs une par une, on accumule 10,000 URLs et on les ajoute d'un coup.

**Publication Kafka** : On groupe les événements par batch de 100 avant de les envoyer.

Cette stratégie de batching multiplie notre throughput par 5-10x par rapport à des opérations individuelles.

### Optimisations PostgreSQL spécifiques

Notre URL Frontier dans PostgreSQL peut contenir des millions de lignes. Sans optimisations, les requêtes deviendraient extrêmement lentes.

**Index composites** : On a créé des index sur les colonnes souvent utilisées ensemble :
```sql
CREATE INDEX idx_url_queue_priority_status 
  ON url_queue(priority DESC, status, next_crawl_time);
```
Cet index permet d'extraire très rapidement les URLs avec la plus haute priorité qui sont prêtes à être crawlées.

**Partitionnement** : Notre table `url_queue` est partitionnée par priorité :
- Partition P1 : priorité 1 (environ 1000 URLs)
- Partition P2 : priorité 2 (environ 10,000 URLs)
- Partition P3 : priorité 3 (environ 1,000,000 URLs)
- Partition P4 : priorité 4 (plusieurs millions d'URLs)

Quand on requête P1, PostgreSQL scan seulement cette petite partition au lieu de toute la table.

**VACUUM régulier** : PostgreSQL ne supprime pas physiquement les lignes quand on fait un DELETE, elles deviennent juste invisibles. Au fil du temps, cela crée des "dead tuples" qui ralentissent les scans. On fait un VACUUM automatique chaque nuit pour nettoyer ces dead tuples.

**Connection pooling avec PgBouncer** : Au lieu que chaque thread ouvre sa propre connexion à PostgreSQL (très coûteux), on utilise PgBouncer qui maintient un pool de connexions partagées. On peut avoir 1000 clients qui partagent 20 connexions réelles à la base.

### Monitoring des performances en temps réel

On ne peut optimiser que ce qu'on mesure. Notre système de monitoring collecte des métriques détaillées :

**Métriques de throughput** :
- Pages crawlées par seconde (actuellement ~50-100 en moyenne)
- URLs ajoutées à la Frontier par seconde
- Documents indexés dans Elasticsearch par seconde
- Événements publiés sur Kafka par seconde

**Métriques de latence** :
- Temps de download moyen d'une page (cible : < 500ms)
- Temps de parsing moyen (cible : < 50ms)
- Temps d'indexation moyen (cible : < 100ms)
- Latence du Frontier extraction (cible : < 10ms)

**Métriques de ressources** :
- Utilisation CPU (on vise 70-80% pour laisser de la marge)
- Utilisation mémoire (on vise < 80%)
- Threads actifs vs idle
- Connexions HTTP ouvertes
- Connexions PostgreSQL utilisées

**Métriques métier** :
- Taux d'erreur par type
- Domaines dans le circuit breaker
- Taille actuelle de la Frontier
- Distribution des priorités dans la Frontier

Ces métriques sont exposées via Prometheus et visualisées dans Grafana. On a des alertes configurées pour nous prévenir si :
- Le throughput descend en dessous de 30 pages/sec (peut-être un problème)
- Le taux d'erreur dépasse 20% (clairement un problème)
- La Frontier devient vide (on n'a plus rien à crawler !)
- L'utilisation CPU dépasse 90% (on doit scaler)

## Gestion des jobs de crawl et scheduling

Notre Crawler ne tourne pas 24/7 au hasard. Nous avons une stratégie sophistiquée de scheduling qui détermine quand et quoi crawler.

### Types de jobs de crawl

Nous avons plusieurs types de jobs avec des objectifs différents :

**Discovery Crawl (crawl de découverte)** : C'est le crawl exploratoire. On part de quelques seed URLs et on découvre progressivement un maximum de pages en suivant les liens. Configuration :
- **Profondeur max** : 5 (on ne va pas au-delà de 5 clics depuis le seed)
- **Max pages par domaine** : 1000 (pour éviter qu'un seul gros site monopolise tout)
- **Durée max** : 24 heures
- **Fréquence** : Une fois par mois pour les nouveaux domaines

**Refresh Crawl (recrawl)** : On revisite des pages déjà indexées pour détecter les changements. Configuration :
- **Cible** : Pages déjà dans notre index
- **Priorité aux pages qui changent souvent** : Sites d'actualités chaque 6h, blogs chaque semaine, sites vitrines chaque mois
- **Détection de changement** : Si le ETag/Last-Modified n'a pas changé, on skip le re-téléchargement
- **Fréquence** : Continue, tourner en boucle

**Focused Crawl (crawl ciblé)** : On crawler spécifiquement un domaine ou un type de contenu. Par exemple "crawler tous les sites .cm" ou "crawler tous les PDFs de rapports gouvernementaux". Configuration :
- **Filtres stricts** : On ne garde que les URLs qui matchent nos critères
- **Profondeur illimitée** : On va aussi loin que nécessaire dans le site
- **Priorité haute** : Ces crawls sont prioritaires

**Incremental Crawl (crawl incrémental)** : On ne crawle que les nouveaux contenus. Utilise les sitemaps XML et les flux RSS/Atom pour découvrir les nouvelles URLs sans avoir à recrawler tout le site. Configuration :
- **Check sitemap** : Toutes les heures pour les sites d'actualités
- **Check RSS** : Toutes les 30 minutes pour les blogs actifs
- **Très efficace** : On récupère uniquement ce qui a changé

### Quartz Scheduler : notre chef d'orchestre

Nous utilisons Quartz Scheduler pour gérer tous ces jobs. Quartz est une bibliothèque Java extrêmement mature qui gère :

**Définition déclarative des jobs** : On définit nos jobs en code ou en configuration :
```java
JobDetail job = JobBuilder.newJob(RefreshCrawlJob.class)
    .withIdentity("refresh-news-sites", "crawl")
    .usingJobData("domains", "actualites.cm,crtv.cm")
    .usingJobData("maxDepth", 2)
    .build();

Trigger trigger = TriggerBuilder.newTrigger()
    .withIdentity("trigger-refresh-news", "crawl")
    .withSchedule(
        CronScheduleBuilder.cronSchedule("0 0 */6 * * ?") // Toutes les 6 heures
    )
    .build();

Scheduler.scheduleJob(job, trigger);
```

**Expressions Cron complexes** : On peut définir des schedules très précis :
- `0 0 2 * * ?` : Tous les jours à 2h du matin
- `0 */15 * * * ?` : Toutes les 15 minutes
- `0 0 8-18 ? * MON-FRI` : Du lundi au vendredi, toutes les heures entre 8h et 18h

**Gestion des overlaps** : Si un job prend plus de temps que prévu et que le prochain trigger arrive, Quartz peut :
- Attendre que le job en cours finisse (comportement par défaut)
- Skipper ce déclenchement
- Interrompre le job en cours
- Lancer une nouvelle instance en parallèle

**Persistence** : Tous les jobs et triggers sont stockés dans PostgreSQL. Si le serveur crash au milieu d'un job, quand on redémarre, Quartz :
- Récupère l'état de tous les jobs
- Relance les jobs qui étaient en cours (configurable)
- Rattrape les triggers qui ont été missés

**Clustering** : Si on a plusieurs instances du Crawler Service (pour la high availability), Quartz se charge de coordonner entre elles. Un seul nœud exécute un job donné à un moment donné, même si vous avez 10 nœuds. Si un nœud tombe, les autres reprennent ses jobs.

### Stratégies de recrawl intelligentes

Toutes les pages ne doivent pas être recrawlées à la même fréquence. On a développé un modèle prédictif qui détermine la fréquence optimale de recrawl pour chaque page :

**Historique de changement** : On garde un historique des 10 derniers crawls de chaque page avec un hash de son contenu. Si une page a changé à chaque crawl dans le passé, elle changera probablement souvent dans le futur. Si elle n'a jamais changé en 1 an, elle ne changera probablement pas demain.

**Calcul de la fréquence de changement** :
```
Change_Frequency = Number_of_Changes / Time_Span

Par exemple :
- Page d'actualités : 10 changements en 10 jours = 1 changement/jour → recrawler chaque jour
- Blog personnel : 2 changements en 180 jours = 0.011 changement/jour → recrawler chaque 90 jours
- Page vitrine : 0 changement en 365 jours → recrawler chaque 180 jours
```

**Ajustement par type de contenu** : On a des multiplicateurs basés sur le type de contenu :
- Actualités : × 2 (on crawle 2× plus souvent que suggéré)
- E-commerce : × 1.5 (les prix et stocks changent)
- Blogs : × 1
- Pages statiques : × 0.5

**Coût vs valeur** : On prend aussi en compte la valeur de la page (basée sur son PageRank et son traffic) vs le coût de la crawler (temps, ressources). Une page peu visitée qui change souvent peut avoir une fréquence de recrawl réduite pour économiser nos ressources.

Le résultat : chaque page dans notre index a une date de "next_crawl" calculée individuellement. Notre Refresh Crawl job priorise toujours les pages dont la date de next_crawl est passée.

### Throttling adaptatif et load balancing

Pour ne pas surcharger ni les sites qu'on crawle ni notre propre infrastructure, on a implémenté plusieurs mécanismes de throttling :

**Throttling par domaine** : Comme déjà mentionné, on limite à 1 requête par seconde par domaine (configurable). Mais ce n'est pas fixe :
- Si on détecte que le site répond lentement (> 2 secondes), on ralentit à 1 requête toutes les 2 secondes
- Si on reçoit des 429 ou 503, on passe à 1 requête toutes les 5 secondes
- Si tout va bien pendant longtemps, on peut accélérer à 1 requête toutes les 0.5 secondes pour les gros sites

**Throttling global** : On limite aussi notre throughput global pour ne pas saturer notre infrastructure :
- Max 100 pages/seconde (même si techniquement on pourrait faire plus)
- Max 1000 connexions HTTP simultanées
- Max 50 insertions Elasticsearch/seconde en moyenne (avec bursts à 200)

**Load balancing intelligent** : Si on a plusieurs instances du Crawler Service (pour la scalabilité), chaque instance travaille sur des domaines différents pour éviter les conflicts :
- Instance 1 : domaines dont le hash est 0-999
- Instance 2 : domaines dont le hash est 1000-1999
- Instance 3 : domaines dont le hash est 2000-2999
- etc.

Cette distribution est automatique et se rééquilibre dynamiquement si une instance tombe ou si une nouvelle instance est ajoutée.

## Intégration avec l'écosystème YowYob

Notre Crawler Service ne vit pas en isolation. Il s'intègre étroitement avec les autres services de notre plateforme.

### Communication avec le Search Service

**Publication des documents indexés** : Chaque fois qu'on indexe un document dans Elasticsearch, on publie un événement `document-indexed` sur Kafka. Le Search Service écoute ces événements pour :
- Mettre à jour son cache de recherche
- Recalculer les trending queries si nécessaire
- Invalider les caches de résultats obsolètes

**Feedback sur la qualité** : Le Search Service nous envoie des métriques sur la qualité des résultats :
- Quel est le CTR (Click-Through Rate) des pages que nous avons indexées ?
- Y a-t-il des pages avec un taux de rebond élevé (les gens cliquent puis reviennent immédiatement) ?
- Quelles pages reçoivent des reviews négatifs des utilisateurs ?

On utilise ces signaux pour ajuster nos scores de qualité et nos priorités de crawl.

### Communication avec le Stats Service

Le Stats Service est friand d'événements pour ses analytics. On lui envoie :

**Événements de crawl** :
```json
{
  "type": "page_crawled",
  "url": "https://example.com/page",
  "domain": "example.com",
  "status": "success",
  "response_time_ms": 450,
  "content_size_bytes": 52000,
  "word_count": 1250,
  "quality_score": 85,
  "language": "fr",
  "timestamp": "2025-12-01T15:30:45Z"
}
```

Le Stats Service agrège ces événements pour fournir des dashboards montrant :
- Combien de pages on crawle par jour/semaine/mois
- Distribution des langues dans notre index
- Domaines les plus crawlés
- Taux de succès vs erreurs par type
- Performance moyenne par domaine

**Événements de découverte** :
```json
{
  "type": "links_discovered",
  "source_url": "https://example.com/page1",
  "discovered_urls": [
    "https://example.com/page2",
    "https://other-site.com/page"
  ],
  "count": 2,
  "timestamp": "2025-12-01T15:30:50Z"
}
```

Cela permet de construire des visualisations du graphe de liens du web camerounais, identifier les hubs, etc.

### Communication avec le Notification Service

Quand des événements importants se produisent dans le Crawler, on notifie les administrateurs :

**Alertes d'erreur** : Si le taux d'erreur dépasse un seuil, on envoie :
```json
{
  "type": "crawler_alert",
  "severity": "warning",
  "message": "Error rate exceeded 25%",
  "details": {
    "error_rate": 0.27,
    "total_attempts": 1000,
    "failed_attempts": 270,
    "top_errors": [
      {"type": "timeout", "count": 150},
      {"type": "503", "count": 80},
      {"type": "dns_failure", "count": 40}
    ]
  },
  "timestamp": "2025-12-01T16:00:00Z"
}
```

Le Notification Service peut alors envoyer un email ou un message Slack à l'équipe d'ops.

**Notifications de découverte** : Quand on découvre un nouveau site camerounais important (détecté par le TLD .cm et un PageRank initial élevé), on notifie l'équipe qui peut décider de le promouvoir.

### Communication avec le Geo Service

Le Geo Service fournit des services de géolocalisation que nous utilisons pendant le crawl :

**Géolocalisation des domaines** : Pour chaque nouveau domaine, on demande au Geo Service de nous dire où il est hébergé (pays, ville si possible). On utilise cette info pour ajuster nos priorités - un site hébergé au Cameroun est probablement plus pertinent pour nous qu'un site hébergé en Chine.

**Extraction de lieux** : Quand on indexe du contenu, le Geo Service peut extraire les noms de lieux mentionnés dans le texte et les géocoder. Par exemple, si un article mentionne "Douala", on enrichit le document avec les coordonnées GPS de Douala. Cela permet ensuite des recherches géo-localisées dans le Search Service.

### Communication avec le User Service

Bien que moins direct, il y a quelques interactions :

**Soumissions utilisateur** : Les utilisateurs authentifiés peuvent soumettre des URLs à indexer via l'interface. Ces soumissions passent par le User Service qui vérifie l'authentification, puis envoie la requête au Crawler.

**Préférences de contenu** : Si un utilisateur indique qu'il s'intéresse particulièrement à un sujet (par exemple "éducation" ou "technologie"), le User Service peut nous demander de prioriser le crawl de sites sur ce sujet.

### Communication avec le Shop Service

Pour le e-commerce, l'intégration est particulièrement étroite :

**Détection de produits** : Quand on crawle une page et qu'on détecte qu'elle contient un produit (balises schema.org Product, patterns de prix, boutons "ajouter au panier"), on envoie le contenu au Shop Service pour extraction structurée des données produit.

**Mise à jour des prix** : Le Shop Service peut demander au Crawler de recrawler spécifiquement les pages produits pour mettre à jour les prix. Ces crawls sont hautement prioritaires car les utilisateurs veulent des prix à jour.

**Découverte de marchands** : Quand on découvre un nouveau site e-commerce camerounais, on le signale au Shop Service qui peut l'ajouter à sa liste de marchands suivis.

## Exemples de configuration et utilisation

Pour bien comprendre comment utiliser le Crawler Service, voyons des exemples concrets de configuration et d'utilisation.

### Lancer un crawl simple via l'API

Le moyen le plus simple de démarrer un crawl est via notre API REST :

```bash
curl -X POST http://localhost:8085/api/crawler/jobs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d '{
    "name": "Crawl sites d'actualités camerounais",
    "type": "DISCOVERY",
    "seedUrls": [
      "https://www.cameroon-tribune.cm",
      "https://www.crtv.cm",
      "https://actucameroun.com"
    ],
    "maxDepth": 3,
    "maxPagesPerDomain": 500,
    "respectRobotsTxt": true,
    "politenessDelayMs": 1000,
    "filters": {
      "allowedDomains": ["*.cm", "actucameroun.com"],
      "minQualityScore": 50,
      "languages": ["fr", "en"]
    }
  }'
```

Réponse :
```json
{
  "jobId": "crawl-job-123e4567",
  "status": "RUNNING",
  "created": "2025-12-01T15:00:00Z",
  "stats": {
    "urlsQueued": 3,
    "urlsCrawled": 0,
    "documentsIndexed": 0,
    "errors": 0
  },
  "estimatedCompletionTime": "2025-12-01T18:00:00Z"
}
```

Vous pouvez ensuite suivre la progression du job :

```bash
curl http://localhost:8085/api/crawler/jobs/crawl-job-123e4567 \
  -H "Authorization: Bearer ${ADMIN_TOKEN}"
```

Réponse après quelques minutes :
```json
{
  "jobId": "crawl-job-123e4567",
  "status": "RUNNING",
  "created": "2025-12-01T15:00:00Z",
  "lastUpdate": "2025-12-01T15:15:30Z",
  "progress": 0.45,
  "stats": {
    "urlsQueued": 1250,
    "urlsCrawled": 450,
    "documentsIndexed": 380,
    "errors": 15,
    "avgCrawlTimeMs": 520,
    "pagesPerSecond": 28
  },
  "topDomains": [
    {"domain": "cameroon-tribune.cm", "pagesCrawled": 245},
    {"domain": "crtv.cm", "pagesCrawled": 132},
    {"domain": "actucameroun.com", "pagesCrawled": 73}
  ],
  "recentErrors": [
    {"url": "https://example.cm/broken", "error": "404 Not Found"},
    {"url": "https://slow-site.cm/page", "error": "timeout"}
  ]
}
```

### Configuration d'un job de recrawl périodique

Pour crawler automatiquement les sites d'actualités toutes les 6 heures, vous configureriez un job Quartz :

```java
@Configuration
public class CrawlJobsConfiguration {

    @Bean
    public JobDetail newsRecrawlJob() {
        return JobBuilder.newJob(RefreshCrawlJob.class)
            .withIdentity("news-recrawl", "periodic-crawls")
            .withDescription("Recrawl news sites every 6 hours")
            .usingJobData("seedUrls", "https://cameroon-tribune.cm,https://crtv.cm")
            .usingJobData("maxDepth", 2)
            .usingJobData("priority", "HIGH")
            .storeDurably()
            .build();
    }

    @Bean
    public Trigger newsRecrawlTrigger(JobDetail newsRecrawlJob) {
        return TriggerBuilder.newTrigger()
            .forJob(newsRecrawlJob)
            .withIdentity("news-recrawl-trigger", "periodic-crawls")
            .withSchedule(
                CronScheduleBuilder.cronSchedule("0 0 */6 * * ?")
                    .inTimeZone(TimeZone.getTimeZone("Africa/Douala"))
            )
            .build();
    }
}
```

Cette configuration sera automatiquement chargée au démarrage du service et le job s'exécutera selon le planning défini.

### Configuration des politeness policies personnalisées

Par défaut, nous respectons un délai de 1 seconde entre requêtes. Mais vous pouvez personnaliser cela par domaine :

```yaml
# application.yml
crawler:
  politeness:
    default-delay-ms: 1000
    per-domain-delays:
      wikipedia.org: 500          # Site robuste, on peut aller plus vite
      example-petit-blog.com: 3000   # Petit blog, on est très poli
      government-site.cm: 2000    # Sites gouvernementaux, on est prudent
    
    max-concurrent-connections:
      default: 5
      per-domain:
        big-news-site.com: 10     # Gros site, on peut paralléliser plus
        small-blog.com: 2         # Petit site, on limite
    
    user-agent: "Mozilla/5.0 (compatible; YowYobBot/1.0; +https://yowyob.com/bot)"
    
    robots-txt:
      respect: true
      cache-ttl-hours: 24
      timeout-ms: 5000
```

### Filtrage du contenu à crawler

Vous pouvez définir des filtres sophistiqués pour ne crawler que ce qui vous intéresse :

```yaml
crawler:
  filters:
    # Filtres d'URL
    url-patterns:
      include:
        - ".*\\.cm/.*"                    # Tous les .cm
        - ".*camerou.*"                   # URLs contenant "camerou"
        - ".*\\/(article|news|blog)\\/.*"  # URLs de type article/news/blog
      exclude:
        - ".*\\/(login|admin|api)\\/.*"   # Exclure admin, API, login
        - ".*\\.pdf$"                     # Exclure PDFs (géré séparément)
        - ".*calendar.*"                  # Exclure calendriers
    
    # Filtres de contenu
    content:
      min-words: 100
      max-words: 50000
      min-quality-score: 50
      allowed-languages: ["fr", "en"]
      
      # Patterns de spam à rejeter
      spam-keywords: ["casino", "viagra", "lottery"]
      max-link-density: 0.30  # Max 30% de liens dans le texte
      
    # Filtres par type de contenu
    content-types:
      allowed: 
        - "text/html"
        - "application/xhtml+xml"
        - "application/pdf"
        - "application/msword"
      max-size-mb:
        html: 5
        pdf: 20
        document: 50
```

### Monitoring et debugging

Pour suivre en détail ce que fait le Crawler :

```bash
# Voir les métriques Prometheus
curl http://localhost:8085/actuator/prometheus

# Métriques intéressantes :
# crawler_pages_crawled_total{status="success"}
# crawler_pages_crawled_total{status="error"}
# crawler_crawl_duration_seconds
# crawler_frontier_size
# crawler_active_threads

# Health check détaillé
curl http://localhost:8085/actuator/health

# Réponse :
{
  "status": "UP",
  "components": {
    "crawler": {
      "status": "UP",
      "details": {
        "activeJobs": 2,
        "frontierSize": 125000,
        "avgCrawlTimeMs": 450,
        "pagesPerSecond": 32
      }
    },
    "postgres": {"status": "UP"},
    "elasticsearch": {"status": "UP"},
    "redis": {"status": "UP"},
    "kafka": {"status": "UP"}
  }
}

# Logs avec niveau de détail ajustable
curl -X POST http://localhost:8085/actuator/loggers/com.yowyob.crawler \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

Pour suivre un domaine spécifique en temps réel :

```bash
# Voir l'état du crawl d'un domaine
curl http://localhost:8085/api/crawler/domains/example.com/stats

# Réponse :
{
  "domain": "example.com",
  "stats": {
    "totalPagesCrawled": 1250,
    "lastCrawl": "2025-12-01T15:45:00Z",
    "avgResponseTimeMs": 320,
    "errorRate": 0.05,
    "robotsTxtRules": {
      "cached": true,
      "lastFetched": "2025-12-01T12:00:00Z",
      "allowedPaths": ["/*"],
      "disallowedPaths": ["/admin/*", "/api/*"],
      "crawlDelay": 1
    }
  },
  "queuedUrls": 45,
  "circuitBreakerState": "CLOSED"
}
```

## Déploiement et opérations

### Déploiement local pour développement

Pour lancer le Crawler Service en local pendant le développement :

```bash
# 1. Cloner le repository
git clone https://github.com/BrianBrusly/YowYob-Search-Backend.git
cd YowYob-Search-Backend/yowyob-crawler-service

# 2. S'assurer que les dépendances sont disponibles
# PostgreSQL, Elasticsearch, Redis, Kafka doivent tourner
# (via docker-compose dans le repo Infrastructure)
cd ../../YowYob-Search-Infrastructure
docker-compose up -d postgres elasticsearch redis kafka

# 3. Retour au Crawler Service
cd ../YowYob-Search-Backend/yowyob-crawler-service

# 4. Configuration de l'environnement
cp src/main/resources/application-dev.yml.example \
   src/main/resources/application-dev.yml
# Éditer application-dev.yml avec vos configurations locales

# 5. Build et lancement
mvn clean install -DskipTests
mvn spring-boot:run -Dspring.profiles.active=dev
```

Le service démarre sur le port 8085. Vérifiez qu'il est up :

```bash
curl http://localhost:8085/actuator/health
```

Vous pouvez maintenant soumettre des jobs de crawl via l'API ou l'interface d'administration.

### Déploiement avec Docker

Pour un déploiement plus proche de la production :

```bash
# Build de l'image Docker
docker build -t yowyob/crawler-service:1.0.0 \
  -f docker/crawler-service.Dockerfile .

# Ou utiliser docker-compose
docker-compose up -d crawler-service
```

Notre Dockerfile utilise une approche multi-stage pour optimiser la taille :

```dockerfile
# Stage 1 : Build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY yowyob-common ./yowyob-common
COPY yowyob-crawler-service ./yowyob-crawler-service
RUN mvn clean package -DskipTests -pl yowyob-crawler-service -am

# Stage 2 : Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S yowyob && adduser -S yowyob -G yowyob
USER yowyob

# Copier le JAR depuis le stage de build
COPY --from=builder /app/yowyob-crawler-service/target/yowyob-crawler-service-1.0.0.jar app.jar

# Configuration JVM optimisée
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8085/actuator/health || exit 1

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Déploiement Kubernetes production

En production, nous déployons sur Kubernetes avec cette configuration :

```yaml
# k8s/crawler-service/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: crawler-service
  namespace: yowyob
spec:
  replicas: 2  # Au moins 2 pour la high availability
  selector:
    matchLabels:
      app: crawler-service
  template:
    metadata:
      labels:
        app: crawler-service
        version: v1.0.0
    spec:
      containers:
      - name: crawler-service
        image: yowyob/crawler-service:1.0.0
        ports:
        - containerPort: 8085
          name: http
        
        env:
        # Variables d'environnement depuis ConfigMap
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: POSTGRES_HOST
          valueFrom:
            configMapKeyRef:
              name: crawler-config
              key: postgres.host
        - name: ELASTICSEARCH_HOST
          valueFrom:
            configMapKeyRef:
              name: crawler-config
              key: elasticsearch.host
        
        # Secrets sensibles
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: crawler-secrets
              key: postgres.password
        
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "3Gi"
            cpu: "2000m"
        
        # Probes pour Kubernetes
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8085
          initialDelaySeconds: 60
          periodSeconds: 10
          timeoutSeconds: 3
          failureThreshold: 3
        
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8085
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 3
          failureThreshold: 3
        
        # Volume mounts si nécessaire
        volumeMounts:
        - name: crawler-config
          mountPath: /app/config
          readOnly: true
      
      volumes:
      - name: crawler-config
        configMap:
          name: crawler-config
```

Configuration du HorizontalPodAutoscaler pour le scaling automatique :

```yaml
# k8s/crawler-service/hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: crawler-service-hpa
  namespace: yowyob
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: crawler-service
  minReplicas: 2
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
  - type : Pods
    pods:
      metric:
        name: crawler_pages_per_second
      target:
        type: AverageValue
        averageValue: "80"  # Scale up si > 80 pages/sec par pod
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
      - type: Percent
        value: 50
        periodSeconds: 60  # Max 50% d'augmentation par minute
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Pods
        value: 1
        periodSeconds: 60  # Max 1 pod de moins par minute
```

CronJob pour les crawls périodiques :

```yaml
# k8s/crawler-service/cronjob-news-refresh.yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: crawler-news-refresh
  namespace: yowyob
spec:
  schedule: "0 */6 * * *"  # Toutes les 6 heures
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: trigger-crawl
            image: curlimages/curl:latest
            env:
            - name: CRAWLER_URL
              value: "http://crawler-service:8085/api/crawler/jobs"
            - name: ADMIN_TOKEN
              valueFrom:
                secretKeyRef:
                  name: crawler-secrets
                  key: admin.token
            command:
            - /bin/sh
            - -c
            - |
              curl -X POST ${CRAWLER_URL} \
                -H "Content-Type: application/json" \
                -H "Authorization: Bearer ${ADMIN_TOKEN}" \
                -d '{
                  "name": "Scheduled news refresh",
                  "type": "REFRESH",
                  "seedUrls": ["https://cameroon-tribune.cm", "https://crtv.cm"],
                  "maxDepth": 2
                }'
          restartPolicy: OnFailure
```

### Stratégies de scaling et load balancing

Notre Crawler Service est conçu pour scaler horizontalement. Voici comment on gère plusieurs instances :

**Distribution par domaine** : Chaque instance "claim" certains domaines basés sur un hash cohérent. Si on a 3 instances :
- Instance 1 : domaines dont `hash(domain) % 3 == 0`
- Instance 2 : domaines dont `hash(domain) % 3 == 1`
- Instance 3 : domaines dont `hash(domain) % 3 == 2`

Cette distribution garantit qu'un seul pod crawle un domaine donné à un moment donné, évitant les conflits et les violations de politeness.

**Coordin Human: Continuer ation via PostgreSQL** : Nous utilisons PostgreSQL comme système de coordination entre instances. Chaque instance :

1. S'enregistre dans une table `crawler_instances` avec son ID, son hostname, et un timestamp de heartbeat
2. Met à jour son heartbeat toutes les 10 secondes
3. Vérifie périodiquement quelles autres instances sont actives (heartbeat récent)
4. Recalcule sa part du travail si le nombre d'instances change

**Failover automatique** : Si une instance tombe :
- Son heartbeat cesse d'être mis à jour
- Après 30 secondes sans heartbeat, les autres instances la considèrent morte
- Les domaines qu'elle gérait sont redistribués aux instances restantes
- Les jobs en cours sont marqués comme "interrompus" et peuvent être repris

**Sticky sessions pour les jobs longs** : Quand un job de crawl est lancé, il est assigné à une instance spécifique. Si cette instance tombe, le job peut être repris par une autre instance depuis le dernier checkpoint sauvegardé en base.

### Gestion des secrets et configuration sensible

Les secrets (mots de passe, tokens) ne sont jamais committés dans le code. Voici notre approche :

**En développement** : Fichier `.env` local (dans `.gitignore`) :
```bash
POSTGRES_PASSWORD=dev_password_local
ELASTICSEARCH_PASSWORD=dev_es_password
ADMIN_API_TOKEN=dev_admin_token_12345
```

**En production Kubernetes** : Utilisation de Secrets :

```yaml
# k8s/crawler-service/secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: crawler-secrets
  namespace: yowyob
type: Opaque
data:
  # Valeurs encodées en base64
  postgres.password: <base64_encoded>
  elasticsearch.password: <base64_encoded>
  admin.token: <base64_encoded>
```

Création sécurisée des secrets :
```bash
# Création depuis des fichiers (recommandé)
kubectl create secret generic crawler-secrets \
  --from-file=postgres.password=./secrets/postgres-pwd.txt \
  --from-file=elasticsearch.password=./secrets/es-pwd.txt \
  --from-file=admin.token=./secrets/admin-token.txt \
  -n yowyob

# Ou depuis des variables d'environnement
kubectl create secret generic crawler-secrets \
  --from-literal=postgres.password="${POSTGRES_PASSWORD}" \
  --from-literal=elasticsearch.password="${ES_PASSWORD}" \
  --from-literal=admin.token="${ADMIN_TOKEN}" \
  -n yowyob
```

**Rotation des secrets** : Tous les 90 jours, nous rotons les secrets :
1. Création d'un nouveau secret `crawler-secrets-v2`
2. Mise à jour du deployment pour utiliser le nouveau secret
3. Rolling update des pods (zero-downtime)
4. Suppression de l'ancien secret après 24h

### Backup et disaster recovery

Notre stratégie de backup couvre tous les composants critiques :

**Backup PostgreSQL** : Script automatisé qui tourne chaque nuit :

```bash
#!/bin/bash
# scripts/backup-crawler-db.sh

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups/crawler-db"
RETENTION_DAYS=30

# Backup complet
pg_dump -h ${POSTGRES_HOST} \
        -U ${POSTGRES_USER} \
        -d yowyob_crawler \
        -F c \
        -f ${BACKUP_DIR}/crawler_db_${TIMESTAMP}.dump

# Compression
gzip ${BACKUP_DIR}/crawler_db_${TIMESTAMP}.dump

# Upload vers S3 pour stockage long terme
aws s3 cp ${BACKUP_DIR}/crawler_db_${TIMESTAMP}.dump.gz \
    s3://yowyob-backups/crawler-db/

# Nettoyage des backups locaux anciens
find ${BACKUP_DIR} -name "*.dump.gz" -mtime +${RETENTION_DAYS} -delete

# Log
echo "[$(date)] Backup completed: crawler_db_${TIMESTAMP}.dump.gz" >> /var/log/backups.log
```

**Backup Elasticsearch indices** : On utilise les snapshots Elasticsearch :

```bash
# Création d'un snapshot repository (une fois)
curl -X PUT "http://elasticsearch:9200/_snapshot/yowyob_backups" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "s3",
    "settings": {
      "bucket": "yowyob-es-snapshots",
      "region": "us-east-1",
      "base_path": "crawler-indices"
    }
  }'

# Snapshot quotidien automatique via Quartz
curl -X PUT "http://elasticsearch:9200/_snapshot/yowyob_backups/snapshot_$(date +%Y%m%d)" \
  -H "Content-Type: application/json" \
  -d '{
    "indices": "yowyob-documents,yowyob-products",
    "ignore_unavailable": true,
    "include_global_state": false
  }'
```

**Restauration après disaster** :

Si la base de données PostgreSQL est complètement perdue :
```bash
# 1. Restaurer le dernier backup
pg_restore -h ${POSTGRES_HOST} \
           -U ${POSTGRES_USER} \
           -d yowyob_crawler \
           -v ${BACKUP_DIR}/latest.dump

# 2. Vérifier l'intégrité
psql -h ${POSTGRES_HOST} -U ${POSTGRES_USER} -d yowyob_crawler \
     -c "SELECT COUNT(*) FROM url_queue;"
psql -h ${POSTGRES_HOST} -U ${POSTGRES_USER} -d yowyob_crawler \
     -c "SELECT COUNT(*) FROM crawled_pages;"

# 3. Redémarrer le service
kubectl rollout restart deployment/crawler-service -n yowyob
```

Si Elasticsearch est perdu :
```bash
# 1. Restaurer depuis snapshot
curl -X POST "http://elasticsearch:9200/_snapshot/yowyob_backups/snapshot_20251201/_restore" \
  -H "Content-Type: application/json" \
  -d '{
    "indices": "yowyob-documents,yowyob-products",
    "ignore_unavailable": true,
    "include_global_state": false
  }'

# 2. Vérifier la restauration
curl "http://elasticsearch:9200/_cat/indices?v"

# 3. Recrawler les contenus récents (depuis le dernier snapshot)
# Via l'API du Crawler
```

### Monitoring et alerting en production

Notre stack de monitoring capture tout ce qui se passe dans le Crawler :

**Dashboards Grafana** : Nous avons créé plusieurs dashboards :

**Dashboard "Crawler Overview"** :
- Graphique du throughput (pages/seconde) sur les dernières 24h
- Taux d'erreur global et par type
- Taille actuelle de la Frontier
- Nombre de jobs actifs
- Utilisation des ressources (CPU, mémoire, threads)

**Dashboard "Crawler Jobs"** :
- Liste des jobs en cours avec leur progression
- Historique des jobs complétés
- Statistiques par job (pages crawlées, erreurs, durée)
- Top 10 des domaines les plus crawlés

**Dashboard "Crawler Quality"** :
- Distribution des scores de qualité du contenu crawlé
- Taux de duplication détecté
- Distribution des langues
- Ratio contenu indexé vs rejeté

**Dashboard "Crawler Performance"** :
- Latence moyenne de crawl par domaine
- Distribution des temps de réponse (p50, p95, p99)
- Taux d'utilisation du cache (robots.txt, DNS, bloom filter)
- Efficacité du batching

**Alertes critiques configurées dans AlertManager** :

```yaml
groups:
- name: crawler_alerts
  interval: 30s
  rules:
  
  # Alerte si le throughput chute drastiquement
  - alert: CrawlerThroughputLow
    expr: rate(crawler_pages_crawled_total[5m]) < 10
    for: 10m
    labels:
      severity: warning
      component: crawler
    annotations:
      summary: "Crawler throughput is abnormally low"
      description: "Crawler is processing less than 10 pages/sec for 10+ minutes. Current: {{ $value | humanize }} pages/sec"
  
  # Alerte si trop d'erreurs
  - alert: CrawlerHighErrorRate
    expr: |
      rate(crawler_pages_crawled_total{status="error"}[5m]) 
      / 
      rate(crawler_pages_crawled_total[5m]) 
      > 0.3
    for: 5m
    labels:
      severity: critical
      component: crawler
    annotations:
      summary: "Crawler error rate is too high"
      description: "More than 30% of crawl attempts are failing. Error rate: {{ $value | humanizePercentage }}"
  
  # Alerte si la Frontier devient vide
  - alert: CrawlerFrontierEmpty
    expr: crawler_frontier_size < 1000
    for: 15m
    labels:
      severity: warning
      component: crawler
    annotations:
      summary: "Crawler Frontier is almost empty"
      description: "Only {{ $value }} URLs left in the frontier. Need to discover more content."
  
  # Alerte si trop de domaines en circuit breaker
  - alert: CrawlerTooManyCircuitBreakers
    expr: count(crawler_circuit_breaker_state{state="open"}) > 50
    for: 10m
    labels:
      severity: warning
      component: crawler
    annotations:
      summary: "Too many domains have circuit breakers open"
      description: "{{ $value }} domains are currently blocked due to repeated failures."
  
  # Alerte si un job est bloqué depuis longtemps
  - alert: CrawlerJobStuck
    expr: |
      time() - crawler_job_last_progress_timestamp 
      > 3600
    for: 30m
    labels:
      severity: warning
      component: crawler
    annotations:
      summary: "Crawler job appears to be stuck"
      description: "Job {{ $labels.job_id }} has made no progress for over 1 hour."
```

Ces alertes sont envoyées vers :
- Slack channel `#ops-alerts`
- Email vers l'équipe d'astreinte
- PagerDuty pour les alertes critiques (hors heures de bureau)

### Procédures opérationnelles standard (SOP)

Nous avons documenté des procédures pour les situations courantes :

**SOP-001 : Le Crawler s'est arrêté complètement**

1. **Diagnostic** :
```bash
# Vérifier l'état des pods
kubectl get pods -n yowyob | grep crawler-service

# Si le pod est down, voir les logs
kubectl logs -n yowyob deployment/crawler-service --tail=100

# Vérifier les événements Kubernetes
kubectl describe pod -n yowyob <crawler-pod-name>
```

2. **Actions** :
- Si OutOfMemory : Augmenter les limites mémoire dans le deployment
- Si CrashLoopBackOff : Vérifier les dépendances (PostgreSQL, Elasticsearch, Redis, Kafka)
- Si le pod ne démarre pas : Vérifier la configuration et les secrets

3. **Redémarrage** :
```bash
kubectl rollout restart deployment/crawler-service -n yowyob
kubectl rollout status deployment/crawler-service -n yowyob
```

**SOP-002 : Taux d'erreur élevé soudain**

1. **Identification** : Vérifier quel type d'erreur domine :
```bash
# Via les métriques Prometheus
curl http://crawler-service:8085/actuator/prometheus | grep crawler_pages_crawled_total

# Via les logs
kubectl logs -n yowyob deployment/crawler-service | grep ERROR | tail -50
```

2. **Actions selon le type** :
- Timeouts réseau : Vérifier la connectivité externe, augmenter les timeouts si nécessaire
- Erreurs 503 : Trop de requêtes vers certains domaines, vérifier les politeness delays
- Erreurs Elasticsearch : Vérifier l'état du cluster ES
- Erreurs PostgreSQL : Vérifier les connexions, peut-être besoin de scaler PgBouncer

**SOP-003 : Frontier se remplit trop vite**

1. **Diagnostic** : Comprendre d'où viennent les URLs :
```sql
-- Se connecter à PostgreSQL
psql -h postgres -U yowyob -d yowyob_crawler

-- Voir les domaines qui contribuent le plus
SELECT domain, COUNT(*) as url_count
FROM url_queue
WHERE status = 'PENDING'
GROUP BY domain
ORDER BY url_count DESC
LIMIT 20;
```

2. **Actions** :
- Si un domaine spam : Le blacklister
- Si découverte légitime mais trop rapide : Ajuster les filtres de découverte
- Si besoin temporaire : Augmenter les ressources de crawl pour consommer plus vite

**SOP-004 : Performance dégradée progressivement**

Symptôme : Le crawler fonctionne mais de plus en plus lentement au fil des jours.

1. **Causes courantes et solutions** :

**Fragmentation PostgreSQL** :
```bash
# Vérifier la fragmentation
psql -h postgres -U yowyob -d yowyob_crawler -c "
SELECT schemaname, tablename, 
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
"

# Si beaucoup de dead tuples, faire un VACUUM FULL (nécessite downtime)
# Ou VACUUM progressif
psql -h postgres -U yowyob -d yowyob_crawler -c "VACUUM ANALYZE;"
```

**Bloom Filter saturé** :
```bash
# Si le Bloom Filter devient trop chargé (trop de faux positifs)
# Recréer un nouveau Bloom Filter plus grand
curl -X POST http://crawler-service:8085/api/crawler/admin/bloom-filter/rebuild \
  -H "Authorization: Bearer ${ADMIN_TOKEN}"
```

**Cache Redis saturé** :
```bash
# Vérifier l'utilisation mémoire Redis
redis-cli -h redis INFO memory

# Si nécessaire, vider les caches anciens
redis-cli -h redis FLUSHDB
# Note : Ceci force à re-télécharger les robots.txt, mais c'est parfois nécessaire
```

## Tests et qualité du code

Nous prenons les tests très au sérieux. Notre suite de tests couvre tous les aspects critiques du Crawler.

### Tests unitaires

Chaque composant a ses tests unitaires qui vérifient la logique en isolation :

```java
// Test du RobotsTxtParser
@Test
void shouldParseRobotsTxtCorrectly() {
    String robotsTxt = """
        User-agent: *
        Disallow: /admin/
        Disallow: /api/
        Allow: /api/public/
        
        User-agent: YowYobBot
        Crawl-delay: 2
        Disallow: /private/
        """;
    
    RobotsTxt parsed = RobotsTxtParser.parse(robotsTxt);
    
    // Vérifications pour tous les bots
    assertFalse(parsed.isAllowed("*", "/admin/test"));
    assertFalse(parsed.isAllowed("*", "/api/users"));
    assertTrue(parsed.isAllowed("*", "/api/public/status"));
    
    // Vérifications spécifiques à YowYobBot
    assertEquals(2, parsed.getCrawlDelay("YowYobBot"));
    assertFalse(parsed.isAllowed("YowYobBot", "/private/data"));
    assertTrue(parsed.isAllowed("YowYobBot", "/blog/article"));
}

// Test du Quality Filter
@Test
void shouldRejectSpamContent() {
    String spamContent = "Click here! Buy now! Limited offer! " +
        "Casino games online! " +
        "Win big money fast! " +
        "Click here Click here Click here";
    
    ContentQualityScore score = QualityFilter.evaluate(spamContent);
    
    assertTrue(score.getSpamScore() > 0.7);
    assertFalse(score.isPassed());
    assertTrue(score.getRejectionReasons().contains("HIGH_SPAM_SCORE"));
}

// Test du URL Normalizer
@Test
void shouldNormalizeUrlsConsistently() {
    String[] variants = {
        "https://Example.com/page?utm_source=fb&id=123",
        "http://example.com/page?id=123&utm_source=fb",
        "https://www.example.com/page?id=123",
        "HTTPS://example.com/page?id=123"
    };
    
    String normalized = URLNormalizer.normalize(variants[0]);
    
    // Toutes les variantes doivent donner le même résultat
    for (String variant : variants) {
        assertEquals(normalized, URLNormalizer.normalize(variant));
    }
    
    // Le résultat doit être propre
    assertEquals("https://example.com/page?id=123", normalized);
}
```

### Tests d'intégration avec TestContainers

Pour tester l'intégration avec les vraies bases de données, nous utilisons TestContainers qui lance des conteneurs Docker pendant les tests :

```java
@SpringBootTest
@Testcontainers
class CrawlerServiceIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("test_crawler")
        .withUsername("test")
        .withPassword("test");
    
    @Container
    static ElasticsearchContainer elasticsearch = 
        new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.0");
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);
    
    @Autowired
    private CrawlOrchestrator orchestrator;
    
    @Autowired
    private URLQueueRepository queueRepository;
    
    @Test
    void shouldCrawlAndIndexSuccessfully() {
        // Créer un job de crawl
        CrawlJob job = new CrawlJob();
        job.setName("Test crawl");
        job.setSeedUrls(List.of("https://example.com"));
        job.setMaxDepth(2);
        
        // Démarrer le crawl
        CrawlJobResult result = orchestrator.executeCrawl(job);
        
        // Attendre la complétion (avec timeout)
        await().atMost(Duration.ofMinutes(5))
               .until(() -> result.getStatus() == CrawlStatus.COMPLETED);
        
        // Vérifications
        assertTrue(result.getPagesCrawled() > 0);
        assertTrue(result.getDocumentsIndexed() > 0);
        assertEquals(0, result.getErrorCount());
        
        // Vérifier que les URLs ont été ajoutées à la Frontier
        long queuedUrls = queueRepository.countByStatus(URLStatus.PENDING);
        assertTrue(queuedUrls > 0);
    }
    
    @Test
    void shouldRespectRobotsTxt() {
        // Mock d'un serveur web avec robots.txt
        WireMockServer wireMock = new WireMockServer(8089);
        wireMock.start();
        
        wireMock.stubFor(get("/robots.txt")
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("User-agent: *\nDisallow: /admin/")));
        
        wireMock.stubFor(get("/admin/secret")
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("<html>Secret content</html>")));
        
        // Essayer de crawler l'URL interdite
        CrawlJob job = new CrawlJob();
        job.setSeedUrls(List.of("http://localhost:8089/admin/secret"));
        
        CrawlJobResult result = orchestrator.executeCrawl(job);
        
        // Vérifier que l'URL a été respectueusement rejetée
        assertTrue(result.getSkippedUrls() > 0);
        assertEquals(0, result.getPagesCrawled());
        assertTrue(result.getRejectionReasons().contains("ROBOTS_TXT_DISALLOW"));
        
        // Vérifier qu'aucune requête n'a été faite à /admin/secret
        wireMock.verify(0, getRequestedFor(urlEqualTo("/admin/secret")));
        
        wireMock.stop();
    }
    
    @Test
    void shouldHandleCircuitBreakerCorrectly() {
        // Simuler un serveur qui répond toujours 500
        WireMockServer wireMock = new WireMockServer(8090);
        wireMock.start();
        
        wireMock.stubFor(get(anyUrl())
            .willReturn(aResponse().withStatus(500)));
        
        // Créer plusieurs URLs du même domaine
        List<String> urls = IntStream.range(0, 20)
            .mapToObj(i -> "http://localhost:8090/page" + i)
            .toList();
        
        CrawlJob job = new CrawlJob();
        job.setSeedUrls(urls);
        
        CrawlJobResult result = orchestrator.executeCrawl(job);
        
        // Vérifier que le circuit breaker s'est ouvert
        CircuitBreakerState state = circuitBreakerRegistry
            .circuitBreaker("localhost:8090")
            .getState();
        
        assertEquals(CircuitBreakerState.State.OPEN, state);
        
        // Vérifier qu'on a arrêté d'essayer après un certain nombre d'échecs
        assertTrue(result.getErrorCount() < 20); // Pas toutes les 20 URLs tentées
        
        wireMock.stop();
    }
}
```

### Tests de charge et de performance

Nous effectuons régulièrement des tests de charge pour nous assurer que le Crawler peut gérer notre volume cible :

```java
@LoadTest
class CrawlerPerformanceTest {
    
    @Test
    @JMeterTest(
        jmx = "crawler-load-test.jmx",
        threads = 100,
        duration = "10m"
    )
    void shouldHandleHighConcurrentLoad() {
        // Ce test simule 100 utilisateurs soumettant des jobs simultanément
        // pendant 10 minutes
        
        // Critères de succès :
        // - Throughput > 50 pages/sec en moyenne
        // - Latence p95 < 500ms
        // - Taux d'erreur < 5%
        // - Pas de OutOfMemory
        // - Pas de deadlocks
    }
    
    @Test
    void shouldScaleLinearlyWithThreads() {
        // Test : le throughput devrait augmenter linéairement
        // avec le nombre de threads (jusqu'à un certain point)
        
        List<Integer> threadCounts = List.of(10, 20, 50, 100, 200);
        Map<Integer, Double> throughputs = new HashMap<>();
        
        for (int threads : threadCounts) {
            CrawlerConfig config = new CrawlerConfig();
            config.setMaxConcurrentCrawls(threads);
            
            double throughput = measureThroughput(config, Duration.ofMinutes(2));
            throughputs.put(threads, throughput);
        }
        
        // Vérifier la scalabilité
        assertTrue(throughputs.get(20) > throughputs.get(10) * 1.8);
        assertTrue(throughputs.get(50) > throughputs.get(20) * 2.3);
        
        // Mais après un certain point, on atteint un plateau
        // (limité par le réseau, les sites externes, etc.)
    }
    
    @Test
    void shouldHandleLargeQueueEfficiently() {
        // Remplir la Frontier avec 1 million d'URLs
        for (int i = 0; i < 1_000_000; i++) {
            URLQueue entry = new URLQueue();
            entry.setUrl("https://example.com/page" + i);
            entry.setPriority(random.nextInt(4));
            queueRepository.save(entry);
        }
        
        // Mesurer le temps pour extraire 10,000 URLs
        Instant start = Instant.now();
        List<URLQueue> extracted = queueRepository
            .findTopPriorityUrls(10_000);
        Duration extractionTime = Duration.between(start, Instant.now());
        
        // Doit être rapide même avec une grosse queue
        assertTrue(extractionTime.toMillis() < 1000); // < 1 seconde
        assertEquals(10_000, extracted.size());
    }
}
```

### Tests de résilience et chaos engineering

Nous testons aussi comment le système réagit face à des pannes :

```java
@ChaosTest
class CrawlerResilienceTest {
    
    @Test
    void shouldRecoverFromPostgresqlFailure() {
        // Démarrer un crawl
        CrawlJob job = startLongRunningCrawl();
        
        // Attendre qu'il soit bien lancé
        await().until(() -> job.getPagesCrawled() > 100);
        
        // Tuer PostgreSQL
        postgresContainer.stop();
        
        // Attendre un peu
        Thread.sleep(5000);
        
        // Relancer PostgreSQL
        postgresContainer.start();
        
        // Le crawler devrait se reconnecter et continuer
        await().atMost(Duration.ofMinutes(2))
               .until(() -> job.getPagesCrawled() > 200);
        
        // Vérifier qu'aucune donnée n'a été perdue
        verifyDataIntegrity(job);
    }
    
    @Test
    void shouldHandleElasticsearchSlowdown() {
        // Simuler Elasticsearch qui devient très lent
        elasticsearchContainer.setNetworkLatency(5000); // 5s de latence
        
        CrawlJob job = startCrawl();
        
        // Le crawler devrait :
        // 1. Détecter la lenteur
        // 2. Activer le batching plus agressif
        // 3. Utiliser la queue de retry
        // 4. Ne pas bloquer complètement
        
        await().atMost(Duration.ofMinutes(5))
               .until(() -> job.getStatus() == CrawlStatus.COMPLETED);
        
        // Vérifier que tout a été indexé (même si lentement)
        assertEquals(job.getPagesCrawled(), job.getDocumentsIndexed());
    }
    
    @Test
    void shouldHandleNetworkPartition() {
        // Simuler une partition réseau temporaire
        // (le crawler ne peut plus joindre internet)
        
        networkProxy.breakConnectivity();
        
        // Le crawler devrait :
        // 1. Accumuler les erreurs
        // 2. Activer les circuit breakers
        // 3. Mais ne pas crasher
        
        Thread.sleep(30000); // 30 secondes de coupure
        
        // Rétablir la connexion
        networkProxy.restoreConnectivity();
        
        // Le crawler devrait reprendre automatiquement
        await().atMost(Duration.ofMinutes(2))
               .until(() -> getCurrentThroughput() > 10);
    }
}
```

## Contribution et développement

### Comment contribuer au Crawler Service

Nous encourageons les contributions ! Voici comment vous pouvez aider :

**Types de contributions bienvenues** :
- Corrections de bugs
- Nouvelles fonctionnalités (après discussion sur une issue)
- Amélioration de la documentation
- Optimisations de performance
- Ajout de tests
- Corrections de typos
- Rapports de bugs détaillés

**Processus de contribution** :

1. **Fork et clone** :
```bash
# Fork sur GitHub, puis
git clone https://github.com/VOTRE_USERNAME/YowYob-Search-Backend.git
cd YowYob-Search-Backend/yowyob-crawler-service
```

2. **Créer une branche feature** :
```bash
git checkout -b feature/amelioration-du-parsing-pdf
# ou
git checkout -b fix/correction-circuit-breaker
```

3. **Développer avec TDD** (Test-Driven Development si possible) :
```bash
# D'abord écrire le test
# Puis implémenter la fonctionnalité
# Puis refactoriser

mvn test  # Les tests doivent passer
```

4. **Suivre nos conventions de code** :
- Utiliser les formatters (Google Java Style)
- Commenter le code complexe
- Javadoc pour les méthodes publiques
- Noms de variables explicites

5. **Commit avec des messages clairs** :
```bash
git commit -m "feat: améliorer l'extraction de texte depuis PDF avec images"
git commit -m "fix: corriger la fuite mémoire dans le circuit breaker"
git commit -m "docs: ajouter exemples d'utilisation du Quality Filter"
```

Nous suivons [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` nouvelle fonctionnalité
- `fix:` correction de bug
- `docs:` documentation
- `refactor:` refactoring sans changement fonctionnel
- `test:` ajout/modification de tests
- `perf:` amélioration de performance
- `chore:` tâches diverses (mise à jour dépendances, etc.)

6. **Push et Pull Request** :
```bash
git push origin feature/amelioration-du-parsing-pdf
```

Puis créer une Pull Request sur GitHub avec :
- Un titre clair
- Une description détaillée de ce que vous avez fait et pourquoi
- Des captures d'écran si c'est visuel
- La référence à l'issue correspondante (si elle existe)

7. **Code Review** : Un ou plusieurs mainteneurs vont reviewer votre code. Soyez patient et ouvert aux feedbacks !

### Setup de l'environnement de développement

Pour un confort de développement optimal :

**IDE recommandé** : IntelliJ IDEA (Ultimate ou Community)

**Plugins utiles** :
- Lombok (pour générer automatiquement getters/setters)
- SonarLint (détection de code smells en temps réel)
- Docker (pour gérer les conteneurs depuis l'IDE)
- Database Tools (pour interagir avec PostgreSQL)

**Configuration IntelliJ** :
```
Settings > Editor > Code Style > Java
  - Import scheme: GoogleStyle (fichier fourni dans /docs/google-java-style.xml)
  
Settings > Build > Compiler
  - Enable annotation processing (pour Lombok et MapStruct)
  
Settings > Tools > Actions on Save
  - Reformat code
  - Optimize imports
  - Run code cleanup
```

**Variables d'environnement pour le développement** :
```bash
# Dans Run Configuration
SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL=DEBUG
POSTGRES_HOST=localhost
ELASTICSEARCH_HOST=localhost
REDIS_HOST=localhost
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

### Architecture de décision (ADRs)

Nous documentons nos décisions architecturales importantes dans des ADRs (Architecture Decision Records). Voici un exemple :

**ADR-001 : Utilisation de Virtual Threads au lieu d'un Thread Pool classique**

**Statut** : Accepté

**Contexte** : Notre Crawler doit gérer des milliers de crawls simultanés. Avec des threads Java classiques, chaque thread consomme ~1MB de mémoire, limitant notre capacité à scaler.

**Décision** : Utiliser les Virtual Threads de Java 21 (Project Loom) au lieu d'un thread pool traditionnel.

**Conséquences** :
- ✅ Nous pouvons créer des millions de Virtual Threads sans souci de mémoire
- ✅ Le code reste simple (style bloquant) mais les performances sont réactives
- ✅ Scaling horizontal plus facile
- ⚠️ Nécessite Java 21 minimum (pas un problème vu que c'est notre cible)
- ⚠️ Quelques bibliothèques legacy peuvent avoir des problèmes avec Virtual Threads (rare)

**Alternatives considérées** :
- Thread pool avec taille fixe : Trop limitant
- Reactive programming pur (Reactor/RxJava) : Code plus complexe
- Approche event-loop (Vert.x, Netty) : Courbe d'apprentissage trop raide

**Date** : 2025-03-15

---

## FAQ et troubleshooting

### Questions fréquentes

**Q : Combien de pages le Crawler peut-il crawler par jour ?**

R : Cela dépend de plusieurs facteurs (vitesse des sites crawlés, qualité de notre connexion internet, ressources allouées), mais avec une configuration standard (2 CPU cores, 3GB RAM), nous crawlons environ **4-6 millions de pages par jour**. Avec plus de ressources, on peut facilement atteindre 10-15 millions.

**Q : Comment le Crawler gère-t-il les sites qui changent leur structure HTML fréquemment ?**

R : Notre Content Parser utilise des heuristiques génériques plutôt que des sélecteurs CSS spécifiques à chaque site. On cherche le plus grand bloc de texte continu, on ignore les éléments avec des ids/classes typiques de navigation (header, nav, footer, sidebar), et on s'adapte automatiquement. Si la structure change, notre extraction reste robuste.

**Q : Le Crawler peut-il gérer des sites avec du JavaScript qui charge le contenu dynamiquement ?**

R : Actuellement, notre Crawler télécharge uniquement le HTML initial. Pour les sites "Single Page Applications" qui chargent tout en JavaScript, nous ne capturons que le shell HTML vide. C'est une limitation connue. Dans la roadmap : intégration d'un headless browser (Playwright) pour crawler ces sites, mais cela consomme beaucoup plus de ressources.

**Q : Comment éviter que le Crawler indexe du contenu privé ou sensible ?**

R : Plusieurs mécanismes :
1. Respect strict du robots.txt
2. Respect de la balise `<meta name="robots" content="noindex">`
3. Authentification jamais tentée (on ne crawle que le contenu public)
4. Filtres sur les patterns d'URL (on ignore /admin/, /private/, etc.)
5. Si un propriétaire de site nous contact, on peut blacklister son domaine immédiatement

**Q : Quelle est la fréquence de recrawl des pages ?**

R : Cela varie selon le type de contenu :
- Sites d'actualités : toutes les 6 heures
- Blogs actifs : toutes les 24 heures
- Sites e-commerce (produits) : toutes les 24-48 heures
- Sites vitrines : toutes les 2-4 semaines
- Pages d'archives : tous les 3-6 mois

**Q : Comment puis-je demander l'indexation prioritaire de mon site ?**

R : Contactez-nous à yowyob.4gi.enspy.promo.2027@gmail.com avec :
- L'URL de votre site
- Une brève description
- Pourquoi c'est important pour les utilisateurs camerounais
  Nous examinerons la demande et pourrons :
- Ajouter votre site en priorité haute
- Crawler plus fréquemment
- Indexer des sections spécifiques

### Problèmes courants et solutions

**Problème : "Le Crawler ne discover pas de nouveaux liens"**

**Symptômes** : La Frontier se vide progressivement, peu de nouvelles URLs découvertes.

**Causes possibles** :
1. Tous les sites dans la Frontier ont été complètement crawlés
2. Les filtres d'URL sont trop restrictifs
3. Les sites ne contiennent pas beaucoup de liens sortants
4. Les robots.txt bloquent trop de chemins

**Solutions** :
```bash
# Vérifier la configuration des filtres
curl http://localhost:8085/api/crawler/config/filters

# Assouplir les filtres si nécessaire
curl -X PATCH http://localhost:8085/api/crawler/config/filters \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d '{
    "urlPatterns": {
      "include": [".*"],  # Accept tout
      "exclude": []
    }
  }'

# Ajouter de nouveaux seeds
curl -X POST http://localhost:8085/api/crawler/seeds \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d '{
    "urls": [
      "https://new-site.cm",
      "https://another-site.cm"
    ]
  }'

# Crawler des sitemaps pour découverte massive
curl -X POST http://localhost:8085/api/crawler/jobs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -d '{
    "name": "Sitemap discovery",
    "type": "SITEMAP",
    "sitemapUrls": [
      "https://cameroon-tribune.cm/sitemap.xml",
      "https://crtv.cm/sitemap_index.xml"
    ]
  }'
```

**Problème : "Certains domaines ne sont jamais crawlés"**

**Symptômes** : Des URLs restent dans la Frontier avec le statut PENDING depuis des jours.

**Diagnostic** :
```bash
# Vérifier l'état du domaine
curl "http://localhost:8085/api/crawler/domains/example.com/status"

# Réponse possible :
{
  "domain": "example.com",
  "status": "CIRCUIT_BREAKER_OPEN",
  "reason": "Too many consecutive failures",
  "lastAttempt": "2025-12-01T10:00:00Z",
  "failureCount": 150,
  "nextRetry": "2025-12-01T11:00:00Z"
}
```

**Solutions** :
```bash
# Si le circuit breaker est ouvert, forcer sa fermeture
curl -X POST "http://localhost:8085/api/crawler/domains/example.com/circuit-breaker/reset" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}"

# Si le domaine est blacklisté par erreur
curl -X DELETE "http://localhost:8085/api/crawler/blacklist/example.com" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}"

# Si le robots.txt bloque tout
curl "http://localhost:8085/api/crawler/domains/example.com/robots-txt"
# Analyser le contenu et voir si c'est légitime
# Si c'est une erreur de leur côté, contacter le webmaster
```

**Problème : "Consommation mémoire excessive"**

**Symptômes** : Le pod Kubernetes est tué pour OutOfMemory, ou la JVM fait du GC constant.

**Diagnostic** :
```bash
# Vérifier l'utilisation mémoire actuelle
curl http://localhost:8085/actuator/metrics/jvm.memory.used

# Heap dump pour analyse
kubectl exec -n yowyob deployment/crawler-service -- \
  jcmd 1 GC.heap_dump /tmp/heap.hprof

# Copier le heap dump localement
kubectl cp yowyob/crawler-pod:/tmp/heap.hprof ./heap.hprof

# Analyser avec Eclipse MAT ou VisualVM
```

**Causes courantes** :
1. Bloom Filter trop grand : Recréer avec une taille appropriée
2. Fuite mémoire dans le parsing : Vérifier qu'on ferme bien tous les streams
3. Trop de connexions HTTP ouvertes : Réduire maxConcurrentConnections
4. Cache Redis client accumule trop de données : Augmenter l'expiration

**Solutions** :
```yaml
# Ajuster les limites mémoire Kubernetes
resources:
  requests:
    memory: "2Gi"
  limits:
    memory: "4Gi"

# Ajuster les paramètres JVM
env:
- name: JAVA_OPTS
  value: "-Xms1g -Xmx3g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError"
```

**Problème : "Indexation dans Elasticsearch échoue systématiquement"**

**Symptômes** : Les pages sont crawlées avec succès mais jamais indexées.

**Diagnostic** :
```bash
# Vérifier les logs du Crawler
kubectl logs -n yowyob deployment/crawler-service | grep "elasticsearch\|indexing"

# Vérifier l'état d'Elasticsearch
curl http://elasticsearch:9200/_cluster/health

# Vérifier les indices
curl http://elasticsearch:9200/_cat/indices?v

# Vérifier les rejections
curl http://elasticsearch:9200/_nodes/stats/thread_pool
```

**Causes possibles** :
1. Elasticsearch cluster en état yellow/red
2. Mapping incompatible (champ avec mauvais type)
3. Documents trop gros (> 100MB)
4. Thread pool saturé (trop de requêtes bulk)

**Solutions** :
```bash
# Si le cluster est red, identifier les shards problématiques
curl http://elasticsearch:9200/_cat/shards?v | grep UNASSIGNED

# Réallouer les shards
curl -X POST http://elasticsearch:9200/_cluster/reroute \
  -H "Content-Type: application/json" \
  -d '{
    "commands": [{
      "allocate_empty_primary": {
        "index": "yowyob-documents",
        "shard": 0,
        "node": "es-node-1",
        "accept_data_loss": true
      }
    }]
  }'

# Si le mapping est incompatible, créer un nouvel index avec le bon mapping
# et réindexer
curl -X POST http://elasticsearch:9200/_reindex \
  -H "Content-Type: application/json" \
  -d '{
    "source": {"index": "yowyob-documents"},
    "dest": {"index": "yowyob-documents-v2"}
  }'
```

## Roadmap et évolutions futures

### Fonctionnalités en développement actif

**Support JavaScript-heavy sites** : Intégration de Playwright pour crawler les sites Single Page Applications. Cela nous permettra d'indexer correctement les sites React/Vue/Angular modernes.

Statut : Prototype fonctionnel, optimisation en cours
Timeline : Q1 2026

**Machine Learning pour la priorisation** : Au lieu de notre algorithme de scoring manuel, entraîner un modèle ML qui apprend quelles pages sont importantes basé sur le comportement des utilisateurs (clics, temps passé, etc.).

Statut : Collecte de données d'entraînement
Timeline : Q2 2026

**Crawler d'images** : Actuellement nous ignorons largement les images. Ajouter un pipeline pour télécharger, analyser (computer vision), et indexer les images.

Statut : Design phase
Timeline : Q3 2026

### Améliorations prévues court terme

**Better duplicate detection** : Implémenter SimHash ou MinHash pour une détection de duplication plus sophistiquée que notre simple hash SHA-256.

**Sitemap auto-discovery** : Détecter automatiquement les sitemaps (en cherchant dans robots.txt, /sitemap.xml, /sitemap_index.xml) sans qu'on ait besoin de les configurer manuellement.

**Crawl scheduling intelligent** : Utiliser des algorithmes prédictifs pour déterminer la meilleure heure pour crawler chaque site (quand il est le moins chargé, quand son contenu change le plus).

**API publique de soumission** : Permettre aux webmasters de soumettre leurs sites directement via une API publique (avec rate limiting et validation).

### Vision long terme

**Crawling distribué global** : Déployer des instances du Crawler dans différentes régions du monde pour crawler depuis différents points de vue géographiques.

**Real-time indexing** : Intégration avec WebSub/PubSubHubbub pour être notifié immédiatement quand un site publie nouveau contenu, permettant une indexation quasi instantanée.

**Semantic understanding** : Utiliser des LLMs (Large Language Models) pour comprendre sémantiquement le contenu et enrichir notre indexation avec des métadonnées générées par IA.

**Blockchain verification** : Pour les contenus sensibles (articles de presse, documents officiels), stocker des preuves cryptographiques sur blockchain pour prouver qu'un contenu existait à une date donnée.

## Support et ressources

### Documentation additionnelle

Tous nos documents sont dans le dossier `/docs/` :

- `CRAWLER-ARCHITECTURE.md` : Architecture détaillée avec diagrammes
- `CRAWLER-API.md` : Documentation complète de l'API REST
- `CRAWLER-DEPLOYMENT.md` : Guide de déploiement pas à pas
- `CRAWLER-MONITORING.md` : Configuration monitoring et alertes
- `CRAWLER-TROUBLESHOOTING.md` : Guide de dépannage complet
- `CRAWLER-PERFORMANCE.md` : Benchmarks et optimisations

### Où obtenir de l'aide

**GitHub Issues** : Pour les bugs et feature requests
https://github.com/BrianBrusly/YowYob-Search-Backend/issues

Avant de créer une issue, vérifiez qu'elle n'existe pas déjà. Utilisez les labels appropriés :
- `bug` : Quelque chose ne fonctionne pas
- `enhancement` : Nouvelle fonctionnalité ou amélioration
- `documentation` : Documentation manquante ou incorrecte
- `question` : Question générale

**GitHub Discussions** : Pour les questions et discussions générales
https://github.com/BrianBrusly/YowYob-Search-Backend/discussions

**Email** : yowyob.4gi.enspy.promo.2027@gmail.com
Pour les questions confidentielles ou les partenariats

**Slack** (contributeurs réguliers) : Demandez une invitation via GitHub

### Ressources d'apprentissage

**Web Crawling en général** :
- "Web Crawling and Data Mining with Apache Nutch" (livre)
- "Mining the Web" par Soumen Chakrabarti
- "Introduction to Information Retrieval" - Chapitre sur le crawling

**Technologies utilisées** :
- Spring Boot : https://spring.io/guides
- Spring WebFlux : https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html
- JSoup : https://jsoup.org/cookbook/
- Apache Tika : https://tika.apache.org/
- Quartz Scheduler : https://www.quartz-scheduler.org/documentation/
- Java Virtual Threads : https://openjdk.org/jeps/444

**Patterns et best practices** :
- "Building Microservices" par Sam Newman
- "Release It!" par Michael Nygard (résilience)
- "Site Reliability Engineering" par Google (monitoring, alerting)

## Remerciements et crédits

Ce Crawler Service a été développé avec passion par l'équipe YowYob dans le cadre de notre projet de 4ème année à l'École Nationale Supérieure Polytechnique de Yaoundé (ENSPY).

**Équipe principale** :
- Brian Brusly
- [Autres membres] - Contributions diverses

**Technologies open-source utilisées** :
Un immense merci aux communautés derrière Spring Boot, Elasticsearch, PostgreSQL, Redis, Kafka, JSoup, Apache Tika, et toutes les autres bibliothèques fantastiques que nous utilisons.

**Inspiration** :
Notre Crawler s'inspire des techniques utilisées par les grands moteurs de recherche (Google, Bing) et des crawlers open-source comme Apache Nutch et Heritrix.

---

## Licence

Ce projet n'est pas sous licence.

---

**Le Crawler Service** - *L'explorateur infatigable du web camerounais*

🕷️ **Crawling intelligent, respectueux et efficace depuis 2025**

---

*Pour toute question ou suggestion, n'hésitez pas à ouvrir une issue sur GitHub ou à nous contacter directement !*