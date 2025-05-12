package com.CinephileLog.review.dto;

import com.CinephileLog.dto.UserResponse;
import com.CinephileLog.movie.dto.MovieResponse;
import com.CinephileLog.review.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse  {
    private Long id;
    private MovieResponse movie;
    private UserResponse user;
    private BigDecimal rating;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime displayDate;
    private Long likeCount;
    private boolean blinded;
    private boolean gradeUp;

    // 기존 생성자 (gradeUp은 false로 기본값 처리됨)
    public ReviewResponse(Review review) {
        this(review, false); // 기본적으로 gradeUp = false
    }

    // 새 생성자 (등급 상승 여부 전달 가능)
    public ReviewResponse(Review review, boolean gradeUp) {
        this.id = review.getId();
        this.movie = new MovieResponse(review.getMovie());
        this.user = new UserResponse(review.getUser());
        this.rating = review.getRating();
        this.content = review.getContent();
        this.createdDate = review.getCreatedDate();
        this.updatedDate = review.getUpdatedDate();
        this.likeCount = review.getLikeCount();
        this.blinded = review.isBlinded();
        this.gradeUp = gradeUp;
        this.displayDate = (updatedDate != null && !updatedDate.equals(createdDate)) ? updatedDate : createdDate;
    }

    public int getRoundedRating() {
        return rating.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    public int getFullStars() {
        return getRoundedRating() / 2;
    }

    public boolean isHalfStar() {
        return getRoundedRating() % 2 == 1;
    }

    public int getEmptyStars() {
        return 5 - getFullStars() - (isHalfStar() ? 1 : 0);
    }
    public String getMoviePoster() {
        return movie != null ? movie.getPosterUrl() : null;
    }

    public String getMovieTitle() {
        return movie != null ? movie.getTitle() : null;
    }


    public boolean isGradeUp() {
        return gradeUp;
    }
}
