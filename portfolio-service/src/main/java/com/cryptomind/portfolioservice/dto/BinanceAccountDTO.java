package com.cryptomind.portfolioservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceAccountDTO {
    public List<Balance> balances;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Balance {
        public String asset;
        public String free;
        public String locked;
    }
}
