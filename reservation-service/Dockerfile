# Build stage
FROM openjdk:17-slim
WORKDIR /app

# Copy pom.xml and download dependencies
COPY target/reservation-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8083

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]