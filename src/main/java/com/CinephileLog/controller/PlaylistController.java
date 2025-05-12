package com.CinephileLog.controller;

import com.CinephileLog.domain.Playlist;
import com.CinephileLog.dto.PlaylistMovieResponse;
import com.CinephileLog.dto.PlaylistResponse;
import com.CinephileLog.dto.PlaylistWithMoviesResponse;
import com.CinephileLog.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public PlaylistResponse createPlaylist(@RequestBody Map<String, String> body,
                                           @AuthenticationPrincipal OAuth2User user) {
        Long userId = ((Number) user.getAttribute("userId")).longValue();
        String name = body.get("name");
        String description = body.getOrDefault("description", null);

        Playlist playlist = playlistService.createPlaylist(userId, name, description);
        return new PlaylistResponse(playlist.getPlaylistId(), playlist.getName(), playlist.getDescription());
    }

    @GetMapping
    public List<PlaylistResponse> getUserPlaylists(@AuthenticationPrincipal OAuth2User user) {
        Long userId = ((Number) user.getAttribute("userId")).longValue();
        List<Playlist> playlists = playlistService.getUserPlaylists(userId);

        return playlists.stream()
                .map(p -> new PlaylistResponse(p.getPlaylistId(), p.getName(), p.getDescription()))
                .toList();
    }

    @GetMapping("/{playlistId}")
    public PlaylistWithMoviesResponse getPlaylist(@PathVariable Long playlistId) {
        Playlist playlist = playlistService.getPlaylistWithMovies(playlistId);
        List<PlaylistMovieResponse> movies = playlist.getPlaylistMovies().stream()
                .map(pm -> new PlaylistMovieResponse(pm.getMovie()))
                .toList();
        return new PlaylistWithMoviesResponse(playlist, movies);
    }

    @PostMapping("/{playlistId}/movies")
    public void addMovieToPlaylist(@PathVariable Long playlistId,
                                   @RequestBody Map<String, Long> body) {
        Long movieId = body.get("movieId");
        playlistService.addMovieToPlaylist(playlistId, movieId);
    }

    @DeleteMapping("/{playlistId}/movies/{movieId}")
    public void removeMovieFromPlaylist(@PathVariable Long playlistId,
                                        @PathVariable Long movieId) {
        playlistService.removeMovieFromPlaylist(playlistId, movieId);
    }

    @DeleteMapping("/{playlistId}")
    public void deletePlaylist(@PathVariable Long playlistId) {
        playlistService.deletePlaylist(playlistId);
    }
}
