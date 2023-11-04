package com.babisamas.movierama.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "Ln2u9LqWbXaQG49T34QeYbZUk6oF5w7RjH3sVc0PzT9k2MjI8wA4mNlP6tVe8bG1");
        ReflectionTestUtils.setField(jwtUtil, "expirationInMillis", 86400000);
    }

    @Test
    void testGenerateTokenReturnsNonNullToken() {
        String username = "user";

        String token = jwtUtil.generateToken(username, Collections.emptyList());

        assertNotNull(token, "The generated token should not be null");
    }

    @Test
    void testGetUsernameFromTokenReturnsCorrectUsername() {
        String username = "user";

        String token = jwtUtil.generateToken(username, Collections.emptyList());
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        assertEquals(username, extractedUsername, "The extracted username should match the one in the token");
    }

    @Test
    void testIsTokenValidReturnsTrueForValidToken() {
        String username = "user";

        String token = jwtUtil.generateToken(username, Collections.emptyList());
        boolean isValid = jwtUtil.isTokenValid(token);

        assertTrue(isValid, "The token should be valid");
    }

    @Test
    void testIsTokenValidReturnsFalseForInvalidToken() {
        String invalidToken = "invalid.token";

        boolean isValid = jwtUtil.isTokenValid(invalidToken);

        assertFalse(isValid, "The token should not be valid");
    }

    @Test
    void testIsTokenValidReturnsFalseForExpiredToken() {
        ReflectionTestUtils.setField(jwtUtil, "expirationInMillis", -3600000); // -1 hour
        String username = "user";

        String expiredToken = jwtUtil.generateToken(username, Collections.emptyList());
        boolean isValid = jwtUtil.isTokenValid(expiredToken);

        assertFalse(isValid, "The token should be expired and not valid");
    }
}


