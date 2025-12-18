# === Stage 1 : Construction avec Maven ===
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers du projet
COPY pom.xml .
COPY src ./src

RUN  mvn clean package -DskipTests

# === Stage 2 : Image d'exécution légère ===
FROM eclipse-temurin:21-jre-alpine

# Définir le repertoire de travail dans l'image d'execution
WORKDIR /app

# Copier le .jar depuis l'image de build
COPY --from=build /app/target/suivi_academique-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port d'ecoute de l'application
EXPOSE 8080 8089

# Lancer le service avec déchiffrement actif
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]