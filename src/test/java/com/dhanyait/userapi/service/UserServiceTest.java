package com.dhanyait.userapi.service;

import static org.junit.jupiter.api.Assertions.*;

// UserServiceTest.java


import com.dhanyait.userapi.dto.UserDto;
import com.dhanyait.userapi.entity.User;
import com.dhanyait.userapi.exception.UserAlreadyExistsException;
import com.dhanyait.userapi.exception.UserNotFoundException;
import com.dhanyait.userapi.mapper.UserMapper;
import com.dhanyait.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User("John", "Doe", "john.doe@example.com");
        user.setId(1L);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userDto = new UserDto("John", "Doe", "john.doe@example.com");
        userDto.setId(1L);
        userDto.setCreatedAt(LocalDateTime.now());
        userDto.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserDto() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        UserDto result = userService.getUserById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(userRepository).findById(1L);
        verify(userMapper).toDto(user);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with id: 1");
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        List<User> users = Arrays.asList(user, new User("Jane", "Smith", "jane@example.com"));
        List<UserDto> userDtos = Arrays.asList(userDto, new UserDto("Jane", "Smith", "jane@example.com"));

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDtoList(users)).thenReturn(userDtos);

        // When
        List<UserDto> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(2);
        verify(userRepository).findAll();
        verify(userMapper).toDtoList(users);
    }

    @Test
    void createUser_WhenEmailDoesNotExist_ShouldCreateUser() {
        // Given
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // When
        UserDto result = userService.createUser(userDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository).save(user);
    }

    @Test
    void createUser_WhenEmailAlreadyExists_ShouldThrowException() {
        // Given
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(userDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("User already exists with email: john.doe@example.com");
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with id: 1");
    }
}
