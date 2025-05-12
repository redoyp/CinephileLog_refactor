package com.CinephileLog.review.service;

import com.CinephileLog.domain.User;
import com.CinephileLog.repository.UserRepository;
import com.CinephileLog.review.domain.Review;
import com.CinephileLog.review.domain.ReviewLike;
import com.CinephileLog.review.repository.ReviewLikeRepository;
import com.CinephileLog.review.repository.ReviewRepository;
import com.CinephileLog.service.GradeService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReviewLikeService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final GradeService gradeService;

    public ReviewLikeService(ReviewLikeRepository reviewLikeRepository, ReviewRepository reviewRepository,
                             UserRepository userRepository, GradeService gradeService) {
        this.reviewLikeRepository = reviewLikeRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.gradeService = gradeService;
    }

    @Transactional
    public boolean reviewLike(Long userId, Review review) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없음"));

        Optional<ReviewLike> existingLike = reviewLikeRepository.findByUserAndReview(user, review);

        if (existingLike.isPresent()) {
            reviewLikeRepository.delete(existingLike.get());
            review.setLikeCount(review.getLikeCount() - 1);
            reviewRepository.save(review);
            return false;
        } else {
            ReviewLike like = new ReviewLike(user, review);
            reviewLikeRepository.save(like);
            review.setLikeCount(review.getLikeCount() + 1);
            reviewRepository.save(review);
            gradeService.updateGradeForUser(userId);
            return true;
        }
    }


    public List<ReviewLike> findReviewLikedByUser(Long userId) {
        return reviewLikeRepository.findByUserUserId(userId);
    }
}
