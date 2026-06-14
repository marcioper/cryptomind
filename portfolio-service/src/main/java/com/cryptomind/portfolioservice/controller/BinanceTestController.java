package com.cryptomind.portfolioservice.controller;

import com.cryptomind.portfolioservice.dto.BinanceAccountDTO;
import com.cryptomind.portfolioservice.service.BinanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/binance")
public class BinanceTestController {

    private final BinanceService binanceService;

    public BinanceTestController(BinanceService binanceService) {
        this.binanceService = binanceService;
    }

    @GetMapping("/balances-old")
    public String getBalances() {
        return binanceService.getAccountBalances();
    }

    @GetMapping("/balances")
    public ResponseEntity<BinanceAccountDTO> balances() {
        return ResponseEntity.ok(binanceService.getAccountBalancesTyped());
    }
}
