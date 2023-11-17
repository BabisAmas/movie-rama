package com.babisamas.movierama.config;

import com.babisamas.movierama.exception.CustomRetryLimitExceededException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ApiError {
        private HttpStatus status;
        private String message;
        private List<String> errors;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError handleUsernameNotFound(UsernameNotFoundException ex) {
        return new ApiError(HttpStatus.NOT_FOUND, "Username not found", Collections.singletonList(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ApiError handleBadCredentials() {
        return new ApiError(HttpStatus.UNAUTHORIZED,"Authentication failed", Collections.singletonList("Invalid username or password.")
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Validation error",
                errors
        );
    }

    @ExceptionHandler(CustomRetryLimitExceededException.class)
    public ResponseEntity<String> handleCustomException(CustomRetryLimitExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}