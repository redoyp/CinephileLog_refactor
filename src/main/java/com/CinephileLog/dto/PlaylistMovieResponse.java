package com.CinephileLog.dto;

import com.CinephileLog.movie.domain.Movie;
import lombok.Getter;

@Getter
public class PlaylistMovieResponse {

    private Long movieId;
    private String title;
    private String posterUrl;

    public PlaylistMovieResponse(Movie movie) {
        this.movieId = movie.getId();
        this.title = movie.getTitle();
        this.posterUrl = movie.getPosterUrl();
    }
}

