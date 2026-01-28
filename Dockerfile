# Build stage
# Utiliser Maven et JDK pour construire l'app
FROM maven:3.9.1-eclipse-temurin-20 AS build

# Définir le repértoire de traville
WORKDIR /app

# Copier les fichiers pom.xml et sources
COPY pom.xml .
COPY src ./src

# Construire l'app et créer le jar
RUN mvn clean package -DskipTests

# Runtime stage
# Créer l'image finale légère pour exécuter Spring Boot
FROM eclipse-temurin:20-jdk
WORKDIR /app

# Copier le jar depuis build stage
COPY --from=build /app/target/*.jar events.jar

# exposer le port sur lequel Spring Boot tourne
EXPOSE 8080

# exécuter le jar
ENTRYPOINT ["java", "-jar", "events.jar"]