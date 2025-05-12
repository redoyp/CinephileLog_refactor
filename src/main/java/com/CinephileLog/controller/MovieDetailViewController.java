package com.CinephileLog.controller;

import com.CinephileLog.domain.Grade;
import com.CinephileLog.movie.dto.MovieResponse;
import com.CinephileLog.movie.service.MovieService;
import com.CinephileLog.review.dto.ReviewResponse;
import com.CinephileLog.review.service.ReviewLikeService;
import com.CinephileLog.review.service.ReviewService;
import com.CinephileLog.service.GradeService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MovieDetailViewController {
    private MovieService movieService;
    private ReviewService reviewService;
    private GradeService gradeService;
    private ReviewLikeService reviewLikeService;

    public MovieDetailViewController(MovieService movieService, ReviewService reviewService, GradeService gradeService, ReviewLikeService reviewLikeService) {
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.gradeService = gradeService;
        this.reviewLikeService = reviewLikeService;
    }

    @GetMapping("/movieDetail/{movieId}")
    public String myProfileView(@PathVariable Long movieId,
                                @AuthenticationPrincipal OAuth2User user, Model model, HttpSession session) {

        Long userId = user.getAttribute("userId");

        //For header fragment
        model.addAttribute("userId", userId);
        model.addAttribute("nickname", session.getAttribute("nickname").toString());
        model.addAttribute("gradeName", session.getAttribute("gradeName").toString());
        model.addAttribute("roleName", session.getAttribute("roleName").toString());
        model.addAttribute("showMenu", true);

        MovieResponse movie = movieService.getMovieById(movieId);
        model.addAttribute("movie", movie);
        model.addAttribute("movieId", movieId);

        List<ReviewResponse> reviews = reviewService.getAllReviews(movieId);
        model.addAttribute("reviews", reviews);

        Set<Long> likedReviewIds = reviewLikeService.findReviewLikedByUser(userId)
                .stream()
                .map(like -> like.getReview().getId())
                .collect(Collectors.toSet());
        model.addAttribute("likedReviewIds", likedReviewIds);

        Grade grade = gradeService.getGradeByUserId(userId);
        model.addAttribute("grade", grade);
        model.addAttribute("userGradeId", grade.getGradeId());

        boolean hasUserReviewed = reviews.stream()
                .anyMatch(review -> review.getUser().getUserId().equals(userId));
        model.addAttribute("hasUserReviewed", hasUserReviewed);

        ReviewResponse userReview = reviews.stream()
                .filter(review -> review.getUser().getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        model.addAttribute("userReview", userReview);

        return "movieDetail";
    }
}
