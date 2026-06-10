# Horror Pool

Spring Boot backend and React frontend for a horror movie catalog app. The app supports movie browsing, TMDB imports, JWT cookie authentication, roles, comments, user watchlists, public watchlists, filtering, and admin management.

![CI](https://github.com/IlyaIbragimov/horror_pool/actions/workflows/ci.yml/badge.svg)

---

## Stack

- Java 21
- Spring Boot 3
- Spring Security + JWT
- PostgreSQL
- Flyway
- Maven
- JPA/Hibernate
- ModelMapper
- React + Vite
- Docker Compose
- resilience4j

---

## Data Source & Attribution

<p>
  <a href="https://www.themoviedb.org/">
    <img src="docs/mdblogo.svg" alt="TMDB Logo" width="140">
  </a>
</p>

This product uses the TMDB API but is not endorsed or certified by TMDB.

---

## Getting Started

### Option 1: Run with Docker

Prerequisites:

- Docker Desktop or Docker Engine with Compose v2
- JDK 21 and Maven if you want to build locally before composing

1. Clone the repository:

```bash
git clone https://github.com/your-username/horror_pool.git
cd horror_pool
```

2. Create `.env` from the example:

```bash
cp .env.example .env
```

3. Edit `.env` and set the required values:

```env
POSTGRES_DB=horror_pool
POSTGRES_USER=postgres
POSTGRES_PASSWORD=change_this_local_password
SPRING_APP_JWT_SECRET=<base64-encoded-32-byte-secret>
SPRING_APP_COOKIE_SECURE=false
TMDB_READ_TOKEN=<your-tmdb-read-token>
```

Generate a JWT secret:

```bash
# macOS/Linux/Git Bash
openssl rand -base64 32
```

```powershell
# PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

Use `SPRING_APP_COOKIE_SECURE=false` for local plain HTTP. Use `true` for HTTPS deployment.

4. Build and test:

```bash
./mvnw test
./mvnw clean package
```

On Windows:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

5. Start containers:

```bash
docker compose up --build -d
```

6. Verify:

- Health: http://localhost:8080/actuator/health
- Swagger UI: http://localhost:8080/swagger-ui/index.html

7. Stop and clean:

```bash
docker compose down -v
```

The Dockerized database is separate from any local PostgreSQL instance. If local port `5432` is busy, change the mapping in `docker-compose.yml`, for example:

```yaml
ports:
  - "5433:5432"
```

### Option 2: Run Locally

Prerequisites:

- JDK 21
- Maven or the Maven wrapper
- PostgreSQL running locally
- Node.js 22+ for frontend development

1. Create a PostgreSQL database named `horror_pool`.

2. Set environment variables, or provide equivalent values in your run configuration:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/horror_pool
SPRING_DATASOURCE_USERNAME=<your-db-user>
SPRING_DATASOURCE_PASSWORD=<your-db-password>
SPRING_APP_JWT_SECRET=<base64-encoded-32-byte-secret>
SPRING_APP_COOKIE_SECURE=false
TMDB_READ_TOKEN=<your-tmdb-read-token>
```

3. Run the backend with the dev profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

4. Run the frontend:

```bash
cd frontend
npm ci
npm run dev
```

The Vite dev server proxies `/horrorpool` requests to `http://localhost:8080`.

---

## Configuration

Important backend configuration:

- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`: database connection.
- `SPRING_APP_JWT_SECRET`: required Base64-encoded secret that decodes to at least 32 bytes.
- `SPRING_APP_COOKIE_SECURE`: `true` for HTTPS deployments, `false` for local HTTP.
- `TMDB_READ_TOKEN`: TMDB API read token.
- `APP_BOOTSTRAP_ADMIN_ENABLED`: optional admin bootstrap toggle, default `false`.
- `ADMIN_USERNAME`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`: required only when admin bootstrap is enabled.

There is no default seeded admin account. Create users via signup, then grant/administer roles directly in the database or enable bootstrap with strong credentials for a controlled environment.

---

## Security Notes

- Authentication uses a JWT stored in an HTTP-only cookie named `horrorPoolCookieJwt`.
- CSRF protection is enabled for cookie-authenticated state-changing requests.
- The frontend obtains a CSRF cookie from `GET /horrorpool/public/csrf` and sends `X-XSRF-TOKEN` on unsafe requests.
- `POST /horrorpool/public/signin` and `POST /horrorpool/public/signup` are excluded from CSRF because they must be usable before authentication.
- JWT secret validation is explicit at startup: missing, non-Base64, or too-short secrets fail fast with a clear error.

---

## API Documentation

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

Base backend path:

```text
/horrorpool
```

---

## API Examples

### Sign Up

```http
POST /horrorpool/public/signup
Content-Type: application/json

{
  "username": "user1",
  "email": "user1@example.com",
  "password": "strongPassword123",
  "confirmPassword": "strongPassword123"
}
```

### Sign In

```http
POST /horrorpool/public/signin
Content-Type: application/json

{
  "username": "user1",
  "password": "strongPassword123"
}
```

### Sign Out

```http
POST /horrorpool/public/signout
```

### Get Movies

```http
GET /horrorpool/public/movie/all?page=0&size=18&sort=title&order=asc
```

### Search Movies

```http
GET /horrorpool/public/movie/search?keyword=alien&year=1979
```

### Add Comment

```http
POST /horrorpool/movie/{movieId}/addComment
Content-Type: application/json

{
  "commentContent": "Loved it!"
}
```

### Create Watchlist

```http
POST /horrorpool/user/watchlist/create
Content-Type: application/json

{
  "title": "Weekend horror",
  "public": true
}
```

### Add Movie to Watchlist

```http
POST /horrorpool/user/watchlist/{watchlistId}/add/{movieId}
```

### Toggle Watchlist Item

```http
PUT /horrorpool/user/watchlist/{watchlistId}/toggle/{watchlistItemId}
```

---

## Roles & Permissions

| Role | Access |
| --- | --- |
| USER | Browse movies, comment, manage own watchlists, follow/rate public watchlists |
| ADMIN | Manage movies, genres, users, and TMDB imports |

---

## TMDB Integration

TMDB import endpoints are admin-only.

### Import Movie by TMDB ID

```http
POST /horrorpool/admin/tmdb/import/{tmdbId}
```

Behavior:

- Fails if the movie already exists.
- Fails if the TMDB movie is not found.
- Imports core movie fields and trailer URL.
- Genres are not attached yet.

### Bulk Import Horror Movies

```http
POST /horrorpool/admin/tmdb/bulkImport
Content-Type: application/json

{
  "pages": 1,
  "minVoteAverage": 6.0,
  "sortBy": "popularity.desc"
}
```

Behavior:

- Uses TMDB Discover with horror genre filtering.
- Existing movies are skipped.
- Import continues when individual movies fail.
- Rate limiting and retry are enabled.

Example response:

```json
{
  "imported": 18,
  "skipped": 2,
  "failed": 0,
  "errors": []
}
```

---

## Features

- User registration and sign-in with JWT cookies
- CSRF-protected cookie authentication
- Movie catalog with search, filters, sorting, and pagination
- Genre browsing
- Movie comments and replies
- User watchlists with watched toggles
- Public watchlists, following, and rating
- Admin movie, genre, and user management
- TMDB movie import and bulk import
- Flyway database migrations
- CI for backend package/tests and frontend lint/build

---

## Known Limitations / TODO

- Deployment config still needs environment-driven CORS and frontend API base URL.
- Some frontend parsing and fetch/loading/pagination patterns are duplicated.
- Some backend page response construction and current-user lookup logic is duplicated.

---

## Testing

Backend tests use the `test` profile and an H2 in-memory database, so local PostgreSQL is not required.

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

Frontend checks:

```bash
cd frontend
npm ci
npm run lint
npm run build
```

---

## CI

GitHub Actions currently:

- builds and tests the backend with Maven
- installs frontend dependencies
- runs frontend lint
- builds the frontend
- uploads the backend JAR artifact
- runs a Docker Compose smoke check against `/actuator/health`

---

## Folder Structure

- `configuration/`: app constants, config classes, role definitions, data initialization
- `controller/`: REST endpoints
- `dto/`: data transfer objects
- `enums/`: enum definitions
- `exception/`: custom exceptions and global handlers
- `model/`: JPA entities
- `payload/`: request/response payload wrappers
- `repository/`: Spring Data JPA repositories
- `security/`: Spring Security config and user details
- `security/jwt/`: JWT token provider and filter
- `service/`: service interfaces
- `service/impl/`: service implementations
- `tmdb/`: TMDB client
- `frontend/`: React/Vite frontend

---

## Contacts

- LinkedIn: https://www.linkedin.com/in/ilya-ibragimov-a78628224/
- Email: ilya.ibragimov@seznam.cz
- Mobile: +420777976293
- GitHub: https://github.com/IlyaIbragimov
