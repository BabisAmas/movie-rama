package com.babisamas.movierama.repository;

import com.babisamas.movierama.dto.MovieDTO;
import com.babisamas.movierama.model.Movie;
import com.babisamas.movierama.model.VoteType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findAll(Pageable pageable);

    @Query("SELECT new com.babisamas.movierama.dto.MovieDTO(m, " +
            "(SELECT COUNT(v1) FROM Vote v1 WHERE v1.movie = m AND v1.type = com.babisamas.movierama.model.VoteType.LIKE), " +
            "(SELECT COUNT(v2) FROM Vote v2 WHERE v2.movie = m AND v2.type = com.babisamas.movierama.model.VoteType.HATE), " +
            "CONCAT(u.firstname, ' ', u.lastname), " +
            "CASE WHEN u.id = :userId THEN true ELSE false END, " +
            "CASE WHEN :userId IS NOT NULL THEN " +
            "(SELECT v3.type FROM Vote v3 WHERE v3.movie = m AND v3.user.id = :userId) " +
            "ELSE null END) " +
            "FROM Movie m " +
            "JOIN m.user u " +
            "GROUP BY m, u.firstname, u.lastname " +
            "ORDER BY " +
            "(SELECT COUNT(v) FROM Vote v WHERE v.movie = m AND v.type = :voteType) DESC, " +
            "m.dateAdded DESC")
    Page<MovieDTO> findMoviesWithLikesHatesAndUserSortedByVoteType(VoteType voteType, Pageable pageable, Long userId);

    @Query("SELECT new com.babisamas.movierama.dto.MovieDTO(m, " +
            "(SELECT COUNT(v1) FROM Vote v1 WHERE v1.movie = m AND v1.type = com.babisamas.movierama.model.VoteType.LIKE), " +
            "(SELECT COUNT(v2) FROM Vote v2 WHERE v2.movie = m AND v2.type = com.babisamas.movierama.model.VoteType.HATE), " +
            "CONCAT(u.firstname, ' ', u.lastname), " +
            "CASE WHEN u.id = :userId THEN true ELSE false END, " +
            "CASE WHEN :userId IS NOT NULL THEN " +
            "(SELECT v3.type FROM Vote v3 WHERE v3.movie = m AND v3.user.id = :userId) " +
            "ELSE null END) " +
            "FROM Movie m " +
            "JOIN m.user u " +
            "GROUP BY m, u.firstname, u.lastname")
    Page<MovieDTO> findMoviesWithLikesHatesAndUserSortedByAddedDate(Pageable pageable, Long userId);
}