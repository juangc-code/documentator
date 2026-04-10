# Multi-stage build for Spring Boot application
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Install git for the application
RUN apk add --no-cache git

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Create a non-root user
RUN addgroup -g 1001 documentator && \
    adduser -D -u 1001 -G documentator documentator

# Change ownership of the app directory
RUN chown -R documentator:documentator /app

# Switch to non-root user
USER documentator

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
