package com.CinephileLog.review.repository;

import com.CinephileLog.review.domain.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.movie.id = :movieId AND r.blinded = false")
    List<Review> findAllByMovie_Id(Long movieId);

    @Query("SELECT r FROM Review r WHERE r.user.userId = :userId AND r.blinded = false")
    List<Review> findAllByUser_UserId(Long userId);

    @Query("SELECT r FROM Review r WHERE r.user.userId = :userId AND r.movie.id = :movieId AND r.blinded = false")
    Review findByUser_UserIdAndMovie_id(Long userId, Long movieId);

    @Query("SELECT r FROM Review r WHERE r.user.nickname = :nickname AND r.movie.id = :movieId AND r.blinded = false")
    Review findByMovie_IdAndUser_Nickname(Long movieId, String nickname);

    @Query("SELECT r FROM Review r WHERE r.user.nickname = :nickname AND r.blinded = false")
    List<Review> findAllByUser_Nickname(String nickname);

    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.user.userId = :userId AND r.movie.id = :movieId AND r.blinded = false")
    boolean existsByUser_UserIdAndMovie_idAndBlindedFalse(Long userId, Long movieId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.userId = :userId AND r.blinded = false")
    int countByUserUserId(@Param("userId") Long userId);


    // 키워드로 검색 (관리자 페이지용)
    @Query("SELECT r FROM Review r " +
            "WHERE (:keyword IS NULL OR LOWER(r.user.nickname) LIKE LOWER(concat('%', :keyword, '%'))) " +
            "OR (:keyword IS NULL OR LOWER(r.content) LIKE LOWER(concat('%', :keyword, '%'))) " +
            "OR (:keyword IS NULL OR LOWER(r.movie.title) LIKE LOWER(concat('%', :keyword, '%')))")
    List<Review> searchReviews(@Param("keyword") String keyword);

    //Get top 3 movie id based on review count
    @Query(value = "SELECT r.movie_id FROM review r " +
            "WHERE r.blinded = false " + // 숨김 해제된 리뷰들 중에서
            "GROUP BY r.movie_id " +
            "ORDER BY MAX(r.like_count) DESC " +
            "LIMIT 3", nativeQuery = true)
    List<Long> findTop3MovieIdsByReview();

    // 등급 4 이상인 유저의 리뷰만 조회
    @Query("SELECT r FROM Review r WHERE r.user.grade.gradeId >= :minGradeId AND r.blinded = false")
    List<Review> findEditorPickReviews(@Param("minGradeId") Long minGradeId);

    @Modifying
    @Query("UPDATE Review r SET r.likeCount = :likeCount WHERE r.id = :reviewId")
    void updateLikeCount(@Param("reviewId") Long reviewId, @Param("likeCount") Long likeCount);


}
