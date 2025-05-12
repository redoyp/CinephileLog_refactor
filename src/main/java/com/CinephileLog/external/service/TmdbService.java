package com.CinephileLog.external.service;

import com.CinephileLog.external.TmdbClient;
import com.CinephileLog.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TmdbService {
    private final TmdbClient tmdbClient;
    private final MovieRepository movieRepository;

//    @Transactional
//    public void saveGenres() {
//        List<TmdbGenre> genres = tmdbClient.fetchGenres();
//
//        for (TmdbGenre dto : genres) {
//            Genre genre = genreRepository.findByName(dto.getName())
//                    .orElseGet(() -> new Genre(dto.getName()));
//
//            genreRepository.save(genre);
//            log.info("저장된 장르 이름: {}", genre.getName());
//        }
//    }

//    @Transactional
//    public void saveMovies() {
//        List<TmdbMovie> movies = tmdbClient.fetchMovies();
//
//        List<Genre> genres = genreRepository.findAll();
//
//        for(TmdbMovie dto : movies) {
//            List<String> genreNames = dto.getGenreNames(genres);
//            Set<Genre> genreSet = genres.stream()
//                    .filter(g -> genreNames.contains(g.getName()))
//                    .collect(Collectors.toSet());
//            if(dto.getTitle() == null) continue;
//
//            // 영화의 credits 정보를 가져오기
//            Credits credits = tmdbClient.fetchCredits(dto.getId());
//            dto.setCredits(credits);  // credits를 TmdbMovie에 설정
//
//            String director = dto.getDirector();
//            String cast = dto.getCast();
//
//            Movie movie = new Movie(
//                    null,
//                    dto.getTitle(),
//                    dto.getTitleOriginal(),
//                    LocalDate.parse(dto.getReleaseDate()),
//                    "https://image.tmdb.org/t/p/w500" + dto.getPosterUrl(),
//                    BigDecimal.valueOf(0.0),
//                    dto.getSynopsis(),
//                    director,
//                    cast,
//                    genreSet
//            );
//
//            movieRepository.save(movie);
//        }
//    }
}
