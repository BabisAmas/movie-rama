package com.babisamas.movierama.exception;

public class CustomRetryLimitExceededException extends RuntimeException {
    public CustomRetryLimitExceededException(String message) {
        super(message);
    }
}