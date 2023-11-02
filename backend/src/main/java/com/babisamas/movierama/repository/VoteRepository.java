package com.babisamas.movierama.repository;

import com.babisamas.movierama.model.Movie;
import com.babisamas.movierama.model.User;
import com.babisamas.movierama.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserAndMovie(User user, Movie movie);
}