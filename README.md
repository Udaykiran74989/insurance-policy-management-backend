# Insurance Policy Management System

A medium-simple, single-application Spring Boot backend for managing customers, insurance products, policies and claims. It is intentionally structured so a Java full-stack developer can understand and explain the complete codebase in an interview.

## Features

- Customer registration, JWT login, profile updates and BCrypt password changes
- Customer-only access to their own policies and claims
- Public insurance product browsing and product-type filtering
- Admin product management with safe deactivation instead of destructive deletes
- Indicative premium calculator with a small, transparent formula
- Policy purchase, generated policy numbers and cancellation
- Claim submission for active customer policies
- Admin claim review workflow: `SUBMITTED` → `UNDER_REVIEW` → `APPROVED` or `REJECTED`
- Admin dashboard summary
- DTO-only REST responses, validation, consistent errors and OpenAPI documentation

## Technology

Java 17, Spring Boot 3.3, Maven, Spring Web, Spring Data JPA/Hibernate, MySQL 8, Spring Security, JWT, BCrypt, Jakarta Validation, Lombok, Springdoc OpenAPI, JUnit 5 and Mockito.

## Architecture

The application uses one deployable Spring Boot service with clean layers:

`Controller → Service → ServiceImpl → Repository → MySQL`

JPA entities are internal persistence models. Request and response DTOs are used at the API boundary, so passwords and other security details are never returned.

## Business flow

1. A customer registers or logs in and receives a JWT containing `userId`, `email` and `role`.
2. The customer browses active products and calls the premium calculator with age, coverage and duration.
3. A purchase repeats the calculation on the server and creates an `ACTIVE` policy with a number such as `POL-2026-000001`.
4. A customer submits a claim only against their own active policy.
5. An admin moves a claim through review and approves or rejects it. Rejection requires remarks.

The premium formula is deliberately not an actuarial model:

`finalPremium = basePremium + ageAdjustment + coverageAdjustment + durationAdjustment`

- Customers below 25 receive a 10% age adjustment.
- Customers above 50 receive a 20% age adjustment.
- Coverage contributes 5% of base premium in proportion to the product's maximum coverage.
- Each year after the first contributes 2% of base premium.

## Project structure

```text
src/main/java/com/insurance/policy/
├── config/          Security, OpenAPI and optional demo seed data
├── controller/      REST endpoints
├── dto/             Request and response records
├── entity/          User, InsuranceProduct, Policy and Claim
├── exception/       Domain exceptions and global error handler
├── repository/      Spring Data JPA repositories
├── security/        JWT service, filter and user details service
└── service/         Interfaces and service/impl business logic
```

## Database setup

1. Install MySQL 8 and create a user with permission to create databases, or create the database manually:

```sql
CREATE DATABASE insurance_policy_db;
```

2. Set environment variables as needed:

```bash
export DB_URL="jdbc:mysql://localhost:3306/insurance_policy_db?useSSL=false&serverTimezone=UTC"
export DB_USERNAME="root"
export DB_PASSWORD="your-password"
export JWT_SECRET="use-a-random-secret-at-least-32-characters-long"
```

The defaults are convenient for local development only. `spring.jpa.hibernate.ddl-auto=update` creates and updates the tables for this demo.

To load the four products and three demo accounts, start with `SEED_DATA=true`. The runner is idempotent when the database already contains data.

## Run

```bash
mvn clean spring-boot:run
```

The server uses `PORT` when supplied and otherwise starts on `http://localhost:8080`.

Build and unit tests:

```bash
mvn clean verify
```

## Demo credentials

Use `SEED_DATA=true` on the first run:

| Role | Email | Password |
|---|---|---|
| ADMIN | admin@insurance.com | Admin@123 |
| CUSTOMER | anita@example.com | Customer@123 |
| CUSTOMER | rahul@example.com | Customer@123 |

## Swagger

Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html). Use the `Authorize` button and enter `Bearer <token>` after logging in.

## API list

### Authentication

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

### Customer

- `GET /api/v1/customers/profile`
- `PUT /api/v1/customers/profile`
- `PUT /api/v1/customers/change-password`

### Products

- `GET /api/v1/products`
- `GET /api/v1/products/{id}`
- `GET /api/v1/products?type=HEALTH`
- `POST /api/v1/products` — admin
- `PUT /api/v1/products/{id}` — admin
- `DELETE /api/v1/products/{id}` — admin; deactivates the product

### Premium and policies

- `POST /api/v1/premium/calculate`
- `POST /api/v1/policies`
- `GET /api/v1/policies`
- `GET /api/v1/policies/{id}`
- `PUT /api/v1/policies/{id}/cancel`
- `GET /api/v1/admin/policies`
- `PUT /api/v1/admin/policies/{id}/status`

### Claims and administration

- `POST /api/v1/claims`
- `GET /api/v1/claims`
- `GET /api/v1/claims/{id}`
- `GET /api/v1/admin/claims`
- `PUT /api/v1/admin/claims/{id}/status`
- `GET /api/v1/admin/dashboard`
- `GET /api/v1/admin/customers`

All admin endpoints require the `ADMIN` role. Customer policy and claim endpoints scope database queries to the authenticated customer's id. The seeded database contains two sample policies and two sample claims in addition to the demo users and products.

## Sample requests

Register:

```json
{
  "name": "Priya Nair",
  "email": "priya@example.com",
  "password": "Customer@123",
  "phone": "9000000003",
  "address": "Chennai"
}
```

Calculate premium:

```json
{
  "productId": 1,
  "age": 35,
  "coverageAmount": 500000,
  "duration": 1
}
```

Purchase:

```json
{
  "productId": 1,
  "age": 35,
  "coverageAmount": 500000,
  "duration": 1
}
```

Submit claim:

```json
{
  "policyId": 1,
  "claimAmount": 100000,
  "reason": "Hospitalisation",
  "description": "Admitted for treatment after an accident."
}
```

Approve a claim after moving it to review:

```json
{
  "status": "UNDER_REVIEW",
  "adminRemarks": "Documents received"
}
```

```json
{
  "status": "APPROVED",
  "adminRemarks": "Claim verified and approved"
}
```

## Interview explanation

This is a modular monolith, not a microservices system. Controllers handle HTTP concerns, services enforce business rules, repositories handle persistence, and DTOs prevent entity leakage. Spring Security authenticates credentials through BCrypt and puts a stateless JWT in the request context. Ownership is enforced in repository queries such as `findByIdAndCustomerId`, while method security separates customer and admin routes. The claim workflow is a small state transition rule in the service layer, and the premium calculator demonstrates testable business logic without pretending to be real actuarial pricing.