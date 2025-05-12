package com.CinephileLog.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlaylistMovieId implements Serializable {

    private Long playlistId;
    private Long movieId;
}
