package com.babisamas.movierama.controller;

import com.babisamas.movierama.model.VoteType;
import com.babisamas.movierama.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/vote")
    public ResponseEntity<Void> vote(@RequestParam Long movieId, @RequestParam VoteType voteType) {
        voteService.castVote(movieId, voteType);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/vote")
    public ResponseEntity<Void> removeVote(@RequestParam Long movieId) {
        voteService.removeVote(movieId);
        return ResponseEntity.ok().build();
    }
}