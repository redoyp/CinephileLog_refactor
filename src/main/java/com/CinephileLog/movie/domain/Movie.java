package com.CinephileLog.movie.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie implements Serializable {
    @Id
    @Column(name = "movie_id")
    private Long id;

    private String title;
    private String titleOriginal;

    private LocalDate releaseDate;
    private String posterUrl;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating;

    private String synopsis;
    private String synopsisOriginal;

    private String director;
    private String directorOriginal;

    private String cast;
    private String castOriginal;

    @Column(name = "genres")
    private String genres;
}
