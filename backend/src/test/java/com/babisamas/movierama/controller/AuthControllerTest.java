package com.babisamas.movierama.controller;

import com.babisamas.movierama.dto.AuthenticationRequestDTO;
import com.babisamas.movierama.security.CustomUserDetailsService;
import com.babisamas.movierama.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtTokenUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void whenValidCredentials_thenReturnsJwt() throws Exception {
        AuthenticationRequestDTO authenticationRequestDTO = new AuthenticationRequestDTO("user", "password");
        UserDetails userDetails = new User("user", "password", Collections.emptyList());
        String expectedToken = "token";

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(anyString(), any())).thenReturn(expectedToken);

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(expectedToken));
    }

    @Test
    void whenAuthenticateWithInvalidCredentials_thenThrowsException() {
        String invalidUsername = "wrong_user";
        String invalidPassword = "wrong_password";
        AuthenticationRequestDTO authenticationRequest = new AuthenticationRequestDTO(invalidUsername, invalidPassword);

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authController.createAuthenticationToken(authenticationRequest));
    }

    @Test
    void whenUserDoesNotExist_thenThrowsException() {
        String username = "non_existing_user";
        when(userDetailsService.loadUserByUsername(username))
                .thenThrow(new UsernameNotFoundException("User not found"));

        AuthenticationRequestDTO authenticationRequestDTO = new AuthenticationRequestDTO();
        authenticationRequestDTO.setUsername(username);
        authenticationRequestDTO.setPassword("anyPassword");

        assertThrows(UsernameNotFoundException.class, () -> authController.createAuthenticationToken(authenticationRequestDTO));
    }
}