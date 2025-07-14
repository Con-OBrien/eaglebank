# 🏦 Eagle Bank API

A simple RESTful banking system built with Java, Spring Boot, PostgreSQL, and Flyway. This service allows management of users, bank accounts, and transactions, with initial discoverability via a HAL-compliant root endpoint.

---

## 📦 Tech Stack

- Java 17+
- Spring Boot 3
- PostgreSQL (via Docker)
- Flyway (for database migration)
- Maven
- HATEOAS for API discoverability

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/bank-api.git
cd bank-api
```

### 2. Start the Database

```bash
docker compose up -d
```

### 3.Run the Spring Boot App

Use Maven to start
```bash
mvn spring-boot:run
```
App will now be available at http://localhost:8080

## 🔍 Discovery

Base URL:
```
GET http://localhost:8080/
```

# ✅ Project Feature Checklist

This project implements a RESTful banking API with users, accounts, and transactions. Below is a detailed feature checklist based on the OpenAPI spec.

---

## 🛠️ Project Setup

- [✅] Java project builds and runs locally
- [✅] PostgreSQL (or other DB) is configured and running
- [✅] Database schema includes `users`, `bank_accounts`, and `transactions`
- [✅] OpenAPI spec file (`openapi.yaml` or `openapi.json`) included
- [ ] Postman collection or cURL examples provided (optional)
- [✅] Configuration via `.env` or application properties

---

## 👤 User Endpoints (`/v1/users`)

### Create & View

- [✅] `POST /v1/users` - Create user
- [✅] `POST /v1/users` - Returns `400` for missing required fields
- [✅] `GET /v1/users/{userId}` - Get **own** user details
- [✅] `GET /v1/users/{userId}` - Returns `403` for other users’ data
- [✅] `GET /v1/users/{userId}` - Returns `404` if user not found

### Update & Delete

- [✅] `PATCH /v1/users/{userId}` - Update **own** user
- [✅] `PATCH /v1/users/{userId}` - Returns `403` for other users
- [✅] `PATCH /v1/users/{userId}` - Returns `404` if user not found
- [✅] `DELETE /v1/users/{userId}` - Delete user with **no accounts**
- [✅] `DELETE /v1/users/{userId}` - Returns `409` if user has accounts
- [✅] `DELETE /v1/users/{userId}` - Returns `403` for other users
- [✅] `DELETE /v1/users/{userId}` - Returns `404` if user not found

---

## 🔐 Authentication

- [✅] `POST /v1/auth/login` - Authenticate and return JWT
- [✅] All routes (except `POST /users` and `POST /auth/login`) require JWT
- [ ] OpenAPI includes Bearer token auth

---

## 🏦 Account Endpoints (`/v1/accounts`)

### Create & List

- [✅] `POST /v1/accounts` - Create account for authenticated user
- [✅] `POST /v1/accounts` - Returns `400` for missing fields
- [✅] `GET /v1/accounts` - List **own** accounts

### View, Update, Delete

- [✅] `GET /v1/accounts/{accountId}` - Get **own** account
- [✅] `GET /v1/accounts/{accountId}` - Returns `403` for other accounts
- [✅] `GET /v1/accounts/{accountId}` - Returns `404` if not found
- [✅] `PATCH /v1/accounts/{accountId}` - Update **own** account
- [✅] `PATCH /v1/accounts/{accountId}` - Returns `403` for other accounts
- [✅] `PATCH /v1/accounts/{accountId}` - Returns `404` if not found
- [ ] `DELETE /v1/accounts/{accountId}` - Delete **own** account
- [ ] `DELETE /v1/accounts/{accountId}` - Returns `403` for other accounts
- [ ] `DELETE /v1/accounts/{accountId}` - Returns `404` if not found

---

## 💸 Transaction Endpoints (`/v1/accounts/{accountId}/transactions`)

### Create

- [✅] `POST` - Deposit into **own** account
- [✅] `POST` - Withdraw with **sufficient funds**
- [✅] `POST` - Returns `422` for insufficient funds
- [✅] `POST` - Returns `403` for other users' accounts
- [✅] `POST` - Returns `404` if account not found
- [✅] `POST` - Returns `400` for missing fields

### View

- [✅] `GET` - List transactions for **own** account
- [✅] `GET` - Returns `403` for other users' accounts
- [✅] `GET` - Returns `404` if account not found
- [✅] `GET /{transactionId}` - Fetch single transaction (own account)
- [✅] `GET /{transactionId}` - Returns `403` for other accounts
- [✅] `GET /{transactionId}` - Returns `404` if transaction not found
- [✅] `GET /{transactionId}` - Returns `404` if transaction-account mismatch

---

## 🚀 Optional / Enhancements

- [ ] Pagination and sorting for `GET` requests
- [ ] Request validation using annotations
- [ ] Global error handler (`@ControllerAdvice`)
- [ ] Swagger UI for testing API locally
- [ ] Unit tests and/or integration tests
- [ ] CI with GitHub Actions or test runner
- [ ] Script or data seeder for dev/testing

---





