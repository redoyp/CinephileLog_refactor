package com.CinephileLog.review.service;

import com.CinephileLog.domain.User;
import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.movie.repository.MovieRepository;
import com.CinephileLog.repository.UserRepository;
import com.CinephileLog.review.domain.Review;
import com.CinephileLog.review.dto.ReviewRequest;
import com.CinephileLog.review.dto.ReviewRequestUpdate;
import com.CinephileLog.review.dto.ReviewResponse;
import com.CinephileLog.review.repository.ReviewRepository;
import com.CinephileLog.service.GradeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final GradeService gradeService;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository,
                         MovieRepository movieRepository, GradeService gradeService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.gradeService = gradeService;
    }

    // 리뷰 생성(로그인 한 유저만)
    @Transactional
    public ReviewResponse createReview(Long movieId, ReviewRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("영화 없음"));

        if (reviewRepository.existsByUser_UserIdAndMovie_idAndBlindedFalse(userId, movieId)) {
            throw new IllegalArgumentException("이미 해당 영화에 대한 리뷰를 작성했습니다.");
        }

        validateRating(request.getRating());
        Review savedReview = reviewRepository.save(request.toEntity(user, movie));

        updateMovieRating(movie);

        boolean gradeUp = gradeService.updateGradeForUser(userId);

        return new ReviewResponse(savedReview, gradeUp);
    }


    // 특정 영화의 모든 리뷰 조회
    public List<ReviewResponse> getAllReviews(Long movieId) {
        movieRepository.findById(movieId)
                .orElseThrow(() -> new NoSuchElementException("해당 하는 영화 없음"));

        List<Review> reviews = reviewRepository.findAllByMovie_Id(movieId);

        return reviews.stream()
                .map(ReviewResponse::new)
                .toList();
    }

    // 특정 유저가 작성한 리뷰들 조회
    public List<ReviewResponse> getUserReviews(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저 없음"));

        List<Review> reviews = reviewRepository.findAllByUser_UserId(userId);

        return reviews.stream()
                .map(ReviewResponse::new)
                .toList();
    }

    // 특정 영화, 유저에 해당하는 리뷰 조회
    public ReviewResponse getReview(Long userId, Long movieId) {
        movieRepository.findById(movieId)
                .orElseThrow(() -> new NoSuchElementException("해당 하는 영화 없음"));

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저 없음"));

        Review review = reviewRepository.findByUser_UserIdAndMovie_id(userId, movieId);

        return new ReviewResponse(review);
    }

    // 특정 영화에서 유저 닉네임으로 작성한 리뷰 조회
    public ReviewResponse getReviewByNickname(Long movieId, String nickname) {
        movieRepository.findById(movieId)
                .orElseThrow(() -> new NoSuchElementException("해당 영화 없음"));

        User user = userRepository.findByNickname(nickname);
        if(user == null) {
            throw new NoSuchElementException("해당 닉네임의 유저가 없습니다");
        }

        Review review = reviewRepository.findByMovie_IdAndUser_Nickname(movieId, nickname);

        return new ReviewResponse(review);
    }

    // 유저 닉네임으로 된 모든 리뷰 조회
    public List<ReviewResponse> getAllReviewsWithNickname(String nickname) {
        List<Review> reviews = reviewRepository.findAllByUser_Nickname(nickname);

        return reviews.stream()
                .map(ReviewResponse::new)
                .toList();
    }

    // reviewId로 리뷰 조회
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 id의 리뷰 없음"));

        return new ReviewResponse(review);
    }

    // 해당하는 id의 리뷰 삭제(admin)
    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    // 리뷰 삭제(로그인 한 유저)
    public void deleteReviewByUser(Long movieId, Long userId) {
        Review review = reviewRepository.findByUser_UserIdAndMovie_id(userId, movieId);

        if(review == null) {
            throw new NoSuchElementException("해당하는 리뷰 없음");
        }

        review.setRating(BigDecimal.ZERO);
        Movie movie = review.getMovie();
        updateMovieRating(movie);

        reviewRepository.delete(review);
    }

    // 리뷰 수정(로그인 한 유저)
    @Transactional
    public ReviewResponse updateReview(Long movieId, ReviewRequestUpdate request, Long userId) {
        Review review = reviewRepository.findByUser_UserIdAndMovie_id(userId, movieId);

        if(review == null) {
            throw new NoSuchElementException("해당하는 리뷰 없음");
        }
        validateRating(request.getRating());

        review.setContent(request.getContent());
        review.setRating(request.getRating());

        Movie movie = review.getMovie();
        updateMovieRating(movie);

        return new ReviewResponse(review);
    }

    // movie 평점에 모든 리뷰들의 평점을 업로드
    private void updateMovieRating(Movie movie) {
        List<Review> reviews = reviewRepository.findAllByMovie_Id(movie.getId());

        if(reviews.isEmpty()) {
            movie.setRating(BigDecimal.ZERO);
        } else {
            BigDecimal averageRating = reviews.stream()
                    .map(Review::getRating)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP);

            movie.setRating(averageRating.setScale(1, RoundingMode.HALF_UP));
        }

        log.info("영화 평점 업데이트: {}", movie.getRating());

        movieRepository.save(movie);
    }

    // 평점 유효성 검사
    private void validateRating(BigDecimal rating) {
        if(rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(BigDecimal.TEN) > 0) {
            throw new IllegalArgumentException("평점은 0.0에서 10.0 사이여야 함");
        }
    }

    // 모든 리뷰 조회 (관리자 페이지용)
    public List<ReviewResponse> findAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(ReviewResponse::new)
                .toList();
    }

    // 닉네임, 리뷰 내용, 영화 제목으로 검색 (관리자 페이지용)
    public List<ReviewResponse> searchReviewsAdmin(String keyword) {
        List<Review> reviews = reviewRepository.searchReviews(keyword);
        return reviews.stream()
                .map(ReviewResponse::new)
                .toList();
    }

    public List<Long> findTop3MovieIdsByReview() {
        return reviewRepository.findTop3MovieIdsByReview();
    }

    // 리뷰 블라인드 처리 (관리자페이지용)
    public void blindReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰가 존재하지 않습니다."));
        review.setBlinded(true); // 블라인드 상태로 변경
        reviewRepository.save(review);
    }

    // 이전에 작성한 리뷰를 블라인드한 후에 유저가 동일한 영화에 리뷰를 다시 또 작성하면
    // 이전에 블라인드 되었던 리뷰를 블라인드 해제할 수 없고,
    // 만약에 블라인드 후 유저가 그 이후에 리뷰를 작성한 적이 없는데 관리자가 블라인드 해제하려고 하면 해제가 된다 => 결론: 리뷰는 항상 영화 당 하나로 유지!

    // After a user's review is hidden, if that same user writes another review for the same movie,
    // the previously hidden review cannot be unhidden.
    // However, if a user's review is hidden, and they haven't written any new reviews for that movie since,
    // then an administrator can unhide the original review.
    // The main idea is that we only want to keep one active review per user for each movie at any time.
    @Transactional
    public void unblindReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰가 존재하지 않습니다."));

        // 해당 유저가 해당 영화에 이미 (blinded = false) 리뷰를 작성했는지 확인
        if (reviewRepository.existsByUser_UserIdAndMovie_idAndBlindedFalse(review.getUser().getUserId(), review.getMovie().getId())) {
            throw new IllegalStateException("해당 영화에 이미 리뷰가 있는 회원의 블라인드된 리뷰는 해제할 수 없습니다.");
        }

        review.setBlinded(false);
        reviewRepository.save(review);
    }
    public List<ReviewResponse> findEditorPickReviews() {
        List<Review> reviews = reviewRepository.findEditorPickReviews(4L); // HOTDOG 이상
        return reviews.stream()
                .map(review -> new ReviewResponse(review, true))
                .toList();
    }
}
