package com.CinephileLog.review.domain;

import com.CinephileLog.domain.User;
import com.CinephileLog.movie.domain.Movie;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table( // 특정 영화에 유저 당 리뷰 한 번만 작성 가능
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"})
)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(nullable = false)
    private String content;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    private boolean blinded;  // 블라인드 여부

    public Review(User user, Movie movie, double rating, String content, boolean blinded) {
        this.user = user;
        this.movie = movie;
        this.rating = BigDecimal.valueOf(rating);
        this.content = content;
        this.likeCount = 0L;
        this.blinded = blinded;
    }
}
