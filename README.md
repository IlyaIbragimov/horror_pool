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

---

## ⚙️ Getting Started

### 1. Clone & Build
```bash
git clone https://github.com/your-username/horror_pool.git
cd horror_pool
mvn clean install
```

### 2. Setup DB (PostgreSQL)
- Create DB named `horror_pool`
- Update credentials in `application.properties`

```
spring.datasource.url=jdbc:postgresql://localhost:5432/horror_pool
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 3. Run
```bash
mvn spring-boot:run
```

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

---

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
- No unit tests yet
- No Swagger/OpenAPI docs
- No Docker support

---

## 🧪 Testing
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
GitHub: https://github.com/IlyaIbragimov
