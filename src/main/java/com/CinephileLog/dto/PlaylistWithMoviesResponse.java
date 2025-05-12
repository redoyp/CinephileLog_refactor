package com.CinephileLog.dto;

import com.CinephileLog.domain.Playlist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class PlaylistWithMoviesResponse {
    private Long playlistId;
    private String name;
    private String description;
    @Getter
    private List<PlaylistMovieResponse> movies;

    public PlaylistWithMoviesResponse(Playlist playlist, List<PlaylistMovieResponse> movies) {
        this.playlistId = playlist.getPlaylistId();
        this.name = playlist.getName();
        this.description = playlist.getDescription();
        this.movies = movies;
    }

}


