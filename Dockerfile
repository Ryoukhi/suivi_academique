# === Stage 1 : Construction avec Maven ===
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers du projet
COPY pom.xml .
COPY src ./src

# Construire l'application
RUN mvn clean package -DskipTests

# === Stage 2 : Image d'exécution légère ===
FROM eclipse-temurin:21-jre-alpine

# Définir le répertoire de travail
WORKDIR /app

# === Mise à jour des packages système pour corriger les vulnérabilités ===
RUN apk update && apk upgrade && \
    apk add --no-cache \
        gnupg=2.4.9-r0 \
        libpng=1.6.53-r0 && \
    rm -rf /var/cache/apk/*

# Copier le .jar depuis l'image de build
COPY --from=build /app/target/suivi_academique-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port d'écoute de l'application
EXPOSE 8080 8089

# Lancer le service avec déchiffrement actif
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
