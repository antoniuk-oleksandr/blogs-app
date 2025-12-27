# Blog Application

> Enterprise-grade REST API for a modern blog platform featuring JWT authentication, Redis caching, and comprehensive observability

[![JavaDoc](https://img.shields.io/badge/docs-JavaDoc-blue?style=for-the-badge&logo=openjdk)](https://antoniuk-oleksandr.github.io/blogs-app/javadoc/)
[![API Docs](https://img.shields.io/badge/docs-OpenAPI-85EA2D?style=for-the-badge&logo=redoc)](https://antoniuk-oleksandr.github.io/blogs-app/openapi/api-documentation.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=antoniuk-oleksandr_blogs-app&metric=alert_status&style=for-the-badge)](https://sonarcloud.io/summary/new_code?id=antoniuk-oleksandr_blogs-app)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=antoniuk-oleksandr_blogs-app&metric=coverage&style=for-the-badge)](https://sonarcloud.io/summary/new_code?id=antoniuk-oleksandr_blogs-app)
[![CI](https://github.com/antoniuk-oleksandr/blogs-app/actions/workflows/ci-cd.yml/badge.svg?branch=dev)](https://github.com/antoniuk-oleksandr/blogs-app/actions/workflows/ci-cd.yaml)

## ✨ Features

### Core Functionality
- 🔐 **JWT Authentication** - Secure token-based authentication with refresh tokens
- 👤 **User Management** - Complete user registration and profile management
- 📝 **Article System** - Create, read, update, and delete blog posts *(Coming Soon)*
- 💬 **Comments** - Threaded comment system *(Coming Soon)*
- 🏷️ **Tags & Categories** - Organize content efficiently *(Coming Soon)*

### Technical Features
- ✅ **SOLID Principles** - Clean, maintainable OOP architecture
- ✅ **Comprehensive Validation** - Request validation with Jakarta Bean Validation
- ✅ **Global Exception Handling** - Centralized error handling with detailed responses
- ✅ **Database Migrations** - Flyway for version-controlled schema changes
- ✅ **Test Coverage** - JUnit, Mockito with JaCoCo reporting
- ✅ **OpenAPI 3.0** - Interactive API documentation
- ✅ **Observability** - Structured logging with Prometheus metrics and Grafana dashboards *(Coming Soon)*
- ✅ **CI/CD Pipeline** - Automated testing and deployment *(Coming Soon)*
- ✅ **Redis Caching** - Performance optimization for frequently accessed data *(Coming Soon)*

## 🛠️ Tech Stack

### Backend
- **Java 21** - Latest LTS with modern language features
- **Spring Boot 3.5.9** - Production-ready application framework
- **Spring Security 6** - Enterprise security framework
- **Spring Data JPA** - Database abstraction with Hibernate

### Database & Caching
- **PostgreSQL 18** - Primary relational database
- **Flyway** - Database migration management
- **Redis 8** - In-memory caching *(Coming Soon)*

### Authentication & Security
- **JWT (HS256)** - Stateless authentication
- **BCrypt** - Password hashing

### Testing & Quality
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking for isolated tests
- **JaCoCo** - Code coverage reporting
- **Testcontainers** - Integration testing with real databases

### Observability & Monitoring
- **SLF4J + Logback** - Structured logging *(Coming Soon)*
- **Prometheus** - Metrics collection *(Coming Soon)*
- **Grafana** - Metrics visualization and log aggregation *(Coming Soon)*

### Build & Tools
- **Gradle 8** - Build automation
- **Docker & Docker Compose** - Containerization
- **Lombok** - Boilerplate reduction
- **SpringDoc OpenAPI** - API documentation generation
- **Redoc** - Static API documentation

## 📚 Documentation

- **[REST API Documentation](https://antoniuk-oleksandr.github.io/blogs-app/openapi/api-documentation.html)** - Complete OpenAPI specification
- **[JavaDoc](https://antoniuk-oleksandr.github.io/blogs-app/javadoc/)** - Detailed code documentation

For architecture details, see the JavaDoc package documentation.

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Docker & Docker Compose

### Installation
```bash
# Clone repository
git clone https://github.com/antoniuk-oleksandr/blogs-app.git
cd blogs-app

# Start infrastructure (PostgreSQL)
docker-compose up -d

# Run application
./gradlew bootRun
```

The API will be available at `http://localhost:8080`

**Interactive API Documentation:** http://localhost:8080/swagger-ui/index.html

### Running with Docker
```bash
# Build and run all services
docker-compose up --build

# Stop all services
docker-compose down
```

## 🧪 Testing
```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```

## 🔧 Configuration

### Environment Variables
```bash
# PostgreSQL Database Configuration
POSTGRES_URL=your_postgres_url
POSTGRES_USERNAME=your_postgres_username
POSTGRES_PASSWORD=your_postgres_password

# JWT configuration
JWT_SECRET_KEY=your_jwt_secret_key
```

See `.env.example` for complete configuration.

## 🎯 Roadmap

### Phase 1: Foundation ✅
- [x] User authentication (JWT)
- [x] User registration
- [x] Global exception handling
- [x] OpenAPI documentation
- [x] Database migrations (Flyway)
- [x] Test coverage with JaCoCo

### Phase 2: Core Features (In Progress)
- [ ] Article CRUD operations
- [ ] Redis caching integration
- [ ] Structured logging with JSON output
- [ ] Prometheus metrics integration
- [ ] Grafana dashboards (metrics + logs)
- [ ] CI/CD pipeline with coverage reports

### Phase 3: Advanced Features
- [ ] Comment system
- [ ] Tag and category management
- [ ] User profile management
- [ ] Article search and filtering
- [ ] Pagination and sorting
- [ ] Testcontainers for integration tests

### Phase 4: Production Ready
- [ ] Performance optimization
- [ ] Rate limiting
- [ ] API versioning
- [ ] Comprehensive monitoring

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Built with Spring Boot**

[Report Bug](https://github.com/antoniuk-oleksandr/blogs-app/issues) · [Request Feature](https://github.com/antoniuk-oleksandr/blogs-app/issues)

</div>