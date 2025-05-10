package com.CinephileLog.review.dto;

import com.CinephileLog.domain.User;
import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.review.domain.Review;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private Long userId;

    @DecimalMin(value = "0.0", inclusive = true, message = "평점은 0.0 이상이어야 합니다.")
    @DecimalMax(value = "10.0", inclusive = true, message = "평점은 10.0 이하여야 합니다.")
    @Digits(integer = 2, fraction = 1, message = "소수점 한 자리까지만 입력 가능합니다.")
    private BigDecimal rating;

    private String content;

    public Review toEntity(User user, Movie movie) {
        return new Review(
                null, user, movie, rating, content,
                LocalDateTime.now(), null,
                0L, false
        );
    }
}
