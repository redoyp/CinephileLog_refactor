package com.CinephileLog.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewLikeResponse {
    private boolean liked;     // true : 좋아요, false : 좋아요 취소
    private boolean gradeUp;   // true : 등업됨, false : 등업 아님

    public ReviewLikeResponse(boolean liked) {
        this.liked = liked;
        this.gradeUp = false; // 기본값
    }

    public ReviewLikeResponse(boolean liked, boolean gradeUp) {
        this.liked = liked;
        this.gradeUp = gradeUp;
    }
}
