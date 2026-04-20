# Transactions API

Small Spring Boot REST API to manage transactions in memory.

This project is implemented incrementally with TDD and clean architecture, following the challenge contract.

## Project Overview

The API supports:

- creating transactions
- retrieving transaction IDs by type
- computing the transitive sum of one transaction and all its descendants

Persistence is in memory only (no SQL).

## Tech Stack

- Java 17
- Spring Boot 4 (Web MVC)
- Maven Wrapper
- JUnit 5
- Spring Boot integration tests

## Architecture Overview

The codebase follows a simple clean architecture split:

- `domain`
  - `model`: business entity (`Transaction`)
  - `repository`: repository contract (`TransactionRepository`)
- `application`
  - use cases (`CreateTransactionUseCase`, `GetTransactionsByTypeUseCase`, `GetTransactionSumUseCase`)
- `infrastructure`
  - in-memory repository implementation (`InMemoryTransactionRepository`)
  - Spring bean wiring (`TransactionBeansConfig`)
- `interfaces/rest`
  - HTTP controller (`TransactionController`)
  - global error handling (`GlobalExceptionHandler`)

## Run Locally

```bash
./mvnw -DfailOnNoContracts=false spring-boot:run
```

Application default URL:

- `http://localhost:8080`

## Run Tests

```bash
./mvnw -DfailOnNoContracts=false test
```

## Docker

### Build image

```bash
./mvnw -DfailOnNoContracts=false -DskipTests package
docker build -t transactions-api:local .
```

### Run container

```bash
docker run --rm -p 8080:8080 --name transactions-api-local transactions-api:local
```

## API Endpoints

### 1) Create transaction

`PUT /transactions/{transactionId}`

Request body:

```json
{
  "amount": 1000.0,
  "type": "cars",
  "parent_id": 10
}
```

`parent_id` is optional.

Success response:

```json
{
  "status": "ok"
}
```

Note: this `PUT` endpoint is implemented exactly as specified by the challenge contract.

### 2) Get transaction IDs by type

`GET /transactions/types/{type}`

Success response:

```json
[1, 2, 3]
```

If no transactions exist for the type:

```json
[]
```

### 3) Get transitive sum

`GET /transactions/sum/{transactionId}`

Success response:

```json
{
  "sum": 130.0
}
```

The sum includes:

- the requested transaction amount
- all descendants recursively through `parent_id`

## Error Response Format

Errors are handled consistently by a global exception handler:

```json
{
  "status": 404,
  "error": "Transaction not found: 99",
  "path": "/transactions/sum/99"
}
```

Typical statuses:

- `400` invalid payload / domain validation errors
- `404` missing parent or missing transaction
- `409` duplicate transaction ID

## Design Decisions

- In-memory repository was chosen to match challenge scope and keep behavior explicit.
- `Double` is used for `amount` to align with the API contract (`double`) and keep the challenge implementation simple.
- Repository remains an interface in `domain` so storage can be replaced later (SQL/Mongo/etc.) with minimal changes.
- Global REST error handling is centralized for consistency and easier API usage.
