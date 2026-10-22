# Expense & Personal Finance Tracker API

**Java · Spring Boot 3 · Spring Data JPA · Hibernate · MySQL · REST**

A RESTful API to manage income, expenses, budgets and spending categories, with monthly summaries and budget tracking.

## Features
- Full CRUD for **categories**, **transactions** (INCOME / EXPENSE) and **budgets**
- Layered architecture: `controller` → `service` → `repository` → `entity` (+ `dto`, `exception`)
- Spring Data JPA + Hibernate with MySQL persistence
- Monthly summary: total income, total expense, balance, breakdown by category
- Budget tracking: limit vs. spent, percent used, status (`WITHIN_BUDGET` / `NEAR_LIMIT` ≥ 80% / `EXCEEDED`)
- Bean Validation on all inputs + centralized `@RestControllerAdvice` error handling (400 / 404 / 409 / 500)

## Prerequisites
Java 17+, Maven 3.8+, MySQL 8 running locally.

## Run
```bash
# 1. (optional) set DB credentials - defaults are root/root on localhost:3306
export DB_USERNAME=root
export DB_PASSWORD=your_password

# 2. start (database `expense_tracker` is created automatically)
mvn spring-boot:run
```
API base URL: `http://localhost:8080/api`

Run tests (uses in-memory H2, no MySQL needed): `mvn test`

## Project structure
```
src/main/java/com/expensetracker
├── controller   REST endpoints
├── service      business logic
├── repository   Spring Data JPA repositories
├── entity       Category, Transaction, Budget, TransactionType
├── dto          request/response records
└── exception    custom exceptions + GlobalExceptionHandler
```

## Endpoints
| Method | URL | Description |
|---|---|---|
| POST / GET | `/api/categories` | create / list categories |
| GET / PUT / DELETE | `/api/categories/{id}` | read / update / delete |
| POST / GET | `/api/transactions` | create / list (filters: `type`, `categoryId`, `month`+`year`) |
| GET / PUT / DELETE | `/api/transactions/{id}` | read / update / delete |
| POST / GET | `/api/budgets` | create / list (filters: `month`, `year`) |
| GET | `/api/budgets/status?month=10&year=2026` | budget vs. actual spending |
| GET / PUT / DELETE | `/api/budgets/{id}` | read / update / delete |
| GET | `/api/summary/monthly?month=10&year=2026` | monthly income/expense summary |

## Sample requests
```bash
# Create categories
curl -X POST localhost:8080/api/categories -H "Content-Type: application/json" \
  -d '{"name":"Food","description":"Groceries and dining"}'
curl -X POST localhost:8080/api/categories -H "Content-Type: application/json" \
  -d '{"name":"Salary","description":"Monthly income"}'

# Add transactions (categoryId from above)
curl -X POST localhost:8080/api/transactions -H "Content-Type: application/json" \
  -d '{"type":"INCOME","amount":50000.00,"description":"October salary","date":"2026-10-01","categoryId":2}'
curl -X POST localhost:8080/api/transactions -H "Content-Type: application/json" \
  -d '{"type":"EXPENSE","amount":1200.50,"description":"Groceries","date":"2026-10-05","categoryId":1}'

# Set a monthly budget
curl -X POST localhost:8080/api/budgets -H "Content-Type: application/json" \
  -d '{"categoryId":1,"month":10,"year":2026,"limitAmount":5000}'

# Track it
curl "localhost:8080/api/budgets/status?month=10&year=2026"
curl "localhost:8080/api/summary/monthly?month=10&year=2026"
```

## Sample error response
```json
{
  "timestamp": "2026-10-19T10:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": { "amount": "Amount must be greater than zero" }
}
```
