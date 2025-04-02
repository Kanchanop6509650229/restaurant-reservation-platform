-- Reservation Service Database Initialization

-- Enable proper time zone
SET GLOBAL time_zone = '+00:00';

-- Create tables for Reservation Service
-- Reservations Table
CREATE TABLE IF NOT EXISTS reservations (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    restaurant_id VARCHAR(36) NOT NULL,
    table_id VARCHAR(36),
    reservation_time DATETIME NOT NULL,
    party_size INT NOT NULL,
    duration_minutes INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20),
    customer_email VARCHAR(255),
    special_requests TEXT,
    reminders_enabled BOOLEAN DEFAULT true,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    confirmation_deadline DATETIME,
    confirmed_at DATETIME,
    cancelled_at DATETIME,
    completed_at DATETIME,
    cancellation_reason VARCHAR(255),
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_reservation_time (reservation_time)
);

-- Wait List Table
CREATE TABLE IF NOT EXISTS wait_list (
    id VARCHAR(36) PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    party_size INT NOT NULL,
    request_date DATE NOT NULL,
    request_time TIME NOT NULL,
    notification_sent BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_restaurant_id (restaurant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_request_date (request_date)
);

-- Reservation Settings Table
CREATE TABLE IF NOT EXISTS reservation_settings (
    id VARCHAR(36) PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL UNIQUE,
    max_party_size INT NOT NULL DEFAULT 20,
    min_reservation_notice_minutes INT NOT NULL DEFAULT 60,
    max_reservation_notice_days INT NOT NULL DEFAULT 90,
    default_reservation_duration_minutes INT NOT NULL DEFAULT 120,
    turn_time_buffer_minutes INT NOT NULL DEFAULT 15,
    confirmation_required BOOLEAN DEFAULT TRUE,
    confirmation_timeout_minutes INT NOT NULL DEFAULT 15,
    special_hours_override TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_restaurant_id (restaurant_id)
);

-- Reservation History Table (for auditing purposes)
CREATE TABLE IF NOT EXISTS reservation_history (
    id VARCHAR(36) PRIMARY KEY,
    reservation_id VARCHAR(36) NOT NULL,
    action VARCHAR(20) NOT NULL,
    previous_status VARCHAR(20),
    new_status VARCHAR(20),
    actor_id VARCHAR(36),
    actor_type VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_reservation_id (reservation_id),
    INDEX idx_action (action)
);

-- Sample Data: Add some reservation settings for existing restaurants
-- Use the same restaurant IDs as used in the restaurant-service database
INSERT INTO reservation_settings (
    id, restaurant_id, max_party_size, min_reservation_notice_minutes, 
    max_reservation_notice_days, default_reservation_duration_minutes,
    turn_time_buffer_minutes, confirmation_required, confirmation_timeout_minutes
) VALUES 
    (UUID(), (SELECT id FROM restaurant_service.restaurants WHERE name = 'Italian Bistro' LIMIT 1), 20, 60, 90, 120, 15, TRUE, 15),
    (UUID(), (SELECT id FROM restaurant_service.restaurants WHERE name = 'Sushi Paradise' LIMIT 1), 10, 120, 60, 90, 20, TRUE, 10),
    (UUID(), (SELECT id FROM restaurant_service.restaurants WHERE name = 'Spice of India' LIMIT 1), 15, 90, 60, 150, 30, TRUE, 15); 