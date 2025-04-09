-- User Service Database Initialization Script
-- This script creates and initializes the database schema for the User Service
-- Includes user authentication, authorization, and profile management
-- Author: Restaurant Team
-- Version: 1.0

-- Set global time zone to UTC for consistent timestamp handling
SET GLOBAL time_zone = '+00:00';

-- Create tables for User Service

-- Users Table
-- Stores user authentication and account information
-- Includes security flags for account status and expiration
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Profiles Table
-- Stores additional user information and preferences
-- Linked to users table with a one-to-one relationship
CREATE TABLE IF NOT EXISTS profiles (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    date_of_birth DATE,
    phone_number VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(50),
    state VARCHAR(50),
    zip_code VARCHAR(20),
    country VARCHAR(50),
    avatar_url VARCHAR(255),
    preferred_language VARCHAR(10),
    marketing_consent BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Roles Table
-- Defines user roles in the system
-- Used for role-based access control (RBAC)
CREATE TABLE IF NOT EXISTS roles (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Permissions Table
-- Defines granular permissions in the system
-- Used for fine-grained access control
CREATE TABLE IF NOT EXISTS permissions (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- User-Roles Many-to-Many Relationship
-- Links users to their assigned roles
-- Enables role-based access control
CREATE TABLE IF NOT EXISTS user_roles (
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Role-Permissions Many-to-Many Relationship
-- Links roles to their assigned permissions
-- Defines what actions each role can perform
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id VARCHAR(36) NOT NULL,
    permission_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Insert Default Roles
-- Creates standard system roles: USER and ADMIN
INSERT IGNORE INTO roles (id, name, description) VALUES 
(UUID(), 'USER', 'Standard user role'),
(UUID(), 'ADMIN', 'Administrator role');

-- Insert Default Permissions
-- Creates standard system permissions for user and profile management
INSERT IGNORE INTO permissions (id, name, description) VALUES 
(UUID(), 'user:read', 'Permission to read user data'),
(UUID(), 'user:write', 'Permission to create/update user data'),
(UUID(), 'user:delete', 'Permission to delete user data'),
(UUID(), 'profile:read', 'Permission to read profile data'),
(UUID(), 'profile:write', 'Permission to create/update profile data'),
(UUID(), 'restaurant:read', 'Permission to read restaurant data');

-- Link Permissions to Roles
-- Assigns appropriate permissions to the USER role
SET @user_role_id = (SELECT id FROM roles WHERE name = 'USER');
SET @user_read_permission = (SELECT id FROM permissions WHERE name = 'user:read');
SET @profile_read_permission = (SELECT id FROM permissions WHERE name = 'profile:read');
SET @profile_write_permission = (SELECT id FROM permissions WHERE name = 'profile:write');
SET @restaurant_read_permission = (SELECT id FROM permissions WHERE name = 'restaurant:read');

INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES 
(@user_role_id, @user_read_permission),
(@user_role_id, @profile_read_permission),
(@user_role_id, @profile_write_permission),
(@user_role_id, @restaurant_read_permission);

-- Assigns appropriate permissions to the ADMIN role
SET @admin_role_id = (SELECT id FROM roles WHERE name = 'ADMIN');
SET @user_write_permission = (SELECT id FROM permissions WHERE name = 'user:write');
SET @user_delete_permission = (SELECT id FROM permissions WHERE name = 'user:delete');

INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES 
(@admin_role_id, @user_read_permission),
(@admin_role_id, @user_write_permission),
(@admin_role_id, @user_delete_permission),
(@admin_role_id, @profile_read_permission),
(@admin_role_id, @profile_write_permission),
(@admin_role_id, @restaurant_read_permission);

-- Create an admin user (password: admin123)
-- INSERT IGNORE INTO users (id, username, email, password, enabled) VALUES 
-- (UUID(), 'admin', 'admin@example.com', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', TRUE);

-- SET @admin_user_id = (SELECT id FROM users WHERE username = 'admin');
-- INSERT IGNORE INTO user_roles (user_id, role_id) VALUES 
-- (@admin_user_id, @admin_role_id);

-- Create indices for faster queries
-- Improves performance of common lookup operations
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_profiles_user_id ON profiles(user_id);