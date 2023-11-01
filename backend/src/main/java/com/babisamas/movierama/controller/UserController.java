package com.babisamas.movierama.controller;

import com.babisamas.movierama.dto.UserRegistrationDTO;
import com.babisamas.movierama.model.User;
import com.babisamas.movierama.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody @Valid UserRegistrationDTO registrationDTO) {
        User newUser = userService.createUser(registrationDTO);
        return ResponseEntity.ok(newUser);
    }
}