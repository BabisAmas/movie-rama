package com.babisamas.movierama.exception;

public class UserCannotVoteOnOwnMovieException extends RuntimeException {
    public UserCannotVoteOnOwnMovieException(String message) {
        super(message);
    }
}