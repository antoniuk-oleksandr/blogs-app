# Blogs App

Spring Boot REST API for a blog platform. The application covers authentication, users, posts, comments, reactions, file storage, and asynchronous post indexing into OpenSearch.

<div align="center">

[![JavaDoc](https://img.shields.io/badge/docs-JavaDoc-blue?style=for-the-badge&logo=openjdk)](https://antoniuk-oleksandr.github.io/blogs-app/javadoc/)
[![API Docs](https://img.shields.io/badge/docs-OpenAPI-85EA2D?style=for-the-badge&logo=redoc)](https://antoniuk-oleksandr.github.io/blogs-app/openapi/api-documentation.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

[![Quality Gate Status](https://img.shields.io/sonar/quality_gate/antoniuk-oleksandr_blogs-app?server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)](https://sonarcloud.io/summary/new_code?id=antoniuk-oleksandr_blogs-app)
[![Sonar Coverage](https://img.shields.io/sonar/coverage/antoniuk-oleksandr_blogs-app?server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)](https://sonarcloud.io/summary/new_code?id=antoniuk-oleksandr_blogs-app)
[![CI/CD Pipeline](https://img.shields.io/github/actions/workflow/status/antoniuk-oleksandr/blogs-app/ci-cd.yaml?style=for-the-badge)](https://github.com/antoniuk-oleksandr/blogs-app/actions/workflows/ci-cd.yaml)

</div>

## What Is Implemented

- JWT authentication: register, login, refresh, logout, current user lookup.
- User profiles: public user lookup and authenticated profile update.
- Posts: create, read by slug, update, delete, image upload support, ownership checks.
- Comments: create, update, delete with author checks.
- Reactions: set or update post reactions.
- Search: OpenSearch-backed post search with relevance/newest/oldest sorting and cursor pagination.
- Search indexing: post changes are published to RabbitMQ and indexed into OpenSearch in batches.
- File storage: S3-compatible storage with LocalStack support for local development.
- Database migrations: PostgreSQL schema managed through Flyway.
- Observability: actuator health/metrics endpoints and Log4j2 JSON logging with optional CloudWatch delivery.
- Quality gates: JUnit 5, Mockito, Testcontainers, JaCoCo, SonarCloud, and GitHub Actions.

## Architecture

The code is organized by feature under `src/main/java/com/example/blogs/app/api`.

Main modules:

- `auth` handles JWT login, refresh, logout, and revoked-token persistence.
- `user` handles public profiles and profile updates.
- `post` owns post lifecycle, slug handling, author checks, and post DTO mapping.
- `comment` owns post comments and comment-level authorization.
- `reaction` owns post reaction state.
- `file` stores uploaded file metadata.
- `search` owns OpenSearch query building, execution, cursor pagination, document mapping, and RabbitMQ indexing events.

Infrastructure and cross-cutting code lives outside feature modules:

- `config` for Spring, security, OpenSearch, S3, and application configuration.
- `exception` for centralized HTTP exception handling.
- `security` for JWT conversion, password hashing, auth filters, and error responses.
- `storage` for S3 key generation, bucket access, and public link building.
- `logging` for CloudWatch/Log4j2 integration.
- `validation` and `util` for shared helpers.

## Search Flow

Post search is eventually consistent:

1. A post is created, updated, or deleted in PostgreSQL.
2. The application publishes a `PostSearchIndexEvent` after the database transaction commits.
3. RabbitMQ stores the indexing event.
4. The listener consumes events in batches.
5. `PostSearchIndexingService` loads the required post data and sends bulk index/delete operations to OpenSearch.
6. `GET /search` queries OpenSearch using relevance, newest, or oldest ordering.

This keeps write requests tied to PostgreSQL first, while OpenSearch is used as a read model for search.

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Security and OAuth2 Resource Server
- PostgreSQL, JPA/Hibernate, Flyway
- RabbitMQ
- OpenSearch Java Client
- S3-compatible object storage, AWS SDK, LocalStack
- Log4j2, Spring Boot Actuator, optional CloudWatch logs
- Gradle, Docker Compose
- JUnit 5, Mockito, AssertJ, Testcontainers, JaCoCo, SonarCloud

## Local Development

Requirements:

- Java 21
- Docker and Docker Compose

Create a root `.env` file for local development:

```bash
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin
POSTGRES_DB=blogs
POSTGRES_PORT=5432
POSTGRES_URL=jdbc:postgresql://localhost:5432/blogs
POSTGRES_USERNAME=admin
JWT_SECRET_KEY=change_me

OPENSEARCH_USERNAME=admin
OPENSEARCH_PASSWORD=admin
RABBITMQ_DEFAULT_USER=admin
RABBITMQ_DEFAULT_PASS=admin
```

The root `.env` is ignored by git. Keep local credentials there, not in tracked property files.

Start local infrastructure:

```bash
just docker-compose-up
```

Run the application:

```bash
./gw bootRun --args='--spring.profiles.active=local'
```

Useful local URLs:

- API: `http://localhost:8080`
- Actuator: `http://localhost:8081/actuator`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenSearch: `http://localhost:9200`
- RabbitMQ Management: `http://localhost:15672`
- Local SonarQube: `http://localhost:9000`

Stop local infrastructure:

```bash
just docker-compose-down
```

## Configuration

Main local configuration is in `src/main/resources/application-local.properties`.

Important environment variables:

```bash
POSTGRES_URL=jdbc:postgresql://localhost:5432/blogs
POSTGRES_USERNAME=admin
POSTGRES_PASSWORD=admin
JWT_SECRET_KEY=change_me

OPENSEARCH_HOST=http://localhost
OPENSEARCH_PORT=9200
OPENSEARCH_USERNAME=admin
OPENSEARCH_PASSWORD=admin

RABBITMQ_DEFAULT_USER=admin
RABBITMQ_DEFAULT_PASS=admin

SEARCH_INDEXING_EXCHANGE=blogs.search.indexing
SEARCH_INDEXING_QUEUE=blogs.search.indexing.posts
SEARCH_INDEXING_ROUTING_KEY=posts.index
SEARCH_INDEXING_BATCH_SIZE=20
```

Production values should be provided through the runtime environment or deployment secrets, not committed files.

## Database And Search Migrations

PostgreSQL migrations are stored in:

```text
src/main/resources/db/migration
```

OpenSearch index migrations are stored in:

```text
src/main/resources/es/migration
```

Flyway validates the relational schema at startup. The search index is managed separately through the OpenSearch migration files.

## Testing

Run the full test suite:

```bash
./gw test
```

Run tests with coverage:

```bash
./gw test jacocoTestReport
```

Open the HTML coverage report:

```bash
xdg-open build/reports/jacoco/test/html/index.html
```

Run only search-related tests:

```bash
./gw test --tests 'com.example.blogs.app.api.search.*' --tests 'com.example.blogs.app.util.CursorUtilsTest'
```

## API Documentation

When the app is running locally:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Published documentation:

- [OpenAPI HTML](https://antoniuk-oleksandr.github.io/blogs-app/openapi/api-documentation.html)
- [JavaDoc](https://antoniuk-oleksandr.github.io/blogs-app/javadoc/)

## CI/CD

GitHub Actions runs the build, tests, coverage reporting, SonarCloud analysis, documentation generation, Docker image publishing, and AWS deployment workflows depending on branch and workflow configuration.

SonarCloud is configured as a quality gate for coverage, maintainability, reliability, and security checks on new code.

## License

This project is licensed under the [MIT License](LICENSE).
