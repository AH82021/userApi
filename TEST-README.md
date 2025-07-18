# Spring Boot REST API Testing Guide

This project includes comprehensive test suites for the Spring Boot REST API with JWT authentication.

## Test Structure

```
src/test/java/
├── com/armancodeblock/user_rest_api/
│   ├── config/
│   │   └── TestConfig.java                    # Test configuration
│   ├── controller/
│   │   └── UserControllerTest.java           # Controller unit tests
│   ├── integration/
│   │   └── UserIntegrationTest.java          # Integration tests
│   ├── repository/
│   │   └── UserRepositoryTest.java           # Repository tests
│   ├── security/
│   │   └── JwtSecurityIntegrationTest.java   # Security tests
│   └── service/
│       └── UserServiceTest.java              # Service unit tests
└── resources/
    └── application-test.properties           # Test configuration
```

## Test Categories

### 1. Unit Tests

- **UserServiceTest**: Tests business logic in isolation
- **UserControllerTest**: Tests REST endpoints with mocked dependencies

### 2. Integration Tests

- **UserIntegrationTest**: Tests complete request-response cycle
- **JwtSecurityIntegrationTest**: Tests JWT authentication and authorization

### 3. Repository Tests

- **UserRepositoryTest**: Tests data access layer with @DataJpaTest

## Running Tests

### Using Maven

```bash
# Run all tests
./mvnw test

# Run unit tests only
./mvnw test -Dtest="**/*Test"

# Run integration tests only
./mvnw test -Dtest="**/*IntegrationTest"

# Run specific test class
./mvnw test -Dtest="UserServiceTest"

# Run tests with coverage
./mvnw test jacoco:report
```

### Using Test Runner Script

```bash
# Make script executable
chmod +x test-runner.sh

# Run the interactive test runner
./test-runner.sh
```

## Test Configuration

### Database Configuration

- Tests use H2 in-memory database
- Configuration in `application-test.properties`
- Automatic cleanup between tests

### Security Configuration

- JWT testing with mock users
- Role-based access control testing
- Test-specific JWT secret

## Test Examples

### Unit Test Example

```java
@Test
void createUser_ShouldReturnSavedUser() {
    // Given
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    User result = userService.createUser(testUser);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("John Doe");
    verify(userRepository, times(1)).save(testUser);
}
```

### Integration Test Example

```java
@Test
void createUser_ShouldPersistUserInDatabase() throws Exception {
    // Given
    User newUser = new User("New User", "new@example.com");

    // When
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newUser)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("New User"));

    // Then - verify in database
    List<User> users = userRepository.findAll();
    assertThat(users).hasSize(3);
}
```

### Security Test Example

```java
@Test
void deleteUser_WithAdminToken_ShouldReturnSuccess() throws Exception {
    // Given
    String adminToken = getJwtToken("admin", "admin123");

    // When & Then
    mockMvc.perform(delete("/api/users/" + userId)
            .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNoContent());
}
```

## Test Coverage

To generate test coverage report:

```bash
./mvnw test jacoco:report
```

View the report at: `target/site/jacoco/index.html`

## Test Best Practices

1. **Naming Convention**: Use `should_when_given` pattern
2. **Test Structure**: Follow Given-When-Then pattern
3. **Isolation**: Each test should be independent
4. **Mock Strategy**: Mock external dependencies
5. **Data Setup**: Use @BeforeEach for test data preparation
6. **Assertions**: Use AssertJ for fluent assertions

## Common Test Scenarios

### Controller Tests

- Valid input handling
- Invalid input validation
- Security authorization
- Error handling

### Service Tests

- Business logic validation
- Exception handling
- Database interaction mocking
- Edge cases

### Integration Tests

- End-to-end request processing
- Database persistence
- Security integration
- Error responses

### Repository Tests

- CRUD operations
- Custom query methods
- Constraint validation
- Transaction handling

## Debugging Tests

### Common Issues

1. **Test dependencies**: Ensure all test dependencies are in `pom.xml`
2. **Test profiles**: Use `@ActiveProfiles("test")` for test configuration
3. **Mock setup**: Verify mock behavior matches actual usage
4. **Database state**: Use `@Transactional` for test isolation

### Useful Test Annotations

- `@SpringBootTest`: Full application context
- `@WebMvcTest`: Web layer testing
- `@DataJpaTest`: Repository testing
- `@MockBean`: Spring Boot mock beans
- `@WithMockUser`: Security testing

## Next Steps

1. Add more edge case tests
2. Implement performance tests
3. Add contract tests for API consumers
4. Set up continuous integration
5. Add mutation testing

## Resources

- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing)
- [Spring Security Testing](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#test)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
