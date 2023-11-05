package com.babisamas.movierama.service;

import com.babisamas.movierama.dto.MovieDTO;
import com.babisamas.movierama.model.Movie;
import com.babisamas.movierama.model.Vote;
import com.babisamas.movierama.model.VoteType;
import com.babisamas.movierama.repository.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Movie getMovieById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with id: " + movieId));
    }

    public Page<MovieDTO> getSortedMovies(Pageable pageable, String sortType) {
        Page<Movie> moviesPage;
        switch (sortType) {
            case "mostLikes":
                return movieRepository.findMoviesWithLikesHatesAndUserSortedByVoteType(VoteType.LIKE, pageable);
            case "mostHates":
                return movieRepository.findMoviesWithLikesHatesAndUserSortedByVoteType(VoteType.HATE, pageable);
            case "addedDateAsc":
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("dateAdded").ascending());
                break;
            case "addedDateDesc":
                pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("dateAdded").descending());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sortType);
        }

        moviesPage = movieRepository.findAll(pageable);
        List<MovieDTO> movieDTOs = moviesPage.getContent().stream()
                .map(this::convertToDto)
                .toList();

        return new PageImpl<>(movieDTOs, pageable, moviesPage.getTotalElements());
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
