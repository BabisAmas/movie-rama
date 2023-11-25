package com.babisamas.movierama.controller;

import com.babisamas.movierama.dto.MovieDTO;
import com.babisamas.movierama.security.CustomUserDetailsService;
import com.babisamas.movierama.security.JwtUtil;
import com.babisamas.movierama.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "testuser")
    void testAddMovie() throws Exception {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("Test Movie");

        given(movieService.addMovie(any(MovieDTO.class))).willReturn(movieDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(movieDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(movieDTO.getTitle()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testAddMovieWithInvalidData() throws Exception {
        MovieDTO invalidMovieDTO = new MovieDTO();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidMovieDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddMovieUnauthorized() throws Exception {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("Test Movie");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(movieDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testAddMovieWithBlankTitle() throws Exception {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("");
        movieDTO.setDescription("Some description");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(movieDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testAddMovieWithTitleExceedingMaxLength() throws Exception {
        String longTitle = String.join("", Collections.nCopies(256, "a"));
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle(longTitle);
        movieDTO.setDescription("Some description");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(movieDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testAddMovieWithDescriptionExceedingMaxLength() throws Exception {
        String longDescription = String.join("", Collections.nCopies(1001, "a"));
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("Valid Title");
        movieDTO.setDescription(longDescription);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(movieDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testListMovies() throws Exception {
        Page<MovieDTO> page = new PageImpl<>(Collections.singletonList(new MovieDTO()));
        given(movieService.getSortedMovies(any(Pageable.class), any(String.class))).willReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/movies")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "addedDateDesc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void testListMoviesUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movies")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
