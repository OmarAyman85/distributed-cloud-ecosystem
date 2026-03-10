# Authy Service - Backend

The Authy service is a centralized authentication and authorization manager designed to support multiple microservices. It provides JWT-based authentication, Multi-Factor Authentication (MFA) via TOTP, and role-based access control.

## Project Structure

```text
authy/
├── src/main/java/com.ayman.distributed/authy/
│   ├── AuthyApplication.java           # Main application entry point
│   ├── config/                         # Configuration classes (Security, OpenAPI, Data Init)
│   ├── controller/                     # REST API Controllers (Authentication, User profile)
│   ├── dto/                            # Data Transfer Objects for API requests/responses
│   ├── exception/                      # Custom exception handling and advice
│   ├── mapper/                         # MapStruct interfaces for Entity/DTO conversion
│   ├── model/                          # Database entities (User, Role, Application, Token)
│   │   ├── embeddable/                  # Embedded JPA types (Address)
│   │   ├── entity/                      # Core JPA Entities
│   │   └── enums/                       # Enumeration types (UserStatus, Gender, etc.)
│   ├── repository/                     # Spring Data JPA Repositories
│   ├── security/                       # Security logic (JWT, MFA, UserDetailsService)
│   │   ├── jwt/                        # JWT generation and validation
│   │   ├── mfa/                        # Two-Factor Authentication logic
│   │   └── service/                    # Security-related services (Logout, UserDetails)
│   └── services/                       # Business logic layer
│       ├── application/                # Application management service
│       ├── authentication/             # Login, registration, and token refresh
│       ├── profilePicture/             # Profile picture storage service
│       └── user/                       # User profile management service
├── src/main/resources/
│   ├── application.yml                 # Application configuration (DB, Security, JWT)
│   └── static/                         # Static assets (if any)
├── src/test/                           # Unit and Integration tests
├── pom.xml                             # Maven project dependencies
└── README.md                           # This file
```

## Key Features

- **Standard Authentication**: Username/Password login with JWT token issuance.
- **MFA Support**: Integrated TOTP (Time-based One-Time Password) for enhanced security.
- **Token Rotation**: Secure refresh token rotation with HTTP-only cookies.
- **Role-Based Access**: Granular control over user roles across different applications.
- **API Documentation**: Integrated Swagger/OpenAPI documentation.

## Setup & Running

### Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL or MariaDB/MySQL

### Configuration

Update `src/main/resources/application.yml` with your database credentials and security secrets.

### Run the Application

```bash
mvn spring-boot:run
```

### API Documentation

Once the application is running, access the Swagger UI at:
`http://localhost:8081/swagger-ui.html`

## End-to-End Tests

E2E tests are located in `src/test/java/com.ayman.distributed/authy/e2e`. Run them using:

```bash
mvn test -Dtest=com.ayman.distributed.authy.e2e.*
```
