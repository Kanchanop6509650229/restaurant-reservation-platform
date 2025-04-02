# Restaurant Reservation Platform

![Version](https://img.shields.io/badge/version-0.1.0-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)

A comprehensive microservices-based platform for restaurant reservations built with Spring Boot and Kafka. The application consists of multiple services that communicate via Kafka messaging to provide a complete restaurant management and reservation system.

## Table of Contents
- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [API Reference](#api-reference)
  - [User Service](#user-service-port-8081)
  - [Restaurant Service](#restaurant-service-port-8082)
  - [Reservation Service](#reservation-service-port-8083)
- [Kafka Topics](#kafka-topics)
- [Postman Collection](#postman-collection)
- [Development](#development)
- [Testing](#testing)
- [Future Enhancements](#future-enhancements)
- [JWT Authentication Guide](#jwt-authentication-flow-implementation-guide)
- [Contributing](#contributing)
- [License](#license)

## Project Overview

The Restaurant Reservation Platform enables:
- Restaurant management with detailed information and operating hours
- Table management with real-time status updates
- User authentication and profile management
- Advanced reservation system with confirmation workflows
- Queue management for walk-in customers
- Event-driven architecture for real-time updates across services

## Project Structure

This project utilizes a standard package structure for Spring Boot applications to organize code based on responsibilities. The main packages used within each service (e.g., user-service, restaurant-service, reservation-service) are as follows:

* **`api`**:
    * **Purpose**: Handles incoming HTTP requests and returns HTTP responses. Acts as the entry point for external clients (e.g., web browsers, mobile apps, or other services).
    * **Common Subpackages**:
        * `controllers`: Contains Spring MVC Controller classes (e.g., `UserController`, `RestaurantController`, `ReservationController`) that map URL endpoints to methods, handle request parameters/bodies, invoke the `service` layer for business operations, and return data (often as DTOs) to the client.

* **`config`**:
    * **Purpose**: Contains various Configuration classes for setting up and customizing the application's behavior.
    * **Examples**: Spring Security setup (`SecurityConfig`), Kafka connection settings (`KafkaProducerConfig`, `KafkaConsumerConfig`), data initialization (`DataInitializer`), Web MVC settings (`WebConfig`, e.g., adding `CurrentUserArgumentResolver`), scheduling (`SchedulingConfig`). These classes define *how* different parts of the application are configured and wired together.

* **`domain`**:
    * **Purpose**: Represents the core of the application, containing the business logic and data model.
    * **Common Subpackages**:
        * `models`: Contains Entity or Domain Model classes (typically JPA Entities mapping to database tables) that represent the core data structures the application works with (e.g., `User`, `Profile`, `Restaurant`, `RestaurantTable`, `Reservation`, `Queue`).
        * `repositories`: Contains interfaces (usually extending Spring Data JPA repositories like `JpaRepository`) defining methods for interacting with the database (e.g., saving, finding, updating, deleting data) for the Domain Models (e.g., `UserRepository`, `RestaurantRepository`, `ReservationRepository`).

* **`dto` (Data Transfer Object)**:
    * **Purpose**: Contains Plain Old Java Objects (POJOs) designed specifically for "transferring data" between different layers of the application, especially between the `service` and `api` layers (controllers), or even between microservices.
    * **Benefits**:
        * **Decoupling**: Helps separate the internal Domain Models from the data structure exposed to or received from the outside (API Contract). This means changes to the Domain Model don't directly impact the API (and vice versa).
        * **Data Shaping**: Allows tailoring the data structure for specific use cases or API endpoints (e.g., sending partial User data in `UserDTO` but receiving necessary registration data in `UserRegistrationRequest`).
        * **Validation**: Often used with Validation Annotations (e.g., `@NotBlank`, `@Email`, `@Min`) to validate incoming data from clients at the Controller layer.
    * **Examples**: `UserDTO`, `ProfileDTO`, `LoginRequest`, `RestaurantDTO`, `TableDTO`, `ReservationCreateRequest`, `QueueDTO`.

* **`service`**:
    * **Purpose**: Contains the main business logic of the application. Classes in the service layer orchestrate calls to repositories to manage data and are invoked by controllers to fulfill user requests (e.g., `UserService`, `RestaurantService`, `ReservationService`).

* **`kafka`**:
    * **Purpose**: Manages interactions with Apache Kafka for event-driven communication.
    * **Common Subpackages**:
        * `producers`: Classes responsible for sending messages (events) to Kafka topics (e.g., `UserEventProducer`).
        * `consumers`: Classes responsible for receiving and processing messages (events) from Kafka topics (e.g., `RestaurantEventConsumer`).

* **`security`**:
    * **Purpose**: Holds classes related to application security, such as JWT handling, user authentication, and authorization logic (e.g., `JwtTokenProvider`, `JwtAuthorizationFilter`, `CustomUserDetailsService`).

* **`exception`**:
    * **Purpose**: Contains classes for centralized exception handling (e.g., `GlobalExceptionHandler`) and custom application-specific exceptions (e.g., `EntityNotFoundException`).

* **`utils`**:
    * **Purpose**: Contains utility classes that might be shared across different parts of the application (e.g., `DateTimeUtils`, `SpatialUtils`).

(Note: Not all subpackages might exist in every service, depending on the specific complexity and needs of that service.)

## Architecture

![architecture-diagram](architecture-diagram.png)

The platform follows a microservices architecture with:

- **User Service**: Authentication, user registration, and profile management
- **Restaurant Service**: Restaurant information, tables, and operating hours
- **Reservation Service**: Reservations, queues, and scheduling
- **Kafka**: Event bus for inter-service communication
- **MySQL Databases**: Separate database for each service

## Prerequisites

- Java 17+
- Docker and Docker Compose
- Maven

## Quick Start

### 1. Build the Project

```bash
mvn clean package
```

### 2. Start Infrastructure with Docker Compose

```bash
cd kafka-infrastructure
docker-compose up -d
```

This starts:
- Zookeeper
- Kafka broker
- MySQL databases
- Kafdrop (Kafka UI)

### 3. Access Services

- **User Service**: http://localhost:8081
- **Restaurant Service**: http://localhost:8082
- **Reservation Service**: http://localhost:8083
- **Kafdrop UI**: http://localhost:9000

## API Reference

### User Service (Port 8081)

#### Authentication

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/auth/login` | POST | Authenticate user and get JWT token | No |

**Login Request Example:**
```json
{
  "username": "user",
  "password": "password123"
}
```

**Login Response Example:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": "1a2b3c4d-5e6f-7g8h-9i0j",
    "message": "Authentication successful"
  }
}
```

#### User Management

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/users/register` | POST | Register a new user | No |
| `/api/users/me` | GET | Get current user info | Yes |
| `/api/users/{id}` | GET | Get user by ID | Yes (Admin or Self) |
| `/api/users` | GET | Get all users | Yes (Admin) |
| `/api/users/{id}` | DELETE | Delete a user | Yes (Admin) |

#### Profile Management

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/users/{id}/profile` | GET | Get user profile | Yes (Admin or Self) |
| `/api/users/{id}/profile` | PUT | Update user profile | Yes (Admin or Self) |

### Restaurant Service (Port 8082)

#### Restaurants

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/restaurants/public/all` | GET | Get all active restaurants | No |
| `/api/restaurants/public/{id}` | GET | Get restaurant by ID | No |
| `/api/restaurants/public/search` | GET | Search restaurants by criteria | No |
| `/api/restaurants/public/nearby` | GET | Find nearby restaurants | No |
| `/api/restaurants` | POST | Create a new restaurant | Yes |
| `/api/restaurants/{id}` | PUT | Update a restaurant | Yes (Owner) |
| `/api/restaurants/{id}/active` | PATCH | Toggle restaurant active status | Yes (Owner) |

#### Tables

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/restaurants/{restaurantId}/tables/public` | GET | Get all tables for a restaurant | No |
| `/api/restaurants/{restaurantId}/tables/public/available` | GET | Get available tables | No |
| `/api/restaurants/{restaurantId}/tables` | POST | Add a table to restaurant | Yes (Owner) |
| `/api/restaurants/{restaurantId}/tables/{tableId}` | PUT | Update a table | Yes (Owner) |
| `/api/restaurants/{restaurantId}/tables/{tableId}/status` | PATCH | Update table status | Yes (Owner) |

#### Operating Hours

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/restaurants/{restaurantId}/operating-hours/public` | GET | Get operating hours | No |
| `/api/restaurants/{restaurantId}/operating-hours` | PUT | Update operating hours | Yes (Owner) |

### Reservation Service (Port 8083)

#### Reservations

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/reservations/availability` | GET | Check table availability | No |
| `/api/reservations` | POST | Create a reservation | Yes |
| `/api/reservations/user` | GET | Get user's reservations | Yes |
| `/api/reservations/restaurant/{restaurantId}` | GET | Get restaurant's reservations | Yes (Owner) |
| `/api/reservations/{id}` | GET | Get reservation by ID | Yes |
| `/api/reservations/{id}` | PUT | Update a reservation | Yes |
| `/api/reservations/{id}/confirm` | POST | Confirm a reservation | Yes |
| `/api/reservations/{id}/cancel` | POST | Cancel a reservation | Yes |

#### Queue Management

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/queues` | POST | Add user to queue | Yes |
| `/api/queues/restaurant/{restaurantId}` | GET | Get restaurant's waiting queue | Yes (Owner) |
| `/api/queues/{id}` | GET | Get queue entry by ID | Yes |
| `/api/queues/{id}/notify` | POST | Notify customer their table is ready | Yes (Owner) |
| `/api/queues/{id}/seat` | POST | Mark party as seated | Yes (Owner) |
| `/api/queues/{id}/cancel` | POST | Remove from queue | Yes |

#### Schedule Management

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/schedules/restaurant/{restaurantId}` | GET | Get restaurant's schedule | No |
| `/api/schedules/restaurant/{restaurantId}/date/{date}` | PUT | Update schedule for a day | Yes (Owner) |

## Kafka Topics

The following Kafka topics are used for inter-service communication:

### User Service Topics
- `user-events`: General user-related events
- `user-registration`: User registration events
- `user-login`: User login events
- `user-profile`: User profile update events

### Restaurant Service Topics
- `restaurant-events`: General restaurant-related events
- `restaurant-update`: Restaurant information update events
- `table-status`: Table status change events
- `capacity-change`: Restaurant capacity change events

### Reservation Service Topics
- `reservation-events`: General reservation events
- `reservation-create`: Reservation creation events
- `reservation-update`: Reservation update events
- `reservation-cancel`: Reservation cancellation events

## Postman Collection

A Postman collection file (`PostmanTesting.json`) is included for testing all available APIs. To use it:

1. Import the collection into Postman
2. Set up the environment variables:
   - `baseUrlUser`: http://localhost:8081
   - `baseUrlRestaurant`: http://localhost:8082
   - `baseUrlReservation`: http://localhost:8083

## Development

### Adding Custom Code

Each service is designed to be independent and can be extended separately:

- **User Service**: Add custom user features in the `user-service` directory
- **Restaurant Service**: Add restaurant features in the `restaurant-service` directory
- **Reservation Service**: Add reservation features in the `reservation-service` directory

### Building Individual Services

You can build and run individual services:

```bash
cd user-service
mvn spring-boot:run
```

### Database Access

- **User Service DB**: localhost:3306
- **Restaurant Service DB**: localhost:3307
- **Reservation Service DB**: localhost:3308

## Future Enhancements

1. Notification Service
   - Email notifications
   - SMS notifications
   - Push notifications

2. Payment Service
   - Payment processing
   - Refund handling
   - Payment history

3. Analytics Dashboard
   - Business intelligence
   - Customer insights
   - Performance metrics

# JWT Authentication Flow Implementation Guide

This guide explains how authentication works in the Restaurant Reservation Platform and how to properly implement JWT authentication in client applications.

## Overview

The platform uses JSON Web Tokens (JWT) for authentication. The authentication flow is:

1. User logs in with username/password to the User Service
2. User Service validates credentials and returns a JWT token
3. Client includes this token in the `Authorization` header for subsequent requests
4. Services validate the token and extract user information for authorization

## Authentication Flow Diagram

```
┌─────────┐                ┌─────────────┐                  ┌──────────────────┐
│  Client │                │ User Service │                  │ Protected Service │
└────┬────┘                └──────┬──────┘                  └─────────┬────────┘
     │                            │                                   │
     │ POST /api/auth/login       │                                   │
     │ {username, password}       │                                   │
     │ ─────────────────────────► │                                   │
     │                            │                                   │
     │ 200 OK                     │                                   │
     │ {token, userId}            │                                   │
     │ ◄───────────────────────── │                                   │
     │                            │                                   │
     │ GET /api/some-endpoint     │                                   │
     │ Authorization: Bearer token│                                   │
     │ ─────────────────────────────────────────────────────────────► │
     │                            │                                   │
     │                            │                                   │ Validate token
     │                            │                                   │ Extract user info
     │                            │                                   │ Check permissions
     │                            │                                   │
     │ 200 OK                     │                                   │
     │ {requested data}           │                                   │
     │ ◄───────────────────────────────────────────────────────────── │
     │                            │                                   │
```

## Implementation Details

### 1. Authentication Request

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "username": "user123",
  "password": "password123"
}
```

**Response (Success):**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoiMWEyYjNjNGQiLCJpYXQiOjE2MTYxNTkwMjIsImV4cCI6MTYxNjI0NTQyMn0.signature",
    "userId": "1a2b3c4d-5e6f-7g8h-9i0j",
    "message": "Authentication successful"
  }
}
```

**Response (Failure):**
```json
{
  "success": false,
  "message": "Invalid username or password",
  "errorCode": "AUTHENTICATION_ERROR"
}
```

### 2. Using the JWT Token

After obtaining the token, include it in the `Authorization` header for subsequent requests:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoiMWEyYjNjNGQiLCJpYXQiOjE2MTYxNTkwMjIsImV4cCI6MTYxNjI0NTQyMn0.signature
```

### 3. JWT Token Structure

The JWT token consists of three parts:
- **Header:** Contains token type and algorithm
- **Payload:** Contains claims (data) about the user
- **Signature:** Verifies the token hasn't been tampered with

The platform JWT payload includes:
- `sub`: Username
- `userId`: User's unique ID
- `iat`: Issued at timestamp
- `exp`: Expiration timestamp

### 4. Token Expiration

Tokens are valid for 24 hours (configurable in `application.properties` with `jwt.expiration`). After expiration, the client must acquire a new token by logging in again.

### 5. Implementation in Different Client Types

#### Web Application (JavaScript)
```javascript
// Login function
async function login(username, password) {
  const response = await fetch('http://localhost:8081/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username, password })
  });
  
  const data = await response.json();
  if (data.success) {
    // Store token in localStorage or sessionStorage
    localStorage.setItem('token', data.data.token);
    localStorage.setItem('userId', data.data.userId);
    return true;
  }
  return false;
}

// Making authenticated requests
async function fetchRestaurants() {
  const token = localStorage.getItem('token');
  
  const response = await fetch('http://localhost:8082/api/restaurants', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  return await response.json();
}
```

#### Mobile Application
```swift
// Swift example (iOS)
func login(username: String, password: String, completion: @escaping (Bool, String?) -> Void) {
    let url = URL(string: "http://localhost:8081/api/auth/login")!
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.addValue("application/json", forHTTPHeaderField: "Content-Type")
    
    let body: [String: Any] = ["username": username, "password": password]
    request.httpBody = try? JSONSerialization.data(withJSONObject: body)
    
    URLSession.shared.dataTask(with: request) { data, response, error in
        guard let data = data, error == nil else {
            completion(false, nil)
            return
        }
        
        if let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
           let success = json["success"] as? Bool,
           success,
           let data = json["data"] as? [String: Any],
           let token = data["token"] as? String {
            
            // Store token securely
            KeychainManager.save(token, forKey: "authToken")
            completion(true, token)
        } else {
            completion(false, nil)
        }
    }.resume()
}
```

#### Postman
1. Make a POST request to `/api/auth/login` with credentials
2. Extract the token from the response
3. Set up an environment variable `authToken`
4. Use the token in subsequent requests with the Authorization header:
   - Type: Bearer Token
   - Token: `{{authToken}}`

### 6. Security Best Practices

1. **Store tokens securely:**
   - Web: Use HttpOnly cookies or secure storage
   - Mobile: Use Keychain (iOS) or Encrypted SharedPreferences (Android)

2. **Implement token refresh:**
   - Optionally implement a refresh token mechanism for seamless user experience

3. **Handle expiration:**
   - Detect 401 Unauthorized responses
   - Redirect to login when token expires

4. **Logout:**
   - Clear stored tokens on client side
   - Optionally implement server-side token invalidation

5. **HTTPS Only:**
   - Always use HTTPS in production to protect tokens in transit

## User Roles and Permissions

The platform has a role-based access control system:

1. **USER Role**:
   - `user:read`: Can read own user data
   - `profile:read`: Can read own profile data
   - `profile:write`: Can update own profile data
   - `restaurant:read`: Can read restaurant data

2. **ADMIN Role**:
   - All USER permissions
   - `user:write`: Can create/update user data
   - `user:delete`: Can delete user data

3. **RESTAURANT_OWNER Role** (assigned in restaurant context):
   - Based on restaurant `ownerId` matching the current user's ID
   - Can manage their own restaurants, tables, etc.

## Testing Authentication

You can use the included Postman collection to test authentication:

1. Use the "Login User" request to authenticate
2. The collection is set up to automatically extract the JWT token and store it in the `authToken` variable
3. All subsequent authenticated requests will use this token

## Troubleshooting

Common authentication issues:

1. **401 Unauthorized**:
   - Token is missing, expired, or invalid
   - Solution: Re-authenticate and get a new token

2. **403 Forbidden**:
   - Token is valid but user lacks permissions
   - Check if the user has the required role or permissions

3. **Token Not Being Sent Correctly**:
   - Ensure the Authorization header format is correct: `Bearer <token>`
   - Check for whitespace or formatting issues

4. **Cross-Origin Issues**:
   - If using a web client, ensure CORS is properly configured
   - Check for preflight OPTIONS requests

## License

This project is licensed under the MIT License.