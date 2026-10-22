# Build stage
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy gradle wrapper and configuration
COPY gradlew gradlew
COPY gradle gradle
COPY buildSrc buildSrc
COPY settings.gradle.kts build.gradle.kts ./
COPY grpc-proto grpc-proto
COPY movie-service movie-service

# Make wrapper executable and build bootJar
RUN chmod +x gradlew && ./gradlew :movie-service:bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/movie-service/build/libs/movie-service.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
