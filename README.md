# Employee Salary Management System
 
A microservices-based salary management system built with Spring Boot. Employees are registered, assigned tasks, and their salary is automatically calculated based on task completions and severity. Salary payoffs are processed on a scheduled basis.
 
---
 
## Architecture Overview
 
```
                        ┌──────────────────┐
                        │ Service Registry │
                        │   (Eureka :8761) │
                        └────────┬─────────┘
                                 │ service discovery
          ┌──────────────────────┼───────────────────────┐
          │                      │                       │
  ┌───────▼──────┐      ┌────────▼─────┐      ┌────────▼──────┐
  │ employee-ms  │      │   task-ms    │      │   salary-ms   │
  │   :8085      │      │   :8086      │      │   :8087       │
  └───────┬──────┘      └──────┬───────┘      └────────┬──────┘
          │                    │                       │
          │  employee.created  │   task.completed      │
          └──────────┐         └──────────┐            │
                     ▼                    ▼            │
              ┌─────────────────────────────┐          │
              │           Kafka             │          │
              │   (KRaft mode :9092/29092)  │──────────┘
              └─────────────────────────────┘
                         │
              ┌──────────▼──────────┐
              │     Kafka UI        │
              │       :8080         │
              └─────────────────────┘
```
 
### Kafka Event Flow
 
| Event | Producer | Consumer | Trigger |
|---|---|---|---|
| `employee.created` | `employee-ms` | `salary-ms` | Admin registers a new employee |
| `task.completed` | `task-ms` | `salary-ms` | Employee finishes a task |
 
When `salary-ms` consumes `employee.created`, it creates a `SalaryCalculator` record for that employee. When it consumes `task.completed`, it adds the calculated amount (`salaryPerTask × severity`) to the employee's running total. A scheduler runs every 30 days to finalize and record the payoff.
 
---
 
## Services
 
### employee-ms (port 8085)
 
Manages employee registration, authentication, and profile data.
 
**Key features:**
- JWT-based authentication (access + refresh tokens)
- Role-based access control (`ADMIN`, `EMPLOYEE`)
- Publishes `employee.created` event via Outbox Pattern on registration
- `salaryPerTask` is auto-calculated as `officialSalary / 20`
**Endpoints:**
 
| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/api/admin/employee` | ADMIN | Register a new employee |
| `PUT` | `/api/auth` | Public | Login |
| `PUT` | `/api/auth/logout` | Authenticated | Logout |
| `PUT` | `/api/auth/refresh-token` | Authenticated | Refresh access token |
| `GET` | `/api/employee/{finCode}` | Authenticated | Get employee by FIN code |
 
---
 
### task-ms (port 8086)
 
Manages task lifecycle for employees. Tasks have a severity multiplier that affects salary calculation.
 
**Key features:**
- Employees can have a maximum of 4 active tasks at once
- Task lifecycle: `TODO → IN_PROGRESS → DONE`
- On task completion, calculates `salaryPerTask × severity` and publishes `task.completed` event via Outbox Pattern
- Calls `employee-ms` via Feign client to get `salaryPerTask`
- Completed tasks are auto-deleted every 30 days via scheduler
**Endpoints:**
 
| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/api/admin/task` | ADMIN | Create a task for an employee |
| `GET` | `/api/task/{id}` | Employee (owner) | Get task by ID |
| `GET` | `/api/task/employee/{employeeId}` | Employee (owner) | List tasks, optionally filtered by status |
| `PUT` | `/api/task/{id}/start` | Employee (owner) | Start a task |
| `PUT` | `/api/task/{id}/finish` | Employee (owner) | Finish a task |
| `DELETE` | `/api/task/{id}` | Employee (owner) | Delete a task |
 
---
 
### salary-ms (port 8087)
 
Consumes Kafka events to track and calculate employee salaries. Processes payoffs on schedule.
 
**Key features:**
- Consumes `employee.created` → creates a `CalculatedSalary` record
- Consumes `task.completed` → accumulates salary amount
- Scheduled payoff every 30 days: moves `CalculatedSalary` → `Salary` and resets accumulator to zero
**Endpoints:**
 
| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/api/admin/calculated-salary/{employeeId}` | ADMIN | Manually create salary calculator |
| `PUT` | `/api/calculated-salary/{employeeId}` | Authenticated | Manually update calculated salary |
 
---
 
### service-registry (port 8761)
 
Eureka service registry. All microservices register here for service discovery.
 
---
 
## Outbox Pattern
 
`employee-ms` and `task-ms` both implement the Transactional Outbox Pattern to guarantee reliable event delivery to Kafka.
 
```
Service                    DB                        Kafka
   │                        │                          │
   │── @Transactional ──────┤                          │
   │   save entity          │                          │
   │   save OutboxEvent ───►│ status=PENDING           │
   │── commit ──────────────┤                          │
   │                        │                          │
   │        OutboxScheduler (every 5s)                 │
   │                        │──── read PENDING ───────►│
   │                        │     send to Kafka        │
   │                        │──── mark PUBLISHED       │
```
 
This ensures that if Kafka is temporarily unavailable, events are never lost — they stay `PENDING` and are retried on the next scheduler tick.
 
---
 
## Tech Stack
 
| Technology | Usage |
|---|---|
| Java 21 | Language |
| Spring Boot 4.0.6 | Framework |
| Spring Security + JWT | Authentication |
| Spring Kafka | Messaging |
| Apache Kafka 4.0.0 (KRaft) | Message broker |
| Spring Cloud Netflix Eureka | Service discovery |
| Spring Cloud OpenFeign | Inter-service HTTP calls |
| PostgreSQL | Database (one per service) |
| Liquibase | Database migrations |
| Lombok | Boilerplate reduction |
| Docker / Docker Compose | Infrastructure |
 
---
 
## Getting Started
 
### Prerequisites
 
- Docker & Docker Compose
- Java 21
- Gradle
### 1. Start Kafka and Kafka UI
 
From the root directory:
 
```bash
docker-compose up -d
```
 
This starts:
- Kafka on `localhost:9092` (KRaft mode, no Zookeeper)
- Kafka UI on `http://localhost:8080`
### 2. Start each service's database
 
Each microservice has its own `docker-compose.yml`:
 
```bash
# In each service directory:
cd employee-ms && docker-compose up -d
cd task-ms     && docker-compose up -d
cd salary-ms   && docker-compose up -d
```
 
| Service | DB Port | User | Password |
|---|---|---|---|
| employee-ms | 5434 | employee_user | employee_password |
| task-ms | 5435 | task_user | task_password |
| salary-ms | 5436 | salary_user | salary_password |
 
> Each service has a `.env` file for database credentials.
 
### 3. Start services (in order)
 
```bash
# 1. Service Registry first
cd service-registry && ./gradlew bootRun
 
# 2. Then any order
cd employee-ms && ./gradlew bootRun -Pprofile=local
cd task-ms     && ./gradlew bootRun -Pprofile=local
cd salary-ms   && ./gradlew bootRun -Pprofile=local
```
 
### 4. Verify
 
- Eureka dashboard: `http://localhost:8761`
- Kafka UI: `http://localhost:8080`
---
 
## Typical Usage Flow
 
```
1. Admin registers employee      POST /api/admin/employee
        │
        └─► employee.created event ──► salary-ms creates SalaryCalculator
 
2. Admin creates task            POST /api/admin/task
        │
        └─► Task assigned to employee with severity multiplier
 
3. Employee starts task          PUT /api/task/{id}/start
 
4. Employee finishes task        PUT /api/task/{id}/finish
        │
        └─► task.completed event ──► salary-ms adds (salaryPerTask × severity)
                                      to employee's running total
 
5. Every 30 days (scheduled)
        └─► salary-ms moves CalculatedSalary → Salary record, resets to 0
```
 
---
 
## JWT Configuration
 
All services share the same JWT secret and expiration settings:
 
```yaml
jwt:
  secret-key: <your-secret>
  access-expiration: 3600000    # 1 hour
  refresh-expiration: 86400000  # 24 hours
```
 
> Update the secret key before deploying to production.
