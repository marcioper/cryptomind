package com.cryptomind.portfolioservice.dto;

import lombok.Data;

@Data
public class RecommendationResponse {
    private String signal;
    private Double confidence;
    private String reason;
    private String interval;
    private String model_version;
}
