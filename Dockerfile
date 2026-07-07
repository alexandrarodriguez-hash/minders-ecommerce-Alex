# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Descargar dependencias para optimizar el cache de Docker
RUN mvn dependency:go-offline
COPY src ./src
# Compilar el proyecto saltando los tests para un despliegue más rápido
RUN mvn package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
