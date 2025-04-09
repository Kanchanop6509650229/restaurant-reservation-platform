#!/bin/bash

# Kafka Initialization Script
# This script initializes Kafka topics for the restaurant reservation platform
# It creates all necessary topics with appropriate partitions and replication factors
# Author: Restaurant Team
# Version: 1.0

# Wait for Kafka to be ready before proceeding
# Uses the cub kafka-ready utility to check Kafka availability
# Parameters:
#   -b: Bootstrap server address
#   1: Number of brokers to wait for
#   30: Maximum wait time in seconds
echo "Waiting for Kafka to be ready..."
cub kafka-ready -b kafka:9092 1 30

echo "Creating Kafka topics..."

# User Service Topics
# Topics for handling user-related events and operations
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic user-events --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic user-registration --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic user-login --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic user-profile --partitions 3 --replication-factor 1

# Restaurant Service Topics
# Topics for handling restaurant-related events and operations
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic restaurant-events --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic restaurant-update --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic table-status --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic capacity-change --partitions 3 --replication-factor 1

# Table Availability Topics
# Topics for handling table availability requests and responses
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic find-available-table-request --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic find-available-table-response --partitions 3 --replication-factor 1

# Reservation Service Topics
# Topics for handling reservation-related events and operations
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic reservation-events --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic reservation-create --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic reservation-update --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic reservation-cancel --partitions 3 --replication-factor 1

# Restaurant Validation Topics
# Topics for handling restaurant validation requests and responses
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic restaurant-validation-request --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic restaurant-validation-response --partitions 3 --replication-factor 1

# Reservation Time Validation Topics
# Topics for handling reservation time validation requests and responses
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic reservation-time-validation-request --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic reservation-time-validation-response --partitions 3 --replication-factor 1

# Notification Service Topics
# Topics for handling notification-related events
kafka-topics --bootstrap-server kafka:9092 --create --if-not-exists --topic notification-events --partitions 3 --replication-factor 1

# List all created topics for verification
echo "Listing all topics:"
kafka-topics --bootstrap-server kafka:9092 --list

echo "Kafka topic initialization completed!"