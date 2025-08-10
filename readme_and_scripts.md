# User API - Spring Boot REST API with Comprehensive Testing

A complete Spring Boot REST API for user management with comprehensive testing suite including unit tests, integration tests, API contract tests, and performance tests.

## Features

- **REST API Endpoints:**
  - `GET /api/users` - Get all users
  - `GET /api/users/{id}` - Get user by ID
  - `POST /api/users` - Create new user
  - `PUT /api/users/{id}` - Update existing user
  - `DELETE /api/users/{id}` - Delete user

- **Comprehensive Testing:**
  - Unit Tests with Mockito
  - Integration Tests with TestContainers
  - API Contract Tests with Spring Cloud Contract
  - Performance Tests with JMeter and custom load testing

- **Technologies:**
  - Java 17
  - Spring Boot 3.2.0
  - Spring Data JPA
  - H2 Database (development/testing)
  - PostgreSQL (production)
  - MapStruct for mapping
  - Maven for build management

## Project Structure

```
src/
├── main/java/com/example/userapi/
│   ├── UserApiApplication.java
│   ├── controller/
│   │   └── UserController.java
│   ├── service/
│   │   └── UserService.java
│   ├── repository/
│   │   └── UserRepository.java
│   ├── entity/
│   │   └── User.java
│   ├── dto/
│   │   └── UserDto.java
│   ├── mapper/
│   │   └── UserMapper.java
│   └── exception/
│       ├── UserNotFoundException.java
│       ├── UserAlreadyExistsException.java
│       └── GlobalExceptionHandler.java
├── test/java/com/example/userapi/
│   ├── service/
│   │   └── UserServiceTest.java (Unit Tests)
│   ├── controller/
│   │   └── UserControllerTest.java (Unit Tests)
│   ├── contract/
│   │   └── ContractTestBase.java (Contract Tests)
│   ├── performance/
│   │   ├── UserPerformanceTest.java
│   │   └── SimplePerformanceTest.java
│   └── UserApiIT.java (Integration Tests)
├── test/resources/
│   ├── contracts/user/
│   │   ├── shouldReturnUser.groovy
│   │   ├── shouldReturnAllUsers.groovy
│   │   └── shouldCreateUser.groovy
│   └── application-test.yml
└── test/jmeter/
    └── UserAPI-LoadTest.jmx
```

## Getting Started

### Prerequisites
- Java 17
- Maven 3.6+
- Docker (optional, for TestContainers)

### Build and Run

1. **Clone the repository:**
```bash
git clone <repository-url>
cd user-api
```

2. **Build the application:**
```bash
mvn clean compile
```

3. **Run the application:**
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

4. **Access H2 Console (development):**
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:devdb`
- Username: `sa`
- Password: (empty)

## Testing

### Running All Tests

```bash
# Run all tests including unit, integration, and contract tests
mvn clean test verify
```

### Unit Tests

```bash
# Run only unit tests
mvn test
```

Unit tests cover:
- Service layer logic with mocked dependencies
- Controller layer with MockMvc
- Exception handling
- Validation logic

### Integration Tests

```bash
# Run only integration tests
mvn test -Dtest="*IT"
```

Integration tests cover:
- Full application context
- Database interactions
- End-to-end API functionality
- Error scenarios

### Contract Tests

Contract tests use Spring Cloud Contract to ensure API compatibility:

```bash
# Generate and run contract tests
mvn clean test -Dtest="*ContractTest"
```

The contracts are defined in Groovy DSL format in `src/test/resources/contracts/`.

### Performance Tests

#### Simple Performance Tests (Java-based)

```bash
# Run simple performance tests
mvn test -Dtest="SimplePerformanceTest"
```

#### JMeter Performance Tests

```bash
# Run JMeter performance tests
mvn clean test -Pperformance
```

Or run JMeter tests directly:

```bash
# Start the application first
mvn spring-boot:run &

# Run JMeter tests
mvn jmeter:jmeter -Pperformance

# Stop the application
pkill -f spring-boot:run
```

## API Documentation

### Create User
```http
POST /api/users
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "createdAt": "2023-01-01T10:00:00",
  "updatedAt": "2023-01-01T10:00:00"
}
```

### Get All Users
```http
GET /api/users
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "createdAt": "2023-01-01T10:00:00",
    "updatedAt": "2023-01-01T10:00:00"
  }
]
```

### Get User by ID
```http
GET /api/users/1
```

**Response (200 OK):** Same as create user response

### Update User
```http
PUT /api/users/1
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com"
}
```

### Delete User
```http
DELETE /api/users/1
```

**Response:** 204 No Content

## Error Handling

The API provides comprehensive error handling with appropriate HTTP status codes:

- **400 Bad Request:** Invalid input data
- **404 Not Found:** User not found
- **409 Conflict:** Email already exists
- **500 Internal Server Error:** Unexpected errors

**Error Response Format:**
```json
{
  "status": 404,
  "message": "User not found with id: 1",
  "timestamp": "2023-01-01T10:00:00"
}
```

**Validation Error Response:**
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2023-01-01T10:00:00",
  "errors": {
    "firstName": "First name is required",
    "email": "Email should be valid"
  }
}
```

## Configuration

### Application Properties

The application supports multiple profiles:

- **default/dev:** H2 in-memory database
- **test:** H2 in-memory database with test-specific settings
- **prod:** PostgreSQL database

### Environment Variables (Production)

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/userapi
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your-password
```

## Performance Test Results

The included performance tests measure:

- **Throughput:** Requests per second
- **Response Time:** Average response time under load
- **Concurrency:** Behavior under concurrent users
- **Resource Usage:** Memory and CPU utilization

### Sample Performance Metrics

Based on simple hardware (typical development machine):

- **GET /api/users:** ~500 requests/second
- **POST /api/users:** ~300 requests/second
- **Average Response Time:** <50ms under normal load
- **95th Percentile:** <100ms

## Monitoring and Health Checks

The application includes Spring Boot Actuator endpoints:

- **Health:** `GET /actuator/health`
- **Metrics:** `GET /actuator/metrics`
- **Info:** `GET /actuator/info`

## Docker Support

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/user-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose (with PostgreSQL)
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DATABASE_URL=jdbc:postgresql://db:5432/userapi
      - DATABASE_USERNAME=postgres
      - DATABASE_PASSWORD=password
    depends_on:
      - db
  
  db:
    image: postgres:13
    environment:
      - POSTGRES_DB=userapi
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

## CI/CD Pipeline

### GitHub Actions Example
```yaml
name: CI/CD Pipeline

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Cache Maven dependencies
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
      
      - name: Run tests
        run: mvn clean verify
      
      - name: Run performance tests
        run: mvn test -Dtest="SimplePerformanceTest"
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add comprehensive tests for new features
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License.