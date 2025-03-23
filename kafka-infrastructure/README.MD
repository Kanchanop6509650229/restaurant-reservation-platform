# Kafka Infrastructure for Restaurant Reservation Platform

This directory contains the infrastructure setup for the Kafka-based microservices architecture of the Restaurant Reservation Platform.

## Components

- **Zookeeper**: Manages Kafka cluster
- **Kafka**: Message broker for service communication
- **Kafdrop**: Web UI for Kafka monitoring
- **MySQL Databases**: Separate databases for each microservice
- **Kafka Initialization**: Automatically creates required Kafka topics

## Getting Started

### Prerequisites

- Docker and Docker Compose
- At least 4GB of RAM allocated to Docker

### Starting the Infrastructure

```bash
# Start all services
docker-compose up -d

# Check the status of the services
docker-compose ps

# View logs
docker-compose logs -f
```

### Accessing Services

- **Kafdrop (Kafka UI)**: http://localhost:9000
- **MySQL User Service**: localhost:3306
- **MySQL Restaurant Service**: localhost:3307

### Stopping the Infrastructure

```bash
# Stop all services but keep the data
docker-compose down

# Stop all services and remove all data
docker-compose down -v
```

## Kafka Topics

The following Kafka topics are automatically created:

- `user-events`: Events related to users
- `user-registration`: User registration events
- `user-login`: User login events
- `user-profile`: User profile update events
- `restaurant-events`: Events related to restaurants
- `restaurant-update`: Restaurant update events
- `table-status`: Table status change events
- `capacity-change`: Restaurant capacity change events
- `reservation-events`: Events related to reservations

## Configuration

### Connecting Microservices

Each microservice should be configured to connect to Kafka at:
```
spring.kafka.bootstrap-servers=kafka:9092
```

And to their respective databases:

**User Service:**
```
spring.datasource.url=jdbc:mysql://mysql-user-service:3306/user_service
spring.datasource.username=user_service
spring.datasource.password=user_password
```

**Restaurant Service:**
```
spring.datasource.url=jdbc:mysql://mysql-restaurant-service:3306/restaurant_service
spring.datasource.username=restaurant_service
spring.datasource.password=restaurant_password
```

## Directory Structure

```
kafka-infrastructure/
├── config/                     # Configuration files
│   └── kafka/                  # Kafka configuration
├── scripts/                    # Scripts for initialization
│   ├── mysql/                  # MySQL initialization scripts
│   │   ├── init-user-db.sql    # User Service DB initialization
│   │   └── init-restaurant-db.sql # Restaurant Service DB initialization
│   ├── Dockerfile.kafka-init   # Dockerfile for Kafka initialization
│   └── init-kafka.sh           # Kafka topics initialization script
├── docker-compose.yml          # Docker Compose configuration
└── README.md                   # Documentation
```

## Troubleshooting

### Kafka Connection Issues

If services can't connect to Kafka, ensure they're using the correct service name:
```
kafka:9092
```

Not `localhost:9092` inside the Docker network.

### Database Connection Issues

Ensure the correct database connection strings are used for each service.

### Ports Already in Use

If you encounter port conflicts, you can change the mapping in the `docker-compose.yml` file.