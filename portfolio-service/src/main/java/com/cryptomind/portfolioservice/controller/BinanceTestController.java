package com.cryptomind.portfolioservice.controller;

import com.cryptomind.portfolioservice.service.BinanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BinanceTestController {

    private final BinanceService binanceService;

    public BinanceTestController(BinanceService binanceService) {
        this.binanceService = binanceService;
    }

    @GetMapping("/api/binance/balances")
    public String getBalances() {
        return binanceService.getAccountBalances();
    }
}
