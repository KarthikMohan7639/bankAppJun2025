# Bank Application

Spring Boot 3 REST API (Java 17, Spring Data JPA, MySQL) for managing users, bank accounts, transactions and loans.

## Run

Requires a MySQL server. Connection settings default to `localhost:3306`, user `root` / `root`, and can be overridden
with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (and `SERVER_PORT`).

```bash
cd Bank_application
./mvnw spring-boot:run
```

Tests run against an in-memory H2 database, so no MySQL is needed:

```bash
./mvnw test
```

## API

| Method | Path | Description |
|---|---|---|
| POST | `/user/createUser` | Create user `{name, email, password}` |
| GET | `/user/all` | List users |
| GET | `/user/getUserByid?id=` | Get user (with accounts and loans) |
| GET | `/user/getbyname?name=` | Search users by name (case-insensitive, partial) |
| PUT | `/user/update/{id}` | Update user |
| DELETE | `/user/delete/{id}` | Delete user (cascades to accounts and loans) |
| POST | `/account/save?userId=` | Open account `{balance, accountNumber?}`; number is generated if omitted |
| GET | `/account/{accountId}` | Get account |
| GET | `/account/byNumber/{accountNumber}` | Get account by account number |
| GET | `/account/getaccountByUserid/{userId}` | List a user's accounts |
| GET | `/account/balance/{accountId}` | Get balance |
| PUT | `/account/deposit/{accountId}/{amount}` | Deposit |
| PUT | `/account/withdraw/{accountId}/{amount}` | Withdraw (fails on insufficient balance) |
| PUT | `/account/fundTransfer/{from}/{to}/{amount}` | Transfer funds |
| DELETE | `/account/delete/{accountId}` | Close account (balance must be 0) |
| POST | `/transaction/save?accountId=` | Record `{amount, type: CREDIT\|DEBIT, description?}`; updates balance |
| GET | `/transaction/{id}` | Get transaction |
| GET | `/transaction/account/{accountId}` | Account statement (newest first) |
| GET | `/transaction/fetchAll/{userId}` | All transactions of a user |
| GET | `/transaction/fetchByAccount/{userId}/{start}/{end}` | By date range (ISO, e.g. `2025-06-01T00:00:00`) |
| GET | `/transaction/getByAmount/{userId}/{min}/{max}` | By amount range |
| POST | `/loan/applyLoan?userId=` | Apply `{amount}` → `PENDING` |
| GET | `/loan/all?status=` | List loans, optionally by status |
| GET | `/loan/{id}` | Get loan |
| GET | `/loan/getLoanDetailsByUserId/{userId}` | A user's loans |
| GET | `/loan/status/{id}` | Loan status |
| PUT | `/loan/update/{id}` | Change amount (only while `PENDING`) |
| PUT | `/loan/approve/{id}` | `PENDING` → `APPROVED` |
| PUT | `/loan/reject/{id}` | `PENDING` → `REJECTED` |
| PUT | `/loan/disburse/{id}/{accountId}` | `APPROVED` → `DISBURSED`, credits the borrower's account |
| PUT | `/loan/repay/{id}/{amount}?accountId=` | Repay (optionally debiting an account); `CLOSED` when fully repaid |
| DELETE | `/loan/deleteloan/{id}` | Delete loan (not while `DISBURSED`) |

Errors are returned as JSON `{timestamp, status, error, message, fieldErrors?}` with `400` for validation and
business-rule failures and `404` for missing resources.
