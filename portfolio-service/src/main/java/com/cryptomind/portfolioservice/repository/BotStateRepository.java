package com.cryptomind.portfolioservice.repository;

import com.cryptomind.portfolioservice.model.BotState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotStateRepository extends JpaRepository<BotState, Long> {
}
