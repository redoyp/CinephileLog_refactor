const blindButton = document.querySelector(".blind-review-btn");
const unblindButton = document.querySelector(".unblind-review-btn");

if (blindButton) {
    blindButton.addEventListener("click", () => {
        const confirmed = confirm("이 리뷰를 숨김 처리하시겠습니까?");
        if (!confirmed) return;

        const reviewId = blindButton.dataset.reviewId;
        fetch(`/admin/reviews/${reviewId}/blind`, { method: "POST" })
            .then(response => {
                if (response.ok) {
                    alert("리뷰가 숨김 처리되었습니다.");
                    window.location.reload(); // 현재 페이지 새로고침
                }
                else {
                    alert("숨김 처리에 실패했습니다.");
                }
            })
            .catch(error => {
                console.error("숨김 처리 오류:", error);
                alert("오류가 발생했습니다.");
            });
    });
}

if (unblindButton) {
    unblindButton.addEventListener("click", () => {
        const confirmed = confirm("이 리뷰의 숨김을 해제하시겠습니까?");
        if (!confirmed) return;

        const reviewId = unblindButton.dataset.reviewId;
        fetch(`/admin/reviews/${reviewId}/unblind`, { method: "POST" })
            .then(response => {
                if (response.ok) {
                    alert("리뷰 숨김이 해제되었습니다.");
                    window.location.reload(); // 현재 페이지 새로고침
                } else {
                    alert("숨김 해제에 실패했습니다.");
                }
            })
            .catch(error => {
                console.error("숨김 해제 오류:", error);
                alert("오류가 발생했습니다.");
            });
    });
}
