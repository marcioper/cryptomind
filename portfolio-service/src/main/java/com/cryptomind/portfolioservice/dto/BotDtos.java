package com.cryptomind.portfolioservice.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class BotStatusDto {
    private boolean running;
    private String symbol;
    private String interval;
    private Double tradeSizeUsdt;
    private Integer maxDailyTrades;
    private Integer dailyTradeCount;
    private Double minConfidence;
    private Double accumulatedPnl;
    private Instant lastRunAt;
    private Instant cooldownUntil;
    private Integer consecutiveLosses;
    private String lastSignal;
    private Double lastConfidence;
}

@Data
class TradeLogDto {
    private Long id;
    private String symbol;
    private String side;
    private Double quantity;
    private Double price;
    private Double notionalUsdt;
    private Double pnl;
    private String signal;
    private Double confidence;
    private String status;
    private Instant executedAt;
}

@Data
class PnlSummaryDto {
    private Double totalPnl;
    private Double returnPct;
    private Integer tradeCount;
    private String period;
    private List<MonthlyPnlDto> monthly;
}

@Data
class MonthlyPnlDto {
    private String month;
    private Double pnl;
}
