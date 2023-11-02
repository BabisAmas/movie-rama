package com.babisamas.movierama.controller;

import com.babisamas.movierama.dto.VoteDTO;
import com.babisamas.movierama.model.VoteType;
import com.babisamas.movierama.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/vote")
    public ResponseEntity<VoteDTO> vote(@RequestParam Long movieId, @RequestParam VoteType voteType) {
        VoteDTO vote = voteService.vote(movieId, voteType);
        return ResponseEntity.ok(vote);
    }
}