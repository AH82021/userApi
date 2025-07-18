package com.armancodeblock.user_rest_api.controller;

import com.armancodeblock.user_rest_api.enity.User;
import com.armancodeblock.user_rest_api.exception.ResourceNotFoundException;
import com.armancodeblock.user_rest_api.service.UserService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com");
        User user2 = new User("Jane Smith", "jane@example.com");
        userList = Arrays.asList(testUser, user2);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        // Given
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<User> response = userController.createUser(testUser);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService).createUser(testUser);
    }

    @Test
    void getAllUsers_ShouldReturnPagedUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(userList, pageable, userList.size());
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        // When
        ResponseEntity<Page<User>> response = userController.getAllUsers(pageable);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<User> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.getContent().size());
        verify(userService).getAllUsers(pageable);
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        // Given
        when(userService.getUserById(1L)).thenReturn(testUser);

        // When
        ResponseEntity<User> response = userController.getUserById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_WithInvalidId_ShouldThrowException() {
        // Given
        when(userService.getUserById(999L))
                .thenThrow(new ResourceNotFoundException("User not found with userId:999"));

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userController.getUserById(999L);
        });
        verify(userService).getUserById(999L);
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        // Given
        doNothing().when(userService).deleteUserById(1L);

        // When
        ResponseEntity<Void> response = userController.deleteUserById(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUserById(1L);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        // Given
        User updatedUser = new User("Updated Name", "updated@example.com");
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(updatedUser);

        // When
        ResponseEntity<User> response = userController.updateUser(1L, updatedUser);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
        verify(userService).updateUser(1L, updatedUser);
    }

    @Test
    void getAllUsersByNamePrefix_ShouldReturnFilteredUsers() {
        // Given
        List<User> filteredUsers = Arrays.asList(testUser);
        when(userService.getAllUsersByNamePrefix("John")).thenReturn(filteredUsers);

        // When
        ResponseEntity<List<User>> response = userController.getAllUserByNamePrefix("John");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<User> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals(testUser, responseBody.get(0));
        verify(userService).getAllUsersByNamePrefix("John");
    }
}
