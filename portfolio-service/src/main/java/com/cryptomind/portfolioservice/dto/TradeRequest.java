package com.cryptomind.portfolioservice.dto;

import lombok.Data;

@Data
public class TradeRequest {
    private Long userId;
    private String action; // "buy" or "sell"
    private String assetSymbol;
    private Double quantity;
}
