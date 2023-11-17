package com.babisamas.movierama.service;

import com.babisamas.movierama.exception.CustomRetryLimitExceededException;
import com.babisamas.movierama.model.Movie;
import com.babisamas.movierama.model.User;
import com.babisamas.movierama.model.Vote;
import com.babisamas.movierama.model.VoteType;
import com.babisamas.movierama.repository.MovieRepository;
import com.babisamas.movierama.repository.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserService userService;
    private final MovieRepository movieRepository;

    @Autowired
    public VoteService(
            VoteRepository voteRepository,
            UserService userService,
            MovieRepository movieRepository) {
        this.voteRepository = voteRepository;
        this.userService = userService;
        this.movieRepository = movieRepository;
    }

    @Transactional
    public void castVote(Long movieId, VoteType voteType) {
        int retryCount = 0;
        int maxRetries = 3;

        while (true) {
            try {
                User currentUser = userService.getLoggedInUser();
                Movie movie = movieRepository.findById(movieId)
                        .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + movieId));

                Optional<Vote> existingVote = voteRepository.findByUserAndMovie(currentUser, movie);

                if (existingVote.isPresent()) {
                    updateExistingVote(existingVote.get(), voteType, movie);
                } else {
                    createAndSaveNewVote(movie, currentUser, voteType);
                }

                break;
            } catch (OptimisticLockException ole) {
                if (++retryCount >= maxRetries) {
                    throw new CustomRetryLimitExceededException("Your vote could not be processed due to concurrent updates. Please try again.");
                }
                sleepBriefly();
            }
        }
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void createAndSaveNewVote(Movie movie, User user, VoteType voteType) {
        Vote newVote = new Vote();
        newVote.setMovie(movie);
        newVote.setUser(user);
        newVote.setType(voteType);
        voteRepository.save(newVote);

        incrementVoteCounter(movie, voteType);
    }

    private void updateExistingVote(Vote vote, VoteType newType, Movie movie) {
        if (vote.getType() != newType) {
            decrementVoteCounter(movie, vote.getType());
            incrementVoteCounter(movie, newType);
            vote.setType(newType);
            voteRepository.save(vote);
        }
    }

    private void incrementVoteCounter(Movie movie, VoteType voteType) {
        if (voteType == VoteType.LIKE) {
            movie.getMovieCounter().incrementLike();
        } else if (voteType == VoteType.HATE) {
            movie.getMovieCounter().incrementHate();
        }
    }

    private void decrementVoteCounter(Movie movie, VoteType voteType) {
        if (voteType == VoteType.LIKE) {
            movie.getMovieCounter().decrementLike();
        } else if (voteType == VoteType.HATE) {
            movie.getMovieCounter().decrementHate();
        }
    }

    public void removeVote(Long movieId) {
        User currentUser = userService.getLoggedInUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + movieId));

        voteRepository.findByUserAndMovie(currentUser, movie)
                .ifPresent(voteRepository::delete);
    }
}
