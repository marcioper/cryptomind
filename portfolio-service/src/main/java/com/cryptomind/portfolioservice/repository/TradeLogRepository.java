package com.cryptomind.portfolioservice.repository;

import com.cryptomind.portfolioservice.model.TradeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TradeLogRepository extends JpaRepository<TradeLog, Long> {
    List<TradeLog> findTop50ByOrderByExecutedAtDesc();
    List<TradeLog> findByExecutedAtAfterOrderByExecutedAtDesc(Instant after);
    long countByExecutedAtAfter(Instant after);
}
