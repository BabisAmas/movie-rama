package com.babisamas.movierama.service;

import com.babisamas.movierama.dto.VoteDTO;
import com.babisamas.movierama.model.Movie;
import com.babisamas.movierama.model.User;
import com.babisamas.movierama.model.Vote;
import com.babisamas.movierama.model.VoteType;
import com.babisamas.movierama.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserService userService;
    private final MovieService movieService;

    @Autowired
    public VoteService(VoteRepository voteRepository, UserService userService, MovieService movieService) {
        this.voteRepository = voteRepository;
        this.userService = userService;
        this.movieService = movieService;
    }

    public VoteDTO vote(Long movieId, VoteType voteType) {
        User currentUser = userService.getLoggedInUser();
        Movie movie = movieService.getMovieById(movieId);

        if (movie.getUser().equals(currentUser)) {
            throw new IllegalArgumentException("Users cannot vote for their own movies");
        }

        Optional<Vote> existingVote = voteRepository.findByUserAndMovie(currentUser, movie);
        Vote savedVote = existingVote.map(vote -> updateVote(vote, voteType))
                .orElseGet(() -> createNewVote(movie, currentUser, voteType));
        return convertToDto(savedVote);
    }

    public void removeVote(Long movieId) {
        User currentUser = userService.getLoggedInUser();
        Movie movie = movieService.getMovieById(movieId);

        voteRepository.findByUserAndMovie(currentUser, movie)
                .ifPresent(voteRepository::delete);
    }

    private Vote createNewVote(Movie movie, User user, VoteType voteType) {
        Vote newVote = new Vote();
        newVote.setMovie(movie);
        newVote.setUser(user);
        newVote.setType(voteType);
        return voteRepository.save(newVote);
    }

    private Vote updateVote(Vote existingVote, VoteType newType) {
        if (!existingVote.getType().equals(newType)) {
            existingVote.setType(newType);
            voteRepository.save(existingVote);
        }
        return existingVote;
    }

    public VoteDTO convertToDto(Vote vote) {
        VoteDTO dto = new VoteDTO();
        dto.setId(vote.getId());
        dto.setType(vote.getType());
        dto.setUserId(vote.getUser().getId());
        dto.setMovieId(vote.getMovie().getId());
        return dto;
    }
}
