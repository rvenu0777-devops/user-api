
package com.dhanyait.userapi.controller;
import com.dhanyait.userapi.dto.UserDto;
import com.dhanyait.userapi.exception.UserNotFoundException;
import com.dhanyait.userapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto("John", "Doe", "john.doe@example.com");
        userDto.setId(1L);
        userDto.setCreatedAt(LocalDateTime.now());
        userDto.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getUser_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(userDto);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void getUser_WhenUserDoesNotExist_ShouldReturn404() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenThrow(new UserNotFoundException("User not found"));

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUsers_ShouldReturnAllUsers() throws Exception {
        // Given
        List<UserDto> users = Arrays.asList(userDto, new UserDto("Jane", "Smith", "jane@example.com"));
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createUser_WhenValidUser_ShouldCreateUser() throws Exception {
        // Given
        UserDto newUserDto = new UserDto("John", "Doe", "john.doe@example.com");
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void createUser_WhenInvalidUser_ShouldReturn400() throws Exception {
        // Given
        UserDto invalidUser = new UserDto("", "", "invalid-email");

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }
}