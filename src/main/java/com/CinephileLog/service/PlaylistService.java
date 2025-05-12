package com.CinephileLog.service;

import com.CinephileLog.domain.Playlist;
import com.CinephileLog.domain.PlaylistMovie;
import com.CinephileLog.domain.PlaylistMovieId;
import com.CinephileLog.domain.User;
import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.movie.repository.MovieRepository;
import com.CinephileLog.repository.PlaylistMovieRepository;
import com.CinephileLog.repository.PlaylistRepository;
import com.CinephileLog.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistMovieRepository playlistMovieRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public Playlist createPlaylist(Long userId, String name, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Playlist playlist = Playlist.builder()
                .user(user)
                .name(name)
                .description(description)
                .build();

        return playlistRepository.save(playlist);
    }

    public List<Playlist> getUserPlaylists(Long userId) {
        return playlistRepository.findAllByUserUserId(userId);
    }

    public Playlist getPlaylistWithMovies(Long playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new EntityNotFoundException("Playlist not found"));
    }

    public void addMovieToPlaylist(Long playlistId, Long movieId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie not found"));

        boolean exists = playlistMovieRepository.existsByIdPlaylistIdAndIdMovieId(playlistId, movieId);
        if (exists) {
            throw new IllegalStateException("이미 존재하는 영화입니다.");
        }

        PlaylistMovie playlistMovie = PlaylistMovie.builder()
                .id(new PlaylistMovieId(playlistId, movieId))
                .playlist(playlist)
                .movie(movie)
                .build();

        playlistMovieRepository.save(playlistMovie);
    }

    public void removeMovieFromPlaylist(Long playlistId, Long movieId) {
        playlistMovieRepository.deleteByPlaylist_PlaylistIdAndMovie_Id(playlistId, movieId);
    }

    public void deletePlaylist(Long playlistId) {
        playlistRepository.deleteById(playlistId);
    }
}
