package com.babisamas.movierama.security;

import com.babisamas.movierama.dto.AuthenticationRequestDTO;
import com.babisamas.movierama.dto.UserRegistrationDTO;
import com.babisamas.movierama.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private void createTestUser() {
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setUsername("testuser");
        registrationDTO.setEmail("testuser@example.com");
        registrationDTO.setPassword("testpass");
        registrationDTO.setFirstname("Test");
        registrationDTO.setLastname("User");
        userService.createUser(registrationDTO);
    }

    @BeforeEach
    public void setup() {
        createTestUser();
    }

    @Test
    void whenValidCredentials_thenReturnsJwt() throws Exception {
        AuthenticationRequestDTO authRequest = new AuthenticationRequestDTO("testuser", "testpass");

        mockMvc.perform(post("/authenticate")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwt").exists());
    }

    @Test
    void whenInvalidCredentials_thenUnauthorized() throws Exception {
        AuthenticationRequestDTO authRequest = new AuthenticationRequestDTO("testuser", "wrongpassword");

        mockMvc.perform(post("/authenticate")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }
}
