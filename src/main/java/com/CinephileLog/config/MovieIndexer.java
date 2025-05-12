//package com.CinephileLog.config;
//
//import com.CinephileLog.movie.domain.Movie;
//import com.CinephileLog.movie.repository.MovieRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.elasticsearch.action.bulk.BulkRequest;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.elasticsearch.action.get.GetRequest;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.action.index.IndexRequest;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class MovieIndexer implements CommandLineRunner {
//
//    private final MovieRepository movieRepository;
//    private final RestHighLevelClient esClient;
//
//    @Override
//    public void run(String... args) throws Exception {
//        log.info("Elasticsearch 인덱싱 시작");
//
//        int page = 0;
//        int pageSize = 1000;
//        long totalIndexed = 0;
//
//        Slice<Movie> result;
//
//        do {
//            Pageable pageable = PageRequest.of(page, pageSize);
//            result = movieRepository.findAllBy(pageable);
//            List<Movie> movies = result.getContent();
//
//            BulkRequest bulkRequest = new BulkRequest();
//
//            for (Movie movie : movies) {
//                boolean exists = esClient.exists(
//                        new GetRequest("movies", movie.getId().toString()),
//                        RequestOptions.DEFAULT
//                );
//                if (exists) continue;
//
//                Map<String, Object> doc = new HashMap<>();
//                doc.put("movieId", movie.getId().toString());
//                doc.put("title", movie.getTitle());
//                doc.put("posterUrl", movie.getPosterUrl());
//                doc.put("releaseYear", movie.getReleaseDate() != null ? movie.getReleaseDate().getYear() : null);
//
//                IndexRequest indexRequest = new IndexRequest("movies")
//                        .id(movie.getId().toString())
//                        .source(doc);
//                bulkRequest.add(indexRequest);
//            }
//
//            if (bulkRequest.numberOfActions() > 0) {
//                BulkResponse response = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
//                if (response.hasFailures()) {
//                    log.warn("⚠️ 일부 인덱싱 실패: {}", response.buildFailureMessage());
//                } else {
//                    totalIndexed += bulkRequest.numberOfActions();
//                    log.info("✅ {}건 인덱싱 완료 (누적: {})", bulkRequest.numberOfActions(), totalIndexed);
//                }
//            }
//            page++;
//        } while (result.hasNext());
//        log.info("인덱싱 완료 - 총 {}건", totalIndexed);
//    }
//}
