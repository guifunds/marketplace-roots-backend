<div align="center">

# Roots — Backend

**REST API for the Roots B2B Marketplace**

Connecting Brazilian producers and exporters with international buyers.

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql)
![Flyway](https://img.shields.io/badge/Flyway-Migrations-CC0200?style=flat-square&logo=flyway)
![Stripe](https://img.shields.io/badge/Stripe-Payments-635BFF?style=flat-square&logo=stripe)

</div>

---

## Overview

This backend powers the **Roots Marketplace**, a B2B platform that connects Brazilian suppliers with international buyers.

The API handles the complete early-access onboarding flow:

- Company registration
- Email confirmation
- Payment processing (Stripe or Demo Mode)
- Signup confirmation
- Health monitoring with Spring Boot Actuator

---

## Tech Stack

| Technology | Description |
|------------|-------------|
| Java 21 | Programming language |
| Spring Boot 3.5 | REST API framework |
| PostgreSQL | Relational database |
| Flyway | Database versioning |
| Stripe | Payment processing |
| Spring Boot Actuator | Health checks & metrics |
| Maven Wrapper | Build tool |

---

## Prerequisites

Before running the application, ensure you have:

- JDK 21
- PostgreSQL 16
- Docker (recommended)

Infrastructure setup:

> See the [root README](../README.md#1-start-the-infrastructure-postgresql--email)

---

## Running the Application

Start the application:

```bash
./mvnw spring-boot:run
```

### Services

| Service | URL |
|---------|-----|
| REST API | http://localhost:8080 |
| Health Check | http://localhost:8081/actuator/health |
| Actuator | http://localhost:8081/actuator |

Flyway migrations run automatically during startup.

---

## Running Tests

```bash
./mvnw test
```

---

## Project Structure

```text
src/main/java/com/origem/backend/

├── config/
│   ├── CORS
│   ├── Stripe
│   └── Application Configuration
│
├── domain/
│   ├── Signup
│   ├── ProfileType
│   └── SignupStatus
│
├── dto/
│   ├── Requests
│   └── Responses
│
├── exception/
│
├── repository/
│
├── service/
│   ├── SignupService
│   ├── PaymentService
│   └── EmailService
│
└── web/
    ├── Controllers
    ├── Exception Handler
    └── Rate Limiting
```

Resources

```text
src/main/resources/

├── application.yml
├── application-prod.yml
└── db/
    └── migration/
```

---

## Configuration

All configuration is managed through environment variables.

The default development configuration works out of the box with the project's root `docker-compose.yml`.

---

## Payment Modes

### Demo Mode

When `DEMO_MODE=true` (default), the endpoint

```http
POST /api/signups/{id}/pay
```

marks the signup as paid without contacting Stripe.

This mode is intended for:

- Local development
- Automated testing
- CI/CD pipelines

### Stripe Mode

To use Stripe:

```properties
DEMO_MODE=false
```

Configure your Stripe API keys and webhook.

For detailed instructions, see the [Configuration Guide](../docs/02-configuration.md#payments--demo-mode-vs-real-stripe).

---

## Features

- RESTful API
- Stripe Integration
- Demo Payment Mode
- Email Confirmation
- Flyway Migrations
- PostgreSQL
- Spring Boot Actuator
- Global Exception Handling
- Rate Limiting
- Environment-based Configuration

---

## License

This project is licensed under the MIT License.
