package com.CinephileLog.external.service;

import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.movie.repository.MovieRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
            Map<String, Object> koData = restTemplate.getForObject(
                    "https://api.themoviedb.org/3/movie/" + id + "?api_key=" + apiKey + "&language=ko-KR", Map.class);
            Map<String, Object> enData = restTemplate.getForObject(
                    "https://api.themoviedb.org/3/movie/" + id + "?api_key=" + apiKey + "&language=en-US", Map.class);

            if (koData == null || enData == null) return Optional.empty();

            Map<String, Object> creditData = restTemplate.getForObject(
                    "https://api.themoviedb.org/3/movie/" + id + "/credits?api_key=" + apiKey + "&language=ko-KR", Map.class);
            List<Map<String, Object>> castList = (List<Map<String, Object>>) creditData.getOrDefault("cast", new ArrayList<>());
            List<Map<String, Object>> crewList = (List<Map<String, Object>>) creditData.getOrDefault("crew", new ArrayList<>());

            Movie movie = new Movie();
            movie.setId(movieId);
            movie.setTitle((String) koData.getOrDefault("title", ""));
            movie.setTitleOriginal((String) koData.getOrDefault("original_title", ""));
            movie.setPosterUrl((String) koData.get("poster_path"));
            movie.setSynopsis((String) koData.getOrDefault("overview", ""));
            movie.setSynopsisOriginal((String) enData.getOrDefault("overview", ""));

            String releaseDateStr = (String) koData.get("release_date");
            if (releaseDateStr != null && !releaseDateStr.isBlank()) {
                movie.setReleaseDate(LocalDate.parse(releaseDateStr));
            }

            BigDecimal voteAverage = new BigDecimal(((Number) koData.get("vote_average")).doubleValue());
            int roundedRating = voteAverage.setScale(0, RoundingMode.HALF_UP).intValue();
            movie.setRating(BigDecimal.valueOf(roundedRating));

            // 감독
            List<String> directors = new ArrayList<>();
            List<String> directorsOriginal = new ArrayList<>();
            for (Map<String, Object> crew : crewList) {
                if ("Director".equals(crew.get("job"))) {
                    directors.add((String) crew.get("name"));
                    directorsOriginal.add((String) crew.get("original_name"));
                }
            }
            movie.setDirector(String.join(", ", directors));
            movie.setDirectorOriginal(String.join(", ", directorsOriginal));

            // 배우 (popularity 기준 상위 5명)
            castList.sort((a, b) -> Double.compare(
                    ((Number) b.getOrDefault("popularity", 0)).doubleValue(),
                    ((Number) a.getOrDefault("popularity", 0)).doubleValue()
            ));
            List<String> topCast = new ArrayList<>();
            List<String> topCastOriginal = new ArrayList<>();
            for (int i = 0; i < Math.min(5, castList.size()); i++) {
                Map<String, Object> actor = castList.get(i);
                topCast.add((String) actor.get("name"));
                topCastOriginal.add((String) actor.get("original_name"));
            }
            movie.setCast(String.join(", ", topCast));
            movie.setCastOriginal(String.join(", ", topCastOriginal));

            // 장르 문자열 저장
            List<Map<String, Object>> genreList = (List<Map<String, Object>>) koData.get("genres");
            if (genreList != null) {
                String genreNames = genreList.stream()
                        .map(g -> (String) g.get("name"))
                        .collect(Collectors.joining(", "));
                movie.setGenres(genreNames);
            }

            movieRepository.save(movie);
            cachedMovieIds.add(movieId);
            System.out.println("✅ 저장 완료 - ID: " + movieId + " (" + movie.getTitle() + ")");
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
