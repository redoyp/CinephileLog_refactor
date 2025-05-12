package com.CinephileLog.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true) // Boolean adult 필드 무시
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TmdbMovie {
    private Long id;
    private String title;
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

    private int rating;

    private Credits credits; // director, cast 를 가져오기 위해. TMDB에서는 credits로 따로 관리

//    public List<String> getGenreNames(List<Genre> genres) {
//        return genres.stream()
//                .filter(g -> genreIds.contains(g.getId()))
//                .map(Genre::getName)
//                .toList();
//    }

//    public String getDirector() {
//        if (credits != null && credits.getCrew() != null) {
//            return credits.getCrew().stream()
//                    .filter(person -> "Director".equals(person.getJob()))
//                    .map(Person::getName)
//                    .collect(Collectors.joining(", "));
//        }
//        return "감독 없음";
//    }

//    public String getCast() {
//        if (credits != null && credits.getCast() != null) {
//            return credits.getCast().stream()
//                    .limit(5)
//                    .map(Person::getName)
//                    .collect(Collectors.joining(", "));
//        }
//        return "배우 없음";
//    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
        this.rating = (int) Math.round(voteAverage);
    }
}
