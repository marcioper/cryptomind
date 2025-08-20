package com.cryptomind.portfolioservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String assetSymbol; // e.g. BTC, ETH
    private Double quantity;
}
