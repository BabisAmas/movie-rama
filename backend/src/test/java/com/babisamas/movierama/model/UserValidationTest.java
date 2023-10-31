package com.babisamas.movierama.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void close() {
        validatorFactory.close();
    }

    @Test
    void whenUserValid_thenNoConstraintViolations() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword("validPassword123#");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenUsernameTooShort_thenConstraintViolation() {
        User user = new User();
        user.setUsername("usr"); // shorter than minimum size
        user.setPassword("validPassword123#");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenUsernameTooLong_thenConstraintViolation() {
        User user = new User();
        user.setUsername("a".repeat(51)); // longer than maximum size
        user.setPassword("validPassword123#");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenUsernameBlank_thenConstraintViolation() {
        User user = new User();
        user.setUsername(""); // empty
        user.setPassword("validPassword123#");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }

    @Test
    void whenEmailInvalid_thenConstraintViolation() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword("validPassword123#");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenEmailBlank_thenConstraintViolation() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword("validPassword123#");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenPasswordTooShort_thenConstraintViolation() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword("short"); // shorter than minimum
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenPasswordBlank_thenConstraintViolation() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword(null); // blank
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenFirstNameBlank_thenConstraintViolation() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword("validPassword123#");
        user.setFirstname(""); // blank
        user.setLastname("Doe");
        user.setEmail("john@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenFirstNameTooLong_thenConstraintViolation() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword("validPassword123#");
        user.setFirstname("a".repeat(101)); // longer than maximum size
        user.setLastname("Doe");
        user.setEmail("john@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenLastNameBlank_thenConstraintViolation() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword("validPassword123#");
        user.setFirstname("John");
        user.setLastname(""); // blank
        user.setEmail("john@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenLastNameTooLong_thenConstraintViolation() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword("validPassword123#");
        user.setFirstname("John");
        user.setLastname("a".repeat(101)); // longer than maximum size
        user.setEmail("john@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenMultipleViolations_thenAllViolationsDetected() {
        User user = new User();
        user.setUsername("usr"); // too short
        user.setPassword("short"); // too short
        user.setFirstname(""); // blank
        user.setLastname(""); // blank
        user.setEmail("invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(5, violations.size()); // Expecting 5 violations
    }
}