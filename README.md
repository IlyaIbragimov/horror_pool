# Horror Pool Backend

Spring Boot backend for a horror movie catalog app, featuring user authentication, JWT via HTTP-only cookies, roles, comments, watchlists, and filtering.

---

## 📦 Stack
- Java 21
- Spring Boot 3
- Spring Security + JWT
- PostgreSQL
- Maven
- JPA/Hibernate
- ModelMapper
- Docker


  ![CI](https://github.com/IlyaIbragimov/horror_pool/actions/workflows/ci.yml/badge.svg)

---

## ⚙️ Getting Started

### Option 1: **Run with Docker (Recommended)**

You don’t need to install PostgreSQL locally — the database runs in a container.

Prereqs:
* Docker Desktop (or Docker Engine) with Compose v2
* JDK 21 + Maven (only to build the JAR before composing)


1) Clone the repository
```bash
git clone https://github.com/your-username/horror_pool.git
cd horror_pool
```

2) Create your .env, copy the example and edit secrets:
```bash
cp .env.example .env
```
Edit .env and set:

* POSTGRES_PASSWORD — DB password (local only)
* SPRING_APP_JWT_SECRET — random strong secret (Base64 recommended)

Generate a Base64 secret:

* macOS/Linux: openssl rand -base64 32
* PowerShell: [Convert]::ToBase64String((1..32 | % {Get-Random -Max 256}))

3) Build the app JAR
```bash
# run unit tests:
mvn -B -Dtest='*Test,!HorrorPoolApplicationTests' clean package

# (or skip tests if you prefer)
mvn -B clean package -DskipTests
```

4) Start containers (app + postgres)
```bash
docker compose up --build -d
```
5) Verify

* Health: http://localhost:8080/actuator/health
* Swagger UI: http://localhost:8080/swagger-ui/index.html

6) Stop / clean
```bash
docker compose down -v
```
Default connection (inside Docker):
* Host: localhost
* Port: 5432 (change in docker-compose.yml if your local 5432 is busy)
* DB: ${POSTGRES_DB} (default horror_pool)
* User: ${POSTGRES_USER} (default postgres)
* Password: ${POSTGRES_PASSWORD} (from your .env)

Note: The Dockerized DB is separate from any local PostgreSQL you might have.

### Option 2: **Run Locally (Manual Setup without Docker)**

Prereqs
* JDK 21, Maven, PostgreSQL running locally

1. Create a PostgreSQL database named horror_pool.
2. Either set environment variables, or edit src/main/resources/application.properties:
```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/horror_pool
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.app.jwtSecret=YOUR_STRONG_SECRET   #  Base64
```
3) Run
```bash
mvn clean install
mvn spring-boot:run
```
Open Swagger: http://localhost:8080/swagger-ui/index.html

### Tips & Troubleshooting

* Port 5432 already in use? Change the mapping in docker-compose.yml:
```bash
postgres:
  ports:
    - "5433:5432"
```
Then connect to localhost:5433.

* Rebuilding after code changes
```bash
mvn -B clean package -DskipTests
docker compose up --build -d
```
* Health endpoints:
Liveness/Readiness: GET /actuator/health (and /actuator/health/readiness)

### What Docker Compose uses

docker-compose.yml reads values from your .env (not committed):

* Non-secret defaults (DB name/user) are visible in the file.
* Secrets (POSTGRES_PASSWORD, SPRING_APP_JWT_SECRET) are required from .env.

.env.example is provided as a template. Create your own .env before running.

---
## 📘 API Documentation (Swagger)

The application integrates [Swagger UI](https://swagger.io/tools/swagger-ui/) via Springdoc OpenAPI for easy API testing and exploration.

🔗 Access the interactive docs at: http://localhost:8080/swagger-ui/index.html

💡 Most endpoints require authentication via JWT stored in HTTP-only cookies. You may need to sign in first to access protected routes. (username: admin, password: adminPassword), use the http://localhost:8080/horrorpool/public/signin endpoint for this.

---

## 🔐 Authentication
- JWT (HTTP-only cookie)
- Login endpoint: `/api/auth/login`
- Cookie name: `horrorPoolCookieJwt`

---

## 🧾 API Examples

### 🔑 Login
```
POST /api/auth/login
{
  "username": "user1",
  "password": "1234"
}
```

### 🎥 Get All Movies
```
GET /api/movies
```

### 💬 Add Comment
```
POST /api/movie/{movieId}/addComment
{
  "commentContent": "Loved it!"
}
```

### 📌 Toggle Watchlist Item
```
PUT /api/watchlist/{watchlistItemId}/toggleWatched
```

## 🛡️ Roles & Permissions

| Role   | Access                                      |
|--------|---------------------------------------------|
| USER   | Browse movies, comment, manage watchlist    |
| ADMIN  | Manage movies, genres, and user accounts    |

---

## ✅ Features
- Register/Login with JWT cookies
- Movie catalog with genres
- User watchlists with "watched" toggle
- Comments per movie
- Admin panel: movie/genre/user management
- Filtering & searching movies

---

## ⚠️ Known Limitations / TODO
- Tests in progress (for now only unit tests for MovieServiceImpl, GenreServiceImpls and WatchlistServiceImpl is covered)
---

## 🧪 Testing
Run unit tests (you need PostgreSQL running with valid credentials in application.properties):
```
mvn test
```

---

## 📁 Folder Structure
- `configuration/` — App constants, config classes, role definitions, and data initialization
- `controller/` — REST endpoints
- `dto/` — Data transfer objects for requests and responses
- `enums/` — Enum definitions
- `exception/` — Custom exceptions and global handler
- `model/` — JPA entities
- `payload/` — Response/request payload wrappers
- `repository/` — Spring Data JPA repositories
- `security/` — JWT logic and Spring Security config
- `security/jwt/` — JWT token and user details
- `service/` — Interfaces for business logic
- `service/impl/` — Service implementations

---

## Contacts
- linkedIn : https://www.linkedin.com/in/ilya-ibragimov-a78628224/
- email: ilya.ibragimov@seznam.cz
- mobile number: +420777976293
- GitHub: https://github.com/IlyaIbragimov
