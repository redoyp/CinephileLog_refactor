package com.CinephileLog.controller;

import com.CinephileLog.external.TmdbClient;
import com.CinephileLog.external.dto.TmdbMovie;
import com.CinephileLog.movie.dto.MovieResponse;
import com.CinephileLog.movie.service.MovieService;
import com.CinephileLog.review.dto.ReviewResponse;
import com.CinephileLog.review.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class HomeViewController {
    private final MovieService movieService;
    private final ReviewService reviewService;
    private final TmdbClient tmdbClient;

    public HomeViewController(MovieService movieService, ReviewService reviewService, TmdbClient tmdbClient) {
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.tmdbClient = tmdbClient;
    }
    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homeView(@AuthenticationPrincipal OAuth2User user, Model model, HttpSession session) {
        if (user != null) {
            model.addAttribute("userId", user.getAttribute("userId"));
            model.addAttribute("nickname", session.getAttribute("nickname").toString());
            model.addAttribute("gradeName", session.getAttribute("gradeName").toString());
            model.addAttribute("roleName", session.getAttribute("roleName").toString());
        }

        // ✅ TMDB 인기 영화 3개
        List<TmdbMovie> moviesOfTopPopularity = tmdbClient.fetchMovies().stream()
                .limit(3)
                .toList();

        // ✅ TMDB 평점 영화 3개
        List<TmdbMovie> moviesOfTopRated = tmdbClient.fetchTopRatedMovies().stream()
                .limit(3)
                .toList();

        // ✅ 6개 영화 통합 리뷰 처리
        List<TmdbMovie> allSixMovies = new ArrayList<>();
        allSixMovies.addAll(moviesOfTopPopularity);
        allSixMovies.addAll(moviesOfTopRated);

        Map<Long, List<ReviewResponse>> reviewsMap = new HashMap<>();
        Map<Long, Integer> reviewCountMap = new HashMap<>();

        for (TmdbMovie movie : allSixMovies) {
            GetMovieReviews(movie.getId(), reviewsMap, reviewCountMap); // 그대로 유지
        }

        model.addAttribute("moviesOfTopPopularity", moviesOfTopPopularity);
        model.addAttribute("moviesOfTopRated", moviesOfTopRated);
        model.addAttribute("reviewsOfSixMovies", reviewsMap);
        model.addAttribute("reviewCountOfSixMovies", reviewCountMap);

        // ✅ 우리 DB 기준 리뷰 수 가장 많은 영화 3개
        List<Long> topReviewMovieList = reviewService.findTop3MovieIdsByReview();
        List<MovieResponse> moviesOfTopReview = topReviewMovieList.stream()
                .map(movieService::getMovieDetail)
                .toList();

        // ✅ 영화별 좋아요 수 많은 리뷰 1개
        Map<Long, ReviewResponse> bestReviewOfTopReview = new HashMap<>();
        Map<Long, Integer> reviewCountOfTopReview = new HashMap<>();

        for (Long movieId : topReviewMovieList) {
            try {
                List<ReviewResponse> allReviews = reviewService.getAllReviews(movieId);

                ReviewResponse mostLiked = allReviews.stream()
                        .max(Comparator.comparingLong(ReviewResponse::getLikeCount))
                        .orElse(null);

                bestReviewOfTopReview.put(movieId, mostLiked);
                reviewCountOfTopReview.put(movieId, allReviews.size());

            } catch (Exception e) {
                bestReviewOfTopReview.put(movieId, null);
                reviewCountOfTopReview.put(movieId, 0);
            }
        }

        model.addAttribute("moviesOfTopReview", moviesOfTopReview);
        model.addAttribute("bestReviewOfTopReview", bestReviewOfTopReview);
        model.addAttribute("reviewCountOfTopReview", reviewCountOfTopReview);

        return "index";
    }


    //Get sample movie reviews and total review count per movie
    private void GetMovieReviews(Long movieId,
                                 Map<Long, List<ReviewResponse>> reviewsMap,
                                 Map<Long, Integer> reviewCountMap) {
        try {
            List<ReviewResponse> allReviews = reviewService.getAllReviews(movieId);
            List<ReviewResponse> sampleReviews = allReviews.stream()
                                                .limit(5)
                                                .toList();

            reviewsMap.put(movieId, sampleReviews);
            reviewCountMap.put(movieId, allReviews.size());
        } catch (Exception e) {
            reviewsMap.put(movieId, Collections.emptyList());
            reviewCountMap.put(movieId, 0);
        }
    }

}
