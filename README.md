# MiniPay Backend README

## Overview

MiniPay is a backend service for processing card and wallet payments for merchants, handling daily settlements, fee
calculations (percentage-based and/or fixed with caps and VAT), and processor simulations. It includes secure JWT
authentication with roles/permissions, merchant onboarding, payment initiation/approval, reporting with exports, and
webhooks for notifications.

Built with Spring Boot (Java), PostgreSQL, and Maven. Key features:

- Secure auth and role-based access.
- Payment simulation (no real processors; use `/simulate` endpoint).
- Fee computation: Merchant fees minus processor costs = MiniPay income.
- Daily batch settlements.
- API docs via Swagger/OpenAPI.
- Robust tests (unit/integration).

This README covers setup, usage, and development notes.

## 1. Architecture Diagram

MiniPay follows a layered monolithic architecture for simplicity, with potential for microservices scaling. Key
components:

- **Client Layer**: External apps/merchants interact via REST APIs.
- **API Layer**: Spring Controllers handle requests, secured by JWT.
- **Service Layer**: Business logic (e.g., PaymentService, SettlementService).
- **Repository Layer**: JPA/Hibernate for DB interactions.
- **Database**: PostgreSQL for persistence.
- **External**: Webhooks to merchant callbacks; simulated processor callbacks.

```mermaid
graph TD
    A[Client/Merchant App] -->|REST API Calls| B[API Gateway / Load Balancer]
    B -->|JWT Auth| C[Controllers: Authentication, Merchants, Payments, Settlements, Reports]
    C --> D[Services: AuthenticationService, MerchantService, PaymentService, SettlementService, ReportGenerationService]
    D --> E[Repositories: JPA Entities/DAOs]
    E --> F[PostgreSQL DB]
    D -->|Simulate| G[Processor Simulator (Internal)]
    D -->|HMAC-Signed| H[Webhooks to Merchant Callback URLs]
    I[Swagger UI] --> C
    J[Tests: JUnit/Mockito] --> D
```

This design prioritizes security (JWT), scalability (paging/filters), and extensibility (e.g., add real processors
later).

## 2. ERD Snapshot

The Entity-Relationship Diagram (ERD) outlines core tables. Assumes PostgreSQL with JPA entities.

```mermaid
erDiagram
    USER ||--o{ MERCHANT : "manages"
    MERCHANT ||--|| CHARGE_SETTINGS : "has"
    MERCHANT ||--o{ PAYMENT : "owns"
    PAYMENT ||--o{ SETTLEMENT_ITEM : "included in"
    SETTLEMENT_ITEM }o--|| SETTLEMENT : "belongs to"
    SETTLEMENT ||--|| MERCHANT : "for"

    USER {
        UUID id PK
        string username
        string password_hash
        enum role "ADMIN, MERCHANT_USER"
    }
    MERCHANT {
        UUID id PK
        string name
        string webhook_secret
        string callback_url
        timestamp created_at
    }
    CHARGE_SETTINGS {
        UUID id PK
        UUID merchant_id FK
        decimal percentage_fee
        decimal fixed_fee
        decimal cap
        decimal vat_rate
        decimal processor_percentage_fee
        decimal processor_fixed_fee
    }
    PAYMENT {
        UUID id PK
        string payment_ref UK
        UUID merchant_id FK
        decimal amount
        string currency
        enum status "PENDING, SUCCESS, FAILED"
        enum channel "CARD, WALLET"
        string order_id
        timestamp timestamp
        decimal msc "merchant service charge"
        decimal vat_amount
        decimal processor_fee
        decimal processor_vat
        decimal amount_payable
    }
    SETTLEMENT {
        UUID id PK
        string settlement_ref UK
        string batch_reference
        UUID merchant_id FK
        enum status "PENDING, COMPLETED"
        date settlement_date
        decimal total_amount
    }
    SETTLEMENT_ITEM {
        UUID id PK
        UUID settlement_id FK
        UUID payment_id FK
        decimal amount
    }
```

Notes:

- Relationships: One-to-Many (e.g., Merchant to Payments).
- Fees/VAT stored on Payments for auditability.
- Timestamps use `LocalDateTime` for timezone handling.

## 3. Setup (Docker and Local), Env Variables, Sample .env

### Prerequisites

- Java 17+ (JDK)
- Maven 3.8+
- PostgreSQL 15+ (local or cloud)
- Docker (for containerized setup)

### Local Setup

1. Clone the repo: `git clone https://github.com/bigboytimi/minipay-psp.git`
2. Navigate: `cd minipay-psp`
3. Create DB: Use PostgreSQL client to create `minipay_db` (user: `minipay_user`, pass: `securepass`).
4. Configure `.env` (see sample below).
5. Build: `mvn clean install`

### Docker Setup

1. Ensure Docker Compose is installed.
2. Use `docker-compose.yml` (sample below) for app + DB.
3. Run: `docker-compose up -d`
4. App runs on `http://localhost:8082`; DB on `5432`.

Sample `docker-compose.yml`:

```yaml
version: '3.8'
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: minipay_db
      POSTGRES_USER: minipay_user
      POSTGRES_PASSWORD: securepass
    ports:
      - "5432:5432"
  app:
    build: .
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/minipaydb
      # Add other env vars
    ports:
      - "8082:8082"
```

### Env Variables

Required for DB, JWT, etc. Load via `.env` or export.

| Variable                        | Description                   | Default/Example                               |
|---------------------------------|-------------------------------|-----------------------------------------------|
| `SPRING_DATASOURCE_URL`         | JDBC URL for PostgreSQL       | `jdbc:postgresql://localhost:5432/minipay_db` |
| `SPRING_DATASOURCE_USERNAME`    | DB user                       | `minipay_user`                                |
| `SPRING_DATASOURCE_PASSWORD`    | DB password                   | `securepass`                                  |
| `JWT_SECRET`                    | Secret for signing JWT tokens | `supersecretkeyatleast32charslong`            |
| `JWT_EXPIRATION_MS`             | Access token expiry (ms)      | `3600000` (1 hour)                            |
| `REFRESH_TOKEN_EXPIRATION_MS`   | Refresh token expiry (ms)     | `86400000` (1 day)                            |
| `SERVER_PORT`                   | App port                      | `8082`                                        |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Hibernate mode                | `update` (dev); `validate` (prod)             |

Sample `.env`:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/minipay_db
SPRING_DATASOURCE_USERNAME=minipay_user
SPRING_DATASOURCE_PASSWORD=securepass
JWT_SECRET=supersecretkeyatleast32charslong
JWT_EXPIRATION_MS=3600000
REFRESH_TOKEN_EXPIRATION_MS=86400000
SERVER_PORT=8082
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

## 4. How to Run Migrations

Uses Flyway for DB migrations (schemas in `src/main/resources/db/migration`).

1. Ensure DB is running and env vars set.
2. Run: `mvn flyway:migrate`
3. To clean/reset (dev only): `mvn flyway:clean && mvn flyway:migrate`

Migrations include initial schema for users, merchants, etc., and indexes (see Performance section).

## 5. How to Start the App & Open Swagger

### Start App

- Local: `mvn spring-boot:run`
- Docker: `docker-compose up`
- App starts on `http://localhost:8082`. Logs show "Started Application in X seconds".

### Open Swagger

- Navigate to `http://localhost:8082/swagger-ui.html`
- Auth: Use `/api/auth/login` to get token, then authorize in Swagger (Bearer token).

## 6. Example Curl Requests

Assume `accessToken` from login. All use `Content-Type: application/json`.

- **Register User** (admin-only):
  ```
  curl --location --request POST 'https://localhost:8082/api/admin/auth/register \
   --header 'Content-Type: application/json' \
   --data '{"username": "testuser", "email": "testuser.com","role": "ADMIN","merchantId": "MER123"}'
  ```

- **Login**:
  ```
  curl --location 'https://localhost:8082/api/auth/login' \
  --header 'Content-Type: application/json' \
  --data '{"username": "testuser","password": "testpass"}'
  ```
  Response: `{ "accessToken": "...", "refreshToken": "..." }`

- **Create Merchant**:
  ```
  curl --location 'https://localhost:8082/api/merchants' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: ••••••' \
  --data-raw '{
  "merchantName": "mer123","merchantEmail": "merchanttest@gmail.com","settlementAccount": "20982748393",
  "settlementBank": "UBA",
  "callbackUrl": "https://webhook.api.com",
  "chargeSettingRequest": {
  "percentageFee": 0,
  "fixedFee": 0,
  "useFixedMSC": true,
  "mscCap": 0,
  "vatRate": 0,
  "platformProviderRate": 0,
  "platformProviderCap": 0}}'
  ```
  
- **Initiate Payment**:
  ```
  curl --location 'https://localhost:8082/api/payments' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxODg2LCJleHAiOjE3NDc3Njk0MzYsImlhdCI6MTc0Nzc2NzMzNn0.fqCzX5ozq3H-Il8Ac8oNmhcST2Zw_dEPMQWxd_8Smnc' \
  --data '{
  "orderId": "298483029383",
  "amount": 1000,
  "currency": "NGN",
  "customerId": "20304848",
  "channel": "CARD",
  "callbackUrl": "https://webhook.api.com"
  }'
  ```

- **Simulate Processor Callback**:
  ```
  curl --location 'https://localhost:8082/api/simulate/processor-callback' \
  --header 'Content-Type: application/json' \
  --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxODg2LCJleHAiOjE3NDc3Njk0MzYsImlhdCI6MTc0Nzc2NzMzNn0.fqCzX5ozq3H-Il8Ac8oNmhcST2Zw_dEPMQWxd_8Smnc' \
  --data '{
  "paymentReference": "ISBD8384883Y3VED",
  "status": true}'
  ```

- **Generate Settlements**:
  ```
  curl -X POST http://localhost:8082/api/settlements/generate?from=2025-09-01&to=2025-09-30 \
  -H "Authorization: Bearer $accessToken"
  ```

- **Export Transaction Report** (CSV):
  ```
  curl -X GET "http://localhost:8082/api/reports/transactions?format=CSV" \
  -H "Authorization: Bearer $accessToken" \
  -o transactions.csv
  ```

## 7. Webhook Verification Guide + Sample Code

Webhooks notify merchants on payment updates. Sent as POST to `callbackUrl` with JSON body (see context). Secured by
HMAC-SHA256 signature in `X-Signature` header.

### Verification Guide

1. Receive webhook POST.
2. Canonicalize body: Sort keys alphabetically, no whitespace (use JSON.stringify with sorted keys).
3. Compute HMAC: SHA256(canonicalJson + webhookSecret), Base64 encode.
4. Compare with `X-Signature`.
5. If match, process; else, reject (HTTP 401).

### Sample Code (Java)

```java

@RestController
public class WebhookController {

    private static final String WEBHOOK_SECRET = "your_webhook_secret"; // from merchant creation
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/webhook")
    public String receiveWebhook(@RequestBody ObjectNode body,
                                 @RequestHeader("X-Signature") String receivedSig) {
        try {
            TreeMap<String, Object> sorted = new TreeMap<>();
            body.fieldNames().forEachRemaining(field -> {
                sorted.put(field, body.get(field));
            });

            String canonicalJson = objectMapper
                    .writer()
                    .without(SerializationFeature.INDENT_OUTPUT)
                    .writeValueAsString(sorted);


            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(WEBHOOK_SECRET.getBytes(), "HmacSHA256");
            hmac.init(keySpec);

            byte[] hash = hmac.doFinal(canonicalJson.getBytes());
            String computedSig = Base64.getEncoder().encodeToString(hash);

            if (!computedSig.equals(receivedSig)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid signature");
            }

            System.out.println("Webhook verified: " + canonicalJson);
            return "OK";

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error verifying webhook", e);
        }
    }
}
```

## 8. Decision Log (Tradeoffs & Assumptions)

- **Authentication: JWT vs Sessions**: Chose JWT for stateless scalability; tradeoffs: Larger headers, revocation
  harder (use short expiry + refresh).
- **Domain-Driven Design**: Monolith for simplicity/speed; assume <10k reqs/day. Easy to split later.
- **DB: PostgreSQL**: JSONB support for flexible fees; assumptions: High read/write, ACID needed for finances.
- **Fee Calc**: Done at payment approval; assumes processor fees known/simulated. Caps applied post-percentage.
- **Settlement: Daily Batches**: Generated manually via API and cronjob in case of prod. Groups by merchant/date.
- **Simulation**: No real processors; assume success/fail via callback for testing.
- **Error Handling**: Global @ControllerAdvice; assumptions: Idempotent payments (retry on fail).
- **Currency**: Assumes NGN; extend for multi-currency.
- **VAT**: Applied on fees; assume 7.5% default, configurable.
- **Tradeoff: Export Formats**: CSV simple (StringBuilder); XLSX uses Apache POI (heavier but feature-rich).

## 9. Test Strategy & How to Run Tests

### Strategy

- **Unit Tests**: Services/-utils (e.g., fee calc); use JUnit5 + Mockito. Cover 80%+.
- **Integration Tests**: Controllers + DB (Testcontainers for Postgres); use @SpringBootTest.
- **Security**: Test JWT validation, roles (e.g., admin-only endpoints).
- **Edge Cases**: Invalid fees, caps, failed payments, empty reports.
- **Tools**: Jacoco for coverage; assume >70% threshold.

### How to Run

- All: `mvn test`
- With coverage: `mvn jacoco:report` (view in `target/site/jacoco/index.html`)
- Specific: `mvn test -Dtest=PaymentServiceTest`

## 10. Performance Notes & Indexes Added

- **Bottlenecks**: Payment lists/settlements (high volume); reports (large exports).
- **Optimizations**: Paging (Pageable in repos); caching (none yet, add @Cacheable for merchants).
- **Indexes Added** (via migrations/annotations):
    - Payments: `CREATE INDEX idx_payment_timestamp ON payments(timestamp);` (for date filters)
    - Payments: `CREATE INDEX idx_payment_merchant_status ON payments(merchant_id, status);` (common queries)
    - Settlements: `CREATE INDEX idx_settlement_date ON settlements(settlement_date);`
    - Merchants: `CREATE INDEX idx_merchant_name ON merchants(name);` (searches)
- **Notes**: Assume 10k payments/day; use EXPLAIN ANALYZE for queries. Exports stream to avoid OOM (ByteArrayResource).
  Monitor with Actuator/Prometheus in prod.
