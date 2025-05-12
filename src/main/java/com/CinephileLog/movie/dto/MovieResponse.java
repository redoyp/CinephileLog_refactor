package com.CinephileLog.movie.dto;

import com.CinephileLog.movie.domain.Movie;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {
    private Long id;
    private String title;
    private String titleOriginal;
    private LocalDate releaseDate;
    private String posterUrl;
    private BigDecimal rating;
    private String synopsis;
    private String synopsisOriginal;
    private String director;
    private String directorOriginal;
    private String cast;
    private String castOriginal;
    private String genres; // 장르 문자열

    public MovieResponse(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.titleOriginal = movie.getTitleOriginal();
        this.releaseDate = movie.getReleaseDate();
        this.posterUrl = movie.getPosterUrl();
        this.rating = movie.getRating();
        this.synopsis = movie.getSynopsis();
        this.synopsisOriginal = movie.getSynopsisOriginal();
        this.director = movie.getDirector();
        this.directorOriginal = movie.getDirectorOriginal();
        this.cast = movie.getCast();
        this.castOriginal = movie.getCastOriginal();
        this.genres = movie.getGenres();
    }
}
