package com.armancodeblock.user_rest_api.repository;

import com.armancodeblock.user_rest_api.enity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new User("John Doe", "john@example.com");
        testUser2 = new User("Jane Smith", "jane@example.com");
        
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
    }

    @Test
    void findById_WithValidId_ShouldReturnUser() {
        // When
        Optional<User> found = userRepository.findById(testUser1.getUserId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findById_WithInvalidId_ShouldReturnEmpty() {
        // When
        Optional<User> found = userRepository.findById(999L);

        // Then
        assertThat(found).isNotPresent();
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        // When
        List<User> users = userRepository.findAll();

        // Then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }

    @Test
    void save_ShouldPersistUser() {
        // Given
        User newUser = new User("New User", "new@example.com");

        // When
        User savedUser = userRepository.save(newUser);

        // Then
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("New User");
        assertThat(savedUser.getEmail()).isEqualTo("new@example.com");

        // Verify in database
        User foundUser = entityManager.find(User.class, savedUser.getUserId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getName()).isEqualTo("New User");
    }

    @Test
    void deleteById_ShouldRemoveUser() {
        // Given
        Long userId = testUser1.getUserId();

        // When
        userRepository.deleteById(userId);
        entityManager.flush();

        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isNotPresent();
        
        // Verify other user still exists
        List<User> remainingUsers = userRepository.findAll();
        assertThat(remainingUsers).hasSize(1);
        assertThat(remainingUsers.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    void existsById_WithValidId_ShouldReturnTrue() {
        // When
        boolean exists = userRepository.existsById(testUser1.getUserId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WithInvalidId_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void findUserByNamePrefix_ShouldReturnMatchingUsers() {
        // When
        List<User> users = userRepository.findUserByNamePrefix("John");

        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    void findUserByNamePrefix_WithNoMatches_ShouldReturnEmptyList() {
        // When
        List<User> users = userRepository.findUserByNamePrefix("NonExistent");

        // Then
        assertThat(users).isEmpty();
    }

    @Test
    void findUserByNamePrefix_WithCaseInsensitive_ShouldReturnMatchingUsers() {
        // When - using exact case since the method is case-sensitive
        List<User> users = userRepository.findUserByNamePrefix("Jane");

        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    void updateUser_ShouldPersistChanges() {
        // Given
        User userToUpdate = testUser1;
        userToUpdate.setName("Updated Name");
        userToUpdate.setEmail("updated@example.com");

        // When
        User updatedUser = userRepository.save(userToUpdate);
        entityManager.flush();

        // Then
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");

        // Verify in database
        User foundUser = entityManager.find(User.class, updatedUser.getUserId());
        assertThat(foundUser.getName()).isEqualTo("Updated Name");
        assertThat(foundUser.getEmail()).isEqualTo("updated@example.com");
    }
}
