# syntax=docker/dockerfile:1
# Build stage
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy gradle wrapper and configuration
COPY gradlew gradlew
COPY gradle gradle
COPY buildSrc buildSrc
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY grpc-proto grpc-proto
COPY movie-service movie-service

# Make wrapper executable and build bootJar with BuildKit cache mount
RUN --mount=type=cache,target=/root/.gradle \
    chmod +x gradlew && ./gradlew :movie-service:bootJar --no-daemon --build-cache -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/movie-service/build/libs/movie-service.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
