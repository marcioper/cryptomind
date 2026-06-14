package com.cryptomind.portfolioservice.service;

import com.cryptomind.portfolioservice.dto.RecommendationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class RecommendationClient {

    private final WebClient webClient;

    public RecommendationClient(
            WebClient.Builder builder,
            @Value("${RECOMMENDATION_BASE_URL:http://recommendation-service:8000}")
            String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<RecommendationResponse> getRecommendationMono(com.cryptomind.portfolioservice.model.Portfolio portfolio) {
        return getRecommendationForSymbol(
                portfolio.getAssetSymbol() != null ? portfolio.getAssetSymbol() + "USDT" : "BTCUSDT",
                "5m"
        );
    }

    public Mono<RecommendationResponse> getRecommendationForSymbol(String symbol, String interval) {
        Map<String, Object> body = Map.of(
                "userId", 1,
                "assets", java.util.List.of(),
                "symbol", symbol,
                "interval", interval
        );

        return webClient.post()
                .uri("/api/v1/recommend")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(RecommendationResponse.class);
    }
}
