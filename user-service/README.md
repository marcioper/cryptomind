# CryptoMind User Service

This service handles user registration, authentication, and user preferences for the CryptoMind platform.

---

## Prerequisites

- Java 21
- Gradle (included via wrapper)
- Docker (for PostgreSQL database)

---

## How to Run

```bash
# 1. Start PostgreSQL (only do this once)
docker run --name cryptomind-postgres -e POSTGRES_PASSWORD=pass1234 -p 5432:5432 -d postgres:15

# 2. Configure Application Properties (check src/main/resources/application.properties)
# Copy and paste these lines into application.properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=pass1234
spring.jpa.hibernate.ddl-auto=update

# 3. Run Locally (for development)
./gradlew bootRun
# Service available at: http://localhost:8080/health

# 4. Build and Run with Docker
./gradlew bootJar
docker build -t cryptomind-user-service .
docker run -p 8080:8080 cryptomind-user-service

# Health Check
curl http://localhost:8080/health
# Should return:
# User Service OK!
