workspace "Restaurant Reservation System" "C4 model of the restaurant reservation platform" {

    model {
        // People/Actors
        customer = person "Customer" "A person who wants to make a restaurant reservation"
        restaurantOwner = person "Restaurant Owner" "A person who manages restaurant information and reservations"
        restaurantStaff = person "Restaurant Staff" "Restaurant employees who handle reservations"

        // Software Systems
        reservationPlatform = softwareSystem "Restaurant Reservation Platform" "Allows customers to find restaurants and make reservations" {
            // Client tier
            webApp = container "Web Application" "Provides reservation functionality to customers via web browser" "React"
            mobileApp = container "Mobile Application" "Provides reservation functionality to customers on mobile devices" "React Native"

            // Each service exposes its own REST API endpoints
            // No API Gateway is used in this architecture

            // Microservices
            reservationService = container "Reservation Service" "Handles all aspects of restaurant reservations" "Spring Boot, Java 17" {
                // API Layer components
                reservationController = component "Reservation Controller" "Handles reservation CRUD operations" "Spring MVC"
                scheduleController = component "Schedule Controller" "Handles restaurant schedule operations" "Spring MVC"

                // Service Layer components
                reservationServiceComponent = component "Reservation Service Component" "Core reservation business logic" "Spring Service"
                tableAvailabilityService = component "Table Availability Service" "Manages table assignments" "Spring Service"
                restaurantValidationService = component "Restaurant Validation Service" "Validates restaurant data" "Spring Service"

                // Repository Layer components
                reservationRepository = component "Reservation Repository" "Data access for reservations" "Spring Data JPA"
                scheduleRepository = component "Schedule Repository" "Data access for schedules" "Spring Data JPA"

                // Domain Layer components
                reservationEntity = component "Reservation Entity" "Reservation domain model" "JPA Entity"
                scheduleEntity = component "Schedule Entity" "Schedule domain model" "JPA Entity"

                // Kafka Layer components
                eventProducer = component "Event Producer" "Publishes domain events" "Spring Kafka"
                eventConsumer = component "Event Consumer" "Consumes events from other services" "Spring Kafka"
            }

            restaurantService = container "Restaurant Service" "Manages restaurant information" "Spring Boot, Java 17" {
                // API Layer components
                restaurantController = component "Restaurant Controller" "Handles restaurant CRUD operations" "Spring MVC"

                // Service Layer components
                restaurantServiceComponent = component "Restaurant Service Component" "Core restaurant business logic" "Spring Service"
                locationService = component "Location Service" "Manages restaurant locations and seating" "Spring Service"

                // Repository Layer components
                restaurantRepository = component "Restaurant Repository" "Data access for restaurants" "Spring Data JPA"

                // Domain Layer components
                restaurantEntity = component "Restaurant Entity" "Restaurant domain model" "JPA Entity"

                // Kafka Layer components
                restaurantEventProducer = component "Restaurant Event Producer" "Publishes restaurant domain events" "Spring Kafka"
                restaurantEventConsumer = component "Restaurant Event Consumer" "Consumes events from other services" "Spring Kafka"
            }

            userService = container "User Service" "Manages user accounts and authentication" "Spring Boot, Java 17" {
                // API Layer components
                userController = component "User Controller" "Handles user CRUD operations" "Spring MVC"
                authController = component "Auth Controller" "Handles authentication and authorization" "Spring MVC"

                // Service Layer components
                userServiceComponent = component "User Service Component" "Core user business logic" "Spring Service"
                authService = component "Auth Service" "Manages authentication and authorization" "Spring Service"
                profileService = component "Profile Service" "Manages user profiles" "Spring Service"

                // Repository Layer components
                userRepository = component "User Repository" "Data access for users" "Spring Data JPA"
                roleRepository = component "Role Repository" "Data access for user roles" "Spring Data JPA"

                // Domain Layer components
                userEntity = component "User Entity" "User domain model" "JPA Entity"
                roleEntity = component "Role Entity" "Role domain model" "JPA Entity"

                // Kafka Layer components
                userEventProducer = component "User Event Producer" "Publishes user domain events" "Spring Kafka"
                userEventConsumer = component "User Event Consumer" "Consumes events from other services" "Spring Kafka"
            }

            // Message broker and supporting infrastructure
            zookeeper = container "Zookeeper" "Coordinates Kafka cluster" "Apache Zookeeper" "Infrastructure"
            kafka = container "Apache Kafka" "Message broker for event-driven communication" "Apache Kafka" "Message Bus"
            kafdrop = container "Kafdrop" "Web UI for monitoring Kafka topics" "Kafdrop" "Infrastructure"

            // Data tier - explicitly tagged as databases
            reservationDB = container "Reservation Database" "Stores reservation information" "MySQL" "Database"
            restaurantDB = container "Restaurant Database" "Stores restaurant information" "MySQL" "Database"
            userDB = container "User Database" "Stores user account information" "MySQL" "Database"
        }

        // External relationships with more detail
        customer -> webApp "Uses to browse restaurants and make reservations" "HTTPS"
        customer -> mobileApp "Uses to browse restaurants and make reservations on mobile devices" "HTTPS"
        restaurantOwner -> webApp "Manages restaurant profile, menus, and reservation settings through" "HTTPS"
        restaurantStaff -> webApp "Manages daily reservations and table assignments through" "HTTPS"

        // Container relationships with more detail - direct REST API calls
        webApp -> reservationService "Makes API calls for table booking, availability checking, and reservation management to" "JSON/HTTPS"
        webApp -> restaurantService "Makes API calls for restaurant browsing, menu viewing, and location data to" "JSON/HTTPS"
        webApp -> userService "Makes API calls for user registration, authentication, and profile management to" "JSON/HTTPS"

        mobileApp -> reservationService "Makes API calls for table booking, availability checking, and reservation management to" "JSON/HTTPS"
        mobileApp -> restaurantService "Makes API calls for restaurant browsing, menu viewing, and location data to" "JSON/HTTPS"
        mobileApp -> userService "Makes API calls for user registration, authentication, and profile management to" "JSON/HTTPS"

        // Component relationships within Reservation Service with more detail

        // Controller relationships with more detail
        reservationController -> reservationServiceComponent "Uses for reservation business logic" "Java Method Calls"
        scheduleController -> tableAvailabilityService "Uses for table management" "Java Method Calls"

        reservationServiceComponent -> tableAvailabilityService "Uses to find and assign tables" "Java Method Calls"
        reservationServiceComponent -> restaurantValidationService "Uses to validate restaurant data" "Java Method Calls"
        reservationServiceComponent -> reservationRepository "Uses for reservation persistence" "Java Method Calls"
        tableAvailabilityService -> scheduleRepository "Uses for schedule data access" "Java Method Calls"
        restaurantValidationService -> eventProducer "Uses to publish validation events" "Java Method Calls"

        reservationRepository -> reservationEntity "Uses for ORM mapping" "JPA"
        scheduleRepository -> scheduleEntity "Uses for ORM mapping" "JPA"

        reservationServiceComponent -> eventProducer "Publishes reservation events using" "Java Method Calls"
        eventConsumer -> reservationServiceComponent "Delivers external events to" "Java Method Calls"

        // Database relationships with more detail
        reservationRepository -> reservationDB "Reads from and writes to for reservation persistence" "JDBC/SQL"
        scheduleRepository -> reservationDB "Reads from and writes to for schedule management" "JDBC/SQL"

        // Restaurant Service database relationships
        restaurantRepository -> restaurantDB "Reads from and writes to for restaurant data" "JDBC/SQL"

        // User Service database relationships
        userRepository -> userDB "Reads from and writes to for user accounts" "JDBC/SQL"
        roleRepository -> userDB "Reads from and writes to for user roles" "JDBC/SQL"

        // Kafka infrastructure relationships with more detail
        kafka -> zookeeper "Managed by for cluster coordination" "Zookeeper Protocol"
        kafdrop -> kafka "Monitors topics and message flow" "Kafka Protocol"

        // Kafka relationships with more detail
        eventProducer -> kafka "Publishes domain events to" "JSON/Kafka Protocol"
        kafka -> eventConsumer "Delivers domain events to" "JSON/Kafka Protocol"

        // Restaurant Service Kafka relationships
        restaurantEventProducer -> kafka "Publishes restaurant events to" "JSON/Kafka Protocol"
        kafka -> restaurantEventConsumer "Delivers restaurant events to" "JSON/Kafka Protocol"

        // User Service Kafka relationships
        userEventProducer -> kafka "Publishes user events to" "JSON/Kafka Protocol"
        kafka -> userEventConsumer "Delivers user events to" "JSON/Kafka Protocol"

        // Restaurant Service internal component relationships
        restaurantController -> restaurantServiceComponent "Uses for restaurant operations" "Java Method Calls"
        restaurantServiceComponent -> restaurantRepository "Uses for data access" "Java Method Calls"
        restaurantServiceComponent -> locationService "Uses for location validation" "Java Method Calls"
        locationService -> restaurantRepository "Uses for location data" "Java Method Calls"
        restaurantServiceComponent -> restaurantEventProducer "Publishes events using" "Java Method Calls"
        restaurantEventConsumer -> restaurantServiceComponent "Delivers events to" "Java Method Calls"

        // Restaurant Service entity relationships
        restaurantRepository -> restaurantEntity "Uses for ORM mapping" "JPA"

        // User Service internal component relationships
        userController -> userServiceComponent "Uses for user operations" "Java Method Calls"
        authController -> authService "Uses for authentication" "Java Method Calls"
        userServiceComponent -> userRepository "Uses for data access" "Java Method Calls"
        userServiceComponent -> authService "Uses for credential validation" "Java Method Calls"
        authService -> userRepository "Uses for user verification" "Java Method Calls"
        authService -> roleRepository "Uses for role management" "Java Method Calls"
        profileService -> userRepository "Uses for profile data" "Java Method Calls"
        userServiceComponent -> userEventProducer "Publishes events using" "Java Method Calls"
        userEventConsumer -> userServiceComponent "Delivers events to" "Java Method Calls"

        // User Service entity relationships
        userRepository -> userEntity "Uses for ORM mapping" "JPA"
        roleRepository -> roleEntity "Uses for ORM mapping" "JPA"
        userEntity -> roleEntity "Has many" "JPA @ManyToMany"
        roleEntity -> userEntity "Belongs to many" "JPA @ManyToMany mappedBy"

        // Direct client-to-controller relationships
        // Clients directly access REST controllers in each service

        webApp -> reservationController "Makes reservation booking and management requests to" "HTTP/REST"
        webApp -> scheduleController "Makes availability checking and schedule management requests to" "HTTP/REST"
        mobileApp -> reservationController "Makes reservation booking and management requests to" "HTTP/REST"
        mobileApp -> scheduleController "Makes availability checking and schedule management requests to" "HTTP/REST"

        // Web and Mobile App to Restaurant Service controllers
        webApp -> restaurantController "Makes restaurant profile and menu viewing requests to" "HTTP/REST"
        mobileApp -> restaurantController "Makes restaurant profile and menu viewing requests to" "HTTP/REST"

        // Web and Mobile App to User Service controllers
        webApp -> userController "Makes user profile management requests to" "HTTP/REST"
        webApp -> authController "Makes login, registration, and token validation requests to" "HTTP/REST"
        mobileApp -> userController "Makes user profile management requests to" "HTTP/REST"
        mobileApp -> authController "Makes login, registration, and token validation requests to" "HTTP/REST"
    }

    views {
        systemContext reservationPlatform "SystemContext" {
            include *
            autoLayout
            description "The system context diagram for the Restaurant Reservation Platform."
        }

        container reservationPlatform "AllServices" {
            include *
            autoLayout
            description "Container diagram showing all services and their connections in the Restaurant Reservation Platform, excluding System User to API Client relationship and API User."
        }

        container reservationPlatform "ReservationServiceFocus" {
            include reservationService
            include webApp
            include mobileApp
            include customer
            include restaurantStaff
            include kafka
            include reservationDB
            include zookeeper
            include kafdrop
            autoLayout
            description "Container diagram focusing only on the Reservation Service and its direct connections with actual users."
        }

        container reservationPlatform "RestaurantServiceFocus" {
            include restaurantService
            include webApp
            include mobileApp
            include customer
            include restaurantOwner
            include kafka
            include restaurantDB
            include zookeeper
            include kafdrop
            autoLayout
            description "Container diagram focusing only on the Restaurant Service and its direct connections with actual users."
        }

        container reservationPlatform "UserServiceFocus" {
            include userService
            include webApp
            include mobileApp
            include customer
            include restaurantOwner
            include restaurantStaff
            include kafka
            include userDB
            include zookeeper
            include kafdrop
            autoLayout
            description "Container diagram focusing only on the User Service and its direct connections with actual users."
        }

        component reservationService "ReservationServiceComponents" {
            include *
            include customer
            include restaurantStaff
            autoLayout
            description "The component diagram for the Reservation Service showing interactions with actual users."
        }

        component restaurantService "RestaurantServiceComponents" {
            include *
            include customer
            include restaurantOwner
            autoLayout
            description "The component diagram for the Restaurant Service showing interactions with actual users."
        }

        component userService "UserServiceComponents" {
            include *
            include customer
            include restaurantOwner
            include restaurantStaff
            autoLayout
            description "The component diagram for the User Service showing interactions with actual users."
        }

        dynamic reservationService "ReservationCreation" "Shows the process of creating a new reservation" {
            reservationController -> reservationServiceComponent "createReservation(request)"
            reservationServiceComponent -> restaurantValidationService "validateRestaurant(restaurantId)"
            restaurantValidationService -> eventProducer "publishValidationRequest()"
            eventProducer -> kafka "send()"
            kafka -> eventConsumer "receive()"
            eventConsumer -> reservationServiceComponent "handleValidationResponse()"
            reservationServiceComponent -> tableAvailabilityService "findAndAssignTable()"
            tableAvailabilityService -> scheduleRepository "findAvailableTables()"
            scheduleRepository -> reservationDB "query(date, restaurantId)"
            reservationServiceComponent -> reservationRepository "save(reservation)"
            reservationRepository -> reservationDB "save(reservation)"
            reservationServiceComponent -> eventProducer "publishReservationCreatedEvent()"
            autoLayout
            description "This diagram shows the sequence of interactions when creating a new reservation."
        }

        dynamic restaurantService "RestaurantCreation" "Shows the process of creating a new restaurant" {
            restaurantController -> restaurantServiceComponent "createRestaurant(request)"
            restaurantServiceComponent -> locationService "validateLocation(locationData)"
            restaurantServiceComponent -> restaurantRepository "save(restaurant)"
            restaurantRepository -> restaurantDB "save(restaurant)"
            restaurantServiceComponent -> restaurantEventProducer "publishRestaurantCreatedEvent()"
            restaurantEventProducer -> kafka "send()"
            autoLayout
            description "This diagram shows the sequence of interactions when creating a new restaurant."
        }

        dynamic userService "UserRegistration" "Shows the process of registering a new user" {
            userController -> userServiceComponent "registerUser(request)"
            userServiceComponent -> authService "validateCredentials(credentials)"
            authService -> roleRepository "findDefaultRole()"
            roleRepository -> userDB "query(roleName)"
            userServiceComponent -> userRepository "save(user)"
            userRepository -> userDB "save(user)"
            userServiceComponent -> userEventProducer "publishUserCreatedEvent()"
            userEventProducer -> kafka "send()"
            autoLayout
            description "This diagram shows the sequence of interactions when registering a new user."
        }

        dynamic userService "UserAuthentication" "Shows the process of authenticating a user" {
            authController -> authService "authenticate(credentials)"
            authService -> userRepository "findByUsername(username)"
            userRepository -> userDB "query(username)"
            authService -> authController "returnAuthToken(token)"
            autoLayout
            description "This diagram shows the sequence of interactions when authenticating a user."
        }

        styles {
            element "Person" {
                shape Person
                background #08427b
                color #ffffff
            }

            element "Software System" {
                background #1168bd
                color #ffffff
            }

            element "Container" {
                background #438dd5
                color #ffffff
            }

            element "Component" {
                background #85bbf0
                color #000000
            }

            element "Database" {
                shape Cylinder
                background #438dd5
                color #ffffff
            }

            element "Message Bus" {
                shape Pipe
                background #438dd5
                color #ffffff
            }

            element "Web Browser" {
                shape WebBrowser
            }

            element "Mobile App" {
                shape MobileDeviceLandscape
            }

            element "User" {
                shape Person
                background #08427b
                color #ffffff
            }
        }
    }
}
