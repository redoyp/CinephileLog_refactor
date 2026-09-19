package com.CinephileLog.external.batch;

import com.CinephileLog.external.service.TmdbApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MovieIdRangePartitioner implements Partitioner {

    @Value("${tmdb.api.keys}")
    private String[] apiKeys;


    @Autowired
    private TmdbApiClient tmdbApiClient;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        int latestId = tmdbApiClient.fetchLatestMovieId(apiKeys[0]);

        // Id 값 50000 까지만 데이터 받아옴
        latestId = Math.min(latestId, 50000);

        int range = latestId / apiKeys.length;

        Map<String, ExecutionContext> result = new HashMap<>();

        for (int i = 0; i < apiKeys.length; i++) {
            ExecutionContext context = new ExecutionContext();
            context.putInt("startId", i * range + 1);
            context.putInt("endId", (i == apiKeys.length - 1) ? latestId : (i + 1) * range);
            context.putString("apiKey", apiKeys[i]);

            result.put("partition" + i, context);
        }

        return result;
    }
}
