package com.babisamas.movierama.service;

import com.babisamas.movierama.dto.MovieDTO;
import com.babisamas.movierama.model.Movie;
import com.babisamas.movierama.model.Vote;
import com.babisamas.movierama.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final UserService userService;

    @Autowired
    public MovieService(MovieRepository movieRepository, UserService userService) {
        this.movieRepository = movieRepository;
        this.userService = userService;
    }

    public MovieDTO addMovie(MovieDTO movieDTO) {
        Movie movie = convertToEntity(movieDTO);
        Movie savedMovie = movieRepository.save(movie);
        return convertToDto(savedMovie);
    }

    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private Movie convertToEntity(MovieDTO movieDTO) {
        Movie movie = new Movie();
        movie.setTitle(movieDTO.getTitle());
        movie.setDescription(movieDTO.getDescription());
        movie.setUser(userService.getLoggedInUser());
        return movie;
    }

    private MovieDTO convertToDto(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setDateAdded(movie.getDateAdded());
        dto.setUserName(movie.getUser().getFullName());
        dto.setNumberOfLikes((int) movie.getVotes().stream().filter(Vote::isLike).count());
        dto.setNumberOfHates((int) movie.getVotes().stream().filter(vote -> !vote.isLike()).count());
        return dto;
    }
}
