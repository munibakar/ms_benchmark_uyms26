# Multi-stage build for User Service

# Stage 1: Build (Use Debian-based image for protobuf compatibility)
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run (Alpine is fine for runtime)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose ports (REST: 9000, gRPC: 9090)
EXPOSE 9000 9090

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:9000/api/users/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

