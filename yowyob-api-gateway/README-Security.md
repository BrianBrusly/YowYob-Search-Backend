#  Guide de Sécurité - Configuration API Gateway

## 📋 Instructions pour les Développeurs

### Configuration des Variables d'Environnement

1. **Copier le template :**
   ```bash
   cp .env.example .env
   ```

2. **Obtenir les vraies valeurs :**
    - Contactez **Brian Brusly** (Responsable Technique)
    - Via WhatsApp/Teams : +237 6XX XX XX XX
    - Demandez le fichier `.env.local.example` avec les vraies valeurs

3. **Remplir votre fichier `.env` :**
   ```env
   REDIS_PASSWORD=votre_mot_de_passe_redis
   JWT_SECRET=votre_clé_jwt_secrète
   # etc...
   ```

## ⚠ Règles de Sécurité IMPORTANTES

### 1. Jamais dans Git :
```yaml
#  MAUVAIS (mot de passe en dur) :
password: "monMotDePasse123"

#  BON (variable d'environnement) :
password: ${REDIS_PASSWORD}
```

### 2. Fichiers sécurisés :
- **`application.yml`** : Contient les références aux variables `${VAR}`
- **`application-dev.yml`** : Valeurs par défaut pour le développement
- **`application-prod.yml`** : Aucune valeur par défaut, tout vient des variables d'env
- **`.env`** : Fichier personnel (dans `.gitignore`)

### 3. Variables sensibles :
```env
# Variables OBLIGATOIRES :
REDIS_PASSWORD=      # Mot de passe Redis
JWT_SECRET=          # Clé secrète JWT (min 256 bits)
SMTP_PASSWORD=       # Mot de passe SMTP

# Variables OPTIONNELLES avec valeurs par défaut :
API_GATEWAY_PORT=8080
LOG_LEVEL=INFO
```

##  En Cas d'Exposition Accidentelle

Si vous avez commité des mots de passe par erreur :

1. **Changez immédiatement** tous les mots de passe exposés
2. **Supprimez le commit sensible :**
   ```bash
   git filter-branch --force --index-filter \
     "git rm --cached --ignore-unmatch *.yml" \
     --prune-empty --tag-name-filter cat -- --all
   ```
3. **Force push :**
   ```bash
   git push origin --force --all
   ```

## 📞 Contacts Sécurité

- **Brian Brusly** : Responsable Technique
- **Chef de Projet** : Via groupe WhatsApp
- **Urgences** : WhatsApp groupe "YowYob Security"

##  Dépannage

### Problème : "Variable not defined"
```bash
# Vérifier que le fichier .env existe
ls -la .env

# Vérifier le contenu
cat .env | grep REDIS_PASSWORD

# Exporter les variables manuellement
export REDIS_PASSWORD="votre_mot_de_passe"
```

### Problème : "Connection refused to Redis"
```bash
# Vérifier que Redis tourne
redis-cli ping

# Vérifier le mot de passe
redis-cli -a votre_mot_de_passe ping
```

---
*Équipe YowYob 4GI-ENSPY Promo 2027*
```

---

## **📦 FICHIER .env.example POUR API GATEWAY**

**Chemin :** `yowyob-api-gateway/.env.example`

```env
# ============================================
# YOWYOB API GATEWAY - VARIABLES D'ENVIRONNEMENT
# ============================================
# Template pour le développement
# Copier en tant que .env et remplir avec les vraies valeurs
# ============================================

# =======================
# SERVER CONFIGURATION
# =======================
API_GATEWAY_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# =======================
# DATABASE CONFIGURATION
# =======================
# PostgreSQL (pour Eureka, etc.)
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=yowyob_gateway
POSTGRES_USER=DEMANDER_A_BRIAN_BRUSLY
POSTGRES_PASSWORD=DEMANDER_A_BRIAN_BRUSLY

# =======================
# REDIS CONFIGURATION
# =======================
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=DEMANDER_A_BRIAN_BRUSLY
REDIS_DATABASE=0
REDIS_TIMEOUT=2000
REDIS_MAX_TOTAL=8
REDIS_MAX_IDLE=8
REDIS_MIN_IDLE=0

# =======================
# JWT CONFIGURATION
# =======================
JWT_SECRET=DEMANDER_A_BRIAN_BRUSLY
JWT_EXPIRATION_MS=900000
JWT_REFRESH_EXPIRATION_MS=604800000
JWT_ISSUER=yowyob-search
JWT_AUDIENCE=yowyob-clients

# =======================
# CORS CONFIGURATION
# =======================
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
CORS_ALLOWED_HEADERS=*
CORS_ALLOW_CREDENTIALS=true
CORS_MAX_AGE=3600

# =======================
# RATE LIMITING
# =======================
RATE_LIMIT_REQUESTS_PER_MINUTE=100
RATE_LIMIT_BURST_CAPACITY=20

# =======================
# MONITORING
# =======================
PROMETHEUS_ENABLED=true
LOG_LEVEL_ROOT=INFO
LOG_LEVEL_COM_YOWYOB=DEBUG
LOG_LEVEL_ORG_SPRINGFRAMEWORK_WEB=INFO

# =======================
# EUREKA (Service Discovery)
# =======================
EUREKA_ENABLED=false
EUREKA_SERVER_URL=http://localhost:8761/eureka/

# =======================
# CONTACT
# =======================
CONTACT_EMAIL=contact@yowyob.com

# =======================
# NOTES
# =======================
# 1. Contactez Brian Brusly pour obtenir les vraies valeurs
# 2. NE JAMAIS COMMITTER le fichier .env rempli
# 3. En production, utilisez des secrets Kubernetes/Vault
```

---