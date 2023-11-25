package com.babisamas.movierama.controller;

import com.babisamas.movierama.model.VoteType;
import com.babisamas.movierama.security.CustomUserDetailsService;
import com.babisamas.movierama.security.JwtUtil;
import com.babisamas.movierama.service.VoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoteController.class)
@AutoConfigureMockMvc
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteService voteService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "testuser")
    void testSuccessfulVote() throws Exception {
        Long movieId = 1L;
        VoteType voteType = VoteType.LIKE;

        doNothing().when(voteService).castVote(movieId, voteType);

        mockMvc.perform(MockMvcRequestBuilders.post("/votes/vote")
                        .with(csrf())
                        .param("movieId", movieId.toString())
                        .param("voteType", voteType.toString()))
                .andExpect(status().isOk());

        verify(voteService, times(1)).castVote(movieId, voteType);
    }

    @Test
    void testVoteEndpointWithoutJwtToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/votes/vote")
                        .with(csrf())
                        .param("movieId", "1")
                        .param("voteType", "LIKE"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testVoteEndpointWithInvalidInput() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/votes/vote")
                        .with(csrf())
                        .param("movieId", "invalidId")
                        .param("voteType", "LIKE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testVoteEndpointWithMissingMovieId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/votes/vote")
                        .with(csrf())
                        .param("voteType", "LIKE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testVoteEndpointWithMissingVoteType() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/votes/vote")
                        .with(csrf())
                        .param("movieId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testSuccessfulVoteRemoval() throws Exception {
        Long movieId = 1L;

        doNothing().when(voteService).removeVote(movieId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/votes/vote")
                        .with(csrf())
                        .param("movieId", movieId.toString()))
                .andExpect(status().isOk());

        verify(voteService, times(1)).removeVote(movieId);
    }


    @Test
    void testRemoveVoteEndpointWithoutJwtToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/votes/vote")
                        .with(csrf())
                        .param("movieId", "1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testRemoveVoteEndpointWithInvalidInput() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/votes/vote")
                        .with(csrf())
                        .param("movieId", "invalidId"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testRemoveVoteEndpointWithMissingMovieId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/votes/vote")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
