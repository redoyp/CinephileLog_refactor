package com.CinephileLog.movie.repository;

import com.CinephileLog.movie.domain.Movie;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @EntityGraph(attributePaths = {"genres"}) // 'genres' 컬렉션을 즉시 로딩
    Optional<Movie> findById(Long movieId);
    List<Movie> findByTitleContainingIgnoreCase(String keyword);
    List<Movie> findByIdIn(List<Long> ids);
    Slice<Movie> findAllBy(Pageable pageable);
    @Query("SELECT m.id FROM Movie m")
    List<Long> findAllIds();

}
