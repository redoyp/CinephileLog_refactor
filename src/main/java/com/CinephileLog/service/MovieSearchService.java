package com.CinephileLog.service;

import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieSearchService {

    private final RestHighLevelClient esClient;
    private final MovieRepository movieRepository;

    public List<Movie> search(String keyword, int page, int size) {
        try {
            SearchRequest request = new SearchRequest("movies");

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("title", keyword)) // 중간 단어 검색
                    .should(QueryBuilders.matchQuery("title.autocomplete", keyword)); // 접두어 기반

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .query(boolQuery)
                    .from(page * size)
                    .size(size);

            request.source(sourceBuilder);
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

            List<Long> movieIds = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                Object movieIdObj = hit.getSourceAsMap().get("movieId");
                if (movieIdObj != null) {
                    try {
                        movieIds.add(Long.parseLong(movieIdObj.toString()));
                    } catch (NumberFormatException ignored) {}
                }
            }

            return movieRepository.findByIdIn(movieIds);

        } catch (IOException e) {
            throw new RuntimeException("검색 실패", e);
        }
    }
}
