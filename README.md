## Highlights

## Usage Instructions

## Running
### Run in Docker
1. Create a `.env` file with a username and password (see `.env.example`) 
2. Run the project in Docker using `docker-compose up --build`

### Building
`./gradlew build`

### Formatting
`./gradlew spotlessApply`

---
## Tech Stack

| Technology                   | Purpose                      |
|------------------------------|------------------------------|
| Java 25                      | Application language         |
| Spring Boot 4                | Application framework        |
| Spring Data JPA / Hibernate  | ORM and schema management    |
| JUnit 5 / Mockito            | Unit and integration testing |
| Postgresql                   | Persistent storage           | 
| GitHub Actions               | CI pipeline                  |
| Gradle                       | Build tooling                |
| Spotless                     | Code formatting enforcement  |

---