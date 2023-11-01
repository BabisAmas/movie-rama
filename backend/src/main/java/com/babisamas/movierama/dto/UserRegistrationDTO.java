package com.babisamas.movierama.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDTO {

    @Size(min = 5, max = 50)
    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6)
    @NotBlank
    private String password;

    @Size(max = 100)
    @NotBlank
    private String firstname;

    @Size(max = 100)
    @NotBlank
    private String lastname;
}