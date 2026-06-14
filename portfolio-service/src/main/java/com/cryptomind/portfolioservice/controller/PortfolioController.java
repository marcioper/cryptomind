package com.cryptomind.portfolioservice.controller;

import com.cryptomind.portfolioservice.dto.BinanceAccountDTO;
import com.cryptomind.portfolioservice.dto.PortfolioDto;
import com.cryptomind.portfolioservice.dto.RecommendationResponse;
import com.cryptomind.portfolioservice.dto.TradeRequest;
import com.cryptomind.portfolioservice.model.Portfolio;
import com.cryptomind.portfolioservice.service.BinanceService;
import com.cryptomind.portfolioservice.service.PortfolioService;
import com.cryptomind.portfolioservice.service.RecommendationClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final RecommendationClient recommendationClient;
    private final BinanceService binanceService;

    public PortfolioController(
            PortfolioService portfolioService,
            RecommendationClient recommendationClient,
            BinanceService binanceService
    ) {
        this.portfolioService = portfolioService;
        this.recommendationClient = recommendationClient;
        this.binanceService = binanceService;
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

    @GetMapping("/balances")
    public ResponseEntity<BinanceAccountDTO> balances() {
        return ResponseEntity.ok(binanceService.getAccountBalancesTyped());
    }

    @PostMapping("/trade")
    public ResponseEntity<Map<String, Object>> trade(@RequestBody TradeRequest req) {
        if (req.getAssetSymbol() == null || req.getQuantity() == null || req.getAction() == null) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Missing required fields"));
        }
        String side = "buy".equalsIgnoreCase(req.getAction()) ? "BUY" : "SELL";
        String raw = binanceService.createOrder(
                req.getAssetSymbol().toUpperCase(),
                side,
                "MARKET",
                String.valueOf(req.getQuantity())
        );
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "ok");
        body.put("side", side);
        body.put("symbol", req.getAssetSymbol().toUpperCase());
        body.put("quantity", req.getQuantity());
        body.put("result", raw);
        return ResponseEntity.ok(body);
    }
}
