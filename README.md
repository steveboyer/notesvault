# Notes Vault
![CI](https://github.com/steveboyer/notesvault/actions/workflows/ci.yml/badge.svg)

## Highlights

- Stateless JWT authentication built on Spring Security
- Per-note authorization at the service layer
  - Owner can read or write
  - Users the note is shared with can read
- ProblemDetail responses for error cases
- Integration tests against real PostgreSQL via Testcontainers
- Full containerized via a single Docker Compose command
- CI pipeline via Github Actions to ensure formatting and build on every push

## Usage Instructions

1. Register a user (`POST` `/auth/register`)
2. Log in (`POST` `/auth/login`) to receive JWT
3. Send the token as a Bearer on all requests to `/notes`
4. Create, update, delete, and read your own notes and share them (read-only) with other users

## Security Considerations

- Stateless JWT auth
- Passwords hashed using BCrypt
  - Plaintext password is never logged or stored
- Authorization enforced in the service layer per note
- Returns a 404 instead of a 403 for notes the user cannot access to prevent leakage of a note's existence
- Returns a 404 instead of a 403 for unknown users to prevent leakage of a user's existence
- Malformed or expired tokens are caught and treated as unauthenticated to prevent leakage
- CSRF disabled

## Assumptions

- Usernames are unique and can be used as the login ID
- Shares are read-only
- Notes have 1 owner

## Tradeoffs

- Schema generated automatically via Hibernate ddl-auto instead of proper migration using Flyway or similar
- Sharing is by user id instead of username
- Minimal permissions model
- A request for a note the user cannot access returns `404 Not Found`, which avoids leaking the note's existence. The
  same `404` is also returned when a share recipient tries to edit a note shared with them, where a `403 Forbidden`
  could arguably be more correct.

## Future improvements

- API versioning
- Pagination on list endpoints
- Token refresh
- Per-share permission levels (read/write) and share by username
- Proper schema migration
- Password resets and more advanced user accounts

## Running

### Run in Docker

1. Create a `.env` file with a username, password, and JWT secret key (see `.env.example`)
2. Run the project in Docker using `docker-compose up --build`

### Building

`./gradlew build`

### Formatting

`./gradlew spotlessApply`

### Testing

`./gradlew test`

---

## Tech Stack

| Technology                  | Purpose                      |
| --------------------------- | ---------------------------- |
| Java 25                     | Application language         |
| Spring Boot 4               | Application framework        |
| Spring Data JPA / Hibernate | ORM and schema management    |
| JUnit 5                     | Unit and integration testing |
| Postgresql                  | Persistent storage           |
| GitHub Actions              | CI pipeline                  |
| Gradle                      | Build tooling                |
| Spotless                    | Code formatting enforcement  |
| Docker / Docker Compose     | Containerization             |

## API Documentation

#### 1. Register User

Registers a new user account and returns the created user details.

`POST` `/auth/register`

### Request

**Headers**

| Key            | Value              |
| :------------- | :----------------- |
| `Content-Type` | `application/json` |

**Body**

```json
{
  "username": "username1",
  "password": "password1"
}
```

### Response

`201 Created`

```json
{
  "id": 1,
  "username": "username1"
}
```

### Example Usage (cURL)

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"username1","password":"password1"}'
```

#### 2. Authenticate User

Authenticates an existing user and returns a JWT for subsequent requests.

`POST` `/auth/login`

### Request

**Headers**

| Key            | Value              |
| :------------- | :----------------- |
| `Content-Type` | `application/json` |

**Body**

```json
{
  "username": "username1",
  "password": "password1"
}
```

### Response

`200 OK`

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Example Usage (cURL)

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"username1","password":"password1"}'
```

---

#### 3. Create a Note

Creates a new note for the currently authenticated user.

`POST` `/notes`

### Request

**Headers**

| Key             | Value                     |
| :-------------- | :------------------------ |
| `Content-Type`  | `application/json`        |
| `Authorization` | `Bearer <your_jwt_token>` |

**Body**

```json
{
  "content": "some string"
}
```

### Response

`201 Created`

```json
{
  "id": 1,
  "content": "some string",
  "userName": "username1",
  "createdAt": "2026-06-25T00:00:00Z",
  "updatedAt": "2026-06-25T00:00:00Z"
}
```

### Example Usage (curl)

```bash
# Assuming $TOKEN contains your JWT
curl -X POST http://localhost:8080/notes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"some string"}'
```

#### 4. Get All Notes

Get all notes for the currently authenticated user

`GET` `/notes`

### Request

**Headers**

| Key             | Value                     |
| :-------------- | :------------------------ |
| `Authorization` | `Bearer <your_jwt_token>` |

### Response

`200 OK`

```json
[
  {
    "id": 1,
    "content": "some string",
    "userName": "username1",
    "createdAt": "2026-06-25T00:00:00Z",
    "updatedAt": "2026-06-25T00:00:00Z"
  }
]
```

### Example usage (curl)

```bash
curl http://localhost:8080/notes \
  -H "Authorization: Bearer $TOKEN"
```

#### 5. Get a Note

`GET` `/notes/{id}`

**Headers**

| Key             | Value                     |
| :-------------- | :------------------------ |
| `Authorization` | `Bearer <your_jwt_token>` |

### Response

`200 OK`  
`404 Not Found` if not owned/shared

```json
{
  "id": 1,
  "content": "some string",
  "userName": "username1",
  "createdAt": "2026-06-25T00:00:00Z",
  "updatedAt": "2026-06-25T00:00:00Z"
}
```

### Example usage (curl)

```bash
curl http://localhost:8080/notes/1 \
  -H "Authorization: Bearer $TOKEN"
```

#### 6. Update a Note

`PUT` `/notes/{id}`

**Headers**

| Key             | Value                     |
| :-------------- | :------------------------ |
| `Content-Type`  | `application/json`        |
| `Authorization` | `Bearer <your_jwt_token>` |

```json
{
  "content": "updated content"
}
```

### Response

`200 OK`  
`404 Not Found` if not owned/found

### Example usage (curl)

```bash
curl -X PUT http://localhost:8080/notes/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"updated content"}'
```

#### 7. Delete a Note

`DELETE` `/notes/{id}`

**Headers**

| Key             | Value                     |
| :-------------- | :------------------------ |
| `Authorization` | `Bearer <your_jwt_token>` |

### Response

`204 No Content`  
`404 Not Found` if not found or not owned

### Example usage (curl)

```bash
curl -X DELETE http://localhost:8080/notes/1 \
  -H "Authorization: Bearer $TOKEN"
```

#### 8. Share a Note

`POST` `/notes/{id}/share`

**Headers**

| Key             | Value                     |
| :-------------- | :------------------------ |
| `Authorization` | `Bearer <your_jwt_token>` |

```json
{
  "shareUserId": 2
}
```

### Response

`200 OK`  
`409 Conflict` if already shared with the user `404 Not found` if the note is not found/owned

### Example usage (curl)

```bash
curl -X POST http://localhost:8080/notes/1/share \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"shareUserId":2}'
```
