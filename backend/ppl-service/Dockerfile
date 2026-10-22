# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy gradle wrapper and configuration
COPY gradlew gradlew
COPY gradle gradle
COPY buildSrc buildSrc
COPY settings.gradle.kts build.gradle.kts ./
COPY grpc-proto grpc-proto
COPY ppl-service ppl-service

# Make wrapper executable and build bootJar
RUN chmod +x gradlew && ./gradlew :ppl-service:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/ppl-service/build/libs/ppl-service.jar app.jar

EXPOSE 8082 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
