package com.CinephileLog.column.service;

import com.CinephileLog.column.domain.ColumnArticle;
import com.CinephileLog.column.dto.ColumnArticleRequest;
import com.CinephileLog.column.dto.ColumnArticleResponse;
import com.CinephileLog.column.repository.ColumnArticleRepository;
import com.CinephileLog.domain.User;
import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.movie.repository.MovieRepository;
import com.CinephileLog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class ColumnArticleService {

    private final ColumnArticleRepository columnArticleRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    public Page<ColumnArticleResponse> getColumnPage(int page, int size, String sort, String direction, String keyword, String field) {
        Sort sorting = direction.equals("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sorting);

        Page<ColumnArticle> result;
        if (StringUtils.hasText(keyword) && StringUtils.hasText(field)) {
            if (field.equals("title")) {
                result = columnArticleRepository.findByTitleContainingAndIsDeletedFalse(keyword, pageable);
            } else if (field.equals("nickname")) {
                result = columnArticleRepository.findByUser_NicknameContainingAndIsDeletedFalse(keyword, pageable);
            } else {
                result = columnArticleRepository.findAllByIsDeletedFalse(pageable);
            }
        } else {
            result = columnArticleRepository.findAllByIsDeletedFalse(pageable);
        }

        return result.map(this::mapToResponse);
    }

    public ColumnArticleResponse getColumnDetail(Long columnId) {
        ColumnArticle column = columnArticleRepository.findByColumnIdAndIsDeletedFalse(columnId)
                .orElseThrow(() -> new RuntimeException("칼럼이 존재하지 않습니다."));
        column.setViewCount(column.getViewCount() + 1);
        columnArticleRepository.save(column);
        return mapToResponse(column);
    }

    public void writeColumn(Long userId, ColumnArticleRequest request) throws AccessDeniedException {
        if (request.getMovieId() == null) {
            throw new IllegalArgumentException("영화를 선택해야 글을 작성할 수 있습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("영화 없음"));

        ColumnArticle column = ColumnArticle.builder()
                .user(user)
                .movie(movie)
                .title(request.getTitle())
                .content(request.getContent())
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .updatedDate(new Timestamp(System.currentTimeMillis()))
                .viewCount(0L)
                .isDeleted(false)
                .build();

        columnArticleRepository.save(column);
    }

    public void updateColumn(Long userId, Long columnId, ColumnArticleRequest request) throws AccessDeniedException {
        ColumnArticle column = columnArticleRepository.findById(columnId).orElseThrow();
        if (!column.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        column.setTitle(request.getTitle());
        column.setContent(request.getContent());
        column.setMovie(movieRepository.findById(request.getMovieId()).orElseThrow());
        column.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        columnArticleRepository.save(column);
    }

    public void deleteColumn(Long userId, Long columnId) throws AccessDeniedException {
        ColumnArticle column = columnArticleRepository.findById(columnId).orElseThrow();
        if (!column.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
        column.setIsDeleted(true);
        columnArticleRepository.save(column);
    }

    private ColumnArticleResponse mapToResponse(ColumnArticle c) {
        return ColumnArticleResponse.builder()
                .columnId(c.getColumnId())
                .movieId(c.getMovie().getId())
                .movieTitle(c.getMovie().getTitle())
                .posterUrl(c.getMovie().getPosterUrl())
                .title(c.getTitle())
                .content(c.getContent())
                .nickname(c.getUser().getNickname())
                .createdDate(c.getCreatedDate())
                .updatedDate(c.getUpdatedDate())
                .viewCount(c.getViewCount())
                .build();
    }

}
