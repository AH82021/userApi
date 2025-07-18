# Spring Boot Testing Guide: Unit Tests and Integration Tests

## Table of Contents

1. [Introduction to Testing in Spring Boot](#introduction)
2. [Setting Up Testing Dependencies](#dependencies)
3. [Unit Testing Fundamentals](#unit-testing)
4. [Integration Testing Fundamentals](#integration-testing)
5. [Testing Your User REST API](#testing-your-api)
6. [Best Practices](#best-practices)
7. [Common Testing Patterns](#common-patterns)
8. [Troubleshooting](#troubleshooting)

---

## 1. Introduction to Testing in Spring Boot {#introduction}

### What is Testing?

Testing is the process of verifying that your application works as expected. In Spring Boot applications, we typically write two types of tests:

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test how components work together

### Why Test?

- **Quality Assurance**: Catch bugs early
- **Confidence**: Deploy with confidence
- **Documentation**: Tests serve as living documentation
- **Refactoring Safety**: Change code without breaking functionality

### Testing Pyramid

```
    /\
   /  \
  / UI \
 /______\
/        \
/Integration\
/____________\
/            \
/   Unit      \
/______________\
```

---

## 2. Setting Up Testing Dependencies {#dependencies}

### Current Dependencies in Your pom.xml

Your project already includes the essential testing dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### What's Included in spring-boot-starter-test:

- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework
- **Spring Test**: Spring testing utilities
- **AssertJ**: Fluent assertions
- **Hamcrest**: Matcher library
- **TestContainers**: Database testing

### Additional Dependencies for Your API Testing

Add these to your pom.xml for comprehensive testing:

```xml
<!-- For testing with embedded database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>

<!-- For testing security -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- For JSON testing -->
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 3. Unit Testing Fundamentals {#unit-testing}

### What is Unit Testing?

Unit testing involves testing individual components (classes, methods) in isolation from their dependencies.

### Key Annotations:

- `@ExtendWith(MockitoExtension.class)`: Enables Mockito
- `@Mock`: Creates mock objects
- `@InjectMocks`: Injects mocks into the test subject
- `@Test`: Marks test methods

### Example: Testing UserService

```java
// src/test/java/com/armancodeblock/user_rest_api/service/UserServiceTest.java
package com.armancodeblock.user_rest_api.service;

import com.armancodeblock.user_rest_api.enity.User;
import com.armancodeblock.user_rest_api.exception.ResourceNotFoundException;
import com.armancodeblock.user_rest_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com");
        User user2 = new User("Jane Smith", "jane@example.com");
        userList = Arrays.asList(testUser, user2);
    }

    @Test
    void createUser_ShouldReturnSavedUser() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser(testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void getAllUsers_ShouldReturnPagedUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(userList, pageable, userList.size());
        when(userRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<User> result = userService.getAllUsers(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("John Doe");
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getUserById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with userId:" + userId);

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void deleteUserById_ShouldCallRepositoryDelete() {
        // Given
        Long userId = 1L;

        // When
        userService.deleteUserById(userId);

        // Then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void updateUser_WithExistingUser_ShouldUpdateAndReturn() {
        // Given
        Long userId = 1L;
        User existingUser = new User("Old Name", "old@example.com");
        User updatedUser = new User("New Name", "new@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.updateUser(userId, updatedUser);

        // Then
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllUsersByNamePrefix_ShouldReturnFilteredUsers() {
        // Given
        String prefix = "John";
        List<User> filteredUsers = Arrays.asList(testUser);
        when(userRepository.findUserByNamePrefix(prefix)).thenReturn(filteredUsers);

        // When
        List<User> result = userService.getAllUsersByNamePrefix(prefix);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        verify(userRepository, times(1)).findUserByNamePrefix(prefix);
    }
}
```

---

## 4. Integration Testing Fundamentals {#integration-testing}

### What is Integration Testing?

Integration testing verifies that different components work together correctly, including:

- Controller + Service + Repository
- Database interactions
- Security configurations
- HTTP requests/responses

### Key Annotations:

- `@SpringBootTest`: Loads full application context
- `@TestMethodOrder`: Controls test execution order
- `@AutoConfigureTestDatabase`: Configures test database
- `@Sql`: Executes SQL scripts before tests
- `@Transactional`: Rolls back transactions after tests

### Example: Testing UserController (Integration Test)

```java
// src/test/java/com/armancodeblock/user_rest_api/controller/UserControllerIntegrationTest.java
package com.armancodeblock.user_rest_api.controller;

import com.armancodeblock.user_rest_api.enity.User;
import com.armancodeblock.user_rest_api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Clear database before each test
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        // Given
        User user = new User("John Doe", "john@example.com");
        String userJson = objectMapper.writeValueAsString(user);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        User user = new User("", "invalid-email"); // Invalid data
        String userJson = objectMapper.writeValueAsString(user);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() throws Exception {
        // Given
        User savedUser = userRepository.save(new User("John Doe", "john@example.com"));

        // When & Then
        mockMvc.perform(get("/api/v1/users/{id}", savedUser.getUserId()))
                .andExpected(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUserById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", 999L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        // Given
        User existingUser = userRepository.save(new User("Old Name", "old@example.com"));
        User updatedUser = new User("New Name", "new@example.com");
        String userJson = objectMapper.writeValueAsString(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/v1/users/{id}", existingUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        User savedUser = userRepository.save(new User("John Doe", "john@example.com"));

        // When & Then
        mockMvc.perform(delete("/api/v1/users/{id}", savedUser.getUserId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteUser_WithUserRole_ShouldReturnForbidden() throws Exception {
        // Given
        User savedUser = userRepository.save(new User("John Doe", "john@example.com"));

        // When & Then
        mockMvc.perform(delete("/api/v1/users/{id}", savedUser.getUserId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void searchUsers_WithPrefix_ShouldReturnFilteredUsers() throws Exception {
        // Given
        userRepository.save(new User("John Doe", "john@example.com"));
        userRepository.save(new User("Jane Smith", "jane@example.com"));
        userRepository.save(new User("Bob Johnson", "bob@example.com"));

        // When & Then
        mockMvc.perform(get("/api/v1/users/search")
                .param("prefix", "J"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
```

---

## 5. Testing Your User REST API {#testing-your-api}

### Step 1: Create Test Configuration

```java
// src/test/resources/application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
logging.level.org.springframework.security=DEBUG
```

### Step 2: Create Test Data Setup

```java
// src/test/java/com/armancodeblock/user_rest_api/TestDataBuilder.java
package com.armancodeblock.user_rest_api;

import com.armancodeblock.user_rest_api.enity.User;
import com.armancodeblock.user_rest_api.enity.AccountCredentials;

public class TestDataBuilder {

    public static User createValidUser() {
        return new User("John Doe", "john@example.com");
    }

    public static User createUserWithId(Long id) {
        User user = new User("John Doe", "john@example.com");
        // Note: You might need to use reflection or a different approach
        // since userId might not have a setter
        return user;
    }

    public static AccountCredentials createAdminCredentials() {
        AccountCredentials credentials = new AccountCredentials();
        credentials.setUsername("admin");
        credentials.setPassword("adminPass");
        return credentials;
    }

    public static AccountCredentials createUserCredentials() {
        AccountCredentials credentials = new AccountCredentials();
        credentials.setUsername("user");
        credentials.setPassword("userPass");
        return credentials;
    }
}
```

### Step 3: Test Security Configuration

```java
// src/test/java/com/armancodeblock/user_rest_api/security/SecurityConfigTest.java
package com.armancodeblock.user_rest_api.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Test
    void loginEndpoint_ShouldBeAccessible_WithoutAuthentication() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(post("/api/v1/login"))
                .andExpect(status().isBadRequest()); // Bad request due to missing body
    }

    @Test
    void getUsersEndpoint_ShouldBeAccessible_WithoutAuthentication() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WithAdminRole_ShouldBeAllowed() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteUser_WithUserRole_ShouldBeForbidden() throws Exception {
        mockMvc = MockMvc builders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isForbidden());
    }
}
```

### Step 4: Test JWT Authentication

```java
// src/test/java/com/armancodeblock/user_rest_api/security/JwtServiceTest.java
package com.armancodeblock.user_rest_api.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Test
    void getToken_ShouldGenerateValidToken() {
        // Given
        String username = "testuser";

        // When
        String token = jwtService.getToken(username);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void getUsernameFromToken_WithValidToken_ShouldReturnUsername() {
        // Given
        String username = "testuser";
        String token = jwtService.getToken(username);

        // When
        String extractedUsername = jwtService.getUsernameFromToken(token);

        // Then
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void getUsernameFromToken_WithInvalidToken_ShouldReturnNull() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        String extractedUsername = jwtService.getUsernameFromToken(invalidToken);

        // Then
        assertThat(extractedUsername).isNull();
    }
}
```

---

## 6. Best Practices {#best-practices}

### Naming Conventions

- Test class: `[ClassUnderTest]Test` or `[ClassUnderTest]IntegrationTest`
- Test method: `methodName_condition_expectedResult`

### Test Structure (AAA Pattern)

```java
@Test
void methodName_condition_expectedResult() {
    // Arrange (Given)
    // Set up test data and mocks

    // Act (When)
    // Execute the method under test

    // Assert (Then)
    // Verify the results
}
```

### Mock vs Integration Testing

- **Use Mocks**: For unit testing, fast execution, isolated testing
- **Use Integration Tests**: For end-to-end testing, database interactions, security testing

### Test Data Management

- Use `@BeforeEach` for setup
- Use `@AfterEach` for cleanup
- Use `@Transactional` for database rollback

### Security Testing

- Use `@WithMockUser` for role-based testing
- Test both authenticated and unauthenticated scenarios
- Test different user roles and permissions

---

## 7. Common Testing Patterns {#common-patterns}

### Repository Testing

```java
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByNameStartingWith_ShouldReturnMatchingUsers() {
        // Given
        entityManager.persistAndFlush(new User("John Doe", "john@example.com"));
        entityManager.persistAndFlush(new User("Jane Smith", "jane@example.com"));

        // When
        List<User> users = userRepository.findByNameStartingWith("J");

        // Then
        assertThat(users).hasSize(2);
    }
}
```

### Controller Testing (Web Layer Only)

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers_ShouldReturnUsers() throws Exception {
        // Given
        List<User> users = Arrays.asList(new User("John", "john@example.com"));
        Page<User> page = new PageImpl<>(users);
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }
}
```

### Testing Exceptions

```java
@Test
void getUserById_WithInvalidId_ShouldThrowResourceNotFoundException() {
    // Given
    Long invalidId = 999L;
    when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.getUserById(invalidId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("User not found");
}
```

---

## 8. Running Tests {#running-tests}

### Command Line

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=UserServiceTest

# Run specific test method
./mvnw test -Dtest=UserServiceTest#createUser_ShouldReturnSavedUser

# Run with profile
./mvnw test -Dspring.profiles.active=test
```

### IDE (IntelliJ/Eclipse)

- Right-click on test class/method → Run Test
- Use green arrow buttons
- View test results in Test Runner window

### Test Coverage

Add JaCoCo plugin to pom.xml:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.7</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## 9. Troubleshooting {#troubleshooting}

### Common Issues

#### 1. Circular Dependency in Tests

```java
// Problem: Circular dependency between SecurityConfig and JwtAuthenticationFilter
// Solution: Use @TestConfiguration to provide test-specific beans

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    @Primary
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return Mockito.mock(JwtAuthenticationFilter.class);
    }
}
```

#### 2. Database Connection Issues

```java
// Use H2 in-memory database for tests
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
```

#### 3. JWT Token Testing

```java
// Mock JWT service for controller tests
@MockBean
private JwtService jwtService;

@Test
void testWithJwtToken() {
    when(jwtService.getUsernameFromToken(anyString())).thenReturn("testuser");
    // ... rest of test
}
```

### Debugging Tests

- Use `@Sql` to setup test data
- Use `@DirtiesContext` to reset application context
- Use `@TestMethodOrder` for ordered test execution
- Add logging to see what's happening

---

## 10. Complete Example Test Suite

Here's a complete example of how to structure your test suite:

```
src/test/java/com/armancodeblock/user_rest_api/
├── TestDataBuilder.java
├── controller/
│   ├── UserControllerTest.java (Unit test with @WebMvcTest)
│   ├── UserControllerIntegrationTest.java (Integration test)
│   └── LoginControllerTest.java
├── service/
│   ├── UserServiceTest.java (Unit test with mocks)
│   └── AuthUserServiceTest.java
├── repository/
│   └── UserRepositoryTest.java (@DataJpaTest)
├── security/
│   ├── JwtServiceTest.java
│   ├── JwtAuthenticationFilterTest.java
│   └── SecurityConfigTest.java
└── integration/
    └── UserRestApiIntegrationTest.java (Full integration test)
```

---

## Summary

This guide provides you with:

1. **Unit Testing**: Test individual components in isolation
2. **Integration Testing**: Test components working together
3. **Security Testing**: Test authentication and authorization
4. **Repository Testing**: Test database interactions
5. **Controller Testing**: Test HTTP endpoints
6. **Best Practices**: Follow industry standards
7. **Common Patterns**: Reusable testing strategies

### Next Steps:

1. Add the additional dependencies to your `pom.xml`
2. Create the test configuration files
3. Start with unit tests for your service layer
4. Add integration tests for your controllers
5. Test your security configuration
6. Run tests regularly during development

Remember: **Good tests are as important as good code!**
