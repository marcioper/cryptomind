package com.cryptomind.portfolioservice.repository;

import com.cryptomind.portfolioservice.model.BotConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotConfigRepository extends JpaRepository<BotConfig, Long> {
}
