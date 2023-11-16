package com.babisamas.movierama.repository;

import com.babisamas.movierama.dto.MovieDTO;
import com.babisamas.movierama.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findAll(Pageable pageable);

    @Query("SELECT new com.babisamas.movierama.dto.MovieDTO(" +
            "m, " +
            "mc.likeCount, " +
            "mc.hateCount, " +
            "CONCAT(u.firstname, ' ', u.lastname), " +
            "CASE WHEN u.id = :userId THEN true ELSE false END, " +
            "v.type) " +
            "FROM Movie m " +
            "JOIN m.user u " +
            "LEFT JOIN m.movieCounter mc " +
            "LEFT JOIN m.votes v ON v.user.id = :userId AND v.movie = m " +
            "GROUP BY m, u.firstname, u.lastname, mc.likeCount, mc.hateCount, v.type")
    Page<MovieDTO> findMoviesWithLikesHatesAndUser(Pageable pageable, Long userId);

}