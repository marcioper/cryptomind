package com.cryptomind.portfolioservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "bot_state")
public class BotState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 1L;

    private Boolean running = false;
    private Instant lastRunAt;
    private Instant cooldownUntil;
    private Integer dailyTradeCount = 0;
    private String lastTradeDay;
    private Integer consecutiveLosses = 0;
    private Double accumulatedPnl = 0.0;
}
