package com.cryptomind.portfolioservice.service;

import com.cryptomind.portfolioservice.dto.RecommendationResponse;
import com.cryptomind.portfolioservice.model.Portfolio;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class RecommendationClient {

    private final WebClient webClient;

    public RecommendationClient(WebClient.Builder webClientBuilder) {
        // If running with Docker Compose, use the service name (e.g., "recommendation-service") as the host
        this.webClient = webClientBuilder.baseUrl("http://recommendation-service:8000").build();
    }

    public Mono<RecommendationResponse> getRecommendationMono(Portfolio portfolio) {
        Map<String, Object> body = Map.of(
                "userId", portfolio.getUserId(),
                "assets", List.of(Map.of(
                        "assetSymbol", portfolio.getAssetSymbol(),
                        "quantity", portfolio.getQuantity()
                ))
        );

        return webClient.post()
                .uri("/api/v1/recommend")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(RecommendationResponse.class);
    }

}
