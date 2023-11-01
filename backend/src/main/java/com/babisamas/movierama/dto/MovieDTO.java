package com.babisamas.movierama.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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
}