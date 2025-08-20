package com.cryptomind.portfolioservice.dto;

import lombok.Data;

@Data
public class PortfolioDto {
    private Long userId;
    private String assetSymbol;
    private Double quantity;
}
