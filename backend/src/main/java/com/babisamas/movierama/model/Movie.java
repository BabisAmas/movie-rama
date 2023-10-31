package com.babisamas.movierama.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1, max = 255)
    @NotBlank
    private String title;

    @Size(max = 1000)
    private String description;

    @CreationTimestamp
    private LocalDateTime dateAdded;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();
}
