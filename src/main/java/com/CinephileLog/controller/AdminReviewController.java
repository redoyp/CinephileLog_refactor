package com.CinephileLog.controller;

import com.CinephileLog.review.dto.ReviewResponse;
import com.CinephileLog.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin/reviews")       // 리뷰 관리
public class AdminReviewController {

    private final ReviewService reviewService;

    @Autowired
    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public String listReviews(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<ReviewResponse> reviews;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 검색어가 있는 경우 관리자용 검색 메서드 호출
            reviews = reviewService.searchReviewsAdmin(keyword);
        } else {
            // 검색어가 없는 경우 모든 리뷰 조회
            reviews = reviewService.findAllReviews();
        }
        model.addAttribute("reviews", reviews);
        model.addAttribute("keyword", keyword);
        return "admin/review/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/admin/reviews";
    }

    @GetMapping("/{id}")
    public String reviewDetail(@PathVariable Long id, @RequestParam(value = "keyword", required = false) String keyword, Model model) {
        ReviewResponse review = reviewService.getReviewById(id);
        model.addAttribute("review", review);
        model.addAttribute("keyword", keyword);
        return "admin/review/detail";
    }

    @PostMapping("/{reviewId}/blind")
    public ResponseEntity<String> blindReview(@PathVariable Long reviewId) {
        reviewService.blindReview(reviewId);
        return ResponseEntity.ok("리뷰가 블라인드 처리되었습니다.");
    }

    @PostMapping("/{reviewId}/unblind")
    public ResponseEntity<String> unblindReview(@PathVariable Long reviewId) {
        reviewService.unblindReview(reviewId);
        return ResponseEntity.ok("리뷰가 블라인드 해제되었습니다.");
    }
}