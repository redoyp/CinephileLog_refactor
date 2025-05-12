package com.CinephileLog.review.controller;

import com.CinephileLog.review.dto.ReviewRequest;
import com.CinephileLog.review.dto.ReviewRequestUpdate;
import com.CinephileLog.review.dto.ReviewResponse;
import com.CinephileLog.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ReviewController {
    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/movies/{movieId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long movieId,
                                                       @Valid @RequestBody ReviewRequest request,
                                                       @AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        Long userId = user.getAttribute("userId");
        ReviewResponse response = reviewService.createReview(movieId, request, userId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/movies/{movieId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getAllReviews(@PathVariable Long movieId) {
        List<ReviewResponse> reviews = reviewService.getAllReviews(movieId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getAllReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponse> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/movies/{movieId}/reviews/{userId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long movieId, @PathVariable Long userId) {
        ReviewResponse review = reviewService.getReview(movieId, userId);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/movies/{movieId}/reviews/nickname")
    public ResponseEntity<ReviewResponse> getReviewByNickname(@PathVariable Long movieId, @RequestParam String nickname) {
        ReviewResponse review = reviewService.getReviewByNickname(movieId, nickname);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/movies/{movieId}/reviews")
    public ResponseEntity<ReviewResponse> deleteReview(@PathVariable Long movieId,
                                                       @AuthenticationPrincipal OAuth2User user) {
        if(user == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = user.getAttribute("userId");

        reviewService.deleteReviewByUser(movieId, userId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/movies/{movieId}/reviews")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long movieId,
                                                       @Valid @RequestBody ReviewRequestUpdate updateRequest,
                                                       @AuthenticationPrincipal OAuth2User user) {
        if(user == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = user.getAttribute("userId");

        ReviewResponse review = reviewService.updateReview(movieId, updateRequest, userId);

        return ResponseEntity.ok(review);
    }
}
