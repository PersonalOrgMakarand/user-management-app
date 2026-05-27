# ---- Build stage ----------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Cache dependencies first
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Build the application
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---- Runtime stage --------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Run as non-root for safety
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /workspace/target/app.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
