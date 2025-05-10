package com.CinephileLog.review.dto;

import com.CinephileLog.dto.UserResponse;
import com.CinephileLog.movie.dto.MovieResponse;
import com.CinephileLog.review.domain.Review;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Setter 추가

@Getter
@Setter // Setter 추가
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private MovieResponse movie;
    private UserResponse user;
    private BigDecimal rating;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long likeCount;
    private boolean blinded;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.movie = new MovieResponse(review.getMovie());
        this.user = new UserResponse(review.getUser());
        this.rating = review.getRating();
        this.content = review.getContent();
        this.createdDate = review.getCreatedDate();
        this.updatedDate = review.getUpdatedDate();
        this.likeCount = review.getLikeCount();
        this.blinded = review.isBlinded();
    }

    // 공통으로 반올림된 정수 값 저장
    public int getRoundedRating() {
        int rounded = rating.setScale(0, RoundingMode.HALF_UP).intValue();
        return rounded;
    }

    // 꽉 채운 별
    public int getFullStars() {
        int full = getRoundedRating() / 2;
        return full;
    }

    // 반 별
    public boolean isHalfStar() {
        boolean half = getRoundedRating() % 2 == 1;
        return half;
    }

    // 빈 별
    public int getEmptyStars() {
        int fullStars = getFullStars();
        boolean halfStar = isHalfStar();

        int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);

        return emptyStars;
    }
}
