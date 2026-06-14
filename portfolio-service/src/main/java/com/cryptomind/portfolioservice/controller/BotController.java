package com.cryptomind.portfolioservice.controller;

import com.cryptomind.portfolioservice.dto.BotStatusDto;
import com.cryptomind.portfolioservice.model.TradeLog;
import com.cryptomind.portfolioservice.service.BotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bot")
public class BotController {

    private final BotService botService;

    public BotController(BotService botService) {
        this.botService = botService;
    }

    @GetMapping("/status")
    public ResponseEntity<BotStatusDto> status() {
        return ResponseEntity.ok(botService.getStatus());
    }

    @GetMapping("/trades")
    public ResponseEntity<List<TradeLog>> trades() {
        return ResponseEntity.ok(botService.getRecentTrades());
    }

    @GetMapping("/pnl")
    public ResponseEntity<Map<String, Object>> pnl(@RequestParam(defaultValue = "30d") String period) {
        return ResponseEntity.ok(botService.getPnl(period));
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> start() {
        botService.start();
        return ResponseEntity.ok(Map.of("status", "started"));
    }

    @PostMapping("/stop")
    public ResponseEntity<Map<String, String>> stop() {
        botService.stop();
        return ResponseEntity.ok(Map.of("status", "stopped"));
    }
}
