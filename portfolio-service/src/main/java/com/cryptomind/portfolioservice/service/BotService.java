package com.cryptomind.portfolioservice.service;

import com.cryptomind.portfolioservice.dto.BotStatusDto;
import com.cryptomind.portfolioservice.dto.RecommendationResponse;
import com.cryptomind.portfolioservice.model.BotConfig;
import com.cryptomind.portfolioservice.model.BotState;
import com.cryptomind.portfolioservice.model.TradeLog;
import com.cryptomind.portfolioservice.repository.BotConfigRepository;
import com.cryptomind.portfolioservice.repository.BotStateRepository;
import com.cryptomind.portfolioservice.repository.TradeLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BotService {

    private static final Logger log = LoggerFactory.getLogger(BotService.class);

    private final BotConfigRepository configRepo;
    private final BotStateRepository stateRepo;
    private final TradeLogRepository tradeLogRepo;
    private final RecommendationClient recommendationClient;
    private final BinanceService binanceService;
    private final RiskEngine riskEngine;

    public BotService(
            BotConfigRepository configRepo,
            BotStateRepository stateRepo,
            TradeLogRepository tradeLogRepo,
            RecommendationClient recommendationClient,
            BinanceService binanceService,
            RiskEngine riskEngine
    ) {
        this.configRepo = configRepo;
        this.stateRepo = stateRepo;
        this.tradeLogRepo = tradeLogRepo;
        this.recommendationClient = recommendationClient;
        this.binanceService = binanceService;
        this.riskEngine = riskEngine;
    }

    public BotConfig getOrCreateConfig() {
        return configRepo.findById(1L).orElseGet(() -> configRepo.save(new BotConfig()));
    }

    public BotState getOrCreateState() {
        return stateRepo.findById(1L).orElseGet(() -> {
            BotState s = new BotState();
            s.setId(1L);
            return stateRepo.save(s);
        });
    }

    public void start() {
        BotConfig config = getOrCreateConfig();
        BotState state = getOrCreateState();
        config.setEnabled(true);
        state.setRunning(true);
        configRepo.save(config);
        stateRepo.save(state);
    }

    public void stop() {
        BotConfig config = getOrCreateConfig();
        BotState state = getOrCreateState();
        config.setEnabled(false);
        state.setRunning(false);
        configRepo.save(config);
        stateRepo.save(state);
    }

    public BotStatusDto getStatus() {
        BotConfig config = getOrCreateConfig();
        BotState state = getOrCreateState();
        BotStatusDto dto = new BotStatusDto();
        dto.setRunning(Boolean.TRUE.equals(state.getRunning()) && Boolean.TRUE.equals(config.getEnabled()));
        dto.setSymbol(config.getSymbol());
        dto.setInterval(config.getInterval());
        dto.setTradeSizeUsdt(config.getTradeSizeUsdt());
        dto.setMaxDailyTrades(config.getMaxDailyTrades());
        dto.setDailyTradeCount(state.getDailyTradeCount());
        dto.setMinConfidence(config.getMinConfidence());
        dto.setAccumulatedPnl(state.getAccumulatedPnl());
        dto.setLastRunAt(state.getLastRunAt());
        dto.setCooldownUntil(state.getCooldownUntil());
        dto.setConsecutiveLosses(state.getConsecutiveLosses());
        tradeLogRepo.findTop50ByOrderByExecutedAtDesc().stream().findFirst().ifPresent(t -> {
            dto.setLastSignal(t.getSignal());
            dto.setLastConfidence(t.getConfidence());
        });
        return dto;
    }

    public List<TradeLog> getRecentTrades() {
        return tradeLogRepo.findTop50ByOrderByExecutedAtDesc();
    }

    public Map<String, Object> getPnl(String period) {
        Instant since = parsePeriod(period);
        List<TradeLog> trades = tradeLogRepo.findByExecutedAtAfterOrderByExecutedAtDesc(since);
        double totalPnl = trades.stream().mapToDouble(t -> t.getPnl() != null ? t.getPnl() : 0).sum();
        Map<String, Double> monthly = new LinkedHashMap<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM").withZone(ZoneOffset.UTC);
        for (TradeLog t : trades) {
            if (t.getPnl() == null) continue;
            String month = fmt.format(t.getExecutedAt());
            monthly.merge(month, t.getPnl(), Double::sum);
        }
        List<Map<String, Object>> monthlyList = new ArrayList<>();
        monthly.forEach((m, pnl) -> monthlyList.add(Map.of("month", m, "pnl", pnl)));

        return Map.of(
                "totalPnl", totalPnl,
                "returnPct", totalPnl / 1000.0 * 100,
                "tradeCount", trades.size(),
                "period", period,
                "monthly", monthlyList
        );
    }

    public void executeTick() {
        BotConfig config = getOrCreateConfig();
        BotState state = getOrCreateState();
        state.setLastRunAt(Instant.now());
        stateRepo.save(state);

        RiskEngine.RiskCheck check = riskEngine.canTrade();
        if (!check.allowed()) {
            log.debug("Bot tick skipped: {}", check.reason());
            return;
        }

        try {
            RecommendationResponse rec = recommendationClient
                    .getRecommendationForSymbol(config.getSymbol(), config.getInterval())
                    .block();

            if (rec == null) return;

            RiskEngine.RiskCheck signalCheck = riskEngine.validateSignal(rec.getSignal(), rec.getConfidence());
            if (!signalCheck.allowed()) {
                log.debug("Signal rejected: {} ({})", rec.getSignal(), signalCheck.reason());
                return;
            }

            double price = binanceService.getSymbolPrice(config.getSymbol());
            double qty = config.getTradeSizeUsdt() / price;
            String side = "BUY".equals(rec.getSignal()) ? "BUY" : "SELL";
            String raw = binanceService.createOrder(config.getSymbol(), side, "MARKET", formatQty(qty));

            TradeLog logEntry = new TradeLog();
            logEntry.setSymbol(config.getSymbol());
            logEntry.setSide(side);
            logEntry.setQuantity(qty);
            logEntry.setPrice(price);
            logEntry.setNotionalUsdt(config.getTradeSizeUsdt());
            logEntry.setSignal(rec.getSignal());
            logEntry.setConfidence(rec.getConfidence());
            logEntry.setStatus("executed");
            logEntry.setRawResponse(raw);
            logEntry.setPnl(estimatePnl(side, config.getTradeSizeUsdt()));
            tradeLogRepo.save(logEntry);
            riskEngine.recordTradeResult(logEntry.getPnl());

            log.info("Bot executed {} {} @ {} (confidence={})", side, config.getSymbol(), price, rec.getConfidence());
        } catch (Exception e) {
            log.error("Bot tick failed", e);
        }
    }

    private double estimatePnl(String side, double notional) {
        // Placeholder until fill parsing; testnet MARKET fills are near immediate
        return "SELL".equals(side) ? notional * 0.001 : -notional * 0.001;
    }

    private String formatQty(double qty) {
        return String.format(Locale.US, "%.6f", qty);
    }

    private Instant parsePeriod(String period) {
        if (period == null || period.isBlank()) period = "30d";
        long days = 30;
        if (period.endsWith("d")) {
            days = Long.parseLong(period.replace("d", ""));
        }
        return Instant.now().minusSeconds(days * 86_400);
    }
}
