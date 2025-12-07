# FIRST Spring Boot Application

Financial Institution Registration and Reporting System (FIRST) - Modern Spring Boot Implementation

## Overview

This is a complete rewrite of the legacy FIRST module from Java EE to Spring Boot, following FINA Software Development Standards. The application manages the registration, licensing, and monitoring of financial institutions.

## Technology Stack

- **Java 17+**
- **Spring Boot 3.2.1**
- **Spring Data JPA** with Hibernate
- **Spring Security** with JWT authentication
- **PostgreSQL** database
- **MapStruct** for DTO mapping
- **Lombok** for boilerplate reduction
- **Alfresco ECM** integration for document management
- **OpenAPI 3.0** (Swagger) for API documentation

## Project Structure

```
src/main/java/net/fina/first/
├── config/          # Configuration classes
├── controller/      # REST API controllers
├── dto/             # Data Transfer Objects
│   ├── auth/        # Authentication DTOs
│   ├── common/      # Common DTOs (ApiResponse, PageResponse)
│   ├── ecm/         # ECM/Alfresco DTOs
│   ├── request/     # Request DTOs
│   └── response/    # Response DTOs
├── exception/       # Custom exceptions and handlers
├── mapper/          # MapStruct mappers
├── model/           # JPA entities
│   ├── base/        # Base entity classes
│   └── enums/       # Enumeration types
├── repository/      # Spring Data JPA repositories
├── security/        # Security configuration and JWT
├── service/         # Business logic services
│   └── ecm/         # ECM/Alfresco services
└── util/            # Utility classes
```

## Core Entities

1. **FiRegistry** - Main Financial Institution registry
2. **Branch** - FI branch offices
3. **Administrator** - FI administrators/directors
4. **Beneficiary** - Beneficial owners (hierarchical)
5. **License** - Operating licenses

## FI Types Supported

- BANK - Commercial Banks
- MICROBANK - Microfinance Banks
- MFO - Microfinance Organizations
- CU - Credit Unions
- FEX - Foreign Exchange Bureaus
- LE - Leasing Companies
- PSP - Payment Service Providers
- VASP - Virtual Asset Service Providers
- AMC - Asset Management Companies
- IF - Investment Funds
- BC - Brokerage Companies
- SE - Stock Exchanges
- SR - Securities Registrars
- CD - Central Depositories

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- PostgreSQL 14+
- Alfresco (optional, for document management)

### Configuration

Set the following environment variables or update `application.yml`:

```bash
# Database
export DB_USERNAME=postgres
export DB_PASSWORD=your_password

# JWT
export JWT_ACCESS_SECRET=your-256-bit-access-secret-key
export JWT_REFRESH_SECRET=your-256-bit-refresh-secret-key

# ECM (optional)
export ECM_ENABLED=true
export ECM_ENDPOINT=http://localhost:8081
export ECM_ADMIN_USER=admin
export ECM_ADMIN_PASS=admin
```

### Build and Run

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run

# Run with profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### API Documentation

Once running, access Swagger UI at:
- http://localhost:8080/api/swagger-ui.html

API docs available at:
- http://localhost:8080/api/v3/api-docs

## API Endpoints

### Authentication
- `POST /api/v1/auth/login` - Authenticate and get JWT
- `POST /api/v1/auth/refresh` - Refresh access token
- `POST /api/v1/auth/change-password` - Change password

### FI Registry
- `GET /api/v1/fi-registries` - List all FIs
- `GET /api/v1/fi-registries/{id}` - Get FI by ID
- `POST /api/v1/fi-registries` - Create new FI
- `PUT /api/v1/fi-registries/{id}` - Update FI
- `POST /api/v1/fi-registries/{id}/submit` - Submit for approval
- `POST /api/v1/fi-registries/{id}/approve` - Approve FI
- `POST /api/v1/fi-registries/{id}/reject` - Reject FI

### Branches, Administrators, Beneficiaries, Licenses
- Nested under `/api/v1/fi-registries/{fiRegistryId}/...`

### Reference Data
- `GET /api/v1/reference-data/fi-types` - FI types
- `GET /api/v1/reference-data/regions` - Regions
- `GET /api/v1/reference-data/legal-forms` - Legal forms
- `GET /api/v1/reference-data/license-types` - License types

### Documents (ECM)
- `POST /api/v1/fi-registries/{id}/documents` - Upload document
- `GET /api/v1/fi-registries/{id}/documents/{docId}/download` - Download

## Security

- JWT-based authentication
- Role-based access control (RBAC)
- Permission-based authorization
- Password policy enforcement
- Account lockout after failed attempts

## Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Migration from Legacy

This application replaces the legacy Java EE FIRST module with:
- EJBs → Spring Services
- JPA with manual management → Spring Data JPA
- JAAS → Spring Security with JWT
- CDI → Spring DI
- JAX-RS → Spring MVC REST

## License

Proprietary - FINA

## Support

For issues and questions, contact the FINA development team.
