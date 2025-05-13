package com.CinephileLog.external;

import com.CinephileLog.external.dto.Credits;
import com.CinephileLog.external.dto.TmdbGenre;
import com.CinephileLog.external.dto.TmdbMovie;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TmdbClient {

    @Value("${tmdb.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "https://api.themoviedb.org/3";

//    public List<TmdbGenre> fetchGenres() {
//        String url = BASE_URL + "/genre/movie/list?api_key=" + apiKey + "&language=ko";
//        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
//
//        ObjectMapper mapper = new ObjectMapper();
//        List<Map<String, Object>> genreList = (List<Map<String, Object>>) response.get("genres");
//
//        return genreList.stream()
//                .map(map -> mapper.convertValue(map, TmdbGenre.class))
//                .toList();
//    }

    // TMDB API 에서 /movie/popular 응답(JSON)은 results라는 키로 영화 리스트를 내줌
    public List<TmdbMovie> fetchMovies() {
        String url = BASE_URL + "/movie/popular?api_key=" + apiKey + "&language=ko&page=1";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> movieList = (List<Map<String, Object>>) response.get("results"); // /movie/popular

        return movieList.stream()
                .map(map -> mapper.convertValue(map, TmdbMovie.class))
                .toList();
    }
    // TMDB API 에서 /movie/top_rated 응답(JSON)은 results라는 키로 영화 리스트를 내줌
    public List<TmdbMovie> fetchTopRatedMovies() {
        String url = BASE_URL + "/movie/top_rated?api_key=" + apiKey + "&language=ko&page=1";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> movieList = (List<Map<String, Object>>) response.get("results"); // /movie/top_rated

        return movieList.stream()
                .map(map -> mapper.convertValue(map, TmdbMovie.class))
                .toList();
    }


//    public Credits fetchCredits(Long movieId) {
//        String url = BASE_URL + "/movie/" + movieId + "/credits?api_key=" + apiKey + "&language=ko";
//        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
//
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper.convertValue(response, Credits.class);  // Credits 객체로 변환
//    }

}
