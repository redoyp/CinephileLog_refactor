// 리뷰 정렬
document.getElementById('sort-select').addEventListener('change', function() {
    const sortBy = this.value;
    sortReviews(sortBy);
});

function sortReviews(sortBy) {
    const reviewsList = document.querySelector('.reviews-list');
    const reviews = Array.from(reviewsList.getElementsByClassName('review-card'));

    reviews.sort((a, b) => {
        const getReviewData = (reviewElement) => {
            const createdDate = new Date(reviewElement.getAttribute('data-created'));
            const updatedDate = new Date(reviewElement.getAttribute('data-updated') || createdDate); // `updatedDate`가 없으면 `createdDate`를 사용
            const likeCount = parseInt(reviewElement.querySelector('.like-count').textContent);
            const latestDate = updatedDate > createdDate ? updatedDate : createdDate; // 최신 날짜를 선택
            return { latestDate, likeCount };
        };

        const dataA = getReviewData(a);
        const dataB = getReviewData(b);

        switch (sortBy) {
            case 'newest':
                return dataB.latestDate - dataA.latestDate;  // 최신순
            case 'oldest':
                return dataA.latestDate - dataB.latestDate;  // 오래된 순
            case 'most-likes':
                return dataB.likeCount - dataA.likeCount;  // 좋아요 많은 순
            case 'least-likes':
                return dataA.likeCount - dataB.likeCount;  // 좋아요 적은 순
            default:
                return 0;
        }
    });

    // 정렬된 리뷰를 DOM에 다시 반영
    reviews.forEach(review => reviewsList.appendChild(review));
}




// 리뷰 삭제
document.addEventListener('DOMContentLoaded', function () {
    const deleteButtons = document.querySelectorAll('.delete-button');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function () {
            if (!confirm('리뷰를 삭제하시겠습니까?')) {
                return;
            }

            const reviewCard = this.closest('.review-card');
            const movieId = getMovieIdFromUrl();

            fetch(`/movies/${movieId}/reviews`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include'
            })
                .then(response => {
                    if (response.ok) {
                        alert('리뷰가 삭제되었습니다.');
                        reviewCard.remove();
                        window.location.reload();
                    } else if (response.status === 401) {
                        alert('로그인이 필요합니다.');
                    } else {
                        alert('리뷰 삭제에 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('리뷰 삭제 중 오류:', error);
                    alert('서버 오류가 발생했습니다.');
                });
        });
    });

    function getMovieIdFromUrl() {
        const match = window.location.pathname.match(/\/movieDetail\/(\d+)/);
        return match ? parseInt(match[1]) : null;
    }

});


// 좋아요 버튼
document.addEventListener('DOMContentLoaded', function () {
    const likeButtons = document.querySelectorAll('.like-button');

    likeButtons.forEach(button => {
        button.addEventListener('click', function () {
            const reviewId = button.getAttribute('data-review-id');
            const isLiked = button.getAttribute('data-liked') === 'true';

            fetch(`/reviews/${reviewId}/like`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'same-origin' // 로그인 쿠키 포함
            })
                .then(response => response.json())
                .then(data => {
                    const likeCountSpan = button.querySelector('.like-count');
                    let likeCount = parseInt(likeCountSpan.textContent, 10);

                    if (data.liked) {
                        button.classList.remove('btn-outline-primary');
                        button.classList.add('btn-primary');
                        button.classList.add('liked');
                        button.setAttribute('data-liked', 'true');
                        likeCountSpan.textContent = (likeCount + 1).toString();
                    } else {
                        button.classList.remove('btn-primary');
                        button.classList.add('btn-outline-primary');
                        button.classList.remove('liked');
                        button.setAttribute('data-liked', 'false');
                        likeCountSpan.textContent = (likeCount - 1).toString();
                    }
                })
                .catch(error => {
                    console.error('좋아요 처리 오류:', error);
                    alert('좋아요 요청 중 오류가 발생했습니다.');
                });
        });
    });
});


// 페이지네이션
document.addEventListener('DOMContentLoaded', function () {
    const reviewsPerPage = 4;  // 한 페이지에 보여줄 리뷰 개수
    let currentPage = 1;  // 현재 페이지
    let totalReviews = document.querySelectorAll('.review-card').length;  // 전체 리뷰 개수

    const reviewCards = document.querySelectorAll('.review-card');
    const paginationNumbers = document.getElementById('pagination-numbers');
    const prevBtn = document.getElementById('prev-btn');
    const nextBtn = document.getElementById('next-btn');

    // 페이지 버튼 생성
    function generatePagination() {
        const totalPages = Math.ceil(totalReviews / reviewsPerPage);
        paginationNumbers.innerHTML = '';  // 기존 페이지 번호 초기화

        for (let i = 1; i <= totalPages; i++) {
            const pageButton = document.createElement('span');
            pageButton.textContent = i;
            pageButton.classList.add('page-btn');
            if (i === currentPage) {
                pageButton.classList.add('active');
            }
            pageButton.addEventListener('click', function () {
                currentPage = i;
                displayReviews();
            });
            paginationNumbers.appendChild(pageButton);
        }

        // 이전/다음 버튼 활성화 상태 업데이트
        prevBtn.disabled = currentPage === 1;
        nextBtn.disabled = currentPage === totalPages;
    }

    // 리뷰 표시
    function displayReviews() {
        const startIdx = (currentPage - 1) * reviewsPerPage;
        const endIdx = startIdx + reviewsPerPage;

        // 모든 리뷰를 숨기고, 현재 페이지에 맞는 리뷰만 보이게 함
        reviewCards.forEach((card, index) => {
            card.style.display = (index >= startIdx && index < endIdx) ? 'block' : 'none';
        });

        generatePagination();
    }

    // 처음 페이지 로드 시 리뷰 표시
    displayReviews();

    // 이전/다음 버튼 클릭 처리
    prevBtn.addEventListener('click', function () {
        if (currentPage > 1) {
            currentPage--;
            displayReviews();
        }
    });

    nextBtn.addEventListener('click', function () {
        const totalPages = Math.ceil(totalReviews / reviewsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            displayReviews();
        }
    });
});



// 리뷰 작성
const ratingStars = document.getElementById("rating-stars");
const ratingValue = document.getElementById("rating-value");

let rating = 0; // 0.5 단위로, 내부적으로는 0.5 ~ 10.0

function renderStars() {
    ratingStars.innerHTML = '';
    for (let i = 1; i <= 5; i++) {
        const star = document.createElement('span');
        star.classList.add('star');

        const starIndex = i * 2; // 내부적으로 별 하나 = 2점 (0.5 * 2 = 1)

        if (rating >= starIndex) {
            star.classList.add('full');
        } else if (rating === starIndex - 1) {
            star.classList.add('half');
        }

        star.textContent = '★';
        star.dataset.index = i;
        ratingStars.appendChild(star);
    }

    ratingValue.textContent = (rating).toFixed(1); // 사용자용 평점 출력
}

// 클릭 이벤트: 반 별/전체 별 판단
ratingStars.addEventListener("click", (e) => {
    if (!e.target.classList.contains("star")) return;

    const star = e.target;
    const rect = star.getBoundingClientRect();
    const clickX = e.clientX - rect.left;
    const isLeftHalf = clickX < rect.width / 2;

    const starIndex = parseInt(star.dataset.index); // 1 ~ 5

    rating = isLeftHalf
        ? (starIndex - 1) * 2 + 1  // 왼쪽: 0.5, 1.5, 2.5, ...
        : (starIndex) * 2;         // 오른쪽: 1.0, 2.0, ..., 5.0 (곱하면 2.0 ~ 10.0)

    renderStars();
});

renderStars();


// 리뷰 생성/수정
document.addEventListener('DOMContentLoaded', () => {
    const submitButton = document.querySelector('.review-submit-btn');
    const ratingValue = document.getElementById('rating-value');
    const contentInput = document.querySelector('.review-input-content');
    const MAX_SCORE = 60;

    if (!contentInput) return;

    const popover = bootstrap.Popover.getOrCreateInstance(contentInput);

    contentInput.addEventListener('input', () => {
        let str = contentInput.value;
        let len = getCustomLength(str);

        if (len > MAX_SCORE) {
            contentInput.value = str.slice(0, -1);
            popover.show();
            setTimeout(() => popover.hide(), 2000);
        }
    });

    if (!submitButton) return;

    submitButton.addEventListener('click', async () => {
        const movieId = submitButton.getAttribute('data-movie-id');
        const reviewId = submitButton.getAttribute('data-review-id');
        const rating = parseFloat(ratingValue.textContent);
        const content = contentInput.value.trim();

        if (isNaN(rating) || rating <= 0) {
            alert('별점을 입력하세요.');
            return;
        }

        if (!content) {
            alert('리뷰 내용을 입력하세요.');
            return;
        }

        if (getCustomLength(content) > MAX_SCORE) {
            alert('리뷰는 한글 20자 또는 영어 60자까지 입력할 수 있습니다.');
            return;
        }

        const reviewData = { rating, content };

        try {
            const response = await fetch(`/movies/${movieId}/reviews`, {
                method: reviewId ? 'PUT' : 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(reviewData)
            });

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || '리뷰 전송에 실패했습니다.');
            }

            alert(reviewId ? '리뷰가 수정되었습니다.' : '리뷰가 등록되었습니다.');
            window.location.reload();
        } catch (error) {
            alert(error.message);
        }
    });
});

// 리뷰 삭제
document.addEventListener("DOMContentLoaded", () => {
    const deleteBtn = document.querySelector(".review-delete-btn");

    if (!deleteBtn) return;

    deleteBtn.addEventListener("click", async () => {
        const movieId = deleteBtn.dataset.movieId;

        if (!confirm("정말로 리뷰를 삭제하시겠습니까?")) return;

        try {
            const response = await fetch(`/movies/${movieId}/reviews`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include'
            });

            if (response.ok) {
                alert('리뷰가 삭제되었습니다.');
                window.location.reload();
            } else if (response.status === 401) {
                alert('로그인이 필요합니다.');
            } else {
                alert('리뷰 삭제에 실패했습니다.');
            }
        } catch (error) {
            console.error('리뷰 삭제 중 오류:', error);
            alert('서버 오류가 발생했습니다.');
        }
    });
});


// 채팅방 입력 버튼 드래그 기능
const chatButton = document.getElementById("chatButton");

let isDragging = false;
let offsetX, offsetY;
let mouseDownTime = 0;

chatButton.addEventListener("mousedown", function (e) {
    isDragging = true;
    offsetX = e.clientX - chatButton.getBoundingClientRect().left;
    offsetY = e.clientY - chatButton.getBoundingClientRect().top;
    mouseDownTime = Date.now();
    e.preventDefault();
});

document.addEventListener("mousemove", function (e) {
    if (isDragging) {
        const x = e.clientX - offsetX;
        const y = e.clientY - offsetY;
        chatButton.style.left = `${x}px`;
        chatButton.style.top = `${y}px`;
        chatButton.style.right = "auto";
        chatButton.style.bottom = "auto";
    }
});

document.addEventListener("mouseup", function (e) {
    if (!isDragging) return;
    isDragging = false;

    const userId = chatButton.dataset.userId;
    const gradeId = parseInt(chatButton.dataset.gradeId, 10);

    const mouseUpTime = Date.now();
    const pressDuration = mouseUpTime - mouseDownTime;

    const moveThreshold = 5; // px
    const timeThreshold = 200; // ms

    const movedX = Math.abs(e.clientX - (chatButton.offsetLeft + offsetX));
    const movedY = Math.abs(e.clientY - (chatButton.offsetTop + offsetY));

    if (pressDuration < timeThreshold && movedX < moveThreshold && movedY < moveThreshold) {
        if (!userId) {
            alert("로그인이 필요합니다.");
            return;
        }

        if (gradeId < 3) {
            alert("채팅은 3등급(Nachos) 이상부터 가능합니다.");
            return;
        }
        window.location.href = chatButton.dataset.href;
    }
});


function getCustomLength(str) {
    let len = 0;
    for (let i = 0; i < str.length; i++) {
        const ch = str.charCodeAt(i);
        if ((ch >= 0xAC00 && ch <= 0xD7A3) || (ch >= 0x1100 && ch <= 0x11FF)) {
            len += 3;
        } else {
            len += 1;
        }
    }
    return len;
}