# Finance Backend — Spring Boot + PostgreSQL

A role-based finance dashboard backend with JWT authentication, financial record management, and dashboard summary APIs.

---

## Tech Stack
- **Java 17+** + **Spring Boot**
- **Spring Security** + **JWT** (jjwt 0.12.6)
- **Spring Data JPA** + **PostgreSQL**
- **Lombok**, **Bean Validation**
- **Swagger UI** (springdoc-openapi)

---

## Setup Instructions

### 1. Create the database
```sql
CREATE DATABASE finance_db;
```

### 2. Configure credentials
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_db
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

app.jwt.secret=thisIsAVeryLongSecretKeyThatIsAtLeast256BitsLongForHmacSha256Algorithm
app.jwt.expiration=86400000

server.port=1200

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs
```

### 3. Run the app
```bash
mvn spring-boot:run
```
Tables are auto-created by Hibernate on first run.

### 4. Insert first Admin user
Run this SQL in pgAdmin or psql:
```sql
INSERT INTO users (name, email, password, role, active)
VALUES (
  'Admin User',
  'admin@finance.com',
  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
  'ADMIN',
  true
);
```
Password is `password`.

---

## Test Users

### Admin
| Field    | Value                    |
|----------|--------------------------|
| Email    | admin@finance.com        |
| Password | admin123                 |
| Role     | ADMIN                    |

### Viewer 1
| Field    | Value                    |
|----------|--------------------------|
| Email    | viewer@finance.com       |
| Password | viewer123                |
| Role     | VIEWER                   |

### Viewer 2
| Field    | Value                    |
|----------|--------------------------|
| Email    | viewer2@finance.com      |
| Password | viewer1234               |
| Role     | VIEWER                   |

### Analyst
| Field    | Value                    |
|----------|--------------------------|
| Email    | analyst@finance.com      |
| Password | analyst                  |
| Role     | ANALYST                  |

---

## Roles and Permissions

| Action                          | VIEWER | ANALYST | ADMIN |
|---------------------------------|--------|---------|-------|
| View dashboard summary          | ✅     | ✅      | ✅    |
| View financial records          | ❌     | ✅      | ✅    |
| Filter records                  | ❌     | ✅      | ✅    |
| Create records                  | ❌     | ❌      | ✅    |
| Update records                  | ❌     | ❌      | ✅    |
| Delete records                  | ❌     | ❌      | ✅    |
| Manage users                    | ❌     | ❌      | ✅    |

---

## API Documentation (Swagger)

After running the app, open in browser:
**http://localhost:1200/swagger-ui.html**

Add the mentioned tokens in swagger-ui.html authorize and get authorized and check the functions


Admin token:
**eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBmaW5hbmNlLmNvbSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc3NTE1MDc5MiwiZXhwIjoxNzc1MjM3MTkyfQ.2sjqir4glsAaXdxlH1eP3JwGoCYX4IQsZ_SNSP5YjOKA3aBlNlFmrMFZMOfisl9b-BMdyBlwgavqia8OHrh3Mg**


Viewer1 token:
**eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2aWV3ZXJAZmluYW5jZS5jb20iLCJyb2xlIjoiVklFV0VSIiwiaWF0IjoxNzc1MTI4NzkxLCJleHAiOjE3NzUyMTUxOTF9.MaJa9wUFcyo6NoOj23NGp2oogObDa-F8HYGP_isDIPemj38DQ_5oj6IeGq_42kxFSEcTldEfV3S4bhiMv5EiCQ**


Viewer2 token:
**eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2aWV3ZXIyQGZpbmFuY2UuY29tIiwicm9sZSI6IlZJRVdFUiIsImlhdCI6MTc3NTE0OTkzNSwiZXhwIjoxNzc1MjM2MzM1fQ.efGzW_opd5TvW_qRzFqKhwCnj3-pjufVVUhE46v0JYuDZsxOWoZoDEYayg6kvQVuMxmoXhqJUIbNV66YouxOUg**


Analyst token:
**eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbmFseXN0QGZpbmFuY2UuY29tIiwicm9sZSI6IkFOQUxZU1QiLCJpYXQiOjE3NzUxNTEwODQsImV4cCI6MTc3NTIzNzQ4NH0.t-FO6CHQ2Q0lGhoU2h9QcY_2LU1u9Y_E72L5MNSAb1nGHhiMnQMry-1iTce8ie1p3N5i7HipmGtAMDY0pXv0dg**

To test protected endpoints in Swagger:
1. Call `POST /api/auth/login` to get JWT token
2. Click **Authorize** button at top right
3. Paste token and click Authorize
4. All protected endpoints now work

---

## API Reference

### Auth
| Method | Endpoint            | Access  | Description   |
|--------|---------------------|---------|---------------|
| POST   | /api/auth/login     | Public  | Get JWT token |

**Request:**
```json
{
  "email": "admin@finance.com",
  "password": "admin123"
}
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "role": "ADMIN",
  "name": "Admin User"
}
```

---

### Users `/api/users` — Admin only
| Method | Endpoint                 | Description          |
|--------|--------------------------|----------------------|
| POST   | /api/users               | Create user          |
| GET    | /api/users               | List all users       |
| GET    | /api/users/{id}          | Get user by ID       |
| PATCH  | /api/users/{id}/role     | Update role          |
| PATCH  | /api/users/{id}/status   | Activate/deactivate  |
| DELETE | /api/users/{id}          | Delete user          |

**Create user request:**
```json
{
  "name": "Analyst User",
  "email": "analyst@finance.com",
  "password": "analyst123",
  "role": "ANALYST"
}
```

---

### Financial Records `/api/records`
| Method | Endpoint          | Access          | Description         |
|--------|-------------------|-----------------|---------------------|
| POST   | /api/records      | Admin           | Create record       |
| GET    | /api/records      | Admin, Analyst  | List with filters   |
| GET    | /api/records/{id} | Admin, Analyst  | Get single record   |
| PUT    | /api/records/{id} | Admin           | Update record       |
| DELETE | /api/records/{id} | Admin           | Soft delete record  |

**Filter query params:**
GET /api/records?type=INCOME
GET /api/records?category=Rent
GET /api/records?type=EXPENSE&category=Rent
GET /api/records?from=2024-01-01&to=2024-12-31
**Create record request:**
```json
{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2024-03-01",
  "notes": "March salary"
}
```

---

### Dashboard `/api/dashboard`
| Method | Endpoint                  | Access                 |
|--------|---------------------------|------------------------|
| GET    | /api/dashboard/summary    | Viewer, Analyst, Admin |

**Response:**
```json
{
  "totalIncome": 6000.00,
  "totalExpense": 2000.00,
  "netBalance": 4000.00,
  "categoryTotals": {
    "Salary": 6000.00,
    "Rent": 2000.00
  },
  "monthlyTrends": [
    { "month": "2024-03", "type": "INCOME", "amount": 6000.00 },
    { "month": "2024-03", "type": "EXPENSE", "amount": 2000.00 }
  ],
  "recentActivity": [...]
}
```

---

## Error Responses

All errors return consistent JSON:
```json
{
  "timestamp": "2024-03-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Record not found: 42"
}
```

| Status | Meaning                       |
|--------|-------------------------------|
| 400    | Bad request / duplicate entry |
| 401    | Invalid or missing token      |
| 403    | Role not allowed              |
| 404    | Resource not found            |
| 422    | Validation failed             |
| 500    | Unexpected server error       |

---

## Assumptions Made
1. One role per user — no multi-role support needed
2. Soft delete for records — deleted flag set to true, data preserved
3. JWT tokens expire after 24 hours
4. Tables auto-created by Hibernate on startup
5. All monetary values use BigDecimal for precision
6. First admin user inserted directly via SQL

## Project Structure

```
src/main/java/com/finance/finance_backend/

├── Config/
│   ├── SecurityConfig.java
│   ├── SwaggerConfig.java
│
├── Controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── RecordController.java
│   ├── DashboardController.java
│
├── Dto/
│   ├── AuthResponse.java
│   ├── DashboardResponse.java
|   ├── LoginRequestDto.java
|   ├── MonthlyTrend.java
|   ├── RecordRequest.java
|   ├── RecordResponse.java
|   ├── UserRequest.java
|   ├── UserResponse.java
|   
├── Exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│
├── Model/
│   ├── User.java
│   ├── FinancialRecord.java
│   ├── Role.java
│   ├── RecordType.java
│
├── Repository/
│   ├── UserRepository.java
│   ├── FinancialRecordRepository.java
│
├── Security/
│   ├── JwtUtil.java
│   ├── JwtFilter.java
│   ├── CustomUserDetailsService.java
│
└── Service/
    ├── UserService.java
    ├── FinancialRecordService.java
    ├── DashboardService.java
```
