# Restaurant Reservation Platform

This project demonstrates a microservices architecture using Spring Boot and Kafka. The application consists of multiple services that communicate via Kafka messaging to provide a complete restaurant reservation system.

## Project Overview

The Restaurant Reservation Platform is designed to help restaurants manage their tables, reservations, and customers effectively. The platform uses event-driven architecture with Kafka to ensure real-time updates across services.

### Key Features

- Restaurant management (add, update, delete restaurants)
- Table management with statuses and availability
- User registration and authentication with JWT
- Profile management for users
- Restaurant search by location, cuisine type, and keywords
- Operating hours management
- Staff management by restaurant
- Branch management with location-based search
- Restaurant statistics for business intelligence

## Project Structure

- **common**: Shared library with common code, DTOs, utilities, and constants
- **restaurant-service**: Service for managing restaurant information and operations
- **user-service**: Service for managing user accounts and authentication
- **reservation-service**: Service for managing table reservations, waitlists, and scheduling
- **kafka-infrastructure**: Docker Compose configuration for setting up the Kafka infrastructure

## Prerequisites

- Java 17+
- Docker and Docker Compose
- Maven

## Getting Started

### 1. Build the Project

```bash
mvn clean package
```

### 2. Initialize Kafka Scripts

```bash
chmod +x kafka-infrastructure/scripts/init-kafka.sh
```

### 3. Start All Services with Docker Compose

```bash
cd kafka-infrastructure
docker-compose up -d
```

This will start:
- Zookeeper
- Kafka broker
- MySQL databases with initialized schemas
- Kafka initialization scripts
- User Service (running on port 8081)
- Restaurant Service (running on port 8082)
- Kafdrop UI for Kafka monitoring (accessible at http://localhost:9000)

You can check the status of all services using:

```bash
docker-compose ps
```

To view the logs of a specific service:

```bash
docker-compose logs -f [service-name]
```

For example:
```bash
docker-compose logs -f restaurant-service
```

## Available APIs

### User Service APIs (Port 8081)

#### Authentication

- **POST /api/auth/login**: Authenticate a user and receive a JWT token
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```

#### User Management

- **POST /api/users/register**: Register a new user
  ```json
  {
    "username": "string",
    "email": "string",
    "password": "string",
    "firstName": "string",
    "lastName": "string",
    "phoneNumber": "string"
  }
  ```
- **GET /api/users/{id}**: Get user by ID (requires authentication)
- **GET /api/users**: Get all users (admin only)
- **DELETE /api/users/{id}**: Delete a user (admin only)

#### Profile Management

- **GET /api/users/{id}/profile**: Get user profile (requires authentication)
- **PUT /api/users/{id}/profile**: Update user profile (requires authentication)
  ```json
  {
    "firstName": "string",
    "lastName": "string",
    "phoneNumber": "string",
    "address": "string",
    "city": "string",
    "state": "string",
    "zipCode": "string",
    "country": "string",
    "preferences": "string"
  }
  ```

### Restaurant Service APIs (Port 8082)

#### Restaurant Management

- **GET /api/restaurants/public/all**: Get all active restaurants
- **GET /api/restaurants/public**: Get paginated list of active restaurants
- **GET /api/restaurants/public/{id}**: Get restaurant by ID
- **GET /api/restaurants/public/search**: Search restaurants by criteria
  ```
  ?keyword=italian&page=0&size=10
  ```
- **GET /api/restaurants/public/nearby**: Find nearby restaurants
  ```
  ?latitude=40.7128&longitude=-74.0060&distance=5.0
  ```
- **POST /api/restaurants**: Create a new restaurant
  ```json
  {
    "name": "string",
    "description": "string",
    "address": "string",
    "city": "string",
    "state": "string",
    "zipCode": "string",
    "country": "string",
    "phoneNumber": "string",
    "email": "string",
    "website": "string",
    "cuisineType": "string",
    "totalCapacity": 0,
    "latitude": 0,
    "longitude": 0
  }
  ```
- **PUT /api/restaurants/{id}**: Update a restaurant
- **PATCH /api/restaurants/{id}/active**: Activate or deactivate a restaurant
  ```
  ?active=true
  ```
- **DELETE /api/restaurants/{id}**: Delete a restaurant

#### Table Management

- **GET /api/restaurants/{restaurantId}/tables**: Get all tables for a restaurant
- **GET /api/restaurants/{restaurantId}/tables/available**: Get available tables
- **GET /api/restaurants/{restaurantId}/tables/{tableId}**: Get table by ID
- **POST /api/restaurants/{restaurantId}/tables**: Create a new table
  ```json
  {
    "tableNumber": "string",
    "capacity": 0,
    "status": "string",
    "location": "string",
    "accessible": true,
    "shape": "string",
    "minCapacity": 0,
    "combinable": true,
    "specialFeatures": "string"
  }
  ```
- **PUT /api/restaurants/{restaurantId}/tables/{tableId}**: Update a table
- **PATCH /api/restaurants/{restaurantId}/tables/{tableId}/status**: Update table status
  ```
  ?status=AVAILABLE&reservationId=string
  ```
- **DELETE /api/restaurants/{restaurantId}/tables/{tableId}**: Delete a table

#### Operating Hours Management

- **GET /api/restaurants/{restaurantId}/operating-hours**: Get operating hours
- **PUT /api/restaurants/{restaurantId}/operating-hours/{day}**: Update operating hours
  ```json
  {
    "openTime": "09:00",
    "closeTime": "22:00",
    "closed": false
  }
  ```

#### Branch Management

- **GET /api/restaurants/{restaurantId}/branches**: Get restaurant branches
- **GET /api/restaurants/{restaurantId}/branches/nearby**: Find nearby branches
  ```
  ?latitude=40.7128&longitude=-74.0060&distance=5.0
  ```

#### Staff Management

- **GET /api/restaurants/{restaurantId}/staff**: Get restaurant staff
- **GET /api/restaurants/{restaurantId}/staff/position/{position}**: Get staff by position

#### Statistics

- **GET /api/restaurants/{restaurantId}/statistics**: Get restaurant statistics

### Reservation Service APIs (Port 8083)

#### Reservation Management

- **GET /api/reservations/{id}**: Get reservation by ID
- **GET /api/users/{userId}/reservations**: Get user's reservations
- **GET /api/restaurants/{restaurantId}/reservations**: Get restaurant's reservations
- **POST /api/reservations**: Create a new reservation
  ```json
  {
    "restaurantId": "string",
    "reservationTime": "2025-03-26T19:00:00",
    "partySize": 4,
    "customerName": "string",
    "customerPhone": "string",
    "customerEmail": "string",
    "specialRequests": "string",
    "remindersEnabled": true
  }
  ```
- **PUT /api/reservations/{id}**: Update a reservation
  ```json
  {
    "reservationTime": "2025-03-26T19:00:00",
    "partySize": 4,
    "customerName": "string",
    "customerPhone": "string",
    "customerEmail": "string",
    "specialRequests": "string",
    "durationMinutes": 120
  }
  ```
- **POST /api/reservations/{id}/confirm**: Confirm a reservation
- **POST /api/reservations/{id}/cancel**: Cancel a reservation
  ```json
  {
    "reason": "string"
  }
  ```

#### Queue Management

- **GET /api/restaurants/{restaurantId}/queue**: Get restaurant's waiting queue
- **POST /api/restaurants/{restaurantId}/queue**: Add to waiting queue
  ```json
  {
    "customerName": "string",
    "partySize": 4,
    "notes": "string"
  }
  ```
- **POST /api/queue/{id}/notify**: Notify customer their table is ready
- **POST /api/queue/{id}/seat**: Mark party as seated
- **POST /api/queue/{id}/cancel**: Remove from queue
  ```json
  {
    "reason": "string"
  }
  ```

#### Schedule Management

- **GET /api/restaurants/{restaurantId}/schedule**: Get restaurant's schedule
- **PUT /api/restaurants/{restaurantId}/schedule/{dayOfWeek}**: Update schedule for a day
  ```json
  {
    "openTime": "10:00",
    "closeTime": "22:00",
    "closed": false
  }
  ```

Service Features:
- Automatic table assignment based on party size and availability
- Reservation quota management to prevent overbooking
- Configurable session lengths (default 120 minutes)
- Confirmation deadline enforcement (default 15 minutes)
- Automatic processing of expired reservations
- Walk-in queue management with estimated wait times
- Real-time updates via Kafka events

## Service Communication

The services communicate with each other through Kafka events. Here are the main topics and their purposes:

### User Service Topics

- **user-events**: General user-related events
- **user-registration**: User registration events
- **user-login**: User login events
- **user-profile**: User profile update events

### Restaurant Service Topics

- **restaurant-events**: General restaurant-related events
- **restaurant-update**: Restaurant information update events
- **table-status**: Table status change events
- **capacity-change**: Restaurant capacity change events

### Future Implementation Topics

#### Reservation Service Topics
- **reservation-events**: General reservation events
- **reservation-create**: Reservation creation events
- **reservation-update**: Reservation update events
- **reservation-cancel**: Reservation cancellation events

#### Notification Service Topics
- **notification-events**: General notification events

## Database Configuration

### User Service Database
- Host: localhost
- Port: 3306
- Database: user_service
- Username: user_service
- Password: user_password

### Restaurant Service Database
- Host: localhost
- Port: 3307
- Database: restaurant_service
- Username: restaurant_service
- Password: restaurant_password

## Security

- JWT-based authentication for user service
- Role-based access control (ADMIN, USER)
- Secure password hashing
- Input validation and sanitization

## Monitoring and Management

- Kafdrop UI available at http://localhost:9000 for Kafka monitoring
- Health check endpoints available for each service
- Docker container logs for service monitoring

## Future Enhancements

1. Reservation Service Implementation
   - Table reservation management
   - Reservation confirmation
   - Cancellation handling
   - Waitlist management

2. Notification Service Implementation
   - Email notifications
   - SMS notifications
   - Push notifications
   - Reservation reminders

3. Payment Service Integration
   - Payment processing
   - Refund handling
   - Payment history

4. Review and Rating System
   - Restaurant reviews
   - User ratings
   - Review moderation

5. Analytics Dashboard
   - Business intelligence
   - Customer insights
   - Performance metrics