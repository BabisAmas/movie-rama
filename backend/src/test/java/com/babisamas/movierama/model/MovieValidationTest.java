package com.babisamas.movierama.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovieValidationTest {

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
    void whenTitleWhitespace_thenConstraintViolation() {
        Movie movie = new Movie();
        movie.setTitle(" "); // Whitespace title
        movie.setDescription("A valid description");

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenTitleIsNull_thenConstraintViolation() {
        Movie movie = new Movie();
        movie.setTitle(null); // Null title
        movie.setDescription("A valid description");

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenTitleTooLong_thenConstraintViolation() {
        Movie movie = new Movie();
        movie.setTitle("a".repeat(256)); // Longer than max size
        movie.setDescription("A valid description");

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenDescriptionTooLong_thenConstraintViolation() {
        Movie movie = new Movie();
        movie.setTitle("Valid Title");
        movie.setDescription("a".repeat(1001)); // Longer than max size

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void whenDescriptionIsNull_thenNoConstraintViolation() {
        Movie movie = new Movie();
        movie.setTitle("Valid Title");
        movie.setDescription(null); // Null description is allowed

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenValidMovie_thenNoConstraintViolation() {
        Movie movie = new Movie();
        movie.setTitle("Valid Title");
        movie.setDescription("A valid description");

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenAllFieldsInvalid_thenMultipleConstraintViolations() {
        Movie movie = new Movie();
        movie.setTitle(null); // Invalid title
        movie.setDescription("a".repeat(1001)); // Invalid description

        Set<ConstraintViolation<Movie>> violations = validator.validate(movie);
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
    }
}