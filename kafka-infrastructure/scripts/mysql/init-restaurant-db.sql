-- Restaurant Service Database Initialization

-- Enable spatial features
SET GLOBAL time_zone = '+00:00';

-- Create tables for Restaurant Service
-- Restaurants Table
CREATE TABLE IF NOT EXISTS restaurants (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    country VARCHAR(100),
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    website VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    cuisine_type VARCHAR(100) NOT NULL,
    total_capacity INT,
    average_rating FLOAT(3,2),
    total_ratings INT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tables Table
CREATE TABLE IF NOT EXISTS restaurant_tables (
    id VARCHAR(36) PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL,
    table_number VARCHAR(10) NOT NULL,
    capacity INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    location VARCHAR(50),
    accessible BOOLEAN DEFAULT FALSE,
    shape VARCHAR(20),
    min_capacity INT,
    combinable BOOLEAN DEFAULT FALSE,
    special_features VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    UNIQUE(restaurant_id, table_number),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

-- Operating Hours Table
CREATE TABLE IF NOT EXISTS operating_hours (
    id VARCHAR(36) PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL,
    day_of_week VARCHAR(10) NOT NULL,
    open_time TIME,
    close_time TIME,
    closed BOOLEAN DEFAULT FALSE,
    break_start_time TIME,
    break_end_time TIME,
    special_hours_description VARCHAR(255),
    UNIQUE(restaurant_id, day_of_week),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

-- Branches Table
CREATE TABLE IF NOT EXISTS branches (
    id VARCHAR(36) PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    country VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(255),
    latitude DOUBLE,
    longitude DOUBLE,
    is_main_branch BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

-- Staff Table
CREATE TABLE IF NOT EXISTS staff (
    id VARCHAR(36) PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    position VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(255),
    user_id VARCHAR(36),
    notes TEXT,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
);

-- Create indices for faster queries
CREATE INDEX idx_restaurants_name ON restaurants(name);
CREATE INDEX idx_restaurants_cuisine_type ON restaurants(cuisine_type);
CREATE INDEX idx_restaurants_city ON restaurants(city);
CREATE INDEX idx_restaurants_active ON restaurants(active);
CREATE INDEX idx_tables_restaurant_id ON restaurant_tables(restaurant_id);
CREATE INDEX idx_tables_status ON restaurant_tables(status);
CREATE INDEX idx_operating_hours_restaurant_id ON operating_hours(restaurant_id);
CREATE INDEX idx_branches_restaurant_id ON branches(restaurant_id);
CREATE INDEX idx_staff_restaurant_id ON staff(restaurant_id);
CREATE INDEX idx_staff_position ON staff(position);

-- Sample Data: Create three sample restaurants
-- Italian Restaurant
INSERT INTO restaurants (
    id, name, description, address, city, state, zip_code, country, 
    phone_number, email, website, latitude, longitude, cuisine_type, 
    total_capacity, average_rating, total_ratings, active
) VALUES (
    UUID(), 'Italian Bistro', 'Authentic Italian cuisine in a cozy atmosphere',
    '123 Main St', 'New York', 'NY', '10001', 'USA',
    '212-555-1234', 'info@italianbistro.com', 'https://www.italianbistro.com',
    40.7128, -74.0060, 'Italian', 60, 4.5, 120, TRUE
);

-- Set the restaurant ID for adding related data
SET @italian_restaurant_id = (SELECT id FROM restaurants WHERE name = 'Italian Bistro');

-- Add tables for Italian restaurant
INSERT INTO restaurant_tables (id, restaurant_id, table_number, capacity, status, location, accessible)
VALUES 
(UUID(), @italian_restaurant_id, '1', 2, 'AVAILABLE', 'WINDOW', FALSE),
(UUID(), @italian_restaurant_id, '2', 2, 'AVAILABLE', 'WINDOW', FALSE),
(UUID(), @italian_restaurant_id, '3', 4, 'AVAILABLE', 'CENTER', FALSE),
(UUID(), @italian_restaurant_id, '4', 4, 'AVAILABLE', 'CENTER', TRUE),
(UUID(), @italian_restaurant_id, '5', 6, 'AVAILABLE', 'PRIVATE', FALSE),
(UUID(), @italian_restaurant_id, '6', 8, 'AVAILABLE', 'OUTDOOR', FALSE);

-- Add operating hours for Italian restaurant
INSERT INTO operating_hours (id, restaurant_id, day_of_week, open_time, close_time, closed)
VALUES 
(UUID(), @italian_restaurant_id, 'MONDAY', '11:30:00', '22:00:00', TRUE),
(UUID(), @italian_restaurant_id, 'TUESDAY', '11:30:00', '22:00:00', FALSE),
(UUID(), @italian_restaurant_id, 'WEDNESDAY', '11:30:00', '22:00:00', FALSE),
(UUID(), @italian_restaurant_id, 'THURSDAY', '11:30:00', '22:00:00', FALSE),
(UUID(), @italian_restaurant_id, 'FRIDAY', '11:30:00', '23:00:00', FALSE),
(UUID(), @italian_restaurant_id, 'SATURDAY', '11:30:00', '23:00:00', FALSE),
(UUID(), @italian_restaurant_id, 'SUNDAY', '12:00:00', '21:00:00', FALSE);

-- Japanese Restaurant
INSERT INTO restaurants (
    id, name, description, address, city, state, zip_code, country, 
    phone_number, email, website, latitude, longitude, cuisine_type, 
    total_capacity, average_rating, total_ratings, active
) VALUES (
    UUID(), 'Sushi Paradise', 'Fresh and delicious Japanese sushi',
    '456 Oak St', 'San Francisco', 'CA', '94101', 'USA',
    '415-555-6789', 'info@sushiparadise.com', 'https://www.sushiparadise.com',
    37.7749, -122.4194, 'Japanese', 45, 4.7, 89, TRUE
);

-- Set the restaurant ID for adding related data
SET @japanese_restaurant_id = (SELECT id FROM restaurants WHERE name = 'Sushi Paradise');

-- Add tables for Japanese restaurant
INSERT INTO restaurant_tables (id, restaurant_id, table_number, capacity, status, location, accessible)
VALUES 
(UUID(), @japanese_restaurant_id, '1', 2, 'AVAILABLE', 'TATAMI', FALSE),
(UUID(), @japanese_restaurant_id, '2', 2, 'AVAILABLE', 'TATAMI', FALSE),
(UUID(), @japanese_restaurant_id, '3', 4, 'AVAILABLE', 'TATAMI', FALSE),
(UUID(), @japanese_restaurant_id, '4', 4, 'AVAILABLE', 'SUSHI_BAR', TRUE),
(UUID(), @japanese_restaurant_id, '5', 8, 'AVAILABLE', 'PRIVATE', FALSE);

-- Add operating hours for Japanese restaurant
INSERT INTO operating_hours (id, restaurant_id, day_of_week, open_time, close_time, closed, break_start_time, break_end_time)
VALUES 
(UUID(), @japanese_restaurant_id, 'MONDAY', '12:00:00', '22:30:00', FALSE, '15:00:00', '17:00:00'),
(UUID(), @japanese_restaurant_id, 'TUESDAY', '12:00:00', '22:30:00', TRUE, NULL, NULL),
(UUID(), @japanese_restaurant_id, 'WEDNESDAY', '12:00:00', '22:30:00', FALSE, '15:00:00', '17:00:00'),
(UUID(), @japanese_restaurant_id, 'THURSDAY', '12:00:00', '22:30:00', FALSE, '15:00:00', '17:00:00'),
(UUID(), @japanese_restaurant_id, 'FRIDAY', '12:00:00', '23:00:00', FALSE, '15:00:00', '17:00:00'),
(UUID(), @japanese_restaurant_id, 'SATURDAY', '12:00:00', '23:00:00', FALSE, NULL, NULL),
(UUID(), @japanese_restaurant_id, 'SUNDAY', '12:00:00', '22:00:00', FALSE, NULL, NULL);

-- Indian Restaurant
INSERT INTO restaurants (
    id, name, description, address, city, state, zip_code, country, 
    phone_number, email, website, latitude, longitude, cuisine_type, 
    total_capacity, average_rating, total_ratings, active
) VALUES (
    UUID(), 'Spice of India', 'Traditional Indian cuisine with modern twists',
    '789 Pine St', 'Chicago', 'IL', '60601', 'USA',
    '312-555-9012', 'info@spiceofindia.com', 'https://www.spiceofindia.com',
    41.8781, -87.6298, 'Indian', 75, 4.3, 152, TRUE
);

-- Set the restaurant ID for adding related data
SET @indian_restaurant_id = (SELECT id FROM restaurants WHERE name = 'Spice of India');

-- Add tables for Indian restaurant
INSERT INTO restaurant_tables (id, restaurant_id, table_number, capacity, status, location, accessible)
VALUES 
(UUID(), @indian_restaurant_id, '1', 2, 'AVAILABLE', 'WINDOW', FALSE),
(UUID(), @indian_restaurant_id, '2', 2, 'AVAILABLE', 'WINDOW', FALSE),
(UUID(), @indian_restaurant_id, '3', 4, 'AVAILABLE', 'CENTER', FALSE),
(UUID(), @indian_restaurant_id, '4', 4, 'AVAILABLE', 'CENTER', TRUE),
(UUID(), @indian_restaurant_id, '5', 6, 'AVAILABLE', 'OUTDOOR', FALSE),
(UUID(), @indian_restaurant_id, '6', 6, 'AVAILABLE', 'OUTDOOR', FALSE),
(UUID(), @indian_restaurant_id, '7', 10, 'AVAILABLE', 'PRIVATE', FALSE);

-- Add operating hours for Indian restaurant
INSERT INTO operating_hours (id, restaurant_id, day_of_week, open_time, close_time, closed)
VALUES 
(UUID(), @indian_restaurant_id, 'MONDAY', '12:00:00', '23:00:00', FALSE),
(UUID(), @indian_restaurant_id, 'TUESDAY', '12:00:00', '23:00:00', FALSE),
(UUID(), @indian_restaurant_id, 'WEDNESDAY', '12:00:00', '23:00:00', FALSE),
(UUID(), @indian_restaurant_id, 'THURSDAY', '12:00:00', '23:00:00', FALSE),
(UUID(), @indian_restaurant_id, 'FRIDAY', '12:00:00', '23:30:00', FALSE),
(UUID(), @indian_restaurant_id, 'SATURDAY', '12:00:00', '23:30:00', FALSE),
(UUID(), @indian_restaurant_id, 'SUNDAY', '13:00:00', '22:00:00', FALSE);

-- Add staff members for each restaurant
-- Italian Restaurant Staff
INSERT INTO staff (id, restaurant_id, name, position, phone_number, email, active)
VALUES 
(UUID(), @italian_restaurant_id, 'Marco Rossi', 'MANAGER', '212-555-1001', 'marco@italianbistro.com', TRUE),
(UUID(), @italian_restaurant_id, 'Sophia Conti', 'CHEF', '212-555-1002', 'sophia@italianbistro.com', TRUE),
(UUID(), @italian_restaurant_id, 'Antonio Marino', 'WAITER', '212-555-1003', 'antonio@italianbistro.com', TRUE),
(UUID(), @italian_restaurant_id, 'Giulia Esposito', 'HOST', '212-555-1004', 'giulia@italianbistro.com', TRUE);

-- Japanese Restaurant Staff
INSERT INTO staff (id, restaurant_id, name, position, phone_number, email, active)
VALUES 
(UUID(), @japanese_restaurant_id, 'Takashi Yamamoto', 'MANAGER', '415-555-6001', 'takashi@sushiparadise.com', TRUE),
(UUID(), @japanese_restaurant_id, 'Haruki Nakamura', 'CHEF', '415-555-6002', 'haruki@sushiparadise.com', TRUE),
(UUID(), @japanese_restaurant_id, 'Yuki Tanaka', 'WAITER', '415-555-6003', 'yuki@sushiparadise.com', TRUE),
(UUID(), @japanese_restaurant_id, 'Aiko Sato', 'HOST', '415-555-6004', 'aiko@sushiparadise.com', TRUE);

-- Indian Restaurant Staff
INSERT INTO staff (id, restaurant_id, name, position, phone_number, email, active)
VALUES 
(UUID(), @indian_restaurant_id, 'Raj Patel', 'MANAGER', '312-555-9001', 'raj@spiceofindia.com', TRUE),
(UUID(), @indian_restaurant_id, 'Priya Sharma', 'CHEF', '312-555-9002', 'priya@spiceofindia.com', TRUE),
(UUID(), @indian_restaurant_id, 'Vikram Singh', 'WAITER', '312-555-9003', 'vikram@spiceofindia.com', TRUE),
(UUID(), @indian_restaurant_id, 'Ananya Das', 'HOST', '312-555-9004', 'ananya@spiceofindia.com', TRUE);