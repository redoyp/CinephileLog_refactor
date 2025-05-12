package com.CinephileLog.repository;

import com.CinephileLog.domain.PlaylistMovie;
import com.CinephileLog.domain.PlaylistMovieId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistMovieRepository extends JpaRepository<PlaylistMovie, PlaylistMovieId> {
    void deleteByPlaylist_PlaylistIdAndMovie_Id(Long playlistId, Long movieId);
    boolean existsByIdPlaylistIdAndIdMovieId(Long playlistId, Long movieId);
}
