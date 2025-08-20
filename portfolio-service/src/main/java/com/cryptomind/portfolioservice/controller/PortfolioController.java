package com.cryptomind.portfolioservice.controller;

import com.cryptomind.portfolioservice.dto.PortfolioDto;
import com.cryptomind.portfolioservice.dto.RecommendationResponse;
import com.cryptomind.portfolioservice.model.Portfolio;
import com.cryptomind.portfolioservice.service.PortfolioService;
import com.cryptomind.portfolioservice.service.RecommendationClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final RecommendationClient recommendationClient;

    public PortfolioController(PortfolioService portfolioService, RecommendationClient recommendationClient) {
        this.portfolioService = portfolioService;
        this.recommendationClient = recommendationClient;
    }

    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody PortfolioDto dto) {
        Portfolio p = portfolioService.createPortfolio(dto);
        return ResponseEntity.ok(p);
    }

    @GetMapping
    public ResponseEntity<List<Portfolio>> getAllPortfolios() {
        return ResponseEntity.ok(portfolioService.getAllPortfolios());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Portfolio>> getPortfoliosByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(portfolioService.getPortfoliosByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getPortfolio(@PathVariable Long id) {
        Portfolio p = portfolioService.getPortfolio(id);
        if (p != null) return ResponseEntity.ok(p);
        else return ResponseEntity.notFound().build();
    }


    @GetMapping("/{id}/recommendation")
    public Mono<ResponseEntity<RecommendationResponse>> getRecommendation(@PathVariable Long id) {
        // If portfolioService.getPortfolio is blocking, wrap in Mono.justOrEmpty:
        return Mono.justOrEmpty(portfolioService.getPortfolio(id))
                .flatMap(portfolio -> recommendationClient.getRecommendationMono(portfolio)
                        .map(recommendation -> ResponseEntity.ok(recommendation))
                )
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
