package com.CinephileLog.external.service;

import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.movie.repository.MovieRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TmdbApiClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final MovieRepository movieRepository;

    private final Set<Long> cachedMovieIds = ConcurrentHashMap.newKeySet();

    @PostConstruct
    public void preloadMovieIds() {
        System.out.println("🎬 영화 ID 캐시 사전 로딩 중...");
        List<Long> existingIds = movieRepository.findAllIds();  // ID만 가져오는 쿼리
        cachedMovieIds.addAll(existingIds);
        System.out.println("✅ 캐시 완료 - 총 ID 수: " + cachedMovieIds.size());
    }

    public Optional<Movie> fetchMovieById(int id, String apiKey) {
        Long movieId = (long) id;

        if (cachedMovieIds.contains(movieId)) {
            System.out.println("⏩ 이미 저장된 영화 (캐시) - ID: " + movieId);
            return Optional.empty();
        }

        try {
            ResponseEntity<Map> koResponse = restTemplate.getForEntity(
                    "https://api.themoviedb.org/3/movie/" + id + "?api_key=" + apiKey + "&language=ko-KR",
                    Map.class
            );
            ResponseEntity<Map> enResponse = restTemplate.getForEntity(
                    "https://api.themoviedb.org/3/movie/" + id + "?api_key=" + apiKey + "&language=en-US",
                    Map.class
            );

            if (koResponse.getStatusCode() != HttpStatus.OK || koResponse.getBody() == null
                    || enResponse.getStatusCode() != HttpStatus.OK || enResponse.getBody() == null) {
                System.out.println("❌ TMDB 응답 실패 - ID: " + id);
                return Optional.empty();
            }

            Map<String, Object> koData = koResponse.getBody();
            Map<String, Object> enData = enResponse.getBody();

            Movie movie = new Movie();
            movie.setId(movieId);
            movie.setTitle((String) koData.getOrDefault("title", ""));
            movie.setTitleOriginal((String) koData.getOrDefault("original_title", ""));

            String releaseDateStr = (String) koData.get("release_date");
            if (releaseDateStr != null && !releaseDateStr.isBlank()) {
                movie.setReleaseDate(LocalDate.parse(releaseDateStr));
            }

            movie.setPosterUrl((String) koData.get("poster_path"));
            movie.setSynopsis((String) koData.getOrDefault("overview", ""));
            movie.setSynopsisOriginal((String) enData.getOrDefault("overview", ""));

            BigDecimal voteAverage = new BigDecimal(((Number) koData.get("vote_average")).doubleValue());
            int roundedRating = voteAverage.setScale(0, RoundingMode.HALF_UP).intValue();
            movie.setRating(BigDecimal.valueOf(roundedRating));

            // 저장 후 캐시에 ID 추가
            movieRepository.save(movie);
            cachedMovieIds.add(movieId);

            return Optional.of(movie);

        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("🔍 존재하지 않는 ID (404): " + id);
        } catch (Exception e) {
            System.err.println("🔥 TMDB 요청 실패 ID=" + id + " → " + e.getMessage());
        }

        return Optional.empty();
    }

    public int fetchLatestMovieId(String apiKey) {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "https://api.themoviedb.org/3/movie/latest?api_key=" + apiKey,
                Map.class
        );
        return (Integer) response.getBody().get("id");
    }
}
