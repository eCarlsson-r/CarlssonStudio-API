# ── Stage 1: Build ───────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copy pom first to cache the dependency layer —
# dependencies only re-download when pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests -B

# ── Stage 2: Runtime ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

# Run as non-root user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Proposals storage directory (mount a volume here in Coolify)
RUN mkdir -p /app/proposals && chown spring:spring /app/proposals

# Copy the fat jar from the build stage
COPY --from=build /build/target/*.jar app.jar

USER spring

EXPOSE 8080

# Container-aware JVM: respects memory limits set by Coolify/Docker
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]