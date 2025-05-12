package com.CinephileLog.external.service;

import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.movie.repository.MovieRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TmdbApiClient {
    private final MovieRepository movieRepository;
    private final Set<Long> cachedMovieIds = ConcurrentHashMap.newKeySet();

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.themoviedb.org/3/movie")
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create(ConnectionProvider.builder("tmdb-pool")
                                    .maxConnections(50)
                                    .pendingAcquireTimeout(Duration.ofSeconds(5))
                                    .build())
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                            .doOnConnected(conn -> conn
                                    .addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                                    .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS)))
            ))
            .build();

    @PostConstruct
    public void preloadMovieIds() {
        List<Long> existingIds = movieRepository.findAllIds();
        cachedMovieIds.addAll(existingIds);
        System.out.println("✅ 캐시 완료 - 총 ID 수: " + cachedMovieIds.size());
    }

    public Optional<Movie> fetchMovieById(int id, String apiKey) {
        Long movieId = (long) id;
        if (cachedMovieIds.contains(movieId)) return Optional.empty();

        try {
            Mono<Map> koMono = webClient.get()
                    .uri("/{id}?api_key={apiKey}&language=ko-KR", id, apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
                            .filter(e -> !(e instanceof WebClientResponseException.NotFound)));

            Mono<Map> enMono = webClient.get()
                    .uri("/{id}?api_key={apiKey}&language=en-US", id, apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
                            .filter(e -> !(e instanceof WebClientResponseException.NotFound)));

            Mono<Map> creditsMono = webClient.get()
                    .uri("/{id}/credits?api_key={apiKey}&language=ko-KR", id, apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
                            .filter(e -> !(e instanceof WebClientResponseException.NotFound)));

            Thread.sleep(250);

            Map<String, Object> koData = koMono.block();
            Map<String, Object> enData = enMono.block();
            Map<String, Object> creditData = creditsMono.block();

            if (koData == null || enData == null) return Optional.empty();

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

            List<String> directors = crewList.stream()
                    .filter(c -> "Director".equals(c.get("job")))
                    .map(c -> (String) c.get("name"))
                    .collect(Collectors.toList());

            List<String> directorsOriginal = crewList.stream()
                    .filter(c -> "Director".equals(c.get("job")))
                    .map(c -> (String) c.get("original_name"))
                    .collect(Collectors.toList());

            movie.setDirector(String.join(", ", directors));
            movie.setDirectorOriginal(String.join(", ", directorsOriginal));

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

            List<Map<String, Object>> genreList = (List<Map<String, Object>>) koData.get("genres");
            if (genreList != null) {
                String genreNames = genreList.stream()
                        .map(g -> (String) g.get("name"))
                        .collect(Collectors.joining(", "));
                movie.setGenres(genreNames);
            }

            return Optional.of(movie);

        } catch (WebClientResponseException.NotFound e) {
            System.out.println("🔍 존재하지 않는 ID (404): " + id);
        } catch (Exception e) {
            System.err.println("TMDB 요청 실패 ID=" + id + " → " + e.getMessage());
        }

        return Optional.empty();
    }

    public int fetchLatestMovieId(String apiKey) {
        try {
            Map response = webClient.get()
                    .uri("/latest?api_key={apiKey}", apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return (Integer) response.get("id");
        } catch (Exception e) {
            throw new RuntimeException("최신 ID 조회 실패", e);
        }
    }
}