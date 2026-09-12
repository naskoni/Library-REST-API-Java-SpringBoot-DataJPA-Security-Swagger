# Library REST API

A REST API for library management, originally created in 2016 and later refactored and modernized with current Java and Spring
technologies.

## Technology Stack

* Java 26
* Spring Boot 4.1.1
* Spring Framework 7
* Spring Security 7
* Spring Data JPA
* Gradle 9.7.1
* JUnit 5
* Springdoc OpenAPI
* Hibernate
* H2 / MySQL / PostgreSQL
* Lombok

## Prerequisites

* Java 26 JDK
* Gradle Wrapper is included, so a separate Gradle installation is not required

The application uses an H2 in-memory database by default, but can also be configured to use MySQL or PostgreSQL.

## Pluggable Exporters

The application supports extending the export functionality without recompiling the main application.

Any implementation of the `Exporter` interface can be compiled and placed in the `pluginClasses` directory.

The directory is configured through `application.properties`:

```properties
dir.classes=pluginClasses
```

The application discovers and loads exporter implementations dynamically when it starts.

The project currently supports two types of exporters:

- CSV exporter — included in the application code and loaded directly by the application.
- XML exporter — implemented as a plugin and loaded dynamically from the pluginClasses directory at application startup.

## Database Configuration

The application uses the following environment variables:

* `LIBRARY.DB.URL` — database JDBC URL
  Default: `jdbc:h2:mem:test`
* `LIBRARY.DB.USERNAME` — database username
  Default: `sa`
* `LIBRARY.DB.PASSWORD` — database password
  Default: `sa`
* `LIBRARY.DDL` — Hibernate schema generation strategy
  Default: `create`

Examples:

```text
H2:
jdbc:h2:mem:test

MySQL:
jdbc:mysql://localhost/library

PostgreSQL:
jdbc:postgresql://localhost:5432/postgres
```

## Swagger / OpenAPI

When running locally, the Swagger UI is available at:

`http://localhost:8080/library/api/swagger-ui/index.html`

The OpenAPI specification is available at:

`http://localhost:8080/library/api/v3/api-docs`

The OpenAPI specification is generated using Springdoc OpenAPI.

## Security

The application uses HTTP Basic Authentication.

The database is initialized with two users:

* `admin` / `admin` — `ROLE_ADMIN`
* `user` / `user` — `ROLE_USER`

Administrators have access to all endpoints.

Regular users cannot access the user management endpoints or perform delete and export operations.

## ETags

The API supports shallow ETags for GET requests.

The ETag returned by a GET request can be sent back using the `If-None-Match` request header.

If the resource has not changed, the API returns:

```text
304 Not Modified
```

with no response body.

## Testing

The project contains unit and integration tests using JUnit 5, Mockito and Spring Test.

Run the tests with:

```bash
./gradlew test
```

Build the application with:

```bash
./gradlew clean build
```
