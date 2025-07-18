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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import  static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
    private UserRepository userRepository;
  @InjectMocks
  private UserService userService;

  private User testUser;

  private List<User> userList;
  @BeforeEach
  void setUp(){
      testUser = new User("John Doe", "john@spring.com");
      User user2 = new User("Jane Doe", "jane@spring.com");
      userList = Arrays.asList(testUser, user2);
  }

  @Test
    void createUser_ShouldReturnSavedUser(){
//Given test data (initial context) or mock behavior
     when(userRepository.save(any(User.class))).thenReturn(testUser);
 //  When : Perform action or method call
     User result = userService.createUser(testUser);
 //Then
      assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("John Doe");
    assertThat(result.getEmail()).isEqualTo("john@spring.com");
    verify(userRepository,times(1)).save(testUser);
  }

@Test
  void getUserById_WithValidUserId_ShouldReturnUser(){
      //Given
      Long userId = 23L;
      when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
      //When
      User result = userService.getUserById(userId);
      //Then
      assertThat(result).isNotNull();
      assertThat(result.getName()).isEqualTo("John Doe");
      assertThat(result.getEmail()).isEqualTo("john@spring.com");
      verify(userRepository,times(1)).findById(userId);
  }
  @
    Test
  void getUserById_WithInvalidUserId_ShouldThrowException(){
      //Given
      Long userId = 23L;
      when(userRepository.findById(userId)).thenReturn(Optional.empty());
        //When & Then
      assertThatThrownBy(()->userService.getUserById(userId))
              .isInstanceOf(ResourceNotFoundException.class)
              .hasMessageContaining("User not found with userId:"+ userId);
      verify(userRepository,times(1)).findById(userId);
  }


}