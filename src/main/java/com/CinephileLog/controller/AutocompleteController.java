package com.CinephileLog.controller;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AutocompleteController {

    private final RestHighLevelClient esClient;

    @GetMapping("/autocomplete")
    public List<Map<String, String>> autocomplete(@RequestParam("keyword") String keyword) throws IOException {
        if (keyword == null || keyword.length() < 2) return List.of();

        SearchRequest request = new SearchRequest("movies");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.prefixQuery("title", keyword.toLowerCase()))
                .size(10);

        request.source(sourceBuilder);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

        List<Map<String, String>> results = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            Map<String, Object> source = hit.getSourceAsMap();
            Map<String, String> item = new HashMap<>();
            item.put("movieId", String.valueOf(source.get("movieId")));
            item.put("title", String.valueOf(source.get("title")));
            item.put("posterUrl", source.get("posterUrl") != null ? source.get("posterUrl").toString() : "");
            item.put("releaseYear", source.get("releaseYear") != null ? source.get("releaseYear").toString() : "");
            results.add(item);
        }
        return results;
    }
}


