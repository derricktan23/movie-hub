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
COPY ppl-service ppl-service

# Make wrapper executable and build bootJar with BuildKit cache mount
RUN --mount=type=cache,target=/root/.gradle \
    chmod +x gradlew && ./gradlew :ppl-service:bootJar --no-daemon --build-cache -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/ppl-service/build/libs/ppl-service.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
