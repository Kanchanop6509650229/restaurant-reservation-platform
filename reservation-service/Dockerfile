# Reservation Service Dockerfile
# Purpose: Builds a container image for the reservation service component
# Base: Uses OpenJDK 17 slim image to reduce size while providing Java runtime

FROM openjdk:17-slim

# Set the working directory for all subsequent instructions
WORKDIR /app

# Copy the compiled JAR file from the build context into the container
# The JAR contains all dependencies, configurations, and application code
COPY target/reservation-service-0.0.1-SNAPSHOT.jar app.jar

# Document that the container listens on port 8083 at runtime
# This is the default port configured for the reservation service
EXPOSE 8083

# Define the command to run when the container starts
# Running the Java application with no additional JVM arguments
ENTRYPOINT ["java", "-jar", "app.jar"]