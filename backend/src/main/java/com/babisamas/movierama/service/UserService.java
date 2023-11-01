package com.babisamas.movierama.service;

import com.babisamas.movierama.dto.UserRegistrationDTO;
import com.babisamas.movierama.model.User;
import com.babisamas.movierama.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserRegistrationDTO registrationDTO) {
        User newUser = new User();
        newUser.setUsername(registrationDTO.getUsername());
        newUser.setEmail(registrationDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        newUser.setFirstname(registrationDTO.getFirstname());
        newUser.setLastname(registrationDTO.getLastname());

        return userRepository.save(newUser);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("User not found");
        }

        String username = authentication.getName();
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
