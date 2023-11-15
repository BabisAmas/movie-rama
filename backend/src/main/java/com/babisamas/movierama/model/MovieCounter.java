package com.babisamas.movierama.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MovieCounter {

    @Id
    private Long id;

    @Min(0)
    private int likeCount = 0;

    @Min(0)
    private int hateCount = 0;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Movie movie;

    public MovieCounter(Movie movie) {
        this.movie = movie;
    }

    public void incrementLike() {
        this.likeCount++;
    }

    public void decrementLike() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementHate() {
        this.hateCount++;
    }

    public void decrementHate() {
        if (this.hateCount > 0) {
            this.hateCount--;
        }
    }
}