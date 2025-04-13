[**English**](./README.md) | [ภาษาไทย](./README.th.md)

---

# Reservation Service

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)

Part of the Restaurant Reservation Platform microservice architecture, the Reservation Service manages all aspects of restaurant reservations including creation, confirmation, cancellation, and updates.

## Overview

The Reservation Service is a core component of the Restaurant Reservation Platform, responsible for managing the entire reservation lifecycle. This service handles reservation creation, confirmation, modification, cancellation, and completion, as well as table assignment and menu item selection.

## Features

- **Complete Reservation Lifecycle Management**
  - Create, confirm, update, and cancel reservations
  - Automatic status transitions based on time
  - Comprehensive history tracking for all changes

- **Table Management**
  - Automatic table assignment based on party size and availability
  - Table status tracking and synchronization
  - Release tables when reservations are completed or cancelled

- **Menu Integration**
  - Add menu items to reservations
  - Maintain local cache of menu items from Kitchen Service
  - Validate menu items belong to the correct restaurant

- **Quota Management**
  - Track and enforce reservation quotas by time slot
  - Prevent overbooking during peak hours

- **Scheduling**
  - Automatic processing of expired reservations
  - Mark past reservations as completed
  - Clean up old reservation data

## Technology Stack

- **Java 17**
- **Spring Boot** - Main application framework
- **Spring Data JPA** - Database interactions
- **Spring Kafka** - Event-driven messaging
- **Spring Security** - Authentication and authorization
- **MySQL** - Primary database (configurable)
- **H2** - In-memory database for development/testing
- **JWT** - For secure authentication
- **Swagger/OpenAPI** - API documentation

## Architecture

The Reservation Service follows a layered architecture:

- **API Layer**: REST controllers for handling HTTP requests
- **Service Layer**: Business logic and transaction management
- **Repository Layer**: Data access and persistence
- **Domain Layer**: Entity models and business rules
- **Kafka Layer**: Event producers and consumers for inter-service communication

## Prerequisites

- JDK 17
- Maven 3.6+
- MySQL 8+ (or compatible database)
- Apache Kafka 3.0+

## Reservation Flow

### Creating a Reservation

1. **Request Validation**
   - Validate user input (party size, date/time, etc.)
   - Check if restaurant exists and is active
   - Verify reservation time is within restaurant operating hours (via Kafka)

2. **Table Assignment**
   - Send request to find available table (via Kafka)
   - Receive table assignment response
   - If no table is available, reject the reservation

3. **Reservation Creation**
   - Create reservation record with PENDING status
   - Set confirmation deadline (default: 15 minutes)
   - Update reservation quota for the time slot
   - Publish reservation created event (via Kafka)

4. **Menu Item Processing** (Optional)
   - Validate menu items belong to the restaurant
   - Add selected menu items to the reservation

### Confirming a Reservation

1. **Authorization Check**
   - Verify user is the reservation creator

2. **Status Update**
   - Change status from PENDING to CONFIRMED
   - Record confirmation timestamp
   - Create history record

3. **Event Publishing**
   - Publish reservation confirmed event (via Kafka)

### Automatic Processing

1. **Expired Reservations**
   - Scheduled task runs every minute
   - Identify reservations past confirmation deadline
   - Cancel expired reservations
   - Release assigned tables
   - Update reservation quota

2. **Completed Reservations**
   - Automatically mark past confirmed reservations as completed
   - Release tables for completed reservations
   - Update table status (via Kafka)

## Configuration

Copy the example properties file to create your configuration:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Update the following properties with your environment-specific values:

- Database connection details
- Kafka broker addresses
- JWT secret
- Service-specific properties

Key configuration options:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant_service
spring.datasource.username=your_username
spring.datasource.password=your_password

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=reservation-service-group

# JWT
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# Reservation Settings
reservation.max-future-days=90
reservation.default-session-length-minutes=120
reservation.min-advance-booking-minutes=60
reservation.max-party-size=20
reservation.confirmation-expiration-minutes=15
```

## Building the Service

```bash
mvn clean package
```

This will:
1. Compile the code
2. Run tests
3. Create the JAR file in the `target` directory

## Running the Service

### Using Maven

```bash
mvn spring-boot:run
```

### Using Java

```bash
java -jar target/reservation-service-0.0.1-SNAPSHOT.jar
```

### Using Docker

```bash
# Build Docker image
docker build -t reservation-service .

# Run container
docker run -p 8083:8083 reservation-service
```

## API Endpoints

The service exposes the following API endpoints:

### Health Endpoints

| Method | Endpoint | Description | Auth Required | Connected Services |
|--------|----------|-------------|--------------|-------------------|
| GET | `/api/health` | Basic health check | No | None (Internal) |
| GET | `/api/health/details` | Detailed health information | No | None (Internal) |

### Reservation Management

| Method | Endpoint | Description | Auth Required | Connected Services |
|--------|----------|-------------|--------------|-------------------|
| GET | `/api/reservations/user` | Get current user's reservations | Yes | User Service |
| GET | `/api/reservations/restaurant/{restaurantId}` | Get restaurant's reservations | Yes (Admin/Owner) | Restaurant Service |
| GET | `/api/reservations/{id}` | Get specific reservation | Yes | None (Internal) |
| POST | `/api/reservations` | Create new reservation | Yes | Restaurant Service, Table Service |
| PUT | `/api/reservations/{id}` | Update reservation | Yes | Restaurant Service, Table Service |
| POST | `/api/reservations/{id}/confirm` | Confirm reservation | Yes | Restaurant Service, Table Service |
| POST | `/api/reservations/{id}/cancel` | Cancel reservation | Yes | Restaurant Service, Table Service |
| POST | `/api/reservations/{id}/menu-items` | Add menu items to reservation | Yes | Kitchen Service |

### Menu Management

| Method | Endpoint | Description | Auth Required | Connected Services |
|--------|----------|-------------|--------------|-------------------|
| GET | `/api/menus/restaurants/{restaurantId}/categories` | Get restaurant menu categories | No | Kitchen Service |
| GET | `/api/menus/categories/{categoryId}` | Get specific menu category | No | Kitchen Service |
| GET | `/api/menus/restaurants/{restaurantId}/items` | Get all menu items for a restaurant | No | Kitchen Service |
| GET | `/api/menus/items/{itemId}` | Get specific menu item | No | Kitchen Service |
| GET | `/api/menus/restaurants/{restaurantId}/search` | Search menu items | No | Kitchen Service |

### Restaurant Search

| Method | Endpoint | Description | Auth Required | Connected Services |
|--------|----------|-------------|--------------|-------------------|
| POST | `/api/restaurants/search` | Search for available restaurants | No | Restaurant Service |

### Schedule Management

| Method | Endpoint | Description | Auth Required | Connected Services |
|--------|----------|-------------|--------------|-------------------|
| GET | `/api/schedules/restaurant/{restaurantId}` | Get restaurant schedule | No | Restaurant Service |
| PUT | `/api/schedules/restaurant/{restaurantId}/date/{date}` | Update schedule for a date | Yes (Admin/Owner) | Restaurant Service |

## Authentication

The service uses JWT-based authentication. Protected endpoints require a valid JWT token in the Authorization header:

```
Authorization: Bearer <jwt_token>
```

## Event Communication

The service communicates with other microservices using Kafka:

### Published Events

- `ReservationCreatedEvent`
- `ReservationConfirmedEvent`
- `ReservationCancelledEvent`
- `ReservationModifiedEvent`
- `TableAssignedEvent`
- `TableStatusChangedEvent`
- `FindAvailableTableRequestEvent`
- `RestaurantValidationRequestEvent`
- `RestaurantSearchRequestEvent`

### Consumed Events

- `MenuItemEvent`
- `UserEvent`
- `FindAvailableTableResponseEvent`
- `RestaurantValidationResponseEvent`
- `RestaurantSearchResponseEvent`
- `ReservationTimeValidationResponseEvent`
- `RestaurantOwnershipResponseEvent`

## Documentation

API documentation is available via Swagger UI at:

```
http://localhost:8083/swagger-ui.html
```

## Scheduled Tasks

The service includes several scheduled tasks:

- **Process Expired Reservations**: Runs every minute to handle reservations that have passed their confirmation deadline
- **Clean Up Old Data**: Runs daily to archive or delete old reservation data
- **Update Table Status**: Automatically releases tables for completed reservations

## Security

All endpoints are secured with JWT authentication. Users can only:
- View their own reservations
- Modify or cancel reservations they created
- Restaurant owners can view all reservations for their restaurants

## Error Handling

The service provides standardized error responses with:

- HTTP status codes
- Error messages
- Error codes
- Request IDs for tracing

## Development Guidelines

### Adding New Endpoints

1. Create a DTO class for request/response
2. Add controller method with appropriate annotations
3. Implement service logic
4. Add integration tests

### Adding New Events

1. Define event class in the common module
2. Configure Kafka producer or consumer
3. Add event handling logic in appropriate service
4. Update type mappings in KafkaProducerConfig or KafkaConsumerConfig

## Project Structure

```
reservation-service/
├── src/main/java/com/restaurant/reservation/
│   ├── api/controllers/         # REST controllers
│   ├── config/                  # Application configuration
│   ├── domain/                  # Domain models and repositories
│   │   ├── models/              # Entity classes
│   │   └── repositories/        # JPA repositories
│   ├── dto/                     # Data Transfer Objects
│   ├── exception/               # Custom exceptions
│   ├── filters/                 # HTTP filters
│   ├── kafka/                   # Kafka configuration
│   │   ├── consumers/           # Kafka consumers
│   │   └── producers/           # Kafka producers
│   ├── security/                # Security configuration
│   └── service/                 # Business logic services
└── src/main/resources/          # Application resources
    └── application.properties   # Application configuration
```

## License

MIT License

Copyright (c) 2025 Restaurant Reservation Platform

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.