package com.babisamas.movierama.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Size(min = 5, max = 50)
    @NotBlank
    private String username;

    @Size(min = 6)
    @NotBlank
    private String password;

    @Size(max = 100)
    @NotBlank
    private String firstname;

    @Size(max = 100)
    @NotBlank
    private String lastname;

    @Column(unique = true)
    @Email
    @NotBlank
    private String email;

    @CreationTimestamp
    private LocalDateTime dateJoined;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Movie> movies = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

    public String getFullName() {
        return firstname + " " + lastname;
    }
}
