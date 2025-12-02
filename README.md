# ============================================
# YOWYOB SEARCH BACKEND - GIT IGNORE CONFIGURATION
# ============================================
# Auteur: YowYob Team 4GI-ENSPY Promo 2027
# Date: 01-12-2025
# Description: Configuration de sécurité pour éviter le commit de données sensibles
# ============================================

# =======================
# DONNÉES SENSIBLES - ABSOLUMENT À NE PAS COMMITTER
# =======================

# Fichiers d'environnement (sauf .env.example)
.env
.env.local
.env.*.local
.secrets/
credentials/
passwords/
*.env
*.env.*
!.env.example  # On garde SEULEMENT le template

# Clés cryptographiques et secrets
config/keys/
!config/keys/README.md  # On garde seulement le README
*.key
*.pem
*.p12
*.pfx
*.jks
*.keystore
*.crt
*.cert
*.pub
*.priv
*.secret
*.token
vapid-keys.json
**/private.pem
**/public.pem

# Fichiers de configuration sensible
application-prod.yml
application-prod.properties
**/application-prod.*
**/secrets/
**/credentials/

# =======================
# BACKEND - JAVA/SPRING (Multi-module Maven)
# =======================

# Fichiers de compilation Maven
**/target/
**/bin/
**/build/
**/out/
!**/pom.xml
!.mvn/
mvnw
mvnw.cmd

# Couverture de code
**/coverage/
**/.jacoco/
**/*.exec
**/jacoco.exec
**/*.gcno
**/*.gcda
**/*.lcov

# Fichiers de compilation spécifiques
**/*.class
**/*.jar
**/*.war
**/*.ear

# Tests et logs
**/test-output/
**/test-results/
**/logs/
**/*.log
**/log/
**/logging/

# Fichiers temporaires
**/tmp/
**/temp/
**/*.tmp
**/*.temp

# =======================
# IDE ET ÉDITEURS
# =======================

# IntelliJ IDEA
.idea/
*.iml
*.iws
*.ipr
out/
**/.idea/
**/*.iml

# Eclipse
.classpath
.project
.settings/
.metadata/
bin/
tmp/
*.tmp
*.bak
*.swp

# VS Code
.vscode/
.vscode/*
!.vscode/settings.json
!.vscode/tasks.json
!.vscode/launch.json
!.vscode/extensions.json
*.code-workspace
**/.vscode/

# NetBeans
nbproject/
nbactions.xml
nb-configuration.xml

# Vim
*.swp
*.swo
*~

# Emacs
*~
\#*\#
/.emacs.desktop
/.emacs.desktop.lock
*.elc
auto-save-list
tramp
.\#*

# =======================
# SYSTÈME D'EXPLOITATION
# =======================

# macOS
.DS_Store
.AppleDouble
.LSOverride
Icon?
._*
.Spotlight-V100
.Trashes
ehthumbs.db
Thumbs.db

# Windows
Thumbs.db
ehthumbs.db
Desktop.ini
$RECYCLE.BIN/
*.cab
*.msi
*.msm
*.msp

# Linux
*~
.fuse_hidden*
.directory
.Trash-*
.nfs*

# =======================
# INFRASTRUCTURE ET DOCKER
# =======================

# Docker
docker-compose.override.yml
docker-compose.prod.yml
**/docker-compose.*.yml
**/.dockerignore
docker-data/

# Terraform
.terraform/
*.tfstate
*.tfstate.*
*.tfvars
.terraform.lock.hcl
terraform.tfstate.backup

# Kubernetes
kubeconfig
*.kubeconfig
**/k8s/temp/

# =======================
# DONNÉES ET SAUVEGARDES
# =======================

# Données de développement
data/
**/data/
*.data
*.db
*.sqlite
*.sqlite3
**/h2/

# Sauvegardes de base de données
backups/
*.sql
*.dump
*.backup
*.bak

# Elasticsearch
elasticsearch-data/
**/elasticsearch/**/data/
**/es-data/

# Redis
redis-data/
dump.rdb
appendonly.aof

# Kafka
kafka-data/
zookeeper-data/

# Postgres
postgres-data/
postgis-data/

# =======================
# FRONTEND - NEXT.JS/TYPESCRIPT
# =======================
# Note: Pour séparation claire frontend/backend

# Next.js
.next/
.next/cache/
.next/standalone/
.next/server/
.next/static/
.next/trace

# Dépendances Node
node_modules/
npm-debug.log*
yarn-debug.log*
yarn-error.log*
pnpm-debug.log*
.pnpm-store/

# Build frontend
dist/
out/
.build/
.next/
.next/**

# Environnement frontend
.env.local
.env.development.local
.env.test.local
.env.production.local
.env.*.local

# Cache frontend
.cache/
*.cache
.cache/

# Tests frontend
coverage/
.nyc_output/
*.lcov

# =======================
# DOCUMENTATION GÉNÉRÉE
# =======================

# Documentation
_site/
.sass-cache/
.jekyll-cache/
.jekyll-metadata
docs/_build/
docs/_site/

# JavaDoc
**/apidocs/
**/javadoc/

# =======================
# AUTRES
# =======================

# Packages
*.7z
*.dmg
*.gz
*.iso
*.rar
*.tar
*.zip

# Fichiers de lock (garder pour le backend Maven)
# Note: package-lock.json pour Node.js seulement
package-lock.json
yarn.lock
pnpm-lock.yaml
composer.lock
Cargo.lock
Pipfile.lock
poetry.lock

# Profiling
profiling/
*.hprof
*.heapdump

# Fichiers de cours et temporaires
*.exe
*.dll
*.so
*.dylib
*.test
*.out

# Fichiers de benchmark
benchmark-results/
*.bench

# =======================
# EXCEPTIONS SPÉCIFIQUES
# =======================
# Fichiers qu'on DOIT garder dans le dépôt

!README.md
!LICENSE
!CONTRIBUTING.md
!.gitignore
!.env.example  # IMPORTANT: Le template d'environnement
!pom.xml  # Fichiers Maven
!**/pom.xml
!.mvn/wrapper/maven-wrapper.properties
!mvnw
!mvnw.cmd
!scripts/**  # On garde les scripts
!docs/**/*.md  # On garde la documentation Markdown

# ============================================
# NOTES IMPORTANTES
# ============================================
#
# 1. Le fichier `.env.example` doit rester dans le dépôt
#    C'est le template que les développeurs copient
#
# 2. Chaque développeur doit créer son propre `.env` localement
#    et le remplir avec les vraies valeurs fournies par l'équipe
#
# 3. En production, les variables sont injectées via:
#    - Kubernetes Secrets
#    - Docker Compose .env files (non commités)
#    - Variables d'environnement du système
#
# 4. Structure recommandée:
#    YowYob-Search-Backend/
#    ├── .env.example     (template public)
#    ├── .env            (local, gitignored)
#    ├── .gitignore      (ce fichier)
#    └── ...
#
# ============================================