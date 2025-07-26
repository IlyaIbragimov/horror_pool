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

---

## ⚙️ Getting Started

### Option 1: **Run with Docker (Recommended)**

No need to install PostgreSQL or set up a local DB!  
Just build and run everything in containers:

```bash
# Clone the repository
git clone https://github.com/your-username/horror_pool.git
cd horror_pool

# Build the JAR (skip tests to avoid DB errors if you don't have PostgreSQL locally)
mvn clean package -DskipTests

# Build and run Docker containers (app + database)
docker-compose up --build
```
The backend will be available at: http://localhost:8080/swagger-ui/index.html

Default DB Credentials (for Dockerized PostgreSQL)

Username: postgres

Password: postgres

Database: horror_pool

All these are set in the docker-compose.yml and passed as environment variables.

### Option 2: **Run Locally (Manual Setup)**

If you prefer running the backend outside Docker, you need:

Java 21+

Maven

PostgreSQL installed and running

1. Create DB and configure credentials
2. Create a PostgreSQL database named horror_pool.
3.  Update your src/main/resources/application.properties:
```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/horror_pool
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.app.jwtSecret=YOUR_JWT_SECRET
```
4. Build and Run
```bash
mvn clean install
mvn spring-boot:run
```
The backend will be available at http://localhost:8080/swagger-ui/index.html

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
- unit tests in progress (for now only MovieServiceImpl is covered)
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
