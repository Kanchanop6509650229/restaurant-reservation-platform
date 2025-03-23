# Kafka Microservices Demo

This project demonstrates a simple Kafka-based microservices architecture using Spring Boot. The application consists of a producer service, a consumer service, and a Kafka infrastructure setup with Docker.

## Project Structure

- **KafkaProducerApp**: Spring Boot application that produces messages to Kafka topics
- **KafkaConsumer**: Spring Boot application that consumes messages from Kafka topics
- **KafkaDocker**: Docker Compose configuration for setting up the Kafka infrastructure

## Prerequisites

- Java 17+ (Java 23 recommended as specified in the producer POM)
- Docker and Docker Compose
- Maven

## Getting Started

### 1. Start the Kafka Infrastructure

```bash
cd KafkaDocker
docker-compose up -d
```

This will start:
- Zookeeper
- Kafka broker
- MySQL database
- Kafdrop (Kafka UI for monitoring) accessible at http://localhost:9000

### 2. Build and Run the Consumer Service

```bash
cd KafkaConsumer
./mvnw clean package
java -jar target/KafkaConsumer-0.0.1.jar
```

### 3. Build and Run the Producer Service

```bash
cd kafkaproducerapp
./mvnw clean package
java -jar target/kafkaproducerapp-0.0.1-SNAPSHOT.jar
```

## Testing the Application

Send a message to Kafka using the producer's REST endpoint:

```bash
curl "http://localhost:8080/kafka/send?message=Hello%20Kafka"
```

The consumer service should log the received message in its console output.

## Available Endpoints

### Producer Service:
- `GET /kafka/send?message={message}` - Send a string message to the "test-topic" Kafka topic
- `POST /kafka/postItem` - Send a JSON model object to Kafka (currently commented out in the code)

## Monitoring

You can monitor Kafka topics and messages using Kafdrop at http://localhost:9000

## Configuration

### Producer Configuration
- Bootstrap server: localhost:9092
- Key/Value serializer: StringSerializer

### Consumer Configuration
- Bootstrap server: kafka:9092 
- Group ID: my-group
- Key/Value deserializer: StringDeserializer
- Auto-offset-reset: earliest

## Docker Configuration

The docker-compose.yml file includes services for:
- Zookeeper
- Kafka
- Spring Boot applications
- MySQL database
- Kafdrop UI

## Notes

The default topics used in the application are:
- test-topic: For simple string messages
- models-topic: For JSON model objects (commented out in the code)