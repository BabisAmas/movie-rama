package com.babisamas.movierama.service;

import com.babisamas.movierama.exception.CustomRetryLimitExceededException;
import com.babisamas.movierama.exception.UserCannotVoteOnOwnMovieException;
import com.babisamas.movierama.model.*;
import com.babisamas.movierama.repository.MovieRepository;
import com.babisamas.movierama.repository.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private UserService userService;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private VoteService voteService;

    private User mockUser;
    private Movie mockMovie;
    private Long movieId;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        User mockUser2 = new User();
        mockUser2.setId(2L);

        movieId = 1L;
        mockMovie = new Movie();
        mockMovie.setId(movieId);
        mockMovie.setMovieCounter(new MovieCounter());
        mockMovie.setUser(mockUser2);
    }

    @Test
    void testCastVote_ShouldSaveNewLikeVoteWhenNoExistingVote() {
        VoteType voteType = VoteType.LIKE;

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));
        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(voteRepository.findByUserAndMovie(any(), eq(mockMovie))).thenReturn(Optional.empty());

        ArgumentCaptor<Vote> voteCaptor = ArgumentCaptor.forClass(Vote.class);
        voteService.castVote(movieId, voteType);
        verify(voteRepository).save(voteCaptor.capture());

        Vote capturedVote = voteCaptor.getValue();
        assertNotNull(capturedVote, "Captured vote should not be null");
        assertEquals(voteType, capturedVote.getType(), "Vote type should be LIKE");
        assertEquals(mockUser, capturedVote.getUser(), "Vote should belong to the correct user");
        assertEquals(mockMovie, capturedVote.getMovie(), "Vote should be associated with the correct movie");
    }

    @Test
    void testCastVote_ShouldThrowCustomRetryLimitExceededOnMaxRetries() {
        VoteType voteType = VoteType.LIKE;

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));
        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(voteRepository.findByUserAndMovie(any(), any())).thenReturn(Optional.empty());

        doThrow(OptimisticLockException.class)
                .when(voteRepository)
                .save(any(Vote.class));

        assertThrows(CustomRetryLimitExceededException.class, () -> voteService.castVote(movieId, voteType));

        verify(voteRepository, times(3)).save(any(Vote.class));
    }

    @Test
    void testCastVote_SuccessfulRetry() {
        VoteType voteType = VoteType.LIKE;
        AtomicInteger invocationCount = new AtomicInteger(0);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));
        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(voteRepository.findByUserAndMovie(any(), any())).thenReturn(Optional.empty());

        doAnswer(invocation -> {
            if (invocationCount.incrementAndGet() == 1) {
                throw new OptimisticLockException();
            }
            return null;
        }).when(voteRepository).save(any(Vote.class));

        voteService.castVote(movieId, voteType);

        verify(voteRepository, times(2)).save(any(Vote.class));
    }

    @Test
    void testCastVote_UpdateExistingVote() {
        VoteType oldVoteType = VoteType.LIKE;
        VoteType newVoteType = VoteType.HATE;

        Vote existingVote = new Vote();
        existingVote.setType(oldVoteType);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));
        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(voteRepository.findByUserAndMovie(any(), any())).thenReturn(Optional.of(existingVote));

        ArgumentCaptor<Vote> voteCaptor = ArgumentCaptor.forClass(Vote.class);
        voteService.castVote(movieId, newVoteType);
        verify(voteRepository).save(voteCaptor.capture());

        Vote updatedVote = voteCaptor.getValue();
        assertEquals(newVoteType, updatedVote.getType(), "Vote type should be updated to HATE");
    }

    @Test
    void testCastVote_MovieNotFound() {
        VoteType voteType = VoteType.LIKE;

        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> voteService.castVote(movieId, voteType));
    }

    @Test
    void testVoteCounterIncrementAndDecrement() {
        mockMovie.getMovieCounter().setLikeCount(1);

        VoteType oldVoteType = VoteType.LIKE;
        VoteType newVoteType = VoteType.HATE;

        Vote existingVote = new Vote();
        existingVote.setType(oldVoteType);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));
        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(voteRepository.findByUserAndMovie(any(), any())).thenReturn(Optional.of(existingVote));

        voteService.castVote(movieId, newVoteType);

        assertEquals(1, mockMovie.getMovieCounter().getHateCount(), "Hate count should be incremented");
        assertEquals(0, mockMovie.getMovieCounter().getLikeCount(), "Like count should be decremented");
    }

    @Test
    void testCastVote_NoActionOnSameVoteType() {
        VoteType voteType = VoteType.LIKE;

        Vote existingVote = new Vote();
        existingVote.setType(voteType);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));
        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(voteRepository.findByUserAndMovie(any(), any())).thenReturn(Optional.of(existingVote));

        voteService.castVote(movieId, voteType);

        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void testCastVote_DatabaseFailure() {
        VoteType voteType = VoteType.LIKE;

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));
        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(voteRepository.findByUserAndMovie(any(), any())).thenReturn(Optional.empty());

        doThrow(RuntimeException.class)
                .when(voteRepository)
                .save(any(Vote.class));

        assertThrows(RuntimeException.class, () -> voteService.castVote(movieId, voteType));
    }

    @Test
    void testCastVote_UserNotFound() {
        VoteType voteType = VoteType.LIKE;

        when(userService.getLoggedInUser()).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> voteService.castVote(movieId, voteType));
    }

    @Test
    void testCastVote_UserVotingOnOwnMovie() {
        mockMovie.setUser(mockUser);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));
        when(userService.getLoggedInUser()).thenReturn(mockUser);

        assertThrows(UserCannotVoteOnOwnMovieException.class, () -> voteService.castVote(movieId, VoteType.LIKE));
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void testRemoveVote_ExistingVote() {
        Vote mockVote = new Vote();

        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));
        when(voteRepository.findByUserAndMovie(mockUser, mockMovie)).thenReturn(Optional.of(mockVote));

        voteService.removeVote(movieId);

        verify(voteRepository, times(1)).delete(mockVote);
        verifyNoMoreInteractions(voteRepository);
    }

    @Test
    void testRemoveVote_NonExistingVote() {
        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(mockMovie));
        when(voteRepository.findByUserAndMovie(mockUser, mockMovie)).thenReturn(Optional.empty());

        voteService.removeVote(movieId);

        verify(voteRepository, never()).delete(any(Vote.class));
        verifyNoMoreInteractions(voteRepository);
    }

    @Test
    void testRemoveVote_InvalidMovieId() {
        when(userService.getLoggedInUser()).thenReturn(mockUser);
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> voteService.removeVote(movieId));
    }

    @Test
    void testRemoveVote_NoAuthenticatedUser() {
        when(userService.getLoggedInUser()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> voteService.removeVote(movieId));
    }
}