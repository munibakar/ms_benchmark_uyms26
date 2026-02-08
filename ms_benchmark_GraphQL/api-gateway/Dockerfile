# Multi-stage build for API Gateway
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8765

# Health check
HEALTHCHECK --interval=30s --timeout=3s --retries=3 --start-period=40s \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8765/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

