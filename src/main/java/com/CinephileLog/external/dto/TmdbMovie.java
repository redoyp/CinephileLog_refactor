package com.CinephileLog.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TmdbMovie {

    private Long id;

    private String title;

    @JsonProperty("original_title")
    private String titleOriginal;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("poster_path")
    private String posterUrl;

    @JsonProperty("overview")
    private String synopsis;

    @JsonProperty("genre_ids")
    private List<Long> genreIds;

    @JsonProperty("vote_average")
    private double voteAverage;

    @JsonProperty("vote_count")
    private int voteCount;

    private int rating;

    private Credits credits;

    private String starRatingVisual;

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
        this.rating = (int) Math.round(voteAverage);
        this.starRatingVisual = generateStarRatingVisual(voteAverage, voteCount);
    }

    private String generateStarRatingVisual(double voteAverage, int voteCount) {
        double stars = voteAverage / 2.0;
        int full = (int) stars;
        boolean half = (stars - full) >= 0.5;
        int empty = 5 - full - (half ? 1 : 0);

        return "★".repeat(full) + (half ? "½" : "") + "☆".repeat(empty) + " (" + voteCount + ")";
    }
}
