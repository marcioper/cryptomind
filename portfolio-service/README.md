# CryptoMind Portfolio Service

Manages user crypto portfolios, tracks balances, and integrates with exchanges and the recommendation service.

---

## Prerequisites

- Java 21
- Gradle (included via wrapper)
- Docker (for PostgreSQL, Kafka, Zookeeper)

---

## How to Run

```bash
# 1. Start PostgreSQL
docker run --name cryptomind-postgres -e POSTGRES_PASSWORD=pass1234 -p 5432:5432 -d postgres:15

# 1b. Start Zookeeper & Kafka (from infra/kafka directory, or wherever docker-compose.yml is)
docker-compose up -d zookeeper kafka

# 2. Configure Application Properties (check src/main/resources/application.properties)
# Copy and paste these lines into application.properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=pass1234
spring.kafka.bootstrap-servers=localhost:9092
spring.jpa.hibernate.ddl-auto=update

# 3. Run Locally (for development)
./gradlew bootRun
# Service available at: http://localhost:8081/health

# 4. Build and Run with Docker
./gradlew bootJar
docker build -t cryptomind-portfolio-service .
docker run -p 8081:8080 cryptomind-portfolio-service

# Health Check
curl http://localhost:8081/health
# Should return:
# Portfolio Service OK!
