package com.babisamas.movierama.controller;

import com.babisamas.movierama.dto.UserRegistrationDTO;
import com.babisamas.movierama.security.CustomUserDetailsService;
import com.babisamas.movierama.security.JwtUtil;
import com.babisamas.movierama.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private UserRegistrationDTO createUserRegistrationDTO(String username, String email, String password, String firstname, String lastname) {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setFirstname(firstname);
        dto.setLastname(lastname);
        return dto;
    }

    @Test
    @WithMockUser(username = "testuser")
    void testRegisterUserWithValidData() throws Exception {
        UserRegistrationDTO validDTO = createUserRegistrationDTO("ValidUser", "valid@example.com", "ValidPass123@", "John", "Doe");

        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(validDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testRegisterUserWithShortUsername() throws Exception {
        UserRegistrationDTO dto = createUserRegistrationDTO("User", "valid@example.com", "ValidPass123@", "John", "Doe");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testRegisterUserWithLongUsername() throws Exception {
        String longUsername = String.join("", Collections.nCopies(51, "a"));
        UserRegistrationDTO dto = createUserRegistrationDTO(longUsername, "valid@example.com", "ValidPass123@", "John", "Doe");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testRegisterUserWithInvalidEmail() throws Exception {
        UserRegistrationDTO dto = createUserRegistrationDTO("ValidUser", "invalidEmail", "ValidPass123@", "John", "Doe");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testRegisterUserWithBlankFirstname() throws Exception {
        UserRegistrationDTO dto = createUserRegistrationDTO("ValidUser", "valid@example.com", "ValidPass123@", "", "Doe");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testRegisterUserWithLongFirstname() throws Exception {
        String longFirstname = String.join("", Collections.nCopies(101, "a"));
        UserRegistrationDTO dto = createUserRegistrationDTO("ValidUser", "valid@example.com", "ValidPass123@", longFirstname, "Doe");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testRegisterUserWithBlankLastname() throws Exception {
        UserRegistrationDTO dto = createUserRegistrationDTO("ValidUser", "valid@example.com", "ValidPass123@", "John", "");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testRegisterUserWithLongLastname() throws Exception {
        String longLastname = String.join("", Collections.nCopies(101, "a"));
        UserRegistrationDTO dto = createUserRegistrationDTO("ValidUser", "valid@example.com", "ValidPass123@", "John", longLastname);
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            " ",
            "",
            "short",
            "nouppercase123",
            "NOLOWERCASE123",
            "NoDigits!!",
            "NoSpecialChar123",
            "Contains Spaces 123!",
            "ValidPass123@",
    })
    @WithMockUser(username = "testuser")
    void testRegisterUserWithInvalidPasswordPattern(String password) throws Exception {
        UserRegistrationDTO dto = createUserRegistrationDTO("ValidUser", "valid@example.com", password, "John", "Doe");
        mockMvc.perform(MockMvcRequestBuilders.post("/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(password.equals("ValidPass123@") ? status().isOk() : status().isBadRequest());
    }
}
