package com.CinephileLog.dto;

import com.CinephileLog.domain.Playlist;

import java.util.List;

public class PlaylistWithMoviesResponse {
    private Long playlistId;
    private String name;
    private String description;
    private List<PlaylistMovieResponse> movies;

    public PlaylistWithMoviesResponse(Playlist playlist, List<PlaylistMovieResponse> movies) {
        this.playlistId = playlist.getPlaylistId();
        this.name = playlist.getName();
        this.description = playlist.getDescription();
        this.movies = movies;
    }

    public List<PlaylistMovieResponse> getMovies() {
        return movies;
    }
}


