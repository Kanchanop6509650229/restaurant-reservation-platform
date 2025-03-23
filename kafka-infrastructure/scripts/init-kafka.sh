#!/bin/bash
echo "Waiting for Kafka to be ready..."
cub kafka-ready -b kafka:9092 1 30

echo "Creating Kafka topics..."

# User Service Topics
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic user-events --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic user-registration --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic user-login --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic user-profile --partitions 3 --replication-factor 1

# Restaurant Service Topics
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic restaurant-events --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic restaurant-update --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic table-status --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic capacity-change --partitions 3 --replication-factor 1

# Reservation Service Topics (for future implementation)
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic reservation-events --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic reservation-create --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic reservation-update --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic reservation-cancel --partitions 3 --replication-factor 1

# Notification Service Topics (for future implementation)
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic notification-events --partitions 3 --replication-factor 1

echo "Listing all topics:"
kafka-topics --bootstrap-server kafka:9092 --list

echo "Kafka topic initialization completed!"