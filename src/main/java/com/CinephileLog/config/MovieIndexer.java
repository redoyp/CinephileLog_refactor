package com.CinephileLog.config;

import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieIndexer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final RestHighLevelClient esClient;

    @Value("${cache.data.init}")
    private boolean movieDataInit;

    @Override
    public void run(String... args) throws Exception {
        if (!movieDataInit) {
            log.info("Elasticsearch 색인 생략");
            return;
        }

        log.info("Elasticsearch 색인 시작");

        int page = 0;
        int pageSize = 1000;
        long totalIndexed = 0;

        Slice<Movie> result;

        do {
            Pageable pageable = PageRequest.of(page, pageSize);
            result = movieRepository.findAllBy(pageable);
            List<Movie> movies = result.getContent();

            BulkRequest autocompleteBulk = new BulkRequest();
            BulkRequest searchBulk = new BulkRequest();

            for (Movie movie : movies) {
                String id = movie.getId().toString();
                Map<String, Object> autoDoc = new HashMap<>();
                autoDoc.put("movieId", id);
                autoDoc.put("title", movie.getTitle());
                autoDoc.put("posterUrl", movie.getPosterUrl());
                autoDoc.put("releaseYear", movie.getReleaseDate() != null ? movie.getReleaseDate().getYear() : null);

                autocompleteBulk.add(
                        new IndexRequest("movies_autocomplete").id(id).source(autoDoc)
                );

                Map<String, Object> searchDoc = new HashMap<>();
                searchDoc.put("movieId", id);
                searchDoc.put("title", movie.getTitle());
                searchDoc.put("synopsis", movie.getSynopsis());
                searchDoc.put("director", movie.getDirector());
                searchDoc.put("posterUrl", movie.getPosterUrl());
                searchDoc.put("releaseYear", movie.getReleaseDate() != null ? movie.getReleaseDate().getYear() : null);

                searchBulk.add(
                        new IndexRequest("movies").id(id).source(searchDoc)
                );
            }

            if (autocompleteBulk.numberOfActions() > 0) {
                esClient.bulk(autocompleteBulk, RequestOptions.DEFAULT);
            }
            if (searchBulk.numberOfActions() > 0) {
                esClient.bulk(searchBulk, RequestOptions.DEFAULT);
            }

            totalIndexed += movies.size();
            page++;

        } while (result.hasNext());

        log.info("Elasticsearch 색인 완료 - 총 {}건", totalIndexed);
    }
}
