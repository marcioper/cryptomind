# CryptoMind v1 Requirements

## INFRA — Infrastructure

- [x] **INFRA-01**: Stack starts with `docker compose up` (postgres + services + frontend)
- [x] **INFRA-02**: Binance secrets via env only (no real keys in repo)
- [x] **INFRA-03**: REST endpoints consistent between frontend and portfolio-service

## DATA — OHLCV Pipeline

- [x] **DATA-01**: Binance klines ingest (1m, 5m, 1h) for BTCUSDT
- [x] **DATA-02**: PostgreSQL persistence with deduplication
- [x] **DATA-03**: Scheduled job keeps candles updated

## ML — Strategy & Model

- [x] **ML-01**: Feature pipeline (returns, volatility, RSI, EMA, relative volume)
- [x] **ML-02**: Trainable baseline model with versioned artifact
- [x] **ML-03**: Inference API returns BUY | SELL | HOLD + confidence
- [x] **ML-04**: Walk-forward backtest with fixed trade size and fees
- [x] **ML-05**: Monthly metrics: win rate, profit factor, max drawdown, monthly return

## BOT — Automated Execution

- [x] **BOT-01**: Scheduler polls inference every 1–5 min
- [x] **BOT-02**: Fixed USDT trade size (configurable)
- [x] **BOT-03**: Guardrails: max trades/day, exposure limits, cooldown, kill switch
- [x] **BOT-04**: MARKET orders on testnet
- [x] **BOT-05**: Immutable trade log in DB

## OBS — Observability

- [x] **OBS-01**: Dashboard shows bot ON/OFF, accumulated PnL, recent trades
- [x] **OBS-02**: Aggregated status endpoint (position, balance, period return %)

## Out of Scope v1

Production accounts, multi-exchange, LLM decisioning, full auth, Kafka event bus, GraphQL/gRPC.
