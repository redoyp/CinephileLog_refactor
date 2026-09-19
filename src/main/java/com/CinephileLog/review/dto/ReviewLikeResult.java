package com.CinephileLog.review.dto;

public class ReviewLikeResult {
    private final boolean liked;
    private final boolean gradeUp;

    public ReviewLikeResult(boolean liked, boolean gradeUp) {
        this.liked = liked;
        this.gradeUp = gradeUp;
    }

    public boolean isLiked() {
        return liked;
    }

    public boolean isGradeUp() {
        return gradeUp;
    }
}

