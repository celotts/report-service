FROM openjdk:17-jdk-slim as build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Imagen final
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar el .jar compilado
COPY --from=build /app/target/*.jar app.jar

# Copiar scripts
COPY wait-for-it.sh wait-for-it.sh
COPY entrypoint.sh entrypoint.sh

# Instalar bash y curl
RUN apt-get update && apt-get install -y curl bash && rm -rf /var/lib/apt/lists/*

# 👇 ¡La línea mágica que faltaba!
RUN chmod +x wait-for-it.sh entrypoint.sh

EXPOSE 8083

# ENTRYPOINT lo defines en docker-compose, así que no pongas nada aquí.