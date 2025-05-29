# Property Rental Platform System Design

## Architecture Overview
The system follows a layered architecture:
- Presentation Layer: REST API endpoints
- Business Layer: Services implementing business logic
- Data Access Layer: Repositories for database operations
- Security Layer: Keycloak integration for authentication/authorization

## Key Components
1. **Authentication/Authorization**: Keycloak SSO with OIDC
2. **Database**: PostgreSQL with Flyway migrations
3. **API**: RESTful endpoints with proper DTO mapping
4. **Testing**: Testcontainers for integration tests

## Security Flow
1. Client authenticates with Keycloak
2. Keycloak issues JWT token
3. Client includes token in Authorization header
4. Spring Security validates token and checks roles

## Deployment
- Docker containers for all services
- Kubernetes for production (optional)
- CI/CD pipeline with GitHub Actions

## Database Optimization
- Indexes on frequently queried columns (booking dates, property IDs)
- Partitioning for large tables
- Read replicas for reporting queries