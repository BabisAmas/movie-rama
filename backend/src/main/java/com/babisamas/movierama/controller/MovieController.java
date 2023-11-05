package com.babisamas.movierama.controller;

import com.babisamas.movierama.dto.MovieDTO;
import com.babisamas.movierama.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public ResponseEntity<MovieDTO> addMovie(@RequestBody @Valid MovieDTO movie) {
        MovieDTO newMovie = movieService.addMovie(movie);
        return new ResponseEntity<>(newMovie, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<MovieDTO>> listMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "addedDateDesc") String sort) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MovieDTO> movieDTOs = movieService.getSortedMovies(pageable, sort);
        return new ResponseEntity<>(movieDTOs, HttpStatus.OK);
    }
}
