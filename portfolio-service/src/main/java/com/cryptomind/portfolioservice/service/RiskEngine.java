package com.cryptomind.portfolioservice.service;

import com.cryptomind.portfolioservice.model.BotConfig;
import com.cryptomind.portfolioservice.model.BotState;
import com.cryptomind.portfolioservice.repository.BotConfigRepository;
import com.cryptomind.portfolioservice.repository.BotStateRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class RiskEngine {

    private final BotConfigRepository configRepo;
    private final BotStateRepository stateRepo;

    public RiskEngine(BotConfigRepository configRepo, BotStateRepository stateRepo) {
        this.configRepo = configRepo;
        this.stateRepo = stateRepo;
    }

    public record RiskCheck(boolean allowed, String reason) {}

    public RiskCheck canTrade() {
        BotConfig config = getConfig();
        BotState state = getState();

        if (!Boolean.TRUE.equals(config.getEnabled()) || !Boolean.TRUE.equals(state.getRunning())) {
            return new RiskCheck(false, "bot_stopped");
        }
        if (state.getCooldownUntil() != null && Instant.now().isBefore(state.getCooldownUntil())) {
            return new RiskCheck(false, "cooldown_active");
        }
        resetDailyCountIfNeeded(state);
        if (state.getDailyTradeCount() >= config.getMaxDailyTrades()) {
            return new RiskCheck(false, "max_daily_trades");
        }
        return new RiskCheck(true, "ok");
    }

    public RiskCheck validateSignal(String signal, Double confidence) {
        BotConfig config = getConfig();
        if ("HOLD".equals(signal)) {
            return new RiskCheck(false, "hold_signal");
        }
        if (confidence != null && confidence < config.getMinConfidence()) {
            return new RiskCheck(false, "low_confidence");
        }
        return canTrade();
    }

    public void recordTradeResult(double pnl) {
        BotState state = getState();
        BotConfig config = getConfig();
        state.setAccumulatedPnl(state.getAccumulatedPnl() + pnl);
        state.setDailyTradeCount(state.getDailyTradeCount() + 1);

        if (pnl < 0) {
            int losses = state.getConsecutiveLosses() + 1;
            state.setConsecutiveLosses(losses);
            if (losses >= config.getLossStreakThreshold()) {
                state.setCooldownUntil(Instant.now().plusSeconds(config.getCooldownMinutes() * 60L));
                state.setConsecutiveLosses(0);
            }
        } else {
            state.setConsecutiveLosses(0);
        }
        stateRepo.save(state);
    }

    private void resetDailyCountIfNeeded(BotState state) {
        String today = LocalDate.now(ZoneOffset.UTC).toString();
        if (!today.equals(state.getLastTradeDay())) {
            state.setLastTradeDay(today);
            state.setDailyTradeCount(0);
            stateRepo.save(state);
        }
    }

    private BotConfig getConfig() {
        return configRepo.findById(1L).orElseGet(() -> {
            BotConfig c = new BotConfig();
            return configRepo.save(c);
        });
    }

    private BotState getState() {
        return stateRepo.findById(1L).orElseGet(() -> {
            BotState s = new BotState();
            s.setId(1L);
            return stateRepo.save(s);
        });
    }
}
