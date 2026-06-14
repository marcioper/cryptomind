# Phase 0-5 Summary

Executed full CryptoMind v1 GSD plan from brownfield state.

## Phase 0 — MVP Consolidation
- Added `POST /api/portfolio/trade` for manual buy/sell
- Fixed `useRecommendation` to use `portfolioId` instead of `userId`
- Sanitized `.env.example` files (placeholder API keys)
- Updated README with setup instructions
- Simplified docker-compose (removed unused Kafka/Redis/Zookeeper from active path)

## Phase 1 — OHLCV Pipeline
- Binance klines client with paginated backfill
- PostgreSQL `candles` table with deduplication
- APScheduler: backfill on startup, minute updates
- `GET /api/v1/candles`

## Phase 2 — Features + Backtest
- Feature pipeline: returns, volatility, RSI, EMA, relative volume
- Labeling with fee-aware BUY/SELL/HOLD thresholds
- Backtest engine with fixed USDT notional and 0.1% fees
- `POST /api/v1/backtest` with monthly metrics

## Phase 3 — ML Training + Inference
- GradientBoostingClassifier with temporal CV
- Versioned model artifacts (`model_latest.joblib`)
- Real inference replacing random signal in `/recommend`
- `POST /api/v1/train` endpoint
- Updated RecommendationClient with symbol/interval + confidence

## Phase 4 — Bot Automation
- JPA entities: BotConfig, BotState, TradeLog
- RiskEngine: daily limits, confidence gate, loss-streak cooldown
- @Scheduled bot tick (5 min default)
- `POST /api/bot/start|stop`, `GET /api/bot/status|trades|pnl`

## Phase 5 — Frontend Dashboard
- BotPanel (start/stop, status, PnL)
- PnlChart (recharts monthly cumulative)
- TradeHistory table
- Manual trading moved to debug `<details>` section

## Self-Check: PASSED
- All planned files created
- Python syntax validated
- Java build requires Docker (no local JRE)

## Known Stubs
- BotService PnL estimation uses placeholder until fill parsing from Binance response
- Auto-train requires 500+ candles (runs after backfill completes)

## How to Run

```bash
cp .env.example .env
cp portfolio-service/.env.example portfolio-service/.env
# Add Binance testnet keys
docker compose up --build
```

- Dashboard: http://localhost:3000
- Train model: `curl -X POST http://localhost:8000/api/v1/train`
- Start bot: `curl -X POST http://localhost:8081/api/bot/start`
