package com.babisamas.movierama.dto;

import com.babisamas.movierama.model.Movie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MovieDTO {
    private Long id;
    @Size(min = 1, max = 255)
    @NotBlank
    private String title;
    @Size(max = 1000)
    private String description;
    private LocalDateTime dateAdded;
    private String userName;
    private int numberOfLikes;
    private int numberOfHates;

    public MovieDTO(Movie movie, long numberOfLikes, long numberOfHates, String userName) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.description = movie.getDescription();
        this.dateAdded = movie.getDateAdded();
        this.userName = userName;
        this.numberOfLikes = (int) numberOfLikes;
        this.numberOfHates = (int) numberOfHates;
    }
}