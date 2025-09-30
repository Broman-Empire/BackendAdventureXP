# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Casher dependencies fra kopi af pom.xml
COPY pom.xml .
RUN mvn dependency:resolve

# Kopierer kildekode
COPY src ./src

# Bygger .jar uden at køre tests
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Kopierer .jar fra build stage
COPY --from=build /app/target/*.jar app.jar

# Kommando for at køre app'en
ENTRYPOINT ["java", "-jar", "app.jar"]
