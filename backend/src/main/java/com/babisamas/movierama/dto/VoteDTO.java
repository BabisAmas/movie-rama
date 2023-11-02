package com.babisamas.movierama.dto;

import com.babisamas.movierama.model.VoteType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoteDTO {
    private Long id;
    private VoteType type;
    private Long userId;
    private Long movieId;
}