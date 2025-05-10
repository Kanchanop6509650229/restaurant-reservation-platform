workspace "Restaurant Reservation System" "C4 model of the restaurant reservation platform" {

    model {
        // People/Actors
        customer = person "Customer" "A person who wants to make a restaurant reservation"
        restaurantOwner = person "Restaurant Owner" "A person who manages restaurant information and reservations"
        restaurantStaff = person "Restaurant Staff" "Restaurant employees who handle reservations"
        systemUser = person "System User" "A user or system that interacts with the API directly"
        apiUser = person "API User" "A user that sends requests directly to service components"

        // Software Systems
        reservationPlatform = softwareSystem "Restaurant Reservation Platform" "Allows customers to find restaurants and make reservations" {
            // Client tier
            webApp = container "Web Application" "Provides reservation functionality to customers via web browser" "React"
            mobileApp = container "Mobile Application" "Provides reservation functionality to customers on mobile devices" "React Native"
            apiClient = container "API Client" "Sends direct requests to the API gateway for automation and integration" "Node.js/Python" "User"

            // API Gateway
            apiGateway = container "API Gateway" "Routes requests to appropriate microservices" "Spring Cloud Gateway"

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

            restaurantService = container "Restaurant Service" "Manages restaurant information" "Spring Boot"
            userService = container "User Service" "Manages user accounts and authentication" "Spring Boot"
            notificationService = container "Notification Service" "Sends notifications to users" "Spring Boot"
            paymentService = container "Payment Service" "Processes payments" "Spring Boot"

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
        systemUser -> apiClient "Sends API requests through for automation and integration" "JSON/HTTPS"
        apiUser -> reservationService "Sends requests directly to for custom integrations" "JSON/HTTPS"

        // Container relationships with more detail
        webApp -> apiGateway "Makes API calls to for all reservation operations" "JSON/HTTPS"
        mobileApp -> apiGateway "Makes API calls to for all reservation operations" "JSON/HTTPS"
        apiClient -> apiGateway "Sends requests to for automated workflows" "JSON/HTTPS"

        apiGateway -> reservationService "Routes reservation-related requests to" "JSON/HTTPS"
        apiGateway -> restaurantService "Routes restaurant profile requests to" "JSON/HTTPS"
        apiGateway -> userService "Routes user authentication and profile requests to" "JSON/HTTPS"
        apiGateway -> notificationService "Routes notification requests to" "JSON/HTTPS"
        apiGateway -> paymentService "Routes payment processing requests to" "JSON/HTTPS"

        // Component relationships within Reservation Service with more detail
        // External API User relationships to components
        apiUser -> reservationController "Sends reservation CRUD requests to" "JSON/HTTPS"
        apiUser -> scheduleController "Sends schedule management requests to" "JSON/HTTPS"

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
        restaurantService -> restaurantDB "Reads from and writes to for restaurant profiles" "JDBC/SQL"
        userService -> userDB "Reads from and writes to for user accounts" "JDBC/SQL"

        // Kafka infrastructure relationships with more detail
        kafka -> zookeeper "Managed by for cluster coordination" "Zookeeper Protocol"
        kafdrop -> kafka "Monitors topics and message flow" "Kafka Protocol"

        // Kafka relationships with more detail
        eventProducer -> kafka "Publishes domain events to" "JSON/Kafka Protocol"
        kafka -> eventConsumer "Delivers domain events to" "JSON/Kafka Protocol"

        restaurantService -> kafka "Publishes restaurant events to" "JSON/Kafka Protocol"
        userService -> kafka "Publishes user events to" "JSON/Kafka Protocol"
        notificationService -> kafka "Publishes notification events to" "JSON/Kafka Protocol"
        paymentService -> kafka "Publishes payment events to" "JSON/Kafka Protocol"

        kafka -> restaurantService "Delivers relevant events to" "JSON/Kafka Protocol"
        kafka -> userService "Delivers relevant events to" "JSON/Kafka Protocol"
        kafka -> notificationService "Delivers relevant events to" "JSON/Kafka Protocol"
        kafka -> paymentService "Delivers relevant events to" "JSON/Kafka Protocol"
    }

    views {
        systemContext reservationPlatform "SystemContext" {
            include *
            autoLayout
            description "The system context diagram for the Restaurant Reservation Platform."
        }

        container reservationPlatform "ReservationServiceFocus" {
            include reservationService
            include apiGateway
            include apiClient
            include kafka
            include reservationDB
            include zookeeper
            include kafdrop
            autoLayout
            description "Container diagram focusing only on the Reservation Service and its direct connections."
        }

        component reservationService "ReservationServiceComponents" {
            include *
            autoLayout
            description "The component diagram for the Reservation Service."
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

        dynamic reservationPlatform "APIClientRequest" "Shows the process of an API client sending a request to the API gateway" {
            systemUser -> apiClient "Initiates API request"
            apiClient -> apiGateway "Sends HTTP request"
            apiGateway -> reservationService "Routes request to appropriate service"
            reservationService -> apiGateway "Returns response"
            apiGateway -> apiClient "Returns HTTP response"
            apiClient -> systemUser "Presents result"
            autoLayout
            description "This diagram shows the sequence of interactions when an API client sends a request to the API gateway."
        }

        dynamic reservationPlatform "ComponentAPIRequest" "Shows the process of an external API user sending a request directly to a component" {
            apiUser -> reservationService "Sends reservation request"
            reservationService -> apiUser "Returns response"
            autoLayout
            description "This diagram shows the sequence of interactions when an external API user sends a request directly to components within the Reservation Service."
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
