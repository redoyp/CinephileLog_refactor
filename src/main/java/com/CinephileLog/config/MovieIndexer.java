package com.CinephileLog.config;

import com.CinephileLog.movie.domain.Movie;
import com.CinephileLog.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieIndexer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final RestHighLevelClient esClient;

    private static final String INDEX_NAME = "movies";

    @Override
    public void run(String... args) throws Exception {
        log.info("Elasticsearch 인덱싱 시작");

        List<Movie> allMovies = movieRepository.findAll();
        int total = allMovies.size();
        log.info("✔️ 총 대상 영화 수: {}", total);

        long start = System.currentTimeMillis();
        BulkRequest bulkRequest = new BulkRequest();
        int batchSize = 1000;
        int counter = 0;

        for (Movie movie : allMovies) {
            GetRequest check = new GetRequest(INDEX_NAME, movie.getId().toString());
            if (esClient.exists(check, RequestOptions.DEFAULT)) continue;

            Map<String, Object> doc = new HashMap<>();
            doc.put("movieId", movie.getId().toString());
            doc.put("title", movie.getTitle());
            doc.put("posterUrl", movie.getPosterUrl());
            doc.put("releaseYear", movie.getReleaseDate() != null ? movie.getReleaseDate().getYear() : null);


            bulkRequest.add(new IndexRequest(INDEX_NAME)
                    .id(movie.getId().toString())
                    .source(doc, XContentType.JSON));
            counter++;

            if (counter % batchSize == 0) {
                BulkResponse response = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                if (response.hasFailures()) {
                    log.warn("❌ 일부 문서 인덱싱 실패: {}", response.buildFailureMessage());
                } else {
                    log.info("✅ [{} / {}] 인덱싱 성공", counter, total);
                }
                bulkRequest = new BulkRequest();
            }
        }

        if (bulkRequest.numberOfActions() > 0) {
            esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            log.info("✅ 마지막 인덱싱 완료");
        }

        long end = System.currentTimeMillis();
        log.info("인덱싱 완료 - 총 {}건 / 소요 시간: {}초", counter, (end - start) / 1000);
    }
}
