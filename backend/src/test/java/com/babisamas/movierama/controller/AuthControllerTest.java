package com.babisamas.movierama.controller;

import com.babisamas.movierama.config.SecurityConfig;
import com.babisamas.movierama.dto.AuthenticationRequestDTO;
import com.babisamas.movierama.security.CustomUserDetailsService;
import com.babisamas.movierama.security.JwtAuthenticationEntryPoint;
import com.babisamas.movierama.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtTokenUtil;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(expectedToken));
    }

    @Test
    void whenRequestMissingFields_thenReturnsBadRequest() throws Exception {
        AuthenticationRequestDTO authenticationRequestDTO = new AuthenticationRequestDTO("", "");

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenRequestHasInvalidDataFormat_thenReturnsBadRequest() throws Exception {
        String invalidJson = "invalid JSON format";

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenBlankPassword_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"validUser\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*]", hasItem(containsString("Password cannot be blank"))));
    }

    @Test
    void whenBlankUsername_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"validPassword\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*]", hasItem(containsString("Username cannot be blank"))));
    }

    @Test
    void whenBadCredentials_thenReturnsUnauthorized() throws Exception {
        AuthenticationRequestDTO authenticationRequestDTO = new AuthenticationRequestDTO("user", "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication failed"));
    }

    @Test
    void whenUserNotFound_thenReturnsServerErrorOrCorrectErrorCode() throws Exception {
        AuthenticationRequestDTO authenticationRequestDTO = new AuthenticationRequestDTO("nonexistentUser", "password");

        when(userDetailsService.loadUserByUsername("nonexistentUser"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isNotFound());
    }
}