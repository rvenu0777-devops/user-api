package com.dhanyait.userapi;


import com.dhanyait.userapi.dto.UserDto;
import com.dhanyait.userapi.entity.User;
import com.dhanyait.userapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@Testcontainers
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class UserApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Alternative with TestContainers (commented out as using H2 for simplicity)
    /*
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    */

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_IntegrationTest() throws Exception {
        // Given
        UserDto newUser = new UserDto("John", "Doe", "john.doe@example.com");

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists());

        // Verify in database
        assertThat(userRepository.count()).isEqualTo(1);
        User savedUser = userRepository.findByEmail("john.doe@example.com").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getFirstName()).isEqualTo("John");
    }

    @Test
    void getUser_IntegrationTest() throws Exception {
        // Given
        User user = new User("Jane", "Smith", "jane.smith@example.com");
        User savedUser = userRepository.save(user);

        // When & Then
        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));
    }

    @Test
    void getUsers_IntegrationTest() throws Exception {
        // Given
        userRepository.save(new User("John", "Doe", "john@example.com"));
        userRepository.save(new User("Jane", "Smith", "jane@example.com"));

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").exists())
                .andExpect(jsonPath("$[1].firstName").exists());
    }

    @Test
    void updateUser_IntegrationTest() throws Exception {
        // Given
        User user = userRepository.save(new User("John", "Doe", "john@example.com"));
        UserDto updateDto = new UserDto("John", "Smith", "john.smith@example.com");

        // When & Then
        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("john.smith@example.com"));

        // Verify in database
        User updatedUser = userRepository.findById(user.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getLastName()).isEqualTo("Smith");
    }

    @Test
    void deleteUser_IntegrationTest() throws Exception {
        // Given
        User user = userRepository.save(new User("John", "Doe", "john@example.com"));

        // When & Then
        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    void createUser_DuplicateEmail_ShouldReturn409() throws Exception {
        // Given
        userRepository.save(new User("John", "Doe", "john@example.com"));
        UserDto duplicateUser = new UserDto("Jane", "Smith", "john@example.com");

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already exists with email: john@example.com"));
    }

    @Test
    void getUser_NonExistentId_ShouldReturn404() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 999"));
    }

    @Test
    void createUser_InvalidData_ShouldReturn400() throws Exception {
        // Given
        UserDto invalidUser = new UserDto("", "", "invalid-email");

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
}
