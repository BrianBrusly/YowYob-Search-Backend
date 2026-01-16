# Dockerfile multi-stage pour l'API Gateway YowYob
# 
# @author YowYob Team 4GI-ENSPY Promo 2027
# @author HEUDEP DJANDJA BRIAN B 22P405
# @version 1.0.0

# ============================================================
# ÉTAPE 1: Build
# ============================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Définir les arguments de build
ARG BUILD_VERSION=1.0.0
ARG BUILD_TIMESTAMP
ARG BUILD_COMMIT

# Variables d'environnement pour Maven
ENV MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=512m"
ENV MAVEN_HOME="/usr/share/maven"

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY yowyob-common/pom.xml yowyob-common/
COPY yowyob-api-gateway/pom.xml yowyob-api-gateway/

# Télécharger les dépendances (création d'une couche cacheable)
RUN mvn dependency:go-offline -B

# Copier les sources
COPY yowyob-common/src yowyob-common/src
COPY yowyob-api-gateway/src yowyob-api-gateway/src

# Construire l'application avec les arguments de build
RUN mvn clean package -DskipTests -pl yowyob-api-gateway -am \
    -Dbuild.version=${BUILD_VERSION} \
    -Dbuild.timestamp=${BUILD_TIMESTAMP} \
    -Dbuild.commit=${BUILD_COMMIT} \
    -Dmaven.test.skip=true

# ============================================================
# ÉTAPE 2: Runtime
# ============================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

# Métadonnées
LABEL maintainer="yowyob.4gi.enspy.promo.2027@gmail.com"
LABEL version="1.0.0"
LABEL description="API Gateway pour la plateforme YowYob Search"
LABEL org.opencontainers.image.source="https://github.com/yowyob/yowyob-search-backend"

# Installer les outils système nécessaires
RUN apk add --no-cache \
    curl \
    tzdata \
    bash \
    busybox-extras \
    && rm -rf /var/cache/apk/*

# Définir le fuseau horaire
ENV TZ=Africa/Douala
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Créer un utilisateur non-root pour plus de sécurité
RUN addgroup -g 1001 -S gateway && \
    adduser -u 1001 -S gateway -G gateway

# Créer les répertoires nécessaires
RUN mkdir -p /app && \
    mkdir -p /app/logs && \
    mkdir -p /app/keys && \
    mkdir -p /app/config && \
    mkdir -p /tmp/heapdumps && \
    chown -R gateway:gateway /app && \
    chown -R gateway:gateway /tmp/heapdumps

# Définir le répertoire de travail
WORKDIR /app

# Copier l'application depuis l'étape de build
COPY --from=build --chown=gateway:gateway /app/yowyob-api-gateway/target/yowyob-api-gateway-*.jar app.jar

# Copier les fichiers de configuration
COPY --chown=gateway:gateway yowyob-api-gateway/src/main/resources/application-prod.yml /app/config/
COPY --chown=gateway:gateway yowyob-api-gateway/src/main/resources/bootstrap.yml /app/config/
COPY --chown=gateway:gateway yowyob-api-gateway/src/main/resources/logback-spring.xml /app/config/

# Copier les scripts d'entrée
COPY --chown=gateway:gateway docker-entrypoint.sh /app/
RUN chmod +x /app/docker-entrypoint.sh

# Copier les clés JWT (devraient être montées via volumes en production)
COPY --chown=gateway:gateway keys/ /app/keys/

# Changer vers l'utilisateur non-root
USER gateway

# Exposer le port de l'application
EXPOSE 8080

# Exposer le port pour les métriques Prometheus (si nécessaire)
EXPOSE 9090

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""
ENV SPRING_CONFIG_LOCATION="file:/app/config/"
ENV SPRING_CLOUD_BOOTSTRAP_LOCATION="file:/app/config/bootstrap.yml"
ENV LOGGING_CONFIG="file:/app/config/logback-spring.xml"
ENV MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE="health,info,metrics,prometheus,gateway"
ENV MANAGEMENT_METRICS_EXPORT_PROMETHEUS_ENABLED="true"
ENV MANAGEMENT_HEALTH_PROBES_ENABLED="true"

# Optimisations JVM pour les conteneurs
ENV JVM_OPTS="-XX:+UseContainerSupport \
              -XX:MaxRAMPercentage=75.0 \
              -XX:+UseG1GC \
              -XX:MaxGCPauseMillis=200 \
              -XX:+UseStringDeduplication \
              -XX:+HeapDumpOnOutOfMemoryError \
              -XX:HeapDumpPath=/tmp/heapdumps \
              -XX:ErrorFile=/tmp/hs_err_pid%p.log \
              -XshowSettings:vm \
              -Djava.security.egd=file:/dev/./urandom \
              -Dfile.encoding=UTF-8 \
              -Duser.timezone=Africa/Douala \
              -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
              -Dspring.config.location=${SPRING_CONFIG_LOCATION} \
              -Dspring.cloud.bootstrap.location=${SPRING_CLOUD_BOOTSTRAP_LOCATION} \
              -Dlogging.config=${LOGGING_CONFIG}"

# Health check (vérifie que l'application répond)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health/liveness || exit 1

# Script d'entrée
ENTRYPOINT ["/app/docker-entrypoint.sh"]

# Commande par défaut
CMD ["java", "-jar", "app.jar"]