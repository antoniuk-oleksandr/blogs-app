# Stage 1: Build
FROM gradle:8-jdk21-alpine AS builder

WORKDIR /app

# Copy Gradle files & source
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src

# Build
RUN ./gradlew bootJar --no-daemon -x test \
    && mv build/libs/*.jar app.jar

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/app.jar .

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
