package com.CinephileLog.service;

import com.CinephileLog.domain.*;
import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.movie.repository.MovieRepository;
import com.CinephileLog.repository.*;
import com.CinephileLog.review.domain.Review;
import com.CinephileLog.review.domain.ReviewLike;
import com.CinephileLog.review.repository.ReviewLikeRepository;
import com.CinephileLog.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class GradeServiceTest {

    @Autowired private GradeService gradeService;
    @Autowired private UserRepository userRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ReviewLikeRepository reviewLikeRepository;
    @Autowired private GradeRepository gradeRepository;
    @Autowired private MovieRepository movieRepository;

    private List<Movie> movieList;

    @BeforeEach
    void setUp() {
        movieList = movieRepository.findAll();
        assertThat(movieList.size()).isGreaterThanOrEqualTo(50);
    }

    @Test
    @DisplayName("리뷰 10개 작성 시 Coke로 승급")
    void upgradeToCoke() {
        User user = saveUserWithGrade(1L);
        for (int i = 0; i < 10; i++) {
            reviewRepository.save(new Review(user, movieList.get(i), 4.5, "내용", false));
        }

        gradeService.updateGradeForUser(user.getUserId());
        User updated = userRepository.findById(user.getUserId()).orElseThrow();
        assertThat(updated.getGrade().getGradeId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("리뷰 30개 + 좋아요 10개 작성 시 Nachos로 승급")
    void upgradeToNachos() {
        User user = saveUserWithGrade(2L);
        for (int i = 0; i < 30; i++) {
            Review r = new Review(user, movieList.get(i), 5.0, "좋아요!", false);
            reviewRepository.save(r);
            if (i < 10) {
                reviewLikeRepository.save(new ReviewLike(user, r));
            }
        }

        gradeService.updateGradeForUser(user.getUserId());
        User updated = userRepository.findById(user.getUserId()).orElseThrow();
        assertThat(updated.getGrade().getGradeId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("상위 5% popcorn, 10% hotdog, 나머지는 nachos로 강등 (리뷰 50개 기준)")
    void reevaluateToPopcornHotdogOrDown_Light() {
        for (int i = 0; i < 30; i++) {
            User user = saveUserWithGrade(4L);
            for (int j = 0; j < 30; j++) {
                Review r = new Review();
                r.setUser(user);
                r.setMovie(movieList.get((i * 2 + j) % 50));
                r.setRating(BigDecimal.valueOf(4.0));
                r.setContent("리뷰 " + j);
                r.setLikeCount(0L);
                reviewRepository.saveAndFlush(r);
                for (int k = 0; k < i * 10; k++) {
                    reviewLikeRepository.save(new ReviewLike(user, r));
                }
            }
        }
        gradeService.weeklyGradeReevaluation();
        List<User> all = userRepository.findAll();
        long popcorn = all.stream().filter(u -> u.getGrade().getGradeId() == 5L).count();
        long hotdog = all.stream().filter(u -> u.getGrade().getGradeId() == 4L).count();
        long downgraded = all.stream().filter(u -> u.getGrade().getGradeId() == 3L).count();

        System.out.printf("Popcorn: %d명, Hotdog: %d명, Nachos 강등: %d명%n", popcorn, hotdog, downgraded);
        List<User> allList = userRepository.findAll();
        allList.sort(Comparator.comparing(u -> u.getGrade().getGradeId()));
        System.out.println("[최종 유저 등급 결과]");
        for (User u : allList) {
            System.out.printf(" %s | ID: %d | 등급: %s (%d)%n",
                    u.getNickname(), u.getUserId(),
                    u.getGrade().getGradeName(), u.getGrade().getGradeId()
            );
        }

        assertThat(popcorn).isEqualTo(2);
        assertThat(hotdog).isEqualTo(3);
        assertThat(downgraded).isEqualTo(25);
    }

    private User saveUserWithGrade(Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId).orElseThrow();
        User user = new User("google", UUID.randomUUID() + "@mail.com", "tester", grade);
        return userRepository.save(user);
    }
}
