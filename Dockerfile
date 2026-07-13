# Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src src
RUN mvn -q -B package -DskipTests

# Runtime
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
USER app

# 8080 = API pública · 8081 = actuator (health/metrics) — mantenha 8081 fora do ingress público
EXPOSE 8080 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
