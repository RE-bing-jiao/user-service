# User Service Microservice

A Spring Boot microservice for managing user data with PostgreSQL and Redis caching.

## Features

- RESTful API endpoints for user management
- PostgreSQL database for persistent storage (18)
- Redis caching for improved performance
- Liquibase for database migration
- Junit and integration tests
- Docker containerization
- CI pipeline with GitHub Actions
- SonarCloud code quality analysis

## Prerequisites

- Docker and Docker Compose
- Java 21 (for local development)
- Maven Wrapper (version 3.3.4 included in project)

## Quick start

### Local Setup

1. Pull DockerHub image from https://hub.docker.com/r/evan1k/user-service

```bash
docker pull evan1k/user-service:tasks-USR-0
```

2. Configure PostgreSQL and Redis locally or use Docker Compose
3. Run your images or docker-compose file

**Note**: When running the standalone image, you must provide external PostgreSQL and Redis services.
