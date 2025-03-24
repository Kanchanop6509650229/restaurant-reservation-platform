-- This script is optional if you're using Hibernate's auto-ddl feature
-- But having explicit schema can be useful for better control

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
    location GEOMETRY(Point, 4326) SRID 4326,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    cuisine_type VARCHAR(100) NOT NULL,
    total_capacity INTEGER,
    average_rating NUMERIC(3,2),
    total_ratings INTEGER,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS restaurant_tables (
    id VARCHAR(36) PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL REFERENCES restaurants(id),
    table_number VARCHAR(10) NOT NULL,
    capacity INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    location VARCHAR(255),
    accessible BOOLEAN DEFAULT FALSE,
    shape VARCHAR(20),
    min_capacity INTEGER,
    combinable BOOLEAN DEFAULT FALSE,
    special_features VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    UNIQUE(restaurant_id, table_number)
);

CREATE TABLE IF NOT EXISTS operating_hours (
    id VARCHAR(36) PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL REFERENCES restaurants(id),
    day_of_week VARCHAR(10) NOT NULL,
    open_time TIME,
    close_time TIME,
    closed BOOLEAN DEFAULT FALSE,
    break_start_time TIME,
    break_end_time TIME,
    special_hours_description VARCHAR(255),
    UNIQUE(restaurant_id, day_of_week)
);

CREATE TABLE IF NOT EXISTS branches (
    id VARCHAR(36) PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL REFERENCES restaurants(id),
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    country VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(255),
    location GEOMETRY(Point, 4326),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    is_main_branch BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS staff (
    id VARCHAR(36) PRIMARY KEY,
    restaurant_id VARCHAR(36) NOT NULL REFERENCES restaurants(id),
    name VARCHAR(255) NOT NULL,
    position VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(255),
    user_id VARCHAR(36),
    notes TEXT,
    active BOOLEAN DEFAULT TRUE
);

-- Create spatial indexes for performance
CREATE INDEX IF NOT EXISTS idx_restaurant_location ON restaurants USING GIST(location);
CREATE INDEX IF NOT EXISTS idx_branch_location ON branches USING GIST(location);