package com.cryptomind.portfolioservice.service;

import com.cryptomind.portfolioservice.dto.PortfolioDto;
import com.cryptomind.portfolioservice.model.Portfolio;
import com.cryptomind.portfolioservice.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public Portfolio createPortfolio(PortfolioDto dto) {
        Portfolio p = new Portfolio();
        p.setUserId(dto.getUserId());
        p.setAssetSymbol(dto.getAssetSymbol());
        p.setQuantity(dto.getQuantity());
        return portfolioRepository.save(p);
    }

    public List<Portfolio> getPortfoliosByUser(Long userId) {
        return portfolioRepository.findByUserId(userId);
    }

    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }

    public Portfolio getPortfolio(Long id) {
        return portfolioRepository.findById(id).orElse(null);
    }
}
