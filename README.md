# OtoPart Platform

Automotive parts e-commerce and delivery platform built with Spring Boot 3.2.

## Tech Stack

- Java 17, Spring Boot 3.2
- PostgreSQL 15, Flyway
- Redis, Spring Security, JWT
- Groq AI (Llama3), Swagger/OpenAPI
- Docker Compose

## Getting Started

```bash
docker-compose up -d postgres redis
mvn spring-boot:run
```

Swagger UI: `http://localhost:9090/api/swagger-ui.html`

## API Endpoints

| Module | Base Path | Auth |
|--------|-----------|------|
| Auth | /api/auth | Public |
| Products | /api/products | Public (GET) |
| Categories | /api/categories | Public (GET) |
| Suppliers | /api/suppliers | Admin |
| Delivery | /api/delivery | Public (GET) |
| Garage | /api/vehicles/garage | User |
| Orders | /api/orders | User |
| Loyalty | /api/loyalty | User |
| Coupons | /api/coupons | User |
| Courier | /api/courier | Courier/Admin |
| AI Assistant | /api/ai | Public |
| Payments | /api/payments | User |
