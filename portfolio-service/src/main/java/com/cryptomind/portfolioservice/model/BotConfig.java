package com.cryptomind.portfolioservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "bot_config")
public class BotConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol = "BTCUSDT";
    private String interval = "5m";
    private Double tradeSizeUsdt = 50.0;
    private Integer maxDailyTrades = 20;
    private Double minConfidence = 0.65;
    private Integer cooldownMinutes = 15;
    private Integer lossStreakThreshold = 3;
    private Boolean enabled = false;
}
