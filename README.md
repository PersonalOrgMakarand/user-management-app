# User Management Application

A production-style Spring Boot 3 demo that exposes CRUD REST APIs for managing
users, backed by an in-memory H2 database and documented with Swagger UI.

## Tech Stack

- Java 17
- Spring Boot 3.2.5 (Web, Data JPA, Validation)
- H2 in-memory database
- springdoc-openapi 2.5.0 (Swagger UI)
- Maven (embedded Tomcat)
- Docker (multi-stage build)

## Prerequisites

- JDK 17
- Maven 3.9+
- Docker (optional, for containerised run)

## Project Structure

```
src/main/java/com/example/usermanagement/
├── UserManagementApplication.java   # main entry
├── config/        # OpenAPI / Swagger config
├── controller/    # REST controllers + global exception handler
├── exception/     # domain exceptions
├── model/         # JPA entity (User)
├── repository/    # Spring Data JPA repository
└── service/       # business logic
src/main/resources/
├── application.properties           # configuration
└── data.sql                         # seed users
```

## Run locally

### Option 1 — Maven (development)

```powershell
mvn spring-boot:run
```

### Option 2 — Build and run the JAR

```powershell
mvn clean package
java -jar target/app.jar
```

The app starts on `http://localhost:8080`. Override the port via:

```powershell
java -jar target/app.jar --server.port=9090
# or
$env:SERVER_PORT=9090; java -jar target/app.jar
```

## Run with Docker

```powershell
docker build -t user-management-app .
docker run --rm -p 8080:8080 user-management-app
```

## REST API

Base path: `http://localhost:8080/users`

| Method | Path           | Description       | Success |
| ------ | -------------- | ----------------- | ------- |
| POST   | `/users`       | Create a user     | 201     |
| GET    | `/users`       | List all users    | 200     |
| GET    | `/users/{id}`  | Get user by id    | 200     |
| PUT    | `/users/{id}`  | Update a user     | 200     |
| DELETE | `/users/{id}`  | Delete a user     | 204     |

### Sample requests (curl)

```bash
# Create
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@x.com","password":"secret1","role":"ADMIN"}'

# List
curl http://localhost:8080/users

# Get by id
curl http://localhost:8080/users/1

# Update
curl -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice B","email":"alice@x.com","password":"secret1","role":"USER"}'

# Delete
curl -X DELETE http://localhost:8080/users/1
```

### Error responses

| Status | When                                    |
| ------ | --------------------------------------- |
| 400    | Validation failed / illegal argument    |
| 404    | User id does not exist                  |
| 409    | Email already registered                |
| 500    | Unhandled server error                  |

All errors are returned as a structured JSON body containing `timestamp`,
`status`, `error`, `message`, `path`, and optional `details` (field errors).

## Swagger / OpenAPI

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/api-docs>

## H2 Console

- URL: <http://localhost:8080/h2-console>
- JDBC URL: `jdbc:h2:mem:userdb`
- Username: `sa`
- Password: *(blank)*

## Notes

- Passwords are stored as plain text in this demo and are hidden from API
  responses via `@JsonProperty(WRITE_ONLY)`. **Do not use this code as-is in
  production** — add password hashing (e.g. BCrypt) and authentication
  (Spring Security) before deploying anywhere real.
- H2 is in-memory: all data is reset on restart. Seed records live in
  `src/main/resources/data.sql`.
