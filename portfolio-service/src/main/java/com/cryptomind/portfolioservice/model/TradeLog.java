package com.cryptomind.portfolioservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "trade_log")
public class TradeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Instant executedAt = Instant.now();
    private String rawResponse;
}
