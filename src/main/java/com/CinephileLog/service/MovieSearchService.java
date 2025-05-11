package com.CinephileLog.service;

import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieSearchService {

    private final RestHighLevelClient esClient;
    private final MovieRepository movieRepository;

    public List<Movie> search(String keyword, int page, int size) {
        try {
            SearchRequest request = new SearchRequest("movies");

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .query(QueryBuilders.matchQuery("title", keyword))
                    .from(page * size)
                    .size(size);

            request.source(sourceBuilder);

            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

            List<String> movieIds = Arrays.stream(response.getHits().getHits())
                    .map(hit -> hit.getSourceAsMap().get("movieId").toString())
                    .collect(Collectors.toList());

            return movieRepository.findByIdIn(
                    movieIds.stream().map(Long::parseLong).collect(Collectors.toList())
            );

        } catch (IOException e) {
            throw new RuntimeException("검색 실패", e);
        }
    }
}
