package com.CinephileLog.movie.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {
    private String title;
    private String titleOriginal;
    private LocalDate releaseDate;
    private String posterUrl;
    private String synopsis;
    private String synopsisOriginal;
    private String director;
    private String directorOriginal;
    private String cast;
    private String castOriginal;
    private String genres;
}
